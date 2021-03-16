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
package me.RockinChaos.itemjoin.listeners.triggers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.item.ItemUtilities.TriggerType;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.api.DependAPI;

public class PlayerGuard implements Listener {
	
	private HashMap < Player, String > playerRegions = new HashMap < Player, String > ();

   /**
	* Called on player movement.
	* Gives and removes any available 
	* custom items upon entering or exiting a region.
	* 
	* @param event - PlayerMoveEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void setRegionItems(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (PlayerHandler.isPlayer(player)) {
			SchedulerUtils.runAsync(() -> {
				if (PlayerHandler.isEnabled(player)) {
					this.handleRegions(player, player.getLocation(), true);
				}
			});
		}
	}
	
   /**
	* Called on player teleport.
	* Gives and removes any available 
	* custom items upon entering or exiting a region.
	* 
	* @param event - PlayerTeleportEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void setRegionItems(PlayerTeleportEvent event) {
		final Player player = event.getPlayer();
		if (PlayerHandler.isPlayer(player)) {
			if (PlayerHandler.isEnabled(player)) {
				this.handleRegions(player, event.getTo(), false);
			}
		}
		ServerUtils.logDebug("{ItemMap} " + player.getName() + " has performed A REGION trigger by teleporting.");
	}
	
   /**
	* Handles the checking of WorldGuard regions, 
	* proceeding if the player has entered or exited a new region.
	* 
	* @param player - The player that has entered or exited a region.
	*/
	private void handleRegions(final Player player, final Location location, final boolean async) {
		String regions = DependAPI.getDepends(false).getGuard().getRegionAtLocation(location);
		if (this.playerRegions.get(player) != null) {
			List < String > regionSet = new ArrayList < String > (Arrays.asList(regions.replace(" ", "").split(",")));
			List < String > playerSet = new ArrayList < String > (Arrays.asList(this.playerRegions.get(player).replace(" ", "").split(",")));
			if (player != null && this.playerRegions != null && this.playerRegions.get(player) != null && regionSet != null) {
				regionSet.removeAll(Arrays.asList(this.playerRegions.get(player).replace(" ", "").split(",")));
			}
			if (playerSet != null && regions != null) {
				playerSet.removeAll(Arrays.asList(regions.replace(" ", "").split(",")));
			}
			for (String region: playerSet) {
				if (region != null && !region.isEmpty()) {
					if (async) { 
						SchedulerUtils.run(() -> ItemUtilities.getUtilities().setItems(player, player.getWorld(), TriggerType.REGION_LEAVE, player.getGameMode(), region));
					} else {
						ItemUtilities.getUtilities().setItems(player, player.getWorld(), TriggerType.REGION_LEAVE, player.getGameMode(), region);
					}
				}
			}
			for (String region: regionSet) {
				if (region != null && !region.isEmpty()) {
					if (async) { 
						SchedulerUtils.run(() -> ItemUtilities.getUtilities().setItems(player, player.getWorld(), TriggerType.REGION_ENTER, player.getGameMode(), region));
					} else {
						ItemUtilities.getUtilities().setItems(player, player.getWorld(), TriggerType.REGION_ENTER, player.getGameMode(), region);
					}
				}
			}
		} else {
			for (String region: Arrays.asList(regions.replace(" ", "").split(","))) {
				if (region != null && !region.isEmpty()) {
					if (async) { 
						SchedulerUtils.run(() -> ItemUtilities.getUtilities().setItems(player, player.getWorld(), TriggerType.REGION_ENTER, player.getGameMode(), region));
					} else {
						ItemUtilities.getUtilities().setItems(player, player.getWorld(), TriggerType.REGION_ENTER, player.getGameMode(), region);
					}
				}
			}
		}
		this.playerRegions.put(player, regions);
	}
}