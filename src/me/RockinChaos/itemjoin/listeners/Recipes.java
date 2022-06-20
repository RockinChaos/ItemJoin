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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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
    private void onPlayerCraft(final PrepareItemCraftEvent event) {
    	Player player = (Player) event.getInventory().getHolder();
    	if (player != null) { 
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
    }
  
   /**
    * Prevents the player from repairing or renaming the custom item in an anvil.
    * 
    * @param event - InventoryClickEvent
    */
	@EventHandler(ignoreCancelled = true)
	private void onRepairAnvil(final InventoryClickEvent event) {
		final boolean isAnvil = event.getInventory().getType().toString().contains("ANVIL");
		final boolean isGrindstone = event.getInventory().getType().toString().contains("GRINDSTONE");
	    if (isAnvil || isGrindstone) {
	        Player player = (Player) event.getWhoClicked();
	        int rSlot = event.getSlot();
	        if (rSlot == 2 && event.getInventory().getItem(1) != null &&
	            event.getInventory().getItem(1).getType() != Material.AIR) {
	            ItemStack item = event.getInventory().getItem(2);
	            if ((isGrindstone || (!StringUtils.containsIgnoreCase(event.getInventory().getItem(1).getType().toString(), "PAPER") && !StringUtils.containsIgnoreCase(event.getInventory().getItem(1).getType().toString(), "NAME_TAG"))) &&
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
    public void onPrepareRecipe(final PrepareItemCraftEvent event) {
    	if (event.getRecipe() != null && event.getRecipe().getResult() != null 
    	 && event.getRecipe().getResult().getType() != Material.AIR && event.getView() != null && event.getView().getPlayer() != null) {
	    	List<ItemMap> mapList = new ArrayList<ItemMap>();
	    	ItemMap checkMap = ItemUtilities.getUtilities().getItemMap(event.getRecipe().getResult(), null, event.getView().getPlayer().getWorld());
	    	if (checkMap != null) { mapList.add(checkMap); } else { return; }
	    	for (ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
	    		if (itemMap != null && itemMap.getIngredients() != null && !itemMap.getIngredients().isEmpty()) {
	    			mapList.add(itemMap);
	    		}
	    	}
	    	if (mapList != null && !mapList.isEmpty()) {
	    		for (ItemMap itemMap : mapList) {
	    			if (this.handleRecipe(event, itemMap)) { break; }
	    		}
	    	} else if (checkMap != null) {
	    		this.handleRecipe(event, checkMap);
	    	}
    	}
    }
    
    private boolean handleRecipe(final PrepareItemCraftEvent event, final ItemMap itemMap) {
    	if (!itemMap.hasPermission((Player) event.getView().getPlayer(), event.getView().getPlayer().getWorld())) {
    		event.getInventory().setResult(new ItemStack(Material.AIR));
    	} else {
    		int ingredientSize = 0;
    		int confirmations = 0;
    		for (Character character: itemMap.getRecipe()) {
    			if (character != 'X') {
    				ingredientSize += 1;
    			}
    		}
    		for (int i = 0; i < event.getInventory().getSize(); i++) {
    			final ItemStack item = event.getInventory().getItem(i + 1);
    			if (item != null) {
    				for (Character ingredient: itemMap.getIngredients().keySet()) {
    					final Set < Entry < String, Integer >> materials = itemMap.getIngredients().get(ingredient).entrySet();
    					ItemMap ingredMap = ItemUtilities.getUtilities().getItemMap(null, materials.iterator().next().getKey(), null);
    					if (itemMap.getRecipe().size() > i && itemMap.getRecipe().get(i) == ingredient) {
    						if (((ingredMap == null && materials.iterator().next().getKey().equalsIgnoreCase(item.getType().name())) || (ingredMap != null && ingredMap.isSimilar(item))) && item.getAmount() == materials.iterator().next().getValue()) {
    							confirmations += 1;
    						}
    					}
    				}
    			}
    			if (confirmations == ingredientSize) {
    				event.getInventory().setResult(itemMap.getItem((Player) event.getView().getPlayer()));
    				return true;
    			} else {
    				event.getInventory().setResult(new ItemStack(Material.AIR));
    			}
    		}
    	}
    	return false;
    }
}