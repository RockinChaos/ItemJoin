package me.RockinChaos.itemjoin.listeners;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.listeners.giveitems.SetItems;
import me.RockinChaos.itemjoin.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
		public void onCountLock(PlayerInteractEvent event) {
			ItemStack item = event.getItem();
			final Player player = event.getPlayer();
			String itemflag = "count-lock";
			GameMode gamemode = player.getGameMode();
			GameMode creative = GameMode.CREATIVE;
			String handString = "";
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK && gamemode != creative) {
				if (!ItemHandler.isAllowedItem(player, item, itemflag)) {
					if (ServerHandler.hasCombatUpdate()) {
						handString = event.getHand().toString();
					}
					reAddItem(player, item, handString, itemflag);
				}
			}
		}
	 
		@EventHandler
	    public void onItemFramePlace(PlayerInteractEntityEvent event) {
	        if(event.getRightClicked() instanceof ItemFrame) {
	    	    ItemStack item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString());
	    	    final Player player = event.getPlayer();
	    	    String itemflag = "placement";
	    	      if (!ItemHandler.isAllowedItem(player, item, itemflag) && item.getType().isBlock())
	    	      {
	    	        event.setCancelled(true);
	    	        PlayerHandler.updateInventory(player);
	    	 }
	        }
	    }
		
		@EventHandler
	    public void onItemFrameCountLock(PlayerInteractEntityEvent event) {
	        if(event.getRightClicked() instanceof ItemFrame) {
	    	    ItemStack item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString());
	    	    final Player player = event.getPlayer();
	    	    String itemflag = "count-lock";
				GameMode gamemode = player.getGameMode();
				GameMode creative = GameMode.CREATIVE;
				String handString = "";
				if (gamemode != creative) {
					if (!ItemHandler.isAllowedItem(player, item, itemflag)) {
						if (ServerHandler.hasCombatUpdate()) {
							handString = event.getHand().toString();
						}
						reAddItem(player, item, handString, itemflag);
					}
				}
	        }
		}
		
	 public static void reAddItem(final Player player, final ItemStack inPlayerInventory, final String handString, final String type) 
	  {
	       if (Utils.isConfigurable()) {
	       for (final String item : ConfigHandler.getConfigurationSection().getKeys(false))
	       {
	    	   ConfigurationSection items = ConfigHandler.getItemSection(item);
			if (items.getString(".slot") != null && inPlayerInventory != null) {
				String slotlist = items.getString(".slot").replace(" ", "");
				String[] slots = slotlist.split(",");
				ItemHandler.clearItemID(player);
				for (final String slot: slots) {
		        	final String ItemID = ItemHandler.getItemID(player, slot);
					final ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + ItemID + item);
					final ItemStack inPlayerInv = new ItemStack(inPlayerInventory);
					inPlayerInv.setAmount(inStoredItems.getAmount());
		        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
		        {
		        public void run()
		          {
						if (!type.equalsIgnoreCase("InvClick") && inStoredItems != null && ItemHandler.isSimilar(inPlayerInv, inStoredItems)) {
					    PlayerHandler.setPerfectHandItem(player, inStoredItems, handString);
						} else if (type.equalsIgnoreCase("InvClick")) {
						if (Utils.isCustomSlot(slot) && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
							SetItems.setCustomSlots(player, item, slot, ItemID);
						} else if (Utils.isInt(slot) && inStoredItems != null && ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) {
							SetItems.setInvSlots(player, item, slot, ItemID);
						}
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

