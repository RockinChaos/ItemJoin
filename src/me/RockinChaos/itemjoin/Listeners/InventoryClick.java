package me.RockinChaos.itemjoin.Listeners;

import me.RockinChaos.itemjoin.handlers.PlayerHandlers;
import me.RockinChaos.itemjoin.utils.CheckItem;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {

	      @EventHandler(priority=EventPriority.HIGHEST)
		  public void onInventoryModify(InventoryClickEvent event) 
		  {
	    	String modifier = ".itemflags";
	    	String mod = "inventory-modify";
	    	Player player = (Player) event.getWhoClicked();
	    	String world = player.getWorld().getName();
	        ItemStack item;
	            if (event.getAction().name().contains("HOTBAR")) {
	                item = event.getView().getBottomInventory().getItem(event.getHotbarButton());
	            } else {
	            	item = event.getCurrentItem();
	            }
	    	      if (!CheckItem.isAllowedItem(player, world, item, modifier, mod))
	    	      {
		    	    	event.setCancelled(true);
		    	        PlayerHandlers.updateInventory(player);
		    	        if (player.getGameMode() == GameMode.CREATIVE) {
		    	        	player.closeInventory();
		    	        }
	    	     }
        }
}
