package me.RockinChaos.itemjoin.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

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
				dropClick.put(player.getName(), true);
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

	public static void CustomDropEvent(final Player player, final ItemStack[] Inv, final ItemStack[] Armor) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			public void run() {
				if (dropClick.get(player.getName()) != null && dropClick.get(player.getName()) == true 
					&& droppedItem.get(player.getName()) != null && droppedItem.get(player.getName()) == true) {
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
					droppedItem.remove(player.getName());
				}
				dropClick.remove(player.getName());
			}
		}, 1L);
	}
}