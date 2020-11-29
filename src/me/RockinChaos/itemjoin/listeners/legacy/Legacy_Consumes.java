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

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;

/**
* Handles the Consumption events for custom items.
* 
* @deprecated This is a LEGACY listener, only use on Minecraft versions below 1.11.
*/
public class Legacy_Consumes implements Listener {
	
	
   /**
	* Gives the players the defined custom items potion effects upon consumption.
	* 
	* @param event - PlayerItemConsumeEvent.
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.11.
	*/
	@EventHandler(ignoreCancelled = true)
	private void onPlayerAppleEffects(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		if (item.getType() == Material.GOLDEN_APPLE) {
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item, null, player.getWorld());
			if (itemMap != null && itemMap.getMaterial() == Material.GOLDEN_APPLE && itemMap.isCustomConsumable()) {
				if (itemMap.getPotionEffect() != null && !itemMap.getPotionEffect().isEmpty()) {
					for (PotionEffect potion: itemMap.getPotionEffect()) { player.addPotionEffect(potion); }
				}
				event.setCancelled(true);
				if (ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) { 
					if (item.getAmount() <= 1) {
						if (itemMap.isReal(PlayerHandler.getPlayer().getMainHandItem(player))) {
							PlayerHandler.getPlayer().setMainHandItem(player, new ItemStack(Material.AIR));
						} else if (itemMap.isReal(PlayerHandler.getPlayer().getOffHandItem(player))) {
							PlayerHandler.getPlayer().setOffHandItem(player, new ItemStack(Material.AIR));
						}
					} else {
						item.setAmount((item.getAmount() - 1)); 
						if (itemMap.isReal(PlayerHandler.getPlayer().getMainHandItem(player))) {
							PlayerHandler.getPlayer().setMainHandItem(player, item);
						} else if (itemMap.isReal(PlayerHandler.getPlayer().getOffHandItem(player))) {
							PlayerHandler.getPlayer().setOffHandItem(player, item);
						}
					}
				}
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
		ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
		Player player = event.getPlayer();
		if (!ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) {
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item, null, player.getWorld());
			item.setAmount(itemMap.getCount());
			ServerHandler.getServer().runThread(main -> {
				if (itemMap != null) { 
					if (PlayerHandler.getPlayer().getHandItem(player) == null || PlayerHandler.getPlayer().getHandItem(player).getAmount() <= 1) {
						if (ServerHandler.getServer().hasSpecificUpdate("1_9")) {
							if (PlayerHandler.getPlayer().getMainHandItem(player) != null && PlayerHandler.getPlayer().getMainHandItem(player).getType() != Material.AIR) {
								PlayerHandler.getPlayer().setMainHandItem(player, item);
							} else if (PlayerHandler.getPlayer().getOffHandItem(player) != null && PlayerHandler.getPlayer().getOffHandItem(player).getType() != Material.AIR) {
								PlayerHandler.getPlayer().setOffHandItem(player, item);
							} else {
								itemMap.giveTo(player);
							}
	 					} 
	 					else { PlayerHandler.getPlayer().setMainHandItem(player, item); }
					} else if (itemMap.isSimilar(PlayerHandler.getPlayer().getHandItem(player))) { 
						PlayerHandler.getPlayer().getHandItem(player).setAmount(itemMap.getCount()); 
	 				} 
				}
	 		}, 2L);
		}
	}
}