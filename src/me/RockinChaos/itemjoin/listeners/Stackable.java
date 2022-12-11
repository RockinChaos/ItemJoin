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

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;

public class Stackable implements Listener {

   /**
	* Stacks custom items with the Stackable itemflag defined.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(ignoreCancelled = true) 
	private void onClickStackable(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final InventoryAction action = event.getAction();
		final int rawSlot = event.getRawSlot();
		final int slot = event.getSlot();
		if (event.getCurrentItem() != null && event.getCursor() != null && event.getCurrentItem().getType() != Material.AIR && event.getCursor().getType() != Material.AIR) {
			final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getCursor(), null, player.getWorld());
			if (itemMap != null && itemMap.isSimilar(player, event.getCurrentItem()) && !ItemUtilities.getUtilities().isAllowed(player, event.getCursor(), "stackable")) {
				event.setCancelled(true);
				ItemHandler.stackItems(player, event.getCursor(), event.getCurrentItem(), -1, false);
			}
		} else if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && !ItemUtilities.getUtilities().isAllowed(player, event.getCurrentItem(), "stackable")) {
			event.setCancelled(true);
			final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getCurrentItem(), null, player.getWorld());
			int REMAINING_STACK_SIZE = event.getCurrentItem().getAmount();
			if (itemMap != null && PlayerHandler.isCraftingInv(event.getView())) {
				if (slot > 8) {
					for (int i = 0; i < 8; i++) {
						if (itemMap.isSimilar(player, player.getInventory().getItem(i))) {
							REMAINING_STACK_SIZE = ItemHandler.stackItems(player, event.getCurrentItem(), player.getInventory().getItem(i), slot, false);
							if (REMAINING_STACK_SIZE <= 0) {
								break;
							}
						}
					}
				} else {
					for (int i = 8; i < 36; i++) {
						if (itemMap.isSimilar(player, player.getInventory().getItem(i))) {
							REMAINING_STACK_SIZE = ItemHandler.stackItems(player, event.getCurrentItem(), player.getInventory().getItem(i), slot, false);
							if (REMAINING_STACK_SIZE <= 0) {
								break;
							}
						}
					}
				} 
			} else if (itemMap != null) {
				if (rawSlot >= player.getOpenInventory().getTopInventory().getSize()) {
					for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
						if (itemMap.isSimilar(player, player.getOpenInventory().getTopInventory().getItem(i))) {
							REMAINING_STACK_SIZE = ItemHandler.stackItems(player, event.getCurrentItem(), player.getOpenInventory().getTopInventory().getItem(i), slot, false);
							if (REMAINING_STACK_SIZE <= 0) {
								break;
							}
						}
					}
				} else {
					for (int i = 0; i < player.getOpenInventory().getBottomInventory().getSize(); i++) {
						if (itemMap.isSimilar(player, player.getOpenInventory().getBottomInventory().getItem(i))) {
							REMAINING_STACK_SIZE = ItemHandler.stackItems(player, event.getCurrentItem(), player.getOpenInventory().getBottomInventory().getItem(i), slot, true);
							if (REMAINING_STACK_SIZE <= 0) {
								break;
							}
						}
					}
				}
			}
			if (REMAINING_STACK_SIZE > 0) { event.setCancelled(false); }
			PlayerHandler.updateInventory(player, 1L);
		} else if (action != InventoryAction.PLACE_ONE && action != InventoryAction.COLLECT_TO_CURSOR && event.getCursor() != null && event.getCursor().getType() != Material.AIR && (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)) {
			final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getCursor(), null, player.getWorld());
			if (itemMap != null && !ItemUtilities.getUtilities().isAllowed(player, event.getCursor(), "stackable")) {
				event.setCancelled(true);
				player.getOpenInventory().setItem(rawSlot, player.getOpenInventory().getCursor().clone());
				player.getOpenInventory().setCursor(new ItemStack(Material.AIR));
			}
		} else if (action == InventoryAction.COLLECT_TO_CURSOR && !ItemUtilities.getUtilities().isAllowed(player, event.getCursor(), "stackable")) {
			final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getCursor(), null, player.getWorld());
			if (itemMap != null) {
				for (int i = 0; i < 36; i++) {
					if (itemMap.isSimilar(player, player.getInventory().getItem(i))) {
						if (event.getCursor().getAmount() == 64) {
							break;
						} else if (player.getInventory().getItem(i).getAmount() != 64) {
							ItemHandler.stackItems(player, player.getInventory().getItem(i), event.getCursor(), i, false);
						}
					}
				}
				for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
					if (itemMap.isSimilar(player, player.getOpenInventory().getTopInventory().getItem(i))) {
						if (event.getCursor().getAmount() == 64) {
							break;
						} else if (player.getOpenInventory().getTopInventory().getItem(i).getAmount() != 64) {
							ItemHandler.stackItems(player, player.getOpenInventory().getTopInventory().getItem(i), event.getCursor(), i, true);
						}
					}
				}
			}
		}
	}
	
   /**
	* Stacks custom items with the Stackable itemflag defined.
	* 
	* @param event - EntityPickupItemEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onPickupStackable(EntityPickupItemEvent event) {
		Entity entity = event.getEntity();
		ItemStack item1 = event.getItem().getItemStack();
		if (entity instanceof Player && item1 != null && item1.getType() != Material.AIR && !ItemUtilities.getUtilities().isAllowed((Player) event.getEntity(), item1, "stackable")) {
			Player player = (Player) event.getEntity();
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item1, null, player.getWorld());
			int REMAINING_STACK_SIZE = item1.getAmount();
			event.setCancelled(true);
			for (int i = 0; i < 36; i++) {
				if (itemMap != null && itemMap.isSimilar(player, player.getInventory().getItem(i))) {
					REMAINING_STACK_SIZE = ItemHandler.stackItems(player, item1, player.getInventory().getItem(i), -2, false);
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