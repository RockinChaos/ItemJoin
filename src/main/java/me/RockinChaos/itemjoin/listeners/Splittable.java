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

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class Splittable implements Listener {

    /**
     * Prevents item splitting of custom items with the Splittable itemflag defined.
     *
     * @param event - InventoryClickEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onClickSplit(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final InventoryAction action = event.getAction();
        final ItemStack currentItem = event.getCurrentItem();
        final ItemStack cursorItem = event.getCursor();
        final boolean isCreative = PlayerHandler.isCreativeMode(player);
        if (currentItem != null && currentItem.getType() != Material.AIR) {
            if (action.equals(InventoryAction.PICKUP_HALF)) {
                final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(currentItem);
                if (itemMap != null && itemMap.isSimilar(player, currentItem) && !ItemUtilities.getUtilities().isAllowed(player, currentItem, "splittable")) {
                    event.setCancelled(true);
                }
            } else if (action.equals(InventoryAction.PLACE_ALL) && isCreative && cursorItem != null && cursorItem.getType() != Material.AIR) {
                final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(currentItem);
                if (itemMap != null && itemMap.isSimilar(player, currentItem) && itemMap.isSimilar(player, cursorItem) && (currentItem.getAmount() / 2) == cursorItem.getAmount() && !ItemUtilities.getUtilities().isAllowed(player, currentItem, "splittable")) {
                    event.setCancelled(true);
                    player.closeInventory();
                }
            }
        }
    }

    /**
     * Prevents item splitting of custom items with the Splittable itemflag defined.
     *
     * @param event - InventoryDragEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onDragSplit(InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack cursorItem = event.getOldCursor();
        if (cursorItem.getType() != Material.AIR) {
            final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(cursorItem);
            if (itemMap != null && itemMap.isSimilar(player, cursorItem) && !ItemUtilities.getUtilities().isAllowed(player, cursorItem, "splittable")) {
                event.setCancelled(true);
            }
        }
    }
}