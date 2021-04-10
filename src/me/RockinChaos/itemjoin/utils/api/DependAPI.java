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

import com.mojang.authlib.properties.Property;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.api.MetricsAPI.SimplePie;
import skinsrestorer.bukkit.SkinsRestorer;

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
    * Checks if Hyperverse is Enabled.
    * 
    * @return If Hyperverse is Enabled.
    */
	public boolean hyperVerseEnabled() {
		return Bukkit.getServer().getPluginManager().isPluginEnabled("Hyperverse");
	}

   /**
    * Checks if Multiverse Core is Enabled.
    * 
    * @return If Multiverse Core is Enabled.
    */
    public boolean coreEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("Multiverse-Core");
    }
    
   /**
    * Checks if Multiverse Inventory is Enabled.
    * 
    * @return If Multiverse Inventory is Enabled.
    */
    public boolean inventoryEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("Multiverse-Inventories");
    }
    
   /**
    * Checks if PlaceHolderAPI is Enabled.
    * 
    * @return If PlaceHolderAPI is Enabled.
    */
    public boolean placeHolderEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
    
   /**
    * Checks if PerWorldPlugins is Enabled.
    * 
    * @return If PerWorldPlugins is Enabled.
    */
    public boolean perPluginsEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("PerWorldPlugins");
    }
    
   /**
    * Checks if PerWorldInventory is Enabled.
    * 
    * @return If PerWorldInventory is Enabled.
    */
    public boolean perInventoryEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("PerWorldInventory");
    }
    
   /**
    * Checks if BetterNick is Enabled.
    * 
    * @return If BetterNick is Enabled.
    */
    public boolean nickEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("BetterNick");
    }
    
   /**
    * Checks if AuthMe is Enabled.
    * 
    * @return If AuthMe is Enabled.
    */
    public boolean authMeEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("AuthMe");
    }
    
   /**
    * Checks if My Worlds is Enabled.
    * 
    * @return If My Worlds is Enabled.
    */
    public boolean myWorldsEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("My_Worlds");
    }
    
   /**
    * Checks if xInventories is Enabled.
    * 
    * @return If xInventories is Enabled.
    */
    public boolean xInventoryEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("xInventories");
    }
    
   /**
    * Checks if TokenEnchant is Enabled.
    * 
    * @return If TokenEnchant is Enabled.
    */
    public boolean tokenEnchantEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("TokenEnchant");
    }
    
   /**
    * Checks if HeadDatabase is Enabled.
    * 
    * @return If HeadDatabase is Enabled.
    */
    public boolean databaseEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("HeadDatabase");
    }
    
   /**
    * Checks if SkinsRestorer is Enabled.
    * 
    * @return If SkinsRestorer is Enabled.
    */
    public boolean skinsRestorerEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("SkinsRestorer");
    }
    
   /**
    * Checks if Citizens is Enabled.
    * 
    * @return If Citizens is Enabled.
    */
    public boolean citizensEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("Citizens");
    }
    
   /**
    * Checks if ChestSort is Enabled.
    * 
    * @return If ChestSort is Enabled.
    */
    public boolean chestSortEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("ChestSort");
    }
    
   /**
    * Checks if ProtocolLib is Enabled.
    * 
    * @return If ProtocolLib is Enabled.
    */
    public boolean protocolEnabled() {
    	return Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib");
    }
    
   /**
    * Gets the set SkinsRestorer skin.
    * 
    * @param owner - The skull owner to have their skin fetched.
    * @return The found Skin Texture value.
    */
    public String getSkinValue(final String owner) {
    	return ((Property) SkinsRestorer.getInstance().getSkinsRestorerBukkitAPI().getSkinData(owner)).getValue();
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
		String enabledPlugins = (this.authMeEnabled() ? "AuthMe, " : "") + (this.nickEnabled() ? "BetterNick, " : "") 
				+ (this.hyperVerseEnabled() ? "Hyperverse, " : "") + (this.coreEnabled() ? "Multiverse-Core, " : "") + (this.inventoryEnabled() ? "Multiverse-Inventories, " : "") 
				+ (this.myWorldsEnabled() ? "My Worlds, " : "") + (this.perInventoryEnabled() ? "PerWorldInventory, " : "") 
				+ (this.perPluginsEnabled() ? "PerWorldPlugins, " : "") + (this.tokenEnchantEnabled() ? "TokenEnchant, " : "") 
				+ (this.getGuard().guardEnabled() ? "WorldGuard, " : "") + (this.databaseEnabled() ? "HeadDatabase, " : "") 
				+ (this.xInventoryEnabled() ? "xInventories, " : "") + (this.placeHolderEnabled() ? "PlaceholderAPI, " : "") + (this.protocolEnabled() ? "ProtocolLib, " : "") + 
				(this.skinsRestorerEnabled() ? "SkinsRestorer, " : "") + (this.citizensEnabled() ? "Citizens, " : "") + (this.chestSortEnabled() ? "ChestSort, " : "")
				+ (this.getVault().vaultEnabled() ? "Vault, " : "");
		if (!enabledPlugins.isEmpty()) { ServerUtils.logInfo("Hooked into { " + enabledPlugins.substring(0, enabledPlugins.length() - 2) + " }"); }
		
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("Vault") && !this.getVault().vaultEnabled()) {
			ServerUtils.logDebug("{VaultAPI} An error has occured while setting up enabling Vault-ItemJoin support, no economy plugin detected."); 
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