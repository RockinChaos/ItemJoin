package me.RockinChaos.itemjoin.giveitems.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
import me.RockinChaos.itemjoin.giveitems.listeners.PlayerGuard;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.EffectAPI;
import me.RockinChaos.itemjoin.utils.Language;
import me.RockinChaos.itemjoin.utils.Legacy;
import me.RockinChaos.itemjoin.utils.Reflection;
import me.RockinChaos.itemjoin.utils.Utils;
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
	private boolean dropFull = false;
	
	private String Arbitrary = null;
	private String itemValue = null;
	
	private Integer count = 1;
	
	private Short durability = null;
	
	private String author;
	private String title;
	private Generation generation;
	private List < String > bookPages = new ArrayList < String > ();
	private List < List <String> > listPages = new ArrayList < List <String> > ();
	
	private short mapId = 1;
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
    
    private String leatherColor;
    private String leatherHex;
    
	private Integer interactCooldown = 0;
	private boolean customConsumable = false;
	private Map < String, Integer > enchants = new HashMap < String, Integer > ();
	
	private Map < String, Long > playersOnInteractCooldown = new HashMap < String, Long > ();
	private HashMap < String, Long > storedSpammedPlayers = new HashMap < String, Long > ();
	private int spamtime = 1;
	
	
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
	private String cooldownMessage;
	private Sound commandSound;
	private String commandParticle;
	private Integer cost = 0;
	private Integer warmDelay = 0;
	private List < Player > warmPending = new ArrayList < Player > ();
	private boolean useCooldown = false;
	private boolean subjectRemoval = false;
	private CommandSequence sequence;
	private CommandType type;
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
	private boolean cancelEvents = false;
	private boolean itemStore = false;
	private boolean itemModify = false;
	private boolean noCrafting = false;
	private boolean noRepairing = false;
	private boolean animate = false;
	private boolean dynamic = false;
	private boolean overwritable = false;
	private boolean blockPlacement = false;
	private boolean hideAttributes = false;
	private boolean hideDurability = false;
	private boolean blockMovement = false;
	private boolean closeInventory = false;
	private boolean selfDroppable = false;
	private boolean deathDroppable = false;
	private boolean disposable = false;
	private boolean itemChangable = false;
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
	private boolean useOnLimitSwitch = false;
	
	private String triggers = null;
	private String limitModes = null;
//  ============================================== //
	
	private String permissionNode = null;
	private boolean permissionNeeded = true;
	private boolean opPermissionNeeded = false;
	
	private List < String > enabledRegions = new ArrayList < String > ();
	private List < String > enabledWorlds = new ArrayList < String > ();
// ======================================================================================== //
	
//  ============================================== //
//  	  		 Initialize ItemMap  		       //
//  ============================================== //
	public ItemMap() { }
	
	public ItemMap(String internalName, String slot) {
        this.nodeLocation = ConfigHandler.getItemSection(internalName);
        this.configName = internalName;
        this.setSlot(slot);
        if (ItemHandler.isCraftingSlot(slot)) { this.craftingItem = true; }
        
        
        if (this.nodeLocation != null) {
        	this.setMultipleSlots();
	        this.setCount(this.nodeLocation.getString(".count"));
			this.setCommandCost();
			this.setCommandWarmDelay();
			this.setCommandSound();
			this.setCommandParticle();
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
	        this.setPermissionNeeded(ConfigHandler.getConfig("config.yml").getBoolean("Permissions.Obtain-Items"));
	    	this.setOPPermissionNeeded(ConfigHandler.getConfig("config.yml").getBoolean("Permissions.Obtain-Items.OP"));
	    	ItemUtilities.setListenerRestrictions(this);
        }
	}
//  ========================================================================================================= //
	
