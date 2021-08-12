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
package me.RockinChaos.itemjoin.handlers;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import me.RockinChaos.itemjoin.ChatExecutor;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.ChatTab;
import me.RockinChaos.itemjoin.item.ItemDesigner;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.listeners.Breaking;
import me.RockinChaos.itemjoin.listeners.Consumes;
import me.RockinChaos.itemjoin.listeners.Drops;
import me.RockinChaos.itemjoin.listeners.Interact;
import me.RockinChaos.itemjoin.listeners.Commands;
import me.RockinChaos.itemjoin.listeners.Clicking;
import me.RockinChaos.itemjoin.listeners.Interfaces;
import me.RockinChaos.itemjoin.listeners.Entities;
import me.RockinChaos.itemjoin.listeners.Pickups;
import me.RockinChaos.itemjoin.listeners.Placement;
import me.RockinChaos.itemjoin.listeners.Projectile;
import me.RockinChaos.itemjoin.listeners.Recipes;
import me.RockinChaos.itemjoin.listeners.Stackable;
import me.RockinChaos.itemjoin.listeners.Storable;
import me.RockinChaos.itemjoin.listeners.Crafting;
import me.RockinChaos.itemjoin.listeners.plugins.ChestSortAPI;
import me.RockinChaos.itemjoin.listeners.Offhand;
import me.RockinChaos.itemjoin.listeners.triggers.LimitSwitch;
import me.RockinChaos.itemjoin.listeners.triggers.PlayerGuard;
import me.RockinChaos.itemjoin.listeners.triggers.PlayerJoin;
import me.RockinChaos.itemjoin.listeners.triggers.PlayerLogin;
import me.RockinChaos.itemjoin.listeners.triggers.PlayerQuit;
import me.RockinChaos.itemjoin.listeners.triggers.Respawn;
import me.RockinChaos.itemjoin.listeners.triggers.WorldSwitch;
import me.RockinChaos.itemjoin.utils.ReflectionUtils;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.api.BungeeAPI;
import me.RockinChaos.itemjoin.utils.api.DependAPI;
import me.RockinChaos.itemjoin.utils.api.LanguageAPI;
import me.RockinChaos.itemjoin.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.utils.api.MetricsAPI;
import me.RockinChaos.itemjoin.utils.api.ProtocolAPI;
import me.RockinChaos.itemjoin.utils.enchants.Glow;
import me.RockinChaos.itemjoin.utils.protocol.ProtocolManager;
import me.RockinChaos.itemjoin.utils.sql.SQL;

public class ConfigHandler {
	
	private HashMap < String, Boolean > noSource = new HashMap < String, Boolean > ();
	
	private boolean Generating = false;
	private int permissionLength = 2;
	private int listLength = 1;
	
	private YamlConfiguration itemsFile = new YamlConfiguration();
	private YamlConfiguration configFile = new YamlConfiguration();
	private YamlConfiguration langFile = new YamlConfiguration();
	
	private static ConfigHandler config;
	
