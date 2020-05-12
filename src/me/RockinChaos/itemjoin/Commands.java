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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.handlers.UpdateHandler;
import me.RockinChaos.itemjoin.utils.LanguageAPI;
import me.RockinChaos.itemjoin.utils.LegacyAPI;
import me.RockinChaos.itemjoin.utils.UI;
import me.RockinChaos.itemjoin.utils.Chances;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLite;
import me.RockinChaos.itemjoin.utils.sqlite.SQDrivers;

public class Commands implements CommandExecutor {
	
	private HashMap < String, Boolean > confirmationRequests = new HashMap < String, Boolean > ();
	
   /**
	* Called when the CommandSender executes a command.
    * @param sender - Source of the command.
    * @param command - Command which was executed.
    * @param label - Alias of the command which was used.
    * @param args - Passed command arguments.
    * @return true if the command is valid.
	*/
	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
				LanguageAPI.getLang(false).dispatchMessage(sender, "&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help &afor the help menu.");
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("help") || args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("1")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
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
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("2")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
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
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("3")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
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
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("4")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> &7- &eRemoves item from inventory.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <Qty> &7- &eRemoves qty of item.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <User> &7- &eRemoves from player.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <User> <Qty> &7- &eRemoves qty.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 5 &afor the next page.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 4/9 &a&l]&a&l&m---------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("5")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetOnline <Item> &7- &eGives to all online.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetOnline <Item> <Qty> &7- &eGives qty to all online.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveOnline <Item> &7- &eRemoves from all online.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveOnline <Item> <Qty> &7- &eRemoves qty.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 6 &afor the next page.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 5/9 &a&l]&a&l&m---------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("6")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetAll &7- &eGives all ItemJoin items.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin GetAll <User> &7- &eGives all items to said player.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveAll &7- &eRemoves all ItemJoin items.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin RemoveAll <User> &7- &eRemoves from player.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 7 &afor the next page.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 6/9 &a&l]&a&l&m---------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("7")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Enable &7- &eEnables ItemJoin for all players.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Enable <User> &7- &eEnables ItemJoin for player.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Enable <User> <World> &7- &eFor player/world.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 8 &afor the next page.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 7/9 &a&l]&a&l&m---------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("8")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Disable &7- &eDisables ItemJoin for all players.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Disable <User> &7- &eDisables ItemJoin for player.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Disable <User> <World> &7- &eFor player/world.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Help 9 &afor the next page.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 8/9 &a&l]&a&l&m---------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("9")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&c&l[DANGER]&eThe Following Destroys Data &nPermanently!&e&c&l[DANGER]");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge &7- &eDeletes the database file!");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge first-join <User> &7- &eFirst-Join data.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge first-world <User> &7- &eFirst-World data.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/ItemJoin Purge ip-limits <User> &7- &eIp-Limits data.");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&aFound a bug? Report it @");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&ahttps://github.com/RockinChaos/ItemJoin/issues");
				LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 9/9 &a&l]&a&l&m---------------[");
				LanguageAPI.getLang(false).dispatchMessage(sender, "");
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.reload")) {
				SQLite.getLite(false).executeLaterStatements();
				ItemUtilities.getUtilities().closeAnimations();
				ItemUtilities.getUtilities().clearItems();
		  		ConfigHandler.getConfig(true);
				LanguageAPI.getLang(false).sendLangMessage("Commands.Default.configReload", sender);
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("info")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					if (PlayerHandler.getPlayer().getHandItem((Player) sender) != null && PlayerHandler.getPlayer().getHandItem((Player) sender).getType() != Material.AIR) {
						LanguageAPI.getLang(false).dispatchMessage(sender, " ");
						LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]-----------------&a&l[&e Item Info &a&l]&a&l&m----------------[");
						LanguageAPI.getLang(false).dispatchMessage(sender, "");
						String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = PlayerHandler.getPlayer().getHandItem((Player) sender).getType().toString();
						LanguageAPI.getLang(false).sendLangMessage("Commands.Info.material", sender, placeHolders);
						if (!ServerHandler.getServer().hasSpecificUpdate("1_13")) {
							placeHolders[3] = LegacyAPI.getLegacy().getDataValue(PlayerHandler.getPlayer().getHandItem((Player) sender)) + "";
							LanguageAPI.getLang(false).sendLangMessage("Commands.Info.dataValue", sender, placeHolders);
						}
						LanguageAPI.getLang(false).dispatchMessage(sender, "");
						LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Item Info Menu &a&l]&a&l&m--------------[");
						LanguageAPI.getLang(false).dispatchMessage(sender, " ");
					} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Item.notInHand", sender); }
				} else if (sender instanceof ConsoleCommandSender) { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.notPlayer", sender); }
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("purge")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.purge")) {
				if (this.confirmationRequests.get(1 + sender.getName()) != null && this.confirmationRequests.get(1 + sender.getName()).equals(true)) {
					SQDrivers.getDatabase("database").purgeDatabase();
			        SQLite.getLite(true);
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = "All Players"; placeHolders[10] = "Database"; placeHolders[9] = "/ij purge";
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeSuccess", sender, placeHolders);
					this.confirmationRequests.remove(1 + sender.getName());
				} else {
					this.confirmationRequests.put(1 + sender.getName(), true);
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = "All Players"; placeHolders[10] = "Database"; placeHolders[9] = "/ij purge";
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeWarn", sender, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeConfirm", sender, placeHolders);
					new BukkitRunnable() {
						@Override
						public void run() {
							if (confirmationRequests.get(1 + sender.getName()) != null && confirmationRequests.get(1 + sender.getName()).equals(true)) {
								LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeTimeOut", sender);
								confirmationRequests.remove(1 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length >= 3 && args[0].equalsIgnoreCase("purge") && args[1].equalsIgnoreCase("ip-limits") 
				|| args.length >= 3 && args[0].equalsIgnoreCase("purge") && args[1].equalsIgnoreCase("first-join")
				|| args.length >= 3 && args[0].equalsIgnoreCase("purge") && args[1].equalsIgnoreCase("first-world")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.purge")) {
				OfflinePlayer player = PlayerHandler.getPlayer().getOfflinePlayer(args[2]);
				if (player == null) {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[2];
					LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
				} else if (this.confirmationRequests.get(2 + sender.getName()) != null && this.confirmationRequests.get(2 + sender.getName()).equals(true) && args[1].equalsIgnoreCase("ip-limits")) {
					SQLite.getLite(false).purgeDatabaseData(player, "ip_limits");
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[2]; placeHolders[10] = "ip-limits"; placeHolders[9] = "/ij purge ip-limits <player>";
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeSuccess", sender, placeHolders);
					this.confirmationRequests.remove(2 + sender.getName());
				} else if (this.confirmationRequests.get(3 + sender.getName()) != null && this.confirmationRequests.get(3 + sender.getName()).equals(true) && args[1].equalsIgnoreCase("first-join")) {
					SQLite.getLite(false).purgeDatabaseData(player, "first_join");
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[2]; placeHolders[10] = "first-join"; placeHolders[9] = "/ij purge first-join <player>";
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeSuccess", sender, placeHolders);
					this.confirmationRequests.remove(3 + sender.getName());
				} else if (this.confirmationRequests.get(3 + sender.getName()) != null && this.confirmationRequests.get(3 + sender.getName()).equals(true) && args[1].equalsIgnoreCase("first-world")) {
					SQLite.getLite(false).purgeDatabaseData(player, "first_world");
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[2]; placeHolders[10] = "first-world"; placeHolders[9] = "/ij purge first-world <player>";
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeSuccess", sender, placeHolders);
					this.confirmationRequests.remove(3 + sender.getName());
				} else if (this.confirmationRequests.get(2 + sender.getName()) == null && args[1].equalsIgnoreCase("ip-limits")) {
					this.confirmationRequests.put(2 + sender.getName(), true);
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[2]; placeHolders[10] = "ip-limits"; placeHolders[9] = "/ij purge ip-limits <player>";
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeWarn", sender, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeConfirm", sender, placeHolders);
					new BukkitRunnable() {
						@Override
						public void run() {
							if (confirmationRequests.get(2 + sender.getName()) != null && confirmationRequests.get(2 + sender.getName()).equals(true)) {
								LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeTimeOut", sender);
								confirmationRequests.remove(2 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				} else if (this.confirmationRequests.get(3 + sender.getName()) == null && args[1].equalsIgnoreCase("first-join")) {
					this.confirmationRequests.put(3 + sender.getName(), true);
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[2]; placeHolders[10] = "first-join"; placeHolders[9] = "/ij purge first-join <player>";
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeWarn", sender, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeConfirm", sender, placeHolders);
					new BukkitRunnable() {
						@Override
						public void run() {
							if (confirmationRequests.get(3 + sender.getName()) != null && confirmationRequests.get(3 + sender.getName()).equals(true)) {
								LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeTimeOut", sender);
								confirmationRequests.remove(3 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				} else if (this.confirmationRequests.get(3 + sender.getName()) == null && args[1].equalsIgnoreCase("first-world")) {
					this.confirmationRequests.put(3 + sender.getName(), true);
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[2]; placeHolders[10] = "first-world"; placeHolders[9] = "/ij purge first-world <player>";
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeWarn", sender, placeHolders);
					LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeConfirm", sender, placeHolders);
					new BukkitRunnable() {
						@Override
						public void run() {
							if (confirmationRequests.get(3 + sender.getName()) != null && confirmationRequests.get(3 + sender.getName()).equals(true)) {
								LanguageAPI.getLang(false).sendLangMessage("Commands.Database.purgeTimeOut", sender);
								confirmationRequests.remove(3 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("menu") || args[0].equalsIgnoreCase("creator")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.menu")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					UI.getCreator().startMenu(sender);
					LanguageAPI.getLang(false).sendLangMessage("Commands.UI.creatorLaunched", sender);
				} else if (sender instanceof ConsoleCommandSender) { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.notPlayer", sender); }
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("enable")) {
			Player argsPlayer = PlayerHandler.getPlayer().getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.enable.others")) { 
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.enable.others")) {
				if (SQLite.getLite(false).isWritable("Global", PlayerHandler.getPlayer().getPlayerID(argsPlayer))) {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1];
					LanguageAPI.getLang(false).sendLangMessage("Commands.Enabled.forPlayer", sender, placeHolders);
				} else {
					SQLite.getLite(false).saveToDatabase(argsPlayer, "Global", "true", "enabled-players");
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = argsPlayer.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.Enabled.forPlayer", sender, placeHolders);
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						placeHolders[1] = sender.getName();
						LanguageAPI.getLang(false).sendLangMessage("Commands.Enabled.forTarget", argsPlayer, placeHolders);
					}
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("enable")) {
			Player argsPlayer = PlayerHandler.getPlayer().getPlayerString(args[1]);
			String world = args[2];
			if (argsPlayer == null && PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.enable.others")) { 
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.enable.others")) {
				if (SQLite.getLite(false).isWritable(world, PlayerHandler.getPlayer().getPlayerID(argsPlayer))) { 
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1]; placeHolders[0] = world;
					LanguageAPI.getLang(false).sendLangMessage("Commands.Enabled.forPlayerWorldFailed", sender, placeHolders); 
				} else {
					SQLite.getLite(false).saveToDatabase(argsPlayer, world, "true", "enabled-players");
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1]; placeHolders[0] = world;
					LanguageAPI.getLang(false).sendLangMessage("Commands.Enabled.forPlayerWorld", sender, placeHolders); 
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						placeHolders[1] = sender.getName();
						LanguageAPI.getLang(false).sendLangMessage("Commands.Enabled.forTargetWorld", argsPlayer, placeHolders); 
					}
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("enable")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.enable")) {
				if (!SQLite.getLite(false).isWritable("Global", "ALL")) {
					SQLite.getLite(false).saveToDatabase(null, "Global", "true", "enabled-players");
					LanguageAPI.getLang(false).sendLangMessage("Commands.Enabled.globalPlayers", sender);
				} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Enabled.globalPlayersFailed", sender);  }
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("disable")) {
			Player argsPlayer = PlayerHandler.getPlayer().getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.disable.others")) {
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.disable.others")) {
				if (!SQLite.getLite(false).isWritable("Global", PlayerHandler.getPlayer().getPlayerID(argsPlayer))) { 
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1];
					LanguageAPI.getLang(false).sendLangMessage("Commands.Disabled.forPlayerFailed", sender, placeHolders); 
				} else {
					SQLite.getLite(false).saveToDatabase(argsPlayer, "Global", "false", "disabled-players");
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1];
					LanguageAPI.getLang(false).sendLangMessage("Commands.Disabled.forPlayer", sender, placeHolders); 
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						placeHolders[1] = sender.getName();
						LanguageAPI.getLang(false).sendLangMessage("Commands.Disabled.forTarget", argsPlayer, placeHolders); 
					}
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("disable")) {
			Player argsPlayer = PlayerHandler.getPlayer().getPlayerString(args[1]);
			String world = args[2];
			if (argsPlayer == null && PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.disable.others")) {
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.disable.others")) {
				if (!SQLite.getLite(false).isWritable(world, PlayerHandler.getPlayer().getPlayerID(argsPlayer))) {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1]; placeHolders[0] = world;
					LanguageAPI.getLang(false).sendLangMessage("Commands.Disabled.forPlayerWorldFailed", sender, placeHolders); 
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1]; placeHolders[0] = world;
					LanguageAPI.getLang(false).sendLangMessage("Commands.Disabled.forPlayerWorld", sender, placeHolders); 
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						placeHolders[1] = sender.getName();
						LanguageAPI.getLang(false).sendLangMessage("Commands.Disabled.forTargetWorld", argsPlayer, placeHolders); 
					}
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("disable")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.disable")) {
				if (SQLite.getLite(false).isWritable("Global", "ALL")) {
					SQLite.getLite(false).saveToDatabase(null, "Global", "false", "disabled-players");
					LanguageAPI.getLang(false).sendLangMessage("Commands.Disabled.globalPlayers", sender); 
				} else {LanguageAPI.getLang(false).sendLangMessage("Commands.Disabled.globalPlayersFailed", sender);  }
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					LanguageAPI.getLang(false).dispatchMessage(sender, "");
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[0] = ((Player) sender).getWorld().getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.World.worldHeader", sender, placeHolders); 
					LanguageAPI.getLang(false).sendLangMessage("Commands.World.worldRow", sender, placeHolders); 
					LanguageAPI.getLang(false).dispatchMessage(sender, "");
					LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]--------------&a&l[&e Worlds In Menu 1/1 &a&l]&a&l&m-------------[");
				} else if (sender instanceof ConsoleCommandSender) { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.notPlayer", sender); }
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("list")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.list")) {
					boolean ItemExists = false;
					LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
						ItemExists = false;
						String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[0] = world.getName();
						LanguageAPI.getLang(false).sendLangMessage("Commands.List.worldHeader", sender, placeHolders); 
						List <String> inputListed = new ArrayList<String>();
						for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
							if (!inputListed.contains(item.getConfigName()) && item.getTempItem() != null && item.inWorld(world)) {
								inputListed.add(item.getConfigName());
								placeHolders[3] = item.getConfigName(); placeHolders[4] = item.getConfigName();
								LanguageAPI.getLang(false).sendLangMessage("Commands.List.itemRow", sender, placeHolders); 
								ItemExists = true;
							}
						}
						if (ItemExists == false) { LanguageAPI.getLang(false).sendLangMessage("Commands.List.noItemsDefined", sender);  }
					}
					LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e List Menu 1/1 &a&l]&a&l&m---------------[");
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("permissions")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.permissions")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.*")) { LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] ItemJoin.*"); } 
					else { LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] ItemJoin.*"); }
					if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.all")) { LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] ItemJoin.All"); } 
					else { LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] ItemJoin.All"); }
					if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.use")) { LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] ItemJoin.Use"); } 
					else { LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] ItemJoin.Use"); }
					if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.reload")) { LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] ItemJoin.Reload"); } 
					else { LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] ItemJoin.Reload"); }
					if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.updates")) { LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] ItemJoin.Updates"); }
					else { LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] ItemJoin.Updates"); }
					if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.autoupdate")) { LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] ItemJoin.AutoUpdate"); }
					else { LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] ItemJoin.AutoUpdate"); }
					if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get")) { LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] ItemJoin.get"); } 
					else { LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] ItemJoin.get"); }
					if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get.others")) { LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] ItemJoin.get.others"); }
					else { LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] ItemJoin.get.others"); }
					if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.permissions")) { LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] ItemJoin.permissions"); } 
					else { LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] ItemJoin.permissions"); }
					for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
						if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin." + world.getName() + ".*")) { LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] ItemJoin." + world.getName() + ".*"); } 
						else { LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] ItemJoin." + world.getName() + ".*"); }
					}
					LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/ItemJoin Permissions 2 &afor the next page.");
					LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------&a&l[&e Permissions Menu 1/2 &a&l]&a&l&m------------[");
				} else if (sender instanceof ConsoleCommandSender) { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.notPlayer", sender); }
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("permissions") && args[1].equalsIgnoreCase("2")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.permissions")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					List<String> customPermissions = new ArrayList<String>();
					for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
						List <String> inputListed = new ArrayList<String>();
						final ItemMap probable = Chances.getChances().getRandom(((Player) sender));
						for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
							if (!customPermissions.contains(item.getPermissionNode()) && !inputListed.contains(item.getConfigName()) && item.inWorld(world) && Chances.getChances().isProbability(item, probable)) {
								if (item.getPermissionNode() != null && !customPermissions.contains(item.getPermissionNode()) || item.getPermissionNode() == null) {
									if (item.getPermissionNode() != null) { customPermissions.add(item.getPermissionNode()); }
									inputListed.add(item.getConfigName());
									if (item.hasPermission(((Player) sender))) { LanguageAPI.getLang(false).dispatchMessage(sender, "&a[\u2714] " + PermissionsHandler.getPermissions().customPermissions(item.getPermissionNode(), item.getConfigName(), world.getName())); } 
									else { LanguageAPI.getLang(false).dispatchMessage(sender, "&c[\u2718] " + PermissionsHandler.getPermissions().customPermissions(item.getPermissionNode(), item.getConfigName(), world.getName())); }
								}
							}
						}
					}
					LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------&a&l[&e Permissions Menu 2/2 &a&l]&a&l&m------------[");
				} else if (sender instanceof ConsoleCommandSender) { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.notPlayer", sender); }
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("getOnline") || args.length == 3 && args[0].equalsIgnoreCase("getOnline") && Utils.getUtils().isInt(args[2])) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get.others")) {
				int amount = 0;
				if (args.length == 3) { amount = Integer.parseInt(args[2]); }
			    Collection < ? > playersOnline = null;
			    Player[] playersOnlineOld = null;
			    List<String> givenPlayers = new ArrayList<String>();
			    try {
			        if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
			            if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
			                playersOnline = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
			                for (Object objPlayer: playersOnline) {
				            	String objectivePlayer = getOnline(((Player) objPlayer), sender, args[1], amount);

			                    if (objectivePlayer == null) { break; }
			                    else if (!objectivePlayer.isEmpty()) { givenPlayers.add(objectivePlayer); }
			                }
			            }
			        } else {
			            playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
			            for (Player objPlayer: playersOnlineOld) {
			            	String objectivePlayer = getOnline(objPlayer, sender, args[1], amount);
		                    if (objectivePlayer == null) { break; }
		                    else if (!objectivePlayer.isEmpty()) { givenPlayers.add(objectivePlayer); }
			            }
			        }
			    } catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e);  }
			    ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(null, args[1], null);
		    	if (itemMap != null) { 
		    	String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[12] = givenPlayers.toString().replace("]", "").replace("[", ""); placeHolders[3] = Utils.getUtils().translateLayout(itemMap.getCustomName(), null);
			    if (amount == 0) { amount = itemMap.getCount(); }
				    placeHolders[11] = amount + "";
			    	if (!givenPlayers.isEmpty()) { LanguageAPI.getLang(false).sendLangMessage("Commands.Get.toOnlinePlayers", sender, placeHolders); } 
				    else { LanguageAPI.getLang(false).sendLangMessage("Commands.Get.onlinePlayersHaveItem", sender, placeHolders); }
		    	}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("getOnline")) {
				if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get.others")) { LanguageAPI.getLang(false).sendLangMessage("Commands.Get.invalidOnlineSyntax", sender); } 
				else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
				return true;
		} else if (args.length == 4 && args[0].equalsIgnoreCase("get") && Utils.getUtils().isInt(args[3]) || args.length == 3 && args[0].equalsIgnoreCase("get") && !Utils.getUtils().isInt(args[2])) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get.others")) {
				Player argsPlayer = PlayerHandler.getPlayer().getPlayerString(args[2]);
				int amount = 0;
				if (args.length == 4) { amount = Integer.parseInt(args[3]); } 
				if (argsPlayer == null) { String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[2];
				LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders);  return true; }
				boolean itemGiven = false;
				final ItemMap probable = Chances.getChances().getRandom(argsPlayer);
				for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
					if (item.inWorld(argsPlayer.getWorld()) && item.getConfigName().equalsIgnoreCase(args[1]) && Chances.getChances().isProbability(item, probable)) {
						String customName = Utils.getUtils().translateLayout(item.getCustomName(), null);
						if (sender instanceof Player) { customName = Utils.getUtils().translateLayout(item.getCustomName(), ((Player) sender)); }
						if (!item.hasItem(argsPlayer) || amount != 0 || item.isAlwaysGive()) {
							if (!(PermissionsHandler.getPermissions().permissionsEnabled()) || item.hasPermission(argsPlayer) && PermissionsHandler.getPermissions().permissionsEnabled()) {
								if (item.isAlwaysGive() && args.length != 4) { amount = item.getCount(); }
								item.giveTo(argsPlayer, amount);
								String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = sender.getName(); placeHolders[3] = customName; if (amount == 0) { amount += 1; } placeHolders[11] = amount + "";
								LanguageAPI.getLang(false).sendLangMessage("Commands.Get.toYou", argsPlayer, placeHolders);
								placeHolders[1] = argsPlayer.getName();
								LanguageAPI.getLang(false).sendLangMessage("Commands.Get.toTarget", sender, placeHolders);
							} else { 
								String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = argsPlayer.getName(); placeHolders[3] = customName;
								LanguageAPI.getLang(false).sendLangMessage("Commands.Get.targetNoPermission", sender, placeHolders);
							}
						} else {
							String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = sender.getName(); placeHolders[3] = customName;
							LanguageAPI.getLang(false).sendLangMessage("Commands.Get.triedGive", argsPlayer, placeHolders);
							placeHolders[1] = argsPlayer.getName();
							LanguageAPI.getLang(false).sendLangMessage("Commands.Get.targetHasItem", sender, placeHolders);
						}
						itemGiven = true;
					}
				}
				if (!itemGiven) { String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); }
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("get") && Utils.getUtils().isInt(args[2]) || args.length == 2 && args[0].equalsIgnoreCase("get")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get")) {
				int amount = 0;
				if (args.length == 3) { amount = Integer.parseInt(args[2]); }
				if (!(sender instanceof ConsoleCommandSender)) {
					boolean itemGiven = false;
					final ItemMap probable = Chances.getChances().getRandom(((Player) sender));
					for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
						if (item.inWorld(((Player) sender).getWorld()) && item.getConfigName().equalsIgnoreCase(args[1]) && Chances.getChances().isProbability(item, probable)) {
							String customName = Utils.getUtils().translateLayout(item.getCustomName(), ((Player) sender));
							if (!item.hasItem(((Player) sender)) || amount != 0 || item.isAlwaysGive()) {
								if (!(PermissionsHandler.getPermissions().permissionsEnabled()) || item.hasPermission(((Player) sender)) && PermissionsHandler.getPermissions().permissionsEnabled()) {
									item.giveTo(((Player) sender), amount);
									String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = customName; 
									if (amount == 0) { amount += 1; } placeHolders[11] = amount + "";
									LanguageAPI.getLang(false).sendLangMessage("Commands.Get.toYou", sender, placeHolders);
								} else {
									String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = customName;
									LanguageAPI.getLang(false).sendLangMessage("Commands.Get.noPermission", sender, placeHolders);
								}
							} else { 
								String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = customName;
								LanguageAPI.getLang(false).sendLangMessage("Commands.Get.youHaveItem", sender, placeHolders);
							}
							itemGiven = true;
						}
					}
					if (!itemGiven) { String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = args[1];
					LanguageAPI.getLang(false).sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); }
				} else if (sender instanceof ConsoleCommandSender) {
					LanguageAPI.getLang(false).sendLangMessage("Commands.Default.notPlayer", sender);
					LanguageAPI.getLang(false).sendLangMessage("Commands.Get.usageSyntax", sender);
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("get")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get")) { LanguageAPI.getLang(false).sendLangMessage("Commands.Get.invalidSyntax", sender); } 
			else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("getall")) {
			Player argsPlayer = PlayerHandler.getPlayer().getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get.others")) {
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get.others")) {
				boolean itemGiven = false;
				boolean itemPermission = false;
				final ItemMap probable = Chances.getChances().getRandom(argsPlayer);
				for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
					if (item.inWorld(argsPlayer.getWorld()) && Chances.getChances().isProbability(item, probable)) {
						if (!(PermissionsHandler.getPermissions().permissionsEnabled()) || item.hasPermission(argsPlayer) && PermissionsHandler.getPermissions().permissionsEnabled()) {
							if (!item.hasItem(argsPlayer) || item.isAlwaysGive()) {
								item.giveTo(argsPlayer);
								itemGiven = true;
							}
						} else { itemPermission = true; }
					}
				}
				if (itemGiven) {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = sender.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.GetAll.toYou", argsPlayer, placeHolders);
					placeHolders[1] = argsPlayer.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.GetAll.toTarget", sender, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = sender.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.GetAll.triedGive", argsPlayer, placeHolders);
					placeHolders[1] = argsPlayer.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.GetAll.targetHasItems", sender, placeHolders);
				}
				if (itemPermission) {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = argsPlayer.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.GetAll.targetNoPermission", sender, placeHolders);
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("getall")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.get")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					boolean itemGiven = false;
					boolean itemPermission = false;
					final ItemMap probable = Chances.getChances().getRandom(((Player) sender));
					for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
						if (item.inWorld(((Player) sender).getWorld()) && Chances.getChances().isProbability(item, probable)) {
							if (!(PermissionsHandler.getPermissions().permissionsEnabled()) || item.hasPermission(((Player) sender)) && PermissionsHandler.getPermissions().permissionsEnabled()) {
								if (!item.hasItem(((Player) sender)) || item.isAlwaysGive()) {
									item.giveTo(((Player) sender));
									itemGiven = true;
								}
							} else { itemPermission = true; } 
						}
					}
					if (itemGiven) {
						LanguageAPI.getLang(false).sendLangMessage("Commands.GetAll.toYou", sender);
					} else {
						LanguageAPI.getLang(false).sendLangMessage("Commands.GetAll.youHaveItems", sender);
					} if (itemPermission) {
						LanguageAPI.getLang(false).sendLangMessage("Commands.GetAll.noPermission", sender);
					}
				} else if (sender instanceof ConsoleCommandSender) {
					LanguageAPI.getLang(false).sendLangMessage("Commands.Default.notPlayer", sender);
					LanguageAPI.getLang(false).sendLangMessage("Commands.Get.usageSynax", sender);
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("removeOnline") || args.length == 3 && args[0].equalsIgnoreCase("removeOnline") && Utils.getUtils().isInt(args[2])) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove.others")) {
			    Collection < ? > playersOnline = null;
			    Player[] playersOnlineOld = null;
			    List<String> removedPlayers = new ArrayList<String>();
			    int amount = 0; if (args.length == 3) { amount = Integer.parseInt(args[2]); }
			    try {
			        if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
			            if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
			                playersOnline = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
			                for (Object objPlayer: playersOnline) {
				            	String objectivePlayer = removeOnline(((Player) objPlayer), sender, args[1], amount);
			                    if (objectivePlayer == null) { break; }
			                    else if (!objectivePlayer.isEmpty()) { removedPlayers.add(objectivePlayer); }
			                }
			            }
			        } else {
			            playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
			            for (Player objPlayer: playersOnlineOld) {
			            	String objectivePlayer = removeOnline(objPlayer, sender, args[1], amount);
		                    if (objectivePlayer == null) { break; }
		                    else if (!objectivePlayer.isEmpty()) { removedPlayers.add(objectivePlayer); }
			            }
			        }
			    } catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e);  }
		    	String[] placeHolders = LanguageAPI.getLang(false).newString(); 
		    	placeHolders[12] = removedPlayers.toString().replace("]", "").replace("[", ""); 
			    ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(null, args[1], null);
		    	if (itemMap != null) {
			    	placeHolders[3] = Utils.getUtils().translateLayout(itemMap.getCustomName(), null);
			    	if (amount == 0) { placeHolders[11] = "\u221e"; } else { placeHolders[11] = amount + ""; }
				    if (!removedPlayers.isEmpty()) { LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.fromOnlinePlayers", sender, placeHolders); } 
				    else { LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.notInOnlinePlayersInventory", sender, placeHolders); }
		    	}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("removeOnline")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove.others")) { LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.invalidOnlineSyntax", sender); } 
			else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("remove") && !Utils.getUtils().isInt(args[2]) || args.length == 4 && args[0].equalsIgnoreCase("remove") && Utils.getUtils().isInt(args[3])) {
			Player argsPlayer = PlayerHandler.getPlayer().getPlayerString(args[2]);
			if (argsPlayer == null && PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove.others")) {
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[2];
				LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove.others")) {
				boolean itemRemoved = false;
				int amount = 0; if (args.length == 4) { amount = Integer.parseInt(args[3]); }
				for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
					if (item.getConfigName().equalsIgnoreCase(args[1])) {
						String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = Utils.getUtils().translateLayout(item.getCustomName(), argsPlayer); 
						placeHolders[1] = sender.getName(); if (amount == 0) { placeHolders[11] = "\u221e"; } else { placeHolders[11] = amount + ""; }
						if (item.hasItem(argsPlayer)) {
							item.removeFrom(argsPlayer, amount);
							LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.fromYou", argsPlayer, placeHolders);
							placeHolders[1] = argsPlayer.getName();
							LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.fromTarget", sender, placeHolders);
						} else { 
							LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.triedRemove", argsPlayer, placeHolders);
							placeHolders[1] = argsPlayer.getName();
							LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.notInTargetInventory", sender, placeHolders);
						}
						itemRemoved = true;
					}
				}
				if (!itemRemoved) {	
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = args[1];
					LanguageAPI.getLang(false).sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); }
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove") || args.length == 3 && args[0].equalsIgnoreCase("remove") && Utils.getUtils().isInt(args[2])) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					boolean itemRemoved = false;
					int amount = 0; if (args.length == 3) { amount = Integer.parseInt(args[2]); }
					for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
						if (item.getConfigName().equalsIgnoreCase(args[1])) {
							String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = Utils.getUtils().translateLayout(item.getCustomName(), ((Player) sender)); if (amount == 0) { placeHolders[11] = "\u221e"; } else { placeHolders[11] = amount + ""; }
							if (item.hasItem(((Player) sender))) {
								item.removeFrom(((Player) sender), amount);
								LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.fromYou", sender, placeHolders);
							} else { 
								LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.notInInventory", sender, placeHolders); }
							itemRemoved = true;
						}
					}
					if (!itemRemoved) { 
						String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = args[1];
						LanguageAPI.getLang(false).sendLangMessage("Commands.Item.invalidItem", sender, placeHolders);
					}
				} else if (sender instanceof ConsoleCommandSender) {
					LanguageAPI.getLang(false).sendLangMessage("Commands.Default.notPlayer", sender);
					LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.usageSyntax", sender);
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove")) { LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.invalidSyntax", sender);
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("removeall")) {
			Player argsPlayer = PlayerHandler.getPlayer().getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove.others")) {
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = args[1];
				LanguageAPI.getLang(false).sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove.others")) {
				boolean itemRemoved = false;
				for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
					if (item.hasItem(argsPlayer)) {
						item.removeFrom(argsPlayer);
						itemRemoved = true;
					}
				}
				if (itemRemoved) {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = sender.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.RemoveAll.fromYou", argsPlayer, placeHolders);
					placeHolders[1] = argsPlayer.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.RemoveAll.fromTarget", sender, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[1] = sender.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.RemoveAll.triedRemove", argsPlayer, placeHolders);
					placeHolders[1] = argsPlayer.getName();
					LanguageAPI.getLang(false).sendLangMessage("Commands.RemoveAll.noItemsInTargetInventory", sender, placeHolders);
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("removeall")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.remove")) {
				if (sender instanceof Player) {
					boolean itemRemoved = false;
					for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
						if (item.hasItem(((Player) sender))) {
							item.removeFrom(((Player) sender));
							itemRemoved = true;
						}
					}
					if (itemRemoved) { LanguageAPI.getLang(false).sendLangMessage("Commands.RemoveAll.fromYou", sender); } 
					else { LanguageAPI.getLang(false).sendLangMessage("Commands.RemoveAll.noItemsInInventory", sender);  }
				} else if (sender instanceof ConsoleCommandSender) {
					LanguageAPI.getLang(false).sendLangMessage("Commands.Default.notPlayer", sender);
					LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.usageSyntax", sender);
				}
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("updates") || args[0].equalsIgnoreCase("update")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.updates")) {
				LanguageAPI.getLang(false).sendLangMessage("Commands.Updates.checking", sender);
				UpdateHandler.getUpdater(false).checkUpdates(sender, false);
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("AutoUpdate")) {
			if (PermissionsHandler.getPermissions().hasPermission(sender, "itemjoin.autoupdate")) {
				LanguageAPI.getLang(false).sendLangMessage("Commands.Updates.forcing", sender);
				UpdateHandler.getUpdater(false).forceUpdates(sender);
			} else { LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else {
			LanguageAPI.getLang(false).sendLangMessage("Commands.Default.unknownCommand", sender);
			return true;
		}
	}
	
   /**
	* Called when the CommandSender executes the getOnline command.
	* @param argsPlayer - The specified player recieving the item.
	* @param sender - Source of the command. 
	* @param args - Passed command arguments.
	* @param amount - Number of items to be given.
	* @return true if the item was successfully given.
	*/
	private String getOnline(Player argsPlayer, CommandSender sender, String args, int amount) {
        boolean itemExists = false;
        boolean itemGiven = false;
		final ItemMap probable = Chances.getChances().getRandom(argsPlayer);
        for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
            if (item.inWorld(argsPlayer.getWorld()) && item.getConfigName().equalsIgnoreCase(args) && Chances.getChances().isProbability(item, probable)) {
                itemExists = true;
                if (!(PermissionsHandler.getPermissions().permissionsEnabled()) || item.hasPermission(argsPlayer) && PermissionsHandler.getPermissions().permissionsEnabled()) {
                	if (!item.hasItem(argsPlayer) || amount != 0 || item.isAlwaysGive()) {
                		if (item.isAlwaysGive() && amount == 0) { amount = item.getCount(); }
                		item.giveTo(argsPlayer, amount);
						String[] placeHolders = LanguageAPI.getLang(false).newString(); 
						placeHolders[3] = Utils.getUtils().translateLayout(item.getCustomName(), argsPlayer); 
						placeHolders[1] = sender.getName(); if (amount == 0) { amount += 1; } placeHolders[11] = amount + "";
						LanguageAPI.getLang(false).sendLangMessage("Commands.Get.toYou", argsPlayer, placeHolders);
						itemGiven = true;
                	} else {
                		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = Utils.getUtils().translateLayout(item.getCustomName(), argsPlayer); placeHolders[1] = argsPlayer.getName();
    					LanguageAPI.getLang(false).sendLangMessage("Commands.Get.triedGive", argsPlayer, placeHolders);
                	}
                }
            }
        }
        if (!itemExists) { String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = args; 
    	LanguageAPI.getLang(false).sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); return null; }
        if (itemGiven) { return argsPlayer.getName(); }
        return "";
	}
	
   /**
	* Called when the CommandSender executes the removeOnline  command.
	* @param argsPlayer - The specified player losing the item.
	* @param sender - Source of the command. 
	* @param args - Passed command arguments.
	* @param amount - Number of items to be removed.
	* @return true if the item was successfully removed.
	*/
	private String removeOnline(Player argsPlayer, CommandSender sender, String args, int amount) {
        boolean itemExists = false;
        boolean itemRemoved = false;
        for (ItemMap item: ItemUtilities.getUtilities().getItems()) {
            if (item.getConfigName().equalsIgnoreCase(args)) {
                itemExists = true;
				String[] placeHolders = LanguageAPI.getLang(false).newString(); 
				placeHolders[3] = Utils.getUtils().translateLayout(item.getCustomName(), argsPlayer); placeHolders[1] = sender.getName(); 
				if (amount == 0) { placeHolders[11] = "\u221e"; } else { placeHolders[11] = amount + ""; }
                if (item.hasItem(argsPlayer)) {
                    item.removeFrom(argsPlayer, amount);
					LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.fromYou", argsPlayer, placeHolders);
					itemRemoved = true;
                } else {
					LanguageAPI.getLang(false).sendLangMessage("Commands.Remove.triedRemove", argsPlayer, placeHolders);
                }
            }
        }
        if (!itemExists) { String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[3] = args;
        LanguageAPI.getLang(false).sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); return null; }
        if (itemRemoved) { return argsPlayer.getName(); }
        return "";
	}
}