package me.RockinChaos.itemjoin.giveitems.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.MemoryHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class Respawn implements Listener {

	@EventHandler
	private void giveOnRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		if (MemoryHandler.isAuthMe() == true) { setAuthenticating(player); } 
		else { setItems(player); }
	}
	
	private void setAuthenticating(final Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (MemoryHandler.isAuthMe() == true && fr.xephi.authme.api.v3.AuthMeApi.getInstance().isAuthenticated(player)) {
					setItems(player);
					this.cancel();
				}
			}
		}.runTaskTimer(ItemJoin.getInstance(), 0, 20);
	}
	
	private void setItems(final Player player) {
		ItemUtilities.safeSet(player, "Respawn");
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			@Override
			public void run() {
				ItemUtilities.updateItems(player, false);
				String Probable = ItemUtilities.getProbabilityItem(player);
				final int session = Utils.getRandom(1, 100000);
				for (ItemMap item : ItemUtilities.getItems()) { 
					if (item.isGiveOnRespawn() && item.inWorld(player.getWorld()) 
							&& ItemUtilities.isChosenProbability(item, Probable) && MemoryHandler.getSQLData().isEnabled(player)
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