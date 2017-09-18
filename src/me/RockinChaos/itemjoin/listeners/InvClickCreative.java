package me.RockinChaos.itemjoin.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class InvClickCreative implements Listener {
	private static HashMap < String, ItemStack[] > mySavedItems = new HashMap < String, ItemStack[] > ();
	private static HashMap < String, Boolean > isCreative = new HashMap < String, Boolean > ();
	private static HashMap < String, Boolean > isGlitchSwap = new HashMap < String, Boolean > ();
	private static HashMap < String, Integer > cooldown = new HashMap < String, Integer > ();

	@EventHandler
	public void onCreativeSwitch(final PlayerGameModeChangeEvent event) {
		final Player player = event.getPlayer();
		GameMode gamemode = event.getNewGameMode();
		isCreative(player, gamemode);
	}
	
	@EventHandler
	public void onCreativeInventoryModify(InventoryClickEvent event) {
		String itemflag = "inventory-modify";
		final Player player = (Player) event.getWhoClicked();
		Initialize(player);
		if (PlayerHandler.isCreativeMode(player)) {
			ItemStack item = null;
			if (cooldown.get(player.getName()) == 1) {
				cooldown.put(player.getName(), 1);
				event.setCancelled(true);
				player.getInventory().clear();
				player.getInventory().setHelmet(null);
				player.getInventory().setChestplate(null);
				player.getInventory().setLeggings(null);
				player.getInventory().setBoots(null);
				if (ServerHandler.hasCombatUpdate()) {
					player.getInventory().setItemInOffHand(null);
				}
					ItemStack readd = new ItemStack(event.getCursor());
					restoreInventory(player, readd);
					PlayerHandler.delayUpdateInventory(player, 5L);
			} else if (cooldown.get(player.getName()) != 1) {
				if (ItemHandler.containsIgnoreCase(event.getAction().name(), "HOTBAR")) {
					item = event.getView().getBottomInventory().getItem(event.getHotbarButton());
					if (item == null && event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
						item = event.getCursor();
					} else if (event.getCursor() == null) {
						item = event.getCurrentItem();
					}
				} else if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
					item = event.getCursor();
				} else {
					item = event.getCurrentItem();
				}
				if (event.getCurrentItem() != null && event.getCursor() != null && event.getCursor().getType() != Material.AIR && event.getCurrentItem().getType() != Material.AIR && !hasItem(player, event.getCursor())) {
					isGlitchSwap.put(player.getName(), true);
				}
				if (cooldown.get(player.getName()) != 1 && ItemHandler.isAllowedItem(player, item, itemflag)) {
					saveInventory(player);
				}
				if (!ItemHandler.isAllowedItem(player, item, itemflag) || event.getCurrentItem() != null && event.getCursor() != null && !hasItem(player, event.getCursor()) && !ItemHandler.isAllowedItem(player, event.getCurrentItem(), itemflag)) {
					cooldown.put(player.getName(), 1);
					event.setCancelled(true);
					player.getInventory().clear();
					player.getInventory().setHelmet(null);
					player.getInventory().setChestplate(null);
					player.getInventory().setLeggings(null);
					player.getInventory().setBoots(null);
					if (ServerHandler.hasCombatUpdate()) {
						player.getInventory().setItemInOffHand(null);
					}
						ItemStack readd = new ItemStack(event.getCursor());
						restoreInventory(player, readd);
						PlayerHandler.delayUpdateInventory(player, 5L);
				}
			}
		}
	}
	
	public static void setRunnable(final Player player) {
		new BukkitRunnable() {
			public void run() {
				if (player.isOnline() && PlayerHandler.isCreativeMode(player) && isCreative.get(player.getName()) == true) {
					saveInventory(player);
				} else if (isCreative.get(player.getName()) != true) {
					this.cancel();
				} else if (!player.isOnline()) {
					isCreative.put(player.getName(), false);
					this.cancel();
				}
			}
		}.runTaskTimerAsynchronously(ItemJoin.getInstance(), 20L, 20L);
	}

	public static void Initialize(Player player) {
		if (isCreative.get(player.getName()) == null) {
			isCreative.put(player.getName(), false);
		}
		if (isGlitchSwap.get(player.getName()) == null) {
			isGlitchSwap.put(player.getName(), false);
		}
		if (cooldown.get(player.getName()) == null) {
			cooldown.put(player.getName(), 0);
		}
	}

	public static void isCreative(Player player, GameMode gamemode) {
		GameMode creative = GameMode.CREATIVE;
		Initialize(player);
		if (isCreative.get(player.getName()) == false && gamemode == creative) {
			isCreative.put(player.getName(), true);
			setRunnable(player);
		} else if (gamemode != creative) {
			isCreative.put(player.getName(), false);
		}
	}

	public static boolean hasItems(Player player) {
		for (ItemStack item: player.getInventory().getContents()) {
			if (item != null) return true;
		}
		for (ItemStack item: player.getInventory().getArmorContents()) {
			if (item != null) return true;
		}
		return false;
	}

	public static boolean hasItem(Player player, ItemStack cursorItem) {
		boolean hasItem = false;
		if (mySavedItems.get(player.getName()) == null) {
			saveInventory(player);
		}
		if (mySavedItems.get(player.getName()) != null) {
		for (ItemStack inPlayerInventory: mySavedItems.get(player.getName())) {
			if (cursorItem != null && ItemHandler.isSimilar(inPlayerInventory, cursorItem) && ItemHandler.isCountSimilar(inPlayerInventory, cursorItem)) {
				hasItem = true;
				return hasItem;
			}
		}
		}
		return hasItem;
	}

	public static void saveInventory(final Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			public void run() {
				if (hasItems(player) && cooldown.get(player.getName()) != 1) {
					mySavedItems.put(player.getName(), player.getInventory().getContents());
				}
			}
		}, 1L);
	}

	private static void restoreInventory(final Player player, final ItemStack readd) {
		if (mySavedItems.get(player.getName()) != null) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), (Runnable) new Runnable() {
				public void run() {
					player.closeInventory();
					player.getInventory().setContents(mySavedItems.get(player.getName()));
					cooldown.put(player.getName(), 0);
					if (isGlitchSwap.get(player.getName()) == true) {
						isGlitchSwap.put(player.getName(), false);
						player.getInventory().addItem(readd);
						saveInventory(player);
					}
				}
			}, 3L);
		}
	}
}