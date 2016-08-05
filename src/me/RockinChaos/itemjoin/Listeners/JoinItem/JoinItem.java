package me.RockinChaos.itemjoin.Listeners.JoinItem;

import java.util.List;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.CacheItems.CacheItems;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandlers;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.Registers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class JoinItem implements Listener {

	public static int failCount = 0;
    public static String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";

	@EventHandler(priority=EventPriority.HIGHEST)
    public void clearOnJoin(PlayerJoinEvent event)
    {
	        final Player player = event.getPlayer();
	        final String world = WorldHandler.getWorld(player.getWorld().getName());
	      	if(ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "clear-on-join") == true && (WorldHandler.isWorld(world))){
	    		if(ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "AllowOPBypass") == true && player.isOp()) {
	        		}
	                else {
	                    player.getInventory().clear();
	                    player.getInventory().setHelmet(null);
	                    player.getInventory().setChestplate(null);
	                    player.getInventory().setLeggings(null);
	                    player.getInventory().setBoots(null);
	                }
	      	}
    }

	@EventHandler(priority=EventPriority.HIGHEST)
    public void giveOnJoin(PlayerJoinEvent event)
    {
	      final Player player = event.getPlayer();
	      long delay = ItemJoin.getSpecialConfig("items.yml").getInt("Global-Settings" + ".Get-Items." + "Delay") * 10L;
	        ItemJoin.pl.items.remove(player.getWorld().getName() + "." + player.getName().toString() + ".items.");
	        CacheItems.run(player);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
			public void run()
	         {
			  if (WorldHandler.isWorld(player.getWorld().getName())) {
	            setJoinItems(player);
	            if (failCount != 0) {
	            	player.sendMessage(Prefix + ChatColor.RED + "Could not give you " + ChatColor.YELLOW + failCount + " items," + ChatColor.RED +  " your inventory is full!");
	            	failCount = 0;
	            }
	            PlayerHandlers.updateInventory(player);
				}
	         }
	      }, delay);
    }
	
	@EventHandler(priority=EventPriority.HIGHEST)
    public void giveOnFirstJoin(PlayerJoinEvent event)
    {
	      final Player player = event.getPlayer();
	      long delay = ItemJoin.getSpecialConfig("items.yml").getInt("Global-Settings" + ".Get-Items." + "Delay") * 10L;
	      Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
	      if(FirstJoinMode == true) {
		    String FirstFindPlayer = ItemJoin.getSpecialConfig("FirstJoin.yml").getString(player.getWorld().getName() + "." + player.getName().toString());
	        if (FirstFindPlayer == null) {
	        	ItemJoin.pl.items.remove(player.getWorld().getName() + "." + player.getName().toString() + ".items.");
	        	CacheItems.run(player);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
			public void run()
			{
		      if (WorldHandler.isWorld(player.getWorld().getName())) {
	            FirstJoin.setJoinItems(player);
	            if (FirstJoin.failCount != 0) {
	            	player.sendMessage(Prefix + ChatColor.RED + "Could not give you " + ChatColor.YELLOW + FirstJoin.failCount + " items," + ChatColor.RED +  " your inventory is full!");
	            	FirstJoin.failCount = 0;
	            }
	            PlayerHandlers.updateInventory(player);
		      }
	         }
	      }, delay);
	    }
	  }
    }
	
    public static void setJoinItems(Player player)
    {
        ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(WorldHandler.checkWorlds(player.getWorld().getName()) + ".items");
        if (selection != null ) {
        for (String item : selection.getKeys(false)) 
        {
      	  ConfigurationSection items = selection.getConfigurationSection(item);
          final String world = WorldHandler.getWorld(player.getWorld().getName());
          String slot = items.getString(".slot");
         if (WorldHandler.isWorld(world)) {
       	  if (player.hasPermission(PermissionsHandler.customPermissions(items, item, world)) 
       			  || player.hasPermission("itemjoin." + world + ".*") 
       			  || player.hasPermission("itemjoin.*")) {
       		  if (slot.equalsIgnoreCase("Helmet")
       				  || slot.equalsIgnoreCase("Arbitrary")
       				  || slot.equalsIgnoreCase("Chestplate") 
       				  || slot.equalsIgnoreCase("Leggings") 
       				  || slot.equalsIgnoreCase("Boots") 
       				  || slot.equalsIgnoreCase("Offhand")) {
       			CustomSlots(player, items, item);
       		  } else {
       		   InventorySlots(player, items, item);
       		  }
   	        }
         }
        }
       }
    }
    
    public static void InventorySlots(Player player, ConfigurationSection items, String item)
    {
          final int slot = items.getInt(".slot");
    	  ItemStack[] inventory = player.getInventory().getContents();
          String WorldChanged = ((List<?>)items.getStringList(".itemflags")).toString();
          Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
    	  if (toSet != null) {
   		      if (slot >= 0 && slot <= 35 && inventory[slot] != null && !CheckItem.isSimilar(inventory[slot], toSet, items, player)) {
   		    	if (FirstJoinMode != true || !WorldChanged.contains("first-join")) {
   		         player.getInventory().setItem(slot, toSet);
   		    	 }
   		        } else if (slot >= 0 && slot <= 35 && inventory[slot] == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode != true || !WorldChanged.contains("first-join")) {
				     player.getInventory().setItem(slot, toSet);
   		          }
   		        }
   	         }
    }
    
    public static void CustomSlots(Player player, ConfigurationSection items, String item)
    {
          final String slot = items.getString(".slot");
          String WorldChanged = ((List<?>)items.getStringList(".itemflags")).toString();
          Boolean FirstJoinMode = ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
          EntityEquipment Equip = player.getEquipment();
    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
    	  if (toSet != null) {
   		      if (slot.equalsIgnoreCase("Arbitrary")) {
   		    	   if (!CheckItem.ContainsItems(player, toSet, items)) {
   		    	    if (FirstJoinMode != true 
   		    			|| !WorldChanged.contains("first-join")) {
   		    	    	if (player.getInventory().firstEmpty() == -1) {
   		    	    		failCount = failCount + 1;
   		    	       } else {
   		    	    	player.getInventory().addItem(toSet);
   		    	     }
   		           }
   		    	}
   		      }
   		      if (slot.equalsIgnoreCase("Helmet") 
   		    		  && Equip.getHelmet() != null 
   		    		  && !CheckItem.isSimilar(Equip.getHelmet(), toSet, items, player)) {
   		    	if (FirstJoinMode != true 
   		    			|| !WorldChanged.contains("first-join")) {
   		    		Equip.setHelmet(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Helmet") 
   		        		&& Equip.getHelmet() == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode != true 
   		        			|| !WorldChanged.contains("first-join")) {
   		        		Equip.setHelmet(toSet);
   		          }
   		        }
   		      if (slot.equalsIgnoreCase("Chestplate") 
   		    		  && Equip.getChestplate() != null 
   		    		  && !CheckItem.isSimilar(Equip.getChestplate(), toSet, items, player)) {
   		    	if (FirstJoinMode != true 
   		    			|| !WorldChanged.contains("first-join")) {
   		    		Equip.setChestplate(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Chestplate") 
   		        		&& Equip.getChestplate() == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode != true 
   		        			|| !WorldChanged.contains("first-join")) {
   		        		Equip.setChestplate(toSet);
   		          }
   		        }
   		      if (slot.equalsIgnoreCase("Leggings") 
   		    		  && Equip.getLeggings() != null 
   		    		  && !CheckItem.isSimilar(Equip.getLeggings(), toSet, items, player)) {
   		    	if (FirstJoinMode != true 
   		    			|| !WorldChanged.contains("first-join")) {
   		    		Equip.setLeggings(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Leggings")
   		        		&& Equip.getLeggings() == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode != true 
   		        			|| !WorldChanged.contains("first-join")) {
   		        		Equip.setLeggings(toSet);
   		          }
   		        }
   		      if (slot.equalsIgnoreCase("Boots") 
   		    		  && Equip.getBoots() != null 
   		    		  && !CheckItem.isSimilar(Equip.getBoots(), toSet, items, player)) {
   		    	if (FirstJoinMode != true 
   		    			|| !WorldChanged.contains("first-join")) {
   		    		Equip.setBoots(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Boots") 
   		        		&& Equip.getBoots() == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode != true 
   		        			|| !WorldChanged.contains("first-join")) {
   		        		Equip.setBoots(toSet);
   		          }
   		        }			    
   		      if (Registers.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand") 
   		    		  && player.getInventory().getItemInOffHand() != null 
   		    		  && !CheckItem.isSimilar(player.getInventory().getItemInOffHand(), toSet, items, player)) {
   		    	if (FirstJoinMode != true 
   		    			|| !WorldChanged.contains("first-join")) {
   		    		player.getInventory().setItemInOffHand(toSet);
   		    	 }
   		        } else if (Registers.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand") 
   		        		&& player.getInventory().getItemInOffHand() == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode != true 
   		        			|| !WorldChanged.contains("first-join")) {
   		        		player.getInventory().setItemInOffHand(toSet);
   		          }
   		        }
   	         }
    }
}
