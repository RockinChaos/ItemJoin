package me.RockinChaos.itemjoin.Listeners.JoinItem;

import java.util.List;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.PermissionsHandler;
import me.RockinChaos.itemjoin.utils.WorldHandler;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class Respawn implements Listener {

	@EventHandler
    public void giveOnRespawn(PlayerRespawnEvent event)
    {
	      final Player player = event.getPlayer();
	      long delay = ItemJoin.getSpecialConfig("config.yml").getInt("Global-Settings" + ".Get-Items." + "Delay")/1000L;
	        ItemJoin.pl.CacheItems(player);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
			public void run()
	         {
	          setRespawnItems(player);
	         }
	      }, delay);
    }

    public static void setRespawnItems(Player player)
    {
        ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(player.getWorld().getName() + ".items");
        for (String item : selection.getKeys(false)) 
        {
      	  ConfigurationSection items = selection.getConfigurationSection(item);
          String Respawn = ((List<?>)items.getStringList(".give-on-modifiers")).toString();
		   if (Respawn.contains("respawn")) {
          final int slot = items.getInt(".slot");
          final String world = WorldHandler.getWorld(player.getWorld().getName());
    	  ItemStack[] inventory = player.getInventory().getContents();
          Boolean FirstJoin = items.getBoolean(".First-Join-Item");
          Boolean FirstJoinMode = ItemJoin.getSpecialConfig("config.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
         if (WorldHandler.isWorld(world)) {
       	  if (player.hasPermission(PermissionsHandler.customPermissions(items, item, world)) || player.hasPermission("itemjoin." + world + ".*") || player.hasPermission("itemjoin.*")) {
   	        if (toSet != null) {
   		      if (inventory[slot] != null && !CheckItem.isSimilar(inventory[slot], toSet, items, player)) {
   		    	if (FirstJoinMode != true || FirstJoin != true) {
   		         player.getInventory().setItem(slot, toSet);
   		    	 }
   		        } else if (inventory[slot] == null) {
   		        	if (FirstJoinMode != true || FirstJoin != true) {
				     player.getInventory().setItem(slot, toSet);
   		          }
   	         }
       	   }
          }
		  }
         }
        }
    }
}
