package me.RockinChaos.itemjoin.handlers;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.Utils;

public class ConfigHandler {
	public static YamlConfiguration loadItems;
	public static YamlConfiguration loadConfig;
	public static YamlConfiguration loadEnLang;
	public static YamlConfiguration loadFirstJoin;
	public static String secretMsg;

	public static FileConfiguration loadConfig(String path) {
		File file = new File(ItemJoin.pl.getDataFolder(), path);
		if (!(file).exists()) {
			try {
				ItemJoin.pl.saveResource(path, false);
			} catch (Exception e) {
				e.printStackTrace();
				ItemJoin.pl.getLogger().warning("Cannot save " + path + " to disk!");
				return null;
			}
		}
		return getPath(path, 1, file);
	}

	public static FileConfiguration getConfig(String path) {
		File file = new File(ItemJoin.pl.getDataFolder(), path);
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
		}
		return null;
	}

	public static void loadConfigs() {
		configFile();
		itemsFile();
		firstjoinFile();
		enLangFile();
	}

	public static void configFile() {
		ConfigHandler.loadConfig("config.yml");
		File File = new File(ItemJoin.pl.getDataFolder(), "config.yml");
		if (File.exists() && ConfigHandler.getConfig("config.yml").getInt("config-Version") != 6) {
			if (ItemJoin.pl.getResource("config.yml") != null) {
				String newGen = "config" + Utils.getRandom(1500000, 10000000) + ".yml";
				File newFile = new File(ItemJoin.pl.getDataFolder(), newGen);
				if (!newFile.exists()) {
					File.renameTo(newFile);
					File configFile = new File(ItemJoin.pl.getDataFolder(), "config.yml");
					configFile.delete();
					ConfigHandler.loadConfig("config.yml");
					ServerHandler.sendConsoleMessage("&aYour config.yml is out of date and new options are available, generating a new one!");
				}
			}
		}
		ConfigHandler.getConfig("config.yml").options().copyDefaults(false);
	}

	public static void itemsFile() {
		ConfigHandler.loadConfig("items.yml");
		File itemsFile = new File(ItemJoin.pl.getDataFolder(), "items.yml");
		if (itemsFile.exists() && ConfigHandler.getConfig("items.yml").getInt("items-Version") != 6) {
			if (ItemJoin.pl.getResource("items.yml") != null) {
				String newGen = "items" + Utils.getRandom(1500000, 10000000) + ".yml";
				File newFile = new File(ItemJoin.pl.getDataFolder(), newGen);
				if (!newFile.exists()) {
					itemsFile.renameTo(newFile);
					File configFile = new File(ItemJoin.pl.getDataFolder(), "items.yml");
					configFile.delete();
					ConfigHandler.loadConfig("items.yml");
					ServerHandler.sendConsoleMessage("&4Your items.yml is out of date and new options are available, generating a new one!");
				}
			}
		}
		ConfigHandler.getConfig("items.yml").options().copyDefaults(false);
	}

	public static void firstjoinFile() {
		File file = new File(ItemJoin.pl.getDataFolder(), "first-join.yml");
		if ((file).exists()) {
			ConfigHandler.loadConfig("first-join.yml");
			File firstjoinFile = new File(ItemJoin.pl.getDataFolder(), "first-join.yml");
			if (firstjoinFile.exists() && ConfigHandler.getConfig("first-join.yml").getInt("first-Version") != 6) {
				if (ItemJoin.pl.getResource("first-join.yml") != null) {
					String newGen = "first-join" + Utils.getRandom(1500000, 10000000) + ".yml";
					File newFile = new File(ItemJoin.pl.getDataFolder(), newGen);
					if (!newFile.exists()) {
						firstjoinFile.renameTo(newFile);
						File configFile = new File(ItemJoin.pl.getDataFolder(), "first-join.yml");
						configFile.delete();
						ConfigHandler.loadConfig("first-join.yml");
						ServerHandler.sendConsoleMessage("&4Your first-join.yml is out of date and new options are available, generating a new one!");
					}
				}
			}
			ConfigHandler.getConfig("first-join.yml").options().copyDefaults(false);
		}
	}
	
	public static void enLangFile() {
	      ConfigHandler.loadConfig("en-lang.yml");
	      File enLang = new File(ItemJoin.pl.getDataFolder(), "en-lang.yml");
	      if (enLang.exists() && ItemJoin.pl.getConfig().getString("Language").equalsIgnoreCase("English") && ConfigHandler.getConfig("en-lang.yml").getInt("en-Version") != 6) {
	      if (ItemJoin.pl.getResource("en-lang.yml") != null) {
	        String newGen = "en-lang" + Utils.getRandom(1500000,10000000) + ".yml";
	        File newFile = new File(ItemJoin.pl.getDataFolder(), newGen);
	           if (!newFile.exists()) {
	    	      enLang.renameTo(newFile);
	              File configFile = new File(ItemJoin.pl.getDataFolder(), "en-lang.yml");
	              configFile.delete();
				  ConfigHandler.loadConfig("en-lang.yml");
				  ServerHandler.sendConsoleMessage("&4Your en-lang.yml is out of date and new options are available, generating a new one!");
	           }
	        }
	      }
		  if (ItemJoin.pl.getConfig().getString("Language").equalsIgnoreCase("English")) {
			  ConfigHandler.getConfig("en-lang.yml").options().copyDefaults(false);
		  }
 }

	public static Boolean hasFirstJoined(Player player, String item) {
		Boolean hasFirstJoined = false;
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		String ItemFlags = items.getString(".itemflags");
		if (ItemHandler.containsIgnoreCase(ItemFlags, "first-join") && hasFirstJoinedConfig(player, item) != null) {
			hasFirstJoined = true;
		}
		return hasFirstJoined;
	}

	public static String hasFirstJoinedConfig(Player player, String item) {
		String hasFJPlayer = null;
		try {
			hasFJPlayer = ConfigHandler.getConfig("first-join.yml").getString(player.getWorld().getName() + "." + item + "." + player.getUniqueId().toString());
		} catch (NullPointerException ex) {}
		return hasFJPlayer;
	}

	public static void saveFirstJoined(Player player, String item) {
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		if (ItemHandler.containsIgnoreCase(items.getString(".itemflags"), "first-join")) {
			File playerFile = new File(ItemJoin.pl.getDataFolder(), "first-join.yml");
			FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
			playerData.set(player.getWorld().getName() + "." + item + "." + player.getUniqueId().toString() + "." + "IGN", player.getName().toString());
			try {
				playerData.save(playerFile);
				ConfigHandler.loadConfig("first-join.yml");
				ConfigHandler.getConfig("first-join.yml").options().copyDefaults(false);
			} catch (IOException e1) {
				ItemJoin.pl.getServer().getLogger().severe("Could not save " + player.getName() + " to the data file first-join.yml!");
				e1.printStackTrace();
			}
		}
	}
	
	public static ConfigurationSection getConfigurationSection() {
		ConfigurationSection section = ConfigHandler.getConfig("items.yml").getConfigurationSection("items");
		return section;
	}
	
	public static ConfigurationSection getItemSection(String item) {
		ConfigurationSection items = ConfigHandler.getConfigurationSection().getConfigurationSection(item);
		return items;
	}

	public static String encodeSecretData(String str) {
		try {
			String hiddenData = "";
			for (char c: str.toCharArray()) {
				hiddenData += "§" + c;
			}
			return hiddenData;
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
			return null;
		}
	}

	public static boolean secretMsg() {
		boolean isSecret = true;
		secretMsg = "ItemJoin";
		return isSecret;
	}
}
