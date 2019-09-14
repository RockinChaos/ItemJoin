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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

public class UpdateHandler {
	
    private boolean updatesAllowed = ConfigHandler.getConfig("config.yml").getBoolean("General.CheckforUpdates");
    private File jarLink;
    private final String AUTOQUERY = "projects/itemjoin/files/latest";
    private final String AUTOHOST = "https://dev.bukkit.org/";
    private final String HOST = "https://www.spigotmc.org/api/general.php";
    private final int PROJECTID = 12661;
    private final String KEY = ("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + PROJECTID);
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
       this.checkUpdates(ItemJoin.getInstance().getServer().getConsoleSender());
    }
    
    /**
     * If the spigotmc host has an available update, redirects to download the jar file from dev-bukkit.
     * Downloads and write the new data to the plugin jar file.
     */
    public void forceUpdates(CommandSender sender) {
    	if (this.updateNeeded(sender)) {
    		ServerHandler.sendMessage(sender, "&aAn update has been found!");
    		ServerHandler.sendMessage(sender, "&aAttempting to update from " + "&ev" + this.localeVersionRaw + " &ato the new "  + "&ev" + this.latestVersionRaw);
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
    					ServerHandler.sendMessage(sender, progressBar.substring(0, currentProgress + 2) + "&c" + progressBar.substring(currentProgress + 2));
    				}
    			}
    			bout.close(); in.close(); fos.close();
    			ServerHandler.sendMessage(sender, "&aSuccessfully updated to v" + this.latestVersionRaw + "!");
    			ServerHandler.sendMessage(sender, "&aYou must restart your server for this to take affect.");
    		} catch (Exception e) {
    			ServerHandler.sendMessage(sender, "&cAn error has occurred while trying to update the plugin ItemJoin.");
    			ServerHandler.sendMessage(sender, "&cPlease try again later, if you continue to see this please contact the plugin developer.");
    			ServerHandler.sendDebugTrace(e);
    		}
    	} else if (!this.updatesAllowed) {
    		ServerHandler.sendMessage(sender, "&cUpdate checking is currently disabled in the config.yml");
    		ServerHandler.sendMessage(sender, "&cIf you wish to use the auto update feature, you will need to enable it.");
        }
    }
    
    /**
     * Checks to see if an update is required, notifying the console window and online op players.
     */
    public void checkUpdates(CommandSender sender) {
    	if (this.updateNeeded(sender)) {
    		if (this.betaVersion) {
    			ServerHandler.sendMessage(sender, "&cYour current version: &bv" + this.localeVersionRaw + "-SNAPSHOT");
    			ServerHandler.sendMessage(sender, "&cThis &bSNAPSHOT &cis outdated and a release version is now available.");
    		} else {
    			ServerHandler.sendMessage(sender, "&cYour current version: &bv" + this.localeVersionRaw);
    		}
    		ServerHandler.sendMessage(sender, "&cA new version is available: " + "&av" + this.latestVersionRaw);
    		ServerHandler.sendMessage(sender, "&aGet it from: https://www.spigotmc.org/resources/itemjoin.12661/history");
    		ServerHandler.sendMessage(sender, "&aIf you wish to auto update, please type /ItemJoin AutoUpdate");
    		this.sendNotifications();
    	} else {
    		if (this.betaVersion) {
    			ServerHandler.sendMessage(sender, "&aYou are running a SNAPSHOT!");
    			ServerHandler.sendMessage(sender, "&aIf you find any bugs please report them!");
    		}
    		ServerHandler.sendMessage(sender, "&aYou are up to date!");
    	}
    }
    
    /**
     * Directly checks to see if the spigotmc host has an update available.
     */
    private Boolean updateNeeded(CommandSender sender) {
    	if (this.updatesAllowed) {
    		ServerHandler.sendMessage(sender, "&aChecking for updates...");
    		try {
    			HttpURLConnection con = (HttpURLConnection) new URL(this.HOST).openConnection();
    			con.setDoOutput(true);
    			con.setRequestMethod("POST");
    			con.getOutputStream().write(this.KEY.getBytes("UTF-8"));
    			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
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
    			ServerHandler.sendMessage(sender, "&cAn error has occured when checking the plugin version!");
    			ServerHandler.sendMessage(sender, "&cPlease contact the plugin developer!");
    			ServerHandler.sendDebugTrace(e);
    			return false;
    		}
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
    						ServerHandler.sendPlayerMessage(((Player) objPlayer), "&eAn update has been found!");
    						ServerHandler.sendPlayerMessage(((Player) objPlayer), "&ePlease update to the latest version: v" + this.latestVersionRaw);
    					}
    				}
    			}
    		} else {
    			playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    			for (Player objPlayer: playersOnlineOld) {
    				if (objPlayer.isOp()) {
						ServerHandler.sendPlayerMessage(objPlayer, "&eAn update has been found!");
						ServerHandler.sendPlayerMessage(objPlayer, "&ePlease update to the latest version: v" + this.latestVersionRaw);
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