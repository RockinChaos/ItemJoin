package me.RockinChaos.itemjoin.handlers;

import org.bukkit.configuration.ConfigurationSection;

public class WorldHandler {
	
	public static Boolean inWorld(ConfigurationSection items, String world) {
		Boolean InWorld = false;
		if (items.getString(".enabled-worlds") != null) {
			if (ItemHandler.containsIgnoreCase(items.getString(".enabled-worlds"), world) 
					|| ItemHandler.containsIgnoreCase(items.getString(".enabled-worlds"), "all") 
					|| ItemHandler.containsIgnoreCase(items.getString(".enabled-worlds"), "global")) {
			InWorld = true;
			}
		} else if (items.getString(".enabled-worlds") == null) {
			InWorld = true;
		}
		return InWorld;
	}
}
