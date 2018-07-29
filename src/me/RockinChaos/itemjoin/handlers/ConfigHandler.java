package me.RockinChaos.itemjoin.handlers;

import java.io.File;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

public class ConfigHandler {
	private static YamlConfiguration loadItems;
	private static YamlConfiguration loadConfig;
	private static YamlConfiguration loadEnLang;
	private static YamlConfiguration loadFirstJoin;
	private static YamlConfiguration loadIPLimit;
	private static String NBTData = "ItemJoin";
	
	public static void loadConfigs() {
		configFile();
		itemsFile();
		enLangFile();
		SQLData.convertYAMLS();
	}

	public static FileConfiguration loadConfig(String path) {
		File file = new File(ItemJoin.getInstance().getDataFolder(), path);
		if (!(file).exists()) {
			try {
				if (path.equalsIgnoreCase("items.yml")) {
					if (ServerHandler.hasAquaticUpdate()) {
						ItemJoin.getInstance().saveResource("items-v1.13.yml", false);
						reNameFile("v1.13");
					} else if (ServerHandler.hasCombatUpdate()) {
						ItemJoin.getInstance().saveResource("items-v1.9.yml", false);
						reNameFile("v1.9");
					} else if (ServerHandler.hasSpecificUpdate("1_8")) {
						ItemJoin.getInstance().saveResource("items-v1.8.yml", false);
						reNameFile("v1.8");
					} else if (ServerHandler.hasSpecificUpdate("1_7")) {
						ItemJoin.getInstance().saveResource("items-v1.7.yml", false);
						reNameFile("v1.7");
					} else {
						ItemJoin.getInstance().saveResource("items-v1.7.yml", false);
						reNameFile("v1.7");
					}
				} else {
					ItemJoin.getInstance().saveResource(path, false);
				}
			} catch (Exception e) {
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
				ItemJoin.getInstance().getLogger().warning("Cannot save " + path + " to disk!");
				return null;
			}
		}
		return getPath(path, 1, file);
	}
	
	public static void reNameFile(String version) {
		File File = new File(ItemJoin.getInstance().getDataFolder(), "items-" + version + ".yml");
		if (File.exists()) {
			String newGen = "items.yml";
			File newFile = new File(ItemJoin.getInstance().getDataFolder(), newGen);
			if (!newFile.exists()) {
				File.renameTo(newFile);
				File configFile = new File(ItemJoin.getInstance().getDataFolder(), "items-" + version + ".yml");
				configFile.delete();
			}
		}
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
				String newGen = "config" + Utils.getRandom(1500000, 10000000) + ".yml";
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
				String newGen = "items" + Utils.getRandom(1500000, 10000000) + ".yml";
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
		getConfig("items.yml").options().copyDefaults(false);
	}
	
	public static void enLangFile() {
	      loadConfig("en-lang.yml");
	      File enLang = new File(ItemJoin.getInstance().getDataFolder(), "en-lang.yml");
	      if (enLang.exists() && ItemJoin.getInstance().getConfig().getString("Language").equalsIgnoreCase("English") && getConfig("en-lang.yml").getInt("en-Version") != 6) {
	      if (ItemJoin.getInstance().getResource("en-lang.yml") != null) {
	        String newGen = "en-lang" + Utils.getRandom(1500000,10000000) + ".yml";
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
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
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
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			return null;
		}
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

	public static String getNBTData() {
		return NBTData;
	}
	
}
