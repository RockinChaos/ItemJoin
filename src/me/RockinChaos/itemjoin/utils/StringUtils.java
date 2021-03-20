/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Statistic;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import de.domedd.betternick.BetterNick;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.api.DependAPI;
import me.clip.placeholderapi.PlaceholderAPI;

public class StringUtils {

    private static StringUtils util;
	
   /**
    * Checks if the condition is met.
    * 
    * @param condition - The condition to be compared against the value.
    * @param operand - The operation to be performed when comparing the condition to the value.
    * @param value - The value being compared against the condition.
    * @return If the condition has operand against value.
    */
	public boolean conditionMet(final String condition, final String operand, final String value) {
		if (operand == null) { return false; }
		if (operand.equalsIgnoreCase("EQUAL") && ((condition != null && value != null && condition.equalsIgnoreCase(value)) || (condition == null && value == null))) {
			return true;
		} else if (operand.equalsIgnoreCase("NOTEQUAL") && ((condition != null && value != null && !condition.equalsIgnoreCase(value)) || ((condition == null && value != null) || (condition != null && value == null)))) {
			return true;
		} else if (operand.equalsIgnoreCase("OVER") && condition != null && value != null && this.isInt(condition) && this.isInt(value) && Integer.parseInt(condition) > Integer.parseInt(value)) {
			return true;
		} else if (operand.equalsIgnoreCase("UNDER") && condition != null && value != null && this.isInt(condition) && this.isInt(value) && Integer.parseInt(condition) < Integer.parseInt(value)) {
			return true;
		}
		return false;
	}
    
