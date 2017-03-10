package me.RockinChaos.itemjoin.Listeners;

import java.util.List;
import java.util.ListIterator;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.PlayerHandlers;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.CheckItem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class Drops implements Listener {

	@EventHandler
	  public void onDrop(PlayerDropItemEvent event) 
	  {
	    ItemStack item = event.getItemDrop().getItemStack();
	    final Player player = event.getPlayer();
	    final String world = player.getWorld().getName();
	    String modifier = ".itemflags";
	    String mod = "self-drops";
	      if (!CheckItem.isAllowedItem(player, world, item, modifier, mod))
	      {
	        event.setCancelled(true);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
	        public void run()
	          {
	        	PlayerHandlers.updateInventory(player);
	          }
	      }, 1L);
   }
  }
	  @EventHandler
	  public void onDeathDrops(PlayerDeathEvent event) 
	  {
		  List<ItemStack> drops = event.getDrops();
		  ListIterator<ItemStack> litr = drops.listIterator();
	    final Player player = event.getEntity();
	    final String world = player.getWorld().getName();
	    String modifier = ".itemflags";
	    String mod = "death-drops";
	    if (WorldHandler.isWorld(world)) {
        while(litr.hasNext()){
            ItemStack stack = litr.next();
	      if (!CheckItem.isAllowedItem(player, world, stack, modifier, mod))
	      {
	    	  litr.remove();
	    }
    }
   }
  }
}
