package me.RockinChaos.itemjoin.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.PlayerHandlers;
import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.Registers;

public class SwapHands implements Listener {

	@EventHandler
	public void onHandModify(PlayerSwapHandItemsEvent event)
	 {
	 if (Registers.hasCombatUpdate()) {
	  ItemStack offhand = event.getOffHandItem();
	  ItemStack mainhand = event.getMainHandItem();
	  final Player player = event.getPlayer();
	  final String world = player.getWorld().getName();
	  String modifier = ".itemflags";
	  String mod = "inventory-modify";
	   if (!CheckItem.isAllowedItem(player, world, offhand, modifier, mod))
	    {
	     event.setCancelled(true);
	     PlayerHandlers.updateInventory(player);
	}
	    if (!CheckItem.isAllowedItem(player, world, mainhand, modifier, mod))
	     {
	      event.setCancelled(true);
	      PlayerHandlers.updateInventory(player);
	}
   }
  }
}
