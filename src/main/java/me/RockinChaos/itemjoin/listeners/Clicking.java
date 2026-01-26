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
package me.RockinChaos.itemjoin.listeners;

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.CompatUtils;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.protocol.events.PlayerPickBlockEvent;
import me.RockinChaos.core.utils.types.ActionBlocks;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.PluginData;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Clicking implements Listener {

    private static final HashMap<String, ItemStack> cursorItem = new HashMap<>();
    private final Map<String, Boolean> droppedItem = new HashMap<>();
    private final Map<String, Boolean> dropClick = new HashMap<>();

    /**
     * Gets the current HashMap of players and their cursor items.
     *
     * @param player - that will have their item updated.
     * @return The HashMap containing the players and their current cursor items.
     */
    public static ItemStack getCursor(final String player) {
        return cursorItem.get(player);
    }

    /**
     * Puts the player into the cursor HashMap with their current cursor item.
     *
     * @param player - that is having their item updated.
     * @param item   - that is being updated.
     */
    public static void putCursor(final String player, final ItemStack item) {
        cursorItem.put(player, item);
    }

    /**
     * Prevents the player from moving all items in their inventory.
     *
     * @param event - InventoryClickEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onGlobalModify(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if (PluginData.getInfo().isPreventString(player, "itemMovement")) {
            if (PluginData.getInfo().isPreventBypass(player) && !(CompatUtils.getInventoryTitle(player).contains(String.valueOf(ChatColor.COLOR_CHAR)) || CompatUtils.getInventoryTitle(player).contains("&"))) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Prevents the player from using the pick block feature to move ANY items in their inventory.
     *
     * @param event - PlayerPickItemEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onGlobalPickItem(final PlayerPickBlockEvent event) {
        final Player player = event.getPlayer();
        if (PluginData.getInfo().isPreventString(player, "itemMovement")) {
            if (PluginData.getInfo().isPreventBypass(player)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Prevents the player from moving the custom item in their inventory.
     *
     * @param event - InventoryClickEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onModify(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory topInventory = CompatUtils.getTopInventory(player);
        final Inventory bottomInventory = CompatUtils.getBottomInventory(player);
        final List<ItemStack> items = new ArrayList<>();
        if (!this.isCreativeDupe(event)) {
            items.add(event.getCurrentItem());
            items.add(event.getCursor());
            if (StringUtils.containsIgnoreCase(event.getAction().name(), "HOTBAR")) {
                if (CompatUtils.getBottomInventory(event).getSize() >= event.getHotbarButton() && event.getHotbarButton() >= 0) {
                    items.add(CompatUtils.getBottomInventory(event).getItem(event.getHotbarButton()));
                } else if (ServerUtils.hasSpecificUpdate("1_9")) {
                    items.add(PlayerHandler.getOffHandItem(player));
                }
            }
            this.LegacyDropEvent(player);
            for (ItemStack item : items) {
                if (!ItemUtilities.getUtilities().isAllowed(player, item, "inventory-modify")) {
                    event.setCancelled(true);
                    if (CompatUtils.getInventoryType(player).name().equalsIgnoreCase("CHEST") && !CompatUtils.getInventoryTitle(player).equalsIgnoreCase("CHEST")) {
                        final ItemStack itemCopy = item.clone();
                        SchedulerUtils.run(() -> {
                            for (int i = 0; i < topInventory.getSize(); i++) {
                                if (topInventory.getItem(i) != null && Objects.equals(topInventory.getItem(i), item)) {
                                    topInventory.setItem(i, new ItemStack(Material.AIR));
                                    bottomInventory.setItem(event.getSlot(), itemCopy);
                                }
                            }
                        });
                    }
                    if (PlayerHandler.isCreativeMode(player)) {
                        player.closeInventory();
                    } else if (!ItemUtilities.getUtilities().isAllowed(player, item, "inventory-close")) {
                        player.openInventory(Bukkit.createInventory(player, 9, "INVENTORY-CLOSE"));
                        player.closeInventory();
                    }
                    PlayerHandler.updateInventory(player, 1L);
                    break;
                }
            }
        }
    }

    /**
     * Prevents the custom item from being equipped upon clicking and moving it to the armor slots.
     *
     * @param event - InventoryClickEvent
     */
    @EventHandler()
    private void onEquipment(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory bottomInventory = CompatUtils.getBottomInventory(event);
        if (StringUtils.containsIgnoreCase(event.getAction().name(), "HOTBAR") && bottomInventory.getSize() >= event.getHotbarButton() && event.getHotbarButton() >= 0 && !event.getClick().name().equalsIgnoreCase("MIDDLE") && event.getSlotType() == SlotType.ARMOR) {
            final ItemStack hotItem = bottomInventory.getItem(event.getHotbarButton());
            if (hotItem != null && hotItem.getType() != Material.AIR && this.isEquipment(hotItem, "EQUIPPED", String.valueOf(event.getSlot())) && !ItemUtilities.getUtilities().isAllowed(player, hotItem, "cancel-equip")) {
                event.setCancelled(true);
                PlayerHandler.updateInventory(player, 1L);
            }
        }
        if (!event.getClick().name().equalsIgnoreCase("MIDDLE") && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if (event.getSlotType() == SlotType.ARMOR
                    && this.isEquipment(event.getCurrentItem(), "UNEQUIPPED", String.valueOf(event.getSlot()))
                    && !ItemUtilities.getUtilities().isAllowed(player, event.getCurrentItem(), "cancel-equip")) {
                event.setCancelled(true);
                PlayerHandler.updateInventory(player, 1L);
            } else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                final String[] itemType = (event.getCurrentItem().getType().name().equalsIgnoreCase("ELYTRA") ? "ELYTRA_CHESTPLATE".split("_") : event.getCurrentItem().getType().name().split("_"));
                if (itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && StringUtils.isInt(StringUtils.getArmorSlot(itemType[1], true))
                        && player.getInventory().getItem(Integer.parseInt(StringUtils.getArmorSlot(itemType[1], true))) == null
                        && this.isEquipment(event.getCurrentItem(), "SHIFT_EQUIPPED", String.valueOf(event.getSlot()))
                        && !ItemUtilities.getUtilities().isAllowed(player, event.getCurrentItem(), "cancel-equip")) {
                    event.setCancelled(true);
                    PlayerHandler.updateInventory(player, 1L);
                }
            }
        }
        if (!event.getClick().name().equalsIgnoreCase("MIDDLE") && !event.getClick().name().contains("SHIFT")
                && event.getSlotType() == SlotType.ARMOR && event.getCursor() != null && event.getCursor().getType() != Material.AIR
                && this.isEquipment(event.getCursor(), "EQUIPPED", String.valueOf(event.getSlot()))
                && !ItemUtilities.getUtilities().isAllowed(player, event.getCursor(), "cancel-equip")) {
            event.setCancelled(true);
            PlayerHandler.updateInventory(player, 1L);
        }
    }

    /**
     * Prevents the custom item from being equipped upon clicking and dragging it to the armor slots.
     *
     * @param event - InventoryDragEvent
     */
    @EventHandler()
    private void onEquipmentDrag(final InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Set<Integer> slideSlots = event.getInventorySlots();
        int slot = 0;
        for (int actualSlot : slideSlots) {
            slot = actualSlot;
            break;
        }
        if (event.getOldCursor().getType() != Material.AIR && this.isEquipment(event.getOldCursor(), "EQUIPPED", String.valueOf(slot)) && !ItemUtilities.getUtilities().isAllowed(player, event.getOldCursor(), "cancel-equip")) {
            event.setCancelled(true);
            PlayerHandler.updateInventory(player, 1L);
        }
    }

    /**
     * Prevents the custom item from being equipped upon right-clicking it from their hand to the armor slots.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler()
    private void onEquipmentClick(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
        if (item != null && item.getType() != Material.AIR && !PlayerHandler.isMenuClick(player, event.getAction())) {
            final String[] itemType = (item.getType().name().equalsIgnoreCase("ELYTRA") ? "ELYTRA_CHESTPLATE".split("_") : item.getType().name().split("_"));
            if (itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && StringUtils.isInt(StringUtils.getArmorSlot(itemType[1], true))
                    && this.isEquipment(item, "EQUIPPED", StringUtils.getArmorSlot(itemType[1], true))
                    && (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.useInteractedBlock().equals(Result.DENY) || player.isSneaking() || !ActionBlocks.isActionBlocks(Objects.requireNonNull(event.getClickedBlock()).getType()))
                    && (!ItemUtilities.getUtilities().isAllowed(player, item, "inventory-modify")
                    || !ItemUtilities.getUtilities().isAllowed(player, item, "cancel-equip")
                    || !ItemUtilities.getUtilities().isAllowed(player, player.getInventory().getItem(Integer.parseInt(StringUtils.getArmorSlot(itemType[1], true))), "inventory-modify")
                    || !ItemUtilities.getUtilities().isAllowed(player, player.getInventory().getItem(Integer.parseInt(StringUtils.getArmorSlot(itemType[1], true))), "cancel-equip"))) {
                event.setCancelled(true);
                PlayerHandler.updateInventory(player, 1L);
            }
        }
    }

    /**
     * Checks if the item being moved is actually an equip-able item.
     *
     * @param item      - the item the player is trying to move.
     * @param clickType - the clicking type that is being performed.
     * @param slot      - the slot the item originally resided in.
     * @return If the item is equipment.
     */
    private boolean isEquipment(final ItemStack item, String clickType, final String slot) {
        final String[] itemType = (item.getType().name().equalsIgnoreCase("ELYTRA") ? "ELYTRA_CHESTPLATE".split("_") :
                (ItemHandler.isSkull(item.getType()) || StringUtils.splitIgnoreCase(item.getType().name(), "HEAD", "_") ? "SKULL_HELMET".split("_") : item.getType().name().split("_")));
        return itemType.length >= 2 && (itemType[1] != null && !itemType[1].isEmpty() && (clickType.equalsIgnoreCase("SHIFT_EQUIPPED") || itemType[1].equalsIgnoreCase(StringUtils.getArmorSlot(slot, false))));
    }

    /**
     * Prevents the player from duplicating immobile items while in creative.
     *
     * @param event - InventoryClickEvent
     */
    public boolean isCreativeDupe(final InventoryClickEvent event) {
        if (PlayerHandler.isCreativeMode((Player) event.getWhoClicked()) && event.getCurrentItem() != null && event.getCursor() != null) {
            final ItemMeta itemMeta = event.getCurrentItem().getItemMeta();
            String currentNBT = (ItemJoin.getCore().getData().dataTagsEnabled() ? ItemHandler.getNBTData(event.getCurrentItem(), PluginData.getInfo().getNBTList())
                    : ((itemMeta != null && itemMeta.hasDisplayName()) ? StringUtils.colorDecode(event.getCurrentItem()) : null));
            String cursorNBT = (ItemJoin.getCore().getData().dataTagsEnabled() ? ItemHandler.getNBTData(event.getCursor(), PluginData.getInfo().getNBTList())
                    : ((itemMeta != null && itemMeta.hasDisplayName()) ? StringUtils.colorDecode(event.getCursor()) : null));
            if (currentNBT != null && cursorNBT != null) {
                return currentNBT.equalsIgnoreCase(cursorNBT);
            }
        }
        return false;
    }

    /**
     * Prevents the player from using the pick block feature to move an item in their inventory.
     *
     * @param event - PlayerPickBlockEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onPickBlock(final PlayerPickBlockEvent event) {
        final Player player = event.getPlayer();
        SchedulerUtils.run(() -> {
            try {
                final Inventory currentInventory = player.getInventory();
                final ItemStack[] snapshot = event.getContents();
                final ItemStack currentHeldItem = currentInventory.getItem(event.getSlot());
                final ItemStack heldItem = event.getHeldItem();
                final int pickSlot = event.getPickSlot();
                if (pickSlot != -1 && snapshot[pickSlot] != null && !ItemUtilities.getUtilities().isAllowed(player, snapshot[pickSlot], "inventory-modify")) {
                    event.setCancelled(true);
                } else if (heldItem.getType() != Material.AIR) {
                    if (!ItemUtilities.getUtilities().isAllowed(player, heldItem, "inventory-modify")) {
                        boolean itemWasMoved = currentHeldItem == null || currentHeldItem.getType() == Material.AIR || !currentHeldItem.isSimilar(heldItem);
                        boolean itemWasReplaced = currentHeldItem != null && currentHeldItem.getType() != Material.AIR && !currentHeldItem.isSimilar(heldItem);
                        if (itemWasMoved || itemWasReplaced) {
                            event.setCancelled(true);
                        }
                    }
                }
            } catch (Exception e) {
                ServerUtils.sendSevereTrace(e);
            }
        });
    }

    /**
     * Automatically updates any animated or dynamic items that reside on the players' cursor.
     *
     * @param event - InventoryClickEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onCursorAnimatedItem(final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory topInventory = CompatUtils.getTopInventory(player);
        if (event.getAction().toString().contains("PLACE_ALL") || event.getAction().toString().contains("PLACE_ONE")) {
            ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getCursor());
            if (itemMap != null && itemMap.isSimilar(player, cursorItem.get(PlayerHandler.getPlayerID(player)))) {
                final int slot = event.getSlot();
                event.setCancelled(true);
                player.setItemOnCursor(new ItemStack(Material.AIR));
                if (event.getRawSlot() <= 4 && event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() != InventoryType.CRAFTING && topInventory.getSize() - 1 >= event.getRawSlot()) {
                    topInventory.setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));
                } else {
                    player.getInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));
                }
                cursorItem.remove(PlayerHandler.getPlayerID(player));
                ServerUtils.logDebug("{ItemMap} (Cursor_Place): Updated Animation Item.");
            }
        } else if (event.getAction().toString().contains("SWAP_WITH_CURSOR")) {
            ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(event.getCursor());
            if (itemMap != null && event.getCurrentItem() != null && itemMap.isSimilar(player, cursorItem.get(PlayerHandler.getPlayerID(player))) && ItemUtilities.getUtilities().isAllowed(player, event.getCurrentItem(), "inventory-modify")) {
                final int slot = event.getSlot();
                final ItemStack item = new ItemStack(event.getCurrentItem());
                event.setCancelled(true);
                player.setItemOnCursor(item);
                if (event.getRawSlot() <= 4 && event.getInventory().getType() == InventoryType.CRAFTING || event.getInventory().getType() != InventoryType.CRAFTING && topInventory.getSize() - 1 >= event.getRawSlot()) {
                    topInventory.setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));
                } else {
                    player.getInventory().setItem(slot, cursorItem.get(PlayerHandler.getPlayerID(player)));
                }
                cursorItem.remove(PlayerHandler.getPlayerID(player));
                ServerUtils.logDebug("{ItemMap} (Cursor_Swap): Updated Animation Item.");
            }
        }
    }

    /**
     * Resolves bugs with older versions of Bukkit, removes all items and resets them to prevent duplicating.
     *
     * @param player - that is dropping the item.
     */
    private void LegacyDropEvent(final Player player) {
        if (!ServerUtils.hasSpecificUpdate("1_9")) {
            dropClick.put(PlayerHandler.getPlayerID(player), true);
            final ItemStack[] Inv = player.getInventory().getContents().clone();
            final ItemStack[] Armor = player.getInventory().getArmorContents().clone();
            SchedulerUtils.runLater(1L, () -> {
                if (this.dropClick.get(PlayerHandler.getPlayerID(player)) != null && this.dropClick.get(PlayerHandler.getPlayerID(player))
                        && this.droppedItem.get(PlayerHandler.getPlayerID(player)) != null && this.droppedItem.get(PlayerHandler.getPlayerID(player))) {
                    player.getInventory().clear();
                    player.getInventory().setHelmet(null);
                    player.getInventory().setChestplate(null);
                    player.getInventory().setLeggings(null);
                    player.getInventory().setBoots(null);
                    if (ServerUtils.hasSpecificUpdate("1_9")) {
                        player.getInventory().setItemInOffHand(null);
                    }
                    player.getInventory().setContents(Inv);
                    player.getInventory().setArmorContents(Armor);
                    PlayerHandler.updateInventory(player, 1L);
                    this.droppedItem.remove(PlayerHandler.getPlayerID(player));
                }
                this.dropClick.remove(PlayerHandler.getPlayerID(player));
            });
        }
    }
}