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

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.Utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
	 		PlayerHandler.getPlayer().updateInventory(player, 1L);
	 	}
	 }
	
	/**
	 * Refills the custom item to its original stack size when placing the item.
	 * 
	 * @param event - PlayerInteractEvent
	 */
	 @EventHandler(ignoreCancelled = false)
	 private void onCountLock(PlayerInteractEvent event) {
	 	ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
	 	Player player = event.getPlayer();
	 	if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK && !PlayerHandler.getPlayer().isCreativeMode(player)) {
	 		if (!ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) {
	 			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item, null, player.getWorld());
	 			item.setAmount(itemMap.getCount());
				if (ItemJoin.getInstance().isEnabled()) {
					Bukkit.getServer().getScheduler().runTaskLater(ItemJoin.getInstance(), () -> {
		 				if (Utils.getUtils().containsIgnoreCase(item.getType().name(), "WATER") || Utils.getUtils().containsIgnoreCase(item.getType().name(), "LAVA") || item.getType().name().equalsIgnoreCase("BUCKET") 
		 				 || Utils.getUtils().containsIgnoreCase(item.getType().name(), "POTION")) {
		 					PlayerHandler.getPlayer().setMainHandItem(player, item);
		 				} else if (itemMap != null) { 
		 					if (PlayerHandler.getPlayer().getHandItem(player) == null || PlayerHandler.getPlayer().getHandItem(player).getAmount() <= 1) {
		 						if (ServerHandler.getServer().hasSpecificUpdate("1_9")) { 
		 							if (event.getHand().equals(EquipmentSlot.HAND)) {
		 								PlayerHandler.getPlayer().setMainHandItem(player, item);
		 							} else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
		 								PlayerHandler.getPlayer().setOffHandItem(player, item);
		 							}
		 						} 
		 						else { PlayerHandler.getPlayer().setMainHandItem(player, item); }
		 					} else if (itemMap.isSimilar(PlayerHandler.getPlayer().getHandItem(player))) { 
		 						PlayerHandler.getPlayer().getHandItem(player).setAmount(itemMap.getCount()); 
		 					} 
		 				}
		 			}, 2L);
				}
			}
	 	}
	 }
	 
	/**
	 * Refills the custom arrow item to its original stack size when using a crossbow.
	 * 
	 * @param event - PlayerInteractEvent
	 */
	 @EventHandler(ignoreCancelled = false)
	 private void onCrossbow(PlayerInteractEvent event) {
	 	ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
	 	Player player = event.getPlayer();
	 	if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && item != null && item.getType().name().equalsIgnoreCase("CROSSBOW") && !PlayerHandler.getPlayer().isCreativeMode(player)) {
			HashMap < Integer, ItemStack > map = new HashMap < Integer, ItemStack > ();
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).getType() == Material.ARROW) {
					ItemStack cloneStack = player.getInventory().getItem(i).clone();
					ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(player.getInventory().getItem(i), null, player.getWorld());
					if (itemMap != null) { cloneStack.setAmount(itemMap.getCount()); }
					map.put(i, cloneStack);
				}
			}
			this.crossyAction(player, map, 10);
	 	}
	 }
	 
	/**
	 * Checks if the Crossbow action was triggered.
	 * 
	 * @param player - The player performing the action.
	 * @param map - The slot and stack to be compared to the players inventory.
	 * @param tries - The number of remaining attempts to refill Crossbow Arrows.
	 */
	 public void crossyAction(Player player, HashMap < Integer, ItemStack > map, int tries) {
	 	if (tries != 0) {
			if (ItemJoin.getInstance().isEnabled()) {
				Bukkit.getServer().getScheduler().runTaskLater(ItemJoin.getInstance(), () -> {
		 			boolean arrowReturned = false;
		 			for (Integer key: map.keySet()) {
		 				if (player.getInventory().getItem(key) == null || player.getInventory().getItem(key).getAmount() != map.get(key).getAmount()) {
		 					if (!ItemUtilities.getUtilities().isAllowed(player, map.get(key), "count-lock")) {
		 						player.getInventory().setItem(key, map.get(key));
		 						arrowReturned = true;
		 					}
		 				}
		 			}
		 			if (arrowReturned) { PlayerHandler.getPlayer().updateInventory(player, 1L); } 
		 			else {this.crossyAction(player, map, (tries - 1)); }
		 		}, 26L);
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
	 			if (ServerHandler.getServer().hasSpecificUpdate("1_9")) { item = PlayerHandler.getPlayer().getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
	 			else { item = PlayerHandler.getPlayer().getPerfectHandItem(event.getPlayer(), ""); }
	 			Player player = event.getPlayer();
	 			if (!ItemUtilities.getUtilities().isAllowed(player, item, "placement")) {
	 				event.setCancelled(true);
	 				PlayerHandler.getPlayer().updateInventory(player, 1L);
	 			}
	 		} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
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
	 			if (ServerHandler.getServer().hasSpecificUpdate("1_9")) { item = PlayerHandler.getPlayer().getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
	 			else { item = PlayerHandler.getPlayer().getPerfectHandItem(event.getPlayer(), ""); }
	 			Player player = event.getPlayer();
	 			if (PlayerHandler.getPlayer().isCreativeMode(player)) {
	 				if (!ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) {
	 					ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item, null, player.getWorld());
	 					if (itemMap != null) { 
		 					if (PlayerHandler.getPlayer().getHandItem(player) == null || PlayerHandler.getPlayer().getHandItem(player).getAmount() <= 1) {
		 						if (ServerHandler.getServer().hasSpecificUpdate("1_9")) { 
		 							if (event.getHand().equals(EquipmentSlot.HAND)) {
		 								PlayerHandler.getPlayer().setMainHandItem(player, item);
		 							} else if (event.getHand().equals(EquipmentSlot.OFF_HAND)) {
		 								PlayerHandler.getPlayer().setOffHandItem(player, item);
		 							}
		 						} 
		 						else { PlayerHandler.getPlayer().setMainHandItem(player, item); }
		 					} else if (itemMap.isSimilar(PlayerHandler.getPlayer().getHandItem(player))) { 
		 						PlayerHandler.getPlayer().getHandItem(player).setAmount(itemMap.getCount()); 
		 					} 
	 					}
	 				}
	 			}
	 		} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
	 	}
	 }
}