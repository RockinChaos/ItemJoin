package com.sk89q.worldguard.bukkit;

public class WorldGuardPlugin {
	
	private final RegionContainer regionContainer = new RegionContainer(this);
	
    public RegionContainer getRegionContainer() { return regionContainer; }

}
