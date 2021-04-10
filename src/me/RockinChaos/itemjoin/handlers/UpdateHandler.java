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

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

public class UpdateHandler {
    private final String HOST = "https://api.github.com/repos/RockinChaos/" + ItemJoin.getInstance().getName().toLowerCase() + "/releases/latest";
    private String versionExact = ItemJoin.getInstance().getDescription().getVersion();
    private String localeVersion = this.versionExact.split("-")[0];
    private String latestVersion;
    private boolean betaVersion = this.versionExact.contains("-SNAPSHOT") || this.versionExact.contains("-BETA") || this.versionExact.contains("-ALPHA");
    private boolean devVersion = this.localeVersion.equals("${project.version}");
    
    private File jarRef;
    private int BYTE_SIZE = 2048;
    
    private boolean updatesAllowed = ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.CheckforUpdates");
    
    private static UpdateHandler updater;
        
   /**
    * Initializes the UpdateHandler and Checks for Updates upon initialization.
    *
    */
    public UpdateHandler() {
       this.jarRef = ItemJoin.getInstance().getPlugin();
       this.checkUpdates(ItemJoin.getInstance().getServer().getConsoleSender(), true);
    }
    
   /**
    * If the GitHub host has an available update, attenots to download the jar file.
    * Downloads and write the new data to the plugin jar file.
    * 
    * @param sender - The executor of the update checking.
    */
    public void forceUpdates(final CommandSender sender) {
    	if (this.updateNeeded(sender, false)) {
    		ServerUtils.messageSender(sender, "&aAn update has been found!");
    		ServerUtils.messageSender(sender, "&aAttempting to update from " + "&ev" + this.localeVersion + " &ato the new "  + "&ev" + this.latestVersion);
    		try {
    			String uri = this.HOST.replace("repos/", "").replace("api.", "").replace("latest", "download/" + "v" + this.latestVersion + "/" + ItemJoin.getInstance().getName().toLowerCase() + ".jar") + "?_=" + System.currentTimeMillis();
    			HttpURLConnection httpConnection = (HttpURLConnection) new URL(uri).openConnection();
    			httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0...");
    			BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
    			FileOutputStream fos = new FileOutputStream(this.jarRef);
    			BufferedOutputStream bout = new BufferedOutputStream(fos, this.BYTE_SIZE);
    			String progressBar = "&a::::::::::::::::::::::::::::::";
    			byte[] data = new byte[this.BYTE_SIZE];
    			long cloudFileSize = httpConnection.getContentLength();
    			long fetchedSize = 0;
    			int bytesRead;
    			while ((bytesRead = in .read(data, 0, this.BYTE_SIZE)) >= 0) {
    				bout.write(data, 0, bytesRead);
    				fetchedSize += bytesRead;
    				final int currentProgress = (int)(((double) fetchedSize / (double) cloudFileSize) * 30);
    				if ((((fetchedSize * 100) / cloudFileSize) % 25) == 0 && currentProgress > 10) {
    					ServerUtils.messageSender(sender, progressBar.substring(0, currentProgress + 2) + "&c" + progressBar.substring(currentProgress + 2));
    				}
    			}
    			bout.close(); in.close(); fos.close();
    			ServerUtils.messageSender(sender, "&aSuccessfully updated to v" + this.latestVersion + "!");
    			ServerUtils.messageSender(sender, "&aYou must restart your server for this to take affect.");
    		} catch (Exception e) {
    			ServerUtils.messageSender(sender, "&cAn error has occurred while trying to update the plugin " + ItemJoin.getInstance().getName() + ".");
    			ServerUtils.messageSender(sender, "&cPlease try again later, if you continue to see this please contact the plugin developer.");
    			ServerUtils.sendDebugTrace(e);
    		}
    	} else {
    		if (this.betaVersion) {
    			ServerUtils.messageSender(sender, "&aYou are running a SNAPSHOT!");
    			ServerUtils.messageSender(sender, "&aIf you find any bugs please report them!");
    		}
    		ServerUtils.messageSender(sender, "&aYou are up to date!");
    	}
    }
    
