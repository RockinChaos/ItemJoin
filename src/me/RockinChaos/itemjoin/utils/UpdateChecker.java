package me.RockinChaos.itemjoin.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import me.RockinChaos.itemjoin.ItemJoin;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateChecker {
    
    private Plugin plugin;
    private URL filesFeed;
    public static ConsoleCommandSender Console = ItemJoin.pl.getServer().getConsoleSender();
    public static String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";
    
    private String version;
    private String link;
    private String jarLink;
    
    public UpdateChecker(Plugin plugin, String url){
            this.plugin = plugin;
            
            try{
                    this.filesFeed = new URL(url);
            }catch (MalformedURLException e){
                    e.printStackTrace();
            }
    }
    
    public boolean updateNeeded() {
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
             	    	 maxValue = Double.parseDouble(plugin.getDescription().getVersion().replace("-SNAPSHOT", ""));
             	    	 minValue = Double.parseDouble(this.version.replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", ""));
            	       }
            	       catch (NumberFormatException ex) {
            	    	   Console.sendMessage(Prefix + ChatColor.RED + "An error has occured when checking the plugin version!");
            	    	   Console.sendMessage(Prefix + ChatColor.RED + "Please contact the plugin developer!");
            	       }
                      if(!(minValue <= maxValue)) {
                    	  if (ItemJoin.pl.getDescription().getVersion().contains("-SNAPSHOT") || ItemJoin.pl.getDescription().getVersion().contains("-BETA") || ItemJoin.pl.getDescription().getVersion().contains("-ALPHA")) {
                    		  Console.sendMessage(Prefix + ChatColor.RED + "This is an outdated SNAPSHOT!");
                         return true;
                    	  }
                    	  else {
                    		  return true;
                    	  }
                    }
                      if((minValue == maxValue)) {
                	  if (ItemJoin.pl.getDescription().getVersion().contains("-SNAPSHOT") || ItemJoin.pl.getDescription().getVersion().contains("-BETA") || ItemJoin.pl.getDescription().getVersion().contains("-ALPHA")) {
                		  Console.sendMessage(Prefix + ChatColor.RED + "This is an outdated SNAPSHOT!");
                     return true;
                	  }
                      }
                      if(!(minValue >= maxValue)) {
                    	  if (ItemJoin.pl.getDescription().getVersion().contains("-SNAPSHOT") || ItemJoin.pl.getDescription().getVersion().contains("-BETA") || ItemJoin.pl.getDescription().getVersion().contains("-ALPHA")) {
                    	  Console.sendMessage(Prefix + ChatColor.GREEN + "You are running a SNAPSHOT!");
                    	  Console.sendMessage(Prefix + ChatColor.GREEN + "If you find any bugs please report them!");
                    	  } else {
                    		  return true;
                    	  }
                     }
            }catch (Exception e) {
                    e.printStackTrace();
            }
            
            return false;
    }

    public static void updateCheck() {
		UpdateChecker checker = new UpdateChecker(ItemJoin.pl, "http://dev.bukkit.org/server-mods/itemjoin/files.rss");
		if(ItemJoin.pl.getConfig().getBoolean("CheckforUpdates") == true) 
		{
			Console.sendMessage(Prefix + ChatColor.GREEN + "Checking for updates...");
              if (checker.updateNeeded())
                {
            	  Console.sendMessage(Prefix + ChatColor.RED + "Your current version: v" + ChatColor.RED + ItemJoin.pl.getDescription().getVersion());
            	  Console.sendMessage(Prefix + ChatColor.RED + "A new version of ItemJoin is available:" + ChatColor.GREEN + " v" +  checker.getVersion());
            	  Console.sendMessage(Prefix + ChatColor.GREEN + "Get it from: " + checker.getLink());
            	  Console.sendMessage(Prefix + ChatColor.GREEN + "Direct Link: " + checker.getJarLink());
              }
              else if(ItemJoin.pl.getConfig().getBoolean("CheckforUpdates") == true)
              {
            	  Console.sendMessage(Prefix + ChatColor.GREEN + "You are up to date!");
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
    
}