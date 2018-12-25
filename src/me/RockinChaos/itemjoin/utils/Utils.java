package me.RockinChaos.itemjoin.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import de.domedd.betternick.api.nickedplayer.NickedPlayer;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.clip.placeholderapi.PlaceholderAPI;

public class Utils {
	
	public static boolean containsIgnoreCase(String string1, String string2) {
		if (string1 != null && string2 != null && string1.toLowerCase().contains(string2.toLowerCase())) {
			return true;
		}
		return false;
	}
	
	public static String convertStringList(List<String> list) {
	    String res = "";
	    for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
	        res += iterator.next() + (iterator.hasNext() ? ", " : "");
	    }
	    return res;
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
	
	public static Integer returnInteger(String text) {
		if (text == null) { return null; }
		else {
			char[] characters = text.toCharArray();
			Integer value = null;
			boolean isPrevDigit = false;
			for (int i = 0; i < characters.length; i++) {
				if (isPrevDigit == false) {
					if (Character.isDigit(characters[i])) {
						isPrevDigit = true;
						value = Character.getNumericValue(characters[i]);
					}
				} else { if (Character.isDigit(characters[i])) { value = (value * 10) + Character.getNumericValue(characters[i]); } else { break; } }
			}
			return value;
		}
	}
	
	public static String colorFormat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s).toString();
	}
	
	public static String translateLayout(String name, Player player) {
		String playerName = "EXEMPT";
		
		if (player != null && Hooks.hasBetterNick()) {
			NickedPlayer np = new NickedPlayer(player);
			if (np.isNicked()) {
			playerName = np.getRealName();
			} else { playerName = player.getName(); }
		} else if (player != null) { playerName = player.getName(); }
		if (playerName != null && player != null && !(player instanceof ConsoleCommandSender)) {
		try { name = name.replace("%player%", playerName); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		try { name = name.replace("%mob_kills%", String.valueOf(player.getStatistic(Statistic.MOB_KILLS))); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		try { name = name.replace("%player_kills%", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS))); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		try { name = name.replace("%player_deaths%", String.valueOf(player.getStatistic(Statistic.DEATHS))); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		try { name = name.replace("%player_food%", String.valueOf(player.getFoodLevel())); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		try { name = name.replace("%player_health%", String.valueOf(player.getHealth())); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		try { name = name.replace("%player_location%", player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + ""); } catch (Exception e) { ServerHandler.sendDebugTrace(e); } }
		if (playerName != null && player != null && (player instanceof ConsoleCommandSender)) { try { name = name.replace("%player%", "CONSOLE"); } catch (Exception e) { ServerHandler.sendDebugTrace(e); } }
	
		name = ChatColor.translateAlternateColorCodes('&', name).toString();
		if (Hooks.hasPlaceholderAPI() == true) {
			try { return PlaceholderAPI.setPlaceholders(player, name); } 
			catch (NoSuchFieldError e) { ServerHandler.sendDebugMessage("Error has occured when setting the PlaceHolder " + e.getMessage() + ", if this issue persits contact the developer of PlaceholderAPI."); return name; }
		}
		return name;
	}
}