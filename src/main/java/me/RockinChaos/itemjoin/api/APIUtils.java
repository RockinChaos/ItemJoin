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

import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.PluginData;
import me.RockinChaos.itemjoin.item.ItemCommand;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.item.ItemUtilities.TriggerType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class APIUtils {

    /**
     * Gives all ItemJoin items to the specified player.
     *
     * @param player that will receive the items.
     */
    public void setItems(final Player player) {
        ItemMap probable = null;
        for (Object itemMap : ItemJoin.getCore().getChances().getItems().keySet()) {
            if (((ItemMap) itemMap).hasItem(player, true)) {
                probable = (ItemMap) itemMap;
            }
        }
        if (probable == null) {
            probable = (ItemMap) ItemJoin.getCore().getChances().getRandom(player);
        }
        final int session = StringUtils.getRandom(1, 80000);
        for (final ItemMap item : ItemUtilities.getUtilities().getItems()) {
            if (item.inWorld(player.getWorld()) && ((probable != null && item.getConfigName().equals(probable.getConfigName())) || item.getProbability() == -1) && PluginData.getInfo().isEnabled(player, item.getConfigName())
                    && item.isLimitMode(player.getGameMode()) && item.hasPermission(player, player.getWorld()) && ItemUtilities.getUtilities().isObtainable(player, item, session, TriggerType.DEFAULT, "IJ_WORLD")) {
                item.giveTo(player);
            }
            item.setAnimations(player);
        }
        ItemUtilities.getUtilities().sendFailCount(player, session, TriggerType.DEFAULT, "IJ_WORLD");
        PlayerHandler.updateInventory(player, 15L);
    }

    /**
     * Checks if the ItemStack in the said world is a custom item.
     *
     * @param item that is being checked.
     * @param world that the item is said to be in.
     * @return If the ItemStack in the specified world is a custom item.
     */
    public boolean isCustom(final ItemStack item, final World world) {
        ItemMap itemMap = this.getMap(item, world, null);
        return itemMap != null;
    }

    /**
     * Fetches the ItemStack defined for the provided itemNode.
     *
     * @param player the player to find the specific custom item.
     * @param itemNode that is the custom items config node.
     * @return The ItemStack of the found custom item.
     */
    public ItemStack getItemStack(final Player player, final String itemNode) {
        final ItemMap itemMap = this.getMap(null, null, itemNode);
        if (itemMap != null) {
            return itemMap.getItemStack(player);
        }
        return null;
    }

    /**
     * Fetches the config node name of the custom item.
     *
     * @param item that will be checked.
     * @param world that the item is said to be in.
     * @return String node of the custom item.
     */
    public String getNode(final ItemStack item, final World world) {
        final ItemMap itemMap = this.getMap(item, world, null);
        if (itemMap != null) {
            return itemMap.getConfigName();
        }
        return null;
    }

    /**
     * Fetches the itemflags that are defined for the custom item.
     *
     * @param itemNode that is the custom items config node.
     * @return List of itemflags for the custom item.
     */
    public List<String> getItemflags(final String itemNode) {
        final ItemMap itemMap = this.getMap(null, null, itemNode);
        final List<String> itemflags = new ArrayList<>();
        if (itemMap != null && itemMap.getItemFlags() != null && !itemMap.getItemFlags().isEmpty()) {
            Collections.addAll(itemflags, itemMap.getItemFlags().replace(" ", "").split(","));
            return itemflags;
        }
        return null;
    }

    /**
     * Fetches commands that are defined for the custom item.
     *
     * @param itemNode that is the custom items config node.
     * @return List of commands for the custom item.
     */
    public List<String> getCommands(final String itemNode) {
        final ItemMap itemMap = this.getMap(null, null, itemNode);
        final List<String> commands = new ArrayList<>();
        if (itemMap != null && itemMap.getCommands() != null && itemMap.getCommands().length > 0) {
            for (ItemCommand command : itemMap.getCommands()) {
                commands.add(command.getRawCommand());
            }
            return commands;
        }
        return null;
    }

    /**
     * Fetches triggers that are defined for the custom item.
     *
     * @param itemNode that is the custom items config node.
     * @return List of triggers for the custom item.
     */
    public List<String> getTriggers(final String itemNode) {
        final ItemMap itemMap = this.getMap(null, null, itemNode);
        final List<String> triggers = new ArrayList<>();
        if (itemMap != null && itemMap.getTriggers() != null && !itemMap.getTriggers().isEmpty()) {
            Collections.addAll(triggers, itemMap.getTriggers().replace(" ", "").split(","));
            return triggers;
        }
        return null;
    }

    /**
     * Fetches the slot that the custom item is defined to be set to.
     *
     * @param itemNode that is the custom items config node.
     * @return The integer slot or custom slot for the custom item.
     */
    public String getSlot(final String itemNode) {
        ItemMap itemMap = this.getMap(null, null, itemNode);
        if (itemMap != null) {
            return itemMap.getSlot();
        }
        return null;
    }

    /**
     * Fetches all slots that the custom item is defined to be set to.
     * In the instance that the custom item is a MultiSlot item.
     *
     * @param itemNode that is the custom items config node.
     * @return List of slots for the custom item.
     */
    public List<String> getMultipleSlots(final String itemNode) {
        final ItemMap itemMap = this.getMap(null, null, itemNode);
        if (itemMap != null) {
            return itemMap.getMultipleSlots();
        }
        return null;
    }

    /**
     * Fetches the mapping of the custom item.
     *
     * @param item that will be checked.
     * @param world that the custom item is said to be in.
     * @param itemNode that is the custom items config node.
     * @return ItemMap that is the located custom item.
     */
    private ItemMap getMap(final ItemStack item, final World world, final String itemNode) {
        for (ItemMap itemMap : ItemUtilities.getUtilities().getItems()) {
            if (world != null && itemMap.inWorld(world) && itemMap.isSimilar(null, item)) {
                return itemMap;
            } else if (world == null && itemMap.isSimilar(null, item)) {
                return itemMap;
            } else if (world == null && item == null && itemMap.getConfigName().equalsIgnoreCase(itemNode)) {
                return itemMap;
            }
        }
        return null;
    }
}