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
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class Glow extends EnchantWrapper {

   /**
    * Creates a new Glow instance.
    *
    */
    public Glow() {
        super("glowing");
    }

   /**
    * Checks if the ItemStack can glow.
    * 
    * @param stack - The ItemStack to be checked.
    * @return If the ItemStack can glow.
    */
    @Override
    public boolean canEnchantItem(final ItemStack stack) {
        return true;
    }

   /**
    * Checks if the Enchantment conflicts with Glow.
    * 
    * @param enchant - The Enchantment to be checked.
    * @return If the Enchantment conflicts.
    */
    @Override
    public boolean conflictsWith(final Enchantment enchant) {
        return false;
    }

   /**
    * Gets the Item Target.
    * 
    * @return The Item Target.
    *
    */
    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

   /**
    * Gets the Max Glow Level.
    * 
    * @return The Max Level.
    */
	@Override
	public int getMaxLevel() {
		return 0;
	}

   /**
    * Gets the name of the Glow Enchant.
    * 
    * @return The Glow name.
    *
    */
	@Override
	public String getName() {
		return "Glowing";
	}

   /**
    * Gets the starting glow level.
    *
    *@return The starting glow level.
    */
	@Override
	public int getStartLevel() {
		return 0;
	}

   /**
    * Checks if the Enchantment is cursed.
    * 
    * @return If the Enchantment is cursed.
    *
    */
	@Override
	public boolean isCursed() {
		return false;
	}

   /**
    * Checks if the Enchantment is treasure.
    * 
    * @return If the Enchantment is treasure.
    *
    */
	@Override
	public boolean isTreasure() {
		return false;
	}
}