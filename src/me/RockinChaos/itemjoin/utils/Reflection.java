package me.RockinChaos.itemjoin.utils;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class Reflection {

	
	public static String getServerVersion() {
		return ItemJoin.getInstance().getServer().getClass().getPackage().getName().substring(23);
	}

	public static Class<?> getOBC(String name) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + '.' + name);
		} catch (Exception e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return null;
	}

	public static Class<?> getNMS(String name) {
		try {
			return Class.forName("net.minecraft.server." + getServerVersion() + '.' + name);
		} catch (Exception e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return null;
	}
	
}
