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

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.CompatUtils;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.listeners.Clicking;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ItemAnimation {

    private final ItemMap itemMap;
    private final List<String> dynamicNames;
    private final List<List<String>> dynamicLores;
    private final List<String> dynamicMaterials;
    private final List<String> dynamicOwners;
    private final List<String> dynamicTextures;
    private final List<String> dynamicPages;

    private static final Map<String, AtomicBoolean> GLOBAL_ANIMATION_FLAGS = new ConcurrentHashMap<>();
    private final List<Integer> scheduledTaskIds = new ArrayList<>();

    private volatile boolean menu = false;
    private List<List<String>> menuLores = null;

    /**
     * Called on new ItemAnimation.
     *
     * @param item - The ItemMap that handles item creation.
     */
    public ItemAnimation(final ItemMap item) {
        this.itemMap = item;
        this.dynamicNames = item.getDynamicNames() != null && !item.getDynamicNames().isEmpty() ? new ArrayList<>(item.getDynamicNames()) : null;
        this.dynamicLores = item.getDynamicLores() != null && !item.getDynamicLores().isEmpty() ? new ArrayList<>(item.getDynamicLores()) : null;
        this.dynamicMaterials = item.getDynamicMaterials() != null && !item.getDynamicMaterials().isEmpty() ? new ArrayList<>(item.getDynamicMaterials()) : null;
        this.dynamicPages = (this.itemMap.getMaterial().toString().equalsIgnoreCase("WRITTEN_BOOK") && item.getPages() != null && !item.getPages().isEmpty()) ? new ArrayList<>(item.getPages()) : null;
        this.dynamicOwners = item.getDynamicOwners() != null && !item.getDynamicOwners().isEmpty() ? new ArrayList<>(item.getDynamicOwners()) : null;
        this.dynamicTextures = (this.dynamicOwners == null && item.getDynamicTextures() != null && !item.getDynamicTextures().isEmpty()) ? new ArrayList<>(item.getDynamicTextures()) : null;
    }

    /**
     * Starts the animations for the specified player.
     *
     * @param player - The player which will have animations started.
     */
    public void openAnimation(final Player player) {
        final AtomicBoolean newFlag = new AtomicBoolean(true);
        if (!canAnimate(player, newFlag)) return;
        AtomicBoolean existingFlag = GLOBAL_ANIMATION_FLAGS.get(this.getPlayerAnimationKey(player));
        if (existingFlag != null) existingFlag.set(false);
        GLOBAL_ANIMATION_FLAGS.put(this.getPlayerAnimationKey(player), newFlag);
        SchedulerUtils.runAsync(() -> {
            if (canAnimate(player, newFlag)) {
                if (this.dynamicNames != null) {
                    this.nameTasks(player, newFlag);
                }
                if (this.dynamicLores != null) {
                    this.loreTasks(player, newFlag);
                }
                if (this.dynamicMaterials != null) {
                    this.materialTasks(player, newFlag);
                }
                if (this.dynamicPages != null) {
                    this.pagesTasks(player, newFlag);
                }
                if (this.dynamicOwners != null) {
                    this.ownerTasks(player, newFlag);
                } else if (this.dynamicTextures != null) {
                    this.textureTasks(player, newFlag);
                }
            }
            ServerUtils.logDebug("{Animation} Successfully STARTED all animations for the item " + this.itemMap.getConfigName() + " with the instanced player " + player.getName() + ".");
        });
    }

    /**
     * Stops the animations for the specified player.
     *
     * @param player - The player which will have animations stopped.
     */
    public void closeAnimation(final Player player) {
        AtomicBoolean flag = GLOBAL_ANIMATION_FLAGS.get(this.getPlayerAnimationKey(player));
        if (flag != null) flag.set(false);
        synchronized (scheduledTaskIds) {
            for (Integer taskId : scheduledTaskIds) {
                if (taskId != null && taskId != 0) {
                    SchedulerUtils.cancelTask(taskId);
                }
            }
            scheduledTaskIds.clear();
        }
        GLOBAL_ANIMATION_FLAGS.remove(this.getPlayerAnimationKey(player));
        ServerUtils.logDebug("{Animation} Successfully CLOSED all animations for the item " + this.itemMap.getConfigName() + " with the instanced player " + player.getName() + ".");
    }

    /**
     * Handles animations for the display name of a custom item.
     *
     * @param player - The player which will have animations handled.
     * @param flag - The flag to check if animation should continue.
     */
    private void nameTasks(final Player player, final AtomicBoolean flag) {
        if (this.dynamicNames == null || this.dynamicNames.isEmpty()) return;
        long cumulativeTicks = 0;
        final List<AnimationFrame> frames = new ArrayList<>();
        for (String name : this.dynamicNames) {
            final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(name));
            cumulativeTicks += (delay != null ? delay : 180);
            frames.add(new AnimationFrame(name, cumulativeTicks));
        }
        scheduleAnimationCycle(player, frames, AnimationType.NAME, flag);
    }

    /**
     * Handles animations for the displayed lore of a custom item.
     *
     * @param player - The player which will have animations handled.
     * @param flag - The flag to check if animation should continue.
     */
    private void loreTasks(final Player player, final AtomicBoolean flag) {
        if (this.dynamicLores == null || this.dynamicLores.isEmpty()) return;
        long cumulativeTicks = 0;
        final List<AnimationFrame> frames = new ArrayList<>();
        for (int position = 0; position < this.dynamicLores.size(); position++) {
            final List<String> lore = this.dynamicLores.get(position);
            final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(lore.get(0)));
            cumulativeTicks += (delay != null ? delay : 180);
            frames.add(new AnimationFrame(lore, cumulativeTicks, position));
        }
        scheduleAnimationCycle(player, frames, AnimationType.LORE, flag);
    }

    /**
     * Handles animations for the material of a custom item.
     *
     * @param player - The player which will have animations handled.
     * @param flag - The flag to check if animation should continue.
     */
    private void materialTasks(final Player player, final AtomicBoolean flag) {
        if (this.dynamicMaterials == null || this.dynamicMaterials.isEmpty()) return;
        long cumulativeTicks = 0;
        final List<AnimationFrame> frames = new ArrayList<>();
        for (String mat : this.dynamicMaterials) {
            final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(mat));
            cumulativeTicks += (delay != null ? delay : 180);
            frames.add(new AnimationFrame(mat, cumulativeTicks));
        }
        scheduleAnimationCycle(player, frames, AnimationType.MATERIAL, flag);
    }

    /**
     * Handles animations for the book pages of a custom book item.
     *
     * @param player - The player which will have animations handled.
     * @param flag - The flag to check if animation should continue.
     */
    private void pagesTasks(final Player player, final AtomicBoolean flag) {
        if (this.dynamicPages == null || this.dynamicPages.isEmpty()) return;
        final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(this.dynamicPages.get(0)));
        long cumulativeTicks = (delay != null ? delay : 180);
        final List<AnimationFrame> frames = new ArrayList<>();
        frames.add(new AnimationFrame(this.dynamicPages, cumulativeTicks));
        scheduleAnimationCycle(player, frames, AnimationType.PAGES, flag);
    }

    /**
     * Handles animations for the skull owner of a custom item.
     *
     * @param player - The player which will have animations handled.
     * @param flag - The flag to check if animation should continue.
     */
    private void ownerTasks(final Player player, final AtomicBoolean flag) {
        if (this.dynamicOwners == null || this.dynamicOwners.isEmpty()) return;
        long cumulativeTicks = 0;
        final List<AnimationFrame> frames = new ArrayList<>();
        for (String owner : this.dynamicOwners) {
            final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(owner));
            cumulativeTicks += (delay != null ? delay : 180);
            frames.add(new AnimationFrame(owner, cumulativeTicks));
        }
        scheduleAnimationCycle(player, frames, AnimationType.OWNER, flag);
    }

    /**
     * Handles animations for the skull texture of a custom item.
     *
     * @param player - The player which will have animations handled.
     * @param flag - The flag to check if animation should continue.
     */
    private void textureTasks(final Player player, final AtomicBoolean flag) {
        if (this.dynamicTextures == null || this.dynamicTextures.isEmpty()) return;
        long cumulativeTicks = 0;
        final List<AnimationFrame> frames = new ArrayList<>();
        for (String texture : this.dynamicTextures) {
            final Integer delay = StringUtils.returnInteger(ItemHandler.getDelayFormat(this.dynamicTextures.get(0)));
            cumulativeTicks += (delay != null ? delay : 180);
            frames.add(new AnimationFrame(texture, cumulativeTicks));
        }
        scheduleAnimationCycle(player, frames, AnimationType.TEXTURE, flag);
    }

    /**
     * Schedules an animation cycle for the given frames.
     *
     * @param player - The player for the animation.
     * @param frames - The list of animation frames.
     * @param type - The type of animation.
     * @param animateFlag - The flag that controls this animation session.
     */
    private void scheduleAnimationCycle(final Player player, final List<AnimationFrame> frames, final AnimationType type, final AtomicBoolean animateFlag) {
        final List<Integer> cycleTaskIds = new ArrayList<>();
        for (int i = 0; i < frames.size(); i++) {
            final AnimationFrame frame = frames.get(i);
            final boolean isLastFrame = (i == frames.size() - 1);
            final int taskId = SchedulerUtils.runAsyncLater(frame.delay, () -> {
                if (!animateFlag.get()) {
                    synchronized (scheduledTaskIds) {
                        scheduledTaskIds.removeAll(cycleTaskIds);
                    }
                    return;
                }
                if (!canAnimate(player, animateFlag)) {
                    synchronized (scheduledTaskIds) {
                        scheduledTaskIds.removeAll(cycleTaskIds);
                    }
                    return;
                }
                applyAnimationFrame(player, frame, type, animateFlag);
                if (isLastFrame) {
                    synchronized (scheduledTaskIds) {
                        scheduledTaskIds.removeAll(cycleTaskIds);
                    }
                    if (animateFlag.get() && canAnimate(player, animateFlag)) {
                        scheduleAnimationCycle(player, frames, type, animateFlag);
                    }
                }
            });
            if (taskId != 0) {
                cycleTaskIds.add(taskId);
                synchronized (scheduledTaskIds) {
                    scheduledTaskIds.add(taskId);
                }
            }
        }
    }

    /**
     * Applies a single animation frame to all relevant items.
     *
     * @param player - The player whose items to animate.
     * @param frame - The animation frame to apply.
     * @param type - The type of animation.
     * @param animateFlag - The flag that controls this animation session.
     */
    private void applyAnimationFrame(final Player player, final AnimationFrame frame, final AnimationType type, final AtomicBoolean animateFlag) {
        if (!animateFlag.get() || !canAnimate(player, animateFlag)) return;
        final ItemStack heldItem = ItemJoin.getCore().getConfig("config.yml").getBoolean("Settings.HeldItem-Animations") ? null : PlayerHandler.getHandItem(player);
        final int heldItemSlot = heldItem != null && heldItem.getType() != Material.AIR ? player.getInventory().getHeldItemSlot() : -1;

        // Animate inventory items
        SchedulerUtils.run(() -> {
            if (!animateFlag.get() || !canAnimate(player, animateFlag)) return;
            final PlayerInventory inventory = player.getInventory();
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                final ItemStack item = inventory.getItem(slot);
                if (isAnimateTarget(item, (slot == heldItemSlot) ? heldItem : null)) {
                    applyFrameToItem(player, item, frame, type);
                }
            }
        });

        // Animate armor items
        SchedulerUtils.run(() -> {
            if (!animateFlag.get() || !canAnimate(player, animateFlag)) return;
            for (ItemStack item : player.getInventory().getArmorContents()) {
                if (isAnimateTarget(item, null)) {
                    applyFrameToItem(player, item, frame, type);
                }
            }
        });

        // Animate items in open inventory (top inventory)
        SchedulerUtils.run(() -> {
            if (!animateFlag.get() || !canAnimate(player, animateFlag)) return;
            final org.bukkit.inventory.Inventory topInventory = CompatUtils.getTopInventory(player);
            // Check if this player is the first viewer (to prevent animation conflicts between multiple viewers)
            if (!topInventory.getViewers().isEmpty() && topInventory.getViewers().get(0).equals(player)) {
                for (ItemStack item : topInventory.getContents()) {
                    if (isAnimateTarget(item, null)) {
                        applyFrameToItem(player, item, frame, type);
                    }
                }
            }
        });

        // Animate cursor item
        SchedulerUtils.run(() -> {
            if (!animateFlag.get() || !canAnimate(player, animateFlag)) return;
            ItemStack cursorItem = player.getItemOnCursor();
            if (cursorItem.getType() != Material.AIR && itemMap.isReal(cursorItem)) {
                ItemStack item = new ItemStack(cursorItem);
                ItemStack clickingCursor = Clicking.getCursor(PlayerHandler.getPlayerID(player));
                if (clickingCursor != null && itemMap.isReal(clickingCursor)) {
                    item = new ItemStack(clickingCursor);
                }
                applyFrameToItem(player, cursorItem, frame, type);
                Clicking.putCursor(PlayerHandler.getPlayerID(player), item);
            }
        });

        // Update inventory
        SchedulerUtils.run(() -> {
            if (!animateFlag.get() || !canAnimate(player, animateFlag)) return;
            if (StringUtils.getSlotConversion(itemMap.getSlot()) != -1 && !ServerUtils.hasSpecificUpdate("1_13")) {
                LegacyAPI.updateInventory(player);
            } else {
                synchronized ("IJ_ANIMATE") {
                    final ItemStack updatedItem = itemMap.getItemStack(player);
                    if (updatedItem != null) {
                        PlayerHandler.updateInventory(player, updatedItem.clone(), 1L);
                    }
                }
            }
        });
    }

    /**
     * Checks if an item is a valid target for animation.
     *
     * @param item - The item to check.
     * @param heldItem - The held item to compare against (null to skip check).
     * @return true if the item should be animated.
     */
    private boolean isAnimateTarget(final ItemStack item, final ItemStack heldItem) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        if (itemMap.getTempItem() == null || !itemMap.isReal(item)) {
            return false;
        }
        return heldItem == null || !itemMap.isReal(heldItem);
    }

    /**
     * Applies an animation frame to a specific item.
     *
     * @param player - The player context.
     * @param item - The item to modify.
     * @param frame - The animation frame.
     * @param type - The animation type.
     */
    @SuppressWarnings("unchecked")
    private void applyFrameToItem(final Player player, final ItemStack item, final AnimationFrame frame, final AnimationType type) {
        switch (type) {
            case NAME:
                setNameData(player, item, (String) frame.data);
                break;
            case LORE:
                List<String> loreData = (List<String>) frame.data;
                if (menu && menuLores != null && frame.position < menuLores.size()) {
                    setLoreData(player, item, menuLores.get(frame.position));
                } else {
                    setLoreData(player, item, loreData);
                }
                break;
            case MATERIAL:
                setMaterialData(item, (String) frame.data);
                break;
            case PAGES:
                setPagesData(player, item, (List<String>) frame.data);
                break;
            case OWNER:
                setSkull(player, item, (String) frame.data, null);
                break;
            case TEXTURE:
                setSkull(player, item, null, (String) frame.data);
                break;
        }
    }

    /**
     * Sets the specified display name to the specified item.
     *
     * @param player     - The player which will have animations handled.
     * @param reviseItem - The item to have its display name animated.
     * @param nameString - The name to set to the item.
     */
    private void setNameData(final Player player, final ItemStack reviseItem, final String nameString) {
        ItemMeta tempMeta = reviseItem.getItemMeta();
        if (tempMeta != null && reviseItem.getType() != Material.AIR) {
            tempMeta = tempMeta.clone();
            String itemData = "";
            if (this.itemMap.getLegacySecret() != null && !this.itemMap.getLegacySecret().isEmpty()) {
                final String encodeData = Objects.requireNonNull(StringUtils.colorEncode(new ItemStack(Material.STICK), this.itemMap.getLegacySecret()).getItemMeta()).getDisplayName();
                if (!encodeData.isEmpty()) {
                    itemData = ChatColor.COLOR_CHAR + "r" + encodeData;
                }
            }
            tempMeta.setDisplayName(StringUtils.translateLayout(ItemHandler.cutDelay(nameString), player) + itemData);
            reviseItem.setItemMeta(tempMeta);
            if (this.itemMap.isDynamicCount()) { // Better but still temporary--implementation for a list of animated item count is planned.
                reviseItem.setAmount(this.itemMap.getCount(player));
            }
        }
    }

    /**
     * Sets the specified displayed lore to the specified item.
     *
     * @param player     - The player which will have animations handled.
     * @param reviseItem - The item to have its displayed lore animated.
     * @param loreString - The lore to set to the item.
     */
    private void setLoreData(final Player player, final ItemStack reviseItem, final List<String> loreString) {
        ItemMeta tempMeta = reviseItem.getItemMeta();
        if (tempMeta != null && reviseItem.getType() != Material.AIR) {
            tempMeta = tempMeta.clone();
            final List<String> loreFormatList = new ArrayList<>();
            for (String s : loreString) {
                String formatLore = ItemHandler.cutDelay(s);
                formatLore = StringUtils.translateLayout(formatLore, player);
                loreFormatList.add(formatLore);
            }
            tempMeta.setLore(loreFormatList);
            reviseItem.setItemMeta(tempMeta);
        }
    }

    /**
     * Sets the specified bukkit material to the specified item.
     *
     * @param reviseItem - The item to have its material animated.
     * @param material   - The material to set to the item.
     */
    private void setMaterialData(final ItemStack reviseItem, final String material) {
        SchedulerUtils.runAsync(() -> {
            Material mat;
            String materialString = ItemHandler.cutDelay(material);
            if (materialString.contains(":") && reviseItem != null && reviseItem.getType() != Material.AIR) {
                final String[] parts = materialString.split(":");
                if (ServerUtils.hasSpecificUpdate("1_13")) {
                    if (!StringUtils.isInt(parts[0])) {
                        parts[0] = "LEGACY_" + parts[0];
                    }
                    if (!StringUtils.isInt(parts[0])) {
                        mat = LegacyAPI.getMaterial(Material.getMaterial(parts[0].toUpperCase()), (byte) Integer.parseInt(parts[1]));
                    } else {
                        mat = LegacyAPI.getMaterial(Integer.parseInt(parts[0]), (byte) Integer.parseInt(parts[1]));
                    }
                    if (mat != Material.AIR) {
                        final Material type = mat;
                        SchedulerUtils.run(() -> reviseItem.setType(type));
                    }
                } else {
                    mat = ItemHandler.getMaterial(parts[0], null);
                    if (mat != Material.AIR) {
                        final Material type = mat;
                        SchedulerUtils.run(() -> reviseItem.setType(type));
                    }
                    LegacyAPI.setDurability(reviseItem, (byte) Integer.parseInt(parts[1]));
                }
            } else {
                mat = ItemHandler.getMaterial(materialString, null);
                if (mat != Material.AIR) {
                    final Material type = mat;
                    SchedulerUtils.run(() -> {
                        if (reviseItem != null) {
                            reviseItem.setType(type);
                        }
                    });
                }
            }
        });
    }

    /**
     * Sets the specified book pages to the specified item.
     *
     * @param player      - The player which will have animations handled.
     * @param reviseItem  - The item to have its book pages animated.
     * @param pagesString - The book pages to set to the item.
     */
    private void setPagesData(final Player player, final ItemStack reviseItem, final List<String> pagesString) {
        if (reviseItem != null && reviseItem.getType() != Material.AIR) {
            reviseItem.setItemMeta(this.itemMap.setJSONBookPages(player, reviseItem, pagesString).getItemMeta());
        }
    }

    /**
     * Sets the specified skull owner and texture to the specified item.
     *
     * @param player        - The player which will have animations handled.
     * @param reviseItem    - The item to have its skull owner and skull texture animated.
     * @param ownerString   - The skull owner to set to the item.
     * @param textureString - The skull texture to set to the item.
     */
    private void setSkull(final Player player, ItemStack reviseItem, final String ownerString, final String textureString) {
        ItemMeta tempMeta = reviseItem.getItemMeta();
        if (tempMeta != null && reviseItem.getType() != Material.AIR) {
            tempMeta = tempMeta.clone();
            if (ownerString != null) {
                ItemHandler.setSkullOwner(tempMeta, player, StringUtils.translateLayout(ItemHandler.cutDelay(ownerString), player));
            } else if (textureString != null && !textureString.contains("hdb-") && !this.itemMap.isHeadDatabase()) {
                ItemHandler.setSkullTexture(tempMeta, ItemHandler.cutDelay(StringUtils.toTextureUUID(player, this.itemMap.getConfigName(), textureString)));
            }
            reviseItem.setItemMeta(tempMeta);
        }
    }

    /**
     * Checks if animations can continue.
     *
     * @param player - The player to check.
     * @param flag - The flag to check.
     * @return true if animations can continue, false otherwise.
     */
    private boolean canAnimate(final Player player, final AtomicBoolean flag) {
        return flag.get() && player != null && player.isOnline() && !player.isDead();
    }

    /**
     * Gets the unique key for a player-item animation.
     *
     * @param player - The player.
     * @return The unique key.
     */
    private String getPlayerAnimationKey(final Player player) {
        return PlayerHandler.getPlayerID(player) + ":" + this.itemMap.getConfigName();
    }

    /**
     * Represents a single frame in an animation sequence.
     */
    private static class AnimationFrame {
        final Object data;
        final long delay;
        final int position;

        AnimationFrame(Object data, long delay) {
            this(data, delay, 0);
        }

        AnimationFrame(Object data, long delay, int position) {
            this.data = data;
            this.delay = delay;
            this.position = position;
        }
    }

    /**
     * Enumeration of animation types.
     */
    private enum AnimationType {
        NAME, LORE, MATERIAL, PAGES, OWNER, TEXTURE
    }

    /**
     * Sets the `/itemjoin menu` as being open for a player.
     *
     * @param bool  - Specifies if the menu is open or closed.
     * @param stage - The stage of the menu as being 0) click to modify or 1) a swap-item.
     */
    public void setMenu(final boolean bool, final int stage) {
        this.menu = bool;
        if (bool && this.dynamicLores != null) {
            this.menuLores = new ArrayList<>();
            for (List<String> lores : this.dynamicLores) {
                final List<String> tempLores = new ArrayList<>(lores);
                tempLores.add("&7");
                tempLores.add("&6---------------------------");
                if (stage == 1) {
                    tempLores.add("&7*Click to set as a swap-item.");
                } else {
                    tempLores.add("&7*Click to modify this custom item.");
                }
                tempLores.add("&9&lNode: &a" + this.itemMap.getConfigName());
                tempLores.add("&7");
                this.menuLores.add(tempLores);
            }
        }
    }
}