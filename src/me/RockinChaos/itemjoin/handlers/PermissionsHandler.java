package me.RockinChaos.itemjoin.handlers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PermissionsHandler {

	public static String customPermissions(ConfigurationSection items, String item, String world) {
		if (ConfigHandler.getConfig("config.yml").getBoolean("Items-Permissions") == false || items.getString(".permission-node") == null) {
			return "itemjoin." + world + "." + item;
		}
		return items.getString(".permission-node");
	}

	public static boolean hasPermission(ConfigurationSection items, String item, Player player) {
		if (ConfigHandler.getConfig("config.yml").getBoolean("Items-Permissions") == false 
				|| player.hasPermission(PermissionsHandler.customPermissions(items, item, player.getWorld().getName())) 
				|| player.hasPermission("itemjoin." + player.getWorld() + ".*") || player.hasPermission("itemjoin.*") || player.hasPermission("itemjoin.all")) {
			return true;
		}
		return false;
	}
}
