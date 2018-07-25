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
	
	

	public static void SwitchServers(Player player, String server) {
        Messenger messenger = ItemJoin.getInstance().getServer().getMessenger();
        if (!messenger.isOutgoingChannelRegistered(ItemJoin.getInstance(), "BungeeCord")) {
            messenger.registerOutgoingPluginChannel(ItemJoin.getInstance(), "BungeeCord");
        }
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (Exception e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		player.sendPluginMessage(ItemJoin.getInstance(), "BungeeCord", out.toByteArray());
	}
	
	public static void ExecuteCommand(Player player, String cmd) {
        Messenger messenger = ItemJoin.getInstance().getServer().getMessenger();
        if (!messenger.isOutgoingChannelRegistered(ItemJoin.getInstance(), "BungeeCord")) {
            messenger.registerOutgoingPluginChannel(ItemJoin.getInstance(), "BungeeCord");
        }
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		try {
			out.writeUTF("Subchannel");
			out.writeUTF("Argument");
			out.writeUTF(cmd);
		} catch (Exception e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		player.sendPluginMessage(ItemJoin.getInstance(), "BungeeCord", out.toByteArray());
	}
	
	

	  
	  @Override
	  public void onPluginMessageReceived(String channel, Player player, byte[] message) {
	    if (!channel.equals("BungeeCord")) {
	      return;
	    }
	    ByteArrayDataInput in = ByteStreams.newDataInput(message);
	    String subchannel = in.readUTF();
	    if (!subchannel.contains("PlayerCount")) {
	    player.sendMessage(subchannel + " " + in.readByte());
	    }
	  }
}
