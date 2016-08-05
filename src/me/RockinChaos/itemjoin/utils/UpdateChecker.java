package me.RockinChaos.itemjoin.utils;
import me.RockinChaos.itemjoin.ItemJoin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class UpdateChecker {
	
	public static double ver;
	
    public static void forceUpdates(CommandSender sender) {
    	if (!(sender instanceof Player)) {
    		sender = ItemJoin.pl.Console;
    	}
    	if(ItemJoin.pl.getConfig().getBoolean("CheckforUpdates") == false) {
    		sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Check for Updates is disabled.");
    		sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "You must enable Check for Updates in the config to use Auto-Update!");
    	}
		if(ItemJoin.pl.getConfig().getBoolean("CheckforUpdates") == true) {
			sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "Checking for updates...");
		try {
	        URL spigot;
			spigot = new URL("http://www.spigotmc.org/api/general.php");
	        HttpURLConnection con = (HttpURLConnection) spigot.openConnection();
	        con.setDoOutput(true);
	        con.setRequestMethod("POST");
	        con.getOutputStream().write("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=12661".getBytes());
	        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        String version = reader.readLine().replaceAll("[a-z]", "");
	        String thisVersion = ItemJoin.pl.getDescription().getVersion().replaceAll("[a-z]", "");
	        if(Double.parseDouble(version) > Double.parseDouble(thisVersion)) {
	        	File update = new File("plugins" + File.separator + "update");
	            if(!update.exists()) {
	                update.mkdir();
	            }
	            ReadableByteChannel in = Channels.newChannel(new URL("https://api.spiget.org/v1/resources/12661/download").openStream());
	            FileOutputStream out = new FileOutputStream(new File(update, "ItemJoin.jar"));
	            FileChannel channel = out.getChannel();
	            channel.transferFrom(in, 0, Long.MAX_VALUE);
	            out.close();
	            reader.close();
	      	    sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "Your current version: v" + ChatColor.GREEN + thisVersion);
	      	    sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "New version downloaded: v" + ChatColor.RED + version);
	      	    sender.sendMessage(ChatColor.GREEN + "[Success] " + ChatColor.RED +"You must restart your server for this to take affect.");
	             } else if (Double.parseDouble(version) == Double.parseDouble(thisVersion)) {
	     			sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "There is no update available!");
	    			sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "You are up to date!");
	             }
          } catch (Exception ex) {
    	  sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Failed to Auto-Update!!");
     	  sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Please contact the plugin developer!");
        }
	  }
    }

    public static Boolean checkUpdates(CommandSender sender) {
    	if (!(sender instanceof Player)) {
    		sender = ItemJoin.pl.Console;
    	}
    	if (ItemJoin.pl.getConfig().getBoolean("CheckforUpdates") == true) {
    		sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "Checking for updates...");
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + 12661).getBytes("UTF-8"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String version = reader.readLine();
            reader.close();
            if (version.length() <= 7) {
            	double webVersion = Double.parseDouble(version.replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", ""));
            	double currentVersion = Double.parseDouble(ItemJoin.pl.getDescription().getVersion().replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", ""));
            	ver = webVersion;
            	String thisVersion = ItemJoin.pl.getDescription().getVersion();
            	if (webVersion == currentVersion) {
                	if (thisVersion.contains("-SNAPSHOT") 
                			|| thisVersion.contains("-BETA") 
                			|| thisVersion.contains("-ALPHA")) {
                	sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "This is an outdated SNAPSHOT!");
              	    sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Your current version: v" + ChatColor.RED + thisVersion);
              	    sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "A new version of ItemJoin is available: " + ChatColor.GREEN +  version);
            	    sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "Get it from: https://www.spigotmc.org/resources/itemjoin.12661/history");
            	    sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "If you wish to auto-update, please type /ItemJoin Update");
            	    
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', ""+ "&7There is a resource update avaliable for &cAdvanceTab&7. Please update to recieve latest version."));
                            sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.YELLOW + "An update has been found for ItemJoin.");
                            sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.YELLOW + "Please update to the latest version v" + version + "!");
                        }
                    }
            	    return true;
                	} else { 
                    sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "You are up to date!");
                    return false;
                	}
                	} else if (!(webVersion <= currentVersion)) {
                	  sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Your current version: v" + ChatColor.RED + thisVersion);
                  	  sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "A new version of ItemJoin is available: " + ChatColor.GREEN +  version);
                	  sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "Get it from: https://www.spigotmc.org/resources/itemjoin.12661/history");
                	  sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "If you wish to auto-update, please type /ItemJoin AutoUpdate");
                	  
                      for (Player player : Bukkit.getOnlinePlayers()) {
                          if (player.isOp()) {
                              player.sendMessage(ChatColor.translateAlternateColorCodes('&', ""+ "&7There is a resource update avaliable for &cAdvanceTab&7. Please update to recieve latest version."));
                              sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.YELLOW + "An update has been found for ItemJoin.");
                              sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.YELLOW + "Please update to the latest version v" + version + "!");
                          }
                      }
                	  return true;
                } else if (!(webVersion >= currentVersion)) {
                	if (thisVersion.contains("-SNAPSHOT") 
                			|| thisVersion.contains("-BETA") 
                			|| thisVersion.contains("-ALPHA")) {
                	sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "You are running a SNAPSHOT!");
                  	sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "If you find any bugs please report them!");
                  	return false;
                	} else {
                    	sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "You are running a version of ItemJoin that is greater than the current posted!");
                  	    sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Your current version: v" + ChatColor.RED + thisVersion);
                  	    sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "The posted version of ItemJoin: " + ChatColor.GREEN +  version);
                	    sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.GREEN + "Get it from: https://www.spigotmc.org/resources/itemjoin.12661/history");
                	    
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.isOp()) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', ""+ "&7There is a resource update avaliable for &cAdvanceTab&7. Please update to recieve latest version."));
                                sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.YELLOW + "An update has been found for ItemJoin.");
                                sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.YELLOW + "Please update to the latest version v" + version + "!");
                            }
                        }
                	    return true;
                	}
                }

            }
        } catch (Exception ex) {
        	sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "An error has occured when checking the plugin version!");
        	sender.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Please contact the plugin developer!");
        	return false;
        }
    }
		return false;
  }
}