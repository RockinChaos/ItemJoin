package me.RockinChaos.itemjoin.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import net.milkbowl.vault.economy.Economy;

public class VaultAPI {
    private Economy econ = null;
    private boolean isEnabled = false;
    
    public VaultAPI() {
    	this.setVaultStatus(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null);
    }
    
	private void enableEconomy() { 
		if (ConfigHandler.getConfig("config.yml").getBoolean("softDepend.Vault") && ItemJoin.getInstance().getServer().getPluginManager().getPlugin("Vault") != null) {
			if (!this.setupEconomy()) {
				ServerHandler.sendErrorMessage("There was an issue setting up Vault to work with ItemJoin!");
				ServerHandler.sendErrorMessage("If this continues, please contact the plugin developer!");
			}
		}
	}

    private boolean setupEconomy() {
        if (ItemJoin.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {  return false; }
        RegisteredServiceProvider<Economy> rsp = ItemJoin.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {  return false; }
        this.econ = rsp.getProvider();
        return this.econ != null;
    }
    
    public Economy getEconomy() {
        return this.econ;
    }
    
    public boolean vaultEnabled() {
    	return this.isEnabled;
    }
    
    private void setVaultStatus(boolean bool) {
    	if (bool) { this.enableEconomy(); }
    	this.isEnabled = bool;
    }
}