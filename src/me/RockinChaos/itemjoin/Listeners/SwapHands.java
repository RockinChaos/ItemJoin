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
	public void onMainHandModify(PlayerSwapHandItemsEvent event)
	 {
	 if (Registers.hasCombatUpdate()) {
	  ItemStack item = event.getOffHandItem();
	  final Player player = event.getPlayer();
	  String modifier = ".itemflags";
	  String mod = "inventory-modify";
	   if (!CheckItem.isAllowedItem(player, item, modifier, mod))
	    {
	     event.setCancelled(true);
	     PlayerHandlers.updateInventory(player);
	}
  }
}
	
	@EventHandler
	public void onOffHandModify(PlayerSwapHandItemsEvent event)
	 {
     if (Registers.hasCombatUpdate()) {
	  ItemStack item = event.getMainHandItem();
	  final Player player = event.getPlayer();
	  String modifier = ".itemflags";
	  String mod = "inventory-modify";
	    if (!CheckItem.isAllowedItem(player, item, modifier, mod))
	     {
	      event.setCancelled(true);
	      PlayerHandlers.updateInventory(player);
	}
  }
}
}
