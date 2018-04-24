package me.RockinChaos.itemjoin.cacheitems;

import java.lang.reflect.Method;

import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Reflection;

import org.bukkit.inventory.ItemStack;

public class Unbreakable {

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
