package me.RockinChaos.itemjoin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;

public class CancelInteract implements Listener {

	
	 @EventHandler
	  public void onCancelInteracts(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    String itemflag = "cancel-events";
	      if (event.hasItem() && event.getAction() != Action.PHYSICAL && !ItemHandler.isAllowedItem(player, item, itemflag))
	      {
	        event.setCancelled(true);
	        PlayerHandler.updateInventory(player);
	 }
   }
}
