package me.RockinChaos.itemjoin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;

public class InventoryClose implements Listener {
	private boolean isWorldChange = false;
	
    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
    	final InventoryView view = event.getView();
        final Player player = (Player) event.getPlayer();
        if (PlayerHandler.isCraftingInv(view)) {
            boolean updateInv = false;
			final String Probable = ItemUtilities.getProbabilityItem(player);
			for (final ItemMap item : ItemUtilities.getItems()) {
				if (hasCraftingItem(item, view, player) && ItemHandler.isCraftingSlot(item.getSlot())) {
					if (ItemUtilities.isChosenProbability(item, Probable) && item.hasPermission(player) && ItemUtilities.isObtainable(player, item, 0)) {
							updateInv = true;
							Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
								@Override
								public void run() {
									item.giveTo(player, false, 0);
								}
							}, 1L);
					}
					item.setAnimations(player);
				}
			}
			if (updateInv) { PlayerHandler.delayUpdateInventory(player, 2L); }
        } else {
        	Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
        		@Override
				public void run() {
        			boolean updateInv = false;
        			for (final ItemMap item: ItemUtilities.getItems()) {
        				if (hasCraftingItems(item, player)) { updateInv = true; }
        			}
        			if (updateInv) { PlayerHandler.delayUpdateInventory(player, 2L); }
        		}
        	}, 1L);
        }
    }
    
    @EventHandler
    private void onDropCraftingItem(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final InventoryView view = player.getOpenInventory();
        final Item dropItem = event.getItemDrop();
        final ItemStack copyDropItem = event.getItemDrop().getItemStack().clone();
        final Location dropLocation = event.getItemDrop().getLocation();
        boolean updateInv = false;
        if (PlayerHandler.isCraftingInv(view)) {
			final String Probable = ItemUtilities.getProbabilityItem(player);
			boolean returnSuccess = false;
			for (final ItemMap item : ItemUtilities.getItems()) {
				if (!returnSuccess && item.isSimilar(dropItem.getItemStack()) && ItemHandler.isCraftingSlot(item.getSlot()) 
						|| ItemHandler.isCraftingSlot(item.getSlot()) && ItemUtilities.getSlotConversion(item.getSlot()) == 0) {
					dropItem.remove();
					if (ItemUtilities.isChosenProbability(item, Probable) && item.hasPermission(player) && ItemUtilities.isObtainable(player, item, 0)) {
						updateInv = true;
							Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
								@Override
								public void run() {
									if (getWorldChange()) {
										item.giveTo(player, false, 0);
									} else if (!item.isSelfDroppable()) {
										if (ItemHandler.isCraftingSlot(item.getSlot()) && ItemUtilities.getSlotConversion(item.getSlot()) == 0) {
											item.giveTo(player, false, 0);
										} else {
											Item dropped = player.getWorld().dropItem(dropLocation, copyDropItem);
											dropped.setVelocity(player.getLocation().getDirection().normalize());
										}
									}
								}
							}, 1L);
					}
					item.setAnimations(player);
					returnSuccess = true;
					if (returnSuccess && ItemHandler.isCraftingSlot(item.getSlot()) && ItemUtilities.getSlotConversion(item.getSlot()) == 0) { break; }
				}
			}
			if (updateInv) { PlayerHandler.delayUpdateInventory(player, 2L); }
        }
    }
    
    @EventHandler
    private void onWorldChangeDrop(PlayerChangedWorldEvent event) {
    	setWorldChange(true);
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			@Override
			public void run() {
				setWorldChange(false);
			}
		}, 4L);
    }
    
    private boolean hasCraftingItem(ItemMap item, InventoryView view, Player player) {
    	for (ItemStack craftItem : view.getTopInventory().getContents()) {
    		if (item.isSimilar(craftItem)) {
    			view.getTopInventory().remove(craftItem);
    			try { craftItem.setAmount(0); } catch (Exception e) { }
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean hasCraftingItems(ItemMap item, Player player) {
    	for (ItemStack craftItem : player.getOpenInventory().getTopInventory().getContents()) {
    		if (item.isSimilar(craftItem)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean getWorldChange() {
    	return isWorldChange;
    }
    
    private void setWorldChange(boolean bool) {
    	isWorldChange = bool;
    }
   
}