package me.RockinChaos.itemjoin.listeners;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class Legacy_Storable implements Listener {
	
	@EventHandler
	public void onInventoryStore(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		String invType = event.getView().getType().toString();
		ItemStack item = null;
		if (Utils.containsIgnoreCase(event.getAction().name(), "HOTBAR")) {
			item = event.getView().getBottomInventory().getItem(event.getHotbarButton());
			if (item == null) { item = event.getCurrentItem(); }
		} else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) { item = event.getCurrentItem(); } 
		else { item = event.getCursor(); }
		if (invType != null) {
			if (invType.contains("CHEST") || invType.contains("FURNACE") || invType.contains("SHULKER_BOX") || invType.contains("HOPPER") || invType.contains("ANVIL") || invType.contains("WORKBENCH") || invType.contains("DISPENSER") || invType.contains("DROPPER")) {
				if (event.getRawSlot() > event.getInventory().getSize() && event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getRawSlot() < event.getInventory().getSize()) {
					if (!ItemHandler.isAllowed(player, item, "item-store")) {
						event.setCancelled(true);
						PlayerHandler.updateInventory(player);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryDragToStore(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		String invType = event.getView().getType().toString();
		int inventorySize = event.getInventory().getSize();
		ItemStack item = event.getOldCursor();
		for (int i: event.getRawSlots()) {
			if (i < inventorySize) {
				if (invType != null) {
					if (invType.contains("CHEST") || invType.contains("FURNACE") || invType.contains("SHULKER_BOX") 
							|| invType.contains("HOPPER") || invType.contains("ANVIL") || invType.contains("WORKBENCH") || invType.contains("DISPENSER") || invType.contains("DROPPER")) {
						if (!ItemHandler.isAllowed(player, item, "item-store")) {
							event.setCancelled(true);
							PlayerHandler.updateInventory(player);
							break;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInteractItemFrame(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof ItemFrame) {
			ItemStack item;
			if (ServerHandler.hasCombatUpdate()) { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
			else { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), ""); }
			Player player = event.getPlayer();
			if (!ItemHandler.isAllowed(player, item, "item-store")) {
				event.setCancelled(true);
				PlayerHandler.updateInventory(player);
			}
		}
	}
}