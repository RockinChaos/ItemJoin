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
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Projectile implements Listener {

    private final HashMap<Integer, ItemStack> projectileList = new HashMap<>();

    /**
     * Refills the players projectile item to its original stack size when consuming the item.
     *
     * @param event - EntityShootBowEvent.
     */
    @EventHandler(ignoreCancelled = true)
    private void onProjectileFire(EntityShootBowEvent event) {
        final LivingEntity entity = event.getEntity();
        if (ServerUtils.hasSpecificUpdate("1_16") && entity instanceof Player && event.getBow() != null) {
            final ItemStack consumable = (event.getConsumable() != null ? event.getConsumable().clone() : event.getConsumable());
            final Player player = (Player) event.getEntity();
            this.projectileList.put(event.getProjectile().getEntityId(), consumable);
            if (!event.getBow().getType().name().equalsIgnoreCase("CROSSBOW") && !ItemUtilities.getUtilities().isAllowed(player, consumable, "count-lock")) {
                SchedulerUtils.runLater(1L, () -> {
                    boolean setConsumable = false;
                    for (final ItemStack item : player.getInventory()) {
                        if (item != null && item.isSimilar(consumable)) {
                            item.setAmount(item.getAmount() + consumable.getAmount());
                            setConsumable = true;
                            ServerUtils.logDebug("{Projectile} Added " + consumable.getAmount() + "x of " + consumable.getType() + " to existing stack in " + player.getName() + "'s inventory.");
                            break;
                        }
                    }
                    if (!setConsumable) {
                        ItemUtilities.getUtilities().getItemMap(consumable).giveTo(player);
                    }
                });
            }
        } else if (entity instanceof Player) {
            final HashMap<Integer, ItemStack> map = new HashMap<>();
            final Player player = (Player) event.getEntity();
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                final ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() == Material.ARROW && event.getProjectile().getType().name().equalsIgnoreCase("ARROW")) {
                    final ItemStack cloneStack = item.clone();
                    final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
                    if (itemMap != null) {
                        cloneStack.setAmount(itemMap.getCount(player));
                    }
                    map.put(i, cloneStack);
                }
            }
            SchedulerUtils.runLater(2L, () -> {
                for (Integer key : map.keySet()) {
                    final ItemStack item = player.getInventory().getItem(key);
                    if (item == null || item.getAmount() != map.get(key).getAmount()) {
                        this.projectileList.put(event.getProjectile().getEntityId(), map.get(key));
                        if (!ItemUtilities.getUtilities().isAllowed(player, map.get(key), "count-lock")) {
                            player.getInventory().setItem(key, map.get(key));
                        }
                    }
                }
                PlayerHandler.updateInventory(player, 1L);
            });
        }
    }

    /**
     * Teleports the Player to the custom projectile's landed position.
     *
     * @param event - ProjectileHitEvent
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit(ProjectileHitEvent event) {
        final org.bukkit.entity.Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player) {
            if (this.projectileList.get(projectile.getEntityId()) != null && !ItemUtilities.getUtilities().isAllowed((Player) projectile.getShooter(), this.projectileList.get(projectile.getEntityId()), "teleport")) {
                final Player player = (Player) projectile.getShooter();
                final Location location = projectile.getLocation();
                final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(this.projectileList.get(projectile.getEntityId()));
                location.setPitch(player.getLocation().getPitch());
                location.setYaw(player.getLocation().getYaw());
                player.teleport(location);
                if (itemMap.getTeleportEffect() != null) {
                    try {
                        projectile.getWorld().playEffect(projectile.getLocation(), Effect.valueOf(itemMap.getTeleportEffect()), 15);
                    } catch (Exception e) {
                        ServerUtils.logSevere("{Projectile} The defined teleport-effect " + itemMap.getTeleportEffect() + " for the item " + itemMap.getConfigName() + " is not valid!");
                    }
                }
                if (itemMap.getTeleportSound() != null && !itemMap.getTeleportSound().isEmpty()) {
                    try {
                        projectile.getWorld().playSound(projectile.getLocation(), Sound.valueOf(itemMap.getTeleportSound()), (float) ((double) itemMap.getTeleportVolume()), (float) ((double) itemMap.getTeleportPitch()));
                    } catch (Exception e) {
                        ServerUtils.logSevere("{Projectile} The defined teleport-sound " + itemMap.getTeleportSound() + " for the item " + itemMap.getConfigName() + " is not valid!");
                    }
                }
                projectile.remove();
            }
        }
    }

    /**
     * Refills the custom projectile item to its original stack size when using a crossbow.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler()
    private void onCrossbow(PlayerInteractEvent event) {
        ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
        Player player = event.getPlayer();
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && item != null && item.getType().name().equalsIgnoreCase("CROSSBOW") && !PlayerHandler.isCreativeMode(player)) {
            HashMap<Integer, ItemStack> map = new HashMap<>();
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                final ItemStack invItem = player.getInventory().getItem(i);
                if (invItem != null && invItem.getType() == Material.ARROW) {
                    ItemStack cloneStack = invItem.clone();
                    ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(player.getInventory().getItem(i));
                    if (itemMap != null) {
                        cloneStack.setAmount(itemMap.getCount(player));
                    }
                    map.put(i, cloneStack);
                }
            }
            this.crossyAction(player, map, 10);
        }
    }

    /**
     * Checks if the Crossbow action was triggered.
     *
     * @param player - The player performing the action.
     * @param map    - The slot and stack to be compared to the players inventory.
     * @param tries  - The number of remaining attempts to refill Crossbow Arrows.
     */
    public void crossyAction(Player player, HashMap<Integer, ItemStack> map, int tries) {
        if (tries != 0) {
            SchedulerUtils.runLater(26L, () -> {
                boolean projectileReturned = false;
                for (Integer key : map.keySet()) {
                    final ItemStack item = player.getInventory().getItem(key);
                    if (item == null || item.getAmount() != map.get(key).getAmount()) {
                        if (!ItemUtilities.getUtilities().isAllowed(player, map.get(key), "count-lock")) {
                            player.getInventory().setItem(key, map.get(key));
                            projectileReturned = true;
                        }
                    }
                }
                if (projectileReturned) {
                    PlayerHandler.updateInventory(player, 1L);
                } else {
                    this.crossyAction(player, map, (tries - 1));
                }
            });
        }
    }
}