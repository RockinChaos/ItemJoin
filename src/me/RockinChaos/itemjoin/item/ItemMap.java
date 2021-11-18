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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;

import me.RockinChaos.itemjoin.ChatToggleExecutor;
import me.RockinChaos.itemjoin.ChatToggleTab;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemCommand.Action;
import me.RockinChaos.itemjoin.item.ItemCommand.CommandSequence;
import me.RockinChaos.itemjoin.utils.ReflectionUtils;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.api.DependAPI;
import me.RockinChaos.itemjoin.utils.api.EffectAPI;
import me.RockinChaos.itemjoin.utils.api.LanguageAPI;
import me.RockinChaos.itemjoin.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.enchants.Glow;
import me.RockinChaos.itemjoin.utils.interfaces.menus.Menu;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.SQL;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class ItemMap {
	
	private String configName;
	private ConfigurationSection nodeLocation;
	private Integer probability = -1;
	
	private ItemStack tempItem = null;
	private ItemMeta tempMeta = null;
	private Material material = Material.AIR;
	private Short dataValue = 0;
	
	private String customName = null;
	private List < String > customLore = new ArrayList < String > ();

	private List<String> AllSlots = new ArrayList<String>();
	private Integer InvSlot = 0;
	private String CustomSlot = null;
	private boolean craftingItem = false;
	
	private boolean giveNext = false;
	private boolean moveNext = false;
	private boolean dropFull = false;
	
	private String Arbitrary = null;
	private String itemValue = null;
	
	private Integer count = 1;
	private Map < String, Double > attributes = new HashMap < String, Double > ();
	
	private Short durability = null;
	private Integer data = null;
	private Integer modelData = null;
	
	private String author;
	private String title;
	private Object generation;
	private List < String > bookPages = new ArrayList < String > ();
	private List < List <String> > listPages = new ArrayList < List <String> > ();
	
	private short mapId = -1;
	private MapView mapView = null;
	private String customMapImage = null;
    
    private FireworkEffect firework = null;
    private Type fireworkType = null;
    private boolean fireworkFlicker = false;
    private boolean fireworkTrail = false;
    private Integer power = 0;
    private List<DyeColor> fireworkColor = new ArrayList<DyeColor>();
    private DyeColor chargeColor = null;
    
    private String skullOwner = null;
    private String skullTexture = null;
    private boolean headDatabase = false;
    
    private List <PotionEffect> effect = new ArrayList<PotionEffect>();
    private List <Pattern> bannerPatterns = new ArrayList<Pattern>();
    
    private Map < Character, String > ingredients = new HashMap < Character, String > ();
    private List <Character> recipe = Arrays.asList( 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' );
    
    private String leatherColor;
    private String leatherHex;
    
    private String teleportEffect;
    private String teleportSound;
    
	private Integer interactCooldown = 0;
	private boolean customConsumable = false;
	private Map < String, Integer > enchants = new HashMap < String, Integer > ();
	
	private Map < String, String > nbtProperty = new HashMap < String, String > ();
	private List < Object > nbtProperties = new ArrayList < Object > ();
	private Map < String, Long > playersOnInteractCooldown = new HashMap < String, Long > ();
	private HashMap < String, Long > storedSpammedPlayers = new HashMap < String, Long > ();
	private int spamtime = 1;
	
	
//  ============================================== //
//         Drop Chances for each item.          //
//  ============================================== //
	private Map < EntityType, Double > mobsDrop = new HashMap < EntityType, Double > ();
	private Map < Material, Double > blocksDrop = new HashMap < Material, Double > ();
//  ============================================== //
	
//  ============================================== //
//         NBT Information for each item.          //
//  ============================================== //
    private String newNBTData;
    private Object newNBTTag;
    private String legacySecret;
//  ============================================== //
	
//  ============================================== //
//     ItemAnimation Information for each item.    //
//  ============================================== //
	private List < String > dynamicNames = new ArrayList < String > ();
	private List < List < String > > dynamicLores = new ArrayList < List <String> > ();
	private List < String > dynamicMaterials = new ArrayList < String > ();
	private List < String > dynamicOwners = new ArrayList < String > ();
	private List < String > dynamicTextures = new ArrayList < String > ();
	private boolean materialAnimated = false;
	private boolean skullAnimated = false;
	private Map < Player, ItemAnimation > localeAnimations = new HashMap < Player, ItemAnimation > ();
//  ====================================================================================================== //
	
	
//  ============================================== //
//      ItemCommand Information for each item.     //
//  ============================================== //
	private ItemCommand[] commands = new ItemCommand[0];
	private Integer cooldownSeconds = 0;
	private Integer commandsReceive = 0;
	private String cooldownMessage;
	private Sound commandSound;
	private String commandParticle;
	private String itemCost;
	private Integer cost = 0;
	private Integer warmDelay = 0;
	private List < Player > warmPending = new ArrayList < Player > ();
	private boolean useCooldown = false;
	private boolean subjectRemoval = false;
	private CommandSequence sequence;
	private List<String> disposableConditions = new ArrayList<String>();
	private List<String> triggerConditions = new ArrayList<String>();
	private Map < String, List<String> > commandConditions = new HashMap < String, List<String> > ();
	private String disposableMessage = null;
	private String triggerMessage = null;
	private Map < String, String > commandMessages = new HashMap < String, String > ();
	private Map < String, Long > playersOnCooldown = new HashMap < String, Long > ();
	private HashMap < String, Long > playersOnCooldownTick = new HashMap < String, Long > ();
//  ============================================================================================= //
	
	
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
	private boolean glowing = false;
	private boolean overwritable = false;
	private boolean blockPlacement = false;
	private boolean hideAttributes = false;
	private boolean hideFlags = false;
	private boolean hideDurability = false;
	private boolean blockEquip = false;
	private boolean blockMovement = false;
	private boolean closeInventory = false;
	private boolean selfDroppable = false;
	private boolean deathDroppable = false;
	private boolean deathKeepable = false;
	private boolean disposable = false;
	private boolean itemChangable = false;
	private boolean alwaysGive = false;
	private boolean autoRemove = false;
	private boolean stackable = false;
	private boolean selectable = false;
	private boolean CreativeBypass = false;
	private boolean AllowOpBypass = false;
	
	private boolean onlyFirstJoin = false;
    private boolean onlyFirstLife = false;
	private boolean onlyFirstWorld = false;
	private boolean ipLimited = false;
//  ============================================== //
	
//  ============================================== //
//             Triggers for each item.             //
//  ============================================== //
	private boolean giveOnDisabled = false;
	private boolean giveOnJoin = false;
	private boolean giveOnRespawn = false;
	private boolean giveOnWorldSwitch = false;
	private boolean giveOnRegionEnter = false;
	private boolean giveOnRegionLeave = false;
	private boolean giveOnRegionAccess = false;
	private boolean giveOnRegionEgress = false;
	private boolean useOnLimitSwitch = false;
	
	private String triggers = null;
	private String limitModes = null;
//  ============================================== //
	
	private String toggleNode = null;
	private String toggleMessage = null;
	private List<String> toggleCommands = new ArrayList < String > ();
	private List<PluginCommand> togglePlugins = new ArrayList < PluginCommand > ();
	
	private String permissionNode = null;
	private boolean permissionNeeded = false;
	private boolean opPermissionNeeded = false;
	
	private List < String > contents = new ArrayList < String > ();
	
	private List < String > enabledRegions = new ArrayList < String > ();
	private List < String > enabledWorlds = new ArrayList < String > ();
	private List < String > disabledWorlds = new ArrayList < String > ();
// ======================================================================================== //
	
   /**
    * Creates a new ItemMap instance.
    * Typically used in the creation of items.
    * 
    * @param designer - The ItemDesigner instance that is creating the ItemMap.
    * @param internalName - The node name of the ItemMap.
    * @param slot - The slot of the ItemMap.
    */
	public ItemMap(final String internalName, final String slot) {
        this.nodeLocation = ConfigHandler.getConfig().getItemSection(internalName);
        this.configName = internalName;
        this.setItemValue(ConfigHandler.getConfig().getItemID(slot));
        this.setSlot(slot);
        if (ItemHandler.isCraftingSlot(slot)) { this.craftingItem = true; }
        
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
	        this.setPermissionNeeded(ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items"));
	    	this.setOPPermissionNeeded(ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items-OP"));
        }
	}
	
   /**
    * Creates a new ItemMap instance.
    * Called when copying an ItemMap, sadly this has to exist.
    * 
    * Welcome to nothing!
    * 
    */
	public ItemMap() { }
//  ========================================================================================================= //
	
//  ============================================== //
//   Setter functions for first ItemMap creation.  //
//  ============================================== //
   /**
    * Sets the ItemMaps Multiple Slots.
    * 
    */
	private void setMultipleSlots() {
        if (this.nodeLocation.getString(".slot") != null && !this.nodeLocation.getString(".slot").isEmpty() && this.nodeLocation.getString(".slot").contains(",")) {
        	String[] slots = this.nodeLocation.getString(".slot").replace(" ", "").split(",");
			for (String slot: slots) { 
				if (slot.startsWith("C[") || slot.startsWith("C(")) { slot = slot.replace("C", "CRAFTING"); }
				if (slot.startsWith("CRAFTING")) { slot = slot.replace("(", "[").replace(")", "]"); }
				this.AllSlots.add(slot); 
			}
        }
	}
	
   /**
    * Sets the ItemMaps Commands Cost.
    * 
    */
	private void setCommandCost() {
		if (this.nodeLocation.getString("commands-item") != null && !this.nodeLocation.getString("commands-item").isEmpty()) { this.itemCost = this.nodeLocation.getString("commands-item"); }
		if (this.nodeLocation.getString("commands-cost") != null && StringUtils.isInt(this.nodeLocation.getString("commands-cost"))) { this.cost = this.nodeLocation.getInt("commands-cost"); }
	}
	
   /**
    * Sets the ItemMaps Commands Receive.
    * 
    */
	private void setCommandReceive() {
		if (this.nodeLocation.getString("commands-receive") != null && StringUtils.isInt(this.nodeLocation.getString("commands-receive"))) { this.commandsReceive = this.nodeLocation.getInt("commands-receive"); }
	}
	
   /**
    * Sets the ItemMaps Commands Warmup Delay.
    * 
    */
	private void setCommandWarmDelay() {
		if (this.nodeLocation.getString("commands-warmup") != null && StringUtils.isInt(this.nodeLocation.getString("commands-warmup"))) { this.warmDelay = this.nodeLocation.getInt("commands-warmup"); }
	}
	
   /**
    * Sets the ItemMaps Commands Sound.
    * 
    */
	private void setCommandSound() {
		try { if (this.nodeLocation.getString(".commands-sound") != null) { this.commandSound = Sound.valueOf(this.nodeLocation.getString(".commands-sound")); } } 
		catch (Exception e) { 
			ServerUtils.logSevere("{ItemMap} Your server is running MC " + ReflectionUtils.getServerVersion() + " and this version of Minecraft does not have the defined command-sound " + this.nodeLocation.getString(".commands-sound") + "."); 
			ServerUtils.sendDebugTrace(e);
		}
	}
	
   /**
    * Sets the ItemMaps Commands Particle.
    * 
    */
	private void setCommandParticle() {
		if (this.nodeLocation.getString(".commands-particle") != null) { this.commandParticle = this.nodeLocation.getString(".commands-particle"); }
	}
	
   /**
    * Sets the ItemMaps Commands Cooldown.
    * 
    */
	private void setCommandCooldown() {
		this.useCooldown = this.nodeLocation.getString("commands-cooldown") != null;
		if (this.useCooldown) { this.cooldownSeconds = this.nodeLocation.getInt("commands-cooldown"); }
		this.cooldownMessage = this.nodeLocation.getString("cooldown-message");
	}
	
   /**
    * Sets the ItemMaps Commands Sequence.
    * 
    */
	private void setCommandSequence() {
		if (this.nodeLocation.getString("commands-sequence") != null) { 
		    if (StringUtils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "SEQUENTIAL")) { this.sequence = CommandSequence.SEQUENTIAL; }
		    else if (StringUtils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "RANDOM_SINGLE")) { this.sequence = CommandSequence.RANDOM_SINGLE; }
		    else if (StringUtils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "RANDOM_LIST")) { this.sequence = CommandSequence.RANDOM_LIST; }
			else if (StringUtils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "RANDOM")) { this.sequence = CommandSequence.RANDOM; }
		    else if (StringUtils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "REMAIN")) { this.sequence = CommandSequence.REMAIN; }
		}
	}
	
   /**
    * Sets the ItemMaps Interact Cooldown.
    * 
    */
	private void setInteractCooldown() {
        if (this.nodeLocation.getString(".use-cooldown") != null) {
        	this.interactCooldown = this.nodeLocation.getInt(".use-cooldown");
        }
	}
	
   /**
    * Sets the ItemMaps Interact Cooldown.
    * 
    */
	private void setTeleportArrow() {
        this.teleportEffect = this.nodeLocation.getString(".teleport-effect");
        this.teleportSound = this.nodeLocation.getString(".teleport-sound");
	}
	
   /**
    * Sets the ItemMaps ItemFlags.
    * 
    */
	private void setItemflags() {
		if (this.nodeLocation.getString(".itemflags") != null) {
			this.itemflags = this.nodeLocation.getString(".itemflags");
			this.vanillaItem = StringUtils.containsIgnoreCase(this.itemflags, "vanilla");
			this.vanillaStatus = StringUtils.containsIgnoreCase(this.itemflags, "vanilla-status");
			this.vanillaControl = StringUtils.containsIgnoreCase(this.itemflags, "vanilla-control");
			this.disposable = StringUtils.containsIgnoreCase(this.itemflags, "disposable");
			this.blockPlacement = StringUtils.containsIgnoreCase(this.itemflags, "placement");
			this.blockMovement = StringUtils.containsIgnoreCase(this.itemflags, "inventory-modify") || StringUtils.containsIgnoreCase(this.itemflags, "inventory-close");
			this.blockEquip = StringUtils.containsIgnoreCase(this.itemflags, "cancel-equip");
			this.closeInventory = StringUtils.containsIgnoreCase(this.itemflags, "inventory-close");
			this.itemChangable = StringUtils.containsIgnoreCase(this.itemflags, "allow-modifications") || StringUtils.containsIgnoreCase(this.itemflags, "item-changable");
			this.alwaysGive = StringUtils.containsIgnoreCase(this.itemflags, "always-give");
			this.autoRemove = StringUtils.containsIgnoreCase(this.itemflags, "auto-remove");
			this.stackable = StringUtils.containsIgnoreCase(this.itemflags, "stackable");
			this.selectable = StringUtils.containsIgnoreCase(this.itemflags, "selectable");
			this.dynamic = StringUtils.containsIgnoreCase(this.itemflags, "dynamic");
			this.animate = StringUtils.containsIgnoreCase(this.itemflags, "animate");
			this.glowing = StringUtils.containsIgnoreCase(this.itemflags, "glowing") || StringUtils.containsIgnoreCase(this.itemflags, "glow");
			this.giveNext = StringUtils.containsIgnoreCase(this.itemflags, "give-next");
			this.moveNext = StringUtils.containsIgnoreCase(this.itemflags, "move-next");
			this.dropFull = StringUtils.containsIgnoreCase(this.itemflags, "drop-full");
			this.itemStore = StringUtils.containsIgnoreCase(this.itemflags, "item-store");
			this.itemModify = StringUtils.containsIgnoreCase(this.itemflags, "item-modifiable");
			this.noCrafting = StringUtils.containsIgnoreCase(this.itemflags, "item-craftable");
			this.noRepairing = StringUtils.containsIgnoreCase(this.itemflags, "item-repairable");
			this.cancelEvents = StringUtils.containsIgnoreCase(this.itemflags, "cancel-events");
			this.countLock = StringUtils.containsIgnoreCase(this.itemflags, "count-lock");
			this.teleportArrow = StringUtils.containsIgnoreCase(this.itemflags, "teleport");
			this.overwritable = StringUtils.containsIgnoreCase(this.itemflags, "overwrite");
			this.ipLimited = StringUtils.containsIgnoreCase(this.itemflags, "ip-limit");
			this.deathKeepable = StringUtils.containsIgnoreCase(this.itemflags, "death-keep");
			this.deathDroppable = StringUtils.containsIgnoreCase(this.itemflags, "death-drops");
			this.selfDroppable = StringUtils.containsIgnoreCase(this.itemflags, "self-drops");
			
			// Shared with Triggers //
			this.setOnlyFirstJoin((StringUtils.containsIgnoreCase(this.itemflags, "first-join")  ? true : this.onlyFirstJoin));
			this.setOnlyFirstLife((StringUtils.containsIgnoreCase(this.itemflags, "first-life")  ? true : this.onlyFirstLife));
			this.onlyFirstWorld = (StringUtils.containsIgnoreCase(this.itemflags, "first-world") ? true : this.onlyFirstWorld);
			this.AllowOpBypass = (StringUtils.containsIgnoreCase(this.itemflags, "AllowOpBypass") ? true : this.AllowOpBypass);
			this.CreativeBypass = (StringUtils.containsIgnoreCase(this.itemflags, "CreativeBypass") ? true : this.CreativeBypass);
		}
	}
	
   /**
    * Sets the ItemMaps Limit Modes.
    * 
    */
	private void setLimitModes() {
		this.limitModes = this.nodeLocation.getString(".limit-modes");
	}
	
   /**
    * Sets the ItemMaps Triggers.
    * 
    */
	private void setTriggers() {
		final String defaultTriggers = ConfigHandler.getConfig().getFile("config.yml").getString("Settings.Default-Triggers");
		this.triggers = (this.nodeLocation.getString("triggers") != null ? this.nodeLocation.getString("triggers") : (defaultTriggers != null && !defaultTriggers.isEmpty() ? defaultTriggers : "JOIN"));
		this.giveOnDisabled = StringUtils.splitIgnoreCase(this.triggers, "DISABLED", ",");
		this.giveOnJoin = StringUtils.splitIgnoreCase(this.triggers, "JOIN", ",");
		this.giveOnRespawn = StringUtils.splitIgnoreCase(this.triggers, "RESPAWN", ",");
		this.giveOnWorldSwitch = StringUtils.splitIgnoreCase(this.triggers, "WORLD-CHANGE", ",") || StringUtils.splitIgnoreCase(this.triggers, "WORLD-SWITCH", ",");
		this.giveOnRegionEnter = StringUtils.splitIgnoreCase(this.triggers, "REGION-ENTER", ",");
		this.giveOnRegionLeave = StringUtils.splitIgnoreCase(this.triggers, "REGION-REMOVE", ",") || StringUtils.splitIgnoreCase(this.triggers, "REGION-EXIT", ",") || StringUtils.splitIgnoreCase(this.triggers, "REGION-LEAVE", ",");
		this.giveOnRegionAccess = StringUtils.splitIgnoreCase(this.triggers, "REGION-ACCESS", ",");
		this.giveOnRegionEgress = StringUtils.splitIgnoreCase(this.triggers, "REGION-EGRESS", ","); 
		if (this.giveOnRegionAccess || this.giveOnRegionEgress) { this.giveOnRegionEnter = false; this.giveOnRegionLeave = false; }
		this.useOnLimitSwitch = StringUtils.splitIgnoreCase(this.triggers, "GAMEMODE-SWITCH", ",");
		this.setOnlyFirstJoin(StringUtils.splitIgnoreCase(this.triggers, "FIRST-JOIN", ","));
		this.setOnlyFirstLife(StringUtils.splitIgnoreCase(this.triggers, "FIRST-LIFE", ","));
		this.onlyFirstWorld = StringUtils.splitIgnoreCase(this.triggers, "FIRST-WORLD", ",");
	}
	
   /**
    * Sets the ItemMaps Stored Contents.
    * 
    */
	public void setContents() {
		if (this.material != null && StringUtils.containsIgnoreCase(this.getMaterial().toString(), "SHULKER") && this.nodeLocation.getString(".contents") != null && this.nodeLocation.getStringList(".contents") != null && !this.nodeLocation.getStringList(".contents").isEmpty()) {
			this.contents = this.nodeLocation.getStringList(".contents");
		} else if (this.material != null && !StringUtils.containsIgnoreCase(this.getMaterial().toString(), "SHULKER") && this.nodeLocation.getString(".contents") != null && this.nodeLocation.getStringList(".contents") != null && !this.nodeLocation.getStringList(".contents").isEmpty()) {
			ServerUtils.logWarn("{ItemMap} The item " + this.getConfigName() + " cannot have contents set as it does not support it.");
		}
	}
	
   /**
    * Sets the ItemMaps Enabled Regions.
    * 
    */
	private void setRegions() {
		if (this.nodeLocation.getString(".enabled-regions") != null && !this.nodeLocation.getString(".enabled-regions").isEmpty()) {
			String[] enabledParts = this.nodeLocation.getString(".enabled-regions").replace(" ,  ", ",").replace(" , ", ",").replace(",  ", ",").replace(", ", ",").split(",");
			for (String region: enabledParts) {
				this.enabledRegions.add(region); 
				DependAPI.getDepends(false).getGuard().addLocaleRegion(region);
			}
		} else if (isGiveOnRegionEnter() || isGiveOnRegionLeave()) { 
			DependAPI.getDepends(false).getGuard().addLocaleRegion("UNDEFINED"); 
			this.enabledRegions.add("UNDEFINED"); }
	}
	
   /**
    * Sets the ItemMaps Conditions.
    * 
    */
	private void setConditions() {
		if (this.nodeLocation.getString(".trigger-fail-message") != null && !this.nodeLocation.getString(".trigger-fail-message").isEmpty()) {
			this.triggerMessage = this.nodeLocation.getString(".trigger-fail-message");
		}
		if (this.nodeLocation.getStringList(".trigger-conditions") != null && !this.nodeLocation.getStringList(".trigger-conditions").isEmpty()) {
			this.triggerConditions = this.nodeLocation.getStringList(".trigger-conditions");
		} else if (this.nodeLocation.getString(".trigger-conditions") != null && !this.nodeLocation.getString(".trigger-conditions").isEmpty()) {
			this.triggerConditions.add(this.nodeLocation.getString(".trigger-conditions"));
		} 
		if (this.nodeLocation.getString(".disposable-fail-message") != null && !this.nodeLocation.getString(".disposable-fail-message").isEmpty()) {
			this.disposableMessage = this.nodeLocation.getString(".disposable-fail-message");
		}
		if (this.nodeLocation.getStringList(".disposable-conditions") != null && !this.nodeLocation.getStringList(".disposable-conditions").isEmpty()) {
			this.disposableConditions = this.nodeLocation.getStringList(".disposable-conditions");
		} else if (this.nodeLocation.getString(".disposable-conditions") != null && !this.nodeLocation.getString(".disposable-conditions").isEmpty()) {
			this.disposableConditions.add(this.nodeLocation.getString(".disposable-conditions"));
		}
		for (Action action : Action.values()) {
			if (this.nodeLocation.getString(action.config() + "-fail-message") != null && !this.nodeLocation.getString(action.config() + "-fail-message").isEmpty()) {
				this.commandMessages.put(action.config(), this.nodeLocation.getString(action.config() + "-fail-message"));
			}
			if (this.nodeLocation.getStringList(action.config() + "-conditions") != null && !this.nodeLocation.getStringList(action.config() + "-conditions").isEmpty()) {
				this.commandConditions.put(action.config(), this.nodeLocation.getStringList(action.config() + "-conditions"));
			} else if (this.nodeLocation.getString(action.config() + "-conditions") != null && !this.nodeLocation.getString(action.config() + "-conditions").isEmpty()) {
				List < String > commandCond = new ArrayList < String > ();
				commandCond.add(this.nodeLocation.getString(action.config() + "-conditions"));
				this.commandConditions.put(action.config(), commandCond);
			}
		}
	}
	
   /**
    * Sets the ItemMaps Enabled Worlds.
    * 
    */
	private void setWorlds() {
		SchedulerUtils.run(() -> {
			if (this.nodeLocation.getString(".enabled-worlds") != null && !this.nodeLocation.getString(".enabled-worlds").isEmpty()) {
				String[] enabledParts = this.nodeLocation.getString(".enabled-worlds").replace(" ,  ", ",").replace(" , ", ",").replace(",  ", ",").replace(", ", ",").split(",");
				for (String enabledWorld : enabledParts) {
					if (enabledWorld.equalsIgnoreCase("ALL") || enabledWorld.equalsIgnoreCase("GLOBAL")) {
						this.enabledWorlds.add("ALL");
					} else {
						for (World world: Bukkit.getServer().getWorlds()) {
							if (enabledWorld.equalsIgnoreCase(world.getName())) {
								this.enabledWorlds.add(world.getName());
							}
						}
					}
				}
			} else { this.enabledWorlds.add("ALL"); }
			if (this.nodeLocation.getString(".disabled-worlds") != null && !this.nodeLocation.getString(".disabled-worlds").isEmpty()) {
				String[] disabledParts = this.nodeLocation.getString(".disabled-worlds").replace(" ,  ", ",").replace(" , ", ",").replace(",  ", ",").replace(", ", ",").split(",");
				for (String disabledWorld : disabledParts) {
					if (disabledWorld.equalsIgnoreCase("ALL") || disabledWorld.equalsIgnoreCase("GLOBAL")) {
						this.disabledWorlds.add("ALL");
					} else {
						for (World world: Bukkit.getServer().getWorlds()) {
							if (disabledWorld.equalsIgnoreCase(world.getName())) {
								this.disabledWorlds.add(world.getName());
							}
						}
					}
				}
			}
		});
	}
	
   /**
    * Sets the Players On Cooldown from the SQLite Database.
    * 
    */
	private void setPlayersOnCooldown() {
		if (this.cooldownSeconds > 0) {
			List<DataObject> dataList = SQL.getData().getDataList(new DataObject(Table.ON_COOLDOWN, null, null, this.getConfigName(), String.valueOf(this.getCommandCooldown()), null));
			for (DataObject dataObject : dataList) {
				if (dataObject != null) {
					this.playersOnCooldown.put(dataObject.getPlayerId(), Long.parseLong(dataObject.getDuration()));
					SQL.getData().removeData(new DataObject(Table.ON_COOLDOWN, null, null, this.getConfigName(), String.valueOf(this.getCommandCooldown()), null));
				}
			}
		}
	}
	
   /**
    * Sets the ItemMaps Temporary ItemStack.
    * 
    */
	public void renderItemStack() {
        if (this.dataValue != null) {
        	this.tempItem = LegacyAPI.newItemStack(this.material, this.count, this.dataValue);
        } else { this.tempItem = new ItemStack(this.material, this.count); }
	}
