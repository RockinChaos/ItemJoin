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
import me.RockinChaos.core.utils.protocol.events.PlayerAutoCraftEvent;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Crafting implements Listener {

    private final HashMap<String, Long> closeDupe = new HashMap<>();
    private final HashMap<String, Boolean> pendingZero = new HashMap<>();
    private final List<String> whiteList = Collections.singletonList("GraphicalQuests");

    /**
     * Prevents players from auto crafting with custom crafting items in their crafting slots.
     *
     * @param event - PlayerAutoCraftEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onAutoCraft(PlayerAutoCraftEvent event) {
        ItemStack[] craftingContents = event.getContents();
        if (craftingContents == null) {
            return;
        }
        ServerUtils.logDebug("{CRAFTING} Protocol-Packet auto recipe was triggered for the player " + event.getPlayer().getName() + ".");
        for (ItemStack craftItem : craftingContents) {
            if (event.isCancelled()) {
                return;
            }
            for (ItemMap itemMap : ItemUtilities.getUtilities().getCraftingItems()) {
                if (!event.isCancelled() && !this.hasRecipePlugin() && itemMap.isReal(craftItem)) {
                    event.setCancelled(true);
                } else if (event.isCancelled()) {
                    return;
                }
            }
        }
    }

    /**
     * Checks if another plugin has control over the Auto Recipe event.
     * Attempts to prevent overriding another plugins event handling.
     *
     * @return If a recipe plugin exists.
     */
    private boolean hasRecipePlugin() {
        for (String plugin : this.whiteList) {
            if (Bukkit.getServer().getPluginManager().isPluginEnabled(plugin) && !StringUtils.containsIgnoreCase(ItemJoin.getCore().getDependencies().getIgnoreList(), plugin)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes custom crafting items from the players inventory when opening a GUI menu or storable inventory.
     *
     * @param event - InventoryOpenEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onCraftingOpen(InventoryOpenEvent event) {
        final Player player = (Player) event.getPlayer();
        if (!PlayerHandler.getOpenCraftItems().containsKey(PlayerHandler.getPlayerID(player))) {
            ServerUtils.logDebug("{CRAFTING} Bukkit inventory was opened for the player " + event.getPlayer().getName() + ".");
            PlayerHandler.addOpenCraftItems(player, PlayerHandler.getTopContents(player));
            ItemHandler.removeCraftItems(player);
            SchedulerUtils.runLater(1L, () -> {
                if (PlayerHandler.isCraftingInv(player)) {
                    PlayerHandler.updateInventory(player);
                }
            });
        }
    }

    /**
     * Gives the custom crafting items back when the player closes their inventory if they had items existing previously.
     *
     * @param event - InventoryCloseEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onCraftingClose(org.bukkit.event.inventory.InventoryCloseEvent event) {
        long dupeDuration = !this.closeDupe.isEmpty() && this.closeDupe.get(PlayerHandler.getPlayerID((Player) event.getPlayer())) != null ? System.currentTimeMillis() - this.closeDupe.get(PlayerHandler.getPlayerID((Player) event.getPlayer())) : -1;
        if (!PlayerHandler.isCraftingInv(event.getView()) || (PlayerHandler.isCraftingInv(event.getView()) && (dupeDuration == -1 || dupeDuration > 30))) {
            final Inventory topInventory = CompatUtils.getTopInventory(event.getView());
            ServerUtils.logDebug("{CRAFTING} Bukkit inventory was closed for the player " + event.getPlayer().getName() + ".");
            ItemStack[] topContents = ItemHandler.cloneContents(topInventory.getContents());
            this.handleClose(slot -> topInventory.setItem(slot, new ItemStack(Material.AIR)), (Player) event.getPlayer(), event.getView(), topContents, true);
        }
    }

    /**
     * Gives the custom crafting items back when the player closes their inventory if they had items existing previously.
     *
     * @param event - InventoryCloseEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onCraftingClose(me.RockinChaos.core.utils.protocol.events.InventoryCloseEvent event) {
        if (PlayerHandler.isCraftingInv(event.getView())) {
            ServerUtils.logDebug("{CRAFTING} Protocol-Packet inventory was closed for the player " + event.getPlayer().getName() + ".");
            this.closeDupe.put(PlayerHandler.getPlayerID(event.getPlayer()), System.currentTimeMillis());
            this.handleClose(slot -> {
                if (!event.isCancelled()) {
                    event.setCancelled(true);
                }
            }, event.getPlayer(), event.getView(), event.getPreviousContents(true), false);
        }
    }

    /**
     * Allows the player to move their custom crafting item around their inventory without them dropping
     * or bugging out. This creates a seamless transition between their inventory and crafting slots.
     *
     * @param event - InventoryClickEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onCraftingClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack[] craftingContents = CompatUtils.getTopInventory(event.getView()).getContents().clone();
        if (PlayerHandler.isCraftingInv(event.getView()) && event.getSlot() <= 4) {
            if (event.getSlot() != 0 && event.getSlotType() == SlotType.CRAFTING) {
                if (craftingContents[0] != null && craftingContents[0].getType() != Material.AIR) {
                    final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(craftingContents[0]);
                    if (itemMap != null && itemMap.isCraftingItem()) {
                        ItemHandler.returnCraftingItem(player, 0, craftingContents[0], 1L);
                    }
                }
            } else if (event.getSlot() == 0 && event.getSlotType() == SlotType.RESULT) {
                if (craftingContents[0] != null && craftingContents[0].getType() != Material.AIR) {
                    for (ItemMap itemMap : ItemUtilities.getUtilities().getCraftingItems()) {
                        if (!itemMap.isMovement() && itemMap.isSimilar(player, craftingContents[0])) {
                            for (int i = 1; i <= 4; i++) {
                                ItemHandler.returnCraftingItem(player, i, craftingContents[i].clone(), 1L);
                            }
                            SchedulerUtils.runLater(1L, () -> CompatUtils.getTopInventory(player).setItem(0, new ItemStack(Material.AIR)));
                            break;
                        }
                    }
                } else if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
                    for (ItemMap itemMap : ItemUtilities.getUtilities().getCraftingItems()) {
                        if (!itemMap.isMovement() && itemMap.isSimilar(player, event.getCursor())) {
                            ItemStack cursor = event.getCursor().clone();
                            player.setItemOnCursor(new ItemStack(Material.AIR));
                            ItemHandler.returnCraftingItem(player, 0, cursor, 1L);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes all crafting items from the 2x2 crafting view when the player leaves the server.
     * Returns the custom crafting item to the player if it is dropped automagically when switching worlds,
     * typically via the nether portal causing duplication glitches.
     *
     * @param event - PlayerDropItemEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onCraftingDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final World world = player.getWorld();
        final ItemStack itemCopy = event.getItemDrop().getItemStack().clone();
        final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(itemCopy);
        double health;
        try {
            health = player.getHealth();
        } catch (Exception e) {
            health = (player.isDead() ? 0 : 1);
        }
        if (health > 0 && itemMap != null && itemMap.isCraftingItem()) {
            event.getItemDrop().remove();
            SchedulerUtils.runLater(2L, () -> {
                if (!world.equals(player.getWorld()) && itemMap.inWorld(player.getWorld())) {
                    itemMap.giveTo(player, itemCopy.getAmount());
                    if (StringUtils.getSlotConversion(itemMap.getSlot()) != 0) {
                        this.returnSlotZero(player);
                    }
                } else if (world.equals(player.getWorld())) {
                    if (player.isOnline() && itemMap.isSelfDroppable()) {
                        itemMap.giveTo(player, itemCopy.getAmount());
                        if (StringUtils.getSlotConversion(itemMap.getSlot()) != 0) {
                            this.returnSlotZero(player);
                        }
                    } else if (player.isOnline()) {
                        PlayerHandler.dropItem(player, itemCopy);
                    }
                }
            });
        }
    }

    /**
     * Called on player switching worlds.
     * Removes any crafting items from the player which ended up in their inventory slots.
     *
     * @param event - PlayerChangedWorldEvent
     */
    @EventHandler(ignoreCancelled = true)
    private void onCraftingWorldSwitch(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final ItemStack[] inventory = player.getInventory().getContents();
        if (ItemHandler.isContentsEmpty(inventory)) {
            for (int i = 0; i < inventory.length; i++) {
                for (ItemMap itemMap : ItemUtilities.getUtilities().getCraftingItems()) {
                    if ((itemMap.isCraftingItem() && itemMap.isReal(inventory[i]))) {
                        player.getInventory().setItem(i, new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    /**
     * Removes custom crafting items from the player when they enter creative mode.
     *
     * @param event - PlayerGameModeChangeEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onSwitchGamemode(PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.CREATIVE) {
            PlayerHandler.addCreativeCraftItems(player, PlayerHandler.getCraftItems().get(PlayerHandler.getPlayerID(player)));
            ItemHandler.removeCraftItems(player);
        } else if (event.getNewGameMode() != GameMode.CREATIVE && PlayerHandler.getCreativeCraftItems().containsKey(PlayerHandler.getPlayerID(player))) {
            this.returnCrafting(event.getPlayer(), PlayerHandler.getCreativeCraftItems().get(PlayerHandler.getPlayerID(player)), 1L, false);
            PlayerHandler.addCraftItems(player, PlayerHandler.getCreativeCraftItems().get(PlayerHandler.getPlayerID(player)));
            PlayerHandler.addCraftItems(player, PlayerHandler.getCreativeCraftItems().get(PlayerHandler.getPlayerID(player)));
            PlayerHandler.removeCreativeCraftItems(player);
        }
        SchedulerUtils.runLater(1L, () -> {
            if (PlayerHandler.isCraftingInv(player)) {
                PlayerHandler.updateInventory(player);
            }
        });
    }

    /**
     * Attempts to save and return the prior open inventory crafting slots.
     *
     * @param input     - The methods to be executed.
     * @param player    - The Player being handled.
     * @param view      - The view being referenced.
     * @param inventory - The inventory being handled.
     */
    private void handleClose(final Consumer<Integer> input, final Player player, final Object view, final ItemStack[] inventory, final boolean slotZero) {
        if (PlayerHandler.isCraftingInv(view)) {
            final Inventory topInventory = CompatUtils.getTopInventory(player);
            if (ItemHandler.isContentsEmpty(inventory)) {
                boolean isCrafting = false;
                for (int i = 0; i <= 4; i++) {
                    for (ItemMap itemMap : ItemUtilities.getUtilities().getCraftingItems()) {
                        if ((itemMap.isCraftingItem() && itemMap.isReal(inventory[i]))) {
                            isCrafting = true;
                            input.accept(i);
                        }
                    }
                }
                for (int i = 0; i <= 4; i++) {
                    if (isCrafting && i != 0 && inventory[i] != null && inventory[i].getType() != Material.AIR) {
                        ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(inventory[i]);
                        if (itemMap == null || !itemMap.isCraftingItem() || !itemMap.isReal(inventory[i])) {
                            final int k = i;
                            final ItemStack drop = inventory[i].clone();
                            SchedulerUtils.run(() -> {
                                double health;
                                try {
                                    health = player.getHealth();
                                } catch (Exception e) {
                                    health = (player.isDead() ? 0 : 1);
                                }
                                if (health > 0) {
                                    topInventory.setItem(k, new ItemStack(Material.AIR));
                                    if (player.getInventory().firstEmpty() != -1) {
                                        player.getInventory().addItem(drop);
                                        ServerUtils.logDebug("{CRAFTING} An item was flagged as non-crafting, adding it back to the player " + player.getName());
                                    } else {
                                        Item itemDropped = player.getWorld().dropItem(player.getLocation(), drop);
                                        itemDropped.setPickupDelay(40);
                                        ServerUtils.logDebug("{CRAFTING} An item was flagged as non-crafting and the player " + player.getName() + " has a full inventory, item will instead be self-dropped.");
                                    }
                                }
                            });
                            inventory[i] = new ItemStack(Material.AIR);
                        }
                    }
                }
                if (isCrafting) {
                    if (!slotZero || ItemUtilities.getUtilities().getItemMap(inventory[0]) != null) {
                        this.returnCrafting(player, inventory, 1L, !slotZero);
                    } else {
                        SchedulerUtils.runLater(1L, () -> {
                            topInventory.setItem(0, new ItemStack(Material.AIR));
                            SchedulerUtils.runLater(1L, () -> {
                                if (PlayerHandler.isCraftingInv(player)) {
                                    PlayerHandler.updateInventory(player, new ItemStack(Material.AIR), 0L);
                                }
                            });
                        });
                    }
                }
            }
        } else {
            SchedulerUtils.run(() -> {
                double health;
                try {
                    health = player.getHealth();
                } catch (Exception e) {
                    health = (player.isDead() ? 0 : 1);
                }
                if (health > 0 && PlayerHandler.isCraftingInv(player) && PlayerHandler.getOpenCraftItems().containsKey(PlayerHandler.getPlayerID(player))) {
                    ItemStack[] openCraftContents = PlayerHandler.getOpenCraftItems().get(PlayerHandler.getPlayerID(player));
                    if (openCraftContents != null && openCraftContents.length != 0) {
                        this.returnCrafting(player, openCraftContents, 1L, false);
                        PlayerHandler.addCraftItems(player, PlayerHandler.getOpenCraftItems().get(PlayerHandler.getPlayerID(player)));
                        PlayerHandler.removeOpenCraftItems(player);
                    }
                }
            });
        }
    }

    /**
     * Returns the zero slot item to prevent ghosting.
     *
     * @param player - The player having the zero slot item returned.
     */
    private void returnSlotZero(final Player player) {
        if ((this.pendingZero.get(PlayerHandler.getPlayerID(player)) != null && !this.pendingZero.get(PlayerHandler.getPlayerID(player)))
                || this.pendingZero.get(PlayerHandler.getPlayerID(player)) == null) {
            this.pendingZero.put(PlayerHandler.getPlayerID(player), true);
            SchedulerUtils.runLater(4L, () -> {
                for (ItemMap craftMap : ItemUtilities.getUtilities().getCraftingItems()) {
                    if (StringUtils.getSlotConversion(craftMap.getSlot()) == 0 && craftMap.inWorld(player.getWorld()) && craftMap.hasPermission(player, player.getWorld())) {
                        craftMap.giveTo(player);
                        this.pendingZero.remove(PlayerHandler.getPlayerID(player));
                    }
                }
            });
        }
    }

    /**
     * Returns the custom crafting item to the player after the specified delay.
     *
     * @param player   - the Player having their item returned.
     * @param contents - the crafting contents to be returned.
     * @param delay    - the delay to wait before returning the item.
     */
    private void returnCrafting(final Player player, final ItemStack[] contents, final long delay, final boolean slotZero) {
        SchedulerUtils.runLater(delay, () -> {
            if (!player.isOnline()) {
                return;
            } else if (!PlayerHandler.isCraftingInv(player)) {
                this.returnCrafting(player, contents, 10L, slotZero);
                return;
            }
            final Inventory topInventory = CompatUtils.getTopInventory(player);
            if (!slotZero && contents != null) {
                for (int i = 4; i >= 0; i--) {
                    final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(contents[i]);
                    if (contents[i] != null && itemMap != null) {
                        topInventory.setItem(i, contents[i]);
                        SchedulerUtils.runLater(1L, () -> {
                            if (PlayerHandler.isCraftingInv(player)) {
                                PlayerHandler.updateInventory(player, itemMap.getItemStack(player), 0L);
                            }
                        });
                    }
                }
            } else if (contents != null) {
                final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(contents[0]);
                if (contents[0] != null && itemMap != null) {
                    topInventory.setItem(0, contents[0]);
                    SchedulerUtils.runLater(1L, () -> {
                        if (PlayerHandler.isCraftingInv(player)) {
                            PlayerHandler.updateInventory(player, itemMap.getItemStack(player), 0L);
                        }
                    });
                }
            }
        });
    }
}