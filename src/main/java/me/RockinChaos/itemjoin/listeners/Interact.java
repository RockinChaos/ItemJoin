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

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.api.ItemAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class Interact implements Listener {

    private final HashMap<ItemStack, Long> interactLock = new HashMap<>();

    /**
     * Cancels any event that is triggered when interacting with the custom item.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onInteractCancel(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if ((PlayerHandler.isMenuClick(player.getOpenInventory(), event.getAction()) && (event.hasItem() && event.getAction() != Action.PHYSICAL && !ItemUtilities.getUtilities().isAllowed(player, item, "cancel-events")
                || (event.getAction() != Action.PHYSICAL && event.getAction() != Action.LEFT_CLICK_AIR && ServerUtils.hasSpecificUpdate("1_9") && event.getHand() != null
                && event.getHand().toString().equalsIgnoreCase("OFF_HAND") && !ItemUtilities.getUtilities().isAllowed(player, PlayerHandler.getMainHandItem(event.getPlayer()), "cancel-events"))))) {
            if (ItemHandler.isBookQuill(item) || ItemHandler.isBookQuill(PlayerHandler.getMainHandItem(event.getPlayer()))) {
                player.closeInventory();
            }
            event.setUseItemInHand(Result.DENY);
            event.setUseInteractedBlock(Result.DENY);
        }
    }

    /**
     * Sets the custom item on cooldown upon interaction.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler()
    private void onInteractCooldown(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if ((event.hasItem() && event.getAction() != Action.PHYSICAL) && ((ItemAPI.isPlaceable(event.getMaterial()) && event.getAction() == Action.RIGHT_CLICK_AIR) || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
            if (itemMap != null && itemMap.getInteractCooldown() != 0) {
                long lockDuration = !this.interactLock.isEmpty() && this.interactLock.get(item) != null ? System.currentTimeMillis() - this.interactLock.get(item) : -1;
                this.interactLock.put(item, System.currentTimeMillis());
                if (itemMap.onInteractCooldown(player)) {
                    if (lockDuration == -1 || lockDuration > 30) {
                        event.setCancelled(true);
                        PlayerHandler.updateInventory(player);
                    }
                }
            }
        }
    }

    /**
     * Prevents the player from selecting custom items with the selectable itemflag upon holding it.
     *
     * @param event - PlayerItemHeldEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onSelectItem(PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItem(event.getNewSlot());
        final int newSlot = event.getNewSlot();
        final int oldSlot = event.getPreviousSlot();
        if (!ItemUtilities.getUtilities().isAllowed(player, item, "selectable")) {
            SchedulerUtils.runLater(10L, () -> {
                if (Objects.equals(PlayerHandler.getMainHandItem(player), item)) {
                    if (!this.setSelectSlot(player, newSlot, (newSlot > oldSlot))) {
                        this.setSelectSlot(player, newSlot, !(newSlot > oldSlot));
                    }
                }
            });
        }
    }

    /**
     * Sets the players hotbar slot towards the direction they were moving from prior.
     *
     * @param player  - The player changing their selected slots.
     * @param slot    - The currently selected slot.
     * @param forward - If they are moving to right of the inventory.
     */
    private boolean setSelectSlot(final Player player, final int slot, final boolean forward) {
        for (int i = slot; (forward ? i < 9 : i >= 0); ) {
            if (ItemUtilities.getUtilities().isAllowed(player, player.getInventory().getItem(i), "selectable")) {
                PlayerHandler.setHotbarSlot(player, i);
                return true;
            } else if (forward) {
                i++;
            } else {
                i--;
            }
        }
        return false;
    }
}