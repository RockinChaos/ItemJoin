package me.RockinChaos.itemjoin.handlers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PermissionsHandler {

	public static String customPermissions(ConfigurationSection items, String item, String worldName) {
		if (items.getString(".permission-node") != null) {
			return items.getString(".permission-node");
		}
		return "itemjoin." + worldName + "." + item;
	}

	public static boolean hasItemsPermission(ConfigurationSection items, String item, Player player) {
		String worldName = player.getWorld().getName();
		if (ConfigHandler.getConfig("config.yml").getBoolean("Items-Permissions") == false 
				|| player.hasPermission(PermissionsHandler.customPermissions(items, item, worldName)) 
				|| player.hasPermission("itemjoin." + worldName + ".*") || player.hasPermission("itemjoin.all")) {
			return true;
		}
		return false;
	}
}
