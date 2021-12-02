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
package me.RockinChaos.itemjoin.utils.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.utils.ReflectionUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.ReflectionUtils.MinecraftMethod;

/**
 * Welcome to the magical land of make-believe.
 * These are Deprecated Legacy Methods and/or non-functioning methods
 * that exist to support legacy versions of Minecraft.
 * 
 */
@SuppressWarnings("deprecation")
public class LegacyAPI {
	
	private static boolean legacyMaterial = false;
	
   /**
    * Updates the Players Inventory.
    * 
    * @param player - The Player to have their Inventory updated.
    */
    public static void updateInventory(final Player player) {
    	player.updateInventory();
    }
    
   /**
    * Gets the ItemStack in the Players Hand.
    * 
    * @param player - The Player to have its ItemStack found.
    * @return The found ItemStack.
    */
    public static ItemStack getInHandItem(final Player player) {
    	return player.getInventory().getItemInHand();
    }
    
   /**
    * Sets the ItemStack to the Players Hand.
    * 
    * @param player - The Player to have the ItemStack given.
    * @param item - The ItemStack to be set to the Players Hand.
    */
    public static void setInHandItem(final Player player, final ItemStack item) {
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
    public static ItemStack newItemStack(final Material material, final int count, final short dataValue) {
    	return new ItemStack(material, count, dataValue);
    }
    
   /**
    * Gets the GameRule for the World.
    * 
    * @param world - The world being referenced.
    * @param gamerule - The gamerule to locate.
    * @return The boolean value fo the gamerule.
    */
    public static boolean getGameRule(final World world, final String gamerule) {
    	String value = world.getGameRuleValue(gamerule);
    	return (value != null && !value.isEmpty() ? Boolean.valueOf(value) : false);
    }
    
   /**
    * Creates a new ShapedRecipe.
    * 
    * @param item - The ItemStack to be crafted.
    * @return The new ShapedRecipe.
    */
    public static ShapedRecipe newShapedRecipe(final ItemStack item) {
    	return new ShapedRecipe(item);
    }

   /**
    * Matches the Material from its Bukkit Material and Data Value.
    * 
    * @param typeID - The ID of the Material to be fetched.
    * @param dataValue - The Data value to be matched.
    * @return The found Bukkit Material.
    */
    public static org.bukkit.Material getMaterial(final int typeID, final byte dataValue) {
        initializeLegacy();
		return ItemJoin.getInstance().getServer().getUnsafe().fromLegacy(new org.bukkit.material.MaterialData(findMaterial(typeID), dataValue));
    }
    
   /**
    * Matches the Material from its Bukkit Material and Data Value.
    * 
    * @param material - The Material to be matched.
    * @param dataValue - The Data value to be matched.
    * @return The found Bukkit Material.
    */
    public static org.bukkit.Material getMaterial(final Material material, final byte dataValue) {
        initializeLegacy();
  		return ItemJoin.getInstance().getServer().getUnsafe().fromLegacy(new org.bukkit.material.MaterialData(material, dataValue));
    }
    
   /**
    * Gets the Material from its corresponding ID.
    * 
    * @param typeID - The ID of the Material to be fetched.
    * @return The found Bukkit Material.
    */
    public static org.bukkit.Material findMaterial(final int typeID) {
        final Material[] foundMaterial = new Material[1];
        EnumSet.allOf(Material.class).forEach(material -> { 
        	try { 
        		if (StringUtils.containsIgnoreCase(material.toString(), "LEGACY_") && material.getId() == typeID || !ServerUtils.hasSpecificUpdate("1_13") && material.getId() == typeID) {
        			try { 
        				initializeLegacy();
        			} catch (Exception e) { e.printStackTrace(); }
        			foundMaterial[0] = material; 
        		} 
        	} catch (Exception e) { }});
        return foundMaterial[0];
    }
    
   /**
    * Sends a info/debug message if the server is running Minecraft 1.13+ and is attempting to call a Legacy material.
    * 
    */
    private static void initializeLegacy() {
		if (ServerUtils.hasSpecificUpdate("1_13") && !legacyMaterial) {
			legacyMaterial = true;
			ServerUtils.logInfo("Initializing Legacy Material Support ..."); 
			ServerUtils.logDebug("Your items.yml has one or more item(s) containing a numerical id and/or data values."); 
			ServerUtils.logDebug("Minecraft 1.13 removed the use of these values, please change your items ids to reflect this change.");
			ServerUtils.logDebug("Your custom items will continue to function but the id set may not appear as expected.");
			ServerUtils.logDebug("If you believe this is a bug, please report it to the developer!"); 
			try {
				throw new Exception("Invalid usage of item id, this is not a bug!");
			} catch (Exception e) {
				ServerUtils.sendDebugTrace(e);
			}
		}
    }

   /**
    * Gets the ID of the specified Material.
    * 
    * @param material - The Material to have its ID fetched.
    * @return The ID of the Material.
    */
	public static int getMaterialID(final Material material) {
	    return material.getId();
	}
	
   /**
    * Gets the Durability from the ItemStack.
    * 
    * @param item - The ItemStack to have its durability fetched.
    * @return The Durability of the ItemStack.
    */
	public static short getDurability(final ItemStack item) {
		return item.getDurability();
	}
	
   /**
    * Sets the Durability to the ItemStack.
    * 
    * @param item - The ItemStack to have its Durability set.
    * @param durability - The Durability to be set to the ItemStack.
    * @return the newly set Durability on the ItemStack.
    */
	public static ItemStack setDurability(final ItemStack item, final short durability) {
		item.setDurability(durability);
		return item;
	}
	
   /**
    * Gets the Enchantments String name.
    * 
    * @param enchant - The Enchantment to have its String name fetched.
    * @return The Enchantments String name.
    */
	public static String getEnchantName(final org.bukkit.enchantments.Enchantment enchant) {
		return enchant.getName();
	}
	
   /**
    * Gets the Enchantment from its String name.
    * 
    * @param name - The String name of the Enchantment.
    * @return The found Enchantment.
    */
	public static org.bukkit.enchantments.Enchantment getEnchant(final String name) {
		return org.bukkit.enchantments.Enchantment.getByName(name.toUpperCase());
	}
	
   /**
    * Gets the current Skull Owner of the SkullMeta.
    * 
    * @param skullMeta - The SkullMeta to have its owner fetched.
    * @return The found Skull Owner.
    */
    public static String getSkullOwner(final org.bukkit.inventory.meta.SkullMeta skullMeta) {
    	return skullMeta.getOwner();
    }
	
   /**
    * Sets the owner to the SkullMeta.
    * 
    * @param skullMeta - The SkullMeta to have its owner set.
    * @param owner - The owner to be set to the SkullMeta.
    * @return The newly set SkullMeta.
    */
	public static org.bukkit.inventory.meta.ItemMeta setSkullOwner(final org.bukkit.inventory.meta.SkullMeta skullMeta, final String owner) {
		skullMeta.setOwner(owner);
		if (!ServerUtils.hasSpecificUpdate("1_13") && ServerUtils.hasSpecificUpdate("1_8")) {
			final Location loc = new Location(Bukkit.getWorlds().get(0), 200, 1, 200);
			final BlockState blockState = loc.getBlock().getState();
			try {
				loc.getBlock().setType(Material.valueOf("SKULL"));
				Skull skull = (Skull)loc.getBlock().getState();
				skull.setSkullType(SkullType.PLAYER);
				skull.setOwner(owner);
				skull.update();
				final String texture = ItemHandler.getSkullTexture(skull);
				if (texture != null && !texture.isEmpty()) {
					ItemHandler.setSkullTexture(skullMeta, texture);
				}
			} catch (Exception e) { }
			blockState.update(true);
		}
		return skullMeta;
	}
	
   /**
    * Gets the Bukkit Player from their String name.
    * 
    * @param playerName - The String name of the Bukkit Player.
    * @return The found Player.
    */
	public static Player getPlayer(final String playerName) {
		return Bukkit.getPlayer(playerName);
	}
	
   /**
    * Gets the Bukkit OfflinePlayer from their String name.
    * 
    * @param playerName - The String name of the Bukkit OfflinePlayer.
    * @return The found OfflinePlayer.
    */
	public static OfflinePlayer getOfflinePlayer(final String playerName) {
		return Bukkit.getOfflinePlayer(playerName);
	}
	
   /**
    * Sets the Map ID to the MapMeta.
    * 
    * @param meta - The MapMeta to have its Map ID set.
    * @param mapId - The Map ID to be set to the item.
    */
    public static org.bukkit.inventory.meta.MapMeta setMapID(final org.bukkit.inventory.meta.MapMeta meta, final int mapId) {
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
    public static short getMapID(final org.bukkit.map.MapView view) {
    	try { 
    		return (short) view.getId();
    	} catch (Exception | NoSuchMethodError e) { 			
			try { 
				return (short) ReflectionUtils.getBukkitClass("map.MapView").getMethod("getId").invoke(view);
			} catch (Exception | NoSuchMethodError e2) { return 1; }
		}
    }
    
   /**
    * Gets the MapView from the specified ID.
    * 
    * @param id - The ID of the MapView to be fetched.
    * @return The Fetched MapView.
    */
    public static org.bukkit.map.MapView getMapView(final int id) {
    	try { 
    		return ItemJoin.getInstance().getServer().getMap((short) id); 
    	} catch (Exception | NoSuchMethodError e) { 
			try {
				return (org.bukkit.map.MapView)ReflectionUtils.getBukkitClass("Bukkit").getMethod("getMap", short.class).invoke(ReflectionUtils.getBukkitClass("map.MapView"), (short)id);
			} catch (Exception | NoSuchMethodError e2) { return null; }
		}
    }
    
   /**
    * Creates a MapView for the main Server World.
    * 
    * @return The new MapView.
    */
    public static org.bukkit.map.MapView createMapView() {
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
	public static ItemMeta setBookPages(final Player player, final ItemMeta meta, final List<String> pages, final ItemMap itemMap) {
		return setPages(player, meta, pages, itemMap);
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
	public static ItemMeta setPages(final Player player, final ItemMeta meta, final List<String> pages, final ItemMap itemMap) {
		if (!ServerUtils.hasSpecificUpdate("1_8") && pages != null && !pages.isEmpty()) {
			List<String> copyPages = new ArrayList<String>();
			for (String page: pages) { copyPages.add(page); }
			copyPages.set(0, ItemHandler.cutDelay(copyPages.get(0)));
			List < String > bookList = new ArrayList < String > ();
			for (int k = 0; k < pages.size(); k++) {
				bookList.add(StringUtils.translateLayout(pages.get(k), player));
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
	public static void setGlowing(final ItemStack tempItem, final ItemMap itemMap) {
		itemMap.setTempItem(setGlowEnchant(tempItem, itemMap));
	}
	
   /**
    * Sets the armor value to the items attributes.
    * 
    * @param tempItem - The ItemStack to be updated.
    * @param itemMap - The ItemMap having their armor value set.
    */
	public static void setAttributes(final ItemStack tempItem, final ItemMap itemMap) {
		if (!ServerUtils.hasSpecificUpdate("1_13") && itemMap.getAttributes() != null && !itemMap.getAttributes().isEmpty()) {
			try {
				String slot = null;
				if (ItemHandler.getDesignatedSlot(itemMap.getMaterial()).equalsIgnoreCase("noslot")) {
					slot = "HAND";
				} else {
					slot = ItemHandler.getDesignatedSlot(itemMap.getMaterial()).toUpperCase();
				}
				Class < ? > craftItemStack = ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack");
				Class < ? > itemClass = ReflectionUtils.getMinecraftClass("ItemStack");
				Class < ? > baseClass = ReflectionUtils.getMinecraftClass("NBTBase");
				Object nms = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, tempItem);
				Object tag = itemClass.getMethod(MinecraftMethod.getTag.getMethod(itemClass)).invoke(nms);
				Object modifiers = ReflectionUtils.getMinecraftClass("NBTTagList").getConstructor().newInstance();
				if (tag == null) { tag = ReflectionUtils.getMinecraftClass("NBTTagCompound").getConstructor().newInstance(); }
				for (String attribute: itemMap.getAttributes().keySet()) {
					int uuid = new BigInteger((itemMap.getConfigName() + attribute).getBytes()).intValue();
					Object attrib = ReflectionUtils.getMinecraftClass("NBTTagCompound").getConstructor().newInstance();
					double value = itemMap.getAttributes().get(attribute);
					String name = attribute.toLowerCase().replaceFirst("_", ".");
					if (name.contains("_")) { String[] nameSplit = name.split("_"); name = nameSplit[0]; nameSplit[0] = ""; for (String rename: nameSplit) { name += org.apache.commons.lang.StringUtils.capitalize(rename); } }
					attrib.getClass().getMethod(MinecraftMethod.setString.getMethod(attrib, String.class, String.class), String.class, String.class).invoke(attrib, "AttributeName", name);
					attrib.getClass().getMethod(MinecraftMethod.setString.getMethod(attrib, String.class, String.class), String.class, String.class).invoke(attrib, "Name", name);
					attrib.getClass().getMethod(MinecraftMethod.setString.getMethod(attrib, String.class, String.class), String.class, String.class).invoke(attrib, "Slot", slot);
					attrib.getClass().getMethod(MinecraftMethod.setDouble.getMethod(attrib, String.class, double.class), String.class, double.class).invoke(attrib, "Amount", value);
					attrib.getClass().getMethod(MinecraftMethod.setInt.getMethod(attrib, String.class, int.class), String.class, int.class).invoke(attrib, "Operation", 0);
					attrib.getClass().getMethod(MinecraftMethod.setInt.getMethod(attrib, String.class, int.class), String.class, int.class).invoke(attrib, "UUIDLeast", uuid);
					attrib.getClass().getMethod(MinecraftMethod.setInt.getMethod(attrib, String.class, int.class), String.class, int.class).invoke(attrib, "UUIDMost", (uuid / 2));
					modifiers.getClass().getMethod(MinecraftMethod.add.getMethod(modifiers, baseClass), baseClass).invoke(modifiers, attrib);
				}
				tag.getClass().getMethod(MinecraftMethod.set.getMethod(tag, String.class, baseClass), String.class,baseClass).invoke(tag, "AttributeModifiers", modifiers);
				itemMap.setTempItem((ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms));
			} catch (Exception e) {
				ServerUtils.sendDebugTrace(e);
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
	private static ItemStack setGlowEnchant(final ItemStack tempItem, final ItemMap itemMap) {
		if (itemMap.isGlowing() && !ServerUtils.hasSpecificUpdate("1_11")) {
			try {
				Class <?> craftItemStack = ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack");
				Class < ? > itemClass = ReflectionUtils.getMinecraftClass("ItemStack");
				Class < ? > baseClass = ReflectionUtils.getMinecraftClass("NBTBase");
				Object nms = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(null, tempItem);
				Object tag = itemClass.getMethod(MinecraftMethod.getTag.getMethod(itemClass)).invoke(nms);
				if (tag == null) { tag = ReflectionUtils.getMinecraftClass("NBTTagCompound").getConstructor().newInstance(); }
				Object ench = ReflectionUtils.getMinecraftClass("NBTTagList").getConstructor().newInstance();
				tag.getClass().getMethod(MinecraftMethod.set.getMethod(tag, String.class, baseClass), String.class, baseClass).invoke(tag, "ench", ench);
				nms.getClass().getMethod(MinecraftMethod.setTag.getMethod(nms, tag.getClass()), tag.getClass()).invoke(nms, tag);
				return (((ItemStack) craftItemStack.getMethod("asCraftMirror", nms.getClass()).invoke(null, nms)));
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		}
		return tempItem;
	}
	
   /**
    * Color Encodes a String so that it is completely hidden in color codes,
    * this will be invisible to a normal eye and will not display any text.
    * Only to be used on server versions below 1.13, will not function on 1.13+.
    * 
    * @param str - The String to be Color Encoded.
    * @return The Color Encoded String.
    */
	public static String colorEncode(final String str) {
		try {
			String hiddenData = "";
			for (char c: str.toCharArray()) {
				hiddenData += "§" + c;
			}
			return hiddenData;
		} catch (Exception e) {
			ServerUtils.sendDebugTrace(e);
			return null;
		}
	}

   /**
    * Decodes a Color Encoded String.
    * Only to be used on server versions below 1.13, will not function on 1.13+.
    * 
    * @param str - The String to be Color Decoded.
    * @return The Color Decoded String.
    */
	public static String colorDecode(final String str) {
		try {
			String[] hiddenData = str.split("(?:\\w{2,}|\\d[0-9A-Fa-f])+");
			String returnData = "";
			if (hiddenData == null) {
				hiddenData = str.split("§");
				for (int i = 0; i < hiddenData.length; i++) {
					returnData += hiddenData[i];
				}
				return returnData;
			} else {
				String[] d = hiddenData[hiddenData.length - 1].split("§");
				for (int i = 1; i < d.length; i++) {
					returnData += d[i];
				}
				return returnData;
			}
		} catch (Exception e) {
			ServerUtils.sendDebugTrace(e);
			return null;
		}
	}
	
   /**
    * Checks if the Sk89q Plugins are the Legacy version.
    * 
    * @return If the plugins are Legacy.
    */
	public static boolean legacySk89q() {
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
    public static int getDataValue(final ItemStack item) {
    	return item.getData().getData();
    }
    
   /**
    * Gets the Data Value for the corresponding Material.
    * 
    * @param material - The Material to have its data value fetched.
    * @return The Data Value.
    */
	public static int getDataValue(final Material material) {
		if (material == Material.STONE) { return 6; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "DIRT")) { return 2; }
		else if (material.toString().equalsIgnoreCase("WOOD")) { return 5; }
		else if (material.toString().equalsIgnoreCase("LOG")) { return 3; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "SAPLING")) { return 5; }
		else if (material.toString().equalsIgnoreCase("SAND")) { return 1; }
		else if (material.toString().equalsIgnoreCase("LEAVES")) { return 3; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "SPONGE")) { return 1; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "SANDSTONE") && !StringUtils.containsIgnoreCase(material.toString(), "STAIRS")) { return 2; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "LONG_GRASS")) { return 2; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "RED_ROSE")) { return 8; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "WOOD_STEP")) { return 5; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "STEP")) { return 7; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "STAINED_GLASS")) { return 15; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "MONSTER_EGGS")) { return 5; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "SMOOTH_BRICK")) { return 3; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "COBBLE_WALL")) { return 1; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "QUARTZ_BLOCK")) { return 2; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "STAINED_CLAY")) { return 15; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "LOG_2")) { return 1; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "LEAVES_2")) { return 1; }
		else if (material.toString().equalsIgnoreCase("PRISMARINE")) { return 2; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "CARPET")) { return 15; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "DOUBLE_PLANT")) { return 5; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "RED_SANDSTONE")) { return 2; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "GOLDEN_APPLE")) { return 1; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "RAW_FISH")) { return 3; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "COOKED_FISHED")) { return 1; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "INK_SAC")) { return 15; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "SKULL_ITEM") && ServerUtils.hasSpecificUpdate("1_9")) { return 5; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "SKULL_ITEM")) { return 4; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "CONCRETE")) { return 15; }
		else if (StringUtils.containsIgnoreCase(material.toString(), "WOOL")) { return 15; }
		return 0;
	}
    
   /**
    * Registers the Legacy Pickups Listener.
    * Only called when the Server version is below 1.12.
    * 
    */
	public static void registerPickups() {
		if (!StringUtils.isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Pickups.class.getSimpleName())) { 
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Pickups(), ItemJoin.getInstance()); 
		}
	}
	
   /**
    * Registers the Legacy Stackable Listener.
    * Only called when the Server version is below 1.12.
    * 
    */
	public static void registerStackable() {
		if (!ServerUtils.hasSpecificUpdate("1_12") && !StringUtils.isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Stackable.class.getSimpleName())) { 
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Stackable(), ItemJoin.getInstance()); 
		}
	}
	
   /**
    * Registers the Legacy Interact Listener.
    * Only called when the Server version is below 1.8.
    * 
    */
	public static void registerCommands() {
		if (!ServerUtils.hasSpecificUpdate("1_8") && !StringUtils.isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Commands.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Commands(), ItemJoin.getInstance());
		}
	}
	
   /**
    * Registers the Legacy Consumes Listener.
    * Only called when the Server version is below 1.11.
    * 
    */
	public static void registerConsumes() {
		if (!ServerUtils.hasSpecificUpdate("1_11") && !StringUtils.isRegistered( me.RockinChaos.itemjoin.listeners.legacy.Legacy_Consumes.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new  me.RockinChaos.itemjoin.listeners.legacy.Legacy_Consumes(), ItemJoin.getInstance());
		}
	}
	
   /**
    * Registers the Legacy Storable Listener.
    * Only called when the Server version is below 1.8.
    * 
    */
	public static void registerStorable() {
		if (!ServerUtils.hasSpecificUpdate("1_8") && !StringUtils.isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Storable.class.getSimpleName())) {
			ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Storable(), ItemJoin.getInstance());
		}
	}
}