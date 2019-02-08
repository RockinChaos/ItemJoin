package me.RockinChaos.itemjoin.handlers;

import java.io.File;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.YAMLGenerator;

public class ConfigHandler {
	private static YamlConfiguration loadItems;
	private static YamlConfiguration loadConfig;
	private static YamlConfiguration loadEnLang;
	private static boolean generate = false;
	private static String NBTData = "ItemJoin";
	private static long delay = 2;
	private static long clearDelay = 0;
	private static boolean getItemPermissions = false;
	private static String preventItemMovement;
	private static String preventPickups;
	private static boolean preventAllowOpBypass = false;
	private static boolean preventAllowCreativeBypass = false;
	private static boolean opCommandPermissions = false;
	
	public static void loadConfigs() {
		configFile();
		itemsFile();
		enLangFile();
	}
	
	public static Boolean isConfigurable() {
		if (ConfigHandler.getConfigurationSection() != null) {
			return true;
		} else if (ConfigHandler.getConfigurationSection() == null) {
			ServerHandler.sendConsoleMessage("&4There are no items detected in the items.yml.");
			ServerHandler.sendConsoleMessage("&4Try adding an item to the items section in the items.yml.");
			ServerHandler.sendConsoleMessage("&eIf you continue to see this message contact the plugin developer!");
			return false;
		}
		return false;
	}
	
	public static FileConfiguration loadConfig(String path) {
		File file = new File(ItemJoin.getInstance().getDataFolder(), path);
		if (!(file).exists()) {
			try {
				ItemJoin.getInstance().saveResource(path, false);
				if (path.contains("items.yml")) { setGenerating(true); }
			} catch (Exception e) {
				ServerHandler.sendDebugTrace(e);
				ItemJoin.getInstance().getLogger().warning("Cannot save " + path + " to disk!");
				return null;
			}
		}
		return getPath(path, 1, file);
	}

	public static FileConfiguration getConfig(String path) {
		File file = new File(ItemJoin.getInstance().getDataFolder(), path);
		if (loadConfig == null) {
			loadConfig(path);
		}
		return getPath(path, 2, file);
	}

	public static YamlConfiguration getPath(String path, int integer, File file) {
		if (path.contains("items.yml")) {
			if (integer == 1) {
				loadItems = YamlConfiguration.loadConfiguration(file);
			}
			return loadItems;
		} else if (path.contains("config.yml")) {
			if (integer == 1) {
				loadConfig = YamlConfiguration.loadConfiguration(file);
			}
			return loadConfig;
		} else if (path.contains("en-lang.yml")) {
			if (integer == 1) {
				loadEnLang = YamlConfiguration.loadConfiguration(file);
			}
			return loadEnLang;
		}
		return null;
	}

	public static void configFile() {
		loadConfig("config.yml");
		File File = new File(ItemJoin.getInstance().getDataFolder(), "config.yml");
		if (File.exists() && getConfig("config.yml").getInt("config-Version") != 7) {
			if (ItemJoin.getInstance().getResource("config.yml") != null) {
				String newGen = "config" + Utils.getRandom(1, 50000) + ".yml";
				File newFile = new File(ItemJoin.getInstance().getDataFolder(), newGen);
				if (!newFile.exists()) {
					File.renameTo(newFile);
					File configFile = new File(ItemJoin.getInstance().getDataFolder(), "config.yml");
					configFile.delete();
					loadConfig("config.yml");
					ServerHandler.sendConsoleMessage("&aYour config.yml is out of date and new options are available, generating a new one!");
				}
			}
		}
		getConfig("config.yml").options().copyDefaults(false);
	}

	public static void itemsFile() {
		loadConfig("items.yml");
		File itemsFile = new File(ItemJoin.getInstance().getDataFolder(), "items.yml");
		if (itemsFile.exists() && getConfig("items.yml").getInt("items-Version") != 6) {
			if (ItemJoin.getInstance().getResource("items.yml") != null) {
				String newGen = "items" + Utils.getRandom(1, 50000) + ".yml";
				File newFile = new File(ItemJoin.getInstance().getDataFolder(), newGen);
				if (!newFile.exists()) {
					itemsFile.renameTo(newFile);
					File configFile = new File(ItemJoin.getInstance().getDataFolder(), "items.yml");
					configFile.delete();
					loadConfig("items.yml");
					ServerHandler.sendConsoleMessage("&4Your items.yml is out of date and new options are available, generating a new one!");
				}
			}
		}
		YAMLSetup();
		getConfig("items.yml").options().copyDefaults(false);
	}
	
