package me.RockinChaos.itemjoin.listeners;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

@SuppressWarnings("deprecation")
public class Legacy_Pickups implements Listener {

	@EventHandler
	public void Deprecated_onGlobalPickup(PlayerPickupItemEvent event) {
	  	Player player = event.getPlayer();
	  	if (ConfigHandler.isPreventPickups()) {
	  		String worlds =ConfigHandler.getEnabledPreventWorlds();
	  		if (worlds == null || worlds.isEmpty() || Utils.containsIgnoreCase(worlds, "ALL") || Utils.containsIgnoreCase(worlds, "GLOBAL") || Utils.containsIgnoreCase(worlds, player.getWorld().getName())) {
	  			if (ConfigHandler.isPreventAllowOpBypass() && player.isOp() 
	  					|| ConfigHandler.isPreventAllowCreativeBypass() && PlayerHandler.isCreativeMode(player)) {} else {
	  				event.setCancelled(true);
	  			}
	  		}
	  	}
	}
}