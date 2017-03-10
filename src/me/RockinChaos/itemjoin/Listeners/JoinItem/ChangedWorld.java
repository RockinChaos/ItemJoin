package me.RockinChaos.itemjoin.Listeners.JoinItem;

import java.util.List;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandlers;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class ChangedWorld implements Listener {

    public static String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";

    @EventHandler
    public void giveOnWorldChanged(PlayerChangedWorldEvent event)
    {
	      final Player player = event.getPlayer();
	      final String worldFrom = event.getFrom().getName();
	      long delay = ConfigHandler.getConfig("items.yml").getInt("Global-Settings" + ".Get-Items." + "Delay") * 10L;
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
			public void run()
	         {
			  if (WorldHandler.isWorld(player.getWorld().getName())) {
		       JoinItem.setClearItems(player, worldFrom, "clear-only-itemjoin-on-world-change", "clear-on-world-change");
	           setWorldChangedItems(player);
	            if (JoinItem.failCount != 0) {
	            	player.sendMessage(Prefix + ChatColor.RED + "Could not give you " + ChatColor.YELLOW + JoinItem.failCount + " items," + ChatColor.RED +  " your inventory is full!");
	            	JoinItem.failCount = 0;
	            }
		        Boolean FirstJoinMode = ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
		        if (FirstJoinMode == true) {
		        String FirstFindPlayer = ConfigHandler.getConfig("FirstJoin.yml").getString(player.getWorld().getName() + "." + player.getName().toString());
		        if (FirstFindPlayer == null) {
		        	FirstJoin.setWorldChangedItems(player);
		            if (FirstJoin.failCount != 0) {
		            	player.sendMessage(Prefix + ChatColor.RED + "Could not give you " + ChatColor.YELLOW + FirstJoin.failCount + " items," + ChatColor.RED +  " your inventory is full!");
		            	FirstJoin.failCount = 0;
		            }
		        }
		        }
	            PlayerHandlers.updateInventory(player);
			  }
	         }
	      }, delay);
    }
    
    public static void setWorldChangedItems(Player player)
    {
	      ConfigurationSection selection = ConfigHandler.getConfig("items.yml").getConfigurationSection(WorldHandler.getWorld(player.getWorld().getName()) + ".items");
	      if (selection != null) {
	      for (String item : selection.getKeys(false)) 
        {
  	      ConfigurationSection items = selection.getConfigurationSection(item);
          String WorldChanged = ((List<?>)items.getStringList(".itemflags")).toString();
		   if (WorldChanged.contains("world-changed")) {
          final String slot = items.getString(".slot");
          final String world = player.getWorld().getName();
          if (WorldHandler.isWorld(world)) {
           	  if (PermissionsHandler.checkPermissions(items, item, world, player)) {
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
