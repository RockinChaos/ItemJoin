package me.xsilverslayerx.itemjoin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import me.clip.placeholderapi.PlaceholderAPI;
import me.xsilverslayerx.itemjoin.utils.Commands;
import me.xsilverslayerx.itemjoin.utils.Listeners;
import me.xsilverslayerx.itemjoin.utils.UpdateChecker;
import me.xsilverslayerx.itemjoin.utils.multiverse.MvListeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.listeners.MVPlayerListener;

  public class ItemJoin
    extends JavaPlugin
  {
	public static ItemJoin pl;
	static String bukkitPackage;
	static String nmsPackage;
	static String version;
    public List<String> worlds;
    public List<String> clearonworldchange;
    public List<String> giveonworldchange;
    public List<String> giveonrespawn;
    public List<String> clearonjoin;
    public List<String> preventdeathdrops;
    public List<String> preventinventorymodify;
    public List<String> preventpickups;
    public List<String> preventijplacement;
    public List<String> preventdrops;
    public Map<String, ItemStack[]> items = new HashMap<String, ItemStack[]>();
    public MVPlayerListener listen;
    public Map<String, String> mvplayermap;
    protected Logger log;
    public boolean hasMultiverse = false;
    public boolean hasInventories = false;
    public boolean hasPlaceholderAPI = false;
    public Player PlayerJoin;
    public String PlayerJoin2;
    public String isSkullOwner;
    ArrayList<String> enchantlist = new ArrayList<String>();
    
    public void onEnable()
    {
	   pl = this;
	  loadSpecialConfig("items.yml");
	  getSpecialConfig("items.yml").options().copyDefaults(false);
	  if(ItemJoin.pl.getConfig().getBoolean("First-Join-Only") == true) {
	  pl.loadSpecialConfig("FirstJoin.yml");
	  pl.getSpecialConfig("FirstJoin.yml").options().copyDefaults(false);
	  }
  	  saveDefaultConfig();
      getConfig().options().copyDefaults(false);
      getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Enabled!");
	  pl.getCommand("itemjoin").setExecutor(new Commands());
	  pl.getCommand("ij").setExecutor(new Commands());
      if (this.getServer().getPluginManager().getPlugin("Multiverse-Core") != null && ItemJoin.pl.getConfig().getBoolean("Multiverse-Core") == true) {
    	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Hooked into Multiverse-Core!");
    	  if (this.getServer().getPluginManager().getPlugin("Multiverse-Core").getDescription().getVersion().contains("2.5")){ //version check//
    	  pl.hasMultiverse = true;
    	  pl.getServer().getPluginManager().registerEvents(new MvListeners(),this); 
    	  } else if (this.getServer().getPluginManager().getPlugin("Multiverse-Core").getDescription().getVersion().contains("2.6")){
        	  pl.hasMultiverse = true;
          pl.getServer().getPluginManager().registerEvents(new MvListeners(),this);    		  
    	  } else {
    		  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "You are running Multiverse Version " + ChatColor.YELLOW + this.getServer().getPluginManager().getPlugin("Multiverse-Core").getDescription().getVersion().toString() + ChatColor.RED + ".");
    		  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "This version of Multiverse is outdated!");
    		  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Please download v2.5 or above for Multiverse Support.");
              getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Get it from: http://dev.bukkit.org/bukkit-plugins/multiverse-core/files/28-2-5-b699-beta/");
    		  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Unhooked from Multiverse-Core!");
    		  pl.getServer().getPluginManager().registerEvents(new Listeners(),this); 
    	  }
      } else if (ItemJoin.pl.getConfig().getBoolean("Multiverse-Core") == true) {
    	  pl.getServer().getPluginManager().registerEvents(new Listeners(),this); 
    	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Could not find Multiverse-Core.");
      } else {
    		  pl.getServer().getPluginManager().registerEvents(new Listeners(),this); 
    	  }
      if (this.getServer().getPluginManager().getPlugin("Multiverse-Inventories") != null && ItemJoin.pl.getConfig().getBoolean("Multiverse-Inventories") == true) {
    	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Hooked into Multiverse-Inventories!");
    	  pl.hasInventories = true;
      } else if (ItemJoin.pl.getConfig().getBoolean("Multiverse-Inventories") == true) {
    	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Could not find Multiverse-Inventories.");
      } else {
      }
		 if (ItemJoin.pl.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && ItemJoin.pl.getConfig().getBoolean("PlaceholderAPI") == true) {
	    	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Hooked into PlaceholderApi!");
	    	  pl.hasPlaceholderAPI = true;
			 }
		 else if (ItemJoin.pl.getConfig().getBoolean("PlaceholderAPI") == true) {
			 getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Could not find PlaceholderApi.");
		 }
		 else {
		 }
      if (this.getServer().getPluginManager().getPlugin("Multiverse-Core") != null && ItemJoin.pl.getConfig().getBoolean("Multiverse-Core") == true) {
      } else if (ItemJoin.pl.getConfig().getBoolean("Multiverse-Core") == true){
    	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Some world support will NOT be provided.");
      } else {
      }
        getWorldNames();
		UpdateChecker checker = new UpdateChecker(this, "http://dev.bukkit.org/server-mods/itemjoin/files.rss");
		if(getConfig().getBoolean("CheckforUpdates") == true) 
		{
			getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Checking for updates...");
              if (checker.updateNeeded())
                {
      			  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Your current version: v" + ChatColor.RED + getDescription().getVersion());
                  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "A new version of ItemJoin is available:" + ChatColor.GREEN + " v" +  checker.getVersion());
                  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Get it from: " + checker.getLink());
                  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Direct Link: " + checker.getJarLink());
              }
              else if(getConfig().getBoolean("CheckforUpdates") == true)
              {
            	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "You are up to date!");
           }
        }
}

    public void onDisable()
    {
    	getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Disabled!");
}
	
