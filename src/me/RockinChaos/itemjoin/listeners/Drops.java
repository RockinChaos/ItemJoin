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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class Drops implements Listener {
	private Map < String, Boolean > isDropping = new HashMap < String, Boolean > ();
	private Map < String, Boolean > possibleDropping = new HashMap < String, Boolean > ();
	
	@EventHandler
	private void onDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		final Player player = event.getPlayer();
		if (!player.isDead() && !ItemUtilities.isAllowed(player, item, "self-drops")) {
			if (!this.possibleDropping.containsKey(PlayerHandler.getPlayerID(player))) { event.setCancelled(true); }
			if (PlayerHandler.isCreativeMode(player) && this.possibleDropping.containsKey(PlayerHandler.getPlayerID(player)) && this.possibleDropping.get(PlayerHandler.getPlayerID(player))) { 
				player.closeInventory();
				event.getItemDrop().remove(); 
				this.isDropping.put(PlayerHandler.getPlayerID(player), true);
				this.possibleDropping.remove(PlayerHandler.getPlayerID(player));
				this.delayedSaftey(player, 1);
			} else if (PlayerHandler.isCreativeMode(player)) { player.closeInventory(); } 
		} else if (player.isDead() && !ItemUtilities.isAllowed(player, item, "self-drops")) {
			event.getItemDrop().remove();
		}
	}

	@EventHandler
	private void onCreativeDrop(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (PlayerHandler.isCreativeMode(player) && this.isDropping.containsKey(PlayerHandler.getPlayerID(player)) && this.isDropping.get(PlayerHandler.getPlayerID(player))) {
			if (!ItemUtilities.isAllowed(player, event.getCurrentItem(), "self-drops")) {
				event.setCancelled(true);
				player.closeInventory();
				PlayerHandler.updateInventory(player);
				this.isDropping.remove(PlayerHandler.getPlayerID(player));
			}
		}
		if (PlayerHandler.isCreativeMode(player) && event.getSlot() == -999 && !this.possibleDropping.containsKey(PlayerHandler.getPlayerID(player))) {
			this.possibleDropping.put(PlayerHandler.getPlayerID(player), true);
			this.delayedSaftey(player, 2); 
		}
	}

	@EventHandler
	private void onDeathDrops(PlayerDeathEvent event) {
		List < ItemStack > drops = event.getDrops();
		ListIterator < ItemStack > litr = drops.listIterator();
		Player player = event.getEntity();
		ItemUtilities.closeAnimations(player);
		while (litr.hasNext()) {
			ItemStack stack = litr.next();
			if (!ItemUtilities.isAllowed(player, stack, "death-drops")) { litr.remove(); }
		}
	}
	
	@EventHandler
	private void onGlobalDrop(PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
	  	if (Utils.containsIgnoreCase(ConfigHandler.isPreventDrops(), "TRUE") || Utils.containsIgnoreCase(ConfigHandler.isPreventDrops(), player.getWorld().getName())
	  		|| Utils.containsIgnoreCase(ConfigHandler.isPreventDrops(), "ALL") || Utils.containsIgnoreCase(ConfigHandler.isPreventDrops(), "GLOBAL")) {
	  		if (ConfigHandler.isPreventOBypass() && player.isOp() || ConfigHandler.isPreventCBypass() && PlayerHandler.isCreativeMode(player)) { } 
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
	
	@EventHandler
	private void onGlobalDeathDrops(PlayerDeathEvent event) {
		List < ItemStack > drops = event.getDrops();
		ListIterator < ItemStack > litr = drops.listIterator();
		Player player = event.getEntity();
		ItemUtilities.closeAnimations(player);
		if (Utils.containsIgnoreCase(ConfigHandler.isPreventDeathDrops(), "TRUE") || Utils.containsIgnoreCase(ConfigHandler.isPreventDeathDrops(), player.getWorld().getName())
		  		|| Utils.containsIgnoreCase(ConfigHandler.isPreventDeathDrops(), "ALL") || Utils.containsIgnoreCase(ConfigHandler.isPreventDeathDrops(), "GLOBAL")) {
	  		if (ConfigHandler.isPreventOBypass() && player.isOp() || ConfigHandler.isPreventCBypass() && PlayerHandler.isCreativeMode(player)) { }
	  		else {
	  			while (litr.hasNext()) {
	  				litr.next(); litr.remove();
				}
	  		}
		}
	}
	
	private void delayedSaftey(final Player player, final int integer) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			@Override
			public void run() {
				switch(integer) {
				  case 1:
					  if (isDropping.containsKey(PlayerHandler.getPlayerID(player))) {
						  isDropping.remove(PlayerHandler.getPlayerID(player));
					  }
				    break;
				  case 2:
					  if (possibleDropping.containsKey(PlayerHandler.getPlayerID(player))) {
						  possibleDropping.remove(PlayerHandler.getPlayerID(player));
					  }
				    break;
				}
			}
		}, 1L);
	}
}