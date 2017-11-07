package me.RockinChaos.itemjoin.listeners;

import java.util.List;
import java.util.ListIterator;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class Drops implements Listener {

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		final ItemStack item = event.getItemDrop().getItemStack();
		final Player player = event.getPlayer();
		String itemflag = "self-drops";
		if (!ItemHandler.isAllowedItem(player, item, itemflag)) {
			if (!ServerHandler.hasCombatUpdate() && InvClickSurvival.dropClick.get(player.getName()) != null && InvClickSurvival.dropClick.get(player.getName()) == true) {
				InvClickSurvival.droppedItem.put(player.getName(), true);
				event.getItemDrop().remove();
			} else {
			event.setCancelled(true);
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
				public void run() {
					PlayerHandler.updateInventory(player);
				}
			}, 1L);
		} else if (!ServerHandler.hasCombatUpdate() && PlayerHandler.isCreativeMode(player) && InvClickSurvival.dropClick.get(player.getName()) != null 
				&& InvClickSurvival.dropClick.get(player.getName()) == true && ItemHandler.isAllowedItem(player, item, itemflag)) {
			InvClickCreative.dropGlitch.put(player.getName(), true);
		}
	}

	@EventHandler
	public void onDeathDrops(PlayerDeathEvent event) {
		List < ItemStack > drops = event.getDrops();
		ListIterator < ItemStack > litr = drops.listIterator();
		final Player player = event.getEntity();
		String itemflag = "death-drops";
		while (litr.hasNext()) {
			ItemStack stack = litr.next();
			if (!ItemHandler.isAllowedItem(player, stack, itemflag)) {
				litr.remove();
			}
		}
	}
}