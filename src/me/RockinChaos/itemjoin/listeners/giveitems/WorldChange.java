package me.RockinChaos.itemjoin.listeners.giveitems;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Language;
import me.RockinChaos.itemjoin.utils.Utils;

public class WorldChange implements Listener {

	@EventHandler
	public void giveOnJoin(PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		if (Hooks.hasAuthMe == true) {
			runAuthMeStats(player);
		} else {
			setItems(player);
		}
	}
	
	public static void runAuthMeStats(final Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (fr.xephi.authme.AuthMe.getApi().isAuthenticated(player)) {
					setItems(player);
					this.cancel();
				}
			}
		}.runTaskTimer(ItemJoin.pl, 0, 20);
	}
	
	public static void setItems(final Player player) {
		final long delay = ConfigHandler.getConfig("items.yml").getInt("items-Delay") * 10L;
		CreateItems.run(player);
		SetItems.setClearingOfItems(player, player.getWorld().getName(), "Clear-On-WorldChanged");
		SetItems.failCount.put(player, 0);
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable() {
			public void run() {
				setJoinItems(player);
				if (SetItems.failCount.get(player) != 0) {
					boolean Overwrite = ConfigHandler.getConfig("items.yml").getBoolean("items-Overwrite");
					if (Overwrite == true) {
						Language.getSendMessage(player, "failedInvFull", SetItems.failCount.get(player).toString());
					} else if (Overwrite == false) {
						Language.getSendMessage(player, "failedOverwrite", SetItems.failCount.get(player).toString());
						}
					SetItems.failCount.remove(player);
				}
				PlayerHandler.delayUpdateInventory(player, 15L);
			}
		}, delay);
	}
	
	public static void setJoinItems(Player player) {
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				final String world = player.getWorld().getName();
				if (WorldHandler.inWorld(items, world) && PermissionsHandler.hasPermission(items, item, player)) {
					if(ItemHandler.containsIgnoreCase(items.getString(".triggers"), "world-changed") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "world-change")) {
					if (items.getString(".slot") != null) {
						String slotlist = items.getString(".slot").replace(" ", "");
						String[] slots = slotlist.split(",");
						ItemHandler.clearItemID(player);
						for (String slot: slots) {
							String ItemID = ItemHandler.getItemID(player, slot);
							if (Utils.isCustomSlot(slot)) {
								SetItems.setCustomSlots(player, item, slot, ItemID);
							} else if (Utils.isInt(slot)) {
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