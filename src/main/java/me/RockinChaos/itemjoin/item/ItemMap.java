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
import me.RockinChaos.core.handlers.PermissionsHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.core.utils.ReflectionUtils;
import me.RockinChaos.core.utils.ReflectionUtils.MinecraftMethod;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.ChatToggleExecutor;
import me.RockinChaos.itemjoin.ChatToggleTab;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.PluginData;
import me.RockinChaos.itemjoin.item.ItemCommand.Action;
import me.RockinChaos.itemjoin.item.ItemCommand.CommandSequence;
import me.RockinChaos.itemjoin.utils.api.EffectAPI;
import me.RockinChaos.itemjoin.utils.menus.Menu;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public class ItemMap implements Cloneable {

    private final Map<String, Long> playersOnInteractCooldown = new HashMap<>();
    private final HashMap<String, Long> storedSpammedPlayers = new HashMap<>();
    private final List<Player> warmPending = new ArrayList<>();
    private final Map<String, Long> playersOnCooldown = new HashMap<>();
    private final HashMap<String, Long> playersOnCooldownTick = new HashMap<>();
    private final List<PluginCommand> togglePlugins = new ArrayList<>();
    private String configName;
    private ConfigurationSection nodeLocation;
    private Integer probability = -1;
    private ItemStack tempItem = null;
    private ItemMeta tempMeta = null;
    private Material material = Material.AIR;
    private Short dataValue = 0;
    private String customName = null;
    private List<String> customLore = new ArrayList<>();
    private List<String> AllSlots = new ArrayList<>();
    private Integer InvSlot = 0;
    private String CustomSlot = null;
    private boolean craftingItem = false;
    private boolean giveNext = false;
    private boolean moveNext = false;
    private boolean dropFull = false;
    private String count = "1";
    private Map<String, Double> attributes = new HashMap<>();
    private Short durability = null;
    private Integer data = null;
    private String modelData = null;
    private String author;
    private String title;
    private Object generation;
    private List<String> bookPages = new ArrayList<>();
    private List<List<String>> listPages = new ArrayList<>();
    private short mapId = -1;
    private MapView mapView = null;
    private String customMapImage = null;
    private FireworkEffect firework = null;
    private Type fireworkType = null;
    private boolean fireworkFlicker = false;
    private boolean fireworkTrail = false;
    private Integer power = 0;
    private List<DyeColor> fireworkColor = new ArrayList<>();
    private DyeColor chargeColor = null;
    private String skullOwner = null;
    private String skullTexture = null;
    private boolean headDatabase = false;
    private List<PotionEffect> effect = new ArrayList<>();
    private List<Pattern> bannerPatterns = new ArrayList<>();
    private Map<String, String> trimPattern = new HashMap<>();
    private Map<Character, ItemRecipe> ingredients = new HashMap<>();
    private List<List<Character>> recipe = new ArrayList<>();
    private String leatherColor;
    private String leatherHex;
    private String teleportEffect;
    private String teleportSound;
    private Double teleportSoundVolume = 1.0;
    private Double teleportSoundPitch = 1.0;
    private Integer interactCooldown = 0;
    private boolean customConsumable = false;
    private Map<String, Integer> enchants = new HashMap<>();
    //  ============================================== //
    private Map<Object, Object> nbtProperty = new HashMap<>();
    private List<Object> nbtProperties = new ArrayList<>();
    //  ============================================== //
//         Drop Chances for each item.          //
//  ============================================== //
    private Map<EntityType, Double> mobsDrop = new HashMap<>();
    //  ============================================== //
    private Map<Material, Double> blocksDrop = new HashMap<>();
    //  ============================================== //
//         NBT Information for each item.          //
//  ============================================== //
    private String newNBTData;
    private Object newNBTTag;
    private String legacySecret;
    //  ============================================== //
//     ItemAnimation Information for each item.    //
//  ============================================== //
    private List<String> dynamicNames = new ArrayList<>();
    private List<List<String>> dynamicLores = new ArrayList<>();
    private List<String> dynamicMaterials = new ArrayList<>();
    //  ====================================================================================================== //
    private List<String> dynamicOwners = new ArrayList<>();
    private List<String> dynamicTextures = new ArrayList<>();
    private boolean materialAnimated = false;
    private Map<Player, ItemAnimation> localeAnimations = new HashMap<>();
    //  ============================================== //
//      ItemCommand Information for each item.     //
//  ============================================== //
    private ItemCommand[] commands = new ItemCommand[0];
    private Integer cooldownSeconds = 0;
    private Integer commandsReceive = 0;
    private String cooldownMessage;
    private Sound commandSound;
    private Double commandSoundVolume = 1.0;
    private Double commandSoundPitch = 1.0;
    private String commandParticle;
    private String itemCost;
    private Integer cost = 0;
    private Integer warmDelay = 0;
    private boolean subjectRemoval = false;
    private CommandSequence sequence;
    private List<String> disposableConditions = new ArrayList<>();
    private List<String> triggerConditions = new ArrayList<>();
    private Map<String, List<String>> commandConditions = new HashMap<>();
    private String disposableMessage = null;
    private String triggerMessage = null;
    //  ============================================================================================= //
    private Map<String, String> commandMessages = new HashMap<>();
    //  ============================================== //
//            Itemflags for each item.             //
//  ============================================== //
    private String itemflags;
    private boolean vanillaItem = false;
    private boolean vanillaStatus = false;
    private boolean vanillaControl = false;
    private boolean unbreakable = false;
    private boolean countLock = false;
    private boolean teleportArrow = false;
    private boolean cancelEvents = false;
    private boolean itemStore = false;
    private boolean itemModify = false;
    private boolean noCrafting = false;
    private boolean noRepairing = false;
    private boolean animate = false;
    private boolean dynamic = false;
    private boolean dynamicCount = false;
    private boolean glowing = false;
    private boolean overwritable = false;
    private boolean blockPlacement = false;
    private boolean hideAttributes = false;
    private boolean hideEnchantments = false;
    private boolean hideFlags = false;
    private boolean hideDurability = false;
    private boolean blockEquip = false;
    private boolean blockMovement = false;
    private boolean closeInventory = false;
    private boolean selfDroppable = false;
    private boolean deathDroppable = false;
    private boolean deathKeepable = false;
    private boolean disposable = false;
    private boolean itemChangeable = false;
    private boolean alwaysGive = false;
    private boolean autoRemove = false;
    private boolean stackable = false;
    private boolean notHat = false;
    private boolean selectable = false;
    private boolean splittable = false;
    private boolean CreativeBypass = false;
    private boolean AllowOpBypass = false;
    private boolean onlyFirstJoin = false;
    private boolean onlyFirstLife = false;
    private boolean onlyFirstWorld = false;
    //  ============================================== //
    private boolean ipLimited = false;
    //  ============================================== //
    //             Triggers for each item.             //
    //  ============================================== //
    private boolean giveOnDisabled = false;
    private boolean giveOnJoin = false;
    private boolean giveOnTeleport = false;
    private boolean giveOnRespawn = false;
    private boolean giveOnRespawnPoint = false;
    private boolean giveOnWorldSwitch = false;
    private boolean giveOnRegionEnter = false;
    private boolean giveOnRegionLeave = false;
    private boolean giveOnRegionAccess = false;
    private boolean giveOnRegionEgress = false;
    private boolean useOnLimitSwitch = false;
    private String triggers = null;
    //  ============================================== //
    private String limitModes = null;
    private String toggleNode = null;
    private String toggleMessage = null;
    private List<String> toggleCommands = new ArrayList<>();
    private String permissionNode = null;
    private boolean permissionNeeded = false;
    private boolean opPermissionNeeded = false;

    private List<String> contents = new ArrayList<>();

    private List<String> enabledRegions = new ArrayList<>();
    private List<String> enabledWorlds = new ArrayList<>();
    private List<String> disabledWorlds = new ArrayList<>();
// ======================================================================================== //

    /**
     * Creates a new ItemMap instance.
     * Typically used in the creation of items.
     *
     * @param internalName - The node name of the ItemMap.
     * @param slot         - The slot of the ItemMap.
     */
    public ItemMap(final String internalName, final String slot) {
        this.nodeLocation = Objects.requireNonNull(ItemJoin.getCore().getConfig("items.yml").getConfigurationSection("items")).getConfigurationSection(internalName);
        this.configName = internalName;
        this.setSlot(slot);
        if (ItemHandler.isCraftingSlot(slot)) {
            this.craftingItem = true;
        }

        if (this.nodeLocation != null) {
            this.setMultipleSlots();
            this.setCount(this.nodeLocation.getString(".count"));
            this.setCommandCost();
            this.setCommandReceive();
            this.setCommandWarmDelay();
            this.setCommandSound();
            this.setCommandParticle();
            this.setCommandCooldown();
            this.setCommandSequence();
            this.setCommands(ItemCommand.arrayFromString(this, this.sequence == CommandSequence.RANDOM_LIST));
            this.setToggleCommands(this.nodeLocation.getString(".toggle"));
            this.setConditions();
            this.setInteractCooldown();
            this.setPlayersOnCooldown();
            this.setTeleportArrow();
            this.setLimitModes();
            this.setTriggers();
            this.setItemflags();
            this.setWorlds();
            this.setRegions();
            this.setTogglePerm(this.nodeLocation.getString(".toggle-permission"));
            this.setToggleMessage(this.nodeLocation.getString(".toggle-message"));
            this.setPerm(this.nodeLocation.getString(".permission-node"));
            this.setPermissionNeeded(ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items"));
            this.setOPPermissionNeeded(ItemJoin.getCore().getConfig("config.yml").getBoolean("Permissions.Obtain-Items-OP"));
        }
    }

    /**
     * Creates a new ItemMap instance.
     * Called when copying an ItemMap, sadly this has to exist.
     */
    public ItemMap() {
    }
//  ========================================================================================================= //

//  ============================================== //
//   Setter functions for first ItemMap creation.  //
//  ============================================== //

    /**
     * Sets the ItemMaps Multiple Slots.
     */
    private void setMultipleSlots() {
        final String itemSlot = this.nodeLocation.getString(".slot");
        if (itemSlot != null && itemSlot.contains(",")) {
            String[] slots = itemSlot.replace(" ", "").split(",");
            for (String slot : slots) {
                if (slot.startsWith("C[") || slot.startsWith("C(")) {
                    slot = slot.replace("C", "CRAFTING");
                }
                if (slot.startsWith("CRAFTING")) {
                    slot = slot.replace("(", "[").replace(")", "]");
                }
                this.AllSlots.add(slot);
            }
        }
    }

    /**
     * Sets the ItemMaps Commands Cost.
     */
    private void setCommandCost() {
        final String commandItem = this.nodeLocation.getString("commands-item");
        final String commandCost = this.nodeLocation.getString("commands-cost");
        if (commandItem != null && !commandItem.isEmpty()) {
            this.itemCost = commandItem;
        }
        if (StringUtils.isInt(commandCost)) {
            this.cost = this.nodeLocation.getInt("commands-cost");
        }
    }

    /**
     * Sets the ItemMaps Commands Receive.
     */
    private void setCommandReceive() {
        if (this.nodeLocation.getString("commands-receive") != null && StringUtils.isInt(this.nodeLocation.getString("commands-receive"))) {
            this.commandsReceive = this.nodeLocation.getInt("commands-receive");
        }
    }

    /**
     * Sets the ItemMaps Commands Warmup Delay.
     */
    private void setCommandWarmDelay() {
        if (this.nodeLocation.getString("commands-warmup") != null && StringUtils.isInt(this.nodeLocation.getString("commands-warmup"))) {
            this.warmDelay = this.nodeLocation.getInt("commands-warmup");
        }
    }

    /**
     * Sets the ItemMaps Commands Sound.
     */
    private void setCommandSound() {
        try {
            final String sound = this.nodeLocation.getString(".commands-sound");
            if (sound != null) {
                if (sound.contains(":")) {
                    final String[] soundParts = sound.split(":");
                    this.commandSound = Sound.valueOf(soundParts[0]);
                    try {
                        this.commandSoundVolume = Double.valueOf(soundParts[1]);
                        this.commandSoundPitch = Double.valueOf(soundParts[2]);
                    } catch (Exception e) {
                        ServerUtils.logSevere("{ItemMap} The formatting for the item " + this.configName + " commands-sound is incorrect and will not be set!");
                        ServerUtils.sendDebugTrace(e);
                    }
                } else {
                    this.commandSound = Sound.valueOf(this.nodeLocation.getString(".commands-sound"));
                }
            }
        } catch (Exception e) {
            ServerUtils.logSevere("{ItemMap} Your server is running MC " + ReflectionUtils.getServerVersion() + " and this version of Minecraft does not have the defined command-sound " + this.nodeLocation.getString(".commands-sound") + ".");
            ServerUtils.sendDebugTrace(e);
        }
    }

    /**
     * Sets the ItemMaps Commands Particle.
     */
    private void setCommandParticle() {
        if (this.nodeLocation.getString(".commands-particle") != null) {
            this.commandParticle = this.nodeLocation.getString(".commands-particle");
        }
    }

    /**
     * Sets the ItemMaps Commands Cooldown.
     */
    private void setCommandCooldown() {
        boolean useCooldown = this.nodeLocation.getString("commands-cooldown") != null;
        if (useCooldown) {
            this.cooldownSeconds = this.nodeLocation.getInt("commands-cooldown");
        }
        this.cooldownMessage = this.nodeLocation.getString("cooldown-message");
    }

    /**
     * Sets the ItemMaps Commands Sequence.
     */
    private void setCommandSequence() {
        if (this.nodeLocation.getString("commands-sequence") != null) {
            if (StringUtils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "SEQUENTIAL")) {
                this.sequence = CommandSequence.SEQUENTIAL;
            } else if (StringUtils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "RANDOM_SINGLE")) {
                this.sequence = CommandSequence.RANDOM_SINGLE;
            } else if (StringUtils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "RANDOM_LIST")) {
                this.sequence = CommandSequence.RANDOM_LIST;
            } else if (StringUtils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "RANDOM")) {
                this.sequence = CommandSequence.RANDOM;
            } else if (StringUtils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "REMAIN")) {
                this.sequence = CommandSequence.REMAIN;
            }
        }
    }

    /**
     * Sets the ItemMaps Interact Cooldown.
     */
    private void setInteractCooldown() {
        if (this.nodeLocation.getString(".use-cooldown") != null) {
            this.interactCooldown = this.nodeLocation.getInt(".use-cooldown");
        }
    }

    /**
     * Sets the ItemMaps Teleport Arrow.
     */
    private void setTeleportArrow() {
        this.teleportEffect = this.nodeLocation.getString(".teleport-effect");
        final String sound = this.nodeLocation.getString(".teleport-sound");
        if (sound != null && sound.contains(":")) {
            final String[] soundParts = sound.split(":");
            this.teleportSound = soundParts[0];
            try {
                this.teleportSoundVolume = Double.valueOf(soundParts[1]);
                this.teleportSoundPitch = Double.valueOf(soundParts[2]);
            } catch (Exception e) {
                ServerUtils.logSevere("{ItemMap} The formatting for the item " + this.configName + " teleport-sound is incorrect and will not be set!");
                ServerUtils.sendDebugTrace(e);
            }
        } else {
            this.teleportSound = sound;
        }
    }

    /**
     * Sets the ItemMaps ItemFlags.
     */
    private void setItemflags() {
        if (this.nodeLocation.getString(".itemflags") != null) {
            this.itemflags = this.nodeLocation.getString(".itemflags");
            this.vanillaItem = StringUtils.splitIgnoreCase(this.itemflags, "vanilla", ",");
            this.vanillaStatus = StringUtils.splitIgnoreCase(this.itemflags, "vanilla-status", ",");
            this.vanillaControl = StringUtils.splitIgnoreCase(this.itemflags, "vanilla-control", ",");
            this.disposable = StringUtils.splitIgnoreCase(this.itemflags, "disposable", ",");
            this.blockPlacement = StringUtils.splitIgnoreCase(this.itemflags, "placement", ",");
            this.blockMovement = StringUtils.splitIgnoreCase(this.itemflags, "inventory-modify", ",") || StringUtils.splitIgnoreCase(this.itemflags, "inventory-close", ",");
            this.blockEquip = StringUtils.splitIgnoreCase(this.itemflags, "cancel-equip", ",");
            this.closeInventory = StringUtils.splitIgnoreCase(this.itemflags, "inventory-close", ",");
            this.itemChangeable = StringUtils.splitIgnoreCase(this.itemflags, "item-changeable", ",");
            this.alwaysGive = StringUtils.splitIgnoreCase(this.itemflags, "always-give", ",");
            this.autoRemove = StringUtils.splitIgnoreCase(this.itemflags, "auto-remove", ",");
            this.stackable = StringUtils.splitIgnoreCase(this.itemflags, "stackable", ",");
            this.notHat = StringUtils.splitIgnoreCase(this.itemflags, "not-hat", ",");
            this.selectable = StringUtils.splitIgnoreCase(this.itemflags, "selectable", ",");
            this.splittable = StringUtils.splitIgnoreCase(this.itemflags, "splittable", ",");
            this.animate = StringUtils.splitIgnoreCase(this.itemflags, "animate", ",");
            this.dynamic = StringUtils.splitIgnoreCase(this.itemflags, "dynamic", ",");
            this.dynamicCount = StringUtils.splitIgnoreCase(this.itemflags, "dynamic-count", ",");
            this.glowing = StringUtils.splitIgnoreCase(this.itemflags, "glowing", ",");
            this.giveNext = StringUtils.splitIgnoreCase(this.itemflags, "give-next", ",");
            this.moveNext = StringUtils.splitIgnoreCase(this.itemflags, "move-next", ",");
            this.dropFull = StringUtils.splitIgnoreCase(this.itemflags, "drop-full", ",");
            this.itemStore = StringUtils.splitIgnoreCase(this.itemflags, "item-store", ",");
            this.itemModify = StringUtils.splitIgnoreCase(this.itemflags, "item-modifiable", ",");
            this.noCrafting = StringUtils.splitIgnoreCase(this.itemflags, "item-craftable", ",");
            this.noRepairing = StringUtils.splitIgnoreCase(this.itemflags, "item-repairable", ",");
            this.cancelEvents = StringUtils.splitIgnoreCase(this.itemflags, "cancel-events", ",");
            this.countLock = StringUtils.splitIgnoreCase(this.itemflags, "count-lock", ",");
            this.teleportArrow = StringUtils.splitIgnoreCase(this.itemflags, "teleport", ",");
            this.overwritable = StringUtils.splitIgnoreCase(this.itemflags, "overwrite", ",");
            this.ipLimited = StringUtils.splitIgnoreCase(this.itemflags, "ip-limit", ",");
            this.deathKeepable = StringUtils.splitIgnoreCase(this.itemflags, "death-keep", ",");
            this.deathDroppable = StringUtils.splitIgnoreCase(this.itemflags, "death-drops", ",");
            this.selfDroppable = StringUtils.splitIgnoreCase(this.itemflags, "self-drops", ",");

            // Shared with Triggers //
            this.setOnlyFirstJoin((StringUtils.splitIgnoreCase(this.itemflags, "first-join", ",") || this.onlyFirstJoin));
            this.setOnlyFirstLife((StringUtils.splitIgnoreCase(this.itemflags, "first-life", ",") || this.onlyFirstLife));
            this.onlyFirstWorld = (StringUtils.splitIgnoreCase(this.itemflags, "first-world", ",") || this.onlyFirstWorld);
            this.AllowOpBypass = (StringUtils.splitIgnoreCase(this.itemflags, "AllowOpBypass", ",") || this.AllowOpBypass);
            this.CreativeBypass = (StringUtils.splitIgnoreCase(this.itemflags, "CreativeBypass", ",") || this.CreativeBypass);
        }
    }

    /**
     * Sets the ItemMaps Limit Modes.
     */
    private void setLimitModes() {
        this.limitModes = this.nodeLocation.getString(".limit-modes");
    }

    /**
     * Sets the ItemMaps Triggers.
     */
    private void setTriggers() {
        final String defaultTriggers = ItemJoin.getCore().getConfig("config.yml").getString("Settings.Default-Triggers");
        this.triggers = (this.nodeLocation.getString("triggers") != null ? this.nodeLocation.getString("triggers") : (defaultTriggers != null && !defaultTriggers.isEmpty() ? defaultTriggers : "JOIN"));
        this.giveOnDisabled = StringUtils.splitIgnoreCase(this.triggers, "DISABLED", ",");
        this.giveOnJoin = StringUtils.splitIgnoreCase(this.triggers, "JOIN", ",");
        this.giveOnTeleport = StringUtils.splitIgnoreCase(this.triggers, "TELEPORT", ",");
        this.giveOnRespawn = StringUtils.splitIgnoreCase(this.triggers, "RESPAWN", ",");
        this.giveOnRespawnPoint = StringUtils.splitIgnoreCase(this.triggers, "RESPAWN-POINT", ",");
        this.giveOnWorldSwitch = StringUtils.splitIgnoreCase(this.triggers, "WORLD-CHANGE", ",") || StringUtils.splitIgnoreCase(this.triggers, "WORLD-SWITCH", ",");
        this.giveOnRegionEnter = StringUtils.splitIgnoreCase(this.triggers, "REGION-ENTER", ",");
        this.giveOnRegionLeave = StringUtils.splitIgnoreCase(this.triggers, "REGION-REMOVE", ",") || StringUtils.splitIgnoreCase(this.triggers, "REGION-EXIT", ",") || StringUtils.splitIgnoreCase(this.triggers, "REGION-LEAVE", ",");
        this.giveOnRegionAccess = StringUtils.splitIgnoreCase(this.triggers, "REGION-ACCESS", ",");
        this.giveOnRegionEgress = StringUtils.splitIgnoreCase(this.triggers, "REGION-EGRESS", ",");
        if (this.giveOnRegionAccess || this.giveOnRegionEgress) {
            this.giveOnRegionEnter = false;
            this.giveOnRegionLeave = false;
            this.giveOnWorldSwitch = false;
        }
        if (this.giveOnRegionEnter || this.giveOnRegionLeave) {
            this.giveOnWorldSwitch = false;
        }
        this.useOnLimitSwitch = StringUtils.splitIgnoreCase(this.triggers, "GAMEMODE-SWITCH", ",");
        this.setOnlyFirstJoin(StringUtils.splitIgnoreCase(this.triggers, "FIRST-JOIN", ","));
        this.setOnlyFirstLife(StringUtils.splitIgnoreCase(this.triggers, "FIRST-LIFE", ","));
        this.setOnlyFirstWorld(StringUtils.splitIgnoreCase(this.triggers, "FIRST-WORLD", ","));
    }

    /**
     * Sets the ItemMaps Stored Contents.
     */
    public void setContents() {
        if (this.material != null && StringUtils.containsIgnoreCase(this.getMaterial().toString(), "SHULKER") && this.nodeLocation.getString(".contents") != null && !this.nodeLocation.getStringList(".contents").isEmpty()) {
            this.contents = this.nodeLocation.getStringList(".contents");
        } else {
            if (this.material != null && !StringUtils.containsIgnoreCase(this.getMaterial().toString(), "SHULKER") && this.nodeLocation.getString(".contents") != null) {
                this.nodeLocation.getStringList(".contents");
                if (!this.nodeLocation.getStringList(".contents").isEmpty()) {
                    ServerUtils.logWarn("{ItemMap} The item " + this.getConfigName() + " cannot have contents set as it does not support it.");
                }
            }
        }
    }

    /**
     * Sets the ItemMaps Enabled Regions.
     */
    private void setRegions() {
        final String enabledRegions = this.nodeLocation.getString(".enabled-regions");
        if (enabledRegions != null && !enabledRegions.isEmpty()) {
            String[] enabledParts = enabledRegions.replace(" ,  ", ",").replace(" , ", ",").replace(",  ", ",").replace(", ", ",").split(",");
            for (String region : enabledParts) {
                this.enabledRegions.add(region);
                ItemJoin.getCore().getDependencies().getGuard().addLocaleRegion(region);
            }
        } else if (isGiveOnRegionEnter() || isGiveOnRegionLeave()) {
            ItemJoin.getCore().getDependencies().getGuard().addLocaleRegion("UNDEFINED");
            this.enabledRegions.add("UNDEFINED");
        }
    }

    /**
     * Sets the ItemMaps Conditions.
     */
    private void setConditions() {
        final String triggerMessage = this.nodeLocation.getString(".trigger-fail-message");
        final String triggerConditions = this.nodeLocation.getString(".trigger-conditions");
        final List<String> triggerConditionsList = this.nodeLocation.getStringList(".trigger-conditions");
        final String disposableMessage = this.nodeLocation.getString(".disposable-fail-message");
        final String disposableConditions = this.nodeLocation.getString(".disposable-conditions");
        final List<String> disposableConditionsList = this.nodeLocation.getStringList(".disposable-conditions");
        if (triggerMessage != null && !triggerMessage.isEmpty()) {
            this.triggerMessage = triggerMessage;
        }
        if (!triggerConditionsList.isEmpty()) {
            this.triggerConditions = triggerConditionsList;
        } else if (triggerConditions != null && !triggerConditions.isEmpty()) {
            this.triggerConditions.add(triggerConditions);
        }
        if (disposableMessage != null && !disposableMessage.isEmpty()) {
            this.disposableMessage = disposableMessage;
        }
        if (!disposableConditionsList.isEmpty()) {
            this.disposableConditions = disposableConditionsList;
        } else if (disposableConditions != null && !disposableConditions.isEmpty()) {
            this.disposableConditions.add(disposableConditions);
        }
        for (Action action : Action.values()) {
            final String actionMessage = this.nodeLocation.getString(action.config() + "-fail-message");
            final String actionConditions = this.nodeLocation.getString(action.config() + "-conditions");
            final List<String> actionConditionsList = this.nodeLocation.getStringList(action.config() + "-conditions");
            if (actionMessage != null && !actionMessage.isEmpty()) {
                this.commandMessages.put(action.config(), actionMessage);
            }
            if (!actionConditionsList.isEmpty()) {
                this.commandConditions.put(action.config(), actionConditionsList);
            } else if (actionConditions != null && !actionConditions.isEmpty()) {
                List<String> commandCond = new ArrayList<>();
                commandCond.add(actionConditions);
                this.commandConditions.put(action.config(), commandCond);
            }
        }
    }

    /**
     * Sets the ItemMaps Enabled Worlds.
     */
    private void setWorlds() {
        SchedulerUtils.run(() -> {
            final String enabledWorlds = this.nodeLocation.getString(".enabled-worlds");
            if (enabledWorlds != null && !enabledWorlds.isEmpty()) {
                String[] enabledParts = enabledWorlds.replace(" ,  ", ",").replace(" , ", ",").replace(",  ", ",").replace(", ", ",").split(",");
                for (String enabledWorld : enabledParts) {
                    if (enabledWorld.equalsIgnoreCase("ALL") || enabledWorld.equalsIgnoreCase("GLOBAL")) {
                        this.enabledWorlds.add("ALL");
                    }
                    if (enabledWorld.equalsIgnoreCase("DISABLED") || enabledWorld.equalsIgnoreCase("DISABLE")) {
                        this.enabledWorlds.add("DISABLED");
                    } else {
                        for (World world : Bukkit.getServer().getWorlds()) {
                            if (enabledWorld.equalsIgnoreCase(world.getName())) {
                                this.enabledWorlds.add(world.getName());
                            } else if (enabledWorld.contains("*") && world.getName().toUpperCase().startsWith(enabledWorld.split("\\*")[0].toUpperCase())) {
                                this.enabledWorlds.add(enabledWorld);
                            }
                        }
                    }
                }
            } else {
                this.enabledWorlds.add("ALL");
            }
            final String disabledWorlds = this.nodeLocation.getString(".disabled-worlds");
            if (disabledWorlds != null && !disabledWorlds.isEmpty()) {
                String[] disabledParts = disabledWorlds.replace(" ,  ", ",").replace(" , ", ",").replace(",  ", ",").replace(", ", ",").split(",");
                for (String disabledWorld : disabledParts) {
                    if (disabledWorld.equalsIgnoreCase("ALL") || disabledWorld.equalsIgnoreCase("GLOBAL")) {
                        this.disabledWorlds.add("ALL");
                    } else {
                        for (World world : Bukkit.getServer().getWorlds()) {
                            if (disabledWorld.equalsIgnoreCase(world.getName())) {
                                this.disabledWorlds.add(world.getName());
                            } else if (disabledWorld.contains("*") && world.getName().toUpperCase().startsWith(disabledWorld.split("\\*")[0].toUpperCase())) {
                                this.disabledWorlds.add(disabledWorld);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Sets the Players On Cooldown from the SQLite Database.
     */
    private void setPlayersOnCooldown() {
        if (this.cooldownSeconds > 0) {
            List<Object> dataList = ItemJoin.getCore().getSQL().getDataList(new DataObject(Table.ON_COOLDOWN, null, null, this.getConfigName(), String.valueOf(this.getCommandCooldown()), null));
            for (Object dataObject : dataList) {
                if (dataObject != null) {
                    this.playersOnCooldown.put(((DataObject) dataObject).getPlayerId(), Long.parseLong(((DataObject) dataObject).getDuration()));
                    ItemJoin.getCore().getSQL().removeData(new DataObject(Table.ON_COOLDOWN, null, null, this.getConfigName(), String.valueOf(this.getCommandCooldown()), null));
                }
            }
        }
    }

    /**
     * Sets the ItemMaps Temporary ItemStack.
     */
    public void renderItemStack() {
        if (this.dataValue != null) {
            this.tempItem = LegacyAPI.newItemStack(this.material, 1, this.dataValue);
        } else {
            this.tempItem = new ItemStack(this.material, 1);
        }
    }
//  ======================================================================================================================================================================================== //

//  ===================== //
//  ~ Setting Functions ~ //
//  ===================== //

    /**
     * Removes the Player from the AnimationHandler.
     *
     * @param player - The Player to be removed.
     */
    public void removeFromAnimationHandler(final Player player) {
        this.localeAnimations.remove(player);
    }

    /**
     * Sets the stack size.
     *
     * @param count - The stack size to be set.
     */
    public void setCount(final String count) {
        this.count = count;
    }

    /**
     * Adds the Player as Warmup Pending.
     *
     * @param player - The Player to be set as Warmup pending.
     */
    private void addWarmPending(final Player player) {
        if (!this.warmPending.contains(player)) {
            this.warmPending.add(player);
        }
    }

    /**
     * Removes the Player from pending Warmup.
     *
     * @param player - The Player to be removed from Warmup pending.
     */
    private void delWarmPending(final Player player) {
        this.warmPending.remove(player);
    }

    /**
     * Adds the recipe pattern.
     *
     * @param recipe - The recipe pattern to be added.
     */
    public void addRecipe(final List<Character> recipe) {
        this.recipe.add(recipe);
    }

    /**
     * Sets the Permission.
     *
     * @param permission - The Permission to be set.
     */
    public void setPerm(final String permission) {
        this.permissionNode = permission == null || permission.isEmpty() ? null : permission;
    }

    /**
     * Sets the Toggle Permission.
     *
     * @param permission - The Permission to be set.
     */
    public void setTogglePerm(final String permission) {
        this.toggleNode = permission == null || permission.isEmpty() ? null : permission;
    }

    /**
     * Sets the Animate Flag.
     *
     * @param bool - The value to be set.
     */
    public void setAnimate(final boolean bool) {
        this.animate = bool;
    }

    /**
     * Sets the Dynamic Count Flag.
     *
     * @param bool - The value to be set.
     */
    public void setDynamicCount(final boolean bool) {
        this.dynamicCount = bool;
    }

    /**
     * Sets the Inventory Close Flag.
     *
     * @param bool - The value to be set.
     */
    public void setCloseInventory(final boolean bool) {
        this.closeInventory = bool;
    }

    /**
     * Sets the MapView.
     *
     * @param view - The MapView to be set.
     */
    public void setMapView(final MapView view) {
        this.mapView = view;
    }

    /**
     * Sets the NBTData.
     *
     * @param nbt - The NBT Data to be set.
     * @param tag - The Object Tag to be set.
     */
    public void setNewNBTData(final String nbt, final Object tag) {
        this.newNBTData = nbt;
        this.newNBTTag = tag;
    }

    /**
     * Sets the NBT Properties.
     *
     * @param tags - The Object Tags to be set.
     */
    public void setNBTProperties(final Map<Object, Object> tagValues, final List<Object> tags) {
        this.nbtProperty = tagValues;
        this.nbtProperties = tags;
    }

    /**
     * Deletes the Toggle Commands.
     */
    public void delToggleCommands() {
        if (!this.togglePlugins.isEmpty()) {
            ServerUtils.unregisterCommands(this.togglePlugins);
        }
    }

    /**
     * Gets the Custom Display Name.
     *
     * @return The Custom Display Name.
     */
    public String getCustomName() {
        return this.customName;
    }

    /**
     * Sets the Custom Display Name.
     *
     * @param customName - The Display Name to be set.
     */
    public void setCustomName(final String customName) {
        this.customName = customName;
    }

    /**
     * Sets the Custom Display Name.
     *
     * @param player - The Player to be used for placeholders.
     */
    private void setCustomName(final Player player) {
        if (this.customName != null && !this.customName.equalsIgnoreCase(ItemHandler.getMaterialName(this.tempItem))) {
            if (this.legacySecret != null && !ServerUtils.hasSpecificUpdate("1_14")) {
                final String itemData = this.tempMeta.getDisplayName();
                this.tempMeta.setDisplayName(StringUtils.translateLayout(ItemHandler.cutDelay(this.customName), player) + ChatColor.COLOR_CHAR + "r" + itemData);
            } else {
                this.tempMeta.setDisplayName(StringUtils.translateLayout(ItemHandler.cutDelay(this.customName), player));
            }
        }
    }

    /**
     * Gets the Dynamic Display Names.
     *
     * @return The Dynamic Display Names.
     */
    public List<String> getDynamicNames() {
        return this.dynamicNames;
    }

    /**
     * Sets the Dynamic Display Names.
     *
     * @param names - The Dynamic Display Names to be set.
     */
    public void setDynamicNames(final List<String> names) {
        this.dynamicNames = names;
    }

    /**
     * Gets the Custom Display Lore.
     *
     * @return The Custom Display Lore.
     */
    public List<String> getCustomLore() {
        return this.customLore;
    }

    /**
     * Sets the Custom Display Lore.
     *
     * @param customLore - The Display Lore to be set.
     */
    public void setCustomLore(final List<String> customLore) {
        this.customLore = new ArrayList<>();
        this.customLore.addAll(customLore);
    }

    /**
     * Sets the Custom Display Lore.
     *
     * @param player - The Player to be used for placeholders.
     */
    private void setCustomLore(final Player player) {
        if (this.customLore != null && !this.customLore.isEmpty()) {
            List<String> loreList = this.customLore;
            List<String> loreFormatList = new ArrayList<>();
            for (String s : loreList) {
                String formatLore = ItemHandler.cutDelay(s);
                formatLore = StringUtils.translateLayout(formatLore, player);
                loreFormatList.add(formatLore);
            }
            this.tempMeta.setLore(loreFormatList);
        }
    }

    /**
     * Gets the Dynamic Display Lores.
     *
     * @return The Dynamic Display Lores.
     */
    public List<List<String>> getDynamicLores() {
        return this.dynamicLores;
    }

    /**
     * Sets the Dynamic Display Lore.
     *
     * @param lores - The Dynamic Display Lore to be set.
     */
    public void setDynamicLores(final List<List<String>> lores) {
        this.dynamicLores = lores;
    }

    /**
     * Gets the Dynamic Materials.
     *
     * @return The Dynamic Materials.
     */
    public List<String> getDynamicMaterials() {
        return this.dynamicMaterials;
    }

    /**
     * Sets the Dynamic Materials.
     *
     * @param mats - The Dynamic Materials to be set.
     */
    public void setDynamicMaterials(final List<String> mats) {
        this.dynamicMaterials = mats;
        this.materialAnimated = true;
    }

    /**
     * Gets the Dynamic Skull Owners.
     *
     * @return The Dynamic Skull Owners.
     */
    public List<String> getDynamicOwners() {
        return this.dynamicOwners;
    }

    /**
     * Sets the Dynamic Skull Owners.
     *
     * @param owners - The Dynamic Skull Owners to be set.
     */
    public void setDynamicOwners(final List<String> owners) {
        this.dynamicOwners = owners;
    }

    /**
     * Gets the Dynamic Skull Textures.
     *
     * @return The Dynamic Skull Textures.
     */
    public List<String> getDynamicTextures() {
        return this.dynamicTextures;
    }

    /**
     * Sets the Dynamic Skull Textures.
     *
     * @param textures - The Dynamic Skull Textures to be set.
     */
    public void setDynamicTextures(final List<String> textures) {
        this.dynamicTextures = textures;
    }

    /**
     * Gets the AnimationHandlers.
     *
     * @return The AnimationsHandlers.
     */
    public Map<Player, ItemAnimation> getAnimationHandler() {
        if (this.localeAnimations == null) {
            this.localeAnimations = new HashMap<>();
        }
        return this.localeAnimations;
    }

    /**
     * Gets the Slot.
     *
     * @return The Slot.
     */
    public String getSlot() {
        if (this.CustomSlot != null) {
            return this.CustomSlot;
        } else if (this.InvSlot != null) {
            return this.InvSlot.toString();
        }
        return null;
    }

    /**
     * Sets the Slot.
     *
     * @param slot - The Slot to be set.
     */
    public void setSlot(final String slot) {
        if (ItemHandler.isCustomSlot(slot)) {
            this.CustomSlot = slot;
            this.InvSlot = null;
        } else if (StringUtils.isInt(slot)) {
            this.InvSlot = Integer.parseInt(slot);
            this.CustomSlot = null;
        }
    }

    /**
     * Gets the Slot.
     *
     * @return The Slot.
     */
    public String getUISlot() {
        if (this.CustomSlot != null && !this.CustomSlot.contains("%")) {
            return this.CustomSlot;
        } else if (this.CustomSlot != null) {
            return (0 + "");
        } else if (this.InvSlot != null) {
            return this.InvSlot.toString();
        }
        return null;
    }

    /**
     * Gets the Multiple Slots.
     *
     * @return The Multiple Slots.
     */
    public List<String> getMultipleSlots() {
        return this.AllSlots;
    }

    /**
     * Sets the Multiple Slots.
     *
     * @param slots - The Multiple Slots to be set.
     */
    public void setMultipleSlots(final List<String> slots) {
        this.AllSlots = slots;
    }

    /**
     * Gets the Disabled Worlds.
     *
     * @return The Disabled Worlds.
     */
    public List<String> getDisabledWorlds() {
        return this.disabledWorlds;
    }

    /**
     * Sets the Disabled Worlds.
     *
     * @param worlds - The Disabled Worlds to be set.
     */
    public void setDisabledWorlds(final List<String> worlds) {
        this.disabledWorlds = worlds;
    }

    /**
     * Gets the Enabled Worlds.
     *
     * @return The Enabled Worlds.
     */
    public List<String> getEnabledWorlds() {
        return this.enabledWorlds;
    }

    /**
     * Sets the Enabled Worlds.
     *
     * @param worlds - The Enabled Worlds to be set.
     */
    public void setEnabledWorlds(final List<String> worlds) {
        this.enabledWorlds = worlds;
    }

    /**
     * Gets the Enabled Regions.
     *
     * @return The Enabled Regions.
     */
    public List<String> getEnabledRegions() {
        return this.enabledRegions;
    }

    /**
     * Sets the Enabled Regions.
     *
     * @param regions - The Enabled Regions to be set.
     */
    public void setEnabledRegions(final List<String> regions) {
        this.enabledRegions = regions;
    }

    /**
     * Gets the Stored Contents.
     *
     * @return The Stored Contents.
     */
    public List<String> getContents() {
        return this.contents;
    }

    /**
     * Sets the Stored Contents.
     *
     * @param contents - The Stored Contents to be set.
     */
    public void setContents(final List<String> contents) {
        this.contents = contents;
    }

    /**
     * Sets the item contents for the storage box.
     */
    private void setContents(final Player player) {
        if (this.contents != null && !this.contents.isEmpty() && ServerUtils.hasSpecificUpdate("1_11")) {
            ShulkerBox box = (ShulkerBox) ((BlockStateMeta) this.tempMeta).getBlockState();
            box.getInventory().clear();
            for (String node : this.contents) {
                boolean isNull = true;
                for (ItemMap item : ItemUtilities.getUtilities().getItems()) {
                    if (item != null && item.getConfigName().equalsIgnoreCase(node)) {
                        isNull = false;
                        if (StringUtils.isInt(item.getSlot()) && Integer.parseInt(item.getSlot()) <= 26) {
                            box.getInventory().setItem(Integer.parseInt(item.getSlot()), item.getItemStack(player));
                        } else if (item.getSlot().equalsIgnoreCase("ARBITRARY")) {
                            box.getInventory().addItem(item.getItemStack(player));
                        } else if (StringUtils.isInt(item.getSlot()) && Integer.parseInt(item.getSlot()) > 26) {
                            ServerUtils.logWarn("{ItemMap} The item " + node + " cannot have the slot " + item.getSlot() + " as the slot cannot be higher than 26 to be set as contents for the item " + this.getConfigName() + ", the item will not be set.");
                        } else if (!StringUtils.isInt(item.getSlot())) {
                            ServerUtils.logWarn("{ItemMap} The item " + node + " cannot have the slot " + item.getSlot() + " as the item " + this.getConfigName() + " does not support it, the item will not be set.");
                        }
                    }
                }
                if (isNull) {
                    ServerUtils.logWarn("{ItemMap} The item " + node + " does not exist and will not be set as contents for " + this.getConfigName() + ".");
                }
            }
            ((BlockStateMeta) this.tempMeta).setBlockState(box);
            box.update();
        }
    }

    /**
     * Gets the Enchantments.
     *
     * @return The Enchantments.
     */
    public Map<String, Integer> getEnchantments() {
        return this.enchants;
    }

    /**
     * Sets the Enchantments.
     *
     * @param enchantments - The Enchantments to be set.
     */
    public void setEnchantments(final Map<String, Integer> enchantments) {
        this.enchants = enchantments;
    }

    /**
     * Sets the ItemStack Enchantments.
     *
     * @param player - The Player to have their TokenEnchant instance fetched.
     */
    private void setEnchantments(final Player player) {
        final Map<String, Integer> enchantStats = (player != null ? ItemUtilities.getUtilities().getStatistics(player).getEnchantments(this) : null);
        if (enchantStats != null && !enchantStats.isEmpty()) {
            for (final Entry<String, Integer> enchantments : enchantStats.entrySet()) {
                if (enchantments.getKey() == null && ItemJoin.getCore().getDependencies().tokenEnchantEnabled() && TokenEnchantAPI.getInstance().getEnchantment(enchantments.getKey()) != null) {
                    TokenEnchantAPI.getInstance().enchant(player, this.tempItem, enchantments.getKey(), enchantments.getValue(), true, 0, true);
                } else {
                    this.tempItem.addUnsafeEnchantment(Objects.requireNonNull(ItemHandler.getEnchantByName(enchantments.getKey())), enchantments.getValue());
                }
            }
        }
    }

    /**
     * Gets the stack size.
     *
     * @return The stack size.
     */
    public Integer getCount(final Player player) {
        int countParse = 1;
        if (this.count != null && !this.count.isEmpty()) {
            try {
                final String translateCount = StringUtils.translateLayout(this.count, player).replaceAll("[^\\d.]", "").replace("-", "").replace(".", "").replace(" ", "");
                countParse = Integer.parseInt(translateCount);
                if (countParse > 127) {
                    while (countParse > 127) {
                        countParse -= 127;
                    }
                }
                {
                    if (countParse == 0) {
                        countParse = 1;
                    }
                }
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
                ServerUtils.logSevere("{ItemMap} The count set for the item " + this.configName + " is set to " + this.count + " but this is not a valid integer!");
                ServerUtils.logSevere("{ItemMap} Check that the set value is an integer or placeholder that parses to an integer.");
                ServerUtils.logSevere("{ItemMap} The count for the item " + this.configName + " will now default to 1.");
            }
        }
        return countParse;
    }

    /**
     * Gets the attribute list.
     *
     * @return The attribute list.
     */
    public Map<String, Double> getAttributes() {
        return this.attributes;
    }

    /**
     * Sets the ItemStack attributes.
     *
     * @param attributeList - The list of attributes to be set.
     */
    public void setAttributes(final Map<String, Double> attributeList) {
        if (attributeList != null && !attributeList.isEmpty()) {
            this.attributes = attributeList;
        }
    }

    /**
     * Gets the ItemFlags.
     *
     * @return The ItemFlags.
     */
    public String getItemFlags() {
        return this.itemflags;
    }

    /**
     * Sets the ItemFlags.
     *
     * @param itemflags - The ItemFlags to be set.
     */
    public void setItemFlags(final String itemflags) {
        this.itemflags = itemflags;
    }

    /**
     * Gets the Triggers.
     *
     * @return The Triggers.
     */
    public String getTriggers() {
        return this.triggers;
    }

    /**
     * Sets the Triggers.
     *
     * @param triggers - The Triggers to be set.
     */
    public void setTriggers(final String triggers) {
        this.triggers = triggers;
    }

    /**
     * Gets the Permission.
     *
     * @return The Permission.
     */
    public String getPermissionNode() {
        return permissionNode;
    }

    /**
     * Gets the Toggle Permission.
     *
     * @return The Permission.
     */
    public String getToggleNode() {
        return toggleNode;
    }

    /**
     * Gets the Toggle Message.
     *
     * @return The Toggle Message.
     */
    public String getToggleMessage() {
        return toggleMessage;
    }

    /**
     * Sets the Toggle Message.
     *
     * @param message - The Message to be set.
     */
    public void setToggleMessage(final String message) {
        this.toggleMessage = message;
    }

    /**
     * Gets the ItemStacks Data Value.
     *
     * @return The ItemStacks Data Value.
     */
    public Short getDataValue() {
        return this.dataValue;
    }

    /**
     * Sets the ItemStacks Data Value.
     *
     * @param dataValue - The ItemStacks Data Value to be set.
     */
    public void setDataValue(final Short dataValue) {
        if (dataValue == null || dataValue == 0) {
            this.dataValue = null;
            return;
        }
        this.dataValue = dataValue;
    }

    /**
     * Gets the Material.
     *
     * @return The Material.
     */
    public Material getMaterial() {
        return this.material;
    }

    /**
     * Sets the Material.
     *
     * @param mat - The Material to be set.
     */
    public void setMaterial(final Material mat) {
        this.material = mat;
    }

    /**
     * Gets the ItemStacks Durability.
     *
     * @return The ItemStacks Durability.
     */
    public Short getDurability() {
        if (this.durability != null) {
            return this.durability;
        }
        return 0;
    }

    /**
     * Sets the ItemStack Durability.
     *
     * @param durability - The ItemStack Durability to be set.
     */
    public void setDurability(final Short durability) {
        this.durability = durability;
    }

    /**
     * Gets the ItemStack Data.
     *
     * @return The ItemStack Data.
     */
    public Integer getData() {
        if (this.data != null) {
            return this.data;
        }
        return 0;
    }

    /**
     * Sets the ItemStack Data.
     *
     * @param data - The ItemStack Data to be set.
     */
    public void setData(final Integer data) {
        this.data = data;
    }

    /**
     * Gets the ItemStack Model Data.
     *
     * @return The ItemStack Model Data.
     */
    public String getModelData() {
        if (this.modelData != null) {
            return this.modelData;
        }
        return null;
    }

    /**
     * Sets the ItemStack Model Data.
     *
     * @param data - The ItemStack Model Data to be set.
     */
    public void setModelData(final String data) {
        this.modelData = data;
    }

    /**
     * Sets the ItemStack Model Data.
     *
     * @param player - The Player to be used for placeholders.
     */
    private void setModelData(final Player player) {
        if (modelData != null) {
            final int modelData = Integer.parseInt(StringUtils.translateLayout(this.modelData, player));
            if (modelData != 0) {
                if (ServerUtils.hasSpecificUpdate("1_14")) {
                    this.tempMeta.setCustomModelData(modelData);
                } else {
                    ServerUtils.logWarn("{ItemMap} The item " + this.getConfigName() + " is using Custom Model Data which is not supported until Minecraft 1.14+.");
                }
            }
        }
    }

    /**
     * Gets the current mobsDrops.
     *
     * @return The mobsDrop Map.
     */
    public Map<EntityType, Double> getMobsDrop() {
        return this.mobsDrop;
    }

    /**
     * Sets the MobsDrop.
     *
     * @param mobsDrop - The mobsDrop to be set.
     */
    public void setMobsDrop(final Map<EntityType, Double> mobsDrop) {
        this.mobsDrop = mobsDrop;
    }

    /**
     * Checks if mobs drop are enabled.
     *
     * @return If mobs drop are enabled.
     */
    public boolean mobsDrop() {
        return (this.mobsDrop != null && !this.mobsDrop.isEmpty());
    }

    /**
     * Gets the current blocksDrop.
     *
     * @return The blocksDrop Map.
     */
    public Map<Material, Double> getBlocksDrop() {
        return this.blocksDrop;
    }

    /**
     * Sets the BlocksDrop.
     *
     * @param blocksDrop - The blocksDrop to be set.
     */
    public void setBlocksDrop(final Map<Material, Double> blocksDrop) {
        this.blocksDrop = blocksDrop;
    }

    /**
     * Checks if blocks drop are enabled.
     *
     * @return If blocks drop are enabled.
     */
    public boolean blocksDrop() {
        return (this.blocksDrop != null && !this.blocksDrop.isEmpty());
    }

    /**
     * Gets the Probability.
     *
     * @return The Probability.
     */
    public Integer getProbability() {
        return (this.probability != null) ? this.probability : 0;
    }

    /**
     * Sets the Probability.
     *
     * @param probability - The Probability to be set.
     */
    public void setProbability(final Integer probability) {
        this.probability = probability;
    }

    /**
     * Gets the Commands Sound.
     *
     * @return The Commands Sound.
     */
    public Sound getCommandSound() {
        return this.commandSound;
    }

    /**
     * Sets the Commands Sound.
     *
     * @param sound - The Commands Sound to be set.
     */
    public void setCommandSound(final Sound sound) {
        this.commandSound = sound;
    }

    /**
     * Gets the Commands Sound Volume.
     *
     * @return The Commands Sound Volume.
     */
    public Double getCommandVolume() {
        return this.commandSoundVolume;
    }

    /**
     * Sets the Commands Sound Volume.
     *
     * @param volume - The Commands Sound Volume to be set.
     */
    public void setCommandVolume(final Double volume) {
        this.commandSoundVolume = volume;
    }

    /**
     * Gets the Commands Sound Pitch.
     *
     * @return The Commands Sound Pitch.
     */
    public Double getCommandPitch() {
        return this.commandSoundPitch;
    }

    /**
     * Sets the Commands Sound Pitch.
     *
     * @param pitch - The Commands Sound Pitch to be set.
     */
    public void setCommandPitch(final Double pitch) {
        this.commandSoundPitch = pitch;
    }

    /**
     * Gets the Commands Warmup Delay.
     *
     * @return The Commands Warmup Delay.
     */
    public Integer getWarmDelay() {
        return this.warmDelay;
    }

    /**
     * Sets the Commands Warmup Delay.
     *
     * @param delay - The Commands Warmup Delay to be set.
     */
    public void setWarmDelay(final Integer delay) {
        this.warmDelay = delay;
    }

    /**
     * Checks if the Player is Pending Warmup.
     *
     * @param player - The Player to be checked.
     */
    private boolean getWarmPending(final Player player) {
        return this.warmPending.contains(player);
    }

    /**
     * Gets the ItemStack.
     *
     * @param player - The Player to have their ItemStack generated.
     * @return The Player Specific ItemStack.
     */
    public ItemStack getItemStack(final Player player) {
        this.updateItem(player);
        return this.tempItem.clone();
    }

    /**
     * Gets the Temporary ItemStack.
     *
     * @return The Temporary ItemStack.
     */
    public ItemStack getTempItem() {
        return this.tempItem;
    }

    /**
     * Sets the Temporary ItemStack.
     *
     * @param temp - The ItemStack to be set.
     */
    public void setTempItem(final ItemStack temp) {
        this.tempItem = temp;
    }

    /**
     * Gets the Config Name.
     *
     * @return The Config Name.
     */
    public String getConfigName() {
        return this.configName;
    }

    /**
     * Gets the Banner Patterns.
     *
     * @return The Banner Patterns.
     */
    public List<Pattern> getBannerPatterns() {
        return this.bannerPatterns;
    }

    /**
     * Sets the Banner Patterns.
     *
     * @param patterns - The Banner Patterns to be set.
     */
    public void setBannerPatterns(final List<Pattern> patterns) {
        this.bannerPatterns = patterns;
    }

    /**
     * Gets the Armor Trim Pattern.
     *
     * @return The Armor Trim Pattern.
     */
    public Map<String, String> getTrimPattern() {
        return this.trimPattern;
    }

    /**
     * Sets the Armor Trim Pattern.
     *
     * @param pattern - The Armor Trim Pattern to be set.
     */
    public void setTrimPattern(final Map<String, String> pattern) {
        this.trimPattern = pattern;
    }

    /**
     * Gets the recipe ingredients.
     *
     * @return The recipe ingredients.
     */
    public Map<Character, ItemRecipe> getIngredients() {
        return this.ingredients;
    }

    /**
     * Checks if the ItemMap is a recipe.
     *
     * @return If the ItemMap is a recipe.
     */
    public boolean isRecipe() {
        return !this.ingredients.isEmpty() && !this.getRecipe().isEmpty();
    }

    /**
     * Sets the recipe ingredients.
     *
     * @param ingredientList - The recipe ingredients to be set.
     */
    public void setIngredients(final Map<Character, ItemRecipe> ingredientList) {
        this.ingredients = ingredientList;
    }

    /**
     * Gets the recipe pattern.
     *
     * @return The recipe pattern.
     */
    public List<List<Character>> getRecipe() {
        return this.recipe;
    }

    /**
     * Sets the recipe pattern.
     *
     * @param recipe - The recipe pattern to be set.
     */
    public void setRecipe(final List<Character> recipe) {
        this.recipe = new ArrayList<>();
        this.recipe.add(recipe);
    }

    /**
     * Gets the Node Location.
     *
     * @return The Node Location.
     */
    public ConfigurationSection getNodeLocation() {
        return this.nodeLocation;
    }

    /**
     * Gets the Limit Modes.
     *
     * @return The Limit Modes.
     */
    public String getLimitModes() {
        if (this.limitModes != null) {
            return this.limitModes;
        }
        return "NONE";
    }

    /**
     * Sets the Limit Modes.
     *
     * @param str - The Limit Modes to be set.
     */
    public void setLimitModes(final String str) {
        this.limitModes = str;
    }

    /**
     * Gets the ItemCommands.
     *
     * @return The ItemCommands.
     */
    public ItemCommand[] getCommands() {
        return this.commands;
    }

    /**
     * Sets the ItemCommands.
     *
     * @param commands - The ItemCommands to be set.
     */
    public void setCommands(final ItemCommand[] commands) {
        this.commands = commands;
    }

    /**
     * Gets the Toggle Commands.
     *
     * @return The Toggle Commands.
     */
    public List<String> getToggleCommands() {
        return this.toggleCommands;
    }

    /**
     * Sets the Toggle Commands.
     *
     * @param toggleSingle - The Toggle Commands to be set.
     */
    private void setToggleCommands(final String toggleSingle) {
        final List<String> commandList = new ArrayList<>();
        if (toggleSingle != null && !this.nodeLocation.getStringList(".toggle").isEmpty() || toggleSingle == null && this.toggleCommands != null && !this.toggleCommands.isEmpty()) {
            for (String command : (toggleSingle != null ? this.nodeLocation.getStringList(".toggle") : this.toggleCommands)) {
                PluginCommand cmd = null;
                try {
                    Constructor<PluginCommand> pluginCommand = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                    pluginCommand.setAccessible(true);
                    cmd = pluginCommand.newInstance((command.contains(" ") ? command.split(" ")[0] : command), ItemJoin.getCore().getPlugin());
                } catch (Exception e) {
                    ServerUtils.sendDebugTrace(e);
                }
                if (cmd != null) {
                    cmd.setDescription(this.configName);
                    cmd.setExecutor(new ChatToggleExecutor());
                    cmd.setTabCompleter(new ChatToggleTab());
                    this.togglePlugins.add(cmd);
                    commandList.add(command);
                }
            }
        } else if (toggleSingle != null && !toggleSingle.isEmpty() && !toggleSingle.equalsIgnoreCase(" ")) {
            PluginCommand cmd = null;
            try {
                Constructor<PluginCommand> pluginCommand = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                pluginCommand.setAccessible(true);
                cmd = pluginCommand.newInstance((toggleSingle.contains(" ") ? toggleSingle.split(" ")[0] : toggleSingle), ItemJoin.getCore().getPlugin());
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
            }
            if (cmd != null) {
                cmd.setDescription(this.configName);
                cmd.setExecutor(new ChatToggleExecutor());
                cmd.setTabCompleter(new ChatToggleTab());
                this.togglePlugins.add(cmd);
                commandList.add(toggleSingle);
            }
        }
        ServerUtils.registerCommands(this.togglePlugins);
        this.toggleCommands = commandList;
    }

    /**
     * Sets the Toggle Commands.
     *
     * @param commands - The list of toggle commands.
     */
    public void setToggleCommands(final List<String> commands) {
        this.toggleCommands = commands;
        this.setToggleCommands((String) null);
    }

    /**
     * Gets the Commands Cooldown.
     *
     * @return The Commands Cooldown.
     */
    public Integer getCommandCooldown() {
        return this.cooldownSeconds;
    }

    /**
     * Sets the Commands Cooldown.
     *
     * @param i - The Commands Cooldown Seconds.
     */
    public void setCommandCooldown(final int i) {
        this.cooldownSeconds = i;
    }

    /**
     * Gets the Commands Cost.
     *
     * @return The Commands Cost.
     */
    public Integer getCommandCost() {
        return this.cost;
    }

    /**
     * Sets the Commands Cost.
     *
     * @param cost - The Commands Cost to be set.
     */
    public void setCommandCost(final Integer cost) {
        this.cost = cost;
    }

    /**
     * Gets the Commands Receive.
     *
     * @return The Commands Receive.
     */
    public Integer getCommandReceive() {
        return this.commandsReceive;
    }

    /**
     * Sets the Commands Receive.
     *
     * @param val - The Commands Receive to be set.
     */
    public void setCommandReceive(final Integer val) {
        this.commandsReceive = val;
    }

    /**
     * Gets the Commands Item Cost.
     *
     * @return The Commands Item Cost.
     */
    public String getItemCost() {
        return this.itemCost;
    }

    /**
     * Sets the Commands Item Cost.
     *
     * @param itemCost - The Commands Item Cost to be set.
     */
    public void setItemCost(final String itemCost) {
        this.itemCost = itemCost;
    }

    /**
     * Gets the Commands Particle.
     *
     * @return The Commands Particle.
     */
    public String getCommandParticle() {
        return this.commandParticle;
    }

    /**
     * Sets the Commands Particle.
     *
     * @param s - The Commands Particle to be set.
     */
    public void setCommandParticle(final String s) {
        this.commandParticle = s;
    }

    /**
     * Gets the Commands Sequence.
     *
     * @return The Commands Sequence.
     */
    public CommandSequence getCommandSequence() {
        return this.sequence;
    }

    /**
     * Sets the CommandSequence.
     *
     * @param sequence - The CommandSequence to be set.
     */
    public void setCommandSequence(final CommandSequence sequence) {
        this.sequence = sequence;
    }

    /**
     * Gets the Book Author.
     *
     * @return The Book Author.
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Sets the Book Author.
     *
     * @param auth - The Book Author to be set.
     */
    public void setAuthor(final String auth) {
        this.author = auth;
    }

    /**
     * Gets the Book Title.
     *
     * @return The Book Title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the Book Title.
     *
     * @param title - The Book Title to be set.
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets the Book Generation.
     *
     * @return The Book Generation.
     */
    public org.bukkit.inventory.meta.BookMeta.Generation getGeneration() {
        return (Generation) this.generation;
    }

    /**
     * Sets the Book Generation.
     *
     * @param gen - The Book Generation to be set.
     */
    public void setGeneration(final org.bukkit.inventory.meta.BookMeta.Generation gen) {
        this.generation = gen;
    }

    /**
     * Gets the Book Pages.
     *
     * @return The Book Pages.
     */
    public List<String> getPages() {
        return this.bookPages;
    }

    /**
     * Sets the Book Pages.
     *
     * @param pages - The Book Pages to be set.
     */
    public void setPages(final List<String> pages) {
        this.bookPages = pages;
    }

    /**
     * Gets the Book List Pages.
     *
     * @return The Book List Pages.
     */
    public List<List<String>> getListPages() {
        return this.listPages;
    }

    /**
     * Sets the Book List Pages.
     *
     * @param pages - The Book List Pages to be set.
     */
    public void setListPages(final List<List<String>> pages) {
        this.listPages = pages;
    }

    /**
     * Gets the Map ID.
     *
     * @return The Map ID.
     */
    public int getMapID() {
        return this.mapId;
    }

    /**
     * Sets the Map ID.
     *
     * @param id - The Map ID to be set.
     */
    public void setMapID(final int id) {
        this.mapId = (short) id;
    }

    /**
     * Gets the Map Image.
     *
     * @return The Map Image.
     */
    public String getMapImage() {
        return this.customMapImage;
    }

    /**
     * Sets the Map Image.
     *
     * @param mapIMG - The Map Image to be set.
     */
    public void setMapImage(final String mapIMG) {
        this.customMapImage = mapIMG;
    }

    /**
     * Gets the FireworkEffect.
     *
     * @return The FireworkEffect.
     */
    public FireworkEffect getFirework() {
        return this.firework;
    }

    /**
     * Sets the FireworkEffect.
     *
     * @param fire - The FireworkEffect to be set.
     */
    public void setFirework(final FireworkEffect fire) {
        this.firework = fire;
    }

    /**
     * Gets the Firework Type.
     *
     * @return The Firework Type.
     */
    public Type getFireworkType() {
        return this.fireworkType;
    }

    /**
     * Sets the Firework Type.
     *
     * @param buildType - The Firework Type to be set.
     */
    public void setFireworkType(final Type buildType) {
        this.fireworkType = buildType;
    }

    /**
     * Gets the Firework Power.
     *
     * @return The Firework Power.
     */
    public int getFireworkPower() {
        return this.power;
    }

    /**
     * Sets the Firework Power.
     *
     * @param power - The Firework Power to be set.
     */
    public void setFireworkPower(final int power) {
        this.power = power;
    }

    /**
     * Gets the Firework Colors.
     *
     * @return The Firework Colors.
     */
    public List<DyeColor> getFireworkColor() {
        return this.fireworkColor;
    }

    /**
     * Sets the Firework Colors.
     *
     * @param colors - The Firework Colors to be set.
     */
    public void setFireworkColor(final List<DyeColor> colors) {
        this.fireworkColor = colors;
    }

    /**
     * Gets the Firework Trail.
     *
     * @return The Firework Trail.
     */
    public boolean getFireworkTrail() {
        return this.fireworkTrail;
    }

    /**
     * Sets the Firework Trail.
     *
     * @param bool - The value to be set.
     */
    public void setFireworkTrail(final boolean bool) {
        this.fireworkTrail = bool;
    }

    /**
     * Gets the Firework Flicker.
     *
     * @return The Firework Flicker.
     */
    public boolean getFireworkFlicker() {
        return this.fireworkFlicker;
    }

    /**
     * Sets the Firework Flicker.
     *
     * @param bool - The value to be set.
     */
    public void setFireworkFlicker(final boolean bool) {
        this.fireworkFlicker = bool;
    }

    /**
     * Gets the Firework Charge Color.
     *
     * @return The Firework Charge Color.
     */
    public DyeColor getChargeColor() {
        return this.chargeColor;
    }

    /**
     * Sets the Firework Charge Color.
     *
     * @param dyeColor - The Firework Charge Color to be set.
     */
    public void setChargeColor(final DyeColor dyeColor) {
        this.chargeColor = dyeColor;
    }

    /**
     * Gets the Skull Owner.
     *
     * @return The Skull Owner.
     */
    public String getSkull() {
        return this.skullOwner;
    }

//  ================================================================================================================================================================================= //

//  ====================== //
//  ~ Accessor Functions ~ //
//  ====================== //

    /**
     * Gets the Skull Texture.
     *
     * @return The Skull Texture.
     */
    public String getSkullTexture() {
        return this.skullTexture;
    }

    /**
     * Sets the Skull Texture.
     *
     * @param skull - The Skull Texture to be set.
     */
    public void setSkullTexture(final String skull) {
        this.skullTexture = skull;
    }

    /**
     * Gets the PotionEffects.
     *
     * @return The PotionEffects.
     */
    public List<PotionEffect> getPotionEffect() {
        return this.effect;
    }

    /**
     * Sets the PotionEffect.
     *
     * @param potion - The PortionEffect to be set.
     */
    public void setPotionEffect(final List<PotionEffect> potion) {
        this.effect = potion;
    }

    /**
     * Gets the Leather Color.
     *
     * @return The Leather Color.
     */
    public String getLeatherColor() {
        return this.leatherColor;
    }

    /**
     * Sets the Leather Color.
     *
     * @param color - The Leather Color to be set.
     */
    public void setLeatherColor(final String color) {
        this.leatherColor = color;
    }

    /**
     * Gets the Leather HexColor.
     *
     * @return The Leather HexColor.
     */
    public String getLeatherHex() {
        return this.leatherHex;
    }

    /**
     * Sets the Leather HexColor.
     *
     * @param hex - The Leather HexColor to be set.
     */
    public void setLeatherHex(final String hex) {
        this.leatherHex = hex;
    }

    /**
     * Gets the NBT Values.
     *
     * @return The NBT Values.
     */
    public Map<Object, Object> getNBTValues() {
        return this.nbtProperty;
    }

    /**
     * Sets the NBT Values.
     *
     * @param tagValues - The Object Tags to be set.
     */
    public void setNBTValues(final Map<Object, Object> tagValues) {
        this.nbtProperty = tagValues;
    }

    /**
     * Gets the NBTData (Secret).
     *
     * @return The NBTData (Secret).
     */
    public String getLegacySecret() {
        if (!ItemJoin.getCore().getData().dataTagsEnabled()) {
            return this.legacySecret;
        } else {
            return null;
        }
    }

    /**
     * Sets the Legacy NBTData (Secret).
     *
     * @param nbt - The NBT Data to be set.
     */
    public void setLegacySecret(final String nbt) {
        this.legacySecret = nbt;
    }

    /**
     * Gets the Interact Cooldown.
     *
     * @return The Interact Cooldown.
     */
    public int getInteractCooldown() {
        return this.interactCooldown;
    }

    /**
     * Sets the Interact cooldown.
     *
     * @param cooldown - The value to be set.
     */
    public void setInteractCooldown(final int cooldown) {
        this.interactCooldown = cooldown;
    }

    /**
     * Gets the Teleport Effect.
     *
     * @return The Teleport Effect.
     */
    public String getTeleportEffect() {
        return this.teleportEffect;
    }

    /**
     * Sets the Teleport Effect.
     *
     * @param name - The value to be set.
     */
    public void setTeleportEffect(final String name) {
        this.teleportEffect = name;
    }

    /**
     * Gets the Teleport Sound.
     *
     * @return The Teleport Sound.
     */
    public String getTeleportSound() {
        return this.teleportSound;
    }

    /**
     * Sets the Teleport Sound.
     *
     * @param name - The value to be set.
     */
    public void setTeleportSound(final String name) {
        this.teleportSound = name;
    }

    /**
     * Gets the Teleport Volume.
     *
     * @return The Teleport Volume.
     */
    public Double getTeleportVolume() {
        return this.teleportSoundVolume;
    }

    /**
     * Sets the Teleport Volume.
     *
     * @param volume - The value to be set.
     */
    public void setTeleportVolume(final Double volume) {
        this.teleportSoundVolume = volume;
    }

    /**
     * Gets the Teleport Pitch.
     *
     * @return The Teleport Pitch.
     */
    public Double getTeleportPitch() {
        return this.teleportSoundPitch;
    }

    /**
     * Sets the Teleport Pitch.
     *
     * @param pitch - The value to be set.
     */
    public void setTeleportPitch(final Double pitch) {
        this.teleportSoundPitch = pitch;
    }

    /**
     * Gets the Commands Cooldown Message.
     *
     * @return The Commands Cooldown Message.
     */
    public String getCooldownMessage() {
        return this.cooldownMessage;
    }

    /**
     * Sets the Commands Cooldown Message.
     *
     * @param s - The Commands Cooldown Message to be set.
     */
    public void setCooldownMessage(final String s) {
        this.cooldownMessage = s;
    }

    /**
     * Gets the Trigger Message.
     *
     * @return The Trigger Message.
     */
    public String getTriggerMessage() {
        return this.triggerMessage;
    }

    /**
     * Sets the Trigger Message.
     *
     * @param s - The Trigger Message to be set.
     */
    public void setTriggerMessage(final String s) {
        this.triggerMessage = s;
    }

    /**
     * Gets the Trigger Conditions.
     *
     * @return The Trigger Conditions.
     */
    public List<String> getTriggerConditions() {
        return this.triggerConditions;
    }

    /**
     * Sets the Trigger Conditions.
     *
     * @param s - The Trigger Conditions to be set.
     */
    public void setTriggerConditions(final List<String> s) {
        this.triggerConditions = s;
    }

    /**
     * Gets the Disposable Message.
     *
     * @return The Disposable Message.
     */
    public String getDisposableMessage() {
        return this.disposableMessage;
    }

    /**
     * Sets the Disposable Message.
     *
     * @param s - The Disposable Message to be set.
     */
    public void setDisposableMessage(final String s) {
        this.disposableMessage = s;
    }

    /**
     * Gets the Disposable Conditions.
     *
     * @return The Disposable Conditions.
     */
    public List<String> getDisposableConditions() {
        return this.disposableConditions;
    }

    /**
     * Sets the Disposable Conditions.
     *
     * @param s - The Disposable Conditions to be set.
     */
    public void setDisposableConditions(final List<String> s) {
        this.disposableConditions = s;
    }

    /**
     * Gets the Commands Message.
     *
     * @return The Commands Message.
     */
    public Map<String, String> getCommandMessages() {
        return this.commandMessages;
    }

    /**
     * Sets the Commands Messages.
     *
     * @param s - The Commands Messages to be set.
     */
    public void setCommandMessages(final Map<String, String> s) {
        this.commandMessages = s;
    }

    /**
     * Gets the Commands Conditions.
     *
     * @return The Commands Condition List.
     */
    public Map<String, List<String>> getCommandConditions() {
        return this.commandConditions;
    }

    /**
     * Sets the Commands Conditions.
     *
     * @param s - The Commands Conditions to be set.
     */
    public void setCommandConditions(final Map<String, List<String>> s) {
        this.commandConditions = s;
    }

    /**
     * Gets the NBTData that should be set to be set to the custom item.
     *
     * @return The NBTData format to be set to an item.
     */
    public String getNBTFormat() {
        return this.getConfigName();
    }

    /**
     * Gets the Custom Consumable.
     *
     * @return The Custom Consumable.
     */
    public boolean isCustomConsumable() {
        return this.customConsumable;
    }

    /**
     * Sets the Custom Consumable.
     *
     * @param bool - The value to be set.
     */
    public void setCustomConsumable(final boolean bool) {
        customConsumable = bool;
    }

    /**
     * Checks if the Player has Permission.
     *
     * @param player - The Player that should have Permission.
     * @param world  - The world to check permission.
     * @return If the Player has Permission.
     */
    public boolean hasPermission(final Player player, final World world) {
        String customPerm = PermissionsHandler.customPermissions(this.permissionNode, world.getName() + "." + this.configName);
        if (!this.isPermissionNeeded() && !player.isOp() || (!this.isOPPermissionNeeded() && player.isOp())) {
            return true;
        } else if (this.isOPPermissionNeeded() && player.isOp()) {
            return player.isPermissionSet(customPerm) && player.hasPermission(customPerm) && (!player.isPermissionSet("itemjoin." + world.getName() + ".*")
                    || (player.isPermissionSet("itemjoin." + world.getName() + ".*") && player.hasPermission("itemjoin." + world.getName() + ".*")))
                    || ((player.isPermissionSet("itemjoin." + world.getName() + ".*") && player.hasPermission("itemjoin." + world.getName() + ".*")) || (player.isPermissionSet(customPerm) && player.hasPermission(customPerm)));
        } else
            return (player.isPermissionSet("itemjoin." + world.getName() + ".*") && player.hasPermission("itemjoin." + world.getName() + ".*")) || (player.isPermissionSet(customPerm) && player.hasPermission(customPerm));
    }

    /**
     * Checks if the item is a Crafting Item.
     *
     * @return If it is enabled.
     */
    public boolean isCraftingItem() {
        return this.craftingItem;
    }

    /**
     * Checks if the item is a HeadDatabase Item.
     *
     * @return If it is enabled.
     */
    public boolean isHeadDatabase() {
        return this.headDatabase;
    }

    /**
     * Sets the HeadDatabase Head.
     *
     * @param head - The value to be set.
     */
    public void setHeadDatabase(final boolean head) {
        this.headDatabase = head;
    }

    /**
     * Checks if you give on join is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isGiveOnJoin() {
        return this.giveOnJoin;
    }

    /**
     * Sets the ItemStack to be given only on Join.
     *
     * @param bool - The value to be set.
     */
    public void setGiveOnJoin(final boolean bool) {
        this.giveOnJoin = bool;
    }

    /**
     * Checks if you give on teleport is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isGiveOnTeleport() {
        return this.giveOnTeleport;
    }

    /**
     * Sets the ItemStack to be given only on teleport.
     *
     * @param bool - The value to be set.
     */
    public void setGiveOnTeleport(final boolean bool) {
        this.giveOnTeleport = bool;
    }

    /**
     * Checks if you give on world switch is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isGiveOnWorldSwitch() {
        return this.giveOnWorldSwitch;
    }

    /**
     * Sets the ItemStack to be given only on World Switch.
     *
     * @param bool - The value to be set.
     */
    public void setGiveOnWorldSwitch(final boolean bool) {
        this.giveOnWorldSwitch = bool;
    }

    /**
     * Checks if you give on respawn is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isGiveOnRespawn() {
        return this.giveOnRespawn;
    }

    /**
     * Sets the ItemStack to be given only on Respawn.
     *
     * @param bool - The value to be set.
     */
    public void setGiveOnRespawn(final boolean bool) {
        this.giveOnRespawn = bool;
    }

    /**
     * Checks if you give on respawn point is enabled.
     * Only gives the item if the player is NOT spawning in a bed, anchor, or spawn-point.
     *
     * @return If it is enabled.
     */
    public boolean isGiveOnRespawnPoint() {
        return this.giveOnRespawnPoint;
    }

    /**
     * Sets the ItemStack to be given only on Respawn Point.
     * Only gives the item if the player is NOT spawning in a bed, anchor, or spawn-point.
     *
     * @param bool - The value to be set.
     */
    public void setGiveOnRespawnPoint(final boolean bool) {
        this.giveOnRespawnPoint = bool;
    }

    /**
     * Checks if you give on region enter is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isGiveOnRegionEnter() {
        return this.giveOnRegionEnter;
    }

    /**
     * Sets the ItemStack to be given only on Region Enter.
     *
     * @param bool - The value to be set.
     */
    public void setGiveOnRegionEnter(final boolean bool) {
        this.giveOnRegionEnter = bool;
    }

    /**
     * Checks if you give on region leave is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isGiveOnRegionLeave() {
        return this.giveOnRegionLeave;
    }

    /**
     * Sets the ItemStack to be given only on Region Leave.
     *
     * @param bool - The value to be set.
     */
    public void setGiveOnRegionLeave(final boolean bool) {
        this.giveOnRegionLeave = bool;
    }

    /**
     * Checks if you give on region enter and remove on region leave is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isGiveOnRegionAccess() {
        return this.giveOnRegionAccess;
    }

    /**
     * Sets the ItemStack to be given only on region enter and removed on region leave.
     *
     * @param bool - The value to be set.
     */
    public void setGiveOnRegionAccess(final boolean bool) {
        this.giveOnRegionAccess = bool;
    }

    /**
     * Checks if you give on region leave and remove on region enter is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isGiveOnRegionEgress() {
        return this.giveOnRegionEgress;
    }

    /**
     * Sets the ItemStack to be given only on region leave and removed on region enter.
     *
     * @param bool - The value to be set.
     */
    public void setGiveOnRegionEgress(final boolean bool) {
        this.giveOnRegionEgress = bool;
    }

    /**
     * Checks if item giving is disabled.
     *
     * @return If it is disabled.
     */
    public boolean isGiveOnDisabled() {
        return this.giveOnDisabled;
    }

    /**
     * Sets the ItemStack to not be given.
     *
     * @param bool - The value to be set.
     */
    public void setGiveOnDisabled(final boolean bool) {
        this.giveOnDisabled = bool;
    }

    /**
     * Checks if you give on first join is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isOnlyFirstJoin() {
        return this.onlyFirstJoin;
    }

    /**
     * Sets the ItemStack to be given only on First Join.
     *
     * @param bool - The value to be set.
     */
    public void setOnlyFirstJoin(final boolean bool) {
        this.onlyFirstJoin = bool;
        if (bool && !this.giveOnRegionEnter && !this.giveOnRegionLeave && !this.giveOnRegionAccess && !this.giveOnRegionEgress) {
            this.giveOnJoin = true;
            this.giveOnRespawn = false;
            this.giveOnRespawnPoint = false;
            this.giveOnTeleport = false;
        }
    }

    /**
     * Checks if you give on first life is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isOnlyFirstLife() {
        return this.onlyFirstLife;
    }

    /**
     * Sets the ItemStack to be given only on First Join but will always be given upon respawn.
     *
     * @param bool - The value to be set.
     */
    public void setOnlyFirstLife(final boolean bool) {
        this.onlyFirstLife = bool;
        if (bool && !this.giveOnRegionEnter && !this.giveOnRegionLeave && !this.giveOnRegionAccess && !this.giveOnRegionEgress) {
            this.giveOnJoin = true;
            this.giveOnRespawn = true;
            this.giveOnTeleport = false;
        }
    }

    /**
     * Checks if you give on first world is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isOnlyFirstWorld() {
        return this.onlyFirstWorld;
    }

    /**
     * Sets the ItemStack to be given only on First World.
     *
     * @param bool - The value to be set.
     */
    public void setOnlyFirstWorld(final boolean bool) {
        this.onlyFirstWorld = bool;
        if (bool && !this.giveOnRegionEnter && !this.giveOnRegionLeave && !this.giveOnRegionAccess && !this.giveOnRegionEgress) {
            this.giveOnJoin = true;
            this.giveOnWorldSwitch = true;
            this.giveOnRespawn = false;
            this.giveOnRespawnPoint = false;
            this.giveOnTeleport = false;
        }
    }

    /**
     * Checks if ip limit is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isIpLimited() {
        return this.ipLimited;
    }

    /**
     * Sets the IP Limits.
     *
     * @param bool - The value to be set.
     */
    public void setIpLimited(final boolean bool) {
        this.ipLimited = bool;
    }

    /**
     * Checks if you use on limit switch is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isUseOnLimitSwitch() {
        return this.useOnLimitSwitch;
    }

    /**
     * Sets the Use on Limit Switch.
     *
     * @param bool - The value to be set.
     */
    public void setUseOnLimitSwitch(final boolean bool) {
        this.useOnLimitSwitch = bool;
    }

    /**
     * Checks if the GameMode is a limit mode.
     *
     * @return If the GameMode is a limit mode.
     */
    public boolean isLimitMode(final GameMode newMode) {
        if (this.limitModes != null) {
            return StringUtils.containsIgnoreCase(this.limitModes, newMode.name());
        }
        return true;
    }

    /**
     * Checks if the region is an enabled region.
     *
     * @return If the region is an enabled region.
     */
    public Boolean inRegion(final String region) {
        if (this.enabledRegions == null || this.enabledRegions.isEmpty()) {
            return true;
        }
        for (String compareRegion : this.enabledRegions) {
            if (compareRegion.equalsIgnoreCase(region) || compareRegion.equalsIgnoreCase("UNDEFINED")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any of the list of regions are an enabled region.
     *
     * @return If any of the list of regions are an enabled region.
     */
    public Boolean inRegion(final List<String> regions) {
        if (this.enabledRegions == null || this.enabledRegions.isEmpty()) {
            return true;
        }
        for (String region : regions) {
            for (String compareRegion : this.enabledRegions) {
                if (compareRegion.equalsIgnoreCase(region) || compareRegion.equalsIgnoreCase("UNDEFINED")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if Permissions are Required.
     *
     * @return If Permissions are Required.
     */
    public boolean isPermissionNeeded() {
        return this.permissionNeeded;
    }

    /**
     * Sets the Permissions to be Required.
     *
     * @param bool - The value to be set.
     */
    public void setPermissionNeeded(final boolean bool) {
        this.permissionNeeded = bool;
    }

    /**
     * Checks if OP Permissions are Required.
     *
     * @return If OP Permissions are Required.
     */
    public boolean isOPPermissionNeeded() {
        return this.opPermissionNeeded;
    }

    /**
     * Sets the OP Permissions to be Required.
     *
     * @param bool - The value to be set.
     */
    public void setOPPermissionNeeded(final boolean bool) {
        this.opPermissionNeeded = bool;
    }

    /**
     * Checks if the Vanilla Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isVanilla() {
        return this.vanillaItem;
    }

    /**
     * Sets the Vanilla Flag.
     *
     * @param bool - The value to be set.
     */
    public void setVanilla(final boolean bool) {
        this.vanillaItem = bool;
    }

    /**
     * Checks if the Vanilla Status Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isVanillaStatus() {
        return this.vanillaStatus;
    }

    /**
     * Sets the Vanilla Status Flag.
     *
     * @param bool - The value to be set.
     */
    public void setVanillaStatus(final boolean bool) {
        this.vanillaStatus = bool;
    }

    /**
     * Checks if the Vanilla Control Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isVanillaControl() {
        return this.vanillaControl;
    }

    /**
     * Sets the Vanilla Control Flag.
     *
     * @param bool - The value to be set.
     */
    public void setVanillaControl(final boolean bool) {
        this.vanillaControl = bool;
    }

    /**
     * Checks if the Give Next Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isGiveNext() {
        return this.giveNext;
    }

    /**
     * Sets the Give Next Flag.
     *
     * @param bool - The value to be set.
     */
    public void setGiveNext(final boolean bool) {
        this.giveNext = bool;
    }

    /**
     * Checks if the Move Next Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isMoveNext() {
        return this.moveNext;
    }

    /**
     * Sets the Move Next Flag.
     *
     * @param bool - The value to be set.
     */
    public void setMoveNext(final boolean bool) {
        this.moveNext = bool;
    }

    /**
     * Checks if the Drop Full Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isDropFull() {
        return this.dropFull;
    }

    /**
     * Sets the Drop Full Flag.
     *
     * @param bool - The value to be set.
     */
    public void setDropFull(final boolean bool) {
        this.dropFull = bool;
    }

    /**
     * Checks if the Unbreakable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isUnbreakable() {
        return this.unbreakable;
    }

    /**
     * Sets the Unbreakable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setUnbreakable(final boolean bool) {
        this.unbreakable = bool;
    }

    /**
     * Checks if the Count Lock Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isCountLock() {
        return this.countLock;
    }

    /**
     * Sets the Count Lock Flag.
     *
     * @param bool - The value to be set.
     */
    public void setCountLock(final boolean bool) {
        this.countLock = bool;
    }

    /**
     * Checks if the Teleport Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isTeleport() {
        return this.teleportArrow;
    }

    /**
     * Sets the Teleport Flag.
     *
     * @param bool - The value to be set.
     */
    public void setTeleport(final boolean bool) {
        this.teleportArrow = bool;
    }

    /**
     * Checks if the Cancel Events Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isCancelEvents() {
        return this.cancelEvents;
    }

    /**
     * Sets the Cancel Events Flag.
     *
     * @param bool - The value to be set.
     */
    public void setCancelEvents(final boolean bool) {
        this.cancelEvents = bool;
    }

    /**
     * Checks if the Item Store Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isItemStore() {
        return this.itemStore;
    }

    /**
     * Sets the Item Store Flag.
     *
     * @param bool - The value to be set.
     */
    public void setItemStore(final boolean bool) {
        this.itemStore = bool;
    }

    /**
     * Checks if the Item Modify Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isItemModify() {
        return this.itemModify;
    }

    /**
     * Sets the Item Modify Flag.
     *
     * @param bool - The value to be set.
     */
    public void setItemModify(final boolean bool) {
        this.itemModify = bool;
    }

    /**
     * Checks if the Item Craftable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isItemCraftable() {
        return this.noCrafting;
    }

    /**
     * Sets the Item Craftable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setItemCraftable(final boolean bool) {
        this.noCrafting = bool;
    }

    /**
     * Checks if the Item Repairable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isItemRepairable() {
        return this.noRepairing;
    }

    /**
     * Sets the Item Repairable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setItemRepairable(final boolean bool) {
        this.noRepairing = bool;
    }

    /**
     * Checks if the Item Changeable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isItemChangeable() {
        return this.itemChangeable;
    }

    /**
     * Sets the Item Changeable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setItemChangeable(final boolean bool) {
        this.itemChangeable = bool;
    }

    /**
     * Checks if the Always Give Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isAlwaysGive() {
        return this.alwaysGive;
    }

    /**
     * Sets the Always Give Flag.
     *
     * @param bool - The value to be set.
     */
    public void setAlwaysGive(final boolean bool) {
        this.alwaysGive = bool;
    }

    /**
     * Checks if the Auto Remove Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isAutoRemove() {
        return this.autoRemove;
    }

    /**
     * Sets the Auto Remove Flag.
     *
     * @param bool - The value to be set.
     */
    public void setAutoRemove(final boolean bool) {
        this.autoRemove = bool;
    }

    /**
     * Checks if the Stackable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isStackable() {
        return this.stackable;
    }

    /**
     * Sets the Stackable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setStackable(final boolean bool) {
        this.stackable = bool;
    }

    /**
     * Checks if the Not Hat Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isNotHat() {
        return this.notHat;
    }

    /**
     * Sets the Not Hat Flag.
     *
     * @param bool - The value to be set.
     */
    public void setNotHat(final boolean bool) {
        this.notHat = bool;
    }

    /**
     * Checks if the Selectable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isSelectable() {
        return this.selectable;
    }

    /**
     * Sets the Selectable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setSelectable(final boolean bool) {
        this.selectable = bool;
    }

    /**
     * Checks if the Splittable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isSplittable() {
        return this.splittable;
    }

    /**
     * Sets the Splittable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setSplittable(final boolean bool) {
        this.splittable = bool;
    }

    /**
     * Checks if the Animate Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isAnimated() {
        return this.animate;
    }

    /**
     * Checks if the Dynamic Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isDynamic() {
        return this.dynamic;
    }

    /**
     * Checks if the Dynamic Count Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isDynamicCount() {
        return this.dynamicCount;
    }

    /**
     * Sets the Dynamic Flag.
     *
     * @param bool - The value to be set.
     */
    public void setDynamic(final boolean bool) {
        this.dynamic = bool;
    }

    /**
     * Checks if the Glowing Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isGlowing() {
        return this.glowing;
    }

    /**
     * Sets the Glowing Flag.
     *
     * @param bool - The value to be set.
     */
    public void setGlowing(final boolean bool) {
        this.glowing = bool;
    }

    /**
     * Checks if the Overwritable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isOverwritable() {
        return this.overwritable;
    }

    /**
     * Sets the Overwritable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setOverwritable(final boolean bool) {
        this.overwritable = bool;
    }

    /**
     * Checks if the Placement Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isPlaceable() {
        return this.blockPlacement;
    }

    /**
     * Sets the Placeable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setPlaceable(final boolean bool) {
        this.blockPlacement = bool;
    }

    /**
     * Checks if the Attributes Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isAttributesInfo() {
        return this.hideAttributes;
    }

    /**
     * Sets the Attributes Flag.
     *
     * @param bool - The value to be set.
     */
    public void setAttributesInfo(final boolean bool) {
        this.hideAttributes = bool;
    }

    /**
     * Checks if the Enchantments Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isEnchantmentsInfo() {
        return this.hideEnchantments;
    }

    /**
     * Sets the Enchantments Flag.
     *
     * @param bool - The value to be set.
     */
    public void setEnchantmentsInfo(final boolean bool) {
        this.hideEnchantments = bool;
    }

    /**
     * Checks if the Attributes Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isFlagsInfo() {
        return this.hideFlags;
    }

    /**
     * Sets the Flags.
     *
     * @param bool - The value to be set.
     */
    public void setFlagsInfo(final boolean bool) {
        this.hideFlags = bool;
    }

    /**
     * Checks if the Durability Bar Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isDurabilityBar() {
        return this.hideDurability;
    }

    /**
     * Sets the Durability Bar Flag.
     *
     * @param bool - The value to be set.
     */
    public void setDurabilityBar(final boolean bool) {
        this.hideDurability = bool;
    }

    /**
     * Checks if the Movement Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isMovement() {
        return this.blockMovement;
    }

    /**
     * Sets the Movement Flag.
     *
     * @param bool - The value to be set.
     */
    public void setMovement(final boolean bool) {
        this.blockMovement = bool;
    }

    /**
     * Checks if the Equip Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isEquip() {
        return this.blockEquip;
    }

    /**
     * Sets the Equip Flag.
     *
     * @param bool - The value to be set.
     */
    public void setEquip(final boolean bool) {
        this.blockEquip = bool;
    }

    /**
     * Checks if the Inventory Close Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isInventoryClose() {
        return this.closeInventory;
    }

    /**
     * Checks if the Disposable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isDisposable() {
        return this.disposable;
    }

    /**
     * Sets the Disposable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setDisposable(final boolean bool) {
        this.disposable = bool;
    }

    /**
     * Checks if the Self Droppable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isSelfDroppable() {
        return this.selfDroppable;
    }

    /**
     * Sets the Self Droppable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setSelfDroppable(final boolean bool) {
        this.selfDroppable = bool;
    }

    /**
     * Checks if the Death Droppable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isDeathDroppable() {
        return this.deathDroppable;
    }

    /**
     * Sets the Death Droppable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setDeathDroppable(final boolean bool) {
        this.deathDroppable = bool;
    }

    /**
     * Checks if the Death Keepable Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isDeathKeepable() {
        return this.deathKeepable;
    }

    /**
     * Sets the Death Keepable Flag.
     *
     * @param bool - The value to be set.
     */
    public void setDeathKeepable(final boolean bool) {
        this.deathKeepable = bool;
    }

    /**
     * Checks if the CreativeBypass Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isCreativeBypass() {
        return this.CreativeBypass;
    }

    /**
     * Sets the CreativeBypass Flag.
     *
     * @param bool - The value to be set.
     */
    public void setCreativeBypass(final boolean bool) {
        this.CreativeBypass = bool;
    }

    /**
     * Checks if the OPBypass Flag is enabled.
     *
     * @return If it is enabled.
     */
    public boolean isOpBypass() {
        return this.AllowOpBypass;
    }

    /**
     * Sets the OPBypass Flag.
     *
     * @param bool - The value to be set.
     */
    public void setOpBypass(final boolean bool) {
        this.AllowOpBypass = bool;
    }

    /**
     * Checks if the ItemStack is subject to removal.
     * Prevents Duplication.
     *
     * @return If the ItemStack is pending removal.
     */
    private boolean isSubjectRemoval() {
        return this.subjectRemoval;
    }

    /**
     * Sets the ItemStack as Subject to Removal.
     *
     * @param bool - The value to be set.
     */
    private void setSubjectRemoval(final boolean bool) {
        this.subjectRemoval = bool;
    }

    /**
     * Checks if the String World Name is a Disabled/Enabled World.
     *
     * @param world      - The name of the World being checked.
     * @param isDisabled - If the worlds being checked are Disabled.
     * @return If the World is a Disabled/Enabled World.
     */
    public boolean containsWorld(final String world, final boolean isDisabled) {
        for (String worldString : (isDisabled ? this.getDisabledWorlds() : this.getEnabledWorlds())) {
            if (worldString.equalsIgnoreCase(world) || worldString.equalsIgnoreCase("ALL") || worldString.equalsIgnoreCase("GLOBAL")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the String Region Name is an Enabled Region.
     *
     * @param region - The name of the Region being checked.
     * @return If the Region is an Enabled Region.
     */
    public boolean containsRegion(final String region) {
        for (String enabledRegion : this.getEnabledRegions()) {
            if (enabledRegion.equalsIgnoreCase(region) || enabledRegion.equalsIgnoreCase("UNDEFINED")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the ItemFlag should be allowed.
     *
     * @param player   - The Player being Checked.
     * @param item     - The ItemStack being checked.
     * @param findFlag - The ItemFlag being found.
     * @return If the ItemFlag is to be prevented.
     */
    public boolean isAllowedItem(final Player player, final ItemStack item, final String findFlag) {
        if (!Menu.isOpen(player) && this.isSimilar(player, item)) {
            if (this.AllowOpBypass && player.isOp() || this.CreativeBypass && player.getGameMode() == GameMode.CREATIVE
                    || findFlag.equalsIgnoreCase("inventory-modify") && player.hasPermission("itemjoin.bypass.inventorymodify")
                    && ItemJoin.getCore().getPlugin().getConfig().getBoolean("Permissions.Movement-Bypass")) {
                return false;
            } else if (findFlag.equals("cancel-events")) {
                return cancelEvents;
            } else if (findFlag.equals("self-drops")) {
                return selfDroppable;
            } else if (findFlag.equals("death-drops")) {
                return deathDroppable;
            } else if (findFlag.equals("death-keep")) {
                return deathKeepable;
            } else if (findFlag.equals("cancel-equip")) {
                return blockEquip;
            } else if (findFlag.equals("inventory-modify")) {
                return blockMovement;
            } else if (findFlag.equals("inventory-close")) {
                return closeInventory;
            } else if (findFlag.equals("item-store")) {
                return itemStore;
            } else if (findFlag.equals("stackable")) {
                return stackable;
            } else if (findFlag.equals("not-hat")) {
                return notHat;
            } else if (findFlag.equals("selectable")) {
                return selectable;
            } else if (findFlag.equals("splittable")) {
                return splittable;
            } else if (findFlag.equals("item-modifiable")) {
                return itemModify;
            } else if (findFlag.equals("item-craftable")) {
                return noCrafting;
            } else if (findFlag.equals("item-repairable")) {
                return noRepairing;
            } else if (findFlag.equals("placement")) {
                return blockPlacement;
            } else if (findFlag.equals("count-lock")) {
                return countLock;
            } else if (findFlag.equals("teleport")) {
                return teleportArrow;
            }
        }
        return false;
    }

    /**
     * Checks if the ItemStack is similar to the defined ItemMap.
     *
     * @param item - The ItemStack being checked.
     * @return If the ItemStack is similar.
     */
    public boolean isReal(final ItemStack item) {
        final String nbtData = ItemHandler.getNBTData(item, PluginData.getInfo().getNBTList());
        return item != null && item.getType() != Material.AIR
                && (this.vanillaControl || this.vanillaStatus
                || (ItemJoin.getCore().getData().dataTagsEnabled() && nbtData != null && nbtData.equalsIgnoreCase(this.newNBTData))
                || (this.legacySecret != null && item.hasItemMeta() && (ServerUtils.hasSpecificUpdate("1_14") || (!ServerUtils.hasSpecificUpdate("1_14") && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()))
                && Objects.requireNonNull(StringUtils.colorDecode(item)).contains(this.legacySecret)));
    }

    /**
     * Checks if the ItemStack is similar to the defined ItemMap.
     *
     * @param player - The player being referenced.
     * @param item   - The ItemStack being checked.
     * @return If the ItemStack is similar.
     */
    public boolean isSimilar(final Player player, final ItemStack item) {
        if ((item != null && item.getType() != Material.AIR && item.getType() == this.material) || (this.materialAnimated && item != null && item.getType() != Material.AIR && this.isMaterial(item))) {
            if (this.vanillaControl || this.vanillaStatus || (ItemJoin.getCore().getData().dataTagsEnabled() && ItemHandler.getNBTData(item, PluginData.getInfo().getNBTList()) != null && Objects.requireNonNull(ItemHandler.getNBTData(item, PluginData.getInfo().getNBTList())).equalsIgnoreCase(this.newNBTData))
                    || (this.legacySecret != null && item.hasItemMeta() && (ServerUtils.hasSpecificUpdate("1_14") || (!ServerUtils.hasSpecificUpdate("1_14") && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()))
                    && Objects.requireNonNull(StringUtils.colorDecode(item)).contains(this.legacySecret))) {
                if (this.isEnchantSimilar(player, item) || !Objects.requireNonNull(item.getItemMeta()).hasEnchants() && this.enchants.isEmpty() || this.isItemChangeable()) {
                    if (this.material.toString().toUpperCase().contains("BOOK")
                            && (this.isBookMeta(player, item)
                            && ((BookMeta) Objects.requireNonNull(item.getItemMeta())).getPages().equals(((BookMeta) Objects.requireNonNull(tempItem.getItemMeta())).getPages()) || this.isDynamic())
                            || this.material.toString().toUpperCase().contains("BOOK") && !this.isBookMeta(player, item) || !this.material.toString().toUpperCase().contains("BOOK") || this.isItemChangeable()) {
                        return !this.vanillaControl || this.displayMeta(item);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the Material is a Skull.
     *
     * @return If the Material is a Skull.
     */
    public boolean isSkull() {
        return this.material.toString().equalsIgnoreCase("PLAYER_HEAD") || this.material.toString().equalsIgnoreCase("SKULL_ITEM");
    }

    /**
     * Sets the Skull Owner.
     *
     * @param skull - The Skull Owner to be set.
     */
    public void setSkull(final String skull) {
        this.skullOwner = skull;
    }

    /**
     * Sets the Skull Owner.
     *
     * @param player - The Player to be used for placeholders.
     */
    private void setSkull(final Player player) {
        if (this.skullOwner != null) {
            this.tempMeta = ItemHandler.setSkullOwner(this.tempMeta, player, StringUtils.translateLayout(this.skullOwner, player));
        } else if (this.skullTexture != null && !this.headDatabase) {
            this.tempMeta = ItemHandler.setSkullTexture(player, this.tempMeta, StringUtils.toTextureUUID(player, this.configName, this.skullTexture));
        }
    }

    /**
     * Checks if the display meta is similar.
     *
     * @param item - The ItemStack being checked.
     * @return If the display meta is similar.
     */
    private boolean displayMeta(final ItemStack item) {
        if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName() && this.tempMeta != null && this.tempMeta.hasDisplayName() && item.getItemMeta().hasLore() && this.tempMeta.hasLore()) {
            return item.getItemMeta().getDisplayName().equalsIgnoreCase(this.tempMeta.getDisplayName()) && Objects.requireNonNull(item.getItemMeta().getLore()).toString().equalsIgnoreCase(Objects.requireNonNull(this.tempMeta.getLore()).toString());
        } else if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName() && this.tempMeta != null && this.tempMeta.hasDisplayName() && !item.getItemMeta().hasLore() && !this.tempMeta.hasLore()) {
            return item.getItemMeta().getDisplayName().equalsIgnoreCase(this.tempMeta.getDisplayName());
        } else if (item.hasItemMeta() && !Objects.requireNonNull(item.getItemMeta()).hasDisplayName() && this.tempMeta != null && !this.tempMeta.hasDisplayName() && item.getItemMeta().hasLore() && this.tempMeta.hasLore()) {
            return Objects.requireNonNull(item.getItemMeta().getLore()).toString().equalsIgnoreCase(Objects.requireNonNull(this.tempMeta.getLore()).toString());
        } else return this.tempMeta == null;
    }
//  ================================================================================================================================================================================= //

//  ================================================================ //
//                      ~ Player Item Updater ~                      //
//  Method(s) update the ItemMap item for player specific variables. //
//  ================================================================ //

    /**
     * Checks if the ItemStack Enchantments are similar.
     *
     * @param player - The player being referenced.
     * @param item   - The ItemStack being checked.
     * @return If the ItemStack Enchantments are similar.
     */
    private boolean isEnchantSimilar(final Player player, final ItemStack item) {
        final ItemMeta itemMeta = item.getItemMeta();
        if (player != null && itemMeta != null && itemMeta.hasEnchants()) {
            final ItemStack checkItem = new ItemStack(item.getType());
            final Map<String, Integer> enchantList = ItemUtilities.getUtilities().getStatistics(player).getEnchantments(this);
            if (enchantList != null && !enchantList.isEmpty()) {
                for (final Entry<String, Integer> enchantments : enchantList.entrySet()) {
                    if (enchantments.getKey() == null && ItemJoin.getCore().getDependencies().tokenEnchantEnabled() && TokenEnchantAPI.getInstance().getEnchantment(enchantments.getKey()) != null) {
                        TokenEnchantAPI.getInstance().enchant(null, checkItem, enchantments.getKey(), enchantments.getValue(), true, 0, true);
                    } else {
                        checkItem.addUnsafeEnchantment(Objects.requireNonNull(ItemHandler.getEnchantByName(enchantments.getKey())), enchantments.getValue());
                    }
                }
            }
            return (this.glowing || (checkItem.getItemMeta() != null && itemMeta.getEnchants().equals(checkItem.getItemMeta().getEnchants())));
        }
        return true;
    }

    /**
     * Checks if the Book Meta is similar.
     *
     * @param player - The Player being referenced.
     * @param item   - The ItemStack being checked.
     * @return If the Book Meta is similar.
     */
    private boolean isBookMeta(final Player player, final ItemStack item) {
        try {
            final boolean bookMeta = ((BookMeta) Objects.requireNonNull(item.getItemMeta())).hasPages();
            if (bookMeta && this.material.toString().toUpperCase().contains("BOOK")) {
                this.tempItem = this.setJSONBookPages(player, this.tempItem, this.bookPages);
            }
            return bookMeta;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the stack size is similar.
     *
     * @param item - The ItemStack being checked.
     * @return If the stack size is similar.
     */
    public boolean isCountSimilar(final Player player, final ItemStack item) {
        return item.getAmount() == this.getCount(player) || !ItemJoin.getCore().getConfig("items.yml").getBoolean("items-RestrictCount") || this.isItemChangeable();
    }

    /**
     * Checks if the Material is a Dynamic Material.
     *
     * @param item - The ItemStack being checked.
     * @return If the Material is a Dynamic Material.
     */
    private boolean isMaterial(final ItemStack item) {
        for (String material : this.dynamicMaterials) {
            material = ItemHandler.cutDelay(material);
            String dataValue = null;
            if (material.contains(":")) {
                String[] parts = material.split(":");
                dataValue = parts[1];
            }
            if (item.getType() == ItemHandler.getMaterial(material, dataValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the Players inventory for the ItemMap.
     *
     * @param player - The Player being checked.
     * @return If the Player already has the ItemMap.
     */
    public boolean hasItem(final Player player, boolean ignoreCount) {
        int arbitrary = ItemUtilities.getUtilities().getArbitrary(this);
        int count = 0;
        for (ItemStack inPlayerInventory : player.getInventory().getContents()) {
            if (this.isSimilar(player, inPlayerInventory) && (ignoreCount || this.isCountSimilar(player, inPlayerInventory))) {
                if (this.getSlot().equalsIgnoreCase("ARBITRARY")) {
                    count++;
                    if (arbitrary == count) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        for (ItemStack equipInventory : Objects.requireNonNull(player.getEquipment()).getArmorContents()) {
            if (this.isSimilar(player, equipInventory) && (ignoreCount || this.isCountSimilar(player, equipInventory))) {
                return true;
            }
        }
        if (ServerUtils.hasSpecificUpdate("1_9")
                && this.isSimilar(player, player.getInventory().getItemInOffHand())
                && (ignoreCount || this.isCountSimilar(player, player.getInventory().getItemInOffHand()))) {
            return true;
        }
        if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
            for (ItemStack craftInventory : player.getOpenInventory().getTopInventory()) {
                if (this.isSimilar(player, craftInventory) && (ignoreCount || this.isCountSimilar(player, craftInventory))) {
                    return true;
                }
            }
        }
        return player.getItemOnCursor().getType() != Material.AIR && this.isSimilar(player, player.getItemOnCursor()) && (ignoreCount || this.isCountSimilar(player, player.getItemOnCursor()));
    }

    /**
     * Gets the ItemStack that is specific to the Player.
     *
     * @param player - The Player to have their ItemStack generated.
     * @return The Player specific ItemStack.
     */
    public ItemStack getItem(final Player player) {
        return updateItem(player).getTempItem();
    }

    /**
     * Updates the ItemStack to be specific to the Player.
     *
     * @param player - The Player to have their ItemStack generated.
     * @return The Player specific ItemStack.
     */
    public ItemMap updateItem(final Player player) {
        if (this.tempItem != null) {
            this.setSkullDatabase();
            this.setUnbreaking();
            this.setEnchantments(player);
            this.setGlowing();
            this.setMapImage();
            this.tempItem = this.setJSONBookPages(player, this.tempItem, this.bookPages);
            this.setNBTData();
            this.tempMeta = this.tempItem.getItemMeta();
        }
        if (tempMeta != null) {
            this.setCustomName(player);
            this.setCustomLore(player);
            this.setSkull(player);
            this.setDurability();
            this.setData();
            this.setModelData(player);
            this.setPotionEffects();
            this.setBanners();
            this.setArmorTrim();
            this.setFireworks();
            this.setFireChargeColor();
            this.setDye(player);
            this.setBookInfo(player);
            this.setAttributes();
            this.setAttributeFlags();
            this.setEnchantmentsFlags();
            this.setFlags();
            this.setContents(player);
            this.tempItem.setItemMeta(this.tempMeta);
            this.tempItem.setAmount(this.getCount(player));
            this.setTempItem(LegacyAPI.setAttributes(this.tempItem, this.configName, this.attributes));
        }
        return this;
    }

    /**
     * Sets the item to glow.
     */
    private void setGlowing() {
        if (this.glowing) {
            ItemHandler.setGlowing(this.tempItem);
        }
    }

    /**
     * Sets the Skull Database Textures.
     */
    private void setSkullDatabase() {
        if (this.headDatabase && this.skullTexture != null) {
            HeadDatabaseAPI api = new HeadDatabaseAPI();
            ItemStack sk = api.getItemHead(this.skullTexture);
            this.tempItem = (sk != null ? sk : this.tempItem.clone());
        }
    }

    /**
     * Sets the ItemStack as Unbreakable.
     */
    private void setUnbreaking() {
        if (this.isUnbreakable() || this.hideDurability) {
            try {
                Class<?> craftItemStack = ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack");
                Object nms = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, this.tempItem);
                Class<?> itemClass = ReflectionUtils.getMinecraftClass("ItemStack");
                Object tag = itemClass.getMethod(MinecraftMethod.getTag.getMethod(itemClass)).invoke(nms);
                if (tag == null) {
                    tag = ReflectionUtils.getMinecraftClass("NBTTagCompound").getConstructor().newInstance();
                }
                tag.getClass().getMethod(MinecraftMethod.setInt.getMethod(tag, String.class, int.class), String.class, int.class).invoke(tag, "Unbreakable", 1);
                nms.getClass().getMethod(MinecraftMethod.setTag.getMethod(nms, tag.getClass()), tag.getClass()).invoke(nms, tag);
                this.tempItem = (ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms);
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
            }
        }
    }

    /**
     * Sets the armor value to the items attributes.
     */
    private void setAttributes() {
        if (ServerUtils.hasSpecificUpdate("1_13") && this.attributes != null && !this.attributes.isEmpty()) {
            try {
                for (String attrib : this.attributes.keySet()) {
                    Attribute attribute = Attribute.valueOf(attrib.toUpperCase());
                    double value = this.attributes.get(attrib);
                    EquipmentSlot slot;
                    if (ItemHandler.getDesignatedSlot(this.material).equalsIgnoreCase("noslot")) {
                        slot = EquipmentSlot.HAND;
                    } else {
                        slot = EquipmentSlot.valueOf(ItemHandler.getDesignatedSlot(this.material).toUpperCase());
                    }
                    AttributeModifier modifier = new AttributeModifier(UUID.nameUUIDFromBytes((this.configName + attrib).getBytes()), attrib.toLowerCase().replace("_", "."), value, AttributeModifier.Operation.ADD_NUMBER, slot);
                    if (this.tempMeta.getAttributeModifiers() == null || !this.tempMeta.getAttributeModifiers().containsValue(modifier)) {
                        this.tempMeta.addAttributeModifier(attribute, modifier);
                    }
                }
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
            }
        }
    }

    /**
     * Sets the Map Image to be displayed.
     */
    private void setMapImage() {
        if (this.customMapImage != null || this.mapId != -1) {
            if (ServerUtils.hasSpecificUpdate("1_13")) {
                MapMeta mapmeta = (MapMeta) this.tempItem.getItemMeta();
                try {
                    if (mapmeta != null && this.mapView != null) {
                        try {
                            mapmeta.setMapView(this.mapView);
                        } catch (NullPointerException e) {
                            ServerUtils.sendDebugTrace(e);
                            ServerUtils.logWarn("{ItemMap} There was an issue rendering the custom map image for " + this.configName + ".");
                        }
                    }
                } catch (NoSuchMethodError e) {
                    LegacyAPI.setMapID(mapmeta, this.mapId);
                }
                this.tempItem.setItemMeta(mapmeta);
            } else {
                LegacyAPI.setDurability(this.tempItem, this.mapId);
            }
        }
    }

    /**
     * Sets the JSON Book Pages to the ItemStack.
     *
     * @param player - The Player being used for placeholders.
     * @param item   - The ItemStack to be updated.
     * @param pages  - The book pages to be set.
     * @return The updated ItemStack.
     */
    public ItemStack setJSONBookPages(final Player player, final ItemStack item, final List<String> pages) {
        if (item.getType().toString().equalsIgnoreCase("WRITTEN_BOOK") && pages != null && !pages.isEmpty()) {
            List<String> copyPages = new ArrayList<>(pages);
            copyPages.set(0, ItemHandler.cutDelay(copyPages.get(0)));
            Object localePages = null;
            try {
                localePages = ReflectionUtils.getMinecraftClass("NBTTagList").getConstructor().newInstance();
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
            }
            if (ServerUtils.hasSpecificUpdate("1_15")) {
                return this.set1_15JSONPages(player, item, localePages, copyPages);
            } else if (ServerUtils.hasSpecificUpdate("1_14")) {
                return this.set1_14JSONPages(player, item, localePages, copyPages);
            } else {
                return this.set1_13JSONPages(player, item, localePages, copyPages);
            }
        }
        return item;
    }

    /**
     * Sets the JSON Book Pages to the ItemStack.
     *
     * @param player      - The Player being used for placeholders.
     * @param item        - The ItemStack to be updated.
     * @param localePages - The NBTTagList of Pages.
     * @param pages       - The book pages to be set.
     * @return The updated ItemStack.
     * @warn Method ONLY USED for Server Version 1.13
     */
    private ItemStack set1_13JSONPages(final Player player, final ItemStack item, final Object localePages, final List<String> pages) {
        for (String textComponent : pages) {
            try {
                textComponent = StringUtils.translateLayout(textComponent, player);
                Object TagString = ReflectionUtils.getMinecraftClass("NBTTagString").getConstructor(String.class).newInstance(textComponent);
                Class<?> baseClass = ReflectionUtils.getMinecraftClass("NBTBase");
                localePages.getClass().getMethod(MinecraftMethod.add.getMethod(localePages, baseClass), baseClass).invoke(localePages, TagString);
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
            }
        }
        try {
            return this.invokePages(item, localePages);
        } catch (Exception e) {
            ServerUtils.sendDebugTrace(e);
        }
        return item;
    }

    /**
     * Sets the JSON Book Pages to the ItemStack.
     *
     * @param player      - The Player being used for placeholders.
     * @param item        - The ItemStack to be updated.
     * @param localePages - The NBTTagList of Pages.
     * @param pages       - The book pages to be set.
     * @return The updated ItemStack.
     * @warn Method ONLY USED for Server Version 1.14
     */
    private ItemStack set1_14JSONPages(final Player player, final ItemStack item, final Object localePages, final List<String> pages) {
        for (int i = pages.size() - 1; i >= 0; i--) {
            String textComponent = pages.get(i);
            try {
                textComponent = StringUtils.translateLayout(textComponent, player);
                Object TagString = ReflectionUtils.getMinecraftClass("NBTTagString").getConstructor(String.class).newInstance(textComponent);
                Class<?> baseClass = ReflectionUtils.getMinecraftClass("NBTBase");
                localePages.getClass().getMethod(MinecraftMethod.add.getMethod(localePages, int.class, baseClass), int.class, baseClass).invoke(localePages, 0, TagString);
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
            }
        }
        try {
            return this.invokePages(item, localePages);
        } catch (Exception e) {
            ServerUtils.sendDebugTrace(e);
        }
        return item;
    }

    /**
     * Sets the JSON Book Pages to the ItemStack.
     *
     * @param player      - The Player being used for placeholders.
     * @param item        - The ItemStack to be updated.
     * @param localePages - The NBTTagList of Pages.
     * @param pages       - The book pages to be set.
     * @return The updated ItemStack.
     * @warn Method ONLY USED for Server Version 1.15
     */
    private ItemStack set1_15JSONPages(final Player player, final ItemStack item, final Object localePages, final List<String> pages) {
        for (int i = pages.size() - 1; i >= 0; i--) {
            String textComponent = pages.get(i);
            try {
                textComponent = StringUtils.translateLayout(textComponent, player);
                Class<?> stringClass = ReflectionUtils.getMinecraftClass("NBTTagString");
                Class<?> baseClass = ReflectionUtils.getMinecraftClass("NBTBase");
                Object TagString = stringClass.getMethod(MinecraftMethod.getPage.getMethod(stringClass), String.class).invoke(null, textComponent);
                localePages.getClass().getMethod(MinecraftMethod.add.getMethod(localePages, int.class, baseClass), int.class, baseClass).invoke(localePages, 0, TagString);
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
            }
        }
        try {
            return this.invokePages(item, localePages);
        } catch (Exception e) {
            ServerUtils.sendDebugTrace(e);
        }
        return item;
    }

    /**
     * Sets the JSON Book Pages to the ItemStack.
     *
     * @param item  - The ItemStack to be updated.
     * @param pages - The book pages to be set.
     * @return The updated ItemStack.
     * @throws Exception An exception.
     */
    private ItemStack invokePages(final ItemStack item, final Object pages) throws Exception {
        Class<?> craftItemStack = ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack");
        Class<?> itemClass = ReflectionUtils.getMinecraftClass("ItemStack");
        Class<?> baseClass = ReflectionUtils.getMinecraftClass("NBTBase");
        Object nms = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
        Object tag = itemClass.getMethod(MinecraftMethod.getTag.getMethod(itemClass)).invoke(nms);
        if (tag == null) {
            tag = ReflectionUtils.getMinecraftClass("NBTTagCompound").getConstructor().newInstance();
        }
        tag.getClass().getMethod(MinecraftMethod.set.getMethod(tag, String.class, baseClass), String.class, baseClass).invoke(tag, "pages", pages);
        nms.getClass().getMethod(MinecraftMethod.setTag.getMethod(nms, tag.getClass()), tag.getClass()).invoke(nms, tag);
        return ((ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms));
    }

    /**
     * Sets the NBTData, making the ItemStack plugin specific.
     */
    private void setNBTData() {
        if (ItemJoin.getCore().getData().dataTagsEnabled() && !this.isVanilla() && !this.isVanillaControl() && !this.isVanillaStatus()) {
            try {
                Class<?> itemClass = ReflectionUtils.getMinecraftClass("ItemStack");
                Object nms = ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, this.tempItem);
                Object cacheTag = itemClass.getMethod(MinecraftMethod.getTag.getMethod(itemClass)).invoke(nms);
                if (cacheTag != null) {
                    cacheTag.getClass().getMethod(MinecraftMethod.setString.getMethod(cacheTag, String.class, String.class), String.class, String.class).invoke(cacheTag, "ItemJoin Name", this.getConfigName());
                    if (this.nbtProperty != null && !this.nbtProperty.isEmpty()) {
                        for (Object tag : this.nbtProperty.keySet()) {
                            String castTag = (String) tag;
                            String castProperty = (String) this.nbtProperty.get(tag);
                            try {
                                cacheTag.getClass().getMethod(MinecraftMethod.setString.getMethod(cacheTag, String.class, String.class), String.class, String.class).invoke(cacheTag, castTag, castProperty);
                            } catch (Exception e) {
                                cacheTag.getClass().getMethod(MinecraftMethod.set.getMethod(cacheTag, String.class, ReflectionUtils.getMinecraftClass("NBTBase")), String.class, ReflectionUtils.getMinecraftClass("NBTBase")).invoke(cacheTag, castTag, castProperty);
                            }
                        }
                    }
                } else {
                    nms.getClass().getMethod(MinecraftMethod.setTag.getMethod(nms, this.newNBTTag.getClass()), this.newNBTTag.getClass()).invoke(nms, this.newNBTTag);
                    if (this.nbtProperties != null && !this.nbtProperties.isEmpty()) {
                        for (Object tag : this.nbtProperties) {
                            nms.getClass().getMethod(MinecraftMethod.setTag.getMethod(nms, tag.getClass()), tag.getClass()).invoke(nms, tag);
                        }
                    }
                }
                this.tempItem = (ItemStack) ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asCraftMirror", nms.getClass()).invoke(null, nms);
            } catch (Exception e) {
                ServerUtils.logSevere("{ItemMap} An error has occurred when setting NBTData to an item.");
                ServerUtils.sendDebugTrace(e);
            }
        } else if (!ItemJoin.getCore().getData().dataTagsEnabled() && this.legacySecret != null && !this.legacySecret.isEmpty()) {
            StringUtils.colorEncode(this.tempItem, this.legacySecret);
        }
    }

    /**
     * Sets the ItemStack Durability.
     */
    private void setDurability() {
        if (this.durability != null && (this.data == null || this.data == 0)) {
            if (ServerUtils.hasSpecificUpdate("1_13")) {
                ((org.bukkit.inventory.meta.Damageable) this.tempMeta).setDamage(this.durability);
            } else {
                LegacyAPI.setDurability(this.tempItem, this.durability);
            }
        }
    }

    /**
     * Sets the ItemStack Data.
     */
    private void setData() {
        if (this.data != null) {
            if (ServerUtils.hasSpecificUpdate("1_13")) {
                ((org.bukkit.inventory.meta.Damageable) this.tempMeta).setDamage(this.data);
            } else {
                LegacyAPI.setDurability(this.tempItem, Short.parseShort(this.data + ""));
            }
        }
    }

    /**
     * Sets the ItemStack PotionEffects
     */
    private void setPotionEffects() {
        if (this.effect != null && !this.effect.isEmpty() && !this.customConsumable) {
            for (PotionEffect potion : this.effect) {
                if (ServerUtils.hasPreciseUpdate("1_20_3")) {
                    ((PotionMeta) tempMeta).setBasePotionType(PotionType.WATER);
                } else if (ServerUtils.hasSpecificUpdate("1_9")) {
                    LegacyAPI.setPotionData(((PotionMeta) tempMeta), PotionType.WATER);
                }
                ((PotionMeta) this.tempMeta).addCustomEffect(potion, true);
            }
        } else if (ServerUtils.hasSpecificUpdate("1_9") && (this.getMaterial().toString().equalsIgnoreCase("POTION") || this.getMaterial().toString().equalsIgnoreCase("SPLASH_POTION") || this.getMaterial().toString().equalsIgnoreCase("LINGERING_POTION"))) {
            if (ServerUtils.hasPreciseUpdate("1_20_3")) {
                ((PotionMeta) tempMeta).setBasePotionType(PotionType.WATER);
            } else if (ServerUtils.hasSpecificUpdate("1_9")) {
                LegacyAPI.setPotionData(((PotionMeta) tempMeta), PotionType.WATER);
            }
        }
    }

    /**
     * Sets the ItemStack Banner Patterns
     */
    private void setBanners() {
        if (this.bannerPatterns != null && !this.bannerPatterns.isEmpty()) {
            ((BannerMeta) this.tempMeta).setPatterns(this.bannerPatterns);
        }
    }

    /**
     * Sets the ItemStack Armor Trim Pattern
     */
    private void setArmorTrim() {
        if (this.trimPattern != null && !this.trimPattern.isEmpty()) {
            final Map.Entry<String, String> entry = this.trimPattern.entrySet().iterator().next();
            final org.bukkit.inventory.meta.trim.TrimMaterial trimMaterial = ItemHandler.getTrimMaterial(entry.getKey());
            final org.bukkit.inventory.meta.trim.TrimPattern trimPattern = ItemHandler.getTrimPattern(entry.getValue());
            if (trimMaterial != null && trimPattern != null) {
                ((ArmorMeta) this.tempMeta).setTrim(new org.bukkit.inventory.meta.trim.ArmorTrim(trimMaterial, trimPattern));
            }
        }
    }

    /**
     * Sets the itemStack FireworkMeta.
     */
    private void setFireworks() {
        if (this.firework != null) {
            ((FireworkMeta) this.tempMeta).clearEffects();
            ((FireworkMeta) this.tempMeta).addEffect(this.firework);
        }

        if (this.power != null && this.power != 0) {
            ((FireworkMeta) this.tempMeta).setPower(this.power);
        }
    }

    /**
     * Sets the Firework Charge Color.
     */
    private void setFireChargeColor() {
        if (this.chargeColor != null) {
            ((FireworkEffectMeta) this.tempMeta).setEffect(FireworkEffect.builder().withColor(this.chargeColor.getColor()).build());
        }
    }

    /**
     * Sets the ItemStack DyeColor.
     */
    private void setDye(final Player player) {
        if (this.leatherColor != null) {
            String newColor;
            if (this.leatherColor.startsWith("%")) {
                newColor = StringUtils.translateLayout(this.leatherColor, player);
                if (newColor.startsWith("#")) {
                    ((LeatherArmorMeta) this.tempMeta).setColor(StringUtils.getColorFromHexColor(newColor));
                } else {
                    try {
                        ((LeatherArmorMeta) this.tempMeta).setColor(DyeColor.valueOf(newColor).getFireworkColor());
                    } catch (Exception e) {
                        try {
                            ((LeatherArmorMeta) this.tempMeta).setColor(StringUtils.getColorFromHexColor(newColor));
                        } catch (Exception e2) {
                            ServerUtils.logSevere("{ItemMap} Unable to find the defined color: " + newColor + " for the item " + this.configName);
                            ServerUtils.sendSevereTrace(e2);
                        }
                    }
                }
            } else {
                ((LeatherArmorMeta) this.tempMeta).setColor(DyeColor.valueOf(this.leatherColor).getFireworkColor());
            }
        } else if (this.leatherHex != null) {
            ((LeatherArmorMeta) this.tempMeta).setColor(StringUtils.getColorFromHexColor(this.leatherHex));
        }
    }

    /**
     * Sets the ItemStack Book Information.
     *
     * @param player - The Player to be used for placeholders.
     */
    private void setBookInfo(final Player player) {
        if (this.author != null) {
            this.author = StringUtils.translateLayout(this.author, player);
            ((BookMeta) this.tempMeta).setAuthor(this.author);
        }

        if (this.title != null) {
            this.title = StringUtils.translateLayout(this.title, player);
            ((BookMeta) this.tempMeta).setTitle(this.title);
        }

        if (this.generation != null && ServerUtils.hasSpecificUpdate("1_10")) {
            ((BookMeta) this.tempMeta).setGeneration((Generation) this.generation);
        }
    }

    /**
     * Sets the Attributes to the Temporary ItemMeta.
     */
    private void setAttributeFlags() {
        if (this.hideAttributes) {
            this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        }
    }

    /**
     * Sets the Enchantments to the Temporary ItemMeta.
     */
    private void setEnchantmentsFlags() {
        if (this.hideEnchantments) {
            this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
    }

    /**
     * Sets the Flags to the Temporary ItemMeta.
     */
    private void setFlags() {
        if (this.hideFlags || this.glowing) {
            this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        if (this.hideFlags) {
            this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
            this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_DESTROYS);
            this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_PLACED_ON);
            this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_POTION_EFFECTS);
            this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
            if (ServerUtils.hasSpecificUpdate("1_20")) {
                this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ARMOR_TRIM);
            }
            if (ServerUtils.hasSpecificUpdate("1_17")) {
                this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_DYE);
            }
        }
    }
//  =========================================================================================================================================================== //

    /**
     * Checks if the World is an Enabled World.
     *
     * @param world - The world to be checked.
     * @return If the World is an Enabled World.
     */
    public boolean inWorld(final World world) {
        if (this.enabledWorlds == null && this.disabledWorlds == null) {
            return true;
        }
        if (this.enabledWorlds != null) {
            synchronized ("IJ_MAP") {
                for (String enabledWorld : this.enabledWorlds) {
                    if (enabledWorld.equalsIgnoreCase(world.getName())
                            || enabledWorld.equalsIgnoreCase("ALL")
                            || enabledWorld.equalsIgnoreCase("GLOBAL")
                            || (enabledWorld.contains("*") && world.getName().toUpperCase().startsWith(enabledWorld.split("\\*")[0].toUpperCase()))) {
                        return !this.isDisabled(world);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the World is a Disabled World.
     *
     * @param world - The world to be checked.
     * @return If the World is a Disabled World.
     */
    public boolean isDisabled(final World world) {
        boolean isDisabled = false;
        for (String disabledWorld : this.disabledWorlds) {
            if (disabledWorld.equalsIgnoreCase(world.getName())
                    || disabledWorld.equalsIgnoreCase("ALL")
                    || disabledWorld.equalsIgnoreCase("GLOBAL") ||
                    (disabledWorld.contains("*") && world.getName().toUpperCase().startsWith(disabledWorld.split("\\*")[0].toUpperCase()))) {
                isDisabled = true;
            }
        }
        return isDisabled;
    }

    /**
     * Gets the condition message for the ItemMap.
     *
     * @param conditions - The condition list to be fetched.
     * @return The fetched condition message.
     */
    private List<String> getConditions(final String conditions) {
        if (conditions.equalsIgnoreCase("disposable-conditions")) {
            return this.disposableConditions;
        } else if (conditions.equalsIgnoreCase("trigger-conditions")) {
            return this.triggerConditions;
        } else {
            return this.commandConditions.get(conditions.replace("-conditions", ""));
        }
    }

    /**
     * Gets the condition message for the ItemMap.
     *
     * @param conditions - The condition list to be fetched.
     * @return The fetched condition message.
     */
    private String getConditionMessage(final String conditions) {
        if (conditions.replace("conditions", "fail-message").equalsIgnoreCase("disposable-fail-message")) {
            return this.disposableMessage;
        } else if (conditions.replace("conditions", "fail-message").equalsIgnoreCase("trigger-fail-message")) {
            return this.triggerMessage;
        } else {
            return this.commandMessages.get(conditions.replace("-conditions", ""));
        }
    }

    /**
     * Checks if the condition is met for the ItemMap.
     *
     * @param player     - The Player being referenced.
     * @param conditions - The condition list to be fetched.
     * @param doCheck    - If the conditions should be checked.
     * @param silent     - If the condition message should be hidden.
     * @return If the condition was successfully met.
     */
    public boolean conditionMet(final Player player, final String conditions, final boolean doCheck, final boolean silent) {
        if (this.getConditions(conditions) != null && !this.getConditions(conditions).isEmpty()) {
            if (doCheck) {
                for (String condition : this.getConditions(conditions)) {
                    String[] parts = (condition != null ? condition.split(":") : null);
                    if (parts != null && parts.length == 3) {
                        String value1 = (parts[0] != null && !StringUtils.isInt(parts[0]) ? StringUtils.translateLayout(parts[0], player) : parts[0]);
                        String operand = parts[1];
                        String value2 = (parts[2] != null && !StringUtils.isInt(parts[2]) ? StringUtils.translateLayout(parts[2], player) : parts[2]);
                        final boolean conditionMet = StringUtils.conditionMet(value1, operand, value2);
                        if (!conditionMet && !silent && this.getConditionMessage(conditions) != null && !this.getConditionMessage(conditions).isEmpty()) {
                            player.sendMessage(StringUtils.translateLayout(this.getConditionMessage(conditions), player));
                            ServerUtils.logDebug("{ItemMap} " + player.getName() + " has not met any of the " + conditions + "(s), for the Item: " + this.getConfigName() + ".");
                        }
                        return conditionMet;
                    } else {
                        ServerUtils.logSevere("{ItemMap} The item " + this.getConfigName() + " has a " + conditions + " defined incorrectly!");
                        ServerUtils.logWarn("{ItemMap} The condition " + condition + " is not the proper format CONDITION:OPERAND:VALUE, the item may not function properly.");
                    }
                }
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * Removes the ItemMap from the Player.
     *
     * @param player - The Player to have the item removed.
     * @param amount - The stack size of the item.
     */
    public void removeFrom(final Player player, int... amount) {
        if (amount.length == 0) {
            amount = new int[]{0};
        }
        PlayerInventory inv = player.getInventory();
        Inventory craftView = player.getOpenInventory().getTopInventory();
        ItemStack[] contents = inv.getContents();
        ItemStack[] craftingContents = player.getOpenInventory().getTopInventory().getContents();
        this.updateItem(player);
        if (amount[0] == 0) {
            if (this.isAnimated() && this.getAnimationHandler().get(player) != null
                    || this.isDynamic() && this.getAnimationHandler().get(player) != null) {
                this.localeAnimations.get(player).closeAnimation(player);
                this.localeAnimations.remove(player);
            }

            for (int k = 0; k < contents.length; k++) {
                if (this.isSimilar(player, contents[k])) {
                    inv.setItem(k, new ItemStack(Material.AIR));
                }
            }
            if (this.isSimilar(player, inv.getHelmet())) {
                inv.setHelmet(new ItemStack(Material.AIR));
            }
            if (this.isSimilar(player, inv.getChestplate())) {
                inv.setChestplate(new ItemStack(Material.AIR));
            }
            if (this.isSimilar(player, inv.getLeggings())) {
                inv.setLeggings(new ItemStack(Material.AIR));
            }
            if (this.isSimilar(player, inv.getBoots())) {
                inv.setBoots(new ItemStack(Material.AIR));
            }
            if (this.isSimilar(player, player.getItemOnCursor())) {
                player.setItemOnCursor(new ItemStack(Material.AIR));
            }
            if (ServerUtils.hasSpecificUpdate("1_9") && this.isSimilar(player, PlayerHandler.getOffHandItem(player))) {
                PlayerHandler.setOffHandItem(player, new ItemStack(Material.AIR));
            }

            if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
                for (int k = 0; k < craftingContents.length; k++) {
                    if (this.isSimilar(player, craftingContents[k])) {
                        craftView.setItem(k, new ItemStack(Material.AIR));
                    }
                }
            }
        } else {
            for (int k = 0; k < contents.length; k++) {
                if (this.isSimilar(player, contents[k])) {
                    inv.setItem(k, ItemHandler.modifyItem(inv.getItem(k), false, amount[0]));
                    return;
                }
            }
            if (this.isSimilar(player, inv.getHelmet())) {
                inv.setHelmet(ItemHandler.modifyItem(inv.getHelmet(), false, amount[0]));
            } else if (this.isSimilar(player, inv.getChestplate())) {
                inv.setChestplate(ItemHandler.modifyItem(inv.getChestplate(), false, amount[0]));
            } else if (this.isSimilar(player, inv.getLeggings())) {
                inv.setLeggings(ItemHandler.modifyItem(inv.getLeggings(), false, amount[0]));
            } else if (this.isSimilar(player, inv.getBoots())) {
                inv.setBoots(ItemHandler.modifyItem(inv.getBoots(), false, amount[0]));
            } else if (this.isSimilar(player, player.getItemOnCursor())) {
                player.setItemOnCursor(ItemHandler.modifyItem(player.getItemOnCursor(), false, amount[0]));
            } else if (ServerUtils.hasSpecificUpdate("1_9") && this.isSimilar(player, PlayerHandler.getOffHandItem(player))) {
                PlayerHandler.setOffHandItem(player, ItemHandler.modifyItem(PlayerHandler.getOffHandItem(player), false, amount[0]));
            } else if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
                for (int k = 0; k < craftingContents.length; k++) {
                    if (this.isSimilar(player, craftingContents[k])) {
                        craftView.setItem(k, ItemHandler.modifyItem(player.getOpenInventory().getItem(k), false, amount[0]));
                        return;
                    }
                }
            }
        }
    }

    /**
     * Gives the Player the ItemMap.
     *
     * @param player - The Player to be given the item.
     * @param amount - The stack size of the item.
     */
    public void giveTo(final Player player, int... amount) {
        if (amount.length == 0) {
            amount = new int[]{0};
        }
        ItemUtilities.getUtilities().setStatistics(player);
        if (this.CustomSlot != null && !this.CustomSlot.contains("%")) {
            final int customAmount = amount[0];
            if (!ServerUtils.hasSpecificUpdate("1_10")) {
                /*
                    A fix to prevent Arbitrary and addItem's from being accidentally overwritten by dedicated slots.
                    This issue only occurs in 1.8 - 1.9.4.
                 */
                SchedulerUtils.runLater(2L, () -> ItemUtilities.getUtilities().setCustomSlots(player, this, customAmount));
            } else {
                ItemUtilities.getUtilities().setCustomSlots(player, this, customAmount);
            }
        } else {
            ItemUtilities.getUtilities().setInvSlots(player, this, amount[0]);
        }
        this.setAnimations(player);
        if (this.getMultipleSlots() != null && !this.getMultipleSlots().isEmpty()) {
            if (this.getSlot().equalsIgnoreCase(this.getMultipleSlots().get(0))) {
                this.executeCommands(player, null, this.tempItem, "ON_RECEIVE", "RECEIVED", this.getSlot());
            }
        } else {
            this.executeCommands(player, null, this.tempItem, "ON_RECEIVE", "RECEIVED", this.getSlot());
        }
    }

    /**
     * Damages the current ItemMap.
     *
     * @param player - The Player to have their item damaged.
     * @param slot   - The slot to have the item damaged
     * @param damage - The Integer amount to be damaged.
     */
    public void damageItem(final Player player, final String slot, final int damage) {
        if ((!slot.startsWith("CH") && slot.startsWith("C")) || StringUtils.isInt(slot)) {
            if (StringUtils.containsIgnoreCase(slot, "CRAFTING")) {
                final int actualSlot = StringUtils.getSlotConversion(slot);
                final ItemStack item = player.getOpenInventory().getTopInventory().getItem(actualSlot);
                if (item != null && this.isSimilar(player, item)) {
                    final ItemMeta itemMeta = item.getItemMeta();
                    if (ServerUtils.hasSpecificUpdate("1_13") && itemMeta != null) {
                        int newDamage = ((org.bukkit.inventory.meta.Damageable) itemMeta).getDamage() + damage;
                        if (item.getType().getMaxDurability() > newDamage) {
                            ((org.bukkit.inventory.meta.Damageable) itemMeta).setDamage(newDamage);
                            item.setItemMeta(itemMeta);
                        } else {
                            player.getOpenInventory().getTopInventory().setItem(Integer.parseInt(slot), new ItemStack(Material.AIR));
                        }
                    } else if (itemMeta != null) {
                        final int newDurability = LegacyAPI.getDurability(item) - damage;
                        if (newDurability > 0) {
                            LegacyAPI.setDurability(item, (short) newDurability);
                        } else {
                            player.getOpenInventory().getTopInventory().setItem(Integer.parseInt(slot), new ItemStack(Material.AIR));
                        }
                    }
                    PlayerHandler.updateInventory(player, 1L);
                }
            } else {
                final ItemStack item = player.getInventory().getItem(Integer.parseInt(slot));
                if (item != null && this.isSimilar(player, item)) {
                    final ItemMeta itemMeta = item.getItemMeta();
                    if (ServerUtils.hasSpecificUpdate("1_13") && itemMeta != null) {
                        int newDamage = ((org.bukkit.inventory.meta.Damageable) itemMeta).getDamage() + damage;
                        if (item.getType().getMaxDurability() > newDamage) {
                            ((org.bukkit.inventory.meta.Damageable) itemMeta).setDamage(newDamage);
                            item.setItemMeta(itemMeta);
                        } else {
                            player.getInventory().setItem(Integer.parseInt(slot), new ItemStack(Material.AIR));
                        }
                    } else if (itemMeta != null) {
                        final int newDurability = LegacyAPI.getDurability(item) - damage;
                        if (newDurability > 0) {
                            LegacyAPI.setDurability(item, (short) newDurability);
                        } else {
                            player.getInventory().setItem(Integer.parseInt(slot), new ItemStack(Material.AIR));
                        }
                    }
                    PlayerHandler.updateInventory(player, 1L);
                }
            }
        }
    }

    /**
     * Gives the Player the ItemMap.
     *
     * @param player - The Player to be given the item.
     * @param slot   - The slot to be placed into.
     */
    public void swapItem(final Player player, final String slot) {
        ItemStack itemStack = this.getItem(player);
        if ((!slot.startsWith("CH") && slot.startsWith("C")) || StringUtils.isInt(slot)) {
            if (StringUtils.containsIgnoreCase(slot, "CRAFTING")) {
                if (StringUtils.getSlotConversion(slot) == 0) {
                    SchedulerUtils.runLater(1L, () -> {
                        if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
                            player.getOpenInventory().getTopInventory().setItem(StringUtils.getSlotConversion(slot), itemStack);
                            PlayerHandler.updateInventory(player, 1L);
                        }
                    });
                } else {
                    player.getOpenInventory().getTopInventory().setItem(StringUtils.getSlotConversion(slot), itemStack);
                }
            } else {
                player.getInventory().setItem(Integer.parseInt(slot), itemStack);
            }
        } else {
            if (Objects.requireNonNull(PlayerHandler.getMainHandItem(player)).getType() == Material.AIR) {
                PlayerHandler.setMainHandItem(player, itemStack);
            } else {
                player.getInventory().addItem(itemStack);
            }
        }
        this.setAnimations(player);
        this.executeCommands(player, null, this.tempItem, "ON_RECEIVE", "RECEIVED", slot);
    }

    /**
     * Sets the Animations of the Player.
     *
     * @param player - The Player having their animations set.
     */
    public void setAnimations(final Player player) {
        if (this.isAnimated() && this.getAnimationHandler().get(player) == null
                || isDynamic() && this.getAnimationHandler().get(player) == null) {
            ItemAnimation Animator = new ItemAnimation(this);
            Animator.openAnimation(player);
            this.localeAnimations.put(player, Animator);
        }
    }

    /**
     * Attempts to Execute the ItemCommands.
     *
     * @param player    - The Player that executed the commands.
     * @param altPlayer - that is associated with the commands.
     * @param itemCopy  - The ItemStack having their commands executed.
     * @param action    - The Action that executed the commands.
     * @param slot      - The Slot of the ItemStack.
     */
    public void executeCommands(final Player player, final Player altPlayer, final ItemStack itemCopy, final String action, final String clickType, final String slot) {
        if (this.commands != null && this.commands.length > 0 && !Menu.isOpen(player) && !this.getWarmPending(player) && this.isExecutable(player, action, clickType) && !this.onCooldown(player) && this.isPlayerChargeable(player, this.itemCost != null && !this.itemCost.isEmpty())) {
            this.warmCycle(player, altPlayer, this, this.getWarmDelay(), player.getLocation(), itemCopy, action, clickType, slot);
        }
    }

    /**
     * Starts the Warmup for the Player pending command execution.
     *
     * @param player    - The Player that executed the commands.
     * @param altPlayer - that is associated with the commands.
     * @param itemMap   - The ItemMap having their commands executed.
     * @param warmCount - The duration of the Warmup.
     * @param location  - The Location of the Warmup.
     * @param itemCopy  - The ItemStack having their commands executed.
     * @param action    - The Action that executed the commands.
     * @param slot      - The Slot of the ItemStack.
     */
    private void warmCycle(final Player player, final Player altPlayer, final ItemMap itemMap, final int warmCount, final Location location, final ItemStack itemCopy, final String action, final String clickType, final String slot) {
        if (warmCount != 0) {
            if (itemMap.warmDelay == warmCount) {
                String[] placeHolders = ItemJoin.getCore().getLang().newString();
                placeHolders[13] = warmCount + "";
                placeHolders[0] = player.getWorld().getName();
                placeHolders[3] = StringUtils.translateLayout(itemMap.getCustomName(), player);
                ItemJoin.getCore().getLang().sendLangMessage("general.warmingUp", player, placeHolders);
                itemMap.addWarmPending(player);
            }
            SchedulerUtils.runLater(20L, () -> {
                if (itemMap.warmLocation(player, location, action)) {
                    String[] placeHolders = ItemJoin.getCore().getLang().newString();
                    placeHolders[13] = warmCount + "";
                    placeHolders[0] = player.getWorld().getName();
                    placeHolders[3] = StringUtils.translateLayout(itemMap.getCustomName(), player);
                    ItemJoin.getCore().getLang().sendLangMessage("general.warmingTime", player, placeHolders);
                    itemMap.warmCycle(player, altPlayer, itemMap, (warmCount - 1), location, itemCopy, action, clickType, slot);
                } else {
                    itemMap.delWarmPending(player);
                    String[] placeHolders = ItemJoin.getCore().getLang().newString();
                    placeHolders[13] = warmCount + "";
                    placeHolders[0] = player.getWorld().getName();
                    placeHolders[3] = StringUtils.translateLayout(itemMap.getCustomName(), player);
                    ItemJoin.getCore().getLang().sendLangMessage("general.warmingHalted", player, placeHolders);
                }
            });
        } else {
            long delay = 0;
            if (itemMap.warmDelay != 0) {
                delay = 20L;
            }
            SchedulerUtils.runLater(delay, () -> {
                if ((!player.isDead() || action.equalsIgnoreCase("ON_DEATH")) && player.isOnline()) {
                    if (this.isExecuted(player, altPlayer, action, clickType, slot)) {
                        if (itemMap.itemCost == null || itemMap.itemCost.isEmpty()) {
                            itemMap.withdrawBalance(player);
                        } else {
                            itemMap.withdrawItemCost(player);
                        }
                        itemMap.playSound(player);
                        itemMap.playParticle(player);
                        itemMap.removeDisposable(player, itemMap, itemCopy, false);
                        itemMap.addPlayerOnCooldown(player);
                    }
                } else {
                    String[] placeHolders = ItemJoin.getCore().getLang().newString();
                    placeHolders[13] = warmCount + "";
                    placeHolders[0] = player.getWorld().getName();
                    placeHolders[3] = StringUtils.translateLayout(itemMap.getCustomName(), player);
                    ItemJoin.getCore().getLang().sendLangMessage("general.warmingHalted", player, placeHolders);
                }
                if (itemMap.warmDelay != 0) {
                    itemMap.delWarmPending(player);
                }
            });
        }
    }

    /**
     * Checks if the Player is still at the original Warmup Location.
     *
     * @param player   - The Player Warming Up.
     * @param location - The Location of the Warmup.
     * @param action   - The action that triggered the Warmup.
     * @return If the Player is still inside the Warmup Location.
     */
    private boolean warmLocation(final Player player, final Location location, final String action) {
        return Objects.equals(player.getLocation().getWorld(), location.getWorld()) && !(player.getLocation().distance(location) >= 1) && (action.equalsIgnoreCase("ON_DEATH") || !player.isDead());
    }

    /**
     * Checks if the ItemCommands are executable.
     *
     * @param player - The Player that executed the command.
     * @param action - The Action that executed the command.
     * @return If the ItemCommands are executable.
     */
    private boolean isExecutable(final Player player, final String action, final String clickType) {
        boolean playerSuccess = false;
        boolean silentLimit = false;
        final ItemCommand[] itemCommands = this.commands;
        for (ItemCommand itemCommand : itemCommands) {
            if (!playerSuccess) {
                playerSuccess = itemCommand.canExecute(action, clickType);
                boolean conditionLimited = !this.conditionMet(player, itemCommand.getAction().config + "-conditions", playerSuccess, silentLimit);
                if (conditionLimited) {
                    silentLimit = true;
                    playerSuccess = false;
                }
            } else {
                break;
            }
        }
        return playerSuccess;
    }

    /**
     * Randomly Selected an ItemCommand Entry for a Single Command.
     *
     * @param itemCommands - The ItemCommands to have an entry randomly selected.
     * @param player       - The Player having their commands randomly selected.
     * @param altPlayer    - that is associated with the commands.
     * @param action       - The Action that executed the commands.
     * @param slot         - The Slot of the ItemStack.
     * @return If it was successful.
     */
    private boolean getRandomMap(final ArrayList<ItemCommand> randomCommands, final ItemCommand[] itemCommands, final Player player, final Player altPlayer, final String action, final String clickType, final String slot) {
        ItemCommand dedicatedMap = (ItemCommand) StringUtils.randomEntry(randomCommands);
        if (player != null && action != null && clickType != null && slot != null && itemCommands != null && !dedicatedMap.execute(player, altPlayer, action, clickType, slot, this)) {
            return this.getRandomMap(randomCommands, itemCommands, player, altPlayer, action, clickType, slot);
        }
        return true;
    }

    /**
     * Randomly Selected an ItemCommand Entry for All Commands.
     *
     * @param itemCommands - The ItemCommands to have an entry randomly selected.
     * @param player       - The Player having their commands randomly selected.
     * @param altPlayer    - that is associated with the commands.
     * @param action       - The Action that executed the commands.
     * @param slot         - The Slot of the ItemStack.
     * @return If it was successful.
     */
    private boolean getRandomAll(final ArrayList<ItemCommand> randomCommands, final ItemCommand[] itemCommands, final Player player, final Player altPlayer, final String action, final String clickType, final String slot) {
        ItemCommand dedicatedMap = (ItemCommand) StringUtils.randomEntry(randomCommands);
        if (player != null && action != null && slot != null && itemCommands != null && !dedicatedMap.execute(player, altPlayer, action, clickType, slot, this)) {
            randomCommands.remove(dedicatedMap);
            return this.getRandomAll(randomCommands, itemCommands, player, altPlayer, action, clickType, slot);
        }
        randomCommands.remove(dedicatedMap);
        if (player != null && action != null && slot != null && itemCommands != null && !randomCommands.isEmpty()) {
            this.getRandomAll(randomCommands, itemCommands, player, altPlayer, action, clickType, slot);
        }
        return true;
    }

    /**
     * Randomly Selected an ItemCommand Entry.
     *
     * @param itemCommands - The ItemCommands to have an entry randomly selected.
     * @return The Randomly selected ItemCommand.
     */
    private String getRandomList(final ItemCommand[] itemCommands) {
        if (this.sequence == CommandSequence.RANDOM_LIST) {
            ArrayList<String> listIndent = new ArrayList<>();
            for (ItemCommand itemCommand : itemCommands) {
                if (!listIndent.contains("+" + itemCommand.getSection() + "+")) {
                    listIndent.add("+" + itemCommand.getSection() + "+");
                }
            }
            return (String) StringUtils.randomEntry(listIndent);
        }
        return null;
    }

    /**
     * Executes the ItemCommands.
     *
     * @param player    - The Player executing the commands.
     * @param altPlayer - that is associated with the commands.
     * @param action    - The Action executing the commands.
     * @param clickType - The click type of the commands.
     * @param slot      - The slot of the ItemStack.
     * @return If the Command(s) were successfully executed.
     */
    private boolean isExecuted(final Player player, final Player altPlayer, final String action, final String clickType, final String slot) {
        boolean playerSuccess = false;
        ItemCommand[] itemCommands = this.commands;
        String chosenIndent = this.getRandomList(itemCommands);
        ArrayList<ItemCommand> randomCommands = new ArrayList<>();
        if (!this.subjectRemoval) {
            for (ItemCommand itemCommand : itemCommands) {
                if (this.sequence == CommandSequence.RANDOM || this.sequence == CommandSequence.RANDOM_SINGLE) {
                    randomCommands.add(itemCommand);
                } else if (this.sequence == CommandSequence.RANDOM_LIST) {
                    if (chosenIndent != null && itemCommand.getSection() != null && itemCommand.getSection().equalsIgnoreCase(chosenIndent.replace("+", ""))) {
                        if (!playerSuccess) {
                            playerSuccess = itemCommand.execute(player, altPlayer, action, clickType, slot, this);
                        } else {
                            itemCommand.execute(player, altPlayer, action, clickType, slot, this);
                        }
                    }
                } else {
                    synchronized (ItemCommand.class) {
                        if (!playerSuccess) {
                            playerSuccess = itemCommand.execute(player, altPlayer, action, clickType, slot, this);
                        } else {
                            itemCommand.execute(player, altPlayer, action, clickType, slot, this);
                        }
                    }
                }
            }
            if (this.sequence == CommandSequence.RANDOM) {
                playerSuccess = this.getRandomAll(randomCommands, itemCommands, player, altPlayer, action, clickType, slot);
            } else if (this.sequence == CommandSequence.RANDOM_SINGLE) {
                playerSuccess = this.getRandomMap(randomCommands, itemCommands, player, altPlayer, action, clickType, slot);
            }
        }
        return playerSuccess;
    }

    /**
     * Checks if the Player has the proper balance/cost, so they can execute the command.
     *
     * @param player       - The Player being charged.
     * @param materialCost - If the cost being charged is an Item.
     * @return If the Player has the required economy balance to execute the command.
     */
    private boolean isPlayerChargeable(final Player player, final boolean materialCost) {
        if (ItemJoin.getCore().getDependencies().getVault().vaultEnabled() && !materialCost && !(this.cost < 0)) {
            double balance = 0.0;
            try {
                balance = ItemJoin.getCore().getDependencies().getVault().getBalance(player);
            } catch (NullPointerException ignored) {
            }
            final boolean balCost = (balance >= this.cost);
            if (balCost || this.cost < 0) {
                return true;
            } else {
                String[] placeHolders = ItemJoin.getCore().getLang().newString();
                placeHolders[6] = this.cost.toString();
                placeHolders[5] = balance + "";
                ItemJoin.getCore().getLang().sendLangMessage("general.econFailed", player, placeHolders);
                return false;
            }
        } else if (materialCost) {
            Material mat = ItemHandler.getMaterial(this.itemCost, null);
            int foundAmount = 0;
            for (ItemStack playerInventory : player.getInventory().getContents()) {
                if (playerInventory != null && playerInventory.getType() == mat) {
                    if (playerInventory.getAmount() >= this.cost) {
                        return true;
                    } else {
                        foundAmount += playerInventory.getAmount();
                        if (foundAmount >= this.cost) {
                            return true;
                        }
                    }
                }
            }
            for (ItemStack equipInventory : Objects.requireNonNull(player.getEquipment()).getArmorContents()) {
                if (equipInventory != null && equipInventory.getType() == mat) {
                    if (equipInventory.getAmount() >= this.cost) {
                        return true;
                    } else {
                        foundAmount += equipInventory.getAmount();
                        if (foundAmount >= this.cost) {
                            return true;
                        }
                    }
                }
            }
            if (ServerUtils.hasSpecificUpdate("1_9")) {
                if (player.getInventory().getItemInOffHand().getType() == mat) {
                    if (player.getInventory().getItemInOffHand().getAmount() >= this.cost) {
                        return true;
                    } else {
                        foundAmount += player.getInventory().getItemInOffHand().getAmount();
                        if (foundAmount >= this.cost) {
                            return true;
                        }
                    }
                }
            }
            if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
                for (ItemStack craftInventory : player.getOpenInventory().getTopInventory()) {
                    if (craftInventory != null && craftInventory.getType() == mat && craftInventory.getAmount() >= this.cost) {
                        if (craftInventory.getAmount() >= this.cost) {
                            return true;
                        } else {
                            foundAmount += craftInventory.getAmount();
                            if (foundAmount >= this.cost) {
                                return true;
                            }
                        }
                    }
                }
            }
            StringBuilder formatCost = new StringBuilder();
            for (String str : this.itemCost.toLowerCase().split("_")) {
                formatCost.append(str.substring(0, 1).toUpperCase()).append(str.substring(1)).append(" ");
            }
            formatCost = new StringBuilder(formatCost.substring(0, formatCost.length() - 1));
            String[] placeHolders = ItemJoin.getCore().getLang().newString();
            placeHolders[4] = formatCost.toString();
            placeHolders[6] = this.cost == 0 ? "1" : this.cost.toString();
            placeHolders[5] = foundAmount + "";
            ItemJoin.getCore().getLang().sendLangMessage("general.itemFailed", player, placeHolders);
            return false;
        }
        return true;
    }

    /**
     * Withdraws the Commands Item Cost from the Players Inventory.
     *
     * @param player - The Player to have their Item Cost changed.
     */
    private void withdrawItemCost(final Player player) {
        Material mat = ItemHandler.getMaterial(this.itemCost, null);
        Integer removeAmount = this.cost;
        if (this.cost == 0) {
            removeAmount = 1;
        }
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            final ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == mat) {
                if (item.getAmount() < removeAmount) {
                    removeAmount -= item.getAmount();
                    player.getInventory().setItem(i, ItemHandler.modifyItem(item, false, item.getAmount()));
                } else {
                    player.getInventory().setItem(i, ItemHandler.modifyItem(player.getInventory().getItem(i), false, removeAmount));
                    break;
                }
            }
        }
        if (ServerUtils.hasSpecificUpdate("1_9")) {
            final ItemStack item = player.getInventory().getItemInOffHand();
            if (item.getType() == mat) {
                if (item.getAmount() < removeAmount) {
                    removeAmount -= item.getAmount();
                    PlayerHandler.setOffHandItem(player, ItemHandler.modifyItem(item, false, item.getAmount()));
                } else {
                    PlayerHandler.setOffHandItem(player, ItemHandler.modifyItem(item, false, removeAmount));
                }
                PlayerHandler.setOffHandItem(player, ItemHandler.modifyItem(item, false, removeAmount));
            }
        }
        if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
            for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
                final ItemStack item = player.getOpenInventory().getTopInventory().getItem(i);
                if (item != null && item.getType() == mat) {
                    if (item.getAmount() < removeAmount) {
                        removeAmount -= item.getAmount();
                        player.getOpenInventory().getTopInventory().setItem(i, ItemHandler.modifyItem(item, false, item.getAmount()));
                    } else {
                        player.getOpenInventory().getTopInventory().setItem(i, ItemHandler.modifyItem(item, false, removeAmount));
                        break;
                    }
                }
            }
        }
        StringBuilder formatCost = new StringBuilder();
        for (String str : this.itemCost.toLowerCase().split("_")) {
            formatCost.append(str.substring(0, 1).toUpperCase()).append(str.substring(1)).append(" ");
        }
        formatCost = new StringBuilder(formatCost.substring(0, formatCost.length() - 1));
        String[] placeHolders = ItemJoin.getCore().getLang().newString();
        placeHolders[4] = formatCost.toString();
        placeHolders[6] = this.cost.toString();
        ItemJoin.getCore().getLang().sendLangMessage("general.itemSuccess", player, placeHolders);
    }

    /**
     * Withdraws the Commands Cost from the Vault Balance for the Player.
     *
     * @param player - The Player to have their economy balance changed.
     */
    private void withdrawBalance(final Player player) {
        if (ItemJoin.getCore().getDependencies().getVault().vaultEnabled()) {
            double balance = 0.0;
            try {
                balance = ItemJoin.getCore().getDependencies().getVault().getBalance(player);
            } catch (NullPointerException ignored) {
            }
            int parseCost = this.cost;
            if (balance >= parseCost) {
                if (parseCost > 0) {
                    try {
                        ItemJoin.getCore().getDependencies().getVault().withdrawBalance(player, parseCost);
                    } catch (NullPointerException e) {
                        ServerUtils.sendDebugTrace(e);
                    }
                    String[] placeHolders = ItemJoin.getCore().getLang().newString();
                    placeHolders[6] = this.cost.toString();
                    ItemJoin.getCore().getLang().sendLangMessage("general.econSuccess", player, placeHolders);
                }
            }
        }
    }

    /**
     * Plays the Commands Sound for the Player.
     *
     * @param player - The Player to have the Sound heard.
     */
    private void playSound(final Player player) {
        if (this.commandSound != null) {
            try {
                player.playSound(player.getLocation(), this.commandSound, (float) ((double) this.commandSoundVolume), (float) ((double) this.commandSoundPitch));
            } catch (Exception e) {
                ServerUtils.logSevere("{ItemMap} There was an issue executing the commands-sound you defined.");
                ServerUtils.logWarn("{ItemMap} " + this.commandSound + " is not a sound in " + ReflectionUtils.getServerVersion() + ".");
                ServerUtils.sendDebugTrace(e);
            }
        }
    }

    /**
     * Plays the Commands Particle for the Player.
     *
     * @param player - The Player to have the Particle visible.
     */
    private void playParticle(final Player player) {
        if (this.commandParticle != null) {
            EffectAPI.spawnParticle(player, this.commandParticle);
        }
    }

    /**
     * Disposes of the specified ItemStack, removing x1 from the Players Inventory.
     *
     * @param player   - The Player disposing of the ItemStack.
     * @param itemCopy - The ItemStack to be disposed.
     * @param allItems - If the item should not have its amount changed.
     */
    public void removeDisposable(final Player player, final ItemMap itemMap, final ItemStack itemCopy, final boolean allItems) {
        if (this.disposable && this.conditionMet(player, "disposable-conditions", true, false) || allItems) {
            if (!allItems) {
                this.setSubjectRemoval(true);
            }
            SchedulerUtils.runLater(1L, () -> {
                if (PlayerHandler.isCreativeMode(player)) {
                    player.closeInventory();
                }
                if (itemMap.isSimilar(player, player.getItemOnCursor())) {
                    player.setItemOnCursor(ItemHandler.modifyItem(player.getItemOnCursor(), allItems, 1));
                    if (!allItems) {
                        this.setSubjectRemoval(false);
                    }
                } else {
                    int itemSlot = player.getInventory().getHeldItemSlot();
                    if (itemMap.isSimilar(player, player.getInventory().getItem(itemSlot))) {
                        player.getInventory().setItem(itemSlot, ItemHandler.modifyItem(player.getInventory().getItem(itemSlot), allItems, 1));
                        if (!allItems) {
                            this.setSubjectRemoval(false);
                        }
                    } else {
                        for (int i = 0; i < player.getInventory().getSize(); i++) {
                            if (itemMap.isSimilar(player, player.getInventory().getItem(i))) {
                                player.getInventory().setItem(i, ItemHandler.modifyItem(player.getInventory().getItem(i), allItems, 1));
                                if (!allItems) {
                                    this.setSubjectRemoval(false);
                                }
                                break;
                            }
                        }
                    }
                    if (this.isSubjectRemoval() && PlayerHandler.isCreativeMode(player)) {
                        player.getInventory().addItem(ItemHandler.modifyItem(itemCopy, allItems, 1));
                        player.setItemOnCursor(new ItemStack(Material.AIR));
                        if (!allItems) {
                            this.setSubjectRemoval(false);
                        }
                    } else if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
                        for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
                            if (itemMap.isSimilar(player, player.getOpenInventory().getTopInventory().getItem(i))) {
                                player.getOpenInventory().getTopInventory().setItem(i, ItemHandler.modifyItem(player.getOpenInventory().getTopInventory().getItem(i), allItems, 1));
                                if (!allItems) {
                                    this.setSubjectRemoval(false);
                                }
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Checks if the Player is on Interact Cooldown.
     *
     * @param player - The Player to be checked.
     * @return If the Player is on Interact Cooldown.
     */
    public boolean onInteractCooldown(final Player player) {
        long playersCooldownList = 0L;
        if (this.playersOnInteractCooldown.containsKey(PlayerHandler.getPlayerID(player) + ".items." + this.configName)) {
            playersCooldownList = this.playersOnInteractCooldown.get(PlayerHandler.getPlayerID(player) + ".items." + this.configName);
        }
        if (System.currentTimeMillis() - playersCooldownList >= this.interactCooldown * 1000) {
            this.playersOnInteractCooldown.put(PlayerHandler.getPlayerID(player) + ".items." + this.configName, System.currentTimeMillis());
            return false;
        } else {
            if (this.onSpamCooldown(player)) {
                this.storedSpammedPlayers.put(PlayerHandler.getPlayerID(player) + ".items." + this.configName, System.currentTimeMillis());
                if (this.cooldownMessage != null && !this.cooldownMessage.isEmpty()) {
                    int timeLeft = (int) (this.interactCooldown - ((System.currentTimeMillis() - playersCooldownList) / 1000));
                    player.sendMessage(StringUtils.translateLayout(this.cooldownMessage.replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", this.customName), player));
                }
            }
            return true;
        }
    }

    /**
     * Checks if the Player is on Cooldown.
     * (Prevents rapid fire commands).
     *
     * @param player - The Player to be checked.
     * @return If the Player is on Cooldown.
     */
    private boolean onSpamCooldown(final Player player) {
        boolean interactSpam = ItemJoin.getCore().getConfig("items.yml").getBoolean("items-Spamming");
        if (!interactSpam) {
            long playersCooldownList = 0L;
            if (this.storedSpammedPlayers.containsKey(PlayerHandler.getPlayerID(player) + ".items." + this.configName)) {
                playersCooldownList = this.storedSpammedPlayers.get(PlayerHandler.getPlayerID(player) + ".items." + this.configName);
            }
            int spamTime = 1;
            return System.currentTimeMillis() - playersCooldownList >= spamTime * 1000;
        }
        return true;
    }

    /**
     * Checks if the Player is on Cooldown.
     *
     * @param player - The Player to be checked.
     * @return If the Player is on Cooldown.
     */
    private boolean onCooldown(final Player player) {
        long playersCooldownList = 0L;
        if (this.playersOnCooldown.containsKey(PlayerHandler.getPlayerID(player))) {
            playersCooldownList = this.playersOnCooldown.get(PlayerHandler.getPlayerID(player));
        }
        if (this.cooldownSeconds != 0) {
            if (System.currentTimeMillis() - playersCooldownList >= this.cooldownSeconds * 1000) {
                return false;
            } else if (this.onCooldownTick(player)) {
                String cooldownMsg = this.cooldownMessage != null ? (this.cooldownMessage.replace("%timeleft%", String.valueOf((this.cooldownSeconds - ((System.currentTimeMillis() - playersCooldownList) / 1000)))).replace("%item%", this.customName).replace("%itemraw%", Objects.requireNonNull(ItemHandler.getMaterialName(this.tempItem)))) : null;
                if (cooldownMsg != null && !this.cooldownMessage.isEmpty()) {
                    cooldownMsg = StringUtils.translateLayout(cooldownMsg, player);
                    player.sendMessage(cooldownMsg);
                }
                this.addPlayerOnCooldownTick(player);
            }
        } else if (this.onCooldownTick(player)) {
            this.addPlayerOnCooldownTick(player);
            return false;
        }
        return true;
    }

    /**
     * Checks if the Player is on Cooldown.
     *
     * @param player - The Player to be checked.
     * @return If the Player is on Cooldown.
     */
    private boolean onCooldownTick(final Player player) {
        if (!ItemJoin.getCore().getConfig("items.yml").getBoolean("items-Spamming")) {
            long playersCooldownList = 0L;
            if (this.playersOnCooldownTick.containsKey(PlayerHandler.getPlayerID(player))) {
                playersCooldownList = this.playersOnCooldownTick.get(PlayerHandler.getPlayerID(player));
            }

            return System.currentTimeMillis() - playersCooldownList >= 1000;
        }
        return true;
    }

    /**
     * Puts the Player on Cooldown.
     * (Prevents rapid fire commands).
     *
     * @param player - The Player to be put on Cooldown.
     */
    private void addPlayerOnCooldownTick(final Player player) {
        this.playersOnCooldownTick.put(PlayerHandler.getPlayerID(player), System.currentTimeMillis());
    }

    /**
     * Puts the Player on Cooldown.
     *
     * @param player - The Player to be put on Cooldown.
     */
    private void addPlayerOnCooldown(final Player player) {
        this.playersOnCooldown.put(PlayerHandler.getPlayerID(player), System.currentTimeMillis());
    }

    /**
     * Gets the Players on Cooldown.
     *
     * @return The Map of Players on Cooldown.
     */
    public Map<String, Long> getPlayersOnCooldown() {
        return this.playersOnCooldown;
    }

    /**
     * Adds an ItemCommand to a Map.
     *
     * @param map     - The Map to have an ItemCommand added.
     * @param command - The ItemCommand to be added to the Map.
     * @return The changed Map instance.
     */
    public Map<String, List<String>> addMapCommand(final Map<String, List<String>> map, final ItemCommand command) {
        String commandSection = (command.getSection() != null ? command.getSection() : "DEFAULT");
        if (map.get(commandSection) != null) {
            List<String> s1 = map.get(commandSection);
            s1.add(command.getRawCommand());
            map.put(commandSection, s1);
        } else {
            List<String> s1 = new ArrayList<>();
            s1.add(command.getRawCommand());
            map.put(commandSection, s1);
        }
        return map;
    }

    /**
     * Saves the ItemCommands to the items.yml.
     *
     * @param itemData - The File having the data saved.
     * @param map      - The Map of entries to save.
     * @param section  - The item section being saved.
     */
    public void setMapCommand(final FileConfiguration itemData, final Map<String, List<String>> map, final String section) {
        for (Entry<String, List<String>> mapElement : map.entrySet()) {
            String mapKey = mapElement.getKey();
            if (mapKey.equalsIgnoreCase("DEFAULT") && map.size() <= 1) {
                mapKey = "";
            } else {
                mapKey = "." + mapKey;
            }
            itemData.set("items." + this.configName + "." + section + mapKey, mapElement.getValue());
        }
    }

    /**
     * Gets the Shape of the Recipe of the Custom Item.
     *
     * @return The trimmed recipe.
     */
    public String[] trimRecipe(final List<String> list) {
        List<Character> recipe = new ArrayList<>();
        String[] recipeShape = {"XXX", "XXX", "XXX"};
        String[] shape = {"", "", ""};
        for (int i = 0; i < list.size(); i++) {
            int charSize = 0;
            for (String character : list.get(i).split("(?<!^)")) {
                StringBuilder sb = new StringBuilder(recipeShape[i]);
                sb.setCharAt(charSize, character.charAt(0));
                recipeShape[i] = sb.toString();
                charSize++;
            }
            for (String character : recipeShape[i].split("(?<!^)")) {
                recipe.add(character.charAt(0));
            }
            shape[i] = list.get(i).replace("X", " ");
        }
        this.addRecipe(recipe);
        String substring = shape[1].substring(0, shape[1].length() - 1);
        String substring1 = shape[0].substring(0, shape[0].length() - 1);
        if ((shape[2].length() == 3 && shape[2].charAt(0) == ' ' && shape[2].charAt(1) == ' ' && shape[2].charAt(2) == ' ') || shape[2].isEmpty()) {
            if (shape[0].length() == 3 && shape[0].charAt(2) == ' ' && shape[1].length() == 3 && shape[1].charAt(2) == ' ') {
                shape = new String[]{substring1, substring};
            } else if (shape[0].length() == 3 && shape[0].charAt(0) == ' ' && shape[0].charAt(1) == ' ' && shape[0].charAt(2) == ' ') {
                shape = new String[]{shape[1]};
            } else if (shape[1].length() == 3 && shape[1].charAt(0) == ' ' && shape[1].charAt(1) == ' ' && shape[1].charAt(2) == ' ') {
                shape = new String[]{shape[0]};
            } else {
                shape = new String[]{shape[0], shape[1]};
            }
        } else if (shape[0].length() < 3 && shape[1].length() < 3 && shape[2].length() == 3 && shape[2].charAt(0) == ' ' && shape[2].charAt(1) == ' ' && shape[2].charAt(2) == ' ') {
            shape = new String[]{shape[0], shape[1]};
        } else if (shape[0].length() == 3 && shape[1].length() == 3 && shape[2].length() == 3 && shape[0].charAt(2) == ' ' && shape[1].charAt(2) == ' ' && shape[2].charAt(2) == ' ') {
            shape = new String[]{substring1, substring, shape[2].substring(0, shape[2].length() - 1)};
        }
        return shape;
    }

    /**
     * Removes this ItemMap instance from the items.yml.
     */
    public void removeFromConfig() {
        File itemFile = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "items.yml");
        FileConfiguration itemData = YamlConfiguration.loadConfiguration(itemFile);
        if (ItemJoin.getCore().getConfig("items.yml").getString("items." + this.configName) != null) {
            itemData.set("items." + this.configName, null);
        }
        try {
            itemData.save(itemFile);
            ItemJoin.getCore().getConfiguration().getSource("items.yml");
            ItemJoin.getCore().getConfig("items.yml").options().copyDefaults(false);
        } catch (Exception e) {
            ItemJoin.getCore().getPlugin().getServer().getLogger().severe("Could not remove the custom item " + this.configName + " from the items.yml data file!");
            ServerUtils.sendDebugTrace(e);
        }
    }

    /**
     * Runs through parsing the first set of data for the ItemMap.
     * This saves the info to the items.yml.
     *
     * @param itemData - The FileConfiguration reference for the items.yml.
     */
    private void parseData_1(final FileConfiguration itemData) {
        final boolean nonNull = (this.dynamicMaterials != null && !this.dynamicMaterials.isEmpty());
        if (!(nonNull)) {
            itemData.set("items." + this.configName + ".id", this.material.toString().toUpperCase() + (this.dataValue != null && this.dataValue != 0 ? ":" + this.dataValue : ""));
        } else {
            for (int i = 0; i < this.dynamicMaterials.size(); i++) {
                itemData.set("items." + this.configName + ".id." + (i + 1), this.dynamicMaterials.get(i));
            }
        }
        if (this.AllSlots != null && !this.AllSlots.isEmpty()) {
            StringBuilder saveSlots = new StringBuilder();
            for (String slot : this.AllSlots) {
                saveSlots.append(slot).append(", ");
            }
            if (!saveSlots.substring(0, saveSlots.length() - 2).equalsIgnoreCase("ARBITRARY")) {
                itemData.set("items." + this.configName + ".slot", saveSlots.substring(0, saveSlots.length() - 2));
            }
        } else if (this.CustomSlot == null) {
            itemData.set("items." + this.configName + ".slot", this.InvSlot);
        } else {
            if (!this.CustomSlot.equalsIgnoreCase("ARBITRARY")) {
                itemData.set("items." + this.configName + ".slot", this.CustomSlot);
            }
        }
        if (this.getCount(null) > 1 && !this.count.contains("%")) {
            itemData.set("items." + this.configName + ".count", this.count);
        }
        if (this.durability != null && this.durability > 0) {
            itemData.set("items." + this.configName + ".durability", this.durability);
        }
        if (this.data != null && this.data > 0) {
            itemData.set("items." + this.configName + ".data", this.data);
        }
        if (this.modelData != null && !this.modelData.isEmpty()) {
            itemData.set("items." + this.configName + ".model-data", this.modelData);
        }
        if (this.author != null && !this.author.isEmpty()) {
            itemData.set("items." + this.configName + ".author", this.author.replace(ChatColor.COLOR_CHAR, '&'));
        }
        if (this.customName != null && !this.customName.isEmpty() && (this.dynamicNames == null || this.dynamicNames.isEmpty())) {
            String setName;
            if (this.legacySecret != null && !ServerUtils.hasSpecificUpdate("1_14")) {
                final ItemMeta itemMeta = this.tempItem.getItemMeta();
                if (itemMeta != null) {
                    itemMeta.setDisplayName("");
                    this.tempItem.setItemMeta(itemMeta);
                }
                StringUtils.colorEncode(this.tempItem, this.legacySecret);
                final String itemInfo = this.tempItem.getItemMeta().getDisplayName();
                setName = this.customName.replace(itemInfo, "").replace(ChatColor.COLOR_CHAR, '&');
            } else {
                setName = this.customName.replace(ChatColor.COLOR_CHAR, '&');
            }
            if (setName.startsWith("&f") && !ItemJoin.getCore().getData().dataTagsEnabled()) {
                setName = setName.substring(2);
            }
            if (!Objects.requireNonNull(ItemHandler.getMaterialName(this.tempItem)).equalsIgnoreCase(setName)) {
                itemData.set("items." + this.configName + ".name", setName);
            }
        } else if (this.dynamicNames != null && !this.dynamicNames.isEmpty()) {
            for (int i = 0; i < this.dynamicNames.size(); i++) {
                itemData.set("items." + this.configName + ".name." + (i + 1), this.dynamicNames.get(i).replace(ChatColor.COLOR_CHAR, '&'));
            }
        }
        if (this.customLore != null && !this.customLore.isEmpty() && (this.dynamicLores == null || this.dynamicLores.isEmpty())) {
            itemData.set("items." + this.configName + ".lore", this.customLore);
        } else if (this.dynamicLores != null && !this.dynamicLores.isEmpty()) {
            for (int i = 0; i < this.dynamicLores.size(); i++) {
                List<String> lores = new ArrayList<>();
                for (String lore : this.dynamicLores.get(i)) {
                    lores.add(lore.replace(ChatColor.COLOR_CHAR, '&'));
                }
                itemData.set("items." + this.configName + ".lore." + (i + 1), lores);
            }
        }
        if (this.listPages != null && !this.listPages.isEmpty()) {
            for (int i = 0; i < this.listPages.size(); i++) {
                List<String> pages = new ArrayList<>();
                for (String page : this.listPages.get(i)) {
                    pages.add(page.replace(ChatColor.COLOR_CHAR, '&'));
                }
                itemData.set("items." + this.configName + ".pages." + (i + 1), pages);
            }
        }
        if (this.probability != null && this.probability != -1 && this.probability != 0) {
            itemData.set("items." + this.configName + ".probability", this.probability);
        }
    }

    /**
     * Runs through parsing the first set of data for the ItemMap.
     * This saves the info to the items.yml.
     *
     * @param itemData - The FileConfiguration reference for the items.yml.
     */
    private void parseData_2(final FileConfiguration itemData) {
        if (this.commands != null && this.commands.length > 0) {
            Map<String, List<String>> interactAll = new HashMap<>();
            Map<String, List<String>> interactLeft = new HashMap<>();
            Map<String, List<String>> interactRight = new HashMap<>();
            Map<String, List<String>> interactAir = new HashMap<>();
            Map<String, List<String>> interactBlock = new HashMap<>();
            Map<String, List<String>> interactLeftAir = new HashMap<>();
            Map<String, List<String>> interactLeftBlock = new HashMap<>();
            Map<String, List<String>> interactRightAir = new HashMap<>();
            Map<String, List<String>> interactRightBlock = new HashMap<>();
            Map<String, List<String>> inventoryAll = new HashMap<>();
            Map<String, List<String>> inventoryMiddle = new HashMap<>();
            Map<String, List<String>> inventoryCreative = new HashMap<>();
            Map<String, List<String>> inventoryLeft = new HashMap<>();
            Map<String, List<String>> inventoryShiftLeft = new HashMap<>();
            Map<String, List<String>> inventoryRight = new HashMap<>();
            Map<String, List<String>> inventoryShiftRight = new HashMap<>();
            Map<String, List<String>> inventorySwapCursor = new HashMap<>();
            Map<String, List<String>> onEquip = new HashMap<>();
            Map<String, List<String>> unEquip = new HashMap<>();
            Map<String, List<String>> onHold = new HashMap<>();
            Map<String, List<String>> onDeath = new HashMap<>();
            Map<String, List<String>> onKill = new HashMap<>();
            Map<String, List<String>> onDamage = new HashMap<>();
            Map<String, List<String>> onHit = new HashMap<>();
            Map<String, List<String>> onFire = new HashMap<>();
            Map<String, List<String>> onConsume = new HashMap<>();
            Map<String, List<String>> onReceive = new HashMap<>();
            Map<String, List<String>> physical = new HashMap<>();
            for (ItemCommand command : this.commands) {
                if (command.matchAction(ItemCommand.Action.INTERACT_ALL)) {
                    interactAll = this.addMapCommand(interactAll, command);
                } else if (command.matchAction(ItemCommand.Action.INTERACT_AIR)) {
                    interactAir = this.addMapCommand(interactAir, command);
                } else if (command.matchAction(ItemCommand.Action.INTERACT_BLOCK)) {
                    interactBlock = this.addMapCommand(interactBlock, command);
                } else if (command.matchAction(ItemCommand.Action.INTERACT_RIGHT_ALL)) {
                    interactRight = this.addMapCommand(interactRight, command);
                } else if (command.matchAction(ItemCommand.Action.INTERACT_RIGHT_AIR)) {
                    interactRightAir = this.addMapCommand(interactRightAir, command);
                } else if (command.matchAction(ItemCommand.Action.INTERACT_RIGHT_BLOCK)) {
                    interactRightBlock = this.addMapCommand(interactRightBlock, command);
                } else if (command.matchAction(ItemCommand.Action.INTERACT_LEFT_ALL)) {
                    interactLeft = this.addMapCommand(interactLeft, command);
                } else if (command.matchAction(ItemCommand.Action.INTERACT_LEFT_AIR)) {
                    interactLeftAir = this.addMapCommand(interactLeftAir, command);
                } else if (command.matchAction(ItemCommand.Action.INTERACT_LEFT_BLOCK)) {
                    interactLeftBlock = this.addMapCommand(interactLeftBlock, command);
                } else if (command.matchAction(ItemCommand.Action.INVENTORY_ALL)) {
                    inventoryAll = this.addMapCommand(inventoryAll, command);
                } else if (command.matchAction(ItemCommand.Action.INVENTORY_MIDDLE)) {
                    inventoryMiddle = this.addMapCommand(inventoryMiddle, command);
                } else if (command.matchAction(ItemCommand.Action.INVENTORY_CREATIVE)) {
                    inventoryCreative = this.addMapCommand(inventoryCreative, command);
                } else if (command.matchAction(ItemCommand.Action.INVENTORY_LEFT)) {
                    inventoryLeft = this.addMapCommand(inventoryLeft, command);
                } else if (command.matchAction(ItemCommand.Action.INVENTORY_SHIFT_LEFT)) {
                    inventoryShiftLeft = this.addMapCommand(inventoryShiftLeft, command);
                } else if (command.matchAction(ItemCommand.Action.INVENTORY_RIGHT)) {
                    inventoryRight = this.addMapCommand(inventoryRight, command);
                } else if (command.matchAction(ItemCommand.Action.INVENTORY_SHIFT_RIGHT)) {
                    inventoryShiftRight = this.addMapCommand(inventoryShiftRight, command);
                } else if (command.matchAction(ItemCommand.Action.INVENTORY_SWAP_CURSOR)) {
                    inventorySwapCursor = this.addMapCommand(inventorySwapCursor, command);
                } else if (command.matchAction(ItemCommand.Action.ON_EQUIP)) {
                    onEquip = this.addMapCommand(onEquip, command);
                } else if (command.matchAction(ItemCommand.Action.UN_EQUIP)) {
                    unEquip = this.addMapCommand(unEquip, command);
                } else if (command.matchAction(ItemCommand.Action.ON_HOLD)) {
                    onHold = this.addMapCommand(onHold, command);
                } else if (command.matchAction(ItemCommand.Action.ON_DEATH)) {
                    onDeath = this.addMapCommand(onDeath, command);
                } else if (command.matchAction(ItemCommand.Action.ON_KILL)) {
                    onKill = this.addMapCommand(onKill, command);
                } else if (command.matchAction(ItemCommand.Action.ON_DAMAGE)) {
                    onDamage = this.addMapCommand(onDamage, command);
                } else if (command.matchAction(ItemCommand.Action.ON_HIT)) {
                    onHit = this.addMapCommand(onHit, command);
                } else if (command.matchAction(ItemCommand.Action.ON_FIRE)) {
                    onFire = this.addMapCommand(onFire, command);
                } else if (command.matchAction(ItemCommand.Action.ON_CONSUME)) {
                    onConsume = this.addMapCommand(onConsume, command);
                } else if (command.matchAction(ItemCommand.Action.ON_RECEIVE)) {
                    onReceive = this.addMapCommand(onReceive, command);
                } else if (command.matchAction(ItemCommand.Action.PHYSICAL)) {
                    physical = this.addMapCommand(physical, command);
                }
            }
            if (!interactAll.isEmpty()) {
                this.setMapCommand(itemData, interactAll, "interact");
            }
            if (!interactAir.isEmpty()) {
                this.setMapCommand(itemData, interactAir, "interact-air");
            }
            if (!interactBlock.isEmpty()) {
                this.setMapCommand(itemData, interactBlock, "interact-block");
            }
            if (!interactRight.isEmpty()) {
                this.setMapCommand(itemData, interactRight, "interact-right");
            }
            if (!interactRightAir.isEmpty()) {
                this.setMapCommand(itemData, interactRightAir, "interact-air-right");
            }
            if (!interactRightBlock.isEmpty()) {
                this.setMapCommand(itemData, interactRightBlock, "interact-block-right");
            }
            if (!interactLeft.isEmpty()) {
                this.setMapCommand(itemData, interactLeft, "interact-left");
            }
            if (!interactLeftAir.isEmpty()) {
                this.setMapCommand(itemData, interactLeftAir, "interact-air-left");
            }
            if (!interactLeftBlock.isEmpty()) {
                this.setMapCommand(itemData, interactLeftBlock, "interact-block-left");
            }

            if (!inventoryAll.isEmpty()) {
                this.setMapCommand(itemData, inventoryAll, "inventory");
            }
            if (!inventoryMiddle.isEmpty()) {
                this.setMapCommand(itemData, inventoryMiddle, "inventory-middle");
            }
            if (!inventoryCreative.isEmpty()) {
                this.setMapCommand(itemData, inventoryCreative, "inventory-creative");
            }
            if (!inventoryLeft.isEmpty()) {
                this.setMapCommand(itemData, inventoryLeft, "inventory-left");
            }
            if (!inventoryShiftLeft.isEmpty()) {
                this.setMapCommand(itemData, inventoryShiftLeft, "inventory-shift-left");
            }
            if (!inventoryRight.isEmpty()) {
                this.setMapCommand(itemData, inventoryRight, "inventory-right");
            }
            if (!inventoryShiftRight.isEmpty()) {
                this.setMapCommand(itemData, inventoryShiftRight, "inventory-shift-right");
            }
            if (!inventorySwapCursor.isEmpty()) {
                this.setMapCommand(itemData, inventorySwapCursor, "inventory-swap-cursor");
            }

            if (!onEquip.isEmpty()) {
                this.setMapCommand(itemData, onEquip, "on-equip");
            }
            if (!unEquip.isEmpty()) {
                this.setMapCommand(itemData, unEquip, "un-equip");
            }
            if (!onHold.isEmpty()) {
                this.setMapCommand(itemData, onHold, "on-hold");
            }
            if (!onDeath.isEmpty()) {
                this.setMapCommand(itemData, onDeath, "on-death");
            }
            if (!onKill.isEmpty()) {
                this.setMapCommand(itemData, onKill, "on-kill");
            }
            if (!onDamage.isEmpty()) {
                this.setMapCommand(itemData, onDamage, "on-damage");
            }
            if (!onHit.isEmpty()) {
                this.setMapCommand(itemData, onHit, "on-hit");
            }
            if (!onFire.isEmpty()) {
                this.setMapCommand(itemData, onFire, "on-fire");
            }
            if (!onConsume.isEmpty()) {
                this.setMapCommand(itemData, onConsume, "on-consume");
            }
            if (!onReceive.isEmpty()) {
                this.setMapCommand(itemData, onReceive, "on-receive");
            }
            if (!physical.isEmpty()) {
                this.setMapCommand(itemData, physical, "physical");
            }
        }
    }

    /**
     * Runs through parsing the first set of data for the ItemMap.
     * This saves the info to the items.yml.
     *
     * @param itemData - The FileConfiguration reference for the items.yml.
     */
    private void parseData_3(final FileConfiguration itemData) {
        if (this.toggleCommands != null && !this.getToggleCommands().isEmpty()) {
            itemData.set("items." + this.configName + ".toggle", this.toggleCommands);
        }
        if (this.commandSound != null) {
            itemData.set("items." + this.configName + ".commands-sound", this.commandSound.name() + ((this.commandSoundVolume != 1.0 && this.commandSoundPitch != 1.0) ? ":" + this.commandSoundVolume + ":" + this.commandSoundPitch : ""));
        }
        if (this.commandParticle != null && !this.commandParticle.isEmpty()) {
            itemData.set("items." + this.configName + ".commands-particle", this.commandParticle);
        }
        if (this.sequence != null && this.sequence != CommandSequence.SEQUENTIAL) {
            itemData.set("items." + this.configName + ".commands-sequence", this.sequence.name());
        }
        if (this.itemCost != null && !this.itemCost.isEmpty()) {
            itemData.set("items." + this.configName + ".commands-item", this.itemCost);
        }
        if (this.cost != null && this.cost != 0) {
            itemData.set("items." + this.configName + ".commands-cost", this.cost);
        }
        if (this.commandsReceive != null && this.commandsReceive != 0) {
            itemData.set("items." + this.configName + ".commands-receive", this.commandsReceive);
        }
        if (this.warmDelay != null && this.warmDelay != 0) {
            itemData.set("items." + this.configName + ".commands-warmup", this.warmDelay);
        }
        if (this.cooldownSeconds != null && this.cooldownSeconds != 0) {
            itemData.set("items." + this.configName + ".commands-cooldown", this.cooldownSeconds);
        }
        if (this.cooldownMessage != null && !this.cooldownMessage.isEmpty()) {
            itemData.set("items." + this.configName + ".cooldown-message", this.cooldownMessage);
        }
        if (this.toggleMessage != null && !this.toggleMessage.isEmpty()) {
            itemData.set("items." + this.configName + ".toggle-message", this.toggleMessage);
        }
        if (this.enchants != null && !this.enchants.isEmpty()) {
            StringBuilder enchantList = new StringBuilder();
            for (Entry<String, Integer> enchantments : this.enchants.entrySet()) {
                enchantList.append(enchantments.getKey()).append(":").append(enchantments.getValue()).append(", ");
            }
            itemData.set("items." + this.configName + ".enchantments", enchantList.substring(0, enchantList.length() - 2));
        }
        if (this.fireworkType != null) {
            itemData.set("items." + this.configName + ".firework.type", this.fireworkType.name());
        }
        if (this.power != null && this.power != 0) {
            itemData.set("items." + this.configName + ".firework.power", this.power);
        }
        if (this.fireworkFlicker) {
            itemData.set("items." + this.configName + ".firework.flicker", true);
        }
        if (this.fireworkTrail) {
            itemData.set("items." + this.configName + ".firework.trail", true);
        }
        if (this.fireworkColor != null && !this.fireworkColor.isEmpty()) {
            StringBuilder colorList = new StringBuilder();
            for (DyeColor color : this.fireworkColor) {
                colorList.append(color.name()).append(", ");
            }
            itemData.set("items." + this.configName + ".firework.colors", colorList.substring(0, colorList.length() - 2));
        }
        if (this.interactCooldown != null && this.interactCooldown != 0) {
            itemData.set("items." + this.configName + ".use-cooldown", this.interactCooldown);
        }
        if (this.teleportEffect != null && !this.teleportEffect.isEmpty()) {
            itemData.set("items." + this.configName + ".teleport-effect", this.teleportEffect);
        }
        if (this.teleportSound != null && !this.teleportSound.isEmpty()) {
            itemData.set("items." + this.configName + ".teleport-sound", this.teleportSound + ((this.teleportSoundVolume != 1.0 && this.teleportSoundPitch != 1.0) ? ":" + this.teleportSoundVolume + ":" + this.teleportSoundPitch : ""));
        }
        if (this.itemflags != null && !this.itemflags.isEmpty()) {
            itemData.set("items." + this.configName + ".itemflags", this.itemflags);
        }
        if (this.triggers != null && !this.triggers.isEmpty()) {
            final String defaultTriggers = ItemJoin.getCore().getConfig("config.yml").getString("Settings.Default-Triggers");
            final StringBuilder saveTriggers = new StringBuilder();
            for (String trigger : this.triggers.replace(" ", "").split(",")) {
                if (defaultTriggers == null || !defaultTriggers.toUpperCase().contains(trigger.toUpperCase())) {
                    saveTriggers.append(trigger).append(", ");
                }
            }
            if (!StringUtils.isEmpty(saveTriggers) || saveTriggers.toString().equals(this.triggers)) {
                itemData.set("items." + this.configName + ".triggers", this.triggers);
            }
        }
        if (this.limitModes != null && !this.limitModes.isEmpty()) {
            itemData.set("items." + this.configName + ".limit-modes", this.limitModes);
        }
        if (this.toggleNode != null && !this.toggleNode.isEmpty()) {
            itemData.set("items." + this.configName + ".toggle-permission", this.toggleNode);
        }
        if (this.permissionNode != null && !this.permissionNode.isEmpty()) {
            itemData.set("items." + this.configName + ".permission-node", this.permissionNode);
        }
        if (this.leatherColor != null && !this.leatherColor.isEmpty()) {
            itemData.set("items." + this.configName + ".leather-color", this.leatherColor);
        } else if (this.leatherHex != null && !this.leatherHex.isEmpty()) {
            itemData.set("items." + this.configName + ".leather-color", this.leatherHex);
        }
        if (this.customMapImage != null && !this.customMapImage.isEmpty()) {
            itemData.set("items." + this.configName + ".custom-map-image", this.customMapImage);
        }
        if (this.mapId != -1) {
            itemData.set("items." + this.configName + ".map-id", this.mapId);
        }
        if (this.skullTexture != null && !this.skullTexture.isEmpty() && (this.dynamicTextures == null || this.dynamicTextures.isEmpty())) {
            itemData.set("items." + this.configName + ".skull-texture", this.skullTexture);
        } else if (this.dynamicTextures != null && !this.dynamicTextures.isEmpty()) {
            for (int i = 0; i < this.dynamicTextures.size(); i++) {
                itemData.set("items." + this.configName + ".skull-texture." + (i + 1), this.dynamicTextures.get(i));
            }
        }
        if (this.skullOwner != null && !this.skullOwner.isEmpty() && (this.dynamicOwners == null || this.dynamicOwners.isEmpty())) {
            itemData.set("items." + this.configName + ".skull-owner", this.skullOwner);
        } else if (this.dynamicOwners != null && !this.dynamicOwners.isEmpty()) {
            for (int i = 0; i < this.dynamicOwners.size(); i++) {
                itemData.set("items." + this.configName + ".skull-owner." + (i + 1), this.dynamicOwners.get(i));
            }
        }
        if (this.chargeColor != null) {
            itemData.set("items." + this.configName + ".charge-color", this.chargeColor.name());
        }
        if (this.bannerPatterns != null && !this.bannerPatterns.isEmpty()) {
            StringBuilder bannerList = new StringBuilder();
            for (Pattern pattern : this.bannerPatterns) {
                bannerList.append(pattern.getColor().name()).append(pattern.getPattern().name()).append(", ");
            }
            itemData.set("items." + this.configName + ".banner-meta", bannerList.substring(0, bannerList.length() - 2));
        }
        if (this.trimPattern != null && !this.trimPattern.isEmpty()) {
            final Map.Entry<String, String> entry = this.trimPattern.entrySet().iterator().next();
            itemData.set("items." + this.configName + ".trim-meta", entry.getKey().toUpperCase() + ":" + entry.getValue().toUpperCase());
        }
        if (this.recipe != null && !this.recipe.isEmpty() && this.ingredients != null && !this.ingredients.isEmpty()) {
            List<String> ingredientList = new ArrayList<>();
            List<String> recipeTempList = new ArrayList<>();
            List<String> recipeList = new ArrayList<>();
            for (Character ingredient : this.ingredients.keySet()) {
                final ItemRecipe itemRecipe = this.ingredients.get(ingredient);
                ingredientList.add(ingredient + ":" + (itemRecipe.getMaterial() != null ? itemRecipe.getMaterial().name() : itemRecipe.getMap()) + (itemRecipe.getData() > 0 ? (":" + itemRecipe.getData()) : "") + (itemRecipe.getCount() > 1 ? (":#" + itemRecipe.getCount()) : ""));
            }
            String recipeLine = "";
            for (Character recipeCharacter : this.recipe.get(0)) {
                recipeLine += recipeCharacter;
                if (StringUtils.countCharacters(recipeLine) == 3) {
                    recipeTempList.add(recipeLine);
                    recipeLine = "";
                }
            }
            if (!recipeLine.isEmpty()) {
                while (StringUtils.countCharacters(recipeTempList.get(0)) != StringUtils.countCharacters(recipeLine)) {
                    recipeLine += "X";
                }
                recipeTempList.add(recipeLine);
            }
            for (String str : this.trimRecipe(recipeTempList)) {
                recipeList.add(str.replace(" ", "X"));
            }
            itemData.set("items." + this.configName + ".recipe", recipeList);
            itemData.set("items." + this.configName + ".ingredients", ingredientList);
        }
    }

    /**
     * Runs through parsing the first set of data for the ItemMap.
     * This saves the info to the items.yml.
     *
     * @param itemData - The FileConfiguration reference for the items.yml.
     */
    private void parseData_4(final FileConfiguration itemData) {
        if (this.mobsDrop()) {
            List<String> mobsList = new ArrayList<>();
            for (EntityType mobs : this.mobsDrop.keySet()) {
                mobsList.add(mobs.name() + ":" + this.mobsDrop.get(mobs).toString());
            }
            itemData.set("items." + this.configName + ".mobs-drop", mobsList);
        }
        if (this.blocksDrop()) {
            List<String> blocksList = new ArrayList<>();
            for (Material blocks : this.blocksDrop.keySet()) {
                blocksList.add(blocks.name() + ":" + this.blocksDrop.get(blocks).toString());
            }
            itemData.set("items." + this.configName + ".blocks-drop", blocksList);
        }
        if (this.effect != null && !this.effect.isEmpty()) {
            StringBuilder effectList = new StringBuilder();
            for (PotionEffect effects : this.effect) {
                effectList.append((ServerUtils.hasPreciseUpdate("1_20_3") ? effects.getType().getKey().getKey() : LegacyAPI.getEffectName(effects.getType()))).append(":").append(effects.getAmplifier()).append(":").append(effects.getDuration()).append(", ");
            }
            itemData.set("items." + this.configName + ".potion-effects", effectList.substring(0, effectList.length() - 2));
        }
        if (this.attributes != null && !this.attributes.isEmpty()) {
            StringBuilder attributeList = new StringBuilder();
            for (String attribute : this.attributes.keySet()) {
                attributeList.append("{").append(attribute).append(":").append(this.attributes.get(attribute)).append("}, ");
            }
            itemData.set("items." + this.configName + ".attributes", attributeList.substring(0, attributeList.length() - 2));
        }
        if (this.contents != null && !this.contents.isEmpty()) {
            itemData.set("items." + this.configName + ".contents", this.contents);
        }
        if (this.nbtProperty != null && !this.nbtProperty.isEmpty()) {
            StringBuilder propertyList = new StringBuilder();
            for (Object property : this.nbtProperty.keySet()) {
                propertyList.append(property).append(":").append(this.nbtProperty.get(property)).append(", ");
            }
            itemData.set("items." + this.configName + ".properties", propertyList.substring(0, propertyList.length() - 2));
        }
        if (this.disposableConditions != null && !this.disposableConditions.isEmpty()) {
            if (disposableConditions.size() == 1) {
                itemData.set("items." + this.configName + ".disposable-conditions", this.disposableConditions.get(0));
            } else if (!triggerConditions.isEmpty()) {
                itemData.set("items." + this.configName + ".disposable-conditions", this.disposableConditions);
            }
        }
        if (this.disposableMessage != null && !this.disposableMessage.isEmpty()) {
            itemData.set("items." + this.configName + ".disposable-fail-message", this.disposableMessage);
        }
        if (this.triggerConditions != null && !this.triggerConditions.isEmpty()) {
            if (triggerConditions.size() == 1) {
                itemData.set("items." + this.configName + ".trigger-conditions", this.triggerConditions.get(0));
            } else {
                itemData.set("items." + this.configName + ".trigger-conditions", this.triggerConditions);
            }
        }
        if (this.triggerMessage != null && !this.triggerMessage.isEmpty()) {
            itemData.set("items." + this.configName + ".trigger-fail-message", this.triggerMessage);
        }
        if (this.commandConditions != null && !this.commandConditions.isEmpty()) {
            for (String property : this.commandConditions.keySet()) {
                if (this.commandConditions.get(property).size() == 1) {
                    itemData.set("items." + this.configName + property + "-conditions", this.commandConditions.get(property).get(0));
                } else if (!this.commandConditions.get(property).isEmpty()) {
                    itemData.set("items." + this.configName + property + "-conditions", this.commandConditions.get(property));
                }
            }
        }
        if (this.commandMessages != null && !this.commandMessages.isEmpty()) {
            for (String property : this.commandMessages.keySet()) {
                itemData.set("items." + this.configName + property + "-fail-message", this.commandMessages.get(property));
            }
        }
        if (this.enabledRegions != null && !this.enabledRegions.isEmpty()) {
            StringBuilder regionList = new StringBuilder();
            for (String region : this.enabledRegions) {
                regionList.append(region).append(", ");
            }
            itemData.set("items." + this.configName + ".enabled-regions", regionList.substring(0, regionList.length() - 2));
        }
        if (this.enabledWorlds != null && !this.enabledWorlds.isEmpty()) {
            StringBuilder worldList = new StringBuilder();
            for (String world : this.enabledWorlds) {
                worldList.append(world).append(", ");
            }
            if (!worldList.toString().startsWith("ALL") && !worldList.toString().startsWith("GLOBAL")) {
                itemData.set("items." + this.configName + ".enabled-worlds", worldList.substring(0, worldList.length() - 2));
            }
        }
        if (this.disabledWorlds != null && !this.disabledWorlds.isEmpty()) {
            StringBuilder worldList = new StringBuilder();
            for (String world : this.disabledWorlds) {
                worldList.append(world).append(", ");
            }
            if (!worldList.toString().startsWith("DISABLED") || !worldList.toString().startsWith("DISABLE")) {
                itemData.set("items." + this.configName + ".disabled-worlds", worldList.substring(0, worldList.length() - 2));
            }
        }
    }

    /**
     * Saves the ItemMap to the items.yml.
     */
    public void saveToConfig() {
        File itemFile = new File(ItemJoin.getCore().getPlugin().getDataFolder(), "items.yml");
        FileConfiguration itemData = YamlConfiguration.loadConfiguration(itemFile);
        this.renderItemStack();
        if (ItemJoin.getCore().getConfig("items.yml").getString("items." + this.configName) != null) {
            itemData.set("items." + this.configName, null);
        }
        parseData_1(itemData);
        {
            parseData_2(itemData);
            {
                parseData_3(itemData);
                {
                    parseData_4(itemData);
                }
            }
        }
        try {
            itemData.save(itemFile);
            ItemJoin.getCore().getConfiguration().getSource("items.yml");
            ItemJoin.getCore().getConfig("items.yml").options().copyDefaults(false);
        } catch (Exception e) {
            ItemJoin.getCore().getPlugin().getServer().getLogger().severe("Could not save the new custom item " + this.configName + " to the items.yml data file!");
            ServerUtils.sendDebugTrace(e);
        }
    }

    /**
     * Creates a Clone of the Current ItemMap instance.
     *
     * @return The newly Cloned ItemMap instance.
     */
    public ItemMap clone() {
        try {
            ItemMap clone = (ItemMap) super.clone();
            for (Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(clone, field.get(this));
            }
            return clone;
        } catch (Exception e) {
            ServerUtils.sendDebugTrace(e);
            return this;
        }
    }
}