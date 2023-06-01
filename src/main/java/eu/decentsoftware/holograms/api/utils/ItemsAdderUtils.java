package eu.decentsoftware.holograms.api.utils;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderUtils{
    
    public static boolean isCustomItem(ItemStack itemStack) {
        return CustomStack.byItemStack(itemStack) != null;
    }
    
    public static ItemStack getItemStack(String namespacedId) {
        CustomStack customStack = CustomStack.getInstance(namespacedId);
        if (customStack == null) {
            return null;
        }
        
        return customStack.getItemStack();
    }
    
    public static String getHoloItemName(ItemStack itemStack) {
        CustomStack customStack = CustomStack.byItemStack(itemStack);
        if (customStack == null) {
            return itemStack.getType().name();
        }
        
        return "itemsadder:" + customStack.getNamespacedID();
    }
}
