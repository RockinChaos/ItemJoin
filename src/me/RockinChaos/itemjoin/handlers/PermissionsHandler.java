/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.handlers;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PermissionsHandler {

	private static PermissionsHandler permissions;

   /**
    * Checks if the permission is a custom permission.
    * This fetches the proper permission node, so if a custom permission is not
    * defined for the item, it returns the default permission node itemjoin.world.itemname.
    * 
    * @param permissionNode - The custom permission node for the item.
    * @param item - The node of the current item.
    * @param worldName - The name of the current world.
    * @return The permission node of the item.
    */
	public String customPermissions(final String permissionNode, final String item, final String worldName) {
		if (permissionNode != null) {
			return permissionNode;
		}
		return "itemjoin." + worldName + "." + item;
	}
	
   /**
    * Checks if the sender has permission.
    * 
    * @param sender - The entity that is having their permissions checked.
    * @param permission - The permission the sender is expected to have.
    * @return If the entity has the proper permission.
    */
	public boolean hasPermission(final CommandSender sender, final String permission) {
		if (sender.hasPermission(permission) || sender.hasPermission("itemjoin.*") || sender.hasPermission("itemjoin.all") || this.isDeveloper(sender) || (sender instanceof ConsoleCommandSender)) {
			return true;
		} else if (!ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Commands-OP") && sender.isOp()) {
			if (permission.equalsIgnoreCase("itemjoin.use") || permission.equalsIgnoreCase("itemjoin.reload") || permission.equalsIgnoreCase("itemjoin.updates")
					|| permission.equalsIgnoreCase("itemjoin.autoupdate") || permission.equalsIgnoreCase("itemjoin.menu") 
					|| permission.equalsIgnoreCase("itemjoin.purge") || permission.equalsIgnoreCase("itemjoin.get") || permission.equalsIgnoreCase("itemjoin.get.others")
							|| permission.equalsIgnoreCase("itemjoin.remove") || permission.equalsIgnoreCase("itemjoin.remove.others") || permission.equalsIgnoreCase("itemjoin.disable") 
							|| permission.equalsIgnoreCase("itemjoin.disable.others") || permission.equalsIgnoreCase("itemjoin.enable") || permission.equalsIgnoreCase("itemjoin.enable.others")
							|| permission.equalsIgnoreCase("itemjoin.list")) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * If Debugging Mode is enabled, the plugin developer will be allowed to execute ONLY this plugins commands for help and support purposes.
    * 
    * @param sender - The entity executing the plugin command.
    * @return If the command sender is the developer of the plugin.
    */
	private boolean isDeveloper(final CommandSender sender) {
		if (ConfigHandler.getConfig().debugEnabled()) {
			if (sender instanceof Player) {
				try { 
					if (((Player)sender).getUniqueId().toString().equalsIgnoreCase("ad6e8c0e-6c47-4e7a-a23d-8a2266d7baee")) { return true; }
				} catch (Exception e) { if (sender.getName().equalsIgnoreCase("RockinChaos")) { return true; } }
			}
		}
		return false;
	}
	
   /**
    * Checks if custom items have permission requirements to get or use them.
    * 
    * @return If the custom items require certain permissions to be given or used.
    */
	public boolean receiveEnabled() {
		return ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Commands-Get");
	}
	
   /**
    * Gets the instance of the PermissionsHandler.
    * 
    * @return The PermissionsHandler instance.
    */
    public static PermissionsHandler getPermissions() { 
        if (permissions == null) { permissions = new PermissionsHandler(); }
        return permissions; 
    } 
}