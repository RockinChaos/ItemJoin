package me.RockinChaos.itemjoin.utils;

import java.util.List;

import me.RockinChaos.itemjoin.ItemJoin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class CheckItem {

	   public static boolean isAllowedItem(Player player, ItemStack checking, String modifier, String mod) {
		   Boolean Allowed = true;
		   boolean BadItem = checking == null;
	       final String world = WorldHandler.getWorld(player.getWorld().getName());
		   if (WorldHandler.isWorld(world) && !BadItem) {
	       ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(player.getWorld().getName() + ".items");
	       if (selection != null) {
	        for (String item : selection.getKeys(false)) 
	        {
	      	ConfigurationSection items = selection.getConfigurationSection(item);
		    boolean Creative = player.getGameMode() == GameMode.CREATIVE;
	    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
				String Modifiers = ((List<?>)items.getStringList("." + modifier)).toString();
				if (isSimilar(checking, toSet, items, player) && Modifiers.contains(mod)) {
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
		   if (item1.getType() == Material.SKULL_ITEM 
				   && Material.getMaterial(items.getString(".id")) == Material.SKULL_ITEM 
				   && ((SkullMeta) item1.getItemMeta()).hasOwner() 
				   && ((SkullMeta) item1.getItemMeta()).getOwner().equalsIgnoreCase(ItemJoin.pl.translateCodes(items.getString(".skull-owner"), player, player.getName()))) {
					   if (item1.hasItemMeta()
							   && item1.getItemMeta().hasDisplayName()
							   && Registers.SecretMsg()
							   && ((SkullMeta) item1.getItemMeta()).getDisplayName().contains(ItemJoin.encodeItemData(ItemJoin.secretMsg))) {
						   isSimilar = true;
				   } else if (item1.hasItemMeta()
						   && item1.getItemMeta().hasDisplayName()
						   && !Registers.SecretMsg()
						   && ((SkullMeta) item1.getItemMeta()).getDisplayName().equalsIgnoreCase(ItemJoin.pl.translateCodes(items.getString(".name"), player, player.getName()))) {	
					   isSimilar = true;
				   } else if (!Registers.SecretMsg()) {	
					   isSimilar = true;
				   }
		  }
   return isSimilar;	   
}
	   
	   public static boolean isSimilar(ItemStack item1, ItemStack item2, ConfigurationSection items, Player player) {
		   boolean isSimilar = false;
		   if (item1.isSimilar(item2) && isCountSimilar(item1, item1)) {
			   isSimilar = true;
		  } else if(isSkullSimilar(items, item1, player) && isCountSimilar(item1, item2)) {
			  isSimilar = true;
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
	        			 || slot.equalsIgnoreCase("Helmet") 
	        			 || slot.equalsIgnoreCase("Chestplate") 
	        			 || slot.equalsIgnoreCase("Leggings") 
	        			 || slot.equalsIgnoreCase("Boots"))) {
	                 ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "For the world " + ChatColor.YELLOW + world + ChatColor.RED + " the item " + ChatColor.YELLOW + item + "'s " + ChatColor.RED + "slot is invalid or does not exist!");
	                 ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "The item " + ChatColor.YELLOW + item + ChatColor.RED +  " will not be set!");
	                 CheckSlot = false;
	        	 }
	         } else if (slot != null && ItemJoin.isInt(slot)) {
	        	 int iSlot = Integer.parseInt(slot);
	        	 if (!(iSlot >= 0 && iSlot <= 35)) {
                 ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "For the world " + ChatColor.YELLOW + world + ChatColor.RED + " the item " + ChatColor.YELLOW + item + "'s " + ChatColor.RED + "slot must be between 0 and 35!");
                 ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "The item " + ChatColor.YELLOW + item + ChatColor.RED +  " will not be set!");
                 CheckSlot = false;
	          }
	         }
			return CheckSlot;
  }

	   public static boolean isArmor(String slot, String world, String item) { 
		   boolean isArmor = false;
	        	 if (slot.equalsIgnoreCase("Offhand") 
	        			 || slot.equalsIgnoreCase("Helmet") 
	        			 || slot.equalsIgnoreCase("Chestplate") 
	        			 || slot.equalsIgnoreCase("Leggings") 
	        			 || slot.equalsIgnoreCase("Boots")) {
	                 ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "For the world " + ChatColor.YELLOW + world + ChatColor.RED + " the item " + ChatColor.YELLOW + item + "'s " + ChatColor.RED + "slot is invalid or does not exist!");
	                 ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "The item " + ChatColor.YELLOW + item + ChatColor.RED +  " will not be set!");
	                 isArmor = true;
	         }
			return isArmor;
  }

	   public static boolean CheckMaterial(Material tempmat, String world, String item) {
		   boolean CheckMaterial = true;
		      if (tempmat == null) {
	        	 ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "For the world " + ChatColor.YELLOW + world + ChatColor.RED + " the item " + ChatColor.YELLOW + item + "'s " + ChatColor.RED + "Material 'ID' is invalid or does not exist!");
	        	 ItemJoin.pl.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] " + ChatColor.RED + "The item " + ChatColor.YELLOW + item + ChatColor.RED +  " will not be set!");
	        	 CheckMaterial = false;
	         }
			return CheckMaterial;
  }
}
