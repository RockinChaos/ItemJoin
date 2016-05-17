package me.RockinChaos.itemjoin.Listeners.JoinItem;

import java.util.List;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.PermissionsHandler;
import me.RockinChaos.itemjoin.utils.WorldHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Respawn implements Listener {

    public static String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";
	
	@EventHandler(priority=EventPriority.HIGHEST)
    public void giveOnRespawn(PlayerRespawnEvent event)
    {
	      final Player player = event.getPlayer();
	      long delay = ItemJoin.getSpecialConfig("config.yml").getInt("Global-Settings" + ".Get-Items." + "Delay") * 10L;
	        ItemJoin.pl.CacheItems(player);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
			@SuppressWarnings("deprecation")
			public void run()
	         {
			  if (WorldHandler.isWorld(player.getWorld().getName())) {
			   setRespawnItems(player);
	            if (JoinItem.failCount != 0) {
	            	player.sendMessage(Prefix + ChatColor.RED + "Could not give you " + ChatColor.YELLOW + JoinItem.failCount + " items," + ChatColor.RED +  " your inventory is full!");
	            	JoinItem.failCount = 0;
	            }
       		   player.updateInventory();
		    	}
	         }
	      }, delay);
    }

    public static void setRespawnItems(Player player)
    {
        ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(WorldHandler.getWorld(player.getWorld().getName()) + ".items");
        if (selection != null) {
        for (String item : selection.getKeys(false)) 
        {
      	  ConfigurationSection items = selection.getConfigurationSection(item);
          String Respawn = ((List<?>)items.getStringList(".give-on-modifiers")).toString();
		   if (Respawn.contains("respawn")) {
          final String slot = items.getString(".slot");
          final String world = WorldHandler.getWorld(player.getWorld().getName());
          if (WorldHandler.isWorld(world)) {
           	  if (player.hasPermission(PermissionsHandler.customPermissions(items, item, world)) 
           			  || player.hasPermission("itemjoin." + world + ".*") 
           			  || player.hasPermission("itemjoin.*")) {
           		  if (slot.equalsIgnoreCase("Helmet") 
           				  || slot.equalsIgnoreCase("Arbitrary")
           				  || slot.equalsIgnoreCase("Chestplate") 
           				  || slot.equalsIgnoreCase("Leggings") 
           				  || slot.equalsIgnoreCase("Boots") 
           				  || slot.equalsIgnoreCase("Offhand")) {
           			JoinItem.CustomSlots(player, items, item);
           		  } else {
           		   JoinItem.InventorySlots(player, items, item);
           		  }
           	  }
             }
		   }
        }
    }
  }
}
