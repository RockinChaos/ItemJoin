package me.RockinChaos.itemjoin.listeners.giveitems;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.RockinChaos.itemjoin.handlers.AnimationHandler;

public class PlayerQuit implements Listener {
	
	@EventHandler
	public void closeOnQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		AnimationHandler.CloseAnimations(player);
	}
}
