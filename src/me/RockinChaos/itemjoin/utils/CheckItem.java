package me.RockinChaos.itemjoin.utils;

import java.util.List;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.WorldHandler;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class CheckItem {
	
    public static ConsoleCommandSender Console = ItemJoin.pl.getServer().getConsoleSender();
    public static String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";

	   public static boolean isAllowedItem(Player player, ItemStack checking, String modifier, String mod) {
		   Boolean Allowed = true;
		   boolean BadItem = checking == null;
	       final String world = WorldHandler.getWorld(player.getWorld().getName());
		   if (WorldHandler.isWorld(world) && !BadItem) {
	       ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(WorldHandler.checkWorlds(player.getWorld().getName()) + ".items");
	       if (selection != null) {
	        for (String item : selection.getKeys(false)) 
	        {
	      	ConfigurationSection items = selection.getConfigurationSection(item);
		    boolean Creative = player.getGameMode() == GameMode.CREATIVE;
	    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
				String Modifiers = ((List<?>)items.getStringList("." + modifier)).toString();
				if (toSet != null && isSimilar(checking, toSet, items, player) && Modifiers.contains(mod)) {
					if (Modifiers.contains("AllowOPBypass") && player.isOp() || Modifiers.contains("CreativeBypass") && Creative) {
					} else {
						Allowed = false;
					}
				}
	        }
		   }
		 }
	    return Allowed;
	}

	   public static boolean isSkullSimilar(ConfigurationSection items, ItemStack item1, Player player) {
		   boolean isSimilar = false;
           String name;
           if (items.getString(".name") != null) {
           name = ItemJoin.pl.formatPlaceholders(items.getString(".name"), player) + ItemJoin.encodeItemData(ItemJoin.secretMsg);
           } else {
        	   String lookup = ItemJoin.getName(item1);
        	   name = ItemJoin.pl.formatPlaceholders("&f" + lookup + ItemJoin.encodeItemData(ItemJoin.secretMsg), player);
           }
		   if (item1.getType() == Material.SKULL_ITEM 
				   && Material.getMaterial(items.getString(".id")) == Material.SKULL_ITEM) {
				   if (((SkullMeta) item1.getItemMeta()).hasOwner() 
				   && ((SkullMeta) item1.getItemMeta()).getOwner().equalsIgnoreCase(ItemJoin.pl.formatPlaceholders(items.getString(".skull-owner"), player)) && item1.hasItemMeta()
				   && item1.getItemMeta().hasDisplayName() && item1.getItemMeta().getDisplayName().equalsIgnoreCase(name)) {
					   isSimilar = true;
				   } else if (!Registers.SecretMsg() && ((SkullMeta) item1.getItemMeta()).hasOwner() 
						   && ((SkullMeta) item1.getItemMeta()).getOwner().equalsIgnoreCase(ItemJoin.pl.formatPlaceholders(items.getString(".skull-owner"), player))) {	
					   isSimilar = true;
				   } else if (!Registers.SecretMsg() && !((SkullMeta) item1.getItemMeta()).hasOwner()) {	
					   isSimilar = true;
				   }
		  }
   return isSimilar;	   
}
	   
	   public static boolean isBlockSimilar(ItemStack item1) {
		   boolean isSimilar = false;
		   if (item1.getType().isBlock()) {
			   isSimilar = true;
		   } else if (item1.getType() == Material.SKULL_ITEM || item1.getType() == Material.SKULL) {
			   isSimilar = true;
		   }
   return isSimilar;	   
}
  
	   public static boolean isBookSimilar(ConfigurationSection items, ItemStack item1, Player player) {
		   boolean isSimilar = false;
		   if (item1.getType() == Material.WRITTEN_BOOK 
				   && Material.getMaterial(items.getString(".id")) == Material.WRITTEN_BOOK) {
					   if (item1.hasItemMeta()
							   && item1.getItemMeta().hasDisplayName()
							   && Registers.SecretMsg()
							   && item1.getItemMeta().getDisplayName().contains(ItemJoin.encodeItemData(ItemJoin.secretMsg))) {
						   isSimilar = true;
				   } else if (item1.hasItemMeta()
						   && item1.getItemMeta().hasDisplayName()
						   && !Registers.SecretMsg()
						   && item1.getItemMeta().getDisplayName().equalsIgnoreCase(ItemJoin.pl.formatPlaceholders(items.getString(".name"), player))) {	
					   isSimilar = true;
				   } else if (!Registers.SecretMsg()) {	
					   isSimilar = true;
				   }
		  }
   return isSimilar;	   
}

	   
	   public static boolean isSimilar(ItemStack item1, ItemStack item2, ConfigurationSection items, Player player) {
		   boolean isSimilar = false;
		   if (item1 != null && item2 != null) {
		   if (item1.isSimilar(item2) && isCountSimilar(item1, item1)) {
			   isSimilar = true;
		  } else if(isSkullSimilar(items, item1, player) && isCountSimilar(item1, item2)) {
			  isSimilar = true;
		  } else if(isBookSimilar(items, item1, player) && isCountSimilar(item1, item2)) {
			  isSimilar = true;
		  }
		 }
   return isSimilar;	   
}
	   
	   public static boolean isCountSimilar(ItemStack item1, ItemStack item2) {
		   boolean isCountSimilar = false;
		   if (item1.getAmount() == item2.getAmount()) {
			   isCountSimilar = true;
		   }
   return isCountSimilar;	   
}
	   
	   public static boolean CheckSlot(String slot, String world, String item) { 
		   boolean CheckSlot = true;
           if (slot != null && !ItemJoin.isInt(slot)) {
	        	 if (!(slot.equalsIgnoreCase("Offhand") 
	        			 || slot.equalsIgnoreCase("Arbitrary")
	        			 || slot.equalsIgnoreCase("Helmet") 
	        			 || slot.equalsIgnoreCase("Chestplate") 
	        			 || slot.equalsIgnoreCase("Leggings") 
	        			 || slot.equalsIgnoreCase("Boots"))) {
	                 Console.sendMessage(Prefix + ChatColor.RED + "For the world " + ChatColor.YELLOW + world + ChatColor.RED + " the item " + ChatColor.YELLOW + item + "'s " + ChatColor.RED + "slot is invalid or does not exist!");
	                 Console.sendMessage(Prefix + ChatColor.RED + "The item " + ChatColor.YELLOW + item + ChatColor.RED +  " will not be set!");
	                 CheckSlot = false;
	        	 }
	         } else if (slot != null && ItemJoin.isInt(slot)) {
	        	 int iSlot = Integer.parseInt(slot);
	        	 if (!(iSlot >= 0 && iSlot <= 35)) {
                 Console.sendMessage(Prefix + ChatColor.RED + "For the world " + ChatColor.YELLOW + world + ChatColor.RED + " the item " + ChatColor.YELLOW + item + "'s " + ChatColor.RED + "slot must be between 0 and 35!");
                 Console.sendMessage(Prefix + ChatColor.RED + "The item " + ChatColor.YELLOW + item + ChatColor.RED +  " will not be set!");
                 CheckSlot = false;
	          }
	         }
			return CheckSlot;
  }

	   public static boolean isArmor(String slot, String world, String item) { 
		   boolean isArmor = false;
		   if (slot != null) {
	        	 if (slot.equalsIgnoreCase("Offhand") 
	        			 || slot.equalsIgnoreCase("Helmet") 
	        			 || slot.equalsIgnoreCase("Chestplate") 
	        			 || slot.equalsIgnoreCase("Leggings") 
	        			 || slot.equalsIgnoreCase("Boots")) {
	                 Console.sendMessage(Prefix + ChatColor.RED + "For the world " + ChatColor.YELLOW + world + ChatColor.RED + " the item " + ChatColor.YELLOW + item + "'s " + ChatColor.RED + "slot is invalid or does not exist!");
	                 Console.sendMessage(Prefix + ChatColor.RED + "The item " + ChatColor.YELLOW + item + ChatColor.RED +  " will not be set!");
	                 isArmor = true;
	         }
		   }
			return isArmor;
  }
	   
	   public static boolean ContainsItems(Player player, ItemStack toSet, ConfigurationSection items) { 
		   boolean hasItem = false;
		    	for (ItemStack contents : player.getInventory().getContents()){
	   		    	  if (CheckItem.isSimilar(contents, toSet, items, player)) {
	   		    		  hasItem = true;
	   		    	  }
	   		    	  }
			return hasItem;
  }

	   public static boolean CheckMaterial(Material tempmat, String world, String item) {
		   boolean CheckMaterial = true;
		      if (tempmat == null) {
	        	 Console.sendMessage(Prefix + ChatColor.RED + "For the world " + ChatColor.YELLOW + world + ChatColor.RED + " the item " + ChatColor.YELLOW + item + "'s " + ChatColor.RED + "Material 'ID' is invalid or does not exist!");
	        	 Console.sendMessage(Prefix + ChatColor.RED + "The item " + ChatColor.YELLOW + item + ChatColor.RED +  " will not be set!");
	        	 CheckMaterial = false;
	         }
			return CheckMaterial;
  }
}
