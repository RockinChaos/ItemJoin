package me.RockinChaos.itemjoin.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Reflection;

public class InvClickSurvival implements Listener {

	public static Map < String, Boolean > droppedItem = new HashMap < String, Boolean > ();
	public static Map < String, Boolean > dropClick = new HashMap < String, Boolean > ();

	@EventHandler
	public void onSurvivalInventoryModify(InventoryClickEvent event) {
		String itemflag = "inventory-modify";
		final Player player = (Player) event.getWhoClicked();
		if (!PlayerHandler.isCreativeMode(player)) {
			ItemStack item = null;
			if (!ServerHandler.hasChangedTheWorldUpdate()) {
				PlayerHandler.updateInventory(player);
			}
			if (ItemHandler.containsIgnoreCase(event.getAction().name(), "HOTBAR")) {
				item = event.getView().getBottomInventory().getItem(event.getHotbarButton());
				if (item == null) {
					item = event.getCurrentItem();
				}
			} else {
				item = event.getCurrentItem();
			}
			if (!ServerHandler.hasCombatUpdate()) {
				dropClick.put(PlayerHandler.getPlayerID(player), true);
				ItemStack[] Inv = player.getInventory().getContents().clone();
				ItemStack[] Armor = player.getInventory().getArmorContents().clone();
				CustomDropEvent(player, Inv, Armor);
			}
			if (!ItemHandler.isAllowedItem(player, item, itemflag)) {
				event.setCancelled(true);
				PlayerHandler.updateInventory(player);
			}
		}
	}
	
    @EventHandler()
    public final void onMoveAnimatedItem(InventoryClickEvent event) { // Possible perma fix for ghost items if rapid animation + rapid move spam of item.
    	final Player player = (Player) event.getWhoClicked();
    	if (event.getAction().toString().contains("PLACE_ALL") && !ItemHandler.isAllowedItem(player, event.getCursor(), "animated")) {
    		final ItemStack item = new ItemStack(event.getCursor());
    		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), (Runnable) new Runnable() {
    			public void run() {
    				PlayerHandler.updateActualSlot(player, item, "PlayerInventory");
    				PlayerHandler.updateActualSlot(player, item, "OpenInventory");
    			}
    		}, (long) 0.001);
    	} else if (event.getAction().toString().contains("MOVE_TO_OTHER_INVENTORY") && !ItemHandler.isAllowedItem(player, event.getCurrentItem(), "animated")) {
    		final SlotType sl = event.getSlotType();
    		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), (Runnable) new Runnable() {
    			public void run() {
    				PlayerHandler.updateLocalizedSlots(player, "PlayerInventory");
    				if (sl == SlotType.CRAFTING || Reflection.getWindowID(player) != 0) {
    					PlayerHandler.updateLocalizedSlots(player, "OpenInventory");
    				}
    			}
    		}, (long) 0.01);
    	}
    }

	public static void CustomDropEvent(final Player player, final ItemStack[] Inv, final ItemStack[] Armor) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			public void run() {
				if (dropClick.get(PlayerHandler.getPlayerID(player)) != null && dropClick.get(PlayerHandler.getPlayerID(player)) == true 
					&& droppedItem.get(PlayerHandler.getPlayerID(player)) != null && droppedItem.get(PlayerHandler.getPlayerID(player)) == true) {
					player.getInventory().clear();
					player.getInventory().setHelmet(null);
					player.getInventory().setChestplate(null);
					player.getInventory().setLeggings(null);
					player.getInventory().setBoots(null);
					if (ServerHandler.hasCombatUpdate()) {
						player.getInventory().setItemInOffHand(null);
					}
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