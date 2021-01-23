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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class PlayerLogin implements Listener {

   /**
	* Called when the player attempts to connect to the server while it is starting.
	* Attempts to keep the player in loading limbo until the plugin is fully loaded.
	* 
	* @param event - AsyncPlayerPreLoginEvent
	*/
	@EventHandler(priority = EventPriority.LOWEST)
	private void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (ConfigHandler.getConfig(false).sqlEnabled() && !ItemJoin.getInstance().isStarted()) {
			ServerHandler.getServer().logDebug("Processing pre-login for " + (ServerHandler.getServer().hasSpecificUpdate("1_8") ? event.getUniqueId() : "Legacy") + " - " + event.getName());
			this.enableLatch();
			ServerHandler.getServer().logDebug("Accepted pre-login for " + (ServerHandler.getServer().hasSpecificUpdate("1_8") ? event.getUniqueId() : "Legacy") + " - " + event.getName());
		}
	}
	
   /**
	* Called when the player attempts to connect to the server before the plugin was even enabled.
	* Denies the player login attempt as they have managed to bypass the AsyncPlayerPreLoginEvent.
	* 
	* @param event - AsyncPlayerPreLoginEvent
	*/
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
    	final Player player = event.getPlayer();
    	if (ConfigHandler.getConfig(false).sqlEnabled() && !ItemJoin.getInstance().isStarted()) {
    		ServerHandler.getServer().logDebug("Denied login for " + (ServerHandler.getServer().hasSpecificUpdate("1_8") ? player.getUniqueId() : "Legacy") + " - " + player.getName() + ", server is still starting!");
    		event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Timed out");
    	}
    }
    
   /**
	* A latching method to prevent a thread from continuing until the plugin has fully loaded.
	* 
	*/
	private void enableLatch() {
		while (!ItemJoin.getInstance().isStarted()) { 
			try { 
				Thread.sleep(2000); 
			} catch (InterruptedException e) { } 
		}
	}
}