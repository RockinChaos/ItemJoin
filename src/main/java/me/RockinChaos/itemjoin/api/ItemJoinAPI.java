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
package me.RockinChaos.itemjoin.api;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("unused")
public class ItemJoinAPI {

    private final APIUtils apiUtils = new APIUtils();

    /**
     * Gives all custom items to the specified player.
     *
     * @param player that will receive the items.
     */
    public void getItems(final Player player) {
        this.apiUtils.setItems(player);
    }

    /**
     * Checks if the ItemStack is an ItemJoin item.
     *
     * @param item that will be checked.
     * @return If the ItemStack is a custom item.
     */
    public boolean isCustom(final ItemStack item) {
        return this.apiUtils.isCustom(item, null);
    }

    /**
     * Checks if the ItemStack in the said world is an ItemJoin item.
     *
     * @param item that is being checked.
     * @param world that the item is said to be in.
     * @return If the ItemStack in the specified world is a custom item.
     */
    public boolean isCustom(final ItemStack item, final World world) {
        return this.apiUtils.isCustom(item, world);
    }

    /**
     * Fetches the ItemStack defined for the provided itemNode.
     * <p>
     * The {@code itemNode} refers to a YAML key where the custom item data is defined.
     * For example, in the YAML below, "example-item" is the itemNode:
     * <pre>
     * example-item:
     *   id: DIAMOND
     *   slot: 0
     * </pre>
     *
     * @param player that will receive the item.
     * @param itemNode The YAML key representing the custom item configuration.
     * @return The ItemStack of the found custom item.
     */
    public ItemStack getItemStack(final Player player, final String itemNode) {
        return this.apiUtils.getItemStack(player, itemNode);
    }

    /**
     * Fetches the config node name of the custom item.
     *
     * @param item that will be checked.
     * @return String node of the custom item.
     */
    public String getNode(final ItemStack item) {
        return this.apiUtils.getNode(item, null);
    }

    /**
     * Fetches the config node name of the custom item.
     *
     * @param item that will be checked.
     * @param world that the item is said to be in.
     * @return String node of the custom item.
     */
    public String getNode(final ItemStack item, final World world) {
        return this.apiUtils.getNode(item, world);
    }

    /**
     * Fetches all itemNodes of the custom items that are currently defined.
     * <p>
     * The {@code itemNode} refers to a YAML key where the custom item data is defined.
     * For example, in the YAML below, "example-item" is the itemNode:
     * <pre>
     * example-item:
     *   id: DIAMOND
     *   slot: 0
     * </pre>
     *
     * @return List of all available itemNodes.
     */
    public List<String> getNodes() {
        return this.apiUtils.getNodes();
    }

    /**
     * Fetches the itemflags that are defined for the custom item.
     *
     * @param itemNode that is the custom items config node.
     * @return List of itemflags for the custom item.
     */
    public List<String> getItemflags(final String itemNode) {
        return this.apiUtils.getItemflags(itemNode);
    }

    /**
     * Fetches commands that are defined for the custom item.
     * <p>
     * The {@code itemNode} refers to a YAML key where the custom item data is defined.
     * For example, in the YAML below, "example-item" is the itemNode:
     * <pre>
     * example-item:
     *   id: DIAMOND
     *   slot: 0
     * </pre>
     *
     * @param itemNode The YAML key representing the custom item configuration.
     * @return List of commands for the custom item.
     */
    public List<String> getCommands(final String itemNode) {
        return this.apiUtils.getCommands(itemNode);
    }

    /**
     * Fetches triggers that are defined for the custom item.
     * <p>
     * The {@code itemNode} refers to a YAML key where the custom item data is defined.
     * For example, in the YAML below, "example-item" is the itemNode:
     * <pre>
     * example-item:
     *   id: DIAMOND
     *   slot: 0
     * </pre>
     *
     * @param itemNode The YAML key representing the custom item configuration.
     * @return List of triggers for the custom item.
     */
    public List<String> getTriggers(final String itemNode) {
        return this.apiUtils.getTriggers(itemNode);
    }

    /**
     * Fetches the slot that the custom item is defined to be set to.
     * <p>
     * The {@code itemNode} refers to a YAML key where the custom item data is defined.
     * For example, in the YAML below, "example-item" is the itemNode:
     * <pre>
     * example-item:
     *   id: DIAMOND
     *   slot: 0
     * </pre>
     *
     * @param itemNode The YAML key representing the custom item configuration.
     * @return The integer slot or custom slot for the custom item.
     */
    public String getSlot(final String itemNode) {
        return this.apiUtils.getSlot(itemNode);
    }

    /**
     * Fetches all slots that the custom item is defined to be set to.
     * <p>
     * The {@code itemNode} refers to a YAML key where the custom item data is defined.
     * For example, in the YAML below, "example-item" is the itemNode:
     * <pre>
     * example-item:
     *   id: DIAMOND
     *   slot: 0
     * </pre>
     *
     * @param itemNode The YAML key representing the custom item configuration.
     * @return List of slots for the custom item.
     */
    public List<String> getMultipleSlots(final String itemNode) {
        return this.apiUtils.getMultipleSlots(itemNode);
    }
}