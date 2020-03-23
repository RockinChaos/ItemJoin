package me.RockinChaos.itemjoin.giveitems.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ItemHandler;

public class PlayerQuit implements Listener {
	
	@EventHandler
	private void Quit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		ItemHandler.saveCraftItems(player);
		ItemHandler.removeCraftItems(player);
		ItemUtilities.closeAnimations(player);
	}
}