package me.RockinChaos.itemjoin.Listeners.JoinItem;

import java.util.HashMap;
import java.util.List;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.CacheItems.CacheItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.xephi.authme.api.API;

@SuppressWarnings("deprecation")
public class JoinItem implements Listener {

	public static int failCount = 0;
    public static String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";

    @EventHandler
    public void giveOnJoin(PlayerJoinEvent event)
    {
	      final Player player = event.getPlayer();
	      if(ItemJoin.hasAuthMe == true) {
	    	  runAuthMeStats(player);
	      } else {
	    	  setTheItems(player);
	      }
    }
    
    public static void runAuthMeStats(final Player player)
    {
        new BukkitRunnable() {
			@Override
    		public void run() {
    	    	if(API.isAuthenticated(player)) {
    	    		setTheItems(player);
    	    		this.cancel();
    	    	}
    			}
    	   }.runTaskTimer(ItemJoin.pl, 0, 20);
    }
    
    public static void setTheItems(final Player player)
    {
	      final String world = player.getWorld().getName();
	      long delay = ConfigHandler.getConfig("items.yml").getInt("Global-Settings" + ".Get-Items." + "Delay") * 10L;
	        ItemJoin.pl.items.remove(player.getWorld().getName() + "." + player.getName().toString() + ".items.");
	        CacheItems.run(player);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
			public void run()
	         {
			  if (WorldHandler.isWorld(player.getWorld().getName())) {
				setClearItems(player, world, "clear-only-itemjoin-on-join", "clear-on-join");
	            setJoinItems(player);
	            if (failCount != 0) {
	            	player.sendMessage(Prefix + ChatColor.RED + "Could not give you " + ChatColor.YELLOW + failCount + " items," + ChatColor.RED +  " your inventory is full!");
	            	failCount = 0;
	            }
	  	        Boolean FirstJoinMode = ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
		        if(FirstJoinMode == true) {
			    String FirstFindPlayer = ConfigHandler.getConfig("FirstJoin.yml").getString(player.getWorld().getName() + "." + player.getName().toString());
		        if (FirstFindPlayer == null) {
		            FirstJoin.setJoinItems(player);
		            if (FirstJoin.failCount != 0) {
		            	player.sendMessage(Prefix + ChatColor.RED + "Could not give you " + ChatColor.YELLOW + FirstJoin.failCount + " items," + ChatColor.RED +  " your inventory is full!");
		            	FirstJoin.failCount = 0;
		            }
		        }
		        }
	            PlayerHandlers.updateInventory(player);
				}
	         }
	      }, delay);
    }

    public static void setClearItems(Player player, String world, String clearOn, String clearOn2)
    {
    	if(ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + clearOn2) == true && (WorldHandler.isWorld(world))){
     		if(ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "AllowOPBypass") == true && player.isOp()) {
    		} else {
                player.getInventory().clear();
                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);
            }
     	 } else {
	      ConfigurationSection selection = ConfigHandler.getConfig("items.yml").getConfigurationSection(WorldHandler.getWorld(WorldHandler.checkWorlds(world)) + ".items");
	      if (selection != null) {
	      for (String item : selection.getKeys(false)) 
      {
	    	  ConfigurationSection items = selection.getConfigurationSection(item);
	    	  clearItemJoinItems(player, world, item, items, clearOn);
         }
	     }
	   }
    }
    
    public static void clearItemJoinItems(Player player, String world, String item, ConfigurationSection items, String clearOn)
    {
 	     String Modifiers = ((List<?>)items.getStringList("." + "itemflags")).toString();
    	 if(ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + clearOn) == true || Modifiers.contains("clear-on-join")|| Modifiers.contains("clear-on-world-change")) {
   	 		if(ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + clearOn) == true && ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".Clear-On." + "AllowOPBypass") == true && player.isOp()) {
   	 		} else {
	 ItemStack toRemove = ItemJoin.pl.items.get(WorldHandler.checkWorlds(world) + "." + player.getName().toString() + ".items." + item);
 			HashMap<String, ItemStack[]> inventoryContents = new HashMap<String, ItemStack[]>();
 			inventoryContents.put(player.getName(), player.getInventory().getContents());
	    	for (ItemStack contents : inventoryContents.get(player.getName())){
		   	   if (CheckItem.isRawSimilar(contents, toRemove, items, player)) {
	    	        player.getInventory().remove(contents);
		   	   }
	    	 }
    	    CheckItem.setArmor(player, toRemove, items, null, null, null, null);
    	    CheckItem.setOffhand(player, toRemove, items, null);
	    	inventoryContents.clear();
   	 		}
    	 }
    }
	
    public static void setJoinItems(Player player)
    {
        ConfigurationSection selection = ConfigHandler.getConfig("items.yml").getConfigurationSection(WorldHandler.checkWorlds(player.getWorld().getName()) + ".items");
        if (selection != null ) {
        for (String item : selection.getKeys(false)) 
        {
      	  ConfigurationSection items = selection.getConfigurationSection(item);
          final String world = player.getWorld().getName();
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
          Boolean FirstJoinMode = ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
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
          Boolean FirstJoinMode = ConfigHandler.getConfig("items.yml").getBoolean("Global-Settings" + ".First-Join." + "FirstJoin-Mode-Enabled");
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
   		    		  && PlayerHandlers.getOffhandItem(player) != null 
   		    		  && !CheckItem.isSimilar(PlayerHandlers.getOffhandItem(player), toSet, items, player)) {
   		    	if (FirstJoinMode != true 
   		    			|| !WorldChanged.contains("first-join")) {
   		    		PlayerHandlers.setOffhandItem(player, toSet);
   		    	 }
   		        } else if (Registers.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand") 
   		        		&& PlayerHandlers.getOffhandItem(player) == null && !CheckItem.ContainsItems(player, toSet, items)) {
   		        	if (FirstJoinMode != true 
   		        			|| !WorldChanged.contains("first-join")) {
   		        		PlayerHandlers.setOffhandItem(player, toSet);
   		          }
   		        }
   	         }
    }
}
