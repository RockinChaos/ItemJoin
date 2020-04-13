/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.utils;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;

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
    
	public Map<String, ProtectedRegion> getRegions(World world) {
		if (ConfigHandler.getDepends().getGuard().guardVersion() >= 700) {
			com.sk89q.worldedit.world.World wgWorld;
			try { wgWorld = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getWorldByName(world.getName()); }
			catch (NoSuchMethodError e) { wgWorld = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(world.getName()); }
			com.sk89q.worldguard.protection.regions.RegionContainer rm = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			if (rm == null) { return null; }
			if (Legacy.hasLegacyWorldEdit()) {
				return rm.get(wgWorld).getRegions();
			} else { return rm.get(wgWorld).getRegions(); }
		} else { return Legacy.getLegacyRegions(world); }
	}
	
    private void setGuardStatus(boolean bool) {
    	if (bool) { this.enableGuard(); }
    	this.isEnabled = bool;
    }
}