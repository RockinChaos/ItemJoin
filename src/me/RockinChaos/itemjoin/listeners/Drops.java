package me.RockinChaos.itemjoin.listeners;

import java.util.List;
import java.util.ListIterator;

import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class Drops implements Listener {
	
	@EventHandler
	private void onDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		final Player player = event.getPlayer();
		if (!ItemUtilities.isAllowed(player, item, "self-drops")) {
			if (PlayerHandler.isCreativeMode(player)) { player.closeInventory(); } 
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void onDeathDrops(PlayerDeathEvent event) {
		List < ItemStack > drops = event.getDrops();
		ListIterator < ItemStack > litr = drops.listIterator();
		Player player = event.getEntity();
		ItemUtilities.closeAnimations(player);
		while (litr.hasNext()) {
			ItemStack stack = litr.next();
			if (!ItemUtilities.isAllowed(player, stack, "death-drops")) { litr.remove(); }
		}
	}
}