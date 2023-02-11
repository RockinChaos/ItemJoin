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
import java.util.Map;

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemData;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class Drops implements Listener {
	
	private Map < String, Boolean > isDropping = new HashMap < String, Boolean > ();
	private Map < String, Boolean > possibleDropping = new HashMap < String, Boolean > ();
	
	
   /**
	* Prevents the player from dropping all items.
	* 
	* @param event - PlayerDropItemEvent.
	*/
	@EventHandler(ignoreCancelled = true)
	private void onGlobalDrop(PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
		if (ItemData.getInfo().isPreventString(player, "Self-Drops")) {
		  	if (ItemData.getInfo().isPreventBypass(player)) { } 
		  	else { 
		  		if (!player.isDead()) {
		  			if (PlayerHandler.isCreativeMode(player)) { player.closeInventory(); } 
					event.setCancelled(true);
		  		} else if (player.isDead()) {
					event.getItemDrop().remove();
				} 
		  	}
		}
	}
		
   /**
	* Prevents the player from dropping all items on death.
	* 
	* @param event - PlayerDeathEvent.
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onGlobalDeathDrops(PlayerDeathEvent event) {
		final Player player = event.getEntity();
		ItemUtilities.getUtilities().closeAnimations(player);
		if (ItemData.getInfo().isPreventString(player, "Death-Drops")) {
		  	if (ItemData.getInfo().isPreventBypass(player)) { }
		  	else if (!LegacyAPI.getGameRule(player.getWorld(), "keepInventory")) {
				for (int k = 0; k < player.getInventory().getSize(); k++) {
					ItemStack stack = player.getInventory().getItem(k);
					if (stack != null && stack.getType() != Material.AIR) { 
						player.getInventory().remove(stack);
						event.getDrops().remove(stack);
					}
				}
		  	}
		}
	}
	
   /**
	* Prevents the player from dropping the custom item.
	* 
	* @param event - PlayerDropItemEvent.
	*/
	@EventHandler(ignoreCancelled = true)
	private void onDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		final Player player = event.getPlayer();
		if (!player.isDead() && !ItemUtilities.getUtilities().isAllowed(player, item, "self-drops") && !ItemUtilities.getUtilities().getItemMap(item).isCraftingItem()) {
			if (!this.possibleDropping.containsKey(PlayerHandler.getPlayerID(player))) { event.setCancelled(true); }
			if (PlayerHandler.isCreativeMode(player) && this.possibleDropping.containsKey(PlayerHandler.getPlayerID(player)) && this.possibleDropping.get(PlayerHandler.getPlayerID(player))) { 
				player.closeInventory();
				event.getItemDrop().remove(); 
				this.isDropping.put(PlayerHandler.getPlayerID(player), true);
				this.possibleDropping.remove(PlayerHandler.getPlayerID(player));
				this.delayedSaftey(player, 1);
			} else if (PlayerHandler.isCreativeMode(player)) { player.closeInventory(); } 
		} else if (player.isDead() && !ItemUtilities.getUtilities().isAllowed(player, item, "self-drops")) {
			event.getItemDrop().remove();
		}
	}

   /**
	* Prevents the player from dropping the custom item.
	* 
	* @param event - InventoryClickEvent.
	*/
	@EventHandler(ignoreCancelled = true)
	private void onCreativeDrop(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (PlayerHandler.isCreativeMode(player) && this.isDropping.containsKey(PlayerHandler.getPlayerID(player)) && this.isDropping.get(PlayerHandler.getPlayerID(player))) {
			if (!ItemUtilities.getUtilities().isAllowed(player, event.getCurrentItem(), "self-drops")) {
				event.setCancelled(true);
				player.closeInventory();
				PlayerHandler.updateInventory(player, 1L);
				this.isDropping.remove(PlayerHandler.getPlayerID(player));
			}
		}
		if (PlayerHandler.isCreativeMode(player) && event.getSlot() == -999 && !this.possibleDropping.containsKey(PlayerHandler.getPlayerID(player))) {
			this.possibleDropping.put(PlayerHandler.getPlayerID(player), true);
			this.delayedSaftey(player, 2); 
		}
	}

   /**
	* Prevents the player from dropping the custom item on death.
	* 
	* @param event - PlayerItemConsumeEvent.
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onDeathDrops(PlayerDeathEvent event) {
		Player player = event.getEntity();
		ItemUtilities.getUtilities().closeAnimations(player);
		if (!LegacyAPI.getGameRule(player.getWorld(), "keepInventory")) {
			for (int k = 0; k < player.getOpenInventory().getTopInventory().getSize(); k++) {
				ItemStack stack = player.getOpenInventory().getTopInventory().getItem(k);
				if (PlayerHandler.isCraftingInv(player.getOpenInventory()) && (!ItemUtilities.getUtilities().isAllowed(player, stack, "death-drops") || !ItemUtilities.getUtilities().isAllowed(player, stack, "death-keep"))) {
					event.getDrops().remove(stack);
					player.getOpenInventory().getTopInventory().remove(stack);
				}
			}
			for (int k = 0; k < player.getInventory().getSize(); k++) {
				ItemStack stack = player.getInventory().getItem(k);
				if (!ItemUtilities.getUtilities().isAllowed(player, stack, "death-drops") || !ItemUtilities.getUtilities().isAllowed(player, stack, "death-keep")) {
					event.getDrops().remove(stack);
					player.getInventory().remove(stack);
				}
			}
		}
	}
	
   /**
	* Prevents the player from dropping the custom item.
	* 
	* @param player - The player dropping the item.
	* @param integer - The case 1 or 2 of a creative mode drop.
	*/
	private void delayedSaftey(final Player player, final int integer) {
		SchedulerUtils.runLater(1L, () -> {
			switch(integer) {
			case 1:
				if (this.isDropping.containsKey(PlayerHandler.getPlayerID(player))) {
					this.isDropping.remove(PlayerHandler.getPlayerID(player));
				}
				break;
				case 2:
					if (this.possibleDropping.containsKey(PlayerHandler.getPlayerID(player))) {
						this.possibleDropping.remove(PlayerHandler.getPlayerID(player));
					}
			    break;
			}
		});
	}
}