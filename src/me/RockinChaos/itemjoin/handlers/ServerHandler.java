package me.RockinChaos.itemjoin.handlers;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.RockinChaos.itemjoin.ItemJoin;

public class ServerHandler {
	
	public static boolean hasCombatUpdate() {
		String pkgname = ItemJoin.getInstance().getServer().getClass().getPackage().getName();
		String combatVersion = "v1_9_R0".replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		String version = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		if (Integer.parseInt(version) >= Integer.parseInt(combatVersion)) {
			return true;
		}
		return false;
	}
	
	public static boolean hasAquaticUpdate() {
		String pkgname = ItemJoin.getInstance().getServer().getClass().getPackage().getName();
		String combatVersion = "v1_13_R0".replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		String version = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		if (Integer.parseInt(version) >= Integer.parseInt(combatVersion)) {
			return true;
		}
		return false;
	}
	
	public static boolean hasSpecificUpdate(String versionString) {
		String pkgname = ItemJoin.getInstance().getServer().getClass().getPackage().getName();
		String localeVersion = "v" + versionString + "_R0";
	    localeVersion = localeVersion.replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		String version = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		if (Integer.parseInt(version) >= Integer.parseInt(localeVersion)) {
			return true;
		}
		return false;
	}

	public static void sendConsoleMessage(String message) {
		String prefix = "&7[&eItemJoin&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		if (!ConfigHandler.isLogColor()) {
			message = ChatColor.stripColor(message);
		}
		if (message.equalsIgnoreCase("") || message.isEmpty()) {
			message = "";
		}
		ItemJoin.getInstance().getServer().getConsoleSender().sendMessage(message);
	}
	
	public static void sendErrorMessage(String message) {
		String prefix = "&e[&4ItemJoin_ERROR&e]&c ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		if (!ConfigHandler.isLogColor()) {
			message = ChatColor.stripColor(message);
		}
		if (message.equalsIgnoreCase("") || message.isEmpty()) {
			message = "";
		}
		ItemJoin.getInstance().getServer().getConsoleSender().sendMessage(message);
	}
	
	public static void sendWarnMessage(String message) {
		String prefix = "&e[&cItemJoin_WARN&e]&c ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		if (!ConfigHandler.isLogColor()) {
			message = ChatColor.stripColor(message);
		}
		if (message.equalsIgnoreCase("") || message.isEmpty()) {
			message = "";
		}
		ItemJoin.getInstance().getServer().getConsoleSender().sendMessage(message);
	}
	
	public static void sendPlayerMessage(Player player, String message) {
		String prefix = "&7[&eItemJoin&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
			if (message.contains("blankmessage")) {
				message = "";
		}
		player.sendMessage(message);
	}
	
	public static void sendMessage(CommandSender sender, String message) {
		String prefix = "&7[&eItemJoin&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		if	(!ConfigHandler.isLogColor()) {
			message = ChatColor.stripColor(message);
		}
		sender.sendMessage(message);
	}

	public static void sendDebugMessage(String message) {
		if (ConfigHandler.isDebugging()) {
			String prefix = "[ItemJoin_DEBUG] &c";
			message = ChatColor.translateAlternateColorCodes('&', message).toString();
			message = ChatColor.stripColor(message);
			message = prefix + message;
			message = ChatColor.translateAlternateColorCodes('&', message).toString();
			ItemJoin.getInstance().getServer().getConsoleSender().sendMessage(message);
		}
	}
	
	public static void sendDebugTrace(Exception e) {
		if (ConfigHandler.isDebugging()) { e.printStackTrace(); }
	}

	public static void purgeCraftItems(boolean sqlCrafting) {
		Collection < ? > playersOnlineNew = null;
		Player[] playersOnlineOld;
		try {
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
					playersOnlineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
					for (Object objPlayer: playersOnlineNew) {
						Player player = ((Player) objPlayer);
						if (sqlCrafting) { ItemHandler.saveCraftItems(player); }
						ItemHandler.removeCraftItems(player);
					}
				}
			} else {
				playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Player player: playersOnlineOld) {
					if (sqlCrafting) { ItemHandler.saveCraftItems(player); }
					ItemHandler.removeCraftItems(player);
				}
			}
		} catch (Exception e) {
			ServerHandler.sendDebugTrace(e);
		}
	}
}
