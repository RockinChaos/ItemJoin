package me.RockinChaos.itemjoin.giveitems.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.listeners.RegionEnter;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.DataStorage;
import me.RockinChaos.itemjoin.utils.Language;
import me.RockinChaos.itemjoin.utils.Legacy;
import me.RockinChaos.itemjoin.utils.Reflection;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.VaultAPI;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class ItemMap {
	
	private String configName;
	private ConfigurationSection nodeLocation;
	private Integer probability = -1;
	
	private ItemStack tempItem = null;
	private ItemMeta tempMeta = null;
	private Material material = null;
	private Short dataValue = 0;
	
	private String customName = null;
	private List < String > customLore = null;

	private Integer InvSlot = 0;
	private String CustomSlot = null;
	
	private String Arbitrary = null;
	private String itemValue = null;
	
	private Integer count = 1;
	
	private Short durability = null;
	
	private String author;
	private String title;
	private Generation generation;
	private List < String > bookPages = new ArrayList < String > ();
	
	private int mapId = 1;
	private MapView mapView = null;
	private String customMapImage = null;
    
    private FireworkEffect firework = null;
    private int power = 1;
    private DyeColor chargeColor = null;
    
    private String skullOwner = null;
    private String skullTexture = null;
    private boolean headDatabase = false;
    
    private List <PotionEffect> effect = null;
    private List <Pattern> bannerPatterns = null;
    
    private Color leatherColor = null;
    
	private int interactCooldown = 0;
	private boolean customConsumable = false;
	private Map < String, Integer > enchants = new HashMap < String, Integer > ();
	
	
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
	private List < String > dynamicNames = null;
	private List < List < String > > dynamicLores = null;
	private List < String > dynamicMaterials = null;
	private List < String > dynamicOwners = null;
	private List < String > dynamicTextures = null;
	private boolean materialAnimated = false;
	private boolean skullAnimated = false;
	private Map < Player, ItemAnimation > localeAnimations = new HashMap < Player, ItemAnimation > ();
//  ====================================================================================================== //
	
	
//  ============================================== //
//      ItemCommand Information for each item.     //
//  ============================================== //
	private ItemCommand[] commands;
	private Integer cooldownSeconds = 0;
	private String cooldownMessage;
	private Sound commandSound;
	private Integer cost = 0;
	private boolean useCooldown = false;
	private CommandSequence sequence = CommandSequence.SEQUENTIAL;
	private CommandType type = CommandType.INTERACT;
	private Map < String, Long > playersOnCooldown = new HashMap < String, Long > ();
	private HashMap < String, Long > playersOnCooldownTick = new HashMap < String, Long > ();
//  ============================================================================================= //
	
	
//  ============================================== //
//            Itemflags for each item.             //
//  ============================================== //
	private String itemflags;
	
	private boolean vanillaItem = false;
	private boolean vanillaStatus = false;
	private boolean unbreakable = false;
	private boolean countLock = false;
	private boolean cancelEvents = false;
	private boolean itemStore = false;
	private boolean noCrafting = false;
	private boolean noRepairing = false;
	private boolean animate = false;
	private boolean dynamic = false;
	private boolean blockPlacement = false;
	private boolean hideAttributes = false;
	private boolean hideDurability = false;
	private boolean blockMovement = false;
	private boolean closeInventory = false;
	private boolean selfDroppable = false;
	private boolean deathDroppable = false;
	private boolean disposable = false;
	private boolean allowModifications = false;
	private boolean alwaysGive = false;
	private boolean CreativeBypass = false;
	private boolean AllowOpBypass = false;
	
	private boolean onlyFirstJoin = false;
	private boolean onlyFirstWorld = false;
	private boolean ipLimited = false;
//  ============================================== //
	
//  ============================================== //
//             Triggers for each item.             //
//  ============================================== //
	private boolean giveOnDisabled = false;
	private boolean giveOnJoin = false;
	private boolean giveOnRespawn = false;
	private boolean giveOnWorldChange = false;
	private boolean giveOnRegionEnter = false;
	private boolean takeOnRegionLeave = false;
	
	private String triggers = null;
	private String limitModes = null;
	private boolean useOnLimitSwitch = false;
//  ============================================== //
	
	private String permissionNode = null;
	private boolean permissionNeeded = true;
	private boolean opPermissionNeeded = false;
	
	private String enabledRegions;
	private List < String > enabledWorlds = new ArrayList < String > ();
// ======================================================================================== //
	
//  ============================================== //
//  	  		 Initialize ItemMap  		       //
//  ============================================== //
	public ItemMap(String internalName, String slot) {
        this.nodeLocation = ConfigHandler.getItemSection(internalName);
        this.configName = internalName;
        
        this.setSlot(slot);
        this.setCount(this.nodeLocation.getString(".count"));
		this.setCommandCost();
		this.setCommandSound();
		this.setCommandCooldown();
		this.setCommandType();
		this.setCommandSequence();
		this.setCommands(ItemCommand.arrayFromString(this));
        this.setInteractCooldown();
        this.setItemflags();
        this.setLimitModes();
        this.setTriggers();
		this.setWorlds();
		this.setRegions();
        this.setPerm(this.nodeLocation.getString(".permission-node"));
        this.setPermissionNeeded(ConfigHandler.getConfig("config.yml").getBoolean("Items-Permissions"));
    	this.setOPPermissionNeeded(ConfigHandler.getConfig("config.yml").getBoolean("OPItems-Permissions"));
	}
//  ========================================================================================================= //
	
//  ============================================== //
//   Setter functions for first ItemMap creation.  //
//  ============================================== //
	private void setCommandCost() {
		if (this.nodeLocation.getString("commands-cost") != null && Utils.isInt(this.nodeLocation.getString("commands-cost"))) { this.cost = this.nodeLocation.getInt("commands-cost"); }
	}
	
	private void setCommandSound() {
		try { if (this.nodeLocation.getString(".commands-sound") != null) { this.commandSound = Sound.valueOf(this.nodeLocation.getString(".commands-sound")); } } 
		catch (Exception e) { ServerHandler.sendDebugTrace(e); ServerHandler.sendDebugMessage("&4Your server is running &eMC " + Reflection.getServerVersion() + 
				" and this version of Minecraft does not have the defined command-sound &e" + this.nodeLocation.getString(".commands-sound")); }
	    
	}
	
	private void setCommandCooldown() {
		this.useCooldown = this.nodeLocation.getString("commands-cooldown") != null;
		if (this.useCooldown) { this.cooldownSeconds = this.nodeLocation.getInt("commands-cooldown"); }
		this.cooldownMessage = this.nodeLocation.getString("cooldown-message");
	}
	
	private void setCommandType() {
		if (this.nodeLocation.getString("commands-type") != null) { 
			if (Utils.containsIgnoreCase(this.nodeLocation.getString("commands-type"), "INTERACT") 
				&& Utils.containsIgnoreCase(this.nodeLocation.getString("commands-type"), "INVENTORY")) { this.type = CommandType.BOTH; }
			else if (Utils.containsIgnoreCase(this.nodeLocation.getString("commands-type"), "INTERACT")) { this.type = CommandType.INTERACT; }
			else if (Utils.containsIgnoreCase(this.nodeLocation.getString("commands-type"), "INVENTORY")) { this.type = CommandType.INVENTORY; }
			else if (Utils.containsIgnoreCase(this.nodeLocation.getString("commands-type"), "BOTH") || Utils.containsIgnoreCase(this.nodeLocation.getString("commands-type"), "ALL")) { this.type = CommandType.BOTH; }
		}
	}
	
	private void setCommandSequence() {
		if (this.nodeLocation.getString("commands-sequence") != null) { 
		    if (Utils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "SEQUENTIAL")) { this.sequence = CommandSequence.SEQUENTIAL; }
			else if (Utils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "RANDOM")) { this.sequence = CommandSequence.RANDOM; }
		}
	}

	private void setInteractCooldown() {
        if (this.nodeLocation.getString(".use-cooldown") != null) {
        	this.interactCooldown = this.nodeLocation.getInt(".use-cooldown");
        }
	}
	
	private void setItemflags() {
		if (this.nodeLocation.getString(".itemflags") != null) {
			this.itemflags = this.nodeLocation.getString(".itemflags");
			this.vanillaItem = Utils.containsIgnoreCase(this.itemflags, "vanilla");
			this.vanillaStatus = Utils.containsIgnoreCase(this.itemflags, "vanilla-status");
			this.disposable = Utils.containsIgnoreCase(this.itemflags, "disposable");
			this.blockPlacement = Utils.containsIgnoreCase(this.itemflags, "placement");
			this.blockMovement = Utils.containsIgnoreCase(this.itemflags, "inventory-modify");
			if (!this.blockMovement) { this.blockMovement = Utils.containsIgnoreCase(this.itemflags, "inventory-close"); }
			this.closeInventory = Utils.containsIgnoreCase(this.itemflags, "inventory-close");
			this.allowModifications = Utils.containsIgnoreCase(this.itemflags, "allow-modifications");
			this.alwaysGive = Utils.containsIgnoreCase(this.itemflags, "always-give");
			this.dynamic = Utils.containsIgnoreCase(this.itemflags, "dynamic");
			this.animate = Utils.containsIgnoreCase(this.itemflags, "animate");
			this.itemStore = Utils.containsIgnoreCase(this.itemflags, "item-store");
			this.noCrafting = Utils.containsIgnoreCase(this.itemflags, "item-craftable");
			this.noRepairing = Utils.containsIgnoreCase(this.itemflags, "item-repairable");
			this.cancelEvents = Utils.containsIgnoreCase(this.itemflags, "cancel-events");
			this.countLock = Utils.containsIgnoreCase(this.itemflags, "count-lock");
			this.setOnlyFirstJoin(Utils.containsIgnoreCase(this.itemflags, "first-join"));
			this.onlyFirstWorld = Utils.containsIgnoreCase(this.itemflags, "first-world");
			this.ipLimited = Utils.containsIgnoreCase(this.itemflags, "ip-limit");
			this.deathDroppable = Utils.containsIgnoreCase(this.itemflags, "death-drops");
			this.selfDroppable = Utils.containsIgnoreCase(this.itemflags, "self-drops");
			this.AllowOpBypass = Utils.containsIgnoreCase(this.itemflags, "AllowOpBypass");
			this.CreativeBypass = Utils.containsIgnoreCase(this.itemflags, "CreativeBypass");
		}
	}
	
	private void setLimitModes() {
		this.limitModes = this.nodeLocation.getString(".limit-modes");
	}
	
	private void setTriggers() {
		if (this.nodeLocation.getString("triggers") != null) {
			this.triggers = this.nodeLocation.getString("triggers");
			this.giveOnDisabled = Utils.containsIgnoreCase(this.triggers, "DISABLED");
			this.giveOnJoin = Utils.containsIgnoreCase(this.triggers, "JOIN");
			this.setOnlyFirstJoin(Utils.containsIgnoreCase(this.triggers, "FIRST-JOIN"));
			this.giveOnRespawn = Utils.containsIgnoreCase(this.triggers, "RESPAWN");
		    this.giveOnWorldChange = Utils.containsIgnoreCase(this.triggers, "WORLD-CHANGE") || Utils.containsIgnoreCase(this.triggers, "WORLD-SWITCH");
		    this.onlyFirstWorld = Utils.containsIgnoreCase(this.triggers, "FIRST-WORLD");
			this.giveOnRegionEnter =Utils.containsIgnoreCase(this.triggers, "REGION-ENTER");
			this.takeOnRegionLeave = Utils.containsIgnoreCase(this.triggers, "REGION-REMOVE");
			this.useOnLimitSwitch = Utils.containsIgnoreCase(this.triggers, "GAMEMODE-SWITCH");
		} else { this.giveOnJoin = true; }
	}
	
	private void setRegions() {
		if (this.nodeLocation.getString(".enabled-regions") != null) {
			this.enabledRegions = this.nodeLocation.getString(".enabled-regions");
			String regionlist = this.nodeLocation.getString(".enabled-regions").replace(" ", "");
			String[] regions = regionlist.split(",");
			for (String region: regions) {
				RegionEnter.addLocaleRegion(region);
			}
		} else if (isGiveOnRegionEnter() || isTakeOnRegionLeave()) { RegionEnter.addLocaleRegion("UNDEFINED"); this.enabledRegions = "UNDEFINED"; }
	}
	
	private void setWorlds() {
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
	}
	
	public void renderItemStack() {
        if (this.dataValue != null) {
        	this.tempItem = Legacy.newLegacyItemStack(this.material, this.count, this.dataValue);
        } else { this.tempItem = new ItemStack(this.material, this.count); }
	}