   /**
    * Checks if string1 contains string2.
    * 
    * @param string1 - The String to be checked if it contains string2.
    * @param string2 - The String that should be inside string1.
    * @return If string1 contains string2.
    */
	public boolean containsIgnoreCase(final String string1, final String string2) {
		if (string1 != null && string2 != null && string1.toLowerCase().contains(string2.toLowerCase())) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if string1 contains string2.
    * 
    * @param string1 - The String to be checked if it contains string2.
    * @param string2 - The String that should be inside string1.
    * @param argument - The argument to be split between the string.
    * @return If string1 contains string2.
    */
	public boolean splitIgnoreCase(final String string1, final String string2, final String argument) {
		String[] parts = string1.split(argument);
		boolean splitParts = string1.contains(argument);
		for (int i = 0; i < (splitParts ? parts.length : 1); i++) {
			if ((splitParts && parts[i] != null && string2 != null && parts[i].toLowerCase().replace(" ", "").equalsIgnoreCase(string2.replace(" ", "").toLowerCase()))
			|| (!splitParts && string1 != null && string2 != null && string1.toLowerCase().equalsIgnoreCase(string2.toLowerCase()))) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Checks if the List contains the String.
    * 
    * @param list - The List to be checked if it contains the String.
    * @param str - The String that should be inside the List.
    * @return If the List contained the String.
    */
	public boolean containsValue(final List<?> list, final String str) {
		boolean bool = false;
		for (Object l : list) { if (l.toString().equalsIgnoreCase(str)) { bool = true; break; } }
		return bool;
	}
	
   /**
    * Splits the String with proper formatting and ordering.
    * 
    * @param str - The String to be Split.
    * @return The newly formatted String[].
    */
	public String[] softSplit(final String str) {
		if (str.split(", ").length < 3) { return str.split("` "); }
		String splitTest = ""; int index = 1;
	    for (String sd : str.split(", ")) { if (index == 3) { splitTest += sd + "` "; index = 1; } else { splitTest += sd + ", "; index++; } }
	    if (splitTest.endsWith(", ")) { splitTest = splitTest.substring(0, splitTest.length() - 2); }
	    return splitTest.split("` ");
	}
	
   /**
    * Splits the String to a List.
    * 
    * @param str - The String to be Split.
    * @return The split String as a List.
    */
	public List<String> split(final String str) {
		List<String> splitList = new ArrayList<String>();
		for (String split : str.split(", ")) {
			splitList.add(split);
		}
		return splitList;
	}
	
   /**
    * Replaces the last occurence of the regex.
    * 
    * @param str - The String to be checked.
    * @param sub1 - The occurence to be removed.
    * @param sub2 - The occurence to placed.
    * @return The newly replaced String.
    */
	public String replaceLast(final String str, final String sub1, final String sub2) {
	    int pos = str.lastIndexOf(sub1); 
	    if (pos > -1) { 
	       return str.substring(0, pos) + sub2 + str.substring(pos + sub1.length(), str.length()); 
	    } else {
	       return str;
	    }
	}
	
   /**
    * Gets the items.yml path for the auto generated item and its corresponding Integer.
    * 
    * @param i - The Integer to be set as the item path.
    * @return The Integer to be set as the auto generated item path.
    */
	public int getPath(final int i) {
		if (ConfigHandler.getConfig().getFile("items.yml").getString("items.item_" + i) != null) {
			return this.getPath(i + 1);
		}
		return i;
	}
	
   /**
    * Color Encodes a String so that it is completely hidden in color codes,
    * this will be invisible to a normal eye and will not display any text.
    * 
    * @param str - The String to be Color Encoded.
    * @return The Color Encoded String.
    */
	public String colorEncode(final String str) {
		try {
			String hiddenData = "";
			for (char c: str.toCharArray()) {
				hiddenData += "§" + c;
			}
			return hiddenData;
		} catch (Exception e) {
			ServerUtils.sendDebugTrace(e);
			return null;
		}
	}

   /**
    * Decodes a Color Encoded String.
    * 
    * @param str - The String to be Color Decoded.
    * @return The Color Decoded String.
    */
	public String colorDecode(final String str) {
		try {
			String[] hiddenData = str.split("(?:\\w{2,}|\\d[0-9A-Fa-f])+");
			String returnData = "";
			if (hiddenData == null) {
				hiddenData = str.split("§");
				for (int i = 0; i < hiddenData.length; i++) {
					returnData += hiddenData[i];
				}
				return returnData;
			} else {
				String[] d = hiddenData[hiddenData.length - 1].split("§");
				for (int i = 1; i < d.length; i++) {
					returnData += d[i];
				}
				return returnData;
			}
		} catch (Exception e) {
			ServerUtils.sendDebugTrace(e);
			return null;
		}
	}
	
   /**
    * Gets the Color from the provided HexColor.
    * 
    * @param hexString - The HexColor to be converted to Color.
    * @return The Color found from the HexColor.
    */
	public Color getColorFromHexColor(final String hexString) {
		int hex = Integer.decode("#" + hexString.replace("#", ""));
		int r = (hex & 0xFF0000) >> 16;
		int g = (hex & 0xFF00) >> 8;
		int b = (hex & 0xFF);
		Color bukkitColor = Color.fromRGB(r, g, b);
		return bukkitColor;
	}
	
   /**
    * Gets the number of characters in the String.
    * 
    * @param str - The String to be checked.
    * @return The number of characters in the String..
    */
	public int countCharacters(final String str) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (Character.isLetter(str.charAt(i)))
				count++;
			}
		return count;
	}
	
   /**
    * Checks if the specified String is an Integer Value.
    * 
    * @param str - The String to be checked.
    * @return If the String is an Integer Value.
    */
	public boolean isInt(final String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) { return false; }
		return true;
	}
	
