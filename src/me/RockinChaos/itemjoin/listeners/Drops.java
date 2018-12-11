package me.RockinChaos.itemjoin.listeners;

import java.util.List;
import java.util.ListIterator;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class Drops implements Listener {

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		Player player = event.getPlayer();
		if (!ItemHandler.isAllowed(player, item, "self-drops")) {
			if (!ServerHandler.hasCombatUpdate() && InvClickSurvival.dropClick.get(PlayerHandler.getPlayerID(player)) != null && InvClickSurvival.dropClick.get(PlayerHandler.getPlayerID(player)) == true) {
				InvClickSurvival.droppedItem.put(PlayerHandler.getPlayerID(player), true);
				event.getItemDrop().remove();
			} else { event.setCancelled(true); }
			PlayerHandler.delayUpdateInventory(player, 1L);
		} else if (!ServerHandler.hasCombatUpdate() && PlayerHandler.isCreativeMode(player) && InvClickSurvival.dropClick.get(PlayerHandler.getPlayerID(player)) != null && InvClickSurvival.dropClick.get(PlayerHandler.getPlayerID(player)) == true && !ItemHandler.isAllowed(player, item, "self-drops")) {
			InvClickCreative.dropGlitch.put(PlayerHandler.getPlayerID(player), true);
		}
	}

	@EventHandler
	public void onDeathDrops(PlayerDeathEvent event) {
		List < ItemStack > drops = event.getDrops();
		ListIterator < ItemStack > litr = drops.listIterator();
		Player player = event.getEntity();
		ItemHandler.closeAnimations(player);
		while (litr.hasNext()) {
			ItemStack stack = litr.next();
			if (!ItemHandler.isAllowed(player, stack, "death-drops")) { litr.remove(); }
		}
	}
}