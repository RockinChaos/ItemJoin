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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

public class UpdateHandler {
	
    private boolean updatesAllowed = ConfigHandler.getConfig("config.yml").getBoolean("General.CheckforUpdates");
    private File jarLink;
    private final String AUTOQUERY = "projects/itemjoin/files/latest";
    private final String AUTOHOST = "https://dev.bukkit.org/";
    private final int PROJECTID = 12661;
    private final String HOST = "https://api.spigotmc.org/legacy/update.php?resource=" + PROJECTID;
    private String versionExact = ItemJoin.getInstance().getDescription().getVersion();
    private boolean betaVersion = versionExact.contains("-SNAPSHOT") || versionExact.contains("-BETA") || versionExact.contains("-ALPHA");
    private String localeVersionRaw = versionExact.split("-")[0];
    private String latestVersionRaw;
    private double localeVersion = Double.parseDouble(localeVersionRaw.replace(".", ""));
    private double latestVersion;
    private int BYTE_SIZE = 2048;
        
    /**
     * Initializes the UpdateHandler and Checks for Updates upon initialization.
     *
     * @param file   The file that the plugin is running from.
     */
    public UpdateHandler(File file){
       this.jarLink = file;
       this.checkUpdates(ItemJoin.getInstance().getServer().getConsoleSender(), true);
    }
    
    /**
     * If the spigotmc host has an available update, redirects to download the jar file from dev-bukkit.
     * Downloads and write the new data to the plugin jar file.
     */
    public void forceUpdates(CommandSender sender) {
    	if (this.updateNeeded(sender, false)) {
    		ServerHandler.messageSender(sender, "&aAn update has been found!");
    		ServerHandler.messageSender(sender, "&aAttempting to update from " + "&ev" + this.localeVersionRaw + " &ato the new "  + "&ev" + this.latestVersionRaw);
    		try {
    			URL downloadUrl = new URL(this.AUTOHOST + this.AUTOQUERY);
    			HttpURLConnection httpConnection = (HttpURLConnection) downloadUrl.openConnection();
    			httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0...");
    			BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
    			FileOutputStream fos = new FileOutputStream(this.jarLink);
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
    					ServerHandler.messageSender(sender, progressBar.substring(0, currentProgress + 2) + "&c" + progressBar.substring(currentProgress + 2));
    				}
    			}
    			bout.close(); in.close(); fos.close();
    			ServerHandler.messageSender(sender, "&aSuccessfully updated to v" + this.latestVersionRaw + "!");
    			ServerHandler.messageSender(sender, "&aYou must restart your server for this to take affect.");
    		} catch (Exception e) {
    			ServerHandler.messageSender(sender, "&cAn error has occurred while trying to update the plugin ItemJoin.");
    			ServerHandler.messageSender(sender, "&cPlease try again later, if you continue to see this please contact the plugin developer.");
    			ServerHandler.sendDebugTrace(e);
    		}
    	}
    }
    
    /**
     * Checks to see if an update is required, notifying the console window and online op players.
     */
    public void checkUpdates(CommandSender sender, boolean onStart) {
    	if (this.updateNeeded(sender, onStart) && this.updatesAllowed) {
    		if (this.betaVersion) {
    			ServerHandler.messageSender(sender, "&cYour current version: &bv" + this.localeVersionRaw + "-SNAPSHOT");
    			ServerHandler.messageSender(sender, "&cThis &bSNAPSHOT &cis outdated and a release version is now available.");
    		} else {
    			ServerHandler.messageSender(sender, "&cYour current version: &bv" + this.localeVersionRaw);
    		}
    		ServerHandler.messageSender(sender, "&cA new version is available: " + "&av" + this.latestVersionRaw);
    		ServerHandler.messageSender(sender, "&aGet it from: https://www.spigotmc.org/resources/itemjoin.12661/history");
    		ServerHandler.messageSender(sender, "&aIf you wish to auto update, please type /ItemJoin AutoUpdate");
    		this.sendNotifications();
    	} else if (this.updatesAllowed) {
    		if (this.betaVersion) {
    			ServerHandler.messageSender(sender, "&aYou are running a SNAPSHOT!");
    			ServerHandler.messageSender(sender, "&aIf you find any bugs please report them!");
    		}
    		ServerHandler.messageSender(sender, "&aYou are up to date!");
    	}
    }
    
    /**
     * Directly checks to see if the spigotmc host has an update available.
     */
    private Boolean updateNeeded(CommandSender sender, boolean onStart) {
    	if (this.updatesAllowed) {
    		ServerHandler.messageSender(sender, "&aChecking for updates...");
    		try {
    			InputStream input = (InputStream) new URL(this.HOST).openStream();
    			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    			String version = reader.readLine();
    			reader.close();
    			if (version.length() <= 7) {
    				this.latestVersionRaw = version.replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", "");
    				this.latestVersion = Double.parseDouble(this.latestVersionRaw.replace(".", ""));
    				if (this.latestVersion == this.localeVersion && this.betaVersion || this.localeVersion > this.latestVersion && !this.betaVersion || this.latestVersion > this.localeVersion) {
    					return true;
    				}
    			}
    		} catch (Exception e) {
    			ServerHandler.messageSender(sender, "&cAn error has occured when checking the plugin version!");
    			ServerHandler.messageSender(sender, "&cPlease contact the plugin developer!");
    			ServerHandler.sendDebugTrace(e);
    			return false;
    		}
    	} else if (!onStart) {
    		ServerHandler.messageSender(sender, "&cUpdate checking is currently disabled in the config.yml");
    		ServerHandler.messageSender(sender, "&cIf you wish to use the auto update feature, you will need to enable it.");
        }
    	return false;
    }
    
    /**
     * Sends out notifications to all online op players that an update is available at the time of checking for updates.
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
    						ServerHandler.messageSender(((Player) objPlayer), "&eAn update has been found!");
    						ServerHandler.messageSender(((Player) objPlayer), "&ePlease update to the latest version: v" + this.latestVersionRaw);
    					}
    				}
    			}
    		} else {
    			playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    			for (Player objPlayer: playersOnlineOld) {
    				if (objPlayer.isOp()) {
						ServerHandler.messageSender(objPlayer, "&eAn update has been found!");
						ServerHandler.messageSender(objPlayer, "&ePlease update to the latest version: v" + this.latestVersionRaw);
    				}
    			}
    		}
    	} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
    }
    
    
    /**
     * Gets the exact string version from the plugin yml file.
     */
    public String getVersion() {
    	return this.versionExact;
    }
    
    /**
     * Gets the plugin jar file directly.
     */
    public File getJarLink() {
    	return this.jarLink;
    }
}