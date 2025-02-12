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
import me.RockinChaos.core.utils.*;
import me.RockinChaos.core.utils.keys.CompositeKey;
import me.RockinChaos.core.utils.keys.PrimaryKey;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Commands implements Listener {

    /**
     * Checks for Creative "Destroy Item" and attempts to prevent commands from being executed.
     * Another yet unnecessary fix to resolve the horribly designed Creative Mode.
     *
     * @param event - PrepareItemCraftEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onCreativeCraft(PrepareItemCraftEvent event) {
        final Player player = CompatUtils.getPlayer(event.getView());
        if (PlayerHandler.isCreativeMode(player) && PlayerHandler.isCraftingInv(player)) {
            TimerUtils.setExpiry("cc_destroy", player.getUniqueId(), 20);
        }
    }

    /**
     * Runs the inventory commands for the custom item upon clicking it.
     *
     * @param event - InventoryClickEvent
     */
    @EventHandler()
    private void onInventory(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack item = event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR ? event.getCurrentItem().clone() : (event.getCursor() != null && event.getCursor().getType() != Material.AIR ? event.getCursor().clone() : event.getCurrentItem());
        final String action = event.getAction().name();
        final String slot = (event.getSlotType().name().equalsIgnoreCase("CRAFTING") ? "CRAFTING[" + event.getSlot() + "]" : String.valueOf(event.getSlot()));
        if (!PlayerHandler.isCreativeMode(player)) {
            this.runCommands(player, null, item, action, event.getClick().name(), slot);
        } else {
            SchedulerUtils.run(() -> {
                if (TimerUtils.isExpired("cc_destroy", player.getUniqueId())) {
                    this.runCommands(player, null, item, action, "CREATIVE", slot);
                }
            });
        }
    }

    /**
     * Runs the on_death and on_kill commands for the custom item upon player death.
     *
     * @param event - PlayerDeathEvent.
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(PlayerDeathEvent event) {
        final Player killer = event.getEntity().getKiller();
        for (ItemStack item : event.getDrops()) {
            this.runCommands(event.getEntity(), null, item, "ON_DEATH", "DEAD", null);
        }
        if (killer != null) {
            for (ItemStack item : killer.getInventory()) {
                this.runCommands(event.getEntity(), null, item, "ON_KILL", "KILLER", null);
            }
            if (PlayerHandler.isCraftingInv(killer))
                for (ItemStack item : CompatUtils.getTopInventory(killer)) {
                    this.runCommands(event.getEntity(), null, item, "ON_KILL", "KILLER", null);
                }
        }
    }

    /**
     * Runs the on_drops commands for the custom item upon item drop.
     *
     * @param event - PlayerDropItemEvent.
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onDrop(PlayerDropItemEvent event) {
        final ItemStack item = event.getItemDrop().getItemStack();
        this.runCommands(event.getPlayer(), null, item, "ON_DROP", "DROP", String.valueOf(event.getPlayer().getInventory().getHeldItemSlot()));
    }

    /**
     * Runs the on_consume commands for the custom item upon item consumption.
     *
     * @param event - PlayerItemConsumeEvent.
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onConsume(PlayerItemConsumeEvent event) {
        final ItemStack item = event.getItem();
        this.runCommands(event.getPlayer(), null, item, "ON_CONSUME", "CONSUME", String.valueOf(event.getPlayer().getInventory().getHeldItemSlot()));
    }

    /**
     * Runs the on_fire commands for the custom item upon the player shooting a bow.
     *
     * @param event - EntityShootBowEvent.
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onFire(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            final ItemStack bow = (event.getBow() != null ? event.getBow().clone() : event.getBow());
            this.runCommands((Player) event.getEntity(), null, bow, "ON_FIRE", "FIRE", String.valueOf(((Player) event.getEntity()).getInventory().getHeldItemSlot()));
            if (ServerUtils.hasSpecificUpdate("1_16")) {
                final ItemStack arrow = (event.getConsumable() != null ? event.getConsumable().clone() : event.getConsumable());
                this.runCommands((Player) event.getEntity(), null, arrow, "ON_FIRE", "FIRE", null);
            }
        }
    }

    /**
     * Runs the on_hold commands for the custom item upon holding it.
     *
     * @param event - PlayerItemHeldEvent
     */
    @EventHandler()
    private void onHold(PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItem(event.getNewSlot());
        final String slot = String.valueOf(event.getNewSlot());
        this.runCommands(player, null, item, "ON_HOLD", "HELD", slot);
    }

    /**
     * Runs the on_join commands for custom items upon joining the server.
     *
     * @param event - PlayerJoinEvent
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if (ItemJoin.getCore().getDependencies().authMeEnabled()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        if (ItemJoin.getCore().getDependencies().isAuthenticated(player)) {
                            handleJoin(player);
                            this.cancel();
                        } else if (!player.isOnline()) {
                            this.cancel();
                        }
                    } catch (NoClassDefFoundError e) {
                        ServerUtils.logSevere("{Commands} You are using an outdated version of AuthMe, ON_JOIN commands will not be executed.");
                        this.cancel();
                    }
                }
            }.runTaskTimer(ItemJoin.getCore().getPlugin(), 0, 20);
        } else {
            this.handleJoin(player);
        }
    }

    /**
     * Handles running the on_join commands for custom items upon joining the server.
     *
     * @param player - the player being referenced.
     */
    private void handleJoin(final Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            final ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                this.runCommands(player, null, item, "ON_JOIN", "JOINED", String.valueOf(i));
            }
        }
        if (PlayerHandler.isCraftingInv(player)) {
            for (int i = 0; i < CompatUtils.getTopInventory(player).getSize(); i++) {
                final ItemStack item = CompatUtils.getTopInventory(player).getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    this.runCommands(player, null, item, "ON_JOIN", "JOINED", String.valueOf(i));
                }
            }
        }
    }

    /**
     * Runs the on_equip and un_equip commands for the custom item upon clicking and moving it to the armor slots.
     *
     * @param event - InventoryClickEvent
     */
    @EventHandler()
    private void onEquipClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory bottomInventory = CompatUtils.getBottomInventory(event);
        if (PlayerHandler.isCraftingInv(event.getView())) {
            if (StringUtils.containsIgnoreCase(event.getAction().name(), "HOTBAR") && bottomInventory.getSize() >= event.getHotbarButton() && event.getHotbarButton() >= 0
                    && !event.getClick().name().equalsIgnoreCase("MIDDLE") && event.getSlotType() == SlotType.ARMOR) {
                final ItemStack hotItem = bottomInventory.getItem(event.getHotbarButton());
                if (hotItem != null && hotItem.getType() != Material.AIR) {
                    this.equipCommands(player, hotItem, "ON_EQUIP", "EQUIPPED", String.valueOf(event.getSlot()));
                }
            }
            if (!event.getClick().name().equalsIgnoreCase("MIDDLE") && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                if (event.getSlotType() == SlotType.ARMOR) {
                    this.equipCommands(player, event.getCurrentItem(), "UN_EQUIP", "UNEQUIPPED", String.valueOf(event.getSlot()));
                } else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    final String[] itemType = (event.getCurrentItem().getType().name().equalsIgnoreCase("ELYTRA") ? "ELYTRA_CHESTPLATE".split("_") :
                            (ItemHandler.isSkull(event.getCurrentItem().getType()) || StringUtils.splitIgnoreCase(event.getCurrentItem().getType().name(), "HEAD", "_") ? "SKULL_HELMET".split("_") : event.getCurrentItem().getType().name().split("_")));
                    if (itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && StringUtils.isInt(StringUtils.getArmorSlot(itemType[1], true))
                            && player.getInventory().getItem(Integer.parseInt(StringUtils.getArmorSlot(itemType[1], true))) == null) {
                        this.equipCommands(player, event.getCurrentItem(), "ON_EQUIP", "SHIFT_EQUIPPED", String.valueOf(event.getSlot()));
                    }
                }
            }
            if (!event.getClick().name().equalsIgnoreCase("MIDDLE") && !event.getClick().name().contains("SHIFT") && event.getSlotType() == SlotType.ARMOR && event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
                this.equipCommands(player, event.getCursor(), "ON_EQUIP", "EQUIPPED", String.valueOf(event.getSlot()));
            }
        }
    }

    /**
     * Runs the on_equip and un_equip commands for the custom item upon clicking and dragging it to the armor slots.
     *
     * @param event - InventoryDragEvent
     */
    @EventHandler()
    private void onEquipDrag(InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Set<Integer> slideSlots = event.getInventorySlots();
        int slot = 0;
        for (int actualSlot : slideSlots) {
            slot = actualSlot;
            break;
        }
        if (event.getOldCursor().getType() != Material.AIR && PlayerHandler.isCraftingInv(event.getView())) {
            this.equipCommands(player, event.getOldCursor(), "ON_EQUIP", "EQUIPPED", String.valueOf(slot));
        }
    }

    /**
     * Runs the on_equip and un_equip commands for the custom item upon right-clicking it from their hand to the armor slots.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler()
    private void onEquip(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Action action = event.getAction();
        final ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
        if (item != null && item.getType() != Material.AIR && PlayerHandler.isCraftingInv(player) && !PlayerHandler.isMenuClick(player, event.getAction())) {
            final String[] itemType = (item.getType().name().equalsIgnoreCase("ELYTRA") ? "ELYTRA_CHESTPLATE".split("_") :
                    (ItemHandler.isSkull(item.getType()) || StringUtils.splitIgnoreCase(item.getType().name(), "HEAD", "_") ? "SKULL_HELMET".split("_") : item.getType().name().split("_")));
            if (itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && StringUtils.isInt(StringUtils.getArmorSlot(itemType[1], true)) && !itemType[0].equalsIgnoreCase("SKULL")) {
                final ItemStack existingItem = player.getInventory().getItem(Integer.parseInt(StringUtils.getArmorSlot(itemType[1], true)));
                if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
                    this.equipCommands(player, item, "ON_EQUIP", "EQUIPPED", StringUtils.getArmorSlot(itemType[1], true));
                } else if ((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) && existingItem != null && existingItem.getType() != Material.AIR) {
                    this.equipCommands(player, item, "UN_EQUIP", "UNEQUIPPED", StringUtils.getArmorSlot(itemType[1], true));
                }
            }
        }
    }

    /**
     * Runs the on_damage commands for the custom item upon damaging an entity or being damaged by an entity.
     *
     * @param event - EntityDamageByEntityEvent
     */
    @EventHandler()
    public void onDamage(EntityDamageByEntityEvent event) {
        final Player player = ((event.getEntity() instanceof Player) ? (Player) event.getEntity() : (event.getDamager() instanceof Player) ? (Player) event.getDamager() : null);
        if (player != null) {
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                this.handleOnDamage(player, (event.getEntity() instanceof Player ? (Player) event.getEntity() : null), String.valueOf(i));
            }
            if (PlayerHandler.isCraftingInv(player)) {
                for (int i = 0; i < CompatUtils.getTopInventory(player).getSize(); i++) {
                    this.handleOnDamage(player, (event.getEntity() instanceof Player ? (Player) event.getEntity() : null), "CR" + i);
                }
            }
        }
    }

    /**
     * Runs the on_hit commands for the custom item upon damaging an entity.
     *
     * @param event - EntityDamageByEntityEvent
     */
    @EventHandler()
    public void onHit(EntityDamageByEntityEvent event) {
        final Player player = ((event.getDamager() instanceof Player) ? (Player) event.getDamager() : null);
        if (player != null) {
            final ItemStack item = PlayerHandler.getHandItem(player);
            final int slot = player.getInventory().getHeldItemSlot();
            if (item.getType() != Material.AIR && !PlayerHandler.isMenuClick(player, Action.LEFT_CLICK_AIR)) {
                this.runCommands(player, (event.getEntity() instanceof Player ? (Player) event.getEntity() : null), item, "ON_HIT", "HIT", Integer.toString(slot));
            }
        }
    }

    /**
     * Saves projectile ItemStacks as their id reference to be used in the ProjectileHitEvent.
     *
     * @param event - ProjectileLaunchEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();
        final Player player = (projectile.getShooter() instanceof Player ? (Player) projectile.getShooter() : null);
        if (player != null) {
            ItemStack item = PlayerHandler.getMainHandItem(player).clone();
            if (item.getAmount() == 0) {
                item.setAmount(1);
            }
            TimerUtils.setExpiry("pj_teleport", new PrimaryKey(projectile.getEntityId()).addMetaData("projectile", item.clone()), 2, TimeUnit.MINUTES);
        }
    }

    /**
     * Runs the on_hit commands for the custom item upon a projectile hitting an enemy.
     *
     * @param event - ProjectileHitEvent
     */
    @EventHandler()
    public void onProjectileHit(ProjectileHitEvent event) {
        final Player player = ((event.getEntity().getShooter() instanceof Player) ? (Player) event.getEntity().getShooter() : null);
        Entity hitEntity = (ServerUtils.hasSpecificUpdate("1_12") ? event.getHitEntity() : null);
        if (!ServerUtils.hasSpecificUpdate("1_12")) {
            for (Entity entity : event.getEntity().getNearbyEntities(2, 2, 2)) {
                if (entity instanceof Player) {
                    hitEntity = entity;
                }
                break;
            }
        }
        final Player altPlayer = ((hitEntity instanceof Player) ? (Player) hitEntity : null);
        final Object projectile = TimerUtils.getAlive("pj_teleport", new PrimaryKey(event.getEntity().getEntityId()));
        if (player != null && hitEntity != null && projectile != null) {
            final int slot = player.getInventory().getHeldItemSlot();
            final ItemStack item = ((ItemStack)((PrimaryKey)projectile).getMetaData("projectile"));
            if (item != null && item.getType() != Material.AIR && !PlayerHandler.isMenuClick(player, Action.LEFT_CLICK_AIR)) {
                this.runCommands(player, altPlayer, item, "ON_HIT", "HIT", Integer.toString(slot));
            }
        }
    }

    /**
     * Runs the commands upon right or left-clicking the custom item on an entity.
     *
     * @param event - PlayerInteractEntityEvent
     */
    @EventHandler()
    private void onEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof org.bukkit.entity.ItemFrame) {
            ItemStack item;
            if (ServerUtils.hasSpecificUpdate("1_9")) {
                item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().name());
            } else {
                item = PlayerHandler.getPerfectHandItem(event.getPlayer(), "");
            }
            final Player player = event.getPlayer();
            final String action = Action.RIGHT_CLICK_BLOCK.name();
            final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(PlayerHandler.getHandItem(player));
            if (itemMap != null && itemMap.isSimilar(player, item)) {
                this.runCommands(player, null, item, action, action.split("_")[0], String.valueOf(player.getInventory().getHeldItemSlot()));
            }
        }
    }

    /**
     * Runs the commands upon right or left-clicking the custom item towards an entity.
     *
     * @param event - PlayerInteractAtEntityEvent
     */
    @EventHandler()
    private void onTargetEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().toString().equalsIgnoreCase("CraftArmorStand")) {
            ItemStack item;
            if (ServerUtils.hasSpecificUpdate("1_9")) {
                item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().name());
            } else {
                item = PlayerHandler.getPerfectHandItem(event.getPlayer(), "");
            }
            final Player player = event.getPlayer();
            final String action = Action.RIGHT_CLICK_BLOCK.name();
            final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(PlayerHandler.getHandItem(player));
            if (itemMap != null && itemMap.isSimilar(player, item)) {
                this.runCommands(player, null, item, action, action.split("_")[0], String.valueOf(player.getInventory().getHeldItemSlot()));
            }
        }
    }

    /**
     * Sets a timer for the player dropping an item to prevent any duplicate command execution, typically because of the hand swing animation.
     *
     * @param event - PlayerDropItemEvent
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onHandDrop(PlayerDropItemEvent event) {
        if (TimerUtils.isExpired("dd_drop", event.getPlayer().getUniqueId())) {
            TimerUtils.setExpiry("dd_drop", event.getPlayer().getUniqueId(), 900, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Runs the commands upon physically interacting with the custom item.
     *
     * @param event - PlayerInteractEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = (event.getItem() != null ? event.getItem().clone() : (event.getAction() == Action.PHYSICAL ? PlayerHandler.getMainHandItem(player) : event.getItem()));
        final String action = event.getAction().name();
        SchedulerUtils.runLater(ServerUtils.isFolia() ? 2L : 0L, () -> { /* need to schedule for the next available tick to prevent commands from being run when dropping items, occasionally PlayerInteractEvent triggers before PlayerDropEvent. */
            if (((PlayerHandler.isAdventureMode(player) && !action.contains("LEFT") || !PlayerHandler.isAdventureMode(player))) && TimerUtils.isExpired("dd_drop", player.getUniqueId())) {
                final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(PlayerHandler.getHandItem(player));
                if (!PlayerHandler.isMenuClick(player, event.getAction()) && itemMap != null && itemMap.isSimilar(player, item)) {
                    if (TimerUtils.isExpired("dd_interact", new CompositeKey(player.getUniqueId(), item))) {
                        TimerUtils.setExpiry("dd_interact", new CompositeKey(player.getUniqueId(), item), 30, TimeUnit.MILLISECONDS);
                        this.runCommands(player, null, item, action, (action.equals(Action.PHYSICAL.name()) ? "INTERACTED" : action.split("_")[0]), String.valueOf(player.getInventory().getHeldItemSlot()));
                    }
                }
            }
        });
    }

    /**
     * Runs the commands upon left-clicking with the custom item in adventure mode.
     *
     * @param event - PlayerAnimationEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onSwingArm(PlayerAnimationEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = PlayerHandler.getHandItem(player);
        SchedulerUtils.runLater(ServerUtils.isFolia() ? 2L : 0L, () -> { /* need to schedule for the next available tick to prevent commands from being run when dropping items, occasionally PlayerAnimationEvent triggers before PlayerDropEvent. */
            if (PlayerHandler.isAdventureMode(player) && TimerUtils.isExpired("dd_drop", player.getUniqueId()) && (!PlayerHandler.isMenuClick(player, Action.LEFT_CLICK_AIR) || PlayerHandler.isMenuClick(player, Action.LEFT_CLICK_BLOCK))) {
                this.runCommands(player, null, item, "LEFT_CLICK_AIR", "LEFT", String.valueOf(player.getInventory().getHeldItemSlot()));
            }
        });
    }

    /**
     * Runs the on damage commands.
     *
     * @param player    - The player being damaged or damaging an entity.
     * @param altPlayer - that is associated with the commands.
     * @param slot      - The slot being checked.
     */
    public void handleOnDamage(final Player player, final Player altPlayer, final String slot) {
        final ItemStack item = (slot.startsWith("CR") ? CompatUtils.getTopInventory(player).getItem(Integer.parseInt(slot.replace("CR", ""))) : player.getInventory().getItem(Integer.parseInt(slot)));
        if (item != null && item.getType() != Material.AIR && !PlayerHandler.isMenuClick(player, Action.LEFT_CLICK_AIR)) {
            this.runCommands(player, altPlayer, item, "ON_DAMAGE", "DAMAGED", slot.replace("CR", ""));
        }
    }

    /**
     * Runs the on_equip and un_equip custom item commands.
     *
     * @param player    - that is running the commands.
     * @param item      - the item the player is trying to use to execute a command.
     * @param action    - the action that is being performed.
     * @param clickType - the clicking type that is being performed.
     * @param slot      - the slot the item originally resided in.
     */
    private void equipCommands(final Player player, final ItemStack item, final String action, final String clickType, final String slot) {
        final String[] itemType = (item.getType().name().equalsIgnoreCase("ELYTRA") ? "ELYTRA_CHESTPLATE".split("_") :
                (ItemHandler.isSkull(item.getType()) || StringUtils.splitIgnoreCase(item.getType().name(), "HEAD", "_") ? "SKULL_HELMET".split("_") : item.getType().name().split("_")));
        if (itemType.length >= 2 && (itemType[1] != null && !itemType[1].isEmpty()
                && (clickType.equalsIgnoreCase("SHIFT_EQUIPPED") || itemType[1].equalsIgnoreCase(StringUtils.getArmorSlot(slot, false))))) {
            if (!PlayerHandler.isCreativeMode(player)) {
                this.runCommands(player, null, item, action, (clickType.equalsIgnoreCase("SHIFT_EQUIPPED") ? "EQUIPPED" : clickType), slot);
            } else {
                SchedulerUtils.run(() -> {
                    if (TimerUtils.isExpired("cc_destroy", player.getUniqueId())) {
                        this.runCommands(player, null, item, action, (clickType.equalsIgnoreCase("SHIFT_EQUIPPED") ? "EQUIPPED" : clickType), slot);
                    }
                });
            }
        }
    }

    /**
     * Runs the custom item commands.
     *
     * @param player    - that is running the commands.
     * @param altPlayer - that is associated with the commands.
     * @param item      - the item the player is trying to use to execute a command.
     * @param action    - the action that is being performed.
     * @param slot      - the slot the item originally resided in.
     */
    private void runCommands(final Player player, final Player altPlayer, final ItemStack item, final String action, final String clickType, final String slot) {
        final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
        SchedulerUtils.runAsync(() -> {
            if (itemMap != null && itemMap.inWorld(player.getWorld()) && itemMap.hasPermission(player, player.getWorld())) {
                itemMap.executeCommands(player, altPlayer, item, action, clickType, (slot == null ? itemMap.getSlot() : slot));
            }
        });
    }
}