//  ======================================================================================================================================================================================== //

//  ===================== //
//  ~ Setting Functions ~ //
//  ===================== //
	public void setTempItem(ItemStack temp) {
		this.tempItem = temp;
	}
	
	public void setTempMeta(ItemMeta temp) {
		this.tempMeta = temp;
	}
	
	public void setMaterial(Material mat) {
		this.material = mat;
	}
	
	public void setCustomName(String customName) {
		if (customName == null || customName.length() == 0) {
			this.customName = null;
			return;
		}
		this.customName = customName;
	}
	
	public void setDynamicNames(List<String> names) {
		this.dynamicNames = names;
	}
	
	public void setCustomLore(List < String > customLore) {
		if (customLore == null || customLore.size() == 0) {
			this.customLore = null;
			return;
		}
		this.customLore = new ArrayList < String > ();
		Iterator < String > iterator = customLore.iterator();
		while (iterator.hasNext()) {
			String s = iterator.next();
			this.customLore.add(s);
		}
	}
	
	public void setDynamicLores(List<List<String>> lores) {
		this.dynamicLores = lores;
	}
	
	public void setDynamicMaterials(List<String> mats) {
		this.dynamicMaterials = mats;
		this.materialAnimated = true;
	}
	
	public void setDynamicOwners(List<String> owners) {
		this.dynamicOwners = owners;
		this.skullAnimated = true;
	}
	
	public void setDynamicTextures(List<String> textures) {
		this.dynamicTextures = textures;
		this.skullAnimated = true;
	}
	
	public void removeFromAnimationHandler(Player player) {
		this.localeAnimations.remove(player);
	}
	
	public void setSlot(String slot) {
		if (ItemHandler.isCustomSlot(slot)) {
			this.CustomSlot = slot;
			this.itemValue = ItemHandler.getItemID(slot); // <--- NEEDS MAJOR WORK.
		} else if (Utils.isInt(slot)) {
			this.InvSlot = Integer.parseInt(slot);
			this.itemValue = ItemHandler.getItemID(slot); // <--- NEEDS MAJOR WORK.
		}
	}
	
	public void setEnabledWorlds(List<String> worlds) {
		this.enabledWorlds = worlds;
	}
	
	public void setEnabledRegions(String regions) {
		this.enabledRegions = regions;
	}
	
	public void setEnchantments(Map<String, Integer> enchantments) {
		this.enchants = enchantments;
	}
	
	public void setCommandType(CommandType type) {
		this.type = type;
	}
	
	public void setCommandsSequence(CommandSequence sequence) {
		this.sequence = sequence;
	}
	
	public void setCount(String count) {
		if (count != null && Utils.isInt(count)) {
			this.count = Integer.parseInt(count);
		}
	}
	
	public void setDurability(Short durability) {
		this.durability = durability;
	}
	
	public void setProbability(Integer probability) {
		this.probability = probability;
	}
	
	public void setCommandSound(Sound sound) {
		this.commandSound = sound;
	}
	
	public void setCost(Integer cost) {
		this.cost = cost;
	}
	
	public void setConfigName(String name) {
		this.configName = name;
	}
	
	public void setBannerPatterns(List <Pattern> patterns) {
		this.bannerPatterns = patterns;
	}
	
	public void setNodeLocation(ConfigurationSection node) {
		this.nodeLocation = node;
	}
	
	public void setDataValue(Short dataValue) {
		if (dataValue == null || dataValue == 0) {
			this.dataValue = null;
			return;
		}
		this.dataValue = dataValue;
	}
	
	public void setPerm(String permission) {
		this.permissionNode = permission == null || permission.length() == 0 ? null : permission;
	}
	
	public void setOnlyFirstJoin(boolean onlyOnFirstJoin) {
		this.onlyFirstJoin = onlyOnFirstJoin;
		this.giveOnJoin = true;
		if (onlyOnFirstJoin && this.giveOnRespawn) {
			this.giveOnRespawn = false;
		}
	}
	
	public void setOnlyFirstWorld(boolean onlyOnFirstWorld) {
		this.onlyFirstWorld = onlyOnFirstWorld;
		if (onlyOnFirstWorld && this.giveOnRespawn) {
			this.giveOnRespawn = false;
		}
	}
	
	public void setGiveOnJoin(boolean bool) {
		this.giveOnJoin = bool;
	}
	
	public void setGiveOnWorldChange(boolean bool) {
		this.giveOnWorldChange = bool;
	}
	
	public void setGiveOnRespawn(boolean bool) {
		this.giveOnRespawn = bool;
	}
	
	public void setGiveOnRegionEnter(boolean bool) {
		this.giveOnRegionEnter = bool;
	}
	
	public void setTakeOnRegionLeave(boolean bool) {
		this.takeOnRegionLeave = bool;
	}
	
	public void setGiveOnDisabled(boolean bool) {
		this.giveOnDisabled = bool;
	}
	
	public void setIpLimted(boolean bool) {
		this.ipLimited = bool;
	}
	
	public void setPermissionNeeded(boolean bool) {
		this.permissionNeeded = bool;
	}
	
	public void setOPPermissionNeeded(boolean bool) {
		this.opPermissionNeeded = bool;
	}
	
	public void setVanilla(boolean bool) {
		this.vanillaItem = bool;
	}
	
	public void setVanillaStatus(boolean bool) {
		this.vanillaStatus = bool;
	}
	
	public void setUnbreakable(boolean bool) {
		this.unbreakable = bool;
	}
	
	public void setCountLock(boolean bool) {
		this.countLock = bool;
	}
	
	public void setCancelEvents(boolean bool) {
		this.cancelEvents = bool;
	}
	
	public void setItemStore(boolean bool) {
		this.itemStore = bool;
	}
	
	public void setItemCraftable(boolean bool) {
		this.noCrafting = bool;
	}
	
	public void setItemRepairable(boolean bool) {
		this.noRepairing = bool;
	}
	
	public void setAllowModifications(boolean bool) {
		this.allowModifications = bool;
	}
	
	public void setAlwaysGive(boolean bool) {
		this.alwaysGive = bool;
	}
	
	public void setAnimate(boolean bool) {
		this.animate = bool;
	}
	
	public void setDynamic(boolean bool) {
		this.dynamic = bool;
	}
	
	public void setPlacement(boolean bool) {
		this.blockPlacement = bool;
	}
	
	public void setAttributes(boolean bool) {
		this.hideAttributes = bool;
	}
	
	public void setHideDurability(boolean bool) {
		this.hideDurability = bool;
	}
	
	public void setMovement(boolean bool) {
		this.blockMovement = bool;
	}
	
	public void setCloseInventory(boolean bool) {
		this.closeInventory = bool;
	}
	
	public void setDisposable(boolean bool) {
		this.disposable = bool;
	}
	
	public void setSelfDroppable(boolean bool) {
		this.selfDroppable = bool;
	}
	
	public void setDeathDroppable(boolean bool) {
		this.deathDroppable = bool;
	}
	
	public void setCreativeBypass(boolean bool) {
		this.CreativeBypass = bool;
	}
	
	public void setOpBypass(boolean bool) {
		this.AllowOpBypass = bool;
	}
	
	public void setAuthor(String auth) {
		this.author = auth;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setGeneration(Generation gen) {
		this.generation = gen;
	}
	
	public void setPages(List <String> pages) {
		this.bookPages = pages;
	}
	
	public void setMapID(int id) {
		this.mapId = id;
	}
	
	public void setMapView(MapView view) {
		this.mapView = view;
	}
	
	public void setMapImage(String mapIMG) {
		this.customMapImage = mapIMG;
	}
	
	public void setFirework(FireworkEffect fire, int power) {
		this.firework = fire;
		this.power = power;
	}
	
	public void setChargeColor(DyeColor dyeColor) {
		this.chargeColor = dyeColor;
	}
	
	public void setSkull(String skull) {
		this.skullOwner = skull;
	}
	
	public void setSkullTexture(String skull) {
		this.skullTexture = skull;
	}
	
	public void setHeadDatabase(boolean head) {
		this.headDatabase = head;
	}
	
	public void setPotionEffect(List <PotionEffect> potion) {
		this.effect = potion;
	}
	
	public void setLeatherColor(Color lColor) {
		this.leatherColor = lColor;
	}
	
	public void setNewNBTData(String nbt, Object tag) {
		this.newNBTData = nbt;
		this.newNBTTag = tag;
	}
	
	public void setLegacySecret(String nbt) {
		this.legacySecret = nbt;
	}
	
	public void setArbitrary(String arb) {
		this.Arbitrary = arb;
	}
	
	public void setItemValue(String val) {
		this.itemValue = val;
	}
	
    public void setCommands(ItemCommand[] commands) {
        this.commands = commands;
    }
    
    public void setCustomConsumable(boolean bool) {
    	customConsumable = bool;
    }
//  ================================================================================================================================================================================= //
	
//  ====================== //
//  ~ Accessor Functions ~ //
//  ====================== //
	public String getCustomName() {
		return this.customName;
	}
	
	public List<String> getDynamicNames() {
		return this.dynamicNames;
	}
	
	public List<String> getCustomLore() {
		return this.customLore;
	}
	
	public List<List<String>> getDynamicLores() {
		return this.dynamicLores;
	}
	
	public List<String> getDynamicMaterials() {
		return this.dynamicMaterials;
	}
	
	public List<String> getDynamicOwners() {
		return this.dynamicOwners;
	}
	
	public List<String> getDynamicTextures() {
		return this.dynamicTextures;
	}
	
	public Map<Player, ItemAnimation> getAnimationHandler() {
		return this.localeAnimations;
	}
	
	public String getSlot() {
		if (this.CustomSlot != null) {
			return this.CustomSlot;
		} else if (this.InvSlot != null) {
			return this.InvSlot.toString();
		}
		
		return null;
	}
	
	public List<String> getEnabledWorlds() {
		return this.enabledWorlds;
	}
	
	public String getEnabledRegions() {
		return this.enabledRegions;
	}
	
	public Map<String, Integer> getEnchantments() {
		return this.enchants;
	}
	
	public Integer getCount() {
		return this.count;
	}
	
	public String getItemFlags() {
		return this.itemflags;
	}
	
	public String getPermissionNode() {
		return permissionNode;
	}
	
	public Short getDataValue() {
		return this.dataValue;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	public Short getDurability() {
		if (this.durability != null) {
			return this.durability;	
		}
		return 0;
	}
	
	public Integer getProbability() {
		if (this.probability != null) {
			return this.probability;	
		}
		return 0;
	}
	
	public Sound getCommandSound() {
		return this.commandSound;	
	}
	
	public Integer getCost() {
		return this.cost;
	}
	
	public ItemStack getTempItem() {
		return this.tempItem;	
	}
	
	public ItemMeta getTempMeta() {
		return this.tempMeta;	
	}
	
	public String getConfigName() {
		return this.configName;	
	}
	
	public List <Pattern> getBannerPatterns() {
		return this.bannerPatterns;
	}
	
	public ConfigurationSection getNodeLocation() {
		return this.nodeLocation;	
	}
	
	public CommandType getCommandType() {
		return this.type;
	}
	
	public CommandSequence getCommandsSequence() {
		return this.sequence;
	}
	
	public String getAuthor() {
		return this.author;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public Generation getGeneration() {
		return this.generation;
	}
	
	public List <String> getPages() {
		return this.bookPages;
	}
	
	public int getMapID() {
		return this.mapId;
	}
	
	public MapView getMapView() {
		return this.mapView;
	}
	
	public String getMapImage() {
		return this.customMapImage;
	}
	
	public FireworkEffect getFirework() {
		return this.firework;
	}
	
	public int getFireworkPower() {
		return this.power;
	}
	
	public DyeColor getChargeColor() {
		return this.chargeColor;
	}
	
	public String getSkull() {
		return this.skullOwner;
	}
	
	public String getSkullTexture() {
		return this.skullTexture;
	}
	
	public List <PotionEffect> getPotionEffect() {
		return this.effect;
	}
	
	public Color getLeatherColor() {
		return this.leatherColor;
	}
	
	public String getNewNBTData() {
		return this.newNBTData;
	}
	
	public String getLegacySecret() {
		if (!DataStorage.hasNewNBTSystem()) {
			return this.legacySecret;
		} else { return ""; }
	}
	
	public String getArbitrary() {
		return this.Arbitrary;
	}
	
	public String getItemValue() {
		return this.itemValue;
	}
	
	public int getInteractCooldown() {
		return this.interactCooldown;
	}
	
	public String getCooldownMessage() {
		return this.cooldownMessage;
	}
	
	public boolean isCustomConsumable() {
		return this.customConsumable;
	}
	
	public boolean hasPermission(Player player) {
		String worldName = player.getWorld().getName();
		if (!this.isPermissionNeeded()) {
			return true;
		} else if (this.isOPPermissionNeeded() && player.isOp()) {
			if (player.isPermissionSet(PermissionsHandler.customPermissions(this.permissionNode, this.configName, worldName)) || player.isPermissionSet("itemjoin." + worldName + ".*")) {
				return true;
			}
		} else if (player.hasPermission(PermissionsHandler.customPermissions(this.permissionNode, this.configName, worldName)) || player.hasPermission("itemjoin." + worldName + ".*")) {
			return true;
		}
		return false;
	}
	
	public boolean isHeadDatabase() {
		return this.headDatabase;
	}
	
	public boolean isGiveOnJoin() {
		return this.giveOnJoin;
	}
	
	public boolean isGiveOnWorldChange() {
		return this.giveOnWorldChange;
	}
	
	public boolean isGiveOnRespawn() {
		return this.giveOnRespawn;
	}
	
	public boolean isGiveOnRegionEnter() {
		return this.giveOnRegionEnter;
	}
	
	public boolean isTakeOnRegionLeave() {
		return this.takeOnRegionLeave;
	}
	
	public boolean isGiveOnDisabled() {
		return this.giveOnDisabled;
	}
	
	public boolean isOnlyFirstJoin() {
		return this.onlyFirstJoin;
	}
	
	public boolean isOnlyFirstWorld() {
		return this.onlyFirstWorld;
	}
	
	public boolean isIpLimted() {
		return this.ipLimited;
	}
	
	public boolean isUseOnLimitSwitch() {
		return this.useOnLimitSwitch;
	}
	
	public boolean isLimitMode(GameMode newMode) {
		if (this.limitModes != null) {
			if (Utils.containsIgnoreCase(this.limitModes, newMode.name())) {
				return true;
			} else if (!Utils.containsIgnoreCase(this.limitModes, newMode.name())) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isPermissionNeeded() {
		return this.permissionNeeded;
	}
	
	public boolean isOPPermissionNeeded() {
		return this.opPermissionNeeded;
	}
	
	public boolean isVanilla() {
		return this.vanillaItem;
	}
	
	public boolean isVanillaStatus() {
		return this.vanillaStatus;
	}
	
	public boolean isUnbreakable() {
		return this.unbreakable;
	}
	
	public boolean isCountLock() {
		return this.countLock;
	}
	
	public boolean isCancelEvents() {
		return this.cancelEvents;
	}
	
	public boolean isItemStore() {
		return this.itemStore;
	}
	
	public boolean isItemCraftable() {
		return this.noCrafting;
	}
	
	public boolean isItemRepairable() {
		return this.noRepairing;
	}
	
	public boolean isModifiyable() {
		return this.allowModifications;
	}
	
	public boolean isAlwaysGive() {
		return this.alwaysGive;
	}
	
	public boolean isAnimated() {
		return this.animate;
	}
	
	public boolean isDynamic() {
		return this.dynamic;
	}
	
	public boolean isPlacement() {
		return this.blockPlacement;
	}
	
	public boolean isAttributes() {
		return this.hideAttributes;
	}
	
	public boolean isDurability() {
		return this.hideDurability;
	}
	
	public boolean isMovement() {
		return this.blockMovement;
	}
	
	public boolean isInventoryClose() {
		return this.closeInventory;
	}
	
	public boolean isDisposable() {
		return this.disposable;
	}

	public boolean isSelfDroppable() {
		return this.selfDroppable;
	}
	
	public boolean isDeathDroppable() {
		return this.deathDroppable;
	}
	
	public boolean isCreativeBypass() {
		return this.CreativeBypass;
	}
	
	public boolean isOpBypass() {
		return this.AllowOpBypass;
	}
	
	public boolean isAllowedItem(Player player, ItemStack item, String findFlag) {
		if (this.isSimilar(item)) {
			if (this.AllowOpBypass && player.isOp() || this.CreativeBypass && player.getGameMode() == GameMode.CREATIVE 
					|| findFlag.equalsIgnoreCase("inventory-modify") && player.hasPermission("itemjoin.bypass.inventorymodify") 
					&& ItemJoin.getInstance().getConfig().getBoolean("InventoryBypass-Permission") == true) {
				return false;
			} 
			else if (findFlag.equals("cancel-events")) { return cancelEvents; } 
			else if (findFlag.equals("self-drops")) { return selfDroppable; } 
			else if (findFlag.equals("death-drops")) { return deathDroppable; } 
			else if (findFlag.equals("inventory-modify")) { return blockMovement; }
			else if (findFlag.equals("inventory-close")) { return closeInventory; }
			else if (findFlag.equals("item-store")) { return itemStore; } 
			else if (findFlag.equals("item-craftable")) { return noCrafting; } 
			else if (findFlag.equals("item-repairable")) { return noRepairing; } 
			else if (findFlag.equals("placement")) { return blockPlacement; } 
			else if (findFlag.equals("count-lock")) { return countLock; }
		}
		return false;
	}
	
	public boolean isSkull() {
		if (this.material.toString().equalsIgnoreCase("PLAYER_HEAD") || this.material.toString().equalsIgnoreCase("SKULL_ITEM")) {
			return true;
		}
		return false;
	}
     
	public boolean isSimilar(ItemStack item) {
		if (item != null && item.getType() != Material.AIR && item.getType() == this.material || this.materialAnimated && item != null && item.getType() != Material.AIR && this.isMaterial(item)) {
			if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && ServerHandler.hasSpecificUpdate("1_8") 
					&& ItemHandler.getNBTData(item) != null && ItemHandler.getNBTData(item).contains(this.newNBTData) || this.legacySecret != null && item.hasItemMeta() 
					&& item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains(this.legacySecret) || this.vanillaItem && this.vanillaStatus) {
				if (!this.isSkull() && skullOwner == null || this.isSkull() && !this.skullAnimated && ((SkullMeta) item.getItemMeta()).hasOwner() 
						&& this.skullOwner != null && PlayerHandler.getSkullOwner(item).equalsIgnoreCase(this.skullOwner) 
						|| this.skullOwner != null && Utils.containsIgnoreCase(this.skullOwner, "%player%")
						|| this.isSkull() && this.skullTexture != null && this.skullOwner == null 
						&& ItemHandler.getSkullSkinTexture(item.getItemMeta()).equalsIgnoreCase(this.skullTexture)
						|| this.isSkull() && this.skullAnimated && this.isSkull(item) || this.isSkull() && this.skullTexture != null && this.skullOwner == null && this.isHeadDatabaseSimilar(item)) {
					if (isEnchantSimilar(item) || !item.getItemMeta().hasEnchants() && enchants.isEmpty() || this.isModifiyable()) {
						if (this.material.toString().toUpperCase().contains("BOOK") 
								&& this.isBookMeta(item) 
								&& ((BookMeta) item.getItemMeta()).getPages().equals(((BookMeta) tempItem.getItemMeta()).getPages())
								|| this.material.toString().toUpperCase().contains("BOOK") && !this.isBookMeta(item) || !this.material.toString().toUpperCase().contains("BOOK") || this.isModifiyable()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean isHeadDatabaseSimilar(ItemStack item) {
		if (this.headDatabase) {
			HeadDatabaseAPI api = new HeadDatabaseAPI();
			ItemStack itemCopy = api.getItemHead(this.skullTexture);
			if (itemCopy != null && ItemHandler.getSkullSkinTexture(item.getItemMeta()).equalsIgnoreCase(ItemHandler.getSkullSkinTexture(itemCopy.getItemMeta()))) {
				return true;
			}
		}
		return false;
	}
	private boolean isEnchantSimilar(ItemStack item) {
		if (item.getItemMeta().hasEnchants() && this.enchants != null && !this.enchants.isEmpty()) { 
			ItemStack checkItem = new ItemStack(item.getType());
			for (Entry<String, Integer> enchantments : this.enchants.entrySet()) {
				if (enchantments.getKey() == null && DataStorage.hasTokenEnchant() == true && TokenEnchantAPI.getInstance().getEnchant(enchantments.getKey()) != null) {
					TokenEnchantAPI.getInstance().enchant(null, checkItem, enchantments.getKey(), enchantments.getValue(), true, 0, true);
				} else { 
					checkItem.addUnsafeEnchantment(ItemHandler.getEnchantByName(enchantments.getKey()), enchantments.getValue()); }
			}
			return item.getItemMeta().getEnchants().equals(checkItem.getItemMeta().getEnchants());
		}
		return false;
	}
	
	private boolean isBookMeta(ItemStack item) {
		try {
			return ((BookMeta) item.getItemMeta()).hasPages();
		} catch (Exception e) { return false; }
	}
	
	public boolean isCountSimilar(ItemStack item) {
		if (item.getAmount() == count || ConfigHandler.getConfig("items.yml").getBoolean("items-RestrictCount") == false || this.isModifiyable()) {
			return true;
		}
		return false;
	}
	
	private boolean isMaterial(ItemStack item) {
		for (String material : this.dynamicMaterials) {
			material = ItemHandler.purgeDelay(material);
			String dataValue = null;
			if (material.contains(":")) { String[] parts = material.split(":"); dataValue = parts[1]; }
			if (item.getType() == ItemHandler.getMaterial(material, dataValue)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isSkull(ItemStack item) {
		if (this.dynamicOwners != null) {
			for (String owners : this.dynamicOwners) {
				owners = ItemHandler.purgeDelay(owners);
				if (PlayerHandler.getSkullOwner(item) != null && PlayerHandler.getSkullOwner(item).equalsIgnoreCase(this.skullOwner) || PlayerHandler.getSkullOwner(item) != null && Utils.containsIgnoreCase(this.skullOwner, "%player%") || ItemHandler.getGameProfiles().get(owners) != null && ItemHandler.getSkullSkinTexture(item.getItemMeta()).equalsIgnoreCase(this.getTexture(ItemHandler.getGameProfiles().get(owners)))) {
					return true;
				} else if (ItemHandler.getGameProfiles().get(owners) == null && !owners.equalsIgnoreCase("%player%")) {
					ItemHandler.generateProfile(owners);
					if (ItemHandler.getGameProfiles().get(owners) != null && ItemHandler.getSkullSkinTexture(item.getItemMeta()).equalsIgnoreCase(this.getTexture(ItemHandler.getGameProfiles().get(owners)))) {
						return true;
					}
				}
			}
			if (this.dynamicOwners.toString().contains("%player%")) { return true; }
		} else if (this.dynamicTextures != null) {
			for (String textures : this.dynamicTextures) {
				textures = ItemHandler.purgeDelay(textures);
				if (ItemHandler.getSkullSkinTexture(item.getItemMeta()).equalsIgnoreCase(textures)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String getTexture(GameProfile profile) {
		final Collection < Property > props = profile.getProperties().get("textures");
		for (final Property property: props) {
			if (property.getName().equals("textures")) { return property.getValue(); }
		}
		return "NULL";
	}
	
	public boolean hasItem(Player player) {
		for (ItemStack inPlayerInventory: player.getInventory().getContents()) {
			if (this.isSimilar(inPlayerInventory) && this.isCountSimilar(inPlayerInventory)) {
				return true;
			}
		}
		for (ItemStack inPlayerInventory: player.getEquipment().getArmorContents()) {
			if (this.isSimilar(inPlayerInventory) && this.isCountSimilar(inPlayerInventory)) {
				return true;
			}
		}
		if (ServerHandler.hasCombatUpdate() 
				&& this.isSimilar(player.getInventory().getItemInOffHand())
				&& this.isCountSimilar(player.getInventory().getItemInOffHand())) {
			return true;
		}
		return false;
	}
//  ================================================================================================================================================================================= //

//  ================================================================ //
//                      ~ Player Item Updater ~                      //
//  Method(s) update the ItemMap item for player specific variables. //
//  ================================================================ //
	public ItemMap updateItem(Player player) {
		this.setSkullDatabase();
		this.setUnbreaking();
		this.setEnchantments(player);
		this.setMapImage();
		this.setJSONBookPages(player);
		this.setNBTData();
		this.tempMeta = this.tempItem.getItemMeta();
		
		this.setCustomName(player);
		this.setCustomLore(player);
		this.setSkull(player);
		this.setDurability();
		this.setPotionEffects();
		this.setBanners();
		this.setFireworks();
		this.setFireChargeColor();
		this.setDye();
		this.setBookInfo(player);
		this.setLegacyBookPages(player);
		this.setAttributes();
		this.tempItem.setItemMeta(tempMeta);
		return this;
	}
	
	private void setSkullDatabase() {
		if (this.headDatabase && this.skullTexture != null) {
			HeadDatabaseAPI api = new HeadDatabaseAPI();
			ItemStack sk = api.getItemHead(this.skullTexture);
			this.tempItem = (sk != null ? sk : this.tempItem.clone());
		}
	}
	
	private void setEnchantments(Player player) {
		if (this.enchants != null && !this.enchants.isEmpty()) {
			for (Entry<String, Integer> enchantments : this.enchants.entrySet()) {
				if (enchantments.getKey() == null && DataStorage.hasTokenEnchant() == true && TokenEnchantAPI.getInstance().getEnchant(enchantments.getKey()) != null) {
					TokenEnchantAPI.getInstance().enchant(player, tempItem, enchantments.getKey(), enchantments.getValue(), true, 0, true);
				} else { this.tempItem.addUnsafeEnchantment(ItemHandler.getEnchantByName(enchantments.getKey()), enchantments.getValue()); }
			}
		}
	}
	
	private void setUnbreaking() {
		if (this.isUnbreakable() || this.hideDurability) {
			this.tempItem = Reflection.setUnbreakable(this.tempItem);
		}
	}
	
	private void setMapImage() {
		if (this.customMapImage != null) {
			if (ServerHandler.hasAquaticUpdate()) {
				MapMeta mapmeta = (MapMeta) this.tempItem.getItemMeta();
				try { mapmeta.setMapView(this.mapView); }
				catch (NoSuchMethodError e) { mapmeta = Legacy.setMapID(mapmeta, this.mapId); }
				this.tempItem.setItemMeta(mapmeta);
			} else {
				Legacy.setLegacyDurability(this.tempItem, (short) this.mapId);
			}
		}
	}
	
	private void setJSONBookPages(Player player) {
		if (this.getMaterial().toString().equalsIgnoreCase("WRITTEN_BOOK") && this.bookPages != null && ServerHandler.hasSpecificUpdate("1_8")) {
			Object localePages = null;
			try { localePages = Reflection.getNMS("NBTTagList").getConstructor().newInstance(); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
			for (String textComponent: this.bookPages) {
				try { 
					textComponent = Utils.translateLayout(textComponent, player);
					Object TagString = Reflection.getNMS("NBTTagString").getConstructor(String.class).newInstance(textComponent);
					localePages.getClass().getMethod("add", Reflection.getNMS("NBTBase")).invoke(localePages, TagString); 
				} catch (Exception e) { ServerHandler.sendDebugTrace(e); } 
			}
			try { this.invokePages(localePages); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		}
	}
	
	private void invokePages(Object pages) throws Exception {
		Class<?> craftItemStack = Reflection.getOBC("inventory.CraftItemStack");
		Object nms = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, this.getTempItem());
		Object tag = Reflection.getNMS("NBTTagCompound").getConstructor().newInstance();
		tag.getClass().getMethod("set", String.class, Reflection.getNMS("NBTBase")).invoke(tag, "pages", pages); 
		nms.getClass().getMethod("setTag", tag.getClass()).invoke(nms, tag);
		this.tempItem = ((ItemStack)craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms));
	}
	
	private void setNBTData() {
		if (DataStorage.hasNewNBTSystem() && !this.isVanilla()) {
			try {
				Object nms = Reflection.getOBC("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, this.tempItem);
				Object cacheTag = Reflection.getNMS("ItemStack").getMethod("getTag").invoke(nms);
				if (cacheTag != null) {
					cacheTag.getClass().getMethod("setString", String.class, String.class).invoke(cacheTag, "ItemJoin Name", this.getConfigName());
					cacheTag.getClass().getMethod("setString", String.class, String.class).invoke(cacheTag, "ItemJoin Slot", this.getItemValue());
				} else { nms.getClass().getMethod("setTag", this.newNBTTag.getClass()).invoke(nms, this.newNBTTag); }
				this.tempItem = (ItemStack) Reflection.getOBC("inventory.CraftItemStack").getMethod("asCraftMirror", nms.getClass()).invoke(null, nms);
			} catch (Exception e) {
				ServerHandler.sendDebugMessage("Error 15443 has occured when setting NBTData to an item.");
				ServerHandler.sendDebugTrace(e);
			}
		}
	}
	
	private void setCustomName(Player player) {
		if (this.customName != null && !this.customName.equalsIgnoreCase(ItemHandler.getName(this.tempItem))) {
			this.tempMeta.setDisplayName(Utils.translateLayout(ItemHandler.purgeDelay(this.customName), player));
		}
	}
	
	private void setCustomLore(Player player) {
		if (this.customLore != null && !this.customLore.isEmpty()) {
			List < String > loreList = this.customLore;
			List < String > loreFormatList = new ArrayList < String > ();
			for (int k = 0; k < loreList.size(); k++) {
				String formatLore = ItemHandler.purgeDelay(loreList.get(k));
				formatLore = Utils.translateLayout(formatLore, player);
				loreFormatList.add(formatLore);
			}
			this.tempMeta.setLore(loreFormatList);
		}
	}
	
	private void setDurability() {
		if (this.durability != null) {
			if (ServerHandler.hasAquaticUpdate()) {
				((Damageable) this.tempMeta).setDamage((short) this.durability);
			} else {
				Legacy.setLegacyDurability(this.tempItem, (short) this.durability);
			}
		}
	}
	
	private void setSkull(Player player) {
		if (this.skullOwner != null) {
			tempMeta = ItemHandler.setSkullOwner(tempMeta, Utils.translateLayout(this.skullOwner, player));
		} else if (this.skullTexture != null && !headDatabase) {
			try {
				GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
				gameProfile.getProperties().put("textures", new Property("textures", new String(this.skullTexture)));
				Field declaredField = this.tempMeta.getClass().getDeclaredField("profile");
				declaredField.setAccessible(true);
				declaredField.set(this.tempMeta, gameProfile);
			} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		}
	}
	
	private void setPotionEffects() {
		if (this.effect != null && !this.effect.isEmpty() && !this.customConsumable) {
			for (PotionEffect potion: this.effect) {
				((PotionMeta) this.tempMeta).addCustomEffect(potion, true);
			}
		}
	}
	
	private void setBanners() {
		if (this.bannerPatterns != null && !this.bannerPatterns.isEmpty()) {
			((BannerMeta) this.tempMeta).setPatterns(this.bannerPatterns);
		}
	}
	
	private void setFireworks() {
		if (this.firework != null) {
			((FireworkMeta) this.tempMeta).clearEffects();
			((FireworkMeta) this.tempMeta).addEffect(this.firework);
			((FireworkMeta) this.tempMeta).setPower(this.power);
			
		}
	}
	
	private void setFireChargeColor() {
		if (this.chargeColor != null) {
			((FireworkEffectMeta) this.tempMeta).setEffect(FireworkEffect.builder().withColor(this.chargeColor.getColor()).build());
		}
	}
	
	private void setDye() {
		if (this.leatherColor != null) {
			((LeatherArmorMeta) this.tempMeta).setColor(this.leatherColor);
		}
	}
	
	private void setBookInfo(Player player) {
		if (this.author != null) {
			this.author = Utils.translateLayout(this.author, player);
			((BookMeta) this.tempMeta).setAuthor(this.author);
		}
		
		if (this.title != null) {
			this.title = Utils.translateLayout(this.title, player);
			((BookMeta) this.tempMeta).setTitle(this.title);
		}
		
		if (this.generation != null) {
			((BookMeta) this.tempMeta).setGeneration(this.generation);
		}
	}
	
	private void setLegacyBookPages(Player player) {
		if (!ServerHandler.hasSpecificUpdate("1_8") && this.bookPages != null && !this.bookPages.isEmpty()) {
			List < String > bookList = new ArrayList < String > ();
			for (int k = 0; k < this.bookPages.size(); k++) {
				bookList.add(Utils.translateLayout(this.bookPages.get(k), player));
			}
			((BookMeta) tempMeta).setPages(bookList);
			this.bookPages = bookList;
		}
	}
	
	private void setAttributes() {
		if (ServerHandler.hasSpecificUpdate("1_8") && this.hideAttributes == true) {
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_DESTROYS);
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_PLACED_ON);
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_POTION_EFFECTS);
			this.tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
		}
	}
//  =========================================================================================================================================================== //	

	public Boolean inWorld(World world) {
		if (this.enabledWorlds == null) { return true; }
			for (String compareWorld: this.enabledWorlds) {
				if (compareWorld.equalsIgnoreCase(world.getName()) 
						|| compareWorld.equalsIgnoreCase("ALL") 
						|| compareWorld.equalsIgnoreCase("GLOBAL")) {
					return true;
				}
			}
		return false;
	}
	
	public void removeFrom(Player player) {
		PlayerInventory inv = player.getInventory();
		Inventory craftView = player.getOpenInventory().getTopInventory();
		ItemStack[] contents = inv.getContents();
		ItemStack[] craftingContents = player.getOpenInventory().getTopInventory().getContents();
		
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
		
		if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
			for (int k = 0; k < craftingContents.length; k++) {
				if (this.isSimilar(craftingContents[k])) { craftView.setItem(k, new ItemStack(Material.AIR)); }
			}
		}
	}
	
	public void giveTo(Player player, boolean noTriggers, int amount) {
		if (CustomSlot != null) { ItemUtilities.setCustomSlots(player, this, noTriggers, this.tempItem.clone(), amount); } 
		else { ItemUtilities.setInvSlots(player, this, noTriggers, this.tempItem.clone(), amount); }
		this.setAnimations(player);
	}
	
	public void setAnimations(Player player) {
		if (this.isAnimated() && this.getAnimationHandler().get(player) == null
				|| isDynamic() && this.getAnimationHandler().get(player) == null) {
			ItemAnimation Animator = new ItemAnimation(this);
			Animator.openAnimation(player);
			this.localeAnimations.put(player, Animator);
		}
	}
	
    public boolean executeCommands(Player player, String action) {
		boolean playerSuccess = false;
    	if (this.commands != null && this.commands.length > 0 && !this.onCooldown(player) && this.isPlayerChargeable(player)) {
    		if (isExecuted(player, action)) {
    			this.withdrawBalance(player, this.cost);
				this.playSound(player);
				this.removeDisposable(player, this.disposable);
				this.addPlayerOnCooldown(player);
    		}
    	}
    	return playerSuccess;
    }
    
    private boolean isExecuted(final Player player, final String action) {
    	boolean playerSuccess = false;
    	ItemCommand[] itemCommands = this.commands;
		for (int i = 0; i < itemCommands.length; i++) { 
			if (!playerSuccess) { playerSuccess = itemCommands[i].execute(player, action); }
			else { itemCommands[i].execute(player, action); }
		}
    	return playerSuccess;
    }
    
    private boolean isPlayerChargeable(Player player) {
		if (VaultAPI.vaultEnabled()) {
			double balance = 0.0; try { balance = PlayerHandler.getBalance(player); } catch (NullPointerException e) { }
			if (balance >= this.cost) {
				return true;
			} else if (!(balance >= this.cost)) {
				String[] placeHolders = Language.newString(); placeHolders[6] = this.cost.toString(); placeHolders[5] = balance + "";
				Language.sendLangMessage("General.itemChargeFailed", player, placeHolders);
				return false;
			}
		}
		return true;
	}
    
    private void withdrawBalance(Player player, int cost) {
		if (VaultAPI.vaultEnabled()) {
			double balance = 0.0;
			try { balance = PlayerHandler.getBalance(player); } catch (NullPointerException e) { }
			if (balance >= this.cost) {
				if (this.cost != 0) {
					try { PlayerHandler.withdrawBalance(player, this.cost); } catch (NullPointerException e) { ServerHandler.sendDebugTrace(e); }
					String[] placeHolders = Language.newString(); placeHolders[6] = this.cost.toString();
					Language.sendLangMessage("General.itemChargeSuccess", player, placeHolders);
				}
			}
		}
	}
	
	private void playSound(Player player) {
		if (this.commandSound != null) {
			try {
				player.playSound(player.getLocation(), this.commandSound, 1, 1);
			} catch (Exception e) {
				ServerHandler.sendErrorMessage("&cThere was an issue executing the commands-sound you defined.");
				ServerHandler.sendErrorMessage("&c" + this.commandSound + "&c is not a sound in " + Reflection.getServerVersion() + ".");
				ServerHandler.sendDebugTrace(e);
			}
		}
	}
	
	private void removeDisposable(final Player player, final boolean isDisposable) {
		final ItemStack item = PlayerHandler.getHandItem(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), (Runnable) new Runnable() {
			public void run() {
				if (isDisposable) {
				    if (player.getItemOnCursor() != null && player.getItemOnCursor().getType() != Material.AIR) { player.setItemOnCursor(null); } 
				    else if (item.getAmount() > 1 && item.getAmount() != 1) { item.setAmount(item.getAmount() - 1); } 
					else if (PlayerHandler.getMainHandItem(player) != null && PlayerHandler.getMainHandItem(player).getType() != Material.AIR && item.isSimilar(PlayerHandler.getMainHandItem(player))) { PlayerHandler.setItemInHand(player, Material.AIR); }
					else if (PlayerHandler.getOffHandItem(player) != null) { PlayerHandler.setOffhandItem(player, new ItemStack(Material.AIR)); }
				}
			}
		}, 1L);
	}
	
	private boolean onCooldown(Player player) {
		int cdmillis = this.cooldownSeconds * 1000;
		long playersCooldownList = 0L;
		if (playersOnCooldown.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player))) {
			playersCooldownList = playersOnCooldown.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player));
		}
		
		if (System.currentTimeMillis() - playersCooldownList >= cdmillis) { return false; } 
			if (this.cooldownMessage != null && onCooldownTick(player)) {
				String cooldownmsg = (this.cooldownMessage.replace("%timeleft%", String.valueOf((this.cooldownSeconds - ((System.currentTimeMillis() - playersCooldownList) / 1000)))).replace("%item%", this.customName).replace("%itemraw%", ItemHandler.getName(this.tempItem)));
				cooldownmsg = Utils.translateLayout(cooldownmsg, player);
				player.sendMessage(cooldownmsg);
				addPlayerOnCooldownTick(player);
			}
		return true;
	}
	
	private boolean onCooldownTick(Player player) {
		if (ConfigHandler.getConfig("items.yml").getBoolean("items-Spamming") != true) {
			long playersCooldownList = 0L;
			if (playersOnCooldownTick.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player))) {
				playersCooldownList = playersOnCooldownTick.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player));
			}
			
			if (!(System.currentTimeMillis() - playersCooldownList >= 1000)) { return false; }
		}
		return true;
	}
	
	private void addPlayerOnCooldownTick(Player player) {
		playersOnCooldownTick.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player), System.currentTimeMillis());
	}
	
	private void addPlayerOnCooldown(Player player) {
		this.playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player), System.currentTimeMillis());
	}
	
	public enum CommandType { BOTH, INTERACT, INVENTORY; }
	public enum CommandSequence { RANDOM, SEQUENTIAL, ALL; }
}