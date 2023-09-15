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

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.item.ItemUtilities.TriggerType;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.Collections;

public class PlayerQuit implements Listener {

    /**
     * Called on player quit.
     *
     * @param event - PlayerQuitEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void setQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (PlayerHandler.isPlayer(player)) {
            final Inventory inventory = ItemHandler.getCraftInventory(player);
            if (inventory != null) {
                ItemJoin.getCore().getSQL().saveData(new DataObject(Table.RETURN_CRAFTITEMS, PlayerHandler.getPlayerID(player), "", ItemHandler.serializeInventory(inventory)));
            }
            {
                ItemUtilities.getUtilities().closeAnimations(player);
                {
                    ItemHandler.removeCraftItems(player);
                    {
                        ItemUtilities.getUtilities().setAuthenticating(player, player.getWorld(), TriggerType.QUIT, player.getGameMode(), "GLOBAL", Collections.singletonList("GLOBAL"));
                    }
                }
            }
        }
        ServerUtils.logDebug("{ItemMap} " + player.getName() + " has performed the QUIT trigger.");
    }
}