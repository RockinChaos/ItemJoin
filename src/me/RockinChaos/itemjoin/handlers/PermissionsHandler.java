package me.RockinChaos.itemjoin.handlers;

import org.bukkit.command.CommandSender;

public class PermissionsHandler {

	public static String customPermissions(String permissionNode, String item, String worldName) {
		if (permissionNode != null) {
			return permissionNode;
		}
		return "itemjoin." + worldName + "." + item;
	}
	
	public static boolean hasCommandPermission(CommandSender sender, String permission) {
		if (sender.hasPermission(permission) || sender.hasPermission("itemjoin.*")) {
			return true;
		} else if (!ConfigHandler.getOPCommandPermissions() && sender.isOp()) {
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
