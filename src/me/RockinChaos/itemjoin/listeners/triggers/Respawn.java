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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.item.ItemUtilities.TriggerType;

public class Respawn implements Listener {
	
   /**
	* Called on player respawn.
	* Gives any available custom items upon respawning.
	* 
	* @param event - PlayerRespawnEvent
	*/
	@EventHandler(ignoreCancelled = true)
	private void setRespawnItems(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		if (PlayerHandler.getPlayer().isPlayer(player)) {
			ItemUtilities.getUtilities().setAuthenticating(player, player.getWorld(), TriggerType.RESPAWN, org.bukkit.GameMode.ADVENTURE, "GLOBAL"); 
		}
	}
}