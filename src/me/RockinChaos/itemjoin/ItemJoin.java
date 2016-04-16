package me.RockinChaos.itemjoin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.RenderImageMaps;
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
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

  public class ItemJoin
    extends JavaPlugin
  {
	public static ItemJoin pl;
    public List<String> worlds;
    public Map<String, ItemStack> items = new HashMap<String, ItemStack>();
    protected Logger log;
    public static boolean hasMultiverse;
    public static boolean hasInventories;
    public static boolean hasPlaceholderAPI;
    public static String secretMsg;
    public ConsoleCommandSender Console = getServer().getConsoleSender();
    public String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";
    
    public void onEnable()
    {
	  pl = this;
	  Registers.configFile();
	  Registers.itemsFile();
	  Registers.firstJoinFile();
	  Registers.SecretMsg();
	  Registers.enLangFile();
	  Registers.registerEvents();
	  Registers.checkHooks();
	  WorldHandler.Worlds();
	  WorldHandler.UpdateItems();
	  UpdateChecker.updateCheck();
      Console.sendMessage(Prefix + ChatColor.GREEN + "has been Enabled!");
}

    public void onDisable()
    {
      Console.sendMessage(Prefix + ChatColor.RED + "Disabled!");
}

    public static FileConfiguration loadSpecialConfig(String path)
    {
      if (!path.endsWith(".yml")) {
        path = String.valueOf(path) + ".yml";
      }
      File file;
      if (!(file = new File(pl.getDataFolder(), path)).exists()) {
        try
        {
          pl.saveResource(path, false);
        }
        catch (Exception e)
        {
          e.printStackTrace();
          pl.getLogger().warning("Cannot save " + path + " to disk!");
          return null;
        }
      }
      YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
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
    	   Console.sendMessage(Prefix + ChatColor.RED + "You have defined the world " + ChatColor.YELLOW + world + ChatColor.RED + " under world-list.");
    	   Console.sendMessage(Prefix + ChatColor.RED + "Yet the section for defining items under " + ChatColor.YELLOW + world + ChatColor.RED + " does not exist!");
    	   Console.sendMessage(Prefix + ChatColor.RED + "Please consult the documentations for help.");
    	   Console.sendMessage(Prefix + ChatColor.RED + "Items for " + ChatColor.YELLOW + world + ChatColor.RED + " will not be set!");
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
	        	 Console.sendMessage(Prefix + ChatColor.RED + "Your server is running " + ChatColor.YELLOW + vers + ChatColor.RED + " and this does not have Offhand support!");
	        	 Console.sendMessage(Prefix + ChatColor.RED + "Because of this, the item " + ChatColor.YELLOW + item + ChatColor.RED +  " will not be set!");
	      } else if (CheckItem.CheckMaterial(tempmat, world, item) && CheckItem.CheckSlot(slot, world, item)) 
          {
           ItemStack tempitem = new ItemStack(tempmat, items.getInt(".count", 1),(short)dataValue);
           BookMeta bookmeta = null;
           if (tempmat == Material.WRITTEN_BOOK) {
        	   bookmeta = (BookMeta) tempitem.getItemMeta();
           }
           ItemMeta tempmeta = tempitem.getItemMeta();
           if (items.getStringList(".lore") != null)
           {
             List<String> templist = items.getStringList(".lore");
             List<String> templist2 = new ArrayList<String>();
             for (int k = 0; k < templist.size(); k++)
             {
               String name = (String)templist.get(k);
               name = formatPlaceholders(name, player);
               templist2.add(name);
             }
             tempmeta.setLore(templist2);
             if (tempmat == Material.WRITTEN_BOOK) {
             bookmeta.setLore(templist2);
             }
           }
           if (items.getString(".name") != null)
           {
             String name = items.getString(".name");
             name = formatPlaceholders(name, player);
             tempmeta.setDisplayName(name + encodeItemData(secretMsg));
             if (tempmat == Material.WRITTEN_BOOK) {
             bookmeta.setDisplayName(name + encodeItemData(secretMsg));
             }
           } else {
        	   String lookup = getName(tempitem);
        	   String name = formatPlaceholders("&f" + lookup + encodeItemData(secretMsg), player);
        	   tempmeta.setDisplayName(name);
        	   if (tempmat == Material.WRITTEN_BOOK) {
               bookmeta.setDisplayName(name);
        	   }
           }
           if (items.getString(".author") != null && tempmat == Material.WRITTEN_BOOK)
           {
        	   bookmeta.setAuthor(formatPlaceholders(items.getString(".author"), player));
           }
           if (items.getString(".pages") != null && tempmat == Material.WRITTEN_BOOK)
           {
               List<String> templist = items.getStringList(".pages");
               List<String> templist2 = new ArrayList<String>();
               templist2.add("cleanSlate");
               for (int k = 0; k < templist.size(); k++)
               {
            	   String pageSetup = (String)templist.get(k);
            	   if (pageSetup.contains("newpage: ") || pageSetup.contains("newline: ") || pageSetup.contains("newpage:") || pageSetup.contains("newline:") || pageSetup.contains(":endthebook:")) {
            		   if (pageSetup.contains("newpage: ") && !templist2.contains("cleanSlate") || pageSetup.contains("newpage:") && !templist2.contains("cleanSlate")) {
                    	   bookmeta.addPage(formatPlaceholders(templist2.toString().replace("[", "").replace("]", "").replaceAll(", ", " "), player));
                    	   templist2.clear();
            		   } else if (pageSetup.contains(":endthebook:")) {
                    	   bookmeta.addPage(formatPlaceholders(templist2.toString().replace("[", "").replace("]", "").replaceAll(", ", " "), player));
                    	   templist2.clear();
            		   } else if (templist2.contains("cleanSlate")) {
            			   templist2.clear();
            		   }
            		   templist2.add(pageSetup.replace("newline: ", "\n").replace("newpage: ", "").replace("newline:", "\n").replace("newpage:", ""));
            	   }
        	   }
           }
           if (items.getString(".custom-map-image") != null && tempmat == Material.MAP)
           {
           	 MapView view = ItemJoin.pl.getServer().getMap(tempitem.getDurability());
        	 String mapUrl = items.getString(".custom-map-image");
   			   for(MapRenderer r:view.getRenderers()) {
   			 	 view.removeRenderer(r);
   			   }
   			 RenderImageMaps.setImage(mapUrl);
   	         view.addRenderer(new RenderImageMaps());
           }
           if (items.getString(".skull-owner") != null  && tempmat == Material.SKULL_ITEM)
           {
               String owner = items.getString(".skull-owner");
               owner = formatPlaceholders(owner, player);
              ((SkullMeta) tempmeta).setOwner(owner);
           }
           tempitem.setItemMeta(tempmeta);
           if (tempmat == Material.WRITTEN_BOOK) {
           tempitem.setItemMeta(bookmeta);
           }
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
            	    	   Console.sendMessage("An error occurred in the config, " + parts[1] + " is not a number and a number was expected!");
            	    	   Console.sendMessage(Prefix + ChatColor.RED + "An error occurred in the config, " + ChatColor.GREEN + parts[1] + ChatColor.RED + " is not a number and a number was expected!");
            	           Console.sendMessage(Prefix + ChatColor.RED + ChatColor.GREEN + "Enchantment: " + parts[0] + " will now be enchanted by level 1.");
            	       }
        	       }
        	       if (Enchantment.getByName(name) != null) {
        	       tempitem.addUnsafeEnchantment(Enchantment.getByName(name), level);
        	       }
        	       if (Enchantment.getByName(name) == null) {
        	    	   Console.sendMessage(Prefix + ChatColor.RED + "An error occurred in the config, " + ChatColor.GREEN + name + ChatColor.RED + " is an incorrect enchantment name!");
        	    	   Console.sendMessage(Prefix + ChatColor.RED + "Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html for a list of correct enchantment names!");
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
   } catch (Exception e){
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
       } else {
           String[] d = hiddenData[hiddenData.length-1].split("§");
           for(int i = 1; i < d.length; i++){
               returnData += d[i];
           }
           return returnData;
       }
   } catch (Exception e){
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

	public String formatPlaceholders(String name, Player player)
       {
		String playerName = "ItemJoin";
		if (player != null) {
			playerName = player.getName();
		}
	     name = name.replace("%player%", playerName);
         name = ChatColor.translateAlternateColorCodes('&', name).toString();
		 if (hasPlaceholderAPI == true) {
		   name = PlaceholderAPI.setPlaceholders(player, name);
		 }
      return name;
    }
}