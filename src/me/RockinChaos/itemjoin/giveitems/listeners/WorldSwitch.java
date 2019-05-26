package me.RockinChaos.itemjoin.giveitems.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class WorldSwitch implements Listener {

	@EventHandler
	private void giveOnWorldSwitch(PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		if (ConfigHandler.getDepends().authMeEnabled()) { setAuthenticating(player); } 
		else { setItems(player); }
	}
	
	private void setAuthenticating(final Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (ConfigHandler.getDepends().authMeEnabled() && fr.xephi.authme.api.v3.AuthMeApi.getInstance().isAuthenticated(player)) {
					setItems(player);
					this.cancel();
				}
			}
		}.runTaskTimer(ItemJoin.getInstance(), 0, 20);
	}
	
	private void setItems(final Player player) {
		ItemUtilities.safeSet(player, "World-Switch");
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			@Override
			public void run() {
				ItemUtilities.updateItems(player, false);
				String Probable = ItemUtilities.getProbabilityItem(player);
				final int session = Utils.getRandom(1, 100000);
				for (ItemMap item : ItemUtilities.getItems()) { 
					if (item.isGiveOnWorldChange() && item.inWorld(player.getWorld()) 
							&& ItemUtilities.isChosenProbability(item, Probable) && ConfigHandler.getSQLData().isEnabled(player)
							&& item.hasPermission(player) && ItemUtilities.isObtainable(player, item, session)) {
						item.giveTo(player, false, 0);
					}
					item.setAnimations(player);
				}
				ItemUtilities.sendFailCount(player, session);
				PlayerHandler.delayUpdateInventory(player, 15L);
			}
		}, ConfigHandler.getItemDelay());
	}
}