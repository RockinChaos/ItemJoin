package me.xsilverslayerx.itemjoin.utils;

import me.xsilverslayerx.itemjoin.ItemJoin;

import org.bukkit.command.*;
import org.bukkit.*;

public class Commands implements CommandExecutor
{
    
// Player Commands //
	
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
            if (args.length == 0) {
                if (!sender.hasPermission("use"))
                {
                        sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
                        return true;
                }
                sender.sendMessage(ChatColor.GREEN + "ItemJoin v" + ChatColor.YELLOW + ItemJoin.pl.getDescription().getVersion() + " by XSilverSlayerX");
                sender.sendMessage(ChatColor.GREEN + "Type /itemjoin help for the help menu.");
                    return true;
            }
            else if (args.length == 1) {
            	if ((args[0].equalsIgnoreCase("reload")) || (args[0].equalsIgnoreCase("r")))
            	{
                            if (!sender.hasPermission("reload"))
                            {
                                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
                                    return true;
                            }
                            ItemJoin.pl.reloadConfig(); // Reloads config.yml //
                            ItemJoin.pl.loadItemsConfigSetup(); // Reloads items.yml //
                            ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + "Configuration Reloaded!");
                            sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Configuration Reloaded!");
                            return true;
                    }
                else if (args.length == 1) {
                	if ((args[0].equalsIgnoreCase("help")) || (args[0].equalsIgnoreCase("h")))
                                {
                        if (!sender.hasPermission("use"))
                        {
                                sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
                                return true;
                        }
                                    sender.sendMessage(ChatColor.GREEN + "<>==<>==<>==<>" + ChatColor.YELLOW + "  ItemJoin Help Menu 1/1  "  + ChatColor.GREEN + "<>==<>==<>==<>");
                                    sender.sendMessage(ChatColor.GREEN + "ItemJoin v." + ChatColor.YELLOW + ItemJoin.pl.getDescription().getVersion() + " by XSilverSlayerX");
                                    sender.sendMessage(ChatColor.GREEN + "/itemjoin help" + ChatColor.RED + " - " + ChatColor.YELLOW + "This help menu.");
                                    sender.sendMessage(ChatColor.GREEN + "/itemjoin reload" + ChatColor.RED + " - " + ChatColor.YELLOW + "Reloads the .yml files.");
                                    sender.sendMessage(ChatColor.GREEN + "/itemjoin updates" + ChatColor.RED + " - " + ChatColor.YELLOW + "Checks for plugin updates.");
                                    sender.sendMessage(ChatColor.GREEN + "Found a bug? Report it @ http://dev.bukkit.org/bukkit-plugins/itemjoin/");
                                    sender.sendMessage(ChatColor.GREEN + "<>==<>==<>==<>" + ChatColor.YELLOW + "  ItemJoin Help Menu 1/1  "  + ChatColor.GREEN + "<>==<>==<>==<>");
                                    return true;
            }
                    else if (args.length == 1) {
                    	if ((args[0].equalsIgnoreCase("update")) || (args[0].equalsIgnoreCase("updates")))
                                    {
                            if (!sender.hasPermission("updates"))
                            {
                                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
                                    return true;
                            }
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
                                	  }
                               	}
                           }
                }
	return false;
    }
}