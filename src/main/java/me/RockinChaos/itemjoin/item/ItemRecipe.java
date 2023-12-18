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
package me.RockinChaos.itemjoin.item;

import org.bukkit.Material;

public class ItemRecipe {

    private final String itemMap;
    private final Material material;
    private final byte dataValue;
    private final int count;

    /**
     * Creates a new ItemRecipe instance.
     *
     * @param itemMap   - The ItemMap being used.
     * @param material  - The Material for the Recipe.
     * @param dataValue - The Data-Value for the Recipe.
     * @param count     - The Amount of the item for the Recipe.
     */
    public ItemRecipe(final String itemMap, final Material material, final byte dataValue, final int count) {
        this.itemMap = itemMap;
        this.material = material;
        this.dataValue = dataValue;
        this.count = count;
    }

    /**
     * Gets the ItemMap of the recipe.
     *
     * @return The ItemMap.
     */
    public String getMap() {
        return this.itemMap;
    }

    /**
     * Gets the Material of the recipe.
     *
     * @return The material of the Recipe.
     */
    public Material getMaterial() {
        return this.material;
    }

    /**
     * Gets the Data-Value of the recipe.
     *
     * @return The data-value of the Recipe.
     */
    public byte getData() {
        return this.dataValue;
    }

    /**
     * Gets the amount of the item for the recipe.
     *
     * @return The count of the recipe.
     */
    public int getCount() {
        return this.count;
    }
}