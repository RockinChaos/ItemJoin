package me.RockinChaos.itemjoin.handlers;

import org.bukkit.command.CommandSender;
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
		if (ConfigHandler.getConfig("config.yml").getBoolean("Items-Permissions") == false) {
			return true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("OPItems-Permissions") != false && player.isOp()) {
			if (player.isPermissionSet(PermissionsHandler.customPermissions(items, item, worldName)) || player.isPermissionSet("itemjoin." + worldName + ".*")) {
				return true;
			}
		} else if (player.hasPermission(PermissionsHandler.customPermissions(items, item, worldName)) || player.hasPermission("itemjoin." + worldName + ".*")) {
			return true;
		}
		return false;
	}
	
	public static boolean hasCommandPermission(CommandSender sender, String permission) {
		if (sender.hasPermission(permission)) {
			return true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("OPCommands-Permissions") != true && sender.isOp()) {
			if (permission.equalsIgnoreCase("itemjoin.use") || permission.equalsIgnoreCase("itemjoin.reload") || permission.equalsIgnoreCase("itemjoin.updates")
					|| permission.equalsIgnoreCase("itemjoin.autoupdate") || permission.equalsIgnoreCase("itemjoin.creator") || permission.equalsIgnoreCase("itemjoin.purge") 
							|| permission.equalsIgnoreCase("itemjoin.save") || permission.equalsIgnoreCase("itemjoin.get") || permission.equalsIgnoreCase("itemjoin.get.others")
							|| permission.equalsIgnoreCase("itemjoin.remove") || permission.equalsIgnoreCase("itemjoin.remove.others") || permission.equalsIgnoreCase("itemjoin.disable") 
							|| permission.equalsIgnoreCase("itemjoin.disable.others") || permission.equalsIgnoreCase("itemjoin.enable") || permission.equalsIgnoreCase("itemjoin.enable.others")
							|| permission.equalsIgnoreCase("itemjoin.list")) {
				return true;
			}
		}
		return false;
	}
	
}
