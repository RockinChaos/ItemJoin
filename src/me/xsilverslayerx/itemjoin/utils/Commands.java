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
        	if (sender.hasPermission("use")) {
                sender.sendMessage(ChatColor.GREEN + "ItemJoin v" + ChatColor.YELLOW + ItemJoin.pl.getDescription().getVersion() + " by XSilverSlayerX");
                sender.sendMessage(ChatColor.GREEN + "Type /itemjoin help for the help menu.");
                return true;
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
        	if (sender.hasPermission("use")) {
                sender.sendMessage(ChatColor.GREEN + "<>==<>==<>==<>" + ChatColor.YELLOW + "     ItemJoin     "  + ChatColor.GREEN + "<>==<>==<>==<>");
                sender.sendMessage(ChatColor.GREEN + "ItemJoin v." + ChatColor.YELLOW + ItemJoin.pl.getDescription().getVersion() + " by XSilverSlayerX");
                sender.sendMessage(ChatColor.GREEN + "/itemjoin help" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "This help menu.");
                sender.sendMessage(ChatColor.GREEN + "/itemjoin reload" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Reloads the .yml files.");
                sender.sendMessage(ChatColor.GREEN + "/itemjoin updates" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Checks for plugin updates.");
                sender.sendMessage(ChatColor.GREEN + "Found a bug? Report it @ http://dev.bukkit.org/bukkit-plugins/itemjoin/");
                sender.sendMessage(ChatColor.GREEN + "<>==<>==<>==<>" + ChatColor.YELLOW + "    Help Menu    "  + ChatColor.GREEN + "<>==<>==<>==<>");
                return true;
            }
        } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
        	if (sender.hasPermission("reload")) {
                ItemJoin.pl.reloadConfig(); // Reloads config.yml //
                ItemJoin.pl.loadItemsConfigSetup(); // Reloads items.yml //
                ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + "Configuration Reloaded!");
                sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Configuration Reloaded!");
                return true;
            } else {
                sender.sendMessage(noPermMSG);
                return true;
            }
        } else if (args[0].equalsIgnoreCase("updates") || args[0].equalsIgnoreCase("update")) {
        	if (sender.hasPermission("updates")) {
        		UpdateChecker checker = new UpdateChecker(ItemJoin.pl, "http://dev.bukkit.org/server-mods/itemjoin/files.rss");
  		        ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + sender.getName() + " has requested to check for updates!");
  	            sender.sendMessage(ChatColor.GREEN + "Checking for updates...");
                if (checker.updateNeeded())
                  {
          	      sender.sendMessage("");
              	  sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Your current version: v" + ChatColor.RED + ItemJoin.pl.getDescription().getVersion());
              	  sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "A new version of ItemJoin is available: v" + ChatColor.GREEN +  checker.getVersion() + ChatColor.WHITE);
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