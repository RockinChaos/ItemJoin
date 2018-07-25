package me.RockinChaos.itemjoin.utils;

import java.util.EnumSet;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import me.RockinChaos.itemjoin.ItemJoin;

@SuppressWarnings("deprecation")
public class Legacy {
	
	public static Material getLegacyMaterial(int id) {
		Material[] idFromList = Material.values();
		return idFromList[id];
	}
	
	public static Material convertLegacyMaterial(int ID, byte Data) {
	    for (Material i : EnumSet.allOf(Material.class)) { if (i.getId() == ID) { return ItemJoin.getInstance().getServer().getUnsafe().fromLegacy(new MaterialData(i, Data)); } }
	    return null;
	}

}
