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
package me.RockinChaos.itemjoin.item.provider;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a third-party plugin capable of providing real ItemStacks
 * for a given lookup key (e.g. an ItemsAdder namespaced ID).
 */
public interface ItemProvider {

    /**
     * The provider's name, matched against the "provider" config value and the plugin.yml softdepend entry.
     *
     * @return the provider name.
     */
    String getName();

    /**
     * Checks if the backing plugin is currently installed and enabled.
     *
     * @return true if the backing plugin is present.
     */
    boolean isAvailable();

    /**
     * Fetches the real, live ItemStack for the given key.
     *
     * @param key the provider-specific item key (e.g. "namespace:item_id").
     * @return the real ItemStack, or null if the key could not be resolved.
     */
    ItemStack provide(final String key);

    /**
     * Checks whether an arbitrary ItemStack is the item identified by the given key,
     * used to recognize provider items that were not sourced through ItemJoin (e.g. picked up in the world).
     *
     * @param item the ItemStack to test.
     * @param key the provider-specific item key.
     * @return true if the ItemStack matches the key.
     */
    boolean matches(final ItemStack item, final String key);
}
