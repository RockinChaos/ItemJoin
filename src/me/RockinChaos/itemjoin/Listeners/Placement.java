package me.RockinChaos.itemjoin.Listeners;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandlers;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.CheckItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Placement implements Listener{

	 @EventHandler
	  public void onPreventPlayerPlace(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    final String world = player.getWorld().getName();
	    String modifier = ".itemflags";
	    String mod = "placement";
	      if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !CheckItem.isAllowedItem(player, world, item, modifier, mod) && CheckItem.isBlockSimilar(item))
	      {
	        event.setCancelled(true);
	        PlayerHandlers.updateInventory(player);
	 }
}

	@EventHandler
	  public void onCountLock(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    final String world = player.getWorld().getName();
	    String modifier = ".itemflags";
	    String mod = "count-lock";
	      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	    		  if(!CheckItem.isAllowedItem(player, world, item, modifier, mod)) 
	      {
	    	  reAddItem(player, item);
	 }
   }
}
	 
	 public static void reAddItem(final Player player, ItemStack item1) 
	  {

	       ConfigurationSection selection = ConfigHandler.getConfig("items.yml").getConfigurationSection(WorldHandler.checkWorlds(player.getWorld().getName()) + ".items");
	        for (String item : selection.getKeys(false)) 
	        {
	      	ConfigurationSection items = selection.getConfigurationSection(item);
	      	String slot = items.getString(".slot");
	      	final ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
	      	if (toSet != null && ItemJoin.isInt(slot) && CheckItem.isSimilar(item1, toSet, items, player)) {
	      		final int isSlot = items.getInt(".slot");
		        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
		        {
		        public void run()
		          {
			        player.getInventory().setItem(isSlot, toSet);
			        PlayerHandlers.updateInventory(player);
		          }
		      }, (long) 0.1);
	      	}
	      }
	  }
}
