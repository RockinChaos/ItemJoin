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

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.api.DependAPI;

public class Breaking implements Listener {
	
   /**
    * Handles the Block Break custom item drops.
    * 
    * @param event - BlockBreakEvent
    */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerBreakBlock(BlockBreakEvent event) {
		final Block block = event.getBlock();
		final Material material = (block != null ? block.getType() : Material.AIR);
		final Player player = event.getPlayer();
		final Collection<ItemStack> drops = block.getDrops(PlayerHandler.getMainHandItem(player));
		SchedulerUtils.runAsync(() -> {
			for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
				if (itemMap.blocksDrop() && block != null && material != Material.AIR && itemMap.getBlocksDrop().containsKey(material) 
				 && itemMap.inWorld(player.getWorld()) && itemMap.isLimitMode(player.getGameMode()) && itemMap.hasPermission(player) && ItemHandler.containsMaterial(drops, material) && Math.random() <= itemMap.getBlocksDrop().get(material)) {
					for (String region : ((DependAPI.getDepends(false).getGuard().guardEnabled() && !itemMap.getEnabledRegions().isEmpty()) ? DependAPI.getDepends(false).getGuard().getRegionAtLocation(player.getLocation()).split(", ") : new String[]{"FALSE"})) {
						if (!DependAPI.getDepends(false).getGuard().guardEnabled() || itemMap.getEnabledRegions().isEmpty() || itemMap.inRegion(region)) { 
							SchedulerUtils.run(() -> block.getWorld().dropItemNaturally(block.getLocation(), itemMap.getItem(player)));
						}
					}
				}
			}
		});
	}
}