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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.events.PlayerPickItemEvent;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;

public class Clicking implements Listener {

	private Map < String, Boolean > droppedItem = new HashMap < String, Boolean > ();
	private Map < String, Boolean > dropClick = new HashMap < String, Boolean > ();
	private static HashMap < String, ItemStack > cursorItem = new HashMap < String, ItemStack > ();
	
   /**
	* Prevents the player from moving all items in their inventory.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onGlobalModify(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
	  	if (StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), "TRUE") || StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), player.getWorld().getName())
		  			|| StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), "ALL") || StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), "GLOBAL")) {
	  		if (ConfigHandler.getConfig().isPreventOP() && player.isOp() || ConfigHandler.getConfig().isPreventCreative() && PlayerHandler.isCreativeMode(player)) { } 
	  		else if (player.getOpenInventory().getTitle().contains("§") || player.getOpenInventory().getTitle().contains("&")) { }
	  		else { event.setCancelled(true); }
	  	}
	}

   /**
	* Prevents the player from using the pick block feature to move ANY items in their inventory.
	* 
	* @param event - PlayerPickItemEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onGlobalPickItem(PlayerPickItemEvent event) {
		Player player = event.getPlayer();
	  	if (StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), "TRUE") || StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), player.getWorld().getName())
		  			|| StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), "ALL") || StringUtils.containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), "GLOBAL")) {
	  		if (ConfigHandler.getConfig().isPreventOP() && player.isOp() || ConfigHandler.getConfig().isPreventCreative() && PlayerHandler.isCreativeMode(player)) { }
	  		else { event.setCancelled(true); }
	  	}
	}

   /**
	* Prevents the player from moving the custom item in their inventory.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onModify(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		List<ItemStack> items = new ArrayList<ItemStack>();
		if (!this.isCreativeDupe(event)) {
			items.add(event.getCurrentItem()); items.add(event.getCursor());
			if (StringUtils.containsIgnoreCase(event.getAction().name(), "HOTBAR")) {
				if (event.getView().getBottomInventory().getSize() >= event.getHotbarButton() && event.getHotbarButton() >= 0) { items.add(event.getView().getBottomInventory().getItem(event.getHotbarButton())); }
				else if (ServerUtils.hasSpecificUpdate("1_9")) { items.add(PlayerHandler.getOffHandItem(player)); } 
			}
			if (!ServerUtils.hasSpecificUpdate("1_8")) { PlayerHandler.updateInventory(player, 1L); }
			this.LegacyDropEvent(player);
			for (ItemStack item : items) {
				if (!ItemUtilities.getUtilities().isAllowed(player, item, "inventory-modify")) {
					event.setCancelled(true);
					if (player.getOpenInventory().getType().name().equalsIgnoreCase("CHEST") && !player.getOpenInventory().getTitle().equalsIgnoreCase("CHEST")) {
						final ItemStack itemCopy = item.clone();
						SchedulerUtils.run(() -> {
							for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
								if (player.getOpenInventory().getTopInventory().getItem(i) != null && player.getOpenInventory().getTopInventory().getItem(i).equals(item)) {
									player.getOpenInventory().getTopInventory().setItem(i, new ItemStack(Material.AIR));
									player.getOpenInventory().getBottomInventory().setItem(event.getSlot(), itemCopy); 
								}
							}
						});
					}
					if (PlayerHandler.isCreativeMode(player)) { player.closeInventory(); }
					else if (!ItemUtilities.getUtilities().isAllowed(player, item, "inventory-close")) { 
						player.openInventory(Bukkit.createInventory(player, 9, "INVENTORY-CLOSE"));
						player.closeInventory(); 
					}
					PlayerHandler.updateInventory(player, 1L);
					break;
				}
			}
		}
	}
	
   /**
	* Prevents the player from duplicating immobile items while in creative.
	* 
	* @param event - InventoryClickEvent
	*/
	public boolean isCreativeDupe(final InventoryClickEvent event) {
		if (PlayerHandler.isCreativeMode((Player) event.getWhoClicked()) && event.getCurrentItem() != null && event.getCursor() != null) {
			String currentNBT = (ItemHandler.dataTagsEnabled() ? ItemHandler.getNBTData(event.getCurrentItem()) 
					: ((event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) ? StringUtils.colorDecode(event.getCurrentItem()) : null));
			String cursorNBT = (ItemHandler.dataTagsEnabled() ? ItemHandler.getNBTData(event.getCursor()) 
					: ((event.getCursor().hasItemMeta() && event.getCursor().getItemMeta().hasDisplayName()) ? StringUtils.colorDecode(event.getCursor()) : null));
			if (currentNBT != null && cursorNBT != null) {
				return currentNBT.equalsIgnoreCase(cursorNBT);
			}	
		}
		return false;
	}
	
