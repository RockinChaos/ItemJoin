package me.RockinChaos.itemjoin.handlers;

import org.bukkit.configuration.ConfigurationSection;

public class WorldHandler {
	
	public static Boolean inWorld(ConfigurationSection items, String world) {
		Boolean InWorld = false;
		if (items.getString(".enabled-worlds") != null) {
			String worldlist = items.getString(".enabled-worlds").replace(" ", "");
			String[] compareWorlds = worldlist.split(",");
			for (String compareWorld: compareWorlds) {
				if (compareWorld.equalsIgnoreCase(world) 
						|| compareWorld.equalsIgnoreCase("all") 
						|| compareWorld.equalsIgnoreCase("global")) {
					InWorld = true;
					return InWorld;
				}
			}
		} else if (items.getString(".enabled-worlds") == null) {
			InWorld = true;
			return InWorld;
		}
		return InWorld;
	}
	
	public static Boolean inGlobalWorld(String world) {
		Boolean InWorld = false;
		if (ConfigHandler.getConfig("config.yml").getString("enabled-worlds") != null) {
			String worldlist = ConfigHandler.getConfig("config.yml").getString("enabled-worlds").replace(" ", "");
			String[] compareWorlds = worldlist.split(",");
			for (String compareWorld: compareWorlds) {
				if (compareWorld.equalsIgnoreCase(world) 
						|| compareWorld.equalsIgnoreCase("all") 
						|| compareWorld.equalsIgnoreCase("global")) {
					InWorld = true;
					return InWorld;
				}
			}
		} else if (ConfigHandler.getConfig("config.yml").getString("enabled-worlds") == null) {
			InWorld = true;
			return InWorld;
		}
		return InWorld;
	}
}