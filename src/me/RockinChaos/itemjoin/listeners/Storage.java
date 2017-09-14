package me.RockinChaos.itemjoin.listeners;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;

public class Storage implements Listener {
	
	@EventHandler
    public void onItemFramePlace(PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof ItemFrame) {
    	    ItemStack item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString());
    	    final Player player = event.getPlayer();
    	    String itemflag = "storage";
    	      if (!ItemHandler.isAllowedItem(player, item, itemflag) && item.getType().isBlock())
    	      {
    	        event.setCancelled(true);
    	        PlayerHandler.updateInventory(player);
    	 }
        }
    }
	

}