   /**
    * Checks to see if an update is required, notifying the console window and online op players.
    * 
    * @param sender - The executor of the update checking.
    * @param onStart - If it is checking for updates on start.
    */
    public void checkUpdates(final CommandSender sender, final boolean onStart) {
    	if (this.updateNeeded(sender, onStart) && this.updatesAllowed) {
    		if (this.betaVersion) {
    			ServerUtils.messageSender(sender, "&cYour current version: &bv" + this.localeVersion + "-SNAPSHOT");
    			ServerUtils.messageSender(sender, "&cThis &bSNAPSHOT &cis outdated and a release version is now available.");
    		} else {
    			ServerUtils.messageSender(sender, "&cYour current version: &bv" + this.localeVersion + "-RELEASE");
    		}
    		ServerUtils.messageSender(sender, "&cA new version is available: " + "&av" + this.latestVersion + "-RELEASE");
    		ServerUtils.messageSender(sender, "&aGet it from: https://github.com/RockinChaos/" + ItemJoin.getInstance().getName().toLowerCase() + "/releases/latest");
    		ServerUtils.messageSender(sender, "&aIf you wish to auto update, please type /" + ItemJoin.getInstance().getName() + " Upgrade");
    		this.sendNotifications();
    	} else if (this.updatesAllowed) {
    		if (this.betaVersion) {
    			ServerUtils.messageSender(sender, "&aYou are running a SNAPSHOT!");
    			ServerUtils.messageSender(sender, "&aIf you find any bugs please report them!");
    		} else if (this.devVersion) {
    			ServerUtils.messageSender(sender, "&aYou are running a DEVELOPER SNAPSHOT!");
    			ServerUtils.messageSender(sender, "&aIf you find any bugs please report them!");
    			ServerUtils.messageSender(sender, "&aYou will not receive any updates requiring you to manually update.");
    		}
    		ServerUtils.messageSender(sender, "&aYou are up to date!");
    	}
    }
    
   /**
    * Directly checks to see if the GitHub host has an update available.
    * 
    * @param sender - The executor of the update checking.
    * @param onStart - If it is checking for updates on start.
    * @return If an update is needed.
    */
    private boolean updateNeeded(final CommandSender sender, final boolean onStart) {
    	if (this.updatesAllowed) {
    		if (!onStart) { ServerUtils.messageSender(sender, "&aChecking for updates..."); }
    		try {
    			URLConnection connection = new URL(this.HOST + "?_=" + System.currentTimeMillis()).openConnection();
    			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    			String JsonString = StringUtils.toString(reader); 
			    JSONObject objectReader = (JSONObject) JSONValue.parseWithException(JsonString);
			    String gitVersion = objectReader.get("tag_name").toString();
    			reader.close();
    			if (gitVersion.length() <= 7) {
    				this.latestVersion = gitVersion.replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", "");
    				String[] latestSplit = this.latestVersion.split("\\.");
    				String[] localeSplit = this.localeVersion.split("\\.");
    				if (this.devVersion) {
    					return false;
    				} else if ((Integer.parseInt(latestSplit[0]) > Integer.parseInt(localeSplit[0]) || Integer.parseInt(latestSplit[1]) > Integer.parseInt(localeSplit[1]) || Integer.parseInt(latestSplit[2]) > Integer.parseInt(localeSplit[2]))
    						|| (this.betaVersion && (Integer.parseInt(latestSplit[0]) == Integer.parseInt(localeSplit[0]) && Integer.parseInt(latestSplit[1]) == Integer.parseInt(localeSplit[1]) && Integer.parseInt(latestSplit[2]) == Integer.parseInt(localeSplit[2])))) {
    					return true;
    				}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    			ServerUtils.messageSender(sender, "&cFailed to check for updates, connection could not be made.");
    			return false;
    		}
    	} else if (!onStart) {
    		ServerUtils.messageSender(sender, "&cUpdate checking is currently disabled in the config.yml");
    		ServerUtils.messageSender(sender, "&cIf you wish to use the auto update feature, you will need to enable it.");
        }
    	return false;
    }
    
   /**
    * Sends out notifications to all online op players that 
    * an update is available at the time of checking for updates.
    * 
    */
    private void sendNotifications() {
    	try {
    		Collection < ? > playersOnline = null;
    		Player[] playersOnlineOld = null;
    		if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    				playersOnline = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    				for (Object objPlayer: playersOnline) {
    					if (((Player) objPlayer).isOp()) {
    						ServerUtils.messageSender(((Player) objPlayer), "&eAn update has been found!");
    						ServerUtils.messageSender(((Player) objPlayer), "&ePlease update to the latest version: v" + this.latestVersion);
    					}
    				}
    			}
    		} else {
    			playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    			for (Player objPlayer: playersOnlineOld) {
    				if (objPlayer.isOp()) {
						ServerUtils.messageSender(objPlayer, "&eAn update has been found!");
						ServerUtils.messageSender(objPlayer, "&ePlease update to the latest version: v" + this.latestVersion);
    				}
    			}
    		}
    	} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
    }
    
    
   /**
    * Gets the exact string version from the plugin yml file.
    * 
    * @return The exact server version.
    */
    public String getVersion() {
    	return this.versionExact;
    }
    
   /**
    * Gets the plugin jar file directly.
    * 
    * @return The plugins jar file.
    */
    public File getJarReference() {
    	return this.jarRef;
    }
    
   /**
    * Gets the instance of the UpdateHandler.
    * 
    * @param regen - If the instance should be regenerated.
    * @return The UpdateHandler instance.
    */
    public static UpdateHandler getUpdater(final boolean regen) { 
        if (updater == null || regen) { updater = new UpdateHandler(); }
        return updater; 
    } 
}