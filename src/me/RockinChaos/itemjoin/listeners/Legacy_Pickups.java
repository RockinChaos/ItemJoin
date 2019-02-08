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
  		if (Utils.containsIgnoreCase(ConfigHandler.isPreventPickups(), "true") || Utils.containsIgnoreCase(ConfigHandler.isPreventPickups(), player.getWorld().getName())
	  			|| Utils.containsIgnoreCase(ConfigHandler.isPreventPickups(), "ALL") || Utils.containsIgnoreCase(ConfigHandler.isPreventPickups(), "GLOBAL")) {
  			if (ConfigHandler.isPreventAllowOpBypass() && player.isOp() || ConfigHandler.isPreventAllowCreativeBypass() && PlayerHandler.isCreativeMode(player)) { } 
  			else { event.setCancelled(true); }
	  	}
	}
}