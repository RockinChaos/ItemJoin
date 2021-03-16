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
package me.RockinChaos.itemjoin.listeners.plugins;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.StringUtils;

public class ChestSortAPI implements Listener {
	
   /**
	* Prevents the player from moving the custom item in their inventory when using ChestSort.
	* 
	* @param event - ChestSortEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onChestSortEvent(de.jeff_media.ChestSortAPI.ChestSortEvent event) {
		Player player = (Player) event.getPlayer();
		if (player == null) { player = (Player) event.getInventory().getViewers().get(0); }
	  	if (StringUtils.getUtils().containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), "TRUE") || StringUtils.getUtils().containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), player.getWorld().getName())
		  			|| StringUtils.getUtils().containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), "ALL") || StringUtils.getUtils().containsIgnoreCase(ConfigHandler.getConfig().getPrevent("itemMovement"), "GLOBAL")) {
	  		if (ConfigHandler.getConfig().isPreventOP() && player.isOp() || ConfigHandler.getConfig().isPreventCreative() && PlayerHandler.isCreativeMode(player)) { } 
	  		else if (player.getOpenInventory().getTitle().contains("§") || player.getOpenInventory().getTitle().contains("&")) { }
	  		else { event.setCancelled(true); }
	  	}
	  	if (!event.isCancelled()) {
			try {
				for (ItemStack item : event.getInventory().getContents()) {
					if (!ItemUtilities.getUtilities().isAllowed(player, item, "inventory-modify")) {
						event.setUnmovable(item);
					}
				}
			} catch (NoSuchMethodError e) { }
	  	}
	}
}