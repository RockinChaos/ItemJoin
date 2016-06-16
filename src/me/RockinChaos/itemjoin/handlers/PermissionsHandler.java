package me.RockinChaos.itemjoin.handlers;

import org.bukkit.configuration.ConfigurationSection;

public class PermissionsHandler {
	
	   public static String customPermissions(ConfigurationSection items, String item, String world) {
	        String customPermission = items.getString(".permission-node");
	        if (customPermission == null) {
	        	customPermission = "itemjoin." + world + "." + item;
	        }
			  return customPermission;
		}
}