//  ============================================== //
//   Setter functions for first ItemMap creation.  //
//  ============================================== //
	private void setMultipleSlots() {
        if (this.nodeLocation.getString(".slot").contains(",")) {
        	String[] slots = this.nodeLocation.getString(".slot").replace(" ", "").split(",");
			for (String s: slots) { this.AllSlots.add(s); }
        }
	}
	
	private void setCommandCost() {
		if (this.nodeLocation.getString("commands-cost") != null && Utils.isInt(this.nodeLocation.getString("commands-cost"))) { this.cost = this.nodeLocation.getInt("commands-cost"); }
	}
	
	private void setCommandWarmDelay() {
		if (this.nodeLocation.getString("commands-warmup") != null && Utils.isInt(this.nodeLocation.getString("commands-warmup"))) { this.warmDelay = this.nodeLocation.getInt("commands-warmup"); }
	}
	
	private void setCommandSound() {
		try { if (this.nodeLocation.getString(".commands-sound") != null) { this.commandSound = Sound.valueOf(this.nodeLocation.getString(".commands-sound")); } } 
		catch (Exception e) { ServerHandler.sendDebugTrace(e); ServerHandler.sendDebugMessage("&4Your server is running &eMC " + Reflection.getServerVersion() + 
				" and this version of Minecraft does not have the defined command-sound &e" + this.nodeLocation.getString(".commands-sound")); }
	    
	}
	
	private void setCommandParticle() {
		if (this.nodeLocation.getString(".commands-particle") != null) { this.commandParticle = this.nodeLocation.getString(".commands-particle"); }
	}
	
	private void setCommandCooldown() {
		this.useCooldown = this.nodeLocation.getString("commands-cooldown") != null;
		if (this.useCooldown) { this.cooldownSeconds = this.nodeLocation.getInt("commands-cooldown"); }
		this.cooldownMessage = this.nodeLocation.getString("cooldown-message");
	}
	
	public void setCommandCooldown(int i) {
		this.cooldownSeconds = i;
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
		    else if (Utils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "RANDOM_SINGLE")) { this.sequence = CommandSequence.RANDOM_SINGLE; }
			else if (Utils.containsIgnoreCase(this.nodeLocation.getString("commands-sequence"), "RANDOM")) { this.sequence = CommandSequence.RANDOM; }
		}
	}

	private void setInteractCooldown() {
        if (this.nodeLocation.getString(".use-cooldown") != null) {
        	this.interactCooldown = this.nodeLocation.getInt(".use-cooldown");
        }
	}
	
	private void setWarmPending(Player player) {
        if (!this.warmPending.contains(player)) {
        	this.warmPending.add(player);
        }
	}
	
	private void delWarmPending(Player player) {
        if (this.warmPending.contains(player)) {
        	this.warmPending.remove(player);
        }
	}
	
	private boolean getWarmPending(Player player) {
        if (this.warmPending.contains(player)) { return true; }
        return false;
	}
	
	private void setItemflags() {
		if (this.nodeLocation.getString(".itemflags") != null) {
			this.itemflags = this.nodeLocation.getString(".itemflags");
			this.vanillaItem = Utils.containsIgnoreCase(this.itemflags, "vanilla ");
			this.vanillaStatus = Utils.containsIgnoreCase(this.itemflags, "vanilla-status");
			this.vanillaControl = Utils.containsIgnoreCase(this.itemflags, "vanilla-control");
			this.disposable = Utils.containsIgnoreCase(this.itemflags, "disposable");
			this.blockPlacement = Utils.containsIgnoreCase(this.itemflags, "placement");
			this.blockMovement = Utils.containsIgnoreCase(this.itemflags, "inventory-modify");
			if (!this.blockMovement) { this.blockMovement = Utils.containsIgnoreCase(this.itemflags, "inventory-close"); }
			this.closeInventory = Utils.containsIgnoreCase(this.itemflags, "inventory-close");
			this.itemChangable = Utils.containsIgnoreCase(this.itemflags, "allow-modifications") || Utils.containsIgnoreCase(this.itemflags, "item-changable");
			this.alwaysGive = Utils.containsIgnoreCase(this.itemflags, "always-give");
			this.dynamic = Utils.containsIgnoreCase(this.itemflags, "dynamic");
			this.animate = Utils.containsIgnoreCase(this.itemflags, "animate");
			this.giveNext = Utils.containsIgnoreCase(this.itemflags, "give-next");
			this.dropFull = Utils.containsIgnoreCase(this.itemflags, "drop-full");
			this.itemStore = Utils.containsIgnoreCase(this.itemflags, "item-store");
			this.itemModify = Utils.containsIgnoreCase(this.itemflags, "item-modifiable");
			this.noCrafting = Utils.containsIgnoreCase(this.itemflags, "item-craftable");
			this.noRepairing = Utils.containsIgnoreCase(this.itemflags, "item-repairable");
			this.cancelEvents = Utils.containsIgnoreCase(this.itemflags, "cancel-events");
			this.countLock = Utils.containsIgnoreCase(this.itemflags, "count-lock");
			this.setOnlyFirstJoin(Utils.containsIgnoreCase(this.itemflags, "first-join"));
			this.onlyFirstWorld = Utils.containsIgnoreCase(this.itemflags, "first-world");
			this.overwritable = Utils.containsIgnoreCase(this.itemflags, "overwrite");
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
			if (Utils.containsIgnoreCase(this.triggers, "FIRST-JOIN")) { 
				this.onlyFirstJoin = true;
				this.giveOnJoin = true;
			}
		    this.giveOnWorldChange = Utils.containsIgnoreCase(this.triggers, "WORLD-CHANGE") || Utils.containsIgnoreCase(this.triggers, "WORLD-SWITCH");
			if (Utils.containsIgnoreCase(this.triggers, "FIRST-WORLD")) { 
				this.giveOnJoin = true;
				this.onlyFirstWorld = true;
				this.giveOnWorldChange = true;
			}
			this.giveOnRespawn = Utils.containsIgnoreCase(this.triggers, "RESPAWN");
			this.giveOnRegionEnter = Utils.containsIgnoreCase(this.triggers, "REGION-ENTER");
			this.takeOnRegionLeave = Utils.containsIgnoreCase(this.triggers, "REGION-REMOVE") || Utils.containsIgnoreCase(this.triggers, "REGION-EXIT") || Utils.containsIgnoreCase(this.triggers, "REGION-LEAVE");
			this.useOnLimitSwitch = Utils.containsIgnoreCase(this.triggers, "GAMEMODE-SWITCH");
		} else { this.giveOnJoin = true; }
	}
	
	private void setRegions() {
		if (this.nodeLocation.getString(".enabled-regions") != null && !this.nodeLocation.getString(".enabled-regions").isEmpty()) {
			String[] enabledParts = this.nodeLocation.getString(".enabled-regions").replace(" ,  ", ",").replace(" , ", ",").replace(",  ", ",").replace(", ", ",").split(",");
			for (String region: enabledParts) {
				this.enabledRegions.add(region); PlayerGuard.addLocaleRegion(region);
			}
		} else if (isGiveOnRegionEnter() || isTakeOnRegionLeave()) { PlayerGuard.addLocaleRegion("UNDEFINED"); this.enabledRegions.add("UNDEFINED"); }
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
			this.customLore = new ArrayList< String > ();
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
	
	public void setLimitModes(String str) {
		this.limitModes = str;
	}
	
	public void setSlot(String slot) {
		if (ItemHandler.isCustomSlot(slot)) {
			this.CustomSlot = slot;
			this.InvSlot = null;
			this.itemValue = ItemHandler.getItemID(slot); // <--- NEEDS MAJOR WORK.
		} else if (Utils.isInt(slot)) {
			this.InvSlot = Integer.parseInt(slot);
			this.CustomSlot = null;
			this.itemValue = ItemHandler.getItemID(slot); // <--- NEEDS MAJOR WORK.
		}
	}
	
	public void setMultipleSlots(List<String> slots) {
		this.AllSlots = slots;
	}
	
	public void setEnabledWorlds(List<String> worlds) {
		this.enabledWorlds = worlds;
	}
	
	public void setEnabledRegions(List<String> regions) {
		this.enabledRegions = regions;
	}
	
	public void setEnchantments(Map<String, Integer> enchantments) {
		this.enchants = enchantments;
	}
	
	public void setItemFlags(String itemflags) {
		this.itemflags = itemflags;
	}
	
	public void setTriggers(String triggers) {
		this.triggers = triggers;
	}
	
	public void setCommandType(CommandType type) {
		this.type = type;
	}
	
	public void setCommandSequence(CommandSequence sequence) {
		this.sequence = sequence;
	}
	
	public void setCommandParticle(String s) {
		this.commandParticle = s;
	}
	
	public void setCooldownMessage(String s) {
		this.cooldownMessage = s;
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
	
	public void setCommandCost(Integer cost) {
		this.cost = cost;
	}
	
	public void setWarmDelay(Integer delay) {
		this.warmDelay = delay;
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
	
	public void setOnlyFirstJoin(boolean bool) {
		this.onlyFirstJoin = bool;
		if (bool) { this.giveOnJoin = true; }
		if (bool && this.giveOnRespawn) {
			this.giveOnRespawn = false;
		}
	}
	
	public void setOnlyFirstWorld(boolean bool) {
		this.onlyFirstWorld = bool;
		if (bool) { this.giveOnJoin = true; }
		if (bool && this.giveOnRespawn) {
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
	
	public void setUseOnLimitSwitch(boolean bool) {
		this.useOnLimitSwitch = bool;
	}
	
	public void setIpLimited(boolean bool) {
		this.ipLimited = bool;
	}
	
	public void setInteractCooldown(int cooldown) {
        this.interactCooldown = cooldown;
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
	
	public void setVanillaControl(boolean bool) {
		this.vanillaControl = bool;
	}
	
	public void setGiveNext(boolean bool) {
		this.giveNext = bool;
	}
	
	public void setDropFull(boolean bool) {
		this.dropFull = bool;
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
	
	public void setItemModify(boolean bool) {
		this.itemModify = bool;
	}
	
	public void setItemCraftable(boolean bool) {
		this.noCrafting = bool;
	}
	
	public void setItemRepairable(boolean bool) {
		this.noRepairing = bool;
	}
	
	public void setItemChangable(boolean bool) {
		this.itemChangable = bool;
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
	
	public void setOverwritable(boolean bool) {
		this.overwritable = bool;
	}
	
	public void setPlaceable(boolean bool) {
		this.blockPlacement = bool;
	}
	
	public void setAttributesInfo(boolean bool) {
		this.hideAttributes = bool;
	}
	
	public void setDurabilityBar(boolean bool) {
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
	
	public void setListPages(List <List <String> > pages) {
		this.listPages = pages;
	}
	
	public void setMapID(int id) {
		this.mapId = (short) id;
	}
	
	public void setMapView(MapView view) {
		this.mapView = view;
	}
	
	public void setMapImage(String mapIMG) {
		this.customMapImage = mapIMG;
	}
	
	public void setFirework(FireworkEffect fire) {
		this.firework = fire;
	}
	
	public void setFireworkType(Type buildType) {
		this.fireworkType = buildType;
	}
	
	public void setFireworkColor(List<DyeColor> colors) {
		this.fireworkColor = colors;
	}
	
	public void setFireworkPower(int power) {
		this.power = power;
	}
	
	public void setFireworkTrail(boolean bool) {
		this.fireworkTrail = bool;
	}
	
	public void setFireworkFlicker(boolean bool) {
		this.fireworkFlicker = bool;
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
	
	public void setLeatherColor(String lColor) {
		this.leatherColor = lColor;
	}
	
	public void setLeatherHex(String hex) {
		this.leatherHex = hex;
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
	
	private void setSubjectRemoval(boolean bool) {
		this.subjectRemoval = bool;
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
		if (this.CustomSlot != null) { return this.CustomSlot; } 
		else if (this.InvSlot != null) { return this.InvSlot.toString(); }
		return null;
	}
	
	public List<String> getMultipleSlots() {
		return this.AllSlots;
	}
	
	public List<String> getEnabledWorlds() {
		return this.enabledWorlds;
	}
	
	public List<String> getEnabledRegions() {
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
	
	public String getTriggers() {
		return this.triggers;
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
	
	public Integer getWarmDelay() {
		return this.warmDelay;
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
	
	public String getLimitModes() {
		if (this.limitModes != null) {
			return this.limitModes;
		}
		return "NONE";
	}
	
	public ItemCommand[] getCommands() {
		return this.commands;
	}
	
	public CommandType getCommandType() {
		return this.type;
	}
	
	public Integer getCommandCooldown() {
		return this.cooldownSeconds;
	}
	
	public Integer getCommandCost() {
		return this.cost;
	}
	
	public String getCommandParticle() {
		return this.commandParticle;
	}
	
	public CommandSequence getCommandSequence() {
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
	
	public List <List <String> > getListPages() {
		return this.listPages;
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
	
	public Type getFireworkType() {
		return this.fireworkType;
	}
	
	public int getFireworkPower() {
		return this.power;
	}
	
	public List<DyeColor> getFireworkColor() {
		return this.fireworkColor;
	}
	
	public boolean getFireworkTrail() {
		return this.fireworkTrail;
	}
	
	public boolean getFireworkFlicker() {
		return this.fireworkFlicker;
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
	
	public String getLeatherColor() {
		return this.leatherColor;
	}
	
	public String getLeatherHex() {
		return this.leatherHex;
	}
	
	public String getNewNBTData() {
		return this.newNBTData;
	}
	
	public String getLegacySecret() {
		if (!ItemUtilities.dataTagsEnabled()) {
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
	
	public boolean isCraftingItem() {
		return this.craftingItem;
	}
	
	public boolean isHeadDatabase() {
		return this.headDatabase;
	}
	
	public boolean isGiveNext() {
		return this.giveNext;
	}
	
	public boolean isDropFull() {
		return this.dropFull;
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
	
	public Boolean inRegion(String region) {
		if (this.enabledRegions == null) { return false; }
			for (String compareRegion: this.enabledRegions) {
				if (compareRegion.equalsIgnoreCase(region) || compareRegion.equalsIgnoreCase("UNDEFINED")) {
					return true;
				}
			}
		return false;
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
	
	public boolean isVanillaControl() {
		return this.vanillaControl;
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
	
	public boolean isItemModify() {
		return this.itemModify;
	}
	
	public boolean isItemCraftable() {
		return this.noCrafting;
	}
	
	public boolean isItemRepairable() {
		return this.noRepairing;
	}
	
	public boolean isItemChangable() {
		return this.itemChangable;
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
	
	public boolean isOverwritable() {
		return this.overwritable;
	}
	
	public boolean isPlaceable() {
		return this.blockPlacement;
	}
	
	public boolean isAttributesInfo() {
		return this.hideAttributes;
	}
	
	public boolean isDurabilityBar() {
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
	
	private boolean isSubjectRemoval() {
		return this.subjectRemoval;
	}
	
	public boolean isAllowedItem(Player player, ItemStack item, String findFlag) {
		if (this.isSimilar(item)) {
			if (this.AllowOpBypass && player.isOp() || this.CreativeBypass && player.getGameMode() == GameMode.CREATIVE 
					|| findFlag.equalsIgnoreCase("inventory-modify") && player.hasPermission("itemjoin.bypass.inventorymodify") 
					&& ItemJoin.getInstance().getConfig().getBoolean("Permissions.Movement-Bypass")) {
				return false;
			} 
			else if (findFlag.equals("cancel-events")) { return cancelEvents; } 
			else if (findFlag.equals("self-drops")) { return selfDroppable; } 
			else if (findFlag.equals("death-drops")) { return deathDroppable; } 
			else if (findFlag.equals("inventory-modify")) { return blockMovement; }
			else if (findFlag.equals("inventory-close")) { return closeInventory; }
			else if (findFlag.equals("item-store")) { return itemStore; } 
			else if (findFlag.equals("item-modifiable")) { return itemModify; } 
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
			if (this.vanillaControl || ItemUtilities.dataTagsEnabled() && ServerHandler.hasSpecificUpdate("1_8") && ItemHandler.getNBTData(item) != null && Utils.containsIgnoreCase(ItemHandler.getNBTData(item), this.newNBTData)
					|| this.legacySecret != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains(this.legacySecret) || this.vanillaStatus) {
				if (this.skullCheck(item)) {
					if (isEnchantSimilar(item) || !item.getItemMeta().hasEnchants() && enchants.isEmpty() || this.isItemChangable()) {
						if (this.material.toString().toUpperCase().contains("BOOK") 
								&& this.isBookMeta(item) 
								&& ((BookMeta) item.getItemMeta()).getPages().equals(((BookMeta) tempItem.getItemMeta()).getPages())
								|| this.material.toString().toUpperCase().contains("BOOK") && !this.isBookMeta(item) || !this.material.toString().toUpperCase().contains("BOOK") || this.isItemChangable()) {
							if (!this.vanillaControl || this.vanillaControl && statsCheck(item)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private boolean statsCheck(ItemStack item) {
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && this.tempMeta.hasDisplayName() && item.getItemMeta().hasLore() && this.tempMeta.hasLore()) {
			if (item.getItemMeta().getDisplayName().equalsIgnoreCase(this.tempMeta.getDisplayName()) && item.getItemMeta().getLore().toString().equalsIgnoreCase(this.tempMeta.getLore().toString())) {
				return true;
			}
		} else if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && this.tempMeta.hasDisplayName() && !item.getItemMeta().hasLore() && !this.tempMeta.hasLore()) {
			if (item.getItemMeta().getDisplayName().equalsIgnoreCase(this.tempMeta.getDisplayName())) {
				return true;
			}
		} else if (item.hasItemMeta() && !item.getItemMeta().hasDisplayName() && !this.tempMeta.hasDisplayName() && item.getItemMeta().hasLore() && this.tempMeta.hasLore()) {
			if (item.getItemMeta().getLore().toString().equalsIgnoreCase(this.tempMeta.getLore().toString())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean skullCheck(ItemStack item) {
		if (!this.isSkull() || skullOwner == null && this.skullTexture == null && PlayerHandler.getSkullOwner(item).equalsIgnoreCase("NULL") && ItemHandler.getSkullSkinTexture(item.getItemMeta()).isEmpty() 
				|| !this.skullAnimated && ((SkullMeta) item.getItemMeta()).hasOwner() && this.skullOwner != null && PlayerHandler.getSkullOwner(item).equalsIgnoreCase(this.skullOwner) 
				|| this.skullOwner != null && this.isSkullOwner(item)
				|| this.skullOwner != null && Utils.containsIgnoreCase(this.skullOwner, "%player%")
				|| this.skullTexture != null && this.skullOwner == null 
				&& ItemHandler.getSkullSkinTexture(item.getItemMeta()).equalsIgnoreCase(this.skullTexture)
				|| this.skullAnimated && this.isSkull(item) || this.skullTexture != null && this.skullOwner == null && this.isHeadDatabaseSimilar(item)) {
			return true;
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
				if (enchantments.getKey() == null && ConfigHandler.getDepends().tokenEnchantEnabled() && TokenEnchantAPI.getInstance().getEnchant(enchantments.getKey()) != null) {
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
		if (item.getAmount() == count || ConfigHandler.getConfig("items.yml").getBoolean("items-RestrictCount") == false || this.isItemChangable()) {
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
		if (this.dynamicOwners != null && !this.dynamicOwners.isEmpty()) {
			for (String owners : this.dynamicOwners) {
				owners = ItemHandler.purgeDelay(owners);
				if (PlayerHandler.getSkullOwner(item) != null && PlayerHandler.getSkullOwner(item).equalsIgnoreCase(this.skullOwner) || PlayerHandler.getSkullOwner(item) != null && Utils.containsIgnoreCase(this.skullOwner, "%player%")) {
					return true;
				} else if (this.isSkullOwner(item) && this.isSkull()){
					return true;
				}
			}
			if (this.dynamicOwners.toString().contains("%player%")) { return true; }
		} else if (this.dynamicTextures != null && !this.dynamicTextures.isEmpty()) {
			for (String textures : this.dynamicTextures) {
				textures = ItemHandler.purgeDelay(textures);
				if (ItemHandler.getSkullSkinTexture(item.getItemMeta()).equalsIgnoreCase(textures)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isSkullOwner(ItemStack item) {
		if (ItemHandler.getSkullSkinTexture(item.getItemMeta()).equalsIgnoreCase(ItemHandler.getSkullSkinTexture(this.tempMeta))) {
			return true;	
		}
		return false;
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
	
	public ItemStack getItem(Player player) {
		return updateItem(player).getTempItem();
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
				if (enchantments.getKey() == null && ConfigHandler.getDepends().tokenEnchantEnabled() && TokenEnchantAPI.getInstance().getEnchant(enchantments.getKey()) != null) {
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
				Legacy.setLegacyDurability(this.tempItem, this.mapId);
			}
		}
	}
	
	private void setJSONBookPages(Player player) {
		if (this.getMaterial().toString().equalsIgnoreCase("WRITTEN_BOOK") && this.bookPages != null && ServerHandler.hasSpecificUpdate("1_8")) {
			Object localePages = null;
			try { localePages = Reflection.getNMS("NBTTagList").getConstructor().newInstance(); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
			if (ServerHandler.hasSpecificUpdate("1_14")) { this.set1_14JSONPages(player, localePages); } 
			else { this.set1_13JSONPages(player, localePages); }
		}
	}
	
	private void set1_13JSONPages(Player player, Object localePages) {
		for (String textComponent: this.bookPages) {
			try { 
				textComponent = Utils.translateLayout(textComponent, player);
				Object TagString = Reflection.getNMS("NBTTagString").getConstructor(String.class).newInstance(textComponent);
				localePages.getClass().getMethod("add", Reflection.getNMS("NBTBase")).invoke(localePages, TagString);
			} catch (Exception e) { ServerHandler.sendDebugTrace(e); } 
		}
		try { this.invokePages(localePages); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
	}
	
	private void set1_14JSONPages(Player player, Object localePages) {
		for (int i = this.bookPages.size() - 1; i >= 0; i--) {
			String textComponent = this.bookPages.get(i);
			try { 
				textComponent = Utils.translateLayout(textComponent, player);
				Object TagString = Reflection.getNMS("NBTTagString").getConstructor(String.class).newInstance(textComponent);
				localePages.getClass().getMethod("add", int.class, Reflection.getNMS("NBTBase")).invoke(localePages, 0, TagString);
			} catch (Exception e) { ServerHandler.sendDebugTrace(e); } 
		}
		try { this.invokePages(localePages); } catch (Exception e) { ServerHandler.sendDebugTrace(e); }
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
		if (ItemUtilities.dataTagsEnabled() && !this.isVanilla() && !this.isVanillaControl() && !this.isVanillaStatus()) {
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
				((Damageable) this.tempMeta).setDamage(this.durability);
			} else {
				Legacy.setLegacyDurability(this.tempItem, this.durability);
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
			((LeatherArmorMeta) this.tempMeta).setColor(DyeColor.valueOf(this.leatherColor).getFireworkColor());
		} else if (this.leatherHex != null) {
			((LeatherArmorMeta) this.tempMeta).setColor(Utils.getColorFromHexColor(this.leatherHex));
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
		if (ServerHandler.hasSpecificUpdate("1_8") && this.hideAttributes) {
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
	
	public void removeFrom(Player player, int amount) {
		PlayerInventory inv = player.getInventory();
		Inventory craftView = player.getOpenInventory().getTopInventory();
		ItemStack[] contents = inv.getContents();
		ItemStack[] craftingContents = player.getOpenInventory().getTopInventory().getContents();
		this.updateItem(player);
		if (amount == 0) {
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
		} else {
			for (int k = 0; k < contents.length; k++) {
				if (this.isSimilar(contents[k])) { inv.setItem(k, newItem(inv.getItem(k), amount)); return; }
			}
			if (this.isSimilar(inv.getHelmet())) { inv.setHelmet(newItem(inv.getHelmet(), amount)); }
			else if (this.isSimilar(inv.getChestplate())) { inv.setChestplate(newItem(inv.getHelmet(), amount)); }
			else if (this.isSimilar(inv.getLeggings())) { inv.setLeggings(newItem(inv.getHelmet(), amount)); }
			else if (this.isSimilar(inv.getBoots())) { inv.setBoots(newItem(inv.getHelmet(), amount)); }
			else if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
				for (int k = 0; k < craftingContents.length; k++) {
					if (this.isSimilar(craftingContents[k])) { craftView.setItem(k, newItem(player.getOpenInventory().getItem(k), amount)); return; }
				}
			}
		}
	}
	
	private ItemStack newItem(ItemStack itemCopy, int amount) {
		ItemStack item = new ItemStack(itemCopy);
		if (item.getAmount() > amount && item.getAmount() != amount || item.getAmount() < amount) { item.setAmount(item.getAmount() - amount); } 
		else if (item.getAmount() == amount) { item = new ItemStack(Material.AIR); }
		return item;
	}
	
	public void giveTo(Player player, boolean noTriggers, int amount) {
		this.updateItem(player);
		if (CustomSlot != null) { ItemUtilities.setCustomSlots(player, this, noTriggers, this.tempItem.clone(), amount); } 
		else { ItemUtilities.setInvSlots(player, this, noTriggers, this.tempItem.clone(), amount); }
		this.setAnimations(player);
	}
	
	public void giveTo(Player player, int amount, String slot) {
		this.updateItem(player);
		ItemStack item = this.tempItem.clone();
		if (amount > 0) { item.setAmount(amount); }
		if (Utils.containsIgnoreCase(slot, "CRAFTING")) { player.getOpenInventory().getTopInventory().setItem(ItemUtilities.getSlotConversion(slot), item);} 
		else { player.getInventory().setItem(Integer.parseInt(slot), item); }
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

    public boolean executeCommands(final Player player, final ItemStack itemCopy, final String action, final String slot) {
		boolean playerSuccess = false;
    	if (this.commands != null && this.commands.length > 0 && !ConfigHandler.getItemCreator().isOpen(player) && !this.getWarmPending(player) && isExecutable(player, action) && !this.onCooldown(player) && this.isPlayerChargeable(player)) {
    		this.warmCycle(player, this, this.getWarmDelay(), player.getLocation(), itemCopy, action, slot);
    	}
    	return playerSuccess;
    }
	
	private void warmCycle(final Player player, final ItemMap itemMap, final int warmCount, final Location location, final ItemStack itemCopy, final String action, final String slot) {
		if (warmCount != 0) {
			if (itemMap.warmDelay == warmCount) { 
				String[] placeHolders = Language.newString(); placeHolders[13] = warmCount + ""; placeHolders[0] = player.getWorld().getName(); placeHolders[3] = Utils.translateLayout(itemMap.getCustomName(), player); 
				Language.sendLangMessage("General.itemWarmingUp", player, placeHolders); 
				itemMap.setWarmPending(player); 
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
				@Override
				public void run() {
					if (itemMap.warmLocation(player, location)) {
						String[] placeHolders = Language.newString(); placeHolders[13] = warmCount + ""; placeHolders[0] = player.getWorld().getName(); placeHolders[3] = Utils.translateLayout(itemMap.getCustomName(), player); 
						Language.sendLangMessage("General.itemWarming", player, placeHolders);
						itemMap.warmCycle(player, itemMap, warmCount - 1, location, itemCopy, action, slot);	
					} else { 
						itemMap.delWarmPending(player); 
						String[] placeHolders = Language.newString(); placeHolders[13] = warmCount + ""; placeHolders[0] = player.getWorld().getName(); placeHolders[3] = Utils.translateLayout(itemMap.getCustomName(), player); 
						Language.sendLangMessage("General.itemWarmingHalted", player, placeHolders);
					}
				}
			}, 20);
		} else {
			int delay = 0;
			if (itemMap.warmDelay != 0) { delay = 20; }
			Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
				@Override
				public void run() {
					if (!player.isDead()) {
						if (isExecuted(player, action, slot, itemCopy)) { 
							itemMap.withdrawBalance(player, itemMap.cost);
			    			itemMap.playSound(player);
			    			itemMap.playParticle(player);
							itemMap.removeDisposable(player, itemCopy, false);
							itemMap.addPlayerOnCooldown(player);
						}
					} else {
						String[] placeHolders = Language.newString(); placeHolders[13] = warmCount + ""; placeHolders[0] = player.getWorld().getName(); placeHolders[3] = Utils.translateLayout(itemMap.getCustomName(), player); 
						Language.sendLangMessage("General.itemWarmingHalted", player, placeHolders);
					}
					if (itemMap.warmDelay != 0) { itemMap.delWarmPending(player); }
				}
			}, delay);
		}
	}
	
	private boolean warmLocation(final Player player, final Location location) {
	    if (player.getLocation().distance(location) >= 1 || player.isDead()) {
	    	return false;
	    }
	    return true;
	}
    
    private boolean isExecutable(final Player player, final String action) {
    	boolean playerSuccess = false;
    	ItemCommand[] itemCommands = this.commands;
    	for (int i = 0; i < itemCommands.length; i++) {
    		if (!playerSuccess) { playerSuccess = itemCommands[i].canExecute(player, action); }
			else { break; }
		}
    	return playerSuccess;
    }
    
    private boolean getRandomMap(final HashMap < Integer, ItemCommand > randomCommands, ItemCommand[] itemCommands, final Player player, final String action, final String slot) {
    	Entry<?, ?> dedicatedMap = Utils.randomEntry(randomCommands);
    	if (!((ItemCommand)dedicatedMap.getValue()).execute(player, action, slot)) { 
    		this.getRandomMap(randomCommands, itemCommands, player, action, slot);
    		return false;
    	}
    	return true;
    }
    
    private boolean getRandomAll(final HashMap < Integer, ItemCommand > randomCommands, ItemCommand[] itemCommands, final Player player, final String action, final String slot) {
    	Entry<?, ?> dedicatedMap = Utils.randomEntry(randomCommands);
    	if (!((ItemCommand)dedicatedMap.getValue()).execute(player, action, slot)) { 
    		randomCommands.remove(dedicatedMap.getKey());
    		this.getRandomAll(randomCommands, itemCommands, player, action, slot);
    		return false;
    	}
    	randomCommands.remove(dedicatedMap.getKey());
    	if (!randomCommands.isEmpty()) {
    		this.getRandomAll(randomCommands, itemCommands, player, action, slot);
    	}
    	return true;
    }
    
    private boolean isExecuted(final Player player, final String action, final String slot, final ItemStack itemCopy) {
    	boolean playerSuccess = false;
    	ItemCommand[] itemCommands = this.commands;
    	HashMap < Integer, ItemCommand > randomCommands = new HashMap < Integer, ItemCommand > ();
    	if (!this.subjectRemoval) {
    		boolean isSwap = false;
    		for (int i = 0; i < itemCommands.length; i++) { 
        		if (this.sequence == CommandSequence.RANDOM) { randomCommands.put(Utils.getRandom(1, 100000), itemCommands[i]); }
        		else if (this.sequence == CommandSequence.RANDOM_SINGLE) { randomCommands.put(Utils.getRandom(1, 100000), itemCommands[i]); }
        		else if (!playerSuccess) { playerSuccess = itemCommands[i].execute(player, action, slot); }
				else { itemCommands[i].execute(player, action, slot); }
        		if (Utils.containsIgnoreCase(itemCommands[i].getCommand(), "swap-item")) { isSwap = true; }
			}
    		if (isSwap) { this.removeDisposable(player, itemCopy, true); }
    	}
    	if (this.sequence == CommandSequence.RANDOM) { playerSuccess = this.getRandomAll(randomCommands, itemCommands, player, action, slot); }
    	else if (this.sequence == CommandSequence.RANDOM_SINGLE) { playerSuccess = this.getRandomMap(randomCommands, itemCommands, player, action, slot); }
    	return playerSuccess;
    }
    
    private boolean isPlayerChargeable(Player player) {
		if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
			double balance = 0.0; try { balance = ConfigHandler.getDepends().getVault().getBalance(player); } catch (NullPointerException e) { }
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
		if (ConfigHandler.getDepends().getVault().vaultEnabled()) {
			double balance = 0.0;
			try { balance = ConfigHandler.getDepends().getVault().getBalance(player); } catch (NullPointerException e) { }
			if (balance >= this.cost) {
				if (this.cost != 0) {
					try { ConfigHandler.getDepends().getVault().withdrawBalance(player, this.cost); } catch (NullPointerException e) { ServerHandler.sendDebugTrace(e); }
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
	
	private void playParticle(Player player) {
		if (this.commandParticle != null) {
			EffectAPI.spawnParticle(player, this.commandParticle);
		}
	}
	
	private void removeDisposable(final Player player, final ItemStack itemCopy, final boolean allItems) {
		if (this.disposable || allItems) {
			if (!allItems) { setSubjectRemoval(true); }
			Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
				@Override
				public void run() {
					if (PlayerHandler.isCreativeMode(player)) { player.closeInventory(); }
					if (isSimilar(player.getItemOnCursor())) {
						player.setItemOnCursor(newItem(player.getItemOnCursor(), allItems));
						if (!allItems) { setSubjectRemoval(false); }
					} else {
						int itemSlot = player.getInventory().getHeldItemSlot();
						if (isSimilar(player.getInventory().getItem(itemSlot))) { player.getInventory().setItem(itemSlot, newItem(player.getInventory().getItem(itemSlot), allItems)); if (!allItems) { setSubjectRemoval(false); }}
						else { 
							for (int i = 0; i < player.getInventory().getSize(); i++) {
								if (isSimilar(player.getInventory().getItem(i))) {
									player.getInventory().setItem(i, newItem(player.getInventory().getItem(i), allItems));
									if (!allItems) { setSubjectRemoval(false); }
									break;
								}
							}
						}
						if (isSubjectRemoval() && PlayerHandler.isCreativeMode(player)) {
							player.getInventory().addItem(newItem(itemCopy, allItems));
							player.setItemOnCursor(new ItemStack(Material.AIR));
							if (!allItems) { setSubjectRemoval(false); }
						}
					}
				}
			}, 1L);
		}
	}
	
	private ItemStack newItem(ItemStack itemCopy, final boolean allItems) {
		ItemStack item = new ItemStack(itemCopy);
		if (item.getAmount() > 1 && item.getAmount() != 1 && !allItems) { item.setAmount(item.getAmount() - 1); } 
		else { item = new ItemStack(Material.AIR); }
		return item;
	}
	
	public boolean onInteractCooldown(Player player) {
		long playersCooldownList = 0L;
		if (this.playersOnInteractCooldown.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + this.configName)) {
			playersCooldownList = this.playersOnInteractCooldown.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + this.configName);
		}
		if (System.currentTimeMillis() - playersCooldownList >= this.interactCooldown * 1000) {
			this.playersOnInteractCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + this.configName, System.currentTimeMillis());
			return false;
		} else {
			if (this.onSpamCooldown(player)) {
				this.storedSpammedPlayers.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + this.configName, System.currentTimeMillis());
				if (this.cooldownMessage != null && !this.cooldownMessage.isEmpty()) {
					int timeLeft = (int)(this.interactCooldown - ((System.currentTimeMillis() - playersCooldownList) / 1000));
					player.sendMessage(Utils.translateLayout(this.cooldownMessage.replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", this.customName), player));
				}
			}
			return true;
		}
	}
	
	private boolean onSpamCooldown(Player player) {
		boolean interactSpam = ConfigHandler.getConfig("items.yml").getBoolean("items-Spamming");
		if (interactSpam != true) {
			long playersCooldownList = 0L;
			if (this.storedSpammedPlayers.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + this.configName)) {
				playersCooldownList = this.storedSpammedPlayers.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + this.configName);
			}
			if (System.currentTimeMillis() - playersCooldownList >= this.spamtime * 1000) { } 
			else { return false; }
		}
		return true;
	}
	
	private boolean onCooldown(Player player) {
		long playersCooldownList = 0L;
		if (this.playersOnCooldown.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player))) {
			playersCooldownList = this.playersOnCooldown.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player));
		}
		
		if (System.currentTimeMillis() - playersCooldownList >= this.cooldownSeconds * 1000) { return false; } 
		else if (this.cooldownMessage != null && onCooldownTick(player)) {
				String cooldownmsg = (this.cooldownMessage.replace("%timeleft%", String.valueOf((this.cooldownSeconds - ((System.currentTimeMillis() - playersCooldownList) / 1000)))).replace("%item%", this.customName).replace("%itemraw%", ItemHandler.getName(this.tempItem)));
				cooldownmsg = Utils.translateLayout(cooldownmsg, player);
				player.sendMessage(cooldownmsg);
				this.addPlayerOnCooldownTick(player);
			}
		return true;
	}
	
	private boolean onCooldownTick(Player player) {
		if (!ConfigHandler.getConfig("items.yml").getBoolean("items-Spamming")) {
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
	
	public void saveToConfig() {
		File itemFile =  new File (ItemJoin.getInstance().getDataFolder(), "items.yml");
		FileConfiguration itemData = YamlConfiguration.loadConfiguration(itemFile);
		if (ConfigHandler.getConfig("items.yml").getString("items." + this.configName) != null) { itemData.set("items." + this.configName, null); } 
		if (!(this.dynamicMaterials != null && !this.dynamicMaterials.isEmpty())) { itemData.set("items." + this.configName + ".id", this.material.toString().toUpperCase() + (this.dataValue != 0 ? ":" + this.dataValue : "")); }
		else if (this.dynamicMaterials != null && !this.dynamicMaterials.isEmpty()) { 
			for (int i = 0; i < this.dynamicMaterials.size(); i++) {
				itemData.set("items." + this.configName + ".id." + (i + 1), this.dynamicMaterials.get(i)); 	
			}
		}
		if (this.AllSlots != null && !this.AllSlots.isEmpty()) { 
			String saveSlots = "";
			for (String slot : this.AllSlots) { saveSlots += slot + ", "; }
			itemData.set("items." + this.configName + ".slot", saveSlots.substring(0, saveSlots.length() - 2)); 
		}
		else if (this.CustomSlot == null) { itemData.set("items." + this.configName + ".slot", this.InvSlot); }
		else { itemData.set("items." + this.configName + ".slot", this.CustomSlot); }
		if (this.count > 1) { itemData.set("items." + this.configName + ".count", this.count); }
		if (this.durability != null && this.durability > 0) { itemData.set("items." + this.configName + ".durability", this.durability); }
		if (this.author != null && !this.author.isEmpty()) { itemData.set("items." + this.configName + ".author", this.author.replace("§", "&")); }
		if (this.customName != null && !this.customName.isEmpty() && (this.dynamicNames == null || this.dynamicNames.isEmpty())) { itemData.set("items." + this.configName + ".name", this.customName.replace("§", "&")); }
		else if (this.dynamicNames != null && !this.dynamicNames.isEmpty()) { 
			for (int i = 0; i < this.dynamicNames.size(); i++) {
				itemData.set("items." + this.configName + ".name." + (i + 1), this.dynamicNames.get(i)); 	
			}
		}
		if (this.customLore != null && !this.customLore.isEmpty() && (this.dynamicLores == null || this.dynamicLores.isEmpty())) { itemData.set("items." + this.configName + ".lore", this.customLore); }
		else if (this.dynamicLores != null && !this.dynamicLores.isEmpty()) { 
			for (int i = 0; i < this.dynamicLores.size(); i++) {
				itemData.set("items." + this.configName + ".lore." + (i + 1), this.dynamicLores.get(i)); 	
			}
		}
		if (this.listPages != null && !this.listPages.isEmpty()) { 
			for (int i = 0; i < this.listPages.size(); i++) {
				itemData.set("items." + this.configName + ".pages." + (i + 1), this.listPages.get(i)); 	
			}
		}
		if (this.probability != null && this.probability != -1 && this.probability != 0) { itemData.set("items." + this.configName + ".probability", this.probability); }
		if (this.commands != null && this.commands.length > 0) {
			List<String> multiClickAll = new ArrayList<String>();
			List<String> leftClickAll = new ArrayList<String>();
			List<String> rightClickAll = new ArrayList<String>();
			List<String> multiClickAir = new ArrayList<String>();
			List<String> multiClickBlock = new ArrayList<String>();
			List<String> leftClickAir = new ArrayList<String>();
			List<String> leftClickBlock = new ArrayList<String>();
			List<String> rightClickAir = new ArrayList<String>();
			List<String> rightClickBlock = new ArrayList<String>();
			List<String> physical = new ArrayList<String>();
			List<String> inventory = new ArrayList<String>();
			for(ItemCommand command : this.commands) {
				if (command.matchAction(ItemCommand.ActionType.MULTI_CLICK_ALL)) { multiClickAll.add(command.getCommand()); }
				else if (command.matchAction(ItemCommand.ActionType.MULTI_CLICK_AIR)) { multiClickAir.add(command.getCommand()); }
				else if (command.matchAction(ItemCommand.ActionType.MULTI_CLICK_BLOCK)) { multiClickBlock.add(command.getCommand()); }
				else if (command.matchAction(ItemCommand.ActionType.RIGHT_CLICK_ALL)) { rightClickAll.add(command.getCommand()); }
				else if (command.matchAction(ItemCommand.ActionType.RIGHT_CLICK_AIR)) { rightClickAir.add(command.getCommand()); }
				else if (command.matchAction(ItemCommand.ActionType.RIGHT_CLICK_BLOCK)) { rightClickBlock.add(command.getCommand()); }
				else if (command.matchAction(ItemCommand.ActionType.LEFT_CLICK_ALL)) { leftClickAll.add(command.getCommand()); }
				else if (command.matchAction(ItemCommand.ActionType.LEFT_CLICK_AIR)) { leftClickAir.add(command.getCommand()); }
				else if (command.matchAction(ItemCommand.ActionType.LEFT_CLICK_BLOCK)) { leftClickBlock.add(command.getCommand()); }
				else if (command.matchAction(ItemCommand.ActionType.PHYSICAL)) { physical.add(command.getCommand()); }
				else if (command.matchAction(ItemCommand.ActionType.INVENTORY)) { inventory.add(command.getCommand()); }
			}
			if (!multiClickAll.isEmpty()) { itemData.set("items." + this.configName + ".commands.multi-click", multiClickAll); }
			if (!multiClickAir.isEmpty()) { itemData.set("items." + this.configName + ".commands.multi-click-air", multiClickAir); }
			if (!multiClickBlock.isEmpty()) { itemData.set("items." + this.configName + ".commands.multi-click-block", multiClickBlock); }
			if (!rightClickAll.isEmpty()) { itemData.set("items." + this.configName + ".commands.right-click", rightClickAll); }
			if (!rightClickAir.isEmpty()) { itemData.set("items." + this.configName + ".commands.right-click-air", rightClickAir); }
			if (!rightClickBlock.isEmpty()) { itemData.set("items." + this.configName + ".commands.right-click-block", rightClickBlock); }
			if (!leftClickAll.isEmpty()) { itemData.set("items." + this.configName + ".commands.left-click", leftClickAll); }
			if (!leftClickAir.isEmpty()) { itemData.set("items." + this.configName + ".commands.left-click-air", leftClickAir); }
			if (!leftClickBlock.isEmpty()) { itemData.set("items." + this.configName + ".commands.left-click-block", leftClickBlock); }
			if (!physical.isEmpty()) { itemData.set("items." + this.configName + ".commands.physical", physical); }
			if (!inventory.isEmpty()) { itemData.set("items." + this.configName + ".commands.inventory", inventory); }
		}
		if (this.type != null) { itemData.set("items." + this.configName + ".commands-type", this.type.name()); }
		if (this.commandSound != null) { itemData.set("items." + this.configName + ".commands-sound", this.commandSound.name()); }
		if (this.commandParticle != null && !this.commandParticle.isEmpty()) { itemData.set("items." + this.configName + ".commands-particle", this.commandParticle); }
		if (this.sequence != null && this.sequence != CommandSequence.SEQUENTIAL) { itemData.set("items." + this.configName + ".commands-sequence", this.sequence.name()); }
		if (this.cost != null && this.cost != 0) { itemData.set("items." + this.configName + ".commands-cost", this.cost); }
		if (this.warmDelay != null && this.warmDelay != 0) { itemData.set("items." + this.configName + ".commands-warmup", this.warmDelay); }
		if (this.cooldownSeconds != null && this.cooldownSeconds != 0) { itemData.set("items." + this.configName + ".commands-cooldown", this.cooldownSeconds); }
		if (this.cooldownMessage != null && !this.cooldownMessage.isEmpty()) { itemData.set("items." + this.configName + ".cooldown-message", this.cooldownMessage); }
		if (this.enchants != null && !this.enchants.isEmpty()) { 
			String enchantList = "";
			for (Entry<String, Integer> enchantments : this.enchants.entrySet()) { enchantList += enchantments.getKey() + ":" + enchantments.getValue() + ", "; }
			itemData.set("items." + this.configName + ".enchantments", enchantList.substring(0, enchantList.length() - 2)); 
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
		if (this.itemflags != null && !this.itemflags.isEmpty()) { itemData.set("items." + this.configName + ".itemflags", this.itemflags); }
		if (this.triggers != null && !this.triggers.isEmpty()) { itemData.set("items." + this.configName + ".triggers", this.triggers); }
		if (this.limitModes != null && !this.limitModes.isEmpty()) { itemData.set("items." + this.configName + ".limit-modes", this.limitModes); }
		if (this.permissionNode != null && !this.permissionNode.isEmpty()) { itemData.set("items." + this.configName + ".permission-node", this.permissionNode); }
		if (this.leatherColor != null && !this.leatherColor.isEmpty()) { itemData.set("items." + this.configName + ".leather-color", this.leatherColor); }
		else if (this.leatherHex != null && !this.leatherHex.isEmpty()) { itemData.set("items." + this.configName + ".leather-color", this.leatherHex); }
		if (this.customMapImage != null && !this.customMapImage.isEmpty()) { itemData.set("items." + this.configName + ".custom-map-image", this.customMapImage); }
		if (this.skullTexture != null && !this.skullTexture.isEmpty()&& (this.skullTexture == null || this.skullTexture.isEmpty())) { itemData.set("items." + this.configName + ".skull-texture", this.skullTexture); }
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
		if (this.effect != null && !this.effect.isEmpty()) { 
			String effectList = "";
			for (PotionEffect effects : this.effect) { effectList += effects.getType().getName() + ":" + effects.getAmplifier() + ":" + effects.getDuration() + ", "; }
			itemData.set("items." + this.configName + ".potion-effect", effectList.substring(0, effectList.length() - 2)); 
		}
		if (this.enabledRegions != null && !this.enabledRegions.isEmpty()) { 
			String regionList = "";
			for (String region : this.enabledRegions) { regionList += region + ", "; }
			itemData.set("items." + this.configName + ".enabled-regions", regionList.substring(0, regionList.length() - 2)); 
		}
		if (this.enabledWorlds != null && !this.enabledWorlds.isEmpty()) { 
			String worldList = "";
			for (String world : this.enabledWorlds) { worldList += world + ", "; }
			itemData.set("items." + this.configName + ".enabled-worlds", worldList.substring(0, worldList.length() - 2)); 
		}
		try { itemData.save(itemFile); ConfigHandler.getConfigData("items.yml"); ConfigHandler.getConfig("items.yml").options().copyDefaults(false); } 
		catch (Exception e) { ItemJoin.getInstance().getServer().getLogger().severe("Could not save the new custom item " + this.configName + " to the items.yml data file!"); ServerHandler.sendDebugTrace(e); }	
	}
	
	public enum CommandType { BOTH, INTERACT, INVENTORY; }
	public enum CommandSequence { RANDOM, RANDOM_SINGLE, SEQUENTIAL, ALL; }
	
	public ItemMap clone() {
        try {
            Object clone = this.getClass().newInstance();
            for (Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(clone, field.get(this));
            }
            return (ItemMap)clone;
        } catch(Exception e) { ServerHandler.sendDebugTrace(e); return this; }
    }
}