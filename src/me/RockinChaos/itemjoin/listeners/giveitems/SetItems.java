package me.RockinChaos.itemjoin.listeners.giveitems;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class SetItems {
	private static HashMap <Player, Integer> failCount = new HashMap <Player, Integer> ();

	public static void setInvSlots(Player player, String item, String slot, String ItemID) {
		ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
		if (ItemHandler.isObtainable(player, item, slot.toString(), ItemID, inStoredItems)) {
			player.getInventory().setItem(Integer.parseInt(slot), inStoredItems);
			ConfigHandler.saveFirstJoined(player, item);
			ConfigHandler.saveIPLimits(player, item);
			ServerHandler.sendDebugMessage("Given the Item; " + inStoredItems.getItemMeta().getDisplayName().replace(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + ItemID), ""));
		}
	}

	public static void setCustomSlots(Player player, String item, String slot, String ItemID) {
		EntityEquipment Equip = player.getEquipment();
		ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
		if (inStoredItems != null) {
			if (slot.equalsIgnoreCase("Arbitrary") && ItemHandler.isObtainable(player, item, slot, ItemID, inStoredItems)) {
				player.getInventory().addItem(inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; " + inStoredItems.getItemMeta().getDisplayName().replace(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + ItemID), ""));
				ConfigHandler.saveFirstJoined(player, item);
				ConfigHandler.saveIPLimits(player, item);
			} else if (slot.equalsIgnoreCase("Helmet") && ItemHandler.isObtainable(player, item, slot, ItemID, inStoredItems)) {
				Equip.setHelmet(inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; " + inStoredItems.getItemMeta().getDisplayName().replace(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + ItemID), ""));
				ConfigHandler.saveFirstJoined(player, item);
				ConfigHandler.saveIPLimits(player, item);
			} else if (slot.equalsIgnoreCase("Chestplate") && ItemHandler.isObtainable(player, item, slot, ItemID, inStoredItems)) {
				Equip.setChestplate(inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; " + inStoredItems.getItemMeta().getDisplayName().replace(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + ItemID), ""));
				ConfigHandler.saveFirstJoined(player, item);
				ConfigHandler.saveIPLimits(player, item);
			} else if (slot.equalsIgnoreCase("Leggings") && ItemHandler.isObtainable(player, item, slot, ItemID, inStoredItems)) {
				Equip.setLeggings(inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; " + inStoredItems.getItemMeta().getDisplayName().replace(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + ItemID), ""));
				ConfigHandler.saveFirstJoined(player, item);
				ConfigHandler.saveIPLimits(player, item);
			} else if (slot.equalsIgnoreCase("Boots") && ItemHandler.isObtainable(player, item, slot, ItemID, inStoredItems)) {
				Equip.setBoots(inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; " + inStoredItems.getItemMeta().getDisplayName().replace(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + ItemID), ""));
				ConfigHandler.saveFirstJoined(player, item);
				ConfigHandler.saveIPLimits(player, item);
			} else if (ServerHandler.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand") && ItemHandler.isObtainable(player, item, slot, ItemID, inStoredItems)) {
				PlayerHandler.setOffhandItem(player, inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; " + inStoredItems.getItemMeta().getDisplayName().replace(ConfigHandler.encodeSecretData(ConfigHandler.getNBTData() + ItemID), ""));
				ConfigHandler.saveFirstJoined(player, item);
				ConfigHandler.saveIPLimits(player, item);
			}
		}
	}
	
	public static void setHeldItemSlot(Player player) {
		if (ConfigHandler.getConfig("config.yml").getString("HeldItem-Slot") != null 
				&& Utils.isInt(ConfigHandler.getConfig("config.yml").getString("HeldItem-Slot")) 
				&& ConfigHandler.getConfig("config.yml").getInt("HeldItem-Slot") <= 8 && ConfigHandler.getConfig("config.yml").getInt("HeldItem-Slot") >= 0) {
			player.getInventory().setHeldItemSlot(ConfigHandler.getConfig("config.yml").getInt("HeldItem-Slot"));
		}
	}

	public static void setClearingOfItems(Player player, String world, String clearOn) {
		if (ConfigHandler.getConfig("config.yml").getString(clearOn) != null && ConfigHandler.getConfig("config.yml").getBoolean(clearOn) == true 
				&& ConfigHandler.getConfig("config.yml").getString("Clear-Items") != null && ConfigHandler.getConfig("config.yml").getString("Clear-Items").equalsIgnoreCase("All")) {
			if (ConfigHandler.getConfig("config.yml").getString("AllowOPBypass") != null && ConfigHandler.getConfig("config.yml").getBoolean("AllowOPBypass") == true && player.isOp()) {} else {
			setClearAllItems(player);
			}
		} else if (ConfigHandler.getConfig("config.yml").getString(clearOn) != null && ConfigHandler.getConfig("config.yml").getBoolean(clearOn) == true 
				&& ConfigHandler.getConfig("config.yml").getString("Clear-Items") != null && ConfigHandler.getConfig("config.yml").getString("Clear-Items").equalsIgnoreCase("ItemJoin")) {
			if (ConfigHandler.getConfig("config.yml").getString("AllowOPBypass") != null && ConfigHandler.getConfig("config.yml").getBoolean("AllowOPBypass") == true && player.isOp()) {} else {
			setClearItemJoinItems(player);
			}
		} else if (ConfigHandler.getConfig("config.yml").getBoolean(clearOn) == true) {
			ServerHandler.sendConsoleMessage("&cError; C122394");
			ServerHandler.sendConsoleMessage("&c" + ConfigHandler.getConfig("config.yml").getString("Clear-Items") + " for Clear-Items in the config.yml is not a valid option.");
		}
	}

	public static void setClearAllItems(Player player) {
				player.getInventory().clear();
				player.getInventory().setHelmet(null);
				player.getInventory().setChestplate(null);
				player.getInventory().setLeggings(null);
				player.getInventory().setBoots(null);
				if (ServerHandler.hasCombatUpdate()) {
				player.getInventory().setItemInOffHand(null);
				}
	}

	public static void setClearItemJoinItems(Player player) {
			PlayerInventory inventory = player.getInventory();
			if (inventory.getHelmet() != null && inventory.getHelmet().hasItemMeta() && inventory.getHelmet().getItemMeta().hasDisplayName() 
					&& ItemHandler.containsIgnoreCase(inventory.getHelmet().getItemMeta().getDisplayName(), ConfigHandler.encodeSecretData(ConfigHandler.getNBTData()))) {
				inventory.setHelmet(null);
			}
			if (inventory.getChestplate() != null && inventory.getChestplate().hasItemMeta() && inventory.getChestplate().getItemMeta().hasDisplayName() 
					&& ItemHandler.containsIgnoreCase(inventory.getChestplate().getItemMeta().getDisplayName(), ConfigHandler.encodeSecretData(ConfigHandler.getNBTData()))) {
				inventory.setChestplate(null);
			}
			if (inventory.getLeggings() != null && inventory.getLeggings().hasItemMeta() && inventory.getLeggings().getItemMeta().hasDisplayName() 
					&& ItemHandler.containsIgnoreCase(inventory.getLeggings().getItemMeta().getDisplayName(), ConfigHandler.encodeSecretData(ConfigHandler.getNBTData()))) {
				inventory.setLeggings(null);
			}
			if (inventory.getBoots() != null && inventory.getBoots().hasItemMeta() && inventory.getBoots().getItemMeta().hasDisplayName() 
					&& ItemHandler.containsIgnoreCase(inventory.getBoots().getItemMeta().getDisplayName(), ConfigHandler.encodeSecretData(ConfigHandler.getNBTData()))) {
				inventory.setBoots(null);
			}
			if (ServerHandler.hasCombatUpdate() && inventory.getItemInOffHand() != null 
					&& inventory.getItemInOffHand().hasItemMeta() && inventory.getItemInOffHand().getItemMeta().hasDisplayName() 
					&& ItemHandler.containsIgnoreCase(inventory.getItemInOffHand().getItemMeta().getDisplayName(), ConfigHandler.encodeSecretData(ConfigHandler.getNBTData()))) {
				inventory.setItemInOffHand(null);
			}
			HashMap < String, ItemStack[] > inventoryContents = new HashMap < String, ItemStack[] > ();
			inventoryContents.put(PlayerHandler.getPlayerID(player), inventory.getContents());
			for (ItemStack contents: inventoryContents.get(PlayerHandler.getPlayerID(player))) {
				if (contents != null && contents.hasItemMeta() && contents.getItemMeta().hasDisplayName() 
						&& ItemHandler.containsIgnoreCase(contents.getItemMeta().getDisplayName(), ConfigHandler.encodeSecretData(ConfigHandler.getNBTData()))) {
					inventory.remove(contents);
				}
			}
			inventoryContents.clear();
	}
	
	public static HashMap<Player, Integer> getFailCount() {
		return failCount;
	}
	
	public static void putFailCount(Player player, int i) {
		failCount.put(player, i);
	}
	
	public static void removeFailCount(Player player) {
		failCount.remove(player);
	}
}
