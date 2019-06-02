package me.RockinChaos.itemjoin.utils;

import org.bukkit.Bukkit;

public class DependAPI {
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
	private GuardAPI worldGuard;
	private VaultAPI vault;
	
	public DependAPI() {
		this.setMCoreStatus(Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core") != null);
		this.setMInventoryStatus(Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Inventories") != null);
		this.setPlaceHolderStatus(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null);
		this.setPerPluginsStatus(Bukkit.getServer().getPluginManager().getPlugin("PerWorldPlugins") != null);
		this.setPerInventoryStatus(Bukkit.getServer().getPluginManager().getPlugin("PerWorldInventory") != null);
		this.setNickStatus(Bukkit.getServer().getPluginManager().getPlugin("BetterNick") != null);
		this.setAuthMeStatus(Bukkit.getServer().getPluginManager().getPlugin("AuthMe") != null);
		this.setMyWorldsStatus(Bukkit.getServer().getPluginManager().getPlugin("My_Worlds") != null);
		this.setXInventoryStatus(Bukkit.getServer().getPluginManager().getPlugin("xInventories") != null);
		this.setTokenEnchantStatus(Bukkit.getServer().getPluginManager().getPlugin("TokenEnchant") != null);
		this.setDatabaseStatus(Bukkit.getServer().getPluginManager().getPlugin("HeadDatabase") != null);
		this.setGuard();
		this.setVault();
	}

    public boolean mCoreEnabled() {
    	return this.multiverseCore;
    }
    
    public boolean mInventoryEnabled() {
    	return this.multiverseInventories;
    }
    
    public boolean placeHolderEnabled() {
    	return this.placeHolderAPI;
    }
    
    public boolean perPluginsEnabled() {
    	return this.perWorldPlugins;
    }
    
    public boolean perInventoryEnabled() {
    	return this.perWorldInventory;
    }
    
    public boolean nickEnabled() {
    	return this.betterNick;
    }
    
    public boolean authMeEnabled() {
    	return this.authMe;
    }
    
    public boolean myWorldsEnabled() {
    	return this.myWorlds;
    }
    
    public boolean xInventoryEnabled() {
    	return this.xInventories;
    }
    
    public boolean tokenEnchantEnabled() {
    	return this.tokenEnchant;
    }
    
    public boolean databaseEnabled() {
    	return this.headDatabase;
    }

    public void setMCoreStatus(boolean bool) {
    	this.multiverseCore = bool;
    }
    
    public void setMInventoryStatus(boolean bool) {
    	this.multiverseInventories = bool;
    }
    
    public void setPlaceHolderStatus(boolean bool) {
    	this.placeHolderAPI = bool;
    }
    
    public void setPerPluginsStatus(boolean bool) {
    	this.perWorldPlugins = bool;
    }
    
    public void setPerInventoryStatus(boolean bool) {
    	this.perWorldInventory = bool;
    }
    
    public void setNickStatus(boolean bool) {
    	this.betterNick = bool;
    }
    
    public void setAuthMeStatus(boolean bool) {
    	this.authMe = bool;
    }
    
    public void setMyWorldsStatus(boolean bool) {
    	this.myWorlds = bool;
    }
    
    public void setXInventoryStatus(boolean bool) {
    	this.xInventories = bool;
    }
    
    public void setTokenEnchantStatus(boolean bool) {
    	this.tokenEnchant = bool;
    }
    
    public void setDatabaseStatus(boolean bool) {
    	this.headDatabase = bool;
    }
    
	public GuardAPI getGuard() {
		return this.worldGuard;
	}
	
	private void setGuard() {
		this.worldGuard = new GuardAPI();
	}
	
	public VaultAPI getVault() {
		return this.vault;
	}
	
	private void setVault() {
		this.vault = new VaultAPI();
	}
}
