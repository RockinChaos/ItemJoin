package me.RockinChaos.itemjoin.utils;

import java.lang.reflect.Method;

import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

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
	
	private static Class<?> getMapNMS(String name) {
		try {
			return Class.forName("org.bukkit.map." + name);
		} catch (Exception e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return null;
	}
	
	public static short getMapID(MapView view) {
		try {
			Class<?> MapView = getMapNMS("MapView");
			Object mapID = MapView.getMethod("getId").invoke(view);
			return (short)mapID; 
		} catch (Exception ex) { return 1; }
	}
	
	public static ItemStack setUnbreakable(ItemStack item) {
		try {
			Class<?> craftItemStack = Reflection.getOBC("inventory.CraftItemStack");
			Method getNMSI = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
			Object nms = getNMSI.invoke(null, item);
			Object tag = Reflection.getNMS("NBTTagCompound").getConstructor().newInstance();
			tag.getClass().getMethod("setInt", String.class, int.class).invoke(tag, "Unbreakable", 1);
			nms.getClass().getMethod("setTag", tag.getClass()).invoke(nms, tag);
			item = (ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms);
		} catch (Exception e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return item;
	}
}