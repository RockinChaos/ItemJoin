package me.RockinChaos.itemjoin.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class InvClickSurvival implements Listener {

	public static Map < String, Boolean > droppedItem = new HashMap < String, Boolean > ();
	public static Map < String, Boolean > dropClick = new HashMap < String, Boolean > ();
	public static HashMap < String, ItemStack > cursorItem = new HashMap < String, ItemStack > ();
	
	@EventHandler
	private void onGlobalInventoryModify(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		  
	  	if (Utils.containsIgnoreCase(ConfigHandler.isPreventItemMovement(), "true") || Utils.containsIgnoreCase(ConfigHandler.isPreventItemMovement(), player.getWorld().getName())
		  			|| Utils.containsIgnoreCase(ConfigHandler.isPreventItemMovement(), "ALL") || Utils.containsIgnoreCase(ConfigHandler.isPreventItemMovement(), "GLOBAL")) {
	  		if (ConfigHandler.isPreventAllowOpBypass() && player.isOp() || ConfigHandler.isPreventAllowCreativeBypass() && PlayerHandler.isCreativeMode(player)) { } 
	  		else { event.setCancelled(true); }
	  	}
	}

	@EventHandler
	private void onSurvivalInventoryModify(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
			ItemStack item = null;
			if (!ServerHandler.hasSpecificUpdate("1_8")) {
				PlayerHandler.updateInventory(player);
			}
			if (Utils.containsIgnoreCase(event.getAction().name(), "HOTBAR")) {
				item = event.getView().getBottomInventory().getItem(event.getHotbarButton());
				if (item == null) { item = event.getCurrentItem(); }
			} else { item = event.getCurrentItem(); }
			if (!ServerHandler.hasCombatUpdate()) {
				dropClick.put(PlayerHandler.getPlayerID(player), true);
				ItemStack[] Inv = player.getInventory().getContents().clone();
				ItemStack[] Armor = player.getInventory().getArmorContents().clone();
				LegacyDropEvent(player, Inv, Armor);
			}
			if (!ItemUtilities.isAllowed(player, item, "inventory-modify")) {
				event.setCancelled(true);
				if (PlayerHandler.isCreativeMode(player)) { player.closeInventory(); }
				else if (!ItemUtilities.isAllowed(player, item, "inventory-close")) { player.closeInventory(); }
				PlayerHandler.updateInventory(player);
			}
	}
	
    @EventHandler()
    private final void onCursorAnimatedItem(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		String itemflag = "inventory-modify";
    	if (event.getAction().toString().contains("PLACE_ALL") || event.getAction().toString().contains("PLACE_ONE")) {
    		ItemMap itemMap = ItemUtilities.getMappedItem(event.getCursor(), player.getWorld());
    		if (itemMap != null && itemMap.isSimilar(cursorItem.get(PlayerHandler.getPlayerID(player)))) {
    		final int slot = event.getSlot();
    		event.setCancelled(true);
    		player.setItemOnCursor(new ItemStack(Material.AIR));
    		if (event.getRawSlot() <= 4 && event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getTopInventory().getSize() - 1 >= event.getRawSlot()) {
    			player.getOpenInventory().getTopInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));
    		} else { player.getInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));   }
			cursorItem.remove(PlayerHandler.getPlayerID(player));
			ServerHandler.sendDebugMessage("Updated Animation Item (Cursor) Code: 2565CV"); 
    		}
    	} else if (event.getAction().toString().contains("SWAP_WITH_CURSOR")) {
    		ItemMap itemMap = ItemUtilities.getMappedItem(event.getCursor(), player.getWorld());
    		if (itemMap != null && itemMap.isSimilar(cursorItem.get(PlayerHandler.getPlayerID(player))) && ItemUtilities.isAllowed(player, event.getCurrentItem(), itemflag)) {
    		final int slot = event.getSlot();
    		final ItemStack item = new ItemStack(event.getCurrentItem());
    		event.setCancelled(true);
    		player.setItemOnCursor(item);
    		if (event.getRawSlot() <= 4 && event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getTopInventory().getSize() - 1 >= event.getRawSlot()) {
    			player.getOpenInventory().getTopInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));
    		} else { player.getInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));   }
			cursorItem.remove(PlayerHandler.getPlayerID(player));
			ServerHandler.sendDebugMessage("Updated Animation Item (Cursor) Code: 6745CV"); 
    		}
    	}
    }

    private static void LegacyDropEvent(final Player player, final ItemStack[] Inv, final ItemStack[] Armor) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			public void run() {
				if (dropClick.get(PlayerHandler.getPlayerID(player)) != null && dropClick.get(PlayerHandler.getPlayerID(player)) == true 
					&& droppedItem.get(PlayerHandler.getPlayerID(player)) != null && droppedItem.get(PlayerHandler.getPlayerID(player)) == true) {
					player.getInventory().clear();
					player.getInventory().setHelmet(null);
					player.getInventory().setChestplate(null);
					player.getInventory().setLeggings(null);
					player.getInventory().setBoots(null);
					if (ServerHandler.hasCombatUpdate()) { player.getInventory().setItemInOffHand(null); }
					player.getInventory().setContents(Inv);
					player.getInventory().setArmorContents(Armor);
					PlayerHandler.updateInventory(player);
					droppedItem.remove(PlayerHandler.getPlayerID(player));
				}
				dropClick.remove(PlayerHandler.getPlayerID(player));
			}
		}, 1L);
	}
}