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
import me.RockinChaos.core.utils.CompatUtils;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.PluginData;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Drops implements Listener {

    private final Map<String, Boolean> isDropping = new HashMap<>();
    private final Map<String, Boolean> possibleDropping = new HashMap<>();

    /**
     * Prevents the player from dropping all items.
     *
     * @param event - PlayerDropItemEvent.
     */
    @EventHandler(ignoreCancelled = true)
    private void onGlobalDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        if (PluginData.getInfo().isPreventString(player, "Self-Drops")) {
            if (PluginData.getInfo().isPreventBypass(player)) {
                if (!player.isDead()) {
                    if (PlayerHandler.isCreativeMode(player)) {
                        player.closeInventory();
                    }
                    event.setCancelled(true);
                } else if (player.isDead()) {
                    event.getItemDrop().remove();
                }
            }
        }
    }

    /**
     * Prevents the player from dropping all items with an open InventoryView.
     *
     * @param event - InventoryClickEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onGlobalClickDrop(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE && PluginData.getInfo().isPreventString(player, "Self-Drops")) {
            if (PluginData.getInfo().isPreventBypass(player)) {
                if (!player.isDead()) {
                    if (PlayerHandler.isCreativeMode(player)) {
                        player.closeInventory();
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Prevents the player from dropping all items on death.
     *
     * @param event - PlayerDeathEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onGlobalDeathDrops(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        ItemUtilities.getUtilities().closeAnimations(player);
        if (PluginData.getInfo().isPreventString(player, "Death-Drops")) {
            if (PluginData.getInfo().isPreventBypass(player) && !LegacyAPI.hasGameRule(player.getWorld(), "keepInventory")) {
                player.getInventory().clear();
                CompatUtils.getTopInventory(player).clear();
                event.getDrops().clear();
            }
        }
    }

    /**
     * Prevents the player from dropping the custom item.
     *
     * @param event - PlayerDropItemEvent.
     */
    @EventHandler(ignoreCancelled = true)
    private void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        final Player player = event.getPlayer();
        if (!player.isDead() && (!ItemUtilities.getUtilities().isAllowed(player, item, "self-drops") || !ItemUtilities.getUtilities().isAllowed(player, item, "erase-drops")) && !ItemUtilities.getUtilities().getItemMap(item).isCraftingItem()) {
            if (!this.possibleDropping.containsKey(PlayerHandler.getPlayerID(player)) && !ItemUtilities.getUtilities().isAllowed(player, item, "self-drops")) {
                event.setCancelled(true);
            } else if (!this.possibleDropping.containsKey(PlayerHandler.getPlayerID(player))) {
                event.getItemDrop().remove();
            }
            if (PlayerHandler.isCreativeMode(player) && this.possibleDropping.containsKey(PlayerHandler.getPlayerID(player)) && this.possibleDropping.get(PlayerHandler.getPlayerID(player))) {
                player.closeInventory();
                event.getItemDrop().remove();
                this.isDropping.put(PlayerHandler.getPlayerID(player), true);
                this.possibleDropping.remove(PlayerHandler.getPlayerID(player));
                this.delayedSafety(player, 1);
            } else if (PlayerHandler.isCreativeMode(player)) {
                player.closeInventory();
            }
        } else if (player.isDead() && (!ItemUtilities.getUtilities().isAllowed(player, item, "self-drops") || !ItemUtilities.getUtilities().isAllowed(player, item, "erase-drops"))) {
            event.getItemDrop().remove();
        }
    }

    /**
     * Prevents the player from dropping the custom item with an open InventoryView.
     *
     * @param event - InventoryClickEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onClickDrop(InventoryClickEvent event) {
        final ItemStack item = event.getCursor();
        final Player player = (Player) event.getWhoClicked();
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE && !player.isDead() && (!ItemUtilities.getUtilities().isAllowed(player, item, "self-drops") || !ItemUtilities.getUtilities().isAllowed(player, item, "erase-drops"))) {
            if (!ItemUtilities.getUtilities().isAllowed(player, item, "self-drops")) {
                event.setCancelled(true);
            } else if (item != null) {
                item.setType(Material.AIR);
            }
        }
    }

    /**
     * Prevents the player from dropping the custom item.
     *
     * @param event - InventoryClickEvent.
     */
    @EventHandler(ignoreCancelled = true)
    private void onCreativeDrop(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (PlayerHandler.isCreativeMode(player) && this.isDropping.containsKey(PlayerHandler.getPlayerID(player)) && this.isDropping.get(PlayerHandler.getPlayerID(player))) {
            if (!ItemUtilities.getUtilities().isAllowed(player, event.getCurrentItem(), "self-drops") || !ItemUtilities.getUtilities().isAllowed(player, event.getCurrentItem(), "erase-drops")) {
                if (!ItemUtilities.getUtilities().isAllowed(player, event.getCurrentItem(), "self-drops")) {
                    event.setCancelled(true);
                } else {

                }
                player.closeInventory();
                PlayerHandler.updateInventory(player, 1L);
                this.isDropping.remove(PlayerHandler.getPlayerID(player));
            }
        }
        if (PlayerHandler.isCreativeMode(player) && event.getSlot() == -999 && !this.possibleDropping.containsKey(PlayerHandler.getPlayerID(player))) {
            this.possibleDropping.put(PlayerHandler.getPlayerID(player), true);
            this.delayedSafety(player, 2);
        }
    }

    /**
     * Prevents the player from dropping the custom item on death.
     *
     * @param event - PlayerItemConsumeEvent.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onDeathDrops(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Inventory topInventory = CompatUtils.getTopInventory(player);
        ItemUtilities.getUtilities().closeAnimations(player);
        if (!LegacyAPI.hasGameRule(player.getWorld(), "keepInventory")) {
            final List<ItemStack> drops = new ArrayList<>(event.getDrops());
            for (final ItemStack stack : drops) {
                if (stack != null && (!ItemUtilities.getUtilities().isAllowed(player, stack, "death-drops") || !ItemUtilities.getUtilities().isAllowed(player, stack, "erase-drops") || !ItemUtilities.getUtilities().isAllowed(player, stack, "death-keep"))) {
                    if (!ItemUtilities.getUtilities().isAllowed(player, stack, "death-keep")) {
                        int slot = -1;
                        final ItemStack keepItem = stack.clone();
                        for (int inventory = 0; inventory < player.getInventory().getSize(); inventory++) {
                            final ItemStack item = player.getInventory().getItem(inventory);
                            final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
                            if (itemMap != null && itemMap.isSimilar(player, stack)) {
                                slot = inventory;
                                break;
                            }
                        }
                        final int setSlot = slot;
                        final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(keepItem);
                        SchedulerUtils.run(() -> {
                            if (player.isOnline()) {
                                if (setSlot == -1) {
                                    player.getInventory().addItem(keepItem);
                                } else {
                                    player.getInventory().setItem(setSlot, keepItem);
                                }
                                itemMap.setAnimations(player);
                                ServerUtils.logDebug("{Drops} " + player.getName() + " has triggered the DEATH-KEEP itemflag for " + ItemUtilities.getUtilities().getItemMap(keepItem).getConfigName() + ".");
                            }
                        });
                    }
                    player.getInventory().remove(stack);
                    event.getDrops().remove(stack);
                }
            }
            if (!topInventory.isEmpty()) {
                for (int craftInventory = 0; craftInventory < topInventory.getSize(); craftInventory++) {
                    final ItemStack stack = topInventory.getItem(craftInventory);
                    if (stack != null && (!ItemUtilities.getUtilities().isAllowed(player, stack, "death-drops") || !ItemUtilities.getUtilities().isAllowed(player, stack, "erase-drops") || !ItemUtilities.getUtilities().isAllowed(player, stack, "death-keep"))) {
                        if (!ItemUtilities.getUtilities().isAllowed(player, stack, "death-keep")) {
                            final ItemStack keepItem = stack.clone();
                            final int setSlot = craftInventory;
                            final AtomicInteger cycleTask = new AtomicInteger();
                            cycleTask.set(SchedulerUtils.runAsyncAtInterval(20L, 40L, () -> {
                                if (player.isOnline() && !player.isDead() && PlayerHandler.isCraftingInv(player)) {
                                    SchedulerUtils.run(() -> {
                                        final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(keepItem);
                                        CompatUtils.getTopInventory(player).setItem(setSlot, keepItem);
                                        itemMap.setAnimations(player);
                                    });
                                    Bukkit.getServer().getScheduler().cancelTask(cycleTask.get());
                                }
                            }));
                        }
                        topInventory.remove(stack);
                    }
                }
            }
        }
    }

    /**
     * Prevents the player from dropping the custom item.
     *
     * @param player  - The player dropping the item.
     * @param integer - The case 1 or 2 of a creative mode drop.
     */
    private void delayedSafety(final Player player, final int integer) {
        SchedulerUtils.runLater(1L, () -> {
            switch (integer) {
                case 1:
                    this.isDropping.remove(PlayerHandler.getPlayerID(player));
                    break;
                case 2:
                    this.possibleDropping.remove(PlayerHandler.getPlayerID(player));
                    break;
            }
        });
    }
}