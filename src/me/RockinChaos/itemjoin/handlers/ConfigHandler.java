package me.RockinChaos.itemjoin.handlers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import me.RockinChaos.itemjoin.Commands;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.listeners.LimitSwitch;
import me.RockinChaos.itemjoin.giveitems.listeners.PlayerGuard;
import me.RockinChaos.itemjoin.giveitems.listeners.PlayerJoin;
import me.RockinChaos.itemjoin.giveitems.listeners.PlayerQuit;
import me.RockinChaos.itemjoin.giveitems.listeners.Respawn;
import me.RockinChaos.itemjoin.giveitems.listeners.WorldSwitch;
import me.RockinChaos.itemjoin.giveitems.utils.ItemDesigner;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.listeners.Consumes;
import me.RockinChaos.itemjoin.listeners.Drops;
import me.RockinChaos.itemjoin.listeners.Interact;
import me.RockinChaos.itemjoin.listeners.InventoryClick;
import me.RockinChaos.itemjoin.listeners.InventoryCrafting;
import me.RockinChaos.itemjoin.listeners.Legacy_Consumes;
import me.RockinChaos.itemjoin.listeners.Legacy_Interact;
import me.RockinChaos.itemjoin.listeners.Legacy_Pickups;
import me.RockinChaos.itemjoin.listeners.Legacy_Storable;
import me.RockinChaos.itemjoin.listeners.Menu;
import me.RockinChaos.itemjoin.listeners.Pickups;
import me.RockinChaos.itemjoin.listeners.Placement;
import me.RockinChaos.itemjoin.listeners.Recipes;
import me.RockinChaos.itemjoin.listeners.Storable;
import me.RockinChaos.itemjoin.listeners.SwitchHands;
import me.RockinChaos.itemjoin.utils.DependAPI;
import me.RockinChaos.itemjoin.utils.UI;
import me.RockinChaos.itemjoin.utils.Language;
import me.RockinChaos.itemjoin.utils.Metrics;
import me.RockinChaos.itemjoin.utils.TabComplete;
import me.RockinChaos.itemjoin.utils.Reflection;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.YAMLGenerator;
import me.RockinChaos.itemjoin.utils.protocol.ProtocolManager;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

public class ConfigHandler {
	
	private static YamlConfiguration itemsYAML;
	private static YamlConfiguration configYAML;
	private static YamlConfiguration langYAML;
	private static boolean yamlGenerating = false;
	
	private static SQLData sqlData;
	private static UpdateHandler updater;
	private static ItemDesigner itemDesigner;
	private static ProtocolManager protocolManager;
	private static UI itemCreator;
	private static Metrics metrics;
	private static DependAPI depends;
	
	public static void generateData(File file) {
		configFile(); itemsFile(); langFile(); registerPrevents();
		setDepends(new DependAPI());
		setSQLData(new SQLData());
		if (file != null) { sendUtilityDepends(); setUpdater(new UpdateHandler(file)); }
		Bukkit.getServer().getScheduler().runTaskAsynchronously(ItemJoin.getInstance(), () -> { setItemDesigner(new ItemDesigner()); setProtolcolManager(new ProtocolManager()); setItemCreator(new UI()); });
		Bukkit.getServer().getScheduler().runTaskLater(ItemJoin.getInstance(), () -> { setMetrics(new Metrics()); }, 100L);
	}
	
