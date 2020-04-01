package me.RockinChaos.itemjoin.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class VaultAPI {
    private Economy econ = null;
    private boolean isEnabled = false;
    
    public VaultAPI() {
    	this.setVaultStatus(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null);
    }
    
	private void enableEconomy() { 
		if (ConfigHandler.getConfig("config.yml").getBoolean("softDepend.Vault") && ItemJoin.getInstance().getServer().getPluginManager().getPlugin("Vault") != null) {
			if (!this.setupEconomy()) {
				ServerHandler.logSevere("{VaultAPI} An error has occured while setting up enabling Vault-ItemJoin support!");
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
    
	public double getBalance(Player player) {
		return this.econ.getBalance(player);
	}
	
	public EconomyResponse withdrawBalance(Player player, int cost) {
		return this.econ.withdrawPlayer(player, cost);
	}
}