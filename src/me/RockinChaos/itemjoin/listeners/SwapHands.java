package me.RockinChaos.itemjoin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class SwapHands implements Listener {

	@EventHandler
	public void onHandModify(PlayerSwapHandItemsEvent event)
	 {
	 if (ServerHandler.hasCombatUpdate()) {
	  ItemStack offhand = event.getOffHandItem();
	  ItemStack mainhand = event.getMainHandItem();
	  final Player player = event.getPlayer();
	  String itemflag = "inventory-modify";
	   if (!ItemHandler.isAllowedItem(player, offhand, itemflag))
	    {
	     event.setCancelled(true);
	     PlayerHandler.updateInventory(player);
	}
	    if (!ItemHandler.isAllowedItem(player, mainhand, itemflag))
	     {
	      event.setCancelled(true);
	      PlayerHandler.updateInventory(player);
	}
   }
  }
}
