package me.RockinChaos.itemjoin.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
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
	
	public static int getWindowID(Player player) {
		 try {
			 Field entityPlayerActiveContainerWindowId = getNMS("EntityPlayer").getField("activeContainer").getType().getField("windowId");
			 Method craftPlayerHandle = getOBC("entity.CraftPlayer").getDeclaredMethod("getHandle");
			 Field entityPlayerActiveContainer = getNMS("EntityPlayer").getField("activeContainer");
			 return (int) entityPlayerActiveContainerWindowId.get(entityPlayerActiveContainer.get(craftPlayerHandle.invoke(player)));
		  } catch (Exception e) { if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
	    }
		 return -2;
	 }
	
}
