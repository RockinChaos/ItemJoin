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
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Placement implements Listener {

	 @EventHandler
	 private void onPreventPlayerPlace(PlayerInteractEvent event) {
	 	ItemStack item = event.getItem();
	 	Player player = event.getPlayer();
	 	if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !ItemUtilities.isAllowed(player, item, "placement")) {
	 		event.setCancelled(true);
	 		PlayerHandler.updateInventory(player);
	 	}
	 }
	 
	 @EventHandler
	 private void onCountLock(PlayerInteractEvent event) {
	 	ItemStack item = event.getItem();
	 	Player player = event.getPlayer();
	 	if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK && !PlayerHandler.isCreativeMode(player)) {
	 		if (!ItemUtilities.isAllowed(player, item, "count-lock")) {
	 			ItemMap itemMap = ItemUtilities.getItemMap(item, null, player.getWorld());
	 			item.setAmount(itemMap.getCount());
	 			new BukkitRunnable() {
	 				@Override
	 				public void run() {
	 					if (itemMap != null) { if (itemMap.isSimilar(PlayerHandler.getHandItem(player))) { PlayerHandler.getHandItem(player).setAmount(itemMap.getCount()); } }
	 				}
	 			}.runTaskLater(ItemJoin.getInstance(), 2L);
	 		}
	 	}
	 }
	 
	 @EventHandler
	 private void onFramePlace(PlayerInteractEntityEvent event) {
	 	if (event.getRightClicked() instanceof ItemFrame) {
	 		try {
	 			ItemStack item = null;
	 			if (ServerHandler.hasSpecificUpdate("1_9")) { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
	 			else { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), ""); }
	 			Player player = event.getPlayer();
	 			if (!ItemUtilities.isAllowed(player, item, "placement")) {
	 				event.setCancelled(true);
	 				PlayerHandler.updateInventory(player);
	 			}
	 		} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
	 	}
	 }
	 
	 @EventHandler
	 private void onFrameLock(PlayerInteractEntityEvent event) {
	 	if (event.getRightClicked() instanceof ItemFrame) {
	 		try {
	 			ItemStack item = null;
	 			if (ServerHandler.hasSpecificUpdate("1_9")) { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
	 			else { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), ""); }
	 			Player player = event.getPlayer();
	 			if (PlayerHandler.isCreativeMode(player)) {
	 				if (!ItemUtilities.isAllowed(player, item, "count-lock")) {
	 					ItemMap itemMap = ItemUtilities.getItemMap(item, null, player.getWorld());
	 					if (itemMap != null) { item.setAmount(itemMap.getCount()); }
	 				}
	 			}
	 		} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
	 	}
	 }
}