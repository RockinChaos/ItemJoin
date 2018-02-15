package me.RockinChaos.itemjoin.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.AnimationHandler;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.listeners.giveitems.SetItems;

public class Commands implements CommandExecutor {
	private static boolean ItemExists = false;
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help &afor the help menu.");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 1 && args[0].equalsIgnoreCase("help") || args.length == 1 && args[0].equalsIgnoreCase("h") 
				|| args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("1") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("1")) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				ServerHandler.sendCommandsMessage(sender, "&aItemJoin v" + ItemJoin.getInstance().getDescription().getVersion() + "&e by RockinChaos");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Help &7- &eThis help menu");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Reload &7- &eReloads the .yml files");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Updates &7- &eChecks for plugin updates");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin AutoUpdate &7- &eUpdate ItemJoin to latest version");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help 2 &afor the next page");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 1/4 &a&l]&a&l&m---------------[");
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
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Save <Name> &7- &eSave the held item to the config");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin List &7- &eCheck items you can get each what worlds");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin World &7- &eCheck what world you are in, debugging");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Permissions &7- &eLists the permissions you have");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin Permissions 2 &7- &ePermissions page 2");
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help 3 &afor the next page");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 2/4 &a&l]&a&l&m---------------[");
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
				ServerHandler.sendCommandsMessage(sender, "&aType &a&l/ItemJoin Help 4 &afor the next page");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 3/4 &a&l]&a&l&m---------------[");
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("4") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("4")) {
			if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
				ServerHandler.sendCommandsMessage(sender, "blankmessage");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin GetAll &7- &eGives all ItemJoin items.");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin GetAll <Player> &7- &eGives all items to said player.");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin RemoveAll &7- &eRemoves all ItemJoin items.");
				ServerHandler.sendCommandsMessage(sender, "&a&l/ItemJoin RemoveAll <Player> &7- &eRemoves from player.");
				ServerHandler.sendCommandsMessage(sender, "&aFound a bug? Report it @");
				ServerHandler.sendCommandsMessage(sender, "&ahttps://github.com/RockinChaos/ItemJoin/issues");
				ServerHandler.sendCommandsMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 4/4 &a&l]&a&l&m---------------[");
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
		} else if (args[0].equalsIgnoreCase("menu") || args[0].equalsIgnoreCase("creator")) {
			// This is a currently unimplemented feature that is currently in development so it is blocked so only the DEV can work on it.
			// This will soon be the items GUI creator that allows you to create items in game for ItemJoin!
			if (sender.getName().equals("RockinChaos")) { // sender.hasPermission("itemjoin.creator") || sender.hasPermission("itemjoin.*")
				if (!(sender instanceof ConsoleCommandSender)) {
					ItemCreator.LaunchCreator(sender);
					//Language.getSendMessage(sender, "creatorlaunched", "");
					return true;
				} else if (sender instanceof ConsoleCommandSender) {
					Language.getSendMessage(sender, "notPlayer", "");
					return true;
				}
			} else {
				//Language.getSendMessage(sender, "noPermission", "");
				Language.getSendMessage(sender, "unknownCommand", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("save") || args[0].equalsIgnoreCase("s")) {
			if (args.length == 2) {
			if (sender.hasPermission("itemjoin.save") || sender.hasPermission("itemjoin.*")) {
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
									enchantList.add(e.getName().toUpperCase() + ":" + level);
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
			if (sender.hasPermission("itemjoin.permissions") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					ServerHandler.sendCommandsMessage(sender, "&a&l&m]------------------&a&l[&e ItemJoin &a&l]&a&l&m-----------------[");
					if (sender.hasPermission("itemjoin.*")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.*");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.*");
					}
					if (sender.hasPermission("itemjoin.all")) {
						ServerHandler.sendCommandsMessage(sender, "&a[\u2714] ItemJoin.All");
					} else {
						ServerHandler.sendCommandsMessage(sender, "&c[\u2718] ItemJoin.All");
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
					for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
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
				Language.setArgsPlayer(sender);
				reAddItem(argsPlayer, sender, args[1]);
				AnimationHandler.OpenAnimations(argsPlayer);
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
			if (sender.hasPermission("itemjoin.get") || sender.hasPermission("itemjoin.*")) {
				Language.getSendMessage(sender, "invalidGetUsage", "");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("getall")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && sender.hasPermission("itemjoin.get.others") || argsPlayer == null && sender.hasPermission("itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[1]);
				return true;
			} else if (sender.hasPermission("itemjoin.get.others") || sender.hasPermission("itemjoin.*")) {
				Language.setArgsPlayer(sender);
				reAddItem(argsPlayer, sender, "00a40gh392bd938d4");
				AnimationHandler.OpenAnimations(argsPlayer);
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("getall")) {
			if (sender.hasPermission("itemjoin.get") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
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
			if (argsPlayer == null && sender.hasPermission("itemjoin.remove.others") || argsPlayer == null && sender.hasPermission("itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[2]);
				return true;
			} else if (sender.hasPermission("itemjoin.remove.others") || sender.hasPermission("itemjoin.*")) {
				Language.setArgsPlayer(sender);
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
		} else if (args.length == 2 && args[0].equalsIgnoreCase("removeall")) {
			Player argsPlayer = PlayerHandler.getPlayerString(args[1]);
			if (argsPlayer == null && sender.hasPermission("itemjoin.remove.others") || argsPlayer == null && sender.hasPermission("itemjoin.*")) {
				Language.getSendMessage(sender, "playerNotFound", args[1]);
				return true;
			} else if (sender.hasPermission("itemjoin.remove.others") || sender.hasPermission("itemjoin.*")) {
				Language.setArgsPlayer(sender);
				removeItem(argsPlayer, sender, "00a40gh392bd938d4");
				return true;
			} else {
				Language.getSendMessage(sender, "noPermission", "");
				return true;
			}
		} else if (args[0].equalsIgnoreCase("removeall")) {
			if (sender.hasPermission("itemjoin.remove") || sender.hasPermission("itemjoin.*")) {
				if (!(sender instanceof ConsoleCommandSender)) {
					removeItem((Player) sender, null, "00a40gh392bd938d4");
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
						if (itemName.equalsIgnoreCase("00a40gh392bd938d4")) {
							if (Utils.isCustomSlot(slot)) {
								ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
								if (!ItemHandler.hasItem(player, inStoredItems)) {
								SetItems.setCustomSlots(player, item, slot, ItemID);
								if (hasRan != true) {
								Language.getSendMessage(player, "givenAllToYou", "&eAll Items");
								hasRan = true;
								if (Language.getArgsPlayer() != null) {
									Language.setArgsPlayer(player);
									Language.getSendMessage(OtherPlayer, "givenAllToPlayer", "&eAll Items");
									Language.setArgsPlayer(null);
								}
								}
								} else if (hasRan != true) {
									hasRan = true;
									if (Language.getArgsPlayer() != null) {
										Language.getSendMessage(player, "playerTriedGiveAllItems", "All Items");
										Language.setArgsPlayer(player);
										Language.getSendMessage(OtherPlayer, "allItemsExistInOthersInventory", "All Items");
										Language.setArgsPlayer(null);
									} else {
										Language.getSendMessage(player, "allItemsExistInInventory", "All Items");
									}
								}
								ItemExists = true;
							} else if (Utils.isInt(slot)) {
								ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
								if (!ItemHandler.hasItem(player, inStoredItems)) {
								SetItems.setInvSlots(player, item, slot, ItemID);
								if (hasRan != true) {
								Language.getSendMessage(player, "givenAllToYou", "&eAll Items");
								hasRan = true;
								if (Language.getArgsPlayer() != null) {
									Language.setArgsPlayer(player);
									Language.getSendMessage(OtherPlayer, "givenAllToPlayer", "&eAll Items");
									Language.setArgsPlayer(null);
								}
								}
								} else if (hasRan != true) {
									hasRan = true;
									if (Language.getArgsPlayer() != null) {
										Language.getSendMessage(player, "playerTriedGiveAllItems", "All Items");
										Language.setArgsPlayer(player);
										Language.getSendMessage(OtherPlayer, "allItemsExistInOthersInventory", "All Items");
										Language.setArgsPlayer(null);
									} else {
										Language.getSendMessage(player, "allItemsExistInInventory", "All Items");
									}
								}
								ItemExists = true;
							}
						} else {
						ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + itemName);
						int dataValue = items.getInt(".data-value");
						Material tempmat = ItemHandler.getMaterial(items);
						ItemStack tempitem = null;
						if (ServerHandler.hasAltUpdate("1_9")) {
							tempitem = new ItemStack(tempmat, items.getInt(".count", 1), (short) dataValue);
						} else if (!ServerHandler.hasAltUpdate("1_9")) {
							try {
								tempitem = new ItemStack(tempmat, items.getInt(".count", 1), (short) dataValue);
							} catch (NullPointerException e) {
								if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
							}
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
							if (Language.getArgsPlayer() != null) {
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "givenToPlayer", name);
								Language.setArgsPlayer(null);
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName) && Utils.isInt(slot) && ItemHandler.isObtainable(player, item, slot, ItemID, inStoredItems)) {
							SetItems.setInvSlots(player, item, slot, ItemID);
							Language.getSendMessage(player, "givenToYou", inStoredItems.getItemMeta().getDisplayName());
							if (Language.getArgsPlayer() != null) {
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "givenToPlayer", name);
								Language.setArgsPlayer(null);
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName) && !ItemHandler.isObtainable(player, item, slot, ItemID, inStoredItems)) {
							if (Language.getArgsPlayer() != null) {
								Language.getSendMessage(player, "playerTriedGive", name);
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "itemExistsInOthersInventory", name);
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
			Language.getSendMessage(player, "itemDoesntExist", itemName);
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
						if (itemName.equalsIgnoreCase("00a40gh392bd938d4")) {
							ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
							if (ItemHandler.hasItem(player, inStoredItems)) {
							   SetItems.setClearItemJoinItems(player);
								if (hasRan != true) {
								Language.getSendMessage(player, "removedAllFromYou", "&eAll Items");
								hasRan = true;
								if (Language.getArgsPlayer() != null) {
									Language.setArgsPlayer(player);
									Language.getSendMessage(OtherPlayer, "removedAllFromPlayer", "&eAll Items");
									Language.setArgsPlayer(null);
								}
								}
								} else if ((hasRan != true)) {
									hasRan = true;
									if (Language.getArgsPlayer() != null) {
										Language.getSendMessage(player, "playerTriedRemoveAll", "All Items");
										Language.setArgsPlayer(player);
										Language.getSendMessage(OtherPlayer, "allItemsDoNotExistInOthersInventory", "Items");
										Language.setArgsPlayer(null);
									} else {
										Language.getSendMessage(player, "allItemsDoNotExistInInventory", "Items");
									}
								}
								ItemExists = true;
						} else {
						ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + itemName);
						int dataValue = items.getInt(".data-value");
						Material tempmat = ItemHandler.getMaterial(items);
						ItemStack tempitem = null;
						if (ServerHandler.hasAltUpdate("1_9")) {
							tempitem = new ItemStack(tempmat, items.getInt(".count", 1), (short) dataValue);
						} else if (!ServerHandler.hasAltUpdate("1_9")) {
							try {
								tempitem = new ItemStack(tempmat, items.getInt(".count", 1), (short) dataValue);
							} catch (NullPointerException e) {
								if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
							}
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
							if (Language.getArgsPlayer() != null) {
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "removedFromPlayer", name);
								Language.setArgsPlayer(null);
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName) && Utils.isInt(slot) && player.getInventory().contains(inStoredItems)) {
							player.getInventory().removeItem(inStoredItems);
							Language.getSendMessage(player, "removedFromYou", name);
							if (Language.getArgsPlayer() != null) {
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "removedFromPlayer", name);
								Language.setArgsPlayer(null);
							}
							ItemExists = true;
						} else if (inStoredItems != null && item.equalsIgnoreCase(itemName)) {
							if (Language.getArgsPlayer() != null) {
								Language.getSendMessage(player, "playerTriedRemove", inStoredItems.getItemMeta().getDisplayName());
								Language.setArgsPlayer(player);
								Language.getSendMessage(OtherPlayer, "itemDoesntExistInOthersInventory", name);
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
			Language.getSendMessage(player, "itemDoesntExist", itemName);
		}
		}
	}
	}