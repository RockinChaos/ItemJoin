package me.RockinChaos.itemjoin.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.inventory.meta.Damageable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.utils.Legacy;
import me.RockinChaos.itemjoin.utils.Reflection;
import me.RockinChaos.itemjoin.utils.Utils;

public class ItemHandler {
	
	private static HashMap < Integer, Integer > ArbitraryID = new HashMap < Integer, Integer > ();
	
	public static void initializeItemID() {
		ArbitraryID.clear();
	}
	
	public static String getItemID(String slot) {
		return slot + getArbitraryID(slot);
	}
	
	public static void clearItemID(Player player) {
		ArbitraryID.remove(1);
	}

	private static String getArbitraryID(String slot) {
		if (slot.equalsIgnoreCase("Arbitrary")) {
			if (ArbitraryID.containsKey(1)) {
				ArbitraryID.put(1, ArbitraryID.get(1) + 1);
				return Integer.toString(ArbitraryID.get(1));
			} else {
				ArbitraryID.put(1, 1);
				return Integer.toString(ArbitraryID.get(1));
			}
		}
		return "";
	}

	public static String getNBTData(ItemStack tempitem) {
		if (ItemUtilities.dataTagsEnabled() && ServerHandler.hasSpecificUpdate("1_8") && tempitem != null && tempitem.getType() != Material.AIR) {
		try {
		Class<?> craftItemStack = Reflection.getOBC("inventory.CraftItemStack");
		Class<?> nmsItemStackClass = Reflection.getNMS("ItemStack");
		Method getNMSI = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
		Object nms = getNMSI.invoke(null, tempitem);
		Object cacheTag = nmsItemStackClass.getMethod("getTag").invoke(nms);
		if (cacheTag != null && cacheTag.getClass().getMethod("getString", String.class).invoke(cacheTag, "ItemJoin") != null 
				|| cacheTag != null && cacheTag.getClass().getMethod("getString", String.class).invoke(cacheTag, "ItemJoin Name") != null
				&& cacheTag != null && cacheTag.getClass().getMethod("getString", String.class).invoke(cacheTag, "ItemJoin Slot") != null) {
			String data1 = (String) cacheTag.getClass().getMethod("getString", String.class).invoke(cacheTag, "ItemJoin Name");
			String data2 = (String) cacheTag.getClass().getMethod("getString", String.class).invoke(cacheTag, "ItemJoin Slot");
			String data = (String) cacheTag.getClass().getMethod("getString", String.class).invoke(cacheTag, "ItemJoin");
			if (data1 != null && data2 != null && data1 != "" && data2 != "") {
				return data1 + " " + data2;
			} else if (data != null && data != "") { 
				return data.replace("Slot: ", "");
			}
		} 
		} catch (Exception e) {
			ServerHandler.sendDebugMessage("Error 254 has occured when getting NBTData to an item.");
			ServerHandler.sendDebugTrace(e);
		}
		}
		return null;
	}
	
	public static ItemStack addLore(ItemStack item, String... lores) {
		ItemMeta meta = item.getItemMeta();
		List<String> newLore = new ArrayList<String>();
		if (meta.hasLore()) { newLore = meta.getLore(); }
		for (String lore : lores) { newLore.add(Utils.colorFormat(lore)); }
		meta.setLore(newLore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemMap getItemMap(final String slot, final List < ItemMap > items) {
		ItemMap itemMap = null;
		for (final ItemMap item: items) {
			if (item.getSlot().equalsIgnoreCase(slot)) {
				itemMap = item;
				break;
			}
		}
		return itemMap;
	}
	
    public static ItemStack getItem(String mat, int count, boolean glowing, String name, String... lore) {
        ItemStack tempItem; if (!ServerHandler.hasSpecificUpdate("1_8") && mat.equals("BARRIER")) { mat = "WOOL:14"; }
        if (getMaterial(mat, null) == null) { mat = "STONE"; } 
        if (ServerHandler.hasAquaticUpdate()) { tempItem = new ItemStack(getMaterial(mat, null), count); } 
        else { short dataValue = 0; if (mat.contains(":")) { String[] parts = mat.split(":"); mat = parts[0]; dataValue = (short) Integer.parseInt(parts[1]); } tempItem = Legacy.newLegacyItemStack(getMaterial(mat, null), count, dataValue); }
        if (glowing && mat != "AIR") { tempItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1); }
        ItemMeta tempMeta = tempItem.getItemMeta();
        if (ServerHandler.hasSpecificUpdate("1_8") && mat != "AIR") { tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS); }
        if (name != null && mat != "AIR") { name = Utils.colorFormat(name); tempMeta.setDisplayName(name); }
        if (lore != null && lore.length != 0 && mat != "AIR") {
        	ArrayList<String> loreList = new ArrayList<String>();
        	for (String loreString: lore) { 
        		if (!loreString.isEmpty()) {
        			if (loreString.contains("/n")) {
        				String[] loreSplit = loreString.split(" /n ");
        				for (String loreStringSplit : loreSplit) { loreList.add(Utils.colorFormat(loreStringSplit)); }
        			} else { loreList.add(Utils.colorFormat(loreString)); }
        		} 
        	}
        	tempMeta.setLore(loreList);
        }
        tempItem.setItemMeta(tempMeta); 
        return tempItem;
    }
	
