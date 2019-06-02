package me.RockinChaos.itemjoin.utils;

import org.bukkit.Bukkit;

public class GuardAPI {
	private boolean isEnabled = false;
	private int guardVersion = 0;
	
	public GuardAPI() {
		this.setGuardStatus(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") != null && Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null);
	}
	
	private void enableGuard() {
		try { this.guardVersion = Integer.parseInt(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion().replace(".", "").substring(0, 3));
		} catch (Exception e) { this.guardVersion = 622; }
	}

    public boolean guardEnabled() {
    	return this.isEnabled;
    }
    
    public int guardVersion() {
    	return this.guardVersion;
    }
	
    private void setGuardStatus(boolean bool) {
    	if (bool) { this.enableGuard(); }
    	this.isEnabled = bool;
    }
}