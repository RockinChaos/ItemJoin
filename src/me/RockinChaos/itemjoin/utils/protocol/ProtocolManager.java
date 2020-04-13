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
package me.RockinChaos.itemjoin.utils.protocol;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import io.netty.channel.Channel;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.events.PlayerAutoCraftEvent;

public class ProtocolManager {
	private TinyProtocol protocol;
	
  	public void handleProtocols() {
  		if (this.protocol != null) { this.closeProtocol(); }
  		this.protocol = new TinyProtocol(ItemJoin.getInstance()) {
  			@Override
  			public Object onPacketInAsync(Player player, Channel channel, Object packet) {
  				try {
	  				if (manageEvents(player, channel, packet)) { 
	  					return null; 
	  				}
  				} catch (Exception e) { }
  				return super.onPacketInAsync(player, channel, packet);
  			}
  			
  			@Override
  			public Object onPacketOutAsync(Player player, Channel channel, Object packet) {
  				return packet;
  			}
  		};
  	}
  	
  	private boolean manageEvents(Player player, Channel channel, Object packet) {
  		try {
	  		if (packet != null && packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInAutoRecipe")) {
	  			PlayerAutoCraftEvent AutoCraft = new PlayerAutoCraftEvent(player, player.getOpenInventory().getTopInventory());
	  			this.callEvent(AutoCraft);
			  	return AutoCraft.isCancelled();
	  		}
  		} catch (Exception e) { }
  		return false;
  	}
  	
    private void callEvent(Event event) {
        HandlerList handlers = event.getHandlers();
        RegisteredListener[] listeners = handlers.getRegisteredListeners();
        for (RegisteredListener registration : listeners) {
            if (!registration.getPlugin().isEnabled()) { continue; }
            try {
                registration.callEvent(event);
            } catch (AuthorNagException e) {
                Plugin plugin = registration.getPlugin();
                if (plugin.isNaggable()) {
                    plugin.setNaggable(false);
                    ItemJoin.getInstance().getLogger().log(Level.SEVERE, String.format(
                    		"Nag author(s): '%s' of '%s' about the following: %s",
                            plugin.getDescription().getAuthors(), plugin.getDescription().getFullName(), e.getMessage()));
                }
            } catch (Throwable e) {
            	 ItemJoin.getInstance().getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getFullName(), e);
            }
        }
    }
    
  	public void closeProtocol() {
  		if (this.protocol != null) {
  			this.protocol.close();
  		}
  	}
}