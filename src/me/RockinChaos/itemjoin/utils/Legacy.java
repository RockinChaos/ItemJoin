package me.RockinChaos.itemjoin.utils;

import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.material.MaterialData;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import me.RockinChaos.itemjoin.ItemJoin;

@SuppressWarnings("deprecation")
public class Legacy {
	
    public static org.bukkit.Material findLegacyMaterial(int typeId) {

        final Material[] foundMaterial = new Material[1];

        for (Material material: EnumSet.allOf(Material.class)) { // Add a way to convert data-value data to a new material type.
            if (material.getId() == typeId) {
                foundMaterial[0] = material;
                return material;
            }
        }
		return null;
}
	
	public static Material convertLegacyMaterial(int ID, byte Data) {
	    for (Material i : EnumSet.allOf(Material.class)) { if (i.getId() == ID) { return ItemJoin.getInstance().getServer().getUnsafe().fromLegacy(new MaterialData(i, Data)); } }
	    return null;
	}
	
	public static ApplicableRegionSet getLegacyRegionSet(World world, Location loc) {
        com.sk89q.worldguard.bukkit.WorldGuardPlugin wg = (com.sk89q.worldguard.bukkit.WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        com.sk89q.worldguard.bukkit.RegionContainer rm = wg.getRegionContainer();
        com.sk89q.worldedit.Vector wgVector = new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ());
        return rm.get(world).getApplicableRegions(wgVector);
	}

}
