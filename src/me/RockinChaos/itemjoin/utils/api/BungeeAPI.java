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
package me.RockinChaos.itemjoin.utils.api;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BungeeAPI implements PluginMessageListener {
	
	private final String PLUGIN_CHANNEL = "plugin:cloudsync";
	private boolean detectFailure = false;
	private static BungeeAPI bungee;

   /**
    * Initializes the BungeeCord Listener.
    *
    */
	public BungeeAPI() {
		final Messenger messenger = ItemJoin.getInstance().getServer().getMessenger();
		if (!messenger.isOutgoingChannelRegistered(ItemJoin.getInstance(), this.PLUGIN_CHANNEL)) {
			messenger.registerOutgoingPluginChannel(ItemJoin.getInstance(), this.PLUGIN_CHANNEL);
			messenger.registerOutgoingPluginChannel(ItemJoin.getInstance(), "BungeeCord");
		}
		if (!messenger.isIncomingChannelRegistered(ItemJoin.getInstance(), this.PLUGIN_CHANNEL)) {
			messenger.registerIncomingPluginChannel(ItemJoin.getInstance(), this.PLUGIN_CHANNEL, this);
			messenger.registerIncomingPluginChannel(ItemJoin.getInstance(), "BungeeCord", this);
		}
	}

   /**
    * Sends the specified Player to the specified Server.
    * 
    * @param player - The Player switching servers.
    * @param server - The String name of the server that the Player is connecting to.
    */
	public void SwitchServers(final Player player, final String server) {
		final ByteArrayDataOutput out = ByteStreams.newDataOutput();
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		player.sendPluginMessage(ItemJoin.getInstance(), "BungeeCord", out.toByteArray());
	}
	
   /**
    * Executes the BungeeCord Command as the Player instance.
    * 
    * @param player - The Player executing the Bungee Command.
    * @param command - The Bungee Command the Player is executing.
    */
	public void ExecuteCommand(final Player player, final String command) {
		if (StringUtils.containsIgnoreCase(player.getListeningPluginChannels().toString(), "plugin:cloudsync")) {
			final ByteArrayDataOutput out = ByteStreams.newDataOutput();
			try {
				out.writeUTF(player.getName());
				out.writeUTF(command);
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			player.sendPluginMessage(ItemJoin.getInstance(), this.PLUGIN_CHANNEL, out.toByteArray());
		} else {
			if (!this.detectFailure) {
				ServerUtils.logSevere("A custom item is set to execute the Bungee command /" + command + " but, CloudSync was not detected on your BungeeCord server.");
				this.detectFailure = true;
			}
		}
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
		if (!channel.equals(this.PLUGIN_CHANNEL)) { return; }
		final ByteArrayDataInput in = ByteStreams.newDataInput(message);
		final String subchannel = in.readUTF();
		if (subchannel.equals("ConnectOther") || subchannel.equals("Connect")) {
			player.sendMessage(subchannel + " " + in.readByte());
		}
	} 
	
   /**
    * Gets the instance of the ConfigHandler.
    * 
    * @return The ConfigHandler instance.
    */
    public static BungeeAPI getBungee(final boolean regen) { 
        if (bungee == null || regen) {
        	bungee = new BungeeAPI(); 
        }
        return bungee; 
    } 
}