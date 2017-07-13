package me.RockinChaos.itemjoin.utils;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.listeners.giveitems.SetItems;

public class Commands implements CommandExecutor {
	public static boolean ItemExists = false;
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "&aItemJoin v" + ItemJoin.pl.getDescription().getVersion() + "&e by RockinChaos");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help &afor the help menu.");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("help") || args.length == 1 && args[0].equalsIgnoreCase("h")) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				ServerHandler.sendCommandsMessage(sender, "&aItemJoin v" + ItemJoin.pl.getDescription().getVersion() + "&e by RockinChaos");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Help &7- &eThis help menu");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Reload &7- &eReloads the .yml files");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Updates &7- &eChecks for plugin updates");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin AutoUpdate &7- &eUpdate ItemJoin to latest version");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help 2 &afor the next page");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 1/3 &a&l]&a&l&m---------------[");
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("2") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("2")) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin List &7- &eCheck items you can get each what worlds");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin World &7- &eCheck what world you are in, debugging");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Permissions &7- &eLists the permissions you have");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Permissions 2 &7- &ePermissions page 2");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help 3 &afor the next page");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 2/3 &a&l]&a&l&m---------------[");
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("3") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("3")) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Get <Item> &7- &eGives that ItemJoin item");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Get <Item> <Player> &7- &eGives to said player");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Remove <Item> &7- &eRemoves item from inventory");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Remove <Item> <Player> &7- &eRemoves from player");
				ServerHandler.sendCommandsMessage(sender, "&aFound a bug? Report it @");
				ServerHandler.sendCommandsMessage(sender, "&ahttps://github.com/RockinChaos/ItemJoin/issues");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 3/3 &a&l]&a&l&m---------------[");
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			if (sender.hasPermission("itemjoin.reload") || sender.hasPermission("itemjoin.*")) {
				CreateItems.items.clear();
				ConfigHandler.loadConfigs();
				CreateItems.setRun();
				Language.getSendMessage(sender, "reloadedConfigs", "");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds") || args[0].equalsIgnoreCase("w")) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
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
			if (sender.hasPermission("itemjoin.list") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					for (World worlds: ItemJoin.pl.getServer().getWorlds()) {
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
								ItemStack inStoredItems = CreateItems.items.get(world + "." + player.getName().toString() + ".items." + ItemID + item);
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
			if (sender.hasPermission("itemjoin.permissions") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					if (sender.hasPermission("itemjoin.*")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.*");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.*");
					}
					if (sender.hasPermission("itemjoin.use")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.Use");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.Use");
					}
					if (sender.hasPermission("itemjoin.reload")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.Reload");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.Reload");
					}
					if (sender.hasPermission("itemjoin.updates")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.Updates");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.Updates");
					}
					if (sender.hasPermission("itemjoin.get")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.get");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.get");
					}
					if (sender.hasPermission("itemjoin.get.others")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.get.others");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.get.others");
					}
					if (sender.hasPermission("itemjoin.permissions")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.permissions");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.permissions");
					}
					for (World world: ItemJoin.pl.getServer().getWorlds()) {
						if (sender.hasPermission("itemjoin." + world.getName() + ".*")) {
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
			if (sender.hasPermission("itemjoin.permissions") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					for (World worlds: ItemJoin.pl.getServer().getWorlds()) {
						for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
							ConfigurationSection items = ConfigHandler.getItemSection(item);
							String world = worlds.getName();
							Player player = ((Player) sender);
							if (items.getString(".slot") != null) {
								String slotlist = items.getString(".slot").replace(" ", "");
								String[] slots = slotlist.split(",");
								ItemHandler.clearItemID(player);
								String ItemID = ItemHandler.getItemID(player, slots[0]);
								ItemStack inStoredItems = CreateItems.items.get(world + "." + player.getName().toString() + ".items." + ItemID + item);
								if (inStoredItems != null && sender.hasPermission(PermissionsHandler.customPermissions(items, item, world))) {
									ServerHandler.sendCommandsMessage(sender, "&a[\u2714] " + PermissionsHandler.customPermissions(items, item, world));
								} else if (inStoredItems != null && !sender.hasPermission(PermissionsHandler.customPermissions(items, item, world))) {
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
			if (argsPlayer == null && sender.hasPermission("itemjoin.get.others") || argsPlayer == null && sender.hasPermission("itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[2]);
				return true;
			} else if (sender.hasPermission("itemjoin.get.others") || sender.hasPermission("itemjoin.*")) {
				Language.argsplayer = sender;
				reAddItem(argsPlayer, sender, args[1]);
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("get")) {
			if (sender.hasPermission("itemjoin.get") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					reAddItem((Player) sender, null, args[1]);
					PlayerHandler.updateInventory((Player) sender);
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
			if (sender.hasPermission("itemjoin.get") || sender.hasPermission("itemjoin.*")) {
				Language.getSendMessage(sender, "invalidGetUsage", "");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[2]);
			if (argsPlayer == null && sender.hasPermission("itemjoin.remove.others") || argsPlayer == null && sender.hasPermission("itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[2]);
				return true;
			} else if (sender.hasPermission("itemjoin.remove.others") || sender.hasPermission("itemjoin.*")) {
				Language.argsplayer = sender;
				removeItem(argsPlayer, sender, args[1]);
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
			if (sender.hasPermission("itemjoin.remove") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
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
			if (sender.hasPermission("itemjoin.remove") || sender.hasPermission("itemjoin.*")) {
				Language.getSendMessage(sender, "invalidRemoveSyntax", "");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("updates") || args[0].equalsIgnoreCase("update")) {
			if (sender.hasPermission("itemjoin.updates") || sender.hasPermission("itemjoin.*")) {
				Language.getSendMessage(sender, "updateChecking", "");
				Updater.checkUpdates(sender);
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("AutoUpdate") || args[0].equalsIgnoreCase("AutoUpdate")) {
			if (sender.hasPermission("itemjoin.autoupdate") || sender.hasPermission("itemjoin.*")) {
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
						ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + ItemID + itemName);
						int dataValue = items.getInt(".data-value");
						Material tempmat = CreateItems.getMaterial(items);
						ItemStack tempitem = null;
						if (ServerHandler.hasViableUpdate()) {
							tempitem = new ItemStack(tempmat, items.getInt(".count", 1), (short) dataValue);
						} else if (!ServerHandler.hasViableUpdate()) {
							try {
								tempitem = new ItemStack(tempmat, items.getInt(".count", 1), (short) dataValue);
							} catch (NullPointerException ex) {}
						}
						String lookup = ItemHandler.getName(tempitem);
						String name = Utils.format("&r" + lookup, player);
						if (items.getString(".name") != null) {
							name = items.getString(".name");
							name = Utils.format("&r" + name, player);
						}
						if (inStoredItems != null && item.equalsIgnoreCase(itemName) && Utils.isCustomSlot(slot) && ItemHandler.isObtainable(player, item, slot, ItemID, inStoredItems)) {
							SetItems.setCustomSlots(player, item, slot, ItemID);
							Language.getSendMessage(player, "givenToYou", inStoredItems.getItemMeta().getDisplayName());
							if (Language.argsplayer != null) {
								Language.argsplayer = player;
								Language.getSendMessage(OtherPlayer, "givenToPlayer", name);
								Language.argsplayer = null;
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName) && Utils.isInt(slot) && ItemHandler.isObtainable(player, item, slot, ItemID, inStoredItems)) {
							SetItems.setInvSlots(player, item, slot, ItemID);
							Language.getSendMessage(player, "givenToYou", inStoredItems.getItemMeta().getDisplayName());
							if (Language.argsplayer != null) {
								Language.argsplayer = player;
								Language.getSendMessage(OtherPlayer, "givenToPlayer", name);
								Language.argsplayer = null;
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName) && !ItemHandler.isObtainable(player, item, slot, ItemID, inStoredItems)) {
							if (Language.argsplayer != null) {
								Language.getSendMessage(player, "playerTriedGive", name);
								Language.argsplayer = player;
								Language.getSendMessage(OtherPlayer, "itemExistsInOthersInventory", name);
								Language.argsplayer = null;
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
			Language.getSendMessage(player, "itemDoesntExist", itemName);
		}
	}

	public static void removeItem(Player player, CommandSender OtherPlayer, String itemName) {
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
						ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + ItemID + itemName);
						int dataValue = items.getInt(".data-value");
						Material tempmat = CreateItems.getMaterial(items);
						ItemStack tempitem = null;
						if (ServerHandler.hasViableUpdate()) {
							tempitem = new ItemStack(tempmat, items.getInt(".count", 1), (short) dataValue);
						} else if (!ServerHandler.hasViableUpdate()) {
							try {
								tempitem = new ItemStack(tempmat, items.getInt(".count", 1), (short) dataValue);
							} catch (NullPointerException ex) {}
						}
						String lookup = ItemHandler.getName(tempitem);
						String name = Utils.format("&r" + lookup, player);
						if (items.getString(".name") != null) {
							name = items.getString(".name");
							name = Utils.format("&r" + name, player);
						}
						if (inStoredItems != null && item.equalsIgnoreCase(itemName) && Utils.isCustomSlot(slot) && player.getInventory().contains(inStoredItems)) {
							player.getInventory().removeItem(inStoredItems);
							if (items.getString(".name") != null) {
								name = items.getString(".name");
								name = Utils.format("&r" + name, player);
							}
							Language.getSendMessage(player, "removedFromYou", inStoredItems.getItemMeta().getDisplayName());
							if (Language.argsplayer != null) {
								Language.argsplayer = player;
								Language.getSendMessage(OtherPlayer, "removedFromPlayer", name);
								Language.argsplayer = null;
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName) && Utils.isInt(slot) && player.getInventory().contains(inStoredItems)) {
							player.getInventory().removeItem(inStoredItems);
							Language.getSendMessage(player, "removedFromYou", name);
							if (Language.argsplayer != null) {
								Language.argsplayer = player;
								Language.getSendMessage(OtherPlayer, "removedFromPlayer", name);
								Language.argsplayer = null;
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName)) {
							if (Language.argsplayer != null) {
								Language.getSendMessage(player, "playerTriedRemove", inStoredItems.getItemMeta().getDisplayName());
								Language.argsplayer = player;
								Language.getSendMessage(OtherPlayer, "itemDoesntExistInOthersInventory", name);
								Language.argsplayer = null;
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
			Language.getSendMessage(player, "itemDoesntExist", itemName);
		}
	}
	}