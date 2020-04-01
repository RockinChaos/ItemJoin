package me.RockinChaos.itemjoin.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.domedd.betternick.BetterNick;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.clip.placeholderapi.PlaceholderAPI;

public class Utils {
	
	public static boolean containsIgnoreCase(String string1, String string2) {
		if (string1 != null && string2 != null && string1.toLowerCase().contains(string2.toLowerCase())) {
			return true;
		}
		return false;
	}
	
	public static boolean containsValue(List<?> list, String s) {
		boolean bool = false;
		for (Object l : list) { if (l.toString().equalsIgnoreCase(s)) { bool = true; break; } }
		return bool;
	}
	
	public static String convertStringList(List<String> list) {
	    String res = "";
	    for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
	        res += iterator.next() + (iterator.hasNext() ? ", " : "");
	    }
	    return res;
	}
	
	public static String[] softSplit(String str) {
		if (str.split(", ").length < 3) { return str.split("` "); }
		String splitTest = ""; int index = 1;
	    for (String sd : str.split(", ")) { if (index == 3) { splitTest += sd + "` "; index = 1; } else { splitTest += sd + ", "; index++; } }
	    if (splitTest.endsWith(", ")) { splitTest = splitTest.substring(0, splitTest.length() - 2); }
	    return splitTest.split("` ");
	}
	
	public static List<String> split(String s) {
		List<String> splitList = new ArrayList<String>();
		for (String split : s.split(", ")) {
			splitList.add(split);
		}
		return splitList;
	}
	
	public static int getPath(final int i) {
		if (ConfigHandler.getConfig("items.yml").getString("items.item_" + i) != null) {
			return getPath(i + 1);
		}
		return i;
	}
	
	public static String nullCheck(String input) {
		if (input == null || input.equalsIgnoreCase("NULL") || input.contains("[]") || input.contains("{}") || input.equals("0&7") || input.equals("-1&a%") || input.equals("") || input.equals(" ")) {
			return "NONE";
		}
		if (input.startsWith("[") && input.endsWith("]")) {
			input = input.substring(0, input.length() - 1).substring(1);
		}
		if (input.startsWith("{") && input.endsWith("}")) {
			input = input.replace("{", "").replace("}", "").replace("=", ":");
		}
		return input;
	}
	
	public static String encrypt(String text) {
		try {
			return Base64.getEncoder().encodeToString(text.getBytes());
		} catch (Exception e) {
			ServerHandler.logDebug("{Utils} Failure to encrypt sensitive text!");
			ServerHandler.sendDebugTrace(e);
		}
		return null;
	}
	
	public static String decrypt(String text) {
		try {
			return new String(Base64.getDecoder().decode(text));
		} catch (Exception e) {
			ServerHandler.logDebug("{Utils} Failure to decrypt sensitive text!");
			ServerHandler.sendDebugTrace(e);
		}
		return null;
	}
	
	public static int getRandom(int lower, int upper) {
		Random random = new Random();
		return random.nextInt((upper - lower) + 1) + lower;
	}
	
	public static Color getColorFromHexColor(String hexString) {
		int hex = Integer.decode("#" + hexString.replace("#", ""));
		int r = ((hex & 0xFF0000) >> 16);
		int g = ((hex & 0xFF00) >> 8);
		int b = (hex & 0xFF);
		Color bukkitColor = Color.fromBGR(r, g, b);
		return bukkitColor;
	}
	
	public static Entry<?, ?> randomEntry(HashMap<?, ?> map) {
		try {
			Field table = HashMap.class.getDeclaredField("table");
			table.setAccessible(true);
			Random rand = new Random();
			Entry<?, ?>[] entries = (Entry[]) table.get(map);
			int start = rand.nextInt(entries.length);
	    	for(int i=0;i<entries.length;i++) {
	    		int idx = (start + i) % entries.length;
	    		Entry<?, ?> entry = entries[idx];
	       		if (entry != null) return entry;
	    	}
		} catch (Exception e) {}
	    return null;
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
	
	public static int getSlotConversion(String str) {
		if (str.equalsIgnoreCase("CRAFTING[0]") || str.equalsIgnoreCase("C[0]") || str.equalsIgnoreCase("C(0)")) {
			return 0;
		} else if (str.equalsIgnoreCase("CRAFTING[1]") || str.equalsIgnoreCase("C[1]") || str.equalsIgnoreCase("C(1)")) {
			return 1;
		} else if (str.equalsIgnoreCase("CRAFTING[2]") || str.equalsIgnoreCase("C[2]") || str.equalsIgnoreCase("C(2)")) {
			return 2;
		} else if (str.equalsIgnoreCase("CRAFTING[3]") || str.equalsIgnoreCase("C[3]") || str.equalsIgnoreCase("C(3)")) {
			return 3;
		} else if (str.equalsIgnoreCase("CRAFTING[4]") || str.equalsIgnoreCase("C[4]") || str.equalsIgnoreCase("C(4)")) {
			return 4;
		}
		return -1;
	}
	
	public static String getArmorSlot(String slot, boolean integer) {
		if (!integer) {
			if (slot.equalsIgnoreCase("39")) { return "HELMET"; }
			else if (slot.equalsIgnoreCase("38")) { return "CHESTPLATE"; }
			else if (slot.equalsIgnoreCase("37")) { return "LEGGINGS"; }
			else if (slot.equalsIgnoreCase("36")) { return "BOOTS"; }
			return slot;
		} else {
			if (slot.equalsIgnoreCase("HELMET") || slot.equalsIgnoreCase("HEAD")) { return "39"; }
			else if (slot.equalsIgnoreCase("CHESTPLATE")) { return "38"; }
			else if (slot.equalsIgnoreCase("LEGGINGS")) { return "37"; }
			else if (slot.equalsIgnoreCase("BOOTS")) { return "36"; }
			return slot;
		}
	}
	
	public static void triggerCommands(Player player) {
		if ((ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds") != null && ConfigHandler.getConfig("config.yml").getStringList("Active-Commands.commands") != null) 
				&& (!ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("DISABLED") || !ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("FALSE"))) {
			String commandsWorlds = ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds").replace(" ", "");
			if (commandsWorlds == null) { commandsWorlds = "DISABLED"; }
			String[] compareWorlds = commandsWorlds.split(",");
			for (String compareWorld: compareWorlds) {
				if (compareWorld.equalsIgnoreCase(player.getWorld().getName()) || compareWorld.equalsIgnoreCase("ALL") || compareWorld.equalsIgnoreCase("GLOBAL")) {
					for (String commands: ConfigHandler.getConfig("config.yml").getStringList("Active-Commands.commands")) {
						String formatCommand = Utils.translateLayout(commands, player).replace("first-join: ", "").replace("first-join:", "");
						if (!ConfigHandler.getSQLData().hasFirstCommanded(player, formatCommand)) {
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), formatCommand);
							if (Utils.containsIgnoreCase(commands, "first-join:")) {
								ConfigHandler.getSQLData().saveFirstCommandData(player, formatCommand);
							}
						}
					}
				}
				break;
			}
		}
	}
	
    public static String getNearbyPlayer(Player player, int range) {
	    ArrayList < Location > sight = new ArrayList < Location > ();
	    ArrayList < Entity > entities = (ArrayList < Entity > ) player.getNearbyEntities(range, range, range);
	    Location origin = player.getEyeLocation();
	    sight.add(origin.clone().add(origin.getDirection()));
	    sight.add(origin.clone().add(origin.getDirection().multiply(range)));
	    sight.add(origin.clone().add(origin.getDirection().multiply(range + 3)));
	   	for (int i = 0; i < sight.size(); i++) {
	    	for (int k = 0; k < entities.size(); k++) {
	    		if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3) {
	    			if (Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5) {
	    				if (Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3) {
	    					if (entities.get(k) instanceof Player) {
	    						if (ServerHandler.hasSpecificUpdate("1_8")) {
	    							return entities.get(k).getName();
	    						} else {
	    							return ((Player) entities.get(k)).getName();
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}
	    }
    	return "INVALID";
    }
	
	public static String colorFormat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s).toString();
	}
	
	public static String translateLayout(String name, Player player, String...placeHolder) {
		String playerName = "EXEMPT";
		
		if (player != null && ConfigHandler.getDepends().nickEnabled()) {
			try {
				de.domedd.betternick.api.nickedplayer.NickedPlayer np = new de.domedd.betternick.api.nickedplayer.NickedPlayer(player);
				if (np.isNicked()) {
					playerName = np.getRealName();
				} else { playerName = player.getName(); }
			} catch (NoClassDefFoundError e) {
				try {
					if (BetterNick.getApi().isPlayerNicked(player)) {
						playerName = BetterNick.getApi().getRealName(player);
					} else { playerName = player.getName(); }	
				} catch (NullPointerException e2) { playerName = player.getName(); }
			}
		} else if (player != null) { playerName = player.getName(); }
		
		if (playerName != null && player != null && !(player instanceof ConsoleCommandSender)) {
			try { name = name.replace("%player%", playerName); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
			try { name = name.replace("%mob_kills%", String.valueOf(player.getStatistic(Statistic.MOB_KILLS))); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
			try { name = name.replace("%player_kills%", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS))); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
			try { name = name.replace("%player_deaths%", String.valueOf(player.getStatistic(Statistic.DEATHS))); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
			try { name = name.replace("%player_food%", String.valueOf(player.getFoodLevel())); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
			try { name = name.replace("%player_health%", String.valueOf(player.getHealth())); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
			try { name = name.replace("%player_location%", player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + ""); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
			try { name = name.replace("%player_interact%", getNearbyPlayer(player, 3)); } catch (Exception e) { ServerHandler.sendDebugTrace(e); } }
		if (player == null) { try { name = name.replace("%player%", "CONSOLE"); } catch (Exception e) { ServerHandler.sendDebugTrace(e); } }
	
		name = ChatColor.translateAlternateColorCodes('&', name).toString();
		if (ConfigHandler.getDepends().placeHolderEnabled()) {
			try { try { return PlaceholderAPI.setPlaceholders(player, name); } 
			catch (NoSuchFieldError e) { ServerHandler.logWarn("An error has occured when setting the PlaceHolder " + e.getMessage() + ", if this issue persits contact the developer of PlaceholderAPI."); return name; }
			} catch (Exception e) { }
		}
		return name;
	}
}