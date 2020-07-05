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
package me.RockinChaos.itemjoin.handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.function.Consumer;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;

import me.RockinChaos.itemjoin.ItemJoin;

public class ServerHandler {
	
	private static ServerHandler server;
	private String packageName = ItemJoin.getInstance().getServer().getClass().getPackage().getName();
	private String serverVersion = this.packageName.substring(this.packageName.lastIndexOf('.') + 1).replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "");

   /**
    * Checks if the server is running the specified version.
    * 
    * @param versionString - The version to compare against the server version, example: '1_13'.
    * @return If the server version is greater than or equal to the specified version.
    */
	public boolean hasSpecificUpdate(final String versionString) {
		if (Integer.parseInt(serverVersion) >= Integer.parseInt(versionString.replace("_", ""))) {
			return true;
		}
		return false;
	}
	
   /**
    * Runs the methods Async on the main thread.
    * 
    * @param input - The methods to be executed Async on the main thread.
    */
	public void runAsyncThread(final Consumer<String> input) {
		Bukkit.getServer().getScheduler().runTaskAsynchronously(ItemJoin.getInstance(), () -> { 
			Bukkit.getServer().getScheduler().runTask(ItemJoin.getInstance(), () -> {
				input.accept("MAIN");
			}); 
		});
	}
	
   /**
    * Runs the methods Async on the main thread.
    * 
    * @param input - The methods to be executed Async on the main thread.
    */
	public void runAsyncThread(final Consumer<String> input, long delay) {
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(ItemJoin.getInstance(), () -> { 
			Bukkit.getServer().getScheduler().runTask(ItemJoin.getInstance(), () -> {
				input.accept("MAIN");
			}); 
		}, delay);
	}
	
   /**
    * Runs the methods Async on the main thread.
    * 
    * @param input - The methods to be executed Async on the main thread.
    */
	public void runAsyncThread(final Consumer<String> input, long delay, long period) {
		Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(ItemJoin.getInstance(), () -> { 
			Bukkit.getServer().getScheduler().runTask(ItemJoin.getInstance(), () -> {
				input.accept("MAIN");
			}); 
		}, delay, period);
	}
	
   /**
    * Sends a low priority log message as the plugin header.
    * 
    * @param message - The unformatted message text to be sent.
    */
	public void logInfo(String message) {
		String prefix = "[ItemJoin] ";
		message = prefix + message;
		if (message.equalsIgnoreCase("") || message.isEmpty()) { message = ""; }
		Bukkit.getServer().getLogger().info(message);
	}
	
   /**
    * Sends a warning message as the plugin header.
    * 
    * @param message - The unformatted message text to be sent.
    */
	public void logWarn(String message) {
		String prefix = "[ItemJoin_WARN] ";
		message = prefix + message;
		if (message.equalsIgnoreCase("") || message.isEmpty()) { message = ""; }
		Bukkit.getServer().getLogger().warning(message);
	}
	
   /**
    * Sends a developer warning message as the plugin header.
    * 
    * @param message - The unformatted message text to be sent.
    */
	public void logDev(String message) {
		String prefix = "[ItemJoin_DEVELOPER] ";
		message = prefix + message;
		if (message.equalsIgnoreCase("") || message.isEmpty()) { message = ""; }
		Bukkit.getServer().getLogger().warning(message);
	}
	
   /**
    * Sends a error message as the plugin header.
    * 
    * @param message - The unformatted message text to be sent.
    */
	public void logSevere(String message) {
		String prefix = "[ItemJoin_ERROR] ";
		message = prefix + message;
		if (message.equalsIgnoreCase("") || message.isEmpty()) { message = ""; }
		Bukkit.getServer().getLogger().severe(message);
	}
	
   /**
    * Sends a debug message as a loggable warning as the plugin header.
    * 
    * @param message - The unformatted message text to be sent.
    */
	public void logDebug(String message) {
		if (ConfigHandler.getConfig(false).debugEnabled()) {
			String prefix = "[ItemJoin_DEBUG] ";
			message = prefix + message;
			if (message.equalsIgnoreCase("") || message.isEmpty()) { message = ""; }
			Bukkit.getServer().getLogger().warning(message);
		}
	}

   /**
    * Sends the StackTrace of an Exception if debugging is enabled.
    * 
    * @param e - The exception to be sent.
    */
	public void sendDebugTrace(final Exception e) {
		if (ConfigHandler.getConfig(false).debugEnabled()) { e.printStackTrace(); }
	}
	
   /**
    * Sends a chat message to the specified sender.
    * 
    * @param sender - The entity to have the message sent.
    * @param message - The unformatted message text to be sent.
    */
	public void messageSender(CommandSender sender, String message) {
		String prefix = "&7[&eItemJoin&7] ";
		message = prefix + message;
		message = ChatColor.translateAlternateColorCodes('&', message).toString();
		if (message.contains("blankmessage") || message.equalsIgnoreCase("") || message.isEmpty()) { message = ""; }
		if	(sender instanceof ConsoleCommandSender) { message = ChatColor.stripColor(message); }
		sender.sendMessage(message);
	}
	
   /**
    * Tries to find the UUID on Mojangs official profile servers,
    * then apply the found players skin to the specified GameProfile.
    * 
    * @param profile - The GameProfile to have its skin set.
    * @param uuid - The UUID of the player to have their skin fetched and set to the GameProfile.
    * @return If the skin was successfully found and set to the specified GameProfile.
    */
	public boolean setSkin(final GameProfile profile, final UUID uuid) {
		try {
			HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uuid))).openConnection();
			if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
				String skin = reply.split("\"value\":\"")[1].split("\"")[0];
				String signature = reply.split("\"signature\":\"")[1].split("\"")[0];
				profile.getProperties().put("textures", new Property("textures", skin, signature));
				return true;
			} else {
				ServerHandler.getServer().logWarn("Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
				return false;
			}
		} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); return false; }
	}
    
   /**
    * Gets the instance of the ServerHandler.
    * 
    * @return The ServerHandler instance.
    */
    public static ServerHandler getServer() { 
        if (server == null) { server = new ServerHandler(); }
        return server; 
    } 
}