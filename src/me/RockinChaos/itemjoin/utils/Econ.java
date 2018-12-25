package me.RockinChaos.itemjoin.utils;

import org.bukkit.plugin.RegisteredServiceProvider;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import net.milkbowl.vault.economy.Economy;

public class Econ {
    public static Economy econ = null;
    
	public static void enableEconomy () { 
		if (ConfigHandler.getConfig("config.yml").getBoolean("Vault") == true && ItemJoin.getInstance().getServer().getPluginManager().getPlugin("Vault") != null) {
	      if (!setupEconomy() ) {
	          ItemJoin.getInstance().getServer().getPluginManager().disablePlugin(ItemJoin.getInstance());
	          return;
	      }
		}
	 }

    private static boolean setupEconomy() {
        if (ItemJoin.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = ItemJoin.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    public static boolean isVaultAPI() {
    	if (ItemJoin.getInstance().getServer().getPluginManager().getPlugin("Vault") != null 
					  && ItemJoin.getInstance().getConfig().getBoolean("Vault") == true) { return true; }
    	return false;
    }
}
