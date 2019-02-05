package me.RockinChaos.itemjoin.handlers;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

public class UpdateHandler {
	
    private String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";
    private boolean updatesAllowed = ItemJoin.getInstance().getConfig().getBoolean("CheckForUpdates");
    private String version = ItemJoin.getInstance().getDescription().getVersion();
    private File jarLink;
    private final String AUTOQUERY = "projects/itemjoin/files/latest";
    private final String AUTOHOST = "https://dev.bukkit.org/";
    private final String HOST = "https://www.spigotmc.org/api/general.php";
    private final int PROJECTID = 12661;
    private final String KEY = ("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + PROJECTID);
    private boolean betaVersion = version.contains("-SNAPSHOT") || version.contains("-BETA") || version.contains("-ALPHA");
    private String localeVersionS = version.replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", "");
    private double localeVersion = Double.parseDouble(localeVersionS.replace(".", ""));
    private String latestVersionS;
    private double latestVersion;
    private int BYTE_SIZE = 2048;
        
    public UpdateHandler(File file){
       this.jarLink = file;
       this.checkUpdates(ItemJoin.getInstance().getServer().getConsoleSender());
    }
    
    public void forceUpdates(CommandSender sender) {
    	if (this.updateNeeded(sender)) {
      	  sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.GREEN + "An update has been found!"));
      	  sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.GREEN + "Attempting to update from " + ChatColor.YELLOW + "v" + localeVersionS +  ChatColor.GREEN +  " to the new "  + ChatColor.YELLOW +  "v" + latestVersionS));
    		try {
    			URL downloadUrl = this.getExactURL(this.AUTOHOST + this.AUTOQUERY);
    			HttpURLConnection httpConnection = (HttpURLConnection) downloadUrl.openConnection();
    			httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0...");
    			BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
    			FileOutputStream fos = new FileOutputStream(this.jarLink);
    			BufferedOutputStream bout = new BufferedOutputStream(fos, this.BYTE_SIZE);
    			String progressBar = ChatColor.GREEN + "::::::::::::::::::::::::::::::";
    			byte[] data = new byte[this.BYTE_SIZE];
    			long cloudFileSize = httpConnection.getContentLength();
    			long fetchedSize = 0;
    			int bytesRead;
    			while ((bytesRead = in .read(data, 0, this.BYTE_SIZE)) >= 0) {
    				bout.write(data, 0, bytesRead);
    				fetchedSize += bytesRead;
    				final int currentProgress = (int)(((double) fetchedSize / (double) cloudFileSize) * 30);
    				if ((((fetchedSize * 100) / cloudFileSize) % 25) == 0 && currentProgress > 10) {
    					sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + progressBar.substring(0, currentProgress + 2) + ChatColor.RED + progressBar.substring(currentProgress + 2)));
    				}
    			}
    			bout.close(); in.close(); fos.close();
    			sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.GREEN + "Successfully updated to v" + this.latestVersionS + "!"));
    			sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.GREEN + "You must restart your server for this to take affect."));
    		} catch (Exception e) {
    			sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.RED + "An error has occurred while trying to update the plugin ItemJoin."));
    			sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.RED + "Please try again later, if you continue to see this please contact the plugin developer."));
    			ServerHandler.sendDebugTrace(e);
    		}
    	} else if (!this.updatesAllowed) {
        	sender.sendMessage(Utils.stripLogColors(sender, Prefix + ChatColor.RED + "Update checking is currently disabled in the config.yml"));
        	sender.sendMessage(Utils.stripLogColors(sender, Prefix + ChatColor.RED + "If you wish to use the auto update feature, you will need to enable it."));
        }
    }
    
    public void checkUpdates(CommandSender sender) {
    	if (this.updateNeeded(sender)) {
    		if (this.betaVersion) {
    			sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.RED + "Your current version: v" + ChatColor.RED + this.localeVersionS + "-SNAPSHOT"));
    			sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.RED + "This SNAPSHOT is outdated and a release version is now available."));
    		} else {
    			sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.RED + "Your current version: v" + ChatColor.RED + this.localeVersionS));
    		}
    		sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.RED + "A new version is available: " + ChatColor.GREEN + "v" + this.latestVersionS));
    		sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.GREEN + "Get it from: https://www.spigotmc.org/resources/itemjoin.12661/history"));
    		sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.GREEN + "If you wish to auto update, please type /ItemJoin AutoUpdate"));
    		this.sendNotifications();
    	} else {
    		if (this.betaVersion) {
    			sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.GREEN + "You are running a SNAPSHOT!"));
    			sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.GREEN + "If you find any bugs please report them!"));
    		}
    		sender.sendMessage(this.Prefix + ChatColor.GREEN + "You are up to date!");
    	}
    }
    
    private Boolean updateNeeded(CommandSender sender) {
    	if (this.updatesAllowed) {
    		sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.GREEN + "Checking for updates..."));
    		try {
    			HttpURLConnection con = (HttpURLConnection) new URL(this.HOST).openConnection();
    			con.setDoOutput(true);
    			con.setRequestMethod("POST");
    			con.getOutputStream().write(this.KEY.getBytes("UTF-8"));
    			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
    			String version = reader.readLine();
    			reader.close();
    			if (version.length() <= 7) {
    				this.latestVersionS = version.replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", "");
    				this.latestVersion = Double.parseDouble(this.latestVersionS.replace(".", ""));
    				if (this.latestVersion == this.localeVersion && this.betaVersion || this.localeVersion > this.latestVersion && !this.betaVersion || this.latestVersion > this.localeVersion) {
    					return true;
    				}
    			}
    		} catch (Exception e) {
    			sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.RED + "An error has occured when checking the plugin version!"));
    			sender.sendMessage(Utils.stripLogColors(sender, this.Prefix + ChatColor.RED + "Please contact the plugin developer!"));
    			ServerHandler.sendDebugTrace(e);
    			return false;
    		}
    	}
    	return false;
    }
    
    private void sendNotifications() {
    	try {
    		Collection < ? > playersOnline = null;
    		Player[] playersOnlineOld = null;
    		if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    				playersOnline = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    				for (Object objPlayer: playersOnline) {
    					if (((Player) objPlayer).isOp()) {
    						((Player) objPlayer).sendMessage(this.Prefix + ChatColor.YELLOW + "An update has been found!");
    						((Player) objPlayer).sendMessage(this.Prefix + ChatColor.YELLOW + "Please update to the latest version: v" + this.latestVersionS + "");
    					}
    				}
    			}
    		} else {
    			playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    			for (Player objPlayer: playersOnlineOld) {
    				if (objPlayer.isOp()) {
    					objPlayer.sendMessage(this.Prefix + ChatColor.YELLOW + "An update has been found!");
    					objPlayer.sendMessage(this.Prefix + ChatColor.YELLOW + "Please update to the latest version: v" + this.latestVersionS + "");
    				}
    			}
    		}
    	} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
    }
    
    private URL getExactURL(String location) throws IOException {
    	URL resourceUrl, base, next;
    	HttpURLConnection conn;
    	String redLoc;
    	while (true) {
    		resourceUrl = new URL(location);
    		conn = (HttpURLConnection) resourceUrl.openConnection();
    		conn.setConnectTimeout(15000);
    		conn.setReadTimeout(15000);
    		conn.setInstanceFollowRedirects(false);
    		conn.setRequestProperty("User-Agent", "Mozilla/5.0...");
    		switch (conn.getResponseCode()) {
    			case HttpURLConnection.HTTP_MOVED_PERM:
    			case HttpURLConnection.HTTP_MOVED_TEMP:
    				redLoc = conn.getHeaderField("Location");
    				base = new URL(location);
    				next = new URL(base, redLoc);
    				location = next.toExternalForm();
    				continue;
    		}
    		break;
    	}
    	return conn.getURL();
    }
    
    public String getVersion() {
    	return this.version;
    }
    
    public File getJarLink() {
    	return this.jarLink;
    }
}