	public static void registerEvents() {
	    ItemJoin.getInstance().getCommand("itemjoin").setExecutor(new Commands());
	    ItemJoin.getInstance().getCommand("itemjoin").setTabCompleter(new TabComplete());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Menu(), ItemJoin.getInstance());
	}
	
	public static void registerPrevents() {
		if ((!Utils.containsIgnoreCase(ConfigHandler.isPreventPickups(), "FALSE") && !Utils.containsIgnoreCase(ConfigHandler.isPreventPickups(), "DISABLED"))) {
			if (ServerHandler.hasSpecificUpdate("1_12") && Reflection.getBukkitClass("event.entity.EntityPickupItemEvent") != null && !isListenerEnabled(Pickups.class.getSimpleName())) { 
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Pickups(), ItemJoin.getInstance()); 
			} else if (!isListenerEnabled(Legacy_Pickups.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Legacy_Pickups(), ItemJoin.getInstance()); }
		}
		if ((!Utils.containsIgnoreCase(ConfigHandler.isPreventModify(), "FALSE") && !Utils.containsIgnoreCase(ConfigHandler.isPreventModify(), "DISABLED"))) {
			if (!isListenerEnabled(InventoryClick.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new InventoryClick(), ItemJoin.getInstance()); }
		}
		if ((!Utils.containsIgnoreCase(ConfigHandler.isPreventDrops(), "FALSE") && !Utils.containsIgnoreCase(ConfigHandler.isPreventDrops(), "DISABLED"))
		|| (!Utils.containsIgnoreCase(ConfigHandler.isPreventDeathDrops(), "FALSE") && !Utils.containsIgnoreCase(ConfigHandler.isPreventDeathDrops(), "DISABLED"))) {
			if (!isListenerEnabled(Drops.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Drops(), ItemJoin.getInstance()); }
		}
	}

	public static FileConfiguration getConfig(String path) {
		File file = new File(ItemJoin.getInstance().getDataFolder(), path);
		if (configYAML == null) { getConfigData(path); }
		return getPath(path, file, false);
	}
	
    public static void saveDefaultData(String path) throws IOException {
        InputStream source;
        File dataDir = ItemJoin.getInstance().getDataFolder();
        if (!dataDir.exists()) { dataDir.mkdir(); }
        if (!path.contains("lang.yml")) { source = ItemJoin.getInstance().getResource("files/configs/" + path); } 
        else { source = ItemJoin.getInstance().getResource("files/locales/" + path); }
        File file = new File(ItemJoin.getInstance().getDataFolder(), path);
        if (!file.exists()) { Files.copy(source, file.toPath(), new CopyOption[0]); }
    }
	
	public static FileConfiguration getConfigData(String path) {
		File file = new File(ItemJoin.getInstance().getDataFolder(), path);
		if (!(file).exists()) {
			try {
				saveDefaultData(path);
				if (path.contains("items.yml")) { setGenerating(true); }
			} catch (Exception e) {
				ServerHandler.sendDebugTrace(e);
				ItemJoin.getInstance().getLogger().warning("Cannot save " + path + " to disk!");
				return null;
			}
		}
		return getPath(path, file, true);
	}

	public static YamlConfiguration getPath(String path, File file, boolean saveData) {
		if (path.contains("items.yml")) {
			if (saveData) { itemsYAML = YamlConfiguration.loadConfiguration(file); }
			return itemsYAML;
		} else if (path.contains("config.yml")) {
			if (saveData) { configYAML = YamlConfiguration.loadConfiguration(file); }
			return configYAML;
		} else if (path.contains("lang.yml")) {
			if (saveData) { langYAML = YamlConfiguration.loadConfiguration(file); }
			return langYAML;
		}
		return null;
	}

	public static void configFile() {
		getConfigData("config.yml");
		File File = new File(ItemJoin.getInstance().getDataFolder(), "config.yml");
		if (File.exists() && getConfig("config.yml").getInt("config-Version") != 7) {
			if (ItemJoin.getInstance().getResource("config.yml") != null) {
				String newGen = "config" + Utils.getRandom(1, 50000) + ".yml";
				File newFile = new File(ItemJoin.getInstance().getDataFolder(), newGen);
				if (!newFile.exists()) {
					File.renameTo(newFile);
					File configFile = new File(ItemJoin.getInstance().getDataFolder(), "config.yml");
					configFile.delete();
					getConfigData("config.yml");
					ServerHandler.logWarn("Your config.yml is out of date and new options are available, generating a new one!");
				}
			}
		}
		getConfig("config.yml").options().copyDefaults(false);
	}

	public static void itemsFile() {
		getConfigData("items.yml");
		File itemsFile = new File(ItemJoin.getInstance().getDataFolder(), "items.yml");
		if (itemsFile.exists() && getConfig("items.yml").getInt("items-Version") != 6) {
			if (ItemJoin.getInstance().getResource("items.yml") != null) {
				String newGen = "items" + Utils.getRandom(1, 50000) + ".yml";
				File newFile = new File(ItemJoin.getInstance().getDataFolder(), newGen);
				if (!newFile.exists()) {
					itemsFile.renameTo(newFile);
					File configFile = new File(ItemJoin.getInstance().getDataFolder(), "items.yml");
					configFile.delete();
					getConfigData("items.yml");
					ServerHandler.logWarn("Your items.yml is out of date and new options are available, generating a new one!");
				}
			}
		}
		YAMLSetup();
		getConfig("items.yml").options().copyDefaults(false);
	}
	
	private static void YAMLSetup() {
		if (isGenerating()) { 
			YAMLGenerator.generateItemsFile();
			getConfigData("items.yml");
			setGenerating(false);
		}
	}
	
	public static void langFile() {
		String lang = getConfig("config.yml").getString("Language").replace(" ", "");
		if (lang.equalsIgnoreCase("TraditionalChinese") || lang.equalsIgnoreCase("TwChinese") || getConfig("config.yml").getString("Language").equalsIgnoreCase("Chinese")) { affixLang("tw"); } 
		else if (lang.equalsIgnoreCase("SimplifiedChinese") || lang.equalsIgnoreCase("CnChinese")) { affixLang("cn"); } 
		else if (Utils.containsIgnoreCase(lang, "Chinese")) { affixLang("tw"); } 
		else if (lang.replace(" ", "").equalsIgnoreCase("Spanish")) { affixLang("es"); } 
		else { affixLang("en"); }
	}
	
	public static void affixLang(String affix) {
		getConfigData(affix + "-lang.yml");
		Language.setLanguage(affix);
		File affixLang = new File(ItemJoin.getInstance().getDataFolder(), affix + "-lang.yml");
		if (affixLang.exists() && getConfig(affix + "-lang.yml").getInt(affix + "-Version") != 7) {
			if (ItemJoin.getInstance().getResource(affix + "-lang.yml") != null) {
				String newGen = affix + "-lang" + Utils.getRandom(1, 50000) + ".yml";
				File newFile = new File(ItemJoin.getInstance().getDataFolder(), newGen);
				if (!newFile.exists()) {
					affixLang.renameTo(newFile);
					File configFile = new File(ItemJoin.getInstance().getDataFolder(), affix + "-lang.yml");
					configFile.delete();
					getConfigData(affix + "-lang.yml");
					ServerHandler.logWarn("Your " + affix + "-lang.yml is out of date and new options are available, generating a new one!");
				}
			}
		}
		getConfig(affix + "-lang.yml").options().copyDefaults(false);
	}

	public static String encodeSecretData(String str) {
		try {
			String hiddenData = "";
			for (char c: str.toCharArray()) {
				hiddenData += "§" + c;
			}
			return hiddenData;
		} catch (Exception e) {
			ServerHandler.sendDebugTrace(e);
			return null;
		}
	}

	public static String decodeSecretData(String str) {
		try {
			String[] hiddenData = str.split("(?:\\w{2,}|\\d[0-9A-Fa-f])+");
			String returnData = "";
			if (hiddenData == null) {
				hiddenData = str.split("§");
				for (int i = 0; i < hiddenData.length; i++) {
					returnData += hiddenData[i];
				}
				return returnData;
			} else {
				String[] d = hiddenData[hiddenData.length - 1].split("§");
				for (int i = 1; i < d.length; i++) {
					returnData += d[i];
				}
				return returnData;
			}
		} catch (Exception e) {
			ServerHandler.sendDebugTrace(e);
			return null;
		}
	}
	
	private static void sendUtilityDepends() {
		ServerHandler.logInfo("softDepend(s) { " + (getDepends().authMeEnabled() ? "AuthMe, " : "") + (getDepends().nickEnabled() ? "BetterNick, " : "") 
				+ (getDepends().mCoreEnabled() ? "Multiverse-Core, " : "") + (getDepends().mInventoryEnabled() ? "Multiverse-Inventories, " : "") 
				+ (getDepends().myWorldsEnabled() ? "My Worlds, " : "") + (getDepends().perInventoryEnabled() ? "PerWorldInventory, " : "") 
				+ (getDepends().perPluginsEnabled() ? "PerWorldPlugins, " : "") + (getDepends().tokenEnchantEnabled() ? "TokenEnchant, " : "") 
				+ (getDepends().getGuard().guardEnabled() ? "WorldGuard, " : "") + (getDepends().databaseEnabled() ? "HeadDatabase, " : "") 
				+ (getDepends().xInventoryEnabled() ? "xInventories, " : "") + (getDepends().placeHolderEnabled() ? "PlaceholderAPI, " : "") 
				+ (getDepends().getVault().vaultEnabled() ? "Vault " : "") + "}");
	}
	
	private static boolean isGenerating() {
		return yamlGenerating;
	}
	
	public static Boolean isConfigurable() {
		if (ConfigHandler.getConfigurationSection() != null) {
			return true;
		} else if (ConfigHandler.getConfigurationSection() == null) {
			ServerHandler.logSevere("{Config} There are no items detected in the items.yml.");
			ServerHandler.logWarn("{Config} Try adding an item to the items section in the items.yml.");
			return false;
		}
		return false;
	}
	
	public static boolean isDebugging() {
		return ConfigHandler.getConfig("config.yml").getBoolean("General.Debugging");
	}
	
	public static boolean isLoggable() {
		return ConfigHandler.getConfig("config.yml").getBoolean("General.Log-Commands");
	}

	public static String isPreventPickups() {
		return ConfigHandler.getConfig("config.yml").getString("Prevent.Pickups");
	}
	
	public static String isPreventModify() {
		return ConfigHandler.getConfig("config.yml").getString("Prevent.itemMovement");
	}
	
	public static String isPreventDrops() {
		return ConfigHandler.getConfig("config.yml").getString("Prevent.Self-Drops");
	}
	
	public static String isPreventDeathDrops() {
		return ConfigHandler.getConfig("config.yml").getString("Prevent.Death-Drops");
	}
	
	public static boolean isPreventOBypass() {
		return Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Prevent.Bypass"), "OP");
	}
	
	public static boolean isPreventCBypass() {
		return Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Prevent.Bypass"), "CREATIVE");
	}
	
	public static UpdateHandler getUpdater() {
		return updater;
	}
	
	public static void setUpdater(UpdateHandler update) {
		updater = update;
	}
	
	public static SQLData getSQLData() {
		return sqlData;
	}
	
	public static void setSQLData(SQLData sql) {
		sqlData = sql;
	}
	
	public static ItemDesigner getItemDesigner() {
		return itemDesigner;
	}
	
	private static void setItemDesigner(ItemDesigner designer) {
		itemDesigner = designer;
	}
	
	public static UI getItemCreator() {
		return itemCreator;
	}
	
	private static void setItemCreator(UI creator) {
		itemCreator = creator;
	}
	
	public static ProtocolManager getProtocolManager() {
		return protocolManager;
	}
	
	private static void setProtolcolManager(ProtocolManager protocol) {
		protocolManager = protocol;
	}
	
	public static DependAPI getDepends() {
		return depends;
	}
	
	private static void setDepends(DependAPI depend) {
		depends = depend;
	}
	
	public static Metrics getMetrics() {
		return metrics;
	}
	
	private static void setMetrics(Metrics metric) {
		metrics = metric;
	}
	
	private static void setGenerating(boolean isGenerating) {
		yamlGenerating = isGenerating;
	}
	
	public static int getHotbarSlot() { 
		if (ConfigHandler.getConfig("config.yml").getString("Settings.HeldItem-Slot") != null 
				&& !ConfigHandler.getConfig("config.yml").getString("Settings.HeldItem-Slot").equalsIgnoreCase("DISABLED") 
				&& Utils.isInt(ConfigHandler.getConfig("config.yml").getString("Settings.HeldItem-Slot"))) {
			return ConfigHandler.getConfig("config.yml").getInt("Settings.HeldItem-Slot");
		}
		return -1;
	}
	
	public static String getNBTData(ItemMap itemMap) {
		if (itemMap != null) {
			return "ItemJoin" + itemMap.getItemValue() + itemMap.getConfigName();
		} else { return "ItemJoin"; }
	}
	
	public static boolean dataTagsEnabled() {
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			return ConfigHandler.getConfig("config.yml").getBoolean("Settings.DataTags");
		}
		return false;
	}
	
	public static void setListenerRestrictions(ItemMap itemMap) {
		if (((!itemMap.isGiveOnDisabled() && itemMap.isGiveOnJoin()) || itemMap.isAutoRemove() 
				|| (ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds") != null 
				&& (!ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("DISABLED") 
				|| !ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("FALSE")))) 
				&& !isListenerEnabled(PlayerJoin.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerJoin(), ItemJoin.getInstance());
		}
		if (((!itemMap.isGiveOnDisabled() && itemMap.isGiveOnRespawn()) || itemMap.isAutoRemove()) && !isListenerEnabled(Respawn.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Respawn(), ItemJoin.getInstance());
		}
		if (((!itemMap.isGiveOnDisabled() && itemMap.isGiveOnWorldChange()) || itemMap.isAutoRemove()) && !isListenerEnabled(WorldSwitch.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new WorldSwitch(), ItemJoin.getInstance());
		}
		if (!itemMap.isGiveOnDisabled() && (itemMap.isGiveOnRegionEnter() || itemMap.isTakeOnRegionLeave() 
			|| (ConfigHandler.getConfig("config.yml").getString("Clear-Items.Region-Enter") != null 
			&& !ConfigHandler.getConfig("config.yml").getString("Clear-Items.Region-Enter").equalsIgnoreCase("DISABLED") 
			&& !ConfigHandler.getConfig("config.yml").getString("Clear-Items.Region-Enter").equalsIgnoreCase("FALSE"))) 
			&& !isListenerEnabled(PlayerGuard.class.getSimpleName()) && ConfigHandler.getDepends().getGuard().guardEnabled()) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerGuard(), ItemJoin.getInstance());
		}
		if (!itemMap.isGiveOnDisabled() && itemMap.isUseOnLimitSwitch() && !isListenerEnabled(LimitSwitch.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new LimitSwitch(), ItemJoin.getInstance());
		}
		if ((itemMap.isAnimated() || itemMap.isDynamic()) && !isListenerEnabled(PlayerQuit.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerQuit(), ItemJoin.getInstance());
		}
		if (itemMap.isCraftingItem() && !isListenerEnabled(InventoryCrafting.class.getSimpleName())) {
			InventoryCrafting.cycleTask();
	        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
	            @Override
	            public void run() {
	            	ItemHandler.restoreCraftItems();
	            	if (ServerHandler.hasSpecificUpdate("1_12")) { getProtocolManager().handleProtocols(); }
	            }
	        }, 40L);
			if (!isListenerEnabled(PlayerQuit.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerQuit(), ItemJoin.getInstance()); }
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new InventoryCrafting(), ItemJoin.getInstance());
		}
		if ((itemMap.isMovement() || itemMap.isInventoryClose()) && !isListenerEnabled(InventoryClick.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new InventoryClick(), ItemJoin.getInstance());
		}
		if ((itemMap.isDeathDroppable() || itemMap.isSelfDroppable()) && !isListenerEnabled(Drops.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Drops(), ItemJoin.getInstance());
		}
		if ((itemMap.isCancelEvents() || (itemMap.getCommands() != null && itemMap.getCommands().length != 0))) {
			if (!ServerHandler.hasSpecificUpdate("1_8") && !isListenerEnabled(Legacy_Interact.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Legacy_Interact(), ItemJoin.getInstance());
			} else if (ServerHandler.hasSpecificUpdate("1_8") && !isListenerEnabled(Interact.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Interact(), ItemJoin.getInstance());
			}
		}
		if ((itemMap.isPlaceable() || itemMap.isCountLock()) && !isListenerEnabled(Placement.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Placement(), ItemJoin.getInstance());
		}
		if (((Utils.containsIgnoreCase(itemMap.getMaterial().name(), "TOTEM") && itemMap.isCountLock()) || itemMap.isCustomConsumable())) {
			if (!ServerHandler.hasSpecificUpdate("1_11") && !isListenerEnabled(Legacy_Consumes.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Legacy_Consumes(), ItemJoin.getInstance());
			} else if (ServerHandler.hasSpecificUpdate("1_11") && !isListenerEnabled(Consumes.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Consumes(), ItemJoin.getInstance());
			}
		}
		if ((itemMap.isItemRepairable() || itemMap.isItemCraftable()) && !isListenerEnabled(Recipes.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Recipes(), ItemJoin.getInstance());
		}
		if (itemMap.isItemStore() || itemMap.isItemModify()) {
			if (!ServerHandler.hasSpecificUpdate("1_8") && !isListenerEnabled(Legacy_Storable.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Legacy_Storable(), ItemJoin.getInstance());
			} else if (ServerHandler.hasSpecificUpdate("1_8") && !isListenerEnabled(Storable.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Storable(), ItemJoin.getInstance());
			}
		}
		if (itemMap.isMovement() && ServerHandler.hasSpecificUpdate("1_9") && Reflection.getBukkitClass("event.player.PlayerSwapHandItemsEvent") != null && !isListenerEnabled(SwitchHands.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new SwitchHands(), ItemJoin.getInstance());
		}
	}

	private static boolean isListenerEnabled(String compare) {
		boolean returnValue = false;
        ArrayList<RegisteredListener> rls = HandlerList.getRegisteredListeners(ItemJoin.getInstance());
        for(RegisteredListener rl: rls) {
        	if (rl.getListener().getClass().getSimpleName().equalsIgnoreCase(compare)) {
        		returnValue = true; break;
        	}
        }
		return returnValue;
	}
	
	public static long getClearDelay() {
		if (!Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.Join"), "DISABLED") 
				&& !Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.Join"), "FALSE")
				|| !Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.World-Switch"), "DISABLED")
				&& !Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.World-Switch"), "FALSE")
				|| !Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.Region-Enter"), "DISABLED")
				&& !Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.Region-Enter"), "FALSE")) {
				return ConfigHandler.getConfig("config.yml").getInt("Clear-Items.Delay-Tick");
		}
		return -1;
	}
	
	public static long getItemDelay() {
		if ((getClearDelay() / 20) >= (ConfigHandler.getConfig("items.yml").getInt("items-Delay") / 2) && getClearDelay() != -1) { return getClearDelay() + 1; }
		return ConfigHandler.getConfig("items.yml").getInt("items-Delay") * 10L;
	}
	
	public static boolean getItemPermissions() {
		return ConfigHandler.getConfig("config.yml").getBoolean("Permissions.Commands-Get");
	}
	
	public static boolean getCommandPermissions() {
		return ConfigHandler.getConfig("config.yml").getBoolean("Permissions.Commands-OP");
	}
	
	public static ConfigurationSection getConfigurationSection() {
		return getConfig("items.yml").getConfigurationSection("items");
	}
	
	public static ConfigurationSection getItemSection(String item) {
		return getConfigurationSection().getConfigurationSection(item);
	}
	
	public static ConfigurationSection getNameSection(ConfigurationSection items) {
		return getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".name");
	}
	
	public static ConfigurationSection getLoreSection(ConfigurationSection items) {
		return getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".lore");
	}
	
	public static ConfigurationSection getMaterialSection(ConfigurationSection items) {
		return getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".id");
	}
	
	public static ConfigurationSection getOwnerSection(ConfigurationSection items) {
		return getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".skull-owner");
	}
	
	public static ConfigurationSection getTextureSection(ConfigurationSection items) {
		return getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".skull-texture");
	}
	
	public static ConfigurationSection getPagesSection(ConfigurationSection items) {
		return getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".pages");
	}
	
	public static ConfigurationSection getCommandsSection(ConfigurationSection items) {
		return getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".commands");
	}
	
	public static ConfigurationSection getCommandsListSection(ConfigurationSection items, String action) {
		return getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".commands." + action);
	}
}