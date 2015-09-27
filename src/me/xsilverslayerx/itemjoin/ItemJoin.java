package me.xsilverslayerx.itemjoin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import me.xsilverslayerx.itemjoin.utils.Commands;
import me.xsilverslayerx.itemjoin.utils.Listeners;
import me.xsilverslayerx.itemjoin.utils.UpdateChecker;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
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
    public List<String> preventdrops;
    public Map<String, ItemStack[]> items = new HashMap<String, ItemStack[]>();
    public MVPlayerListener listen;
    public Map<String, String> playermap;
    protected Logger log;
    
    public void onEnable()
    {
		pl = this;
	  loadItemsConfig("items.yml");
      getItemsConfig().options().copyDefaults(false);
      loadItemsConfigSetup();
  	  saveDefaultConfig();
      getConfig().options().copyDefaults(false);
      getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Enabled!");
	  pl.getCommand("itemjoin").setExecutor(new Commands());
	  pl.getCommand("ij").setExecutor(new Commands());
	  pl.getServer().getPluginManager().registerEvents(new Listeners(),this); 
      MultiverseCore multiverseCore = (MultiverseCore)getServer().getPluginManager().getPlugin("Multiverse-Core");
      this.listen = multiverseCore.getPlayerListener();
		UpdateChecker checker = new UpdateChecker(this, "http://dev.bukkit.org/server-mods/itemjoin/files.rss");
		if(getConfig().getBoolean("CheckforUpdates") == true) 
		{
			getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Checking for updates...");
              if (checker.updateNeeded())
                {
      			  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Your current version: v" + ChatColor.RED + getDescription().getVersion());
                  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "A new version of ItemJoin is available: v" + ChatColor.GREEN +  checker.getVersion());
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

    public static FileConfiguration loadItemsConfig(String path) {
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

   public static FileConfiguration getItemsConfig() {
	  File file = new File(pl.getDataFolder(), "items.yml");
          if (!file.exists()) {
                file.mkdir();
      }
     return YamlConfiguration.loadConfiguration(file); // file found, load into config and return it.
}
   
   public void loadItemsConfigSetup()
   {
     this.worlds = getItemsConfig().getStringList("worldlist");
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
       getLogger().info(world);
       ItemStack[] tempitems = new ItemStack[36];
       for (int j = 0; j < 36; j++) {
         tempitems[j] = null;
       }
       for (int j = 1; j <= 36; j++)
       {
         Material tempmat = Material.getMaterial(getItemsConfig().getString(world + ".items." + j + ".id"));
         if (tempmat == null)
         {
           tempitems[(j - 1)] = null;
         }
         else
         {
           tempitems[(j - 1)] = new ItemStack(tempmat, getItemsConfig().getInt(world + ".items." + j + ".count", 1));
           ItemMeta tempmeta = tempitems[(j - 1)].getItemMeta();
           if (getItemsConfig().getStringList(world + ".items." + j + ".lore") != null)
           {
             List<String> templist = getItemsConfig().getStringList(world + ".items." + j + ".lore");
             List<String> templist2 = new ArrayList<String>();
             for (int k = 0; k < templist.size(); k++)
             {
               String name = (String)templist.get(k);
               name = colorCodes(name);
               templist2.add(name);
             }
             tempmeta.setLore(templist2);
           }
           if (!getItemsConfig().getString(world + ".items." + j + ".name", "none").equalsIgnoreCase("none"))
           {
             String name = getItemsConfig().getString(world + ".items." + j + ".name");
             name = colorCodes(name);
             tempmeta.setDisplayName(name);
           }
           tempitems[(j - 1)].setItemMeta(tempmeta);
         }
       }
       this.items.put(world, tempitems);
     }
   }

// Supports Color Codes //
    
    public String colorCodes(String name)
    {
      name = name.replace("&0", ChatColor.BLACK.toString());
      name = name.replace("&1", ChatColor.DARK_BLUE.toString());
      name = name.replace("&2", ChatColor.DARK_GREEN.toString());
      name = name.replace("&3", ChatColor.DARK_AQUA.toString());
      name = name.replace("&4", ChatColor.DARK_RED.toString());
      name = name.replace("&5", ChatColor.DARK_PURPLE.toString());
      name = name.replace("&6", ChatColor.GOLD.toString());
      name = name.replace("&7", ChatColor.GRAY.toString());
      name = name.replace("&8", ChatColor.DARK_GRAY.toString());
      name = name.replace("&9", ChatColor.BLUE.toString());
      name = name.replace("&a", ChatColor.GREEN.toString());
      name = name.replace("&b", ChatColor.AQUA.toString());
      name = name.replace("&c", ChatColor.RED.toString());
      name = name.replace("&d", ChatColor.LIGHT_PURPLE.toString());
      name = name.replace("&e", ChatColor.YELLOW.toString());
      name = name.replace("&f", ChatColor.WHITE.toString());
      name = name.replace("&k", ChatColor.MAGIC.toString());
      name = name.replace("&l", ChatColor.BOLD.toString());
      name = name.replace("&m", ChatColor.STRIKETHROUGH.toString());
      name = name.replace("&n", ChatColor.UNDERLINE.toString());
      name = name.replace("&o", ChatColor.ITALIC.toString());
      name = name.replace("&r", ChatColor.RESET.toString());
      name = name.replace("&&", "&");
      return name;
    }
  }