   /**
    * Registers the command executors and events.
    * 
    */
	public void registerEvents() {
	    ItemJoin.getInstance().getCommand("itemjoin").setExecutor(new ChatExecutor());
	    ItemJoin.getInstance().getCommand("itemjoin").setTabCompleter(new ChatTab());
		ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Interfaces(), ItemJoin.getInstance());
		this.registerGlow();
	}
	
   /**
    * Copies files into memory.
    * 
    */
	public void copyFiles() {
		this.copyFile("config.yml", "config-Version", 8);
		this.copyFile("items.yml", "items-Version", 8);
		this.copyFile(LanguageAPI.getLang(true).getFile(), LanguageAPI.getLang(false).getFile().split("-")[0] + "-Version", 8);
	}
	
   /**
    * Registers new instances of the plugin classes.
    * 
    * @param silent - If any messages should be sent.
    */
	private void registerClasses(final boolean silent) {
		final boolean reload = ItemJoin.getInstance().isStarted();
		ItemJoin.getInstance().setStarted(false);
		ServerUtils.clearErrorStatements();
		LogHandler.getFilter(true);
		BungeeAPI.getBungee(true);
		this.copyFiles();
		SchedulerUtils.runAsync(() -> {
        	DependAPI.getDepends(true);
			int customItems = (this.getConfigurationSection() != null ? this.getConfigurationSection().getKeys(false).size() : 0);
			if (!silent) { 
				DependAPI.getDepends(false).sendUtilityDepends();
				ServerUtils.logInfo(customItems + " Custom item(s) loaded!"); 
			}
			this.registerPrevent();
			SQL.newData(reload); {
				SchedulerUtils.runAsyncLater(2L, () -> {
					ItemDesigner.getDesigner(true); {
						SchedulerUtils.runSingleAsync(() -> {
							ItemJoin.getInstance().setStarted(true);
							this.setPages();
						});
					}
				}); { 
					SchedulerUtils.runAsyncLater(100L, () -> {
						final MetricsAPI metrics = new MetricsAPI(ItemJoin.getInstance(), 4115);
						DependAPI.getDepends(false).addCustomCharts(metrics);
						ServerUtils.sendErrorStatements(null);
					});
				}
			}
		});
	}
	
   /**
    * Registers the GLOBAL prevent actions.
    * 
    */
	private void registerPrevent() {
		if (((this.clearEnabled("Join") || this.triggerEnabled("Join") || this.triggerEnabled("First-Join")) || StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "JOIN")) && !StringUtils.isRegistered(PlayerJoin.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerJoin(), ItemJoin.getInstance());
		}
		if (((this.clearEnabled("World-Switch") || this.triggerEnabled("World-Switch")) || StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "WORLD-SWITCH")) && !StringUtils.isRegistered(WorldSwitch.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new WorldSwitch(), ItemJoin.getInstance());
		}
		if (((this.clearEnabled("Quit") || this.triggerEnabled("Quit")) || StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "QUIT")) && !StringUtils.isRegistered(PlayerQuit.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerQuit(), ItemJoin.getInstance());
		}
		if ((StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "RESPAWN") || this.triggerEnabled("Respawn")) && !StringUtils.isRegistered(Respawn.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Respawn(), ItemJoin.getInstance());
		}
		if (StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "GAMEMODE-SWITCH") && !StringUtils.isRegistered(LimitSwitch.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new LimitSwitch(), ItemJoin.getInstance());
		}
		if ((this.clearEnabled("Region-Enter") || StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "REGION-ENTER")) && !StringUtils.isRegistered(PlayerGuard.class.getSimpleName()) && DependAPI.getDepends(false).getGuard().guardEnabled()) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerGuard(), ItemJoin.getInstance());
		}
		if (StringUtils.containsIgnoreCase(this.getHotbarTriggers(), "REGION-LEAVE") && !StringUtils.isRegistered(PlayerGuard.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerGuard(), ItemJoin.getInstance());
		}
		if (!StringUtils.isRegistered(PlayerLogin.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerLogin(), ItemJoin.getInstance()); }
		if ((!StringUtils.splitIgnoreCase(this.getPrevent("Pickups"), "FALSE", ",") && !StringUtils.splitIgnoreCase(this.getPrevent("Pickups"), "DISABLED", ","))) {
			if (ServerUtils.hasSpecificUpdate("1_12") && ReflectionUtils.getBukkitClass("event.entity.EntityPickupItemEvent") != null && !StringUtils.isRegistered(Pickups.class.getSimpleName())) { 
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Pickups(), ItemJoin.getInstance()); 
			} else { LegacyAPI.registerPickups(); }
		}
		if ((!StringUtils.splitIgnoreCase(this.getPrevent("itemMovement"), "FALSE", ",") && !StringUtils.splitIgnoreCase(this.getPrevent("itemMovement"), "DISABLED", ","))) {
			if (!StringUtils.isRegistered(Clicking.class.getSimpleName())) { 
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Clicking(), ItemJoin.getInstance()); 
				if (ServerUtils.hasSpecificUpdate("1_8") && !DependAPI.getDepends(false).protocolEnabled() && !ProtocolManager.isHandling()) { ProtocolManager.handleProtocols(); }
				else if (ServerUtils.hasSpecificUpdate("1_8") && DependAPI.getDepends(false).protocolEnabled() && !ProtocolAPI.isHandling()) { ProtocolAPI.handleProtocols(); }
			}
		}
		if ((!StringUtils.splitIgnoreCase(this.getPrevent("Self-Drops"), "FALSE", ",") && !StringUtils.splitIgnoreCase(this.getPrevent("Self-Drops"), "DISABLED", ","))
		|| (!StringUtils.splitIgnoreCase(this.getPrevent("Death-Drops"), "FALSE", ",") && !StringUtils.splitIgnoreCase(this.getPrevent("Death-Drops"), "DISABLED", ","))) {
			if (!StringUtils.isRegistered(Drops.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Drops(), ItemJoin.getInstance()); }
		}
	}
	
   /**
    * Registers the glow enchantment.
    * 
    */
    public void registerGlow() {
    	if (ServerUtils.hasSpecificUpdate("1_13")) {
	    	try {
	    		Field f = Enchantment.class.getDeclaredField("acceptingNew");
	    		f.setAccessible(true);
	    		f.set(null, true);
	    		Glow glow = new Glow();
	    		Enchantment.registerEnchantment(glow);
	    	} catch (IllegalArgumentException e) { } catch (Exception e) { ServerUtils.sendDebugTrace(e); }
    	}
    }

   /**
    * Gets the file from the specified path.
    * 
    * @param path - The File to be fetched.
    * @return The file.
    */
	public FileConfiguration getFile(final String path) {
		final File file = new File(ItemJoin.getInstance().getDataFolder(), path);
		if (this.configFile == null) { this.getSource(path); }
		try {
			return this.getLoadedConfig(file, false);
		} catch (Exception e) {
			ServerUtils.sendSevereTrace(e);
			ServerUtils.logSevere("Cannot load " + file.getName() + " from disk!");
		}
		return null;
	}
	
   /**
    * Gets the source file from the specified path.
    * 
    * @param path - The File to be loaded.
    * @return The source file.
    */
	public FileConfiguration getSource(final String path) {
		final File file = new File(ItemJoin.getInstance().getDataFolder(), path);
		if (!(file).exists()) {
			try {
				InputStream source;
				final File dataDir = ItemJoin.getInstance().getDataFolder();
				if (!dataDir.exists()) { dataDir.mkdir(); }
				if (!path.contains("lang.yml")) { source = ItemJoin.getInstance().getResource("files/configs/" + path); } 
				else { source = ItemJoin.getInstance().getResource("files/locales/" + path); }
        		if (!file.exists()) { Files.copy(source, file.toPath(), new CopyOption[0]); }
				if (path.contains("items.yml")) { this.Generating = true; }
			} catch (Exception e) {
				ServerUtils.sendSevereTrace(e);
				ServerUtils.logWarn("Cannot save " + path + " to disk!");
				this.noSource.put(path, true);
				return null;
			}
		}
		try {
			YamlConfiguration config = this.getLoadedConfig(file, true);
			this.noSource.put(path, false);
			return config;
		} catch (Exception e) {
			ServerUtils.sendSevereTrace(e);
			ServerUtils.logSevere("Cannot load " + file.getName() + " from disk!");
			this.noSource.put(file.getName(), true);
		}
		return null;
	}

   /**
    * Gets the file and loads it into memory if specified.
    * 
    * @param file - The file to be loaded.
    * @param commit - If the File should be committed to memory.
    * @return The Memory loaded config file.
    */
	public YamlConfiguration getLoadedConfig(final File file, final boolean commit) throws Exception {
		if (file.getName().contains("items.yml")) {
			if (commit) { this.itemsFile.load(file); }
			return this.itemsFile;
		} else if (file.getName().contains("config.yml")) {
			if (commit) { this.configFile.load(file); }
			return this.configFile;
		} else if (file.getName().contains("lang.yml")) {
			if (commit) { this.langFile.load(file); }
			return this.langFile;
		}
		return null;
	}

   /**
    * Copies the specified config file to the data folder.
    * 
    * @param configFile - The name and extension of the config file to be copied.
    * @param version - The version String to be checked in the config file.
    * @param id - The expected version id to be found in the config file.
    */
	private void copyFile(final String configFile, final String version, final int id) {
		this.getSource(configFile);
		File File = new File(ItemJoin.getInstance().getDataFolder(), configFile);
		if (File.exists() && !this.noSource.get(configFile) && this.getFile(configFile).getInt(version) != id) {
			InputStream source;
			if (!configFile.contains("lang.yml")) { source = ItemJoin.getInstance().getResource("files/configs/" + configFile); } 
			else { source = ItemJoin.getInstance().getResource("files/locales/" + configFile); }
			if (source != null) {
				String[] namePart = configFile.split("\\.");
				String renameFile = namePart[0] + "-old-" + StringUtils.getRandom(1, 50000) + namePart[1];
				File renamedFile = new File(ItemJoin.getInstance().getDataFolder(), renameFile);
				if (!renamedFile.exists()) {
					File.renameTo(renamedFile);
					File copyFile = new File(ItemJoin.getInstance().getDataFolder(), configFile);
					copyFile.delete();
					this.getSource(configFile);
					ServerUtils.logWarn("Your " + configFile + " is out of date and new options are available, generating a new one!");
				}
			}
		} else if (this.noSource.get(configFile)) {
			ServerUtils.logSevere("Your " + configFile + " is not using proper YAML Syntax and will not be loaded!");
			ServerUtils.logSevere("Check your YAML formatting by using a YAML-PARSER such as http://yaml-online-parser.appspot.com/");
		}
		if (!this.noSource.get(configFile)) { 
			if (this.Generating && configFile.equalsIgnoreCase("items.yml")) { 
				this.generateItemsFile();
				this.getSource("items.yml");
				this.Generating = false;
			}
			this.getFile(configFile).options().copyDefaults(false);
			if (configFile.contains("lang.yml")) { LanguageAPI.getLang(false).setPrefix(); }
		}
	}
	
   /**
    * Saves the changed configuration data to the File.
    * 
    * @param dataFile - The FileConfiguration being modified.
    * @param file - The file name being accessed.
    */
	public void saveFile(final FileConfiguration dataFile, final File fileFolder, final String file) {
		try {
			dataFile.save(fileFolder); 
			this.getSource(file); 
			this.getFile(file).options().copyDefaults(false); 
		} catch (Exception e) { 
			ItemJoin.getInstance().getServer().getLogger().severe("Could not save data to the " + file + " data file!"); 
			ServerUtils.sendDebugTrace(e); 
		}	
	}
	
   /**
    * Softly reloads the configuration files.
    * Usefully when editing booleans.
    * 
    */
	public void softReload() {
		this.copyFiles();
		this.registerPrevent();
	}
	
   /**
    * Properly reloads the configuration files.
    * 
    * @param silent - If any messages should be sent.
    */
	public void reloadConfigs(final boolean silent) {
		ItemUtilities.getUtilities().closeAnimations();
		ItemUtilities.getUtilities().clearItems();
		config = new ConfigHandler(); 
        config.registerClasses(silent);
	}
	
   /**
    * Sets the number of list and permission pages.
    * 
    */
	public void setPages() {
		SchedulerUtils.runAsync(() -> {
			int customItems = (this.getConfigurationSection() != null ? this.getConfigurationSection().getKeys(false).size() : 0);
			if (customItems > 15) {
				this.permissionLength = (int) Math.ceil((double)customItems / 15) + 1;
			}
			
			int listCount = 0;
			for (World world: Bukkit.getWorlds()) {
				List < String > listItems = new ArrayList < String > ();
				listCount++;
				for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
					if (!listItems.contains(itemMap.getConfigName()) && itemMap.inWorld(world)) {
						listCount++;
						listItems.add(itemMap.getConfigName());
					}
				}
			}
			
			if (listCount > 15) {
				this.listLength = (int) Math.ceil((double)listCount / 15);
			}
		});
	}
	
   /**
    * Gets the number of permission pages.
    * 
    * @return The number of permission pages.
    */
	public int getPermissionPages() {
		return this.permissionLength;
	}
	
   /**
    * Gets the number of list pages.
    * 
    * @return The number of list pages.
    */
	public int getListPages() {
		return this.listLength;
	}
	
   /**
    * Gets the Hotbar slot that is defined to be set.
    * 
    * @return The Integer hotbar value.
    */
	public int getHotbarSlot() { 
		if (this.getFile("config.yml").getString("Settings.HeldItem-Slot") != null 
				&& !this.getFile("config.yml").getString("Settings.HeldItem-Slot").equalsIgnoreCase("DISABLED") 
				&& StringUtils.isInt(this.getFile("config.yml").getString("Settings.HeldItem-Slot"))) {
			return this.getFile("config.yml").getInt("Settings.HeldItem-Slot");
		}
		return -1;
	}
	
   /**
    * Gets the Hotbar Triggers that is defined to be set.
    * 
    * @return The String list of hotbar triggers.
    */
	public String getHotbarTriggers() { 
		if (this.getFile("config.yml").getString("Settings.HeldItem-Triggers") != null 
				&& !this.getFile("config.yml").getString("Settings.HeldItem-Triggers").equalsIgnoreCase("DISABLED") 
				&& !this.getFile("config.yml").getString("Settings.HeldItem-Triggers").equalsIgnoreCase("FALSE")) {
			return this.getFile("config.yml").getString("Settings.HeldItem-Triggers");
		}
		return "";
	}
	
   /**
    * Gets the database table prefix.
    * 
    * @return The database table prefix.
    */
    public String getTable() {
    	return (this.getFile("config.yml").getString("Database.prefix") != null ? this.getFile("config.yml").getString("Database.prefix") : "ij_");
    }
	
   /**
    * Checks if the remote MySQL database is enabled.
    * 
    * @return If the remote MySQL database is enabled.
    */
    public boolean sqlEnabled() {
    	return this.getFile("config.yml").getString("Database.MySQL") != null && this.getFile("config.yml").getBoolean("Database.MySQL");
    }
	
   /**
    * Checks if the specified clear items type is enabled.
    * 
    * @param type - The item clearing trigger.
    * @return If the clear type is enabled.
    */
	public boolean clearEnabled(final String type) {
		if (this.getFile("config.yml").getString("Clear-Items." + type) != null 
		&& !this.getFile("config.yml").getString("Clear-Items." + type).equalsIgnoreCase("DISABLED") && !this.getFile("config.yml").getString("Clear-Items." + type).equalsIgnoreCase("FALSE")) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the specified trigger commands is enabled.
    * 
    * @param type - The commands trigger.
    * @return If the trigger type is enabled.
    */
	public boolean triggerEnabled(final String type) {
		if (this.getFile("config.yml").getString("Active-Commands.triggers") != null && StringUtils.containsIgnoreCase(this.getFile("config.yml").getString("Active-Commands.triggers"), type)
			&& (!this.getFile("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("DISABLED") && !this.getFile("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("FALSE"))) {
			return true;
		}
		return false;
	}

   /**
    * Checks if Debugging is enabled.
    * 
    * @return If Debugging is enabled.
    */
	public boolean debugEnabled() {
		return this.getFile("config.yml").getBoolean("General.Debugging");
	}
	
   /**
    * Gets the defined Prevent.
    * 
    * @param name - The name of the Prevent.
    * @return The defined Prevent as a String to compare later.
    */
	public String getPrevent(final String name) {
		return this.getFile("config.yml").getString("Prevent." + name);
	}
	
   /**
    * Checks if OP Bypass is enabled for Prevent actions.
    * 
    * @return If OP is defined for the Prevent actions.
    */
	public boolean isPreventOP() {
		return StringUtils.containsIgnoreCase(this.getFile("config.yml").getString("Prevent.Bypass"), "OP");
	}
	
   /**
    * Checks if CREATIVE Bypass is enabled for Prevent actions.
    * 
    * @return If CREATIVE is defined for the Prevent actions.
    */
	public boolean isPreventCreative() {
		return StringUtils.containsIgnoreCase(this.getFile("config.yml").getString("Prevent.Bypass"), "CREATIVE");
	}
	
   /**
    * Checks if items are defined correctly in the items.yml.
    * 
    * @return If items exist in the items.yml.
    */
	public boolean itemsExist() {
		if (this.getConfigurationSection() != null) {
			return true;
		} else if (this.getConfigurationSection() == null) {
			ServerUtils.logWarn("{Config} There are no items detected in the items.yml.");
			ServerUtils.logWarn("{Config} Try adding an item to the items section in the items.yml.");
			return false;
		}
		return false;
	}
	
   /**
    * Gets the material defined for the custom item.
    * 
    * @param nodeLocation - The path to the custom items node.
    * @return The material set for the custom item.
    */
	public ConfigurationSection getMaterialSection(final ConfigurationSection nodeLocation) {
		return (this.getFile("items.yml") != null ? this.getFile("items.yml").getConfigurationSection(nodeLocation.getCurrentPath() + ".id") : null);
	}
	
   /**
    * Gets the list of commands defined for the custom item.
    * 
    * @param nodeLocation - The path to the custom items node.
    * @return The list of commands defined for the custom item.
    */
	public ConfigurationSection getCommandsSection(final ConfigurationSection nodeLocation) {
		return (this.getFile("items.yml") != null ? this.getFile("items.yml").getConfigurationSection(nodeLocation.getCurrentPath()) : null);
	}
	
   /**
    * Gets the Options defined for the custom item.
    * 
    * @param nodeName - The node name of the custom item in the items.yml.
    * @return The list of options defined for the custom item.
    */
	public ConfigurationSection getItemSection(final String nodeName) {
		return (this.getConfigurationSection() != null ? this.getConfigurationSection().getConfigurationSection(nodeName) : null);
	}
	
   /**
    * Gets the ConfigurationSection for the list of custom items.
    * 
    * @return The list of custom items.
    */
	public ConfigurationSection getConfigurationSection() {
		return (this.getFile("items.yml") != null ? this.getFile("items.yml").getConfigurationSection("items") : null);
	}
	
   /**
    * Calculates the exact numer of arbitrary slots,
    * assigning numbers to each slot to uniquely identify each item.
    * 
    * @param slot - The numerical or custom slot value.
    * @return The exact slot ID to be set to the item.
    */
	private int ArbitraryID = 0;
	public String getItemID(String slot) {
		if (slot.equalsIgnoreCase("Arbitrary")) {
			this.ArbitraryID += 1;
			slot += this.ArbitraryID;
		}
		return slot;
	}
	
   /**
    * Registers Events that are utilized by the specified ItemMap.
    * 
    * @param itemMap - The ItemMap that needs its events registered.
    */
	public void registerListeners(final ItemMap itemMap) {
		if (((!itemMap.isGiveOnDisabled() && itemMap.isGiveOnJoin()) || itemMap.isAutoRemove() || (this.getFile("config.yml").getString("Active-Commands.enabled-worlds") != null 
			&& (!this.getFile("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("DISABLED") || !this.getFile("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("FALSE")))) 
			&& !StringUtils.isRegistered(PlayerJoin.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerJoin(), ItemJoin.getInstance());
		}
		if ((((!itemMap.isGiveOnDisabled() && itemMap.isGiveOnRespawn()) || itemMap.isDeathKeepable()) || itemMap.isAutoRemove()) && !StringUtils.isRegistered(Respawn.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Respawn(), ItemJoin.getInstance());
		}
		if (((!itemMap.isGiveOnDisabled() && itemMap.isGiveOnWorldSwitch()) || itemMap.isAutoRemove()) && !StringUtils.isRegistered(WorldSwitch.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new WorldSwitch(), ItemJoin.getInstance());
		}
		if (!itemMap.isGiveOnDisabled() && (itemMap.isGiveOnRegionEnter() || itemMap.isGiveOnRegionLeave() || itemMap.isGiveOnRegionAccess() || itemMap.isGiveOnRegionEgress()) 
			&& !StringUtils.isRegistered(PlayerGuard.class.getSimpleName()) && DependAPI.getDepends(false).getGuard().guardEnabled()) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerGuard(), ItemJoin.getInstance());
		}
		if (!itemMap.isGiveOnDisabled() && itemMap.isUseOnLimitSwitch() && !StringUtils.isRegistered(LimitSwitch.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new LimitSwitch(), ItemJoin.getInstance());
		}
		if ((itemMap.isAnimated() || itemMap.isDynamic()) && !StringUtils.isRegistered(PlayerQuit.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerQuit(), ItemJoin.getInstance());
		}
		if (itemMap.mobsDrop() && !StringUtils.isRegistered(Entities.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Entities(), ItemJoin.getInstance());
		}
		if (itemMap.blocksDrop() && !StringUtils.isRegistered(Breaking.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Breaking(), ItemJoin.getInstance());
		}
		if (itemMap.isCraftingItem() && !StringUtils.isRegistered(Crafting.class.getSimpleName())) {
			PlayerHandler.cycleCrafting();
			SchedulerUtils.runLater(40L, () -> {
				PlayerHandler.restoreCraftItems();
				if (ServerUtils.hasSpecificUpdate("1_8") && !DependAPI.getDepends(false).protocolEnabled() && !ProtocolManager.isHandling()) { ProtocolManager.handleProtocols(); }
				else if (ServerUtils.hasSpecificUpdate("1_8") && DependAPI.getDepends(false).protocolEnabled() && !ProtocolAPI.isHandling()) { ProtocolAPI.handleProtocols(); }
			});
			if (!StringUtils.isRegistered(PlayerQuit.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerQuit(), ItemJoin.getInstance());
			}
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Crafting(), ItemJoin.getInstance());
		}
		if ((itemMap.isMovement() || itemMap.isEquip() || itemMap.isInventoryClose())) {
			if (!StringUtils.isRegistered(Clicking.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Clicking(), ItemJoin.getInstance());
				if (ServerUtils.hasSpecificUpdate("1_8") && !DependAPI.getDepends(false).protocolEnabled() && !ProtocolManager.isHandling()) { ProtocolManager.handleProtocols(); }
				else if (ServerUtils.hasSpecificUpdate("1_8") && DependAPI.getDepends(false).protocolEnabled() && !ProtocolAPI.isHandling()) { ProtocolAPI.handleProtocols(); }
			}
			if (DependAPI.getDepends(false).chestSortEnabled() && !StringUtils.isRegistered(ChestSortAPI.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new ChestSortAPI(), ItemJoin.getInstance());
			}
		}
		if (ServerUtils.hasSpecificUpdate("1_12") && itemMap.isStackable() && !StringUtils.isRegistered(Stackable.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Stackable(), ItemJoin.getInstance());
		} else if (itemMap.isStackable()) {
			LegacyAPI.registerStackable();
		}
		if ((itemMap.isDeathKeepable() || itemMap.isDeathDroppable() || itemMap.isSelfDroppable()) && !StringUtils.isRegistered(Drops.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Drops(), ItemJoin.getInstance());
		}
		if (itemMap.getCommands() != null && itemMap.getCommands().length != 0) {
			if (ServerUtils.hasSpecificUpdate("1_8") && !StringUtils.isRegistered(Commands.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Commands(), ItemJoin.getInstance());
			} else {
				LegacyAPI.registerCommands();
			}
		}
		if ((itemMap.isCancelEvents() || itemMap.isSelectable() || itemMap.getInteractCooldown() != 0)) {
			if (!StringUtils.isRegistered(Interact.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Interact(), ItemJoin.getInstance());
			}
		}
		if ((itemMap.isPlaceable() || itemMap.isCountLock()) && !StringUtils.isRegistered(Placement.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Placement(), ItemJoin.getInstance());
		}
		if ((itemMap.isCountLock() || itemMap.isTeleport()) && !StringUtils.isRegistered(Projectile.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Projectile(), ItemJoin.getInstance());
		}
		if (itemMap.isCountLock() || itemMap.isCustomConsumable()) {
			if (ServerUtils.hasSpecificUpdate("1_11") && !StringUtils.isRegistered(Consumes.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Consumes(), ItemJoin.getInstance());
			} else {
				LegacyAPI.registerConsumes();
			}
		}
		if ((itemMap.isItemRepairable() || itemMap.isItemCraftable()) && !StringUtils.isRegistered(Recipes.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Recipes(), ItemJoin.getInstance());
		}
		if (itemMap.isItemStore() || itemMap.isItemModify()) {
			if (ServerUtils.hasSpecificUpdate("1_8") && !StringUtils.isRegistered(Storable.class.getSimpleName())) {
				ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Storable(), ItemJoin.getInstance());
			} else {
				LegacyAPI.registerStorable();
			}
		}
		if (itemMap.isMovement() && ServerUtils.hasSpecificUpdate("1_9") && ReflectionUtils.getBukkitClass("event.player.PlayerSwapHandItemsEvent") != null && !StringUtils.isRegistered(Offhand.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Offhand(), ItemJoin.getInstance());
		}
	}
	
   /**
    * Generates the Data for the FileConfiguration that is specific
    * to the current Server version.
    * 
    */
	public void generateItemsFile() {
		File itemsFile = new File(ItemJoin.getInstance().getDataFolder(), "items.yml");
        FileConfiguration itemsData = YamlConfiguration.loadConfiguration(itemsFile);
        
        if (ServerUtils.hasSpecificUpdate("1_14")) {
			itemsData.set("items.devine-item.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
			itemsData.set("items.map-item.id", "FILLED_MAP");
			itemsData.set("items.gamemode-token.id", "FIREWORK_STAR");
			itemsData.set("items.gamemode-token.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
			itemsData.set("items.bungeecord-item.id", "PURPLE_STAINED_GLASS");
			itemsData.set("items.bungeecord-item.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
			itemsData.set("items.animated-panes.id.1", "<delay:40>BLACK_STAINED_GLASS_PANE");
			itemsData.set("items.animated-panes.id.2", "<delay:20>BLUE_STAINED_GLASS_PANE");
			itemsData.set("items.animated-panes.id.3", "<delay:20>GREEN_STAINED_GLASS_PANE");
			itemsData.set("items.animated-panes.id.4", "<delay:20>MAGENTA_STAINED_GLASS_PANE");
			itemsData.set("items.animated-panes.id.5", "<delay:20>ORANGE_STAINED_GLASS_PANE");
			itemsData.set("items.animated-panes.id.6", "<delay:20>RED_STAINED_GLASS_PANE");
			itemsData.set("items.banner-item.id", "WHITE_BANNER");
			itemsData.set("items.animated-sign.id", "OAK_SIGN");
			itemsData.set("items.skull-item.id", "PLAYER_HEAD");
			itemsData.set("items.potion-arrow.id", "TIPPED_ARROW");
			itemsData.set("items.potion-arrow.name", "&fDeath Arrow");
			itemsData.set("items.potion-arrow.potion-effect", "WITHER:1:20");
			itemsData.set("items.firework-item.id", "FIREWORK_ROCKET");
			itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, LIGHT_GRAY, GREEN");
			itemsData.set("items.potion-apple.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, GLOWING:1:410, REGENERATION:1:160");
			itemsData.set("items.profile-item.id", "PLAYER_HEAD");
			itemsData.set("items.random-pane-1.id", "YELLOW_STAINED_GLASS_PANE");
			itemsData.set("items.random-pane-2.id", "BLUE_STAINED_GLASS_PANE");
			itemsData.set("items.random-pane-3.id", "PINK_STAINED_GLASS_PANE");
        } else if (ServerUtils.hasSpecificUpdate("1_13")) {
			itemsData.set("items.devine-item.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
			itemsData.set("items.map-item.id", "FILLED_MAP");
			itemsData.set("items.gamemode-token.id", "FIREWORK_STAR");
			itemsData.set("items.gamemode-token.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
			itemsData.set("items.bungeecord-item.id", "PURPLE_STAINED_GLASS");
			itemsData.set("items.bungeecord-item.commands-sound", "BLOCK_NOTE_BLOCK_PLING");
			itemsData.set("items.animated-panes.id.1", "<delay:40>BLACK_STAINED_GLASS_PANE");
			itemsData.set("items.animated-panes.id.2", "<delay:20>BLUE_STAINED_GLASS_PANE");
			itemsData.set("items.animated-panes.id.3", "<delay:20>GREEN_STAINED_GLASS_PANE");
			itemsData.set("items.animated-panes.id.4", "<delay:20>MAGENTA_STAINED_GLASS_PANE");
			itemsData.set("items.animated-panes.id.5", "<delay:20>ORANGE_STAINED_GLASS_PANE");
			itemsData.set("items.animated-panes.id.6", "<delay:20>RED_STAINED_GLASS_PANE");
			itemsData.set("items.banner-item.id", "WHITE_BANNER");
			itemsData.set("items.animated-sign.id", "SIGN");
			itemsData.set("items.skull-item.id", "PLAYER_HEAD");
			itemsData.set("items.potion-arrow.id", "TIPPED_ARROW");
			itemsData.set("items.potion-arrow.name", "&fDeath Arrow");
			itemsData.set("items.potion-arrow.potion-effect", "WITHER:1:20");
			itemsData.set("items.firework-item.id", "FIREWORK_ROCKET");
			itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, LIGHT_GRAY, GREEN");
			itemsData.set("items.potion-apple.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, GLOWING:1:410, REGENERATION:1:160");
			itemsData.set("items.profile-item.id", "PLAYER_HEAD");
			itemsData.set("items.random-pane-1.id", "YELLOW_STAINED_GLASS_PANE");
			itemsData.set("items.random-pane-2.id", "BLUE_STAINED_GLASS_PANE");
			itemsData.set("items.random-pane-3.id", "PINK_STAINED_GLASS_PANE");
		} else if (ServerUtils.hasSpecificUpdate("1_9")) {
			itemsData.set("items.devine-item.commands-sound", "BLOCK_NOTE_PLING");
			itemsData.set("items.map-item.id", "MAP");
			itemsData.set("items.gamemode-token.id", "FIREWORK_CHARGE");
			itemsData.set("items.gamemode-token.commands-sound", "BLOCK_NOTE_PLING");
			itemsData.set("items.bungeecord-item.id", "STAINED_GLASS:12");
			itemsData.set("items.bungeecord-item.commands-sound", "BLOCK_NOTE_PLING");
			itemsData.set("items.animated-panes.id.1", "<delay:40>STAINED_GLASS_PANE:15");
			itemsData.set("items.animated-panes.id.2", "<delay:20>STAINED_GLASS_PANE:11");
			itemsData.set("items.animated-panes.id.3", "<delay:20>STAINED_GLASS_PANE:13");
			itemsData.set("items.animated-panes.id.4", "<delay:20>STAINED_GLASS_PANE:2");
			itemsData.set("items.animated-panes.id.5", "<delay:20>STAINED_GLASS_PANE:1");
			itemsData.set("items.animated-panes.id.6", "<delay:20>STAINED_GLASS_PANE:14");
			itemsData.set("items.banner-item.id", "BANNER");
			itemsData.set("items.animated-sign.id", "SIGN");
			itemsData.set("items.skull-item.id", "SKULL_ITEM:3");
			itemsData.set("items.potion-arrow.id", "TIPPED_ARROW");
			itemsData.set("items.potion-arrow.name", "&fDeath Arrow");
			itemsData.set("items.potion-arrow.potion-effect", "WITHER:1:20");
			itemsData.set("items.firework-item.id", "FIREWORK");
			itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, SILVER, GREEN");
			itemsData.set("items.potion-apple.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, GLOWING:1:410, REGENERATION:1:160");
			itemsData.set("items.profile-item.id", "SKULL_ITEM:3");
			itemsData.set("items.random-pane-1.id", "STAINED_GLASS_PANE:4");
			itemsData.set("items.random-pane-2.id", "STAINED_GLASS_PANE:4");
			itemsData.set("items.random-pane-3.id", "STAINED_GLASS_PANE:6");
		} else if (ServerUtils.hasSpecificUpdate("1_8")) {
			itemsData.set("items.devine-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.devine-item.attributes", "{GENERIC_ATTACK_DAMAGE:15.2}");
			itemsData.set("items.map-item.id", "MAP");
			itemsData.set("items.gamemode-token.id", "FIREWORK_CHARGE");
			itemsData.set("items.gamemode-token.commands-sound", "NOTE_PLING");
			itemsData.set("items.bungeecord-item.id", "STAINED_GLASS:12");
			itemsData.set("items.bungeecord-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.animated-panes.id.1", "<delay:40>STAINED_GLASS_PANE:15");
			itemsData.set("items.animated-panes.id.2", "<delay:20>STAINED_GLASS_PANE:11");
			itemsData.set("items.animated-panes.id.3", "<delay:20>STAINED_GLASS_PANE:13");
			itemsData.set("items.animated-panes.id.4", "<delay:20>STAINED_GLASS_PANE:2");
			itemsData.set("items.animated-panes.id.5", "<delay:20>STAINED_GLASS_PANE:1");
			itemsData.set("items.animated-panes.id.6", "<delay:20>STAINED_GLASS_PANE:14");
			itemsData.set("items.banner-item.id", "BANNER");
			itemsData.set("items.animated-sign.id", "SIGN");
			itemsData.set("items.skull-item.id", "SKULL_ITEM:3");
			itemsData.set("items.potion-arrow.id", "ARROW");
			itemsData.set("items.potion-arrow.name", "&fArrow");
			itemsData.set("items.potion-arrow.potion-effect", null);
			itemsData.set("items.firework-item.id", "FIREWORK");
			itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, SILVER, GREEN");
			itemsData.set("items.potion-apple.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, INVISIBILITY:1:410, REGENERATION:1:160");
			itemsData.set("items.profile-item.id", "SKULL_ITEM:3");
			itemsData.set("items.random-pane-1.id", "STAINED_GLASS_PANE:4");
			itemsData.set("items.random-pane-2.id", "STAINED_GLASS_PANE:3");
			itemsData.set("items.random-pane-3.id", "STAINED_GLASS_PANE:6");
			itemsData.set("items.offhand-item", null);
		} else if (ServerUtils.hasSpecificUpdate("1_7")) {
			itemsData.set("items.devine-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.devine-item.attributes", "{GENERIC_ATTACK_DAMAGE:15.2}");
			itemsData.set("items.map-item.id", "MAP");
			itemsData.set("items.gamemode-token.id", "FIREWORK_CHARGE");
			itemsData.set("items.gamemode-token.commands-sound", "NOTE_PLING");
			itemsData.set("items.bungeecord-item.id", "STAINED_GLASS:12");
			itemsData.set("items.bungeecord-item.commands-sound", "NOTE_PLING");
			itemsData.set("items.animated-panes.id.1", "<delay:40>STAINED_GLASS_PANE:15");
			itemsData.set("items.animated-panes.id.2", "<delay:20>STAINED_GLASS_PANE:11");
			itemsData.set("items.animated-panes.id.3", "<delay:20>STAINED_GLASS_PANE:13");
			itemsData.set("items.animated-panes.id.4", "<delay:20>STAINED_GLASS_PANE:2");
			itemsData.set("items.animated-panes.id.5", "<delay:20>STAINED_GLASS_PANE:1");
			itemsData.set("items.animated-panes.id.6", "<delay:20>STAINED_GLASS_PANE:14");
			itemsData.set("items.banner-item", null);
			itemsData.set("items.melooooon-item.id", 382);
			itemsData.set("items.melooooon-item.slot", 20);
			itemsData.set("items.melooooon-item.name", "&aWater Melooooon!");
			itemsData.set("items.melooooon-item.interact.-", "'message: &aIts a Water Melooooon!'");
			itemsData.set("items.melooooon-item.commands-sequence", "RANDOM");
			itemsData.set("items.melooooon-item.itemflags", "hide-attributes, self-drops, CreativeBypass");
			itemsData.set("items.melooooon-item.triggers", "join, respawn, world-change, region-enter");
			itemsData.set("items.melooooon-item.enabled-worlds", "world, world_nether, world_the_end");
			itemsData.set("items.animated-sign.id", "SIGN");
			itemsData.set("items.skull-item.id", "SKULL_ITEM:3");
			itemsData.set("items.potion-arrow.id", "ARROW");
			itemsData.set("items.potion-arrow.name", "&fArrow");
			itemsData.set("items.potion-arrow.potion-effect", null);
			itemsData.set("items.firework-item.id", "FIREWORK");
			itemsData.set("items.firework-item.firework.colors", "GRAY, WHITE, PURPLE, SILVER, GREEN");
			itemsData.set("items.potion-apple.potion-effect", "JUMP:2:120, NIGHT_VISION:2:400, INVISIBILITY:1:410, REGENERATION:1:160");
			itemsData.set("items.profile-item.id", "SKULL_ITEM:3");
			itemsData.set("items.random-pane-1.id", "STAINED_GLASS_PANE:4");
			itemsData.set("items.random-pane-2.id", "STAINED_GLASS_PANE:3");
			itemsData.set("items.random-pane-3.id", "STAINED_GLASS_PANE:6");
			itemsData.set("items.offhand-item", null);
		}

		try {
			itemsData.save(itemsFile);
			this.getSource("items.yml");
			this.getFile("items.yml").options().copyDefaults(false);
		} catch (Exception e) {
			ItemJoin.getInstance().getServer().getLogger().severe("Could not save important data changes to the data file items.yml!");
			e.printStackTrace();
		}
	}
	
   /**
    * Gets the instance of the ConfigHandler.
    * 
    * @return The ConfigHandler instance.
    */
    public static ConfigHandler getConfig() { 
        if (config == null) {
        	config = new ConfigHandler(); 
        	config.registerClasses(false);
        }
        return config; 
    } 
}