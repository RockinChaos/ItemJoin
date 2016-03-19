package me.RockinChaos.itemjoin.Listeners;

import me.RockinChaos.itemjoin.utils.CheckItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {

	  @SuppressWarnings("deprecation")
	  @EventHandler
	  public void onInventoryModify(InventoryClickEvent event) 
	  {
	    ItemStack item = event.getCurrentItem();
	    final Player player = (Player) event.getWhoClicked();
	    String modifier = ".prevent-modifiers";
	    String mod = "inventory-modify";
	      if (!CheckItem.isAllowedItem(player, item, modifier, mod))
	      {
	        event.setCancelled(true);
	        player.updateInventory();
	}
  }
}