// Items.yml File Do Stuff //

    public FileConfiguration loadSpecialConfig(String path) {
        File file;
        if (!path.endsWith(".yml")) {
            path = String.valueOf(path) + ".yml";
        }
        if (!(file = new File(pl.getDataFolder(), path)).exists()) {
            try {
                pl.saveResource(path, false);
            }
            catch (Exception e) {
                e.printStackTrace();
                pl.getLogger().warning("Cannot save " + path + " to disk!");
                return null;
            }
      }
     YamlConfiguration config = YamlConfiguration.loadConfiguration((File)file);
     return config;
}

   public FileConfiguration getSpecialConfig(String path) {
	  File file = new File(pl.getDataFolder(), String.valueOf(path));
          if (!file.exists()) {
                file.mkdir();
      }
     return YamlConfiguration.loadConfiguration(file);
}
   
   public void loadItemsConfigSetup()
   {
     for (int i = 0; i < this.worlds.size(); i++)
     {
         String world = (String)this.worlds.get(i);
       ItemStack[] tempitems = new ItemStack[103];
       for (int j = 0; j < 103; j++) {
         tempitems[j] = null;
       }
       for (int j = 1; j <= 103; j++)
       {
         int dataValue = pl.getSpecialConfig("items.yml").getInt(world + ".items." + j + ".data-value");
         Material tempmat = Material.getMaterial(pl.getSpecialConfig("items.yml").getString(world + ".items." + j + ".id"));
         if (tempmat == null)
         {
           tempitems[(j - 1)] = null;
         }
         else
         {
           tempitems[(j - 1)] = new ItemStack(tempmat, pl.getSpecialConfig("items.yml").getInt(world + ".items." + j + ".count", 1),(short)dataValue);
           ItemMeta tempmeta = tempitems[(j - 1)].getItemMeta();
           if (pl.getSpecialConfig("items.yml").getStringList(world + ".items." + j + ".lore") != null)
           {
             List<String> templist = pl.getSpecialConfig("items.yml").getStringList(world + ".items." + j + ".lore");
             List<String> templist2 = new ArrayList<String>();
             for (int k = 0; k < templist.size(); k++)
             {
               String name = (String)templist.get(k);
               name = translateCodes(name);
               templist2.add(name);
             }
             tempmeta.setLore(templist2);
           }
           if (!pl.getSpecialConfig("items.yml").getString(world + ".items." + j + ".name", "none").equalsIgnoreCase("none"))
           {
             String name = pl.getSpecialConfig("items.yml").getString(world + ".items." + j + ".name");
             name = translateCodes(name);
             tempmeta.setDisplayName(name);
           }
           if (pl.getSpecialConfig("items.yml").getString(world + ".items." + j + ".skull-owner") != null  && Material.getMaterial(pl.getSpecialConfig("items.yml").getString(world + ".items." + j + ".id")) == Material.SKULL_ITEM)
           {
               String owner = pl.getSpecialConfig("items.yml").getString(world + ".items." + j + ".skull-owner");
               owner = translateCodes(owner);
               isSkullOwner = owner;
              ((SkullMeta) tempmeta).setOwner(owner);
           }
           tempitems[(j - 1)].setItemMeta(tempmeta);
           if (pl.getSpecialConfig("items.yml").getStringList(world + ".items." + j + ".enchantment") != null)
           {
        	   List<String> enchantments = pl.getSpecialConfig("items.yml").getStringList(world + ".items." + j + ".enchantment");
        	   for (String enchantment: enchantments) {
        	       String[] parts = enchantment.split(":");
        	       String name = parts[0];
        	       int level = 1;
        	       try {
        	           level = Integer.parseInt(parts[1]);
        	       }
        	       catch (NumberFormatException ex) {
        	    	   getServer().getConsoleSender().sendMessage("An error occurred in the config, " + parts[1] + " is not a number and a number was expected!");
        	    		  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "An error occurred in the config, " + ChatColor.GREEN + parts[1] + ChatColor.RED + " is not a number and a number was expected!");
        	    		  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + ChatColor.GREEN + "Enchantment: " + parts[0] + " will now be enchanted by level 1.");
        	       }
        	       tempitems[(j - 1)].addUnsafeEnchantment(Enchantment.getByName(name), level);
        	   }
       		}
         }
       }
       this.items.put(world, tempitems);
   }
}
   
   public void getWorldNames()
   {
	   this.worlds = getSpecialConfig("items.yml").getStringList("worldlist");
	   this.clearonjoin = getConfig().getStringList("clear-on-join-worldlist");
	   this.clearonworldchange = getConfig().getStringList("clear-on-world-change-worldlist");
	   this.giveonworldchange = getConfig().getStringList("give-on-world-change-worldlist");
	   this.giveonrespawn = getConfig().getStringList("give-on-respawn-worldlist");
	   this.preventdeathdrops = getConfig().getStringList("prevent-death-drops-worldlist");
	   this.preventinventorymodify = getConfig().getStringList("prevent-inventory-modify-worldlist");
	   this.preventpickups = getConfig().getStringList("prevent-pickups-worldlist");
	   this.preventdrops = getConfig().getStringList("prevent-drops-worldlist");
     for (int i = 0; i < this.worlds.size(); i++)
     {
       String world = (String)this.worlds.get(i);
       getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Cached " + ChatColor.YELLOW + world + ChatColor.GREEN + " from the items.yml!");
     }
}

// translateCodes //
    
    public String translateCodes(String name)
       {
	     name = name.replace("%player%", PlayerJoin2);
         name = ChatColor.translateAlternateColorCodes('&', name).toString();
		     if (hasPlaceholderAPI = true) {
		        name = PlaceholderAPI.setPlaceholders(PlayerJoin, name);
		   }
      return name;
    }
}