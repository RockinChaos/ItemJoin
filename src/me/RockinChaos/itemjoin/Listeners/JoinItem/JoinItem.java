package me.RockinChaos.itemjoin.Listeners.JoinItem;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.PermissionsHandler;
import me.RockinChaos.itemjoin.utils.WorldHandler;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class JoinItem implements Listener {


	@EventHandler(priority=EventPriority.HIGHEST)
    public void clearOnJoin(PlayerJoinEvent event)
    {
	        final Player player = event.getPlayer();
	        final String world = WorldHandler.getWorld(player.getWorld().getName());
	      	if(ItemJoin.getSpecialConfig("config.yml").getBoolean("Global-Settings" + ".Clear-On." + "clear-on-join") == true && (WorldHandler.isWorld(world))){
	    		if(ItemJoin.getSpecialConfig("config.yml").getBoolean("Global-Settings" + ".Clear-On." + "AllowOPBypass") == true && player.isOp()) {
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
	      long delay = ItemJoin.getSpecialConfig("config.yml").getInt("Global-Settings" + ".Get-Items." + "Delay") * 10L;
	        ItemJoin.pl.items.remove(player.getWorld().getName() + "." + player.getName().toString() + ".items.");
	        ItemJoin.pl.CacheItems(player);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
			@SuppressWarnings("deprecation")
			public void run()
	         {
			  if (WorldHandler.isWorld(player.getWorld().getName())) {
	            setJoinItems(player);
      		    player.updateInventory();
				}
	         }
	      }, delay);
    }
	
	@EventHandler(priority=EventPriority.HIGHEST)
    public void giveOnFirstJoin(PlayerJoinEvent event)
    {
	      final Player player = event.getPlayer();
	      long delay = ItemJoin.getSpecialConfig("config.yml").getInt("Global-Settings" + ".Get-Items." + "Delay") * 10L;
	      Boolean FirstJoinMode = ItemJoin.getSpecialConfig("config.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
	      if(FirstJoinMode == true) {
		    String FirstFindPlayer = ItemJoin.getSpecialConfig("FirstJoin.yml").getString(player.getWorld().getName() + "." + player.getName().toString());
	        if (FirstFindPlayer == null) {
	        	ItemJoin.pl.items.remove(player.getWorld().getName() + "." + player.getName().toString() + ".items.");
		        ItemJoin.pl.CacheItems(player);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
			@SuppressWarnings("deprecation")
			public void run()
			{
		      if (WorldHandler.isWorld(player.getWorld().getName())) {
	            FirstJoin.setJoinItems(player);
      		    player.updateInventory();
		      }
	         }
	      }, delay);
	    }
	  }
    }
	
    public static void setJoinItems(Player player)
    {
        ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(player.getWorld().getName() + ".items");
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
       				  || slot.equalsIgnoreCase("Chestplate") 
       				  || slot.equalsIgnoreCase("Leggings") 
       				  || slot.equalsIgnoreCase("Boots") 
       				  || slot.equalsIgnoreCase("Offhand")) {
       			ArmorySlots(player, items, item);
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
          Boolean FirstJoin = items.getBoolean(".First-Join-Item");
          Boolean FirstJoinMode = ItemJoin.getSpecialConfig("config.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
    	  if (toSet != null) {
   		      if (slot >= 0 && slot <= 35 && inventory[slot] != null && !CheckItem.isSimilar(inventory[slot], toSet, items, player)) {
   		    	if (FirstJoinMode != true || FirstJoin != true) {
   		         player.getInventory().setItem(slot, toSet);
   		    	 }
   		        } else if (slot >= 0 && slot <= 35 && inventory[slot] == null) {
   		        	if (FirstJoinMode != true || FirstJoin != true) {
				     player.getInventory().setItem(slot, toSet);
   		          }
   		        }
   	         }
    }
    
    public static void ArmorySlots(Player player, ConfigurationSection items, String item)
    {
          final String slot = items.getString(".slot");
          Boolean FirstJoin = items.getBoolean(".First-Join-Item");
          Boolean FirstJoinMode = ItemJoin.getSpecialConfig("config.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
          EntityEquipment Equip = player.getEquipment();
    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
    	  if (toSet != null) {
   		      if (slot.equalsIgnoreCase("Helmet") 
   		    		  && Equip.getHelmet() != null 
   		    		  && !CheckItem.isSimilar(Equip.getHelmet(), toSet, items, player)) {
   		    	if (FirstJoinMode != true 
   		    			|| FirstJoin != true) {
   		    		Equip.setHelmet(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Helmet") 
   		        		&& Equip.getHelmet() == null) {
   		        	if (FirstJoinMode != true 
   		        			|| FirstJoin != true) {
   		        		Equip.setHelmet(toSet);
   		          }
   		        }
   		      if (slot.equalsIgnoreCase("Chestplate") 
   		    		  && Equip.getChestplate() != null 
   		    		  && !CheckItem.isSimilar(Equip.getChestplate(), toSet, items, player)) {
   		    	if (FirstJoinMode != true 
   		    			|| FirstJoin != true) {
   		    		Equip.setChestplate(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Chestplate") 
   		        		&& Equip.getChestplate() == null) {
   		        	if (FirstJoinMode != true 
   		        			|| FirstJoin != true) {
   		        		Equip.setChestplate(toSet);
   		          }
   		        }
   		      if (slot.equalsIgnoreCase("Leggings") 
   		    		  && Equip.getLeggings() != null 
   		    		  && !CheckItem.isSimilar(Equip.getLeggings(), toSet, items, player)) {
   		    	if (FirstJoinMode != true 
   		    			|| FirstJoin != true) {
   		    		Equip.setLeggings(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Leggings")
   		        		&& Equip.getLeggings() == null) {
   		        	if (FirstJoinMode != true 
   		        			|| FirstJoin != true) {
   		        		Equip.setLeggings(toSet);
   		          }
   		        }
   		      if (slot.equalsIgnoreCase("Boots") 
   		    		  && Equip.getBoots() != null 
   		    		  && !CheckItem.isSimilar(Equip.getBoots(), toSet, items, player)) {
   		    	if (FirstJoinMode != true 
   		    			|| FirstJoin != true) {
   		    		Equip.setBoots(toSet);
   		    	 }
   		        } else if (slot.equalsIgnoreCase("Boots") 
   		        		&& Equip.getBoots() == null) {
   		        	if (FirstJoinMode != true 
   		        			|| FirstJoin != true) {
   		        		Equip.setBoots(toSet);
   		          }
   		        }			    
   		      String version = ItemJoin.pl.getServer().getVersion();
   		      if (version.contains("1.9") && slot.equalsIgnoreCase("Offhand") 
   		    		  && player.getInventory().getItemInOffHand() != null 
   		    		  && !CheckItem.isSimilar(player.getInventory().getItemInOffHand(), toSet, items, player)) {
   		    	if (FirstJoinMode != true 
   		    			|| FirstJoin != true) {
   		    		player.getInventory().setItemInOffHand(toSet);
   		    	 }
   		        } else if (version.contains("1.9") && slot.equalsIgnoreCase("Offhand") 
   		        		&& player.getInventory().getItemInOffHand() == null) {
   		        	if (FirstJoinMode != true 
   		        			|| FirstJoin != true) {
   		        		player.getInventory().setItemInOffHand(toSet);
   		          }
   		        }
   	         }
    }
}
