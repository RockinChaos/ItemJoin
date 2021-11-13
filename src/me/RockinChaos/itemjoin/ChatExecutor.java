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

import me.RockinChaos.itemjoin.ChatComponent.ClickAction;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.UpdateHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.api.ChanceAPI;
import me.RockinChaos.itemjoin.utils.api.LanguageAPI;
import me.RockinChaos.itemjoin.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.utils.interfaces.menus.Menu;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.SQL;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;

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
			LanguageAPI.getLang(false).dispatchMessage(sender, ("&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos"), "&bThis should be the version submitted to the developer \n&bwhen submitting a bug or feature request.", "https://github.com/RockinChaos/ItemJoin/issues", ClickAction.OPEN_URL);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help &afor the help menu.", "&eClick to View the Help Menu.", "/itemjoin help", ClickAction.RUN_COMMAND);
		} else if (Execute.HELP.accept(sender, args, 1)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
            LanguageAPI.getLang(false).dispatchMessage(sender, ("&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos"), "&bThis should be the version submitted to the developer \n&bwhen submitting a bug or feature request.", "https://github.com/RockinChaos/ItemJoin/issues", ClickAction.OPEN_URL);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Help &7- &eThis help menu.", "&aExecuting this command shows this help menu!", "/itemjoin help", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Reload &7- &eReloads the .yml files.", "&aFully reloads the plugin, fetching \n&aany changes made to the .yml files. \n\n&aBe sure to save changes made to your .yml files!", "/itemjoin reload", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Updates &7- &eChecks for plugin updates.", "&aChecks to see if there are any updates available for this plugin.", "/itemjoin updates", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Upgrade &7- &eUpdates to latest version.", "&aAttempts to Upgrade this plugin to the latest version. \n&aYou will need to restart the server for this process to complete.", "/itemjoin upgrade", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 2 &afor the next page.", "&eClick to View the Next Page.", "/itemjoin help 2", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 1/10 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 2)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin List &7- &eView existing items and their worlds.", "&aView an entire list of the existing \n&acustom items and their respective worlds.", "/itemjoin list", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin World &7- &eCheck what world you are in, debugging.", "&aDisplays the world that your Player is currently in. \n&aUseful for debugging, such as comparing to your enabled-worlds.", "/itemjoin world", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Menu &7- &eOpens the GUI Creator for custom items.", "&aCreate custom items in-game without the need \n&ato manually edit the .yml files.", "/itemjoin menu", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Permissions &7- &eLists your permissions.", "&aLists the Permissions for your Player. \n\n&aGreen &bmeans you have permission whereas \n&cRed &bmeans you do not have permission.", "/itemjoin permissions", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Query <Item> &7- &eDisplays the custom item data.", "&aDisplays the important item info for the existing custom item-node.", "/itemjoin query ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Info &7- &eGets data-info of the held item.", "&aDisplays the important item info for the item you are currently holding. \n&aUseful for finding the data-value and id of the item to be used.", "/itemjoin info", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 3 &afor the next page.", "&eClick to View the Next Page.", "/itemjoin help 3", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 2/10 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 3)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Get <Item> &7- &eGives that ItemJoin item.", "&aGives you the custom item in its designated slot. \n\n&aThis will not work if you already have the item.", "/itemjoin get ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <Qty> &7- &eGives amount of said item.", "&aGives you the custom item in its designated slot with the specified quantity. \n\n&aThis &cWILL &awork if you already have the item.", "/itemjoin get ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <User> &7- &eGives to said player.", "&aGives the custom item to the specified player name. \n\n&aThis will not work if they already have the item.", "/itemjoin get ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <User> <Qty> &7- &eGives qty to player.", "&aGives the custom item to the specified player name with the specified quantity. \n\n&aThis &cWILL &awork if they already have the item.", "/itemjoin get ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 4 &afor the next page.", "&eClick to View the Next Page.", "/itemjoin help 4", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 3/10 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 4)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> &7- &eRemoves item from inventory.", "&aRemoves the custom item from your inventory. \n\n&aThis will not work if you do not have the item.", "/itemjoin remove ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <Qty> &7- &eRemoves qty of item.", "&aRemoves the custom item from your inventory with the specified quantity. \n\n&aThis will not work if you do not have the item.", "/itemjoin remove ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <Qty> &7- &eRemoves qty of item.", "&aRemoves the custom item from the specified player name. \n\n&aThis will not work if they do not have the item.", "/itemjoin remove ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <User> <Qty> &7- &eRemoves qty.", "&aRemoves the custom item from the specified player name with the specified quantity. \n\n&aThis will not work if they do not have the item.", "/itemjoin remove ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 5 &afor the next page.", "&eClick to View the Next Page.", "/itemjoin help 5", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 4/10 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 5)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetOnline <Item> &7- &eGives to all online.", "&aGives the custom item to all online players.", "/itemjoin getonline ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetOnline <Item> <Qty> &7- &eGives qty to all online.", "&aGives the custom item with the specified quantity to all online players.", "/itemjoin getonline ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveOnline <Item> &7- &eRemoves from all online.", "&aRemoves the custom item from all online players.", "/itemjoin removeonline ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveOnline <Item> <Qty> &7- &eRemoves qty.", "&aRemoves the custom item with the specified quantity from all online players.", "/itemjoin removeonline ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 6 &afor the next page.", "&eClick to View the Next Page.", "/itemjoin help 6", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 5/10 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 6)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetAll &7- &eGives all ItemJoin items.", "&aGives you all the custom items.", "/itemjoin getall", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetAll <User> &7- &eGives all items to said player.", "&aGives all custom items to the specified player name.", "/itemjoin getall ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveAll &7- &eRemoves all ItemJoin items.", "&aRemoves all custom items from your inventory.", "/itemjoin removeall", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveAll <User> &7- &eRemoves from player.", "&aRemoves all custom items from the specified player name.", "/itemjoin removeall ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 7 &afor the next page.", "&eClick to View the Next Page.", "/itemjoin help 7", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 6/10 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 7)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Enable &7- &eEnables ItemJoin for all players.", "&aEnables ItemJoin for all players. \nPlayers will be able to get custom items.", "/itemjoin enable", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Enable <User> &7- &eEnables ItemJoin for player.", "&aEnables ItemJoin for the specified player name. \nThis player will be able to get custom items.", "/itemjoin enable ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Enable <User> <World> &7- &eFor player/world.", "&aEnables ItemJoin for the specified player name in the specified world. \nThis player will be able to get custom items in this world.", "/itemjoin enable ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 8 &afor the next page.", "&eClick to View the Next Page.", "/itemjoin help 8", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 7/10 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 8)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Disable &7- &eDisables ItemJoin for all players.", "&aDisables ItemJoin for all players. \nPlayers will NOT be able to get custom items.", "/itemjoin disable", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Disable <User> &7- &eDisables ItemJoin for player.", "&aDisables ItemJoin for the specified player name. \nThis player will NOT be able to get custom items.", "/itemjoin disable ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Disable <User> <World> &7- &eFor player/world.", "&aDisables ItemJoin for the specified player name in the specified world. \nThis player will NOT be able to get custom items in this world.", "/itemjoin disable ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 9 &afor the next page.", "&eClick to View the Next Page.", "/itemjoin help 9", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 8/10 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 9)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge &7- &eDeletes the database file.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges ALL Player Data from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge map-id <Image> &7- &eMap-Images data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the map-id data for the custom-image from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge map-id ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge first-join <User> &7- &eFirst-Join data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the first-join data for the player name from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge first-join ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge first-world <User> &7- &eFirst-World data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the first-world data for the player name from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge first-world ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge ip-limits <User> &7- &eIp-Limits data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the ip-limits data for the player name from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge ip-limits ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 10 &afor the next page.", "&eClick to View the Next Page.", "/itemjoin help 10", ClickAction.RUN_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]--------------&a&l[&e Help Menu 9/10 &a&l]&a&l&m---------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.HELP.accept(sender, args, 10)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge enabled-players <User> &7- &eThe Data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the enabled-players data for the player name from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge enabled-players ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge first-commands <User> &7- &eThe Data.", "&c&l[DANGER] &eThe Following Destroys Data &nPermanently!&e&c&l [DANGER] \n\n&aPurges the first-commands data for the player name from the Database file! \n\n&c&n&lTHIS CANNOT BE UNDONE.", "/itemjoin purge first-commands ", ClickAction.SUGGEST_COMMAND);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&aFound a bug? Report it @");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&ahttps://github.com/RockinChaos/ItemJoin/issues", "&eClick to Submit a Bug or Feature Request.", "https://github.com/RockinChaos/ItemJoin/issues", ClickAction.OPEN_URL);
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Help Menu 10/10 &a&l]&a&l&m--------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
		} else if (Execute.RELOAD.accept(sender, args, 0)) {
			ItemHandler.saveCooldowns();
			ConfigHandler.getConfig().reloadConfigs(false);
			LanguageAPI.getLang(false).sendLangMessage("commands.default.configReload", sender);
		} else if (Execute.MENU.accept(sender, args, 0)) {
			Menu.startMenu(sender);
			LanguageAPI.getLang(false).sendLangMessage("commands.menu.openMenu", sender);
		} else if (Execute.INFO.accept(sender, args, 0)) {
			this.info(sender);
		} else if (Execute.QUERY.accept(sender, args, 0)) {
			this.query(sender, args);
		} else if (Execute.WORLD.accept(sender, args, 0)) {
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[0] = ((Player) sender).getWorld().getName();
			LanguageAPI.getLang(false).sendLangMessage("commands.world.worldHeader", sender, placeHolders);
			LanguageAPI.getLang(false).sendLangMessage("commands.world.worldRow", sender, placeHolders);
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]--------------&a&l[&e Worlds In Menu 1/1 &a&l]&a&l&m-------------[");
		} else if (Execute.LIST.accept(sender, args, 1)) {
			this.list(sender, 1);
		} else if (Execute.LIST.accept(sender, args, 2)) {
			this.list(sender, Integer.parseInt(args[1]));
		} else if (Execute.PERMISSIONS.accept(sender, args, 1)) {
			this.permissions(sender, 1);
		} else if (Execute.PERMISSIONS.accept(sender, args, 2)) {
			this.permissions(sender, Integer.parseInt(args[1]));
		} else if (Execute.PURGE.accept(sender, args, 0)) {
			if (args.length == 1) { this.purge(sender, "Database", "All Players"); } 
			else if (args[1].equalsIgnoreCase("map-ids") || args[1].equalsIgnoreCase("ip-limits") || args[1].equalsIgnoreCase("first-join") || args[1].equalsIgnoreCase("first-world") || args[1].equalsIgnoreCase("enabled-players") 
				  || args[1].equalsIgnoreCase("first-commands")) { 
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
			LanguageAPI.getLang(false).sendLangMessage("commands.updates.checkRequest", sender);
			SchedulerUtils.runAsync(() -> {
				UpdateHandler.getUpdater(false).checkUpdates(sender, false); 
			});
		} else if (Execute.UPGRADE.accept(sender, args, 0)) {
			LanguageAPI.getLang(false).sendLangMessage("commands.updates.updateRequest", sender);
			SchedulerUtils.runAsync(() -> {
				UpdateHandler.getUpdater(false).forceUpdates(sender); 
			});
		} else if (this.matchExecutor(args) == null) {
			LanguageAPI.getLang(false).sendLangMessage("commands.default.unknownCommand", sender);
		} else if (!this.matchExecutor(args).playerRequired(sender, args)) {
			LanguageAPI.getLang(false).sendLangMessage("commands.default.noPlayer", sender);
			Execute executor = this.matchExecutor(args);
			if (executor.equals(Execute.GET))            { LanguageAPI.getLang(false).sendLangMessage("commands.get.usageSyntax", sender); } 
			else if (executor.equals(Execute.GETALL))    { LanguageAPI.getLang(false).sendLangMessage("commands.get.usageSyntax", sender); } 
			else if (executor.equals(Execute.REMOVE))    { LanguageAPI.getLang(false).sendLangMessage("commands.remove.usageSyntax", sender); } 
			else if (executor.equals(Execute.REMOVEALL)) { LanguageAPI.getLang(false).sendLangMessage("commands.remove.usageSyntax", sender); }
		} else if (!this.matchExecutor(args).hasSyntax(args, 0)) {
			Execute executor = this.matchExecutor(args);
			if (executor.equals(Execute.GET))               { LanguageAPI.getLang(false).sendLangMessage("commands.get.badSyntax", sender); } 
			else if (executor.equals(Execute.GETONLINE))    { LanguageAPI.getLang(false).sendLangMessage("commands.get.badOnlineSyntax", sender); } 
			else if (executor.equals(Execute.REMOVE))       { LanguageAPI.getLang(false).sendLangMessage("commands.remove.badSyntax", sender); } 
			else if (executor.equals(Execute.REMOVEONLINE)) { LanguageAPI.getLang(false).sendLangMessage("commands.remove.badOnlineSyntax", sender); }
			else if (executor.equals(Execute.PURGE))        { LanguageAPI.getLang(false).sendLangMessage("commands.default.unknownCommand", sender); }
			else if (executor.equals(Execute.QUERY))        { LanguageAPI.getLang(false).sendLangMessage("commands.query.badSyntax", sender); }
		} else if (!this.matchExecutor(args).hasPermission(sender, args)) {
			LanguageAPI.getLang(false).sendLangMessage("commands.default.noPermission", sender);
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
		if (PlayerHandler.getHandItem((Player) sender) != null && PlayerHandler.getHandItem((Player) sender).getType() != Material.AIR) {
			LanguageAPI.getLang(false).dispatchMessage(sender, " ");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]-----------------&a&l[&e Item Info &a&l]&a&l&m----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			String[] placeHolders = LanguageAPI.getLang(false).newString(); 
			placeHolders[3] = PlayerHandler.getHandItem((Player) sender).getType().toString();
			LanguageAPI.getLang(false).sendLangMessage("commands.info.material", sender, placeHolders);
			if (!ServerUtils.hasSpecificUpdate("1_13")) {
				placeHolders[3] = LegacyAPI.getDataValue(PlayerHandler.getHandItem((Player) sender)) + "";
				LanguageAPI.getLang(false).sendLangMessage("commands.info.data", sender, placeHolders);
			}
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]-----------------&a&l[&e Item Info &a&l]&a&l&m----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, " ");
		} else { LanguageAPI.getLang(false).sendLangMessage("commands.item.noItemHeld", sender); }	
	}
	
   /**
	* Called when the CommandSender executes the Query command.
	* @param sender - Source of the command. 
	* @param args - Passed command arguments.
	* 
	*/
	private void query(final CommandSender sender, final String[] args) {
		final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(null, args[1], null);
		String[] placeHolders = LanguageAPI.getLang(false).newString(); 
		placeHolders[3] = (itemMap != null ? itemMap.getConfigName() : args[1]);
		if (itemMap != null) {
			placeHolders[4] = ((itemMap.getDynamicMaterials() == null || itemMap.getDynamicMaterials().isEmpty()) ? itemMap.getMaterial().name() : itemMap.getDynamicMaterials().toString().replace("[", "").replace("{", "").replace("]", "").replace("}", ""));
			placeHolders[17] = ((itemMap.getMultipleSlots() == null || itemMap.getMultipleSlots().isEmpty()) ? itemMap.getSlot() : itemMap.getMultipleSlots().toString().replace("[", "").replace("{", "").replace("]", "").replace("}", ""));
			if (sender instanceof Player) {
				if (itemMap.hasPermission(((Player) sender), ((Player) sender).getWorld())) {
					placeHolders[18] = "&a[\u2714] " + PermissionsHandler.customPermissions(itemMap.getPermissionNode(), itemMap.getConfigName(), ((Player) sender).getWorld().getName());
				} else {
					placeHolders[18] = "&c[\u2718] " + PermissionsHandler.customPermissions(itemMap.getPermissionNode(), itemMap.getConfigName(), ((Player) sender).getWorld().getName());
				}
			} else { 
				placeHolders[18] = "&a[\u2714] " + PermissionsHandler.customPermissions(itemMap.getPermissionNode(), itemMap.getConfigName(), ((Player) sender).getWorld().getName());
			}
			LanguageAPI.getLang(false).dispatchMessage(sender, " ");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]-----------------&a&l[&e Query Data &a&l]&a&l&m----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).sendLangMessage("commands.query.node", sender, placeHolders);
			LanguageAPI.getLang(false).sendLangMessage("commands.query.material", sender, placeHolders);
			LanguageAPI.getLang(false).sendLangMessage("commands.query.slot", sender, placeHolders);
			LanguageAPI.getLang(false).sendLangMessage("commands.query.permission", sender, placeHolders);
			LanguageAPI.getLang(false).dispatchMessage(sender, "");
			LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]-----------------&a&l[&e Query Data &a&l]&a&l&m----------------[");
			LanguageAPI.getLang(false).dispatchMessage(sender, " ");
		} else { 
			LanguageAPI.getLang(false).sendLangMessage("commands.item.noItem", sender, placeHolders);	
		}
	}
	
   /**
	* Called when the CommandSender executes the list command.
	* @param sender - Source of the command. 
	* 
	*/
	private void list(final CommandSender sender, final int page) {
		LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
		int maxPage = ConfigHandler.getConfig().getListPages();
		int lineCount = 0;
		boolean worldSent = false;
		for (World world: Bukkit.getWorlds()) {
			if (((lineCount + 1) != (page * 15))) {
			boolean itemFound = false;
			String[] placeHolders = LanguageAPI.getLang(false).newString();
			placeHolders[0] = world.getName();
			if (!(lineCount > (page * 15)) && (page == 1 || !(lineCount < ((page - 1) * 15)))) {
				LanguageAPI.getLang(false).sendLangMessage("commands.list.worldHeader", sender, placeHolders);
				lineCount++;
				worldSent = true;
			} else { lineCount++; }
			List < String > inputListed = new ArrayList < String > ();
			for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
				if (!inputListed.contains(itemMap.getConfigName()) && itemMap.inWorld(world)) {
					if ((page == 1 && !(lineCount >= (page * 15))) || (page != 1 && !(lineCount > (page * 15))) && (page == 1 || !(lineCount < ((page - 1) * 15)))) {
						if (!worldSent) { 
							placeHolders[0] = world.getName();
							if (!(lineCount > (page * 15)) && (page == 1 || !(lineCount < ((page - 1) * 15)))) {
								LanguageAPI.getLang(false).sendLangMessage("commands.list.worldHeader", sender, placeHolders);
								lineCount++;
								worldSent = true;
							}
						}
						placeHolders[3] = itemMap.getConfigName();
						placeHolders[4] = itemMap.getConfigName();
						inputListed.add(itemMap.getConfigName());
						LanguageAPI.getLang(false).sendLangMessage("commands.list.itemRow", sender, placeHolders);
						lineCount++;
					} else { lineCount++; }
					itemFound = true;
				}
			}
			if (!itemFound) {
				LanguageAPI.getLang(false).sendLangMessage("commands.list.noItems", sender);
				lineCount++;
			}
			}
		}
		if (page != maxPage) { LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin List " + (page + 1) + " &afor the next page.", "&eClick to View the Next Page.", "/itemjoin list " + (page + 1), ClickAction.RUN_COMMAND); }
		LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e List Menu " + page + "/" + maxPage + " &a&l]&a&l&m---------------[");
	}
	
   /**
	* Called when the CommandSender executes the Permisisons command.
	* @param sender - Source of the command. 
	* @param page - The page number to be displayed.
	* 
	*/
	private void permissions(final CommandSender sender, final int page) {
		LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
		int maxPage = ConfigHandler.getConfig().getPermissionPages();
		if (page == 1) {
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.*") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.*");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.all") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.All");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.use") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Use");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.reload") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Reload");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.updates") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Updates");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.upgrade") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Upgrade");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.permissions") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Permissions");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.query") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Query");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.get") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Get");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.remove") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Remove");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.enable") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Enable");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.disable") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Disable");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.get.others") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Get.Others");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.remove.others") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Remove.Others");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.enable.others") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Enable.Others");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.disable.others") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Disable.Others");
			LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin.bypass.inventorymodify") ? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin.Bypass.InventoryModify");
			for (World world: Bukkit.getWorlds()) { 
				LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.hasPermission(sender, "itemjoin." + world.getName()  + ".*")
					&& ((ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items-OP") && sender.isOp() 
							? sender.isPermissionSet("itemjoin." + world.getName() + ".*") : !ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items-OP"))
					||  (ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items") && !sender.isOp() 
							? sender.isPermissionSet("itemjoin." + world.getName() + ".*") : !ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items")))
				? "&a[\u2714]" : "&c[\u2718]") + " ItemJoin." + world.getName() + ".*"); 
			}
		} else if (page != 0) {
			List < String > customPermissions = new ArrayList < String > ();
			List < String > inputMessage = new ArrayList < String > ();
			for (World world: Bukkit.getServer().getWorlds()) {
				final ItemMap probable = ChanceAPI.getChances().getRandom(((Player) sender));
				for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
					if ((item.getPermissionNode() != null ? !customPermissions.contains(item.getPermissionNode()) : true) && item.inWorld(world) && ChanceAPI.getChances().isProbability(item, probable)) {
						if (item.getPermissionNode() != null) { customPermissions.add(item.getPermissionNode()); }
						if (item.hasPermission(((Player) sender), world)) {
							inputMessage.add("&a[\u2714] " + PermissionsHandler.customPermissions(item.getPermissionNode(), item.getConfigName(), world.getName()));
						} else {
							inputMessage.add("&c[\u2718] " + PermissionsHandler.customPermissions(item.getPermissionNode(), item.getConfigName(), world.getName()));
						}
					}
				}
			}
			for (int i = (page == 2 ? 0 : ((page - 2) * 15) + 1); i <= ((page - 1) * 15 <= inputMessage.size() ? (page - 1) * 15 : (inputMessage.size() - 1)); i++) {
				LanguageAPI.getLang(false).dispatchMessage(sender, inputMessage.get(i));
			}
		}
		if (page != maxPage) { LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Permissions " + (page + 1) + " &afor the next page.", "&eClick to View the Next Page.", "/itemjoin permissions " + (page + 1), ClickAction.RUN_COMMAND); }
		LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------&a&l[&e Permissions Menu " + page + "/" + maxPage + " &a&l]&a&l&m-----------[");
	}
	
   /**
	* Called when the CommandSender executes the Purge command.
	* @param sender - Source of the command. 
	* @param table - The table being purged of data.
	* @param args - The player name having their data purged.
	* 
	*/
	private HashMap < String, Boolean > confirmationRequests = new HashMap < String, Boolean > ();
	private void purge(final CommandSender sender, final String table, final String args) {
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args; placeHolders[10] = table; 
		OfflinePlayer foundPlayer = null;
		if (!table.equalsIgnoreCase("Database")) { 
			placeHolders[9] = "/ij purge " + table + (table.equalsIgnoreCase("map-ids") ? " <image>" : " <player>"); 
			if (!table.equalsIgnoreCase("map-ids")) { foundPlayer = LegacyAPI.getOfflinePlayer(args); }
			if (!table.equalsIgnoreCase("map-ids") && foundPlayer == null && !args.equalsIgnoreCase("ALL")) { LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders); return; } 
		} else { placeHolders[9] = "/ij purge"; }
		if (this.confirmationRequests.get(table + sender.getName()) != null && this.confirmationRequests.get(table + sender.getName()).equals(true)) {
			if (!table.equalsIgnoreCase("Database")) { 
				PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args));
				DataObject dataObject = (table.replace("-", "_").equalsIgnoreCase("map_ids") 
						? new DataObject(Table.MAP_IDS, null, "", args, "") : (table.replace("-", "_").equalsIgnoreCase("first_join") 
						? new DataObject(Table.FIRST_JOIN, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "", "") : (table.replace("-", "_").equalsIgnoreCase("first_world") 
						? new DataObject(Table.FIRST_WORLD, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "", "") : (table.replace("-", "_").equalsIgnoreCase("ip_limits") 
						? new DataObject(Table.IP_LIMITS, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "", "") : (table.replace("-", "_").equalsIgnoreCase("enabled_players") 
						? new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "", "", "") : (table.replace("-", "_").equalsIgnoreCase("first_commands") 
						? new DataObject(Table.FIRST_COMMANDS, PlayerHandler.getPlayerID(PlayerHandler.getPlayerString(args)), "", "", "") : null))))));
				if (dataObject != null) { SQL.getData().removeData(dataObject); }
			} 
			else {
			ItemJoin.getInstance().setStarted(false);
				SQL.getData().purgeDatabase(); {
					SchedulerUtils.runAsync(() -> {
						SQL.newData(false); {
							SchedulerUtils.runAsyncLater(2L, () -> {
								SchedulerUtils.runSingleAsync(() -> {
									ItemJoin.getInstance().setStarted(true);	
								});
							});
						}
					});
				}
			}
			LanguageAPI.getLang(false).sendLangMessage("commands.database.purgeSuccess", sender, placeHolders);
			this.confirmationRequests.remove(table + sender.getName());
		} else {
			this.confirmationRequests.put(table + sender.getName(), true);
			LanguageAPI.getLang(false).sendLangMessage("commands.database.purgeWarn", sender, placeHolders);
			LanguageAPI.getLang(false).sendLangMessage("commands.database.purgeConfirm", sender, placeHolders);
			SchedulerUtils.runLater(100L, () -> {
				if (this.confirmationRequests.get(table + sender.getName()) != null && this.confirmationRequests.get(table + sender.getName()).equals(true)) {
					LanguageAPI.getLang(false).sendLangMessage("commands.database.purgeTimeOut", sender);
					this.confirmationRequests.remove(table + sender.getName());
				} 
			});
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
		Player argsPlayer = (arguments >= 2 ? PlayerHandler.getPlayerString(player) : null);
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = (arguments >= 2 ? player : sender.getName()); placeHolders[0] = world;
		if (arguments >= 2 && argsPlayer == null) { LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders); return; }
		DataObject dataObject = SQL.getData().getData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(true)));
		if (dataObject == null || Boolean.valueOf(dataObject.getEnabled()).equals(false)) {
			SQL.getData().removeData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(false)));
			SQL.getData().saveData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(true)));
			LanguageAPI.getLang(false).sendLangMessage("commands.enabled." + (arguments == 3 ? "forPlayerWorld" : (arguments == 2 ? "forPlayer" : "globalPlayers")), sender, placeHolders); 
			if (arguments >= 2 && !sender.getName().equalsIgnoreCase(argsPlayer.getName())) { 
				placeHolders[1] = sender.getName(); 
				LanguageAPI.getLang(false).sendLangMessage("commands.enabled." + (arguments == 3 ? "forTargetWorld" : "forTarget"), argsPlayer, placeHolders); 
			}
		} else { LanguageAPI.getLang(false).sendLangMessage("commands.enabled." + (arguments == 3 ? "forPlayerWorldFailed" : (arguments == 2 ? "forPlayerFailed" : "globalPlayersFailed")), sender, placeHolders); }
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
		Player argsPlayer = (arguments >= 2 ? PlayerHandler.getPlayerString(player) : null);
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = (arguments >= 2 ? player : sender.getName()); placeHolders[0] = world;
		if (arguments >= 2 && argsPlayer == null) { LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders); return; }
		DataObject dataObject = SQL.getData().getData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(false)));
		if (dataObject == null || Boolean.valueOf(dataObject.getEnabled()).equals(true)) {
			SQL.getData().removeData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(true)));
			SQL.getData().saveData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(argsPlayer), world, "ALL", String.valueOf(false)));
			LanguageAPI.getLang(false).sendLangMessage("commands.disabled." + (arguments == 3 ? "forPlayerWorld" : (arguments == 2 ? "forPlayer" : "globalPlayers")), sender, placeHolders); 
			if (arguments >= 2 && !sender.getName().equalsIgnoreCase(argsPlayer.getName())) { 
				placeHolders[1] = sender.getName(); 
				LanguageAPI.getLang(false).sendLangMessage("commands.disabled." + (arguments == 3 ? "forTargetWorld" : "forTarget"), argsPlayer, placeHolders); 
			}
		} else { LanguageAPI.getLang(false).sendLangMessage("commands.disabled." + (arguments == 3 ? "forPlayerWorldFailed" : (arguments == 2 ? "forPlayerFailed" : "globalPlayersFailed")), sender, placeHolders); }
	}
	
   /**
	* Called when the CommandSender executes the Get command.
	* @param sender - Source of the command. 
	* @param args - Passed command arguments.
	* @param remove - If the item is expected to be removed.
	* 
	*/
	private void handleItems(final CommandSender sender, final String[] args, final boolean remove) {
		Player argsPlayer = (args.length >= 3 ? PlayerHandler.getPlayerString(args[2]) : (Player)sender);
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = (args.length >= 3 && argsPlayer != null ? argsPlayer.getName() : (args.length >= 3 ? args[2] : sender.getName())); placeHolders[3] = args[1];
		int amount = (((args.length >= 3 && argsPlayer == null) || (args.length > 3)) && StringUtils.isInt(args[args.length - 1]) ? Integer.parseInt(args[args.length - 1]) : 0);
		if (args.length >= 3 && !StringUtils.isInt(args[2]) && argsPlayer == null) { LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders); return; } else if (argsPlayer == null && sender instanceof Player) { argsPlayer = (Player)sender; }
		boolean messageSent = false;
		ItemMap itemMapExist = ItemUtilities.getUtilities().getItemMap(null, args[1], argsPlayer.getWorld());
		if (itemMapExist == null) { LanguageAPI.getLang(false).sendLangMessage("commands.item.noItem", sender, placeHolders); return; }
		for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
			if (itemMap.getConfigName().equalsIgnoreCase(args[1])) {
				String customName = StringUtils.translateLayout(itemMap.getCustomName(), argsPlayer); placeHolders[3] = customName;
				if ((remove && itemMap.hasItem(argsPlayer, true)) || (!remove && (itemMap.conditionMet(argsPlayer, "trigger-conditions") && (ItemUtilities.getUtilities().canOverwrite(argsPlayer, itemMap) && (amount != 0 || itemMap.isAlwaysGive() || !itemMap.hasItem(argsPlayer, false)))))) {
					if (remove || !PermissionsHandler.receiveEnabled() || (itemMap.hasPermission(argsPlayer, argsPlayer.getWorld()) && PermissionsHandler.receiveEnabled())) {
						if (itemMap.isAlwaysGive() && (args.length < 2 || (!StringUtils.isInt(args[args.length - 1])))) { amount = itemMap.getCount(); }
						if (StringUtils.getSlotConversion(itemMap.getSlot()) != 0 && PlayerHandler.isCraftingInv(argsPlayer.getOpenInventory()) && argsPlayer.getOpenInventory().getTopInventory().getItem(0) != null && !argsPlayer.getOpenInventory().getTopInventory().getItem(0).getType().equals(Material.AIR)) {
							ItemHandler.returnCraftingItem(argsPlayer, 0, argsPlayer.getOpenInventory().getTopInventory().getItem(0).clone(), 0L);
						}
						if (remove) { itemMap.removeFrom(argsPlayer, amount); } 
						else        { itemMap.giveTo(argsPlayer, amount); }
						placeHolders[11] = Integer.toString((amount == 0 ? 1 : amount)); placeHolders[1] = sender.getName();
						if (!messageSent) { LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.removedYou" : "get.givenYou"), argsPlayer, placeHolders); }
						if (!messageSent && (args.length >= 3 && !StringUtils.isInt(args[2]) && !sender.getName().equalsIgnoreCase(argsPlayer.getName()))) { placeHolders[1] = argsPlayer.getName(); LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.removedTarget" : "get.givenTarget"), sender, placeHolders); }
						PlayerHandler.quickCraftSave(argsPlayer);
					} else if (!remove && !messageSent) {
						LanguageAPI.getLang(false).sendLangMessage("commands.get." + (args.length >= 3 && !StringUtils.isInt(args[2]) && !sender.getName().equalsIgnoreCase(argsPlayer.getName()) ? "targetNoPermission" : "noPermission"), sender, placeHolders);
					}
				} else if (!messageSent && (args.length >= 3 && !StringUtils.isInt(args[2]) && !sender.getName().equalsIgnoreCase(argsPlayer.getName()))) {
					placeHolders[1] = sender.getName();
					LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.targetTriedRemoval" : "get.targetTriedGive"), argsPlayer, placeHolders);
					placeHolders[1] = argsPlayer.getName();
					LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.targetFailedInventory" : "get.targetFailedInventory"), sender, placeHolders);
				} else if (!messageSent) {
					placeHolders[1] = sender.getName();
					LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.failedInventory" : "get.failedInventory"), sender, placeHolders);
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
		PlayerHandler.forOnlinePlayers(argsPlayer -> {
			boolean messageSent = false;
			ItemMap itemMapExist = ItemUtilities.getUtilities().getItemMap(null, args[1], argsPlayer.getWorld());
			if (itemMapExist == null) { LanguageAPI.getLang(false).sendLangMessage("commands.item.noItem", sender, placeHolders); return; }
			placeHolders[3] = StringUtils.translateLayout(itemMapExist.getCustomName(), argsPlayer); placeHolders[1] = sender.getName();
			placeHolders[11] = (amount == 0 ? "&lAll" : Integer.toString(amount));
			for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
				if (itemMap.getConfigName().equalsIgnoreCase(args[1])) {
					if (remove || !PermissionsHandler.receiveEnabled() || (itemMap.hasPermission(argsPlayer, argsPlayer.getWorld()) && PermissionsHandler.receiveEnabled())) {
						if ((remove && itemMap.hasItem(argsPlayer, true)) || (!remove && (itemMap.conditionMet(argsPlayer, "trigger-conditions") && (ItemUtilities.getUtilities().canOverwrite(argsPlayer, itemMap) && (amount != 0 || itemMap.isAlwaysGive() || !itemMap.hasItem(argsPlayer, false)))))) {
							if (StringUtils.getSlotConversion(itemMap.getSlot()) != 0 && PlayerHandler.isCraftingInv(argsPlayer.getOpenInventory()) && argsPlayer.getOpenInventory().getTopInventory().getItem(0) != null && !argsPlayer.getOpenInventory().getTopInventory().getItem(0).getType().equals(Material.AIR)) {
								ItemHandler.returnCraftingItem(argsPlayer, 0, argsPlayer.getOpenInventory().getTopInventory().getItem(0).clone(), 0L);
							}
							if (remove) { itemMap.removeFrom(argsPlayer, amount); }
							else { itemMap.giveTo(argsPlayer, amount); }
							if (!messageSent && !sender.getName().equalsIgnoreCase(argsPlayer.getName())) { LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.removedYou" : "get.givenYou"), argsPlayer, placeHolders); }
							if (!messageSent && !handledPlayers.contains(argsPlayer.getName())) { handledPlayers.add(argsPlayer.getName()); }
							PlayerHandler.quickCraftSave(argsPlayer);
						} else if (!messageSent) { 
							if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) { LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.targetTriedRemoval" : "get.targetTriedGive"), argsPlayer, placeHolders); }
							if (!failedPlayers.contains(argsPlayer.getName())) { failedPlayers.add(argsPlayer.getName()); }
						}
						if (!messageSent) { messageSent = true; }
					}
				}
			}
		});	
		placeHolders[12] = handledPlayers.toString().replace("]", "").replace("[", "");
		if (!handledPlayers.isEmpty()) {
			LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.removedOnline" : "get.givenOnline"), sender, placeHolders);
		} else if (!failedPlayers.isEmpty()) {
			LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.onlineFailedInventory" : "get.onlineFailedInventory"), sender, placeHolders);
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
		Player argsPlayer = (args.length >= 2 ? PlayerHandler.getPlayerString(args[1]) : (Player) sender);
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = (args.length >= 2 ? args[1] : sender.getName());
		if (argsPlayer == null) { LanguageAPI.getLang(false).sendLangMessage("commands.default.noTarget", sender, placeHolders); return; }
		boolean itemGiven = false; boolean failedPermissions = false;
		if (!remove && PlayerHandler.isCraftingInv(argsPlayer.getOpenInventory()) && argsPlayer.getOpenInventory().getTopInventory().getItem(0) != null && !argsPlayer.getOpenInventory().getTopInventory().getItem(0).getType().equals(Material.AIR)) {
			ItemHandler.returnCraftingItem(argsPlayer, 0, argsPlayer.getOpenInventory().getTopInventory().getItem(0).clone(), 0L);
		}
		final ItemMap probable = ChanceAPI.getChances().getRandom(argsPlayer);
		for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
			if ((!remove ? (itemMap.inWorld(argsPlayer.getWorld()) && ChanceAPI.getChances().isProbability(itemMap, probable) && ItemUtilities.getUtilities().canOverwrite(argsPlayer, itemMap) && 
				(!PermissionsHandler.receiveEnabled() || (itemMap.hasPermission(argsPlayer, argsPlayer.getWorld()) && PermissionsHandler.receiveEnabled()))) : remove)) {
				if ((remove && itemMap.hasItem(argsPlayer, true)) || ((!remove && !itemMap.hasItem(argsPlayer, false)) || (!remove && itemMap.isAlwaysGive()))) {
					if (remove || (!remove && itemMap.conditionMet(argsPlayer, "trigger-conditions"))) {
						if (remove) { itemMap.removeFrom(argsPlayer); } 
						else { itemMap.giveTo(argsPlayer); }
						if (!itemGiven) { itemGiven = true; }
						PlayerHandler.quickCraftSave(argsPlayer);
					}
				}
			} else if (!failedPermissions && !itemMap.hasPermission(argsPlayer, argsPlayer.getWorld())) { failedPermissions = true; }
		}
		if (itemGiven) {
			failedPermissions = false;
			placeHolders[1] = sender.getName();
			LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.removedYou_All": "get.givenYou_All"), argsPlayer, placeHolders);
			if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) { placeHolders[1] = argsPlayer.getName(); LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.removedTarget_All": "get.givenTarget_All"), sender, placeHolders); }
		} else {
			placeHolders[1] = argsPlayer.getName();
			LanguageAPI.getLang(false).sendLangMessage("commands." + (!sender.getName().equalsIgnoreCase(argsPlayer.getName()) ? (remove ? "remove.targetFailedInventory_All" : "get.targetFailedInventory_All") : (remove ? "remove.failedInventory_All" : "get.failedInventory_All")), sender, placeHolders);
			if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) { placeHolders[1] = sender.getName(); LanguageAPI.getLang(false).sendLangMessage("commands." + (remove ? "remove.targetTriedRemoval_All": "get.targetFailedInventory_All"), argsPlayer, placeHolders); }
		}
		if (failedPermissions) { placeHolders[1] = argsPlayer.getName(); LanguageAPI.getLang(false).sendLangMessage("commands.get." + (!sender.getName().equalsIgnoreCase(argsPlayer.getName()) ? "targetNoPermission_All" : "noPermission_All"), sender, placeHolders); }
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
		QUERY("query", "itemjoin.query", false),
		WORLD("world, worlds", "itemjoin.use", true),
		LIST("list", "itemjoin.list", false),
		PERMISSIONS("permission, permissions", "itemjoin.permissions", true),
		PURGE("purge", "itemjoin.purge", false),
		ENABLE("enable", "itemjoin.enable, itemjoin.enable.others", false),
		DISABLE("disable", "itemjoin.disable, itemjoin.disable.others", false),
		GET("get, give", "itemjoin.get, itemjoin.get.others", true),
		GETALL("getAll, giveAll", "itemjoin.get, itemjoin.get.others", true),
		GETONLINE("getOnline, giveOnline", "itemjoin.get.others", false),
		REMOVE("remove", "itemjoin.remove, itemjoin.remove.others", true),
		REMOVEALL("removeAll", "itemjoin.remove, itemjoin.remove.others", true),
		REMOVEONLINE("removeOnline", "itemjoin.remove.others", false),
		UPDATE("update, updates", "itemjoin.updates", false),
		UPGRADE("upgrade", "itemjoin.upgrade", false);
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
			return (((args.length == 0 && this.equals(Execute.DEFAULT) && this.hasPermission(sender, args)) || (args.length != 0 && StringUtils.splitIgnoreCase(this.command, args[0], ",")) && this.hasSyntax(args, page) && this.playerRequired(sender, args) && this.hasPermission(sender, args))); 
		}
		
       /**
	    * Checks if the executed command is the same as the executor.
	    * @param sender - Source of the command. 
	    * @param args - Passed command arguments.
	    * @param page - The page number to be expected.
	    * 
	    */
		public boolean acceptArgs(final String[] args) {
			return (args.length == 0 || StringUtils.splitIgnoreCase(this.command, args[0], ","));
		}
		
       /**
	    * Checks if the Command being executed has the proper formatting or syntax.
	    * @param args - Passed command arguments.
	    * @param page - The page number to be expected.
	    * 
	    */
		private boolean hasSyntax(final String[] args, final int page) {
			return ((args.length >= 2 && (args[1].equalsIgnoreCase(String.valueOf(page)) || (page == 2 
				 && StringUtils.isInt(args[1]) && Integer.parseInt(args[1]) != 0 && Integer.parseInt(args[1]) != 1 && ((this.equals(Execute.PERMISSIONS) && Integer.parseInt(args[1]) <= ConfigHandler.getConfig().getPermissionPages()) 
				 || (this.equals(Execute.LIST) && Integer.parseInt(args[1]) <= ConfigHandler.getConfig().getListPages()))) 
				 || (page == 1 && this.equals(Execute.PERMISSIONS) && StringUtils.isInt(args[1]) && Integer.parseInt(args[1]) == 0)
				 || (!StringUtils.isInt(args[1]) && !this.equals(Execute.PURGE)))) 
				 || (args.length < 2 && (!this.equals(Execute.GET) && !this.equals(Execute.GETONLINE) && !this.equals(Execute.REMOVE) && !this.equals(Execute.REMOVEONLINE) && !this.equals(Execute.QUERY))
				 || (this.equals(Execute.PURGE) && (args.length == 1 
				 || (args.length >= 3 && (args[1].equalsIgnoreCase("map-ids") || args[1].equalsIgnoreCase("ip-limits") || args[1].equalsIgnoreCase("first-join") || args[1].equalsIgnoreCase("first-world") 
				 || args[1].equalsIgnoreCase("enabled-players") || args[1].equalsIgnoreCase("first-commands")))))));
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
			return (multiPerms && ((((!this.equals(Execute.GET) && !this.equals(Execute.REMOVE)) && ((args.length >= 2 && !StringUtils.isInt(args[1]) && PermissionsHandler.hasPermission(sender, permissions[1])) 
			   || ((args.length < 2 || (args.length >= 2 && StringUtils.isInt(args[1]))) && PermissionsHandler.hasPermission(sender, permissions[0]))))) 
			   || (((this.equals(Execute.GET) || this.equals(Execute.REMOVE)) && (((args.length == 3 && StringUtils.isInt(args[2])) || args.length == 2)) && PermissionsHandler.hasPermission(sender, permissions[0])) 
			   || (((args.length == 3 && !StringUtils.isInt(args[2])) || args.length >= 3)) && PermissionsHandler.hasPermission(sender, permissions[1]))))
		       || (!multiPerms && PermissionsHandler.hasPermission(sender, this.permission));
		}
		
       /**
	    * Checks if the Command requires the instance to be a Player.
	    * @param sender - Source of the command. 
	    * @param args - Passed command arguments.
	    * 
	    */
		public boolean playerRequired(final CommandSender sender, final String[] args) {
			return (!this.player || (!(sender instanceof ConsoleCommandSender)) 
					|| ((this.equals(Execute.GETALL) || this.equals(Execute.REMOVEALL)) && args.length >= 2)
					|| ((this.equals(Execute.GET) || this.equals(Execute.REMOVE)) && !(((args.length == 3 && PlayerHandler.getPlayerString(args[2]) == null && StringUtils.isInt(args[2])) || args.length == 2))));
		}
	}
}