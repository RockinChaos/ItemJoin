package me.RockinChaos.itemjoin.handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.Damageable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;

import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.utils.Legacy;
import me.RockinChaos.itemjoin.utils.Reflection;
import me.RockinChaos.itemjoin.utils.Utils;

public class ItemHandler {
	
	private static HashMap < Integer, Integer > ArbitraryID = new HashMap < Integer, Integer > ();
	private static HashMap < String, GameProfile > gameProfiles = new HashMap < String, GameProfile > ();
	
	public static boolean isAllowed(Player player, ItemStack item, String itemflag) {
		ItemMap fetched = getMappedItem(item, player.getWorld());
		if (fetched != null && fetched.isAllowedItem(player, item, itemflag)) {
			return false;
		}
		return true;
	}
	
	public static ItemMap getMappedItem(ItemStack lookUp, World world) {
		for (ItemMap item : ItemUtilities.getItems()) {
			if (item.isSimilar(lookUp) && item.inWorld(world)) {
				return item;
			}
		}
		return null;
	}
	
	public static void closeAnimations(Player player) {
		for (ItemMap item : ItemUtilities.getItems()) {
			if (item.isAnimated() && item.getAnimationHandler().get(player) != null) {
				item.getAnimationHandler().get(player).closeAnimation(player);
				item.removeFromAnimationHandler(player);
			}
		}
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
		if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true 
				&& ServerHandler.hasSpecificUpdate("1_8") && tempitem != null && tempitem.getType() != Material.AIR) {
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
	
	public static Material getMaterial(ConfigurationSection items) {
		try {
		if (Utils.isInt(items.getString(".id")) && !ServerHandler.hasAquaticUpdate()) {
			return Legacy.findLegacyMaterial(items.getInt(".id"));
		} else if (Utils.isInt(items.getString(".id")) && ServerHandler.hasAquaticUpdate()) {
			ServerHandler.sendConsoleMessage("&4[WARNING] The item " + items.getName() + " is using an ItemID (Numerical Value) which is no longer supported as of Minecraft 1.13, instead use its material name.");
			ServerHandler.sendConsoleMessage("&4This will cause issues, please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html for a list of material names.");
			int dataValue;
			if (items.getString(".data-value") != null) { dataValue = items.getInt(".data-value"); } else { dataValue = 0; }
			return Legacy.convertLegacyMaterial(items.getInt(".id"), (byte) dataValue);
		} else if (!ServerHandler.hasAquaticUpdate()) {
			return Material.getMaterial(items.getString(".id").toUpperCase());
		} else {
			return Material.matchMaterial(items.getString(".id").toUpperCase());
		}
		} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		return null;
	}
	
	public static String getDelay(String context) {
		return ("<delay:" + Utils.returnInteger(context) + ">");
	}
	
	public static String purgeDelay(String context) {
		return context.replace(getDelay(context), "");
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
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			try {
				Method fetchProfile = Reflection.getOBC("entity.CraftPlayer").getDeclaredMethod("getProfile");
				Field declaredField = tempmeta.getClass().getDeclaredField("profile");
				declaredField.setAccessible(true);
				if (PlayerHandler.getPlayerString(owner) != null) {
					declaredField.set(tempmeta, fetchProfile.invoke(PlayerHandler.getPlayerString(owner)));
				} else if (PlayerHandler.getPlayerString(owner) == null) {
					if (gameProfiles.get(owner) == null) {
						GameProfile profile = new GameProfile(PlayerHandler.getOfflinePlayer(owner).getUniqueId(), owner);
						setSkin(profile, PlayerHandler.getOfflinePlayer(owner).getUniqueId());
						gameProfiles.put(owner, profile);
					}
					declaredField.set(tempmeta, gameProfiles.get(owner));
				}
			} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		} else {
			ServerHandler.sendDebugMessage("Minecraft does not support offline player heads below Version 1.8.");
			ServerHandler.sendDebugMessage("Player heads will only be given a skin if the player has previously joined the sever.");
			return Legacy.setLegacySkullOwner(((SkullMeta) tempmeta), owner);
		}
		return tempmeta;
	}
	
	private static boolean setSkin(GameProfile profile, UUID uuid) {
		try {
			HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uuid))).openConnection();
			if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
				String skin = reply.split("\"value\":\"")[1].split("\"")[0];
				String signature = reply.split("\"signature\":\"")[1].split("\"")[0];
				profile.getProperties().put("textures", new Property("textures", skin, signature));
				return true;
			} else {
				System.out.println("Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
				return false;
			}
		} catch (Exception e) { ServerHandler.sendDebugTrace(e); return false; }
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
		} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
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
				|| slot.equalsIgnoreCase("Chestplate") || slot.equalsIgnoreCase("Leggings") || slot.equalsIgnoreCase("Boots")) {
			return true;
		}
		return false;
	}
	
	public static boolean containsNBTData(ItemStack inPlayerInventory) {
		if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && ServerHandler.hasSpecificUpdate("1_8") && inPlayerInventory != null && inPlayerInventory.getType() != Material.AIR && getNBTData(inPlayerInventory) != null) {
			return true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") != true || ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && !ServerHandler.hasSpecificUpdate("1_8")) { 
				if (inPlayerInventory != null && inPlayerInventory.hasItemMeta() && inPlayerInventory.getItemMeta().hasDisplayName()
						&& ConfigHandler.decodeSecretData(inPlayerInventory.getItemMeta().getDisplayName()).contains(ConfigHandler.decodeSecretData(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData(null))))) {
					return true;
				}
		}
		return false;
	}
}