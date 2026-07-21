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
package me.RockinChaos.itemjoin.listeners.plugins;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderAPI implements Listener {

    /**
     * ItemsAdder loads its custom item registry and resource pack asynchronously, which is not
     * guaranteed to be complete by the time ItemJoin resolves ItemsAdder-backed items during server
     * startup (e.g. give-on-join). This leaves affected players holding ItemStacks that are missing
     * data such as tooltips. Once ItemsAdder signals that it has fully finished loading, re-resolve
     * and replace any already-held ItemStacks sourced from it.
     *
     * @param event - ItemsAdderLoadDataEvent
     */
    @EventHandler
    private void onItemsAdderLoadData(final ItemsAdderLoadDataEvent event) {
        ServerUtils.logInfo("{ItemsAdder} Data finished loading (cause: " + event.getCause() + "), refreshing externally provided items for online players.");
        ItemUtilities.getUtilities().refreshExternalItems();
    }
}
