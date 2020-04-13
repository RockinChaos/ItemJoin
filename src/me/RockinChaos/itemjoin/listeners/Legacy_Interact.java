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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class Legacy_Interact implements Listener {
	
	 @EventHandler(priority = EventPriority.LOWEST)
	 private void onInteractCancel(PlayerInteractEvent event) {
	 	ItemStack item = event.getItem();
	 	Player player = event.getPlayer();
	 	if (event.hasItem() && event.getAction() != Action.PHYSICAL && !ItemUtilities.isAllowed(player, item, "cancel-events") 
	 			|| event.hasItem() && event.getAction() != Action.PHYSICAL && ServerHandler.hasSpecificUpdate("1_9") && event.getHand() != null 
	 			&& event.getHand().toString().equalsIgnoreCase("OFF_HAND") && !ItemUtilities.isAllowed(player, PlayerHandler.getMainHandItem(event.getPlayer()), "cancel-events")) {
	 		event.setCancelled(true);
	 		PlayerHandler.updateInventory(player);
	 	}
	 }

	 @EventHandler
	 private void onInteractCooldown(PlayerInteractEvent event) {
	 	Player player = event.getPlayer();
	 	ItemStack item = event.getItem();
	 	if (event.hasItem() && event.getAction() != Action.PHYSICAL) {
	 		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	 			ItemMap itemMap = ItemUtilities.getItemMap(item, null, player.getWorld());
	 			if (itemMap != null && itemMap.getInteractCooldown() != 0) {
	 				if (itemMap.onInteractCooldown(player)) {
	 					event.setCancelled(true);
	 				}
	 			}
	 		}
	 	}
	 }

	@EventHandler
	private void onInventoryCommands(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		String action = event.getAction().toString();
		String slot = String.valueOf(event.getSlot());
		if (event.getSlotType().name().equalsIgnoreCase("CRAFTING")) { slot = "CRAFTING[" + slot + "]"; }
		if (this.setupCommands(player, item, action, slot)) { event.setCancelled(true); }
	}
	
	@EventHandler
	private void onEntityCommands(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof org.bukkit.entity.ItemFrame) {
			ItemStack item;
			if (ServerHandler.hasSpecificUpdate("1_9")) { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
			else { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), ""); }
			Player player = event.getPlayer();
			String action = Action.RIGHT_CLICK_BLOCK.name();
			ItemMap itemMap = ItemUtilities.getItemMap(PlayerHandler.getHandItem(player), null, player.getWorld());
			if (itemMap != null && itemMap.isSimilar(item)) {
				if (this.setupCommands(player, item, action, String.valueOf(player.getInventory().getHeldItemSlot()))) { event.setCancelled(true); }
			}
		}
	}

	@EventHandler
	private void onInteractCommands(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		final Player player = event.getPlayer();
		String action = event.getAction().toString();
		if (PlayerHandler.isAdventureMode(player) && !action.contains("LEFT") 
				|| !PlayerHandler.isAdventureMode(player)) {
			ItemMap itemMap = ItemUtilities.getItemMap(PlayerHandler.getHandItem(player), null, player.getWorld());
			if (itemMap != null && itemMap.isSimilar(item)) {
				if (this.setupCommands(player, item, action, "")) { event.setCancelled(true); }
			}
		}
	}
	
	@EventHandler
	public void onSwingHandCommands(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		ItemStack item = PlayerHandler.getHandItem(player);
		if (PlayerHandler.isAdventureMode(player)) {
			if (this.setupCommands(player, item, "LEFT_CLICK_AIR", String.valueOf(player.getInventory().getHeldItemSlot()))) { event.setCancelled(true); }
		}
	}
	
	private boolean setupCommands(Player player, ItemStack item, String action, String slot) {
		ItemMap itemMap = ItemUtilities.getItemMap(item, null, player.getWorld());
		if (itemMap != null && itemMap.inWorld(player.getWorld()) && itemMap.hasPermission(player)) {
			return itemMap.executeCommands(player, item, action, slot);
		}
		return false;
	}
}
