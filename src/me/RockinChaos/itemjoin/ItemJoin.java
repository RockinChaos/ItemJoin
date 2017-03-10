package me.RockinChaos.itemjoin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.Registers;
import me.RockinChaos.itemjoin.utils.UpdateChecking;
import me.RockinChaos.itemjoin.utils.Econ;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

  public class ItemJoin
    extends JavaPlugin
  {
	public static ItemJoin pl;
    public List<String> worlds;
    public Map<String, ItemStack> items = new HashMap<String, ItemStack>();
    public Logger log;
    public static File fileAa;
    public static boolean hasVault;
    public static boolean hasMultiverse;
    public static boolean hasInventories;
    public static boolean hasPlaceholderAPI;
    public static boolean hasPerWorldPlugins;
    public static boolean hasPerWorldInventory;
    public static boolean hasAuthMe;
    public static String secretMsg;
    
    public void onEnable()
    {
	  pl = this;
	  Registers.configFile();
	  Registers.itemsFile();
	  Registers.firstJoinFile();
	  Registers.enLangFile();
	  Registers.SecretMsg();
	  Registers.registerEvents();
	  Registers.checkHooks();
	  WorldHandler.Worlds();
	  WorldHandler.UpdateItems();
      UpdateChecking.checkUpdates(UpdateChecking.Console);
	  fileAa = getFile();
	  Econ.enableEconomy();
	  if(!Registers.hasBetterVersion()) {
	  UpdateChecking.Console.sendMessage(UpdateChecking.Prefix + ChatColor.RED + "You are using an outdated version of Minecraft.");
	  UpdateChecking.Console.sendMessage(UpdateChecking.Prefix + ChatColor.RED + "DISABLED: %hitplayer% variable.");
	  UpdateChecking.Console.sendMessage(UpdateChecking.Prefix + ChatColor.RED + "DISABLED: hide-attributes itemflag.");
	  }
      UpdateChecking.Console.sendMessage(UpdateChecking.Prefix + ChatColor.GREEN + "has been Enabled!");
      }

    public void onDisable()
    {
      UpdateChecking.Console.sendMessage(UpdateChecking.Prefix + ChatColor.RED + "Disabled!");
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