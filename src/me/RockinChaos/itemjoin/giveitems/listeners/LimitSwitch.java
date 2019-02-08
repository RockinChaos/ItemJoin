package me.RockinChaos.itemjoin.giveitems.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.MemoryHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class LimitSwitch implements Listener {

	@EventHandler
	private void giveOnGameModeSwitch(PlayerGameModeChangeEvent event) {
		final Player player = event.getPlayer();
		final GameMode newMode = event.getNewGameMode();
		if (RegionEnter.getPlayerRegions().get(player) != null) { RegionEnter.delPlayerRegion(player); }
		if (MemoryHandler.isAuthMe() == true) { setAuthenticating(player, newMode); } 
		else { setItems(player, newMode); }
	}
	
	private void setAuthenticating(final Player player, final GameMode newMode) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (MemoryHandler.isAuthMe() == true && fr.xephi.authme.api.v3.AuthMeApi.getInstance().isAuthenticated(player)) {
					setItems(player, newMode);
					this.cancel();
				}
			}
		}.runTaskTimer(ItemJoin.getInstance(), 0, 20);
	}
	
	private void setItems(final Player player, final GameMode newMode) {
		ItemUtilities.safeSet(player, "Limit-Modes");
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			public void run() {
				String Probable = ItemUtilities.getProbabilityItem(player);
				final int session = Utils.getRandom(1, 100000);
				for (ItemMap item : ItemUtilities.getItems()) { 
					if (item.isUseOnLimitSwitch() && item.inWorld(player.getWorld()) 
							&& ItemUtilities.isChosenProbability(item, Probable) && MemoryHandler.getSQLData().isEnabled(player)
							&& item.hasPermission(player) && ItemUtilities.isObtainable(player, item, session)) {
						item.giveTo(player, false, 0); 
						item.setAnimations(player);
					} else if (item.isUseOnLimitSwitch() && !item.isLimitMode(newMode) && item.inWorld(player.getWorld()) && item.hasItem(player)) {
						item.removeFrom(player);
					}
				}
				ItemUtilities.sendFailCount(player, session);
				PlayerHandler.delayUpdateInventory(player, 15L);
			}
		}, 1L);
	}
}
