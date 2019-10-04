package me.RockinChaos.itemjoin.listeners;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class InventoryCrafting implements Listener {
	private static HashMap<String, ItemStack[]> craftingItems = new HashMap<String, ItemStack[]>();
	private HashMap<String, Boolean> worldSwitch = new HashMap<String, Boolean>();
	
	@EventHandler(priority = EventPriority.HIGHEST)
    private void onCraftingClose(InventoryCloseEvent event) {
    	final InventoryView view = event.getView();
    	final Player player = (Player) event.getPlayer();
    	final ItemStack[] craftingContents = view.getTopInventory().getContents().clone();
    	if (PlayerHandler.isCraftingInv(view)) {
    		for (int i = 0; i <= 4; i++) {
    			boolean craftingItem = false;
    			for (ItemMap itemMap: ItemUtilities.getItems()) {
    				if (itemMap.isCraftingItem() && itemMap.isSimilar(craftingContents[i])) {
    					craftingItem = true;
    					break;
    				}
    			}
    			if (!craftingItem && craftingContents[i] != null && craftingContents[i].getType() != Material.AIR) {
    				if (player.getInventory().firstEmpty() != -1) {
    					player.getInventory().addItem(craftingContents[i].clone());
    				} else {
    					Item dropped = player.getWorld().dropItem(player.getLocation(), craftingContents[i].clone());
    					dropped.setVelocity(player.getLocation().getDirection().normalize());
    				}
    				craftingContents[i] = new ItemStack(Material.AIR);
    			}
    		}
    		for (int i = 0; i <= 4; i++) { view.setItem(i, new ItemStack(Material.AIR)); }
    		for (int i = 4; i >= 0; i--) { this.delayReturnItem(player, i, craftingContents[i]); }
    	}
    }
    
	@EventHandler(priority = EventPriority.HIGHEST)
    private void onCraftingClick(InventoryClickEvent event) {
    	final InventoryView view = event.getView();
    	final Player player = (Player) event.getWhoClicked();
    	final ItemStack[] craftingContents = view.getTopInventory().getContents().clone();
    	if (PlayerHandler.isCraftingInv(view) && event.getSlot() <= 4) {
    		if (event.getSlot() != 0 && event.getSlotType() == SlotType.CRAFTING) {
    			if (craftingContents[0] != null && craftingContents[0].getType() != Material.AIR) {
    				this.delayReturnItem(player, 0, craftingContents[0]);
    			}
    		} else if (event.getSlot() == 0 && event.getSlotType() == SlotType.RESULT) {
    			if (craftingContents[0] != null && craftingContents[0].getType() != Material.AIR) {
    				for (ItemMap itemMap: ItemUtilities.getItems()) {
    					if (itemMap.isCraftingItem() && !itemMap.isMovement() && itemMap.isSimilar(craftingContents[0])) {
    						for (int i = 1; i <= 4; i++) { this.delayReturnItem(player, i, craftingContents[i].clone()); }
    						break;
    					}
    				}
    			} else if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
    				for (ItemMap itemMap: ItemUtilities.getItems()) {
    					if (itemMap.isCraftingItem() && !itemMap.isMovement() && itemMap.isSimilar(event.getCursor())) {
    						ItemStack cursor = event.getCursor().clone();
    						player.setItemOnCursor(new ItemStack(Material.AIR));
    						this.delayReturnItem(player, 0, cursor);
    						break;
    					}
    				}
    			}
    		}
    	}
    }
    
    @EventHandler
    private void onCraftingDrop(PlayerDropItemEvent event) {
    	final Player player = (Player) event.getPlayer();
    	final ItemStack item = event.getItemDrop().getItemStack().clone();
    	Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
    		@Override
    		public void run() {
    			if (worldSwitch.containsKey(PlayerHandler.getPlayerID(player)) && worldSwitch.get(PlayerHandler.getPlayerID(player))) {
    				for (ItemMap itemMap: ItemUtilities.getItems()) {
    					if (itemMap.isCraftingItem() && itemMap.isSimilar(item)) {
    						event.getItemDrop().remove();
    						break;
    					}
    				}
    			}
    		}
    	}, 2L);
    }
    
    @EventHandler
    private void onSwitchTrigger(PlayerChangedWorldEvent event) {
    	final Player player = (Player) event.getPlayer();
    	worldSwitch.put(PlayerHandler.getPlayerID(player), true);
    	Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
    		@Override
    		public void run() {
    			if (craftingItems.containsKey(PlayerHandler.getPlayerID(player))) {
    				for (int i = 4; i >= 0; i--) {
    					delayReturnItem(player, i, craftingItems.get(PlayerHandler.getPlayerID(player))[i]);
    				}
    			}
    			worldSwitch.remove(PlayerHandler.getPlayerID(player));
    		}
    	}, 4L);
    }
    
    public static void cycleTask() {
    	Bukkit.getScheduler().scheduleSyncRepeatingTask(ItemJoin.getInstance(), new Runnable() {
    		public void run() {
    			Collection < ? > playersOnlineNew = null;
    			Player[] playersOnlineOld;
    			try {
    				if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    					if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    						playersOnlineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    						for (Object objPlayer: playersOnlineNew) {
    							if (((Player) objPlayer).isOnline()) {
    								ItemStack[] tempContents = ((Player) objPlayer).getOpenInventory().getTopInventory().getContents();
    								ItemStack[] contents = new ItemStack[5];
    								if (contents != null && tempContents != null) {
	    								for (int i = 0; i <= 4; i++) {
	    									if (contents[i] != null) {
	    										contents[i] = tempContents[i].clone();
	    									}
	    								}
    								}
    								craftingItems.put(PlayerHandler.getPlayerID(((Player) objPlayer)), contents);
    							} else {
    								craftingItems.remove(PlayerHandler.getPlayerID((Player) objPlayer));
    							}
    						}
    					}
    				} else {
    					playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    					for (Player player: playersOnlineOld) {
    						if (player.isOnline()) {
    							ItemStack[] tempContents = player.getOpenInventory().getTopInventory().getContents();
    							ItemStack[] contents = new ItemStack[5];
    							for (int i = 0; i <= 4; i++) {
    								contents[i] = tempContents[i].clone();
    							}
    							craftingItems.put(PlayerHandler.getPlayerID(player), contents);
    						} else {
    							craftingItems.remove(PlayerHandler.getPlayerID(player));
    						}
    					}
    				}
    			} catch (Exception e) {
    				ServerHandler.sendDebugTrace(e);
    			}
    		}
    	}, 0L, 100L);
    }
    
    private void delayReturnItem(final Player player, final int slot, final ItemStack item) {
    	Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
    		@Override
    		public void run() {
    			player.getOpenInventory().getTopInventory().setItem(slot, item);
    			PlayerHandler.updateInventory(player);
    		}
    	}, 1L);
    }
}