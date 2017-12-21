package me.RockinChaos.itemjoin.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import de.domedd.betternick.api.nickedplayer.NickedPlayer;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.clip.placeholderapi.PlaceholderAPI;

public class Utils {

	public static String format(String name, Player player) {
		String playerName = "ItemJoin";
		
		if (player != null && Hooks.hasBetterNick()) {
			NickedPlayer np = new NickedPlayer(player);
			if (np.isNicked()) {
			playerName = np.getRealName();
			} else {
				playerName = player.getName();
			}
		} else if (player != null) {
			playerName = player.getName();
		}
		
		name = name.replace("%player%", playerName);
		name = ChatColor.translateAlternateColorCodes('&', name).toString();
		if (Hooks.hasPlaceholderAPI() == true) {
			try {
			return PlaceholderAPI.setPlaceholders(player, name);
			} catch (NullPointerException e) {
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			}
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
		} catch (NumberFormatException e) { return false; }
		return true;
	}
	
	public static Integer returnInteger(String s) {
		if (s == null) return null;
		else {
			char[] characters = s.toCharArray();
			Integer value = null;
			boolean isPrevDigit = false;
			for (int i = 0; i < characters.length; i++) {
				if (isPrevDigit == false) {
					if (Character.isDigit(characters[i])) {
						isPrevDigit = true;
						value = Character.getNumericValue(characters[i]);
					}
				} else {
					if (Character.isDigit(characters[i])) {
						value = (value * 10) + Character.getNumericValue(characters[i]);
					} else {
						break;
					}
				}
			}
			return value;
		}
	}
	
	public static String convertStringList(List<String> list) {
	    String res = "";
	    for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
	        res += iterator.next() + (iterator.hasNext() ? ", " : "");
	    }
	    return res;
	}

	public static boolean isCustomSlot(String slot) {
		if (slot.equalsIgnoreCase("Offhand") || slot.equalsIgnoreCase("Arbitrary") || slot.equalsIgnoreCase("Helmet") 
				|| slot.equalsIgnoreCase("Chestplate") || slot.equalsIgnoreCase("Leggings") || slot.equalsIgnoreCase("Boots")) {
			return true;
		}
		return false;
	}

	public static Boolean isConfigurable() {
		if (ConfigHandler.getConfigurationSection() != null) {
			return true;
		} else if (ConfigHandler.getConfigurationSection() == null) {
			ServerHandler.sendConsoleMessage("&4There are no items detected in the items.yml.");
			ServerHandler.sendConsoleMessage("&4Try adding an item to the items section in the items.yml.");
			ServerHandler.sendConsoleMessage("&eIf you continue to see this message contact the plugin developer!");
			return false;
		}
		return false;
	}

	public static boolean canBypass(Player player, String ItemFlags) {
		boolean Creative = player.getGameMode() == GameMode.CREATIVE;
		if (ItemHandler.containsIgnoreCase(ItemFlags, "AllowOPBypass") && player.isOp() 
				|| ItemHandler.containsIgnoreCase(ItemFlags, "CreativeByPass") && Creative) {
			return true;
		}
		return false;
	}
}
