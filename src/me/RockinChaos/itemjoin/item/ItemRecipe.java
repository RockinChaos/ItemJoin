package me.RockinChaos.itemjoin.item;

import org.bukkit.Material;

public class ItemRecipe {
	
	private String itemMap;
	private Material material;
	private byte dataValue;
	private int count;
	
	public ItemRecipe(final String itemMap, final Material material, final byte dataValue, final int count) {
		this.itemMap = itemMap;
		this.material = material;
		this.dataValue = dataValue;
		this.count = count;
	}
	
	public String getMap() {
		return this.itemMap;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	public byte getData() {
		return this.dataValue;
	}
	
	public int getCount() {
		return this.count;
	}
}