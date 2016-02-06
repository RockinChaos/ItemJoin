package me.RockinChaos.itemjoin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import me.RockinChaos.itemjoin.utils.Commands;
import me.RockinChaos.itemjoin.utils.Listeners;
import me.RockinChaos.itemjoin.utils.UpdateChecker;
import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

  public class ItemJoin
    extends JavaPlugin
  {
	public static ItemJoin pl;
	static String bukkitPackage;
	static String nmsPackage;
	static String version;
    public List<String> worlds;
    public Map<String, ItemStack[]> items = new HashMap<String, ItemStack[]>();
    protected Logger log;
    public static boolean hasMultiverse;
    public static boolean hasInventories;
    public static boolean hasPlaceholderAPI;
    public Player PlayerJoin;
    public String PlayerJoin2;
    public static String secretMsg = "ItemJoin";
    
    public void onEnable()
    {
	   pl = this;
	  loadSpecialConfig("items.yml");
	  getSpecialConfig("items.yml").options().copyDefaults(false);
        worlds = pl.getSpecialConfig("items.yml").getStringList("world-list");
        for(int i = 0; i < worlds.size(); i++) {
        	worlds.set(i, worlds.get(i).toLowerCase());
        }

	  if (ItemJoin.pl.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled") == true) {
	  loadSpecialConfig("FirstJoin.yml");
	  getSpecialConfig("FirstJoin.yml").options().copyDefaults(false);
	  }
  	  saveDefaultConfig();
      getConfig().options().copyDefaults(false);
      getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Enabled!");
	  getCommand("itemjoin").setExecutor(new Commands());
	  getCommand("ij").setExecutor(new Commands());
	  getServer().getPluginManager().registerEvents(new Listeners(),this);
	  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && ItemJoin.pl.getConfig().getBoolean("PlaceholderAPI") == true) {
    	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Hooked into PlaceholderAPI!");
    	  hasPlaceholderAPI = true;
		 } else if (ItemJoin.pl.getConfig().getBoolean("PlaceholderAPI") == true) {
		 getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Could not find PlaceholderAPI.");
		 hasPlaceholderAPI = false;
		 }
	  if (this.getServer().getPluginManager().getPlugin("Multiverse-Core") != null && ItemJoin.pl.getConfig().getBoolean("Multiverse-Core") == true) {
    	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Hooked into Multiverse-Core!");
    	  hasMultiverse = true;
      } else if (ItemJoin.pl.getConfig().getBoolean("Multiverse-Core") == true) {
    	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Could not find Multiverse-Core.");
    	  hasMultiverse = false;
      }
	  if (this.getServer().getPluginManager().getPlugin("Multiverse-Inventories") != null && ItemJoin.pl.getConfig().getBoolean("Multiverse-Inventories") == true) {
    	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Hooked into Multiverse-Inventories!");
    	  hasInventories = true;
      } else if (ItemJoin.pl.getConfig().getBoolean("Multiverse-Inventories") == true) {
    	  hasInventories = false;
    	  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Could not find Multiverse-Inventories.");
      }
	     for (int i = 0; i < this.worlds.size(); i++)
	     {
	       String world = (String)this.worlds.get(i).toLowerCase();
	       getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Cached " + ChatColor.YELLOW + world + ChatColor.GREEN + " from the items.yml!");
	     }
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
   
   public void CacheItems()
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
             tempmeta.setDisplayName(name + encodeItemData(secretMsg));
           } else {
        	   String lookup = getName(tempitems[(j - 1)]);
        	   String name = translateCodes("&f" + lookup + encodeItemData(secretMsg));
        	   tempmeta.setDisplayName(name);
           }
           
           if (pl.getSpecialConfig("items.yml").getString(world + ".items." + j + ".skull-owner") != null  && Material.getMaterial(pl.getSpecialConfig("items.yml").getString(world + ".items." + j + ".id")) == Material.SKULL_ITEM)
           {
               String owner = pl.getSpecialConfig("items.yml").getString(world + ".items." + j + ".skull-owner");
               owner = translateCodes(owner);
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
       this.items.put(world.toLowerCase() + PlayerJoin2, tempitems);
   }
}
   
   public static String encodeItemData(String str){
   try {
       String hiddenData = "";
       for(char c : str.toCharArray()){
           hiddenData += "§" + c;
       }
       return hiddenData;
   }catch (Exception e){
       e.printStackTrace();
       return null;
     }
   }

   public static String decodeItemData(String str){
   try {
       String[] hiddenData = str.split("(?:\\w{2,}|\\d[0-9A-Fa-f])+");
       String returnData = "";
       if(hiddenData == null){
           hiddenData = str.split("§");
           for(int i = 0; i < hiddenData.length; i++){
               returnData += hiddenData[i];
           }
           return returnData;
       }else{
           String[] d = hiddenData[hiddenData.length-1].split("§");
           for(int i = 1; i < d.length; i++){
               returnData += d[i];
           }
           return returnData;
       }

   }catch (Exception e){
       e.printStackTrace();
       return null;
     }
   }

   public static String getName(ItemStack stack) {
	   return CraftItemStack.asNMSCopy(stack).getName();
	   }
   
   public static boolean isInt(String s) {
	    try {
	        Integer.parseInt(s);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
   
 // translateCodes //
    
    public String translateCodes(String name)
       {
	     name = name.replace("%player%", PlayerJoin2);
         name = ChatColor.translateAlternateColorCodes('&', name).toString();
		 if (ItemJoin.pl.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null && ItemJoin.pl.getConfig().getBoolean("PlaceholderAPI") == true) {
		   name = PlaceholderAPI.setPlaceholders(PlayerJoin, name);
		 }
      return name;
    }
}