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

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.CompatUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemRecipe;
import me.RockinChaos.itemjoin.item.ItemUtilities;
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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Recipes implements Listener {

    /**
     * Prevents the player from using the custom item in a crafting recipe.
     *
     * @param event - PrepareItemCraftEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onPlayerCraft(final PrepareItemCraftEvent event) {
        final Player player = (Player) event.getInventory().getHolder();
        if (player != null) {
            final Inventory topInventory = CompatUtils.getTopInventory(player);
            for (int i = 0; i < topInventory.getSize(); i++) {
                final ItemStack item = topInventory.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    if (!ItemUtilities.getUtilities().isAllowed(player, item, "item-craftable")) {
                        ItemStack reAdd = item.clone();
                        topInventory.setItem(i, null);
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
            final Player player = (Player) event.getWhoClicked();
            int rSlot = event.getSlot();
            final ItemStack invItem = event.getInventory().getItem(1);
            if (rSlot == 2 && invItem != null && invItem.getType() != Material.AIR) {
                ItemStack item = event.getInventory().getItem(2);
                if ((isGrindstone || (!StringUtils.containsIgnoreCase(invItem.getType().toString(), "PAPER") && !StringUtils.containsIgnoreCase(invItem.getType().toString(), "NAME_TAG"))) &&
                        !ItemUtilities.getUtilities().isAllowed(player, item, "item-repairable") || !ItemUtilities.getUtilities().isAllowed(player, invItem, "item-repairable")) {
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
        if (event.getRecipe() != null) {
            if (event.getRecipe().getResult().getType() != Material.AIR) {
                final Player player = CompatUtils.getPlayer(event.getView());
                final List<ItemMap> mapList = new ArrayList<>();
                final ItemMap checkMap = ItemUtilities.getUtilities().getItemMap(event.getRecipe().getResult());
                if (checkMap != null && checkMap.isRecipe()) {
                    mapList.add(checkMap);
                } else {
                    return;
                }
                for (ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
                    if (itemMap != null && itemMap.isRecipe()) {
                        mapList.add(itemMap);
                    }
                }
                final Inventory inventoryClone = Bukkit.createInventory(null, 9);
                int setSlot = 0;
                for (int i = 0; i < event.getInventory().getSize(); i++) {
                    final ItemStack item = event.getInventory().getItem(i);
                    if (setSlot >= 9) {
                        break;
                    } else if (i > 0 || !checkMap.isSimilar(player, item)) {
                        if (item != null) {
                            inventoryClone.setItem(setSlot, item);
                        }
                        setSlot++;
                    }
                }
                if (!mapList.isEmpty()) {
                    for (ItemMap itemMap : mapList) {
                        if (this.handleRecipe(itemMap, event.getInventory(), inventoryClone, player, false, false, null)) {
                            break;
                        }
                    }
                } else {
                    this.handleRecipe(checkMap, event.getInventory(), inventoryClone, player, false, false, null);
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
    public void onCraftRecipe(final CraftItemEvent event) {
        final ItemMap checkMap = ItemUtilities.getUtilities().getItemMap(event.getRecipe().getResult());
        if (checkMap != null && checkMap.isRecipe()) {
            final Inventory inventoryClone = Bukkit.createInventory(null, 18);
            int setSlot = 0;
            for (int i = 0; i < event.getInventory().getSize(); i++) {
                final ItemStack item = event.getInventory().getItem(i);
                if (setSlot >= 9) {
                    break;
                } else if (!checkMap.isSimilar(CompatUtils.getPlayer(event.getView()), item)) {
                    if (item != null) {
                        inventoryClone.setItem(setSlot, item.clone());
                    }
                    setSlot++;
                }
            }
            this.handleRecipe(checkMap, event.getInventory(), inventoryClone, CompatUtils.getPlayer(event.getView()), true, event.isShiftClick(), event);
        }
    }

    /**
     * Handles the recipe examination check.
     *
     * @param itemMap        - The itemMap being checked.
     * @param craftInventory - The direct CraftingInventory reference.
     * @param inventoryClone - The clone of CraftingInventory reference.
     * @param player         - The player being referenced.
     * @param isCrafted      - If the event is a Crafted Event or Prepared Event.
     * @param isShiftClick   - If the Player is Shift-Clicking to craft.
     * @return If the loop should break.
     */
    private boolean handleRecipe(final ItemMap itemMap, final CraftingInventory craftInventory, final Inventory inventoryClone, final Player player, final boolean isCrafted, final boolean isShiftClick, final CraftItemEvent event) {
        if (!itemMap.hasPermission(player, player.getWorld())) {
            craftInventory.setResult(new ItemStack(Material.AIR));
        } else {
            final ItemStack result = (craftInventory.getResult() != null ? craftInventory.getResult().clone() : new ItemStack(Material.AIR));
            final boolean isLegacy = !ServerUtils.hasSpecificUpdate("1_13");
            boolean success = false;
            for (final List<Character> tempRecipe : itemMap.getRecipe()) {
                if (!success) {
                    boolean removed = false;
                    int resultSize = 0;
                    int ingredientSize = 0;
                    int confirmations = 0;
                    for (Character character : tempRecipe) {
                        if (character != 'X') {
                            ingredientSize += 1;
                        }
                    }
                    if (!isCrafted) {
                        confirmations = this.getConfirmations(itemMap, player, inventoryClone);
                    } else {
                        boolean cycleShift = true;
                        while (this.getConfirmations(itemMap, player, inventoryClone) == ingredientSize && cycleShift) {
                            cycleShift = isShiftClick;
                            for (int i = 0; i < inventoryClone.getSize(); i++) {
                                final ItemStack item = inventoryClone.getItem(i);
                                if (item != null) {
                                    for (Character ingredient : itemMap.getIngredients().keySet()) {
                                        final ItemRecipe itemRecipe = itemMap.getIngredients().get(ingredient);
                                        ItemMap ingredMap = ItemUtilities.getUtilities().getItemMap(itemRecipe.getMap());
                                        if ((((ingredMap == null
                                                && itemRecipe.getMaterial().equals(item.getType())
                                                && (!isLegacy || (LegacyAPI.getDataValue(item) == itemRecipe.getData()))))
                                                || (ingredMap != null
                                                && ingredMap.isSimilar(player, item)))
                                                && item.getAmount() >= itemRecipe.getCount()) {
                                            int removal = (item.getAmount() - itemRecipe.getCount());
                                            if (removal <= 0) {
                                                craftInventory.setItem((i + 1), new ItemStack(Material.AIR));
                                                inventoryClone.setItem(i, new ItemStack(Material.AIR));
                                            } else {
                                                final ItemStack craftItem = craftInventory.getItem((i + 1));
                                                if (craftItem != null) {
                                                    craftItem.setAmount(removal);
                                                    item.setAmount(removal);
                                                }
                                            }
                                            removed = true;
                                        }
                                    }
                                }
                            }
                            resultSize++;
                        }
                    }
                    if (!isCrafted && confirmations == ingredientSize) {
                        craftInventory.setResult(itemMap.getItem(player));
                        return true;
                    } else if (!isCrafted) {
                        craftInventory.setResult(new ItemStack(Material.AIR));
                    } else if (removed) {
                        if (resultSize > 0 && isShiftClick) {
                            result.setAmount(resultSize);
                        }
                        event.setCancelled(true);
                        if (isShiftClick) {
                            success = true;
                            player.getInventory().addItem(result);
                        } else if (CompatUtils.getCursor(player).getType() != Material.AIR && CompatUtils.getCursor(player).isSimilar(result)) {
                            success = true;
                            CompatUtils.getCursor(player).setAmount(CompatUtils.getCursor(player).getAmount() + result.getAmount());
                        } else {
                            success = true;
                            CompatUtils.setCursor(player, result);
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
     * @param itemMap        - The itemMap being checked.
     * @param player         - The player being referenced.
     * @param inventoryClone - The clone of CraftingInventory reference.
     * @return If the loop should break.
     */
    private int getConfirmations(final ItemMap itemMap, final Player player, final Inventory inventoryClone) {
        int confirmations = 0;
        for (int i = 0; i < inventoryClone.getSize(); i++) {
            final ItemStack item = inventoryClone.getItem(i);
            if (item != null) {
                for (Character ingredient : itemMap.getIngredients().keySet()) {
                    final ItemRecipe itemRecipe = itemMap.getIngredients().get(ingredient);
                    ItemMap ingredMap = ItemUtilities.getUtilities().getItemMap(itemRecipe.getMap());
                    if (((ingredMap == null && itemRecipe.getMaterial().equals(item.getType())) || (ingredMap != null && ingredMap.isSimilar(player, item))) && item.getAmount() >= itemRecipe.getCount()) {
                        confirmations += 1;
                    }
                }
            }
        }
        return confirmations;
    }
}