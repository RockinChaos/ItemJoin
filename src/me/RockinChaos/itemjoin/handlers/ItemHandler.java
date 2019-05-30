package me.RockinChaos.itemjoin.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.inventory.meta.Damageable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

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
				if (!Utils.isInt(material)) { return Legacy.convertLegacyMaterial(Legacy.getLegacyMaterialID(Material.getMaterial(material.toUpperCase())), (byte) dataValue); } 
				else { return Legacy.convertLegacyMaterial(Integer.parseInt(material), (byte) dataValue); }
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
	
	public static Color getColorFromHexColor(String hexString) {
		int hex = Integer.decode("#" + hexString);
		int r = ((hex & 0xFF0000) >> 16);
		int g = ((hex & 0xFF00) >> 8);
		int b = (hex & 0xFF);
		Color bukkitColor = Color.fromBGR(r, g, b);
		return bukkitColor;
	}
}