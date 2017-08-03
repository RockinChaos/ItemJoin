package me.RockinChaos.itemjoin.listeners;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

@SuppressWarnings("deprecation")
public class Deprecated_Pickups implements Listener {

	  @EventHandler
	  public void Deprecated_onPickup(PlayerPickupItemEvent event)
	   {
		final Player player = event.getPlayer();
		boolean Creative = player.getGameMode() == GameMode.CREATIVE;
	    if(ConfigHandler.getConfig("config.yml").getBoolean("Prevent-Pickups") == true) {
	      if(ConfigHandler.getConfig("config.yml").getBoolean("AllowOPBypass") == true
	      		&& player.isOp()
	      		|| ConfigHandler.getConfig("config.yml").getBoolean("CreativeBypass") == true
	      		&& Creative) {
	       } else {
	         event.setCancelled(true);
	  	 }
	 }
}
}
