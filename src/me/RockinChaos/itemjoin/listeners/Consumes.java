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
import org.bukkit.event.entity.EntityResurrectEvent;
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
	private void onPlayerConsumesItem(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		if (item.getType() == Material.GOLDEN_APPLE) {
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item, null, player.getWorld());
			if (itemMap != null && itemMap.getMaterial() == Material.GOLDEN_APPLE && itemMap.isCustomConsumable()) {
				if (itemMap.getPotionEffect() != null && !itemMap.getPotionEffect().isEmpty()) {
					for (PotionEffect potion: itemMap.getPotionEffect()) { player.addPotionEffect(potion); }
				}
				event.setCancelled(true);
				player.getInventory().remove(item);
			}
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