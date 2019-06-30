package me.RockinChaos.itemjoin.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class Language {
	private static Lang langType = Lang.ENGLISH;
	
	public static void dispatchMessage(CommandSender sender, String langMessage) { 
		Player player = null; if (sender instanceof Player) { player = (Player) sender; }
		langMessage = Utils.translateLayout(langMessage, player);
		sender.sendMessage(Utils.stripLogColors(sender, langMessage));
	}
	
	public static void sendLangMessage(String nodeLocation, CommandSender sender, String...placeHolder) {
		Player player = null; if (sender instanceof Player) { player = (Player) sender; }
		String langMessage = ConfigHandler.getConfig(langType.nodeLocation()).getString(nodeLocation);
		String prefix = Utils.translateLayout(ConfigHandler.getConfig(langType.nodeLocation()).getString("Prefix"), player); if (prefix == null || !showPrefix(nodeLocation)) { prefix = ""; } else { prefix += " "; }
		if (langMessage != null && !langMessage.isEmpty()) {
			langMessage = translateLangHolders(langMessage, initializeRows(placeHolder));
			langMessage = Utils.translateLayout(langMessage, player);
			String[] langLines = langMessage.split(" /n ");
			for (String langLine : langLines) {
				String langStrip = prefix + langLine;
				if (sender instanceof ConsoleCommandSender) { langStrip = Utils.stripLogColors(sender, langStrip); } 
				if (isConsoleMessage(nodeLocation)) { ServerHandler.sendConsoleMessage(Utils.stripLogColors(sender, langLine)); }
				else { sender.sendMessage(langStrip);	}
			}
		}
	}
	
	private static String[] initializeRows(String...placeHolder) {
		if (placeHolder == null || placeHolder.length != newString().length) {
			String[] langHolder = Language.newString();
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
	
	private static String translateLangHolders(String langMessage, String...langHolder) {
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
				.replace("%timeleft%", langHolder[13]);
	}
	
	private static boolean showPrefix(String nodeLocation) {
		if (nodeLocation.equalsIgnoreCase("Commands.List.itemRow") || nodeLocation.equalsIgnoreCase("Commands.List.worldHeader")
				|| nodeLocation.equalsIgnoreCase("Commands.List.noItemsDefined") || nodeLocation.equalsIgnoreCase("Commands.Info.material")
				 || nodeLocation.equalsIgnoreCase("Commands.Info.dataValue") || nodeLocation.equalsIgnoreCase("Commands.World.worldRow") 
				 || nodeLocation.equalsIgnoreCase("Commands.World.worldHeader") || nodeLocation.equalsIgnoreCase("Commands.World.worldsFoundHeader")) {
			return false;
		}
		return true;
	}
	
	private static boolean isConsoleMessage(String nodeLocation) {
		if (nodeLocation.equalsIgnoreCase("Commands.Updates.checking") 
				|| nodeLocation.equalsIgnoreCase("Commands.Updates.forcing")) {
			return true;
		}
		return false;
	}
	
	public static String[] newString() {
		return new String[14];
	}
	
	public static String getLanguage() {
		return langType.name().substring(0, 1).toUpperCase() + langType.name().substring(1).toLowerCase();
	}
	
	public static void setLanguage(String lang) {
		if (lang.equalsIgnoreCase("tw")) {
			langType = Lang.TWCHINESE;
		} else if (lang.equalsIgnoreCase("cn")) {
			langType = Lang.CNCHINESE;
		} else if (lang.equalsIgnoreCase("en")) {
			langType = Lang.ENGLISH;
		}
	}
	
	private enum Lang {
		DEFAULT("en-lang.yml", 0), ENGLISH("en-lang.yml", 1), TWCHINESE("tw-lang.yml", 1), CNCHINESE("cn-lang.yml", 1);
		private Lang(final String nodeLocation, final int i) { this.nodeLocation = nodeLocation; }
		private final String nodeLocation;
		private String nodeLocation() { return nodeLocation; }
	}
}