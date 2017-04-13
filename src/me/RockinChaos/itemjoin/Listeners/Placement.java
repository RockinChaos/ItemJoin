package me.RockinChaos.itemjoin.Listeners;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandlers;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.Registers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class Placement implements Listener{

	 @EventHandler
	  public void onPreventPlayerPlace(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    final String world = player.getWorld().getName();
	    String modifier = ".itemflags";
	    String mod = "placement";
	      if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !CheckItem.isAllowedItem(player, world, item, modifier, mod) && CheckItem.isBlockSimilar(item))
	      {
	        event.setCancelled(true);
	        PlayerHandlers.updateInventory(player);
	 }
}

	@EventHandler
	  public void onCountLock(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    final String world = player.getWorld().getName();
	    String modifier = ".itemflags";
	    String mod = "count-lock";
	      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	    		  if(!CheckItem.isAllowedItem(player, world, item, modifier, mod)) 
	      {
	    	  reAddItem(player, item);
	 }
   }
}
	 
	 public static void reAddItem(final Player player, final ItemStack item1) 
	  {

	       ConfigurationSection selection = ConfigHandler.getConfig("items.yml").getConfigurationSection(WorldHandler.checkWorlds(player.getWorld().getName()) + ".items");
	        for (final String item : selection.getKeys(false)) 
	        {
	      	final ConfigurationSection items = selection.getConfigurationSection(item);
	      	final String slot = items.getString(".slot");
	      	final ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
		        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
		        {
		        public void run()
		          {
					if (toSet != null && ItemJoin.isInt(slot) && CheckItem.isSimilar2(item1, toSet, items, player)) {
				          final int slot1 = items.getInt(".slot");
				    	  ItemStack[] inventory = player.getInventory().getContents();
				    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
				    	  if (toSet != null) {
				   		      if (slot1 >= 0 && slot1 <= 35 && inventory[slot1] != null && !CheckItem.isSimilar(inventory[slot1], toSet, items, player)) {
				   		         player.getInventory().setItem(slot1, toSet);
				   		        } else if (slot1 >= 0 && slot1 <= 35 && inventory[slot1] == null && !CheckItem.ContainsItems(player, toSet, items)) {
								     player.getInventory().setItem(slot1, toSet);
				   		        }
				   	         }
					} else if (toSet != null && !ItemJoin.isInt(slot) && CheckItem.isSimilar2(item1, toSet, items, player)) {
				          EntityEquipment Equip = player.getEquipment();
				    	  ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
				    	  if (toSet != null) {
				   		      if (slot.equalsIgnoreCase("Arbitrary")) {
				   		    	   if (!CheckItem.ContainsItems(player, toSet, items)) {
				   		    	    	player.getInventory().addItem(toSet);
				   		    	}
				   		      }
				   		      if (slot.equalsIgnoreCase("Helmet") 
				   		    		  && Equip.getHelmet() != null 
				   		    		  && !CheckItem.isSimilar(Equip.getHelmet(), toSet, items, player)) {
				   		    		Equip.setHelmet(toSet);
				   		        } else if (slot.equalsIgnoreCase("Helmet") 
				   		        		&& Equip.getHelmet() == null && !CheckItem.ContainsItems(player, toSet, items)) {
				   		        		Equip.setHelmet(toSet);
				   		          }
				   		      if (slot.equalsIgnoreCase("Chestplate") 
				   		    		  && Equip.getChestplate() != null 
				   		    		  && !CheckItem.isSimilar(Equip.getChestplate(), toSet, items, player)) {
				   		    		Equip.setChestplate(toSet);
				   		        } else if (slot.equalsIgnoreCase("Chestplate") 
				   		        		&& Equip.getChestplate() == null && !CheckItem.ContainsItems(player, toSet, items)) {
				   		        		Equip.setChestplate(toSet);
				   		        }
				   		      if (slot.equalsIgnoreCase("Leggings") 
				   		    		  && Equip.getLeggings() != null 
				   		    		  && !CheckItem.isSimilar(Equip.getLeggings(), toSet, items, player)) {
				   		    		Equip.setLeggings(toSet);
				   		        } else if (slot.equalsIgnoreCase("Leggings")
				   		        		&& Equip.getLeggings() == null && !CheckItem.ContainsItems(player, toSet, items)) {
				   		        		Equip.setLeggings(toSet);
				   		        }
				   		      if (slot.equalsIgnoreCase("Boots") 
				   		    		  && Equip.getBoots() != null 
				   		    		  && !CheckItem.isSimilar(Equip.getBoots(), toSet, items, player)) {
				   		    		Equip.setBoots(toSet);
				   		        } else if (slot.equalsIgnoreCase("Boots") 
				   		        		&& Equip.getBoots() == null && !CheckItem.ContainsItems(player, toSet, items)) {
				   		    		Equip.setBoots(toSet);
				   		        }			    
				   		      if (Registers.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand") 
				   		    		  && PlayerHandlers.getOffhandItem(player) != null 
				   		    		  && !CheckItem.isSimilar(PlayerHandlers.getOffhandItem(player), toSet, items, player)) {
				   		    		PlayerHandlers.setOffhandItem(player, toSet);
				   		        } else if (Registers.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand") 
				   		        		&& PlayerHandlers.getOffhandItem(player) == null && !CheckItem.ContainsItems(player, toSet, items)) {
				   		        		PlayerHandlers.setOffhandItem(player, toSet);
				   		        }
				   	         }
					}
			        PlayerHandlers.updateInventory(player);
		          }
		      }, (long) 0.01);
	      	}
	  }
}
