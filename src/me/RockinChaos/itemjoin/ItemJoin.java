package me.RockinChaos.itemjoin;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.Registers;
import me.RockinChaos.itemjoin.utils.UpdateChecker;
import me.RockinChaos.itemjoin.utils.WorldHandler;
import me.clip.placeholderapi.PlaceholderAPI;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
    public Map<String, ItemStack> items = new HashMap<String, ItemStack>();
    protected Logger log;
    public static boolean hasMultiverse;
    public static boolean hasInventories;
    public static boolean hasPlaceholderAPI;
    public static String secretMsg;
    
    public void onEnable()
    {
	  pl = this;
	  Registers.configFile();
	  Registers.itemsFile();
	  Registers.firstJoinFile();
	  Registers.SecretMsg();
	  Registers.enLangFile();
	  Registers.registerEvents();
	  ConsoleCommandSender Console = getServer().getConsoleSender();
	  Registers.checkHooks();
	  WorldHandler.Worlds();
	  UpdateChecker.updateCheck();
      Console.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.GREEN + "has been  Enabled!");
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
   
  @SuppressWarnings("deprecation")
  public void CacheItems(Player player)
    {
     for (int i = 0; i < this.worlds.size(); i++)
     {
       String world = WorldHandler.getWorld((String)this.worlds.get(i));;
       if (getSpecialConfig("items.yml").getConfigurationSection(world) != null
    		   && getSpecialConfig("items.yml").getConfigurationSection(world + ".items") != null) {
       ConfigurationSection selection = getSpecialConfig("items.yml").getConfigurationSection(world + ".items");
       if (selection == null ) {
    	   getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "You have defined the world " + ChatColor.YELLOW + world + ChatColor.RED + " under world-list.");
    	   getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Yet the section for defining items under " + ChatColor.YELLOW + world + ChatColor.RED + " does not exist!");
    	   getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Please consult the documentations for help.");
    	   getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Items for " + ChatColor.YELLOW + world + ChatColor.RED + " will not be set!");
       } else if (selection != null ) {
       for (String item : selection.getKeys(false))
       {
     	ConfigurationSection items = selection.getConfigurationSection(item);
         int dataValue = items.getInt(".data-value");
         String slot = items.getString(".slot");
         Material tempmat = null;
         if (isInt(items.getString(".id"))) {
        	 tempmat = Material.getMaterial(items.getInt(".id"));
         } else {
        	 tempmat = Material.getMaterial(items.getString(".id"));
         }
    	 String vers = ItemJoin.pl.getServer().getVersion();
	     if (!vers.contains("1.9") && items.getString(".slot").equalsIgnoreCase("Offhand")) {  
	        	 ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Your server is running " + ChatColor.YELLOW + vers + ChatColor.RED + " and this does not have Offhand support!");
	        	 ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Because of this, the item " + ChatColor.YELLOW + item + ChatColor.RED +  " will not be set!");
	      } else if (CheckItem.CheckMaterial(tempmat, world, item) && CheckItem.CheckSlot(slot, world, item)) 
          {
           ItemStack tempitem = new ItemStack(tempmat, items.getInt(".count", 1),(short)dataValue);
           ItemMeta tempmeta = tempitem.getItemMeta();
           if (items.getStringList(".lore") != null)
           {
             List<String> templist = items.getStringList(".lore");
             List<String> templist2 = new ArrayList<String>();
             for (int k = 0; k < templist.size(); k++)
             {
               String name = (String)templist.get(k);
               name = translateCodes(name, player, player.getName());
               templist2.add(name);
             }
             tempmeta.setLore(templist2);
           }
           if (items.getString(".name") != null)
           {
             String name = items.getString(".name");
             name = translateCodes(name, player, player.getName());
             tempmeta.setDisplayName(name + encodeItemData(secretMsg));
           } else {
        	   String lookup = getName(tempitem);
        	   String name = translateCodes("&f" + lookup + encodeItemData(secretMsg), player, player.getName());
        	   tempmeta.setDisplayName(name);
           }
           
           if (items.getString(".skull-owner") != null  && tempmat == Material.SKULL_ITEM)
           {
               String owner = items.getString(".skull-owner");
               owner = translateCodes(owner, player, player.getName());
              ((SkullMeta) tempmeta).setOwner(owner);
           }
           tempitem.setItemMeta(tempmeta);
           if (items.getString(".enchantment") != null)
           {
        	   List<String> enchantments = items.getStringList(".enchantment");
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
        	       tempitem.addUnsafeEnchantment(Enchantment.getByName(name), level);
        	       }
        	       if (Enchantment.getByName(name) == null) {
        	    	   getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "An error occurred in the config, " + ChatColor.GREEN + name + ChatColor.RED + " is an incorrect enchantment name!");
        	    	   getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html for a list of correct enchantment names!");
        	       }
        	   }
           }
 	      this.items.put(world + "." + player.getName().toString() + ".items." + item, tempitem);
         }
        }
      }
    }
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
	   return WordUtils.capitalizeFully(stack.getType().name().toLowerCase().replace('_', ' '));
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
    public String translateCodes(String name, Player player, String p)
       {
	     name = name.replace("%player%", p);
         name = ChatColor.translateAlternateColorCodes('&', name).toString();
		 if (hasPlaceholderAPI == true) {
		   name = PlaceholderAPI.setPlaceholders(player, name);
		 }
      return name;
    }
}