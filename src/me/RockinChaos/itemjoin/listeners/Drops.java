package me.RockinChaos.itemjoin.listeners;

import java.util.List;
import java.util.ListIterator;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;

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
	    String itemflag = "self-drops";
	      if (!ItemHandler.isAllowedItem(player, item, itemflag))
	      {
	        event.setCancelled(true);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
	        public void run()
	          {
	        	PlayerHandler.updateInventory(player);
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
	    String itemflag = "death-drops";
        while(litr.hasNext()){
            ItemStack stack = litr.next();
	      if (!ItemHandler.isAllowedItem(player, stack, itemflag))
	      {
	    	  litr.remove();
	    }
   }
  }
}
