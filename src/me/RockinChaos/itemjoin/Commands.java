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
import me.RockinChaos.itemjoin.utils.Language;
import me.RockinChaos.itemjoin.utils.Legacy;
import me.RockinChaos.itemjoin.utils.Chances;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;
import me.RockinChaos.itemjoin.utils.sqlite.SQLite;

public class Commands implements CommandExecutor {
	public static HashMap < String, Boolean > cmdConfirm = new HashMap < String, Boolean > ();
	
	@Override
	public boolean onCommand(final CommandSender sender, Command c, String l, String[] args) {
		if (args.length == 0) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				Language.dispatchMessage(sender, "&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos");
				Language.dispatchMessage(sender, "&aType &a&l/ItemJoin Help &afor the help menu.");
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("help") || args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("1")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				Language.dispatchMessage(sender, "");
				Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.dispatchMessage(sender, "&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Help &7- &eThis help menu.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Reload &7- &eReloads the .yml files.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Updates &7- &eChecks for plugin updates.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin AutoUpdate &7- &eUpdate ItemJoin to latest version.");
				Language.dispatchMessage(sender, "&aType &a&l/ItemJoin Help 2 &afor the next page.");
				Language.dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 1/9 &a&l]&a&l&m---------------[");
				Language.dispatchMessage(sender, "");
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("2")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				Language.dispatchMessage(sender, "");
				Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.dispatchMessage(sender, "&a&l/ItemJoin List &7- &eCheck items you can get each what worlds.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin World &7- &eCheck what world you are in, debugging.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Menu &7- &eOpens the GUI Creator for custom items.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Permissions &7- &eLists the permissions you have.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Permissions 2 &7- &ePermissions page 2.");
				Language.dispatchMessage(sender, "&aType &a&l/ItemJoin Help 3 &afor the next page.");
				Language.dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 2/9 &a&l]&a&l&m---------------[");
				Language.dispatchMessage(sender, "");
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("3")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				Language.dispatchMessage(sender, "");
				Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Info &7- &eGets data-info of the held item.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Get <Item> &7- &eGives that ItemJoin item.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <Qty> &7- &eGives amount of said item.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <User> &7- &eGives to said player.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Get <Item> <User> <Qty> &7- &eGives qty to player.");
				Language.dispatchMessage(sender, "&aType &a&l/ItemJoin Help 4 &afor the next page.");
				Language.dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 3/9 &a&l]&a&l&m---------------[");
				Language.dispatchMessage(sender, "");
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("4")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				Language.dispatchMessage(sender, "");
				Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> &7- &eRemoves item from inventory.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <Qty> &7- &eRemoves qty of item.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <User> &7- &eRemoves from player.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Remove <Item> <User> <Qty> &7- &eRemoves qty.");
				Language.dispatchMessage(sender, "&aType &a&l/ItemJoin Help 5 &afor the next page.");
				Language.dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 4/9 &a&l]&a&l&m---------------[");
				Language.dispatchMessage(sender, "");
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("5")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				Language.dispatchMessage(sender, "");
				Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.dispatchMessage(sender, "&a&l/ItemJoin GetOnline <Item> &7- &eGives to all online.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin GetOnline <Item> <Qty> &7- &eGives qty to all online.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin RemoveOnline <Item> &7- &eRemoves from all online.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin RemoveOnline <Item> <Qty> &7- &eRemoves qty.");
				Language.dispatchMessage(sender, "&aType &a&l/ItemJoin Help 6 &afor the next page.");
				Language.dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 5/9 &a&l]&a&l&m---------------[");
				Language.dispatchMessage(sender, "");
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("6")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				Language.dispatchMessage(sender, "");
				Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.dispatchMessage(sender, "&a&l/ItemJoin GetAll &7- &eGives all ItemJoin items.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin GetAll <User> &7- &eGives all items to said player.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin RemoveAll &7- &eRemoves all ItemJoin items.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin RemoveAll <User> &7- &eRemoves from player.");
				Language.dispatchMessage(sender, "&aType &a&l/ItemJoin Help 7 &afor the next page.");
				Language.dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 6/9 &a&l]&a&l&m---------------[");
				Language.dispatchMessage(sender, "");
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("7")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				Language.dispatchMessage(sender, "");
				Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Enable &7- &eEnables ItemJoin for all players.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Enable <User> &7- &eEnables ItemJoin for player.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Enable <User> <World> &7- &eFor player/world.");
				Language.dispatchMessage(sender, "&aType &a&l/ItemJoin Help 8 &afor the next page.");
				Language.dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 7/9 &a&l]&a&l&m---------------[");
				Language.dispatchMessage(sender, "");
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("8")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				Language.dispatchMessage(sender, "");
				Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Disable &7- &eDisables ItemJoin for all players.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Disable <User> &7- &eDisables ItemJoin for player.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Disable <User> <World> &7- &eFor player/world.");
				Language.dispatchMessage(sender, "&aType &a&l/ItemJoin Help 9 &afor the next page.");
				Language.dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 8/9 &a&l]&a&l&m---------------[");
				Language.dispatchMessage(sender, "");
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("9")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				Language.dispatchMessage(sender, "");
				Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.dispatchMessage(sender, "&c&l[DANGER]&eThe Following Destroys Data &nPermanently!&e&c&l[DANGER]");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Purge &7- &eDeletes the database file!");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Purge first-join <User> &7- &eFirst-Join data.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Purge first-world <User> &7- &eFirst-World data.");
				Language.dispatchMessage(sender, "&a&l/ItemJoin Purge ip-limits <User> &7- &eIp-Limits data.");
				Language.dispatchMessage(sender, "&aFound a bug? Report it @");
				Language.dispatchMessage(sender, "&ahttps://github.com/RockinChaos/ItemJoin/issues");
				Language.dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 9/9 &a&l]&a&l&m---------------[");
				Language.dispatchMessage(sender, "");
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.reload")) {
				ConfigHandler.getSQLData().executeLaterStatements();
				ItemUtilities.closeAnimations();
				ItemUtilities.clearItems();
		  		ConfigHandler.generateData(null);
				Language.sendLangMessage("Commands.Default.configReload", sender);
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("info")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					if (PlayerHandler.getHandItem((Player) sender) != null && PlayerHandler.getHandItem((Player) sender).getType() != Material.AIR) {
						Language.dispatchMessage(sender, " ");
						Language.dispatchMessage(sender, "&a&l&m]-----------------&a&l[&e Item Info &a&l]&a&l&m----------------[");
						Language.dispatchMessage(sender, "");
						String[] placeHolders = Language.newString(); placeHolders[3] = PlayerHandler.getHandItem((Player) sender).getType().toString();
						Language.sendLangMessage("Commands.Info.material", sender, placeHolders);
						if (!ServerHandler.hasSpecificUpdate("1_13")) {
							placeHolders[3] = Legacy.getDataValue(PlayerHandler.getHandItem((Player) sender)) + "";
							Language.sendLangMessage("Commands.Info.dataValue", sender, placeHolders);
						}
						Language.dispatchMessage(sender, "");
						Language.dispatchMessage(sender, "&a&l&m]---------------&a&l[&e Item Info Menu &a&l]&a&l&m--------------[");
						Language.dispatchMessage(sender, " ");
					} else { Language.sendLangMessage("Commands.Item.notInHand", sender); }
				} else if (sender instanceof ConsoleCommandSender) { Language.sendLangMessage("Commands.Default.notPlayer", sender); }
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("purge")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.purge")) {
				if (cmdConfirm.get(1 + sender.getName()) != null && cmdConfirm.get(1 + sender.getName()).equals(true)) {
					SQLite.purgeDatabase("database");
			        ConfigHandler.setSQLData(new SQLData());
					String[] placeHolders = Language.newString(); placeHolders[1] = "All Players"; placeHolders[10] = "Database"; placeHolders[9] = "/ij purge";
					Language.sendLangMessage("Commands.Database.purgeSuccess", sender, placeHolders);
					cmdConfirm.remove(1 + sender.getName());
				} else {
					cmdConfirm.put(1 + sender.getName(), true);
					String[] placeHolders = Language.newString(); placeHolders[1] = "All Players"; placeHolders[10] = "Database"; placeHolders[9] = "/ij purge";
					Language.sendLangMessage("Commands.Database.purgeWarn", sender, placeHolders);
					Language.sendLangMessage("Commands.Database.purgeConfirm", sender, placeHolders);
					new BukkitRunnable() {
						@Override
						public void run() {
							if (cmdConfirm.get(1 + sender.getName()) != null && cmdConfirm.get(1 + sender.getName()).equals(true)) {
								Language.sendLangMessage("Commands.Database.purgeTimeOut", sender);
								cmdConfirm.remove(1 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length >= 3 && args[0].equalsIgnoreCase("purge") && args[1].equalsIgnoreCase("ip-limits") 
				|| args.length >= 3 && args[0].equalsIgnoreCase("purge") && args[1].equalsIgnoreCase("first-join")
				|| args.length >= 3 && args[0].equalsIgnoreCase("purge") && args[1].equalsIgnoreCase("first-world")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.purge")) {
				OfflinePlayer player = PlayerHandler.getOfflinePlayer(args[2]);
				if (player == null) {
					String[] placeHolders = Language.newString(); placeHolders[1] = args[2];
					Language.sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
				} else if (cmdConfirm.get(2 + sender.getName()) != null && cmdConfirm.get(2 + sender.getName()).equals(true) && args[1].equalsIgnoreCase("ip-limits")) {
					ConfigHandler.getSQLData().purgeDatabaseData("ip_limits", player);
					String[] placeHolders = Language.newString(); placeHolders[1] = args[2]; placeHolders[10] = "ip-limits"; placeHolders[9] = "/ij purge ip-limits <player>";
					Language.sendLangMessage("Commands.Database.purgeSuccess", sender, placeHolders);
					cmdConfirm.remove(2 + sender.getName());
				} else if (cmdConfirm.get(3 + sender.getName()) != null && cmdConfirm.get(3 + sender.getName()).equals(true) && args[1].equalsIgnoreCase("first-join")) {
					ConfigHandler.getSQLData().purgeDatabaseData("first_join", player);
					String[] placeHolders = Language.newString(); placeHolders[1] = args[2]; placeHolders[10] = "first-join"; placeHolders[9] = "/ij purge first-join <player>";
					Language.sendLangMessage("Commands.Database.purgeSuccess", sender, placeHolders);
					cmdConfirm.remove(3 + sender.getName());
				} else if (cmdConfirm.get(3 + sender.getName()) != null && cmdConfirm.get(3 + sender.getName()).equals(true) && args[1].equalsIgnoreCase("first-world")) {
					ConfigHandler.getSQLData().purgeDatabaseData("first_world", player);
					String[] placeHolders = Language.newString(); placeHolders[1] = args[2]; placeHolders[10] = "first-world"; placeHolders[9] = "/ij purge first-world <player>";
					Language.sendLangMessage("Commands.Database.purgeSuccess", sender, placeHolders);
					cmdConfirm.remove(3 + sender.getName());
				} else if (cmdConfirm.get(2 + sender.getName()) == null && args[1].equalsIgnoreCase("ip-limits")) {
					cmdConfirm.put(2 + sender.getName(), true);
					String[] placeHolders = Language.newString(); placeHolders[1] = args[2]; placeHolders[10] = "ip-limits"; placeHolders[9] = "/ij purge ip-limits <player>";
					Language.sendLangMessage("Commands.Database.purgeWarn", sender, placeHolders);
					Language.sendLangMessage("Commands.Database.purgeConfirm", sender, placeHolders);
					new BukkitRunnable() {
						@Override
						public void run() {
							if (cmdConfirm.get(2 + sender.getName()) != null && cmdConfirm.get(2 + sender.getName()).equals(true)) {
								Language.sendLangMessage("Commands.Database.purgeTimeOut", sender);
								cmdConfirm.remove(2 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				} else if (cmdConfirm.get(3 + sender.getName()) == null && args[1].equalsIgnoreCase("first-join")) {
					cmdConfirm.put(3 + sender.getName(), true);
					String[] placeHolders = Language.newString(); placeHolders[1] = args[2]; placeHolders[10] = "first-join"; placeHolders[9] = "/ij purge first-join <player>";
					Language.sendLangMessage("Commands.Database.purgeWarn", sender, placeHolders);
					Language.sendLangMessage("Commands.Database.purgeConfirm", sender, placeHolders);
					new BukkitRunnable() {
						@Override
						public void run() {
							if (cmdConfirm.get(3 + sender.getName()) != null && cmdConfirm.get(3 + sender.getName()).equals(true)) {
								Language.sendLangMessage("Commands.Database.purgeTimeOut", sender);
								cmdConfirm.remove(3 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				} else if (cmdConfirm.get(3 + sender.getName()) == null && args[1].equalsIgnoreCase("first-world")) {
					cmdConfirm.put(3 + sender.getName(), true);
					String[] placeHolders = Language.newString(); placeHolders[1] = args[2]; placeHolders[10] = "first-world"; placeHolders[9] = "/ij purge first-world <player>";
					Language.sendLangMessage("Commands.Database.purgeWarn", sender, placeHolders);
					Language.sendLangMessage("Commands.Database.purgeConfirm", sender, placeHolders);
					new BukkitRunnable() {
						@Override
						public void run() {
							if (cmdConfirm.get(3 + sender.getName()) != null && cmdConfirm.get(3 + sender.getName()).equals(true)) {
								Language.sendLangMessage("Commands.Database.purgeTimeOut", sender);
								cmdConfirm.remove(3 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("menu") || args[0].equalsIgnoreCase("creator")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.menu")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					ConfigHandler.getItemCreator().startMenu(sender);
					Language.sendLangMessage("Commands.UI.creatorLaunched", sender);
				} else if (sender instanceof ConsoleCommandSender) { Language.sendLangMessage("Commands.Default.notPlayer", sender); }
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("enable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasPermission(sender, "itemjoin.enable.others")) { 
				String[] placeHolders = Language.newString(); placeHolders[1] = args[1];
				Language.sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.hasPermission(sender, "itemjoin.enable.others")) {
				if (ConfigHandler.getSQLData().isWritable("Global", PlayerHandler.getPlayerID(argsPlayer))) {
					String[] placeHolders = Language.newString(); placeHolders[1] = args[1];
					Language.sendLangMessage("Commands.Enabled.forPlayer", sender, placeHolders);
				} else {
					ConfigHandler.getSQLData().saveToDatabase(argsPlayer, "Global", "true", "enabled-players");
					String[] placeHolders = Language.newString(); placeHolders[1] = argsPlayer.getName();
					Language.sendLangMessage("Commands.Enabled.forPlayer", sender, placeHolders);
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						placeHolders[1] = sender.getName();
						Language.sendLangMessage("Commands.Enabled.forTarget", argsPlayer, placeHolders);
					}
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("enable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			String world = args[2];
			if (argsPlayer == null && PermissionsHandler.hasPermission(sender, "itemjoin.enable.others")) { 
				String[] placeHolders = Language.newString(); placeHolders[1] = args[1];
				Language.sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.hasPermission(sender, "itemjoin.enable.others")) {
				if (ConfigHandler.getSQLData().isWritable(world, PlayerHandler.getPlayerID(argsPlayer))) { 
					String[] placeHolders = Language.newString(); placeHolders[1] = args[1]; placeHolders[0] = world;
					Language.sendLangMessage("Commands.Enabled.forPlayerWorldFailed", sender, placeHolders); 
				} else {
					ConfigHandler.getSQLData().saveToDatabase(argsPlayer, world, "true", "enabled-players");
					String[] placeHolders = Language.newString(); placeHolders[1] = args[1]; placeHolders[0] = world;
					Language.sendLangMessage("Commands.Enabled.forPlayerWorld", sender, placeHolders); 
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						placeHolders[1] = sender.getName();
						Language.sendLangMessage("Commands.Enabled.forTargetWorld", argsPlayer, placeHolders); 
					}
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("enable")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.enable")) {
				if (!ConfigHandler.getSQLData().isWritable("Global", "ALL")) {
					ConfigHandler.getSQLData().saveToDatabase(null, "Global", "true", "enabled-players");
					Language.sendLangMessage("Commands.Enabled.globalPlayers", sender);
				} else { Language.sendLangMessage("Commands.Enabled.globalPlayersFailed", sender);  }
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("disable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasPermission(sender, "itemjoin.disable.others")) {
				String[] placeHolders = Language.newString(); placeHolders[1] = args[1];
				Language.sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.hasPermission(sender, "itemjoin.disable.others")) {
				if (!ConfigHandler.getSQLData().isWritable("Global", PlayerHandler.getPlayerID(argsPlayer))) { 
					String[] placeHolders = Language.newString(); placeHolders[1] = args[1];
					Language.sendLangMessage("Commands.Disabled.forPlayerFailed", sender, placeHolders); 
				} else {
					ConfigHandler.getSQLData().saveToDatabase(argsPlayer, "Global", "false", "disabled-players");
					String[] placeHolders = Language.newString(); placeHolders[1] = args[1];
					Language.sendLangMessage("Commands.Disabled.forPlayer", sender, placeHolders); 
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						placeHolders[1] = sender.getName();
						Language.sendLangMessage("Commands.Disabled.forTarget", argsPlayer, placeHolders); 
					}
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("disable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			String world = args[2];
			if (argsPlayer == null && PermissionsHandler.hasPermission(sender, "itemjoin.disable.others")) {
				String[] placeHolders = Language.newString(); placeHolders[1] = args[1];
				Language.sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.hasPermission(sender, "itemjoin.disable.others")) {
				if (!ConfigHandler.getSQLData().isWritable(world, PlayerHandler.getPlayerID(argsPlayer))) {
					String[] placeHolders = Language.newString(); placeHolders[1] = args[1]; placeHolders[0] = world;
					Language.sendLangMessage("Commands.Disabled.forPlayerWorldFailed", sender, placeHolders); 
				} else {
					String[] placeHolders = Language.newString(); placeHolders[1] = args[1]; placeHolders[0] = world;
					Language.sendLangMessage("Commands.Disabled.forPlayerWorld", sender, placeHolders); 
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						placeHolders[1] = sender.getName();
						Language.sendLangMessage("Commands.Disabled.forTargetWorld", argsPlayer, placeHolders); 
					}
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("disable")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.disable")) {
				if (ConfigHandler.getSQLData().isWritable("Global", "ALL")) {
					ConfigHandler.getSQLData().saveToDatabase(null, "Global", "false", "disabled-players");
					Language.sendLangMessage("Commands.Disabled.globalPlayers", sender); 
				} else {Language.sendLangMessage("Commands.Disabled.globalPlayersFailed", sender);  }
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					Language.dispatchMessage(sender, "");
					String[] placeHolders = Language.newString(); placeHolders[0] = ((Player) sender).getWorld().getName();
					Language.sendLangMessage("Commands.World.worldHeader", sender, placeHolders); 
					Language.sendLangMessage("Commands.World.worldRow", sender, placeHolders); 
					Language.dispatchMessage(sender, "");
					Language.dispatchMessage(sender, "&a&l&m]--------------&a&l[&e Worlds In Menu 1/1 &a&l]&a&l&m-------------[");
				} else if (sender instanceof ConsoleCommandSender) { Language.sendLangMessage("Commands.Default.notPlayer", sender); }
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("list")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.list")) {
					boolean ItemExists = false;
					Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
						ItemExists = false;
						String[] placeHolders = Language.newString(); placeHolders[0] = world.getName();
						Language.sendLangMessage("Commands.List.worldHeader", sender, placeHolders); 
						List <String> inputListed = new ArrayList<String>();
						for (ItemMap item: ItemUtilities.getItems()) {
							if (!inputListed.contains(item.getConfigName()) && item.getTempItem() != null && item.inWorld(world)) {
								inputListed.add(item.getConfigName());
								placeHolders[3] = item.getConfigName(); placeHolders[4] = item.getConfigName();
								Language.sendLangMessage("Commands.List.itemRow", sender, placeHolders); 
								ItemExists = true;
							}
						}
						if (ItemExists == false) { Language.sendLangMessage("Commands.List.noItemsDefined", sender);  }
					}
					Language.dispatchMessage(sender, "&a&l&m]----------------&a&l[&e List Menu 1/1 &a&l]&a&l&m---------------[");
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("permissions")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.permissions")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					if (PermissionsHandler.hasPermission(sender, "itemjoin.*")) { Language.dispatchMessage(sender, "&a[\u2714] ItemJoin.*"); } 
					else { Language.dispatchMessage(sender, "&c[\u2718] ItemJoin.*"); }
					if (PermissionsHandler.hasPermission(sender, "itemjoin.all")) { Language.dispatchMessage(sender, "&a[\u2714] ItemJoin.All"); } 
					else { Language.dispatchMessage(sender, "&c[\u2718] ItemJoin.All"); }
					if (PermissionsHandler.hasPermission(sender, "itemjoin.use")) { Language.dispatchMessage(sender, "&a[\u2714] ItemJoin.Use"); } 
					else { Language.dispatchMessage(sender, "&c[\u2718] ItemJoin.Use"); }
					if (PermissionsHandler.hasPermission(sender, "itemjoin.reload")) { Language.dispatchMessage(sender, "&a[\u2714] ItemJoin.Reload"); } 
					else { Language.dispatchMessage(sender, "&c[\u2718] ItemJoin.Reload"); }
					if (PermissionsHandler.hasPermission(sender, "itemjoin.updates")) { Language.dispatchMessage(sender, "&a[\u2714] ItemJoin.Updates"); }
					else { Language.dispatchMessage(sender, "&c[\u2718] ItemJoin.Updates"); }
					if (PermissionsHandler.hasPermission(sender, "itemjoin.autoupdate")) { Language.dispatchMessage(sender, "&a[\u2714] ItemJoin.AutoUpdate"); }
					else { Language.dispatchMessage(sender, "&c[\u2718] ItemJoin.AutoUpdate"); }
					if (PermissionsHandler.hasPermission(sender, "itemjoin.get")) { Language.dispatchMessage(sender, "&a[\u2714] ItemJoin.get"); } 
					else { Language.dispatchMessage(sender, "&c[\u2718] ItemJoin.get"); }
					if (PermissionsHandler.hasPermission(sender, "itemjoin.get.others")) { Language.dispatchMessage(sender, "&a[\u2714] ItemJoin.get.others"); }
					else { Language.dispatchMessage(sender, "&c[\u2718] ItemJoin.get.others"); }
					if (PermissionsHandler.hasPermission(sender, "itemjoin.permissions")) { Language.dispatchMessage(sender, "&a[\u2714] ItemJoin.permissions"); } 
					else { Language.dispatchMessage(sender, "&c[\u2718] ItemJoin.permissions"); }
					for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
						if (PermissionsHandler.hasPermission(sender, "itemjoin." + world.getName() + ".*")) { Language.dispatchMessage(sender, "&a[\u2714] ItemJoin." + world.getName() + ".*"); } 
						else { Language.dispatchMessage(sender, "&c[\u2718] ItemJoin." + world.getName() + ".*"); }
					}
					Language.dispatchMessage(sender, "&aType &a&l/ItemJoin Permissions 2 &afor the next page.");
					Language.dispatchMessage(sender, "&a&l&m]------------&a&l[&e Permissions Menu 1/2 &a&l]&a&l&m------------[");
				} else if (sender instanceof ConsoleCommandSender) { Language.sendLangMessage("Commands.Default.notPlayer", sender); }
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("permissions") && args[1].equalsIgnoreCase("2")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.permissions")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					Language.dispatchMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					List<String> customPermissions = new ArrayList<String>();
					for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
						List <String> inputListed = new ArrayList<String>();
						final Chances probability = new Chances();
						final ItemMap probable = probability.getRandom(((Player) sender));
						for (ItemMap item: ItemUtilities.getItems()) {
							if (!customPermissions.contains(item.getPermissionNode()) && !inputListed.contains(item.getConfigName()) && item.inWorld(world) && probability.isProbability(item, probable)) {
								if (item.getPermissionNode() != null && !customPermissions.contains(item.getPermissionNode()) || item.getPermissionNode() == null) {
									if (item.getPermissionNode() != null) { customPermissions.add(item.getPermissionNode()); }
									inputListed.add(item.getConfigName());
									if (item.hasPermission(((Player) sender))) { Language.dispatchMessage(sender, "&a[\u2714] " + PermissionsHandler.customPermissions(item.getPermissionNode(), item.getConfigName(), world.getName())); } 
									else { Language.dispatchMessage(sender, "&c[\u2718] " + PermissionsHandler.customPermissions(item.getPermissionNode(), item.getConfigName(), world.getName())); }
								}
							}
						}
					}
					Language.dispatchMessage(sender, "&a&l&m]------------&a&l[&e Permissions Menu 2/2 &a&l]&a&l&m------------[");
				} else if (sender instanceof ConsoleCommandSender) { Language.sendLangMessage("Commands.Default.notPlayer", sender); }
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("getOnline") || args.length == 3 && args[0].equalsIgnoreCase("getOnline") && Utils.isInt(args[2])) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.get.others")) {
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
			    } catch (Exception e) { ServerHandler.sendDebugTrace(e);  }
			    ItemMap itemMap = ItemUtilities.getItemMap(null, args[1], null);
		    	if (itemMap != null) { 
		    	String[] placeHolders = Language.newString(); placeHolders[12] = givenPlayers.toString().replace("]", "").replace("[", ""); placeHolders[3] = Utils.translateLayout(itemMap.getCustomName(), null);
			    if (amount == 0) { amount = itemMap.getCount(); }
				    placeHolders[11] = amount + "";
			    	if (!givenPlayers.isEmpty()) { Language.sendLangMessage("Commands.Get.toOnlinePlayers", sender, placeHolders); } 
				    else { Language.sendLangMessage("Commands.Get.onlinePlayersHaveItem", sender, placeHolders); }
		    	}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("getOnline")) {
				if (PermissionsHandler.hasPermission(sender, "itemjoin.get.others")) { Language.sendLangMessage("Commands.Get.invalidOnlineSyntax", sender); } 
				else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
				return true;
		} else if (args.length == 4 && args[0].equalsIgnoreCase("get") && Utils.isInt(args[3]) || args.length == 3 && args[0].equalsIgnoreCase("get") && !Utils.isInt(args[2])) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.get.others")) {
				Player argsPlayer = PlayerHandler.getPlayerString(args[2]);
				int amount = 0;
				if (args.length == 4) { amount = Integer.parseInt(args[3]); } 
				if (argsPlayer == null) { String[] placeHolders = Language.newString(); placeHolders[1] = args[2];
				Language.sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders);  return true; }
				boolean itemGiven = false;
				final Chances probability = new Chances();
				final ItemMap probable = probability.getRandom(argsPlayer);
				for (ItemMap item: ItemUtilities.getItems()) {
					if (item.inWorld(argsPlayer.getWorld()) && item.getConfigName().equalsIgnoreCase(args[1]) && probability.isProbability(item, probable)) {
						String customName = Utils.translateLayout(item.getCustomName(), null);
						if (sender instanceof Player) { customName = Utils.translateLayout(item.getCustomName(), ((Player) sender)); }
						if (!item.hasItem(argsPlayer) || amount != 0 || item.isAlwaysGive()) {
							if (!(ConfigHandler.getItemPermissions()) || item.hasPermission(argsPlayer) && ConfigHandler.getItemPermissions()) {
								if (item.isAlwaysGive() && args.length != 4) { amount = item.getCount(); }
								item.giveTo(argsPlayer, true, amount);
								String[] placeHolders = Language.newString(); placeHolders[1] = sender.getName(); placeHolders[3] = customName; if (amount == 0) { amount += 1; } placeHolders[11] = amount + "";
								Language.sendLangMessage("Commands.Get.toYou", argsPlayer, placeHolders);
								placeHolders[1] = argsPlayer.getName();
								Language.sendLangMessage("Commands.Get.toTarget", sender, placeHolders);
							} else { 
								String[] placeHolders = Language.newString(); placeHolders[1] = argsPlayer.getName(); placeHolders[3] = customName;
								Language.sendLangMessage("Commands.Get.targetNoPermission", sender, placeHolders);
							}
						} else {
							String[] placeHolders = Language.newString(); placeHolders[1] = sender.getName(); placeHolders[3] = customName;
							Language.sendLangMessage("Commands.Get.triedGive", argsPlayer, placeHolders);
							placeHolders[1] = argsPlayer.getName();
							Language.sendLangMessage("Commands.Get.targetHasItem", sender, placeHolders);
						}
						itemGiven = true;
					}
				}
				if (!itemGiven) { String[] placeHolders = Language.newString(); placeHolders[3] = args[1];
				Language.sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); }
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("get") && Utils.isInt(args[2]) || args.length == 2 && args[0].equalsIgnoreCase("get")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.get")) {
				int amount = 0;
				if (args.length == 3) { amount = Integer.parseInt(args[2]); }
				if (!(sender instanceof ConsoleCommandSender)) {
					boolean itemGiven = false;
					final Chances probability = new Chances();
					final ItemMap probable = probability.getRandom(((Player) sender));
					for (ItemMap item: ItemUtilities.getItems()) {
						if (item.inWorld(((Player) sender).getWorld()) && item.getConfigName().equalsIgnoreCase(args[1]) && probability.isProbability(item, probable)) {
							String customName = Utils.translateLayout(item.getCustomName(), ((Player) sender));
							if (!item.hasItem(((Player) sender)) || amount != 0 || item.isAlwaysGive()) {
								if (!(ConfigHandler.getItemPermissions()) || item.hasPermission(((Player) sender)) && ConfigHandler.getItemPermissions()) {
									item.giveTo(((Player) sender), true, amount);
									String[] placeHolders = Language.newString(); placeHolders[3] = customName; 
									if (amount == 0) { amount += 1; } placeHolders[11] = amount + "";
									Language.sendLangMessage("Commands.Get.toYou", sender, placeHolders);
								} else {
									String[] placeHolders = Language.newString(); placeHolders[3] = customName;
									Language.sendLangMessage("Commands.Get.noPermission", sender, placeHolders);
								}
							} else { 
								String[] placeHolders = Language.newString(); placeHolders[3] = customName;
								Language.sendLangMessage("Commands.Get.youHaveItem", sender, placeHolders);
							}
							itemGiven = true;
						}
					}
					if (!itemGiven) { String[] placeHolders = Language.newString(); placeHolders[3] = args[1];
					Language.sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); }
				} else if (sender instanceof ConsoleCommandSender) {
					Language.sendLangMessage("Commands.Default.notPlayer", sender);
					Language.sendLangMessage("Commands.Get.usageSyntax", sender);
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("get")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.get")) { Language.sendLangMessage("Commands.Get.invalidSyntax", sender); } 
			else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("getall")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasPermission(sender, "itemjoin.get.others")) {
				String[] placeHolders = Language.newString(); placeHolders[1] = args[1];
				Language.sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.hasPermission(sender, "itemjoin.get.others")) {
				boolean itemGiven = false;
				boolean itemPermission = false;
				final Chances probability = new Chances();
				final ItemMap probable = probability.getRandom(argsPlayer);
				for (ItemMap item: ItemUtilities.getItems()) {
					if (item.inWorld(argsPlayer.getWorld()) && probability.isProbability(item, probable)) {
						if (!(ConfigHandler.getItemPermissions()) || item.hasPermission(argsPlayer) && ConfigHandler.getItemPermissions()) {
							if (!item.hasItem(argsPlayer) || item.isAlwaysGive()) {
								item.giveTo(argsPlayer, !item.isAlwaysGive(), 0);
								itemGiven = true;
							}
						} else { itemPermission = true; }
					}
				}
				if (itemGiven) {
					String[] placeHolders = Language.newString(); placeHolders[1] = sender.getName();
					Language.sendLangMessage("Commands.GetAll.toYou", argsPlayer, placeHolders);
					placeHolders[1] = argsPlayer.getName();
					Language.sendLangMessage("Commands.GetAll.toTarget", sender, placeHolders);
				} else {
					String[] placeHolders = Language.newString(); placeHolders[1] = sender.getName();
					Language.sendLangMessage("Commands.GetAll.triedGive", argsPlayer, placeHolders);
					placeHolders[1] = argsPlayer.getName();
					Language.sendLangMessage("Commands.GetAll.targetHasItems", sender, placeHolders);
				}
				if (itemPermission) {
					String[] placeHolders = Language.newString(); placeHolders[1] = argsPlayer.getName();
					Language.sendLangMessage("Commands.GetAll.targetNoPermission", sender, placeHolders);
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("getall")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.get")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					boolean itemGiven = false;
					boolean itemPermission = false;
					final Chances probability = new Chances();
					final ItemMap probable = probability.getRandom(((Player) sender));
					for (ItemMap item: ItemUtilities.getItems()) {
						if (item.inWorld(((Player) sender).getWorld()) && probability.isProbability(item, probable)) {
							if (!(ConfigHandler.getItemPermissions()) || item.hasPermission(((Player) sender)) && ConfigHandler.getItemPermissions()) {
								if (!item.hasItem(((Player) sender)) || item.isAlwaysGive()) {
									item.giveTo(((Player) sender), !item.isAlwaysGive(), 0);
									itemGiven = true;
								}
							} else { itemPermission = true; } 
						}
					}
					if (itemGiven) {
						Language.sendLangMessage("Commands.GetAll.toYou", sender);
					} else {
						Language.sendLangMessage("Commands.GetAll.youHaveItems", sender);
					} if (itemPermission) {
						Language.sendLangMessage("Commands.GetAll.noPermission", sender);
					}
				} else if (sender instanceof ConsoleCommandSender) {
					Language.sendLangMessage("Commands.Default.notPlayer", sender);
					Language.sendLangMessage("Commands.Get.usageSynax", sender);
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("removeOnline") || args.length == 3 && args[0].equalsIgnoreCase("removeOnline") && Utils.isInt(args[2])) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.remove.others")) {
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
			    } catch (Exception e) { ServerHandler.sendDebugTrace(e);  }
		    	String[] placeHolders = Language.newString(); 
		    	placeHolders[12] = removedPlayers.toString().replace("]", "").replace("[", ""); 
			    ItemMap itemMap = ItemUtilities.getItemMap(null, args[1], null);
		    	if (itemMap != null) {
			    	placeHolders[3] = Utils.translateLayout(itemMap.getCustomName(), null);
			    	if (amount == 0) { placeHolders[11] = "\u221e"; } else { placeHolders[11] = amount + ""; }
				    if (!removedPlayers.isEmpty()) { Language.sendLangMessage("Commands.Remove.fromOnlinePlayers", sender, placeHolders); } 
				    else { Language.sendLangMessage("Commands.Remove.notInOnlinePlayersInventory", sender, placeHolders); }
		    	}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("removeOnline")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.remove.others")) { Language.sendLangMessage("Commands.Remove.invalidOnlineSyntax", sender); } 
			else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("remove") && !Utils.isInt(args[2]) || args.length == 4 && args[0].equalsIgnoreCase("remove") && Utils.isInt(args[3])) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[2]);
			if (argsPlayer == null && PermissionsHandler.hasPermission(sender, "itemjoin.remove.others")) {
				String[] placeHolders = Language.newString(); placeHolders[1] = args[2];
				Language.sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.hasPermission(sender, "itemjoin.remove.others")) {
				boolean itemRemoved = false;
				int amount = 0; if (args.length == 4) { amount = Integer.parseInt(args[3]); }
				for (ItemMap item: ItemUtilities.getItems()) {
					if (item.getConfigName().equalsIgnoreCase(args[1])) {
						String[] placeHolders = Language.newString(); placeHolders[3] = Utils.translateLayout(item.getCustomName(), argsPlayer); 
						placeHolders[1] = sender.getName(); if (amount == 0) { placeHolders[11] = "\u221e"; } else { placeHolders[11] = amount + ""; }
						if (item.hasItem(argsPlayer)) {
							item.removeFrom(argsPlayer, amount);
							Language.sendLangMessage("Commands.Remove.fromYou", argsPlayer, placeHolders);
							placeHolders[1] = argsPlayer.getName();
							Language.sendLangMessage("Commands.Remove.fromTarget", sender, placeHolders);
						} else { 
							Language.sendLangMessage("Commands.Remove.triedRemove", argsPlayer, placeHolders);
							placeHolders[1] = argsPlayer.getName();
							Language.sendLangMessage("Commands.Remove.notInTargetInventory", sender, placeHolders);
						}
						itemRemoved = true;
					}
				}
				if (!itemRemoved) {	
					String[] placeHolders = Language.newString(); placeHolders[3] = args[1];
					Language.sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); }
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove") || args.length == 3 && args[0].equalsIgnoreCase("remove") && Utils.isInt(args[2])) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.remove")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					boolean itemRemoved = false;
					int amount = 0; if (args.length == 3) { amount = Integer.parseInt(args[2]); }
					for (ItemMap item: ItemUtilities.getItems()) {
						if (item.getConfigName().equalsIgnoreCase(args[1])) {
							String[] placeHolders = Language.newString(); placeHolders[3] = Utils.translateLayout(item.getCustomName(), ((Player) sender)); if (amount == 0) { placeHolders[11] = "\u221e"; } else { placeHolders[11] = amount + ""; }
							if (item.hasItem(((Player) sender))) {
								item.removeFrom(((Player) sender), amount);
								Language.sendLangMessage("Commands.Remove.fromYou", sender, placeHolders);
							} else { 
								Language.sendLangMessage("Commands.Remove.notInInventory", sender, placeHolders); }
							itemRemoved = true;
						}
					}
					if (!itemRemoved) { 
						String[] placeHolders = Language.newString(); placeHolders[3] = args[1];
						Language.sendLangMessage("Commands.Item.invalidItem", sender, placeHolders);
					}
				} else if (sender instanceof ConsoleCommandSender) {
					Language.sendLangMessage("Commands.Default.notPlayer", sender);
					Language.sendLangMessage("Commands.Remove.usageSyntax", sender);
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.remove")) { Language.sendLangMessage("Commands.Remove.invalidSyntax", sender);
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("removeall")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasPermission(sender, "itemjoin.remove.others")) {
				String[] placeHolders = Language.newString(); placeHolders[1] = args[1];
				Language.sendLangMessage("Commands.Default.targetNotFound", sender, placeHolders); 
			} else if (PermissionsHandler.hasPermission(sender, "itemjoin.remove.others")) {
				boolean itemRemoved = false;
				for (ItemMap item: ItemUtilities.getItems()) {
					if (item.hasItem(argsPlayer)) {
						item.removeFrom(argsPlayer, 0);
						itemRemoved = true;
					}
				}
				if (itemRemoved) {
					String[] placeHolders = Language.newString(); placeHolders[1] = sender.getName();
					Language.sendLangMessage("Commands.RemoveAll.fromYou", argsPlayer, placeHolders);
					placeHolders[1] = argsPlayer.getName();
					Language.sendLangMessage("Commands.RemoveAll.fromTarget", sender, placeHolders);
				} else {
					String[] placeHolders = Language.newString(); placeHolders[1] = sender.getName();
					Language.sendLangMessage("Commands.RemoveAll.triedRemove", argsPlayer, placeHolders);
					placeHolders[1] = argsPlayer.getName();
					Language.sendLangMessage("Commands.RemoveAll.noItemsInTargetInventory", sender, placeHolders);
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("removeall")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.remove")) {
				if (sender instanceof Player) {
					boolean itemRemoved = false;
					for (ItemMap item: ItemUtilities.getItems()) {
						if (item.hasItem(((Player) sender))) {
							item.removeFrom(((Player) sender), 0);
							itemRemoved = true;
						}
					}
					if (itemRemoved) { Language.sendLangMessage("Commands.RemoveAll.fromYou", sender); } 
					else { Language.sendLangMessage("Commands.RemoveAll.noItemsInInventory", sender);  }
				} else if (sender instanceof ConsoleCommandSender) {
					Language.sendLangMessage("Commands.Default.notPlayer", sender);
					Language.sendLangMessage("Commands.Remove.usageSyntax", sender);
				}
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("updates") || args[0].equalsIgnoreCase("update")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.updates")) {
				Language.sendLangMessage("Commands.Updates.checking", sender);
				ConfigHandler.getUpdater().checkUpdates(sender, false);
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else if (args[0].equalsIgnoreCase("AutoUpdate")) {
			if (PermissionsHandler.hasPermission(sender, "itemjoin.autoupdate")) {
				Language.sendLangMessage("Commands.Updates.forcing", sender);
				ConfigHandler.getUpdater().forceUpdates(sender);
			} else { Language.sendLangMessage("Commands.Default.noPermission", sender); }
			return true;
		} else {
			Language.sendLangMessage("Commands.Default.unknownCommand", sender);
			return true;
		}
	}
	
	private String getOnline(Player argsPlayer, CommandSender sender, String args, int amount) {
        boolean itemExists = false;
        boolean itemGiven = false;
        final Chances probability = new Chances();
		final ItemMap probable = probability.getRandom(argsPlayer);
        for (ItemMap item: ItemUtilities.getItems()) {
            if (item.inWorld(argsPlayer.getWorld()) && item.getConfigName().equalsIgnoreCase(args) && probability.isProbability(item, probable)) {
                itemExists = true;
                if (!(ConfigHandler.getItemPermissions()) || item.hasPermission(argsPlayer) && ConfigHandler.getItemPermissions()) {
                	if (!item.hasItem(argsPlayer) || amount != 0 || item.isAlwaysGive()) {
                		if (item.isAlwaysGive() && amount == 0) { amount = item.getCount(); }
                		item.giveTo(argsPlayer, true, amount);
						String[] placeHolders = Language.newString(); 
						placeHolders[3] = Utils.translateLayout(item.getCustomName(), argsPlayer); 
						placeHolders[1] = sender.getName(); if (amount == 0) { amount += 1; } placeHolders[11] = amount + "";
						Language.sendLangMessage("Commands.Get.toYou", argsPlayer, placeHolders);
						itemGiven = true;
                	} else {
                		String[] placeHolders = Language.newString(); placeHolders[3] = Utils.translateLayout(item.getCustomName(), argsPlayer); placeHolders[1] = argsPlayer.getName();
    					Language.sendLangMessage("Commands.Get.triedGive", argsPlayer, placeHolders);
                	}
                }
            }
        }
        if (!itemExists) { String[] placeHolders = Language.newString(); placeHolders[3] = args; 
    	Language.sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); return null; }
        if (itemGiven) { return argsPlayer.getName(); }
        return "";
	}
	
	private String removeOnline(Player argsPlayer, CommandSender sender, String args, int amount) {
        boolean itemExists = false;
        boolean itemRemoved = false;
        for (ItemMap item: ItemUtilities.getItems()) {
            if (item.getConfigName().equalsIgnoreCase(args)) {
                itemExists = true;
				String[] placeHolders = Language.newString(); 
				placeHolders[3] = Utils.translateLayout(item.getCustomName(), argsPlayer); placeHolders[1] = sender.getName(); 
				if (amount == 0) { placeHolders[11] = "\u221e"; } else { placeHolders[11] = amount + ""; }
                if (item.hasItem(argsPlayer)) {
                    item.removeFrom(argsPlayer, amount);
					Language.sendLangMessage("Commands.Remove.fromYou", argsPlayer, placeHolders);
					itemRemoved = true;
                } else {
					Language.sendLangMessage("Commands.Remove.triedRemove", argsPlayer, placeHolders);
                }
            }
        }
        if (!itemExists) { String[] placeHolders = Language.newString(); placeHolders[3] = args;
        Language.sendLangMessage("Commands.Item.invalidItem", sender, placeHolders); return null; }
        if (itemRemoved) { return argsPlayer.getName(); }
        return "";
	}
}