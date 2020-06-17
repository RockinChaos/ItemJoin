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

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.Utils;

public class InventoryClick implements Listener {

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
	  	if (Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getPrevent("itemMovement"), "TRUE") || Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getPrevent("itemMovement"), player.getWorld().getName())
		  			|| Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getPrevent("itemMovement"), "ALL") || Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getPrevent("itemMovement"), "GLOBAL")) {
	  		if (ConfigHandler.getConfig(false).isPreventOP() && player.isOp() || ConfigHandler.getConfig(false).isPreventCreative() && PlayerHandler.getPlayer().isCreativeMode(player)) { } 
	  		else if (player.getOpenInventory().getTitle().contains("§") || player.getOpenInventory().getTitle().contains("&")) { }
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
		items.add(event.getCurrentItem()); items.add(event.getCursor());
		if (Utils.getUtils().containsIgnoreCase(event.getAction().name(), "HOTBAR")) { items.add(event.getView().getBottomInventory().getItem(event.getHotbarButton())); }
		if (!ServerHandler.getServer().hasSpecificUpdate("1_8")) { PlayerHandler.getPlayer().updateInventory(player, 1L); }
		this.LegacyDropEvent(player);
		for (ItemStack item : items) {
			if (!ItemUtilities.getUtilities().isAllowed(player, item, "inventory-modify")) {
				event.setCancelled(true);
				if (PlayerHandler.getPlayer().isCreativeMode(player)) { player.closeInventory(); }
				else if (!ItemUtilities.getUtilities().isAllowed(player, item, "inventory-close")) { player.closeInventory(); }
				PlayerHandler.getPlayer().updateInventory(player, 1L);
			}
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
    		if (itemMap != null && itemMap.isSimilar(cursorItem.get(PlayerHandler.getPlayer().getPlayerID(player)))) {
    		final int slot = event.getSlot();
    		event.setCancelled(true);
    		player.setItemOnCursor(new ItemStack(Material.AIR));
    		if (event.getRawSlot() <= 4 && event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getTopInventory().getSize() - 1 >= event.getRawSlot()) {
    			player.getOpenInventory().getTopInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayer().getPlayerID(player)));
    		} else { player.getInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayer().getPlayerID(player)));   }
			cursorItem.remove(PlayerHandler.getPlayer().getPlayerID(player));
			ServerHandler.getServer().logDebug("{ItemMap} (Cursor_Place): Updated Animation Item."); 
    		}
    	} else if (event.getAction().toString().contains("SWAP_WITH_CURSOR")) {
    		ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getCursor(), null, player.getWorld());
    		if (itemMap != null && itemMap.isSimilar(cursorItem.get(PlayerHandler.getPlayer().getPlayerID(player))) && ItemUtilities.getUtilities().isAllowed(player, event.getCurrentItem(), itemflag)) {
    		final int slot = event.getSlot();
    		final ItemStack item = new ItemStack(event.getCurrentItem());
    		event.setCancelled(true);
    		player.setItemOnCursor(item);
    		if (event.getRawSlot() <= 4 && event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getTopInventory().getSize() - 1 >= event.getRawSlot()) {
    			player.getOpenInventory().getTopInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayer().getPlayerID(player)));
    		} else { player.getInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayer().getPlayerID(player)));   }
			cursorItem.remove(PlayerHandler.getPlayer().getPlayerID(player));
			ServerHandler.getServer().logDebug("{ItemMap} (Cursor_Swap): Updated Animation Item."); 
    		}
    	}
    }

   /**
    * Resolves bugs with older versions of Bukkit, removes all iutems and resets them to prevent duplicating.
    * 
    * @param player - that is dropping the item.
    */
    private void LegacyDropEvent(final Player player) {
    	if (!ServerHandler.getServer().hasSpecificUpdate("1_9")) {
			dropClick.put(PlayerHandler.getPlayer().getPlayerID(player), true);
			final ItemStack[] Inv = player.getInventory().getContents().clone();
			final ItemStack[] Armor = player.getInventory().getArmorContents().clone();
			Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
				@Override
				public void run() {
					if (dropClick.get(PlayerHandler.getPlayer().getPlayerID(player)) != null && dropClick.get(PlayerHandler.getPlayer().getPlayerID(player)) == true 
						&& droppedItem.get(PlayerHandler.getPlayer().getPlayerID(player)) != null && droppedItem.get(PlayerHandler.getPlayer().getPlayerID(player)) == true) {
						player.getInventory().clear();
						player.getInventory().setHelmet(null);
						player.getInventory().setChestplate(null);
						player.getInventory().setLeggings(null);
						player.getInventory().setBoots(null);
						if (ServerHandler.getServer().hasSpecificUpdate("1_9")) { player.getInventory().setItemInOffHand(null); }
						player.getInventory().setContents(Inv);
						player.getInventory().setArmorContents(Armor);
						PlayerHandler.getPlayer().updateInventory(player, 1L);
						droppedItem.remove(PlayerHandler.getPlayer().getPlayerID(player));
					}
					dropClick.remove(PlayerHandler.getPlayer().getPlayerID(player));
				}
			}, 1L);
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