	public static Material getMaterial(String material, String dataVal) {
		try {
			boolean isLegacy = (dataVal != null);
			if (material.contains(":")) { String[] parts = material.split(":"); material = parts[0]; dataVal = parts[1]; isLegacy = true; }
			if (Utils.isInt(material) && !ServerHandler.hasAquaticUpdate()) {
				return Legacy.findLegacyMaterial(Integer.parseInt(material));
			} else if (Utils.isInt(material) && ServerHandler.hasAquaticUpdate() || isLegacy && ServerHandler.hasAquaticUpdate()) {
				int dataValue;
				if (!Utils.isInt(material)) { material = "LEGACY_" + material; }
				if (dataVal != null) { dataValue = Integer.parseInt(dataVal); } else { dataValue = 0; }
				if (!Utils.isInt(material)) { return Legacy.getLegacyMaterial(Material.getMaterial(material.toUpperCase()), (byte) dataValue); } 
				else { return Legacy.getLegacyMaterial(Integer.parseInt(material), (byte) dataValue); }
			} else if (!ServerHandler.hasAquaticUpdate()) {
				return Material.getMaterial(material.toUpperCase());
			} else {
				return Material.matchMaterial(material.toUpperCase());
			}
		} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		return null;
	}
	
	public static String getMaterialPath(ConfigurationSection itemNode) {
		String material = ItemHandler.purgeDelay(itemNode.getString(".id"));
		if (ConfigHandler.getMaterialSection(itemNode) != null) {
			List<String> materials = new ArrayList<String>();
			for (String materialKey : ConfigHandler.getMaterialSection(itemNode).getKeys(false)) {
				String materialList = itemNode.getString(".id." + materialKey);
				if (materialList != null) {
					materials.add(materialList);
				}
			}
			material = ItemHandler.purgeDelay(itemNode.getString(".id." + ConfigHandler.getMaterialSection(itemNode).getKeys(false).iterator().next()));
			return material;
		}
		return material;
	}
	
    public static short getMapID(MapView view) {
    	if (!ItemUtilities.getMapMethod()) {
    		try { return (short) view.getId(); } 
			catch (NoSuchMethodError e) { ItemUtilities.setMapMethod(true); return Reflection.getMapID(view); }
    	} else { return Reflection.getMapID(view); }
    }
	
	public static String getDelay(String context) {
		if (Utils.containsIgnoreCase(context, "<delay:" + Utils.returnInteger(context) + ">") 
				|| Utils.containsIgnoreCase(context, "delay:" + Utils.returnInteger(context) + "") 
				|| Utils.containsIgnoreCase(context, "<delay: " + Utils.returnInteger(context) + ">")
				|| Utils.containsIgnoreCase(context, "delay: " + Utils.returnInteger(context) + "")) {
			return ("<delay:" + Utils.returnInteger(context) + ">");
		}
		return null;
	}
	
	public static String purgeDelay(String context) {
		if (getDelay(context) != null) {
			return context.replace(getDelay(context), "");
		} 
		return context;
	}
	
	public static List<String> purgeDelay(List <String> context) {
		List<String> newContext = new ArrayList<String>();
		for (String minorContext : context) {
			newContext.add(purgeDelay(minorContext));
		}
		return newContext;
	}
	
    public static ItemStack setSkullTexture(ItemStack item, String skullTexture) {
    	try {
	        ItemMeta itemMeta = item.getItemMeta();
			GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
			gameProfile.getProperties().put("textures", new Property("textures", new String(skullTexture)));
			Field declaredField = itemMeta.getClass().getDeclaredField("profile");
			declaredField.setAccessible(true);
			declaredField.set(itemMeta, gameProfile);
			item.setItemMeta(itemMeta);
    	} catch (Exception e) { }
    	return item;
    }
    
