package me.RockinChaos.itemjoin.Listeners.JoinItem;

import java.util.List;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.PermissionsHandler;
import me.RockinChaos.itemjoin.utils.WorldHandler;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ChangedWorld implements Listener {

	@EventHandler
    public void clearOnWorldChanged(PlayerJoinEvent event)
    {
	        final Player player = event.getPlayer();
	        final String world = WorldHandler.getWorld(player.getWorld().getName());
	      	if(ItemJoin.getSpecialConfig("config.yml").getBoolean("Global-Settings" + ".Clear-On." + "clear-on-world-change") == true && (WorldHandler.isWorld(world))){
	    		if(ItemJoin.getSpecialConfig("config.yml").getBoolean("Global-Settings" + ".Clear-On." + "AllowOPBypass") == true && player.isOp()) {
	        		}
	                else {
	                    player.getInventory().clear();
	                    player.getInventory().setHelmet(null);
	                    player.getInventory().setChestplate(null);
	                    player.getInventory().setLeggings(null);
	                    player.getInventory().setBoots(null);
	                }
	      	}
    }

	@EventHandler
    public void giveOnWorldChanged(PlayerChangedWorldEvent event)
    {
	      final Player player = event.getPlayer();
	      long delay = ItemJoin.getSpecialConfig("config.yml").getInt("Global-Settings" + ".Get-Items." + "Delay")/1000L;
	        ItemJoin.pl.CacheItems(player);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
			public void run()
	         {
			  if (WorldHandler.isWorld(player.getWorld().getName())) {
	          setWorldChangedItems(player);
			  }
	         }
	      }, delay);
    }
	
    @SuppressWarnings("deprecation")
	public static void setWorldChangedItems(Player player)
    {
        ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(player.getWorld().getName() + ".items");
        for (String item : selection.getKeys(false)) 
        {
      	  ConfigurationSection items = selection.getConfigurationSection(item);
          String WorldChanged = ((List<?>)items.getStringList(".give-on-modifiers")).toString();
		   if (WorldChanged.contains("world-changed")) {
          final String slot = items.getString(".slot");
          final String world = WorldHandler.getWorld(player.getWorld().getName());
          if (WorldHandler.isWorld(world)) {
           	  if (player.hasPermission(PermissionsHandler.customPermissions(items, item, world)) 
           			  || player.hasPermission("itemjoin." + world + ".*") 
           			  || player.hasPermission("itemjoin.*")) {
           		  if (slot.equalsIgnoreCase("Helmet") 
           				  || slot.equalsIgnoreCase("Chestplate") 
           				  || slot.equalsIgnoreCase("Leggings") 
           				  || slot.equalsIgnoreCase("Boots") 
           				  || slot.equalsIgnoreCase("Offhand")) {
           			JoinItem.ArmorySlots(player, items, item);
           			player.updateInventory();
           		  } else {
           		   JoinItem.InventorySlots(player, items, item);
           		   player.updateInventory();
           		  }
           	  }
             }
		   }
        }
    }
}
