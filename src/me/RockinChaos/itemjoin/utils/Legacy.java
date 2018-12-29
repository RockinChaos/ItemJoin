package me.RockinChaos.itemjoin.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.material.MaterialData;

import com.sk89q.worldguard.protection.ApplicableRegionSet;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

@SuppressWarnings("deprecation")
public class Legacy {
	
	// WELCOME TO THE LAND OF MAKE-BELIEVE! //
	
    public static org.bukkit.Material findLegacyMaterial(int typeId) {
        Material[] foundMaterial = new Material[1];
        for (Material material: EnumSet.allOf(Material.class)) { // Add a way to convert data-value data to a new material type.
            if (material.getId() == typeId) {
                foundMaterial[0] = material;
                return material;
            }
        }
		return null;
    }
    
    public static void updateLegacyInventory(Player player) {
    	player.updateInventory();
    }
    
    public static void setLegacyInHandItem(Player player, ItemStack item) {
    	player.setItemInHand(item);
    }
    
    public static ItemStack getLegacyInHandItem(Player player) {
    	return player.getInventory().getItemInHand();
    }
    
    public static String getLegacySkullOwner(SkullMeta skullMeta) {
    	return skullMeta.getOwner();
    }
    
    public static MapView getMapView(int id) {
		return ItemJoin.getInstance().getServer().getMap((short) id);
    }
    
    public static MapView createLegacyMapView() {
    	try {
    		for (World world : Bukkit.getServer().getWorlds()) {
    			if (world != null) {
    				return ItemJoin.getInstance().getServer().createMap(world);
    			}
    		}
    	} catch (Exception e) { }
    	return null;
    }
    
	public static Class<?> getMapNMS(String name) {
		try {
			return Class.forName("org.bukkit.map." + name);
		} catch (Exception e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return null;
	}
    
    public static short getMapID(MapView view) {
		try { return (short) view.getId(); } 
		catch (NoSuchMethodError e) { return Reflection.getMapID(view); }
    }
    
    public static ItemStack newLegacyItemStack(Material material, int count, short dataValue) {
    	return new ItemStack(material, count, dataValue);
    }
	
	public static Material convertLegacyMaterial(int ID, byte Data) {
	    for (Material i : EnumSet.allOf(Material.class)) { if (i.getId() == ID) { return ItemJoin.getInstance().getServer().getUnsafe().fromLegacy(new MaterialData(i, Data)); } }
	    return null;
	}
	
	public static int getLegacyMaterialID(Material mat) {
	    return mat.getId();
	}
	
	public static ItemStack setLegacyDurability(ItemStack item, short durability) {
		item.setDurability(durability);
		return item;
	}
	
	public static short getLegacyDurability(ItemStack item) {
		return item.getDurability();
	}
	
	public static String getLegacyEnchantName(Enchantment e) {
		return e.getName();
	}
	
	public static Enchantment getLegacyEnchantByName(String name) {
		return Enchantment.getByName(name.toUpperCase());
	}
	
	public static ItemMeta setLegacySkullOwner(SkullMeta skullMeta, String owner) {
		skullMeta.setOwner(owner);
		return skullMeta;
	}
	
	public static Player getLegacyPlayer(String playerName) {
		return Bukkit.getPlayer(playerName);
	}
	
	public static ApplicableRegionSet getLegacyRegionSet(World world, Location loc) {
        com.sk89q.worldguard.bukkit.WorldGuardPlugin wg = (com.sk89q.worldguard.bukkit.WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        com.sk89q.worldguard.bukkit.RegionContainer rm = wg.getRegionContainer();
        if (hasLegacyWorldEdit()) {
        	com.sk89q.worldedit.Vector wgVector = new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ());
        	return rm.get(world).getApplicableRegions(wgVector);
        } else { return rm.get(world).getApplicableRegions(loc); }
	}
	
	public static boolean hasLegacyWorldEdit() {
		try {
			Class<?> wEdit = Class.forName("com.sk89q.worldedit.Vector");
			if (wEdit != null) { return true; }
			return false;
		} catch (Exception e) { return false; }
	}
	
    public static com.sk89q.worldedit.math.BlockVector3 asBlockVector(org.bukkit.Location location) {
        checkNotNull(location); // Is this really needed?
        return com.sk89q.worldedit.math.BlockVector3.at(location.getX(), location.getY(), location.getZ());
    }
}