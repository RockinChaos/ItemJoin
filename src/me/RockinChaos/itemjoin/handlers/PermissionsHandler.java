package me.RockinChaos.itemjoin.handlers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PermissionsHandler {

	   public static String customPermissions(ConfigurationSection items, String item, String world) {
	        String customPermission = items.getString(".permission-node");
	        if (customPermission == null) {
	        	customPermission = "itemjoin." + world + "." + item;
	        }
			  return customPermission;
		}
	   
	   public static boolean checkPermissions(ConfigurationSection items, String item, String world, Player player) {
	        boolean checkPermission = false;
	        if (player.hasPermission(PermissionsHandler.customPermissions(items, item, world)) 
         			  || player.hasPermission("itemjoin." + world + ".*") 
         			  || player.hasPermission("itemjoin.*")) {
	        	checkPermission = true;
	        }
			  return checkPermission;
		}
}
