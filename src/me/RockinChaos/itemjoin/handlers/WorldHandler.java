package me.RockinChaos.itemjoin.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.CacheItems.CacheItems;
import me.RockinChaos.itemjoin.utils.Registers;

public class WorldHandler {
	
    public static ConsoleCommandSender Console = ItemJoin.pl.getServer().getConsoleSender();
    public static String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";

	public static boolean isWorld(String world) {	
		 boolean isWorld = false;
	     for (String worlds : ItemJoin.pl.worlds) {
		   if (worlds.equalsIgnoreCase(world)) {
			 isWorld = true;
		   }
	    }
		return isWorld; 
	}
		public static String getWorld(String world) {	// Throwing errors here //
		 String value = world;
		 for (String key : ConfigHandler.getConfig("items.yml").getKeys(false)) {
		   if (key != null && key.equalsIgnoreCase(value)) {
		     value = key;
		    } else if(ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".Get-Items." + "Global-Items") == true) {
		    	value = "global";
		    }
	     }
	    return value;
	}

		public static void Worlds() {
		     for (int i = 0; i < ItemJoin.pl.worlds.size(); i++)
		     {
		       String world = (String)ItemJoin.pl.worlds.get(i);
		       Console.sendMessage(Prefix + ChatColor.GREEN + "Cached " + ChatColor.YELLOW + world);
		     }
		}
		
		public static String checkWorld(int i) {
			String world = WorldHandler.getWorld((String)ItemJoin.pl.worlds.get(i));
			   if (ConfigHandler.getConfig("items.yml").getConfigurationSection(world) != null) {
				   return WorldHandler.getWorld((String)ItemJoin.pl.worlds.get(i));
			   } else if (ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".Get-Items." + "Global-Items") == true 
					   && ConfigHandler.getConfig("items.yml").getConfigurationSection(getWorld("global")) != null) {
		    	 return getWorld("global");
			   }
			return "DoesNotExist!"; 
		}
		
		public static String checkWorlds(String worlds) {
			String world = WorldHandler.getWorld(worlds);
			   if (ConfigHandler.getConfig("items.yml").getConfigurationSection(world) != null) {
				   return world;
			   } else if (ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".Get-Items." + "Global-Items") == true 
					   && ConfigHandler.getConfig("items.yml").getConfigurationSection(getWorld("global")) != null) {
		    	 return getWorld("global");
			   }
			return "DoesNotExist!"; 
		}
		
		public static void UpdateItems() {
			if(Registers.hasBetterVersion()) {
		     for (Player player : Bukkit.getServer().getOnlinePlayers())
		     {
		    	CacheItems.run(player);
		     }
		}
		}
		
}
