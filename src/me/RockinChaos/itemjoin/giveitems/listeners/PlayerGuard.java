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
package me.RockinChaos.itemjoin.giveitems.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities.TriggerType;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.DependAPI;
import me.RockinChaos.itemjoin.utils.LegacyAPI;
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
		String regions = this.getRegionsAtLocation(player);
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
	
   /**
	* Gets the current region(s) the player is currently in.
	* 
	* @param player - The player that has entered or exited a region.
	* @return regionSet The applicable regions at the players location.
	*/
	private String getRegionsAtLocation(final Player player) {
		ApplicableRegionSet set = null;
		String regionSet = "";
		try { set = this.getApplicableRegionSet(player.getWorld(), player.getLocation()); } 
		catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
		if (set == null) { return regionSet; }
		for (ProtectedRegion r: set) {
			if (regionSet.isEmpty()) { regionSet += r.getId(); }
			else { regionSet += ", " + r.getId(); }
		}
		return regionSet;
	}
	
   /**
	* Gets the applicable region(s) set at the players location.
	* 
	* @param world - The world that the player is currently in.
	* @param location - The exact location of the player.
	* @return ApplicableRegionSet The WorldGuard RegionSet.
	*/
	private ApplicableRegionSet getApplicableRegionSet(final World world, final Location location) throws Exception {
		if (DependAPI.getDepends(false).getGuard().guardVersion() >= 700) {
			com.sk89q.worldedit.world.World wgWorld;
			try { wgWorld = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getWorldByName(world.getName()); } 
			catch (NoSuchMethodError e) { wgWorld = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(world.getName()); }
			com.sk89q.worldguard.protection.regions.RegionContainer rm = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			if (rm == null) { return null; }
			if (LegacyAPI.getLegacy().legacySk89q()) {
				final com.sk89q.worldedit.Vector wgVector = new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ());
				return rm.get(wgWorld).getApplicableRegions(wgVector);
			} else { return rm.get(wgWorld).getApplicableRegions(LegacyAPI.getLegacy().asBlockVector(location)); }
		} else { return LegacyAPI.getLegacy().getRegionSet(world, location); }
	}
}