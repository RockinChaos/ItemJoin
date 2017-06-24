package me.RockinChaos.itemjoin.listeners;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.listeners.giveitems.SetItems;
import me.RockinChaos.itemjoin.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Placement implements Listener{

	 @EventHandler
	  public void onPreventPlayerPlace(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    String itemflag = "placement";
	      if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !ItemHandler.isAllowedItem(player, item, itemflag) && item.getType().isBlock())
	      {
	        event.setCancelled(true);
	        PlayerHandler.updateInventory(player);
	 }
}

	@EventHandler
	  public void onCountLock(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    String itemflag = "count-lock";
	      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	    		  if(!ItemHandler.isAllowedItem(player, item, itemflag)) 
	      {
	    	  reAddItem(player, item);
	 }
   }
}
	 
	 public static void reAddItem(final Player player, final ItemStack inPlayerInventory) 
	  {
	       if (Utils.isConfigurable()) {
	       for (final String item : ConfigHandler.getConfigurationSection().getKeys(false))
	       {
	    	   ConfigurationSection items = ConfigHandler.getItemSection(item);
			if (items.getString(".slot") != null) {
				String slotlist = items.getString(".slot").replace(" ", "");
				String[] slots = slotlist.split(",");
				ItemHandler.clearItemID(player);
				for (final String slot: slots) {
		        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
		        {
		        public void run()
		          {
		        	String ItemID = ItemHandler.getItemID(player, slot);
					ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + ItemID + item);
					if (Utils.isCustomSlot(slot) && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
						SetItems.setCustomSlots(player, item, slot, ItemID);
					} else if (Utils.isInt(slot) && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
						SetItems.setInvSlots(player, item, slot, ItemID);
					}
			        PlayerHandler.updateInventory(player);
		          }
		      }, (long) 0.01);
	      	}
	      }
	  }
	}
  }
}

