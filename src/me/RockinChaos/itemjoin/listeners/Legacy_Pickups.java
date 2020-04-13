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

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

@SuppressWarnings("deprecation")
public class Legacy_Pickups implements Listener {

	@EventHandler
	private void Deprecated_onGlobalPickup(PlayerPickupItemEvent event) {
	  	Player player = event.getPlayer();
  		if (Utils.containsIgnoreCase(ConfigHandler.isPreventPickups(), "true") || Utils.containsIgnoreCase(ConfigHandler.isPreventPickups(), player.getWorld().getName())
	  			|| Utils.containsIgnoreCase(ConfigHandler.isPreventPickups(), "ALL") || Utils.containsIgnoreCase(ConfigHandler.isPreventPickups(), "GLOBAL")) {
  			if (ConfigHandler.isPreventOBypass() && player.isOp() || ConfigHandler.isPreventCBypass() && PlayerHandler.isCreativeMode(player)) { } 
  			else { event.setCancelled(true); }
	  	}
	}
}