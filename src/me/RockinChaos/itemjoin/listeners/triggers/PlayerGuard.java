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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.item.ItemUtilities.TriggerType;
import me.RockinChaos.itemjoin.utils.DependAPI;
import me.RockinChaos.itemjoin.utils.sqlite.SQLite;

public class PlayerGuard implements Listener {
	
	private HashMap < Player, String > playerRegions = new HashMap < Player, String > ();

   /**
	* Called on player movement.
	* Gives and removes any available 
	* custom items upon entering or exiting a region.
	* 
	* @param event - PlayerMoveEvent
	*/
	@EventHandler
	private void setRegionItems(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (SQLite.getLite(false).isEnabled(player)) {
			this.handleRegions(player);
		}
	}
	
   /**
	* Handles the checking of WorldGuard regions, 
	* proceeding if the player has entered or exited a new region.
	* 
	* @param player - The player that has entered or exited a region.
	*/
	private void handleRegions(final Player player) {
		String regions = DependAPI.getDepends(false).getGuard().getRegionsAtLocation(player);
		if (this.playerRegions.get(player) != null) {
			List < String > regionSet = Arrays.asList(regions.replace(" ", "").split(","));
			List < String > playerSet = Arrays.asList(this.playerRegions.get(player).replace(" ", "").split(","));
			List < String > regionSetList = new ArrayList < String > (regionSet);
			List < String > playerSetList = new ArrayList < String > (playerSet);
			regionSetList.removeAll(playerSet);
			playerSetList.removeAll(regionSet);
			if (!playerSetList.isEmpty()) {
				for (String region: playerSetList) {
					if (region != null && !region.isEmpty()) {
						ItemUtilities.getUtilities().setItems(player, TriggerType.REGIONLEAVE, org.bukkit.GameMode.ADVENTURE, region);
					}
				}
			}
			if (!regionSetList.isEmpty()) {
				for (String region: regionSetList) {
					if (region != null && !region.isEmpty()) {
						ItemUtilities.getUtilities().setItems(player, TriggerType.REGIONENTER, org.bukkit.GameMode.ADVENTURE, region);
					}
				}
			}
		} else {
			List < String > regionSet = Arrays.asList(regions.replace(" ", "").split(","));
			for (String region: regionSet) {
				if (region != null && !region.isEmpty()) {
					ItemUtilities.getUtilities().setItems(player, TriggerType.REGIONENTER, org.bukkit.GameMode.ADVENTURE, region);
				}
			}
		}
		this.playerRegions.put(player, regions);
	}
}