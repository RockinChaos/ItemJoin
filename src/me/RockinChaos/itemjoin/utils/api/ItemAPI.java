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
import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.StringUtils;

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
		GRANITE,
		DIORITE,
		ANDESITE,
		DEEPSLATE,
		POLISHED,
		CALCITE,
		BRICKS,
		BARS,
		CHAIN,
		TUFF,
		DRIPSTONE,
		BEDROCK,
		COAL_BLOCK,
		IRON_BLOCK,
		GOLD_BLOCK,
		DIAMOND_BLOCK,
		NETHERITE_BLOCK,
		LAPIS_BLOCK,
		COPPER,
		CUT_COPPER_STAIRS,
		EXPOSED_CUT_COPPER_STAIRS,
		WEATHERED_CUT_COPPER_STAIRS,
		OXIDIZED_CUT_COPPER_STAIRS,
		CUT_COPPER_SLAB,
		EXPOSED_CUT_COPPER_SLAB,
		WEATHERED_CUT_COPPER_SLAB,
		OXIDIZED_CUT_COPPER_SLAB,
		WAXED_COPPER_BLOCK,
		WAXED_CUT_COPPER_STAIRS,
		WAXED_EXPOSED_CUT_COPPER_STAIRS,
		WAXED_WEATHERED_CUT_COPPER_STAIRS,
		WAXED_OXIDIZED_CUT_COPPER_STAIRS,
		WAXED_CUT_COPPER_SLAB,
		WAXED_EXPOSED_CUT_COPPPER_SLAB,
		WAXED_WEATHERED_CUT_COPPER_SLAB,
		WAXED_OXIDIZED_CUT_COPPER_SLAB,
		SANDSTONE,
		STONE_SLAB,
		COBBLESTONE_SLAB,
		SMOOTH_STONE_SLAB,
		SANDSTONE_SLAB,
		CUT_SANDSTONE_SLAB,
		BRICK_SLAB,
		STONE_BRICK_SLAB,
		NETHER_BRICK_SLAB,
		QUARTZ_SLAB,
		RED_SANDSTONE_SLAB,
		CUT_RED_SANDSTONE_SLAB,
		PURPUR_SLAB,
		PRISMARINE_SLAB,
		PRISMARINE_BRICK_SLAB,
		DARK_PRISMARINE_SLAB,
		QUARTZ,
		OBSIDIAN,
		PURPUR_BLOCK,
		PURPUR_PILLAR,
		PURPUR_STAIRS,
		FURNACE,
		COBBLESTONE_STAIRS,
		BRICK_STAIRS,
		STONE_BRICK_STAIRS,
		NETHER_BRICK_STAIRS,
		NETHER_BRICK_FENCE,
		ENCHANTING_TABLE,
		END_PORTAL_FRAME,
		END_STONE,
		SANDSTONE_STAIRS,
		ENDER_CHEST,
		EMERALD_BLOCK,
		BEACON,
		WALL,
		ANVIL,
		CHISELED_QUARTZ_BLOCK,
		QUARTZ_BLOCK,
		QUARTZ_BRICKS,
		QUARTZ_PILLAR,
		QUARTZ_STAIRS,
		TERRACOTTA,
		PRISMARINE,
		PRISMARINE_STAIRS,
		PRISMARINE_BRICK_STAIRS,
		DARK_PRISMARINE_STAIRS,
		RED_SANDSTONE_STAIRS,
		MAGMA_BLOCK,
		NETHER_WART_BLOCK,
		WARPED_WART_BLOCK,
		BONE_BLOCK,
		CONCRETE,
		BASALT,
		RAW_IRON_BLOCK,
		RAW_GOLD_BLOCK,
		RAW_COPPER_BLOCK,
		AMETHYST_BLOCK,
		BUDDING_AMETHYST
		;
	}
	
   /**
    * Enum list of placeable materials.
    * 
    */
	public enum Toolable {
		SPAWNER,
		STONE,
		COBBLESTONE,
		ORE,
		
		;
	}
	
   /**
    * Checks if the Material is Placeable.
    * 
    * @param material - The Material to be checked.
    * @return If the Material is Placeable.
    */
	public static boolean isPlaceable(final Material material) {
		if (material.isBlock()) { return true; }
	    for (Placeable tag: Placeable.values()) {
	    	String[] mats = material.name().split("_");
	    	if (tag.name().equalsIgnoreCase((tag.name().contains("_") ? material.name() : (mats.length > 1 ? mats[(mats.length - 1)] : mats[0])))) {
	        	return false;
	        }
	    }
		return false;
	}
	
   /**
    * Checks if the Material is Toolable.
    * 
    * @param material - The Material to be checked.
    * @return If the Material is Toolable.
    */
	public static boolean isToolable(final Player player, final Material material) {
		if (!material.isBlock()) { return true; }
		boolean requiresTool = false;
		for (Toolable tag: Toolable.values()) {
			String[] mats = material.name().split("_");
			if (tag.name().equalsIgnoreCase((tag.name().contains("_") ? material.name() : (mats.length > 1 ? mats[(mats.length - 1)] : mats[0])))) {
				requiresTool = !(PlayerHandler.getMainHandItem(player) != null && StringUtils.containsIgnoreCase(PlayerHandler.getMainHandItem(player).getType().name(), "PICKAXE"));
			}
		}
		return !requiresTool;
	}
}