package me.RockinChaos.itemjoin.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class Language {
	private static CommandSender argsplayer;
	
	public static void informPlayer(CommandSender sender, String message) {
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		if (sender instanceof ConsoleCommandSender && ConfigHandler.getConfig("config.yml").getBoolean("Log-Coloration") != true) {
			message = ChatColor.stripColor(message);
		}
		if (message.equalsIgnoreCase("") || message.isEmpty()) {
			message = "";
	}
		sender.sendMessage(message);
	}

	public static void sendMessage(CommandSender sender, String MessageType, String ReplacementText) {
		if (ItemJoin.getInstance().getConfig().getString("Language") != null 
				&& ItemJoin.getInstance().getConfig().getString("Language").equalsIgnoreCase("English")) {
			sendEnglishMessage(sender, MessageType, ReplacementText);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void sendEnglishMessage(CommandSender sender, String MessageType, String ReplacementText) {
		Player player;
		if(sender instanceof ConsoleCommandSender) { player = ItemJoin.getInstance().getServer().getPlayer("Console"); } 
		else if (!(sender instanceof ConsoleCommandSender)) { player = (Player) sender; } else { player = ItemJoin.getInstance().getServer().getPlayer("NULL"); }
		if (ConfigHandler.getConfig("en-lang.yml").getString(MessageType) != null && !ConfigHandler.getConfig("en-lang.yml").getString(MessageType).isEmpty()) {
			String Prefix = "";
			if (!MessageType.equalsIgnoreCase("inWorldListHeader") && !MessageType.equalsIgnoreCase("inWorldListed") 
					&& !MessageType.equalsIgnoreCase("listWorldsHeader") && !MessageType.equalsIgnoreCase("listItems") && !MessageType.equalsIgnoreCase("itemInfo")) {
			Prefix = Utils.translateLayout(ConfigHandler.getConfig("en-lang.yml").getString("Prefix"), player);
			}
			String sendMessage = Utils.translateLayout(ConfigHandler.getConfig("en-lang.yml").getString(MessageType), player);
				String ReplacementTextList = ReplacementText;
				String[] TextSplits = ReplacementTextList.split(",");
				for (String ReplaceText: TextSplits) {
			sendMessage = sendMessage.replace("%items%", ReplaceText);
			sendMessage = sendMessage.replace("%item%", ReplaceText);
			if (MessageType.equalsIgnoreCase("itemChargeFailed")) {
				sendMessage = sendMessage.replace("%amount%", TextSplits[1].replace(" ", ""));
				sendMessage = sendMessage.replace("%cost%", TextSplits[0]);
			}
			
			
			if (MessageType.equalsIgnoreCase("givenToPlayer") || MessageType.equalsIgnoreCase("removedFromPlayer") || MessageType.equalsIgnoreCase("playerTriedRemove")
					|| MessageType.equalsIgnoreCase("removedAllFromPlayer") || MessageType.equalsIgnoreCase("givenAllToPlayer")
					|| MessageType.equalsIgnoreCase("playerTriedGive") || MessageType.equalsIgnoreCase("itemExistsInOthersInventory") || MessageType.equalsIgnoreCase("itemDoesntExistInOthersInventory") 
					|| MessageType.equalsIgnoreCase("allItemsDoNotExistInOthersInventory") || MessageType.equalsIgnoreCase("playerTriedRemoveAll") 
					|| MessageType.equalsIgnoreCase("givenToPlayerNoPerms") || MessageType.equalsIgnoreCase("givenAllPlayerNoPerms")
					|| MessageType.equalsIgnoreCase("allItemsExistInOthersInventory") || MessageType.equalsIgnoreCase("playerTriedGiveAllItems")) {
				if (argsplayer != null) {
				sendMessage = sendMessage.replace("%argsplayer%", argsplayer.getName());
					if (argsplayer instanceof Player) {
						sendMessage = sendMessage.replace("%argsplayer_world%", ((Player)argsplayer).getWorld().getName());
					} else {
						sendMessage = sendMessage.replace("%argsplayer_world%", "Console");
					}
				}
			}
			
			
			
			sendMessage = sendMessage.replace("%purgedata%", ReplaceText);
			sendMessage = sendMessage.replace("%command%", ReplaceText);
			sendMessage = sendMessage.replace("%item_type%", ReplaceText);
			sendMessage = sendMessage.replace("%database%", ReplaceText);
			sendMessage = sendMessage.replace("%argsplayer%", ReplaceText);
			sendMessage = sendMessage.replace("%argsplayer_world%", ReplaceText);
			sendMessage = sendMessage.replace("%failcount%", ReplaceText);
			sendMessage = sendMessage.replace("%failedcount%", ReplaceText);
			sendMessage = sendMessage.replace("%cost%", ReplaceText);
			sendMessage = sendMessage.replace("%amount%", ReplaceText);
			sendMessage = sendMessage.replace("%world%", ReplaceText);
			sendMessage = sendMessage.replace("main010Warn", "first-join, ip-limit, and various other data");
				}
		if (MessageType.equalsIgnoreCase("updateChecking")) {
			ServerHandler.sendConsoleMessage(sendMessage);
		} else {
			    String[] splitMessage = sendMessage.replace(" <n> ", "<n>").replace("<n> ", "<n>").replace(" <n>", "<n>").split("<n>");
			    for (String newMessage : splitMessage) {
			    	informPlayer(sender, Prefix + newMessage);
			    }
		}
		}
	}
	
	public static CommandSender getArgsPlayer() {
		return argsplayer;
	}
	
	public static void setArgsPlayer(CommandSender info) {
			argsplayer = info;
	}
}