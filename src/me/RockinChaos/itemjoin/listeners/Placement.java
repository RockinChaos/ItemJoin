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

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Placement implements Listener {

    /**
	 * Prevents the player from placing the custom item.
	 * 
	 * @param event - PlayerInteractEvent
	 */
	 @EventHandler(ignoreCancelled = true)
	 private void onPreventPlayerPlace(PlayerInteractEvent event) {
	 	ItemStack item = event.getItem();
	 	Player player = event.getPlayer();
	 	if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !ItemUtilities.getUtilities().isAllowed(player, item, "placement")) {
	 		event.setCancelled(true);
	 		PlayerHandler.updateInventory(player, 1L);
	 	}
	 }
	
	/**
	 * Refills the custom item to its original stack size when placing the item.
	 * 
	 * @param event - PlayerInteractEvent
	 */
	 @EventHandler(ignoreCancelled = false)
	 private void onCountLock(PlayerInteractEvent event) {
	 	final ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
	 	final Player player = event.getPlayer();
	 	final int slot = player.getInventory().getHeldItemSlot();
	 	if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK && !PlayerHandler.isCreativeMode(player)) {
	 		if (!ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) {
	 			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
	 			item.setAmount(itemMap.getCount(player));
				SchedulerUtils.run(() -> {
		 			if (StringUtils.containsIgnoreCase(item.getType().name(), "WATER") || StringUtils.containsIgnoreCase(item.getType().name(), "LAVA") || item.getType().name().equalsIgnoreCase("BUCKET") 
		 			 || StringUtils.containsIgnoreCase(item.getType().name(), "POTION")) {
		 				if (player.getInventory().getHeldItemSlot() == slot) {
		 					PlayerHandler.setMainHandItem(player, item);
		 				} else if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
		 					player.getInventory().setItem(slot, item);
		 				}
		 			} else if (itemMap != null) { 
		 				if (PlayerHandler.getHandItem(player) == null || PlayerHandler.getHandItem(player).getAmount() <= 1) {
		 					if (ServerUtils.hasSpecificUpdate("1_9")) { 
		 						if (event.getHand().equals(EquipmentSlot.HAND)) {
		 						if (player.getInventory().getHeldItemSlot() == slot) {
		 							PlayerHandler.setMainHandItem(player, item);
		 						} else if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
		 							player.getInventory().setItem(slot, item);
		 						}
		 						} else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
		 							PlayerHandler.setOffHandItem(player, item);
		 						}
		 					} 
		 					else { 
		 						if (player.getInventory().getHeldItemSlot() == slot) {
		 							PlayerHandler.setMainHandItem(player, item);
		 						} else if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
		 							player.getInventory().setItem(slot, item);
		 						}
		 					}
		 				} else if (itemMap.isSimilar(player, PlayerHandler.getHandItem(player))) { 
		 					PlayerHandler.getHandItem(player).setAmount(itemMap.getCount(player)); 
		 				}
		 			}
		 		});
			}
	 	}
	 }
	 
	/**
	 * Prevents the player from placing a custom item inside an itemframe.
	 * 
	 * @param event - PlayerInteractEntityEvent
	 */
	 @EventHandler(ignoreCancelled = true)
	 private void onFramePlace(PlayerInteractEntityEvent event) {
	 	if (event.getRightClicked() instanceof ItemFrame) {
	 		try {
	 			ItemStack item = null;
	 			if (ServerUtils.hasSpecificUpdate("1_9")) { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
	 			else { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), ""); }
	 			Player player = event.getPlayer();
	 			if (!ItemUtilities.getUtilities().isAllowed(player, item, "placement")) {
	 				event.setCancelled(true);
	 				PlayerHandler.updateInventory(player, 1L);
	 			}
	 		} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
	 	}
	 }
	 
	/**
	 * Refills the custom item to its original stack size when placing the item into a itemframe.
	 * 
	 * @param event - PlayerInteractEntityEvent
	 */
	 @EventHandler(ignoreCancelled = false)
	 private void onFrameLock(PlayerInteractEntityEvent event) {
	 	if (event.getRightClicked() instanceof ItemFrame) {
	 		try {
	 			ItemStack item = null;
	 			if (ServerUtils.hasSpecificUpdate("1_9")) { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
	 			else { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), ""); }
	 			Player player = event.getPlayer();
	 			if (PlayerHandler.isCreativeMode(player)) {
	 				if (!ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) {
	 					ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
	 					if (itemMap != null) { 
		 					if (PlayerHandler.getHandItem(player) == null || PlayerHandler.getHandItem(player).getAmount() <= 1) {
		 						if (ServerUtils.hasSpecificUpdate("1_9")) { 
		 							if (event.getHand().equals(EquipmentSlot.HAND)) {
		 								PlayerHandler.setMainHandItem(player, item);
		 							} else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
		 								PlayerHandler.setOffHandItem(player, item);
		 							}
		 						} 
		 						else { PlayerHandler.setMainHandItem(player, item); }
		 					} else if (itemMap.isSimilar(player, PlayerHandler.getHandItem(player))) { 
		 						PlayerHandler.getHandItem(player).setAmount(itemMap.getCount(player)); 
		 					} 
	 					}
	 				}
	 			}
	 		} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
	 	}
	 }
}