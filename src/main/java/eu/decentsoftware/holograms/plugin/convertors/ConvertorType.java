package eu.decentsoftware.holograms.plugin.convertors;

import eu.decentsoftware.holograms.api.utils.config.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public enum ConvertorType {
	CMI("CMI", "cmi"),
	GHOLO("GHolo", "gholo", "gh"),
	HOLOGRAPHIC_DISPLAYS("HolographicDisplays", "DH", "hd");

	public static ConvertorType fromString(String alias) {
		for (ConvertorType convertorType : ConvertorType.values()) {
			if (convertorType.getName().equalsIgnoreCase(alias) || convertorType.getAliases().contains(alias)) {
				return convertorType;
			}
		}
		return null;
	}

	private final String name;
	private final List<String> aliases;

	ConvertorType(String name, String... aliases) {
		this.name = name;
		this.aliases = aliases == null ? Collections.emptyList() : Arrays.asList(aliases);
	}
	
	public String getName() {
		return name;
	}

	public List<String> getAliases() {
		return aliases;
	}

}
