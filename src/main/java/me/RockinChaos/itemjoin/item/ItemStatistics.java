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

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.itemjoin.ItemJoin;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStatistics {

    private final Map<ItemMap, Map<String, Integer>> itemEnchants = new HashMap<>();

    /**
     * Sets the ItemStatistics information for the specific player.
     *
     * @param player - The player being referenced.
     * @param items  - The ItemMaps to be modified.
     */
    public ItemStatistics(final Player player, final List<ItemMap> items) {
        for (final ItemMap itemMap : items) {
            this.setEnchantments(player, itemMap);
        }
    }

    /**
     * Sets the Custom Enchants to the Custom Item,
     * adding the specified enchantments to the item.
     * Translates any placeholders setting the proper
     * enchant name and enchant level.
     *
     * @param player  - The player being referenced.
     * @param itemMap - The ItemMap being modified.
     */
    private void setEnchantments(final Player player, final ItemMap itemMap) {
        final String enchants = itemMap.getNodeLocation().getString(".enchantment");
        if (enchants != null) {
            final String enchantList = enchants.replace(" ", "");
            final String[] enchantments = enchantList.split(",");
            final Map<String, Integer> listEnchants = new HashMap<>();
            for (final String enchantment : enchantments) {
                final String[] parts = enchantment.split(":");
                final String name = StringUtils.translateLayout(parts[0], player).toUpperCase();
                final String levelPart = StringUtils.translateLayout(parts[1], player);
                final Enchantment enchantName = ItemHandler.getEnchantByName(name);
                int level = 1;
                if (StringUtils.containsIgnoreCase(enchantment, ":")) {
                    try {
                        level = Integer.parseInt(levelPart);
                    } catch (NumberFormatException e) {
                        ServerUtils.logSevere("{ItemStatistics} An error occurred in the config, " + levelPart + " is not a number and a number was expected!");
                        ServerUtils.logWarn("{ItemStatistics} Enchantment: " + name + " will now be enchanted by level 1.");
                        ServerUtils.sendDebugTrace(e);
                    }
                }
                if (level > 0 && enchantName != null) {
                    listEnchants.put(name, level);
                } else if (level > 0 && ItemJoin.getCore().getDependencies().tokenEnchantEnabled() && TokenEnchantAPI.getInstance().getEnchantment(name) != null) {
                    listEnchants.put(name, level);
                } else if (level > 0 && !ItemJoin.getCore().getDependencies().tokenEnchantEnabled()) {
                    ServerUtils.logSevere("{ItemStatistics} An error occurred in the config, " + name + " is not a proper enchant name!");
                    ServerUtils.logWarn("{ItemStatistics} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html for a list of correct enchantment names.");
                }
            }
            this.itemEnchants.put(itemMap, listEnchants);
        }
    }

    /**
     * Gets the properly translated enchantments for the ItemMap.
     *
     * @param itemMap - The ItemMap being referenced.
     * @return The list of enchantments and their level for the ItemMap.
     */
    public Map<String, Integer> getEnchantments(final ItemMap itemMap) {
        return itemEnchants.get(itemMap);
    }
}