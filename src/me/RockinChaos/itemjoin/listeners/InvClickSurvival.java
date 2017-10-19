package me.RockinChaos.itemjoin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class InvClickSurvival implements Listener {

	@EventHandler
	public void onSurvivalInventoryModify(InventoryClickEvent event) {
		String itemflag = "inventory-modify";
		final Player player = (Player) event.getWhoClicked();
		if (!PlayerHandler.isCreativeMode(player)) {
			ItemStack item = null;
			if (!ServerHandler.hasChangedTheWorldUpdate()) {
				PlayerHandler.updateInventory(player);
			}
			if (ItemHandler.containsIgnoreCase(event.getAction().name(), "HOTBAR")) {
				item = event.getView().getBottomInventory().getItem(event.getHotbarButton());
				if (item == null) {
					item = event.getCurrentItem();
				}
			} else {
				item = event.getCurrentItem();
			}
			if (!ItemHandler.isAllowedItem(player, item, itemflag)) {
				event.setCancelled(true);
				PlayerHandler.updateInventory(player);
			}
		}
	}
}