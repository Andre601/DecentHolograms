package eu.decentsoftware.holograms.api.utils.items;

import eu.decentsoftware.holograms.api.utils.Common;
import eu.decentsoftware.holograms.api.utils.HeadDatabaseUtils;
import eu.decentsoftware.holograms.api.utils.ItemsAdderUtils;
import eu.decentsoftware.holograms.api.utils.PAPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Data
@AllArgsConstructor
public class HologramItem {

	private final String content;
	private String nbt;
	private String extras;
	private String itemsAdderItem = null;
	private Material material;
	private short durability = 0;
	private boolean enchanted = false;

	public HologramItem(String string) {
		this.content = string;
		this.parseContent();
	}

	public ItemStack parse() {
		return this.parse(null);
	}
	
	public ItemStack parse(Player player) {
		ItemBuilder itemBuilder = new ItemBuilder(material);
		if (durability > 0) itemBuilder.withDurability(durability);
		if (material.name().contains("SKULL") || material.name().contains("HEAD")) {
			if (extras != null) {
				String extrasFinal = player == null ? extras.trim() : PAPI.setPlaceholders(player, extras).trim();
				if (!extrasFinal.isEmpty()) {
					if (extrasFinal.startsWith("HEADDATABASE_") && Common.isPluginEnabled("HeadDatabase")) {
						String headDatabaseId = extrasFinal.substring("HEADDATABASE_".length());
						itemBuilder.withItemStack(HeadDatabaseUtils.getHeadItemStackById(headDatabaseId));
					} else if (extrasFinal.length() <= 16) {
						itemBuilder.withSkullOwner(extrasFinal);
					} else {
						itemBuilder.withSkullTexture(extrasFinal);
					}
					//noinspection deprecation
					itemBuilder.withDurability((short) SkullType.PLAYER.ordinal());
				}
			}
		}
		if (itemsAdderItem != null && !itemsAdderItem.isEmpty() && Common.isPluginEnabled("ItemsAdder")) {
			itemBuilder.withItemStack(ItemsAdderUtils.getItemStack(itemsAdderItem));
		}
		if (enchanted) itemBuilder.withUnsafeEnchantment(Enchantment.DURABILITY, 0);

		ItemStack itemStack = itemBuilder.toItemStack();
		if (nbt != null) {
			try {
				// noinspection deprecation
				Bukkit.getUnsafe().modifyItemStack(itemStack, player == null ? nbt : PAPI.setPlaceholders(player, nbt));
			} catch (Throwable ignored) {}
		}
		return itemStack;
	}

	private void parseContent() {
		String string = this.content;

		// Find NBT tag
		if (string.contains("{") && string.contains("}")) {
			int nbtStart = string.indexOf('{');
			int nbtEnd = string.lastIndexOf('}');
			if (nbtStart > 0 && nbtEnd > 0 && nbtEnd > nbtStart) {
				this.nbt = string.substring(nbtStart, nbtEnd + 1);
				string = string.substring(0, nbtStart) + string.substring(nbtEnd + 1);
			}
		}

		// Find extras
		if (string.contains("(") && string.contains(")")) {
			int extrasStart = string.indexOf('(');
			int extrasEnd = string.lastIndexOf(')');
			if (extrasStart > 0 && extrasEnd > 0 && extrasEnd > extrasStart) {
				this.extras = string.substring(extrasStart + 1, extrasEnd);
				string = string.substring(0, extrasStart) + string.substring(extrasEnd + 1);
			}
		}

		if (string.contains("!ENCHANTED")) {
			string = string.replace("!ENCHANTED", "");
			this.enchanted = true;
		}

		// Parse material
		String materialString = string.trim().split(" ", 2)[0];
		String materialName = materialString;
		if (materialString.toLowerCase().startsWith("itemsadder:") && Common.isPluginEnabled("ItemsAdder")) {
			materialString = materialString.substring("itemsadder:".length());
			itemsAdderItem = materialString;
		}
		if (materialString.contains(":")) {
			// This only happens when ItemsAdder is not enabled. So we treat it as invalid item.
			if (materialString.toLowerCase().startsWith("itemsadder:")) {
				this.material = Material.STONE;
				return;
			}
			
			String[] materialStringSpl = materialString.split(":", 2);
			materialName = materialStringSpl[0];
			try {
				this.durability = Short.parseShort(materialStringSpl[1]);
			} catch (Throwable t) {
				this.durability = 0;
			}
		}
		// The Material doesn't matter as it will be overriden by ItemsAdder later on.
		if (itemsAdderItem != null && !itemsAdderItem.isEmpty()) {
			this.material = Material.STONE;
			return;
		}
		
		this.material = DecentMaterial.parseMaterial(materialName);

		// Material couldn't be parsed, set it to stone.
		if (this.material == null) {
			this.material = Material.STONE;
		}
	}

	@SuppressWarnings("deprecation")
	public static HologramItem fromItemStack(ItemStack itemStack) {
		Validate.notNull(itemStack);

		StringBuilder stringBuilder = new StringBuilder();
		ItemBuilder itemBuilder = new ItemBuilder(itemStack);
		String material = itemStack.getType().name();
		if (Common.isPluginEnabled("ItemsAdder") && ItemsAdderUtils.isCustomItem(itemStack)) {
			material = ItemsAdderUtils.getHoloItemName(itemStack);
		}
		
		stringBuilder.append(material);
		int durability = itemStack.getDurability();
		if (durability > 0) {
			stringBuilder.append(":").append(durability);
		}
		stringBuilder.append(" ");
		Map<Enchantment, Integer> enchants = itemStack.getEnchantments();
		if (enchants != null && !enchants.isEmpty()) {
			stringBuilder.append("!ENCHANTED").append(" ");
		}
		if (material.contains("HEAD") || material.contains("SKULL")) {
			String owner = itemBuilder.getSkullOwner();
			String texture = itemBuilder.getSkullTexture();
			if (owner != null) {
				stringBuilder.append("(").append(owner).append(")");
			} else if (texture != null) {
				stringBuilder.append("(").append(texture).append(")");
			}
		}
		return new HologramItem(stringBuilder.toString());
	}

	public static ItemStack parseItemStack(String string, Player player) {
		string = PAPI.setPlaceholders(player, string);
		return new HologramItem(string).parse();
	}

}
