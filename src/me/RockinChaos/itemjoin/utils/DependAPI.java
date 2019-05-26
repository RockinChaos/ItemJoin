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
    	return multiverseCore;
    }
    
    public boolean mInventoryEnabled() {
    	return multiverseInventories;
    }
    
    public boolean placeHolderEnabled() {
    	return placeHolderAPI;
    }
    
    public boolean perPluginsEnabled() {
    	return perWorldPlugins;
    }
    
    public boolean perInventoryEnabled() {
    	return perWorldInventory;
    }
    
    public boolean nickEnabled() {
    	return betterNick;
    }
    
    public boolean authMeEnabled() {
    	return authMe;
    }
    
    public boolean myWorldsEnabled() {
    	return myWorlds;
    }
    
    public boolean xInventoryEnabled() {
    	return xInventories;
    }
    
    public boolean tokenEnchantEnabled() {
    	return tokenEnchant;
    }
    
    public boolean databaseEnabled() {
    	return headDatabase;
    }

    public void setMCoreStatus(boolean bool) {
    	multiverseCore = bool;
    }
    
    public void setMInventoryStatus(boolean bool) {
    	multiverseInventories = bool;
    }
    
    public void setPlaceHolderStatus(boolean bool) {
    	placeHolderAPI = bool;
    }
    
    public void setPerPluginsStatus(boolean bool) {
    	perWorldPlugins = bool;
    }
    
    public void setPerInventoryStatus(boolean bool) {
    	perWorldInventory = bool;
    }
    
    public void setNickStatus(boolean bool) {
    	betterNick = bool;
    }
    
    public void setAuthMeStatus(boolean bool) {
    	authMe = bool;
    }
    
    public void setMyWorldsStatus(boolean bool) {
    	myWorlds = bool;
    }
    
    public void setXInventoryStatus(boolean bool) {
    	xInventories = bool;
    }
    
    public void setTokenEnchantStatus(boolean bool) {
    	tokenEnchant = bool;
    }
    
    public void setDatabaseStatus(boolean bool) {
    	headDatabase = bool;
    }
    
	public GuardAPI getGuard() {
		return worldGuard;
	}
	
	private void setGuard() {
		worldGuard = new GuardAPI();
	}
	
	public VaultAPI getVault() {
		return vault;
	}
	
	private void setVault() {
		vault = new VaultAPI();
	}
}
