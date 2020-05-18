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
package me.RockinChaos.itemjoin.utils.enchants;

import org.bukkit.enchantments.Enchantment;

import me.RockinChaos.itemjoin.ItemJoin;

public abstract class EnchantWrapper extends Enchantment {

   /**
    * Creates a new Enchantment.
    * 
    * @param namespace - The name that should be set as the Enchantment.
    */
    public EnchantWrapper(final String namespace) {
        super(new org.bukkit.NamespacedKey(ItemJoin.getInstance(), namespace));
    }
}