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
package me.RockinChaos.itemjoin.utils.api;

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Objects;

public class GuardAPI {

    /**
     * Saves the current items in the Player Inventory to be returned later.
     *
     * @param player    - The Player that had their items saved.
     * @param region    - The region that the items are being saved from.
     * @param type      - The clear type that is being executed.
     * @param craftView - The players current CraftView.
     * @param inventory - The players current Inventory.
     * @param clearType - If ALL items are being cleared.
     */
    public static void saveReturnItems(final Player player, final String region, final String type, final Inventory craftView, final PlayerInventory inventory, final int clearType) {
        boolean doReturn = (ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Options") != null && StringUtils.splitIgnoreCase(Objects.requireNonNull(ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Options")).replace(" ", ""), "RETURN", ","));
        List<ItemMap> protectItems = ItemUtilities.getUtilities().getProtectItems();
        DataObject dataObject = (DataObject) ItemJoin.getCore().getSQL().getData(new DataObject(Table.RETURN_ITEMS, PlayerHandler.getPlayerID(player), player.getWorld().getName(), region, ""));
        if (region != null && !region.isEmpty() && type.equalsIgnoreCase("REGION-ENTER") && doReturn && dataObject == null) {
            Inventory saveInventory = Bukkit.createInventory(null, 54);
            for (int i = 0; i <= 47; i++) {
                for (int k = 0; k < (!protectItems.isEmpty() ? protectItems.size() : 1); k++) {
                    if (i <= 41 && inventory.getSize() >= i && ItemUtilities.getUtilities().canClear(inventory.getItem(i), String.valueOf(i), k, clearType)) {
                        saveInventory.setItem(i, Objects.requireNonNull(inventory.getItem(i)).clone());
                    } else if (i >= 42 && ItemUtilities.getUtilities().canClear(craftView.getItem(i - 42), "CRAFTING[" + (i - 42) + "]", k, clearType) && PlayerHandler.isCraftingInv(player.getOpenInventory())) {
                        saveInventory.setItem(i, Objects.requireNonNull(craftView.getItem(i - 42)).clone());
                    }
                }
            }
            ItemJoin.getCore().getSQL().saveData(new DataObject(Table.RETURN_ITEMS, PlayerHandler.getPlayerID(player), player.getWorld().getName(), region, ItemHandler.serializeInventory(saveInventory)));
        }
    }

    /**
     * Returns the previously removed Region Items to the Player.
     *
     * @param player - The Player that had their items returned.
     * @param region - The region the items were removed from.
     */
    public static void pasteReturnItems(final Player player, final String region) {
        boolean doReturn = (ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Options") != null && StringUtils.splitIgnoreCase(Objects.requireNonNull(ItemJoin.getCore().getConfig("config.yml").getString("Clear-Items.Options")).replace(" ", ""), "RETURN", ","));
        if (region != null && !region.isEmpty() && doReturn) {
            DataObject dataObject = (DataObject) ItemJoin.getCore().getSQL().getData(new DataObject(Table.RETURN_ITEMS, PlayerHandler.getPlayerID(player), player.getWorld().getName(), region, ""));
            Inventory inventory = (dataObject != null ? ItemHandler.deserializeInventory(dataObject.getInventory64().replace(region + ".", "")) : null);
            for (int i = 47; i >= 0; i--) {
                if (inventory != null && inventory.getItem(i) != null && Objects.requireNonNull(inventory.getItem(i)).getType() != Material.AIR) {
                    if (i <= 41) {
                        player.getInventory().setItem(i, Objects.requireNonNull(inventory.getItem(i)).clone());
                    } else if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
                        player.getOpenInventory().getTopInventory().setItem(i - 42, Objects.requireNonNull(inventory.getItem(i)).clone());
                        PlayerHandler.updateInventory(player, 1L);
                    }
                }
                if (dataObject != null) {
                    ItemJoin.getCore().getSQL().removeData(dataObject);
                }
            }
        }
    }
}