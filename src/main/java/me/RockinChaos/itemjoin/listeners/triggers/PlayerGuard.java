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
import me.RockinChaos.itemjoin.PluginData;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.item.ItemUtilities.TriggerType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

public class PlayerGuard implements Listener {
    private final HashMap<Player, String> fromRegions = new HashMap<>();
    private final List<Player> movementCooldown = new ArrayList<>();
    private final List<Player> changeCooldown = new ArrayList<>();

    /**
     * Called on player movement.
     * Gives and removes any available
     * custom items upon entering or exiting a region.
     *
     * @param event PlayerMoveEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void setRegionItems(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (event.getTo() != null && event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ() && Objects.equals(event.getFrom().getWorld(), event.getTo().getWorld())) { return; }
        SchedulerUtils.runAsync(() -> {
            if (PlayerHandler.isPlayer(player) && !this.onMovementCooldown(player) && PluginData.getInfo().isEnabled(player, "ALL")) {
                this.handleRegions(player, player.getLocation(), true, event.getFrom());
            }
        });
    }

    /**
     * Called on player teleport.
     * Gives and removes any available
     * custom items upon entering or exiting a region.
     *
     * @param event PlayerTeleportEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void setRegionItems(PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        if (PlayerHandler.isPlayer(player) && PluginData.getInfo().isEnabled(player, "ALL")) {
            this.handleRegions(player, event.getTo(), false, event.getFrom());
        }
        ServerUtils.logDebug("{ItemMap} " + player.getName() + " has performed A REGION trigger by teleporting.");
    }

    /**
     * Handles the checking of WorldGuard regions,
     * proceeding if the player has entered or exited a new region.
     *
     * @param player The player that has entered or exited a region.
     */
    private void handleRegions(final Player player, final Location location, final boolean async, final Location fromLocation) {
        final String regionList = ItemJoin.getCore().getDependencies().getGuard().getRegionAtLocation(location);
        final List<String> regions = Arrays.asList((regionList + ", GLOBAL").replace(" ", "").split(","));
        List<String> prevRegions = this.fromRegions.get(player) != null ? Arrays.asList(this.fromRegions.get(player).replace(" ", "").split(",")) : Collections.emptyList();
        if (!regionList.equals(this.fromRegions.get(player) != null ? this.fromRegions.get(player) : "") || !this.onChangedCooldown(player) || !async) {
            if (!prevRegions.isEmpty()) {
                for (String region : prevRegions) {
                    if (!region.isEmpty()) {
                        runAuth(async, player, fromLocation.getWorld(), TriggerType.REGION_LEAVE, region, regions);
                    }
                }
            }
            for (String region : regions) {
                if (!region.isEmpty()) {
                    runAuth(async, player, location.getWorld(), TriggerType.REGION_ENTER, region, regions);
                }
            }
            this.fromRegions.put(player, regionList);
        }
    }

    /**
     * Runs the region authentication logic for the player.
     *
     * @param async True to run on the main thread later, false to run immediately
     * @param player The player to process
     * @param world The world the event occurred in
     * @param type The region trigger type (enter/leave)
     * @param region The region name
     * @param regions All regions currently affecting the player
     */
    private void runAuth(boolean async, Player player, World world, TriggerType type, String region, List<String> regions) {
        if (async) {
            SchedulerUtils.run(() -> ItemUtilities.getUtilities().setAuthenticating(player, world, type, player.getGameMode(), region, regions));
        } else {
            ItemUtilities.getUtilities().setAuthenticating(player, world, type, player.getGameMode(), region, regions);
        }
    }

    /**
     * Checks if the player is on Movement Cooldown.
     * If the player is not on cooldown, they will automatically be added for 10 ticks.
     *
     * @return If The player is currently on Movement Cooldown.
     */
    private boolean onMovementCooldown(final Player player) {
        if (!this.movementCooldown.contains(player)) {
            this.movementCooldown.add(player);
            SchedulerUtils.runLater(10L, () -> this.movementCooldown.remove(player));
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if the player is on Changed Cooldown.
     * If the player is not on cooldown, they will automatically be added for 160 ticks.
     *
     * @return If The player is currently on Changed Cooldown.
     */
    private boolean onChangedCooldown(final Player player) {
        if (!this.changeCooldown.contains(player)) {
            this.changeCooldown.add(player);
            SchedulerUtils.runLater(160L, () -> this.changeCooldown.remove(player));
            return false;
        } else {
            return true;
        }
    }
}