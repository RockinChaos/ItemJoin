package me.RockinChaos.itemjoin.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.PlayerHandlers;
import me.RockinChaos.itemjoin.utils.CheckItem;

public class CancelInteract implements Listener {

	
	 @EventHandler
	  public void onCancelInteracts(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    final String world = player.getWorld().getName();
	    String modifier = ".itemflags";
	    String mod = "cancel-events";
	      if (event.hasItem() && event.getAction() != Action.PHYSICAL && !CheckItem.isAllowedItem(player, world, item, modifier, mod))
	      {
	        event.setCancelled(true);
	        PlayerHandlers.updateInventory(player);
	 }
   }
}
