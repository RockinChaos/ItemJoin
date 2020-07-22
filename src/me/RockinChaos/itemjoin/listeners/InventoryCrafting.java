/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.listeners;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.handlers.events.PlayerAutoCraftEvent;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.LegacyAPI;

public class InventoryCrafting implements Listener {
	
	private static HashMap<String, ItemStack[]> craftingItems = new HashMap<String, ItemStack[]>();
	private static HashMap<String, ItemStack[]> craftingOpenItems = new HashMap<String, ItemStack[]>();
	private static HashMap<String, ItemStack[]> creativeCraftingItems = new HashMap<String, ItemStack[]>();
	private static HashMap<String, Boolean> antiSpamPlayers = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> worldSwitch = new HashMap<String, Boolean>();
	
   /**
    * Prevents players from autocrafting with custom crafting items in their crafting slots.
    * 
    * @param event - PlayerAutoCraftEvent
    */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	private void onAutoCraft(PlayerAutoCraftEvent event) {
		for (int i = 0; i <= 4; i++) {
  			final ItemStack[] craftingContents = event.getContents().clone();
  			if (event.isCancelled()) { return; }
  			for (ItemMap itemMap: ItemUtilities.getUtilities().getCraftingItems()) {
  				if (!event.isCancelled() && itemMap.isSimilar(craftingContents[i])) {
  					event.setCancelled(true);
  				} else if (event.isCancelled()) { return; }
  			}
  		}
	}
	
