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
package me.RockinChaos.itemjoin.listeners.legacy;

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Objects;

/**
 * Handles the Consumption events for custom items.
 *
 * @deprecated This is a LEGACY listener, only use on Minecraft versions below 1.11.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
public class Legacy_Consumes implements Listener {

    /**
     * Gives the players the defined custom items potion effects upon consumption.
     *
     * @param event - PlayerItemConsumeEvent.
     * @deprecated This is a LEGACY event, only use on Minecraft versions below 1.11.
     */
    @EventHandler(ignoreCancelled = true)
    private void onConsumeEffects(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
        if (itemMap != null && itemMap.getMaterial().isEdible() && itemMap.isCustomConsumable()) {
            if (itemMap.getPotionEffect() != null && !itemMap.getPotionEffect().isEmpty()) {
                for (PotionEffect potion : itemMap.getPotionEffect()) {
                    player.addPotionEffect(potion);
                }
            }
            event.setCancelled(true);
            if (ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) {
                if (item.getAmount() <= 1) {
                    if (itemMap.isReal(PlayerHandler.getMainHandItem(player))) {
                        PlayerHandler.setMainHandItem(player, new ItemStack(Material.AIR));
                    } else if (itemMap.isReal(PlayerHandler.getOffHandItem(player))) {
                        PlayerHandler.setOffHandItem(player, new ItemStack(Material.AIR));
                    }
                } else {
                    item.setAmount((item.getAmount() - 1));
                    if (itemMap.isReal(PlayerHandler.getMainHandItem(player))) {
                        PlayerHandler.setMainHandItem(player, item);
                    } else if (itemMap.isReal(PlayerHandler.getOffHandItem(player))) {
                        PlayerHandler.setOffHandItem(player, item);
                    }
                }
            }
        }
    }

    /**
     * Gives the players the defined custom items potion effects upon consuming a skull.
     *
     * @param event - PlayerInteractEvent.
     * @deprecated This is a LEGACY event, only use on Minecraft versions below 1.11.
     */
    @EventHandler()
    private void onConsumeSkullEffects(PlayerInteractEvent event) {
        final ItemStack item = event.getItem();
        final Player player = event.getPlayer();
        final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
        if (itemMap != null && ItemHandler.isSkull(itemMap.getMaterial()) && itemMap.isCustomConsumable()) {
            if (itemMap.getPotionEffect() != null && !itemMap.getPotionEffect().isEmpty()) {
                for (PotionEffect potion : itemMap.getPotionEffect()) {
                    player.addPotionEffect(potion);
                }
            }
            event.setCancelled(true);
            if (item != null && item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else if (itemMap.isReal(PlayerHandler.getMainHandItem(player))) {
                PlayerHandler.setMainHandItem(player, new ItemStack(Material.AIR));
            } else if (itemMap.isReal(PlayerHandler.getOffHandItem(player))) {
                PlayerHandler.setOffHandItem(player, new ItemStack(Material.AIR));
            }
        }
    }

    /**
     * Refills the custom item to its original stack size when consuming the item.
     *
     * @param event - PlayerItemConsumeEvent.
     * @deprecated This is a LEGACY event, only use on Minecraft versions below 1.11.
     */
    @EventHandler(ignoreCancelled = true)
    private void onPlayerConsumesItem(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem().clone();
        Player player = event.getPlayer();
        if (!ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) {
            ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
            item.setAmount(itemMap.getCount(player));
            SchedulerUtils.runLater(2L, () -> {
                if (PlayerHandler.getHandItem(player) == null || Objects.requireNonNull(PlayerHandler.getHandItem(player)).getAmount() <= 1) {
                    if (ServerUtils.hasSpecificUpdate("1_9")) {
                        if (PlayerHandler.getMainHandItem(player) != null && Objects.requireNonNull(PlayerHandler.getMainHandItem(player)).getType() != Material.AIR) {
                            PlayerHandler.setMainHandItem(player, item);
                        } else if (PlayerHandler.getOffHandItem(player) != null && Objects.requireNonNull(PlayerHandler.getOffHandItem(player)).getType() != Material.AIR) {
                            PlayerHandler.setOffHandItem(player, item);
                        } else {
                            itemMap.giveTo(player);
                        }
                    } else {
                        PlayerHandler.setMainHandItem(player, item);
                    }
                } else if (itemMap.isSimilar(player, PlayerHandler.getHandItem(player))) {
                    Objects.requireNonNull(PlayerHandler.getHandItem(player)).setAmount(itemMap.getCount(player));
                }
            });
        }
    }
}