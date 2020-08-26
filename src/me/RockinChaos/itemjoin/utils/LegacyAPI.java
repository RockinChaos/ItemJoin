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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;

/**
 * Welcome to the magical land of make-believe.
 * These are Deprecated Legacy Methods and/or non-functioning methods
 * that exist to support legacy versions of Minecraft.
 * 
 */
@SuppressWarnings("deprecation")
public class LegacyAPI {
	
	private static LegacyAPI legacy;
	
   /**
    * Updates the Players Inventory.
    * 
    * @param player - The Player to have their Inventory updated.
    */
    public void updateInventory(final Player player) {
    	player.updateInventory();
    }
    
   /**
    * Gets the ItemStack in the Players Hand.
    * 
    * @param player - The Player to have its ItemStack found.
    * @return The found ItemStack.
    */
    public ItemStack getInHandItem(final Player player) {
    	return player.getInventory().getItemInHand();
    }
    
   /**
    * Sets the ItemStack to the Players Hand.
    * 
    * @param player - The Player to have the ItemStack given.
    * @param item - The ItemStack to be set to the Players Hand.
    */
    public void setInHandItem(final Player player, final ItemStack item) {
    	player.setItemInHand(item);
    }
	
   /**
    * Creates a new ItemStack.
    * 
    * @param material - The Material to be set to the ItemStack.
    * @param count - The ItemStack size.
    * @param dataValue - The Data Value to set to the ItemStack.
    * @return The new ItemStack.
    */
    public ItemStack newItemStack(final Material material, final int count, final short dataValue) {
    	return new ItemStack(material, count, dataValue);
    }
    
   /**
    * Creates a new ShapedRecipe.
    * 
    * @param item - The ItemStack to be crafted.
    * @return The new ShapedRecipe.
    */
    public ShapedRecipe newShapedRecipe(final ItemStack item) {
    	return new ShapedRecipe(item);
    }

   /**
    * Matches the Material from its Bukkit Material and Data Value.
    * 
    * @param typeID - The ID of the Material to be fetched.
    * @param dataValue - The Data value to be matched.
    * @return The found Bukkit Material.
    */
    public org.bukkit.Material getMaterial(final int typeID, final byte dataValue) {
		return ItemJoin.getInstance().getServer().getUnsafe().fromLegacy(new org.bukkit.material.MaterialData(this.findMaterial(typeID), dataValue));
    }
    
   /**
    * Matches the Material from its Bukkit Material and Data Value.
    * 
    * @param material - The Material to be matched.
    * @param dataValue - The Data value to be matched.
    * @return The found Bukkit Material.
    */
    public org.bukkit.Material getMaterial(final Material material, final byte dataValue) {
  		return ItemJoin.getInstance().getServer().getUnsafe().fromLegacy(new org.bukkit.material.MaterialData(material, dataValue));
    }
    
   /**
    * Gets the Material from its corresponding ID.
    * 
    * @param typeID - The ID of the Material to be fetched.
    * @return The found Bukkit Material.
    */
    public org.bukkit.Material findMaterial(final int typeID) {
        final Material[] foundMaterial = new Material[1];
        EnumSet.allOf(Material.class).forEach(material -> { try { if (Utils.getUtils().containsIgnoreCase(material.toString(), "LEGACY_") && material.getId() == typeID || !ServerHandler.getServer().hasSpecificUpdate("1_13") && material.getId() == typeID) { foundMaterial[0] = material; } } catch (Exception e) { }});
        return foundMaterial[0];
    }

   /**
    * Gets the ID of the specified Material.
    * 
    * @param material - The Material to have its ID fetched.
    * @return The ID of the Material.
    */
	public int getMaterialID(final Material material) {
	    return material.getId();
	}
	
   /**
    * Gets the Durability from the ItemStack.
    * 
    * @param item - The ItemStack to have its durability fetched.
    * @return The Durability of the ItemStack.
    */
	public short getDurability(final ItemStack item) {
		return item.getDurability();
	}
	
