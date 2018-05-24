package me.RockinChaos.itemjoin.listeners.giveitems;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.AnimationHandler;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.listeners.InvClickCreative;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

public class WorldChange implements Listener {

	@EventHandler
	public void giveOnJoin(PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		if(RegionEnter.getInRegion().get(player) != null) {
		RegionEnter.removeInRegion(player);
		}
		if (Hooks.hasAuthMe() == true) {
			runAuthMeStats(player);
		} else {
			setItems(player);
		}
	}
	
	public static void runAuthMeStats(final Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (fr.xephi.authme.api.v3.AuthMeApi.getInstance().isAuthenticated(player)) {
					setItems(player);
					this.cancel();
				}
			}
		}.runTaskTimer(ItemJoin.getInstance(), 0, 20);
	}
	
	public static void setItems(final Player player) {
		final long delay = ConfigHandler.getConfig("items.yml").getInt("items-Delay") * 10L;
		CreateItems.run(player);
		InvClickCreative.isCreative(player, player.getGameMode());
		SetItems.setClearingOfItems(player, player.getWorld().getName(), "Clear-On-WorldChanged");
		SetItems.setHeldItemSlot(player);
		SetItems.putFailCount(player, 0);
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			public void run() {
				setJoinItems(player);
				SetItems.itemsOverwrite(player);
				PlayerHandler.delayUpdateInventory(player, 15L);
				AnimationHandler.OpenAnimations(player);
			}
		}, delay);
	}
	
	public static void setJoinItems(Player player) {
		String nameProbability = SetItems.setProbabilityItems(player);
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				final String world = player.getWorld().getName();
				if (WorldHandler.inWorld(items, world) && PermissionsHandler.hasItemsPermission(items, item, player) && SQLData.isEnabled(player)) {
					if(ItemHandler.containsIgnoreCase(items.getString(".triggers"), "world-changed") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "world-change")) {
				    if (items.getString(".probability") != null && item.equalsIgnoreCase(nameProbability) || items.getString(".probability") == null) {
					if (items.getString(".slot") != null) {
						String slotlist = items.getString(".slot").replace(" ", "");
						String[] slots = slotlist.split(",");
						ItemHandler.clearItemID(player);
						for (String slot: slots) {
							String ItemID = ItemHandler.getItemID(player, slot);
							ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
							if (Utils.isCustomSlot(slot) && ItemHandler.isObtainable(player, items, item, slot, ItemID, inStoredItems)) {
								SetItems.setCustomSlots(player, item, slot, ItemID);
							} else if (Utils.isInt(slot) && ItemHandler.isObtainable(player, items, item, slot, ItemID, inStoredItems)) {
								SetItems.setInvSlots(player, item, slot, ItemID);
							}
						}
					}
				}
			  }
			}
		}
	  }
	}
}