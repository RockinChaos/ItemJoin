package me.RockinChaos.itemjoin.handlers;

import java.util.HashMap;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.listeners.giveitems.SetItems;
import me.RockinChaos.itemjoin.utils.Utils;

public class ItemHandler {
	public static HashMap < Player, Integer > ArbitraryID = new HashMap < Player, Integer > ();

	public static boolean isAllowedItem(Player player, ItemStack inPlayerInventory, String itemflag) {
		Boolean Allowed = true;
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
									break;
								} else {
									Allowed = false;
									break;
								}
							}
						}
					}
				}
			}
		}
		return Allowed;
	}

	public static Boolean isObtainable(Player player, String item, String slot, String ItemID, ItemStack inStoredItems) {
		Boolean isObtainable = false;
		if (inStoredItems != null && Utils.isInt(slot) && Integer.parseInt(slot) >= 0 && Integer.parseInt(slot) <= 35 && !hasItem(player, inStoredItems) && canOverwrite(player, slot, item) && !ConfigHandler.hasFirstJoined(player, item)) {
			isObtainable = true;
		} else if (inStoredItems != null && Utils.isCustomSlot(slot) && !hasItem(player, inStoredItems) && canOverwrite(player, slot, item) && !ConfigHandler.hasFirstJoined(player, item)) {
			isObtainable = true;
		}
		return isObtainable;
	}
	
	public static String getName(ItemStack stack) {
		try {
		return WordUtils.capitalizeFully(stack.getType().name().toLowerCase().replace('_', ' '));
		} catch (NullPointerException ex) {}
		return "Error";
	}

	public static String getItemID(Player player, String slot) {
		return slot + getArbitraryID(player, slot);
	}

	public static void clearItemID(Player player) {
		ArbitraryID.remove(player);
	}

	public static String getArbitraryID(Player player, String slot) {
		String ArbID = "";
		if (slot.equalsIgnoreCase("Arbitrary")) {
			if (ArbitraryID.containsKey(player)) {
				ArbitraryID.put(player, ArbitraryID.get(player) + 1);
				ArbID = Integer.toString(ArbitraryID.get(player));
			} else {
				ArbitraryID.put(player, 1);
				ArbID = Integer.toString(ArbitraryID.get(player));
			}
		}
		return ArbID;
	}

	public static boolean containsIgnoreCase(String string1, String string2) {
		boolean isSimilarString = false;
		if (string1 != null && string1.toLowerCase().contains(string2.toLowerCase())) {
			isSimilarString = true;
		}
		return isSimilarString;
	}

	public static boolean isSimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		boolean isSimilar = false;
		if (inPlayerInventory != null && inStoredItems != null) {
			if (inPlayerInventory.isSimilar(inStoredItems)) {
				isSimilar = true;
			}  else if (inPlayerInventory != null && inPlayerInventory.getType().equals(Material.SKULL_ITEM) && inStoredItems.getType().equals(Material.SKULL_ITEM) && inPlayerInventory.hasItemMeta() 
					&& inPlayerInventory.getItemMeta().hasDisplayName() && inStoredItems.hasItemMeta() && inStoredItems.getItemMeta().hasDisplayName() && inPlayerInventory.getItemMeta().getDisplayName().contains(inStoredItems.getItemMeta().getDisplayName()) 
					&& ((SkullMeta) inPlayerInventory.getItemMeta()).hasOwner() && ((SkullMeta) inStoredItems.getItemMeta()).hasOwner() && ((SkullMeta) inPlayerInventory.getItemMeta()).getOwner().equalsIgnoreCase(((SkullMeta) inStoredItems.getItemMeta()).getOwner())) {
				isSimilar = true;
			} else if (inPlayerInventory != null && inPlayerInventory.getDurability() >= 1) {
				ItemStack inPlayerInventoryTemp = new ItemStack(inPlayerInventory);
				inPlayerInventoryTemp.setDurability(inStoredItems.getDurability());
				if (inPlayerInventoryTemp.isSimilar(inStoredItems)) {
					isSimilar = true;
				}
				}
		}
		return isSimilar;
	}

	public static boolean isCountSimilar(ItemStack inPlayerInventory, ItemStack inStoredItems) {
		boolean isCountSimilar = false;
		if (inPlayerInventory.getAmount() == inStoredItems.getAmount()) {
			isCountSimilar = true;
		}
		return isCountSimilar;
	}

	public static boolean hasItem(Player player, ItemStack inStoredItems) {
		boolean hasItem = false;
		for (ItemStack inPlayerInventory: player.getInventory().getContents()) {
			if (isSimilar(inPlayerInventory, inStoredItems) && isCountSimilar(inPlayerInventory, inStoredItems)) {
				hasItem = true;
				break;
			}
		}
		for (ItemStack inPlayerInventory: player.getEquipment().getArmorContents()) {
			if (isSimilar(inPlayerInventory, inStoredItems) && isCountSimilar(inPlayerInventory, inStoredItems)) {
				hasItem = true;
				break;
			}
		}
		if (ServerHandler.hasCombatUpdate() 
				&& isSimilar(player.getInventory().getItemInOffHand(), inStoredItems) 
				&& isCountSimilar(player.getInventory().getItemInOffHand(), inStoredItems)) {
			hasItem = true;
		}
		return hasItem;
	}

	public static Boolean canOverwrite(Player player, String slot, String item) {
		Boolean canOverwrite = true;
		boolean Overwrite = ConfigHandler.getConfig("items.yml").getBoolean("items-Overwrite");
		try {
		if (Overwrite == false && Utils.isInt(slot) && player.getInventory().getItem(Integer.parseInt(slot)) != null) {
			canOverwrite = false;
			SetItems.failCount.put(player, SetItems.failCount.get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Arbitrary") && player.getInventory().firstEmpty() == -1) {
			canOverwrite = false;
			SetItems.failCount.put(player, SetItems.failCount.get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Helmet") && player.getInventory().getHelmet() != null) {
			canOverwrite = false;
			SetItems.failCount.put(player, SetItems.failCount.get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Chestplate") && player.getInventory().getChestplate() != null) {
			canOverwrite = false;
			SetItems.failCount.put(player, SetItems.failCount.get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Leggings") && player.getInventory().getLeggings() != null) {
			canOverwrite = false;
			SetItems.failCount.put(player, SetItems.failCount.get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && slot.equalsIgnoreCase("Boots") && player.getInventory().getBoots() != null) {
			canOverwrite = false;
			SetItems.failCount.put(player, SetItems.failCount.get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
		} else if (Overwrite == false && Utils.isCustomSlot(slot) && ServerHandler.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand")) {
			if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
			canOverwrite = false;
			SetItems.failCount.put(player, SetItems.failCount.get(player) + 1);
			ServerHandler.sendDebugMessage("Failed to give; " + item);
			}
			}
		} catch (NullPointerException ex) {
			
		}
		return canOverwrite;
	}
}