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
import me.RockinChaos.itemjoin.handlers.AnimationHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.ItemCreator;

public class InvClickSurvival implements Listener {

	public static Map < String, Boolean > droppedItem = new HashMap < String, Boolean > ();
	public static Map < String, Boolean > dropClick = new HashMap < String, Boolean > ();

	@EventHandler
	public void onSurvivalInventoryModify(InventoryClickEvent event) {
		String itemflag = "inventory-modify";
		final Player player = (Player) event.getWhoClicked();
		if (!PlayerHandler.isCreativeMode(player)) {
			ItemStack item = null;
			if (!ServerHandler.hasAltUpdate("1_8")) {
				PlayerHandler.updateInventory(player);
			}
			if (ItemHandler.containsIgnoreCase(event.getAction().name(), "HOTBAR")) {
				item = event.getView().getBottomInventory().getItem(event.getHotbarButton());
				if (item == null) { item = event.getCurrentItem(); }
			} else { item = event.getCurrentItem(); }
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
    public final void onCursorAnimatedItem(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		String itemflag = "inventory-modify";
    	if (event.getAction().toString().contains("PLACE_ALL") || event.getAction().toString().contains("PLACE_ONE")) {
    		if (ItemHandler.isSimilar(event.getCursor(), AnimationHandler.cursorItem.get(PlayerHandler.getPlayerID(player)))) {
    		final int slot = event.getSlot();
    		event.setCancelled(true);
    		player.setItemOnCursor(new ItemStack(Material.AIR));
    		if (event.getRawSlot() <= 4 && event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getTopInventory().getSize() - 1 >= event.getRawSlot()) {
    			player.getOpenInventory().getTopInventory().setItem(slot, AnimationHandler.cursorItem.get(PlayerHandler.getPlayerID(player)));
    		} else { player.getInventory().setItem(slot, AnimationHandler.cursorItem.get(PlayerHandler.getPlayerID(player)));   }
			AnimationHandler.cursorItem.remove(PlayerHandler.getPlayerID(player));
			ServerHandler.sendDebugMessage("Updated Animation Item (Cursor) Code: 2565CV"); 
    		}
    	} else if (event.getAction().toString().contains("SWAP_WITH_CURSOR")) {
    		if (ItemHandler.isSimilar(event.getCursor(), AnimationHandler.cursorItem.get(PlayerHandler.getPlayerID(player))) && ItemHandler.isAllowedItem(player, event.getCurrentItem(), itemflag)) {
    		final int slot = event.getSlot();
    		final ItemStack item = new ItemStack(event.getCurrentItem());
    		event.setCancelled(true);
    		player.setItemOnCursor(item);
    		if (event.getRawSlot() <= 4 && event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() != InventoryType.CRAFTING && player.getOpenInventory().getTopInventory().getSize() - 1 >= event.getRawSlot()) {
    			player.getOpenInventory().getTopInventory().setItem(slot, AnimationHandler.cursorItem.get(PlayerHandler.getPlayerID(player)));
    		} else { player.getInventory().setItem(slot, AnimationHandler.cursorItem.get(PlayerHandler.getPlayerID(player)));   }
			AnimationHandler.cursorItem.remove(PlayerHandler.getPlayerID(player));
			ServerHandler.sendDebugMessage("Updated Animation Item (Cursor) Code: 6745CV"); 
    		}
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