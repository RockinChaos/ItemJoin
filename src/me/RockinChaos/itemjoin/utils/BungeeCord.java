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
package me.RockinChaos.itemjoin.utils;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BungeeCord implements PluginMessageListener {
	
private static BungeeCord bungee;

   /**
    * Sends the specified Player to the specified Server.
    * 
    * @param player - The Player switching servers.
    * @param server - The String name of the server that the Player is connecting to.
    */
	public void SwitchServers(final Player player, final String server) {
		Messenger messenger = ItemJoin.getInstance().getServer().getMessenger();
		if (!messenger.isOutgoingChannelRegistered(ItemJoin.getInstance(), "BungeeCord")) {
			messenger.registerOutgoingPluginChannel(ItemJoin.getInstance(), "BungeeCord");
		}
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
		player.sendPluginMessage(ItemJoin.getInstance(), "BungeeCord", out.toByteArray());
	}
	
   /**
    * Executes the BungeeCord Command as the Player instance.
    * 
    * @param player - The Player executing the Bungee Command.
    * @param command - The Bungee Command the Player is executing.
    */
	public void ExecuteCommand(final Player player, final String command) {
		Messenger messenger = ItemJoin.getInstance().getServer().getMessenger();
		if (!messenger.isOutgoingChannelRegistered(ItemJoin.getInstance(), "BungeeCord")) {
			messenger.registerOutgoingPluginChannel(ItemJoin.getInstance(), "BungeeCord");
		}
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		try {
			out.writeUTF("Subchannel");
			out.writeUTF("Argument");
			out.writeUTF(command);
		} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
		player.sendPluginMessage(ItemJoin.getInstance(), "BungeeCord", out.toByteArray());
	}
	
   /**
    * Sends the Server Switch message when attempting to switch servers.
    * 
    * @param channel - The channel recieving the message.
    * @param player - The Player switching servers.
    * @param message - The message being sent to the Player..
    */
	@Override
	public void onPluginMessageReceived(final String channel, final Player player, final byte[] message) {
		if (!channel.equals("BungeeCord")) { return; }
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in .readUTF();
		if (!subchannel.contains("PlayerCount")) {
			player.sendMessage(subchannel + " " + in .readByte());
		}
	} 
	
   /**
    * Gets the instance of the BungeeCord.
    * 
    * @return The BungeeCord instance.
    */
    public static BungeeCord getBungee() { 
        if (bungee == null) {
        	bungee = new BungeeCord();
        }
        return bungee; 
    } 
}