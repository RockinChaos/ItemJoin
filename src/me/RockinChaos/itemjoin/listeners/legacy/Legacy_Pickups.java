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

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
* Handles the Pickup events for custom items.
* 
* @deprecated This is a LEGACY listener, only use on Minecraft versions below 1.12.
*/
public class Legacy_Pickups implements Listener {

   /**
	* Prevents the player from picking up all items.
	* 
	* @param event - EntityPickupItemEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.12.
	*/
	@EventHandler(ignoreCancelled = true)
	private void Deprecated_onGlobalPickup(org.bukkit.event.player.PlayerPickupItemEvent event) {
	  	Player player = event.getPlayer();
  		if (Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getPrevent("Pickups"), "TRUE") || Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getPrevent("Pickups"), player.getWorld().getName())
	  			|| Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getPrevent("Pickups"), "ALL") || Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getPrevent("Pickups"), "GLOBAL")) {
  			if (ConfigHandler.getConfig(false).isPreventOP() && player.isOp() || ConfigHandler.getConfig(false).isPreventCreative() && PlayerHandler.getPlayer().isCreativeMode(player)) { } 
  			else { event.setCancelled(true); }
	  	}
	}
}