package me.RockinChaos.itemjoin.listeners;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

@SuppressWarnings("deprecation")
public class Legacy_Pickups implements Listener {

	  @EventHandler
	  public void Deprecated_onPickup(PlayerPickupItemEvent event) {
	  	Player player = event.getPlayer();
	  	if (ConfigHandler.getConfig("config.yml").getBoolean("Prevent-Pickups") == true) {
	  		if (ConfigHandler.getConfig("config.yml").getBoolean("AllowOPBypass") == true && player.isOp() 
	  				|| ConfigHandler.getConfig("config.yml").getBoolean("CreativeBypass") == true && PlayerHandler.isCreativeMode(player)) { } else {
	  			event.setCancelled(true);
	  		}
	  	}
	  }
}