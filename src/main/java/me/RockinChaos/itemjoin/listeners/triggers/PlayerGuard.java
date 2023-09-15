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

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.item.ItemData;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.item.ItemUtilities.TriggerType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PlayerGuard implements Listener {

    private final HashMap<Player, String> playerRegions = new HashMap<>();

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
                if (ItemData.getInfo().isEnabled(player, "ALL")) {
                    event.getFrom();
                    this.handleRegions(player, player.getLocation(), true, event.getFrom());
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
            if (ItemData.getInfo().isEnabled(player, "ALL")) {
                event.getFrom();
                this.handleRegions(player, event.getTo(), false, event.getFrom());
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
    private void handleRegions(final Player player, final Location location, final boolean async, final Location fromLocation) {
        String regions = ItemJoin.getCore().getDependencies().getGuard().getRegionAtLocation(location);
        List<String> regionSetFull = new ArrayList<>(Arrays.asList(regions.replace(" ", "").split(",")));
        if (player != null && this.playerRegions.get(player) != null) {
            List<String> regionSet = new ArrayList<>(Arrays.asList(regions.replace(" ", "").split(",")));
            List<String> playerSet = new ArrayList<>(Arrays.asList(this.playerRegions.get(player).replace(" ", "").split(",")));
            if (this.playerRegions.get(player) != null) {
                regionSet.removeAll(Arrays.asList(this.playerRegions.get(player).replace(" ", "").split(",")));
            }
            playerSet.removeAll(Arrays.asList(regions.replace(" ", "").split(",")));
            for (String region : playerSet) {
                if (region != null && !region.isEmpty()) {
                    if (async) {
                        SchedulerUtils.run(() -> ItemUtilities.getUtilities().setItems(player, fromLocation.getWorld(), TriggerType.REGION_LEAVE, player.getGameMode(), region, regionSetFull));
                    } else {
                        ItemUtilities.getUtilities().setItems(player, location.getWorld(), TriggerType.REGION_LEAVE, player.getGameMode(), region, regionSetFull);
                    }
                }
            }
            for (String region : regionSet) {
                if (region != null && !region.isEmpty()) {
                    if (async) {
                        SchedulerUtils.run(() -> ItemUtilities.getUtilities().setItems(player, location.getWorld(), TriggerType.REGION_ENTER, player.getGameMode(), region, regionSetFull));
                    } else {
                        ItemUtilities.getUtilities().setItems(player, location.getWorld(), TriggerType.REGION_ENTER, player.getGameMode(), region, regionSetFull);
                    }
                }
            }
        } else if (player != null) {
            for (String region : regions.replace(" ", "").split(",")) {
                if (region != null && !region.isEmpty()) {
                    if (async) {
                        SchedulerUtils.run(() -> ItemUtilities.getUtilities().setItems(player, location.getWorld(), TriggerType.REGION_ENTER, player.getGameMode(), region, regionSetFull));
                    } else {
                        ItemUtilities.getUtilities().setItems(player, location.getWorld(), TriggerType.REGION_ENTER, player.getGameMode(), region, regionSetFull);
                    }
                }
            }
        }
        this.playerRegions.put(player, regions);
    }
}