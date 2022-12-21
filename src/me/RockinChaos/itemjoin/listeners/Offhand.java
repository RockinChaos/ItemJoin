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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemData;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.core.utils.ServerUtils;

public class Offhand implements Listener {
	
   /**
	* Prevents the player from moving all items from their mainhand to offhand slot or from their offhand to mainhand slot.
	* 
	* @param event - PlayerSwapHandItemsEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onGlobalHandModify(PlayerSwapHandItemsEvent event) {
		if (ServerUtils.hasSpecificUpdate("1_9")) {
			final Player player = event.getPlayer();
			if (ItemData.getInfo().isPreventString(player, "itemMovement")) {
				if (ItemData.getInfo().isPreventBypass(player)) { } 
				else if (player.getOpenInventory().getTitle().contains("§") || player.getOpenInventory().getTitle().contains("&")) { }
				else { 
					event.setCancelled(true); 
					PlayerHandler.updateInventory(player, 1L);
				}
			}
		}
	}
	
   /**
	* Prevents the player from moving the custom item from their mainhand to offhand slot or from their offhand to mainhand slot.
	* 
	* @param event - PlayerSwapHandItemsEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onHandModify(PlayerSwapHandItemsEvent event) {
		if (ServerUtils.hasSpecificUpdate("1_9")) {
			ItemStack offhand = event.getOffHandItem();
			ItemStack mainhand = event.getMainHandItem();
			Player player = event.getPlayer();
			if (!ItemUtilities.getUtilities().isAllowed(player, offhand, "inventory-modify")) {
				event.setCancelled(true);
				PlayerHandler.updateInventory(player, 1L);
			} else if (!ItemUtilities.getUtilities().isAllowed(player, mainhand, "inventory-modify")) {
				event.setCancelled(true);
				PlayerHandler.updateInventory(player, 1L);
			}
		}
	}
}