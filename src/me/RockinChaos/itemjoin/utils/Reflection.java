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
			ServerHandler.sendDebugTrace(e);
		}
		return null;
	}

	public static Class<?> getNMS(String name) {
		try {
			return Class.forName("net.minecraft.server." + getServerVersion() + '.' + name);
		} catch (Exception e) {
			ServerHandler.sendDebugTrace(e);
		}
		return null;
	}
	
	public static Class<?> getEventClass(String name) {
	    try {
	    return Class.forName("org.bukkit.event." + name);
		} catch (ClassNotFoundException e) {
			ServerHandler.sendDebugMessage(name + " Does not exist in this version of Minecraft!");
			ServerHandler.sendDebugTrace(e);
		}
		return null;
	}
	
	private static Class<?> getMapNMS(String name) {
		try {
			return Class.forName("org.bukkit.map." + name);
		} catch (Exception e) {
			ServerHandler.sendDebugTrace(e);
		}
		return null;
	}
	
	private static Class<?> getBukkitNMS(String name) {
		try {
			return Class.forName("org.bukkit." + name);
		} catch (Exception e) {
			ServerHandler.sendDebugTrace(e);
		}
		return null;
	}
	
	public static short getMapID(MapView view) {
		try {
			Object mapID = getMapNMS("MapView").getMethod("getId").invoke(view); // check this
			return (short)mapID; 
		} catch (Exception ex) { return 1; }
	}

	public static MapView getMapView(int id) {
		try {
			Object mapView = getBukkitNMS("Bukkit").getMethod("getMap", short.class).invoke(getMapNMS("MapView"), (short)id);
			return (MapView)mapView; 
		} catch (Exception ex) { return null; }
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
		} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		return item;
	}
}