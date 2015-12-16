package me.xsilverslayerx.itemjoin.utils;

import org.bukkit.command.*;
import org.bukkit.*;

import me.xsilverslayerx.itemjoin.ItemJoin;

public class Commands implements CommandExecutor
{

    public static String noPermMSG;
    public static String notPlayer;
    
    static {
        Commands.noPermMSG = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.DARK_RED + "You don't have permission to use that command!";
        Commands.notPlayer = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.DARK_RED + "You must be a player to use that command";
    }
    
// Player Commands //
	
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
        	if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
                sender.sendMessage(ChatColor.GREEN + "ItemJoin v." + ItemJoin.pl.getDescription().getVersion() + ChatColor.YELLOW + " by XSilverSlayerX");
                sender.sendMessage(ChatColor.GREEN + "Type" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + " /ItemJoin Help " + ChatColor.GREEN + "for the help menu.");
                return true;
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
        	if (sender.hasPermission("itemjoin.use") || sender.hasPermission("itemjoin.*")) {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]--------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " ItemJoin " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "--------------[");
                sender.sendMessage(ChatColor.GREEN + "ItemJoin v." + ItemJoin.pl.getDescription().getVersion() + ChatColor.YELLOW + " by XSilverSlayerX");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Help" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "This help menu.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Reload" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Reloads the .yml files.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Loaded" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Lists the loaded worlds for ItemJoin.");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "/ItemJoin Updates" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Checks for plugin updates.");
                sender.sendMessage(ChatColor.GREEN + "Found a bug? Report it @");
                sender.sendMessage(ChatColor.GREEN + "http://dev.bukkit.org/bukkit-plugins/itemjoin/");
                sender.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "]------------" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "[" + ChatColor.YELLOW + " Help Menu 1/1 " + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "]" + ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH.toString() + "------------[");
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
                ItemJoin.pl.loadItemsConfigSetup();
          	  if(ItemJoin.pl.getConfig().getBoolean("First-Join-Only") == true) {
          		  ItemJoin.pl.loadSpecialConfig("firstJoin.yml");
          		  ItemJoin.pl.getSpecialConfig("firstJoin.yml").options().copyDefaults(false);
          		  }
  		        ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + sender.getName() + " has reloaded the Configuration files.");
  		        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Configuration Reloaded!");
                for (int i = 0; i < ItemJoin.pl.worlds.size(); i++)
                {
                  String world = (String)ItemJoin.pl.worlds.get(i);
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
                  String world = (String)ItemJoin.pl.worlds.get(i);
                  sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Cached " + ChatColor.YELLOW + world + ChatColor.GREEN + " from the items.yml!");
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