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

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
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

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;

public class Projectile implements Listener {
	
   /**
    * Refills the players arrows item to its original stack size when consuming the item.
    * 
    * @param event - EntityShootBowEvent.
	*/
	@EventHandler(ignoreCancelled = true)
	private void onArrowFire(EntityShootBowEvent event) {
		LivingEntity entity = event.getEntity();
		if (ServerUtils.hasSpecificUpdate("1_16") && entity instanceof Player && event.getBow() != null && event.getBow().getType() == Material.BOW) {
			ItemStack item = (event.getConsumable() != null ? event.getConsumable().clone() : event.getConsumable());
			Player player = (Player) event.getEntity();
			this.arrowList.put(event.getProjectile().getEntityId(), item);
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
					ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(player.getInventory().getItem(i));
					if (itemMap != null) { cloneStack.setAmount(itemMap.getCount(player)); }
					map.put(i, cloneStack);
				}
			}
			SchedulerUtils.runLater(2L, () -> {
				for (Integer key: map.keySet()) {
					if (player.getInventory().getItem(key) == null || player.getInventory().getItem(key).getAmount() != map.get(key).getAmount()) {
						this.arrowList.put(event.getProjectile().getEntityId(), map.get(key));
						if (!ItemUtilities.getUtilities().isAllowed(player, map.get(key), "count-lock")) {
							player.getInventory().setItem(key, map.get(key));
						}
					}
				}
				PlayerHandler.updateInventory(player, 1L);
			});
		}
	}
	private HashMap<Integer, ItemStack> arrowList = new HashMap<Integer, ItemStack>();

   /**
	* Teleports the Player to the custom arrow's landed position.
    * 
	* @param event - ProjectileHitEvent
	*/
    @EventHandler(priority=EventPriority.NORMAL)
    public void onArrowHit(ProjectileHitEvent event) {
        final org.bukkit.entity.Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow && ((Arrow)projectile).getShooter() instanceof Player) {
        	if (this.arrowList.get(projectile.getEntityId()) != null && !ItemUtilities.getUtilities().isAllowed((Player)((Arrow)projectile).getShooter(), this.arrowList.get(projectile.getEntityId()), "teleport")) { 
	            final Player player = (Player)((Arrow)projectile).getShooter();
	            final Location location = ((Arrow)projectile).getLocation();
	            final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(this.arrowList.get(projectile.getEntityId()));
	            location.setPitch(player.getLocation().getPitch());
	            location.setYaw(player.getLocation().getYaw());
	            player.teleport(location);
	            if (itemMap.getTeleportEffect() != null) {
	            	try {
	            		((Arrow)projectile).getWorld().playEffect(((Arrow)projectile).getLocation(), Effect.valueOf(itemMap.getTeleportEffect()), 15);
	            	} catch (Exception e) {
	            		ServerUtils.logSevere("The defined teleport-effect " + itemMap.getTeleportEffect() + " for the item " + itemMap.getConfigName() + " is not valid!"); 
	            	}
	            }
	            if (itemMap.getTeleportSound() != null && !itemMap.getTeleportSound().isEmpty()) {
	            	try {
	            		((Arrow)projectile).getWorld().playSound(((Arrow)projectile).getLocation(), Sound.valueOf(itemMap.getTeleportSound()), (float) ((double)itemMap.getTeleportVolume()), (float) ((double)itemMap.getTeleportPitch()));
	            	} catch (Exception e) {
	            		ServerUtils.logSevere("The defined teleport-sound " + itemMap.getTeleportSound() + " for the item " + itemMap.getConfigName() + " is not valid!"); 
	            	}
	            }
	            projectile.remove();
        	}
        }
    }

   /**
	* Refills the custom arrow item to its original stack size when using a crossbow.
	* 
	* @param event - PlayerInteractEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onCrossbow(PlayerInteractEvent event) {
		ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
	 	Player player = event.getPlayer();
	 	if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && item != null && item.getType().name().equalsIgnoreCase("CROSSBOW") && !PlayerHandler.isCreativeMode(player)) {
	 		HashMap < Integer, ItemStack > map = new HashMap < Integer, ItemStack > ();
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).getType() == Material.ARROW) {
					ItemStack cloneStack = player.getInventory().getItem(i).clone();
					ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(player.getInventory().getItem(i));
					if (itemMap != null) { cloneStack.setAmount(itemMap.getCount(player)); }
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
	* @param map - The slot and stack to be compared to the players inventory.
	* @param tries - The number of remaining attempts to refill Crossbow Arrows.
	*/
	public void crossyAction(Player player, HashMap < Integer, ItemStack > map, int tries) {
		if (tries != 0) {
			SchedulerUtils.runLater(26L, () -> {
				boolean arrowReturned = false;
				for (Integer key: map.keySet()) {
					if (player.getInventory().getItem(key) == null || player.getInventory().getItem(key).getAmount() != map.get(key).getAmount()) {
						if (!ItemUtilities.getUtilities().isAllowed(player, map.get(key), "count-lock")) {
		 					player.getInventory().setItem(key, map.get(key));
		 					arrowReturned = true;
		 				}
		 			}
		 		}
		 		if (arrowReturned) { PlayerHandler.updateInventory(player, 1L); } 
		 		else {this.crossyAction(player, map, (tries - 1)); }
		 	});
	 	}
	}
}