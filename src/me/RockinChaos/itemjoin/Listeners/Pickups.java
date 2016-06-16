package me.RockinChaos.itemjoin.Listeners;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.WorldHandler;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class Pickups implements Listener {

	  @EventHandler(ignoreCancelled=true)
	  public void onPickup(PlayerPickupItemEvent event)
	   {
		  final Player player = event.getPlayer();
		  final String world = WorldHandler.getWorld(player.getWorld().getName());
		  boolean Creative = player.getGameMode() == GameMode.CREATIVE;
	    if(ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Prevention." + "prevent-pickups") == true && WorldHandler.isWorld(world)){
	      if(ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Prevention." + "AllowOPBypass") == true
	      		&& player.isOp()
	      		|| ItemJoin.getSpecialConfig("items.yml").getBoolean("Global-Settings" + ".Prevention." + "CreativeBypass") == true
	      		&& Creative) {
	       } else {
	         event.setCancelled(true);
	  	 }
	 }
}
}
