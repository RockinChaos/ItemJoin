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
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
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
        final ItemStack item = event.getItem();
        final Player player = event.getPlayer();
        if ((!PlayerHandler.isMenuClick(player, event.getAction()) && (event.hasItem() && event.getAction() != Action.PHYSICAL && !ItemUtilities.getUtilities().isAllowed(player, item, "cancel-events")
                || (event.getAction() != Action.PHYSICAL && event.getAction() != Action.LEFT_CLICK_AIR && ServerUtils.hasSpecificUpdate("1_9") && event.getHand() != null
                && event.getHand().toString().equalsIgnoreCase("OFF_HAND") && !ItemUtilities.getUtilities().isAllowed(player, PlayerHandler.getMainHandItem(event.getPlayer()), "cancel-events"))))) {
            if ((item != null && ItemHandler.isBookQuill(item)) || ItemHandler.isBookQuill(PlayerHandler.getMainHandItem(event.getPlayer()))) {
                player.closeInventory();
            }
            event.setUseItemInHand(Result.DENY);
            event.setUseInteractedBlock(Result.DENY);
        }
    }

    /**
     * Sets the custom item on cooldown upon interaction.
     * Handles various bucket liquids.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onInteractCooldown(PlayerInteractEvent event) {
        final ItemStack item = event.getItem();
        if (item != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            switch (item.getType().name()) {
                case "LAVA_BUCKET":
                case "WATER_BUCKET":
                case "MILK_BUCKET":
                case "COD_BUCKET":
                case "SALMON_BUCKET":
                case "PUFFERFISH_BUCKET":
                case "TROPICAL_FISH_BUCKET":
                    this.handleUseCooldown(event.getPlayer(), item, event);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Sets the custom item on cooldown upon shooting a bow.
     * Handles Bows and Crossbows.
     *
     * @param event - EntityShootBowEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onBowShootCooldown(final EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            this.handleUseCooldown((Player) event.getEntity(), event.getBow(), event);
        }
    }

    /**
     * Sets the custom item on cooldown upon consuming an item.
     * Handles Food and Potions.
     *
     * @param event - PlayerItemConsumeEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onConsumeCooldown(final PlayerItemConsumeEvent event) {
        this.handleUseCooldown(event.getPlayer(), event.getItem(), event);
    }

    /**
     * Sets the custom item on cooldown upon placing a block.
     * Handles placing Blocks.
     *
     * @param event - BlockPlaceEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlaceCooldown(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        this.handleUseCooldown(player, PlayerHandler.getPerfectHandItem(player, ServerUtils.hasSpecificUpdate("1_9") ? event.getHand().name() : ""), event);
    }

    /**
     * Sets the custom item on cooldown upon launching an item.
     * Handles throwing Ender Pearls, Snowballs, Eggs, etc.
     *
     * @param event - ProjectileLaunchEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunchCooldown(final ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            final Player player = ((Player)event.getEntity().getShooter());
            final ItemStack item = PlayerHandler.getMainHandItem(player).getType() != Material.AIR && StringUtils.containsIgnoreCase(PlayerHandler.getMainHandItem(player).getType().name(), (event.getEntity().getType().name())) ? PlayerHandler.getMainHandItem(player) : PlayerHandler.getOffHandItem(player).getType() != Material.AIR && StringUtils.containsIgnoreCase(PlayerHandler.getOffHandItem(player).getType().name(), (event.getEntity().getType().name())) ? PlayerHandler.getOffHandItem(player) : PlayerHandler.getMainHandItem(player);
            this.handleUseCooldown(player, item, event);
        }
    }

    /**
     * Sets the custom item on cooldown.
     *
     * @param player - The Player who triggered the event.
     * @param item   - The item that triggered the event.
     * @param event  - The event to cancel if the item is on cooldown.
     */
    private void handleUseCooldown(final Player player, final ItemStack item, final Cancellable event) {
        final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
        if (itemMap != null && itemMap.getInteractCooldown() > 0) {
            final long lockDuration = !this.interactLock.isEmpty() && this.interactLock.get(item) != null ? System.currentTimeMillis() - this.interactLock.get(item) : -1;
            this.interactLock.put(item, System.currentTimeMillis());
            if (itemMap.onInteractCooldown(player, item)) {
                if (lockDuration == -1 || lockDuration > 30) {
                    event.setCancelled(true);
                    PlayerHandler.updateInventory(player);
                }
            }
        }
    }

    /**
     * Prevents the player from selecting custom items with the selectable itemflag upon holding it.
     *
     * @param event - InventoryClickEvent.
     */
    @EventHandler()
    private void onMoveToSelectItem(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack item = StringUtils.containsIgnoreCase(event.getAction().name(), "HOTBAR") ? (event.getCurrentItem() != null ? event.getCurrentItem().clone() : event.getCurrentItem()) : (event.getCursor() != null ? event.getCursor().clone() : event.getCursor());
        final int slot = event.getSlot() <= 8 ? event.getSlot() : event.getHotbarButton() >= 0 ? event.getHotbarButton() : 9;
        if (slot <= 8 && !ItemUtilities.getUtilities().isAllowed(player, item, "selectable")) {
            SchedulerUtils.run(() -> {
                if (Objects.equals(PlayerHandler.getMainHandItem(player), item)) {
                    if (!setSelectSlot(player, slot, true)) {
                        setSelectSlot(player, slot, false);
                    }
                }
            });
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
            final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
            SchedulerUtils.runLater(itemMap.getSelectableDelay(), () -> {
                if (Objects.equals(PlayerHandler.getMainHandItem(player), item)) {
                    if (!setSelectSlot(player, newSlot, (newSlot > oldSlot))) {
                        setSelectSlot(player, newSlot, !(newSlot > oldSlot));
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
    public static boolean setSelectSlot(final Player player, final int slot, final boolean forward) {
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