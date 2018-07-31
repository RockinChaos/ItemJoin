package com.sk89q.worldguard.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class WorldGuardPlugin extends JavaPlugin {
	
    private final RegionContainer regionContainer = new RegionContainer(this);
    
    public RegionContainer getRegionContainer() { return regionContainer; }
}
