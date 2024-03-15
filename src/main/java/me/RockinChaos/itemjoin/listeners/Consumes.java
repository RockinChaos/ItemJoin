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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class Consumes implements Listener {

    /**
     * Gives the players the defined custom items potion effects upon consumption.
     *
     * @param event - PlayerItemConsumeEvent.
     */
    @EventHandler(ignoreCancelled = true)
    private void onConsumeEffects(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
        if (itemMap != null && itemMap.getMaterial().isEdible() && itemMap.isCustomConsumable() && itemMap.inWorld(event.getPlayer().getWorld())) {
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
     */
    @EventHandler()
    private void onConsumeSkullEffects(PlayerInteractEvent event) {
        final ItemStack item = event.getItem();
        final Player player = event.getPlayer();
        final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
        if (itemMap != null && ItemHandler.isSkull(itemMap.getMaterial()) && itemMap.isCustomConsumable() && itemMap.inWorld(event.getPlayer().getWorld())) {
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
     */
    @EventHandler(ignoreCancelled = true)
    private void onPlayerConsumesItem(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem().clone();
        Player player = event.getPlayer();
        if (!ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) {
            ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
            item.setAmount(itemMap.getCount(player));
            SchedulerUtils.runLater(2L, () -> {
                final ItemStack handItem = PlayerHandler.getHandItem(player);
                if (handItem.getAmount() <= 1) {
                    if (ServerUtils.hasSpecificUpdate("1_9")) {
                        final ItemStack mainItem = PlayerHandler.getMainHandItem(player);
                        final ItemStack offItem = PlayerHandler.getOffHandItem(player);
                        if (mainItem.getType() != Material.AIR) {
                            PlayerHandler.setMainHandItem(player, item);
                        } else if (offItem.getType() != Material.AIR) {
                            PlayerHandler.setOffHandItem(player, item);
                        } else {
                            itemMap.giveTo(player);
                        }
                    } else {
                        PlayerHandler.setMainHandItem(player, item);
                    }
                } else if (itemMap.isSimilar(player, handItem)) {
                    handItem.setAmount(itemMap.getCount(player));
                }
            });
        }
    }

    /**
     * Refills the players totem item to the original stack count upon use.
     *
     * @param event - EntityResurrectEvent.
     */
    @EventHandler(ignoreCancelled = true)
    private void onRefillTotem(EntityResurrectEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            final ItemStack mainStack = PlayerHandler.getMainHandItem(player).clone();
            final ItemStack offStack = PlayerHandler.getOffHandItem(player).clone();
            ItemMap mainHandMap = ItemUtilities.getUtilities().getItemMap(mainStack);
            ItemMap offHandMap = ItemUtilities.getUtilities().getItemMap(offStack);
            if ((mainHandMap != null && !ItemUtilities.getUtilities().isAllowed(player, mainStack, "count-lock")) || (offHandMap != null && !ItemUtilities.getUtilities().isAllowed(player, offStack, "count-lock"))) {
                if (StringUtils.containsIgnoreCase(mainStack.getType().name(), "TOTEM") && mainHandMap != null || StringUtils.containsIgnoreCase(offStack.getType().name(), "TOTEM") && offHandMap != null) {
                    SchedulerUtils.runLater(1L, () -> {
                        final ItemStack mainItem = PlayerHandler.getMainHandItem(player);
                        final ItemStack offItem = PlayerHandler.getOffHandItem(player);
                        if (mainHandMap != null && mainHandMap.isSimilar(player, mainStack)) {
                            if (StringUtils.containsIgnoreCase(mainItem.getType().name(), "TOTEM")) {
                                mainItem.setAmount(mainHandMap.getCount(player));
                            } else if (StringUtils.containsIgnoreCase(offItem.getType().name(), "TOTEM")) {
                                offItem.setAmount(mainHandMap.getCount(player));
                            }
                            if (mainItem.getType() == Material.AIR) {
                                PlayerHandler.setMainHandItem(player, mainStack);
                            } else if (offItem.getType() == Material.AIR) {
                                PlayerHandler.setOffHandItem(player, mainStack);
                            }
                        } else if (offHandMap != null && offHandMap.isSimilar(player, offStack)) {
                            if (StringUtils.containsIgnoreCase(offItem.getType().name(), "TOTEM")) {
                                offItem.setAmount(offHandMap.getCount(player));
                            } else if (StringUtils.containsIgnoreCase(mainItem.getType().name(), "TOTEM")) {
                                mainItem.setAmount(offHandMap.getCount(player));
                            }
                            if (offItem.getType() == Material.AIR) {
                                PlayerHandler.setOffHandItem(player, offStack);
                            } else if (mainItem.getType() == Material.AIR) {
                                PlayerHandler.setMainHandItem(player, offStack);
                            }
                        }
                    });
                }
            }
        }
    }
}