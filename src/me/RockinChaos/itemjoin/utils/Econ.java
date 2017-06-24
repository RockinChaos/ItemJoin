package me.RockinChaos.itemjoin.utils;

import org.bukkit.plugin.RegisteredServiceProvider;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import net.milkbowl.vault.economy.Economy;

public class Econ {
    public static Economy econ = null;
    
	public static void enableEconomy () { 
		if (ConfigHandler.getConfig("config.yml").getBoolean("Vault") == true && ItemJoin.pl.getServer().getPluginManager().getPlugin("Vault") != null) {
	      if (!setupEconomy() ) {
	          ItemJoin.pl.getServer().getPluginManager().disablePlugin(ItemJoin.pl);
	          return;
	      }
		}
	 }

    private static boolean setupEconomy() {
        if (ItemJoin.pl.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = ItemJoin.pl.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
	   public static boolean isVaultAPI() {
		   boolean hasVaultAPI = false;
			  if (ItemJoin.pl.getServer().getPluginManager().getPlugin("Vault") != null 
					  && ItemJoin.pl.getConfig().getBoolean("Vault") == true) {
				  hasVaultAPI = true;
				 }
			return hasVaultAPI;
	   }
}
