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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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
    		final Inventory inventoryClone = Bukkit.createInventory(null, 9);
    		int setSlot = 0;
    		for (int i = 0; i < event.getInventory().getSize(); i++) {
    			if (setSlot >= 9) { break; }
    			else if (!checkMap.isSimilar(event.getInventory().getItem(i))) {
    				inventoryClone.setItem(setSlot, event.getInventory().getItem(i).clone());
    				setSlot++;
    			}
    		}
	    	if (mapList != null && !mapList.isEmpty()) {
	    		for (ItemMap itemMap : mapList) {
	    			if (this.handleRecipe(itemMap, event.getInventory(), inventoryClone, event.getView(), false, false)) { break; }
	    		}
	    	} else if (checkMap != null) {
	    		this.handleRecipe(checkMap, event.getInventory(), inventoryClone, event.getView(), false, false);
	    	}
    	}
    }
    
   /**
	* Called when the player tries to craft a recipe with a custom item.
	* 
	* @param event - PrepareItemCraftEvent
	*/
    @EventHandler()
    public void onCraftRecipe(final CraftItemEvent event) {
    	final ItemMap checkMap = ItemUtilities.getUtilities().getItemMap(event.getRecipe().getResult(), null, event.getView().getPlayer().getWorld());
    	if (checkMap != null) {
    		final Inventory inventoryClone = Bukkit.createInventory(null, 18);
    		int setSlot = 0;
    		for (int i = 0; i < event.getInventory().getSize(); i++) {
    			if (setSlot >= 9) { break; }
    			else if (!checkMap.isSimilar(event.getInventory().getItem(i))) {
    				inventoryClone.setItem(setSlot, event.getInventory().getItem(i).clone());
    				setSlot++;
    			}
    			
    		}
    		this.handleRecipe(checkMap, event.getInventory(), inventoryClone, event.getView(), true, event.isShiftClick());
    	}
    }
    
   /**
	* Handles the recipe examination check.
	* 
	* @param event - PrepareItemCraftEvent
	* @param itemMap - The itemMap being checked.
	* @return If the loop should break.
	*/
    private boolean handleRecipe(final ItemMap itemMap, final CraftingInventory craftInventory, final Inventory inventoryClone, final InventoryView view, final boolean isCrafted, final boolean isShiftClick) {
    	if (!itemMap.hasPermission((Player) view.getPlayer(), view.getPlayer().getWorld())) {
    		craftInventory.setResult(new ItemStack(Material.AIR));
    	} else {
    		final ItemStack result = (craftInventory.getResult() != null ? craftInventory.getResult().clone() : new ItemStack(Material.AIR));
    		boolean removed = false;
    		int resultSize = 0;
    		int ingredientSize = 0;
    		int confirmations = 0;
    		for (Character character: itemMap.getRecipe()) {
    			if (character != 'X') {
    				ingredientSize += 1;
    			}
    		}
    		if (!isCrafted) { confirmations = this.getConfirmations(inventoryClone, itemMap); }
    		else { 
    			boolean cycleShift = true;
    			while (this.getConfirmations(inventoryClone, itemMap) == ingredientSize && cycleShift) {
    				cycleShift = isShiftClick;
		    		for (int i = 0; i < inventoryClone.getSize(); i++) {
		    			final ItemStack item = inventoryClone.getItem(i);
		    			if (item != null) {
		    				for (Character ingredient: itemMap.getIngredients().keySet()) {
		    					final Set < Entry < String, Integer >> materials = itemMap.getIngredients().get(ingredient).entrySet();
		    					ItemMap ingredMap = ItemUtilities.getUtilities().getItemMap(null, materials.iterator().next().getKey(), null);
		    					if (itemMap.getRecipe().size() > i && itemMap.getRecipe().get(i) == ingredient) {
		    						if (((ingredMap == null && materials.iterator().next().getKey().equalsIgnoreCase(item.getType().name())) || (ingredMap != null && ingredMap.isSimilar(item))) && item.getAmount() >= materials.iterator().next().getValue()) {
		    							if (!isCrafted) {
		    								confirmations += 1;
		    							} else {
		    								int removal = (item.getAmount() - materials.iterator().next().getValue());
		    								if (removal == materials.iterator().next().getValue()) {
		    									removal = removal + 1;
		    								}
		    								if (removal <= 0) { 
		    									craftInventory.getItem((i + 1)).setAmount(1);
		    									inventoryClone.getItem(i).setAmount(1);
		    									removed = true;
		    								} else {
		    									craftInventory.getItem((i + 1)).setAmount(removal);
		    									inventoryClone.getItem(i).setAmount(removal);
		    									removed = true;
		    								}
		    							}
		    						}
		    					}
		    				}
		    			}
		    			
		    		}
		    		resultSize++;
    			}
    		}
    			if (!isCrafted && confirmations == ingredientSize) {
    				craftInventory.setResult(itemMap.getItem((Player) view.getPlayer()));
    				return true;
    			} else if (!isCrafted) {
    				craftInventory.setResult(new ItemStack(Material.AIR));
    			} else if (isCrafted && removed) {
    				if (resultSize > 0 && isShiftClick) { result.setAmount(resultSize); }
    				craftInventory.setResult(result);
    			}
    	}
    	return false;
    }
    
    private int getConfirmations(final Inventory inventoryClone, final ItemMap itemMap) {
    	int confirmations = 0;
    		for (int i = 0; i < inventoryClone.getSize(); i++) {
    			final ItemStack item = inventoryClone.getItem(i);
    			if (item != null) {
    				for (Character ingredient: itemMap.getIngredients().keySet()) {
    					final Set < Entry < String, Integer >> materials = itemMap.getIngredients().get(ingredient).entrySet();
    					ItemMap ingredMap = ItemUtilities.getUtilities().getItemMap(null, materials.iterator().next().getKey(), null);
    					if (itemMap.getRecipe().size() > i && itemMap.getRecipe().get(i) == ingredient) {
    						if (((ingredMap == null && materials.iterator().next().getKey().equalsIgnoreCase(item.getType().name())) || (ingredMap != null && ingredMap.isSimilar(item))) && item.getAmount() >= materials.iterator().next().getValue()) {
    								confirmations += 1;
    							}
    						}
    					}
    				}
    			}
    		return confirmations;
    }
}