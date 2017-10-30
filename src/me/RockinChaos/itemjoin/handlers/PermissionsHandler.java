package me.RockinChaos.itemjoin.handlers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PermissionsHandler {

	public static String customPermissions(ConfigurationSection items, String item, String world) {
		String customPermission = items.getString(".permission-node");
		if (ConfigHandler.getConfig("config.yml").getBoolean("Items-Permissions") == false || customPermission == null) {
			customPermission = "itemjoin." + world + "." + item;
		}
		return customPermission;
	}

	public static boolean hasPermission(ConfigurationSection items, String item, Player player) {
		boolean checkPermission = false;
		if (ConfigHandler.getConfig("config.yml").getBoolean("Items-Permissions") == false 
				|| player.hasPermission(PermissionsHandler.customPermissions(items, item, player.getWorld().getName())) 
				|| player.hasPermission("itemjoin." + player.getWorld() + ".*") || player.hasPermission("itemjoin.*") || player.hasPermission("itemjoin.all")) {
			checkPermission = true;
		}
		return checkPermission;
	}
}
