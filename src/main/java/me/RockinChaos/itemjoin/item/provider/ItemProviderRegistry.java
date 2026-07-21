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

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all registered ItemProviders, keyed by their name.
 * Registration only ever references third-party classes from within the ItemProvider
 * implementation itself, and only registers when the backing plugin is present,
 * so a missing third-party plugin never causes a NoClassDefFoundError here.
 */
public class ItemProviderRegistry {

    private static final Map<String, ItemProvider> providers = new HashMap<>();

    /**
     * Registers the ItemProvider if its backing plugin is currently available.
     *
     * @param provider the provider to register.
     */
    public static void register(final ItemProvider provider) {
        if (provider.isAvailable()) {
            providers.put(provider.getName().toLowerCase(), provider);
        }
    }

    /**
     * Gets the registered ItemProvider with the specified name.
     *
     * @param name the provider name (case-insensitive).
     * @return the ItemProvider, or null if none is registered under that name.
     */
    public static ItemProvider get(final String name) {
        return (name == null ? null : providers.get(name.toLowerCase()));
    }
}
