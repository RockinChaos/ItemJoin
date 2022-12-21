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

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import me.RockinChaos.itemjoin.item.ItemData;

public class Pickups implements Listener {

   /**
	* Prevents the player from picking up all items.
	* 
	* @param event - EntityPickupItemEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onGlobalPickup(EntityPickupItemEvent event) {
	  	final Entity entity = event.getEntity();
	  	if (entity instanceof Player) {
	  		final Player player = (Player) event.getEntity();
	  		if (ItemData.getInfo().isPreventString(player, "Pickups")) {
	  			if (ItemData.getInfo().isPreventBypass(player)) { } 
	  			else { event.setCancelled(true); }
	  		}
	  	}
	}
}