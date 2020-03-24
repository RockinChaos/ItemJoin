package me.RockinChaos.itemjoin.utils;

import java.util.EnumSet;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.material.MaterialData;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

@SuppressWarnings("deprecation")
public class Legacy {
	
	// WELCOME TO THE LAND OF MAKE-BELIEVE! //
    
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
    
    public static org.bukkit.inventory.meta.MapMeta setMapID(org.bukkit.inventory.meta.MapMeta meta, int mapId) {
    	MapMeta mapmeta = meta;
    	mapmeta.setMapId(mapId);
    	return mapmeta; 
    }
    
    public static int getDataValue(ItemStack item) {
    	return item.getData().getData();
    }
    
	public static int getDataValue(Material material) {
		if (material == Material.STONE) { return 6; }
		else if (Utils.containsIgnoreCase(material.toString(), "DIRT")) { return 2; }
		else if (material.toString().equalsIgnoreCase("WOOD")) { return 5; }
		else if (material.toString().equalsIgnoreCase("LOG")) { return 3; }
		else if (Utils.containsIgnoreCase(material.toString(), "SAPLING")) { return 5; }
		else if (material.toString().equalsIgnoreCase("SAND")) { return 1; }
		else if (material.toString().equalsIgnoreCase("LEAVES")) { return 3; }
		else if (Utils.containsIgnoreCase(material.toString(), "SPONGE")) { return 1; }
		else if (Utils.containsIgnoreCase(material.toString(), "SANDSTONE") && !Utils.containsIgnoreCase(material.toString(), "STAIRS")) { return 2; }
		else if (Utils.containsIgnoreCase(material.toString(), "LONG_GRASS")) { return 2; }
		else if (Utils.containsIgnoreCase(material.toString(), "RED_ROSE")) { return 8; }
		else if (Utils.containsIgnoreCase(material.toString(), "WOOD_STEP")) { return 5; }
		else if (Utils.containsIgnoreCase(material.toString(), "STEP")) { return 7; }
		else if (Utils.containsIgnoreCase(material.toString(), "STAINED_GLASS")) { return 15; }
		else if (Utils.containsIgnoreCase(material.toString(), "MONSTER_EGGS")) { return 5; }
		else if (Utils.containsIgnoreCase(material.toString(), "SMOOTH_BRICK")) { return 3; }
		else if (Utils.containsIgnoreCase(material.toString(), "COBBLE_WALL")) { return 1; }
		else if (Utils.containsIgnoreCase(material.toString(), "QUARTZ_BLOCK")) { return 2; }
		else if (Utils.containsIgnoreCase(material.toString(), "STAINED_CLAY")) { return 15; }
		else if (Utils.containsIgnoreCase(material.toString(), "LOG_2")) { return 1; }
		else if (Utils.containsIgnoreCase(material.toString(), "LEAVES_2")) { return 1; }
		else if (material.toString().equalsIgnoreCase("PRISMARINE")) { return 2; }
		else if (Utils.containsIgnoreCase(material.toString(), "CARPET")) { return 15; }
		else if (Utils.containsIgnoreCase(material.toString(), "DOUBLE_PLANT")) { return 5; }
		else if (Utils.containsIgnoreCase(material.toString(), "RED_SANDSTONE")) { return 2; }
		else if (Utils.containsIgnoreCase(material.toString(), "GOLDEN_APPLE")) { return 1; }
		else if (Utils.containsIgnoreCase(material.toString(), "RAW_FISH")) { return 3; }
		else if (Utils.containsIgnoreCase(material.toString(), "COOKED_FISHED")) { return 1; }
		else if (Utils.containsIgnoreCase(material.toString(), "INK_SAC")) { return 15; }
		else if (Utils.containsIgnoreCase(material.toString(), "SKULL_ITEM") && ServerHandler.hasCombatUpdate()) { return 5; }
		else if (Utils.containsIgnoreCase(material.toString(), "SKULL_ITEM")) { return 4; }
		else if (Utils.containsIgnoreCase(material.toString(), "CONCRETE")) { return 15; }
		else if (Utils.containsIgnoreCase(material.toString(), "WOOL")) { return 15; }
		return 0;
	}
	
    public static short getMapID(MapView view) {
    	try { return (short) view.getId(); } 
		catch (NoSuchMethodError e1) { 
	    	try {
	    		Object mapID = Reflection.getMinecraftClass("MapView").getMethod("getId").invoke(view);
	    		return (short)mapID; 
	    	} catch (Exception e2) { return 1; }
		}	
    }
    
    public static MapView getMapView(int id) {
    	try { return ItemJoin.getInstance().getServer().getMap((short) id); } 
		catch (NoSuchMethodError e1) { 
			try {
				Object mapView = Reflection.getBukkitClass("Bukkit").getMethod("getMap", short.class).invoke(Reflection.getBukkitClass("map.MapView"), (short)id);
				return (MapView)mapView; 
			} catch (Exception e2) { return null; }
		}
    }

    public static MapView createLegacyMapView() {
    	try {
    		return ItemJoin.getInstance().getServer().createMap(ItemJoin.getInstance().getServer().getWorlds().get(0));
    	} catch (Exception e) { }
    	return null;
    }
    
    public static ItemStack newLegacyItemStack(Material material, int count, short dataValue) {
    	return new ItemStack(material, count, dataValue);
    }
    
    public static org.bukkit.Material getLegacyMaterial(int typeID, byte dataValue) {
		return ItemJoin.getInstance().getServer().getUnsafe().fromLegacy(new MaterialData(findLegacyMaterial(typeID), dataValue));
    }
    
    public static org.bukkit.Material getLegacyMaterial(Material typeID, byte dataValue) {
  		return ItemJoin.getInstance().getServer().getUnsafe().fromLegacy(new MaterialData(typeID, dataValue));
    }
    
    public static org.bukkit.Material findLegacyMaterial(int typeID) {
        final Material[] foundMaterial = new Material[1];
        EnumSet.allOf(Material.class).forEach(material -> { try { if (Utils.containsIgnoreCase(material.toString(), "LEGACY_") && material.getId() == typeID || !ServerHandler.hasAquaticUpdate() && material.getId() == typeID) { foundMaterial[0] = material; } } catch (Exception e) { }});
        return foundMaterial[0];
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
	
	public static Map<String, ProtectedRegion> getLegacyRegions(World world) {
        com.sk89q.worldguard.bukkit.WorldGuardPlugin wg = (com.sk89q.worldguard.bukkit.WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        com.sk89q.worldguard.bukkit.RegionContainer rm = wg.getRegionContainer();
        if (hasLegacyWorldEdit()) {
        	return rm.get(world).getRegions();
        } else { return rm.get(world).getRegions(); }
	}
	
	public static boolean hasLegacyWorldEdit() {
		try {
			Class<?> wEdit = Class.forName("com.sk89q.worldedit.Vector");
			if (wEdit != null) { return true; }
			return false;
		} catch (Exception e) { return false; }
	}
	
    public static com.sk89q.worldedit.math.BlockVector3 asBlockVector(org.bukkit.Location location) {
        return com.sk89q.worldedit.math.BlockVector3.at(location.getX(), location.getY(), location.getZ());
    }
}