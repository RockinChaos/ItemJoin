package me.RockinChaos.itemjoin.handlers;

import java.lang.reflect.Method;
import java.util.HashMap;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.listeners.giveitems.SetItems;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Reflection;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

public class ItemHandler {
	private static HashMap < String, Integer > ArbitraryID = new HashMap < String, Integer > ();

	public static boolean isAllowedItem(Player player, ItemStack inPlayerInventory, String itemflag) {
		if (inPlayerInventory != null) {
			if (Utils.isConfigurable()) {
				for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
					ConfigurationSection items = ConfigHandler.getItemSection(item);
					String world = player.getWorld().getName();
					String ItemFlags = items.getString(".itemflags");
					int Arbitrary = 0;
					String ItemID;
					if (WorldHandler.inWorld(items, world) && items.getString(".slot") != null) {
						String slotlist = items.getString(".slot").replace(" ", "");
						String[] slots = slotlist.split(",");
						for (String slot: slots) {
							if (slot.equalsIgnoreCase("Arbitrary")) {
								Arbitrary = Arbitrary + 1;
								ItemID = slot + Arbitrary;
							} else { ItemID = slot; }
							ItemStack inStoredItems = CreateItems.items.get(world + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
							if (ItemHandler.isSimilar(inPlayerInventory, inStoredItems) && ItemHandler.containsIgnoreCase(ItemFlags, itemflag)) {
								if (Utils.canBypass(player, ItemFlags, itemflag)) { return true; } else { return false; }
							}
						}
					}
				}
			}
		}
		return true;
	}

	public static Boolean isObtainable(Player player, ConfigurationSection items, String item, String slot, String ItemID, ItemStack inStoredItems) {
		if (inStoredItems != null && Utils.isInt(slot) && Integer.parseInt(slot) >= 0 && Integer.parseInt(slot) <= 35) {
			if (!hasItem(player, inStoredItems) || !hasItem(player, inStoredItems, slot) && ItemHandler.containsIgnoreCase(items.getString(".itemflags"), "vanilla")) {
				if (!SQLData.hasFirstJoined(player, item) && !SQLData.hasIPLimited(player, item) && canOverwrite(player, slot, item)) {
				return true;
				}
			}
		} else if (inStoredItems != null && Utils.isCustomSlot(slot)) {
			if (!hasItem(player, inStoredItems) || !hasItem(player, inStoredItems, slot) && ItemHandler.containsIgnoreCase(items.getString(".itemflags"), "vanilla")) {
				if (!SQLData.hasFirstJoined(player, item) && !SQLData.hasIPLimited(player, item) && canOverwrite(player, slot, item)) {
				return true;
				}
			}
		}
		return false;
	}
	
