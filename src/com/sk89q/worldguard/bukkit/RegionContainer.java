package com.sk89q.worldguard.bukkit;

import com.sk89q.worldguard.protection.managers.RegionContainerImpl;
import com.sk89q.worldguard.protection.managers.RegionManager;
import javax.annotation.Nullable;

import org.bukkit.World;

public class RegionContainer {

    private RegionContainerImpl container;

    RegionContainer(WorldGuardPlugin plugin) {}
    
    @Nullable
    public RegionManager get(World world) {
        return container.get(world.getName());
    }

}
