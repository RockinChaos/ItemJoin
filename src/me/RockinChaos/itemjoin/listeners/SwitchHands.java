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

import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class SwitchHands implements Listener {
	
	@EventHandler
	private void onHandModify(PlayerSwapHandItemsEvent event) {
		if (ServerHandler.hasSpecificUpdate("1_9")) {
			ItemStack offhand = event.getOffHandItem();
			ItemStack mainhand = event.getMainHandItem();
			Player player = event.getPlayer();
			if (!ItemUtilities.isAllowed(player, offhand, "inventory-modify")) {
				event.setCancelled(true);
				PlayerHandler.updateInventory(player);
			} else if (!ItemUtilities.isAllowed(player, mainhand, "inventory-modify")) {
				event.setCancelled(true);
				PlayerHandler.updateInventory(player);
			}
		}
	}
}