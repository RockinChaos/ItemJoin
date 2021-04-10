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
import me.RockinChaos.itemjoin.handlers.events.InventoryCloseEvent;
import me.RockinChaos.itemjoin.handlers.events.PlayerAutoCraftEvent;
import me.RockinChaos.itemjoin.handlers.events.PlayerPickItemEvent;

public class ProtocolManager {
	
	private static TinyProtocol protocol;
	
   /**
    * Handles both server side and client side protocol packets.
    * 
    */
  	public static void handleProtocols() {
  		if (protocol != null) { closeProtocol(); }
  		protocol = new TinyProtocol(ItemJoin.getInstance()) {
  			
  		   /**
  		    * Handles all incomming client packets.
  		    * 
            * @param player - the player tied to the packet.
            * @param channel - the channel the packet was called on.
            * @param packet - the packet object.
  		    */
  			@Override
  			public Object onPacketInAsync(final Player player, final Channel channel, final Object packet) {
  				String packetName = (packet != null ? packet.getClass().getSimpleName() : null);
	  			if (manageEvents(player, packetName)) { 
	  				return null; 
	  			}
  				return super.onPacketInAsync(player, channel, packet);
  			}
  		
  		   /**
  		    * Handles all outgoing server packets.
  		    * 
            * @param player - the player tied to the packet.
            * @param channel - the channel the packet was called on.
            * @param packet - the packet object.
  		    */
  			@Override
  			public Object onPacketOutAsync(final Player player, final Channel channel, final Object packet) {
  				return packet;
  			}
  		};
  	}
  	
   /**
    * Handles the custom plugin events corresponding to their packet names.
    * 
    * @param player - the player tied to the packet.
    * @param channel - the channel the packet was called on.
    * @param packet - the packet object.
    */
  	public static boolean manageEvents(final Player player, final String packetName) {
  		try {
  			if (packetName != null) {
	  			if (packetName.equalsIgnoreCase("PacketPlayInPickItem")) {
		  			PlayerPickItemEvent PickItem = new PlayerPickItemEvent(player, player.getInventory());
		  			callEvent(PickItem);
				  	return PickItem.isCancelled();
		  		} else if (packetName.equalsIgnoreCase("PacketPlayInAutoRecipe")) {
		  			PlayerAutoCraftEvent AutoCraft = new PlayerAutoCraftEvent(player, player.getOpenInventory().getTopInventory());
		  			callEvent(AutoCraft);
				  	return AutoCraft.isCancelled();
		  		} else if (packetName.equalsIgnoreCase("PacketPlayInCloseWindow")) {
		  			InventoryCloseEvent CloseInventory = new InventoryCloseEvent(player.getOpenInventory());
		  			callEvent(CloseInventory);
				  	return CloseInventory.isCancelled();
		  		}
  			}
  		} catch (Exception e) { }
  		return false;
  	}
  	
   /**
    * Allows an event to be called on a different Async Thread.
    * Functions the same as PluginManager.callEvent(event);
    * 
    * @param event - The event to be triggered.
    */
    private static void callEvent(final Event event) {
    	HandlerList handlers = event.getHandlers();
    	RegisteredListener[] listeners = handlers.getRegisteredListeners();
    	for (RegisteredListener registration: listeners) {
    		if (!registration.getPlugin().isEnabled()) { continue; }
    		try {
    			if (event != null && registration != null && registration.getPlugin() != null) { 
    				registration.callEvent(event);
    			}
    		} catch (AuthorNagException e) {
    			Plugin plugin = registration.getPlugin();
    			if (plugin.isNaggable()) {
    				plugin.setNaggable(false);
    				ItemJoin.getInstance().getLogger().log(Level.SEVERE, String.format("Nag author(s): '%s' of '%s' about the following: %s", plugin.getDescription().getAuthors(), plugin.getDescription().getFullName(), e.getMessage()));
    			}
    		} catch (Throwable e) {
    			ItemJoin.getInstance().getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getFullName(), e);
    		}
    	}
    }
    
   /**
    * Closes the currently open protocol handler(s).
    * 
    */
  	public static void closeProtocol() {
  		if (protocol != null) {
  			protocol.close();
  		}
  	}
  	
   /**
    * Checks if the protocol handler(s) are open.
    * 
    * @return If the protocol handler(s) are open.
    */
  	public static boolean isHandling() {
  		return (protocol != null);
  	}
}