   /**
    * Sets the Durability to the ItemStack.
    * 
    * @param item - The ItemStack to have its Durability set.
    * @param durability - The Durability to be set to the ItemStack.
    * @return the newly set Durability on the ItemStack.
    */
	public ItemStack setDurability(final ItemStack item, final short durability) {
		item.setDurability(durability);
		return item;
	}
	
   /**
    * Gets the Enchantments String name.
    * 
    * @param enchant - The Enchantment to have its String name fetched.
    * @return The Enchantments String name.
    */
	public String getEnchantName(final org.bukkit.enchantments.Enchantment enchant) {
		return enchant.getName();
	}
	
   /**
    * Gets the Enchantment from its String name.
    * 
    * @param name - The String name of the Enchantment.
    * @return The found Enchantment.
    */
	public org.bukkit.enchantments.Enchantment getEnchant(final String name) {
		return org.bukkit.enchantments.Enchantment.getByName(name.toUpperCase());
	}
	
   /**
    * Gets the current Skull Owner of the SkullMeta.
    * 
    * @param skullMeta - The SkullMeta to have its owner fetched.
    * @return The found Skull Owner.
    */
    public String getSkullOwner(final org.bukkit.inventory.meta.SkullMeta skullMeta) {
    	return skullMeta.getOwner();
    }
	
   /**
    * Sets the owner to the SkullMeta.
    * 
    * @param skullMeta - The SkullMeta to have its owner set.
    * @param owner - The owner to be set to the SkullMeta.
    * @return The newly set SkullMeta.
    */
	public org.bukkit.inventory.meta.ItemMeta setSkullOwner(final org.bukkit.inventory.meta.SkullMeta skullMeta, final String owner) {
		skullMeta.setOwner(owner);
		return skullMeta;
	}
	
   /**
    * Gets the Bukkit Player from their String name.
    * 
    * @param playerName - The String name of the Bukkit Player.
    * @return The found Player.
    */
	public Player getPlayer(final String playerName) {
		return Bukkit.getPlayer(playerName);
	}
	
