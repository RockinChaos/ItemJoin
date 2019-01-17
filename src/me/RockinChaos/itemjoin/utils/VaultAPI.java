package me.RockinChaos.itemjoin.utils;

import org.bukkit.plugin.RegisteredServiceProvider;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import net.milkbowl.vault.economy.Economy;

public class VaultAPI {
    private static Economy econ = null;
    private static boolean isEnabled = false;
    
	public static void enableEconomy() { 
		if (ConfigHandler.getConfig("config.yml").getBoolean("Vault") == true && ItemJoin.getInstance().getServer().getPluginManager().getPlugin("Vault") != null) {
			if (!setupEconomy()) {
	          ServerHandler.sendErrorMessage("There was an issue setting up Vault to work with ItemJoin!");
	          ServerHandler.sendErrorMessage("If this continues, please contact the plugin developer!");
	          return;
	          }
			}
		}

    private static boolean setupEconomy() {
        if (ItemJoin.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {  return false; }
        RegisteredServiceProvider<Economy> rsp = ItemJoin.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {  return false; }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    public static Economy getEconomy() {
        return econ;
    }
    
    public static boolean vaultEnabled() {
    	return isEnabled;
    }
    
    public static void setVaultStatus(boolean bool) {
    	isEnabled = bool;
    }
}