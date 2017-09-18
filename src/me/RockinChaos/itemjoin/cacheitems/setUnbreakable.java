package me.RockinChaos.itemjoin.cacheitems;

import java.lang.reflect.Method;

import me.RockinChaos.itemjoin.ItemJoin;

import org.bukkit.inventory.ItemStack;

public class setUnbreakable {

	public static String getServerVersion() {
		return ItemJoin.getInstance().getServer().getClass().getPackage().getName().substring(23);
	}

	public static Class<?> getOBC(String name) {
		try {
			return Class.forName("org.bukkit.craftbukkit." + getServerVersion() + '.' + name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> getNMS(String name) {
		try {
			return Class.forName("net.minecraft.server." + getServerVersion() + '.' + name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ItemStack Unbreakable(ItemStack item) {
		try {
			Class<?> craftItemStack = getOBC("inventory.CraftItemStack");
			Method getNMSI = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
			Object nms = getNMSI.invoke(null, item);
			Object tag = getNMS("NBTTagCompound").getConstructor().newInstance();
			tag.getClass().getMethod("setInt", String.class, int.class).invoke(tag, "Unbreakable", 1);
			nms.getClass().getMethod("setTag", tag.getClass()).invoke(nms, tag);
			item = (ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms);
		} catch (Exception e) {}
		return item;
	}
}