   /**
	* Removes custom crafting items from the players inventory when opening a GUI menu or storable inventory.
	* 
	* @param event - InventoryOpenEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onCraftingOpen(InventoryOpenEvent event) {
    	final Player player = (Player) event.getPlayer();
    	if (!craftingOpenItems.containsKey(PlayerHandler.getPlayer().getPlayerID(player))) {
	    	if (antiSpamPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)) == null) { craftingOpenItems.put(PlayerHandler.getPlayer().getPlayerID(player), PlayerHandler.getPlayer().getTopContents(player)); }
			LegacyAPI.getLegacy().removeCraftItems(player);
	    	PlayerHandler.getPlayer().updateInventory(player, 1L);
    	}
    }
	
   /**
	* Gives the custom crafting items back when the player closes their inventory if they had items existing previously.
	* 
	* @param event - InventoryCloseEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	private void onCraftingClose(org.bukkit.event.inventory.InventoryCloseEvent event) {
		if (!ServerHandler.getServer().hasSpecificUpdate("1_8") || !PlayerHandler.getPlayer().isCraftingInv(event.getView())) {
			ItemStack[] topContents = ItemHandler.getItem().cloneContents(event.getView().getTopInventory().getContents());
	    	this.handleClose(slot -> { 
	    		event.getView().getTopInventory().setItem(slot, new ItemStack(Material.AIR));
	    	}, (Player)event.getPlayer(), event.getView(), topContents);
    	}
    }
    
   /**
	* Gives the custom crafting items back when the player closes their inventory if they had items existing previously.
	* 
	* @param event - InventoryCloseEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    private void onCraftingClose(me.RockinChaos.itemjoin.handlers.events.InventoryCloseEvent event) {
		if (ServerHandler.getServer().hasSpecificUpdate("1_8")) {
	    	this.handleClose(slot -> { 
	    		event.removeItem(event.getTopContents()[slot], slot);
	    	}, event.getPlayer(), event.getView(), event.getPreviousContents(true));
		}
    }
    
   /**
	* Allows the player to move their custom crafting item around their inventory without them dropping
	* or bugging out. This creates a seamless transition between their inventory and crafting slots.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    private void onCraftingClick(InventoryClickEvent event) {
    	final InventoryView view = event.getView();
    	final Player player = (Player) event.getWhoClicked();
    	final ItemStack[] craftingContents = view.getTopInventory().getContents().clone();
    	if (PlayerHandler.getPlayer().isCraftingInv(view) && event.getSlot() <= 4) {
    		if (event.getSlot() != 0 && event.getSlotType() == SlotType.CRAFTING) {
    			if (craftingContents[0] != null && craftingContents[0].getType() != Material.AIR) {
    				this.delayReturnItem(player, 0, craftingContents[0], 1L);
    			}
    		} else if (event.getSlot() == 0 && event.getSlotType() == SlotType.RESULT) {
    			if (craftingContents[0] != null && craftingContents[0].getType() != Material.AIR) {
    				for (ItemMap itemMap: ItemUtilities.getUtilities().getCraftingItems()) {
    					if (!itemMap.isMovement() && itemMap.isSimilar(craftingContents[0])) {
    						for (int i = 1; i <= 4; i++) { this.delayReturnItem(player, i, craftingContents[i].clone(), 1L); }
    						break;
    					}
    				}
    			} else if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
    				for (ItemMap itemMap: ItemUtilities.getUtilities().getCraftingItems()) {
    					if (!itemMap.isMovement() && itemMap.isSimilar(event.getCursor())) {
    						ItemStack cursor = event.getCursor().clone();
    						player.setItemOnCursor(new ItemStack(Material.AIR));
    						this.delayReturnItem(player, 0, cursor, 1L);
    						break;
    					}
    				}
    			}
    		}
    	}
    }
    
   /**
	* Returns the custom crafting item to the player if it is dropped automagically when switching worlds,
	* typically via the nether portal causing duplication glitches.
	* 
	* @param event - PlayerDropItemEvent
	*/
    @EventHandler(ignoreCancelled = true)
    private void onCraftingDrop(PlayerDropItemEvent event) {
    	final Player player = (Player) event.getPlayer();
    	final ItemStack item = event.getItemDrop().getItemStack().clone();
    	if (player.getHealth() > 0 && !event.isCancelled()) {
    		final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item, null, player.getWorld());
	    	Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
	    		@Override
	    		public void run() {
	    			if (itemMap != null && !event.isCancelled() && worldSwitch.containsKey(PlayerHandler.getPlayer().getPlayerID(player)) && worldSwitch.get(PlayerHandler.getPlayer().getPlayerID(player))) {
	    				event.getItemDrop().getItemStack().setItemMeta(null);
	    				event.getItemDrop().remove();
	    			}
	    		}
	    	}, 2L);
    	}
    }
    
   /**
    * Sets the players status as having switched worlds to prevent custom crafting item drop duplication.
    * 
    * @param event - PlayerChangedWorldEvent
    */
    @EventHandler(ignoreCancelled = true)
    private void onSwitchTrigger(PlayerChangedWorldEvent event) {
    	final Player player = (Player) event.getPlayer();
    	this.worldSwitch.put(PlayerHandler.getPlayer().getPlayerID(player), true);
    	Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
    		@Override
    		public void run() {
    			if (craftingItems.containsKey(PlayerHandler.getPlayer().getPlayerID(player))) {
    				delayReturnCrafting(event.getPlayer(), craftingItems.get(PlayerHandler.getPlayer().getPlayerID(player)), 1L);
    			}
    			worldSwitch.remove(PlayerHandler.getPlayer().getPlayerID(player));
    		}
    	}, 4L);
    }
    
   /**
    * Removes custom crafting items from the player when they enter creative mode.
    * 
    * @param event - PlayerGameModeChangeEvent
    */
    @EventHandler(ignoreCancelled = true)
    private void onSwitchGamemode(PlayerGameModeChangeEvent event) {
    	final Player player = (Player) event.getPlayer();
    	if (event.getNewGameMode() == GameMode.CREATIVE) {
    		creativeCraftingItems.put(PlayerHandler.getPlayer().getPlayerID(player), craftingItems.get(PlayerHandler.getPlayer().getPlayerID(player)));
    		LegacyAPI.getLegacy().removeCraftItems(player);
    	} else if (event.getNewGameMode() != GameMode.CREATIVE && creativeCraftingItems.containsKey(PlayerHandler.getPlayer().getPlayerID(player))) {
    		this.delayReturnCrafting(event.getPlayer(), creativeCraftingItems.get(PlayerHandler.getPlayer().getPlayerID(player)), 1L);
    		craftingItems.put(PlayerHandler.getPlayer().getPlayerID(player), creativeCraftingItems.get(PlayerHandler.getPlayer().getPlayerID(player)));
    		creativeCraftingItems.remove(PlayerHandler.getPlayer().getPlayerID(player));
    	}
    	PlayerHandler.getPlayer().updateInventory(player, 1L);
    }
    
   /**
    * Constantly cycles through the players crafting slots saving them to a HashMap for later use.
    * 
    */
    public static void cycleTask() {
    	Bukkit.getScheduler().runTaskTimerAsynchronously(ItemJoin.getInstance(), new Runnable() {
    		public void run() {
    			Collection < ? > playersOnlineNew = null;
    			Player[] playersOnlineOld;
    			try {
    				if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    					if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    						playersOnlineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    						for (Object objPlayer: playersOnlineNew) {
    							if (((Player) objPlayer).isOnline() && PlayerHandler.getPlayer().isCraftingInv(((Player) objPlayer).getOpenInventory())) {
    								ItemStack[] tempContents = ((Player) objPlayer).getOpenInventory().getTopInventory().getContents();
    								ItemStack[] contents = new ItemStack[5];
    								if (contents != null && tempContents != null) {
	    								for (int i = 0; i <= 4; i++) {
	    									contents[i] = tempContents[i].clone();
	    								}
    								}
    								craftingItems.put(PlayerHandler.getPlayer().getPlayerID(((Player) objPlayer)), contents);
    							} else {
    								craftingItems.remove(PlayerHandler.getPlayer().getPlayerID((Player) objPlayer));
    							}
    						}
    					}
    				} else {
    					playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    					for (Player player: playersOnlineOld) {
    						if (player.isOnline() && PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
    							ItemStack[] tempContents = player.getOpenInventory().getTopInventory().getContents();
    							ItemStack[] contents = new ItemStack[5];
    							if (contents != null && tempContents != null) {
    								for (int i = 0; i <= 4; i++) {
    									contents[i] = tempContents[i].clone();
    								}
    							}
    							craftingItems.put(PlayerHandler.getPlayer().getPlayerID(player), contents);
    						} else {
    							craftingItems.remove(PlayerHandler.getPlayer().getPlayerID(player));
    						}
    					}
    				}
    			} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
    		}
    	}, 0L, 40L);
    }
    
   /**
    * Attempts to save and return the prior open inventory crafting slots.
    * 
    * @param input - The methods to be executed.
    * @param player - The Player being handled.
    * @param view - The view being referenced.
    * @param inventory - The inventory being handled.
    */
	private void handleClose(final Consumer < Integer > input, final Player player, final InventoryView view, final ItemStack[] inventory) {
		if (PlayerHandler.getPlayer().isCraftingInv(view)) {
			if (!ItemHandler.getItem().isContentsEmpty(inventory) && antiSpamPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)) == null) {
				antiSpamPlayers.put(PlayerHandler.getPlayer().getPlayerID(player), true);
				for (int i = 0; i <= 4; i++) {
					for (ItemMap itemMap: ItemUtilities.getUtilities().getCraftingItems()) {
						if (!itemMap.isSimilar(inventory[i])) {
							input.accept(i);
						}
					}
				}
				this.delayReturnCrafting(player, inventory, 1L);
			}
		} else {
			ServerHandler.getServer().runAsyncThread(main -> {
				if (PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory()) && craftingOpenItems.containsKey(PlayerHandler.getPlayer().getPlayerID(player))) {
					ItemStack[] openCraftContents = craftingOpenItems.get(PlayerHandler.getPlayer().getPlayerID(player));
					if (openCraftContents != null && openCraftContents.length != 0 && antiSpamPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)) == null) {
						antiSpamPlayers.put(PlayerHandler.getPlayer().getPlayerID(player), true);
						this.delayReturnCrafting(player, openCraftContents, 1L);
						craftingItems.put(PlayerHandler.getPlayer().getPlayerID(player), craftingOpenItems.get(PlayerHandler.getPlayer().getPlayerID(player)));
						craftingOpenItems.remove(PlayerHandler.getPlayer().getPlayerID(player));
					}
				}
			});
		}
	}
	
   /**
    * Returns the custom crafting item to the player after the specified delay.
    * 
    * @param player - the Player having their item returned.
    * @param contents - the crafting contents to be returned.
    * @param delay - the delay to wait before returning the item.
    */
	private void delayReturnCrafting(final Player player, final ItemStack[] contents, final long delay) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), () -> {
			if (!player.isOnline()) { return; } else if (!PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) { this.delayReturnCrafting(player, contents, 10L); return; }
			for (int i = 4; i >= 0; i--) {
				player.getOpenInventory().getTopInventory().setItem(i, contents[i]);
				PlayerHandler.getPlayer().updateInventory(player, ItemUtilities.getUtilities().getItemMap(contents[i], null, player.getWorld()), 1L);
			}
			if (antiSpamPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)) != null) { antiSpamPlayers.remove(PlayerHandler.getPlayer().getPlayerID(player)); }
		}, delay);
	}
	
   /**
    * Returns the custom crafting item to the player after the specified delay.
    * 
    * @param player - the Player having their item returned.
    * @param slot - the slot to return the crafting item to.
    * @param item - the item to be returned.
    * @param delay - the delay to wait before returning the item.
    */
    private void delayReturnItem(final Player player, final int slot, final ItemStack item, long delay) {
    	if (item == null) { return; } if (slot == 0) { delay += 1L; }
    	Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
    		@Override
    		public void run() {
    			if (!player.isOnline()) { return; }
    			if (PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
    	    		player.getOpenInventory().getTopInventory().setItem(slot, item);	
    	    		PlayerHandler.getPlayer().updateInventory(player, 1L);
    			} else {
    				delayReturnItem(player, slot, item, 10L);
    			}
    		}
    	}, delay);
    }
    
   /**
    * Quick saves the current inventories crafting items.
    * 
    * @oaram player - The player having their crafting items saved.
    */
    public static void quickSave(Player player) {
    	if (PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
    		ItemStack[] contents = new ItemStack[5];
    		if (contents != null && player.getOpenInventory().getTopInventory().getContents() != null) {
    			for (int i = 0; i <= 4; i++) {
    				contents[i] = player.getOpenInventory().getTopInventory().getContents()[i].clone();
    			}
    		}
    		craftingItems.put(PlayerHandler.getPlayer().getPlayerID(player), contents);
    	}
    }
    
   /**
    * Gets the crafting items HashMap of players crafting contents.
    * 
    * @return The HashMap of players and their crafting contents.
    */
    public static HashMap<String, ItemStack[]> getCraftItems() {
    	return craftingItems;
    }
    
   /**
    * Gets the crafting items HashMap of players prior to creative crafting contents.
    * 
    * @return The HashMap of players and their prior to creative crafting contents.
    */
    public static HashMap<String, ItemStack[]> getCreativeCraftItems() {
    	return creativeCraftingItems;
    }
    
   /**
    * Gets the crafting items HashMap of players prior to opened inventory crafting contents.
    * 
    * @return The HashMap of players and their prior to opened inventory crafting contents.
    */
    public static HashMap<String, ItemStack[]> getOpenCraftItems() {
    	return craftingOpenItems;
    }
}