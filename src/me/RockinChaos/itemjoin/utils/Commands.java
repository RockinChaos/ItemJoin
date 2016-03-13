package me.RockinChaos.itemjoin.utils;

import java.util.List;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.*;

import me.RockinChaos.itemjoin.ItemJoin;

public class Commands implements CommandExecutor
{

	public static String Prefix;
    public static String noPermission;
    public static String notPlayer;
    public static String consoleAltSyntax;
    public static String unknownCommand;
    public static String badGetUsage;
    public static String givenItem;
    public static String givenOthersItem;
    public static String receivedOthersItem;
    public static String badSlot1;
    public static String badSlot2;
    public static String slotUndefined;
    public static String consoleReloadedConfig;
    public static String reloadedConfig;
    public static String cachedWorlds;
    public static String loadedWorlds;
    public static String loadedWorldsListed;
    public static String worldIn;
    public static String worldInListed;

 // Player Commands //
	
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
        	if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
                sender.sendMessage(ChatColor.GREEN + "ItemJoin v." + ItemJoin.pl.getDescription().getVersion() + ChatColor.YELLOW + " by RockinChaos");
                sender.sendMessage(ChatColor.GREEN + "Type" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + " /ItemJoin Help " + ChatColor.GREEN + "for the help menu.");
                return true;
            } else {
                sender.sendMessage(noPermission);
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
            sender.sendMessage(noPermission);
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
            sender.sendMessage(noPermission);
            return true;
        }
        } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
        	if (sender.hasPermission("itemjoin.reload") || sender.hasPermission("itemjoin.*")) {
                ItemJoin.pl.worlds = ItemJoin.getSpecialConfig("items.yml").getStringList("world-list");
        		ItemJoin.loadSpecialConfig("items.yml");
        		ItemJoin.getSpecialConfig("items.yml").options().copyDefaults(false);
        	    ItemJoin.pl.saveDefaultConfig();
        	  	ItemJoin.pl.getConfig().options().copyDefaults(false);
                ItemJoin.pl.reloadConfig();
          	     if (ItemJoin.pl.getConfig().getString("Language").equalsIgnoreCase("English")) {
          	       ItemJoin.loadSpecialConfig("en-lang.yml");
          	       ItemJoin.getSpecialConfig("en-lang.yml").options().copyDefaults(false);
          	       RegisterEnLang();
          		  }
                if(!(sender instanceof ConsoleCommandSender)) { 
           	      ItemJoin.pl.PlayerJoin = (Player) sender;
        	      ItemJoin.pl.PlayerJoin2 = ((Player)sender).getName();
                }
                ItemJoin.pl.CacheItems();
                if (ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled") == true) {
          		  ItemJoin.loadSpecialConfig("firstJoin.yml");
          		  ItemJoin.getSpecialConfig("firstJoin.yml").options().copyDefaults(false);
          		  }
  		        ItemJoin.pl.getServer().getConsoleSender().sendMessage(consoleReloadedConfig.replace("%player_reloaded%", sender.getName()));
  		        sender.sendMessage(reloadedConfig);
  		         List<String> PrintWorlds = ItemJoin.getSpecialConfig("items.yml").getStringList("world-list");
  			     for (int i = 0; i < PrintWorlds.size(); i++)
  			     {
  			       String world = (String)PrintWorlds.get(i);
  			       sender.sendMessage(cachedWorlds.replace("%cache_world%", world));
  			     }
                return true;
            } else {
                sender.sendMessage(noPermission);
                return true;
            }
        } else if (args[0].equalsIgnoreCase("loaded") || args[0].equalsIgnoreCase("l")) {
        	if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
                sender.sendMessage(loadedWorlds);
 		         List<String> PrintWorlds = ItemJoin.getSpecialConfig("items.yml").getStringList("world-list");
 			     for (int i = 0; i < PrintWorlds.size(); i++)
 			     {
 			       String world = (String)PrintWorlds.get(i);
 			       sender.sendMessage(loadedWorldsListed.replace("%loaded_worlds%", world));
 			     }
                return true;
            } else {
                sender.sendMessage(noPermission);
                return true;
            }
        } else if (args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds") || args[0].equalsIgnoreCase("w")) {
        	if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
        		if(!(sender instanceof ConsoleCommandSender)) { 
                sender.sendMessage(worldIn);
                sender.sendMessage(worldInListed.replace("%in_worlds%", ((Player) sender).getWorld().getName()));
                return true;
        		} else if(sender instanceof ConsoleCommandSender) { 
        			sender.sendMessage(notPlayer);
        			return true;
        		}
            } else {
                sender.sendMessage(noPermission);
                return true;
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("permissions") || args.length == 1 && args[0].equalsIgnoreCase("perm") || args.length == 1 && args[0].equalsIgnoreCase("perms")) {
        	if (sender.hasPermission("itemjoin.permissions") || sender.hasPermission("itemjoin.*")) {
        		if(!(sender instanceof ConsoleCommandSender)) { 
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
                  String world = (String)ItemJoin.pl.worlds.get(i);
                  if (sender.hasPermission("itemjoin."+ world + ".*")){
                  	sender.sendMessage(ChatColor.GREEN.toString() + "[\u2714] ItemJoin."+ world + ".*");
                  } else {
                  	sender.sendMessage(ChatColor.RED.toString() + "[\u2718] ItemJoin."+ world + ".*");
                  }
                }
                sender.sendMessage(ChatColor.GREEN.toString() + "Type /ItemJoin Permissions 2 for the next page.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " Permissions Menu 1/2 " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "------------[");
                return true;
    		} else if(sender instanceof ConsoleCommandSender) { 
    			sender.sendMessage(notPlayer);
    			return true;
    		}
            } else {
                sender.sendMessage(noPermission);
                return true;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("permissions") && args[1].equalsIgnoreCase("2") || args.length == 2 && args[0].equalsIgnoreCase("perm") && args[1].equalsIgnoreCase("2") || args.length == 2 && args[0].equalsIgnoreCase("perms") && args[1].equalsIgnoreCase("2")) {
        	if (sender.hasPermission("itemjoin.permissions") || sender.hasPermission("itemjoin.*")) {
        		if(!(sender instanceof ConsoleCommandSender)) { 
        		sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]--------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " ItemJoin " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "--------------[");
                for (int i = 0; i < ItemJoin.pl.worlds.size(); i++)
                {
                  String world = (String)ItemJoin.pl.worlds.get(i);
                  ItemStack[] toSet = (ItemStack[])ItemJoin.pl.items.get(world + ((Player)sender).getName());
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
    		} else if(sender instanceof ConsoleCommandSender) { 
    			sender.sendMessage(notPlayer);
    			return true;
    		}
            } else {
                sender.sendMessage(noPermission);
                return true;
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("get") && ItemJoin.isInt(args[1])) {
        	Player argsPlayer = Bukkit.getPlayerExact(args[2]);
        	if (argsPlayer == null && sender.hasPermission("itemjoin.get.others") || argsPlayer == null && sender.hasPermission("itemjoin.*")) {
        		sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "The player " + ChatColor.AQUA + args[2] + ChatColor.RED + " could not be found!");
        		return true;
        	} else if (sender.hasPermission("itemjoin.get.others") || sender.hasPermission("itemjoin.*")) {
        		String world = argsPlayer.getWorld().getName();
        		ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + argsPlayer.getName());
        		int id = Integer.parseInt(args[1]);
        		if (id >= 1 && id <= 38 && items[id - 1] != null) {
        		argsPlayer.getInventory().setItem(id - 1, items[id - 1]);
        		sender.sendMessage(givenOthersItem.replace("%given_item%", items[id - 1].getItemMeta().getDisplayName()).replace("%given_player%", argsPlayer.getName()));
        		argsPlayer.sendMessage(receivedOthersItem.replace("%received_item%", items[id - 1].getItemMeta().getDisplayName()).replace("%received_player%", sender.getName()));
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
	        		sender.sendMessage(givenOthersItem.replace("%given_item%", items[id - 1].getItemMeta().getDisplayName()).replace("%given_player%", argsPlayer.getName()));
	        		argsPlayer.sendMessage(receivedOthersItem.replace("%received_item%", items[id - 1].getItemMeta().getDisplayName()).replace("%received_player%", sender.getName()));
        		} else if (id >= 1 && id <= 36 && items[id - 1] == null || id >= 100 && id <= 103 && items[id - 1] == null) {
            		sender.sendMessage(slotUndefined.replace("%bad_slot%", args[1]));
        		} else {
        			sender.sendMessage(badSlot1.replace("%bad_slot%", args[1]));
        			sender.sendMessage(badSlot2.replace("%bad_slot%", args[1]));
        		}
        		return true;
            } else {
                sender.sendMessage(noPermission);
                return true;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("get") && ItemJoin.isInt(args[1])) {
        	if (sender.hasPermission("itemjoin.get") || sender.hasPermission("itemjoin.*")) {
    		if(!(sender instanceof ConsoleCommandSender)) { 
        		String world = ((Player)sender).getWorld().getName();
        		ItemStack[] items = (ItemStack[])ItemJoin.pl.items.get(world + ((Player)sender).getName());
        		int id = Integer.parseInt(args[1]);
        		if (id >= 1 && id <= 38 && items[id - 1] != null) {
        			((Player)sender).getInventory().setItem(id - 1, items[id - 1]);
        			sender.sendMessage(givenItem.replace("%given_item%", items[id - 1].getItemMeta().getDisplayName()));
        		} else if (id >= 100 && id <= 103 && items[id - 1] != null) {
					if (id == 100) {
						((Player)sender).getEquipment().setBoots(items[id - 1]);
					} else if (id == 101) {
						((Player)sender).getEquipment().setLeggings(items[id - 1]);
					} else if (id == 102) {
						((Player)sender).getEquipment().setChestplate(items[id - 1]);
	    		    } else if (id == 103) {
	    		    	((Player)sender).getEquipment().setHelmet(items[id - 1]);
	 	    		}
					sender.sendMessage(givenItem.replace("%given_item%", items[id - 1].getItemMeta().getDisplayName()));
        		} else if (id >= 1 && id <= 36 && items[id - 1] == null || id >= 100 && id <= 103 && items[id - 1] == null) {
            		sender.sendMessage(slotUndefined.replace("%bad_slot%", args[1]));
        		} else {
        			sender.sendMessage(badSlot1.replace("%bad_slot%", args[1]));
        			sender.sendMessage(badSlot2.replace("%bad_slot%", args[1]));
        		}
        		return true;
    		} else if(sender instanceof ConsoleCommandSender) { 
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
    
    public static void RegisterEnLang() {
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("Prefix") != null) {
        	Commands.Prefix = ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("Prefix"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("Prefix") == null) {
        	Commands.Prefix = "";
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("noPermission") != null) {
            Commands.noPermission = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("noPermission"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("notPlayer") != null) {
            Commands.notPlayer = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("notPlayer"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("consoleAltSyntax") != null) {
            Commands.consoleAltSyntax = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("consoleAltSyntax"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("unknownCommand") != null) {
            Commands.unknownCommand = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("unknownCommand"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("badGetUsage") != null) {
            Commands.badGetUsage = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("badGetUsage"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("givenItem") != null) {
            Commands.givenItem = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("givenItem"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("givenOthersItem") != null) {
            Commands.givenOthersItem = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("givenOthersItem"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("receivedOthersItem") != null) {
            Commands.receivedOthersItem = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("receivedOthersItem"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("badSlot1") != null) {
            Commands.badSlot1 = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("badSlot1"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("badSlot2") != null) {
            Commands.badSlot2 = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("badSlot2"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("slotUndefined") != null) {
            Commands.slotUndefined = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("slotUndefined"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("consoleReloadedConfig") != null) {
            Commands.consoleReloadedConfig = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("consoleReloadedConfig"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("reloadedConfig") != null) {
            Commands.reloadedConfig = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("reloadedConfig"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("cachedWorlds") != null) {
            Commands.cachedWorlds = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("cachedWorlds"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("loadedWorlds") != null) {
            Commands.loadedWorlds = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("loadedWorlds"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("loadedWorldsListed") != null) {
            Commands.loadedWorldsListed = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("loadedWorldsListed"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("worldIn") != null) {
            Commands.worldIn = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("worldIn"));
            }
            if (ItemJoin.getSpecialConfig("en-lang.yml").getString("worldInListed") != null) {
            Commands.worldInListed = Prefix + ItemJoin.pl.translateCodes(ItemJoin.getSpecialConfig("en-lang.yml").getString("worldInListed"));
        }
    }
}