   /**
	* Prevents the player from using the pick block feature to move an item in their inventory.
	* 
	* @param event - PlayerPickItemEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onPickItem(PlayerPickItemEvent event) {
		final Player player = event.getPlayer();
		final ItemStack itemCopy = (event.getPickHand() != null ? event.getPickHand().clone() : event.getPickHand());
		final Material pickMaterial = event.getTargetBlock().getType();
		if (!ItemUtilities.getUtilities().isAllowed(player, itemCopy, "inventory-modify")) {
			for (int i = 0; i <= 8; i++) {
				if (event.getContents()[i] != null && event.getContents()[i].getType() == pickMaterial) {
					break;
				} else if (i == 8) {
					event.setCancelled(true);
				}
			}
		} else {
			SchedulerUtils.run(() -> {
				final ItemStack itemCopy_2 = (event.getPickHand() != null ? event.getPickHand().clone() : event.getPickHand());
				if (!ItemUtilities.getUtilities().isAllowed(player, itemCopy_2, "inventory-modify")) {
					final int pickSlot = event.getPickSlot();
					if (pickSlot != -1) {
						player.getInventory().setItem(pickSlot, itemCopy_2);
						PlayerHandler.setMainHandItem(player, itemCopy);
					}
				}
			});
		}
	}
	
   /**
	* Automatically updates any animated or dynamic items that reside on the players cursor.
	* 
	* @param event - InventoryClickEvent
	*/
    @EventHandler(ignoreCancelled = true)
    private final void onCursorAnimatedItem(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		String itemflag = "inventory-modify";
    	if (event.getAction().toString().contains("PLACE_ALL") || event.getAction().toString().contains("PLACE_ONE")) {
    		ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getCursor(), null, player.getWorld());
    		if (itemMap != null && itemMap.isSimilar(cursorItem.get(PlayerHandler.getPlayerID(player)))) {
    		final int slot = event.getSlot();
    		event.setCancelled(true);
    		player.setItemOnCursor(new ItemStack(Material.AIR));
    		if (event.getRawSlot() <= 4 && event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getTopInventory().getSize() - 1 >= event.getRawSlot()) {
    			player.getOpenInventory().getTopInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));
    		} else { player.getInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));   }
			cursorItem.remove(PlayerHandler.getPlayerID(player));
			ServerUtils.logDebug("{ItemMap} (Cursor_Place): Updated Animation Item."); 
    		}
    	} else if (event.getAction().toString().contains("SWAP_WITH_CURSOR")) {
    		ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getCursor(), null, player.getWorld());
    		if (itemMap != null && itemMap.isSimilar(cursorItem.get(PlayerHandler.getPlayerID(player))) && ItemUtilities.getUtilities().isAllowed(player, event.getCurrentItem(), itemflag)) {
    		final int slot = event.getSlot();
    		final ItemStack item = new ItemStack(event.getCurrentItem());
    		event.setCancelled(true);
    		player.setItemOnCursor(item);
    		if (event.getRawSlot() <= 4 && event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getTopInventory().getSize() - 1 >= event.getRawSlot()) {
    			player.getOpenInventory().getTopInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));
    		} else { player.getInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));   }
			cursorItem.remove(PlayerHandler.getPlayerID(player));
			ServerUtils.logDebug("{ItemMap} (Cursor_Swap): Updated Animation Item."); 
    		}
    	}
    }

   /**
    * Resolves bugs with older versions of Bukkit, removes all iutems and resets them to prevent duplicating.
    * 
    * @param player - that is dropping the item.
    */
    private void LegacyDropEvent(final Player player) {
    	if (!ServerUtils.hasSpecificUpdate("1_9")) {
			dropClick.put(PlayerHandler.getPlayerID(player), true);
			final ItemStack[] Inv = player.getInventory().getContents().clone();
			final ItemStack[] Armor = player.getInventory().getArmorContents().clone();
			SchedulerUtils.runLater(1L, () -> {
				if (this.dropClick.get(PlayerHandler.getPlayerID(player)) != null && this.dropClick.get(PlayerHandler.getPlayerID(player)) == true 
					&& this.droppedItem.get(PlayerHandler.getPlayerID(player)) != null && this.droppedItem.get(PlayerHandler.getPlayerID(player)) == true) {
					player.getInventory().clear();
					player.getInventory().setHelmet(null);
					player.getInventory().setChestplate(null);
					player.getInventory().setLeggings(null);
					player.getInventory().setBoots(null);
					if (ServerUtils.hasSpecificUpdate("1_9")) { player.getInventory().setItemInOffHand(null); }
					player.getInventory().setContents(Inv);
					player.getInventory().setArmorContents(Armor);
					PlayerHandler.updateInventory(player, 1L);
					this.droppedItem.remove(PlayerHandler.getPlayerID(player));
				}
				this.dropClick.remove(PlayerHandler.getPlayerID(player));
			});
    	}
	}
    
   /**
    * Gets the current HashMap of players and their cursor items.
    * 
    * @param player - that will have their item updated.
    * @return The HashMap containing the players and their current cursor items.
    */
    public static ItemStack getCursor(String player) {
    	return cursorItem.get(player);
    }
    
    /**
     * Puts the player into the cursor HashMap with their current cursor item.
     * 
     * @param player - that is having their item updated.
     * @param item - that is being updated.
     */
     public static void putCursor(String player, ItemStack item) {
     	cursorItem.put(player, item);
     }
}