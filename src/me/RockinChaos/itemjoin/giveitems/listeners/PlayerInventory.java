package me.RockinChaos.itemjoin.giveitems.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

public class PlayerInventory implements Listener {
	
    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
    	final InventoryView view = event.getView();
        final Player player = (Player) event.getPlayer();
        boolean updateInv = false;
        if (PlayerHandler.isCraftingInv(view)) {
			final String Probable = ItemUtilities.getProbabilityItem(player);
			for (final ItemMap item : ItemUtilities.getItems()) {
				if (hasCraftingItem(item, view, player) && ItemHandler.isCraftingSlot(item.getSlot())) {
					if (ItemUtilities.isChosenProbability(item, Probable) && SQLData.isEnabled(player)
							&& item.hasPermission(player) && ItemUtilities.isObtainable(player, item)) {
						updateInv = true;
							Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
								public void run() {
									item.giveTo(player, false, 0);
								}
							}, 2L);
					}
					item.setAnimations(player);
				}
			}
			if (updateInv) { PlayerHandler.delayUpdateInventory(player, 2L); }
        }
    }
    
    @EventHandler
    private void onDropCraftingItem(PlayerDropItemEvent event) {
        final Player player = (Player) event.getPlayer();
        final InventoryView view = player.getOpenInventory();
        final Item dropItem = event.getItemDrop();
        boolean updateInv = false;
        if (PlayerHandler.isCraftingInv(view)) {
			final String Probable = ItemUtilities.getProbabilityItem(player);
			for (final ItemMap item : ItemUtilities.getItems()) {
				if (item.isSimilar(dropItem.getItemStack()) && ItemHandler.isCraftingSlot(item.getSlot()) 
						|| ItemHandler.isCraftingSlot(item.getSlot()) && ItemUtilities.getSlotConversion(item.getSlot()) == 0) {
					dropItem.remove();
					if (ItemUtilities.isChosenProbability(item, Probable) && SQLData.isEnabled(player)
							&& item.hasPermission(player) && ItemUtilities.isObtainable(player, item)) {
						updateInv = true;
							Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
								public void run() {
									item.giveTo(player, false, 0);
								}
							}, 2L);
					}
					item.setAnimations(player);
				}
			}
			if (updateInv) { PlayerHandler.delayUpdateInventory(player, 2L); }
        }
    }
    
    private boolean hasCraftingItem(ItemMap item, InventoryView view, Player player) {
    	for (ItemStack craftItem : view.getTopInventory().getContents()) {
    		if (item.isSimilar(craftItem)) {
    			craftItem.setAmount(0);
    			return true;
    		}
    	}
    	return false;
    }
}