   /**
    * Gets the Bukkit OfflinePlayer from their String name.
    * 
    * @param playerName - The String name of the Bukkit OfflinePlayer.
    * @return The found OfflinePlayer.
    */
	public OfflinePlayer getOfflinePlayer(final String playerName) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		return player.hasPlayedBefore() ? Bukkit.getOfflinePlayer(playerName) : null;
	}
	
   /**
    * Sets the Map ID to the MapMeta.
    * 
    * @param meta - The MapMeta to have its Map ID set.
    * @param mapId - The Map ID to be set to the item.
    */
    public org.bukkit.inventory.meta.MapMeta setMapID(final org.bukkit.inventory.meta.MapMeta meta, final int mapId) {
    	org.bukkit.inventory.meta.MapMeta mapmeta = meta;
    	mapmeta.setMapId(mapId);
    	return mapmeta; 
    }
	
   /**
    * Gets the ID from the MapView.
    * 
    * @param view - The MapView to have its ID fetched.
    * @return The ID of the MapView.
    */
    public short getMapID(final org.bukkit.map.MapView view) {
    	try { 
    		return (short) view.getId();
    	} catch (Exception | NoSuchMethodError e) { 			
			try { 
				return (short) Reflection.getReflection().getBukkitClass("map.MapView").getMethod("getId").invoke(view);
			} catch (Exception | NoSuchMethodError e2) { return 1; }
		}
    }
    
   /**
    * Gets the MapView from the specified ID.
    * 
    * @param id - The ID of the MapView to be fetched.
    * @return The Fetched MapView.
    */
    public org.bukkit.map.MapView getMapView(final int id) {
    	try { 
    		return ItemJoin.getInstance().getServer().getMap((short) id); 
    	} catch (Exception | NoSuchMethodError e) { 
			try {
				return (org.bukkit.map.MapView)Reflection.getReflection().getBukkitClass("Bukkit").getMethod("getMap", short.class).invoke(Reflection.getReflection().getBukkitClass("map.MapView"), (short)id);
			} catch (Exception | NoSuchMethodError e2) { return null; }
		}
    }
    
   /**
    * Creates a MapView for the main Server World.
    * 
    * @return The new MapView.
    */
    public org.bukkit.map.MapView createMapView() {
    	try {
    		return ItemJoin.getInstance().getServer().createMap(ItemJoin.getInstance().getServer().getWorlds().get(0));
    	} catch (Exception | NoSuchMethodError e) { return null; }
    }
    
   /**
    * Sets the Legacy Book Pages to the ItemStack.
    * 
    * @param player - The Player being used for placeholders.
    * @param meta - The ItemMeta to be updated.
    * @param pages - The book pages to be set.
    * @param itemMap - The ItemMap having their book pages set.
    * @warn Only to be used on server versions below 1.8.
    * @return The updated ItemMeta.
    */
	public ItemMeta setBookPages(final Player player, final ItemMeta meta, final List<String> pages, final ItemMap itemMap) {
		return this.setPages(player, meta, pages, itemMap);
	}
    
   /**
    * Sets the Legacy Book Pages to the ItemStack.
    * 
    * @param player - The Player being used for placeholders.
    * @param meta - The ItemMeta to be updated.
    * @param pages - The book pages to be set.
    * @param itemMap - The ItemMap having their book pages set.
    * @deprecated Only to be used on server versions below 1.8.
    * @return The updated ItemMeta.
    */
	public ItemMeta setPages(final Player player, final ItemMeta meta, final List<String> pages, final ItemMap itemMap) {
		if (!ServerHandler.getServer().hasSpecificUpdate("1_8") && pages != null && !pages.isEmpty()) {
			List<String> copyPages = new ArrayList<String>();
			for (String page: pages) { copyPages.add(page); }
			copyPages.set(0, ItemHandler.getItem().cutDelay(copyPages.get(0)));
			List < String > bookList = new ArrayList < String > ();
			for (int k = 0; k < pages.size(); k++) {
				bookList.add(Utils.getUtils().translateLayout(pages.get(k), player));
			}
			((BookMeta) meta).setPages(bookList);
			itemMap.setPages(bookList);
			return meta;
		}
		return meta;
	}
	
   /**
    * Sets the ItemStack as glowing.
    * 
    * @param tempItem - The ItemStack to be updated.
    * @param itemMap - The ItemMap having their book pages set.
    */
	public void setGlowing(final ItemStack tempItem, final ItemMap itemMap) {
		itemMap.setTempItem(this.setGlowEnchant(tempItem, itemMap));
	}
	
   /**
    * Sets the armor value to the items attributes.
    * 
    * @param tempItem - The ItemStack to be updated.
    * @param itemMap - The ItemMap having their armor value set.
    */
	public void setAttributes(final ItemStack tempItem, final ItemMap itemMap) {
		if (!ServerHandler.getServer().hasSpecificUpdate("1_13") && itemMap.getAttributes() != null && !itemMap.getAttributes().isEmpty()) {
			try {
				String slot = ItemHandler.getItem().getDesignatedSlot(itemMap.getMaterial());
				Class < ? > craftItemStack = Reflection.getReflection().getCraftBukkitClass("inventory.CraftItemStack");
				Object nms = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, tempItem);
				Object tag = Reflection.getReflection().getMinecraftClass("ItemStack").getMethod("getTag").invoke(nms);
				Object modifiers = Reflection.getReflection().getMinecraftClass("NBTTagList").getConstructor().newInstance();
				if (tag == null) { tag = Reflection.getReflection().getMinecraftClass("NBTTagCompound").getConstructor().newInstance(); }
				for (String attribute: itemMap.getAttributes().keySet()) {
					int uuid = new BigInteger((itemMap.getConfigName() + attribute).getBytes()).intValue();
					Object attrib = Reflection.getReflection().getMinecraftClass("NBTTagCompound").getConstructor().newInstance();
					double value = itemMap.getAttributes().get(attribute);
					String name = attribute.toLowerCase().replaceFirst("_", ".");
					if (name.contains("_")) { String[] nameSplit = name.split("_"); name = nameSplit[0]; nameSplit[0] = ""; for (String rename: nameSplit) { name += StringUtils.capitalize(rename); } }
					attrib.getClass().getMethod("setString", String.class, String.class).invoke(attrib, "AttributeName", name);
					attrib.getClass().getMethod("setString", String.class, String.class).invoke(attrib, "Name", name);
					attrib.getClass().getMethod("setString", String.class, String.class).invoke(attrib, "Slot", slot);
					attrib.getClass().getMethod("setDouble", String.class, double.class).invoke(attrib, "Amount", value);
					attrib.getClass().getMethod("setInt", String.class, int.class).invoke(attrib, "Operation", 0);
					attrib.getClass().getMethod("setInt", String.class, int.class).invoke(attrib, "UUIDLeast", uuid);
					attrib.getClass().getMethod("setInt", String.class, int.class).invoke(attrib, "UUIDMost", (uuid / 2));
					modifiers.getClass().getMethod("add", Reflection.getReflection().getMinecraftClass("NBTBase")).invoke(modifiers, attrib);
				}
				tag.getClass().getMethod("set", String.class, Reflection.getReflection().getMinecraftClass("NBTBase")).invoke(tag, "AttributeModifiers", modifiers);
				itemMap.setTempItem((ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms));
			} catch (Exception e) {
				ServerHandler.getServer().sendDebugTrace(e);
			}
		}
	}
	
   /**
    * Sets the ItemStack as glowing.
    * 
    * @param tempItem - The ItemStack to be updated.
    * @param itemMap - The ItemMap having their book pages set.
    * @deprecated Only to be used on server versions below 1.8.
    * @return The updated ItemStack.
    */
	private ItemStack setGlowEnchant(final ItemStack tempItem, final ItemMap itemMap) {
		if (itemMap.isGlowing() && !ServerHandler.getServer().hasSpecificUpdate("1_11")) {
			try {
				Class <?> craftItemStack = Reflection.getReflection().getCraftBukkitClass("inventory.CraftItemStack");
				Object nms = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, tempItem);
				Object tag = Reflection.getReflection().getMinecraftClass("ItemStack").getMethod("getTag").invoke(nms);
				if (tag == null) { tag = Reflection.getReflection().getMinecraftClass("NBTTagCompound").getConstructor().newInstance(); }
				Object ench = Reflection.getReflection().getMinecraftClass("NBTTagList").getConstructor().newInstance();
				tag.getClass().getMethod("set", String.class, Reflection.getReflection().getMinecraftClass("NBTBase")).invoke(tag, "ench", ench);
				nms.getClass().getMethod("setTag", tag.getClass()).invoke(nms, tag);
				return (((ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms)));
			} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
		}
		return tempItem;
	}
	
   /**
    * Checks if the Sk89q Plugins are the Legacy version.
    * 
    * @return If the plugins are Legacy.
    */
	public boolean legacySk89q() {
		try {
			if (Class.forName("com.sk89q.worldedit.Vector") != null) { 
				return true; 
			}
		} catch (Exception e) { }
		return false;
	}
	
   /**
    * Gets the Data Value from the ItemStack.
    * 
    * @param item - The ItemStack to have its Data Value fetched.
    */
    public int getDataValue(final ItemStack item) {
    	return item.getData().getData();
    }
    
   /**
    * Gets the Data Value for the corresponding Material.
    * 
    * @param material - The Material to have its data value fetched.
    * @return The Data Value.
    */
	public int getDataValue(final Material material) {
		if (material == Material.STONE) { return 6; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "DIRT")) { return 2; }
		else if (material.toString().equalsIgnoreCase("WOOD")) { return 5; }
		else if (material.toString().equalsIgnoreCase("LOG")) { return 3; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "SAPLING")) { return 5; }
		else if (material.toString().equalsIgnoreCase("SAND")) { return 1; }
		else if (material.toString().equalsIgnoreCase("LEAVES")) { return 3; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "SPONGE")) { return 1; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "SANDSTONE") && !Utils.getUtils().containsIgnoreCase(material.toString(), "STAIRS")) { return 2; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "LONG_GRASS")) { return 2; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "RED_ROSE")) { return 8; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "WOOD_STEP")) { return 5; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "STEP")) { return 7; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "STAINED_GLASS")) { return 15; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "MONSTER_EGGS")) { return 5; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "SMOOTH_BRICK")) { return 3; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "COBBLE_WALL")) { return 1; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "QUARTZ_BLOCK")) { return 2; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "STAINED_CLAY")) { return 15; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "LOG_2")) { return 1; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "LEAVES_2")) { return 1; }
		else if (material.toString().equalsIgnoreCase("PRISMARINE")) { return 2; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "CARPET")) { return 15; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "DOUBLE_PLANT")) { return 5; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "RED_SANDSTONE")) { return 2; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "GOLDEN_APPLE")) { return 1; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "RAW_FISH")) { return 3; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "COOKED_FISHED")) { return 1; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "INK_SAC")) { return 15; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "SKULL_ITEM") && ServerHandler.getServer().hasSpecificUpdate("1_9")) { return 5; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "SKULL_ITEM")) { return 4; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "CONCRETE")) { return 15; }
		else if (Utils.getUtils().containsIgnoreCase(material.toString(), "WOOL")) { return 15; }
		return 0;
	}
    
   /**
    * Registers the Legacy Pickups Listener.
    * Only called when the Server version is below 1.12.
    * 
    */
	public void registerPickups() {
		if (!Utils.getUtils().isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Pickups.class.getSimpleName())) { 
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Pickups(), ItemJoin.getInstance()); 
		}
	}
	
   /**
    * Registers the Legacy Stackable Listener.
    * Only called when the Server version is below 1.12.
    * 
    */
	public void registerStackable() {
		if (!ServerHandler.getServer().hasSpecificUpdate("1_12") && !Utils.getUtils().isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Stackable.class.getSimpleName())) { 
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Stackable(), ItemJoin.getInstance()); 
		}
	}
	
   /**
    * Registers the Legacy Interact Listener.
    * Only called when the Server version is below 1.8.
    * 
    */
	public void registerCommands() {
		if (!ServerHandler.getServer().hasSpecificUpdate("1_8") && !Utils.getUtils().isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Commands.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Commands(), ItemJoin.getInstance());
		}
	}
	
   /**
    * Registers the Legacy Consumes Listener.
    * Only called when the Server version is below 1.11.
    * 
    */
	public void registerConsumes() {
		if (!ServerHandler.getServer().hasSpecificUpdate("1_11") && !Utils.getUtils().isRegistered( me.RockinChaos.itemjoin.listeners.legacy.Legacy_Consumes.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new  me.RockinChaos.itemjoin.listeners.legacy.Legacy_Consumes(), ItemJoin.getInstance());
		}
	}
	
   /**
    * Registers the Legacy Storable Listener.
    * Only called when the Server version is below 1.8.
    * 
    */
	public void registerStorable() {
		if (!ServerHandler.getServer().hasSpecificUpdate("1_8") && !Utils.getUtils().isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Storable.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Storable(), ItemJoin.getInstance());
		}
	}
    
   /**
    * Gets the instance of the LegacyAPI.
    * 
    * @return The LegacyAPI instance.
    */
    public static LegacyAPI getLegacy() { 
        if (legacy == null) { legacy = new LegacyAPI(); }
        return legacy; 
    }
}