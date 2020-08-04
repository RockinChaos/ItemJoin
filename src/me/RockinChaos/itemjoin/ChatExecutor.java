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
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.handlers.UpdateHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.listeners.Crafting;
import me.RockinChaos.itemjoin.utils.LanguageAPI;
import me.RockinChaos.itemjoin.utils.LegacyAPI;
import me.RockinChaos.itemjoin.utils.UI;
import me.RockinChaos.itemjoin.utils.Chances;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLite;
import me.RockinChaos.itemjoin.utils.sqlite.SQDrivers;

public class ChatExecutor implements CommandExecutor {
	
   /**
	* Called when the CommandSender executes a command.
    * @param sender - Source of the command.
    * @param command - Command which was executed.
    * @param label - Alias of the command which was used.
    * @param args - Passed command arguments.
    * @return true if the command is valid.
	*/
	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (Execute.DEFAULT.accept(sender, args, 0)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help &afor the help menu.");
		} else if (Execute.HELP.accept(sender, args, 1)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Help &7- &eThis help menu.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Reload &7- &eReloads the .yml files.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Updates &7- &eChecks for plugin updates.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin AutoUpdate &7- &eUpdate ItemJoin to latest version.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 2 &afor the next page.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 1/9 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 2)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin List &7- &eCheck items you can get each what worlds.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin World &7- &eCheck what world you are in, debugging.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Menu &7- &eOpens the GUI Creator for custom items.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Permissions &7- &eLists the permissions you have.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Permissions 2 &7- &ePermissions page 2.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 3 &afor the next page.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 2/9 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 3)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Info &7- &eGets data-info of the held item.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Get <Item> &7- &eGives that ItemJoin item.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <Qty> &7- &eGives amount of said item.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <User> &7- &eGives to said player.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <User> <Qty> &7- &eGives qty to player.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 4 &afor the next page.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 3/9 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 4)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> &7- &eRemoves item from inventory.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <Qty> &7- &eRemoves qty of item.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <User> &7- &eRemoves from player.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <User> <Qty> &7- &eRemoves qty.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 5 &afor the next page.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 4/9 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 5)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetOnline <Item> &7- &eGives to all online.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetOnline <Item> <Qty> &7- &eGives qty to all online.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveOnline <Item> &7- &eRemoves from all online.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveOnline <Item> <Qty> &7- &eRemoves qty.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 6 &afor the next page.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 5/9 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 6)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetAll &7- &eGives all ItemJoin items.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetAll <User> &7- &eGives all items to said player.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveAll &7- &eRemoves all ItemJoin items.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveAll <User> &7- &eRemoves from player.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 7 &afor the next page.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 6/9 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 7)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Enable &7- &eEnables ItemJoin for all players.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Enable <User> &7- &eEnables ItemJoin for player.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Enable <User> <World> &7- &eFor player/world.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 8 &afor the next page.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 7/9 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 8)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Disable &7- &eDisables ItemJoin for all players.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Disable <User> &7- &eDisables ItemJoin for player.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Disable <User> <World> &7- &eFor player/world.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 9 &afor the next page.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 8/9 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 9)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&c&l[DANGER]&eThe Following Destroys Data &nPermanently!&e&c&l[DANGER]");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge &7- &eDeletes the database file!");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge map-id <Image> &7- &eMap-Images data.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge first-join <User> &7- &eFirst-Join data.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge first-world <User> &7- &eFirst-World data.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge ip-limits <User> &7- &eIp-Limits data.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge enabled-players <User> &7- &eData.");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aFound a bug? Report it @");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&ahttps://github.com/RockinChaos/ItemJoin/issues");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 9/9 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.RELOAD.accept(sender, args, 0)) {
			SQLite.getLite(false).executeLaterStatements();
			ItemUtilities.getUtilities().closeAnimations();
			ItemUtilities.getUtilities().clearItems();
			ConfigHandler.getConfig(true);
			LanguageAPI.getLang(false).sendLangMessage("Commands.Default.configReload", sender);
		} else if (Execute.MENU.accept(sender, args, 0)) {
			UI.getCreator().startMenu(sender);
			LanguageAPI.getLang(false).sendLangMessage("Commands.UI.creatorLaunched", sender);
		} else if (Execute.INFO.accept(sender, args, 0)) {
			this.info(sender);
		} else if (Execute.WORLD.accept(sender, args, 0)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[0] = ((Player) sender).getWorld().getName();
			LanguageAPI.getLang(false).sendLangMessage("Commands.World.worldHeader", sender, placeHolders);
			LanguageAPI.getLang(false).sendLangMessage("Commands.World.worldRow", sender, placeHolders);
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]--------------&a&l[&e Worlds In Menu 1/1 &a&l]&a&l&m-------------[");
		} else if (Execute.LIST.accept(sender, args, 0)) {
			this.list(sender);
		} else if (Execute.PERMISSIONS.accept(sender, args, 1)) {
			this.permissions(sender, 1);
		} else if (Execute.PERMISSIONS.accept(sender, args, 2)) {
			this.permissions(sender, Integer.parseInt(args[1]));
		} else if (Execute.PURGE.accept(sender, args, 0)) {
			if (args.length == 1) { this.purge(sender, "Database", "All Players"); } 
			else if (args[1].equalsIgnoreCase("map-ids") || args[1].equalsIgnoreCase("ip-limits") || args[1].equalsIgnoreCase("first-join") || args[1].equalsIgnoreCase("first-world") || args[1].equalsIgnoreCase("enabled-players")) { 
				this.purge(sender, args[1], args[2]); 
			}
		} else if (Execute.ENABLE.accept(sender, args, 0)) {
			this.enable(sender, (args.length >= 2 ? args[1] : "ALL"), (args.length == 3 ? args[2] : "Global"), args.length);
		} else if (Execute.DISABLE.accept(sender, args, 0)) {
			this.disable(sender, (args.length >= 2 ? args[1] : "ALL"), (args.length == 3 ? args[2] : "Global"), args.length);
		} else if (Execute.GETONLINE.accept(sender, args, 0)) {
			if (args.length >= 2) { this.handleOnline(sender, args, false); }
		} else if (Execute.GET.accept(sender, args, 0)) {
			this.handleItems(sender, args, false);
		} else if (Execute.GETALL.accept(sender, args, 0)) {
			this.handleAllItems(sender, args, false);
		} else if (Execute.REMOVEONLINE.accept(sender, args, 0)) {
			if (args.length >= 2) { this.handleOnline(sender, args, true); }
		} else if (Execute.REMOVE.accept(sender, args, 0)) {
			this.handleItems(sender, args, true);
		} else if (Execute.REMOVEALL.accept(sender, args, 0)) {
			this.handleAllItems(sender, args, true);
		} else if (Execute.UPDATE.accept(sender, args, 0)) {
			LanguageAPI.getLang(false).sendLangMessage("Commands.Updates.checking", sender);
			UpdateHandler.getUpdater(false).checkUpdates(sender, false);
		} else if (Execute.AUTOUPDATE.accept(sender, args, 0)) {
			LanguageAPI.getLang(false).sendLangMessage("Commands.Updates.forcing", sender);
			UpdateHandler.getUpdater(false).forceUpdates(sender);
		} else if (this.matchExecutor(args) == null) {
			LanguageAPI.getLang(false).sendLangMessage("Commands.Default.unknownCommand", sender);
		} else if (!this.matchExecutor(args).playerRequired(sender, args)) {
			LanguageAPI.getLang(false).sendLangMessage("Commands.Default.notPlayer", sender);
			Execute executor = this.matchExecutor(args);
			if (executor.equals(Execute.GET))            { LanguageAPI.getLang(false).sendLangMessage("Commands.Get.usageSyntax", sender); } 
			else if (executor.equals(Execute.GETALL))    { LanguageAPI.getLang(false).sendLangMessage("Commands.Get.usageSyntax", sender); } 
			else if (executor.equals(Execute.REMOVE))    { LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.usageSyntax", sender); } 
			else if (executor.equals(Execute.REMOVEALL)) { LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.usageSyntax", sender); }
		} else if (!this.matchExecutor(args).hasSyntax(args, 0)) {
			Execute executor = this.matchExecutor(args);
			if (executor.equals(Execute.GET))               { LanguageAPI.getLang(false).sendLangMessage("Commands.Get.invalidSyntax", sender); } 
			else if (executor.equals(Execute.GETONLINE))    { LanguageAPI.getLang(false).sendLangMessage("Commands.Get.invalidOnlineSyntax", sender); } 
			else if (executor.equals(Execute.REMOVE))       { LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.invalidSyntax", sender); } 
			else if (executor.equals(Execute.REMOVEONLINE)) { LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.invalidOnlineSyntax", sender); }
			else if (executor.equals(Execute.PURGE))        { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.unknownCommand", sender); }
		} else if (!this.matchExecutor(args).hasPermission(sender, args)) {
			LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender);
		}
		return true;
	}
	
   /**
	* Called when the CommandSender fails to execute a command.
	* @param args - Passed command arguments.
	* @return The found Executor.
	* 
	*/
	private Execute matchExecutor(final String[] args) {
		for (Execute command : Execute.values()) {
			if (command.acceptArgs(args)) {
				return command;
			}
		}
		return null;
	}
	
   /**
	* Called when the CommandSender executes the Info command.
	* @param sender - Source of the command. 
	* 
	*/
	private void info(final CommandSender sender) {
		if (PlayerHandler.getPlayer().getHandItem((Player) sender) != null && PlayerHandler.getPlayer().getHandItem((Player) sender).getType() != Material.AIR) {
			LanguageAPI.getLang(false).dispatchMessage(sender, " ");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]-----------------&a&l[&e Item Info &a&l]&a&l&m----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			String[] placeHolders = LanguageAPI.getLang(false).newString(); 
			placeHolders[3] = PlayerHandler.getPlayer().getHandItem((Player) sender).getType().toString();
			LanguageAPI.getLang(false).sendLangMessage("Commands.Info.material", sender, placeHolders);
			if (!ServerHandler.getServer().hasSpecificUpdate("1_13")) {
				placeHolders[3] = LegacyAPI.getLegacy().getDataValue(PlayerHandler.getPlayer().getHandItem((Player) sender)) + "";
				LanguageAPI.getLang(false).sendLangMessage("Commands.Info.dataValue", sender, placeHolders);
			}
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Item Info Menu &a&l]&a&l&m--------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, " ");
		} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Item.notInHand", sender); }	
	}
	
   /**
	* Called when the CommandSender executes the list command.
	* @param sender - Source of the command. 
	* 
	*/
	private void list(final CommandSender sender) {
		LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
		for (World world: Bukkit.getWorlds()) {
			boolean itemFound = false;
			String[] placeHolders = LanguageAPI.getLang(false).newString();
			placeHolders[0] = world.getName();
			LanguageAPI.getLang(false).sendLangMessage("Commands.List.worldHeader", sender, placeHolders);
			List < String > inputListed = new ArrayList < String > ();
			for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
				if (!inputListed.contains(itemMap.getConfigName()) && itemMap.inWorld(world)) {
					placeHolders[3] = itemMap.getConfigName();
					placeHolders[4] = itemMap.getConfigName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.List.itemRow", sender, placeHolders);
					inputListed.add(itemMap.getConfigName());
					itemFound = true;
				}
			}
			if (!itemFound) {
				LanguageAPI.getLang(false).sendLangMessage("Commands.List.noItemsDefined", sender);
			}
		}
		LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e List Menu 1/1 &a&l]&a&l&m---------------[");
	}
	
   /**
	* Called when the CommandSender executes the Permisisons command.
	* @param sender - Source of the command. 
	* @param page - The page number to be displayed.
	* 
	*/
	private void permissions(final CommandSender sender, final int page) {
		LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
		int maxPage = 2;
		if (page == 1) {
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.*") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.*");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.all") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.All");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Use");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.reload") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Reload");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.updates") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Updates");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.autoupdate") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.AutoUpdate");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.Permissions") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Permissions");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Get");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.Remove") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Remove");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.enable") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Enable");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.disable") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Disable");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get.others") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Get.Others");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove.others") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Remove.Others");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.enable.others") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Enable.Others");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.disable.others") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Disable.Others");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.bypass.inventorymodify") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Bypass.InventoryModify");
			for (World world: Bukkit.getWorlds()) { 
				LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin." + world.getName()  + ".*")
					&& ((ConfigHandler.getConfig(false).getFile("config.yml").getBoolean("Permissions.Obtain-Items-OP") && sender.isOp() 
							? sender.isPermissionSet("itemjoin." + world.getName() + ".*") : !ConfigHandler.getConfig(false).getFile("config.yml").getBoolean("Permissions.Obtain-Items-OP"))
					||  (ConfigHandler.getConfig(false).getFile("config.yml").getBoolean("Permissions.Obtain-Items") && !sender.isOp() 
							? sender.isPermissionSet("itemjoin." + world.getName() + ".*") : !ConfigHandler.getConfig(false).getFile("config.yml").getBoolean("Permissions.Obtain-Items")))
				? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin." + world.getName() + ".*"); 
			}
		} else if (page == 2) {
			List < String > customPermissions = new ArrayList < String > ();
			for (World world: Bukkit.getServer().getWorlds()) {
				List < String > inputListed = new ArrayList < String > ();
				final ItemMap probable = Chances.getChances().getRandom(((Player) sender));
				for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
					if ((item.getPermissionNode() != null ? !customPermissions.contains(item.getPermissionNode()) : true) && !inputListed.contains(item.getConfigName()) && item.inWorld(world) && Chances.getChances().isProbability(item, probable)) {
						if (item.getPermissionNode() != null) { customPermissions.add(item.getPermissionNode()); }
						inputListed.add(item.getConfigName());
						if (item.hasPermission(((Player) sender))) {
							LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] " + PermissionsHandler.getPermissions().customPermissions(item.getPermissionNode(), item.getConfigName(), world.getName()));
						} else {
							LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] " + PermissionsHandler.getPermissions().customPermissions(item.getPermissionNode(), item.getConfigName(), world.getName()));
						}
					}
				}
			}
		}
		if (page != maxPage) { LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Permissions " + (page + 1) + " &afor the next page."); }
		LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]-------------&a&l[&e Permissions Menu " + page + "/" + maxPage + " &a&l]&a&l&m------------[");
	}
	
   /**
	* Called when the CommandSender executes the Purge command.
	* @param sender - Source of the command. 
	* @param player - The Player name having their data purged.
	* @param table - The table being purged of data.
	* 
	*/
	private HashMap < String, Boolean > confirmationRequests = new HashMap < String, Boolean > ();
	private void purge(final CommandSender sender, final String table, final String args) {
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args; placeHolders[10] = table; 
		OfflinePlayer foundPlayer = null;
		if (!table.equalsIgnoreCase("Database")) { 
			placeHolders[9] = "/ij purge " + table + (table.equalsIgnoreCase("map-ids") ? " <image>" : " <player>"); 
			if (!table.equalsIgnoreCase("map-ids")) { foundPlayer = LegacyAPI.getLegacy().getOfflinePlayer(args); }
			if (!table.equalsIgnoreCase("map-ids") && foundPlayer == null && !args.equalsIgnoreCase("ALL")) { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); return; } 
		} else { placeHolders[9] = "/ij purge"; }
		if (this.confirmationRequests.get(table + sender.getName()) != null && this.confirmationRequests.get(table + sender.getName()).equals(true)) {
			if (!table.equalsIgnoreCase("Database")) { SQLite.getLite(false).purgeDatabaseData(foundPlayer, args, table.replace("-", "_")); } 
			else {
				SQDrivers.getDatabase("database").purgeDatabase();
				SQLite.getLite(true);
			}
			LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeSuccess", sender, placeHolders);
			this.confirmationRequests.remove(table + sender.getName());
		} else {
			this.confirmationRequests.put(table + sender.getName(), true);
			LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeWarn", sender, placeHolders);
			LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeConfirm", sender, placeHolders);
			ServerHandler.getServer().runThread(main -> {
				if (this.confirmationRequests.get(table + sender.getName()) != null && this.confirmationRequests.get(table + sender.getName()).equals(true)) {
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeTimeOut", sender);
					this.confirmationRequests.remove(table + sender.getName());
				} 
			}, 100L);
		}
	}
	
   /**
	* Called when the CommandSender executes the Enable command.
	* @param sender - Source of the command. 
	* @param player - The player attempting to be disabled.
	* @param world - The world attempting to be disabled.
	* @param arguments - The max length of agruments in the command line.
	* 
	*/
	private void enable(final CommandSender sender, final String player, final String world, final int arguments) {
		Player argsPlayer = (arguments >= 2 ? PlayerHandler.getPlayer().getPlayerString(player) : null);
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = (arguments >= 2 ? player : sender.getName()); placeHolders[0] = world;
		if (arguments >= 2 && argsPlayer == null) { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); return; }
		if (!SQLite.getLite(false).isWritable((arguments == 3 ? world : "Global"), (arguments >= 2 ? PlayerHandler.getPlayer().getPlayerID(argsPlayer) : "ALL"), true)) {
			SQLite.getLite(false).saveToDatabase(argsPlayer, world, "true", "enabled-players");
			LanguageAPI.getLang(false).sendLangMessage("Commands.Enabled." + (arguments == 3 ? "forPlayerWorld" : (arguments == 2 ? "forPlayer" : "globalPlayers")), sender, placeHolders); 
			if (arguments >= 2 && !sender.getName().equalsIgnoreCase(argsPlayer.getName())) { 
				placeHolders[1] = sender.getName(); 
				LanguageAPI.getLang(false).sendLangMessage("Commands.Enabled." + (arguments == 3 ? "forTargetWorld" : "forTarget"), argsPlayer, placeHolders); 
			}
		} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Enabled." + (arguments == 3 ? "forPlayerWorldFailed" : (arguments == 2 ? "forPlayerFailed" : "globalPlayersFailed")), sender, placeHolders); }
	}
	
   /**
	* Called when the CommandSender executes the Disable command.
	* @param sender - Source of the command. 
	* @param player - The player attempting to be disabled.
	* @param world - The world attempting to be disabled.
	* @param arguments - The max length of agruments in the command line.
	* 
	*/
	private void disable(final CommandSender sender, final String player, final String world, final int arguments) {
		Player argsPlayer = (arguments >= 2 ? PlayerHandler.getPlayer().getPlayerString(player) : null);
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = (arguments >= 2 ? player : sender.getName()); placeHolders[0] = world;
		if (arguments >= 2 && argsPlayer == null) { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); return; }
		if (SQLite.getLite(false).isWritable((arguments == 3 ? world : "Global"), (arguments >= 2 ? PlayerHandler.getPlayer().getPlayerID(argsPlayer) : "ALL"), true)) {
			SQLite.getLite(false).saveToDatabase(argsPlayer, world, "false", "enabled-players");
			LanguageAPI.getLang(false).sendLangMessage("Commands.Disabled." + (arguments == 3 ? "forPlayerWorld" : (arguments == 2 ? "forPlayer" : "globalPlayers")), sender, placeHolders); 
			if (arguments >= 2 && !sender.getName().equalsIgnoreCase(argsPlayer.getName())) { 
				placeHolders[1] = sender.getName(); 
				LanguageAPI.getLang(false).sendLangMessage("Commands.Disabled." + (arguments == 3 ? "forTargetWorld" : "forTarget"), argsPlayer, placeHolders); 
			}
		} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Disabled." + (arguments == 3 ? "forPlayerWorldFailed" : (arguments == 2 ? "forPlayerFailed" : "globalPlayersFailed")), sender, placeHolders); }
	}
	
   /**
	* Called when the CommandSender executes the Get command.
	* @param sender - Source of the command. 
	* @param args - Passed command arguments.
	* @param remove - If the item is expected to be removed.
	* 
	*/
	private void handleItems(final CommandSender sender, final String[] args, final boolean remove) {
		Player argsPlayer = (args.length >= 3 && !Utils.getUtils().isInt(args[2]) ? PlayerHandler.getPlayer().getPlayerString(args[2]) : (Player)sender);
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = (args.length >= 3 && !Utils.getUtils().isInt(args[2]) && argsPlayer != null ? argsPlayer.getName() : (args.length >= 3 ? args[2] : sender.getName())); placeHolders[3] = args[1];
		int amount = (args.length >= 3 && Utils.getUtils().isInt(args[args.length - 1]) ? Integer.parseInt(args[args.length - 1]) : 0);
		if (args.length >= 3 && !Utils.getUtils().isInt(args[2]) && argsPlayer == null) { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); return; }
		boolean messageSent = false;
		ItemMap itemMapExist = ItemUtilities.getUtilities().getItemMap(null, args[1], argsPlayer.getWorld());
		if (itemMapExist == null) { LanguageAPI.getLang(false).sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); return; }
		for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
			if (itemMap.getConfigName().equalsIgnoreCase(args[1])) {
				String customName = Utils.getUtils().translateLayout(itemMap.getCustomName(), argsPlayer); placeHolders[3] = customName;
				if ((remove && itemMap.hasItem(argsPlayer)) || (!remove && (amount != 0 || itemMap.isAlwaysGive() || !itemMap.hasItem(argsPlayer)))) {
					if (remove || !PermissionsHandler.getPermissions().receiveEnabled() || (itemMap.hasPermission(argsPlayer) && PermissionsHandler.getPermissions().receiveEnabled())) {
						if (itemMap.isAlwaysGive() && (args.length < 2 || (!Utils.getUtils().isInt(args[args.length - 1])))) { amount = itemMap.getCount(); }
						if (remove) { itemMap.removeFrom(argsPlayer, amount); } 
						else        { itemMap.giveTo(argsPlayer, amount); }
						placeHolders[11] = Integer.toString((amount == 0 ? 1 : amount)); placeHolders[1] = sender.getName();
						if (!messageSent) { LanguageAPI.getLang(false).sendLangMessage("Commands." + (remove ? "Remove.fromYou" : "Get.toYou"), argsPlayer, placeHolders); }
						if (!messageSent && (args.length >= 3 && !Utils.getUtils().isInt(args[2]) && !sender.getName().equalsIgnoreCase(argsPlayer.getName()))) { placeHolders[1] = argsPlayer.getName(); LanguageAPI.getLang(false).sendLangMessage("Commands.Get.toTarget", sender, placeHolders); }
						Crafting.quickSave(argsPlayer);
					} else if (!remove && !messageSent) {
						LanguageAPI.getLang(false).sendLangMessage("Commands.Get." + (args.length >= 3 && !Utils.getUtils().isInt(args[2]) && !sender.getName().equalsIgnoreCase(argsPlayer.getName()) ? "targetNoPermission" : "noPermission"), sender, placeHolders);
					}
				} else if (!messageSent && (args.length >= 3 && !Utils.getUtils().isInt(args[2]) && !sender.getName().equalsIgnoreCase(argsPlayer.getName()))) {
					placeHolders[1] = sender.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands." + (remove ? "Remove.triedRemove" : "Get.triedGive"), argsPlayer, placeHolders);
					placeHolders[1] = argsPlayer.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands." + (remove ? "Remove.notInTargetInventory" : "Get.targetHasItem"), sender, placeHolders);
				} else if (!messageSent) {
					placeHolders[1] = sender.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands." + (remove ? "Remove.notInInventory" : "Get.youHaveItem"), sender, placeHolders);
				}
				if (!messageSent) { messageSent = true; }
			}
		}
	}
	
   /**
	* Called when the CommandSender executes the removeOnline command.
	* @param sender - Source of the command. 
	* @param args - Passed command arguments.
	* @param remove - If the item is expected to be removed.
	* 
	*/
	private void handleOnline(final CommandSender sender, final String[] args, final boolean remove) {
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = args[1];
		List < String > handledPlayers = new ArrayList < String > ();
		List < String > failedPlayers = new ArrayList < String > ();
		int amount = (args.length == 3 ? Integer.parseInt(args[2]) : 0);
		PlayerHandler.getPlayer().forOnlinePlayers(argsPlayer -> {
			boolean messageSent = false;
			ItemMap itemMapExist = ItemUtilities.getUtilities().getItemMap(null, args[1], argsPlayer.getWorld());
			if (itemMapExist == null) { LanguageAPI.getLang(false).sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); return; }
			placeHolders[3] = Utils.getUtils().translateLayout(itemMapExist.getCustomName(), argsPlayer); placeHolders[1] = sender.getName();
			placeHolders[11] = (amount == 0 ? "&lAll" : Integer.toString(amount));
			for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
				if (itemMap.getConfigName().equalsIgnoreCase(args[1])) {
					if (remove || !PermissionsHandler.getPermissions().receiveEnabled() || (itemMap.hasPermission(argsPlayer) && PermissionsHandler.getPermissions().receiveEnabled())) {
						if ((remove && itemMap.hasItem(argsPlayer)) || (!remove && (amount != 0 || itemMap.isAlwaysGive() || !itemMap.hasItem(argsPlayer)))) {
							if (remove) { itemMap.removeFrom(argsPlayer, amount); }
							else { itemMap.giveTo(argsPlayer, amount); }
							if (!messageSent && !sender.getName().equalsIgnoreCase(argsPlayer.getName())) { LanguageAPI.getLang(false).sendLangMessage("Commands." + (remove ? "Remove.fromYou" : "Get.toYou"), argsPlayer, placeHolders); }
							if (!messageSent && !handledPlayers.contains(argsPlayer.getName())) { handledPlayers.add(argsPlayer.getName()); }
							Crafting.quickSave(argsPlayer);
						} else if (!messageSent) { 
							if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) { LanguageAPI.getLang(false).sendLangMessage("Commands." + (remove ? "Remove.triedRemove" : "Get.triedGive"), argsPlayer, placeHolders); }
							if (!failedPlayers.contains(argsPlayer.getName())) { failedPlayers.add(argsPlayer.getName()); }
						}
						if (!messageSent) { messageSent = true; }
					}
				}
			}
		});	
		placeHolders[12] = handledPlayers.toString().replace("]", "").replace("[", "");
		if (!handledPlayers.isEmpty()) {
			LanguageAPI.getLang(false).sendLangMessage("Commands." + (remove ? "Remove.fromOnlinePlayers" : "Get.toOnlinePlayers"), sender, placeHolders);
		} else if (!failedPlayers.isEmpty()) {
			LanguageAPI.getLang(false).sendLangMessage("Commands." + (remove ? "Remove.notInOnlinePlayersInventory" : "Get.onlinePlayersHaveItem"), sender, placeHolders);
		}
	}
	
   /**
	* Called when the CommandSender executes the removeOnline command.
	* @param sender - Source of the command. 
	* @param args - Passed command arguments.
	* @param remove - If the item is expected to be removed.
	* 
	*/
	private void handleAllItems(final CommandSender sender, final String[] args, final boolean remove) {
		Player argsPlayer = (args.length >= 2 ? PlayerHandler.getPlayer().getPlayerString(args[1]) : (Player) sender);
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = (args.length >= 2 ? args[1] : sender.getName());
		if (argsPlayer == null) { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); return; }
		boolean itemGiven = false; boolean failedPermissions = false;
		final ItemMap probable = Chances.getChances().getRandom(argsPlayer);
		for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
			if ((!remove ? (itemMap.inWorld(argsPlayer.getWorld()) && Chances.getChances().isProbability(itemMap, probable) && 
				(!PermissionsHandler.getPermissions().receiveEnabled() || (itemMap.hasPermission(argsPlayer) && PermissionsHandler.getPermissions().receiveEnabled()))) : remove)) {
				if ((remove && itemMap.hasItem(argsPlayer)) || ((!remove && !itemMap.hasItem(argsPlayer)) || (!remove && itemMap.isAlwaysGive()))) {
					if (remove) { itemMap.removeFrom(argsPlayer); } 
					else { itemMap.giveTo(argsPlayer); }
					if (!itemGiven) { itemGiven = true; }
					Crafting.quickSave(argsPlayer);
				}
			} else if (!failedPermissions && !itemMap.hasPermission(argsPlayer)) { failedPermissions = true; }
		}
		if (itemGiven) {
			failedPermissions = false;
			placeHolders[1] = sender.getName();
			LanguageAPI.getLang(false).sendLangMessage("Commands." + (remove ? "RemoveAll.fromYou": "GetAll.toYou"), argsPlayer, placeHolders);
			if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) { placeHolders[1] = argsPlayer.getName(); LanguageAPI.getLang(false).sendLangMessage("Commands." + (remove ? "RemoveAll.fromTarget": "GetAll.toTarget"), sender, placeHolders); }
		} else {
			placeHolders[1] = argsPlayer.getName();
			LanguageAPI.getLang(false).sendLangMessage("Commands." + (!sender.getName().equalsIgnoreCase(argsPlayer.getName()) ? (remove ? "RemoveAll.noItemsInTargetInventory" : "GetAll.targetHasItems") : (remove ? "RemoveAll.noItemsInInventory" : "GetAll.youHaveItems")), sender, placeHolders);
			if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) { placeHolders[1] = sender.getName(); LanguageAPI.getLang(false).sendLangMessage("Commands." + (remove ? "RemoveAll.triedRemove": "GetAll.triedGive"), argsPlayer, placeHolders); }
		}
		if (failedPermissions) { placeHolders[1] = argsPlayer.getName(); LanguageAPI.getLang(false).sendLangMessage("Commands.GetAll." + (!sender.getName().equalsIgnoreCase(argsPlayer.getName()) ? "targetNoPermission" : "noPermission"), sender, placeHolders); }
	}
	
   /**
	* Defines the config Command type for the command.
	* 
	*/
	public enum Execute {
		DEFAULT("", "itemjoin.use", false),
		HELP("help", "itemjoin.use", false),
		RELOAD("rl, reload", "itemjoin.reload", false),
		MENU("menu, creator", "itemjoin.menu", true),
		INFO("info", "itemjoin.use", true),
		WORLD("world, worlds", "itemjoin.use", true),
		LIST("list", "itemjoin.list", false),
		PERMISSIONS("permission, permissions", "itemjoin.permissions", true),
		PURGE("purge", "itemjoin.purge", false),
		ENABLE("enable", "itemjoin.enable, itemjoin.enable.others", false),
		DISABLE("disable", "itemjoin.disable, itemjoin.disable.others", false),
		GET("get", "itemjoin.get, itemjoin.get.others", true),
		GETALL("getAll", "itemjoin.get, itemjoin.get.others", true),
		GETONLINE("getOnline", "itemjoin.get.others", false),
		REMOVE("remove", "itemjoin.remove, itemjoin.remove.others", true),
		REMOVEALL("removeAll", "itemjoin.remove, itemjoin.remove.others", true),
		REMOVEONLINE("removeOnline", "itemjoin.remove.others", false),
		UPDATE("update, updates", "itemjoin.updates", false),
		AUTOUPDATE("autoupdate", "itemjoin.autoupdate", false);
		private final String command;
		private final String permission;
		private final boolean player;
		
       /**
	    * Creates a new Execute instance.
	    * @param command - The expected command argument. 
	    * @param permission - The expected command permission requirement.
	    * @param player - If the command is specific to a player instance, cannot be executed by console.
	    * 
	    */
		private Execute(final String command, final String permission, final boolean player) { 
			this.command = command; this.permission = permission; this.player = player; 
		}
		
       /**
	    * Called when the CommandSender executes a command.
	    * @param sender - Source of the command. 
	    * @param args - Passed command arguments.
	    * @param page - The page number to be expected.
	    * 
	    */
		public boolean accept(final CommandSender sender, final String[] args, final int page) { 
			return (args.length == 0 || (Utils.getUtils().splitIgnoreCase(this.command, args[0]) 
			  && this.hasSyntax(args, page)))
			  && this.playerRequired(sender, args)
			  && this.hasPermission(sender, args); 
		}
		
       /**
	    * Checks if the executed command is the same as the executor.
	    * @param sender - Source of the command. 
	    * @param args - Passed command arguments.
	    * @param page - The page number to be expected.
	    * 
	    */
		public boolean acceptArgs(final String[] args) {
			return Utils.getUtils().splitIgnoreCase(this.command, args[0]);
		}
		
       /**
	    * Checks if the Command being executed has the proper formatting or syntax.
	    * @param args - Passed command arguments.
	    * @param page - The page number to be expected.
	    * 
	    */
		private boolean hasSyntax(final String[] args, final int page) {
			return ((args.length >= 2 && (args[1].equalsIgnoreCase(String.valueOf(page)) || (!Utils.getUtils().isInt(args[1]) && !this.equals(Execute.PURGE)))) 
				 || (args.length < 2 && (!this.equals(Execute.GET) && !this.equals(Execute.GETONLINE) && !this.equals(Execute.REMOVE) && !this.equals(Execute.REMOVEONLINE))
				 || (this.equals(Execute.PURGE) && (args.length == 1 
				 || (args.length >= 3 && (args[1].equalsIgnoreCase("map-ids") || args[1].equalsIgnoreCase("ip-limits") || args[1].equalsIgnoreCase("first-join") || args[1].equalsIgnoreCase("first-world") || args[1].equalsIgnoreCase("enabled-players")))))));
		}
		
       /**
	    * Checks if the Player has permission to execute the Command.
	    * @param sender - Source of the command. 
	    * @param args - Passed command arguments.
	    * 
	    */
		public boolean hasPermission(final CommandSender sender, final String[] args) {
			String[] permissions = this.permission.replace(" ", "").split(",");
			boolean multiPerms = this.permission.contains(",");
			return (multiPerms && ((((!this.equals(Execute.GET) && !this.equals(Execute.REMOVE)) && ((args.length >= 2 && !Utils.getUtils().isInt(args[1]) && PermissionsHandler.getPermissions().hasPermission(sender, permissions[1])) 
			   || ((args.length < 2 || (args.length >= 2 && Utils.getUtils().isInt(args[1]))) && PermissionsHandler.getPermissions().hasPermission(sender, permissions[0]))))) 
			   || (((this.equals(Execute.GET) || this.equals(Execute.REMOVE)) && (((args.length == 3 && Utils.getUtils().isInt(args[2])) || args.length == 2)) && PermissionsHandler.getPermissions().hasPermission(sender, permissions[0])) 
			   || (((args.length == 3 && !Utils.getUtils().isInt(args[2])) || args.length >= 3)) && PermissionsHandler.getPermissions().hasPermission(sender, permissions[1]))))
		       || (!multiPerms && PermissionsHandler.getPermissions().hasPermission(sender, this.permission));
		}
		
       /**
	    * Checks if the Command requires the instance to be a Player.
	    * @param sender - Source of the command. 
	    * @param args - Passed command arguments.
	    * 
	    */
		public boolean playerRequired(final CommandSender sender, final String[] args) {
			return (!this.player
				|| (!(sender instanceof ConsoleCommandSender)) 
				|| !(this.player && (((this.equals(Execute.GETALL) || this.equals(Execute.REMOVEALL)) && args.length < 2)
				|| (this.equals(Execute.GET) || this.equals(Execute.REMOVE)) && ((args.length == 3 && Utils.getUtils().isInt(args[2])) || args.length == 2))));
		}
	}
}