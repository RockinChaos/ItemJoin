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
package me.RockinChaos.itemjoin.utils.api;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.ReflectionUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.api.MetricsAPI.SimplePie;

public class DependAPI {
	
	private static DependAPI depends;
	
   /**
    * Creates a new DependAPI instance.
    * 
    */
	public DependAPI() {
		GuardAPI.getGuard(true);
		VaultAPI.getVault(true);
	}
	
   /**
    * Gets the list of dependencies that should be ignored.
    * 
    * @return The list of dependencies to be ignored.
    */
	public String getIgnoreList() {
		return ConfigHandler.getConfig().getFile("config.yml").getString("General.ignoreDepend");
	}
	
   /**
    * Checks if Hyperverse is Enabled.
    * 
    * @return If Hyperverse is Enabled.
    */
	public boolean exploitFixerEnabled() {
		return Bukkit.getServer().getPluginManager().isPluginEnabled("ExploitFixer") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "ExploitFixer");
	}
	
   /**
    * Checks if Hyperverse is Enabled.
    * 
    * @return If Hyperverse is Enabled.
    */
	public boolean hyperVerseEnabled() {
		return Bukkit.getServer().getPluginManager().isPluginEnabled("Hyperverse") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "Hyperverse");
	}

   /**
    * Checks if Multiverse Core is Enabled.
    * 
    * @return If Multiverse Core is Enabled.
    */
    public boolean coreEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("Multiverse-Core") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "Multiverse-Core");
    }
    
   /**
    * Checks if Multiverse Inventory is Enabled.
    * 
    * @return If Multiverse Inventory is Enabled.
    */
    public boolean inventoryEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("Multiverse-Inventories") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "Multiverse-Inventories");
    }
    
   /**
    * Checks if PlaceHolderAPI is Enabled.
    * 
    * @return If PlaceHolderAPI is Enabled.
    */
    public boolean placeHolderEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "PlaceholderAPI");
    }
    
   /**
    * Checks if PerWorldPlugins is Enabled.
    * 
    * @return If PerWorldPlugins is Enabled.
    */
    public boolean perPluginsEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("PerWorldPlugins") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "PerWorldInventory");
    }
    
   /**
    * Checks if PerWorldInventory is Enabled.
    * 
    * @return If PerWorldInventory is Enabled.
    */
    public boolean perInventoryEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("PerWorldInventory") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "PerWorldInventory");
    }
    
   /**
    * Checks if BetterNick is Enabled.
    * 
    * @return If BetterNick is Enabled.
    */
    public boolean nickEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("BetterNick") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "BetterNick");
    }
    
   /**
    * Checks if NickAPI is Enabled.
    * 
    * @return If NickAPI is Enabled.
    */
    public boolean nickAPIEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("NickAPI") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "NickAPI");
    }
    
   /**
    * Checks if AuthMe is Enabled.
    * 
    * @return If AuthMe is Enabled.
    */
    public boolean authMeEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("AuthMe") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "AuthMe");
    }
    
   /**
    * Checks if My Worlds is Enabled.
    * 
    * @return If My Worlds is Enabled.
    */
    public boolean myWorldsEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("My_Worlds") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "My_Worlds");
    }
    
   /**
    * Checks if xInventories is Enabled.
    * 
    * @return If xInventories is Enabled.
    */
    public boolean xInventoryEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("xInventories") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "xInventories");
    }
    
   /**
    * Checks if TokenEnchant is Enabled.
    * 
    * @return If TokenEnchant is Enabled.
    */
    public boolean tokenEnchantEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("TokenEnchant") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "TokenEnchant");
    }
    
   /**
    * Checks if HeadDatabase is Enabled.
    * 
    * @return If HeadDatabase is Enabled.
    */
    public boolean databaseEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("HeadDatabase") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "HeadDatabase");
    }
    
   /**
    * Checks if SkinsRestorer is Enabled.
    * 
    * @return If SkinsRestorer is Enabled.
    */
    public boolean skinsRestorerEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("SkinsRestorer") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "SkinsRestorer");
    }
    
   /**
    * Checks if Citizens is Enabled.
    * 
    * @return If Citizens is Enabled.
    */
    public boolean citizensEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("Citizens") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "Citizens");
    }
    
   /**
    * Checks if ChestSort is Enabled.
    * 
    * @return If ChestSort is Enabled.
    */
    public boolean chestSortEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("ChestSort") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "ChestSort");
    }
    
   /**
    * Checks if ProtocolLib is Enabled.
    * 
    * @return If ProtocolLib is Enabled.
    */
    public boolean protocolEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "ProtocolLib");
    }
    
   /**
    * Gets the set SkinsRestorer skin.
    * 
    * @param owner - The skull owner to have their skin fetched.
    * @return The found Skin Texture value.
    */
    public String getSkinValue(final String owner) {
    	Class<?> netty = null;
    	try {
    		netty = ReflectionUtils.getClass("net.skinsrestorer.bukkit.SkinsRestorer");
    	} catch (Exception e1) {
    		try {
    			netty = ReflectionUtils.getClass("skinsrestorer.bukkit.SkinsRestorer");
    		} catch (Exception e2) {
    			ServerUtils.sendDebugTrace(e2);
    			ServerUtils.logSevere("{DependAPI} [1] Unsupported SkinsRestorer version detected, unable to set the skull owner " + owner + ".");
    		}
    	}
    	if (netty != null) {
	    	try {
				final Object skinsRestorer = netty.getMethod("getInstance").invoke(null);
				final Object skinsAPI = skinsRestorer.getClass().getMethod("getSkinsRestorerBukkitAPI").invoke(skinsRestorer);
				final Object playerData = skinsAPI.getClass().getMethod("getSkinName", String.class).invoke(skinsAPI, owner);
				final String ownerData = (playerData != null ? (String) playerData : owner);
				final Object skinData = skinsAPI.getClass().getMethod("getSkinData", String.class).invoke(skinsAPI, ownerData);
				return (skinData != null ? (String) skinData.getClass().getMethod("getValue").invoke(skinData) : null);
			} catch (Exception e1) {
				try {
					netty = ReflectionUtils.getClass("net.skinsrestorer.api.SkinsRestorerAPI");
					final Object skinsRestorer = netty.getMethod("getApi").invoke(null);
					final Object playerData = skinsRestorer.getClass().getMethod("getSkinName", String.class).invoke(skinsRestorer, owner);
					final String ownerData = (playerData != null ? (String) playerData : owner);
					final Object skinData = skinsRestorer.getClass().getMethod("getSkinData", String.class).invoke(skinsRestorer, ownerData);
					return (skinData != null ? (String) skinData.getClass().getMethod("getValue").invoke(skinData) : null);
				} catch (Exception e2) {
					ServerUtils.sendDebugTrace(e2);
					ServerUtils.logSevere("{DependAPI} [2] Unsupported SkinsRestorer version detected, unable to set the skull owner " + owner + ".");
				}
			}
    	}
    	return null;
    }
    
   /**
    * Gets the GuardAPI instance.
    * 
    * @return The current GuardAPI instance.
    */
	public GuardAPI getGuard() {
		return GuardAPI.getGuard(false);
	}
	
   /**
    * Gets the VaultAPI instance.
    * 
    * @return The current VaultAPI instance.
    */
	public VaultAPI getVault() {
		return VaultAPI.getVault(false);
	}
	
   /**
    * Sends a logging message of the found and enabled soft dependencies.
    * 
    */
	public void sendUtilityDepends() {
		String enabledPlugins = (this.authMeEnabled() ? "AuthMe, " : "") + (this.nickEnabled() ? "BetterNick, " : "") + (this.nickAPIEnabled() ? "NickAPI, " : "") 
				+ (this.exploitFixerEnabled() ? "ExploitFixer, " : "")+ (this.hyperVerseEnabled() ? "Hyperverse, " : "") + (this.coreEnabled() ? "Multiverse-Core, " : "") + (this.inventoryEnabled() ? "Multiverse-Inventories, " : "") 
				+ (this.myWorldsEnabled() ? "My Worlds, " : "") + (this.perInventoryEnabled() ? "PerWorldInventory, " : "") 
				+ (this.perPluginsEnabled() ? "PerWorldPlugins, " : "") + (this.tokenEnchantEnabled() ? "TokenEnchant, " : "") 
				+ (this.getGuard().guardEnabled() ? "WorldGuard, " : "") + (this.databaseEnabled() ? "HeadDatabase, " : "") 
				+ (this.xInventoryEnabled() ? "xInventories, " : "") + (this.placeHolderEnabled() ? "PlaceholderAPI, " : "") + (this.protocolEnabled() ? "ProtocolLib, " : "") + 
				(this.skinsRestorerEnabled() ? "SkinsRestorer, " : "") + (this.citizensEnabled() ? "Citizens, " : "") + (this.chestSortEnabled() ? "ChestSort, " : "")
				+ (this.getVault().vaultEnabled() ? "Vault, " : "");
		if (!enabledPlugins.isEmpty()) { ServerUtils.logInfo("Hooked into { " + enabledPlugins.substring(0, enabledPlugins.length() - 2) + " }"); }
		if (this.getIgnoreList() != null && !this.getIgnoreList().isEmpty() && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "NONE") 
				&& !StringUtils.containsIgnoreCase(this.getIgnoreList(), "DISABLED") && !StringUtils.containsIgnoreCase(this.getIgnoreList(), "DISABLE")) {
			ServerUtils.logInfo("The following plugins will be ignored { " + this.getIgnoreList() + " }");
		}
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("Vault") && !this.getVault().vaultEnabled()) {
			ServerUtils.logDebug("{VaultAPI} An error has occured while setting up enabling Vault-ItemJoin support, no economy plugin detected."); 
		}
		
		if (this.exploitFixerEnabled()) {
			final FileConfiguration exploitConfig = Bukkit.getPluginManager().getPlugin("ExploitFixer").getConfig();
			if (exploitConfig.getString("itemsfix.enabled") != null && exploitConfig.getBoolean("itemsfix.enabled")) {
				ServerUtils.logSevere("{DependAPI} ExploitFixer has been detected with itemsfix enabled! ItemJoin and other custom items plugins WILL BREAK with this feature enabled."
						+ "Please set itemsfix.enabled to false in the config.yml of ExploitFixer to resolve this conflict.");
			}
		}
		
	}

   /**
    * Adds Custom Charts to the Metrics.
    * 
    * @param metrics - The referenced Metrics connection.
    */
	public void addCustomCharts(final MetricsAPI metrics) {
		metrics.addCustomChart(new SimplePie("items", () -> ItemUtilities.getUtilities().getItems().size() + " "));
		metrics.addCustomChart(new SimplePie("itemPermissions", () -> ConfigHandler.getConfig().getFile("config.yml").getBoolean("Permissions.Obtain-Items") ? "True" : "False"));
		metrics.addCustomChart(new SimplePie("language", () -> LanguageAPI.getLang(false).getLanguage()));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.authMeEnabled() ? "AuthMe" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.nickEnabled() ? "BetterNick" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.nickAPIEnabled() ? "NickAPI" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.exploitFixerEnabled() ? "ExploitFixer" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.hyperVerseEnabled() ? "HeadDatabase" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.hyperVerseEnabled() ? "Hyperverse" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.coreEnabled() ? "Multiverse-Core" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.inventoryEnabled() ? "Multiverse-Inventories" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.myWorldsEnabled() ? "My Worlds" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.perInventoryEnabled() ? "PerWorldInventory" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.perPluginsEnabled() ? "PerWorldPlugins" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.placeHolderEnabled() ? "PlaceholderAPI" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.protocolEnabled() ? "ProtocolLib" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.skinsRestorerEnabled() ? "SkinsRestorer" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.citizensEnabled() ? "Citizens" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.chestSortEnabled() ? "ChestSort" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.tokenEnchantEnabled() ? "TokenEnchant" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.getVault().vaultEnabled() ? "Vault" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.getGuard().guardEnabled() ? "WorldGuard" : ""));
		metrics.addCustomChart(new SimplePie("softDepend", () -> this.xInventoryEnabled() ? "xInventories" : ""));
	} 
	
   /**
    * Gets the instance of the DependAPI.
    * 
    * @param regen - If the DependAPI should have a new instance created.
    * @return The DependAPI instance.
    */
    public static DependAPI getDepends(final boolean regen) { 
        if (depends == null || regen) { depends = new DependAPI(); }
        return depends; 
    }
}