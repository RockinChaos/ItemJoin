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

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;

public class Consumes implements Listener {

   /**
    * Gives the players the defined custom items potion effects upon consumption.
    * 
    * @param event - PlayerItemConsumeEvent.
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
	}
	
   /**
    * Refills the custom item to its original stack size when consuming the item.
    * 
    * @param event - PlayerItemConsumeEvent.
	*/
	@EventHandler(ignoreCancelled = true)
	private void onPlayerConsumesItem(PlayerItemConsumeEvent event) {
		ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
		Player player = event.getPlayer();
		if (!ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) {
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item, null, player.getWorld());
			item.setAmount(itemMap.getCount());
			SchedulerUtils.runLater(2L, () -> {
				if (itemMap != null) { 
					if (PlayerHandler.getHandItem(player) == null || PlayerHandler.getHandItem(player).getAmount() <= 1) {
						if (ServerUtils.hasSpecificUpdate("1_9")) {
							if (PlayerHandler.getMainHandItem(player) != null && PlayerHandler.getMainHandItem(player).getType() != Material.AIR) {
								PlayerHandler.setMainHandItem(player, item);
							} else if (PlayerHandler.getOffHandItem(player) != null && PlayerHandler.getOffHandItem(player).getType() != Material.AIR) {
								PlayerHandler.setOffHandItem(player, item);
							} else {
								itemMap.giveTo(player);
							}
		 				} 
		 				else { PlayerHandler.setMainHandItem(player, item); }
					} else if (itemMap.isSimilar(PlayerHandler.getHandItem(player))) { 
						PlayerHandler.getHandItem(player).setAmount(itemMap.getCount()); 
		 			} 
				}
		 	});
		}
	}
	
   /**
    * Refills the players arrows item to its original stack size when consuming the item.
    * 
    * @param event - EntityShootBowEvent.
	*/
	@EventHandler(ignoreCancelled = true)
	private void onPlayerFireArrow(EntityShootBowEvent event) {
		LivingEntity entity = event.getEntity();
		if (ServerUtils.hasSpecificUpdate("1_16") && entity instanceof Player && event.getBow() != null && event.getBow().getType() == Material.BOW) {
			ItemStack item = (event.getConsumable() != null ? event.getConsumable().clone() : event.getConsumable());
			Player player = (Player) event.getEntity();
			if (entity instanceof Player && !ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) {
				event.setConsumeItem(false);
				PlayerHandler.updateInventory(player, 1L);
			}
		} else if (entity instanceof Player) {
			HashMap < Integer, ItemStack > map = new HashMap < Integer, ItemStack > ();
			Player player = (Player) event.getEntity();
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).getType() == Material.ARROW && event.getProjectile().getType().name().equalsIgnoreCase("ARROW")) {
					ItemStack cloneStack = player.getInventory().getItem(i).clone();
					ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(player.getInventory().getItem(i), null, player.getWorld());
					if (itemMap != null) { cloneStack.setAmount(itemMap.getCount()); }
					map.put(i, cloneStack);
				}
			}
			SchedulerUtils.runLater(2L, () -> {
				for (Integer key: map.keySet()) {
					if (player.getInventory().getItem(key) == null || player.getInventory().getItem(key).getAmount() != map.get(key).getAmount()) {
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
	 * Refills the players totem item to the original stack count upon use.
	 * 
    * @param event - EntityResurrectEvent.
	 */
	 @EventHandler(ignoreCancelled = true)
	 private void onRefillTotem(EntityResurrectEvent event) {
	 	if (event.getEntity() instanceof Player) {
	 		Player player = (Player) event.getEntity();
	 		ItemStack saveMainStack = null; if (PlayerHandler.getMainHandItem(player) != null) { saveMainStack = PlayerHandler.getMainHandItem(player).clone(); }
	 		final ItemStack mainStack = saveMainStack;
	 		ItemStack saveOffStack = null; if (PlayerHandler.getOffHandItem(player) != null) { saveOffStack = PlayerHandler.getOffHandItem(player).clone(); }
	 		final ItemStack offStack = saveOffStack;
	 		ItemMap mainHandMap = ItemUtilities.getUtilities().getItemMap(mainStack, null, player.getWorld());
	 		ItemMap offHandMap = ItemUtilities.getUtilities().getItemMap(offStack, null, player.getWorld());
	 		if ((mainHandMap != null && !ItemUtilities.getUtilities().isAllowed(player, mainStack, "count-lock")) || (offHandMap != null && !ItemUtilities.getUtilities().isAllowed(player, offStack, "count-lock"))) {
	 			if ((StringUtils.getUtils().containsIgnoreCase(mainStack.getType().name(), "TOTEM") && mainHandMap != null) || (StringUtils.getUtils().containsIgnoreCase(offStack.getType().name(), "TOTEM") && offHandMap != null)) {
					SchedulerUtils.runLater(1L, () -> {
		 				if (mainHandMap != null && mainHandMap.isSimilar(mainStack)) {
		 					if (StringUtils.getUtils().containsIgnoreCase(PlayerHandler.getMainHandItem(player).getType().name(), "TOTEM")) {
		 						PlayerHandler.getMainHandItem(player).setAmount(mainHandMap.getCount());
		 					} else if (StringUtils.getUtils().containsIgnoreCase(PlayerHandler.getOffHandItem(player).getType().name(), "TOTEM")) {
		 						PlayerHandler.getOffHandItem(player).setAmount(mainHandMap.getCount());
		 					}
		 					if (PlayerHandler.getMainHandItem(player).getType() == Material.AIR) {
		 						PlayerHandler.setMainHandItem(player, mainStack);
		 					} else if (PlayerHandler.getOffHandItem(player).getType() == Material.AIR) {
		 						PlayerHandler.setOffHandItem(player, mainStack);
		 					}
		 				} else if (offHandMap != null && offHandMap.isSimilar(offStack)) {
		 					if (StringUtils.getUtils().containsIgnoreCase(PlayerHandler.getOffHandItem(player).getType().name(), "TOTEM")) {
		 						PlayerHandler.getOffHandItem(player).setAmount(offHandMap.getCount());
		 					} else if (StringUtils.getUtils().containsIgnoreCase(PlayerHandler.getMainHandItem(player).getType().name(), "TOTEM")) {
		 						PlayerHandler.getMainHandItem(player).setAmount(offHandMap.getCount());
		 					}
		 					if (PlayerHandler.getOffHandItem(player).getType() == Material.AIR) {
		 						PlayerHandler.setOffHandItem(player, offStack);
		 					} else if (PlayerHandler.getMainHandItem(player).getType() == Material.AIR) {
		 						PlayerHandler.setMainHandItem(player, offStack);
		 					}
		 				}
		 			});
	 			}
	 		}
	 	}
	 }
}