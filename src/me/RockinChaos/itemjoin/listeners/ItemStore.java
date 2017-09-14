package me.RockinChaos.itemjoin.listeners;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class ItemStore implements Listener {

	@EventHandler
	public void onInventoryStore(InventoryClickEvent event) {
		String itemflag = "item-store";
		final Player player = (Player) event.getWhoClicked();
		final Inventory chest = event.getView().getTopInventory();
		final InventoryHolder holder = chest.getHolder();
			ItemStack item = null;
			if (!ServerHandler.hasViableUpdate()) {
				PlayerHandler.updateInventory(player);
			}
			if (ItemHandler.containsIgnoreCase(event.getAction().name(), "HOTBAR")) {
				item = event.getView().getBottomInventory().getItem(event.getHotbarButton());
				if (item == null) {
					item = event.getCurrentItem();
				}
			} else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
				item = event.getCurrentItem();
			} else {
				item = event.getCursor();
			}
			if (holder != null && holder.toString().contains("Chest") 
					|| holder != null && holder.toString().contains("Furnace") || holder != null && holder.toString().contains("ShulkerBox") 
					|| holder != null && holder.toString().contains("Hopper") || chest.toString().contains("Anvil") || event.getInventory().equals(player.getEnderChest())) {
				if (event.getRawSlot() > event.getInventory().getSize() && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getRawSlot() < event.getInventory().getSize()) {
					if (!ItemHandler.isAllowedItem(player, item, itemflag)) {
					event.setCancelled(true);
					PlayerHandler.updateInventory(player);
				}
			}
		}
	}

	@EventHandler
	public void onInventoryDragToStore(InventoryDragEvent event) {
		String itemflag = "item-store";
		final Player player = (Player) event.getWhoClicked();
		final Inventory chest = event.getView().getTopInventory();
		final InventoryHolder holder = chest.getHolder();
		final int inventorySize = event.getInventory().getSize();
			ItemStack item = event.getOldCursor();
			if (!ServerHandler.hasViableUpdate()) {
				PlayerHandler.updateInventory(player);
			}
			for (int i: event.getRawSlots()) {
				if (i < inventorySize) {
					if (holder != null && holder.toString().contains("Chest") 
							|| holder != null && holder.toString().contains("Furnace") || holder != null && holder.toString().contains("ShulkerBox") 
							|| holder != null && holder.toString().contains("Hopper") || chest.toString().contains("Anvil") || event.getInventory().equals(player.getEnderChest())) {
						if (!ItemHandler.isAllowedItem(player, item, itemflag)) {
							event.setCancelled(true);
							PlayerHandler.updateInventory(player);
							break;
						}
					}
				}
			}
	}

	@EventHandler
	public void onItemFramePlace(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof ItemFrame) {
			ItemStack item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString());
			final Player player = event.getPlayer();
			String itemflag = "item-store";
			if (!ItemHandler.isAllowedItem(player, item, itemflag) && item.getType().isBlock()) {
				event.setCancelled(true);
				PlayerHandler.updateInventory(player);
			}
		}
	}
}
