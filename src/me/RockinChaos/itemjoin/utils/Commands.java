package me.RockinChaos.itemjoin.utils;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.*;

import me.RockinChaos.itemjoin.ItemJoin;

public class Commands implements CommandExecutor
{

    public static String noPermMSG;
    public static String notPlayer;
    
    static {
        Commands.noPermMSG = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.DARK_RED + "You don't have permission to use that command!";
        Commands.notPlayer = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.DARK_RED + "You must be a player to use that command";
    }
    
// Player Commands //
	
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	final Player player = (Player) sender;
        if (args.length == 0) {
        	if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
                sender.sendMessage(ChatColor.GREEN + "ItemJoin v." + ItemJoin.pl.getDescription().getVersion() + ChatColor.YELLOW + " by RockinChaos");
                sender.sendMessage(ChatColor.GREEN + "Type" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + " /ItemJoin Help " + ChatColor.GREEN + "for the help menu.");
                return true;
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("help") || args.length == 1 && args[0].equalsIgnoreCase("h")) {
        	if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]--------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " ItemJoin " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "--------------[");
                sender.sendMessage(ChatColor.GREEN + "ItemJoin v." + ItemJoin.pl.getDescription().getVersion() + ChatColor.YELLOW + " by RockinChaos");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Help" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "This help menu.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Reload" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Reloads the .yml files.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Loaded" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Lists the loaded worlds for ItemJoin.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Updates" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Checks for plugin updates.");
                sender.sendMessage(ChatColor.GREEN + "Type" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + " /ItemJoin Help 2 " + ChatColor.GREEN + "for the next page.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " Help Menu 1/2 " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "------------[");
                sender.sendMessage("");
                return true;
        } else {
            sender.sendMessage(noPermMSG);
            return true;
        }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("help") && args[1].equalsIgnoreCase("2") || args.length == 2 && args[0].equalsIgnoreCase("h") && args[1].equalsIgnoreCase("2")) {
        	if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]--------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " ItemJoin " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "--------------[");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Permissions" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Lists the permissions you have.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Permissions 2" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Permissions page 2.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Get <Slot>" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Gives that item.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Get <Slot> <Player>" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Gives to said player.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin World" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Check what world you are in. (debugging).");
                sender.sendMessage(ChatColor.GREEN + "Found a bug? Report it @");
                sender.sendMessage(ChatColor.GREEN + "http://dev.bukkit.org/bukkit-plugins/itemjoin/");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " Help Menu 2/2 " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "------------[");
                sender.sendMessage("");
                return true;
        } else {
            sender.sendMessage(noPermMSG);
            return true;
        }
        } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
        	if (sender.hasPermission("itemjoin.reload") || sender.hasPermission("itemjoin.*")) {
        		ItemJoin.pl.loadSpecialConfig("items.yml");
        		ItemJoin.pl.getSpecialConfig("items.yml").options().copyDefaults(false);
        	    ItemJoin.pl.saveDefaultConfig();
        	  	ItemJoin.pl.getConfig().options().copyDefaults(false);
                ItemJoin.pl.reloadConfig();
           	    ItemJoin.pl.PlayerJoin = player;
        	    ItemJoin.pl.PlayerJoin2 = player.getName();
                ItemJoin.pl.CacheItems();
                if (ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled") == true) {
          		  ItemJoin.pl.loadSpecialConfig("firstJoin.yml");
          		  ItemJoin.pl.getSpecialConfig("firstJoin.yml").options().copyDefaults(false);
          		  }
  		        ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + sender.getName() + " has reloaded the Configuration files.");
  		        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Configuration Reloaded!");
                for (int i = 0; i < ItemJoin.pl.worlds.size(); i++)
                {
                  String world = (String)ItemJoin.pl.worlds.get(i).toLowerCase();
                  sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Cached " + ChatColor.YELLOW + world + ChatColor.GREEN + " from the items.yml!");
                }
                return true;
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else if (args[0].equalsIgnoreCase("loaded") || args[0].equalsIgnoreCase("l")) {
        	if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
                sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Loaded worlds:");
                for (int i = 0; i < ItemJoin.pl.worlds.size(); i++)
                {
                  String world = (String)ItemJoin.pl.worlds.get(i).toLowerCase();
                  sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Cached " + ChatColor.YELLOW + world + ChatColor.GREEN + " from the items.yml!");
                }
                return true;
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else if (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds") || args[0].equalsIgnoreCase("w")) {
        	if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
                sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "You are in the world:");
                sender.sendMessage(ChatColor.GREEN + "- " + player.getWorld().getName().toLowerCase());
                return true;
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("permissions") || args.length == 1 && args[0].equalsIgnoreCase("perm") || args.length == 1 && args[0].equalsIgnoreCase("perms")) {
        	if (sender.hasPermission("itemjoin.permissions") || sender.hasPermission("itemjoin.*")) {
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]--------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " ItemJoin " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "--------------[");
        		if (sender.hasPermission("itemjoin.*")){
                	sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.*");
                } else {
                	sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.*");
                }
                if (sender.hasPermission("itemjoin.use")){
                	sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.Use");
                } else {
                	sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.Use");
                }
                if (sender.hasPermission("itemjoin.reload")){
                	sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.Reload");
                } else {
                	sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.Reload");
                }
                if (sender.hasPermission("itemjoin.updates")){
                	sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.Updates");
                } else {
                	sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.Updates");
                }
                if (sender.hasPermission("itemjoin.get")){
                	sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.get");
                } else {
                	sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.get");
                }
                if (sender.hasPermission("itemjoin.get.others")){
                	sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.get.others");
                } else {
                	sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.get.others");
                }
        		if (sender.hasPermission("itemjoin.permissions")){
                	sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin.permissions");
                } else {
                	sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin.permissions");
                }
                for (int i = 0; i < ItemJoin.pl.worlds.size(); i++)
                {
                  String world = (String)ItemJoin.pl.worlds.get(i).toLowerCase();
                  if (sender.hasPermission("itemjoin."+ world + ".*")){
                  	sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin."+ world + ".*");
                  } else {
                  	sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin."+ world + ".*");
                  }
                }
                sender.sendMessage(ChatColor.GREEN.toString() + "Type /ItemJoin Permissions 2 for the next page.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " Permissions Menu 1/2 " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "------------[");
                return true;
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("permissions") && args[1].equalsIgnoreCase("2") || args.length == 2 && args[0].equalsIgnoreCase("perm") && args[1].equalsIgnoreCase("2") || args.length == 2 && args[0].equalsIgnoreCase("perms") && args[1].equalsIgnoreCase("2")) {
        	if (sender.hasPermission("itemjoin.permissions") || sender.hasPermission("itemjoin.*")) {
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]--------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " ItemJoin " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "--------------[");
                for (int i = 0; i < ItemJoin.pl.worlds.size(); i++)
                {
                  String world = (String)ItemJoin.pl.worlds.get(i).toLowerCase();
                  ItemStack[] toSet = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
                  for (int z = 1; z < 104; z++)
                  {
                  if (sender.hasPermission("itemjoin."+ world + "." + z) && toSet[z - 1] != null) {
                  	sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin."+ world + "." + z);
                  } else if (toSet[z - 1] != null) {
                  	sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin."+ world + "." + z);
                  }
                }
               }
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " Permissions Menu 2/2 " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "------------[");
                return true;
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("get") && ItemJoin.isInt(args[1])) {
        	Player argsPlayer = Bukkit.getPlayerExact(args[2]);
        	if (argsPlayer == null && sender.hasPermission("itemjoin.get.others") || argsPlayer == null && sender.hasPermission("itemjoin.*")) {
        		sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "The player " + ChatColor.AQUA + args[2] + ChatColor.RED + " could not be found!");
        		return true;
        	} else if (sender.hasPermission("itemjoin.get.others") || sender.hasPermission("itemjoin.*")) {
        		String world = argsPlayer.getWorld().getName().toLowerCase();
        		ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + argsPlayer.getName());
        		int id = Integer.parseInt(args[1]);
        		if (id >= 1 && id <= 38 && items[id - 1] != null) {
        		argsPlayer.getInventory().setItem(id - 1, items[id - 1]);
        		sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "You gave " + argsPlayer.getName() + " [" + items[id - 1].getItemMeta().getDisplayName() + ChatColor.GREEN + "].");
				argsPlayer.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + sender.getName() + " gave you " + "[" + items[id - 1].getItemMeta().getDisplayName() + ChatColor.GREEN + "].");
        		} else if (id >= 100 && id <= 103 && items[id - 1] != null) {
					if (id == 100) {
   		        	 argsPlayer.getEquipment().setBoots(items[id - 1]);
					} else if (id == 101) {
	    		     argsPlayer.getEquipment().setLeggings(items[id - 1]);
					} else if (id == 102) {
	 	    		 argsPlayer.getEquipment().setChestplate(items[id - 1]);
	    		    } else if (id == 103) {
	 	    		 argsPlayer.getEquipment().setHelmet(items[id - 1]);
	 	    		}
					sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "You gave " + argsPlayer.getName() + " [" + items[id - 1].getItemMeta().getDisplayName() + ChatColor.GREEN + "].");
					argsPlayer.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + sender.getName() + " gave you " + "[" + items[id - 1].getItemMeta().getDisplayName() + ChatColor.GREEN + "].");
        		} else if (id >= 1 && id <= 36 && items[id - 1] == null || id >= 100 && id <= 103 && items[id - 1] == null) {
            		sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "There is no item defined for " + ChatColor.AQUA + "Slot " + id + "!");
        		} else {
        			sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.AQUA + "Slot " + id + ChatColor.RED + " Is an invalid slot number!");
        			sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Please only use slots 1 - 36 and 100 - 103.");
        		}
        		return true;
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("get") && ItemJoin.isInt(args[1])) {
        	if (sender.hasPermission("itemjoin.get") || sender.hasPermission("itemjoin.*")) {
        		String world = player.getWorld().getName().toLowerCase();
        		ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + player.getName());
        		int id = Integer.parseInt(args[1]);
        		if (id >= 1 && id <= 38 && items[id - 1] != null) {
        		player.getInventory().setItem(id - 1, items[id - 1]);
        		sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "You were given " + "[" + items[id - 1].getItemMeta().getDisplayName() + ChatColor.GREEN + "].");
        		} else if (id >= 100 && id <= 103 && items[id - 1] != null) {
					if (id == 100) {
   		        	 player.getEquipment().setBoots(items[id - 1]);
					} else if (id == 101) {
	    		     player.getEquipment().setLeggings(items[id - 1]);
					} else if (id == 102) {
	 	    		 player.getEquipment().setChestplate(items[id - 1]);
	    		    } else if (id == 103) {
	 	    		 player.getEquipment().setHelmet(items[id - 1]);
	 	    		}
					sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "You were given " + "[" + items[id - 1].getItemMeta().getDisplayName() + ChatColor.GREEN + "].");
        		} else if (id >= 1 && id <= 36 && items[id - 1] == null || id >= 100 && id <= 103 && items[id - 1] == null) {
            		sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "There is no item defined for " + ChatColor.AQUA + "Slot " + id + "!");
        		} else {
        			sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.AQUA + "Slot " + id + ChatColor.RED + " Is an invalid slot number!");
        			sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Please only use slots 1 - 36 and 100 - 103.");
        		}
        		return true;
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else if (args[0].equalsIgnoreCase("updates") || args[0].equalsIgnoreCase("update")) {
        	if (sender.hasPermission("itemjoin.updates") || sender.hasPermission("itemjoin.*")) {
        		UpdateChecker checker = new UpdateChecker(ItemJoin.pl, "http://dev.bukkit.org/server-mods/itemjoin/files.rss");
  		        ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + sender.getName() + " has requested to check for updates!");
  	            sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Checking for updates...");
                if (checker.updateNeeded())
                  {
              	  sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Your current version: v" + ChatColor.RED + ItemJoin.pl.getDescription().getVersion());
              	  sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "A new version of ItemJoin is available: " + ChatColor.GREEN + "v" +  checker.getVersion() + ChatColor.WHITE);
              	  sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Get it from: " + checker.getLink() + ChatColor.WHITE);
              	  sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Direct Link: " + checker.getJarLink() + ChatColor.WHITE);
              	return true;
                    }
                else if(ItemJoin.pl.getConfig().getBoolean("CheckforUpdates") == true) {
              	  sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "You are up to date!");
              	return true;
                }
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.DARK_RED + ChatColor.RED + "Unknown command!");
            return true;
        }
        return false;
    }
}