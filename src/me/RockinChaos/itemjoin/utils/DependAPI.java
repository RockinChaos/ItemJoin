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
package me.RockinChaos.itemjoin.utils;

import org.bukkit.Bukkit;

import com.mojang.authlib.properties.Property;

import me.RockinChaos.itemjoin.handlers.ServerHandler;
import skinsrestorer.bukkit.SkinsRestorer;

public class DependAPI {
	
	private boolean hyperVerse = false;
	private boolean multiverseCore = false;
	private boolean multiverseInventories = false;
	private boolean placeHolderAPI = false;
	private boolean perWorldPlugins = false;
	private boolean perWorldInventory = false;
	private boolean betterNick = false;
	private boolean authMe = false;
	private boolean myWorlds = false;
	private boolean xInventories = false;
	private boolean tokenEnchant = false;
	private boolean headDatabase = false;
	private boolean skinsRestorer = false;
	private boolean citizens = false;
	
	private static DependAPI depends;
	
   /**
    * Creates a new DependAPI instance.
    * 
    */
	public DependAPI() {
		this.setHyperverseStatus(Bukkit.getServer().getPluginManager().getPlugin("Hyperverse") != null);
		this.setCoreStatus(Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core") != null);
		this.setInventoryStatus(Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Inventories") != null);
		this.setPlaceHolderStatus(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null);
		this.setPerPluginsStatus(Bukkit.getServer().getPluginManager().getPlugin("PerWorldPlugins") != null);
		this.setPerInventoryStatus(Bukkit.getServer().getPluginManager().getPlugin("PerWorldInventory") != null);
		this.setNickStatus(Bukkit.getServer().getPluginManager().getPlugin("BetterNick") != null);
		this.setAuthMeStatus(Bukkit.getServer().getPluginManager().getPlugin("AuthMe") != null);
		this.setMyWorldsStatus(Bukkit.getServer().getPluginManager().getPlugin("My_Worlds") != null);
		this.setXInventoryStatus(Bukkit.getServer().getPluginManager().getPlugin("xInventories") != null);
		this.setTokenEnchantStatus(Bukkit.getServer().getPluginManager().getPlugin("TokenEnchant") != null);
		this.setDatabaseStatus(Bukkit.getServer().getPluginManager().getPlugin("HeadDatabase") != null);
		this.setSkinsRestorerStatus(Bukkit.getServer().getPluginManager().getPlugin("SkinsRestorer") != null);
		this.setCitizensStatus(Bukkit.getServer().getPluginManager().getPlugin("Citizens") != null);
		GuardAPI.getGuard(true);
		VaultAPI.getVault(true);
	}
	
   /**
    * Checks if Hyperverse is Enabled.
    * 
    * @return If Hyperverse is Enabled.
    */
	public boolean hyperVerseEnabled() {
		return this.hyperVerse;
	}

   /**
    * Checks if Multiverse Core is Enabled.
    * 
    * @return If Multiverse Core is Enabled.
    */
    public boolean coreEnabled() {
    	return this.multiverseCore;
    }
    
   /**
    * Checks if Multiverse Inventory is Enabled.
    * 
    * @return If Multiverse Inventory is Enabled.
    */
    public boolean inventoryEnabled() {
    	return this.multiverseInventories;
    }
    
   /**
    * Checks if PlaceHolderAPI is Enabled.
    * 
    * @return If PlaceHolderAPI is Enabled.
    */
    public boolean placeHolderEnabled() {
    	return this.placeHolderAPI;
    }
    
   /**
    * Checks if PerWorldPlugins is Enabled.
    * 
    * @return If PerWorldPlugins is Enabled.
    */
    public boolean perPluginsEnabled() {
    	return this.perWorldPlugins;
    }
    
   /**
    * Checks if PerWorldInventory is Enabled.
    * 
    * @return If PerWorldInventory is Enabled.
    */
    public boolean perInventoryEnabled() {
    	return this.perWorldInventory;
    }
    
   /**
    * Checks if BetterNick is Enabled.
    * 
    * @return If BetterNick is Enabled.
    */
    public boolean nickEnabled() {
    	return this.betterNick;
    }
    
   /**
    * Checks if AuthMe is Enabled.
    * 
    * @return If AuthMe is Enabled.
    */
    public boolean authMeEnabled() {
    	return this.authMe;
    }
    
   /**
    * Checks if My Worlds is Enabled.
    * 
    * @return If My Worlds is Enabled.
    */
    public boolean myWorldsEnabled() {
    	return this.myWorlds;
    }
    
   /**
    * Checks if xInventories is Enabled.
    * 
    * @return If xInventories is Enabled.
    */
    public boolean xInventoryEnabled() {
    	return this.xInventories;
    }
    
   /**
    * Checks if TokenEnchant is Enabled.
    * 
    * @return If TokenEnchant is Enabled.
    */
    public boolean tokenEnchantEnabled() {
    	return this.tokenEnchant;
    }
    
   /**
    * Checks if HeadDatabase is Enabled.
    * 
    * @return If HeadDatabase is Enabled.
    */
    public boolean databaseEnabled() {
    	return this.headDatabase;
    }
    
   /**
    * Checks if SkinsRestorer is Enabled.
    * 
    * @return If SkinsRestorer is Enabled.
    */
    public boolean skinsRestorerEnabled() {
    	return this.skinsRestorer;
    }
    
   /**
    * Checks if Citizens is Enabled.
    * 
    * @return If Citizens is Enabled.
    */
    public boolean citizensEnabled() {
    	return this.citizens;
    }

   /**
    * Sets the status of Hyperverse.
    * 
    * @param bool - If Hyperverse is enabled.
    */
    public void setHyperverseStatus(final boolean bool) {
    	this.hyperVerse = bool;
    }
    
   /**
    * Sets the status of Multiverse Core.
    * 
    * @param bool - If Multiverse Core is enabled.
    */
    public void setCoreStatus(final boolean bool) {
    	this.multiverseCore = bool;
    }
    
   /**
    * Sets the status of Multiverse Inventory.
    * 
    * @param bool - If Multiverse Inventory is enabled.
    */
    public void setInventoryStatus(final boolean bool) {
    	this.multiverseInventories = bool;
    }
    
   /**
    * Sets the status of PlaceHolderAPI.
    * 
    * @param bool - If PlaceHolderAPI is enabled.
    */
    public void setPlaceHolderStatus(final boolean bool) {
    	this.placeHolderAPI = bool;
    }
    
   /**
    * Sets the status of PerWorldPlugins.
    * 
    * @param bool - If PerWorldPlugins is enabled.
    */
    public void setPerPluginsStatus(final boolean bool) {
    	this.perWorldPlugins = bool;
    }
    
   /**
    * Sets the status of PerWorldInventory.
    * 
    * @param bool - If PerWorldInventory is enabled.
    */
    public void setPerInventoryStatus(final boolean bool) {
    	this.perWorldInventory = bool;
    }
    
   /**
    * Sets the status of BetterNick.
    * 
    * @param bool - If BetterNick is enabled.
    */
    public void setNickStatus(final boolean bool) {
    	this.betterNick = bool;
    }
    
   /**
    * Sets the status of AuthMe.
    * 
    * @param bool - If AuthMe is enabled.
    */
    public void setAuthMeStatus(boolean bool) {
    	this.authMe = bool;
    }
    
   /**
    * Sets the status of My Worlds.
    * 
    * @param bool - If My Worlds is enabled.
    */
    public void setMyWorldsStatus(final boolean bool) {
    	this.myWorlds = bool;
    }
    
   /**
    * Sets the status of xInventory.
    * 
    * @param bool - If xInventory is enabled.
    */
    public void setXInventoryStatus(final boolean bool) {
    	this.xInventories = bool;
    }
    
   /**
    * Sets the status of TokenEnchant.
    * 
    * @param bool - If TokenEnchant is enabled.
    */
    public void setTokenEnchantStatus(final boolean bool) {
    	this.tokenEnchant = bool;
    }
    
   /**
    * Sets the status of HeadDatabase.
    * 
    * @param bool - If HeadDatabase is enabled.
    */
    public void setDatabaseStatus(final boolean bool) {
    	this.headDatabase = bool;
    }
    
   /**
    * Sets the status of SkinsRestorer.
    * 
    * @param bool - If SkinsRestorer is enabled.
    */
    public void setSkinsRestorerStatus(final boolean bool) {
    	this.skinsRestorer = bool;
    }
    
   /**
    * Sets the status of Citizens.
    * 
    * @param bool - If Citizens is enabled.
    */
    public void setCitizensStatus(final boolean bool) {
    	this.citizens = bool;
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
				+ (this.xInventoryEnabled() ? "xInventories, " : "") + (this.placeHolderEnabled() ? "PlaceholderAPI, " : "") + (this.skinsRestorer ? "SkinsRestorer, " : "") + (this.citizens ? "Citizens, " : "") 
				+ (this.getVault().vaultEnabled() ? "Vault " : "");
		if (!enabledPlugins.isEmpty()) { ServerHandler.getServer().logInfo("Hooked into { " + enabledPlugins + "}"); }
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