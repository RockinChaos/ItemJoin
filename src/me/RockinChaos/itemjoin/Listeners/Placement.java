package me.RockinChaos.itemjoin.Listeners;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.CheckItem;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Placement implements Listener{

	 @SuppressWarnings("deprecation")
	@EventHandler
	  public void onPreventPlayerPlace(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    String modifier = ".prevent-modifiers";
	    String mod = "placement";
	      if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !CheckItem.isAllowedItem(player, item, modifier, mod))
	      {
	        event.setCancelled(true);
	        player.updateInventory();
	 }
}

	@EventHandler
	  public void onCountLockPlace(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    String modifier = ".prevent-modifiers";
	    String mod = "count-lock";
	      if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !CheckItem.isAllowedItem(player, item, modifier, mod))
	      {
	    	  reAddItem(player, item);
	 }
}
	 
	 @SuppressWarnings("deprecation")
	  public static void reAddItem(Player player, ItemStack item1) 
	  {

	       ConfigurationSection selection = ItemJoin.getSpecialConfig("items.yml").getConfigurationSection(player.getWorld().getName() + ".items");
	        for (String item : selection.getKeys(false)) 
	        {
	      	ConfigurationSection items = selection.getConfigurationSection(item);
	      	String slot = items.getString(".slot");
	      	ItemStack toSet = ItemJoin.pl.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + item);
	      	if (toSet != null && ItemJoin.isInt(slot) && CheckItem.isSimilar(item1, toSet, items, player)) {
	      		int isSlot = items.getInt(".slot");
	        player.getInventory().setItem(isSlot, toSet);
	        player.updateInventory();
	      	}
	      }
	  }
}
