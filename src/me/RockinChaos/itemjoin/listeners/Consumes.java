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
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.Utils;

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
	
   /**
    * Refills the players arrows item to its original stack size when consuming the item.
    * 
    * @param event - EntityShootBowEvent.
	*/
	@EventHandler(ignoreCancelled = true)
	private void onPlayerFireArrow(EntityShootBowEvent event) {
		LivingEntity entity = event.getEntity();
		if (ServerHandler.getServer().hasSpecificUpdate("1_16") && entity instanceof Player && event.getBow() != null && event.getBow().getType() == Material.BOW) {
			ItemStack item = (event.getConsumable() != null ? event.getConsumable().clone() : event.getConsumable());
			Player player = (Player) event.getEntity();
			if (entity instanceof Player && !ItemUtilities.getUtilities().isAllowed(player, item, "count-lock")) {
				event.setConsumeItem(false);
				PlayerHandler.getPlayer().updateInventory(player, 1L);
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
			ServerHandler.getServer().runThread(main -> {
				for (Integer mdd: map.keySet()) {
					if (player.getInventory().getItem(mdd) == null || player.getInventory().getItem(mdd).getAmount() != map.get(mdd).getAmount()) {
						if (!ItemUtilities.getUtilities().isAllowed(player, map.get(mdd), "count-lock")) {
							player.getInventory().setItem(mdd, map.get(mdd));
						}
					}
				}
				PlayerHandler.getPlayer().updateInventory(player, 1L);
			}, 2L);
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
	 		ItemStack saveMainStack = null; if (PlayerHandler.getPlayer().getMainHandItem(player) != null) { saveMainStack = PlayerHandler.getPlayer().getMainHandItem(player).clone(); }
	 		final ItemStack mainStack = saveMainStack;
	 		ItemStack saveOffStack = null; if (PlayerHandler.getPlayer().getOffHandItem(player) != null) { saveOffStack = PlayerHandler.getPlayer().getOffHandItem(player).clone(); }
	 		final ItemStack offStack = saveOffStack;
	 		ItemMap mainHandMap = ItemUtilities.getUtilities().getItemMap(mainStack, null, player.getWorld());
	 		ItemMap offHandMap = ItemUtilities.getUtilities().getItemMap(offStack, null, player.getWorld());
	 		if ((mainHandMap != null && !ItemUtilities.getUtilities().isAllowed(player, mainStack, "count-lock")) || (offHandMap != null && !ItemUtilities.getUtilities().isAllowed(player, offStack, "count-lock"))) {
	 			if ((Utils.getUtils().containsIgnoreCase(mainStack.getType().name(), "TOTEM") && mainHandMap != null) || (Utils.getUtils().containsIgnoreCase(offStack.getType().name(), "TOTEM") && offHandMap != null)) {
	 				ServerHandler.getServer().runThread(main -> {
	 					if (mainHandMap != null && mainHandMap.isSimilar(mainStack)) {
	 						if (Utils.getUtils().containsIgnoreCase(PlayerHandler.getPlayer().getMainHandItem(player).getType().name(), "TOTEM")) {
	 							PlayerHandler.getPlayer().getMainHandItem(player).setAmount(mainHandMap.getCount());
	 						} else if (Utils.getUtils().containsIgnoreCase(PlayerHandler.getPlayer().getOffHandItem(player).getType().name(), "TOTEM")) {
	 							PlayerHandler.getPlayer().getOffHandItem(player).setAmount(mainHandMap.getCount());
	 						}
	 						if (PlayerHandler.getPlayer().getMainHandItem(player).getType() == Material.AIR) {
	 							PlayerHandler.getPlayer().setMainHandItem(player, mainStack);
	 						} else if (PlayerHandler.getPlayer().getOffHandItem(player).getType() == Material.AIR) {
	 							PlayerHandler.getPlayer().setOffHandItem(player, mainStack);
	 						}
	 					} else if (offHandMap != null && offHandMap.isSimilar(offStack)) {
	 						if (Utils.getUtils().containsIgnoreCase(PlayerHandler.getPlayer().getOffHandItem(player).getType().name(), "TOTEM")) {
	 							PlayerHandler.getPlayer().getOffHandItem(player).setAmount(offHandMap.getCount());
	 						} else if (Utils.getUtils().containsIgnoreCase(PlayerHandler.getPlayer().getMainHandItem(player).getType().name(), "TOTEM")) {
	 							PlayerHandler.getPlayer().getMainHandItem(player).setAmount(offHandMap.getCount());
	 						}
	 						if (PlayerHandler.getPlayer().getOffHandItem(player).getType() == Material.AIR) {
	 							PlayerHandler.getPlayer().setOffHandItem(player, offStack);
	 						} else if (PlayerHandler.getPlayer().getMainHandItem(player).getType() == Material.AIR) {
	 							PlayerHandler.getPlayer().setMainHandItem(player, offStack);
	 						}
	 					}
	 				}, 1L);
	 			}
	 		}
	 	}
	 }
}