	private static void YAMLSetup() {
		if (isGenerating()) { 
			YAMLGenerator.generateItemsFile();
			loadConfig("items.yml");
			setGenerating(false);
		}
	}
	
	private static void setGenerating(boolean isGenerating) {
		generate = isGenerating;
	}
	
	private static boolean isGenerating() {
		return generate;
	}
	
	public static void enLangFile() {
	      loadConfig("en-lang.yml");
	      File enLang = new File(ItemJoin.getInstance().getDataFolder(), "en-lang.yml");
	      if (enLang.exists() && ItemJoin.getInstance().getConfig().getString("Language").equalsIgnoreCase("English") && getConfig("en-lang.yml").getInt("en-Version") != 7) {
	      if (ItemJoin.getInstance().getResource("en-lang.yml") != null) {
	        String newGen = "en-lang" + Utils.getRandom(1, 50000) + ".yml";
	        File newFile = new File(ItemJoin.getInstance().getDataFolder(), newGen);
	           if (!newFile.exists()) {
	    	      enLang.renameTo(newFile);
	              File configFile = new File(ItemJoin.getInstance().getDataFolder(), "en-lang.yml");
	              configFile.delete();
				  loadConfig("en-lang.yml");
				  ServerHandler.sendConsoleMessage("&4Your en-lang.yml is out of date and new options are available, generating a new one!");
	           }
	        }
	      }
		  if (ItemJoin.getInstance().getConfig().getString("Language").equalsIgnoreCase("English")) {
			  getConfig("en-lang.yml").options().copyDefaults(false);
		  }
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
	
	public static String getNBTData(ItemMap itemMap) {
		if (itemMap != null) {
			return NBTData + itemMap.getItemValue() + itemMap.getConfigName();
		} else { return NBTData; }
	}
	
	public static long getClearDelay() {
		return clearDelay;
	}
	
	public static void loadClearDelay() {
		if (!Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.Join"), "DISABLED") 
			&& !Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.Join"), "FALSE")
			|| !Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.World-Switch"), "DISABLED")
			&& !Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.World-Switch"), "FALSE")) {
			clearDelay = ConfigHandler.getConfig("config.yml").getInt("Clear-Items.Delay-Tick") * 10L;
		}
	}
	
	public static long getItemDelay() {
		return delay;
	}
	
	public static void loadDelay() {
		delay = ConfigHandler.getConfig("items.yml").getInt("items-Delay") * 10L;
		if (clearDelay >= delay) { delay = clearDelay + 1; }
	}
	
	public static boolean getAllItemPermissions() {
		return getItemPermissions;
	}
	
	public static boolean getOPCommandPermissions() {
		return opCommandPermissions;
	}
	
	public static void loadOPCommandPermissions() {
		opCommandPermissions = ConfigHandler.getConfig("config.yml").getBoolean("Permissions.Commands-OP");
	}
	
	public static void loadGetItemPermissions() {
		getItemPermissions = ConfigHandler.getConfig("config.yml").getBoolean("Permissions.Commands-Get");
	}
	
	public static void loadGlobalPreventSettings() {
		preventPickups = ConfigHandler.getConfig("config.yml").getString("Prevent.Pickups");
		preventItemMovement = ConfigHandler.getConfig("config.yml").getString("Prevent.itemMovement");
		preventAllowOpBypass = Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Prevent.Bypass"), "OP");
		preventAllowCreativeBypass = Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Prevent.Bypass"), "CREATIVE");
	}
	
	public static String isPreventPickups() {
		return preventPickups;
	}
	
	public static String isPreventItemMovement() {
		return preventItemMovement;
	}
	
	public static boolean isPreventAllowOpBypass() {
		return preventAllowOpBypass;
	}
	
	public static boolean isPreventAllowCreativeBypass() {
		return preventAllowCreativeBypass;
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
}