package me.RockinChaos.itemjoin.utils;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.clip.placeholderapi.PlaceholderAPI;

public class Utils {
	public static boolean hasVault;
	public static boolean hasMultiverse;
	public static boolean hasInventories;
	public static boolean hasPlaceholderAPI;
	public static boolean hasPerWorldPlugins;
	public static boolean hasPerWorldInventory;

	public static String format(String name, Player player) {
		String playerName = "ItemJoin";
		if (player != null) {
			playerName = player.getName();
		}
		name = name.replace("%player%", playerName);
		name = ChatColor.translateAlternateColorCodes('&', name).toString();
		if (hasPlaceholderAPI == true) {
			name = PlaceholderAPI.setPlaceholders(player, name);
		}
		return name;
	}

	public static int getRandom(int lower, int upper) {
		Random random = new Random();
		return random.nextInt((upper - lower) + 1) + lower;
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static boolean isCustomSlot(String slot) {
		Boolean isCustom = false;
		if (slot.equalsIgnoreCase("Offhand") || slot.equalsIgnoreCase("Arbitrary") || slot.equalsIgnoreCase("Helmet") 
				|| slot.equalsIgnoreCase("Chestplate") || slot.equalsIgnoreCase("Leggings") || slot.equalsIgnoreCase("Boots")) {
			isCustom = true;
		}
		return isCustom;
	}

	public static Boolean isConfigurable() {
		Boolean isConfigurable = false;
		if (ConfigHandler.getConfigurationSection() != null) {
			isConfigurable = true;
		} else if (ConfigHandler.getConfigurationSection() == null) {
			ServerHandler.sendConsoleMessage("&4There are no items detected in the items.yml.");
			ServerHandler.sendConsoleMessage("&4Try adding an item to the items section in the items.yml.");
			ServerHandler.sendConsoleMessage("&eIf you continue to see this message contact the plugin developer!");
			isConfigurable = false;
		}
		return isConfigurable;
	}

	public static boolean canBypass(Player player, String ItemFlags) {
		boolean canBypass = false;
		boolean Creative = player.getGameMode() == GameMode.CREATIVE;
		if (ItemHandler.containsIgnoreCase(ItemFlags, "AllowOPBypass") && player.isOp() 
				|| ItemHandler.containsIgnoreCase(ItemFlags, "CreativeByPass") && Creative) {
			canBypass = true;
		}
		return canBypass;
	}
}
