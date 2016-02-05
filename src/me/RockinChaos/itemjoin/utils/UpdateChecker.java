package me.RockinChaos.itemjoin.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.plugin.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateChecker {
    
    private Plugin plugin;
    private URL filesFeed;
    
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
                    
                    if (!plugin.getDescription().getVersion().equals(this.version)) {
                            return true;
                    }
            }catch (Exception e) {
                    e.printStackTrace();
            }
            
            return false;
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