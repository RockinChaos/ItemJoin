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
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.types.Clear;
import me.RockinChaos.core.utils.types.Hats;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class Processes implements Listener {

    /**
     * Prevents the player from wearing the custom item as a hat.
     *
     * @param event - PlayerCommandPreprocessEvent
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHat(final PlayerCommandPreprocessEvent event) {
        final String command = event.getMessage();
        final Player player = event.getPlayer();
        final ItemStack item = PlayerHandler.getHandItem(player);
        if (item.getType() != org.bukkit.Material.AIR && Hats.isHat(command)) {
            final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
            if (itemMap != null && itemMap.isNotHat()) {
                event.setMessage("itemjoin_blocked");
                event.setCancelled(true);
                ServerUtils.logDebug("{Processes} " + player.getName() + " tried to perform the command " + command + " on the item " + itemMap.getConfigName() + " but was blocked by the itemflag no-hat.");
                String[] placeHolders = ItemJoin.getCore().getLang().newString();
                placeHolders[1] = player.getName();
                placeHolders[3] = itemMap.getConfigName();
                placeHolders[9] = command;
                ItemJoin.getCore().getLang().sendLangMessage("commands.item.badCommand", player, placeHolders);
            }
        }
    }

    /**
     * Prevents the player from clearing the custom item.
     *
     * @param event - PlayerCommandPreprocessEvent
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommand(final PlayerCommandPreprocessEvent event) {
        final String command = event.getMessage();
        final Player player = event.getPlayer();
        if (Clear.isClear(command)) {
            for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(player.getInventory().getItem(slot));
                if (itemMap != null && itemMap.isAllowedItem(player, player.getInventory().getItem(slot), "no-clear")) {
                    final ItemStack keepItem = Objects.requireNonNull(player.getInventory().getItem(slot)).clone();
                    final int setSlot = slot;
                    SchedulerUtils.run(() -> {
                        player.getInventory().setItem(setSlot, keepItem);
                        ServerUtils.logDebug("{Processes} " + player.getName() + " has triggered the NO-CLEAR itemflag for " + ItemUtilities.getUtilities().getItemMap(keepItem).getConfigName() + ".");
                    });
                }
            }
        }
    }
}