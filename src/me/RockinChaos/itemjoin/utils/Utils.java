package me.RockinChaos.itemjoin.utils;

import java.io.File;

import me.RockinChaos.itemjoin.ItemJoin;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Utils
extends JavaPlugin
{

	public static void checkHooks() {
	  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && ItemJoin.pl.getConfig().getBoolean("PlaceholderAPI") == true) {
		  ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Hooked into PlaceholderAPI!");
    	  ItemJoin.hasPlaceholderAPI = true;
		 } else if (ItemJoin.pl.getConfig().getBoolean("PlaceholderAPI") == true) {
	     ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Could not find PlaceholderAPI.");
		 ItemJoin.hasPlaceholderAPI = false;
		 }
	  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("Multiverse-Core") != null && ItemJoin.pl.getConfig().getBoolean("Multiverse-Core") == true) {
		  ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Hooked into Multiverse-Core!");
    	  ItemJoin.hasMultiverse = true;
      } else if (ItemJoin.pl.getConfig().getBoolean("Multiverse-Core") == true) {
    	  ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Could not find Multiverse-Core.");
    	  ItemJoin.hasMultiverse = false;
      }
	  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("Multiverse-Inventories") != null && ItemJoin.pl.getConfig().getBoolean("Multiverse-Inventories") == true) {
		  ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Hooked into Multiverse-Inventories!");
    	  ItemJoin.hasInventories = true;
      } else if (ItemJoin.pl.getConfig().getBoolean("Multiverse-Inventories") == true) {
    	  ItemJoin.hasInventories = false;
    	  ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Could not find Multiverse-Inventories.");
      }
	}
	
	public static void Worlds() {
	     for (int i = 0; i < ItemJoin.pl.worlds.size(); i++)
	     {
	       String world = (String)ItemJoin.pl.worlds.get(i);
	       ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Cached " + ChatColor.YELLOW + world);
	     }
	}
	   public static void configFile() {
		  	  ItemJoin.pl.saveDefaultConfig();
		  	ItemJoin.pl.getConfig().options().copyDefaults(false);
		}
	   
	   public static void itemsFile() {
			  ItemJoin.loadSpecialConfig("items.yml");
			  ItemJoin.getSpecialConfig("items.yml").options().copyDefaults(false);
			  ItemJoin.pl.worlds = ItemJoin.getSpecialConfig("items.yml").getStringList("world-list");
		}
	   
	   public static void firstJoinFile() {
			  if (ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled") == true) {
				  ItemJoin.loadSpecialConfig("FirstJoin.yml");
				  ItemJoin.getSpecialConfig("FirstJoin.yml").options().copyDefaults(false);
				  }
		}
	   
	   public static void enLangFile() {
		      File enLang = new File(ItemJoin.pl.getDataFolder(), "en-lang.yml");
		      if (enLang.exists() && ItemJoin.pl.getConfig().getString("Language").equalsIgnoreCase("English") && ItemJoin.getSpecialConfig("en-lang.yml").getInt("en-Version") != 1) {
		      if (ItemJoin.pl.getResource("en-lang.yml") != null) {
		        String newGen = "en-lang" + ItemJoin.getRandom(1500000,10000000) + ".yml";
		        File newFile = new File(ItemJoin.pl.getDataFolder(), newGen);
		           if (!newFile.exists()) {
		    	      enLang.renameTo(newFile);
		              File configFile = new File(ItemJoin.pl.getDataFolder(), "en-lang.yml");
		              configFile.delete();
		              ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "You are using an outdated or bad en-lang!");
		       	   ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "New options may be avaliable, Generating a new one!");
		           }
		        }
		      }
			  if (ItemJoin.pl.getConfig().getString("Language").equalsIgnoreCase("English")) {
				  ItemJoin.loadSpecialConfig("en-lang.yml");
				  ItemJoin.getSpecialConfig("en-lang.yml").options().copyDefaults(false);
			  }
		}
}