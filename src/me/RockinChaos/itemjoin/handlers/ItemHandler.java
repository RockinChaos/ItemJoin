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
import org.bukkit.inventory.meta.SkullMeta;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.cacheitems.setUnbreakable;
import me.RockinChaos.itemjoin.listeners.giveitems.SetItems;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Utils;

public class ItemHandler {
	private static HashMap < Player, Integer > ArbitraryID = new HashMap < Player, Integer > ();

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
							} else {
								ItemID = slot;
							}
							ItemStack inStoredItems = CreateItems.items.get(world + "." + player.getName().toString() + ".items." + ItemID + item);
							if (ItemHandler.isSimilar(inPlayerInventory, inStoredItems) && ItemHandler.containsIgnoreCase(ItemFlags, itemflag)) {
								if (Utils.canBypass(player, ItemFlags)) {
									return true;
								} else {
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	public static Boolean isObtainable(Player player, String item, String slot, String ItemID, ItemStack inStoredItems) {
		if (inStoredItems != null && Utils.isInt(slot) && Integer.parseInt(slot) >= 0 && Integer.parseInt(slot) <= 35 && !hasItem(player, inStoredItems) && canOverwrite(player, slot, item) && !ConfigHandler.hasFirstJoined(player, item)) {
			return true;
		} else if (inStoredItems != null && Utils.isCustomSlot(slot) && !hasItem(player, inStoredItems) && canOverwrite(player, slot, item) && !ConfigHandler.hasFirstJoined(player, item)) {
			return true;
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
		ArbitraryID.remove(player);
	}

	public static String getArbitraryID(Player player, String slot) {
		if (slot.equalsIgnoreCase("Arbitrary")) {
			if (ArbitraryID.containsKey(player)) {
				ArbitraryID.put(player, ArbitraryID.get(player) + 1);
				return Integer.toString(ArbitraryID.get(player));
			} else {
				ArbitraryID.put(player, 1);
				return Integer.toString(ArbitraryID.get(player));
			}
		}
		return "";
	}

	public static boolean containsIgnoreCase(String string1, String string2) {
		if (string1 != null && string1.toLowerCase().contains(string2.toLowerCase())) {
			return true;
		}
		return false;
	}

	public static boolean isSimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		if (inPlayerInventory != null && inStoredItems != null) {
			ItemStack inPlayerInventoryTemp = new ItemStack(inPlayerInventory);
			ItemStack inStoredItemsTemp = new ItemStack(inStoredItems);
			if (inPlayerInventoryTemp.isSimilar(inStoredItemsTemp) 
					|| isDurability(inPlayerInventoryTemp, inStoredItemsTemp) && inPlayerInventoryTemp.isSimilar(inStoredItemsTemp) 
					|| isDisplayNameSimilar(inPlayerInventoryTemp, inStoredItemsTemp) && inPlayerInventoryTemp.isSimilar(inStoredItemsTemp) 
					|| isEnchantsSimilar(inPlayerInventoryTemp, inStoredItemsTemp) && inPlayerInventoryTemp.isSimilar(inStoredItemsTemp)) {
				return true;
			} else if (!ServerHandler.hasCombatUpdate() && isCustomSimilar(inPlayerInventoryTemp, inStoredItemsTemp)) {
				return true;
			} else if (inPlayerInventoryTemp.getType().equals(Material.SKULL_ITEM) && inStoredItemsTemp.getType().equals(Material.SKULL_ITEM) && ((SkullMeta) inPlayerInventoryTemp.getItemMeta()).hasOwner() && ((SkullMeta) inStoredItemsTemp.getItemMeta()).hasOwner() && PlayerHandler.getSkullOwner(inPlayerInventoryTemp).equalsIgnoreCase(PlayerHandler.getSkullOwner(inStoredItemsTemp)) && isCustomSimilar(inPlayerInventoryTemp, inStoredItemsTemp)) {
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
				return true;
			} else if (isEnchantsSimilar(inPlayerInventory, inStoredItems) && isNBTDataSimilar(inPlayerInventory, inStoredItems)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isDurability(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		if (inPlayerInventory.getDurability() >= 1) {
			inPlayerInventory.setDurability(inStoredItems.getDurability());
			return true;
		}
		return false;
	}

	public static boolean isDisplayNameSimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		if (inPlayerInventory.hasItemMeta() && inPlayerInventory.getItemMeta().hasDisplayName() && inStoredItems.hasItemMeta() && inStoredItems.getItemMeta().hasDisplayName() && inPlayerInventory.getItemMeta().getDisplayName().equals(inStoredItems.getItemMeta().getDisplayName())) {
			return true;
		} else if (isNBTDataSimilar(inPlayerInventory, inStoredItems) || ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") != true && inPlayerInventory.hasItemMeta() && inPlayerInventory.getItemMeta().hasDisplayName() && inPlayerInventory.getItemMeta().getDisplayName().contains(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData()))) {
			ItemMeta itemMeta = CreateItems.getTempMeta(inPlayerInventory);
			itemMeta.setDisplayName(inStoredItems.getItemMeta().getDisplayName());
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
		if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true && getNBTData(inPlayerInventory) != null && getNBTData(inStoredItems) != null && getNBTData(inPlayerInventory).equals(getNBTData(inStoredItems))) {
			return true;
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") != true) {
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
	
	public static String getNBTData(ItemStack tempitem) {
		if (ConfigHandler.getConfig("config.yml").getBoolean("NewNBT-System") == true) {
		try {
		Class<?> craftItemStack = setUnbreakable.getOBC("inventory.CraftItemStack");
		Class<?> nmsItemStackClass = setUnbreakable.getNMS("ItemStack");
		Method getNMSI = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
		Object nms = getNMSI.invoke(null, tempitem);
		Object cacheTag = nmsItemStackClass.getMethod("getTag").invoke(nms);
		if (cacheTag != null && cacheTag.getClass().getMethod("getString", String.class).invoke(cacheTag, "ItemJoin") != null)  {
			String test = (String) cacheTag.getClass().getMethod("getString", String.class).invoke(cacheTag, "ItemJoin");
			return test;
		} 
		} catch (Exception e) {
			ServerHandler.sendDebugMessage("Error 133 has occured when getting NBTData to an item.");
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

	public static Boolean canOverwrite(Player player, String slot, String item) {
		boolean Overwrite = ConfigHandler.getConfig("items.yml").getBoolean("items-Overwrite");
		try {
		if (Overwrite == false && Utils.isInt(slot) && player.getInventory().getItem(Integer.parseInt(slot)) != null) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Arbitrary") && player.getInventory().firstEmpty() == -1) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Helmet") && player.getInventory().getHelmet() != null) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Chestplate") && player.getInventory().getChestplate() != null) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Leggings") && player.getInventory().getLeggings() != null) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Boots") && player.getInventory().getBoots() != null) {
			SetItems.putFailCount(player, SetItems.getFailCount().get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			return false;
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && ServerHandler.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand")) {
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