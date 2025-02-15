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
package me.RockinChaos.itemjoin.listeners.plugins.legacy;

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.CompatUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.itemjoin.PluginData;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.menus.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Legacy_ChestSortAPI implements Listener {

    /**
     * Prevents the player from moving the custom item in their inventory when using ChestSort.
     *
     * @param event - ChestSortEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onChestSortEvent(de.jeff_media.ChestSortAPI.ChestSortEvent event) {
        Player player = (Player) event.getPlayer();
        if (player == null) {
            List<HumanEntity> viewers = event.getInventory().getViewers();
            if (!viewers.isEmpty() && viewers.get(0) instanceof Player) {
                player = (Player) viewers.get(0);
            }
        }
        if (player != null) {
            if (PluginData.getInfo().isPreventString(player, "itemMovement")) {
                if (!(PluginData.getInfo().isPreventBypass(player) && (CompatUtils.getInventoryTitle(player).contains(String.valueOf(ChatColor.COLOR_CHAR)) || CompatUtils.getInventoryTitle(player).contains("&")))) {
                    event.setCancelled(true);
                }
            }
            if (Menu.isOpen(player)) {
                event.setCancelled(true);
            } else {
                try {
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (!ItemUtilities.getUtilities().isAllowed(player, item, "inventory-modify")) {
                            event.setUnmovable(item);
                        }
                    }
                    if (!PlayerHandler.isCraftingInv(player)) {
                        for (ItemStack item : CompatUtils.getTopInventory(player)) {
                            if (!ItemUtilities.getUtilities().isAllowed(player, item, "inventory-modify")) {
                                event.setUnmovable(item);
                            }
                        }
                    }
                } catch (NoSuchMethodError ignored) {
                }
            }
        } else {
            ServerUtils.logDebug("{ChestSort_LEGACY} Unable to detect the specified player, sort event is unable to be checked!");
        }
    }
}