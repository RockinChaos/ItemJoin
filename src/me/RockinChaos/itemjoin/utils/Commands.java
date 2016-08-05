package me.RockinChaos.itemjoin.utils;

import java.util.List;

import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.*;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.CacheItems.CacheItems;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandlers;

public class Commands implements CommandExecutor {

	public static String Prefix;
	public static String noPermission;
	public static String notPlayer;
	public static String consoleAltSyntax;
	public static String unknownCommand;
	public static String badGetUsage;
	public static String givenItem;
	public static String givenOthersItem;
	public static String receivedOthersItem;
	public static String itemDoesNotExist;
	public static String badSlot1;
	public static String badSlot2;
	public static String badID;
	public static String consoleReloadedConfig;
	public static String reloadedConfig;
	public static String cachedWorlds;
	public static String loadedWorlds;
	public static String loadedWorldsListed;
	public static String listWorlds;
	public static String listItems;
	public static String nolistItems;
	public static String worldIn;
	public static String worldInListed;
	public static String inventoryFull;
	public static String inventoryFullOthers;
	public static String itemCostSuccess;
	public static String itemCostFailed;
	public static ConsoleCommandSender Console = ItemJoin.pl.getServer().getConsoleSender();
	public static String CPrefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";
	public static boolean failedGive = false;

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				sender.sendMessage(ChatColor.GREEN + "ItemJoin v." + ItemJoin.pl.getDescription().getVersion()
						+ ChatColor.YELLOW + " by RockinChaos");
				sender.sendMessage(ChatColor.GREEN + "Type" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
						+ " /ItemJoin Help " + ChatColor.GREEN + "for the help menu.");
				return true;
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("help")
				|| args.length == 1 && args[0].equalsIgnoreCase("h")) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
						+ ChatColor.STRIKETHROUGH.toString() + "]--------------" + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " ItemJoin " + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
						+ ChatColor.STRIKETHROUGH.toString() + "--------------[");
				sender.sendMessage(ChatColor.GREEN + "ItemJoin v." + ItemJoin.pl.getDescription().getVersion()
						+ ChatColor.YELLOW + " by RockinChaos");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Help"
						+ ChatColor.GRAY + " - " + ChatColor.YELLOW + "This help menu.");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Reload"
						+ ChatColor.GRAY + " - " + ChatColor.YELLOW + "Reloads the .yml files.");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Loaded"
						+ ChatColor.GRAY + " - " + ChatColor.YELLOW + "Lists the loaded worlds for ItemJoin.");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Updates"
						+ ChatColor.GRAY + " - " + ChatColor.YELLOW + "Checks for plugin updates.");
				sender.sendMessage(ChatColor.GREEN + "Type" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
						+ " /ItemJoin Help 2 " + ChatColor.GREEN + "for the next page.");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
						+ ChatColor.STRIKETHROUGH.toString() + "]------------" + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " Help Menu 1/2 "
						+ ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "------------[");
				sender.sendMessage("");
				return true;
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("2")
				|| args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("2")) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
						+ ChatColor.STRIKETHROUGH.toString() + "]--------------" + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " ItemJoin " + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
						+ ChatColor.STRIKETHROUGH.toString() + "--------------[");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Permissions"
						+ ChatColor.GRAY + " - " + ChatColor.YELLOW + "Lists the permissions you have.");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Permissions 2"
						+ ChatColor.GRAY + " - " + ChatColor.YELLOW + "Permissions page 2.");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Get <Item>"
						+ ChatColor.GRAY + " - " + ChatColor.YELLOW + "Gives that item.");
				sender.sendMessage(
						ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Get <Item> <Player>"
								+ ChatColor.GRAY + " - " + ChatColor.YELLOW + "Gives to said player.");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin World"
						+ ChatColor.GRAY + " - " + ChatColor.YELLOW + "Check what world you are in. (debugging).");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin List"
						+ ChatColor.GRAY + " - " + ChatColor.YELLOW + "Check items you can get each what worlds.");
				sender.sendMessage(ChatColor.GREEN + "Found a bug? Report it @");
				sender.sendMessage(ChatColor.GREEN + "http://dev.bukkit.org/bukkit-plugins/itemjoin/");
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
						+ ChatColor.STRIKETHROUGH.toString() + "]------------" + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " Help Menu 2/2 "
						+ ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "------------[");
				sender.sendMessage("");
				return true;
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			if (sender.hasPermission("itemjoin.reload") || sender.hasPermission("itemjoin.*")) {
				ItemJoin.loadSpecialConfig("items.yml");
				ItemJoin.getSpecialConfig("items.yml").options().copyDefaults(false);
				ItemJoin.pl.worlds = ItemJoin.getSpecialConfig("items.yml").getStringList("world-list");
				ItemJoin.pl.saveDefaultConfig();
				ItemJoin.pl.getConfig().options().copyDefaults(false);
				ItemJoin.pl.reloadConfig();
				Registers.firstJoinFile();
				if (ItemJoin.pl.getConfig().getString("Language").equalsIgnoreCase("English")) {
					ItemJoin.loadSpecialConfig("en-lang.yml");
					ItemJoin.getSpecialConfig("en-lang.yml").options().copyDefaults(false);
				}
				RegisterEnLang(PlayerHandlers.PlayerHolder());
				ItemJoin.pl.items.clear();
				for (Player player : ItemJoin.pl.getServer().getOnlinePlayers()) {
					CacheItems.run(player);
				}
				sender.sendMessage(reloadedConfig);
				List<String> PrintWorlds = ItemJoin.getSpecialConfig("items.yml").getStringList("world-list");
				for (int i = 0; i < PrintWorlds.size(); i++) {
					String world = (String) PrintWorlds.get(i);
					sender.sendMessage(cachedWorlds.replace("%cache_world%", world));
				}
				Console.sendMessage(consoleReloadedConfig.replace("%player_reloaded%", sender.getName()));
				return true;
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args[0].equalsIgnoreCase("loaded") || args[0].equalsIgnoreCase("l")) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				sender.sendMessage(loadedWorlds);
				List<String> PrintWorlds = ItemJoin.getSpecialConfig("items.yml").getStringList("world-list");
				for (int i = 0; i < PrintWorlds.size(); i++) {
					String world = (String) PrintWorlds.get(i);
					sender.sendMessage(loadedWorldsListed.replace("%loaded_worlds%", world));
				}
				return true;
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds")
				|| args[0].equalsIgnoreCase("w")) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					sender.sendMessage(worldIn);
					sender.sendMessage(worldInListed.replace("%in_worlds%", ((Player) sender).getWorld().getName()));
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					sender.sendMessage(notPlayer);
					return true;
				}
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l")) {
			if (sender.hasPermission("itemjoin.list") || sender.hasPermission("itemjoin.*")) {
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
						+ ChatColor.STRIKETHROUGH.toString() + "]----------------" + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " ItemJoin " + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
						+ ChatColor.STRIKETHROUGH.toString() + "----------------[");
				for (int i = 0; i < ItemJoin.pl.worlds.size(); i++) {
					String world = ItemJoin.pl.worlds.get(i);
					sender.sendMessage(listWorlds.replace("%world%", world));
					ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml")
							.getConfigurationSection(world + ".items");
					if (selection != null) {
						for (String item : selection.getKeys(false)) {
							sender.sendMessage(listItems.replace("%items%", item));
						}
					} else if (selection == null) {
							sender.sendMessage(nolistItems);
					}
				}
				sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
						+ ChatColor.STRIKETHROUGH.toString() + "]--------------" + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " List Menu 1/1 "
						+ ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString()
						+ ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "--------------[");
				return true;
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("permissions")
				|| args.length == 1 && args[0].equalsIgnoreCase("perm")
				|| args.length == 1 && args[0].equalsIgnoreCase("perms")) {
			if (sender.hasPermission("itemjoin.permissions") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
							+ ChatColor.STRIKETHROUGH.toString() + "]--------------" + ChatColor.GREEN.toString()
							+ ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " ItemJoin "
							+ ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString()
							+ ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "--------------[");
					if (sender.hasPermission("itemjoin.*")) {
						sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.*");
					} else {
						sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.*");
					}
					if (sender.hasPermission("itemjoin.use")) {
						sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.Use");
					} else {
						sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.Use");
					}
					if (sender.hasPermission("itemjoin.reload")) {
						sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.Reload");
					} else {
						sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.Reload");
					}
					if (sender.hasPermission("itemjoin.updates")) {
						sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.Updates");
					} else {
						sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.Updates");
					}
					if (sender.hasPermission("itemjoin.get")) {
						sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.get");
					} else {
						sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.get");
					}
					if (sender.hasPermission("itemjoin.get.others")) {
						sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.get.others");
					} else {
						sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.get.others");
					}
					if (sender.hasPermission("itemjoin.permissions")) {
						sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.permissions");
					} else {
						sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.permissions");
					}
					for (int i = 0; i < ItemJoin.pl.worlds.size(); i++) {
						String world = (String) ItemJoin.pl.worlds.get(i);
						if (sender.hasPermission("itemjoin." + world + ".*")) {
							sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin." + world + ".*");
						} else {
							sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin." + world + ".*");
						}
					}
					sender.sendMessage(ChatColor.GREEN.toString() + "Type /ItemJoin Permissions 2 for the next page.");
					sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
							+ ChatColor.STRIKETHROUGH.toString() + "]------------" + ChatColor.GREEN.toString()
							+ ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " Permissions Menu 1/2 "
							+ ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString()
							+ ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "------------[");
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					sender.sendMessage(notPlayer);
					return true;
				}
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("permissions") && args[1].equalsIgnoreCase("2")
				|| args.length == 2 && args[0].equalsIgnoreCase("perm") && args[1].equalsIgnoreCase("2")
				|| args.length == 2 && args[0].equalsIgnoreCase("perms") && args[1].equalsIgnoreCase("2")) {
			if (sender.hasPermission("itemjoin.permissions") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
							+ ChatColor.STRIKETHROUGH.toString() + "]--------------" + ChatColor.GREEN.toString()
							+ ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " ItemJoin "
							+ ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString()
							+ ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "--------------[");
					for (int i = 0; i < ItemJoin.pl.worlds.size(); i++) {
						String world = ItemJoin.pl.worlds.get(i);
						ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml")
								.getConfigurationSection(world + ".items");
						for (String item : selection.getKeys(false)) {
							ConfigurationSection items = selection.getConfigurationSection(item);
							ItemStack toSet = ItemJoin.pl.items
									.get(world + "." + sender.getName().toString() + ".items." + item);
							if (toSet != null
									&& sender.hasPermission(PermissionsHandler.customPermissions(items, item, world))) {
								sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] "
										+ PermissionsHandler.customPermissions(items, item, world));
							} else if (toSet != null && !sender
									.hasPermission(PermissionsHandler.customPermissions(items, item, world))) {
								sender.sendMessage(ChatColor.RED.toString() + "[\u2718] "
										+ PermissionsHandler.customPermissions(items, item, world));
							}
						}
					}
					sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString()
							+ ChatColor.STRIKETHROUGH.toString() + "]------------" + ChatColor.GREEN.toString()
							+ ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " Permissions Menu 2/2 "
							+ ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString()
							+ ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "------------[");
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					sender.sendMessage(notPlayer);
					return true;
				}
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args.length == 3 && args[0].equalsIgnoreCase("get")) {
			Player argsPlayer = PlayerHandlers.StringPlayer(args[2]);
			if (argsPlayer == null && sender.hasPermission("itemjoin.get.others")
					|| argsPlayer == null && sender.hasPermission("itemjoin.*")) {
				sender.sendMessage(CPrefix + ChatColor.RED + "The player " + ChatColor.AQUA + args[2] + ChatColor.RED
						+ " could not be found!");
				return true;
			} else if (sender.hasPermission("itemjoin.get.others") || sender.hasPermission("itemjoin.*")) {
				String world = argsPlayer.getWorld().getName();
				ItemStack toSet = ItemJoin.pl.items.get(
						argsPlayer.getWorld().getName() + "." + argsPlayer.getName().toString() + ".items." + args[1]);
				String slot = ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + args[1] + ".slot");
				Material tempmat = PlayerHandlers.getLocateMaterial(world, args[1]);
				EntityEquipment Equip = argsPlayer.getEquipment();
				if (toSet != null && !CheckItem.CheckMaterial(tempmat, world, args[1])) {
					sender.sendMessage(badID.replace("%bad_id%",
							ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + args[1] + ".id")));
				} else if (toSet != null && !CheckItem.CheckSlot(slot, world, args[1])) {
					sender.sendMessage(badSlot1.replace("%bad_slot%", slot));
					sender.sendMessage(badSlot2.replace("%bad_slot%", slot));
				}
				if (toSet != null && CheckItem.CheckSlot(slot, world, args[1]) && !ItemJoin.isInt(slot)) {
					if (slot.equalsIgnoreCase("Arbitrary")) {
						if (argsPlayer.getInventory().firstEmpty() == -1) {
							sender.sendMessage(
									inventoryFull.replace("%given_item%", toSet.getItemMeta().getDisplayName())
											.replace("%given_player%", argsPlayer.getName())
											.replace("%received_player%", sender.getName()));
							failedGive = true;
						} else {
							((Player) sender).getInventory().addItem(toSet);
						}
					} else if (slot.equalsIgnoreCase("Helmet")) {
						Equip.setHelmet(toSet);
					} else if (slot.equalsIgnoreCase("Chestplate")) {
						Equip.setChestplate(toSet);
					} else if (slot.equalsIgnoreCase("Leggings")) {
						Equip.setLeggings(toSet);
					} else if (slot.equalsIgnoreCase("Boots")) {
						Equip.setBoots(toSet);
					} else if (slot.equalsIgnoreCase("Offhand")) {
						argsPlayer.getInventory().setItemInOffHand(toSet);
					}
					if (failedGive != true) {
						sender.sendMessage(givenOthersItem.replace("%given_item%", toSet.getItemMeta().getDisplayName())
								.replace("%given_player%", argsPlayer.getName()));
						argsPlayer.sendMessage(
								receivedOthersItem.replace("%received_item%", toSet.getItemMeta().getDisplayName())
										.replace("%received_player%", sender.getName()));
					}
					failedGive = false;
					PlayerHandlers.updateInventory(argsPlayer);
				} else if (toSet != null && CheckItem.CheckSlot(slot, world, args[1])) {
					int Slot = Integer.parseInt(slot);
					argsPlayer.getInventory().setItem(Slot, toSet);
					if (failedGive != true) {
						sender.sendMessage(givenOthersItem.replace("%given_item%", toSet.getItemMeta().getDisplayName())
								.replace("%given_player%", argsPlayer.getName()));
						argsPlayer.sendMessage(
								receivedOthersItem.replace("%received_item%", toSet.getItemMeta().getDisplayName())
										.replace("%received_player%", sender.getName()));
					}
					PlayerHandlers.updateInventory(argsPlayer);
					failedGive = false;
				} else if (toSet == null) {
					if (failedGive != true) {
						sender.sendMessage(itemDoesNotExist.replace("%bad_item%", args[1]));
					}
					failedGive = false;
				}
				return true;
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("get")) {
			if (sender.hasPermission("itemjoin.get") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					String world = ((Player) sender).getWorld().getName();
					ItemStack toSet = ItemJoin.pl.items.get(((Entity) sender).getWorld().getName() + "."
							+ sender.getName().toString() + ".items." + args[1]);
					String slot = ItemJoin.getSpecialConfig("items.yml")
							.getString(world + ".items." + args[1] + ".slot");
					Material tempmat = PlayerHandlers.getLocateMaterial(world, args[1]);
					EntityEquipment Equip = ((LivingEntity) sender).getEquipment();
					if (toSet != null && !CheckItem.CheckMaterial(tempmat, world, args[1])) {
						sender.sendMessage(badID.replace("%bad_id%",
								ItemJoin.getSpecialConfig("items.yml").getString(world + ".items." + args[1] + ".id")));
					} else if (toSet != null && !CheckItem.CheckSlot(slot, world, args[1])) {
						sender.sendMessage(badSlot1.replace("%bad_slot%", slot));
						sender.sendMessage(badSlot2.replace("%bad_slot%", slot));
					}
					if (toSet != null && CheckItem.CheckSlot(slot, world, args[1]) && !ItemJoin.isInt(slot)) {
						if (slot.equalsIgnoreCase("Arbitrary")) {
							if (((Player) sender).getInventory().firstEmpty() == -1) {
								sender.sendMessage(
										inventoryFull.replace("%given_item%", toSet.getItemMeta().getDisplayName()));
								failedGive = true;
							} else {
								((Player) sender).getInventory().addItem(toSet);
							}
						} else if (slot.equalsIgnoreCase("Helmet")) {
							Equip.setHelmet(toSet);
						} else if (slot.equalsIgnoreCase("Chestplate")) {
							Equip.setChestplate(toSet);
						} else if (slot.equalsIgnoreCase("Leggings")) {
							Equip.setLeggings(toSet);
						} else if (slot.equalsIgnoreCase("Boots")) {
							Equip.setBoots(toSet);
						} else if (slot.equalsIgnoreCase("Offhand")) {
							((HumanEntity) sender).getInventory().setItemInOffHand(toSet);
						}
						if (failedGive != true) {
							sender.sendMessage(givenItem.replace("%given_item%", toSet.getItemMeta().getDisplayName()));
						}
						failedGive = false;
						PlayerHandlers.updateInventory((Player) sender);
					} else if (toSet != null && CheckItem.CheckSlot(slot, world, args[1])) {
						int Slot = Integer.parseInt(slot);
						((Player) sender).getInventory().setItem(Slot, toSet);
						if (failedGive != true) {
							sender.sendMessage(givenItem.replace("%given_item%", toSet.getItemMeta().getDisplayName()));
						}
						failedGive = false;
						PlayerHandlers.updateInventory((Player) sender);
					} else if (toSet == null) {
						sender.sendMessage(itemDoesNotExist.replace("%bad_item%", args[1]));
					}
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					sender.sendMessage(notPlayer);
					sender.sendMessage(consoleAltSyntax);
					return true;
				}
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("get")) {
			if (sender.hasPermission("itemjoin.get") || sender.hasPermission("itemjoin.*")) {
				sender.sendMessage(badGetUsage);
				return true;
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args[0].equalsIgnoreCase("updates") || args[0].equalsIgnoreCase("update")) {
			if (sender.hasPermission("itemjoin.updates") || sender.hasPermission("itemjoin.*")) {
				Console.sendMessage(
				CPrefix + ChatColor.RED + sender.getName() + " has requested to check for updates!");
				UpdateChecker.checkUpdates(sender);
				return true;
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else if (args[0].equalsIgnoreCase("AutoUpdate") || args[0].equalsIgnoreCase("AutoUpdate")) {
			if (sender.hasPermission("itemjoin.autoupdate") || sender.hasPermission("itemjoin.*")) {
				Console.sendMessage(
				CPrefix + ChatColor.RED + sender.getName() + " has requested to force update the server!");
			    UpdateChecker.forceUpdates(sender);
				return true;
			} else {
				sender.sendMessage(noPermission);
				return true;
			}
		} else {
			if (ItemJoin.getSpecialConfig("en-lang.yml").getString("unknownCommand") != null) {
				sender.sendMessage(unknownCommand);
			}
			return true;
		}
		return false;
	}

	public static void RegisterEnLang(Player player) {
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("Prefix") != null) {
			Commands.Prefix = ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("Prefix"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("Prefix") == null) {
			Commands.Prefix = "";
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("noPermission") != null) {
			Commands.noPermission = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("noPermission"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("notPlayer") != null) {
			Commands.notPlayer = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("notPlayer"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("consoleAltSyntax") != null) {
			Commands.consoleAltSyntax = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("consoleAltSyntax"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("unknownCommand") != null) {
			Commands.unknownCommand = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("unknownCommand"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("badGetUsage") != null) {
			Commands.badGetUsage = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("badGetUsage"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("givenItem") != null) {
			Commands.givenItem = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("givenItem"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("givenOthersItem") != null) {
			Commands.givenOthersItem = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("givenOthersItem"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("receivedOthersItem") != null) {
			Commands.receivedOthersItem = Prefix + ItemJoin.pl.formatPlaceholders(
					ItemJoin.getSpecialConfig("en-lang.yml").getString("receivedOthersItem"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("itemDoesNotExist") != null) {
			Commands.itemDoesNotExist = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("itemDoesNotExist"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("badSlot1") != null) {
			Commands.badSlot1 = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("badSlot1"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("badSlot2") != null) {
			Commands.badSlot2 = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("badSlot2"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("badID") != null) {
			Commands.badID = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("badID"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("consoleReloadedConfig") != null) {
			Commands.consoleReloadedConfig = Prefix + ItemJoin.pl.formatPlaceholders(
					ItemJoin.getSpecialConfig("en-lang.yml").getString("consoleReloadedConfig"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("reloadedConfig") != null) {
			Commands.reloadedConfig = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("reloadedConfig"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("cachedWorlds") != null) {
			Commands.cachedWorlds = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("cachedWorlds"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("loadedWorlds") != null) {
			Commands.loadedWorlds = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("loadedWorlds"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("loadedWorldsListed") != null) {
			Commands.loadedWorldsListed = Prefix + ItemJoin.pl.formatPlaceholders(
					ItemJoin.getSpecialConfig("en-lang.yml").getString("loadedWorldsListed"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("listWorlds") != null) {
			Commands.listWorlds = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("listWorlds"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("listItems") != null) {
			Commands.listItems = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("listItems"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("nolistItems") != null) {
			Commands.nolistItems = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("nolistItems"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("worldIn") != null) {
			Commands.worldIn = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("worldIn"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("inventoryFull") != null) {
			Commands.inventoryFull = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("inventoryFull"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("inventoryFullOthers") != null) {
			Commands.inventoryFullOthers = Prefix + ItemJoin.pl.formatPlaceholders(
					ItemJoin.getSpecialConfig("en-lang.yml").getString("inventoryFullOthers"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("itemCostSuccess") != null) {
			Commands.itemCostSuccess = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("itemCostSuccess"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("itemCostFailed") != null) {
			Commands.itemCostFailed = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("itemCostFailed"), player);
		}
		if (ItemJoin.getSpecialConfig("en-lang.yml").getString("worldInListed") != null) {
			Commands.worldInListed = Prefix + ItemJoin.pl
					.formatPlaceholders(ItemJoin.getSpecialConfig("en-lang.yml").getString("worldInListed"), player);
		}
	}
}