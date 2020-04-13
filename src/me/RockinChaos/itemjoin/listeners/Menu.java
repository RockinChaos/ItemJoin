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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.utils.interfaces.Interface;

public class Menu implements Listener {
  	
//  ============================================== //
//          Handlers for virtualInventory          //
//  ============================================== //
  	private Interface expiredInventory;

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Interface) {
			((Interface) holder).onClick(event);
    		this.expiredInventory = ((Interface) holder);
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (this.expiredInventory != null && this.expiredInventory.chatPending()) {
			this.expiredInventory.onChat(event);
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (ConfigHandler.getItemCreator() != null && ConfigHandler.getItemCreator().modifyMenu((Player)event.getPlayer())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!ConfigHandler.getItemCreator().isOpen((Player)event.getPlayer())) {
						ConfigHandler.getItemCreator().setModifyMenu(false, (Player)event.getPlayer());
						for (ItemMap itemMap : ItemUtilities.getItems()) {
							if (itemMap.getAnimationHandler() != null && itemMap.getAnimationHandler().get(event.getPlayer()) != null) {
								itemMap.getAnimationHandler().get(event.getPlayer()).setMenu(false, 0);
							}
						}
					}
				}
			}.runTaskLater(ItemJoin.getInstance(), 40L);
		}
	}
//  ===============================================================================

}