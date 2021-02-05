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
package me.RockinChaos.itemjoin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.SQL;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;

public class ChatTab implements TabCompleter {
	
   /**
	* Called when a Player tries to TabComplete.
    * @param sender - Source of the command.
    * @param command - Command which was executed.
    * @param label - Alias of the command which was used.
    * @param args - Passed command arguments.
    * @return The String List of TabComplete commands.
	*/
	@Override
	public List < String > onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
		final List < String > completions = new ArrayList < > ();
		final List < String > commands = new ArrayList < > ();
		if (args.length == 2 && args[0].equalsIgnoreCase("help") && PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
			 commands.addAll( Arrays.asList("2","3","4","5","6","7","8","9"));
		} else if (args.length == 2 && args[0].equalsIgnoreCase("permissions") && PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.permissions")) {
			for (int i = 1; i <= ConfigHandler.getConfig().getPermissionPages(); i++) {
				commands.add(Integer.toString(i));
			}
		} else if ((args.length == 2 || args.length == 3) && args[0].equalsIgnoreCase("purge") && PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.purge")) {
			if (args.length == 2) {
				commands.addAll(Arrays.asList("map-ids","first-join","first-world","ip-limits","enabled-players","first-commands"));
			} else {
				List<DataObject> dataList = new ArrayList<DataObject>();
				try {
					dataList = SQL.getData().getDataList(new DataObject(Table.valueOf("IJ_" + args[1].toUpperCase().replace("-", "_"))));
				} catch (Exception e) { }
				for (DataObject dataObject: dataList) {
					String objectString = (args[1].equalsIgnoreCase("map-ids") ? dataObject.getMapIMG() : 
						(PlayerHandler.getPlayer().getPlayerString(dataObject.getPlayerId()) != null ? PlayerHandler.getPlayer().getPlayerString(dataObject.getPlayerId()).getName() : dataObject.getPlayerId()));
					commands.add(objectString);
				}
			}
		} else if ((args.length == 2 || args.length == 3) && (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("enable"))) {
			if (args.length == 2 && ((PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.enable.others") && args[0].equalsIgnoreCase("enable")) 
				|| (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.disable.others") && args[0].equalsIgnoreCase("disable")))) {
				PlayerHandler.getPlayer().forOnlinePlayers(player -> {
					commands.add(player.getName());
				});
			} else {
				for (World world: Bukkit.getServer().getWorlds()) {
					commands.add(world.getName());
				}
			}
		} else if ((args.length == 2 || args.length == 3 || args.length == 4) && (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("getOnline") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("removeOnline"))) {
			if (args.length == 2) {
				for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
					commands.add(itemMap.getConfigName());
				}
			} else if (args.length == 3 && ((PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get.others") && (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("getOnline"))) 
				|| (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove.others") && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("removeOnline"))))) {
				commands.addAll(Arrays.asList("2","4","8","16"));
				if (!args[0].equalsIgnoreCase("getOnline") && !args[0].equalsIgnoreCase("removeOnline")) {
					PlayerHandler.getPlayer().forOnlinePlayers(player -> {
						commands.add(player.getName());
					});
				}
			} else if (args.length == 4 && !Utils.getUtils().isInt(args[2]) && !args[0].equalsIgnoreCase("getOnline") && !args[0].equalsIgnoreCase("removeOnline") && ((PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get.others") 
				&& args[0].equalsIgnoreCase("get")) || (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove.others") && args[0].equalsIgnoreCase("remove")))) {
				commands.addAll(Arrays.asList("2","3","4","6","8","16","32","64"));
			}
		} else if (args.length == 2 && (args[0].equalsIgnoreCase("getAll") && PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get.others") || args[0].equalsIgnoreCase("removeAll") 
			&& PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove.others"))) {
			PlayerHandler.getPlayer().forOnlinePlayers(player -> {
				commands.add(player.getName());
			});
		} else if (args.length == 1) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) { 		 	commands.addAll(Arrays.asList("help","info","world")); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.permissions")) { 	commands.add("permissions"); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.purge")) { 		 	commands.add("purge"); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.enable")) { 	 	commands.add("enable"); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.disable")) { 	 	commands.add("disable"); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get")) { 	   	 	commands.addAll(Arrays.asList("get","getAll")); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get.others")) {  	commands.add("getOnline"); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove")) { 	    commands.addAll(Arrays.asList("remove","removeAll")); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove.others")) {  commands.add("removeOnline"); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.reload")) { 		commands.add("reload"); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.menu")) { 			commands.add("menu"); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.list")) { 			commands.add("list"); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.updates")) { 		commands.add("updates"); }
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.autoupdate")) { 	commands.add("autoupdate"); }
		}
		StringUtil.copyPartialMatches(args[(args.length - 1)], commands, completions);
		Collections.sort(completions);
		return completions;
	}
}