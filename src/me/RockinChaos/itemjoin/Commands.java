package me.RockinChaos.itemjoin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemDesigner;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Language;
import me.RockinChaos.itemjoin.utils.Updater;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;
import me.RockinChaos.itemjoin.utils.sqlite.SQLite;

public class Commands implements CommandExecutor {
	public static HashMap < String, Boolean > cmdConfirm = new HashMap < String, Boolean > ();
	
	public boolean onCommand(final CommandSender sender, Command c, String l, String[] args) {
		if (args.length == 0) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
				Language.informPlayer(sender, "&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos");
				Language.informPlayer(sender, "&aType &a&l/ItemJoin Help &afor the help menu.");
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("help") || args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("1")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
				Language.informPlayer(sender, "");
				Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.informPlayer(sender, "&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos");
				Language.informPlayer(sender, "&a&l/ItemJoin Help &7- &eThis help menu.");
				Language.informPlayer(sender, "&a&l/ItemJoin Reload &7- &eReloads the .yml files.");
				Language.informPlayer(sender, "&a&l/ItemJoin Updates &7- &eChecks for plugin updates.");
				Language.informPlayer(sender, "&a&l/ItemJoin AutoUpdate &7- &eUpdate ItemJoin to latest version.");
				Language.informPlayer(sender, "&aType &a&l/ItemJoin Help 2 &afor the next page.");
				Language.informPlayer(sender, "&a&l&m]----------------&a&l[&e Help Menu 1/8 &a&l]&a&l&m---------------[");
				Language.informPlayer(sender, "");
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("2")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
				Language.informPlayer(sender, "");
				Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.informPlayer(sender, "&a&l/ItemJoin Save <Name> &7- &eSave the held item to the config.");
				Language.informPlayer(sender, "&a&l/ItemJoin List &7- &eCheck items you can get each what worlds.");
				Language.informPlayer(sender, "&a&l/ItemJoin World &7- &eCheck what world you are in, debugging.");
				Language.informPlayer(sender, "&a&l/ItemJoin Permissions &7- &eLists the permissions you have.");
				Language.informPlayer(sender, "&a&l/ItemJoin Permissions 2 &7- &ePermissions page 2.");
				Language.informPlayer(sender, "&aType &a&l/ItemJoin Help 3 &afor the next page.");
				Language.informPlayer(sender, "&a&l&m]----------------&a&l[&e Help Menu 2/8 &a&l]&a&l&m---------------[");
				Language.informPlayer(sender, "");
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("3")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
				Language.informPlayer(sender, "");
				Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.informPlayer(sender, "&a&l/ItemJoin Info &7- &eGets data-info of the held item.");
				Language.informPlayer(sender, "&a&l/ItemJoin Get <Item> &7- &eGives that ItemJoin item.");
				Language.informPlayer(sender, "&a&l/ItemJoin Get <Item> <Qty> &7- &eGives amount of said item.");
				Language.informPlayer(sender, "&a&l/ItemJoin Get <Item> <User> &7- &eGives to said player.");
				Language.informPlayer(sender, "&a&l/ItemJoin Get <Item> <User> <Qty> &7- &eGives Qty to player.");
				Language.informPlayer(sender, "&aType &a&l/ItemJoin Help 4 &afor the next page.");
				Language.informPlayer(sender, "&a&l&m]----------------&a&l[&e Help Menu 3/8 &a&l]&a&l&m---------------[");
				Language.informPlayer(sender, "");
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("4")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
				Language.informPlayer(sender, "");
				Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.informPlayer(sender, "&a&l/ItemJoin GetOnline <Item> &7- &eGives to all online.");
				Language.informPlayer(sender, "&a&l/ItemJoin GetOnline <Item> <Qty> &7- &eGives Qty to all online.");
				Language.informPlayer(sender, "&a&l/ItemJoin Remove <Item> &7- &eRemoves item from inventory.");
				Language.informPlayer(sender, "&a&l/ItemJoin Remove <Item> <User> &7- &eRemoves from player.");
				Language.informPlayer(sender, "&a&l/ItemJoin RemoveOnline <Item> &7- &eRemoves from all online.");
				Language.informPlayer(sender, "&aType &a&l/ItemJoin Help 5 &afor the next page.");
				Language.informPlayer(sender, "&a&l&m]----------------&a&l[&e Help Menu 4/8 &a&l]&a&l&m---------------[");
				Language.informPlayer(sender, "");
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("5")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
				Language.informPlayer(sender, "");
				Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.informPlayer(sender, "&a&l/ItemJoin GetAll &7- &eGives all ItemJoin items.");
				Language.informPlayer(sender, "&a&l/ItemJoin GetAll <User> &7- &eGives all items to said player.");
				Language.informPlayer(sender, "&a&l/ItemJoin RemoveAll &7- &eRemoves all ItemJoin items.");
				Language.informPlayer(sender, "&a&l/ItemJoin RemoveAll <User> &7- &eRemoves from player.");
				Language.informPlayer(sender, "&aType &a&l/ItemJoin Help 6 &afor the next page.");
				Language.informPlayer(sender, "&a&l&m]----------------&a&l[&e Help Menu 5/8 &a&l]&a&l&m---------------[");
				Language.informPlayer(sender, "");
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("6")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
				Language.informPlayer(sender, "");
				Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.informPlayer(sender, "&a&l/ItemJoin Enable &7- &eEnables ItemJoin for all players.");
				Language.informPlayer(sender, "&a&l/ItemJoin Enable <User> &7- &eEnables ItemJoin for player.");
				Language.informPlayer(sender, "&a&l/ItemJoin Enable <User> <World> &7- &eFor player/world.");
				Language.informPlayer(sender, "&aType &a&l/ItemJoin Help 7 &afor the next page.");
				Language.informPlayer(sender, "&a&l&m]----------------&a&l[&e Help Menu 6/8 &a&l]&a&l&m---------------[");
				Language.informPlayer(sender, "");
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("7")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
				Language.informPlayer(sender, "");
				Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.informPlayer(sender, "&a&l/ItemJoin Disable &7- &eDisables ItemJoin for all players.");
				Language.informPlayer(sender, "&a&l/ItemJoin Disable <User> &7- &eDisables ItemJoin for player.");
				Language.informPlayer(sender, "&a&l/ItemJoin Disable <User> <World> &7- &eFor player/world.");
				Language.informPlayer(sender, "&aType &a&l/ItemJoin Help 8 &afor the next page.");
				Language.informPlayer(sender, "&a&l&m]----------------&a&l[&e Help Menu 7/8 &a&l]&a&l&m---------------[");
				Language.informPlayer(sender, "");
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("8")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
				Language.informPlayer(sender, "");
				Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				Language.informPlayer(sender, "&c&l[DANGER]&eThe Following Destroys Data &nPermanently!&e&c&l[DANGER]");
				Language.informPlayer(sender, "&a&l/ItemJoin Purge &7- &eDeletes the database file!");
				Language.informPlayer(sender, "&a&l/ItemJoin Purge first-join <User> &7- &eFirst-Join data.");
				Language.informPlayer(sender, "&a&l/ItemJoin Purge ip-limits <User> &7- &eIp-Limits data.");
				Language.informPlayer(sender, "&aFound a bug? Report it @");
				Language.informPlayer(sender, "&ahttps://github.com/RockinChaos/ItemJoin/issues");
				Language.informPlayer(sender, "&a&l&m]----------------&a&l[&e Help Menu 8/8 &a&l]&a&l&m---------------[");
				Language.informPlayer(sender, "");
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.reload")) {
				ConfigHandler.loadConfigs();
				ItemUtilities.clearItems();
				ItemDesigner itemDesigner = new ItemDesigner();
				itemDesigner.generateItems();
				ItemUtilities.updateItems();
				Language.sendMessage(sender, "reloadedConfigs", "");
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("info")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					if (PlayerHandler.getHandItem((Player) sender) != null && PlayerHandler.getHandItem((Player) sender).getType() != Material.AIR) {
						Language.informPlayer(sender, " ");
						Language.informPlayer(sender, "&a&l&m]-----------------&a&l[&e Item Info &a&l]&a&l&m----------------[");
						Language.informPlayer(sender, "");
						Language.sendMessage(sender, "itemInfo", PlayerHandler.getHandItem((Player) sender).getType().toString());
						Language.informPlayer(sender, "");
						Language.informPlayer(sender, "&a&l&m]---------------&a&l[&e Item Info Menu &a&l]&a&l&m--------------[");
						Language.informPlayer(sender, " ");
					} else { Language.sendMessage(sender, "noItemInHand", ""); }
				} else if (sender instanceof ConsoleCommandSender) { Language.sendMessage(sender, "notPlayer", ""); }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("purge")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.reload")) {
				if (cmdConfirm.get(1 + sender.getName()) != null && cmdConfirm.get(1 + sender.getName()).equals(true)) {
					SQLite.purgeDatabase("database");
					Language.sendMessage(sender, "databasePurged", "ALL data");
					cmdConfirm.remove(1 + sender.getName());
				} else {
					cmdConfirm.put(1 + sender.getName(), true);
					Language.sendMessage(sender, "databasePurgeWarn", "main010Warn");
					Language.sendMessage(sender, "databasePurgeConfirm", "/ij purge");
					new BukkitRunnable() {
						public void run() {
							if (cmdConfirm.get(1 + sender.getName()) != null && cmdConfirm.get(1 + sender.getName()).equals(true)) {
								Language.sendMessage(sender, "databasePurgeTimeOut", "");
								cmdConfirm.remove(1 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				}
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length >= 3 && args[0].equalsIgnoreCase("purge") && args[1].equalsIgnoreCase("ip-limits") || args.length >= 3 && args[0].equalsIgnoreCase("purge") && args[1].equalsIgnoreCase("first-join")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.purge")) {
				OfflinePlayer player = PlayerHandler.getOfflinePlayer(args[2]);
				if (player == null) {
					Language.sendMessage(sender, "playerNotFound", args[2]);
				} else if (cmdConfirm.get(2 + sender.getName()) != null && cmdConfirm.get(2 + sender.getName()).equals(true) && args[1].equalsIgnoreCase("ip-limits")) {
					SQLData.purgeDatabaseData("ip_limits", player);
					Language.sendMessage(sender, "databasePurged", "ip-limits data for " + args[2]);
					cmdConfirm.remove(2 + sender.getName());
				} else if (cmdConfirm.get(3 + sender.getName()) != null && cmdConfirm.get(3 + sender.getName()).equals(true) && args[1].equalsIgnoreCase("first-join")) {
					SQLData.purgeDatabaseData("first_join", player);
					Language.sendMessage(sender, "databasePurged", "first-join data for " + args[2]);
					cmdConfirm.remove(3 + sender.getName());
				} else if (cmdConfirm.get(2 + sender.getName()) == null && args[1].equalsIgnoreCase("ip-limits")) {
					cmdConfirm.put(2 + sender.getName(), true);
					Language.sendMessage(sender, "databasePurgeWarn", "ip-limits data for " + args[2]);
					Language.sendMessage(sender, "databasePurgeConfirm", "/ij purge ip-limits <player>");
					new BukkitRunnable() {
						public void run() {
							if (cmdConfirm.get(2 + sender.getName()) != null && cmdConfirm.get(2 + sender.getName()).equals(true)) {
								Language.sendMessage(sender, "databasePurgeTimeOut", "");
								cmdConfirm.remove(2 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				} else if (cmdConfirm.get(3 + sender.getName()) == null && args[1].equalsIgnoreCase("first-join")) {
					cmdConfirm.put(3 + sender.getName(), true);
					Language.sendMessage(sender, "databasePurgeWarn", "first-join data for " + args[2]);
					Language.sendMessage(sender, "databasePurgeConfirm", "/ij purge first-join <player>");
					new BukkitRunnable() {
						public void run() {
							if (cmdConfirm.get(3 + sender.getName()) != null && cmdConfirm.get(3 + sender.getName()).equals(true)) {
								Language.sendMessage(sender, "databasePurgeTimeOut", "");
								cmdConfirm.remove(3 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				}
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("menu") || args[0].equalsIgnoreCase("creator")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.creator")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					//ItemCreator.LaunchCreator(sender); // Not an implemented method..
					Language.sendMessage(sender, "creatorLaunched", "");
				} else if (sender instanceof ConsoleCommandSender) { Language.sendMessage(sender, "notPlayer", ""); }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("enable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable.others")) { Language.sendMessage(sender, "playerNotFound", args[1]); } 
			else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable.others")) {
				if (SQLData.isWritable("Global", PlayerHandler.getPlayerID(argsPlayer))) { Language.sendMessage(argsPlayer, "enabledForPlayerFailed", ""); } 
				else {
					SQLData.saveToDatabase(argsPlayer, "true", "enabled-players", "Global");
					Language.sendMessage(argsPlayer, "enabledForPlayer", "");
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						Language.setArgsPlayer(argsPlayer);
						Language.sendMessage(sender, "enabledForOther", "");
					}
				}
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("enable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			String world = args[2];
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable.others")) { Language.sendMessage(sender, "playerNotFound", args[1]); } 
			else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable.others")) {
				if (SQLData.isWritable(world, PlayerHandler.getPlayerID(argsPlayer))) { Language.sendMessage(argsPlayer, "enabledForPlayerWorldFailed", world); }
				else {
					SQLData.saveToDatabase(argsPlayer, "true", "enabled-players", world);
					Language.sendMessage(argsPlayer, "enabledForPlayerWorld", world);
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						Language.setArgsPlayer(argsPlayer);
						Language.sendMessage(sender, "enabledForOtherWorld", world);
					}
				}
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("enable")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable")) {
				if (!SQLData.isWritable("Global", "ALL")) {
					SQLData.saveToDatabase(null, "true", "enabled-players", "Global");
					Language.sendMessage(sender, "enabledGlobal", "");
				} else { Language.sendMessage(sender, "enabledGlobalFailed", ""); }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("disable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable.others")) {
				Language.sendMessage(sender, "playerNotFound", args[1]);
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable.others")) {
				if (!SQLData.isWritable("Global", PlayerHandler.getPlayerID(argsPlayer))) { Language.sendMessage(argsPlayer, "disabledForPlayerFailed", ""); }
				else {
					SQLData.saveToDatabase(argsPlayer, "false", "disabled-players", "Global");
					Language.sendMessage(argsPlayer, "disabledForPlayer", "");
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						Language.setArgsPlayer(argsPlayer);
						Language.sendMessage(sender, "disabledForOther", "");
					}
				}
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("disable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			String world = args[2];
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable.others")) {
				Language.sendMessage(sender, "playerNotFound", args[1]);
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable.others")) {
				if (!SQLData.isWritable(world, PlayerHandler.getPlayerID(argsPlayer))) {
					Language.sendMessage(argsPlayer, "disabledForPlayerWorldFailed", world);
				} else {
					SQLData.saveToDatabase(argsPlayer, "false", "disabled-players", world);
					Language.sendMessage(argsPlayer, "disabledForPlayerWorld", world);
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						Language.setArgsPlayer(argsPlayer);
						Language.sendMessage(sender, "enabledForOtherWorld", world);
					}
				}
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("disable")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable")) {
				if (SQLData.isWritable("Global", "ALL")) {
					SQLData.saveToDatabase(null, "false", "disabled-players", "Global");
					Language.sendMessage(sender, "disabledGlobal", "");
				} else { Language.sendMessage(sender, "disabledGlobalFailed", ""); }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("save")) {
			if (args.length == 2) {
				if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.save")) {
					if (!(sender instanceof ConsoleCommandSender)) {
						Player player = (Player) sender;
						ItemStack item = new ItemStack(PlayerHandler.getPerfectHandItem(player, "HAND"));
						if (item != null && item.getType() != Material.AIR) {
							String world = player.getWorld().getName();
							String type = item.getType().toString();
							File itemsFile = new File(ItemJoin.getInstance().getDataFolder(), "items.yml");
							FileConfiguration itemData = YamlConfiguration.loadConfiguration(itemsFile);
							itemData.set("items." + args[1] + "." + "id", type);
							itemData.set("items." + args[1] + "." + "slot", 0);
							if (item.getAmount() > 1) {
								itemData.set("items." + args[1] + "." + "count", item.getAmount());
							}
							if (item.getType().getMaxDurability() < 30 && ItemHandler.getDurability(item) > 0) {
								itemData.set("items." + args[1] + "." + "data-value", ItemHandler.getDurability(item));
							}
							if (item.getType().getMaxDurability() > 30 && ItemHandler.getDurability(item) != 0 && ItemHandler.getDurability(item) != ((short) item.getType().getMaxDurability())) {
								itemData.set("items." + args[1] + "." + "durability", ItemHandler.getDurability(item));
							}		
							if (item.hasItemMeta()) {
								if (item.getItemMeta().hasDisplayName()) {
									String name = item.getItemMeta().getDisplayName();
									for (int i = 0; i <= 36; i++) {
										name = name.replace(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData(null) + i), "");
										name = name.replace(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData(null) + "Arbitrary" + i), "");
									}
									itemData.set("items." + args[1] + "." + "name", name.replace("§", "&"));
								}
								if (item.getItemMeta().hasLore()) {
									List < String > oldLore = item.getItemMeta().getLore();
									List < String > newLore = new ArrayList < String > ();
									for (String stepLore: oldLore) {
										newLore.add(stepLore.replace("§", "&"));
									}
									itemData.set("items." + args[1] + "." + "lore", newLore);
								}
								if (item.getItemMeta().hasEnchants()) {
									List < String > enchantList = new ArrayList < String > ();
									for (Enchantment e: item.getItemMeta().getEnchants().keySet()) {
										int level = item.getItemMeta().getEnchants().get(e);
										enchantList.add(ItemHandler.getEnchantName(e).toUpperCase() + ":" + level);
									}
									itemData.set("items." + args[1] + "." + "enchantment", Utils.convertStringList(enchantList));
								}
							}
							itemData.set("items." + args[1] + "." + "itemflags", "death-drops");
							itemData.set("items." + args[1] + "." + "triggers", "join");
							itemData.set("items." + args[1] + "." + "enabled-worlds", world);
							try { 
								itemData.save(itemsFile);
							} catch (IOException e) {
								ItemJoin.getInstance().getServer().getLogger().severe("Could not save " + args[1] + " to the items.yml!");
								ServerHandler.sendDebugTrace(e);
							}
							Language.sendMessage(sender, "playerSavedItem", args[1]);
						} else { Language.sendMessage(sender, "playerFailedSavedItem", args[1]); }
					} else if (sender instanceof ConsoleCommandSender) { Language.sendMessage(sender, "notPlayer", ""); }
				} else { Language.sendMessage(sender, "noPermission", ""); }
			} else { Language.sendMessage(sender, "playerInvalidSavedItem", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					Language.informPlayer(sender, "");
					Language.sendMessage(sender, "inWorldListHeader", "");
					Language.sendMessage(sender, "inWorldListed", ((Player) sender).getWorld().getName());
					Language.informPlayer(sender, "");
					Language.informPlayer(sender, "&a&l&m]--------------&a&l[&e Worlds In Menu 1/1 &a&l]&a&l&m-------------[");
				} else if (sender instanceof ConsoleCommandSender) { Language.sendMessage(sender, "notPlayer", ""); }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("list")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.list")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					boolean ItemExists = false;
					Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
						ItemExists = false;
						Language.sendMessage(sender, "listWorldsHeader", world.getName());
						List <String> inputListed = new ArrayList<String>();
						for (ItemMap item: ItemUtilities.getItems()) {
							if (!inputListed.contains(item.getConfigName()) && item.getTempItem() != null && item.inWorld(world)) {
								inputListed.add(item.getConfigName());
								Language.sendMessage(sender, "listItems", item.getConfigName());
								ItemExists = true;
							}
						}
						if (ItemExists == false) { Language.sendMessage(sender, "noItemsListed", ""); }
					}
					Language.informPlayer(sender, "&a&l&m]----------------&a&l[&e List Menu 1/1 &a&l]&a&l&m---------------[");
				} else if (sender instanceof ConsoleCommandSender) { Language.sendMessage(sender, "notPlayer", ""); }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("permissions")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.permissions")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) { Language.informPlayer(sender, "&a[\u2714] ItemJoin.*"); } 
					else { Language.informPlayer(sender, "&c[\u2718] ItemJoin.*"); }
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.all")) { Language.informPlayer(sender, "&a[\u2714] ItemJoin.All"); } 
					else { Language.informPlayer(sender, "&c[\u2718] ItemJoin.All"); }
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) { Language.informPlayer(sender, "&a[\u2714] ItemJoin.Use"); } 
					else { Language.informPlayer(sender, "&c[\u2718] ItemJoin.Use"); }
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.reload")) { Language.informPlayer(sender, "&a[\u2714] ItemJoin.Reload"); } 
					else { Language.informPlayer(sender, "&c[\u2718] ItemJoin.Reload"); }
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.updates")) { Language.informPlayer(sender, "&a[\u2714] ItemJoin.Updates"); }
					else { Language.informPlayer(sender, "&c[\u2718] ItemJoin.Updates"); }
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get")) { Language.informPlayer(sender, "&a[\u2714] ItemJoin.get"); } 
					else { Language.informPlayer(sender, "&c[\u2718] ItemJoin.get"); }
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others")) { Language.informPlayer(sender, "&a[\u2714] ItemJoin.get.others"); }
					else { Language.informPlayer(sender, "&c[\u2718] ItemJoin.get.others"); }
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.permissions")) { Language.informPlayer(sender, "&a[\u2714] ItemJoin.permissions"); } 
					else { Language.informPlayer(sender, "&c[\u2718] ItemJoin.permissions"); }
					for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
						if (PermissionsHandler.hasCommandPermission(sender, "itemjoin." + world.getName() + ".*")) { Language.informPlayer(sender, "&a[\u2714] ItemJoin." + world.getName() + ".*"); } 
						else { Language.informPlayer(sender, "&c[\u2718] ItemJoin." + world.getName() + ".*"); }
					}
					Language.informPlayer(sender, "&aType &a&l/ItemJoin Permissions 2 &afor the next page.");
					Language.informPlayer(sender, "&a&l&m]------------&a&l[&e Permissions Menu 1/2 &a&l]&a&l&m------------[");
				} else if (sender instanceof ConsoleCommandSender) { Language.sendMessage(sender, "notPlayer", ""); }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("permissions") && args[1].equalsIgnoreCase("2")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.permissions")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					Language.informPlayer(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					List<String> customPermissions = new ArrayList<String>();
					for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
						List <String> inputListed = new ArrayList<String>();
						String Probable = ItemUtilities.getProbabilityItem(((Player) sender));
						for (ItemMap item: ItemUtilities.getItems()) {
							if (!customPermissions.contains(item.getPermissionNode()) && !inputListed.contains(item.getConfigName()) && item.inWorld(world) && ItemUtilities.isChosenProbability(item, Probable)) {
								if (item.getPermissionNode() != null && !customPermissions.contains(item.getPermissionNode()) || item.getPermissionNode() == null) {
									if (item.getPermissionNode() != null) { customPermissions.add(item.getPermissionNode()); }
									inputListed.add(item.getConfigName());
									if (item.hasPermission(((Player) sender))) { Language.informPlayer(sender, "&a[\u2714] " + PermissionsHandler.customPermissions(item.getPermissionNode(), item.getConfigName(), world.getName())); } 
									else { Language.informPlayer(sender, "&c[\u2718] " + PermissionsHandler.customPermissions(item.getPermissionNode(), item.getConfigName(), world.getName())); }
								}
							}
						}
					}
					Language.informPlayer(sender, "&a&l&m]------------&a&l[&e Permissions Menu 2/2 &a&l]&a&l&m------------[");
				} else if (sender instanceof ConsoleCommandSender) { Language.sendMessage(sender, "notPlayer", ""); }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("getOnline") || args.length == 3 && args[0].equalsIgnoreCase("getOnline") && Utils.isInt(args[2])) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others")) {
				int amount = 0;
				if (args.length == 3) { amount = Integer.parseInt(args[2]); }
			    Collection < ? > playersOnline = null;
			    Player[] playersOnlineOld = null;
			    try {
			        if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
			            if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
			                playersOnline = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
			                for (Object objPlayer: playersOnline) {
			                    if (!getOnline(((Player) objPlayer), sender, args[1], amount)) { break; }
			                }
			            }
			        } else {
			            playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
			            for (Player objPlayer: playersOnlineOld) {
		                    if (!getOnline(objPlayer, sender, args[1], amount)) { break; }
			            }
			        }
			    } catch (Exception e) { ServerHandler.sendDebugTrace(e);  }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("getOnline")) {
				if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others")) { Language.sendMessage(sender, "invalidGetOnlineUsage", ""); } 
				else { Language.sendMessage(sender, "noPermission", ""); }
				return true;
		} else if (args.length == 4 && args[0].equalsIgnoreCase("get") && Utils.isInt(args[3]) || args.length == 3 && args[0].equalsIgnoreCase("get") && !Utils.isInt(args[2])) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others")) {
				Player argsPlayer = PlayerHandler.getPlayerString(args[2]);
				int amount = 0;
				if (args.length == 4) { amount = Integer.parseInt(args[3]); } 
				if (argsPlayer == null) { Language.sendMessage(sender, "playerNotFound", args[2]); return true; }
				boolean itemGiven = false;
				Language.setArgsPlayer(sender);
				String Probable = ItemUtilities.getProbabilityItem(argsPlayer);
				for (ItemMap item: ItemUtilities.getItems()) {
					if (item.inWorld(argsPlayer.getWorld()) && item.getConfigName().equalsIgnoreCase(args[1]) && ItemUtilities.isChosenProbability(item, Probable)) {
						if (!item.hasItem(argsPlayer) || amount != 0 || item.isAlwaysGive()) {
							if (!(ConfigHandler.getAllItemPermissions()) || item.hasPermission(argsPlayer) && ConfigHandler.getAllItemPermissions()) {
								if (item.isAlwaysGive() && args.length != 4) { amount = item.getCount(); }
								item.giveTo(argsPlayer, true, amount);
								Language.sendMessage(argsPlayer, "givenToYou", Utils.translateLayout(item.getCustomName(), argsPlayer));
								Language.setArgsPlayer(argsPlayer);
								Language.sendMessage(sender, "givenToPlayer", Utils.translateLayout(item.getCustomName(), argsPlayer));
							} else { 
								Language.setArgsPlayer(argsPlayer);
								Language.sendMessage(sender, "givenToPlayerNoPerms", Utils.translateLayout(item.getCustomName(), argsPlayer)); 
								}
						} else { 
							Language.sendMessage(argsPlayer, "playerTriedGive", Utils.translateLayout(item.getCustomName(), argsPlayer)); 
							Language.setArgsPlayer(argsPlayer);
							Language.sendMessage(sender, "itemExistsInOthersInventory", Utils.translateLayout(item.getCustomName(), argsPlayer));
						}
						itemGiven = true;
					}
				}
				if (!itemGiven) { Language.sendMessage(sender, "itemDoesntExist", args[1]); }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("get") && Utils.isInt(args[2]) || args.length == 2 && args[0].equalsIgnoreCase("get")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get")) {
				int amount = 0;
				if (args.length == 3) { amount = Integer.parseInt(args[2]); }
				if (!(sender instanceof ConsoleCommandSender)) {
					boolean itemGiven = false;
					Language.setArgsPlayer(null);
					String Probable = ItemUtilities.getProbabilityItem(((Player) sender));
					for (ItemMap item: ItemUtilities.getItems()) {
						if (item.inWorld(((Player) sender).getWorld()) && item.getConfigName().equalsIgnoreCase(args[1]) && ItemUtilities.isChosenProbability(item, Probable)) {
							if (!item.hasItem(((Player) sender)) || amount != 0 || item.isAlwaysGive()) {
								if (!(ConfigHandler.getAllItemPermissions()) || item.hasPermission(((Player) sender)) && ConfigHandler.getAllItemPermissions()) {
									if (item.isAlwaysGive() && args.length != 4) { amount = item.getCount(); }
									item.giveTo(((Player) sender), true, amount);
									Language.sendMessage(((Player) sender), "givenToYou", Utils.translateLayout(item.getCustomName(), ((Player) sender)));
								} else { Language.sendMessage(((Player) sender), "givenNoPerms", Utils.translateLayout(item.getCustomName(), ((Player) sender))); }
							} else { Language.sendMessage(((Player) sender), "itemExistsInInventory", Utils.translateLayout(item.getCustomName(), ((Player) sender))); }
							itemGiven = true;
						}
					}
					if (!itemGiven) { Language.sendMessage(((Player) sender), "itemDoesntExist", args[1]); }
				} else if (sender instanceof ConsoleCommandSender) {
					Language.sendMessage(sender, "notPlayer", "");
					Language.sendMessage(sender, "correctGetSyntax", "");
				}
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("get")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get")) { Language.sendMessage(sender, "invalidGetUsage", ""); } 
			else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("getall")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others")) {
				Language.sendMessage(sender, "playerNotFound", args[1]);
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others")) {
				boolean itemGiven = false;
				boolean itemPermission = false;
				Language.setArgsPlayer(sender);
				String Probable = ItemUtilities.getProbabilityItem(argsPlayer);
				for (ItemMap item: ItemUtilities.getItems()) {
					if (item.inWorld(argsPlayer.getWorld()) && ItemUtilities.isChosenProbability(item, Probable)) {
						if (!(ConfigHandler.getAllItemPermissions()) || item.hasPermission(argsPlayer) && ConfigHandler.getAllItemPermissions()) {
							if (!item.hasItem(argsPlayer) || item.isAlwaysGive()) {
								item.giveTo(argsPlayer, !item.isAlwaysGive(), 0);
								itemGiven = true;
							}
						} else { itemPermission = true; }
					}
				}
				if (itemGiven) {
					Language.sendMessage(argsPlayer, "givenAllToYou", "All Items");
					Language.setArgsPlayer(argsPlayer);
					Language.sendMessage(sender, "givenAllToPlayer", "All Items");
					Language.setArgsPlayer(sender);
				} else {
					Language.sendMessage(argsPlayer, "playerTriedGiveAllItems", "All Items");
					Language.setArgsPlayer(argsPlayer);
					Language.sendMessage(sender, "allItemsExistInOthersInventory", "All Items");
					Language.setArgsPlayer(sender);
				}
				if (itemPermission) {
					Language.setArgsPlayer(argsPlayer);
					Language.sendMessage(sender, "givenAllPlayerNoPerms", "All Items");
				}
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("getall")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					boolean itemGiven = false;
					boolean itemPermission = false;
					Language.setArgsPlayer(null);
					String Probable = ItemUtilities.getProbabilityItem(((Player) sender));
					for (ItemMap item: ItemUtilities.getItems()) {
						if (item.inWorld(((Player) sender).getWorld()) && ItemUtilities.isChosenProbability(item, Probable)) {
							if (!(ConfigHandler.getAllItemPermissions()) || item.hasPermission(((Player) sender)) && ConfigHandler.getAllItemPermissions()) {
								if (!item.hasItem(((Player) sender)) || item.isAlwaysGive()) {
									item.giveTo(((Player) sender), !item.isAlwaysGive(), 0);
									itemGiven = true;
								}
							} else { itemPermission = true; } 
						}
					}
					if (itemGiven) { Language.sendMessage(((Player) sender), "givenAllToYou", "All Items"); } 
					else { Language.sendMessage(((Player) sender), "allItemsExistInInventory", "All Items");  }
					if (itemPermission) { Language.sendMessage(((Player) sender), "givenAllNoPerms", "All Items"); }
				} else if (sender instanceof ConsoleCommandSender) {
					Language.sendMessage(sender, "notPlayer", "");
					Language.sendMessage(sender, "correctGetSyntax", "");
				}
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("removeOnline")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others")) {
			    Collection < ? > playersOnline = null;
			    Player[] playersOnlineOld = null;
			    try {
			        if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
			            if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
			                playersOnline = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
			                for (Object objPlayer: playersOnline) {
			                    if (!removeOnline(((Player) objPlayer), sender, args[1])) { break; }
			                }
			            }
			        } else {
			            playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
			            for (Player objPlayer: playersOnlineOld) {
		                    if (!removeOnline(objPlayer, sender, args[1])) { break; }
			            }
			        }
			    } catch (Exception e) { ServerHandler.sendDebugTrace(e);  }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("removeOnline")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others")) { Language.sendMessage(sender, "invalidRemoveOnlineSyntax", ""); } 
			else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[2]);
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others")) {
				Language.sendMessage(sender, "playerNotFound", args[2]);
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others")) {
				boolean itemRemoved = false;
				Language.setArgsPlayer(sender);
				for (ItemMap item: ItemUtilities.getItems()) {
					if (item.getConfigName().equalsIgnoreCase(args[1])) {
						if (item.hasItem(argsPlayer)) {
							item.removeFrom(argsPlayer);
							Language.sendMessage(argsPlayer, "removedFromYou", Utils.translateLayout(item.getCustomName(), argsPlayer));
							Language.setArgsPlayer(argsPlayer);
							Language.sendMessage(sender, "removedFromPlayer", Utils.translateLayout(item.getCustomName(), argsPlayer));
						} else { 
							Language.sendMessage(argsPlayer, "playerTriedRemove", Utils.translateLayout(item.getCustomName(), argsPlayer)); 
							Language.setArgsPlayer(argsPlayer);
							Language.sendMessage(sender, "itemDoesntExistInOthersInventory", Utils.translateLayout(item.getCustomName(), argsPlayer));
						}
						itemRemoved = true;
					}
				}
				if (!itemRemoved) { Language.sendMessage(sender, "itemDoesntExist", args[1]); }
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					boolean itemRemoved = false;
					Language.setArgsPlayer(null);
					for (ItemMap item: ItemUtilities.getItems()) {
						if (item.getConfigName().equalsIgnoreCase(args[1])) {
							if (item.hasItem(((Player) sender))) {
								item.removeFrom(((Player) sender));
								Language.sendMessage(((Player) sender), "removedFromYou", Utils.translateLayout(item.getCustomName(), ((Player) sender)));
							} else { Language.sendMessage(((Player) sender), "itemDoesntExistInInventory", Utils.translateLayout(item.getCustomName(), ((Player) sender))); }
							itemRemoved = true;
						}
					}
					if (!itemRemoved) { Language.sendMessage(((Player) sender), "itemDoesntExist", args[1]); }
				} else if (sender instanceof ConsoleCommandSender) {
					Language.sendMessage(sender, "notPlayer", "");
					Language.sendMessage(sender, "correctRemoveSyntax", "");
				}
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove")) { Language.sendMessage(sender, "invalidRemoveSyntax", "");
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("removeall")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others")) {
				Language.sendMessage(sender, "playerNotFound", args[1]);
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others")) {
				boolean itemRemoved = false;
				Language.setArgsPlayer(sender);
				for (ItemMap item: ItemUtilities.getItems()) {
					if (item.hasItem(argsPlayer)) {
						item.removeFrom(argsPlayer);
						itemRemoved = true;
					}
				}
				if (itemRemoved) {
					Language.sendMessage(argsPlayer, "removedAllFromYou", "All Items");
					Language.setArgsPlayer(argsPlayer);
					Language.sendMessage(sender, "removedAllFromPlayer", "All Items");
					Language.setArgsPlayer(sender);
				} else {
					Language.sendMessage(argsPlayer, "playerTriedRemoveAll", "All Items");
					Language.setArgsPlayer(argsPlayer);
					Language.sendMessage(sender, "allItemsDoNotExistInOthersInventory", "All Items");
					Language.setArgsPlayer(sender);
				}
				
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("removeall")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove")) {
				if (sender instanceof Player) {
					Language.setArgsPlayer(null);
					boolean itemRemoved = false;
					Language.setArgsPlayer(null);
					for (ItemMap item: ItemUtilities.getItems()) {
						if (item.hasItem(((Player) sender))) {
							item.removeFrom(((Player) sender));
							itemRemoved = true;
						}
					}
					if (itemRemoved) { Language.sendMessage(((Player) sender), "removedAllFromYou", "All Items"); } 
					else { Language.sendMessage(((Player) sender), "allItemsDoNotExistInInventory", "All Items");  }
				} else if (sender instanceof ConsoleCommandSender) {
					Language.sendMessage(sender, "notPlayer", "");
					Language.sendMessage(sender, "correctRemoveSyntax", "");
				}
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("updates") || args[0].equalsIgnoreCase("update")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.updates")) {
				Language.sendMessage(sender, "updateChecking", "");
				Updater.checkUpdates(sender);
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else if (args[0].equalsIgnoreCase("AutoUpdate")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.autoupdate")) {
				Language.sendMessage(sender, "updateForced", "");
				Updater.forceUpdates(sender);
			} else { Language.sendMessage(sender, "noPermission", ""); }
			return true;
		} else {
			if (ConfigHandler.getConfig("en-lang.yml").getString("unknownCommand") != null) { Language.sendMessage(sender, "unknownCommand", ""); }
			return true;
		}
	}
	
	private boolean getOnline(Player argsPlayer, CommandSender sender, String args, int amount) {
        boolean itemGiven = false;
        Language.setArgsPlayer(sender);
        String Probable = ItemUtilities.getProbabilityItem(argsPlayer);
        for (ItemMap item: ItemUtilities.getItems()) {
            if (item.inWorld(argsPlayer.getWorld()) && item.getConfigName().equalsIgnoreCase(args) && ItemUtilities.isChosenProbability(item, Probable)) {
                if (!(ConfigHandler.getAllItemPermissions()) || item.hasPermission(argsPlayer) && ConfigHandler.getAllItemPermissions()) {
                	if (item.isAlwaysGive() && amount == 0) { amount = item.getCount(); }
                    item.giveTo(argsPlayer, true, amount);
                    Language.sendMessage(argsPlayer, "givenToYou", Utils.translateLayout(item.getCustomName(), argsPlayer));
                    Language.setArgsPlayer(argsPlayer);
                    Language.sendMessage(sender, "givenToPlayer", Utils.translateLayout(item.getCustomName(), argsPlayer));
                } else {
                    Language.setArgsPlayer(argsPlayer);
                    Language.sendMessage(sender, "givenToPlayerNoPerms", Utils.translateLayout(item.getCustomName(), argsPlayer));
                }
                itemGiven = true;
            }
        }
        if (!itemGiven) { Language.sendMessage(sender, "itemDoesntExist", args); return false; }
        return true;
	}
	
	private boolean removeOnline(Player argsPlayer, CommandSender sender, String args) {
        boolean itemRemoved = false;
        Language.setArgsPlayer(sender);
        for (ItemMap item: ItemUtilities.getItems()) {
            if (item.getConfigName().equalsIgnoreCase(args)) {
                if (item.hasItem(argsPlayer)) {
                    item.removeFrom(argsPlayer);
                    Language.sendMessage(argsPlayer, "removedFromYou", Utils.translateLayout(item.getCustomName(), argsPlayer));
                    Language.setArgsPlayer(argsPlayer);
                    Language.sendMessage(sender, "removedFromPlayer", Utils.translateLayout(item.getCustomName(), argsPlayer));
                } else {
        			Language.sendMessage(argsPlayer, "playerTriedRemove", Utils.translateLayout(item.getCustomName(), argsPlayer)); 
        			Language.setArgsPlayer(argsPlayer);
        			Language.sendMessage(sender, "itemDoesntExistInOthersInventory", Utils.translateLayout(item.getCustomName(), argsPlayer));
                }
                itemRemoved = true;
            }
        }
        if (!itemRemoved) { Language.sendMessage(sender, "itemDoesntExist", args); return false; }
        return true;
	}
}