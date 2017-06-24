package me.RockinChaos.itemjoin.handlers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.ItemJoin;

public class ServerHandler {
	public static ConsoleCommandSender Console = ItemJoin.pl.getServer().getConsoleSender();

	public static boolean hasExplorationUpdate() {
		boolean hasCombatUpdate = false;
		String pkgname = ItemJoin.pl.getServer().getClass().getPackage().getName();
		String combatVersion = "v1_11_R0".replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		String version = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		if (Integer.parseInt(version) >= Integer.parseInt(combatVersion)) {
			hasCombatUpdate = true;
		}
		return hasCombatUpdate;
	}
	
	public static boolean hasFrostburnUpdate() {
		boolean hasCombatUpdate = false;
		String pkgname = ItemJoin.pl.getServer().getClass().getPackage().getName();
		String combatVersion = "v1_10_R0".replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		String version = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		if (Integer.parseInt(version) >= Integer.parseInt(combatVersion)) {
			hasCombatUpdate = true;
		}
		return hasCombatUpdate;
	}
	
	public static boolean hasCombatUpdate() {
		boolean hasCombatUpdate = false;
		String pkgname = ItemJoin.pl.getServer().getClass().getPackage().getName();
		String combatVersion = "v1_9_R0".replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		String version = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		if (Integer.parseInt(version) >= Integer.parseInt(combatVersion)) {
			hasCombatUpdate = true;
		}
		return hasCombatUpdate;
	}

	public static boolean hasViableUpdate() {
		boolean hasViableUpdate = false;
		String pkgname = ItemJoin.pl.getServer().getClass().getPackage().getName();
		String combatVersion = "v1_7_R0".replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		String version = pkgname.substring(pkgname.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");
		if (Integer.parseInt(version) > Integer.parseInt(combatVersion)) {
			hasViableUpdate = true;
		}
		return hasViableUpdate;
	}

	public static void sendConsoleMessage(String message) {
		String prefix = "&7[&eItemJoin&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		if (ConfigHandler.loadConfig != null) {
			if(ConfigHandler.getConfig("config.yml").getBoolean("Log-Coloration") != true) {
			message = ChatColor.stripColor(message);
			}
		}
		if (ItemHandler.containsIgnoreCase(message, "blankmessage")) {
			message = "";
	}
		Console.sendMessage(message);
	}
	
	public static void sendCommandsMessage(CommandSender sender, String message) {
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		if (sender instanceof ConsoleCommandSender && ConfigHandler.getConfig("config.yml").getBoolean("Log-Coloration") != true) {
			message = ChatColor.stripColor(message);
		}
		if (ItemHandler.containsIgnoreCase(message, "blankmessage")) {
			message = "";
	}
		sender.sendMessage(message);
	}
	
	public static void sendPlayerMessage(Player player, String message) {
		String prefix = "&7[&eItemJoin&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
			if (ItemHandler.containsIgnoreCase(message, "blankmessage")) {
				message = "";
		}
		player.sendMessage(message);
	}

	public static void sendDebugMessage(String message) {
		String prefix = "[ITEMJOIN_DEBUG] &c";
		if (ConfigHandler.getConfig("config.yml").getBoolean("Debugging-Mode") == true) {
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		message = ChatColor.stripColor(message);
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		Console.sendMessage(message);
		}
	}
}
