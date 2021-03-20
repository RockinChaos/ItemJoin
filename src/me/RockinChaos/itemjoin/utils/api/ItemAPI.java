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

import org.bukkit.Material;

public class ItemAPI {
	
   /**
    * Enum list of placeable materials.
    * 
    */
	public enum Placeable {
		BOAT,
		REDSTONE,
		FRAME,
		STAND,
		CRYSTAL,
		;
	}
	
   /**
    * Checks if the Material is Placeable.
    * 
    * @param material - The Material to be checked.
    * @return If the Material is Placeable.
    */
	public static boolean isPlaceable(Material material) {
		if (material.isBlock()) { return true; }
	    for (Placeable tag: Placeable.values()) {
	    	String[] mats = material.name().split("_");
	    	if (tag.name().equalsIgnoreCase((tag.name().contains("_") ? material.name() : (mats.length > 1 ? mats[(mats.length - 1)] : mats[0])))) {
	        	return false;
	        }
	    }
		return false;
	}
}