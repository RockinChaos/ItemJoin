package me.RockinChaos.itemjoin.Listeners;

import me.RockinChaos.itemjoin.utils.CheckItem;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CancelInteract implements Listener {

	
	 @SuppressWarnings("deprecation")
	@EventHandler
	  public void onCancelInteracts(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    String modifier = ".prevent-modifiers";
	    String mod = "cancel-events";
	      if (event.getAction() != Action.PHYSICAL && !CheckItem.isAllowedItem(player, item, modifier, mod))
	      {
	        event.setCancelled(true);
	        player.updateInventory();
	 }
}
}
