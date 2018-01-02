package me.RockinChaos.itemjoin.listeners.giveitems;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.AnimationHandler;
import me.RockinChaos.itemjoin.handlers.CommandHandler;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.listeners.InvClickCreative;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Language;
import me.RockinChaos.itemjoin.utils.Utils;

public class PlayerJoin implements Listener {

	@EventHandler
	public void giveOnJoin(PlayerJoinEvent event) {
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
		SetItems.setClearingOfItems(player, player.getWorld().getName(), "Clear-On-Join");
		SetItems.setHeldItemSlot(player);
		CommandHandler.runGlobalCmds(player);
		SetItems.putFailCount(player, 0);
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			public void run() {
				setJoinItems(player);
				if (SetItems.getFailCount().get(player) != null && SetItems.getFailCount().get(player) != 0) {
					boolean Overwrite = ConfigHandler.getConfig("items.yml").getBoolean("items-Overwrite");
					if (Overwrite == true) {
						Language.getSendMessage(player, "failedInvFull", SetItems.getFailCount().get(player).toString());
					} else if (Overwrite == false) {
						Language.getSendMessage(player, "failedOverwrite", SetItems.getFailCount().get(player).toString());
						}
					SetItems.removeFailCount(player);
				}
				PlayerHandler.delayUpdateInventory(player, 15L);
				AnimationHandler.setAnimations(player);
			}
		}, delay);
	}
	
	public static void setJoinItems(Player player) {
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				final String world = player.getWorld().getName();
				if (WorldHandler.inWorld(items, world) && PermissionsHandler.hasPermission(items, item, player)) {
					if(ItemHandler.containsIgnoreCase(items.getString(".triggers"), "join") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "on-join") || items.getString(".triggers") == null) {
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