package me.RockinChaos.itemjoin.handlers;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.RockinChaos.itemjoin.ItemJoin;

public class ConfigHandler {
	
	public static YamlConfiguration loadItems;
	public static YamlConfiguration loadConfig;
	public static YamlConfiguration loadEnLang;
	public static YamlConfiguration loadFirstJoin;

    public static FileConfiguration loadConfig(String path)
    {
      File file = new File(ItemJoin.pl.getDataFolder(), path);
      if (!(file).exists()) {
        try
        {
        	ItemJoin.pl.saveResource(path, false);
        }
        catch (Exception e)
        {
          e.printStackTrace();
          ItemJoin.pl.getLogger().warning("Cannot save " + path + " to disk!");
          return null;
        }
      }
	return getPath(path, 1, file);
    }
    
    public static FileConfiguration getConfig(String path)
    {
      File file = new File(ItemJoin.pl.getDataFolder(), path);
      if (!(file).exists()) {
        try
        {
        	ItemJoin.pl.saveResource(path, false);
        }
        catch (Exception e)
        {
          e.printStackTrace();
          ItemJoin.pl.getLogger().warning("Cannot save " + path + " to disk!");
          return null;
        }
      }
	return getPath(path, 2, file);
    }
    
    public static YamlConfiguration getPath(String path, int integer, File file)
    {
        if(path.contains("items.yml")) {
        	if(integer == 1) {
          	    loadItems = YamlConfiguration.loadConfiguration(file);
        	}
           return loadItems;
        } else if(path.contains("config.yml")) {
        	if(integer == 1) {
        		loadConfig = YamlConfiguration.loadConfiguration(file);
        	}
           return loadConfig;
        } else if(path.contains("en-lang.yml")) {
        	if(integer == 1) {
        		loadEnLang = YamlConfiguration.loadConfiguration(file);
        	}
           return loadEnLang;
        } else if(path.contains("FirstJoin.yml")) {
        	if(integer == 1) {
        		loadFirstJoin = YamlConfiguration.loadConfiguration(file);
        	}
           return loadFirstJoin;
        }
		   return null;

    }
    
}
