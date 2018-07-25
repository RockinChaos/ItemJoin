package me.RockinChaos.itemjoin.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.AnimationHandler;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.listeners.giveitems.SetItems;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;
import me.RockinChaos.itemjoin.utils.sqlite.SQLite;

public class Commands implements CommandExecutor {
	private static boolean ItemExists = false;
	public static HashMap < String, Boolean > cmdConfirm = new HashMap < String, Boolean > ();
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help &afor the help menu.");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("help") || args.length == 1 && args[0].equalsIgnoreCase("h") 
				|| args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("1") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("1")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				ServerHandler.sendCommandsMessage(sender, "&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Help &7- &eThis help menu");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Reload &7- &eReloads the .yml files");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Updates &7- &eChecks for plugin updates");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin AutoUpdate &7- &eUpdate ItemJoin to latest version");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help 2 &afor the next page");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 1/7 &a&l]&a&l&m---------------[");
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("2") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("2")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Save <Name> &7- &eSave the held item to the config");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin List &7- &eCheck items you can get each what worlds");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin World &7- &eCheck what world you are in, debugging");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Permissions &7- &eLists the permissions you have");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Permissions 2 &7- &ePermissions page 2");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help 3 &afor the next page");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 2/7 &a&l]&a&l&m---------------[");
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("3") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("3")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Get <Item> &7- &eGives that ItemJoin item");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Get <Item> <Player> &7- &eGives to said player");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Remove <Item> &7- &eRemoves item from inventory");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Remove <Item> <Player> &7- &eRemoves from player");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help 4 &afor the next page");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 3/7 &a&l]&a&l&m---------------[");
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("4") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("4")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin GetAll &7- &eGives all ItemJoin items.");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin GetAll <Player> &7- &eGives all items to said player.");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin RemoveAll &7- &eRemoves all ItemJoin items.");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin RemoveAll <Player> &7- &eRemoves from player.");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help 5 &afor the next page");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 4/7 &a&l]&a&l&m---------------[");
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("5") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("5")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Enable &7- &eEnables ItemJoin for all players.");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Enable <Player> &7- &eEnables ItemJoin for player.");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Enable <Player> <World> &7- &eFor player/world.");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help 6 &afor the next page");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 5/7 &a&l]&a&l&m---------------[");
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
			} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("6") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("6")) {
				if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
					ServerHandler.sendCommandsMessage(sender, "blankmessage");
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Disable &7- &eDisables ItemJoin for all players.");
					ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Disable <Player> &7- &eDisables ItemJoin for player.");
					ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Disable <Player> <World> &7- &eFor player/world.");
					ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help 7 &afor the next page");
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 6/7 &a&l]&a&l&m---------------[");
					ServerHandler.sendCommandsMessage(sender, "blankmessage");
					return true;
				} else {
					Language.getSendMessage(sender, "noPermission", "");
					return true;
				}
			} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("7") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("7")) {
				if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
					ServerHandler.sendCommandsMessage(sender, "blankmessage");
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					ServerHandler.sendCommandsMessage(sender, "&c&l[DANGER]&eThe Following Destroys Data &nPermanently!&e&c&l[DANGER]");
					ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Purge &7- &eDeletes the database file!");
					ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Purge first-join <Player> &7- &eFirst-Join data.");
					ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Purge ip-limits <Player> &7- &eIp-Limits data.");
					ServerHandler.sendCommandsMessage(sender, "&aFound a bug? Report it @");
					ServerHandler.sendCommandsMessage(sender, "&ahttps://github.com/RockinChaos/ItemJoin/issues");
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 7/7 &a&l]&a&l&m---------------[");
					ServerHandler.sendCommandsMessage(sender, "blankmessage");
					return true;
				} else {
					Language.getSendMessage(sender, "noPermission", "");
					return true;
				}
		} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.reload") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				CreateItems.items.clear();
				ConfigHandler.loadConfigs();
				CreateItems.setRun();
				Language.getSendMessage(sender, "reloadedConfigs", "");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("purge")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.reload") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (cmdConfirm.get(1 + sender.getName()) != null && cmdConfirm.get(1 + sender.getName()).equals(true)) {
					SQLite.purgeDatabase("database");
				    Language.getSendMessage(sender, "databasePurged", "ALL data");
				    cmdConfirm.remove(1 + sender.getName());
				} else {
					cmdConfirm.put(1 + sender.getName(), true);
					Language.getSendMessage(sender, "databasePurgeWarn", "main010Warn");
					Language.getSendMessage(sender, "databasePurgeConfirm", "/ij purge");
					new BukkitRunnable() {
						public void run() {
							if (cmdConfirm.get(1 + sender.getName()) != null && cmdConfirm.get(1 + sender.getName()).equals(true)) {
								Language.getSendMessage(sender, "databasePurgeTimeOut", "");
								cmdConfirm.remove(1 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				}
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length >= 3 && args[0].equalsIgnoreCase("purge") && args[1].equalsIgnoreCase("ip-limits") 
				|| args.length >= 3 && args[0].equalsIgnoreCase("purge") && args[1].equalsIgnoreCase("first-join")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.purge") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				OfflinePlayer player = PlayerHandler.getOfflinePlayer(args[2]);
				if (player == null) {
					Language.getSendMessage(sender, "playerNotFound", args[2]);
					return true;
				}

				if (cmdConfirm.get(2 + sender.getName()) != null && cmdConfirm.get(2 + sender.getName()).equals(true) && args[1].equalsIgnoreCase("ip-limits")) {
					SQLData.purgeDatabaseData("ip_limits", player);
				    Language.getSendMessage(sender, "databasePurged", "ip-limits data for " + args[2]);
				    cmdConfirm.remove(2 + sender.getName());
				} else if (cmdConfirm.get(3 + sender.getName()) != null && cmdConfirm.get(3 + sender.getName()).equals(true) && args[1].equalsIgnoreCase("first-join")) {
					SQLData.purgeDatabaseData("first_join", player);
				    Language.getSendMessage(sender, "databasePurged", "first-join data for " + args[2]);
				    cmdConfirm.remove(3 + sender.getName());
				} else if (cmdConfirm.get(2 + sender.getName()) == null && args[1].equalsIgnoreCase("ip-limits")) {
					cmdConfirm.put(2 + sender.getName(), true);
					Language.getSendMessage(sender, "databasePurgeWarn", "ip-limits data for " + args[2]);
					Language.getSendMessage(sender, "databasePurgeConfirm", "/ij purge ip-limits <player>");
					new BukkitRunnable() {
						public void run() {
							if (cmdConfirm.get(2 + sender.getName()) != null && cmdConfirm.get(2 + sender.getName()).equals(true)) {
								Language.getSendMessage(sender, "databasePurgeTimeOut", "");
								cmdConfirm.remove(2 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				} else if (cmdConfirm.get(3 + sender.getName()) == null && args[1].equalsIgnoreCase("first-join")) {
					cmdConfirm.put(3 + sender.getName(), true);
					Language.getSendMessage(sender, "databasePurgeWarn", "first-join data for " + args[2]);
					Language.getSendMessage(sender, "databasePurgeConfirm", "/ij purge first-join <player>");
					new BukkitRunnable() {
						public void run() {
							if (cmdConfirm.get(3 + sender.getName()) != null && cmdConfirm.get(3 + sender.getName()).equals(true)) {
								Language.getSendMessage(sender, "databasePurgeTimeOut", "");
								cmdConfirm.remove(3 + sender.getName());
							}
						}
					}.runTaskLater(ItemJoin.getInstance(), 100L);
				}
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("menu") || args[0].equalsIgnoreCase("creator")) {
			if (sender.getName() != null && sender.getName().equalsIgnoreCase("RockinChaos")) { // PermissionsHandler.hasCommandPermission(sender, "itemjoin.creator") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")
				if (!(sender instanceof ConsoleCommandSender)) {
					ItemCreator.LaunchCreator(sender);
					Language.getSendMessage(sender, "creatorLaunched", "");
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					Language.getSendMessage(sender, "notPlayer", "");
					return true;
				}
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("enable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable.others") || argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[1]);
				return true;
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable.others") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (SQLData.isWritable("Global", PlayerHandler.getPlayerID(argsPlayer))) {
					Language.getSendMessage(argsPlayer, "enabledForPlayerFailed", "");
				} else {
				SQLData.saveToDatabase(argsPlayer, "true", "enabled-players", "Global");
				Language.getSendMessage(argsPlayer, "enabledForPlayer", "");
				if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
					Language.setArgsPlayer(argsPlayer);
					Language.getSendMessage(sender, "enabledForOther", "");
				}
				}
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 3 && args[0].equalsIgnoreCase("enable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			String world = args[2];
			if (world.equalsIgnoreCase("Global")) { world = "Global"; }
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable.others") || argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[1]);
				return true;
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable.others") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (SQLData.isWritable(world, PlayerHandler.getPlayerID(argsPlayer))) {
					Language.getSendMessage(argsPlayer, "enabledForPlayerWorldFailed", world);
				} else {
				SQLData.saveToDatabase(argsPlayer, "true", "enabled-players", world);
				Language.getSendMessage(argsPlayer, "enabledForPlayerWorld", world);
				if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
					Language.setArgsPlayer(argsPlayer);
					Language.getSendMessage(sender, "enabledForOtherWorld", world);
				}
				}
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("enable")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (!SQLData.isWritable("Global", "ALL")) {
					SQLData.saveToDatabase(null, "true", "enabled-players", "Global");
					Language.getSendMessage(sender, "enabledGlobal", "");
				} else {
					Language.getSendMessage(sender, "enabledGlobalFailed", "");
				}
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("disable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable.others") || argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[1]);
				return true;
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable.others") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (!SQLData.isWritable("Global", PlayerHandler.getPlayerID(argsPlayer))) {
					Language.getSendMessage(argsPlayer, "disabledForPlayerFailed", "");
				} else {
					SQLData.saveToDatabase(argsPlayer, "false", "disabled-players", "Global");
					Language.getSendMessage(argsPlayer, "disabledForPlayer", "");
					if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
						Language.setArgsPlayer(argsPlayer);
						Language.getSendMessage(sender, "disabledForOther", "");
					}
				}
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 3 && args[0].equalsIgnoreCase("disable")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			String world = args[2];
			if (world.equalsIgnoreCase("Global")) { world = "Global"; }
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable.others") || argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[1]);
				return true;
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable.others") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (!SQLData.isWritable(world, PlayerHandler.getPlayerID(argsPlayer))) {
					Language.getSendMessage(argsPlayer, "disabledForPlayerWorldFailed", world);
				} else {
				SQLData.saveToDatabase(argsPlayer, "false", "disabled-players", world);
				Language.getSendMessage(argsPlayer, "disabledForPlayerWorld", world);
				if (!sender.getName().equalsIgnoreCase(argsPlayer.getName())) {
					Language.setArgsPlayer(argsPlayer);
					Language.getSendMessage(sender, "enabledForOtherWorld", world);
				}
				}
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("disable")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (SQLData.isWritable("Global", "ALL")) {
					SQLData.saveToDatabase(null, "false", "disabled-players", "Global");
					Language.getSendMessage(sender, "disabledGlobal", "");
				} else {
					Language.getSendMessage(sender, "disabledGlobalFailed", "");
				}
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("s")) {
			if (args.length == 2) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.save") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
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
						if (item.getType().getMaxDurability() < 30 && ((short) item.getDurability()) > 0) {
							itemData.set("items." + args[1] + "." + "data-value", ((short) item.getDurability()));
						}
						if (item.getType().getMaxDurability() > 30 && ((short) item.getDurability()) != 0 && ((short) item.getDurability()) != ((short) item.getType().getMaxDurability())) {
							itemData.set("items." + args[1] + "." + "durability", ((short) item.getDurability()));
						}
						if (item.hasItemMeta()) {
							if (item.getItemMeta().hasDisplayName()) {
								String name = item.getItemMeta().getDisplayName();
								for (int i = 0; i <= 36; i++) {
									name = name.replace(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + i), "");
									name = name.replace(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + "Arbitrary" + i), "");
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
							if (ServerHandler.hasDebuggingMode()) {
								e.printStackTrace();
							}
						}
						Language.getSendMessage(sender, "playerSavedItem", args[1]);
						return true;
					} else {
						Language.getSendMessage(sender, "playerFailedSavedItem", args[1]);
						return true;
					}
				} else if (sender instanceof ConsoleCommandSender) {
					Language.getSendMessage(sender, "notPlayer", "");
					return true;
				}
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
			} else {
				Language.getSendMessage(sender, "playerInvalidSavedItem", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds") || args[0].equalsIgnoreCase("w")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					ServerHandler.sendCommandsMessage(sender, "");
					Language.getSendMessage(sender, "inWorldListHeader", "");
					Language.getSendMessage(sender, "inWorldListed", ((Player) sender).getWorld().getName());
					ServerHandler.sendCommandsMessage(sender, "");
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]--------------&a&l[&e Worlds In Menu 1/1 &a&l]&a&l&m-------------[");
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					Language.getSendMessage(sender, "notPlayer", "");
					return true;
				}
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.list") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					for (World worlds: ItemJoin.getInstance().getServer().getWorlds()) {
						ItemExists = false;
						Language.getSendMessage(sender, "listWorldsHeader", worlds.getName());
						for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
							ConfigurationSection items = ConfigHandler.getItemSection(item);
							String world = worlds.getName();
							Player player = ((Player) sender);
							if (items.getString(".slot") != null) {
								String slotlist = items.getString(".slot").replace(" ", "");
								String[] slots = slotlist.split(",");
								ItemHandler.clearItemID(player);
								String ItemID = ItemHandler.getItemID(player, slots[0]);
								ItemStack inStoredItems = CreateItems.items.get(world + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
								if (inStoredItems != null) {
									Language.getSendMessage(sender, "listItems", item);
									ItemExists = true;
								}
							}
						}
						if (ItemExists == false) {
							Language.getSendMessage(sender, "noItemsListed", "");
						}
					}
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e List Menu 1/1 &a&l]&a&l&m---------------[");
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					Language.getSendMessage(sender, "notPlayer", "");
					return true;
				}
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("permissions") || args.length == 1 && args[0].equalsIgnoreCase("perm") || args.length == 1 && args[0].equalsIgnoreCase("perms")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.permissions") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.*");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.*");
					}
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.all")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.All");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.All");
					}
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.Use");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.Use");
					}
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.reload")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.Reload");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.Reload");
					}
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.updates")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.Updates");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.Updates");
					}
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.get");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.get");
					}
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.get.others");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.get.others");
					}
					if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.permissions")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.permissions");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.permissions");
					}
					for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
						if (PermissionsHandler.hasCommandPermission(sender, "itemjoin." + world.getName() + ".*")) {
							ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin." + world.getName() + ".*");
						} else {
							ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin." + world.getName() + ".*");
						}
					}
					ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Permissions 2 &afor the next page.");
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------&a&l[&e Permissions Menu 1/2 &a&l]&a&l&m------------[");
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					Language.getSendMessage(sender, "notPlayer", "");
					return true;
				}
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("permissions") && args[1].equalsIgnoreCase("2") || args.length == 2 && args[0].equalsIgnoreCase("perm") && args[1].equalsIgnoreCase("2") || args.length == 2 && args[0].equalsIgnoreCase("perms") && args[1].equalsIgnoreCase("2")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.permissions") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					for (World worlds: ItemJoin.getInstance().getServer().getWorlds()) {
						for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
							ConfigurationSection items = ConfigHandler.getItemSection(item);
							String world = worlds.getName();
							Player player = ((Player) sender);
							if (items.getString(".slot") != null) {
								String slotlist = items.getString(".slot").replace(" ", "");
								String[] slots = slotlist.split(",");
								ItemHandler.clearItemID(player);
								String ItemID = ItemHandler.getItemID(player, slots[0]);
								ItemStack inStoredItems = CreateItems.items.get(world + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
								if (inStoredItems != null && PermissionsHandler.hasItemsPermission(items, item, (Player)sender)) {
									ServerHandler.sendCommandsMessage(sender, "&a[\u2714] " + PermissionsHandler.customPermissions(items, item, world));
								} else if (inStoredItems != null && !PermissionsHandler.hasItemsPermission(items, item, (Player)sender)) {
									ServerHandler.sendCommandsMessage(sender, "&c[\u2718] " + PermissionsHandler.customPermissions(items, item, world));
								}
							}
						}
					}
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------&a&l[&e Permissions Menu 2/2 &a&l]&a&l&m------------[");
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					Language.getSendMessage(sender, "notPlayer", "");
					return true;
				}
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 3 && args[0].equalsIgnoreCase("get")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[2]);
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others") || argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[2]);
				return true;
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.setArgsPlayer(sender);
				reAddItem(argsPlayer, sender, args[1]);
				AnimationHandler.OpenAnimations(argsPlayer);
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("get")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					Language.setArgsPlayer(null);
					reAddItem((Player) sender, null, args[1]);
					PlayerHandler.updateInventory((Player) sender);
					AnimationHandler.OpenAnimations((Player) sender);
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					Language.getSendMessage(sender, "notPlayer", "");
					Language.getSendMessage(sender, "correctGetSyntax", "");
					return true;
				}
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("get")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "invalidGetUsage", "");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("getall")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others") || argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[1]);
				return true;
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.setArgsPlayer(sender);
				reAddItem(argsPlayer, sender, "00a40gh392bd938d4");
				AnimationHandler.OpenAnimations(argsPlayer);
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("getall")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					Language.setArgsPlayer(null);
					reAddItem((Player) sender, null, "00a40gh392bd938d4");
					PlayerHandler.updateInventory((Player) sender);
					AnimationHandler.OpenAnimations((Player) sender);
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					Language.getSendMessage(sender, "notPlayer", "");
					Language.getSendMessage(sender, "correctGetSyntax", "");
					return true;
				}
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[2]);
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others") || argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[2]);
				return true;
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.setArgsPlayer(sender);
				removeItem(argsPlayer, sender, args[1]);
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					Language.setArgsPlayer(null);
					removeItem((Player) sender, null, args[1]);
					PlayerHandler.updateInventory((Player) sender);
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					Language.getSendMessage(sender, "notPlayer", "");
					Language.getSendMessage(sender, "correctRemoveSyntax", "");
					return true;
				}
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("remove")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "invalidRemoveSyntax", "");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("removeall")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others") || argsPlayer == null && PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[1]);
				return true;
			} else if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.setArgsPlayer(sender);
				removeItem(argsPlayer, sender, "00a40gh392bd938d4");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("removeall")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				if (sender instanceof Player) {
					Language.setArgsPlayer(null);
					removeItem((Player) sender, null, "00a40gh392bd938d4noother");
					PlayerHandler.updateInventory((Player) sender);
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					Language.getSendMessage(sender, "notPlayer", "");
					Language.getSendMessage(sender, "correctRemoveSyntax", "");
					return true;
				}
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("updates") || args[0].equalsIgnoreCase("update")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.updates") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "updateChecking", "");
				Updater.checkUpdates(sender);
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("AutoUpdate") || args[0].equalsIgnoreCase("AutoUpdate")) {
			if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.autoupdate") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.*")) {
				Language.getSendMessage(sender, "updateForced", "");
				Updater.forceUpdates(sender);
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else {
			if (ConfigHandler.getConfig("en-lang.yml").getString("unknownCommand") != null) {
				Language.getSendMessage(sender, "unknownCommand", "");
			}
			return true;
		}
		return false;
	}

	public static void reAddItem(Player player, CommandSender OtherPlayer, String itemName) {
		Boolean Success = false;
		Boolean MissingPerms = false;
		ItemExists = false;
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				if (items.getString(".slot") != null) {
					String slotlist = items.getString(".slot").replace(" ", "");
					String[] slots = slotlist.split(",");
					ItemHandler.clearItemID(player);
					for (String slot: slots) {
						String ItemID = ItemHandler.getItemID(player, slot);
						if (itemName.equalsIgnoreCase("00a40gh392bd938d4")) {
							if (Utils.isCustomSlot(slot)) {
								ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
								if (!ItemHandler.hasItem(player, inStoredItems)) {
									if (!(ConfigHandler.getConfig("config.yml").getBoolean("GetItem-Permissions")) || PermissionsHandler.hasItemsPermission(items, item, player) && ConfigHandler.getConfig("config.yml").getBoolean("GetItem-Permissions")) {
										SetItems.setCustomSlots(player, item, slot, ItemID);
										Success = true;
									} else {
										MissingPerms = true;
									}
						
								}
								ItemExists = true;
							} else if (Utils.isInt(slot)) {
								ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
								if (!ItemHandler.hasItem(player, inStoredItems)) {
									if (!(ConfigHandler.getConfig("config.yml").getBoolean("GetItem-Permissions")) || PermissionsHandler.hasItemsPermission(items, item, player) && ConfigHandler.getConfig("config.yml").getBoolean("GetItem-Permissions")) {
										SetItems.setInvSlots(player, item, slot, ItemID);
										Success = true;
									} else {
										MissingPerms = true;
									}
								}
								ItemExists = true;
							}
						} else {
						ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + itemName);
						if (inStoredItems != null && item.equalsIgnoreCase(itemName) && Utils.isCustomSlot(slot) && !ItemHandler.hasItem(player, inStoredItems) && ItemHandler.canOverwrite(player, slot, item)) {
							if (!(ConfigHandler.getConfig("config.yml").getBoolean("GetItem-Permissions")) || PermissionsHandler.hasItemsPermission(items, item, player) && ConfigHandler.getConfig("config.yml").getBoolean("GetItem-Permissions")) {
								SetItems.setCustomSlots(player, item, slot, ItemID);
							} else {
								if (OtherPlayer != null) {
									Language.setArgsPlayer(player);
									Language.getSendMessage(OtherPlayer, "givenToPlayerNoPerms", inStoredItems.getItemMeta().getDisplayName());
								} else {
									Language.getSendMessage(player, "givenNoPerms", inStoredItems.getItemMeta().getDisplayName());
								}
								return;
							}
							Language.getSendMessage(player, "givenToYou", inStoredItems.getItemMeta().getDisplayName());
							if (Language.getArgsPlayer() != null) {
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "givenToPlayer", inStoredItems.getItemMeta().getDisplayName());
								Language.setArgsPlayer(null);
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName) && Utils.isInt(slot) && Integer.parseInt(slot) >= 0 && Integer.parseInt(slot) <= 35 && !ItemHandler.hasItem(player, inStoredItems) && ItemHandler.canOverwrite(player, slot, item)) {
							if (!(ConfigHandler.getConfig("config.yml").getBoolean("GetItem-Permissions")) || PermissionsHandler.hasItemsPermission(items, item, player) && ConfigHandler.getConfig("config.yml").getBoolean("GetItem-Permissions")) {
								SetItems.setInvSlots(player, item, slot, ItemID);
							} else {
								if (OtherPlayer != null) {
									Language.setArgsPlayer(player);
									Language.getSendMessage(OtherPlayer, "givenToPlayerNoPerms", inStoredItems.getItemMeta().getDisplayName());
								} else {
									Language.getSendMessage(player, "givenNoPerms", inStoredItems.getItemMeta().getDisplayName());
								}
								return;
							}
							Language.getSendMessage(player, "givenToYou", inStoredItems.getItemMeta().getDisplayName());
							
							if (OtherPlayer != null) {
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "givenToPlayer", inStoredItems.getItemMeta().getDisplayName());
								Language.setArgsPlayer(null);
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName) && ItemHandler.hasItem(player, inStoredItems)) {
							if (Language.getArgsPlayer() != null) {
								Language.getSendMessage(player, "playerTriedGive", inStoredItems.getItemMeta().getDisplayName());
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "itemExistsInOthersInventory", inStoredItems.getItemMeta().getDisplayName());
								Language.setArgsPlayer(null);
							} else {
								Language.getSendMessage(player, "itemExistsInInventory", inStoredItems.getItemMeta().getDisplayName());
							}
							ItemExists = true;
						}
						PlayerHandler.updateInventory(player);
					}
				}
			}
		}	
		if (ItemExists == false) {
			if (OtherPlayer != null) {
				Language.setArgsPlayer(player);
				Language.getSendMessage(OtherPlayer, "itemDoesntExist", itemName);
			} else {
				Language.getSendMessage(player, "itemDoesntExist", itemName);
			}
		}
		}
		
		if (itemName.equalsIgnoreCase("00a40gh392bd938d4")) {
			if (Success == true) {
			Language.getSendMessage(player, "givenAllToYou", "&eAll Items");
			if (Language.getArgsPlayer() != null) {
				Language.setArgsPlayer(player);
				Language.getSendMessage(OtherPlayer, "givenAllToPlayer", "&eAll Items");
				Language.setArgsPlayer(null);
			}
			
			if (MissingPerms != false) {
				if (OtherPlayer != null) {
					Language.setArgsPlayer(player);
					Language.getSendMessage(OtherPlayer, "givenAllPlayerNoPerms", "&eAll Items");
				} else {
					Language.getSendMessage(player, "givenAllNoPerms", "&eAll Items");
				}
			}
			} else if (Success != true) {
				if (Language.getArgsPlayer() != null) {
					Language.getSendMessage(player, "playerTriedGiveAllItems", "All Items");
					Language.setArgsPlayer(player);
					Language.getSendMessage(OtherPlayer, "allItemsExistInOthersInventory", "All Items");
					Language.setArgsPlayer(null);
				} else {
					Language.getSendMessage(player, "allItemsExistInInventory", "All Items");
				}
			}
		}
		
	}

	public static void removeItem(Player player, CommandSender OtherPlayer, String itemName) {
		Boolean hasRan = false;
		ItemExists = false;
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				if (items.getString(".slot") != null) {
					String slotlist = items.getString(".slot").replace(" ", "");
					String[] slots = slotlist.split(",");
					ItemHandler.clearItemID(player);
					for (String slot: slots) {
						String ItemID = ItemHandler.getItemID(player, slot);
						if (itemName.equalsIgnoreCase("00a40gh392bd938d4") || itemName.equalsIgnoreCase("00a40gh392bd938d4noother")) {
							ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
							if (ItemHandler.hasItem(player, inStoredItems)) {
							   SetItems.setClearItemJoinItems(player);
								if (hasRan != true) {
								Language.getSendMessage(player, "removedAllFromYou", "&eAll Items");
								hasRan = true;
								if (Language.getArgsPlayer() != null) {
									Language.setArgsPlayer(player);
									if (!(itemName.equalsIgnoreCase("00a40gh392bd938d4noother"))) {
									Language.getSendMessage(OtherPlayer, "removedAllFromPlayer", "&eAll Items");
									Language.setArgsPlayer(null);
									}
								}
								}
								} else if ((hasRan != true)) {
									hasRan = true;
									if (Language.getArgsPlayer() != null) {
										Language.getSendMessage(player, "playerTriedRemoveAll", "All Items");
										Language.setArgsPlayer(player);
										if (!(itemName.equalsIgnoreCase("00a40gh392bd938d4noother"))) {
										Language.getSendMessage(OtherPlayer, "allItemsDoNotExistInOthersInventory", "Items");
										Language.setArgsPlayer(null);
										}
									} else {
										Language.getSendMessage(player, "allItemsDoNotExistInInventory", "Items");
									}
								}
								ItemExists = true;
						} else {
						ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + itemName);
						if (inStoredItems != null && item.equalsIgnoreCase(itemName) && Utils.isCustomSlot(slot) && player.getInventory().contains(inStoredItems)) {
							player.getInventory().removeItem(inStoredItems);
							Language.getSendMessage(player, "removedFromYou", inStoredItems.getItemMeta().getDisplayName());
							if (Language.getArgsPlayer() != null) {
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "removedFromPlayer", inStoredItems.getItemMeta().getDisplayName());
								Language.setArgsPlayer(null);
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName) && Utils.isInt(slot) && player.getInventory().contains(inStoredItems)) {
							player.getInventory().removeItem(inStoredItems);
							Language.getSendMessage(player, "removedFromYou", inStoredItems.getItemMeta().getDisplayName());
							if (Language.getArgsPlayer() != null) {
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "removedFromPlayer", inStoredItems.getItemMeta().getDisplayName());
								Language.setArgsPlayer(null);
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName)) {
							if (Language.getArgsPlayer() != null) {
								Language.getSendMessage(player, "playerTriedRemove", inStoredItems.getItemMeta().getDisplayName());
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "itemDoesntExistInOthersInventory", inStoredItems.getItemMeta().getDisplayName());
								Language.setArgsPlayer(null);
							} else {
								Language.getSendMessage(player, "itemDoesntExistInInventory", inStoredItems.getItemMeta().getDisplayName());
							}
							ItemExists = true;
						}
						PlayerHandler.updateInventory(player);
					}
				}
			}
		}
		if (ItemExists == false) {
			if (OtherPlayer != null) {
				Language.getSendMessage(OtherPlayer, "itemDoesntExist", itemName);
			} else {
				Language.getSendMessage(player, "itemDoesntExist", itemName);
			}
		}
		}
	}
	}