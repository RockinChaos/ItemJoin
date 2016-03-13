package me.RockinChaos.itemjoin;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import me.RockinChaos.itemjoin.utils.Commands;
import me.RockinChaos.itemjoin.utils.Listeners;
import me.RockinChaos.itemjoin.utils.UpdateChecker;
import me.RockinChaos.itemjoin.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
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
    @SuppressWarnings("deprecation")
	public Player PlayerJoin = getServer().getPlayer("ItemJoin");
    public String PlayerJoin2 = "ItemJoin";
    public static String secretMsg = "ItemJoin";
    
    public void onEnable()
    {
	  pl = this;
	  Utils.configFile();
      Utils.itemsFile();
      Utils.firstJoinFile();
      Utils.enLangFile();
	  ConsoleCommandSender Console = getServer().getConsoleSender();
      Console.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Enabled!");
	  getCommand("itemjoin").setExecutor(new Commands());
	  getCommand("ij").setExecutor(new Commands());
	  Commands.RegisterEnLang();
	  getServer().getPluginManager().registerEvents(new Listeners(),this);
	  Utils.checkHooks();
	  Utils.Worlds();
	  UpdateChecker.updateCheck();
}

    public void onDisable()
    {
  	  ConsoleCommandSender Console = getServer().getConsoleSender();
      Console.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Disabled!");
}

    public static FileConfiguration loadSpecialConfig(String path) {
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

   public static FileConfiguration getSpecialConfig(String path) {
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
       String world = Listeners.getWorld((String)this.worlds.get(i));;
       ItemStack[] tempitems = new ItemStack[103];
       for (int j = 0; j < 103; j++) {
         tempitems[j] = null;
       }
       for (int j = 1; j <= 103; j++)
       {
         int dataValue = getSpecialConfig("items.yml").getInt(world + ".items." + j + ".data-value");
         Material tempmat = Material.getMaterial(getSpecialConfig("items.yml").getString(world + ".items." + j + ".id"));
         if (tempmat == null)
         {
           tempitems[(j - 1)] = null;
         }
         else
         {
           tempitems[(j - 1)] = new ItemStack(tempmat, getSpecialConfig("items.yml").getInt(world + ".items." + j + ".count", 1),(short)dataValue);
           ItemMeta tempmeta = tempitems[(j - 1)].getItemMeta();
           if (getSpecialConfig("items.yml").getStringList(world + ".items." + j + ".lore") != null)
           {
             List<String> templist = getSpecialConfig("items.yml").getStringList(world + ".items." + j + ".lore");
             List<String> templist2 = new ArrayList<String>();
             for (int k = 0; k < templist.size(); k++)
             {
               String name = (String)templist.get(k);
               name = translateCodes(name);
               templist2.add(name);
             }
             tempmeta.setLore(templist2);
           }
           if (!getSpecialConfig("items.yml").getString(world + ".items." + j + ".name", "none").equalsIgnoreCase("none"))
           {
             String name = getSpecialConfig("items.yml").getString(world + ".items." + j + ".name");
             name = translateCodes(name);
             tempmeta.setDisplayName(name + encodeItemData(secretMsg));
           } else {
        	   String lookup = getName(tempitems[(j - 1)]);
        	   String name = translateCodes("&f" + lookup + encodeItemData(secretMsg));
        	   tempmeta.setDisplayName(name);
           }
           
           if (getSpecialConfig("items.yml").getString(world + ".items." + j + ".skull-owner") != null  && Material.getMaterial(getSpecialConfig("items.yml").getString(world + ".items." + j + ".id")) == Material.SKULL_ITEM)
           {
               String owner = getSpecialConfig("items.yml").getString(world + ".items." + j + ".skull-owner");
               owner = translateCodes(owner);
              ((SkullMeta) tempmeta).setOwner(owner);
           }
           tempitems[(j - 1)].setItemMeta(tempmeta);
           if (getSpecialConfig("items.yml").getStringList(world + ".items." + j + ".enchantment") != null)
           {
        	   List<String> enchantments = getSpecialConfig("items.yml").getStringList(world + ".items." + j + ".enchantment");
        	   for (String enchantment: enchantments) {
        	       String[] parts = enchantment.split(":");
        	       String name = parts[0].toUpperCase();
        	       int level = 1;
        	       if (enchantment.contains(":")) {
            	       try {
            	           level = Integer.parseInt(parts[1]);
            	       }
            	       catch (NumberFormatException ex) {
            	    	   getServer().getConsoleSender().sendMessage("An error occurred in the config, " + parts[1] + " is not a number and a number was expected!");
            	    		  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "An error occurred in the config, " + ChatColor.GREEN + parts[1] + ChatColor.RED + " is not a number and a number was expected!");
            	    		  getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + ChatColor.GREEN + "Enchantment: " + parts[0] + " will now be enchanted by level 1.");
            	       }
        	       }
        	       if (Enchantment.getByName(name) != null) {
        	       tempitems[(j - 1)].addUnsafeEnchantment(Enchantment.getByName(name), level);
        	       }
        	       if (Enchantment.getByName(name) == null) {
        	    	   getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "An error occurred in the config, " + ChatColor.GREEN + name + ChatColor.RED + " is an incorrect enchantment name!");
        	    	   getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html for a list of correct enchantment names!");
        	       }
        	   }
           }
         }
       }
       this.items.put(world + PlayerJoin2, tempitems);
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
   
   public static int getRandom(int lower, int upper) {
	        Random random = new Random();
	        return random.nextInt((upper - lower) + 1) + lower;
	    }
   
   public String decodeUTF8(byte[] bytes) {
	    return new String(bytes, Charset.forName("UTF-8"));
	}

 // translateCodes //
    public String translateCodes(String name)
       {
	     name = name.replace("%player%", PlayerJoin2);
         name = ChatColor.translateAlternateColorCodes('&', name).toString();
		 if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null 
				 && getConfig().getBoolean("PlaceholderAPI") == true) {
		   name = PlaceholderAPI.setPlaceholders(PlayerJoin, name);
		 }
      return name;
    }
}