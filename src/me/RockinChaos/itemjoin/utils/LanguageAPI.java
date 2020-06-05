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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class LanguageAPI {
	private Lang langType = Lang.ENGLISH;
	
    private static LanguageAPI lang;
	
   /**
    * Executes a Message to the Sender.
    * 
    * @param sender - The sender receiving the Message.
    * @param langMessage - The Message being sent.
    */
	public void dispatchMessage(final CommandSender sender, String langMessage) { 
		Player player = null; if (sender instanceof Player) { player = (Player) sender; }
		langMessage = Utils.getUtils().translateLayout(langMessage, player);
		if (sender instanceof ConsoleCommandSender) { langMessage = ChatColor.stripColor(langMessage); } 
		sender.sendMessage(langMessage);
	}
	
   /**
    * Executes a Lang Message for the Sender.
    * 
    * @param nodeLocation - The String location of the Language Message. 
    * @param sender - The Sender who will receive the Message. 
    * @param placeHolder - Placeholders to be placed into the Langugage Message.
    */
	public void sendLangMessage(final String nodeLocation, final CommandSender sender, final String...placeHolder) {
		Player player = null; if (sender instanceof Player) { player = (Player) sender; }
		String langMessage = ConfigHandler.getConfig(false).getFile(this.langType.nodeLocation()).getString(nodeLocation);
		String prefix = Utils.getUtils().translateLayout(ConfigHandler.getConfig(false).getFile(this.langType.nodeLocation()).getString("Prefix"), player); 
		if (prefix == null || prefix.isEmpty() || !this.showPrefix(nodeLocation)) { prefix = ""; } else { prefix += " "; }
		if (langMessage != null && !langMessage.isEmpty()) {
			langMessage = this.translateLangHolders(langMessage, this.initializeRows(placeHolder));
			langMessage = Utils.getUtils().translateLayout(langMessage, player);
			String[] langLines = langMessage.split(" /n ");
			for (String langLine : langLines) {
				String langStrip = prefix + langLine;
				if (sender instanceof ConsoleCommandSender) { langStrip = ChatColor.stripColor(langStrip); } 
				if (this.isConsoleMessage(nodeLocation)) { ServerHandler.getServer().logInfo(ChatColor.stripColor(langLine)); }
				else { sender.sendMessage(langStrip);	}
			}
		}
	}
	
   /**
    * Initializes the Placeholders for the Lang Message.
    * 
    * @param placeHolder - Placeholders to be placed into the Langugage Message.
    */
	private String[] initializeRows(final String...placeHolder) {
		if (placeHolder == null || placeHolder.length != this.newString().length) {
			String[] langHolder = this.newString();
			for (int i = 0; i < langHolder.length; i++) {
				langHolder[i] = "&lnull";
			}
			return langHolder;
		} else {
			String[] langHolder = placeHolder;
			for (int i = 0; i < langHolder.length; i++) {
				if (langHolder[i] == null) {
					langHolder[i] = "&lnull";
				}
			}
			return langHolder;
		}
	}
	
   /**
    * Translates the Language PlaceHolders into the actual Placeholders defined for the Message.
    * 
    * @param langMessage - The Message being sent.
    * @param langHolder - Placeholders to be placed into the Langugage Message.
    */
	private String translateLangHolders(final String langMessage, final String...langHolder) {
		return langMessage
				.replace("%world%", langHolder[0])
				.replace("%targetplayer%", langHolder[1])
				.replace("%targetplayer_world%", langHolder[2])
				.replace("%item%", langHolder[3])
				.replace("%item_type%", langHolder[4])
				.replace("%balance%", langHolder[5])
				.replace("%cost%", langHolder[6])
				.replace("%failcount%", langHolder[7])
				.replace("%failedcount%", langHolder[7])
				.replace("%database%", langHolder[8])
				.replace("%command%", langHolder[9])
				.replace("%purgedata%", langHolder[10])
				.replace("%amount%", langHolder[11])
				.replace("%players%", langHolder[12])
				.replace("%timeleft%", langHolder[13])
				.replace("%type%", langHolder[14])
				.replace("%example%", langHolder[15])
				.replace("%input%", langHolder[16]);
	}
	
   /**
    * Checks if the Language Prefix should be shown.
    * 
    * @param nodeLocation - The String location of the Language Message.
    * @return If the Prefix is to be shown.
    */
	private boolean showPrefix(final String nodeLocation) {
		if (nodeLocation.equalsIgnoreCase("Commands.List.itemRow") || nodeLocation.equalsIgnoreCase("Commands.List.worldHeader")
				|| nodeLocation.equalsIgnoreCase("Commands.List.noItemsDefined") || nodeLocation.equalsIgnoreCase("Commands.Info.material")
				 || nodeLocation.equalsIgnoreCase("Commands.Info.dataValue") || nodeLocation.equalsIgnoreCase("Commands.World.worldRow") 
				 || nodeLocation.equalsIgnoreCase("Commands.World.worldHeader") || nodeLocation.equalsIgnoreCase("Commands.World.worldsFoundHeader")) {
			return false;
		}
		return true;
	}
	
   /**
    * Checks if the Language Message is a Console Message.
    * 
    * @param nodeLocation - The String location of the Language Message.
    * @return If the Language Message is a Console Message.
    */
	private boolean isConsoleMessage(final String nodeLocation) {
		if (nodeLocation.equalsIgnoreCase("Commands.Updates.checking") 
				|| nodeLocation.equalsIgnoreCase("Commands.Updates.forcing")) {
			return true;
		}
		return false;
	}
	
   /**
    * Creates a new String Array for langHolders.
    * 
    * @return The new String Array.
    */
	public String[] newString() {
		return new String[18];
	}
	
   /**
    * Gets the current Language that is being translated.
    * 
    * @return The Language.
    */
	public String getLanguage() {
		return this.langType.name().substring(0, 1).toUpperCase() + this.langType.name().substring(1).toLowerCase();
	}
	
   /**
    * Gets the current Language File name.
    * 
    * @return The Language File name.
    */
	public String getFile() {
		return this.langType.nodeLocation();
	}
	
   /**
    * Sets the current Language.
    * 
    * @param lang - The Language to be set.
    */
	public void setLanguage(final String lang) {
		if (lang.equalsIgnoreCase("tw")) {
			this.langType = Lang.TWCHINESE;
		} else if (lang.equalsIgnoreCase("cn")) {
			this.langType = Lang.CNCHINESE;
		} else if (lang.equalsIgnoreCase("en")) {
			this.langType = Lang.ENGLISH;
		} else if (lang.equalsIgnoreCase("es")) {
			this.langType = Lang.SPANISH;
		}
	}
	
   /**
    * Gets the current Language from the config and saves it to memory.
    * 
    */
	public void langFile() {
		String lang = ConfigHandler.getConfig(false).getFile("config.yml").getString("Language").replace(" ", "");
		if (lang.equalsIgnoreCase("TraditionalChinese") || lang.equalsIgnoreCase("TwChinese") || lang.equalsIgnoreCase("Chinese")) { this.setLanguage("tw"); } 
		else if (lang.equalsIgnoreCase("SimplifiedChinese") || lang.equalsIgnoreCase("CnChinese")) { this.setLanguage("cn"); } 
		else if (Utils.getUtils().containsIgnoreCase(lang, "Chinese")) { this.setLanguage("tw"); } 
		else if (lang.replace(" ", "").equalsIgnoreCase("Spanish")) { this.setLanguage("es"); } 
		else { this.setLanguage("en"); }
	}
	
   /**
	* Defines the Lang type for the Language.
	* 
	*/
	private enum Lang {
		DEFAULT("en-lang.yml", 0), ENGLISH("en-lang.yml", 1), SPANISH("es-lang.yml", 2), TWCHINESE("tw-lang.yml", 3), CNCHINESE("cn-lang.yml", 4);
		private Lang(final String nodeLocation, final int i) { this.nodeLocation = nodeLocation; }
		private final String nodeLocation;
		private String nodeLocation() { return nodeLocation; }
	}
	
   /**
    * Gets the instance of the Language.
    * 
    * @param regen - If the instance should be regenerated.
    * @return The Language instance.
    */
    public static LanguageAPI getLang(final boolean regen) { 
        if (lang == null || regen) {
        	lang = new LanguageAPI();
        	lang.langFile();
        }
        return lang; 
    } 
}