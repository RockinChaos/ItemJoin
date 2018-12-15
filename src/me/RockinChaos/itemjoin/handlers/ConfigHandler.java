package me.RockinChaos.itemjoin.handlers;

import java.io.File;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.YAMLGenerator;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

public class ConfigHandler {
	private static YamlConfiguration loadItems;
	private static YamlConfiguration loadConfig;
	private static YamlConfiguration loadEnLang;
	private static YamlConfiguration loadFirstJoin;
	private static YamlConfiguration loadIPLimit;
	private static boolean generate = false;
	private static String NBTData = "ItemJoin";
	private static long delay;
	private static boolean getItemPermissions = false;
	private static boolean preventInventoryModify = false;
	private static boolean preventPickups = false;
	private static boolean preventAllowOpBypass = false;
	private static boolean preventAllowCreativeBypass = false;
	private static String enabledPreventWorlds;
	
	public static void loadConfigs() {
		configFile();
		itemsFile();
		enLangFile();
		SQLData.convertYAMLS();
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
				generate = true;
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
		} else if (path.contains("first-join.yml")) {
			if (integer == 1) {
				loadFirstJoin = YamlConfiguration.loadConfiguration(file);
			}
			return loadFirstJoin;
		} else if (path.contains("ip-limit.yml")) {
			if (integer == 1) {
				loadIPLimit = YamlConfiguration.loadConfiguration(file);
			}
			return loadIPLimit;
		}
		return null;
	}

	public static void configFile() {
		loadConfig("config.yml");
		File File = new File(ItemJoin.getInstance().getDataFolder(), "config.yml");
		if (File.exists() && getConfig("config.yml").getInt("config-Version") != 6) {
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
		if (generate) { 
			YAMLGenerator.generateItemsFile();
			loadConfig("items.yml");
		}
		getConfig("items.yml").options().copyDefaults(false);
	}
	
	public static void enLangFile() {
	      loadConfig("en-lang.yml");
	      File enLang = new File(ItemJoin.getInstance().getDataFolder(), "en-lang.yml");
	      if (enLang.exists() && ItemJoin.getInstance().getConfig().getString("Language").equalsIgnoreCase("English") && getConfig("en-lang.yml").getInt("en-Version") != 6) {
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
	
	public static long getItemDelay() {
		return delay;
	}
	
	public static void loadDelay() {
		delay = ConfigHandler.getConfig("items.yml").getInt("items-Delay") * 10L;
	}
	
	public static boolean getItemPermissions() {
		return getItemPermissions;
	}
	
	public static void loadGetItemPermissions() {
		getItemPermissions = ConfigHandler.getConfig("config.yml").getBoolean("GetItem-Permissions");
	}
	
	public static void loadGlobalPreventSettings() {
		preventPickups = ConfigHandler.getConfig("config.yml").getBoolean("Prevent-Pickups");
		preventInventoryModify = ConfigHandler.getConfig("config.yml").getBoolean("Prevent-InventoryModify");
		preventAllowOpBypass = ConfigHandler.getConfig("config.yml").getBoolean("AllowOPBypass");
		preventAllowCreativeBypass = ConfigHandler.getConfig("config.yml").getBoolean("CreativeBypass");
		enabledPreventWorlds = ConfigHandler.getConfig("config.yml").getString("enabled-prevent-worlds");
	}
	
	public static boolean isPreventPickups() {
		return preventPickups;
	}
	
	public static boolean isPreventInventoryModify() {
		return preventInventoryModify;
	}
	
	public static boolean isPreventAllowOpBypass() {
		return preventAllowOpBypass;
	}
	
	public static boolean isPreventAllowCreativeBypass() {
		return preventAllowCreativeBypass;
	}
	
	public static String getEnabledPreventWorlds() {
		return enabledPreventWorlds;
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
	
	public static ConfigurationSection getPagesSection(ConfigurationSection items) {
		return getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".pages");
	}
	
	public static ConfigurationSection getCommandsSection(ConfigurationSection items) {
		return getConfig("items.yml").getConfigurationSection(items.getCurrentPath() + ".commands");
	}	
}