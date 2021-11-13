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

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.api.LanguageAPI;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.SQL;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;

public class ChatToggleExecutor implements CommandExecutor {
	
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
		if (!(sender instanceof ConsoleCommandSender)) {
			final Player player = (Player)sender;
			ItemMap itemMap = this.getCommandMap(player, command, args);
			if (itemMap != null) {
				DataObject dataObject = SQL.getData().getData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(player), "Global", itemMap.getConfigName(), String.valueOf(false)));
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = player.getName(); placeHolders[0] = player.getWorld().getName(); placeHolders[3] = itemMap.getConfigName();
				if ((dataObject == null || Boolean.valueOf(dataObject.getEnabled()).equals(true)) && ((itemMap.getToggleNode() == null || itemMap.getToggleNode().isEmpty()) || (itemMap.getToggleNode() != null && !itemMap.getToggleNode().isEmpty() && sender.hasPermission(itemMap.getToggleNode())))) {
					if (PlayerHandler.isEnabled(player, "ALL")) {
						SQL.getData().removeData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(player), "Global", itemMap.getConfigName(), String.valueOf(true)));
						SQL.getData().saveData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(player), "Global", itemMap.getConfigName(), String.valueOf(false))); {
							if (itemMap.hasItem(player, true)) {
								itemMap.removeFrom(player);
							}
							final String toggleMessage = (itemMap.getToggleMessage() != null ? StringUtils.translateLayout(itemMap.getToggleMessage(), player, placeHolders) 
														: StringUtils.translateLayout("&cYou have disabled the item " + itemMap.getConfigName() + " and it will no longer be given.", player));
							ServerUtils.messageSender(player, toggleMessage);
						}
					} else {
						LanguageAPI.getLang(false).sendLangMessage("commands.disabled.togglePlayerFailed", sender, placeHolders);
					}
				} else if ((dataObject != null && Boolean.valueOf(dataObject.getEnabled()).equals(false)) && ((itemMap.getToggleNode() == null || itemMap.getToggleNode().isEmpty()) || (itemMap.getToggleNode() != null && !itemMap.getToggleNode().isEmpty() && sender.hasPermission(itemMap.getToggleNode())))) {
					if (PlayerHandler.isEnabled(player, "ALL")) {
						SQL.getData().removeData(new DataObject(Table.ENABLED_PLAYERS, PlayerHandler.getPlayerID(player), "Global", itemMap.getConfigName(), String.valueOf(false))); {
							if (!itemMap.hasItem(player, true)) {
								itemMap.giveTo(player);
							}
							final String toggleMessage = (itemMap.getToggleMessage() != null ? StringUtils.translateLayout(itemMap.getToggleMessage(), player, placeHolders) 
														: StringUtils.translateLayout("&aYou have enabled the item " + itemMap.getConfigName() + " and it will now be given.", player));
							ServerUtils.messageSender(player, toggleMessage);
						}
					} else {
						LanguageAPI.getLang(false).sendLangMessage("commands.enabled.togglePlayerFailed", sender, placeHolders);
					}
				} else if (!(itemMap.getToggleNode() != null && !itemMap.getToggleNode().isEmpty() && sender.hasPermission(itemMap.getToggleNode()))) {
					LanguageAPI.getLang(false).sendLangMessage("commands.default.noPermission", sender);
				}
			} else {
				sender.sendMessage("Unknown command. Type \"/help\" for help.");	
			}
		} else {
			LanguageAPI.getLang(false).sendLangMessage("commands.default.noPlayer", sender);
		}
		return true;
	}
	
   /**
	* Attempts to get the corresponding ItemMap to the executed command including arguments.
    * @param player - Source of the command.
    * @param command - Command which was executed.
    * @param args - Passed command arguments.
    * @return The found ItemMap, or null if it doesn't exist.
	*/
	private ItemMap getCommandMap(final Player player, final Command command, final String[] args) {
		ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(null, command.getDescription(), player.getWorld());
		boolean canToggle = false;
		for (String commands : itemMap.getToggleCommands()) {
			if (!canToggle && commands.equalsIgnoreCase( command.getName())) {
				canToggle = true; break;
			}
		}
		if (args.length != 0 && !canToggle) {
			String fullCommand = command.getName();
			for (String length : args) {
				fullCommand += " " + length;
			}
			for (ItemMap item : ItemUtilities.getUtilities().getItems()) {
				if (!canToggle) {
					for (String commands : item.getToggleCommands()) {
						if (commands.equalsIgnoreCase(fullCommand.trim()) && !canToggle) {
							itemMap = item; canToggle = true; break;
						}
					} 
				} else { break; }
			}
		}
		return (canToggle ? itemMap : null);
	}
}