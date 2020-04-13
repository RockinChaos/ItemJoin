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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;

public class Legacy_Consumes implements Listener {

	@EventHandler
	private void onPlayerConsumesItem(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		if (item.getType() == Material.GOLDEN_APPLE) {
			ItemMap itemMap = ItemUtilities.getItemMap(item, null, player.getWorld());
			if (itemMap != null && itemMap.getMaterial() == Material.GOLDEN_APPLE && itemMap.isCustomConsumable()) {
				if (itemMap.getPotionEffect() != null && !itemMap.getPotionEffect().isEmpty()) {
					for (PotionEffect potion: itemMap.getPotionEffect()) { player.addPotionEffect(potion); }
				}
				event.setCancelled(true);
				player.getInventory().remove(item);
			}
		}
	}
}