package me.RockinChaos.itemjoin.Listeners;

import java.util.ArrayList;

import me.RockinChaos.itemjoin.utils.CheckItem;
import me.RockinChaos.itemjoin.utils.WorldHandler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class ItemsDamaged implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
	    final Player player = event.getPlayer();
	    ItemStack item = player.getInventory().getItemInMainHand();
	    String modifier = ".prevent-modifiers";
	    String frase = "item-damage-blocks";
	      if (!CheckItem.isAllowedItem(player, item, modifier, frase))
	      {
	    	item.setDurability((short) -1);
	        player.updateInventory();
	 }
}

	@SuppressWarnings("deprecation")
	@EventHandler
	  public void onWeaponArmorDamaged(EntityDamageByEntityEvent event) 
	  {
		 ItemStack item = null;
		 Player player = null;
		 if (event.getDamager() instanceof Player) {
	     item = ((Player) event.getDamager()).getInventory().getItemInMainHand();
	     player = (Player) event.getDamager();
	     WorldHandler.getWorld(player.getWorld().getName());
		 } else if (event.getEntity() instanceof Player) {
            ItemStack[] armor = ((Player) event.getEntity()).getInventory().getArmorContents();
             for (ItemStack i : armor){
                     item = i;
             }
			 player = (Player) event.getEntity();
			 WorldHandler.getWorld(player.getWorld().getName());
		 }
		final Player update = player;
	    String modifier = ".prevent-modifiers";
	    String frase = "item-damage-living";
	    if(event.getDamager() instanceof Player || event.getEntity() instanceof Player) {
	      if (!CheckItem.isAllowedItem(player, item, modifier, frase))
	      {
	    	  if (event.getDamager() instanceof Player) {
	    		  item.setDurability((short) -1);
            } else if (event.getEntity() instanceof Player) {
                    ItemStack[] armor = ((Player) event.getEntity()).getInventory().getArmorContents();
                    for (ItemStack i : armor){
                            i.setDurability((short) -1);
                    }
		            update.updateInventory();
            }
	 }
	  }
}

	 @SuppressWarnings("deprecation")
	@EventHandler
	  public void onBowShootDamaged(EntityShootBowEvent event) 
	  {
		if (event.getEntity() instanceof Player) {
	    ItemStack item = event.getBow();
	    final Player player = (Player) event.getEntity();
	    String modifier = ".prevent-modifiers";
	    String frase = "item-damage-living";
	    if(event.getEntity() instanceof Player) {
	      if (!CheckItem.isAllowedItem(player, item, modifier, frase))
	      {
          	item.setDurability((short) -1);
	        player.updateInventory();
	 }
	}
  }
	  }
	 
	@SuppressWarnings("deprecation")
	public boolean isTool(ItemStack item) {	
	 boolean isTool = false;
	 ArrayList<Integer> Tools = new ArrayList<Integer>();
	 Tools.add(268);
	 Tools.add(269);
	 Tools.add(270);
	 Tools.add(271);
	 Tools.add(290);
	 Tools.add(272);
	 Tools.add(273);
	 Tools.add(274);
	 Tools.add(275);
	 Tools.add(291);
	 Tools.add(267);
	 Tools.add(257);
	 Tools.add(256);
	 Tools.add(258);
	 Tools.add(292);
	 Tools.add(283);
	 Tools.add(284);
	 Tools.add(285);
	 Tools.add(286);
	 Tools.add(293);
	 Tools.add(276);
	 Tools.add(277);
	 Tools.add(278);
	 Tools.add(279);
	 Tools.add(294);
	 if (Tools.contains(item.getTypeId())) {
	   isTool = true;
	}
	return isTool; 
 }
}
