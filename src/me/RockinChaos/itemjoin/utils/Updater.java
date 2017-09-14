package me.RockinChaos.itemjoin.utils;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Updater {
	
    public static ConsoleCommandSender Console = ItemJoin.pl.getServer().getConsoleSender();
    public static String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";
    private Plugin plugin;
    private URL filesFeed;
    private String version;
    private String link;
    private String jarLink;
    public static File AbsoluteFile;
        
        public Updater(Plugin plugin, String url){
                this.plugin = plugin;
                
                try{
                        this.filesFeed = new URL(url);
                }catch (MalformedURLException e){
                        e.printStackTrace();
                }
        }
        
        public boolean updateNeeded(CommandSender manager) {
        	CommandSender sender;
        	if (!(manager instanceof Player)) {
        		sender = Console;
        	} else {
        		sender = manager;
        	}
                try {
                        InputStream input = this.filesFeed.openConnection().getInputStream();
                        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
                        
                        Node latestFile = document.getElementsByTagName("item").item(0);
                        NodeList children = latestFile.getChildNodes();
                        
                        this.version = children.item(1).getTextContent().replaceAll("[a-zA-Z ]", "");
                        this.link = children.item(3).getTextContent();
                        
                        input.close();
                        
                        input = (new URL(this.link)).openConnection().getInputStream();
                        
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        String line;
                        
                        while ((line = reader.readLine()) != null){
                                if (line.trim().startsWith("<li class=\"user-action user-action-download\">")){
                                        this.jarLink = line.substring(line.indexOf("href=\"") + 6, line.lastIndexOf("\""));
                                        
                                        break;
                                }
                        }
                        
                        reader.close();
                        input.close();
                            double maxValue = 0;
                            double minValue = 0;
                 	       try {
                 	    	 maxValue = Double.parseDouble(plugin.getDescription().getVersion().replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", ""));
                 	    	 minValue = Double.parseDouble(this.version.replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", ""));
                	       }
                	       catch (NumberFormatException ex) {
                	    	   sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "An error has occurred when checking the plugin version!"));
                	    	   sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "Please contact the plugin developer!"));
                	    	   sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "Error Code; C139018"));
                	       }
                          if(!(minValue <= maxValue)) {
                        	  if (ItemJoin.pl.getDescription().getVersion().contains("-SNAPSHOT") || ItemJoin.pl.getDescription().getVersion().contains("-BETA") || ItemJoin.pl.getDescription().getVersion().contains("-ALPHA")) {
                        		  sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "This is an outdated SNAPSHOT!"));
                             return true;
                        	  }
                        	  else {
                        		  return true;
                        	  }
                        }
                          if((minValue == maxValue)) {
                    	  if (ItemJoin.pl.getDescription().getVersion().contains("-SNAPSHOT") || ItemJoin.pl.getDescription().getVersion().contains("-BETA") || ItemJoin.pl.getDescription().getVersion().contains("-ALPHA")) {
                    		  sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "This is an outdated SNAPSHOT!"));
                         return true;
                    	  }
                          }
                          if(!(minValue >= maxValue)) {
                        	  if (ItemJoin.pl.getDescription().getVersion().contains("-SNAPSHOT") || ItemJoin.pl.getDescription().getVersion().contains("-BETA") || ItemJoin.pl.getDescription().getVersion().contains("-ALPHA")) {
                        	  sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "You are running a SNAPSHOT!"));
                        	  sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "If you find any bugs please report them!"));
                        	  } else {
                        		  return true;
                        	  }
                         }
                }catch (Exception e) {
                        e.printStackTrace();
                }
                
                return false;
        }

        public static void forceUpdates(CommandSender manager) {
        	CommandSender sender;
        	if (!(manager instanceof Player)) {
        		sender = Console;
        	} else {
        		sender = manager;
        	}
        	Updater checker = new Updater(ItemJoin.pl, "https://dev.bukkit.org/server-mods/itemjoin/files.rss");
        	if(ItemJoin.pl.getConfig().getBoolean("CheckForUpdates") == false) {
        		sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "Check for Updates is disabled."));
        		sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "You must enable Check for Updates in the config to use auto update!"));
        	}
    		if(ItemJoin.pl.getConfig().getBoolean("CheckForUpdates") == true) 
    		{
    			sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "Checking for updates..."));
                  if (checker.updateNeeded(sender))
                    {
                	  sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "An update has been found!"));
                	  sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "Attempting to update from " + ChatColor.YELLOW + "v" + ItemJoin.pl.getDescription().getVersion() +  ChatColor.GREEN +  " to the new "  + ChatColor.YELLOW +  "v" + checker.getVersion()));
    				try {
          				URL fileUrl = new URL(checker.getJarLink());
          				final int fileLength = fileUrl.openConnection().getContentLength();
        				ReadableByteChannel rbc1 = Channels.newChannel(fileUrl.openStream());
        				FileOutputStream fos1 = new FileOutputStream(AbsoluteFile);
		                int count;
		                long downloaded = 0;
				        BufferedInputStream in = null;
		                in = new BufferedInputStream(fileUrl.openStream());
		                final byte[] data = new byte[fileLength];
						fos1.getChannel().transferFrom(rbc1, 0, 1 << 24);
		                while ((count = in.read(data, 0, fileLength)) != -1) {
			                downloaded += count;
			                fos1.write(data, 0, count);
			                final int percent = (int) ((downloaded * 100) / fileLength);
			                if (((percent % 10) == 0)) {
			                	sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.YELLOW + "Downloading update " + "v" + checker.getVersion() + ": " + percent + "% of " + fileLength + " bytes."));
			                }
		                }
						fos1.close();
						sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "has successfully been updated to v" +  checker.getVersion()));
						sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "You must restart your server for this to take affect."));
					} catch (IOException e) {
						sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "An error has occurred while trying to update the plugin ItemJoin."));
						sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "Please try again later, if you continue to see this please contact the plugin developer."));
						e.printStackTrace();
					}
                  }
                  else if(ItemJoin.pl.getConfig().getBoolean("CheckForUpdates") == true)
                  {
                	  sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "You are up to date!"));
               }
            }
    }

        public String getVersion() {
                return this.version;
        }
        
        public String getLink() {
                return this.link;
        }
        
        public String getJarLink() {
                return this.jarLink;
        }

    public static Boolean checkUpdates(CommandSender manager) {
    	CommandSender sender;
    	if (!(manager instanceof Player)) {
    		sender = Console;
    	} else {
    		sender = manager;
    	}
    	if (ItemJoin.pl.getConfig().getBoolean("CheckForUpdates") == true) {
    		sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "Checking for updates..."));
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + 12661).getBytes("UTF-8"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String version = reader.readLine();
            reader.close();
            if (version.length() <= 7) {
            	double webVersion = Double.parseDouble(version.replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", "").replace(".", ""));
            	double currentVersion = Double.parseDouble(ItemJoin.pl.getDescription().getVersion().replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", "").replace(".", ""));
            	String thisVersion = ItemJoin.pl.getDescription().getVersion();
            	if (webVersion == currentVersion) {
                	if (thisVersion.contains("-SNAPSHOT") 
                			|| thisVersion.contains("-BETA") 
                			|| thisVersion.contains("-ALPHA")) {
                	sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "This is an outdated SNAPSHOT!"));
              	    sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "Your current version: v" + ChatColor.RED + thisVersion));
              	    sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "A new version of ItemJoin is available: " + ChatColor.GREEN +  version));
            	    sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "Get it from: https://www.spigotmc.org/resources/itemjoin.12661/history"));
            	    sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "If you wish to auto update, please type /ItemJoin AutoUpdate"));
            	    
            	    if(ServerHandler.hasViableUpdate()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp()) {
                            player.sendMessage(Prefix + ChatColor.YELLOW + "An update has been found for ItemJoin.");
                            player.sendMessage(Prefix + ChatColor.YELLOW + "Please update to the latest version " + version + "!");
                        }
                    }
            	    }
            	    return true;
                	} else { 
                    sender.sendMessage(Prefix + ChatColor.GREEN + "You are up to date!");
                    return false;
                	}
                	} else if (!(webVersion <= currentVersion)) {
                	  sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "Your current version: v" + ChatColor.RED + thisVersion));
                  	  sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "A new version of ItemJoin is available: " + ChatColor.GREEN +  version));
                	  sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "Get it from: https://www.spigotmc.org/resources/itemjoin.12661/history"));
                	  sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "If you wish to auto update, please type /ItemJoin AutoUpdate"));
                	  
                	  if(ServerHandler.hasViableUpdate()) {
                      for (Player player : Bukkit.getOnlinePlayers()) {
                          if (player.isOp()) {
                        	  player.sendMessage(Prefix + ChatColor.YELLOW + "An update has been found for ItemJoin.");
                              player.sendMessage(Prefix + ChatColor.YELLOW + "Please update to the latest version " + version + "!");
                          }
                      }
                	  }
                	  return true;
                } else if (!(webVersion >= currentVersion)) {
                	if (thisVersion.contains("-SNAPSHOT") 
                			|| thisVersion.contains("-BETA") 
                			|| thisVersion.contains("-ALPHA")) {
                	sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "You are running a SNAPSHOT!"));
                  	sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "If you find any bugs please report them!"));
                  	return false;
                	} else {
                    	sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "You are running a version of ItemJoin that is greater than the current posted!"));
                  	    sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "Your current version: v" + ChatColor.RED + thisVersion));
                  	    sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "The posted version of ItemJoin: " + ChatColor.GREEN +  version));
                	    sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "Get it from: https://www.spigotmc.org/resources/itemjoin.12661/history"));
                	    sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.GREEN + "If you wish to auto update, please type /ItemJoin AutoUpdate"));
                	    
                	    if(ServerHandler.hasViableUpdate()) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.isOp()) {
                                player.sendMessage(Prefix + ChatColor.YELLOW + "An update has been found for ItemJoin.");
                                player.sendMessage(Prefix + ChatColor.YELLOW + "Please update to the latest version " + version + "!");
                            }
                        }
                	    }
                	    return true;
                	}
                }

            }
        } catch (Exception ex) {
        	sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "An error has occured when checking the plugin version!"));
        	sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "Please contact the plugin developer!"));
        	sender.sendMessage(ServerHandler.StripLogColors(sender, Prefix + ChatColor.RED + "Error is " + ex + " C13904"));
        	return false;
        }
    }
		return false;
  }
}