   /**
    * Checks if the specified String is an Double Value.
    * 
    * @param str - The String to be checked.
    * @return If the String is an Double Value.
    */
	public boolean isDouble(final String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException e) { return false; }
		return true;
	}
	
   /**
    * Gets the first found Integer from the specified String.
    * 
    * @param str - The String to be checked.
    * @return The first found Integer.
    */
	public Integer returnInteger(final String str) {
		if (str == null) { return null; }
		else {
			char[] characters = str.toCharArray();
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
	
   /**
    * Gives all custom items to the specified player.sadsadasds
    * 
    * @param player - that will recieve the items.
    */
	public UUID UUIDConversion(String uuidString) {
		UUID uuid = null;
		if (uuidString != null && !uuidString.isEmpty()) {
			uuidString = uuidString.replace("-", "");
			uuid = new UUID( 
					new BigInteger(uuidString.substring(0, 16), 16).longValue(),
			        new BigInteger(uuidString.substring(16), 16).longValue());
		}
		return uuid;
	}
	
   /**
    * Converts a BufferedReader to a String output.
    * 
    * @param reader - the BufferedReader to be converted.
    * @return The resulting appended String.
    */
	public String toString(BufferedReader reader) throws IOException {
		StringBuilder result = new StringBuilder();
		String line = null; while ((line = reader.readLine()) != null) { result.append(line); }
		return result.toString();
	}
	
   /**
    * Encrypts the String to Base64.
    * 
    * @param str - The String to be encrypted.
    * @return The Base64 encoded String.
    */
	public String encrypt(final String str) {
		try {
			return Base64.getEncoder().encodeToString(str.getBytes("UTF-8"));
		} catch (Exception e) {
			ServerUtils.logDebug("{Utils} Failure to encrypt sensitive text!");
			ServerUtils.sendDebugTrace(e);
		}
		return null;
	}
	
   /**
    * Decrypts the encoded Base64 String.
    * 
    * @param str - The String to be decrypted.
    * @return The decrypted String.
    */
	public String decrypt(final String str) {
		try {
			return new String(Base64.getDecoder().decode(str), "UTF-8");
		} catch (Exception e) {
			ServerUtils.logDebug("{Utils} Failure to decrypt sensitive text!");
			ServerUtils.sendDebugTrace(e);
		}
		return null;
	}
 
   /**
    * Gets a random Integer between the upper and lower limits.
    * 
    * @param lower - The lower limit.
    * @param upper - The upper limit.
    * @return The randomly selected Integer between the limits.
    */
	public int getRandom(final int lower, final int upper) {
		return new Random().nextInt((upper - lower) + 1) + lower;
	}
	
   /**
    * Randomly selects an Entry from a HashMap.
    * 
    * @param map - The HashMap to have a entry selected.
    * @return The randomly selected entry.
    */
	public Entry<?, ?> randomEntry(final HashMap<?, ?> map) {
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
	
   /**
    * Checks if the String contains the location.
    * 
    * @param location - The location that the String should contain.
    * @param str - The String to be checked.
    * @return 
    */
	public boolean containsLocation(final String location, final String str) {
		if (str.equalsIgnoreCase("ALL") || str.equalsIgnoreCase("GLOBAL") 
			|| str.equalsIgnoreCase("ENABLED") || str.equalsIgnoreCase("TRUE")) {
			return true;
		} else {
			for (String eventLoc: str.split(",")) {
				if (eventLoc.equalsIgnoreCase(location)) {
					return true;
				}
			}
		}
		return false;
	}
	
   /**
    * Gets the Crafting Slot ID Value.
    * 
    * @param str - The String to be checked.
    * @return The Crafting Slot Value.
    */
	public int getSlotConversion(final String str) {
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
	
   /**
    * Gets the Armor Slot ID.
    * 
    * @param slot - The slot to be checked.
    * @param integer - If the return value should be a String or Integer value.
    * @return The Armor Slot ID.
    */
	public String getArmorSlot(final String slot, final boolean integer) {
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
   
   /**
    * Checks if the specified Listener is Registered.
    * 
    * @param listener - The name of the Listener to be checked.
    * @return If the Listener is Registered.
    */
	public boolean isRegistered(final String listener) {
		boolean returnValue = false;
        ArrayList<RegisteredListener> rls = HandlerList.getRegisteredListeners(ItemJoin.getInstance());
        for(RegisteredListener rl: rls) {
        	if (rl.getListener().getClass().getSimpleName().equalsIgnoreCase(listener)) {
        		returnValue = true; break;
        	}
        }
		return returnValue;
	}
	
   /**
    * Checks if the input is NULL, and returns either NONE for NULL / EMPTY,
    * or the properly formatted list as a String.
    * 
    * @param input - The String to be checked.
    * @return The newly formatted String.
    */
	public String nullCheck(String input) {
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
	
   /**
    * Translated the specified String by formatting its hex color codes.
    * 
    * @param str - The String to have its Color Codes properly Converted to Mojang Hex Colors.
    * @return The translated string.
    */
    public String translateHexColorCodes(final String str) {
    	final char COLOR_CHAR = ChatColor.COLOR_CHAR;
    	Matcher matcher = Pattern.compile("&#([A-Fa-f0-9]{6})").matcher(str);
    	StringBuffer buffer = new StringBuffer(str.length() + 4 * 8);
    	while (matcher.find()) {
    		String group = matcher.group(1);
    		matcher.appendReplacement(buffer, COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
    	}
    	return matcher.appendTail(buffer).toString();
    }
	
   /**
    * Formats any color codes found in the String to Bukkit Colors so the
    * text will be colorfully formatted.
    * 
    * @param str - The String to have its Color Codes properly Converted to Bukkit Colors.
    * @return The newly formatted String.
    */
	public String colorFormat(final String str) {
		return ChatColor.translateAlternateColorCodes('&', this.translateHexColorCodes(str));
	}
	
   /**
    * Translates the specified String by foramtting its color codes and replacing placeholders.
    * 
    * @param str - The String being translated.
    * @param player - The Player having their String translated.
    * @param placeHolder - The placeholders to be replaced into the String.
    * @return The newly translated String.
    */
	public String translateLayout(String str, final Player player, final String...placeHolder) {
		String playerName = "EXEMPT";
		
		if (player != null && DependAPI.getDepends(false).nickEnabled()) {
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
			try { str = str.replace("%player%", playerName); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { str = str.replace("%mob_kills%", String.valueOf(player.getStatistic(Statistic.MOB_KILLS))); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { str = str.replace("%player_kills%", String.valueOf(player.getStatistic(Statistic.PLAYER_KILLS))); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { str = str.replace("%player_deaths%", String.valueOf(player.getStatistic(Statistic.DEATHS))); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { str = str.replace("%player_food%", String.valueOf(player.getFoodLevel())); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { 
				final double health = (ServerUtils.hasSpecificUpdate("1_8") ? player.getHealth() : (double)player.getClass().getMethod("getHealth", double.class).invoke(player));
				str = str.replace("%player_health%", String.valueOf(health)); 
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { str = str.replace("%player_location%", player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { if (placeHolder != null && placeHolder.length <= 1) { str = str.replace("%player_hit%", placeHolder[0]); } } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			try { if (Bukkit.isPrimaryThread()) { str = str.replace("%player_interact%", PlayerHandler.getNearbyPlayer(player, 3)); } } catch (Exception e) { ServerUtils.sendDebugTrace(e); } }
			if (player == null) { try { str = str.replace("%player%", "CONSOLE"); } catch (Exception e) { ServerUtils.sendDebugTrace(e); } }
			str = ChatColor.translateAlternateColorCodes('&', this.translateHexColorCodes(str));
		if (DependAPI.getDepends(false).placeHolderEnabled()) {
			try { try { return PlaceholderAPI.setPlaceholders(player, str); } 
			catch (NoSuchFieldError e) { ServerUtils.logWarn("An error has occured when setting the PlaceHolder " + e.getMessage() + ", if this issue persits contact the developer of PlaceholderAPI."); return str; }
			} catch (Exception e) { }
		}
		return str;
	}
	
   /**
    * Gets the instance of the Utils.
    * 
    * @return The Utils instance.
    */
    public static StringUtils getUtils() { 
        if (util == null) { util = new StringUtils(); }
        return util; 
    } 
}