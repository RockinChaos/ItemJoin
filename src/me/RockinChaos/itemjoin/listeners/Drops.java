package me.RockinChaos.itemjoin.listeners;

import java.util.List;
import java.util.ListIterator;

import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

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
		if (!player.isDead() && !ItemUtilities.isAllowed(player, item, "self-drops")) {
			if (PlayerHandler.isCreativeMode(player)) { player.closeInventory(); } 
			event.setCancelled(true);
		} else if (player.isDead() && !ItemUtilities.isAllowed(player, item, "self-drops")) {
			event.getItemDrop().remove();
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
	
	@EventHandler
	private void onGlobalDrop(PlayerDropItemEvent event) {
		final Player player = event.getPlayer();
	  	if (Utils.containsIgnoreCase(ConfigHandler.isPreventDrops(), "TRUE") || Utils.containsIgnoreCase(ConfigHandler.isPreventDrops(), player.getWorld().getName())
	  		|| Utils.containsIgnoreCase(ConfigHandler.isPreventDrops(), "ALL") || Utils.containsIgnoreCase(ConfigHandler.isPreventDrops(), "GLOBAL")) {
	  		if (ConfigHandler.isPreventOBypass() && player.isOp() || ConfigHandler.isPreventCBypass() && PlayerHandler.isCreativeMode(player)) { } 
	  		else { 
	  			if (!player.isDead()) {
	  				if (PlayerHandler.isCreativeMode(player)) { player.closeInventory(); } 
					event.setCancelled(true);
	  			} else if (player.isDead()) {
					event.getItemDrop().remove();
				} 
	  		}
	  	}
	}
	
	@EventHandler
	private void onGlobalDeathDrops(PlayerDeathEvent event) {
		List < ItemStack > drops = event.getDrops();
		ListIterator < ItemStack > litr = drops.listIterator();
		Player player = event.getEntity();
		ItemUtilities.closeAnimations(player);
		if (Utils.containsIgnoreCase(ConfigHandler.isPreventDeathDrops(), "TRUE") || Utils.containsIgnoreCase(ConfigHandler.isPreventDeathDrops(), player.getWorld().getName())
		  		|| Utils.containsIgnoreCase(ConfigHandler.isPreventDeathDrops(), "ALL") || Utils.containsIgnoreCase(ConfigHandler.isPreventDeathDrops(), "GLOBAL")) {
	  		if (ConfigHandler.isPreventOBypass() && player.isOp() || ConfigHandler.isPreventCBypass() && PlayerHandler.isCreativeMode(player)) { }
	  		else {
	  			while (litr.hasNext()) {
	  				litr.next(); litr.remove();
				}
	  		}
		}
	}
}