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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
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
import me.RockinChaos.itemjoin.utils.Utils;

public class Crafting implements Listener {
	
	private static HashMap<String, ItemStack[]> craftingItems = new HashMap<String, ItemStack[]>();
	private static HashMap<String, ItemStack[]> craftingOpenItems = new HashMap<String, ItemStack[]>();
	private static HashMap<String, ItemStack[]> creativeCraftingItems = new HashMap<String, ItemStack[]>();
	
   /**
    * Prevents players from autocrafting with custom crafting items in their crafting slots.
    * 
    * @param event - PlayerAutoCraftEvent
    */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onAutoCraft(PlayerAutoCraftEvent event) {
		for (int i = 0; i <= 4; i++) {
  			final ItemStack[] craftingContents = event.getContents().clone();
  			if (event.isCancelled()) { return; }
  			for (ItemMap itemMap: ItemUtilities.getUtilities().getCraftingItems()) {
  				if (!event.isCancelled() && itemMap.isReal(craftingContents[i])) {
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
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    private void onCraftingOpen(InventoryOpenEvent event) {
    	final Player player = (Player) event.getPlayer();
    	if (!craftingOpenItems.containsKey(PlayerHandler.getPlayer().getPlayerID(player))) {
	    	craftingOpenItems.put(PlayerHandler.getPlayer().getPlayerID(player), PlayerHandler.getPlayer().getTopContents(player));
			ItemHandler.getItem().removeCraftItems(player);
	    	PlayerHandler.getPlayer().updateInventory(player, 1L);
    	}
    }
	
   /**
	* Gives the custom crafting items back when the player closes their inventory if they had items existing previously.
	* 
	* @param event - InventoryCloseEvent
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onCraftingClose(org.bukkit.event.inventory.InventoryCloseEvent event) {
		if (!ServerHandler.getServer().hasSpecificUpdate("1_8") || !PlayerHandler.getPlayer().isCraftingInv(event.getView())) {
			ItemStack[] topContents = ItemHandler.getItem().cloneContents(event.getView().getTopInventory().getContents());
	    	this.handleClose(slot -> { 
	    		event.getView().getTopInventory().setItem(slot, new ItemStack(Material.AIR));
	    	}, (Player)event.getPlayer(), event.getView(), topContents, true);
    	}
    }
    
   /**
	* Gives the custom crafting items back when the player closes their inventory if they had items existing previously.
	* 
	* @param event - InventoryCloseEvent
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    private void onCraftingClose(me.RockinChaos.itemjoin.handlers.events.InventoryCloseEvent event) {
		if (ServerHandler.getServer().hasSpecificUpdate("1_8") && PlayerHandler.getPlayer().isCraftingInv(event.getView())) {
	    	this.handleClose(slot -> { 
	    		if (!event.isCancelled()) { event.setCancelled(true); }
	    	}, event.getPlayer(), event.getView(), event.getPreviousContents(true), false);
		}
    }
    
   /**
	* Allows the player to move their custom crafting item around their inventory without them dropping
	* or bugging out. This creates a seamless transition between their inventory and crafting slots.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    private void onCraftingClick(InventoryClickEvent event) {
    	final InventoryView view = event.getView();
    	final Player player = (Player) event.getWhoClicked();
    	final ItemStack[] craftingContents = view.getTopInventory().getContents().clone();
    	if (PlayerHandler.getPlayer().isCraftingInv(view) && event.getSlot() <= 4) {
    		if (event.getSlot() != 0 && event.getSlotType() == SlotType.CRAFTING) {
    			if (craftingContents[0] != null && craftingContents[0].getType() != Material.AIR) {
    				final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(craftingContents[0], null, player.getWorld());
    				if (itemMap != null && itemMap.isCraftingItem()) {
    					ItemHandler.getItem().returnCraftingItem(player, 0, craftingContents[0], 1L);
    				}
    			}
    		} else if (event.getSlot() == 0 && event.getSlotType() == SlotType.RESULT) {
    			if (craftingContents[0] != null && craftingContents[0].getType() != Material.AIR) {
    				for (ItemMap itemMap: ItemUtilities.getUtilities().getCraftingItems()) {
    					if (!itemMap.isMovement() && itemMap.isSimilar(craftingContents[0])) {
    						for (int i = 1; i <= 4; i++) { ItemHandler.getItem().returnCraftingItem(player, i, craftingContents[i].clone(), 1L); }
    						ServerHandler.getServer().runThread(main -> player.getOpenInventory().getTopInventory().setItem(0, new ItemStack(Material.AIR)), 1L);
    						break;
    					}
    				}
    			} else if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
    				for (ItemMap itemMap: ItemUtilities.getUtilities().getCraftingItems()) {
    					if (!itemMap.isMovement() && itemMap.isSimilar(event.getCursor())) {
    						ItemStack cursor = event.getCursor().clone();
    						player.setItemOnCursor(new ItemStack(Material.AIR));
    						ItemHandler.getItem().returnCraftingItem(player, 0, cursor, 1L);
    						break;
    					}
    				}
    			}
    		}
    	}
    }
	
   /**
	* Removes all crafting items from the 2x2 crafting view when the player leaves the server.
	* 
	* @param event - PlayerDropItemEvent
	*/
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    private void onCraftingLogout(PlayerDropItemEvent event) {
    	final Player player = (Player) event.getPlayer();
    	final ItemStack itemCopy = event.getItemDrop().getItemStack().clone();
    	final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(itemCopy, null, player.getWorld());
    	if (itemMap != null && itemMap.isCraftingItem() && !player.isDead()) {
	    	event.getItemDrop().remove();
	    	ServerHandler.getServer().runThread(main -> {
	    		if (player.isOnline() && itemMap.isSelfDroppable()) {
	    			itemMap.giveTo(player, itemCopy.getAmount());
	    			if (Utils.getUtils().getSlotConversion(itemMap.getSlot()) != 0) { 
	    				this.returnSlotZero(player, 4L);
	    			}
	    		} else if (player.isOnline()) {
	    			this.dropItem(player, itemCopy);
	    		}
	    	}, 2L);
    	}
    }
    
   /**
	* Returns the custom crafting item to the player if it is dropped automagically when switching worlds,
	* typically via the nether portal causing duplication glitches.
	* 
	* @param event - PlayerDropItemEvent
	*/
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    private void onCraftingWorlds(PlayerDropItemEvent event) {
    	final Player player = (Player) event.getPlayer();
    	final World world = player.getWorld();
    	final ItemStack item = event.getItemDrop().getItemStack().clone();
    	if (player.getHealth() > 0) {
    		final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item, null, player.getWorld());
	    	ServerHandler.getServer().runThread(main -> {
	    		if (!world.equals(player.getWorld()) && itemMap != null && itemMap.inWorld(player.getWorld()) && itemMap.hasPermission(player)) {
	    			event.getItemDrop().getItemStack().setItemMeta(null);
	    			event.getItemDrop().remove();
	    			itemMap.giveTo(player, item.getAmount());
	    			this.returnSlotZero(player, 20L);
	    		}
	    	}, 2L);
    	}
    }

   /**
    * Removes custom crafting items from the player when they enter creative mode.
    * 
    * @param event - PlayerGameModeChangeEvent
    */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    private void onSwitchGamemode(PlayerGameModeChangeEvent event) {
    	final Player player = (Player) event.getPlayer();
    	if (event.getNewGameMode() == GameMode.CREATIVE) {
    		creativeCraftingItems.put(PlayerHandler.getPlayer().getPlayerID(player), craftingItems.get(PlayerHandler.getPlayer().getPlayerID(player)));
    		ItemHandler.getItem().removeCraftItems(player);
    	} else if (event.getNewGameMode() != GameMode.CREATIVE && creativeCraftingItems.containsKey(PlayerHandler.getPlayer().getPlayerID(player))) {
    		this.returnCrafting(event.getPlayer(), creativeCraftingItems.get(PlayerHandler.getPlayer().getPlayerID(player)), 1L, false);
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
	private void handleClose(final Consumer < Integer > input, final Player player, final InventoryView view, final ItemStack[] inventory, final boolean slotZero) {
		if (PlayerHandler.getPlayer().isCraftingInv(view)) {
			if (!ItemHandler.getItem().isContentsEmpty(inventory)) {
				boolean isCrafting = false;
				for (int i = 0; i <= 4; i++) {
					for (ItemMap itemMap: ItemUtilities.getUtilities().getCraftingItems()) {
						if ((itemMap.isCraftingItem() && itemMap.isReal(inventory[i]))) {
							isCrafting = true;
							input.accept(i);
						}
					}
				}
				for (int i = 0; i <= 4; i++) {
					if (isCrafting && i != 0 && inventory[i] != null && inventory[i].getType() != Material.AIR) {
						ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(inventory[i], null, player.getWorld());
						if (itemMap == null || !itemMap.isCraftingItem() || !itemMap.isReal(inventory[i])) {
							final int k = i;
							ItemStack drop = inventory[i].clone();
							ServerHandler.getServer().runThread(main -> {
								player.getOpenInventory().getTopInventory().setItem(k, new ItemStack(Material.AIR));
								if (player.getInventory().firstEmpty() != -1) {
									player.getInventory().addItem(drop);
								} else {
									Item itemDropped = player.getWorld().dropItem(player.getLocation(), drop);
									itemDropped.setPickupDelay(40);
								}
							});
							inventory[i] = new ItemStack(Material.AIR);
						}
					}
				}
				this.returnCrafting(player, inventory, 1L, !slotZero);
			}
		} else {
			ServerHandler.getServer().runThread(main -> {
				if (PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory()) && craftingOpenItems.containsKey(PlayerHandler.getPlayer().getPlayerID(player))) {
					ItemStack[] openCraftContents = craftingOpenItems.get(PlayerHandler.getPlayer().getPlayerID(player));
					if (openCraftContents != null && openCraftContents.length != 0) {
						this.returnCrafting(player, openCraftContents, 1L, false);
						craftingItems.put(PlayerHandler.getPlayer().getPlayerID(player), craftingOpenItems.get(PlayerHandler.getPlayer().getPlayerID(player)));
						craftingOpenItems.remove(PlayerHandler.getPlayer().getPlayerID(player));
					}
				}
			});
		}
	}
	

   /**
	* Returns the zero slot item to prevent ghosting.
	* 
	* @param player - The player having the zero slot item returned.
	*/
    private HashMap<String, Boolean> pendingZero = new HashMap<String, Boolean>();
    private void returnSlotZero(final Player player, final long delay) {
    	if ((this.pendingZero.get(PlayerHandler.getPlayer().getPlayerID(player)) != null && !this.pendingZero.get(PlayerHandler.getPlayer().getPlayerID(player)))
    	  || this.pendingZero.get(PlayerHandler.getPlayer().getPlayerID(player)) == null) {
    		this.pendingZero.put(PlayerHandler.getPlayer().getPlayerID(player), true);
    		ServerHandler.getServer().runThread(main_2 -> {
	    		for (ItemMap craftMap: ItemUtilities.getUtilities().getCraftingItems()) {
		    		if (Utils.getUtils().getSlotConversion(craftMap.getSlot()) == 0 && craftMap.inWorld(player.getWorld()) && craftMap.hasPermission(player)) {
		    			craftMap.giveTo(player);
		    			this.pendingZero.remove(PlayerHandler.getPlayer().getPlayerID(player));
		    		}
	    		}
    		}, delay);
    	}
    }
	
   /**
    * Returns the custom crafting item to the player after the specified delay.
    * 
    * @param player - the Player having their item returned.
    * @param contents - the crafting contents to be returned.
    * @param delay - the delay to wait before returning the item.
    */
	private void returnCrafting(final Player player, final ItemStack[] contents, final long delay, final boolean slotZero) {
		ServerHandler.getServer().runThread(main -> {
			if (!player.isOnline()) { return; } else if (!PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) { this.returnCrafting(player, contents, 10L, slotZero); return; }
			if (!slotZero) {
				for (int i = 4; i >= 0; i--) {
					player.getOpenInventory().getTopInventory().setItem(i, contents[i]);
					PlayerHandler.getPlayer().updateInventory(player, ItemUtilities.getUtilities().getItemMap(contents[i], null, player.getWorld()), 1L);
				}
			} else { 
				player.getOpenInventory().getTopInventory().setItem(0, contents[0]); 
				PlayerHandler.getPlayer().updateInventory(player, ItemUtilities.getUtilities().getItemMap(contents[0], null, player.getWorld()), 1L);
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
    
    private void dropItem(final Player player, final ItemStack item) { 
    	Location location = player.getLocation();
    	location.setY(location.getY() + 1);
    	Item dropped = player.getWorld().dropItem(location, item);
		dropped.setVelocity(location.getDirection().multiply(.30));
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