//  ======================================================================================================================================================================================== //

//  ===================== //
//  ~ Setting Functions ~ //
//  ===================== //
   /**
    * Sets the Temporary ItemStack.
    * 
    * @param temp - The ItemStack to be set.
    */
	public void setTempItem(final ItemStack temp) {
		this.tempItem = temp;
	}
	
   /**
    * Sets the Temporary ItemMeta.
    * 
    * @param temp - The ItemMeta to be set.
    */
	public void setTempMeta(final ItemMeta temp) {
		this.tempMeta = temp;
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
    * Sets the Custom Display Name.
    * 
    * @param customName - The Display Name to be set.
    */
	public void setCustomName(final String customName) {
		this.customName = customName;
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
    * Sets the Custom Display Lore.
    * 
    * @param customLore - The Display Lore to be set.
    */
	public void setCustomLore(final List < String > customLore) {
		this.customLore = new ArrayList < String > ();
		Iterator < String > iterator = customLore.iterator();
		while (iterator.hasNext()) {
			String s = iterator.next();
			this.customLore.add(s);
		}
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
    * Sets the Dynamic Materials.
    * 
    * @param mats - The Dynamic Materials to be set.
    */
	public void setDynamicMaterials(final List<String> mats) {
		this.dynamicMaterials = mats;
		this.materialAnimated = true;
	}
	
   /**
    * Sets the Dynamic Skull Owners.
    * 
    * @param owners - The Dynamic Skull Owners to be set.
    */
	public void setDynamicOwners(final List<String> owners) {
		this.dynamicOwners = owners;
		this.skullAnimated = true;
	}
	
   /**
    * Sets the Dynamic Skull Textures.
    * 
    * @param textures - The Dynamic Skull Textures to be set.
    */
	public void setDynamicTextures(final List<String> textures) {
		this.dynamicTextures = textures;
		this.skullAnimated = true;
	}
	
   /**
    * Removes the Player from the AnimationHandler.
    * 
    * @param player - The Player to be removed.
    */
	public void removeFromAnimationHandler(final Player player) {
		this.localeAnimations.remove(player);
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
    * Sets the Multiple Slots.
    * 
    * @param slots - The Multiple Slots to be set.
    */
	public void setMultipleSlots(final List<String> slots) {
		this.AllSlots = slots;
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
    * Sets the Enabled Worlds.
    * 
    * @param worlds - The Enabled Worlds to be set.
    */
	public void setEnabledWorlds(final List<String> worlds) {
		this.enabledWorlds = worlds;
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
    * Sets the Stored Contents.
    * 
    * @param contents - The Stored Contents to be set.
    */
	public void setContents(final List<String> contents) {
		this.contents = contents;
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
    * Sets the ItemFlags.
    * 
    * @param itemflags - The ItemFlags to be set.
    */
	public void setItemFlags(final String itemflags) {
		this.itemflags = itemflags;
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
    * Sets the CommandSequence.
    * 
    * @param sequence - The CommandSequence to be set.
    */
	public void setCommandSequence(final CommandSequence sequence) {
		this.sequence = sequence;
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
    * Sets the Commands Cooldown.
    * 
    * @param i - The Commands Cooldown Seconds.
    */
	public void setCommandCooldown(final int i) {
		this.cooldownSeconds = i;
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
    * Sets the Trigger Message.
    * 
    * @param s - The Trigger Message to be set.
    */
	public void setTriggerMessage(final String s) {
		this.triggerMessage = s;
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
    * Sets the Disposable Message.
    * 
    * @param s - The Disposable Message to be set.
    */
	public void setDisposableMessage(final String s) {
		this.disposableMessage = s;
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
    * Sets the Commands Messages.
    * 
    * @param s - The Commands Messages to be set.
    */
	public void setCommandMessages(final Map<String, String> s) {
		this.commandMessages = s;
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
    * Sets the stack size.
    * 
    * @param count - The stack size to be set.
    */
	public void setCount(final String count) {
		if (count != null && StringUtils.isInt(count) && Integer.parseInt(count) != 0) {
			this.count = Integer.parseInt(count);
		} else { this.count = 1; }
	}
	
   /**
    * Sets the ItemStack attributes.
    * 
    * @param attributeList - The list of attributes to be set.
    */
	public void setAttributes(final Map < String, Double > attributeList) {
		if (attributeList != null && !attributeList.isEmpty()) {
			this.attributes = attributeList;
		}
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
    * Sets the ItemStack Data.
    * 
    * @param data - The ItemStack Data to be set.
    */
	public void setData(final Integer data) {
		this.data = data;
	}
	
   /**
    * Sets the ItemStack Model Data.
    * 
    * @param data - The ItemStack Model Data to be set.
    */
	public void setModelData(final Integer data) {
		this.modelData = data;
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
    * Sets the BlocksDrop.
    * 
    * @param blocksDrop - The blocksDrop to be set.
    */
	public void setBlocksDrop(final Map<Material, Double> blocksDrop) {
		this.blocksDrop = blocksDrop;
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
    * Sets the Commands Sound.
    * 
    * @param sound - The Commands Sound to be set.
    */
	public void setCommandSound(final Sound sound) {
		this.commandSound = sound;
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
    * Sets the Commands Item Cost.
    * 
    * @param itemCost - The Commands Item Cost to be set.
    */
	public void setItemCost(final String itemCost) {
		this.itemCost = itemCost;
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
        if (this.warmPending.contains(player)) {
        	this.warmPending.remove(player);
        }
	}
	
   /**
    * Sets the Config Name.
    * 
    * @param name - The Config Name to be set.
    */
	public void setConfigName(final String name) {
		this.configName = name;
	}
	
   /**
    * Sets the Banner Patterns.
    * 
    * @param patterns - The Banner Patterns to be set.
    */
	public void setBannerPatterns(final List <Pattern> patterns) {
		this.bannerPatterns = patterns;
	}
	
   /**
    * Sets the recipe ingredients.
    * 
    * @param ingredients - The recipe ingredients to be set.
    */
	public void setIngredients(final Map <Character, String> ingredients) {
		this.ingredients = ingredients;
	}
	
   /**
    * Sets the recipe pattern.
    * 
    * @param recipe - The recipe pattern to be set.
    */
	public void setRecipe(final List <Character> recipe) {
		this.recipe = recipe;
	}
	
   /**
    * Sets the Node Location.
    * 
    * @param node - The Node Location to be set.
    */
	public void setNodeLocation(final ConfigurationSection node) {
		this.nodeLocation = node;
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
    * Sets the Permission.
    * 
    * @param permission - The Permission to be set.
    */
	public void setPerm(final String permission) {
		this.permissionNode = permission == null || permission.length() == 0 ? null : permission;
	}
	
   /**
    * Sets the Toggle Permission.
    * 
    * @param permission - The Permission to be set.
    */
	public void setTogglePerm(final String permission) {
		this.toggleNode = permission == null || permission.length() == 0 ? null : permission;
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
    * Sets the ItemStack to be given only on First Join.
    * 
    * @param bool - The value to be set.
    */
	public void setOnlyFirstJoin(final boolean bool) {
		this.onlyFirstJoin = bool;
		if (bool && !this.giveOnRegionEnter && !this.giveOnRegionLeave && !this.giveOnRegionAccess && !this.giveOnRegionEgress) { 
			this.giveOnJoin = true; 
			this.giveOnRespawn = false; 
		}
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
		}
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
		}
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
    * Sets the ItemStack to be given only on World Switch.
    * 
    * @param bool - The value to be set.
    */
	public void setGiveOnWorldSwitch(final boolean bool) {
		this.giveOnWorldSwitch = bool;
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
    * Sets the ItemStack to be given only on Region Enter.
    * 
    * @param bool - The value to be set.
    */
	public void setGiveOnRegionEnter(final boolean bool) {
		this.giveOnRegionEnter = bool;
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
    * Sets the ItemStack to be given only on region enter and removed on region leave.
    * 
    * @param bool - The value to be set.
    */
	public void setGiveOnRegionAccess(final boolean bool) {
		this.giveOnRegionAccess = bool;
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
    * Sets the ItemStack to not be given.
    * 
    * @param bool - The value to be set.
    */
	public void setGiveOnDisabled(final boolean bool) {
		this.giveOnDisabled = bool;
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
    * Sets the IP Limits.
    * 
    * @param bool - The value to be set.
    */
	public void setIpLimited(final boolean bool) {
		this.ipLimited = bool;
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
    * Sets the Teleport Effect.
    * 
    * @param name - The value to be set.
    */
	public void setTeleportEffect(final String name) {
        this.teleportEffect = name;
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
    * Sets the Permissions to be Required.
    * 
    * @param bool - The value to be set.
    */
	public void setPermissionNeeded(final boolean bool) {
		this.permissionNeeded = bool;
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
    * Sets the Vanilla Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setVanilla(final boolean bool) {
		this.vanillaItem = bool;
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
    * Sets the Vanilla Control Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setVanillaControl(final boolean bool) {
		this.vanillaControl = bool;
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
    * Sets the Move Next Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setMoveNext(final boolean bool) {
		this.moveNext = bool;
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
    * Sets the Unbreakable Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setUnbreakable(final boolean bool) {
		this.unbreakable = bool;
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
    * Sets the Teleport Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setTeleport(final boolean bool) {
		this.teleportArrow = bool;
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
    * Sets the Item Store Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setItemStore(final boolean bool) {
		this.itemStore = bool;
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
    * Sets the Item Craftable Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setItemCraftable(final boolean bool) {
		this.noCrafting = bool;
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
    * Sets the Item Changable Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setItemChangable(final boolean bool) {
		this.itemChangable = bool;
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
    * Sets the Auto Remove Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setAutoRemove(final boolean bool) {
		this.autoRemove = bool;
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
    * Sets the Selectable Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setSelectable(final boolean bool) {
		this.selectable = bool;
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
    * Sets the Dynamic Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setDynamic(final boolean bool) {
		this.dynamic = bool;
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
    * Sets the Overwritable Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setOverwritable(final boolean bool) {
		this.overwritable = bool;
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
    * Sets the Attributes Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setAttributesInfo(final boolean bool) {
		this.hideAttributes = bool;
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
    * Sets the Durability Bar Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setDurabilityBar(final boolean bool) {
		this.hideDurability = bool;
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
    * Sets the Equip Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setEquip(final boolean bool) {
		this.blockEquip = bool;
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
    * Sets the Dispoable Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setDisposable(final boolean bool) {
		this.disposable = bool;
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
    * Sets the Death Droppable Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setDeathDroppable(final boolean bool) {
		this.deathDroppable = bool;
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
    * Sets the CreativeBypass Flag.
    * 
    * @param bool - The value to be set.
    */
	public void setCreativeBypass(final boolean bool) {
		this.CreativeBypass = bool;
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
    * Sets the Book Author.
    * 
    * @param auth - The Book Author to be set.
    */
	public void setAuthor(final String auth) {
		this.author = auth;
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
    * Sets the Book Generation.
    * 
    * @param gen - The Book Generation to be set.
    */
	public void setGeneration(final org.bukkit.inventory.meta.BookMeta.Generation gen) {
		this.generation = gen;
	}
	
   /**
    * Sets the Book Pages.
    * 
    * @param pages - The Book Pages to be set.
    */
	public void setPages(final List <String> pages) {
		this.bookPages = pages;
	}
	
   /**
    * Sets the Book List Pages.
    * 
    * @param pages - The Book List Pages to be set.
    */
	public void setListPages(final List <List <String> > pages) {
		this.listPages = pages;
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
    * Sets the MapView.
    * 
    * @param view - The MapView to be set.
    */
	public void setMapView(final MapView view) {
		this.mapView = view;
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
    * Sets the FireworkEffect.
    * 
    * @param fire - The FireworkEffeect to be set.
    */
	public void setFirework(final FireworkEffect fire) {
		this.firework = fire;
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
    * Sets the Firework Colors.
    * 
    * @param colors - The Firework Colors to be set.
    */
	public void setFireworkColor(final List<DyeColor> colors) {
		this.fireworkColor = colors;
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
    * Sets the Firework Trail.
    * 
    * @param bool - The value to be set.
    */
	public void setFireworkTrail(final boolean bool) {
		this.fireworkTrail = bool;
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
    * Sets the Firework Charge Color.
    * 
    * @param dyeColor - The Firework Charge Color to be set.
    */
	public void setChargeColor(final DyeColor dyeColor) {
		this.chargeColor = dyeColor;
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
    * Sets the Skull Texture.
    * 
    * @param skull - The Skull Texture to be set.
    */
	public void setSkullTexture(final String skull) {
		this.skullTexture = skull;
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
    * Sets the PotionEffect.
    * 
    * @param pootion - The PortionEffect to be set.
    */
	public void setPotionEffect(final List <PotionEffect> potion) {
		this.effect = potion;
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
    * Sets the Leather HexColor.
    * 
    * @param hex - The Leather HexColor to be set.
    */
	public void setLeatherHex(final String hex) {
		this.leatherHex = hex;
	}
	
   /**
    * Sets the NBTData.
    * 
    * @param nbt - The NBT Data to be set.
    * @oaram tag - The Object Tag to be set.
    */
	public void setNewNBTData(final String nbt, final Object tag) {
		this.newNBTData = nbt;
		this.newNBTTag = tag;
	}
	
   /**
    * Sets the NBT Properties.
    * 
    * @oaram tags - The Object Tags to be set.
    */
	public void setNBTProperties(final Map<String, String> tagValues, final List<Object> tags) {
		this.nbtProperty = tagValues;
		this.nbtProperties = tags;
	}
	
   /**
    * Sets the NBT Values.
    * 
    * @oaram tags - The Object Tags to be set.
    */
	public void setNBTValues(final Map<String, String> tagValues) {
		this.nbtProperty = tagValues;
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
    * Sets the Arbitrary.
    * 
    * @param arb - The Arbitrary to be set.
    */
	public void setArbitrary(final String arb) {
		this.Arbitrary = arb;
	}
	
   /**
    * Sets the Item Value (Arbitrary ID).
    * 
    * @param str - The Item Value to be set.
    */
	public void setItemValue(final String str) {
		this.itemValue = str;
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
    * Sets the ItemCommands.
    * 
    * @param commands - The ItemCommands to be set.
    */
    public void setCommands(final ItemCommand[] commands) {
        this.commands = commands;
    }
    
   /**
    * Sets the Toggle Commands.
    * 
    * @param commands - The Toggle Commands to be set.
    */
    private void setToggleCommands(final String toggleSingle) {
    	final List<String> commandList = new ArrayList<String>();
    	if ((toggleSingle != null && this.nodeLocation.getStringList(".toggle") != null && !this.nodeLocation.getStringList(".toggle").isEmpty())
    			|| (toggleSingle == null && this.toggleCommands != null && !this.toggleCommands.isEmpty())) {
    		for (String command : (toggleSingle != null ? this.nodeLocation.getStringList(".toggle") : this.toggleCommands)) {
    			PluginCommand cmd = null;
    			try {
    				Constructor<PluginCommand> pluginCommand = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
    				pluginCommand.setAccessible(true);
    				cmd = (PluginCommand)pluginCommand.newInstance(new Object[]{(command.contains(" ") ? command.split(" ")[0] : command), ItemJoin.getInstance()});
    			} catch (Exception e) {
    				ServerUtils.sendDebugTrace(e);
    			}
	    		cmd.setDescription(this.configName);
            	cmd.setExecutor(new ChatToggleExecutor());
            	cmd.setTabCompleter(new ChatToggleTab());
	    		this.togglePlugins.add(cmd);
	    		commandList.add(command);
    		}
    	} else if (toggleSingle != null && !toggleSingle.isEmpty() && !toggleSingle.equalsIgnoreCase(" ")) {
			PluginCommand cmd = null;
		    try {
		    	Constructor<PluginCommand> pluginCommand = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
		    	pluginCommand.setAccessible(true);
		    	cmd = (PluginCommand)pluginCommand.newInstance(new Object[]{(toggleSingle.contains(" ") ? toggleSingle.split(" ")[0] : toggleSingle), ItemJoin.getInstance()});
		    } catch (Exception e) {
		    	ServerUtils.sendDebugTrace(e);
		    }
    		cmd.setDescription(this.configName);
            cmd.setExecutor(new ChatToggleExecutor());
            cmd.setTabCompleter(new ChatToggleTab());
    		this.togglePlugins.add(cmd);
    		commandList.add(toggleSingle);
    	}
    	ServerUtils.registerCommands(this.togglePlugins);
        this.toggleCommands = commandList;
    }
    
   /**
    * Sets the Toggle Commands.
    * 
    */
	public void setToggleCommands(final List<String> cmds) {
		this.toggleCommands = cmds;
		this.setToggleCommands((String)null);
	}
    
   /**
    * Deletes the Toggle Commands.
    * 
    * @param commands - The Toggle Commands to be deleted.
    */
    public void delToggleCommands() {
    	if (this.togglePlugins != null && !this.togglePlugins.isEmpty()) {
    		ServerUtils.unregisterCommands(this.togglePlugins);
    	}
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
    * Sets the Commands Receive.
    * 
    * @param val - The Commands Recieve to be set.
    */
	public void setCommandReceive(final Integer val) {
		this.commandsReceive = val;
	}
    
//  ================================================================================================================================================================================= //
	
//  ====================== //
//  ~ Accessor Functions ~ //
//  ====================== //
   /**
    * Gets the Custom Display Name.
    * 
    * @return The Custom Display Name.
    */
	public String getCustomName() {
		return this.customName;
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
    * Gets the Custom Display Lore.
    * 
    * @return The Custom Display Lore.
    */
	public List<String> getCustomLore() {
		return this.customLore;
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
    * Gets the Dynamic Materials.
    * 
    * @return The Dynamic Materials.
    */
	public List<String> getDynamicMaterials() {
		return this.dynamicMaterials;
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
    * Gets the Dynamic Skull Textures.
    * 
    * @return The Dynamic Skull Textures.
    */
	public List<String> getDynamicTextures() {
		return this.dynamicTextures;
	}
	
   /**
    * Gets the AnimationHandlers.
    * 
    * @return The AnimationsHandlers.
    */
	public Map<Player, ItemAnimation> getAnimationHandler() {
		return this.localeAnimations;
	}
	
   /**
    * Gets the Slot.
    * 
    * @return The Slot.
    */
	public String getSlot() {
		if (this.CustomSlot != null) { return this.CustomSlot; } 
		else if (this.InvSlot != null) { return this.InvSlot.toString(); }
		return null;
	}
	
   /**
    * Gets the Slot.
    * 
    * @return The Slot.
    */
	public String getUISlot() {
		if (this.CustomSlot != null && !this.CustomSlot.contains("%")) { return this.CustomSlot; } 
		else if (this.CustomSlot != null) { return (0 + ""); }
		else if (this.InvSlot != null) { return this.InvSlot.toString(); }
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
    * Gets the Disabled Worlds.
    * 
    * @return The Disabled Worlds.
    */
	public List<String> getDisabledWorlds() {
		return this.disabledWorlds;
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
    * Gets the Enabled Regions.
    * 
    * @return The Enabled Regions.
    */
	public List<String> getEnabledRegions() {
		return this.enabledRegions;
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
    * Gets the Enchantments.
    * 
    * @return The Enchantments.
    */
	public Map<String, Integer> getEnchantments() {
		return this.enchants;
	}
	
   /**
    * Gets the stack size.
    * 
    * @return The stack size.
    */
	public Integer getCount() {
		return this.count;
	}
	
   /**
    * Gets the attribute list.
    * 
    * @return The attribute list.
    */
	public Map < String, Double > getAttributes() {
		return this.attributes;
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
    * Gets the Triggers.
    * 
    * @return The Triggers.
    */
	public String getTriggers() {
		return this.triggers;
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
    * Gets the ItemStacks Data Value.
    * 
    * @return The ItemStacks Data Value.
    */
	public Short getDataValue() {
		return this.dataValue;
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
    * Gets the ItemStack Model Data.
    * 
    * @return The ItemStack Model Data.
    */
	public Integer getModelData() {
		if (this.modelData != null) {
			return this.modelData;	
		}
		return 0;
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
		if (this.probability != null) {
			return this.probability;	
		}
		return 0;
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
    * Gets the Commands Warmup Delay.
    * 
    * @return The Commands Warmup Delay.
    */
	public Integer getWarmDelay() {
		return this.warmDelay;
	}
	
   /**
    * Checks if the Player is Pending Warmup.
    * 
    * @param player - The Player to be checked.
    */
	private boolean getWarmPending(final Player player) {
        if (this.warmPending.contains(player)) { return true; }
        return false;
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
    * Gets the Temporary ItemMeta.
    * 
    * @return The Temporary ItemMeta.
    */
	public ItemMeta getTempMeta() {
		return this.tempMeta;	
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
	public List <Pattern> getBannerPatterns() {
		return this.bannerPatterns;
	}
	
   /**
    * Gets the recipe ingredients.
    * 
    * @return The recipe ingredients.
    */
	public Map<Character, String> getIngredients() {
		return this.ingredients;
	}
	
   /**
    * Gets the recipe pattern.
    * 
    * @return The recipe pattern.
    */
	public List<Character> getRecipe() {
		return this.recipe;
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
    * Gets the ItemCommands.
    * 
    * @return The ItemCommands.
    */
	public ItemCommand[] getCommands() {
		return this.commands;
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
    * Gets the Commands Cooldown.
    * 
    * @return The Commands Cooldown.
    */
	public Integer getCommandCooldown() {
		return this.cooldownSeconds;
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
    * Gets the Commands Receive.
    * 
    * @return The Commands Receive.
    */
	public Integer getCommandReceive() {
		return this.commandsReceive;
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
    * Gets the Commands Particle.
    * 
    * @return The Commands Particle.
    */
	public String getCommandParticle() {
		return this.commandParticle;
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
    * Gets the Book Author.
    * 
    * @return The Book Author.
    */
	public String getAuthor() {
		return this.author;
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
    * Gets the Book Generation.
    * 
    * @return The Book Generation.
    */
	public org.bukkit.inventory.meta.BookMeta.Generation getGeneration() {
		return (Generation) this.generation;
	}
	
   /**
    * Gets the Book Pages.
    * 
    * @return The Book Pages.
    */
	public List <String> getPages() {
		return this.bookPages;
	}
	
   /**
    * Gets the Book List Pages.
    * 
    * @return The Book List Pages.
    */
	public List <List <String> > getListPages() {
		return this.listPages;
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
    * Gets the MapView.
    * 
    * @return The MapView.
    */
	public MapView getMapView() {
		return this.mapView;
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
    * Gets the FireworkEffect.
    * 
    * @return The FireworkEffect.
    */
	public FireworkEffect getFirework() {
		return this.firework;
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
    * Gets the Firework Power.
    * 
    * @return The Firework Power.
    */
	public int getFireworkPower() {
		return this.power;
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
    * Gets the Firework Trail.
    * 
    * @return The Firework Trail.
    */
	public boolean getFireworkTrail() {
		return this.fireworkTrail;
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
    * Gets the Firework Charge Color.
    * 
    * @return The Firework Charge Color.
    */
	public DyeColor getChargeColor() {
		return this.chargeColor;
	}
	
   /**
    * Gets the Skull Owner.
    * 
    * @return The Skull Owner.
    */
	public String getSkull() {
		return this.skullOwner;
	}
	
   /**
    * Gets the Skull Texture.
    * 
    * @return The Skull Texture.
    */
	public String getSkullTexture() {
		return this.skullTexture;
	}
	
   /**
    * Gets the PotionEffects.
    * 
    * @return The PotionEffects.
    */
	public List <PotionEffect> getPotionEffect() {
		return this.effect;
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
    * Gets the Leather HexColor.
    * 
    * @return The Leather HexColor.
    */
	public String getLeatherHex() {
		return this.leatherHex;
	}
	
   /**
    * Gets the NBTData.
    * 
    * @return The NBTData.
    */
	public String getNewNBTData() {
		return this.newNBTData;
	}
	
   /**
    * Gets the NBT Properties.
    * 
    * @return The NBT Properties.
    */
	public List<Object> getNBTProperties() {
		return this.nbtProperties;
	}
	
   /**
    * Gets the NBT Values.
    * 
    * @return The NBT Values.
    */
	public Map<String, String> getNBTValues() {
		return this.nbtProperty;
	}
	
   /**
    * Gets the NBTData (Secret).
    * 
    * @return The NBTData (Secret).
    */
	public String getLegacySecret() {
		if (!ItemHandler.dataTagsEnabled()) {
			return this.legacySecret;
		} else { return null; }
	}
	
   /**
    * Gets the Arbitrary.
    * 
    * @return The Arbitrary.
    */
	public String getArbitrary() {
		return this.Arbitrary;
	}
	
   /**
    * Gets the Item Value (Arbitrary ID).
    * 
    * @return The Item Value (Arbitrary ID).
    */
	public String getItemValue() {
		return this.itemValue;
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
    * Gets the Teleport Effect.
    * 
    * @return The Teleport Effect.
    */
	public String getTeleportEffect() {
		return this.teleportEffect;
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
    * Gets the Commands Cooldown Message.
    * 
    * @return The Commands Cooldown Message.
    */
	public String getCooldownMessage() {
		return this.cooldownMessage;
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
    * Gets the Trigger Conditions.
    * 
    * @return The Trigger Conditions.
    */
	public List<String> getTriggerConditions() {
		return this.triggerConditions;
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
    * Gets the Disposable Conditions.
    * 
    * @return The Disposable Conditions.
    */
	public List<String> getDisposableConditions() {
		return this.disposableConditions;
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
    * Gets the Commands Conditions.
    * 
    * @return The Commands Conditions.
    */
	public Map<String, List<String>> getCommandConditions() {
		return this.commandConditions;
	}
	
   /**
    * Gets the NBTData that should be set to be set to the custom item.
    * 
    * @param itemMap - The ItemMap to have its NBTData defined.
    * @return The NBTData format to be set to an item.
    */
	public String getNBTFormat() {
		return this.getItemValue() + this.getConfigName();
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
    * Checks if the Player has Permission.
    * 
    * @param player - The Player that should have Permission.
    * @param world - The world to check permission.
    * @return If the Player has Permission.
    */
	public boolean hasPermission(final Player player, final World world) {
		String customPerm = PermissionsHandler.customPermissions(this.permissionNode, this.configName, world.getName());
		if (!this.isPermissionNeeded() && !player.isOp() || (!this.isOPPermissionNeeded() && player.isOp())) {
			return true;
		} else if (this.isOPPermissionNeeded() && player.isOp()) {
			if ((player.isPermissionSet(customPerm) && player.hasPermission(customPerm) && (!player.isPermissionSet("itemjoin." + world.getName() + ".*") 
			|| (player.isPermissionSet("itemjoin." + world.getName() + ".*") && player.hasPermission("itemjoin." + world.getName() + ".*"))) 
			|| ((player.isPermissionSet("itemjoin." + world.getName() + ".*") && player.hasPermission("itemjoin." + world.getName() + ".*")) || (player.isPermissionSet(customPerm) && player.hasPermission(customPerm))))) {
				return true;
			}
		} else if (((player.isPermissionSet("itemjoin." + world.getName() + ".*") && player.hasPermission("itemjoin." + world.getName() + ".*")) || (player.isPermissionSet(customPerm) && player.hasPermission(customPerm)))) {
			return true;
		}
		return false;
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
    * Checks if give on join is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isGiveOnJoin() {
		return this.giveOnJoin;
	}
	
   /**
    * Checks if give on world switch is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isGiveOnWorldSwitch() {
		return this.giveOnWorldSwitch;
	}
	
   /**
    * Checks if give on respawn is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isGiveOnRespawn() {
		return this.giveOnRespawn;
	}
	
   /**
    * Checks if give on region enter is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isGiveOnRegionEnter() {
		return this.giveOnRegionEnter;
	}
	
   /**
    * Checks if give on region leave is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isGiveOnRegionLeave() {
		return this.giveOnRegionLeave;
	}
	
   /**
    * Checks if give on region enter and remove on region leave is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isGiveOnRegionAccess() {
		return this.giveOnRegionAccess;
	}
	
   /**
    * Checks if give on region leave and remove on region enter is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isGiveOnRegionEgress() {
		return this.giveOnRegionEgress;
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
    * Checks if give on first join is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isOnlyFirstJoin() {
		return this.onlyFirstJoin;
	}
	
   /**
    * Checks if give on first life is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isOnlyFirstLife() {
		return this.onlyFirstLife;
	}
	
   /**
    * Checks if give on first world is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isOnlyFirstWorld() {
		return this.onlyFirstWorld;
	}
	
   /**
    * Checks if ip limit is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isIpLimted() {
		return this.ipLimited;
	}
	
   /**
    * Checks if use on limit switch is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isUseOnLimitSwitch() {
		return this.useOnLimitSwitch;
	}
	
   /**
    * Checks if the GameMode is a limit mode.
    * 
    * @return If the GameMode is a limit mode.
    */
	public boolean isLimitMode(final GameMode newMode) {
		if (this.limitModes != null) {
			if (StringUtils.containsIgnoreCase(this.limitModes, newMode.name())) {
				return true;
			} else if (!StringUtils.containsIgnoreCase(this.limitModes, newMode.name())) {
				return false;
			}
		}
		return true;
	}
	
   /**
    * Checks if the region is an enabled region.
    * 
    * @return If the region is a enabled region.
    */
	public Boolean inRegion(final String region) {
		if (this.enabledRegions == null) { return false; }
			for (String compareRegion: this.enabledRegions) {
				if (compareRegion.equalsIgnoreCase(region) || compareRegion.equalsIgnoreCase("UNDEFINED")) {
					return true;
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
    * Checks if OP Permissions are Required.
    * 
    * @return If OP Permissions are Required.
    */
	public boolean isOPPermissionNeeded() {
		return this.opPermissionNeeded;
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
    * Checks if the Vanilla Status Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isVanillaStatus() {
		return this.vanillaStatus;
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
    * Checks if the Give Next Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isGiveNext() {
		return this.giveNext;
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
    * Checks if the Drop Full Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isDropFull() {
		return this.dropFull;
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
    * Checks if the Count Lock Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isCountLock() {
		return this.countLock;
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
    * Checks if the Cancel Events Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isCancelEvents() {
		return this.cancelEvents;
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
    * Checks if the Item Modify Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isItemModify() {
		return this.itemModify;
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
    * Checks if the Item Repairable Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isItemRepairable() {
		return this.noRepairing;
	}
	
   /**
    * Checks if the Item Changable Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isItemChangable() {
		return this.itemChangable;
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
    * Checks if the Auto Remove Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isAutoRemove() {
		return this.autoRemove;
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
    * Checks if the Selectable Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isSelectable() {
		return this.selectable;
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
    * Checks if the Glowing Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isGlowing() {
		return this.glowing;
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
    * Checks if the Placement Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isPlaceable() {
		return this.blockPlacement;
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
    * Checks if the Attributes Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isFlagsInfo() {
		return this.hideFlags;
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
    * Checks if the Movement Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isMovement() {
		return this.blockMovement;
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
    * Checks if the Self Droppable Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isSelfDroppable() {
		return this.selfDroppable;
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
    * Checks if the Death Keepable Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isDeathKeepable() {
		return this.deathKeepable;
	}
	
   /**
    * Checks if the CeativeBypass Flag is enabled.
    * 
    * @return If it is enabled.
    */
	public boolean isCreativeBypass() {
		return this.CreativeBypass;
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
    * Checks if the ItemStack is subject to removal. 
    * Prevents Duplication.
    * 
    * @return If the ItemStack is pending removal.
    */
	private boolean isSubjectRemoval() {
		return this.subjectRemoval;
	}
	
   /**
    * Checks if the String World Name is an Disabled/Enabled World.
    * 
    * @param world - The name of the World being checked.
    * @param isDisabled - If the worlds being checked are Disabled.
    * @return If the World is an Disabled/Enabled World.
    */
	public boolean containsWorld(final String world, final boolean isDisabled) {
		for (String worldString: (isDisabled ? this.getDisabledWorlds() : this.getEnabledWorlds())) {
			if (worldString.equalsIgnoreCase(world) || worldString.equalsIgnoreCase("ALL") || worldString.equalsIgnoreCase("GLOBAL")) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Checks if the String Region Name is an Enabled Region.
    * 
    * @param region - The name of the Reegion being checked.
    * @return If the Region is an Enabled Region.
    */
	public boolean containsRegion(final String region) {
		for (String enabledRegion: this.getEnabledRegions()) {
			if (enabledRegion.equalsIgnoreCase(region) || enabledRegion.equalsIgnoreCase("UNDEFINED")) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Checks if the ItemFlag should be allowed.
    * 
    * @param player - The Player being Checked.
    * @param item - The ItemStack being checked.
    * @param findFlag - The ItemFlag being found.
    * @return If the ItemFlag is to be prevented.
    */
	public boolean isAllowedItem(final Player player, final ItemStack item, final String findFlag) {
		if (!Menu.isOpen(player) && this.isSimilar(item)) {
			if (this.AllowOpBypass && player.isOp() || this.CreativeBypass && player.getGameMode() == GameMode.CREATIVE 
					|| findFlag.equalsIgnoreCase("inventory-modify") && player.hasPermission("itemjoin.bypass.inventorymodify") 
					&& ItemJoin.getInstance().getConfig().getBoolean("Permissions.Movement-Bypass")) {
				return false;
			} 
			else if (findFlag.equals("cancel-events")) { return cancelEvents; } 
			else if (findFlag.equals("self-drops")) { return selfDroppable; } 
			else if (findFlag.equals("death-drops")) { return deathDroppable; } 
			else if (findFlag.equals("death-keep")) { return deathKeepable; } 
			else if (findFlag.equals("cancel-equip")) { return blockEquip; }
			else if (findFlag.equals("inventory-modify")) { return blockMovement; }
			else if (findFlag.equals("inventory-close")) { return closeInventory; }
			else if (findFlag.equals("item-store")) { return itemStore; } 
			else if (findFlag.equals("stackable")) { return stackable; } 
			else if (findFlag.equals("selectable")) { return selectable; } 
			else if (findFlag.equals("item-modifiable")) { return itemModify; } 
			else if (findFlag.equals("item-craftable")) { return noCrafting; } 
			else if (findFlag.equals("item-repairable")) { return noRepairing; } 
			else if (findFlag.equals("placement")) { return blockPlacement; } 
			else if (findFlag.equals("count-lock")) { return countLock; }
			else if (findFlag.equals("teleport")) { return teleportArrow; }
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
		if (item != null && item.getType() != Material.AIR
				&& (this.vanillaControl || this.vanillaStatus
				|| (ItemHandler.dataTagsEnabled() && ItemHandler.getNBTData(item) != null && ItemHandler.getNBTData(item).equalsIgnoreCase(this.newNBTData))
				|| (this.legacySecret != null && item.hasItemMeta() && (ServerUtils.hasSpecificUpdate("1_14") || (!ServerUtils.hasSpecificUpdate("1_14") && item.getItemMeta().hasDisplayName())) 
				&& StringUtils.colorDecode(item) != null && StringUtils.colorDecode(item).contains(this.legacySecret)))) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the ItemStack is similar to the defined ItemMap.
    * 
    * @param item - The ItemStack being checked.
    * @return If the ItemStack is similar.
    */
	public boolean isSimilar(final ItemStack item) {
		if ((item != null && item.getType() != Material.AIR && item.getType() == this.material) || (this.materialAnimated && item != null && item.getType() != Material.AIR && this.isMaterial(item))) {
			if (this.vanillaControl || this.vanillaStatus || (ItemHandler.dataTagsEnabled() && ItemHandler.getNBTData(item) != null && ItemHandler.getNBTData(item).equalsIgnoreCase(this.newNBTData))
					|| (this.legacySecret != null && item.hasItemMeta() && (ServerUtils.hasSpecificUpdate("1_14") || (!ServerUtils.hasSpecificUpdate("1_14") && item.getItemMeta().hasDisplayName())) 
					&& StringUtils.colorDecode(item) != null && StringUtils.colorDecode(item).contains(this.legacySecret))) {
				if (this.skullMeta(item)) {
					if (isEnchantSimilar(item) || !item.getItemMeta().hasEnchants() && enchants.isEmpty() || this.isItemChangable()) {
						if (this.material.toString().toUpperCase().contains("BOOK") 
								&& (this.isBookMeta(item) 
								&& ((BookMeta) item.getItemMeta()).getPages().equals(((BookMeta) tempItem.getItemMeta()).getPages()) || this.isDynamic())
								|| this.material.toString().toUpperCase().contains("BOOK") && !this.isBookMeta(item) || !this.material.toString().toUpperCase().contains("BOOK") || this.isItemChangable()) {
							if (!this.vanillaControl || this.vanillaControl && this.displayMeta(item)) {
								return true;
							}
						}
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
		if (this.material.toString().equalsIgnoreCase("PLAYER_HEAD") || this.material.toString().equalsIgnoreCase("SKULL_ITEM")) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the diplay meta is similar.
    * 
    * @param item - The ItemStack being checked.
    * @return If the display meta is similar.
    */
	private boolean displayMeta(final ItemStack item) {
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && this.tempMeta != null && this.tempMeta.hasDisplayName() && item.getItemMeta().hasLore() && this.tempMeta.hasLore()) {
			if (item.getItemMeta().getDisplayName().equalsIgnoreCase(this.tempMeta.getDisplayName()) && item.getItemMeta().getLore().toString().equalsIgnoreCase(this.tempMeta.getLore().toString())) {
				return true;
			}
		} else if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && this.tempMeta != null && this.tempMeta.hasDisplayName() && !item.getItemMeta().hasLore() && !this.tempMeta.hasLore()) {
			if (item.getItemMeta().getDisplayName().equalsIgnoreCase(this.tempMeta.getDisplayName())) {
				return true;
			}
		} else if (item.hasItemMeta() && !item.getItemMeta().hasDisplayName() && this.tempMeta != null && !this.tempMeta.hasDisplayName() && item.getItemMeta().hasLore() && this.tempMeta.hasLore()) {
			if (item.getItemMeta().getLore().toString().equalsIgnoreCase(this.tempMeta.getLore().toString())) {
				return true;
			}
		} else if (this.tempMeta == null) { return true; }
		return false;
	}
	
   /**
    * Checks if the Skull Meta is similar.
    * 
    * @param item - The ItemStack being checked.
    * @return If the skull meta is similar.
    */
	private boolean skullMeta(final ItemStack item) {
		if (!this.isSkull() || this.skullOwner == null && this.skullTexture == null && PlayerHandler.getSkullOwner(item).equalsIgnoreCase("NULL") && ItemHandler.getSkullTexture(item.getItemMeta()).isEmpty() 
				|| !this.skullAnimated && ((SkullMeta) item.getItemMeta()).hasOwner() && this.skullOwner != null && PlayerHandler.getSkullOwner(item).equalsIgnoreCase(this.skullOwner) 
				|| this.skullOwner != null && this.isSkullData(item)
				|| this.skullOwner != null && StringUtils.containsIgnoreCase(this.skullOwner, "%player%")
				|| this.skullTexture != null && this.skullOwner == null 
				&& ItemHandler.getSkullTexture(item.getItemMeta()).equalsIgnoreCase(this.skullTexture)
				|| this.skullAnimated && this.isSkull(item) || this.skullTexture != null && this.skullOwner == null && this.isHeadSimilar(item)) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the HeadDatabase Skull is Similar.
    * 
    * @param item - The ItemStack being checked.
    * @return If the HeadDatabase Skull is similar.
    */
	private boolean isHeadSimilar(final ItemStack item) {
		if (this.headDatabase) {
			HeadDatabaseAPI api = new HeadDatabaseAPI();
			ItemStack itemCopy = api.getItemHead(this.skullTexture);
			if (itemCopy != null && ItemHandler.getSkullTexture(item.getItemMeta()).equalsIgnoreCase(ItemHandler.getSkullTexture(itemCopy.getItemMeta()))) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Checks if the ItemStack Enchantments are similar.
    * 
    * @param item - The ItemStack being checked.
    * @return If the ItemStack Enchantments are similar.
    */
	private boolean isEnchantSimilar(final ItemStack item) {
		if (item.getItemMeta().hasEnchants() && ((this.enchants != null && !this.enchants.isEmpty()) || this.glowing)) { 
			ItemStack checkItem = new ItemStack(item.getType());
			for (Entry<String, Integer> enchantments : this.enchants.entrySet()) {
				if (enchantments.getKey() == null && DependAPI.getDepends(false).tokenEnchantEnabled() && TokenEnchantAPI.getInstance().getEnchantment(enchantments.getKey()) != null) {
					TokenEnchantAPI.getInstance().enchant(null, checkItem, enchantments.getKey(), enchantments.getValue(), true, 0, true);
				} else { 
					checkItem.addUnsafeEnchantment(ItemHandler.getEnchantByName(enchantments.getKey()), enchantments.getValue()); }
			}
			return (this.glowing ? true : item.getItemMeta().getEnchants().equals(checkItem.getItemMeta().getEnchants()));
		}
		return false;
	}
	
   /**
    * Checks if the Book Meta is similar.
    * 
    * @param item - The ItemStack being checked.
    * @return If the Book Meta is similar.
    */
	private boolean isBookMeta(final ItemStack item) {
		try {
			return ((BookMeta) item.getItemMeta()).hasPages();
		} catch (Exception e) { return false; }
	}
	
   /**
    * Checks if the stack size is similar.
    * 
    * @param item - The ItemStack being checked.
    * @return If the stack size is similar.
    */
	public boolean isCountSimilar(final ItemStack item) {
		if (item.getAmount() == count || ConfigHandler.getConfig().getFile("items.yml").getBoolean("items-RestrictCount") == false || this.isItemChangable()) {
			return true;
		}
		return false;
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
			if (material.contains(":")) { String[] parts = material.split(":"); dataValue = parts[1]; }
			if (item.getType() == ItemHandler.getMaterial(material, dataValue)) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Checks if the Skull Owners or Textures are similar.
    * 
    * @param item - The ItemStack being checked.
    * @return If the Skull Owwners or Skull Textures are similar.
    */
	private boolean isSkull(final ItemStack item) {
		if (this.dynamicOwners != null && !this.dynamicOwners.isEmpty()) {
			for (String owners : this.dynamicOwners) {
				owners = ItemHandler.cutDelay(owners);
				if (PlayerHandler.getSkullOwner(item) != null && PlayerHandler.getSkullOwner(item).equalsIgnoreCase(this.skullOwner) || PlayerHandler.getSkullOwner(item) != null && StringUtils.containsIgnoreCase(this.skullOwner, "%player%")) {
					return true;
				} else if (this.isSkullData(item) && this.isSkull()){
					return true;
				}
			}
			if (this.dynamicOwners.toString().contains("%player%")) { return true; }
		} else if (this.dynamicTextures != null && !this.dynamicTextures.isEmpty()) {
			for (String textures : this.dynamicTextures) {
				textures = ItemHandler.cutDelay(textures);
				if (ItemHandler.getSkullTexture(item.getItemMeta()).equalsIgnoreCase(textures)) {
					return true;
				}
			}
		}
		return false;
	}
	
   /**
    * Checks if the Skull Data is Similar.
    * 
    * @param item - The ItemStack being checked.
    * @return If the Skull Data is similar.
    */
	private boolean isSkullData(final ItemStack item) {
		if (ItemHandler.getSkullTexture(item.getItemMeta()).equalsIgnoreCase(ItemHandler.getSkullTexture(this.tempMeta))) {
			return true;	
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
		for (ItemStack inPlayerInventory: player.getInventory().getContents()) {
			if (this.isSimilar(inPlayerInventory) && (ignoreCount || this.isCountSimilar(inPlayerInventory))) {
				return true;
			}
		}
		for (ItemStack equipInventory: player.getEquipment().getArmorContents()) {
			if (this.isSimilar(equipInventory) && (ignoreCount || this.isCountSimilar(equipInventory))) {
				return true;
			}
		}
		if (ServerUtils.hasSpecificUpdate("1_9") 
				&& this.isSimilar(player.getInventory().getItemInOffHand())
				&& (ignoreCount || this.isCountSimilar(player.getInventory().getItemInOffHand()))) {
			return true;
		}
		if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
			for (ItemStack craftInventory: player.getOpenInventory().getTopInventory()) {
				if (this.isSimilar(craftInventory) && (ignoreCount || this.isCountSimilar(craftInventory))) {
					return true;
				}
			}
		}
		return (player.getItemOnCursor() != null && player.getItemOnCursor().getType() != Material.AIR && this.isSimilar(player.getItemOnCursor()) && (ignoreCount || this.isCountSimilar(player.getItemOnCursor())));
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
//  ================================================================================================================================================================================= //

//  ================================================================ //
//                      ~ Player Item Updater ~                      //
//  Method(s) update the ItemMap item for player specific variables. //
//  ================================================================ //
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
			this.setModelData();
			this.setPotionEffects();
			this.setBanners();
			this.setFireworks();
			this.setFireChargeColor();
			this.setDye();
			this.setBookInfo(player);
			LegacyAPI.setBookPages(player, this.tempMeta, this.bookPages, this);
			this.setAttributes();
			this.setAttributeFlags();
			this.setFlags();
			this.realGlow();
			this.setContents(player);
			this.tempItem.setItemMeta(this.tempMeta);
			LegacyAPI.setGlowing(this.tempItem, this);
			LegacyAPI.setAttributes(this.tempItem, this);
		}
		return this;
	}
	
   /**
    * Sets the item contents for the storage box.
    * 
    */
	private void setContents(final Player player) {
		if (this.contents != null && !this.contents.isEmpty() && ServerUtils.hasSpecificUpdate("1_11")) {
			ShulkerBox box = (ShulkerBox) ((BlockStateMeta)this.tempMeta).getBlockState();
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
				if (isNull) { ServerUtils.logWarn("{ItemMap} The item " + node + " does not exist and will not be set as contents for " + this.getConfigName() + "."); }
			}
			((BlockStateMeta)this.tempMeta).setBlockState(box);
			box.update();
		}
	}
	
   /**
    * Sets the item to glow.
    * 
    */
	private void realGlow() {
		if (this.glowing) { 
			if (ServerUtils.hasSpecificUpdate("1_13")) {
				Glow glow = new Glow();
				this.tempMeta.addEnchant(glow, 1, true);
			} else if (!ServerUtils.hasSpecificUpdate("1_13") && ServerUtils.hasSpecificUpdate("1_11")) {
				this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
				this.tempMeta.addEnchant(Enchantment.LURE, 0, true);
			} 
		}
	}
	
   /**
    * Sets the Skull Database Textures.
    * 
    */
	private void setSkullDatabase() {
		if (this.headDatabase && this.skullTexture != null) {
			HeadDatabaseAPI api = new HeadDatabaseAPI();
			ItemStack sk = api.getItemHead(this.skullTexture);
			this.tempItem = (sk != null ? sk : this.tempItem.clone());
		}
	}
	
   /**
    * Sets the ItemStack Enchantments.
    * 
    * @param player - The Player to have their TokenEnchant instance fetched.
    */
	private void setEnchantments(final Player player) {
		if (this.enchants != null && !this.enchants.isEmpty()) {
			for (Entry<String, Integer> enchantments : this.enchants.entrySet()) {
				if (enchantments.getKey() == null && DependAPI.getDepends(false).tokenEnchantEnabled() && TokenEnchantAPI.getInstance().getEnchantment(enchantments.getKey()) != null) {
					TokenEnchantAPI.getInstance().enchant(player, this.tempItem, enchantments.getKey(), enchantments.getValue(), true, 0, true);
				} else { this.tempItem.addUnsafeEnchantment(ItemHandler.getEnchantByName(enchantments.getKey()), enchantments.getValue()); }
			}
		}
	}
	
   /**
    * Sets the ItemStack as Unbreakable.
    * 
    */
	private void setUnbreaking() {
		if (this.isUnbreakable() || this.hideDurability) {
			try {
				Class<?> craftItemStack = ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack");
				Object nms = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, this.tempItem);
				Object tag = ReflectionUtils.getMinecraftClass("ItemStack").getMethod("getTag").invoke(nms);
				if (tag == null) { tag = ReflectionUtils.getMinecraftClass("NBTTagCompound").getConstructor().newInstance(); }
				tag.getClass().getMethod("setInt", String.class, int.class).invoke(tag, "Unbreakable", 1);
				nms.getClass().getMethod("setTag", tag.getClass()).invoke(nms, tag);
				this.tempItem = (ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms);
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		}
	}
	
   /**
    * Sets the armor value to the items attributes.
    * 
    */
	private void setAttributes() {
		if (ServerUtils.hasSpecificUpdate("1_13") && this.attributes != null && !this.attributes.isEmpty()) {
			try {
				for (String attrib: this.attributes.keySet()) {
					Attribute attribute = Attribute.valueOf(attrib.toUpperCase());
					double value = this.attributes.get(attrib);
					EquipmentSlot slot = null;
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
    * 
    */
	private void setMapImage() {
		if (this.customMapImage != null || this.mapId != -1) {
			if (ServerUtils.hasSpecificUpdate("1_13")) {
				MapMeta mapmeta = (MapMeta) this.tempItem.getItemMeta();
				try { mapmeta.setMapView(this.mapView); }
				catch (NoSuchMethodError e) { mapmeta = LegacyAPI.setMapID(mapmeta, this.mapId); }
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
    * @param item - The ItemStack to be updated.
    * @param pages - The book pages to be set.
    * @return The updated ItemStack.
    */
	public ItemStack setJSONBookPages(final Player player, final ItemStack item, final List<String> pages) {
		if (item.getType().toString().equalsIgnoreCase("WRITTEN_BOOK") && pages != null && !pages.isEmpty() && pages.size() != 0 && ServerUtils.hasSpecificUpdate("1_8")) {
			List<String> copyPages = new ArrayList<String>();
			for (String page: pages) { copyPages.add(page); }
			copyPages.set(0, ItemHandler.cutDelay(copyPages.get(0)));
			Object localePages = null;
			try { localePages = ReflectionUtils.getMinecraftClass("NBTTagList").getConstructor().newInstance(); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
			if (ServerUtils.hasSpecificUpdate("1_15")) { return this.set1_15JSONPages(player, item, localePages, copyPages); } 
			else if (ServerUtils.hasSpecificUpdate("1_14")) { return this.set1_14JSONPages(player, item, localePages, copyPages); }
			else { return this.set1_13JSONPages(player, item, localePages, copyPages); }
		}
		return item;
	}
	
   /**
    * Sets the JSON Book Pages to the ItemStack.
    * @warn Method ONLY USED for Server Version 1.13
    * 
    * @param player - The Player being used for placeholders.
    * @param item - The ItemStack to be updated.
    * @param localePages - The NBTTagList of Pages.
    * @param pages - The book pages to be set.
    * @return The updated ItemStack.
    */
	private ItemStack set1_13JSONPages(final Player player, final ItemStack item, final Object localePages, final List<String> pages) {
		for (String textComponent: pages) {
			try { 
				textComponent = StringUtils.translateLayout(textComponent, player);
				Object TagString = ReflectionUtils.getMinecraftClass("NBTTagString").getConstructor(String.class).newInstance(textComponent);
				localePages.getClass().getMethod("add", ReflectionUtils.getMinecraftClass("NBTBase")).invoke(localePages, TagString);
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); } 
		}
		try { return this.invokePages(item, localePages); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		return item;
	}
	
   /**
    * Sets the JSON Book Pages to the ItemStack.
    * @warn Method ONLY USED for Server Version 1.14
    * 
    * @param player - The Player being used for placeholders.
    * @param item - The ItemStack to be updated.
    * @param localePages - The NBTTagList of Pages.
    * @param pages - The book pages to be set.
    * @return The updated ItemStack.
    */
	private ItemStack set1_14JSONPages(final Player player, final ItemStack item, final Object localePages, final List<String> pages) {
		for (int i = pages.size() - 1; i >= 0; i--) {
			String textComponent = pages.get(i);
			try { 
				textComponent = StringUtils.translateLayout(textComponent, player);
				Object TagString = ReflectionUtils.getMinecraftClass("NBTTagString").getConstructor(String.class).newInstance(textComponent);
				localePages.getClass().getMethod("add", int.class, ReflectionUtils.getMinecraftClass("NBTBase")).invoke(localePages, 0, TagString);
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); } 
		}
		try { return this.invokePages(item, localePages); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		return item;
	}
	
   /**
    * Sets the JSON Book Pages to the ItemStack.
    * @warn Method ONLY USED for Server Version 1.15
    * 
    * @param player - The Player being used for placeholders.
    * @param item - The ItemStack to be updated.
    * @param localePages - The NBTTagList of Pages.
    * @param pages - The book pages to be set.
    * @return The updated ItemStack.
    */
	private ItemStack set1_15JSONPages(final Player player, final ItemStack item, final Object localePages, final List<String> pages) {
		for (int i = pages.size() - 1; i >= 0; i--) {
			String textComponent = pages.get(i);
			try { 
				textComponent = StringUtils.translateLayout(textComponent, player);
				Object TagString = ReflectionUtils.getMinecraftClass("NBTTagString").getMethod("a", String.class).invoke(null, textComponent);
				localePages.getClass().getMethod("add", int.class, ReflectionUtils.getMinecraftClass("NBTBase")).invoke(localePages, 0, TagString);
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); } 
		}
		try { return this.invokePages(item, localePages); } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		return item;
	}
	
   /**
    * Sets the JSON Book Pages to the ItemStack.
    * 
    * @param item - The ItemStack to be updated.
    * @param pages - The book pages to be set.
    * @throws Exception An exception.
    * @return The updated ItemStack.
    */
	private ItemStack invokePages(final ItemStack item, final Object pages) throws Exception {
		Class<?> craftItemStack = ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack");
		Object nms = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
		Object tag = ReflectionUtils.getMinecraftClass("ItemStack").getMethod("getTag").invoke(nms);
		if (tag == null) { tag = ReflectionUtils.getMinecraftClass("NBTTagCompound").getConstructor().newInstance(); }
		tag.getClass().getMethod("set", String.class, ReflectionUtils.getMinecraftClass("NBTBase")).invoke(tag, "pages", pages); 
		nms.getClass().getMethod("setTag", tag.getClass()).invoke(nms, tag);
		return ((ItemStack)craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms));
	}
	
   /**
    * Sets the NBTData, making the ItemStack plugin specific.
    * 
    */
	private void setNBTData() {
		if (ItemHandler.dataTagsEnabled() && !this.isVanilla() && !this.isVanillaControl() && !this.isVanillaStatus()) {
			try {
				Object nms = ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, this.tempItem);
				Object cacheTag = ReflectionUtils.getMinecraftClass("ItemStack").getMethod("getTag").invoke(nms);
				if (cacheTag != null) {
					cacheTag.getClass().getMethod("setString", String.class, String.class).invoke(cacheTag, "ItemJoin Name", this.getConfigName());
					cacheTag.getClass().getMethod("setString", String.class, String.class).invoke(cacheTag, "ItemJoin Slot", this.getItemValue());
					if (this.nbtProperty != null && !this.nbtProperty.isEmpty()) {
						for (String tag: this.nbtProperty.keySet()) {
							cacheTag.getClass().getMethod("setString", String.class, String.class).invoke(cacheTag, tag, this.nbtProperty.get(tag));
						}
					}
				} else { 
					nms.getClass().getMethod("setTag", this.newNBTTag.getClass()).invoke(nms, this.newNBTTag);
					if (this.nbtProperties != null && !this.nbtProperties.isEmpty()) {
						for (Object tag: this.nbtProperties) {
							nms.getClass().getMethod("setTag", tag.getClass()).invoke(nms, tag);
						}
					}
				}
				this.tempItem = (ItemStack) ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asCraftMirror", nms.getClass()).invoke(null, nms);
			} catch (Exception e) {
				ServerUtils.logSevere("{ItemMap} An error has occured when setting NBTData to an item.");
				ServerUtils.sendDebugTrace(e);
			}
		} else if (!ItemHandler.dataTagsEnabled()) {
			this.tempItem = StringUtils.colorEncode(this.tempItem, this.legacySecret);
		}
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
				this.tempMeta.setDisplayName(StringUtils.translateLayout(ItemHandler.cutDelay(this.customName), player) + "§r" + itemData);
			} else {
				this.tempMeta.setDisplayName(StringUtils.translateLayout(ItemHandler.cutDelay(this.customName), player));
			}
		}
	}
	
   /**
    * Sets the Custom Display Lore.
    * 
    * @param player - The Player to be used for placeholders.
    */
	private void setCustomLore(final Player player) {
		if (this.customLore != null && !this.customLore.isEmpty()) {
			List < String > loreList = this.customLore;
			List < String > loreFormatList = new ArrayList < String > ();
			for (int k = 0; k < loreList.size(); k++) {
				String formatLore = ItemHandler.cutDelay(loreList.get(k));
				formatLore = StringUtils.translateLayout(formatLore, player);
				loreFormatList.add(formatLore);
			}
			this.tempMeta.setLore(loreFormatList);
		}
	}
	
   /**
    * Sets the ItemStack Durability.
    * 
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
    * 
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
    * Sets the ItemStack Model Data.
    * 
    */
	private void setModelData() {
		if (this.modelData != null && this.modelData != 0) {
			if (ServerUtils.hasSpecificUpdate("1_14")) {
				this.tempMeta.setCustomModelData(this.modelData);
			} else { ServerUtils.logWarn("{ItemMap} The item " + this.getConfigName() + " is using Custom Model Data which is not supported until Minecraft 1.14+."); }
		}
	}
	
   /**
    * Sets the Skull Owner.
    * 
    * @param player - The Player to be used for placeholders.
    */
	private void setSkull(final Player player) {
		if (this.skullOwner != null) {
			this.tempMeta = ItemHandler.setSkullOwner(this.tempMeta, StringUtils.translateLayout(this.skullOwner, player));
		} else if (this.skullTexture != null && !this.headDatabase) {
			try {
				if (ServerUtils.hasSpecificUpdate("1_8")) {
					GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
					gameProfile.getProperties().put("textures", new Property("textures", new String(((this.skullOwner != null && DependAPI.getDepends(false).skinsRestorerEnabled()) ? DependAPI.getDepends(false).getSkinValue(StringUtils.translateLayout(this.skullOwner, player)) : StringUtils.toTextureUUID(player, this.configName, this.skullTexture)))));
					Field declaredField = this.tempMeta.getClass().getDeclaredField("profile");
					declaredField.setAccessible(true);
					declaredField.set(this.tempMeta, gameProfile);
				}
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		}
	}
	
   /**
    * Sets the ItemStack PotionEffects
    * 
    */
	private void setPotionEffects() {
		if (this.effect != null && !this.effect.isEmpty() && !this.customConsumable) {
			for (PotionEffect potion: this.effect) {
				((PotionMeta) this.tempMeta).addCustomEffect(potion, true);
			}
		}
	}
	
   /**
    * Sets the ItemStack Banner Patterns
    * 
    */
	private void setBanners() {
		if (this.bannerPatterns != null && !this.bannerPatterns.isEmpty()) {
			((BannerMeta) this.tempMeta).setPatterns(this.bannerPatterns);
		}
	}
	
   /**
    * Sets the itemStack FireworkMeta.
    * 
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
    * 
    */
	private void setFireChargeColor() {
		if (this.chargeColor != null) {
			((FireworkEffectMeta) this.tempMeta).setEffect(FireworkEffect.builder().withColor(this.chargeColor.getColor()).build());
		}
	}
	
   /**
    * Sets the ItemStack DyeColor.
    * 
    */
	private void setDye() {
		if (this.leatherColor != null) {
			((LeatherArmorMeta) this.tempMeta).setColor(DyeColor.valueOf(this.leatherColor).getFireworkColor());
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
    * 
    */
	private void setAttributeFlags() {
		if (ServerUtils.hasSpecificUpdate("1_8") && this.hideAttributes) {
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
		}
	}
	
   /**
    * Sets the Flags to the Temporary ItemMeta.
    * 
    */
	private void setFlags() {
		if (ServerUtils.hasSpecificUpdate("1_8") && this.hideFlags) {
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_DESTROYS);
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_PLACED_ON);
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_POTION_EFFECTS);
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
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
		if (this.enabledWorlds == null && this.disabledWorlds == null) { return true; }
			for (String enabledWorld : this.enabledWorlds) {
				if (enabledWorld.equalsIgnoreCase(world.getName()) 
						|| enabledWorld.equalsIgnoreCase("ALL") 
						|| enabledWorld.equalsIgnoreCase("GLOBAL")) {
					return !this.isDisabled(world);
				}
			}
		return false;
	}
	
   /**
    * Checks if the World is an Disabled World.
    * 
    * @param world - The world to be checked.
    * @return If the World is an Disabled World.
    */
	public boolean isDisabled(final World world) {
		boolean isDisabled = false;
		for (String disabledWorld : this.disabledWorlds) {
			if (disabledWorld.equalsIgnoreCase(world.getName()) 
					|| disabledWorld.equalsIgnoreCase("ALL") 
					|| disabledWorld.equalsIgnoreCase("GLOBAL")) {
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
	private List < String > getConditions(final String conditions) {
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
    * @param player - The Player being referenced.
    * @param conditions - The condition list to be fetched.
    * @return If the condition was successfully met.
    */
	public boolean conditionMet(final Player player, final String conditions) {
		if (this.getConditions(conditions) != null && !this.getConditions(conditions).isEmpty()) {
			for (String condition : this.getConditions(conditions)) {
				String[] parts = (condition != null ? condition.split(":") : null);
				if (parts != null && parts.length == 3) {
					String value1 = (parts[0] != null && !StringUtils.isInt(parts[0]) ? StringUtils.translateLayout(parts[0], player) : parts[0]);
					String operand = parts[1];
					String value2 = (parts[2] != null && !StringUtils.isInt(parts[2]) ? StringUtils.translateLayout(parts[2], player) : parts[2]);
					final boolean conditionMet = StringUtils.conditionMet(value1, operand, value2);
					if (!conditionMet && this.getConditionMessage(conditions) != null && !this.getConditionMessage(conditions).isEmpty()) {
						player.sendMessage(StringUtils.translateLayout(this.getConditionMessage(conditions), player));
						ServerUtils.logDebug("{ItemMap} " + player.getName() + " has not met any of the " + conditions + "(s), for the Item: " + this.getConfigName() + "."); 
					}
					return conditionMet;
				} else if (!(parts != null && parts.length == 3)) {
					ServerUtils.logSevere("{ItemMap} The item " + this.getConfigName() + " has a " + conditions + " defined incorrectly!");
					ServerUtils.logWarn("{ItemMap} The condition " + condition + " is not the proper format CONDITION:OPERAND:VALUE, the item may not function properly.");
				}
			}
		} else { return true; }
		return false;
	}
	
   /**
    * Removes the ItemMap from the Player.
    * 
    * @param player - The Player to have the item removed.
    * @param amount - The stack size of the item.
    */
	public void removeFrom(final Player player, int...amount) {
		if (amount.length == 0) { amount = new int[]{0}; } 
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
				if (this.isSimilar(contents[k])) { inv.setItem(k, new ItemStack(Material.AIR)); }
			}
			if (this.isSimilar(inv.getHelmet())) { inv.setHelmet(new ItemStack(Material.AIR)); }
			if (this.isSimilar(inv.getChestplate())) { inv.setChestplate(new ItemStack(Material.AIR)); }
			if (this.isSimilar(inv.getLeggings())) { inv.setLeggings(new ItemStack(Material.AIR)); }
			if (this.isSimilar(inv.getBoots())) { inv.setBoots(new ItemStack(Material.AIR)); }
			if (ServerUtils.hasSpecificUpdate("1_9") && this.isSimilar(PlayerHandler.getOffHandItem(player))) { PlayerHandler.setOffHandItem(player, new ItemStack(Material.AIR)); }
			
			if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
				for (int k = 0; k < craftingContents.length; k++) {
					if (this.isSimilar(craftingContents[k])) { craftView.setItem(k, new ItemStack(Material.AIR)); }
				}
			}
		} else {
			for (int k = 0; k < contents.length; k++) {
				if (this.isSimilar(contents[k])) { inv.setItem(k, ItemHandler.modifyItem(inv.getItem(k), false, amount[0])); return; }
			}
			if (this.isSimilar(inv.getHelmet())) { inv.setHelmet(ItemHandler.modifyItem(inv.getHelmet(), false, amount[0])); }
			else if (this.isSimilar(inv.getChestplate())) { inv.setChestplate(ItemHandler.modifyItem(inv.getChestplate(), false, amount[0])); }
			else if (this.isSimilar(inv.getLeggings())) { inv.setLeggings(ItemHandler.modifyItem(inv.getLeggings(), false, amount[0])); }
			else if (this.isSimilar(inv.getBoots())) { inv.setBoots(ItemHandler.modifyItem(inv.getBoots(), false, amount[0])); }
			else if (ServerUtils.hasSpecificUpdate("1_9") && this.isSimilar(PlayerHandler.getOffHandItem(player))) { PlayerHandler.setOffHandItem(player, ItemHandler.modifyItem(PlayerHandler.getOffHandItem(player), false, amount[0])); }
			else if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
				for (int k = 0; k < craftingContents.length; k++) {
					if (this.isSimilar(craftingContents[k])) { craftView.setItem(k, ItemHandler.modifyItem(player.getOpenInventory().getItem(k), false, amount[0])); return; }
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
	public void giveTo(final Player player, int...amount) {
		if (amount.length == 0) { amount = new int[]{0}; }
		if (this.CustomSlot != null && !this.CustomSlot.contains("%")) { ItemUtilities.getUtilities().setCustomSlots(player, this, amount[0]); } 
		else { ItemUtilities.getUtilities().setInvSlots(player, this, amount[0]); }
		this.setAnimations(player);
		if (this.getMultipleSlots() != null && !this.getMultipleSlots().isEmpty()) {
			if (this.getSlot().equalsIgnoreCase(this.getMultipleSlots().get(0))) {
				this.executeCommands(player, null, this.tempItem, "ON_RECEIVE", "RECEIVED", this.getSlot());
			}
		} else { this.executeCommands(player, null, this.tempItem, "ON_RECEIVE", "RECEIVED", this.getSlot()); }
	}
	
   /**
    * Gives the Player the ItemMap.
    * 
    * @param player - The Player to be given the item.
    * @param slot - The slot to be placed into.
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
			} 
			else { 
				player.getInventory().setItem(Integer.parseInt(slot), itemStack);
			}
		} else {
			if (PlayerHandler.getMainHandItem(player) == null || PlayerHandler.getMainHandItem(player).getType() == Material.AIR) {
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
    * @param player - The Player that executed the commands.
    * @param altPlayer - that is associated with the commands.
    * @param itemCopy - The ItemStack having their commands executed.
    * @param action - The Action that executed the commands.
    * @param slot - The Slot of the ItemStack.
    * @return If the commands successfully executed.
    */
    public void executeCommands(final Player player, final Player altPlayer, final ItemStack itemCopy, final String action, final String clickType, final String slot) {
    	if (this.commands != null && this.commands.length > 0 && !Menu.isOpen(player) && !this.getWarmPending(player) && this.isExecutable(player, action, clickType) && !this.onCooldown(player) && this.isPlayerChargeable(player, this.itemCost != null && !this.itemCost.isEmpty())) {
    		this.warmCycle(player, altPlayer, this, this.getWarmDelay(), player.getLocation(), itemCopy, action, clickType, slot);
    	}
    }
	
   /**
    * Starts the Warmup for the Player pending command execution.
    * 
    * @param player - The Player that executed the commands.
    * @param altPlayer - that is associated with the commands.
    * @param itemMap - The ItemMap having their commands executed.
    * @param warmCount - The duration of the Warmup.
    * @param location - The Location of the Warmup.
    * @param itemCopy - The ItemStack having their commands executed.
    * @param action - The Action that executed the commands.
    * @param slot - The Slot of the ItemStack.
    */
	private void warmCycle(final Player player, final Player altPlayer, final ItemMap itemMap, final int warmCount, final Location location, final ItemStack itemCopy, final String action, final String clickType, final String slot) {
		if (warmCount != 0) {
			if (itemMap.warmDelay == warmCount) { 
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[13] = warmCount + ""; placeHolders[0] = player.getWorld().getName(); placeHolders[3] = StringUtils.translateLayout(itemMap.getCustomName(), player); 
				LanguageAPI.getLang(false).sendLangMessage("general.warmingUp", player, placeHolders); 
				itemMap.addWarmPending(player); 
			}
			SchedulerUtils.runLater(20L, () -> {
				if (itemMap.warmLocation(player, location, action)) {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[13] = warmCount + ""; placeHolders[0] = player.getWorld().getName(); placeHolders[3] = StringUtils.translateLayout(itemMap.getCustomName(), player); 
					LanguageAPI.getLang(false).sendLangMessage("general.warmingTime", player, placeHolders);
					itemMap.warmCycle(player, altPlayer, itemMap, (warmCount - 1), location, itemCopy, action, clickType, slot);	
				} else { 
					itemMap.delWarmPending(player); 
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[13] = warmCount + ""; placeHolders[0] = player.getWorld().getName(); placeHolders[3] = StringUtils.translateLayout(itemMap.getCustomName(), player); 
					LanguageAPI.getLang(false).sendLangMessage("general.warmingHalted", player, placeHolders);
				}
			});
		} else {
			long delay = 0;
			if (itemMap.warmDelay != 0) { delay = 20L; }
			SchedulerUtils.runLater(delay, () -> {
				if ((!player.isDead() || action.equalsIgnoreCase("ON_DEATH")) && player.isOnline()) {
					if (this.isExecuted(player, altPlayer, action, clickType, slot)) { 
						if (itemMap.itemCost == null || itemMap.itemCost.isEmpty()) { itemMap.withdrawBalance(player); } 
						else { itemMap.withdrawItemCost(player); }
					    itemMap.playSound(player);
					    itemMap.playParticle(player);
					    itemMap.removeDisposable(player, itemMap, itemCopy, false);
						itemMap.addPlayerOnCooldown(player);
					}
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[13] = warmCount + ""; placeHolders[0] = player.getWorld().getName(); placeHolders[3] = StringUtils.translateLayout(itemMap.getCustomName(), player); 
					LanguageAPI.getLang(false).sendLangMessage("general.warmingHalted", player, placeHolders);
				}
				if (itemMap.warmDelay != 0) { itemMap.delWarmPending(player); }
			});
		}
	}
	
   /**
    * Checks if the Player is still at the original Warmup Location.
    * 
    * @param player - The Player Warming Up.
    * @param Location - The Location of the Warmup.
    * @return If the Player is still inside the Warmup Location.
    */
	private boolean warmLocation(final Player player, final Location location, final String action) {
	    if (!player.getLocation().getWorld().equals(location.getWorld()) || player.getLocation().distance(location) >= 1 || (!action.equalsIgnoreCase("ON_DEATH") && player.isDead())) {
	    	return false;
	    }
	    return true;
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
    	ItemCommand[] itemCommands = this.commands;
    	for (int i = 0; i < itemCommands.length; i++) {
    		if (!playerSuccess) { playerSuccess = itemCommands[i].canExecute(player, action, clickType); }
			else { break; }
		}
    	return playerSuccess;
    }
	
   /**
    * Randomly Selected an ItemCommand Entry for a Single Command.
    * 
    * @param itemCommands - The ItemCommands to have an entry randomly selected.
    * @param player - The Player having their commands randomly selected.
    * @param altPlayer - that is associated with the commands.
    * @param action - The Action that executed the commands.
    * @param slot - The Slot of the ItemStack.
    * @return If it was successful.
    */
    private boolean getRandomMap(final ArrayList < ItemCommand > randomCommands, final ItemCommand[] itemCommands, final Player player, final Player altPlayer, final String action, final String clickType, final String slot) {
    	ItemCommand dedicatedMap = (ItemCommand) StringUtils.randomEntry(randomCommands);
    	if (dedicatedMap != null && dedicatedMap != null && player != null && action != null && clickType != null && slot != null && itemCommands != null && randomCommands != null
        && !dedicatedMap.execute(player, altPlayer, action, clickType, slot, this)) { 
    		return this.getRandomMap(randomCommands, itemCommands, player, altPlayer, action, clickType, slot);
    	}
    	return true;
    }
	
   /**
    * Randomly Selected an ItemCommand Entry for All Commands.
    * 
    * @param itemCommands - The ItemCommands to have an entry randomly selected.
    * @param player - The Player having their commands randomly selected.
    * @param altPlayer - that is associated with the commands.
    * @param action - The Action that executed the commands.
    * @param slot - The Slot of the ItemStack.
    * @return If it was successful.
    */
    private boolean getRandomAll(final ArrayList < ItemCommand > randomCommands, final ItemCommand[] itemCommands, final Player player, final Player altPlayer, final String action, final String clickType, final String slot) {
    	ItemCommand dedicatedMap = (ItemCommand) StringUtils.randomEntry(randomCommands);
    	if (dedicatedMap != null && dedicatedMap != null && player != null && action != null && slot != null && itemCommands != null && randomCommands != null 
        && !dedicatedMap.execute(player, altPlayer, action, clickType, slot, this)) { 
    		randomCommands.remove(dedicatedMap);
    		return this.getRandomAll(randomCommands, itemCommands, player, altPlayer, action, clickType, slot);
    	}
    	if (dedicatedMap != null && randomCommands != null) { randomCommands.remove(dedicatedMap); }
    	if (dedicatedMap != null && dedicatedMap != null && player != null && action != null && slot != null && itemCommands != null && randomCommands != null && 
    	   !randomCommands.isEmpty()) {
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
    		ArrayList < String > listIdent = new ArrayList < String > ();
    		for (int i = 0; i < itemCommands.length; i++) {
    			if (!listIdent.contains("+" + itemCommands[i].getSection() + "+")) {
    				listIdent.add("+" + itemCommands[i].getSection() + "+");
    			}
    		}
    		return (String) StringUtils.randomEntry(listIdent);
    	}
    	return null;
    }
	
   /**
    * Executes the ItemCommands.
    * 
    * @param player - The Player executing the commands.
    * @param altPlayer - that is associated with the commands.
    * @param action - The Action executing the commands.
    * @param slot - The slot of the ItemStack.
    * @param itemCopy - The ItemStack having their commands executed.
    * @return If the Command(s) were successfully executed.
    */
    private boolean isExecuted(final Player player, final Player altPlayer, final String action, final String clickType, final String slot) {
    	boolean playerSuccess = false;
    	ItemCommand[] itemCommands = this.commands;
    	String chosenIdent = this.getRandomList(itemCommands);
    	ArrayList < ItemCommand > randomCommands = new ArrayList < ItemCommand > ();
    	if (!this.subjectRemoval) {
    		for (int i = 0; i < itemCommands.length; i++) { 
        		if (this.sequence == CommandSequence.RANDOM || this.sequence == CommandSequence.RANDOM_SINGLE) { randomCommands.add(itemCommands[i]); }
        		else if (this.sequence == CommandSequence.RANDOM_LIST) {
        			if (itemCommands[i].getSection() != null && itemCommands[i].getSection().equalsIgnoreCase(chosenIdent.replace("+", ""))) {
	        			if (!playerSuccess) { playerSuccess = itemCommands[i].execute(player, altPlayer, action, clickType, slot, this); } 
	        			else { itemCommands[i].execute(player, altPlayer, action, clickType, slot, this); } 
	        		}
        		}
				else { 
					synchronized(ItemCommand.class) {
						if (!playerSuccess) { playerSuccess = itemCommands[i].execute(player, altPlayer, action, clickType, slot, this); }
						else { itemCommands[i].execute(player, altPlayer, action, clickType, slot, this); }
					}
				}
			}
    		if (this.sequence == CommandSequence.RANDOM) { playerSuccess = this.getRandomAll(randomCommands, itemCommands, player, altPlayer, action, clickType, slot); }
    		else if (this.sequence == CommandSequence.RANDOM_SINGLE) { playerSuccess = this.getRandomMap(randomCommands, itemCommands, player, altPlayer, action, clickType, slot); }
    	}
    	return playerSuccess;
    }
	
   /**
    * Checks if the Player has the proper balance/cost so they can execute the command.
    * 
    * @param player - The Player being charged.
    * @param materialCost - If the cost being charged is an Item.
    * @return If the Player has the required economy balance to execute the command.
    */
    private boolean isPlayerChargeable(final Player player, final boolean materialCost) {
		if (DependAPI.getDepends(false).getVault().vaultEnabled() && !materialCost) {
			double balance = 0.0; try { balance = DependAPI.getDepends(false).getVault().getBalance(player); } catch (NullPointerException e) { }
			if (balance >= this.cost || this.cost < 0) {
				return true;
			} else if (!(balance >= this.cost)) {
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[6] = this.cost.toString(); placeHolders[5] = balance + "";
				LanguageAPI.getLang(false).sendLangMessage("general.econFailed", player, placeHolders);
				return false;
			}
		} else if (materialCost) {
			Material mat = ItemHandler.getMaterial(this.itemCost, null);
			int foundAmount = 0;
			for (ItemStack playerInventory: player.getInventory().getContents()) {
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
			for (ItemStack equipInventory: player.getEquipment().getArmorContents()) {
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
			if (ServerUtils.hasSpecificUpdate("1_9") && player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() == mat) {
				if (player.getInventory().getItemInOffHand().getAmount() >= this.cost) {
					return true;
				} else { 
					foundAmount += player.getInventory().getItemInOffHand().getAmount();
					if (foundAmount >= this.cost) {
						return true;
					}
				}
			}
			if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
				for (ItemStack craftInventory: player.getOpenInventory().getTopInventory()) {
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
			String formatCost = "";
			for (String str : this.itemCost.toLowerCase().split("_")) { formatCost += str.substring(0, 1).toUpperCase() + str.substring(1) + " "; }
			formatCost = formatCost.substring(0, formatCost.length() - 1);
			String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[4] = formatCost; placeHolders[6] = this.cost == 0 ? "1" : this.cost.toString(); placeHolders[5] = foundAmount + "";
			LanguageAPI.getLang(false).sendLangMessage("general.itemFailed", player, placeHolders);
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
		Integer removeAmount = this.cost; if (this.cost == 0) { removeAmount = 1; }
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).getType() == mat) {
				if (player.getInventory().getItem(i).getAmount() < removeAmount) {
					removeAmount -= player.getInventory().getItem(i).getAmount();
					player.getInventory().setItem(i, ItemHandler.modifyItem(player.getInventory().getItem(i), false, player.getInventory().getItem(i).getAmount()));
				} else { player.getInventory().setItem(i, ItemHandler.modifyItem(player.getInventory().getItem(i), false, removeAmount)); break; } 
			}
		}
		if (ServerUtils.hasSpecificUpdate("1_9") && player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() == mat) {
			if (player.getInventory().getItemInOffHand().getAmount() < removeAmount) {
				removeAmount -= player.getInventory().getItemInOffHand().getAmount();
				PlayerHandler.setOffHandItem(player, ItemHandler.modifyItem(player.getInventory().getItemInOffHand(), false, player.getInventory().getItemInOffHand().getAmount()));
			} else { PlayerHandler.setOffHandItem(player, ItemHandler.modifyItem(player.getInventory().getItemInOffHand(), false, removeAmount));} 
			PlayerHandler.setOffHandItem(player, ItemHandler.modifyItem(player.getInventory().getItemInOffHand(), false, removeAmount));
		}
		if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
			for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
				if (player.getOpenInventory().getTopInventory().getItem(i) != null && player.getOpenInventory().getTopInventory().getItem(i).getType() == mat) {
					if (player.getOpenInventory().getTopInventory().getItem(i).getAmount() < removeAmount) {
						removeAmount -= player.getOpenInventory().getTopInventory().getItem(i).getAmount();
						player.getOpenInventory().getTopInventory().setItem(i, ItemHandler.modifyItem(player.getOpenInventory().getTopInventory().getItem(i), false, player.getOpenInventory().getTopInventory().getItem(i).getAmount()));
					} else { player.getOpenInventory().getTopInventory().setItem(i, ItemHandler.modifyItem(player.getOpenInventory().getTopInventory().getItem(i), false, removeAmount)); break; } 
				}
			}
		}
		String formatCost = "";
		for (String str : this.itemCost.toLowerCase().split("_")) { formatCost += str.substring(0, 1).toUpperCase() + str.substring(1) + " "; }
		formatCost = formatCost.substring(0, formatCost.length() - 1);
		String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[4] = formatCost; placeHolders[6] = this.cost.toString();
		LanguageAPI.getLang(false).sendLangMessage("general.itemSuccess", player, placeHolders);
    }
	
   /**
    * Withdraws the Commands Cost from the Vault Balance for the Player.
    * 
    * @param player - The Player to have their economy balance changed.
    */
    private void withdrawBalance(final Player player) {
		if (DependAPI.getDepends(false).getVault().vaultEnabled()) {
			double balance = 0.0;
			try { balance = DependAPI.getDepends(false).getVault().getBalance(player); } catch (NullPointerException e) { }
			int parseCost = this.cost;
			if (balance >= parseCost) {
				if (parseCost > 0) {
					try { DependAPI.getDepends(false).getVault().withdrawBalance(player, parseCost); } catch (NullPointerException e) { ServerUtils.sendDebugTrace(e); }
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[6] = this.cost.toString();
					LanguageAPI.getLang(false).sendLangMessage("general.econSuccess", player, placeHolders);
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
				player.playSound(player.getLocation(), this.commandSound, 1, 1);
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
    * @param player - The Player disposing of the ItemStack.
    * @param itemCopy - The ItemStack to be disposed.
    * @param allItems - If the item should not have its amount changed.
    */
	public void removeDisposable(final Player player, final ItemMap itemMap, final ItemStack itemCopy, final boolean allItems) {
		if (this.disposable && this.conditionMet(player, "disposable-conditions") || allItems) {
			if (!allItems) { this.setSubjectRemoval(true); }
			SchedulerUtils.runLater(1L, () -> {
				if (PlayerHandler.isCreativeMode(player)) { player.closeInventory(); }
				if (itemMap.isSimilar(player.getItemOnCursor())) {
					player.setItemOnCursor(ItemHandler.modifyItem(player.getItemOnCursor(), allItems, 1));
					if (!allItems) { this.setSubjectRemoval(false); }
				} else {
					int itemSlot = player.getInventory().getHeldItemSlot();
					if (itemMap.isSimilar(player.getInventory().getItem(itemSlot))) { player.getInventory().setItem(itemSlot, ItemHandler.modifyItem(player.getInventory().getItem(itemSlot), allItems, 1)); if (!allItems) { this.setSubjectRemoval(false); }}
					else { 
						for (int i = 0; i < player.getInventory().getSize(); i++) {
							if (itemMap.isSimilar(player.getInventory().getItem(i))) {
								player.getInventory().setItem(i, ItemHandler.modifyItem(player.getInventory().getItem(i), allItems, 1));
								if (!allItems) { this.setSubjectRemoval(false); }
								break;
							}
						}
					}
					if (this.isSubjectRemoval() && PlayerHandler.isCreativeMode(player)) {
						player.getInventory().addItem(ItemHandler.modifyItem(itemCopy, allItems, 1));
						player.setItemOnCursor(new ItemStack(Material.AIR));
						if (!allItems) { this.setSubjectRemoval(false); }
					} else if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
						for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
							if (itemMap.isSimilar(player.getOpenInventory().getTopInventory().getItem(i))) {
								player.getOpenInventory().getTopInventory().setItem(i, ItemHandler.modifyItem(player.getOpenInventory().getTopInventory().getItem(i), allItems, 1));
								if (!allItems) { this.setSubjectRemoval(false); }
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
					int timeLeft = (int)(this.interactCooldown - ((System.currentTimeMillis() - playersCooldownList) / 1000));
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
		boolean interactSpam = ConfigHandler.getConfig().getFile("items.yml").getBoolean("items-Spamming");
		if (interactSpam != true) {
			long playersCooldownList = 0L;
			if (this.storedSpammedPlayers.containsKey(PlayerHandler.getPlayerID(player) + ".items." + this.configName)) {
				playersCooldownList = this.storedSpammedPlayers.get(PlayerHandler.getPlayerID(player) + ".items." + this.configName);
			}
			if (System.currentTimeMillis() - playersCooldownList >= this.spamtime * 1000) { } 
			else { return false; }
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
			if (System.currentTimeMillis() - playersCooldownList >= this.cooldownSeconds * 1000) { return false; } 
			else if (this.onCooldownTick(player)) {
				String cooldownmsg = this.cooldownMessage != null ? (this.cooldownMessage.replace("%timeleft%", String.valueOf((this.cooldownSeconds - ((System.currentTimeMillis() - playersCooldownList) / 1000)))).replace("%item%", this.customName).replace("%itemraw%", ItemHandler.getMaterialName(this.tempItem))) : null;
				if (cooldownmsg != null && !this.cooldownMessage.isEmpty()) { 
					cooldownmsg = StringUtils.translateLayout(cooldownmsg, player);
					player.sendMessage(cooldownmsg);
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
		if (!ConfigHandler.getConfig().getFile("items.yml").getBoolean("items-Spamming")) {
			long playersCooldownList = 0L;
			if (this.playersOnCooldownTick.containsKey(PlayerHandler.getPlayerID(player))) {
				playersCooldownList = this.playersOnCooldownTick.get(PlayerHandler.getPlayerID(player));
			}
			
			if (!(System.currentTimeMillis() - playersCooldownList >= 1000)) { return false; }
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
    * @param map - The Map to have an ItemCommand addded.
    * @param command - The ItemCommand to be added to the Map.
    * @return The changed Map instance.
    */
	public Map<String, List<String>> addMapCommand(final Map<String, List<String>> map, final ItemCommand command) {
		String commandSection = (command.getSection() != null ? command.getSection() : "DEFAULT");
		if (map.get(commandSection) != null) { 
			List <String> s1 = map.get(commandSection); s1.add(command.getRawCommand());
			map.put(commandSection, s1);  
		} 
		else {
			List <String> s1 = new ArrayList<String>(); s1.add(command.getRawCommand());
			map.put(commandSection, s1);  
		}
		return map;
	}
	
   /**
    * Saves the ItemCommands to the items.yml.
    * 
    * @param itemData - The File having the data saved.
    * @param map - The Map of entries to save.
    * @param section - The item section being saved.
    */
	public void setMapCommand(final FileConfiguration itemData, final Map<String, List<String>> map, final String section) {
		Iterator<Entry<String, List<String>>> iterate = map.entrySet().iterator(); 
		while (iterate.hasNext()) { 
			Entry<String, List<String>> mapElement = (Entry<String, List<String>>)iterate.next(); 
			String mapKey = mapElement.getKey(); 
			if (mapKey.equalsIgnoreCase("DEFAULT") && map.size() <= 1) { mapKey = ""; } else { mapKey = "." + mapKey; }
			itemData.set("items." + this.configName + "." + section + mapKey, mapElement.getValue()); 
		}
	}
	
   /**
	* Gets the Shape of the Recipe of the Custom Item.
	* 
	* @return The trimmed recipe.
	*/
	public String[] trimRecipe(final List<String> list) {
		List < Character > recipe = new ArrayList < Character > ();
		List < String > recipeList = list;
		String[] recipeShape = { "XXX", "XXX", "XXX" };
		String[] shape = { "", "", "" };
		for (int i = 0; i < recipeList.size(); i++) {
			int charSize = 0;
			for (String character: recipeList.get(i).split("(?<!^)")) {
				StringBuilder sb = new StringBuilder(recipeShape[i]);
				sb.setCharAt(charSize, character.charAt(0));
				recipeShape[i] = sb.toString();
				charSize++;
			}
			for (String character: recipeShape[i].split("(?<!^)")) { 
				recipe.add(character.charAt(0));
			}
			shape[i] = recipeList.get(i).replace("X", " ");
		}
		this.setRecipe(recipe);
		if ((shape[2].length() == 3 && shape[2].charAt(0) == ' ' && shape[2].charAt(1) == ' ' && shape[2].charAt(2) == ' ') || shape[2].isEmpty()) {
			if (shape[0].length() == 3 && shape[0].charAt(2) == ' ' && shape[1].length() == 3 && shape[1].charAt(2) == ' ') {
				shape = new String[] { shape[0].substring(0, shape[0].length() - 1), shape[1].substring(0, shape[1].length() - 1) };
			} else if ((shape[0].length() == 3 && shape[0].charAt(0) == ' ' && shape[0].charAt(1) == ' ' && shape[0].charAt(2) == ' ') || shape[0].isEmpty()) {
				shape = new String[] { shape[1] };
			} else if ((shape[1].length() == 3 && shape[1].charAt(0) == ' ' && shape[1].charAt(1) == ' ' && shape[1].charAt(2) == ' ') || shape[1].isEmpty()) {
				shape = new String[] { shape[0] };
			} else {
				shape = new String[] { shape[0], shape[1] };
			}
		} else if (shape[0].length() < 3 && shape[1].length() < 3 && shape[2].length() == 3 && shape[2].charAt(0) == ' ' && shape[2].charAt(1) == ' ' && shape[2].charAt(2) == ' ') {
			shape = new String[] { shape[0], shape[1] };
		} else if (shape[0].length() == 3 && shape[1].length() == 3 && shape[2].length() == 3 && shape[0].charAt(2) == ' ' && shape[1].charAt(2) == ' ' && shape[2].charAt(2) == ' ') {
			shape = new String[] { shape[0].substring(0, shape[0].length() - 1), shape[1].substring(0, shape[1].length() - 1), shape[2].substring(0, shape[2].length() - 1) };
		}
		return shape;
	}
	
   /**
    * Removes this ItemMap instance from the items.yml.
    * 
    */
	public void removeFromConfig() {
		File itemFile =  new File (ItemJoin.getInstance().getDataFolder(), "items.yml");
		FileConfiguration itemData = YamlConfiguration.loadConfiguration(itemFile);
		if (ConfigHandler.getConfig().getFile("items.yml").getString("items." + this.configName) != null) { itemData.set("items." + this.configName, null); } 
		try { itemData.save(itemFile); ConfigHandler.getConfig().getSource("items.yml"); ConfigHandler.getConfig().getFile("items.yml").options().copyDefaults(false); } 
		catch (Exception e) { ItemJoin.getInstance().getServer().getLogger().severe("Could not remove the custom item " + this.configName + " from the items.yml data file!"); ServerUtils.sendDebugTrace(e); }	
	}
	
   /**
    * Saves the ItemMap to the items.yml.
    * 
    */
	public void saveToConfig() {
		File itemFile =  new File (ItemJoin.getInstance().getDataFolder(), "items.yml");
		FileConfiguration itemData = YamlConfiguration.loadConfiguration(itemFile);
		this.renderItemStack();
		if (ConfigHandler.getConfig().getFile("items.yml").getString("items." + this.configName) != null) { itemData.set("items." + this.configName, null); } 
		if (!(this.dynamicMaterials != null && !this.dynamicMaterials.isEmpty())) { itemData.set("items." + this.configName + ".id", this.material.toString().toUpperCase() + (this.dataValue != null && this.dataValue != 0 ? ":" + this.dataValue : "")); }
		else if (this.dynamicMaterials != null && !this.dynamicMaterials.isEmpty()) { 
			for (int i = 0; i < this.dynamicMaterials.size(); i++) {
				itemData.set("items." + this.configName + ".id." + (i + 1), this.dynamicMaterials.get(i)); 	
			}
		}
		if (this.AllSlots != null && !this.AllSlots.isEmpty()) { 
			String saveSlots = "";
			for (String slot : this.AllSlots) { saveSlots += slot + ", "; }
			if (!saveSlots.substring(0, saveSlots.length() - 2).equalsIgnoreCase("ARBITRARY")) {
				itemData.set("items." + this.configName + ".slot", saveSlots.substring(0, saveSlots.length() - 2)); 
			}
		}
		else if (this.CustomSlot == null) { itemData.set("items." + this.configName + ".slot", this.InvSlot); }
		else { 
			if (!this.CustomSlot.equalsIgnoreCase("ARBITRARY")) {
				itemData.set("items." + this.configName + ".slot", this.CustomSlot); 
			}
		}
		if (this.count > 1) { itemData.set("items." + this.configName + ".count", this.count); }
		if (this.durability != null && this.durability > 0) { itemData.set("items." + this.configName + ".durability", this.durability); }
		if (this.data != null && this.data > 0) { itemData.set("items." + this.configName + ".data", this.data); }
		if (this.modelData != null && this.modelData > 0) { itemData.set("items." + this.configName + ".model-data", this.modelData); }
		if (this.author != null && !this.author.isEmpty()) { itemData.set("items." + this.configName + ".author", this.author.replace("§", "&")); }
		if (this.customName != null && !this.customName.isEmpty() && (this.dynamicNames == null || this.dynamicNames.isEmpty())) { 
			String setName = null;
			if (this.legacySecret != null && !ServerUtils.hasSpecificUpdate("1_14")) {
				final ItemMeta itemMeta = this.tempItem.getItemMeta();
				itemMeta.setDisplayName("");
				this.tempItem.setItemMeta(itemMeta);
				StringUtils.colorEncode(this.tempItem, this.legacySecret);
				final String itemInfo = this.tempItem.getItemMeta().getDisplayName();
				setName = this.customName.replace(itemInfo, "").replace("§", "&");
			} else {
				setName = this.customName.replace("§", "&");
			}
			if (setName.startsWith("&f") && (!ItemHandler.dataTagsEnabled() || !ServerUtils.hasSpecificUpdate("1_8"))) { setName = setName.substring(2, setName.length()); }
				if (!ItemHandler.getMaterialName(this.tempItem).equalsIgnoreCase(setName)) { 
					itemData.set("items." + this.configName + ".name", setName); 
				}
			}
		else if (this.dynamicNames != null && !this.dynamicNames.isEmpty()) { 
			for (int i = 0; i < this.dynamicNames.size(); i++) {
				itemData.set("items." + this.configName + ".name." + (i + 1), this.dynamicNames.get(i).replace("§", "&")); 	
			}
		}
		if (this.customLore != null && !this.customLore.isEmpty() && (this.dynamicLores == null || this.dynamicLores.isEmpty())) { itemData.set("items." + this.configName + ".lore", this.customLore); }
		else if (this.dynamicLores != null && !this.dynamicLores.isEmpty()) { 
			for (int i = 0; i < this.dynamicLores.size(); i++) {
				List <String> lores = this.dynamicLores.get(i);
				for (int k = 0; k < lores.size(); k++) {
					lores.get(k).replace("§", "&");
				}
				itemData.set("items." + this.configName + ".lore." + (i + 1), lores); 	
			}
		}
		if (this.listPages != null && !this.listPages.isEmpty()) { 
			for (int i = 0; i < this.listPages.size(); i++) {
				List <String> pages = this.listPages.get(i);
				for (int k = 0; k < pages.size(); k++) {
					pages.get(k).replace("§", "&");
				}
				itemData.set("items." + this.configName + ".pages." + (i + 1), pages); 	
			}
		}
		if (this.probability != null && this.probability != -1 && this.probability != 0) { itemData.set("items." + this.configName + ".probability", this.probability); }
		if (this.commands != null && this.commands.length > 0) {
			Map<String, List<String>> interactAll = new HashMap<String, List<String>>();
			Map<String, List<String>> interactLeft = new HashMap<String, List<String>>();
			Map<String, List<String>> interactRight = new HashMap<String, List<String>>();
			Map<String, List<String>> interactAir = new HashMap<String, List<String>>();
			Map<String, List<String>> interactBlock = new HashMap<String, List<String>>();
			Map<String, List<String>> interactLeftAir = new HashMap<String, List<String>>();
			Map<String, List<String>> interactLeftBlock = new HashMap<String, List<String>>();
			Map<String, List<String>> interactRightAir = new HashMap<String, List<String>>();
			Map<String, List<String>> interactRightBlock = new HashMap<String, List<String>>();
			Map<String, List<String>> inventoryAll = new HashMap<String, List<String>>();
			Map<String, List<String>> inventoryMiddle = new HashMap<String, List<String>>();
			Map<String, List<String>> inventoryCreative = new HashMap<String, List<String>>();
			Map<String, List<String>> inventoryLeft = new HashMap<String, List<String>>();
			Map<String, List<String>> inventoryShiftLeft = new HashMap<String, List<String>>();
			Map<String, List<String>> inventoryRight = new HashMap<String, List<String>>();
			Map<String, List<String>> inventoryShiftRight = new HashMap<String, List<String>>();
			Map<String, List<String>> inventorySwapCursor = new HashMap<String, List<String>>();
			Map<String, List<String>> onEquip = new HashMap<String, List<String>>();
			Map<String, List<String>> unEquip = new HashMap<String, List<String>>();
			Map<String, List<String>> onHold = new HashMap<String, List<String>>();
			Map<String, List<String>> onDeath = new HashMap<String, List<String>>();
			Map<String, List<String>> onDamage = new HashMap<String, List<String>>();
			Map<String, List<String>> onHit = new HashMap<String, List<String>>();
			Map<String, List<String>> onFire = new HashMap<String, List<String>>();
			Map<String, List<String>> onConsume = new HashMap<String, List<String>>();
			Map<String, List<String>> onReceive = new HashMap<String, List<String>>();
			Map<String, List<String>> physical = new HashMap<String, List<String>>();
			for(ItemCommand command : this.commands) {
				if (command.matchAction(ItemCommand.Action.INTERACT_ALL)) { interactAll = this.addMapCommand(interactAll, command); }
				else if (command.matchAction(ItemCommand.Action.INTERACT_AIR)) { interactAir = this.addMapCommand(interactAir, command); }
				else if (command.matchAction(ItemCommand.Action.INTERACT_BLOCK)) { interactBlock = this.addMapCommand(interactBlock, command); }
				else if (command.matchAction(ItemCommand.Action.INTERACT_RIGHT_ALL)) { interactRight = this.addMapCommand(interactRight, command); }
				else if (command.matchAction(ItemCommand.Action.INTERACT_RIGHT_AIR)) { interactRightAir = this.addMapCommand(interactRightAir, command);}
				else if (command.matchAction(ItemCommand.Action.INTERACT_RIGHT_BLOCK)) { interactRightBlock = this.addMapCommand(interactRightBlock, command); }
				else if (command.matchAction(ItemCommand.Action.INTERACT_LEFT_ALL)) { interactLeft = this.addMapCommand(interactLeft, command); }
				else if (command.matchAction(ItemCommand.Action.INTERACT_LEFT_AIR)) { interactLeftAir = this.addMapCommand(interactLeftAir, command); }
				else if (command.matchAction(ItemCommand.Action.INTERACT_LEFT_BLOCK)) { interactLeftBlock = this.addMapCommand(interactLeftBlock, command); }
				
				else if (command.matchAction(ItemCommand.Action.INVENTORY_ALL)) { inventoryAll = this.addMapCommand(inventoryAll, command); }
				else if (command.matchAction(ItemCommand.Action.INVENTORY_MIDDLE)) { inventoryMiddle = this.addMapCommand(inventoryMiddle, command); }
				else if (command.matchAction(ItemCommand.Action.INVENTORY_CREATIVE)) { inventoryCreative = this.addMapCommand(inventoryCreative, command); }
				else if (command.matchAction(ItemCommand.Action.INVENTORY_LEFT)) { inventoryLeft = this.addMapCommand(inventoryLeft, command); }
				else if (command.matchAction(ItemCommand.Action.INVENTORY_SHIFT_LEFT)) { inventoryShiftLeft = this.addMapCommand(inventoryShiftLeft, command); }
				else if (command.matchAction(ItemCommand.Action.INVENTORY_RIGHT)) { inventoryRight = this.addMapCommand(inventoryRight, command); }
				else if (command.matchAction(ItemCommand.Action.INVENTORY_SHIFT_RIGHT)) { inventoryShiftRight = this.addMapCommand(inventoryShiftRight, command); }
				else if (command.matchAction(ItemCommand.Action.INVENTORY_SWAP_CURSOR)) { inventorySwapCursor = this.addMapCommand(inventorySwapCursor, command); }
				
				else if (command.matchAction(ItemCommand.Action.ON_EQUIP)) { onEquip = this.addMapCommand(onEquip, command); }
				else if (command.matchAction(ItemCommand.Action.UN_EQUIP)) { unEquip = this.addMapCommand(unEquip, command); }
				else if (command.matchAction(ItemCommand.Action.ON_HOLD)) { onHold = this.addMapCommand(onHold, command); }
				else if (command.matchAction(ItemCommand.Action.ON_DEATH)) { onDeath = this.addMapCommand(onDeath, command); }
				else if (command.matchAction(ItemCommand.Action.ON_DAMAGE)) { onDamage = this.addMapCommand(onDamage, command); }
				else if (command.matchAction(ItemCommand.Action.ON_HIT)) { onHit = this.addMapCommand(onHit, command); }
				else if (command.matchAction(ItemCommand.Action.ON_FIRE)) { onFire = this.addMapCommand(onFire, command); }
				else if (command.matchAction(ItemCommand.Action.ON_CONSUME)) { onConsume = this.addMapCommand(onConsume, command); }
				else if (command.matchAction(ItemCommand.Action.ON_RECEIVE)) { onReceive = this.addMapCommand(onReceive, command); }
				else if (command.matchAction(ItemCommand.Action.PHYSICAL)) { physical = this.addMapCommand(physical, command); }
			}
			if (!interactAll.isEmpty()) { this.setMapCommand(itemData, interactAll, "interact"); }
			if (!interactAir.isEmpty()) { this.setMapCommand(itemData, interactAir, "interact-air"); }
			if (!interactBlock.isEmpty()) { this.setMapCommand(itemData, interactBlock, "interact-block"); }
			if (!interactRight.isEmpty()) { this.setMapCommand(itemData, interactRight, "interact-right"); }
			if (!interactRightAir.isEmpty()) { this.setMapCommand(itemData, interactRightAir, "interact-air-right"); }
			if (!interactRightBlock.isEmpty()) { this.setMapCommand(itemData, interactRightBlock, "interact-block-right"); }
			if (!interactLeft.isEmpty()) { this.setMapCommand(itemData, interactLeft, "interact-left"); }
			if (!interactLeftAir.isEmpty()) { this.setMapCommand(itemData, interactLeftAir, "interact-air-left"); }
			if (!interactLeftBlock.isEmpty()) { this.setMapCommand(itemData, interactLeftBlock, "interact-block-left"); }
			
			if (!inventoryAll.isEmpty()) { this.setMapCommand(itemData, inventoryAll, "inventory"); }
			if (!inventoryMiddle.isEmpty()) { this.setMapCommand(itemData, inventoryMiddle, "inventory-middle"); }
			if (!inventoryCreative.isEmpty()) { this.setMapCommand(itemData, inventoryCreative, "inventory-creative"); }
			if (!inventoryLeft.isEmpty()) { this.setMapCommand(itemData, inventoryLeft, "inventory-left"); }
			if (!inventoryShiftLeft.isEmpty()) { this.setMapCommand(itemData, inventoryShiftLeft, "inventory-shift-left"); }
			if (!inventoryRight.isEmpty()) { this.setMapCommand(itemData, inventoryRight, "inventory-right"); }
			if (!inventoryShiftRight.isEmpty()) { this.setMapCommand(itemData, inventoryShiftRight, "inventory-shift-right"); }
			if (!inventorySwapCursor.isEmpty()) { this.setMapCommand(itemData, inventorySwapCursor, "inventory-swap-cursor"); }
			
			if (!onEquip.isEmpty()) { this.setMapCommand(itemData, onEquip, "on-equip"); }
			if (!unEquip.isEmpty()) { this.setMapCommand(itemData, unEquip, "un-equip"); }
			if (!onHold.isEmpty()) { this.setMapCommand(itemData, onHold, "on-hold"); }
			if (!onDeath.isEmpty()) { this.setMapCommand(itemData, onDeath, "on-death"); }
			if (!onDamage.isEmpty()) { this.setMapCommand(itemData, onDamage, "on-damage"); }
			if (!onHit.isEmpty()) { this.setMapCommand(itemData, onHit, "on-hit"); }
			if (!onFire.isEmpty()) { this.setMapCommand(itemData, onFire, "on-fire"); }
			if (!onConsume.isEmpty()) { this.setMapCommand(itemData, onConsume, "on-consume"); }
			if (!onReceive.isEmpty()) { this.setMapCommand(itemData, onReceive, "on-receive"); }
			if (!physical.isEmpty()) { this.setMapCommand(itemData, physical, "physical"); }
		}
		if (this.toggleCommands != null && !this.getToggleCommands().isEmpty()) { itemData.set("items." + this.configName + ".toggle", this.toggleCommands); }
		if (this.commandSound != null) { itemData.set("items." + this.configName + ".commands-sound", this.commandSound.name()); }
		if (this.commandParticle != null && !this.commandParticle.isEmpty()) { itemData.set("items." + this.configName + ".commands-particle", this.commandParticle); }
		if (this.sequence != null && this.sequence != CommandSequence.SEQUENTIAL) { itemData.set("items." + this.configName + ".commands-sequence", this.sequence.name()); }
		if (this.itemCost != null && !this.itemCost.isEmpty()) { itemData.set("items." + this.configName + ".commands-item", this.itemCost); }
		if (this.cost != null && this.cost != 0) { itemData.set("items." + this.configName + ".commands-cost", this.cost); }
		if (this.commandsReceive != null && this.commandsReceive != 0) { itemData.set("items." + this.configName + ".commands-receive", this.commandsReceive); }
		if (this.warmDelay != null && this.warmDelay != 0) { itemData.set("items." + this.configName + ".commands-warmup", this.warmDelay); }
		if (this.cooldownSeconds != null && this.cooldownSeconds != 0) { itemData.set("items." + this.configName + ".commands-cooldown", this.cooldownSeconds); }
		if (this.cooldownMessage != null && !this.cooldownMessage.isEmpty()) { itemData.set("items." + this.configName + ".cooldown-message", this.cooldownMessage); }
		if (this.toggleMessage != null && !this.toggleMessage.isEmpty()) { itemData.set("items." + this.configName + ".toggle-message", this.toggleMessage); }
		if (this.enchants != null && !this.enchants.isEmpty()) { 
			String enchantList = "";
			for (Entry<String, Integer> enchantments : this.enchants.entrySet()) { enchantList += enchantments.getKey() + ":" + enchantments.getValue() + ", "; }
			itemData.set("items." + this.configName + ".enchantment", enchantList.substring(0, enchantList.length() - 2)); 
		}
		if (this.fireworkType != null) { itemData.set("items." + this.configName + ".firework.type", this.fireworkType.name()); }
		if (this.power != null && this.power != 0) { itemData.set("items." + this.configName + ".firework.power", this.power); }
		if (this.fireworkFlicker) { itemData.set("items." + this.configName + ".firework.flicker", this.fireworkFlicker); }
		if (this.fireworkTrail) { itemData.set("items." + this.configName + ".firework.trail", this.fireworkTrail); }
		if (this.fireworkColor != null && !this.fireworkColor.isEmpty()) { 
			String colorList = "";
			for (DyeColor color : this.fireworkColor) { colorList += color.name() + ", "; }
			itemData.set("items." + this.configName + ".firework.colors", colorList.substring(0, colorList.length() - 2)); 
		}
		if (this.interactCooldown != null && this.interactCooldown != 0) { itemData.set("items." + this.configName + ".use-cooldown", this.interactCooldown); }
		if (this.teleportEffect != null && !this.teleportEffect.isEmpty()) { itemData.set("items." + this.configName + ".teleport-effect", this.teleportEffect); }
		if (this.teleportSound != null && !this.teleportSound.isEmpty()) { itemData.set("items." + this.configName + ".teleport-sound", this.teleportSound); }
		if (this.itemflags != null && !this.itemflags.isEmpty()) { itemData.set("items." + this.configName + ".itemflags", this.itemflags); }
		if (this.triggers != null && !this.triggers.isEmpty()) { itemData.set("items." + this.configName + ".triggers", this.triggers); }
		if (this.limitModes != null && !this.limitModes.isEmpty()) { itemData.set("items." + this.configName + ".limit-modes", this.limitModes); }
		if (this.toggleNode != null && !this.toggleNode.isEmpty()) { itemData.set("items." + this.configName + ".toggle-permission", this.toggleNode); }
		if (this.permissionNode != null && !this.permissionNode.isEmpty()) { itemData.set("items." + this.configName + ".permission-node", this.permissionNode); }
		if (this.leatherColor != null && !this.leatherColor.isEmpty()) { itemData.set("items." + this.configName + ".leather-color", this.leatherColor); }
		else if (this.leatherHex != null && !this.leatherHex.isEmpty()) { itemData.set("items." + this.configName + ".leather-color", this.leatherHex); }
		if (this.customMapImage != null && !this.customMapImage.isEmpty()) { itemData.set("items." + this.configName + ".custom-map-image", this.customMapImage); }
		if (this.mapId != -1) { itemData.set("items." + this.configName + ".map-id", this.mapId); }
		if (this.skullTexture != null && !this.skullTexture.isEmpty() && (this.dynamicTextures == null || this.dynamicTextures.isEmpty())) { itemData.set("items." + this.configName + ".skull-texture", this.skullTexture); }
		else if (this.dynamicTextures != null && !this.dynamicTextures.isEmpty()) { 
			for (int i = 0; i < this.dynamicTextures.size(); i++) {
				itemData.set("items." + this.configName + ".skull-texture." + (i + 1), this.dynamicTextures.get(i)); 	
			}
		}
		if (this.skullOwner != null && !this.skullOwner.isEmpty()&& (this.dynamicOwners == null || this.dynamicOwners.isEmpty())) { itemData.set("items." + this.configName + ".skull-owner", this.skullOwner); }
		else if (this.dynamicOwners != null && !this.dynamicOwners.isEmpty()) { 
			for (int i = 0; i < this.dynamicOwners.size(); i++) {
				itemData.set("items." + this.configName + ".skull-owner." + (i + 1), this.dynamicOwners.get(i)); 	
			}
		}
		if (this.chargeColor != null) { itemData.set("items." + this.configName + ".charge-color", this.chargeColor.name()); }
		if (this.bannerPatterns != null && !this.bannerPatterns.isEmpty()) { 
			String bannerList = "";
			for (Pattern pattern : this.bannerPatterns) { bannerList += pattern.getColor().name() + pattern.getPattern().name() + ", "; }
			itemData.set("items." + this.configName + ".banner-meta", bannerList.substring(0, bannerList.length() - 2)); 
		}
		if (this.recipe != null && !this.recipe.isEmpty() && this.ingredients != null && !this.ingredients.isEmpty()) {
			List<String> ingredientList = new ArrayList<String>();
			List<String> recipeTempList = new ArrayList<String>();
			List<String> recipeList = new ArrayList<String>();
			for (Character ingredient: this.ingredients.keySet()) { 
				ingredientList.add(ingredient + ":" + this.ingredients.get(ingredient).toString());
			}
			String recipeLine = "";
			for (Character recipeCharacter: this.recipe) {
				recipeLine += recipeCharacter;
				if (StringUtils.countCharacters(recipeLine) == 3) {
					recipeTempList.add(recipeLine);
					recipeLine = "";
				}
			}
			if (!recipeLine.isEmpty()) { 
				while (StringUtils.countCharacters(recipeTempList.get(0)) != StringUtils.countCharacters(recipeLine)) { recipeLine += "X"; }
				recipeTempList.add(recipeLine);  
			}
			for (String str: this.trimRecipe(recipeTempList)) { recipeList.add(str.replace(" ", "X")); }
			itemData.set("items." + this.configName + ".recipe", recipeList);
			itemData.set("items." + this.configName + ".ingredients", ingredientList);
		}
		if (this.mobsDrop()) {
			List<String> mobsList = new ArrayList<String>();
			for (EntityType mobs: this.mobsDrop.keySet()) { 
				mobsList.add(mobs.name() + ":" + this.mobsDrop.get(mobs).toString());
			}
			itemData.set("items." + this.configName + ".mobs-drop", mobsList);
		}
		if (this.blocksDrop()) {
			List<String> blocksList = new ArrayList<String>();
			for (Material blocks: this.blocksDrop.keySet()) { 
				blocksList.add(blocks.name() + ":" + this.blocksDrop.get(blocks).toString());
			}
			itemData.set("items." + this.configName + ".blocks-drop", blocksList);
		}
		if (this.effect != null && !this.effect.isEmpty()) { 
			String effectList = "";
			for (PotionEffect effects : this.effect) { effectList += effects.getType().getName() + ":" + effects.getAmplifier() + ":" + effects.getDuration() + ", "; }
			itemData.set("items." + this.configName + ".potion-effects", effectList.substring(0, effectList.length() - 2)); 
		}
		if (this.attributes != null && !this.attributes.isEmpty()) { 
			String attributeList = "";
			for (String attribute : this.attributes.keySet()) { attributeList += "{" + attribute + ":" + this.attributes.get(attribute) + "}, "; }
			itemData.set("items." + this.configName + ".attributes", attributeList.substring(0, attributeList.length() - 2)); 
		}
		if (this.contents != null && !this.contents.isEmpty()) { 
			itemData.set("items." + this.configName + ".contents", this.contents); 
		}
		if (this.nbtProperty != null && !this.nbtProperty.isEmpty()) { 
			String propertyList = "";
			for (String property : this.nbtProperty.keySet()) { propertyList += property + ":" + this.nbtProperty.get(property) + ", "; }
			itemData.set("items." + this.configName + ".properties", propertyList.substring(0, propertyList.length() - 2)); 
		}
		if (this.disposableConditions != null && !this.disposableConditions.isEmpty()) { 
			if (disposableConditions.size() == 1) {
				itemData.set("items." + this.configName + ".disposable-conditions", this.disposableConditions.get(0)); 
			} else if (triggerConditions.size() > 0) {
				itemData.set("items." + this.configName + ".disposable-conditions", this.disposableConditions); 
			}
		}
		if (this.disposableMessage != null && !this.disposableMessage.isEmpty()) { 
			itemData.set("items." + this.configName + ".disposable-fail-message", this.disposableMessage); 
		}
		if (this.triggerConditions != null && !this.triggerConditions.isEmpty()) { 
			if (triggerConditions.size() == 1) {
				itemData.set("items." + this.configName + ".trigger-conditions", this.triggerConditions.get(0)); 
			} else if (triggerConditions.size() > 0) {
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
				} else if (this.commandConditions.get(property).size() > 0) {
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
			String regionList = "";
			for (String region : this.enabledRegions) { regionList += region + ", "; }
			itemData.set("items." + this.configName + ".enabled-regions", regionList.substring(0, regionList.length() - 2)); 
		}
		if (this.enabledWorlds != null && !this.enabledWorlds.isEmpty()) { 
			String worldList = "";
			for (String world : this.enabledWorlds) { worldList += world + ", "; }
			if (!worldList.startsWith("ALL") && !worldList.startsWith("GLOBAL")) {
				itemData.set("items." + this.configName + ".enabled-worlds", worldList.substring(0, worldList.length() - 2)); 
			}
		}
		if (this.disabledWorlds != null && !this.disabledWorlds.isEmpty()) { 
			String worldList = "";
			for (String world : this.disabledWorlds) { worldList += world + ", "; }
			if (!worldList.startsWith("ALL") && !worldList.startsWith("GLOBAL")) {
				itemData.set("items." + this.configName + ".disabled-worlds", worldList.substring(0, worldList.length() - 2)); 
			}
		}
		try { itemData.save(itemFile); ConfigHandler.getConfig().getSource("items.yml"); ConfigHandler.getConfig().getFile("items.yml").options().copyDefaults(false); } 
		catch (Exception e) { ItemJoin.getInstance().getServer().getLogger().severe("Could not save the new custom item " + this.configName + " to the items.yml data file!"); ServerUtils.sendDebugTrace(e); }	
	}
	
   /**
    * Creates a Clone of the Current ItemMap instance.
    * 
    * @return The newly Cloned ItemMap instance.
    */
	public ItemMap clone() {
        try {
            Object clone = this.getClass().getDeclaredConstructor().newInstance();
	            for (Field field : this.getClass().getDeclaredFields()) {
	                field.setAccessible(true);
	                field.set(clone, field.get(this));
	            }
            return (ItemMap)clone;
        } catch(Exception e) { ServerUtils.sendDebugTrace(e); return this; }
    }
}