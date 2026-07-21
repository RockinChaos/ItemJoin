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
package me.RockinChaos.itemjoin.item.provider.providers;

import dev.lone.itemsadder.api.CustomStack;
import me.RockinChaos.itemjoin.item.provider.ItemProvider;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * Provides real, live ItemStacks sourced directly from ItemsAdder's registry,
 * instead of copying NBT data onto a fabricated ItemStack.
 */
public class ItemsAdderProvider implements ItemProvider {

    @Override
    public String getName() {
        return "ItemsAdder";
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
    }

    @Override
    public ItemStack provide(final String key) {
        final CustomStack customStack = CustomStack.getInstance(key);
        return (customStack != null ? customStack.getItemStack() : null);
    }

    @Override
    public boolean matches(final ItemStack item, final String key) {
        final CustomStack customStack = CustomStack.byItemStack(item);
        return (customStack != null && customStack.getId().equalsIgnoreCase(key));
    }
}