	public static String sterilizeInventory(Inventory inventory) {
	    try {
	    	java.io.ByteArrayOutputStream str = new java.io.ByteArrayOutputStream();
	        org.bukkit.util.io.BukkitObjectOutputStream data = new org.bukkit.util.io.BukkitObjectOutputStream(str);
	        data.writeInt(inventory.getSize());
	        for (int i = 0; i < inventory.getSize(); i++) {
	            data.writeObject(inventory.getItem(i));
	        }
	        data.close();
	        return Base64.getEncoder().encodeToString(str.toByteArray());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	public static Inventory deserializeInventory(String inventoryData) {
	    try {
	    	java.io.ByteArrayInputStream stream = new java.io.ByteArrayInputStream(Base64.getDecoder().decode(inventoryData));
	        org.bukkit.util.io.BukkitObjectInputStream data = new org.bukkit.util.io.BukkitObjectInputStream(stream);
	        Inventory inventory = Bukkit.createInventory(null, data.readInt());
	        for (int i = 0; i < inventory.getSize(); i++) {
	            inventory.setItem(i, (ItemStack) data.readObject());
	        }
	        data.close();
	        return inventory;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public static String getName(ItemStack stack) {
		try {
			return WordUtils.capitalizeFully(stack.getType().name().toLowerCase().replace('_', ' '));
		} catch (NullPointerException e) {ServerHandler.sendDebugTrace(e); }
		return null;
	}
	
	public static String getEnchantName(Enchantment e) {
		if (!ServerHandler.hasAquaticUpdate()) { return Legacy.getLegacyEnchantName(e); } 
		else { return e.getKey().getKey().toString(); }
	}
	
	public static Enchantment getEnchantByName(String name) {
		if (!ServerHandler.hasAquaticUpdate()) {
			Enchantment enchantName = Legacy.getLegacyEnchantByName(name);
			if (enchantName != null) { return enchantName; }
		} else {
			try {
				Enchantment enchantName = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(name.toLowerCase()));
				if (enchantName != null) {
					return enchantName;
				} else { return Legacy.getLegacyEnchantByName(name); }
			} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		}
		return null;
	}
	
	public static short getDurability(ItemStack item) {
		if (!ServerHandler.hasAquaticUpdate()) {
			return Legacy.getLegacyDurability(item);
		} else { return ((short) ((Damageable) item.getItemMeta()).getDamage()); }
	}
	
	public static ItemMeta setSkullOwner(ItemMeta tempmeta, String owner) {
		return Legacy.setLegacySkullOwner((SkullMeta) tempmeta, owner);
	}
	
	public static String getSkullSkinTexture(ItemMeta meta) {
		try {
			final Class < ? > cls = Reflection.getOBC("inventory.CraftMetaSkull");
			final Object real = cls.cast(meta);
			final Field field = real.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			final GameProfile profile = (GameProfile) field.get(real);
			final Collection < Property > props = profile.getProperties().get("textures");
			for (final Property property: props) {
				if (property.getName().equals("textures")) { return property.getValue(); }
			}
		} catch (Exception e) { }
		return "";
	}
	
	public static boolean isSkull(Material type) {
		if (type.toString().equalsIgnoreCase("SKULL_ITEM") || type.toString().equalsIgnoreCase("PLAYER_HEAD")) {
			return true;
		}
		return false;
	}

	public static boolean isCountSimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		if (inPlayerInventory.getAmount() == inStoredItems.getAmount()) {
			return true;
		} else if (ConfigHandler.getConfig("items.yml").getBoolean("items-RestrictCount") == false) {
			return true;
		}
		return false;
	}
	
	public static boolean isCustomSlot(String slot) {
		if (slot.equalsIgnoreCase("Offhand") || slot.equalsIgnoreCase("Arbitrary") || slot.equalsIgnoreCase("Helmet") 
				|| slot.equalsIgnoreCase("Chestplate") || slot.equalsIgnoreCase("Leggings") || slot.equalsIgnoreCase("Boots") || isCraftingSlot(slot)) {
			return true;
		}
		return false;
	}
	
	public static boolean isCraftingSlot(String slot) {
		if (slot.equalsIgnoreCase("CRAFTING[0]") || slot.equalsIgnoreCase("CRAFTING[1]") 
				|| slot.equalsIgnoreCase("CRAFTING[2]") || slot.equalsIgnoreCase("CRAFTING[3]") || slot.equalsIgnoreCase("CRAFTING[4]")) {
			return true;
		}
		return false;
	}
	
	public static boolean containsNBTData(ItemStack inPlayerInventory) {
		if (ItemUtilities.dataTagsEnabled() && ServerHandler.hasSpecificUpdate("1_8") && inPlayerInventory != null && inPlayerInventory.getType() != Material.AIR && getNBTData(inPlayerInventory) != null) {
			return true;
		} else if (!ItemUtilities.dataTagsEnabled() || ItemUtilities.dataTagsEnabled() && !ServerHandler.hasSpecificUpdate("1_8")) { 
				if (inPlayerInventory != null && inPlayerInventory.hasItemMeta() && inPlayerInventory.getItemMeta().hasDisplayName()
						&& ConfigHandler.decodeSecretData(inPlayerInventory.getItemMeta().getDisplayName()).contains(ConfigHandler.decodeSecretData(ConfigHandler.encodeSecretData(ItemUtilities.getNBTData(null))))) {
					return true;
				}
		}
		return false;
	}
}