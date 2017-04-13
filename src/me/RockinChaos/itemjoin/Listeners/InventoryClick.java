package me.RockinChaos.itemjoin.Listeners;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.PlayerHandlers;
import me.RockinChaos.itemjoin.utils.CheckItem;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {
	
	@EventHandler
	public void onInventoryModify(InventoryClickEvent event) {
		String modifier = ".itemflags";
		String mod = "inventory-modify";
		Player player = (Player) event.getWhoClicked();
		String world = player.getWorld().getName();
		GameMode gamemode = player.getGameMode();
		GameMode creative = GameMode.CREATIVE;
		ItemStack item = null;
		if (event.getAction().name().contains("HOTBAR")) {
			item = event.getView().getBottomInventory().getItem(event.getHotbarButton());
			if (item == null) {
				item = event.getCurrentItem();
			}
		} else {
			item = event.getCurrentItem();
		}
		if (!CheckItem.isAllowedItem(player, world, item, modifier, mod)) {
			if (gamemode == creative) {
				setCancelled(player, item);
				for(Entity entities : player.getWorld().getEntities()) {
				if (entities instanceof Item) {
	                if( (CheckItem.isSimilar3(((Item) entities).getItemStack(), item, player))) {
	                	entities.remove();
	                }
				}
				}
				PlayerHandlers.updateInventory(player);
			} else {
				event.setCancelled(true);
				PlayerHandlers.updateInventory(player);
			}
		}
	}

public void setCancelled(final Player player, final ItemStack item) {
	Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable() {
		public void run() {
			player.getInventory().removeItem(item);
			for(Entity entities : player.getWorld().getEntities()) {
			if (entities instanceof Item) {
                if( (CheckItem.isSimilar3(((Item) entities).getItemStack(), item, player))) {
                	entities.remove();
                }
			}
			}
			if (CheckItem.isSimilar3(player.getInventory().getHelmet(), item, player)) {
				player.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
			}
			if (CheckItem.isSimilar3(player.getInventory().getChestplate(), item, player)) {
				player.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
			}
			if (CheckItem.isSimilar3(player.getInventory().getLeggings(), item, player)) {
				player.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
			}
			if (CheckItem.isSimilar3(player.getInventory().getBoots(), item, player)) {
				player.getInventory().setBoots(new ItemStack(Material.AIR, 1));
			}
			player.closeInventory();
			Placement.reAddItem(player, item);
		}
	}, (long) 0.01);
}
}
