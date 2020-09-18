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
package me.RockinChaos.itemjoin.listeners.triggers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class PlayerLogin implements Listener {
	private static boolean startComplete = false;
	
   /**
	* Called on player login.
	* Prevents the player from logging in before the plugin is finished loading.
	* 
	* @param event - PlayerLoginEvent
	*/
	@EventHandler(priority = EventPriority.LOWEST)
	private void setLoginItems(PlayerLoginEvent event) {
		ItemHandler.getItem().preLoad(event.getPlayer());
		if (!startComplete) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Utils.getUtils().colorFormat("&7&l[&e&lItemJoin&l&7]\n&cYou cannot join before the server has started!\n&cPlugin has not finished loading, Please try again in a few minutes."));
		}
	}
	
   /**
	* Sets the plugin as fully loaded.
	* 
	*/
	public static void startComplete() {
		startComplete = true;
	}
}