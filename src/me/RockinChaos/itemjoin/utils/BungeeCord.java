package me.RockinChaos.itemjoin.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.RockinChaos.itemjoin.ItemJoin;

import org.bukkit.entity.Player;

public class BungeeCord {

	public static void SwitchServers(Player player, String server) {
		ItemJoin.pl.getServer().getMessenger().registerOutgoingPluginChannel(ItemJoin.pl, "BungeeCord");
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (IOException ex) {}
		player.sendPluginMessage(ItemJoin.pl, "BungeeCord", b.toByteArray());
	}
}