	public static String getName(ItemStack stack) {
		try {
		return WordUtils.capitalizeFully(stack.getType().name().toLowerCase().replace('_', ' '));
		} catch (NullPointerException e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return "Error";
	}

	public static String getItemID(Player player, String slot) {
		return slot + getArbitraryID(player, slot);
	}

	public static void clearItemID(Player player) {
		ArbitraryID.remove(PlayerHandler.getPlayerID(player));
	}

	public static String getArbitraryID(Player player, String slot) {
		if (slot.equalsIgnoreCase("Arbitrary")) {
			if (ArbitraryID.containsKey(PlayerHandler.getPlayerID(player))) {
				ArbitraryID.put(PlayerHandler.getPlayerID(player), ArbitraryID.get(PlayerHandler.getPlayerID(player)) + 1);
				return Integer.toString(ArbitraryID.get(PlayerHandler.getPlayerID(player)));
			} else {
				ArbitraryID.put(PlayerHandler.getPlayerID(player), 1);
				return Integer.toString(ArbitraryID.get(PlayerHandler.getPlayerID(player)));
			}
		}
		return "";
	}

	public static boolean containsIgnoreCase(String string1, String string2) {
		if (string1 != null && string2 != null && string1.toLowerCase().contains(string2.toLowerCase())) {
			return true;
		}
		return false;
	}

	public static boolean isSimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		if (inPlayerInventory != null && inPlayerInventory.getType() != Material.AIR && inStoredItems != null && inStoredItems.getType() != Material.AIR) {
			ItemStack inPlayerInventoryTemp = new ItemStack(inPlayerInventory);
			ItemStack inStoredItemsTemp = new ItemStack(inStoredItems);
			if (inPlayerInventoryTemp.isSimilar(inStoredItemsTemp) 
					|| isDurabilitySimilar(inPlayerInventoryTemp, inStoredItemsTemp) && inPlayerInventoryTemp.isSimilar(inStoredItemsTemp) 
					|| isDisplayNameSimilar(inPlayerInventoryTemp, inStoredItemsTemp) && inPlayerInventoryTemp.isSimilar(inStoredItemsTemp)
					|| isLoreSimilar(inPlayerInventoryTemp, inStoredItemsTemp) && inPlayerInventoryTemp.isSimilar(inStoredItemsTemp)
					|| isEnchantsSimilar(inPlayerInventoryTemp, inStoredItemsTemp) && inPlayerInventoryTemp.isSimilar(inStoredItemsTemp)) {
				return true;
			} else if (!ServerHandler.hasCombatUpdate() && isCustomSimilar(inPlayerInventoryTemp, inStoredItemsTemp)) {
				return true;
			} else if (ItemHandler.containsIgnoreCase(PlayerHandler.getSkullOwner(inPlayerInventory), PlayerHandler.getSkullOwner(inStoredItems))
					&& isCustomSimilar(inPlayerInventoryTemp, inStoredItemsTemp)) {
				return true;
			} else if (Hooks.hasTokenEnchant() && isCustomSimilar(inPlayerInventoryTemp, inStoredItemsTemp)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isCustomSimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		if (inPlayerInventory.hasItemMeta() && inStoredItems.hasItemMeta() && inPlayerInventory.getType().equals(inStoredItems.getType()) && isDisplayNameSimilar(inPlayerInventory, inStoredItems)) {
			if (inPlayerInventory.getItemMeta().hasLore() && inStoredItems.getItemMeta().hasLore() && inPlayerInventory.getItemMeta().getLore().equals(inStoredItems.getItemMeta().getLore()) && isEnchantsSimilar(inPlayerInventory, inStoredItems) && isNBTDataSimilar(inPlayerInventory, inStoredItems)) {
				if (isNBTDataSimilar(inPlayerInventory, inStoredItems)) {
				}
				return true;
			} else if (isEnchantsSimilar(inPlayerInventory, inStoredItems) && isNBTDataSimilar(inPlayerInventory, inStoredItems)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isDurabilitySimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		if (inPlayerInventory.getDurability() >= 1) {
			inPlayerInventory.setDurability(inStoredItems.getDurability());
			return true;
		}
		return false;
	}

	public static boolean isDisplayNameSimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		if (inPlayerInventory.hasItemMeta() && inPlayerInventory.getItemMeta().hasDisplayName() && inStoredItems.hasItemMeta() && inStoredItems.getItemMeta().hasDisplayName() && inPlayerInventory.getItemMeta().getDisplayName().equals(inStoredItems.getItemMeta().getDisplayName())) {
			return true;
		} else if (isNBTDataSimilar(inPlayerInventory, inStoredItems)) {
			ItemMeta itemMeta = CreateItems.getTempMeta(inPlayerInventory);
			if (itemMeta != null && inStoredItems.hasItemMeta() && inStoredItems.getItemMeta().hasDisplayName()) {
				itemMeta.setDisplayName(inStoredItems.getItemMeta().getDisplayName());
				inPlayerInventory.setItemMeta(itemMeta);
			return true;
			}
		}
		return false;
	}

	public static boolean isLoreSimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		if (inPlayerInventory.hasItemMeta() && inPlayerInventory.getItemMeta().hasLore() && inStoredItems.hasItemMeta() && inStoredItems.getItemMeta().hasLore() && inPlayerInventory.getItemMeta().getLore().equals(inStoredItems.getItemMeta().getLore())) {
			return true;
		} else if (isNBTDataSimilar(inPlayerInventory, inStoredItems)) {
			ItemMeta itemMeta = CreateItems.getTempMeta(inPlayerInventory);
			itemMeta.setLore(inStoredItems.getItemMeta().getLore());
			inPlayerInventory.setItemMeta(itemMeta);
			return true;
		}
		return false;
	}
	
	public static boolean isEnchantsSimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		if (inPlayerInventory.hasItemMeta() && inStoredItems.hasItemMeta()) {
			if (inPlayerInventory.getItemMeta().hasEnchants() && inStoredItems.getItemMeta().hasEnchants() && inPlayerInventory.getItemMeta().getEnchants().equals(inStoredItems.getItemMeta().getEnchants())) {
				if (Bukkit.getServer().getPluginManager().getPlugin("TokenEnchant") != null) {
					ItemMeta itemMeta = inStoredItems.getItemMeta();
					itemMeta.setLore(inPlayerInventory.getItemMeta().getLore());
					itemMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
					inStoredItems.setItemMeta(itemMeta);
					return true;
				} else {
					return true;
				}
			} else if (!inPlayerInventory.getItemMeta().hasEnchants() && !inStoredItems.getItemMeta().hasEnchants()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNBTDataSimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && ServerHandler.hasAltUpdate("1_8") && getNBTData(inPlayerInventory) != null && getNBTData(inStoredItems) != null && getNBTData(inStoredItems).contains(getNBTData(inPlayerInventory))) {
			return true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") != true || ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && !ServerHandler.hasAltUpdate("1_8")) { 
				if (inPlayerInventory.hasItemMeta() 
				&& inPlayerInventory.getItemMeta().hasDisplayName() && inStoredItems.hasItemMeta() && inStoredItems.getItemMeta().hasDisplayName()
				&& inPlayerInventory.getItemMeta().getDisplayName().contains(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData()))
				&& containsIgnoreCase(ConfigHandler.decodeSecretData(inStoredItems.getItemMeta().getDisplayName()), ConfigHandler.decodeSecretData(inPlayerInventory.getItemMeta().getDisplayName()))) {
					return true;
				}
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

	public static boolean hasItem(Player player, ItemStack inStoredItems) {
		for (ItemStack inPlayerInventory: player.getInventory().getContents()) {
			if (isSimilar(inPlayerInventory, inStoredItems) && isCountSimilar(inPlayerInventory, inStoredItems)) {
				return true;
			}
		}
		for (ItemStack inPlayerInventory: player.getEquipment().getArmorContents()) {
			if (isSimilar(inPlayerInventory, inStoredItems) && isCountSimilar(inPlayerInventory, inStoredItems)) {
				return true;
			}
		}
		if (ServerHandler.hasCombatUpdate() 
				&& isSimilar(player.getInventory().getItemInOffHand(), inStoredItems) 
				&& isCountSimilar(player.getInventory().getItemInOffHand(), inStoredItems)) {
			return true;
		}
		return false;
	}
	
	
	public static boolean hasItem(Player player, ItemStack inStoredItems, String slot) {
		if (Utils.isInt(slot)) {
			ItemStack inPlayerInventory = player.getInventory().getItem(Integer.parseInt(slot));
			if (isSimilar(inPlayerInventory, inStoredItems) && isCountSimilar(inPlayerInventory, inStoredItems)) {
				return true;
			}
		} else if (Utils.isCustomSlot(slot) && Utils.getCustomSlot(player, slot) != null) {
			ItemStack inPlayerInventory = Utils.getCustomSlot(player, slot);
			if (isSimilar(inPlayerInventory, inStoredItems) && isCountSimilar(inPlayerInventory, inStoredItems)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasNBTData(ItemStack inPlayerInventory) {
		if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && ServerHandler.hasAltUpdate("1_8") && inPlayerInventory != null && inPlayerInventory.getType() != Material.AIR && getNBTData(inPlayerInventory) != null) {
			return true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") != true || ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && !ServerHandler.hasAltUpdate("1_8")) { 
				if (inPlayerInventory != null && inPlayerInventory.hasItemMeta() && inPlayerInventory.getItemMeta().hasDisplayName()
						&& ConfigHandler.decodeSecretData(inPlayerInventory.getItemMeta().getDisplayName()).contains(ConfigHandler.decodeSecretData(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData())))) {
					return true;
				}
		}
		return false;
	}
	
	public static String getNBTData(ItemStack tempitem) {
		if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true 
				&& ServerHandler.hasAltUpdate("1_8") && tempitem != null && tempitem.getType() != Material.AIR) {
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
				return data;
			}
		} 
		} catch (Exception e) {
			ServerHandler.sendDebugMessage("Error 254 has occured when getting NBTData to an item.");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		}
		return null;
	}
	
	public static int getCount(ConfigurationSection items) {
		try {
			if (items.getString(".count") != null && Utils.isInt(items.getString(".count"))) {
				return items.getInt(".count");
			}
		} catch (Exception e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return 1;
	}
	
	public static short getDataValue(ConfigurationSection items) {
		try {
			if (items.getString(".data-value") != null && Utils.isInt(items.getString(".data-value"))) {
				return (short) items.getInt(".data-value");
			}
		} catch (Exception e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return 0;
	}

	@SuppressWarnings("deprecation")
	public static Material getMaterial(ConfigurationSection items) {
		try {
		if (Utils.isInt(items.getString(".id"))) {
			return Material.getMaterial(items.getInt(".id"));
		} else {
			return Material.getMaterial(items.getString(".id").toUpperCase());
		}
		} catch (Exception e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return null;
	}

	public static Boolean isMaterial(String Mats) {
		Material getMaterial = Material.getMaterial(Mats);
		if (getMaterial != null) {
			return true;
		}
		return false;
	}
	
	public static Boolean isOverwrite(Player player) {
		if (ConfigHandler.getConfig("items.yml").getString("items-Overwrite") != null && WorldHandler.isOverwriteWorld(player.getWorld().getName()) 
				|| ConfigHandler.getConfig("items.yml").getString("items-Overwrite") != null && ConfigHandler.getConfig("items.yml").getBoolean("items-Overwrite") == true) {
			return true;
		}
		return false;
	}

	public static Boolean canOverwrite(Player player, String slot, String item) {
		try {
		if (!isOverwrite(player) && Utils.isInt(slot) && player.getInventory().getItem(Integer.parseInt(slot)) != null) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (!isOverwrite(player) && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Arbitrary") && player.getInventory().firstEmpty() == -1) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (!isOverwrite(player) && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Helmet") && player.getInventory().getHelmet() != null) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (!isOverwrite(player) && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Chestplate") && player.getInventory().getChestplate() != null) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (!isOverwrite(player) && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Leggings") && player.getInventory().getLeggings() != null) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (!isOverwrite(player) && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Boots") && player.getInventory().getBoots() != null) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (!isOverwrite(player) && Utils.isCustomSlot(slot) && ServerHandler.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand")) {
			if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
			}
			}
		} catch (NullPointerException e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return true;
	}
}