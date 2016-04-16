package me.RockinChaos.itemjoin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.ItemJoin;

public class WorldHandler {

	public static boolean isWorld(String world) {	
		 boolean isWorld = false;
	     for (String worlds : ItemJoin.pl.worlds) {
		   if (worlds.equalsIgnoreCase(world)) {
			 isWorld = true;
		   }
	    }
		return isWorld; 
	}
		public static String getWorld(String world) {	
		 String value = world;
		 for (String key : ItemJoin.getSpecialConfig("items.yml").getKeys(false)) {
		    if (key.equalsIgnoreCase(value)) {
		      value = key;
		    }
	     }
	    return value;
	}
		public static void Worlds() {
		     for (int i = 0; i < ItemJoin.pl.worlds.size(); i++)
		     {
		       String world = (String)ItemJoin.pl.worlds.get(i);
		       ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "Cached " + ChatColor.YELLOW + world);
		     }
		}
		
		public static void UpdateItems() {
		     for (Player player : Bukkit.getServer().getOnlinePlayers())
		     {
		       ItemJoin.pl.CacheItems(player);
		     }
		}
		
}
