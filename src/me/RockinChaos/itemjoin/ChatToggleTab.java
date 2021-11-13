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
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;

public class ChatToggleTab implements TabCompleter {
	
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
		if (!(sender instanceof ConsoleCommandSender)) {
			String fullCommand = command.getName();
			for (String length : args) {
				fullCommand += " " + length;
			}
			for (ItemMap item : ItemUtilities.getUtilities().getItems()) {
				for (String cmd : item.getToggleCommands()) {
					if (cmd.startsWith(fullCommand) && !cmd.equalsIgnoreCase(fullCommand.trim()) && (item.getToggleNode() != null && !item.getToggleNode().isEmpty() && sender.hasPermission(item.getToggleNode()))) {
						commands.add(cmd.replaceFirst(fullCommand, ""));
					}
				} 
			}
		}
		StringUtil.copyPartialMatches(args[(args.length - 1)], commands, completions);
		Collections.sort(completions);
		return completions;
	}
}