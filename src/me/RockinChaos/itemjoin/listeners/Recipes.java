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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import me.RockinChaos.itemjoin.item.ItemRecipe;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.api.LegacyAPI;

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
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
    				if (event.getInventory().getItem(i) != null ) {
    					inventoryClone.setItem(setSlot, event.getInventory().getItem(i).clone());
    				}
    				setSlot++;
    			}
    		}
	    	if (mapList != null && !mapList.isEmpty()) {
	    		for (ItemMap itemMap : mapList) {
	    			if (this.handleRecipe(itemMap, event.getInventory(), inventoryClone, event.getView(), false, false, null)) { break; }
	    		}
	    	} else if (checkMap != null) {
	    		this.handleRecipe(checkMap, event.getInventory(), inventoryClone, event.getView(), false, false, null);
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
    				if (event.getInventory().getItem(i) != null) {
    					inventoryClone.setItem(setSlot, event.getInventory().getItem(i).clone());
    				}
    				setSlot++;
    			}
    		}
    		this.handleRecipe(checkMap, event.getInventory(), inventoryClone, event.getView(), true, event.isShiftClick(), event);
    	}
    }
    
   /**
	* Handles the recipe examination check.
	*
	* @param itemMap - The itemMap being checked.
	* @param craftInventory - The direct CraftingInventory reference.
	* @param inventoryClone - The a clone of CraftingInventory reference.
	* @param view - The inventory view of the CraftingInventory.
	* @param isCrafted - If the event is a Crafted Event or Prepared Event.
	* @param isShiftClick - If the Player is Shift-Clicking to craft.
	* @return If the loop should break.
	*/
    private boolean handleRecipe(final ItemMap itemMap, final CraftingInventory craftInventory, final Inventory inventoryClone, final InventoryView view, final boolean isCrafted, final boolean isShiftClick, final CraftItemEvent event) {
    	if (!itemMap.hasPermission((Player) view.getPlayer(), view.getPlayer().getWorld())) {
    		craftInventory.setResult(new ItemStack(Material.AIR));
    	} else {
    		final ItemStack result = (craftInventory.getResult() != null ? craftInventory.getResult().clone() : new ItemStack(Material.AIR));
    		final boolean isLegacy = !ServerUtils.hasSpecificUpdate("1_13");
    		boolean success = false;
    		for (List<Character> recipe : itemMap.getRecipe()) {
    			if (!success) {
	    			boolean removed = false;
	    			int resultSize = 0;
	    			int ingredientSize = 0;
	    			int confirmations = 0;
		    		for (Character character: recipe) {
		    			if (character != 'X') {
		    				ingredientSize += 1;
		    			}
		    		}
		    		if (!isCrafted) {
		    			confirmations = this.getConfirmations(itemMap, inventoryClone, recipe);
		    		} else {
		    			boolean cycleShift = true;
		    			while (this.getConfirmations(itemMap, inventoryClone, recipe) == ingredientSize && cycleShift) {
		    				cycleShift = isShiftClick;
		    				for (int i = 0; i < inventoryClone.getSize(); i++) {
		    					final ItemStack item = inventoryClone.getItem(i);
		    					if (item != null) {
		    						for (Character ingredient: itemMap.getIngredients().keySet()) {
		    							final ItemRecipe itemRecipe = itemMap.getIngredients().get(ingredient);
		    							ItemMap ingredMap = ItemUtilities.getUtilities().getItemMap(null, itemRecipe.getMap(), null);
		    							if (recipe.size() > i && recipe.get(i) == ingredient) {
		    								if ((((ingredMap == null
		    										&& itemRecipe.getMaterial().equals(item.getType()) 
		    										&& (!isLegacy 
		    												|| (LegacyAPI.getDataValue(item) == itemRecipe.getData())))) 
		    										|| (ingredMap != null 
		    										&& ingredMap.isSimilar(item))) 
		    										&& item.getAmount() >= itemRecipe.getCount()) {
		    									int removal = (item.getAmount() - itemRecipe.getCount());
		    									if (removal <= 0) {
		    										craftInventory.setItem((i + 1), new ItemStack(Material.AIR));
		    										inventoryClone.setItem(i, new ItemStack(Material.AIR));
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
		    				resultSize++;
		    			}
		    		}
		    		if (!isCrafted && confirmations == ingredientSize) {
		    			success = true;
		    			craftInventory.setResult(itemMap.getItem((Player) view.getPlayer()));
		    			return true;
		    		} else if (!isCrafted) {
		    			craftInventory.setResult(new ItemStack(Material.AIR));
		    		} else if (isCrafted && removed) {
		    			if (resultSize > 0 && isShiftClick) {
		    				result.setAmount(resultSize);
		    			}
		    			event.setCancelled(true);
		    			if (isShiftClick) {
		    				success = true;
		    				view.getPlayer().getInventory().addItem(result);
		    			} else if (view.getPlayer().getOpenInventory().getCursor() != null && view.getPlayer().getOpenInventory().getCursor().isSimilar(result)) {
		    				success = true;
		    				view.getPlayer().getOpenInventory().getCursor().setAmount((view.getPlayer().getOpenInventory().getCursor().getAmount() + result.getAmount()));
		    			} else {
		    				success = true;
		    				view.getPlayer().getOpenInventory().setCursor(result);
		    			}
		    		}
		    	}
    		}
    	}
    	return false;
    }
    
   /**
	* Checks if the recipe is valid.
	*
	* @param itemMap - The itemMap being checked.
	* @param inventoryClone - The a clone of CraftingInventory reference.
	* @return If the loop should break.
	*/
    private int getConfirmations(final ItemMap itemMap, final Inventory inventoryClone, final List<Character> recipe) {
    	int confirmations = 0;
    	for (int i = 0; i < inventoryClone.getSize(); i++) {
    		final ItemStack item = inventoryClone.getItem(i);
    		if (item != null) {
    			for (Character ingredient: itemMap.getIngredients().keySet()) {
    				final ItemRecipe itemRecipe = itemMap.getIngredients().get(ingredient);
    				ItemMap ingredMap = ItemUtilities.getUtilities().getItemMap(null, itemRecipe.getMap(), null);
    				if (recipe.size() > i && recipe.get(i) == ingredient) {
    					if (((ingredMap == null && itemRecipe.getMaterial().equals(item.getType())) || (ingredMap != null && ingredMap.isSimilar(item))) && item.getAmount() >= itemRecipe.getCount()) {
    						confirmations += 1;
    					}
    				}
    			}
    		}
    	}
    	return confirmations;
    }
}