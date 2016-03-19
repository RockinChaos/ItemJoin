package me.RockinChaos.itemjoin.Listeners;

import me.RockinChaos.itemjoin.utils.CheckItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class Mainhand implements Listener {

	  
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMainHandModify(PlayerSwapHandItemsEvent event)
	 {
	  ItemStack item = event.getOffHandItem();
	  final Player player = event.getPlayer();
	  String modifier = ".prevent-modifiers";
	  String mod = "inventory-modify";
	   if (!CheckItem.isAllowedItem(player, item, modifier, mod))
	    {
	     event.setCancelled(true);
	     player.updateInventory();
	}
}
}
