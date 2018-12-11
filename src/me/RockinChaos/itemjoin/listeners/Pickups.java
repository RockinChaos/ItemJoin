package me.RockinChaos.itemjoin.listeners;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class Pickups implements Listener {

	  @EventHandler
	  public void onPickup(EntityPickupItemEvent event) {
	  	Entity entity = event.getEntity();
	  	if (entity instanceof Player) {
	  		Player player = (Player) event.getEntity();
	  		if (ConfigHandler.getConfig("config.yml").getBoolean("Prevent-Pickups") == true) {
	  			if (ConfigHandler.getConfig("config.yml").getBoolean("AllowOPBypass") == true && player.isOp() 
	  					|| ConfigHandler.getConfig("config.yml").getBoolean("CreativeBypass") == true && PlayerHandler.isCreativeMode(player)) { } else {
	  				event.setCancelled(true);
	  			}
	  		}
	  	}
	  }
}