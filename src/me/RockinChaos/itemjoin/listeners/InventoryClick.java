package me.RockinChaos.itemjoin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class InventoryClick implements Listener {

	@EventHandler
	public void onInventoryModify(InventoryClickEvent event) {
		String itemflag = "inventory-modify";
		Player player = (Player) event.getWhoClicked();
		GameMode gamemode = player.getGameMode();
		GameMode creative = GameMode.CREATIVE;
		ItemStack item = null;
		if (gamemode != creative && !ServerHandler.hasViableUpdate()) {
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
		if (!ItemHandler.isAllowedItem(player, item, itemflag)) {
			if (gamemode == creative) {
				player.closeInventory();
				setCancelled(player, item);
			} else {
				event.setCancelled(true);
				PlayerHandler.updateInventory(player);
			}
		}
	}

	public void setCancelled(final Player player, final ItemStack item) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable() {
			public void run() {
				player.getInventory().removeItem(item);
				if (ItemHandler.isSimilar(player.getInventory().getHelmet(), item)) {
					player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
				}
				if (ItemHandler.isSimilar(player.getInventory().getChestplate(), item)) {
					player.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
				}
				if (ItemHandler.isSimilar(player.getInventory().getLeggings(), item)) {
					player.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
				}
				if (ItemHandler.isSimilar(player.getInventory().getBoots(), item)) {
					player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
				}
				Placement.reAddItem(player, item);
			}
		}, (long) 0.01);
	}
}