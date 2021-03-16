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
package me.RockinChaos.itemjoin.listeners.legacy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;

/**
* Handles the Stackable events for custom items.
* 
* @deprecated This is a LEGACY listener, only use on Minecraft versions below 1.12.
*/
public class Legacy_Stackable implements Listener {

   /**
	* Stacks custom items with the Stackable itemflag defined.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onClickStackable(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (event.getCurrentItem() != null && event.getCursor() != null && event.getCurrentItem().getType() != Material.AIR && event.getCursor().getType() != Material.AIR) {
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getCursor(), null, player.getWorld());
			if (itemMap != null && itemMap.isSimilar(event.getCurrentItem()) && !ItemUtilities.getUtilities().isAllowed(player, event.getCursor(), "stackable")) {
				event.setCancelled(true);
				ItemHandler.stackItems(player, event.getCursor(), event.getCurrentItem(), -1);
			}
		} else if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && !ItemUtilities.getUtilities().isAllowed(player, event.getCurrentItem(), "stackable")) {
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getCurrentItem(), null, player.getWorld());
			event.setCancelled(true);
			int REMAINING_STACK_SIZE = event.getCurrentItem().getAmount();
			if (itemMap != null && event.getSlot() > 8 && event.getView().getType().name().equalsIgnoreCase("CRAFTING")) {
				for (int i = 0; i < 8; i++) {
					if (itemMap.isSimilar(player.getInventory().getItem(i))) {
						REMAINING_STACK_SIZE = ItemHandler.stackItems(player, event.getCurrentItem(), player.getInventory().getItem(i), event.getSlot());
						if (REMAINING_STACK_SIZE <= 0) {
							break;
						}
					}
				}
			} else if (itemMap != null && event.getView().getType().name().equalsIgnoreCase("CRAFTING")) {
				for (int i = 8; i < 36; i++) {
					if (itemMap.isSimilar(player.getInventory().getItem(i))) {
						REMAINING_STACK_SIZE = ItemHandler.stackItems(player, event.getCurrentItem(), player.getInventory().getItem(i), event.getSlot());
						if (REMAINING_STACK_SIZE <= 0) {
							break;
						}
					}
				}
			}
			if (REMAINING_STACK_SIZE > 0) { event.setCancelled(false); }
			PlayerHandler.updateInventory(player, 1L);
		}
	}
	
   /**
	* Stacks custom items with the Stackable itemflag defined.
	* 
	* @param event - PlayerPickupItemEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.12.
	*/
	@EventHandler(ignoreCancelled = true)
	private void onPickupStackable(org.bukkit.event.player.PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		ItemStack item1 = event.getItem().getItemStack();
		if (item1 != null && item1.getType() != Material.AIR && !ItemUtilities.getUtilities().isAllowed(player, item1, "stackable")) {
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item1, null, player.getWorld());
			int REMAINING_STACK_SIZE = item1.getAmount();
			event.setCancelled(true);
			for (int i = 0; i < 36; i++) {
				if (itemMap != null && itemMap.isSimilar(player.getInventory().getItem(i))) {
					REMAINING_STACK_SIZE = ItemHandler.stackItems(player, item1, player.getInventory().getItem(i), -2);
					if (REMAINING_STACK_SIZE <= 0) {
						break;
					}
				}
			}
			if (REMAINING_STACK_SIZE > 0) { event.setCancelled(false); } else { event.getItem().remove(); }
			PlayerHandler.updateInventory(player, 1L);
		}
	}
}