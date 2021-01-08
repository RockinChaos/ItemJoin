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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.UI;
import me.RockinChaos.itemjoin.utils.interfaces.Interface;

public class Menu implements Listener {

  	private Interface expiredInventory;

   /**
    * Handles the click action for the virtualInventory.
    * 
    * @param event - InventoryClickEvent
    */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onClick(InventoryClickEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Interface) {
			((Interface) holder).onClick(event);
    		this.expiredInventory = ((Interface) holder);
		}
	}

   /**
	* Handles the chat action for the virtualInventory.
	* 
	* @param event - AsyncPlayerChatEvent
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onChat(AsyncPlayerChatEvent event) {
		if (this.expiredInventory != null && this.expiredInventory.chatPending()) {
			this.expiredInventory.onChat(event);
		}
	}
	
   /**
	* Handles the inventory close action for the virtualInventory.
	* 
	* @param event - InventoryCloseEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void onClose(InventoryCloseEvent event) {
		if (UI.getCreator() != null && UI.getCreator().modifyMenu((Player) event.getPlayer())) {
			if (ItemJoin.getInstance().isEnabled()) {
				Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(ItemJoin.getInstance(), () -> { 
					if (!UI.getCreator().isOpen((Player) event.getPlayer())) {
						UI.getCreator().setModifyMenu(false, (Player) event.getPlayer());
						for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
							if (itemMap.getAnimationHandler() != null && itemMap.getAnimationHandler().get(event.getPlayer()) != null) {
								itemMap.getAnimationHandler().get(event.getPlayer()).setMenu(false, 0);
							}
						}
					}
				}, 40L);
			}
		}
	}
}