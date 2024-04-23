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
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.item.ItemUtilities.TriggerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.Collections;

public class WorldSwitch implements Listener {

    /**
     * Called on player switching worlds.
     * Gives any available custom items upon switching worlds.
     *
     * @param event - PlayerChangedWorldEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void setSwitchItems(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        if (PlayerHandler.isPlayer(player)) {
            if (ItemJoin.getCore().getDependencies().getGuard().guardEnabled()) {
                ItemUtilities.getUtilities().handleRegions(player, event.getFrom(), TriggerType.WORLD_SWITCH, player.getGameMode());
            } else {
                ItemUtilities.getUtilities().setAuthenticating(player, event.getFrom(), TriggerType.WORLD_SWITCH, player.getGameMode(), "IJ_WORLD", Collections.singletonList("IJ_WORLD"));
            }
        }
        ServerUtils.logDebug("{ItemMap} " + player.getName() + " has performed the WORLD-SWITCH trigger.");
    }
}