/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.StringUtils;

public class Recipes implements Listener {
	
   /**
    * Prevents the player from using the custom item in a crafting recipe.
    * 
    * @param event - PrepareItemCraftEvent
    */
    @EventHandler(ignoreCancelled = true)
    private void onPlayerCraft(PrepareItemCraftEvent event) {
    	Player player = (Player) event.getInventory().getHolder();
    	for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
    		if (player.getOpenInventory().getTopInventory().getItem(i) != null && player.getOpenInventory().getTopInventory().getItem(i).getType() != Material.AIR) {
    			if (!ItemUtilities.getUtilities().isAllowed(player, player.getOpenInventory().getTopInventory().getItem(i), "item-craftable")) {
    				ItemStack reAdd = player.getOpenInventory().getTopInventory().getItem(i).clone();
    				player.getOpenInventory().getTopInventory().setItem(i, null);
    				player.getInventory().addItem(reAdd);
    				PlayerHandler.updateInventory(player, 1L);
    				break;
    			}
    		}
    	}
    }
  
   /**
    * Prevents the player from repairing or renaming the custom item in an anvil.
    * 
    * @param event - InventoryClickEvent
    */
	@EventHandler(ignoreCancelled = true)
	private void onRepairAnvil(InventoryClickEvent event) {
	    if (event.getInventory().getType().toString().contains("ANVIL")) {
	        Player player = (Player) event.getWhoClicked();
	        int rSlot = event.getSlot();
	        if (rSlot == 2 && event.getInventory().getItem(1) != null &&
	            event.getInventory().getItem(1).getType() != Material.AIR) {
	            ItemStack item = event.getInventory().getItem(2);
	            if (!StringUtils.getUtils().containsIgnoreCase(event.getInventory().getItem(1).getType().toString(), "PAPER") && !StringUtils.getUtils().containsIgnoreCase(event.getInventory().getItem(1).getType().toString(), "NAME_TAG") &&
	                !ItemUtilities.getUtilities().isAllowed(player, item, "item-repairable") || !ItemUtilities.getUtilities().isAllowed(player, event.getInventory().getItem(1), "item-repairable")) {
	                event.setCancelled(true);
	                PlayerHandler.updateExperienceLevels(player);
	                PlayerHandler.updateInventory(player, 1L);
	            }
	        }
	    }
	}
	
   /**
	* Called when the player tries to craft a recipe with a custom item.
	* 
	* @param event - PrepareItemCraftEvent
	*/
    @EventHandler()
    public void onPrepareRecipe(PrepareItemCraftEvent event) {
    	if (event.getRecipe() != null && event.getRecipe().getResult() != null && event.getRecipe().getResult().getType() != Material.AIR) {
	    	ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getRecipe().getResult(), null, event.getView().getPlayer().getWorld());
	    	if (itemMap != null && itemMap.getIngredients() != null && !itemMap.getIngredients().isEmpty()) {
	    		for (int i = 0; i < event.getInventory().getSize(); i++ ){
	    			final ItemStack item = event.getInventory().getItem(i + 1);
	    			if (item != null) {
	    				for (Character ingredient: itemMap.getIngredients().keySet()) {
	    					ItemMap ingredMap = ItemUtilities.getUtilities().getItemMap(null, itemMap.getIngredients().get(ingredient) , null);
	    					if (ingredMap != null && itemMap.getRecipe().size() > i && itemMap.getRecipe().get(i) == ingredient && !ingredMap.isSimilar(item)) {
	    						event.getInventory().setResult(new ItemStack(Material.AIR));
	    						break;
	    					}
	    				}
	    			}
				}
	    	}
    	}
    }
}