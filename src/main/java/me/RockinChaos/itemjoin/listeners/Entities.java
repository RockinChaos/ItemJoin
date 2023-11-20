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

import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class Entities implements Listener {

    /**
     * Handles the Mob Death custom item drops.
     *
     * @param event - EntityDeathEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onMobDeath(EntityDeathEvent event) {
        final LivingEntity victim = event.getEntity();
        final Player killer = victim.getKiller();
        SchedulerUtils.runAsync(() -> {
            for (ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
                if (itemMap.mobsDrop() && itemMap.getMobsDrop().containsKey(victim.getType()) && itemMap.inWorld(victim.getWorld())
                        && (killer == null || (itemMap.isLimitMode(killer.getGameMode()) && itemMap.hasPermission(killer, killer.getWorld()))) && Math.random() <= itemMap.getMobsDrop().get(victim.getType())) {
                    for (String region : ((ItemJoin.getCore().getDependencies().getGuard().guardEnabled() && !itemMap.getEnabledRegions().isEmpty()) ? ItemJoin.getCore().getDependencies().getGuard().getRegionAtLocation(victim.getLocation()).split(", ") : new String[]{"FALSE"})) {
                        final World victimWorld = victim.getLocation().getWorld();
                        if (victimWorld != null && (!ItemJoin.getCore().getDependencies().getGuard().guardEnabled() || itemMap.getEnabledRegions().isEmpty() || itemMap.inRegion(region))) {
                            SchedulerUtils.run(() -> victimWorld.dropItem(victim.getLocation(), itemMap.getItem((killer))));
                        }
                    }
                }
            }
        });
    }
}