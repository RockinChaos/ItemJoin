package me.RockinChaos.itemjoin.Listeners;

import java.util.List;
import java.util.ListIterator;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.WorldHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class Drops implements Listener {

	  @EventHandler(ignoreCancelled=true)
	  public void onDrop(PlayerDropItemEvent event) 
	  {
	    ItemStack item = event.getItemDrop().getItemStack();
	    final Player player = event.getPlayer();
	    String modifier = ".prevent-modifiers";
	    String mod = "self-drops";
	      if (!CheckItem.isAllowedItem(player, item, modifier, mod))
	      {
	        event.setCancelled(true);
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable()
	        {
	        @SuppressWarnings("deprecation")
			public void run()
	          {
	            player.updateInventory();
	          }
	      }, 1L);
   }
  }
	  @EventHandler(ignoreCancelled=true)
	  public void onDeathDrops(PlayerDeathEvent event) 
	  {
		  List<ItemStack> drops = event.getDrops();
		  ListIterator<ItemStack> litr = drops.listIterator();
	    final Player player = event.getEntity();
	    final String world = WorldHandler.getWorld(player.getWorld().getName());
	    String modifier = ".prevent-modifiers";
	    String mod = "death-drops";
	    if (WorldHandler.isWorld(world)) {
        while(litr.hasNext()){
            ItemStack stack = litr.next();
	      if (!CheckItem.isAllowedItem(player, stack, modifier, mod))
	      {
	    	  litr.remove();
	    }
    }
   }
  }
}
