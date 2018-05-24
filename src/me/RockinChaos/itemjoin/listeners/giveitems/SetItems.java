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
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.Language;
import me.RockinChaos.itemjoin.utils.ProbabilityUtilities;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

public class SetItems {
	private static HashMap <Player, Integer> failCount = new HashMap <Player, Integer> ();

	public static void setInvSlots(Player player, String item, String slot, String ItemID) {
		ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
			player.getInventory().setItem(Integer.parseInt(slot), inStoredItems);
			SQLData.saveAllToDatabase(player, item);
			ServerHandler.sendDebugMessage("Given the Item; " + item);
	}

	public static void setCustomSlots(Player player, String item, String slot, String ItemID) {
		EntityEquipment Equip = player.getEquipment();
		ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
		if (inStoredItems != null) {
			if (slot.equalsIgnoreCase("Arbitrary")) {
				player.getInventory().addItem(inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; [" + item + "]");
				SQLData.saveAllToDatabase(player, item);
			} else if (slot.equalsIgnoreCase("Helmet")) {
				Equip.setHelmet(inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; [" + item + "]");
				SQLData.saveAllToDatabase(player, item);
			} else if (slot.equalsIgnoreCase("Chestplate")) {
				Equip.setChestplate(inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; [" + item + "]");
				SQLData.saveAllToDatabase(player, item);
			} else if (slot.equalsIgnoreCase("Leggings")) {
				Equip.setLeggings(inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; [" + item + "]");
				SQLData.saveAllToDatabase(player, item);
			} else if (slot.equalsIgnoreCase("Boots")) {
				Equip.setBoots(inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; [" + item + "]");
				SQLData.saveAllToDatabase(player, item);
			} else if (ServerHandler.hasCombatUpdate() && slot.equalsIgnoreCase("Offhand")) {
				PlayerHandler.setOffhandItem(player, inStoredItems);
				ServerHandler.sendDebugMessage("Given the Item; [" + item + "]");
				SQLData.saveAllToDatabase(player, item);
			}
		}
	}

	public static String setProbabilityItems(Player player) {
		ProbabilityUtilities probabilities = new ProbabilityUtilities();
		for (String name : CreateItems.probability.keySet()) {
			probabilities.addChance(name, CreateItems.probability.get(name));
		}
		return (String) probabilities.getRandomElement();
	}
	
	public static void setHeldItemSlot(Player player) {
		if (ConfigHandler.getConfig("config.yml").getString("HeldItem-Slot") != null 
				&& Utils.isInt(ConfigHandler.getConfig("config.yml").getString("HeldItem-Slot")) 
				&& ConfigHandler.getConfig("config.yml").getInt("HeldItem-Slot") <= 8 && ConfigHandler.getConfig("config.yml").getInt("HeldItem-Slot") >= 0) {
			player.getInventory().setHeldItemSlot(ConfigHandler.getConfig("config.yml").getInt("HeldItem-Slot"));
		}
	}

	public static void setClearingOfItems(Player player, String world, String clearOn) {
		if (ConfigHandler.getConfig("config.yml").getString("Clear-Items") != null && ConfigHandler.getConfig("config.yml").getString("Clear-Items").equalsIgnoreCase("All")) {
		if (ConfigHandler.getConfig("config.yml").getString(clearOn) != null && WorldHandler.inGlobalWorld(world, clearOn)  
				|| ConfigHandler.getConfig("config.yml").getString(clearOn) != null && ConfigHandler.getConfig("config.yml").getBoolean(clearOn) == true) {
			if (ConfigHandler.getConfig("config.yml").getString("AllowOPBypass") != null && ConfigHandler.getConfig("config.yml").getBoolean("AllowOPBypass") == true && player.isOp()) {} else {
			setClearAllItems(player);
			}}
		} else if (ConfigHandler.getConfig("config.yml").getString("Clear-Items") != null && ConfigHandler.getConfig("config.yml").getString("Clear-Items").equalsIgnoreCase("ItemJoin")) {
			if (ConfigHandler.getConfig("config.yml").getString(clearOn) != null && WorldHandler.inGlobalWorld(world, clearOn) 
					|| ConfigHandler.getConfig("config.yml").getString(clearOn) != null && ConfigHandler.getConfig("config.yml").getBoolean(clearOn) == true) {
			if (ConfigHandler.getConfig("config.yml").getString("AllowOPBypass") != null && ConfigHandler.getConfig("config.yml").getBoolean("AllowOPBypass") == true && player.isOp()) {} else {
			setClearItemJoinItems(player);
			}}
		} else if (ConfigHandler.getConfig("config.yml").getBoolean(clearOn) == true || WorldHandler.inGlobalWorld(world, clearOn)) {
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
		if (ServerHandler.hasCombatUpdate()) { player.getInventory().setItemInOffHand(null); }
	}

	public static void setClearItemJoinItems(Player player) {
			PlayerInventory inventory = player.getInventory();
			if (inventory.getHelmet() != null && ItemHandler.hasNBTData(inventory.getHelmet())) {
				inventory.setHelmet(null);
			}
			if (inventory.getChestplate() != null && ItemHandler.hasNBTData(inventory.getChestplate())) {
				inventory.setChestplate(null);
			}
			if (inventory.getLeggings() != null && ItemHandler.hasNBTData(inventory.getLeggings())) {
				inventory.setLeggings(null);
			}
			if (inventory.getBoots() != null &&  ItemHandler.hasNBTData(inventory.getBoots())) {
				inventory.setBoots(null);
			}
			if (ServerHandler.hasCombatUpdate() && inventory.getItemInOffHand() != null && ItemHandler.hasNBTData(inventory.getItemInOffHand())) {
				inventory.setItemInOffHand(null);
			}
			HashMap < String, ItemStack[] > inventoryContents = new HashMap < String, ItemStack[] > ();
			inventoryContents.put(PlayerHandler.getPlayerID(player), inventory.getContents());
			for (ItemStack contents: inventoryContents.get(PlayerHandler.getPlayerID(player))) {
				if (contents != null && ItemHandler.hasNBTData(contents)) {
					inventory.remove(contents);
				}
			}
			inventoryContents.clear();
	}
	
	public static void itemsOverwrite(Player player) {
		if (SetItems.getFailCount().get(player) != null && SetItems.getFailCount().get(player) != 0) {
			if (ConfigHandler.getConfig("items.yml").getString("items-Overwrite") != null && WorldHandler.isOverwriteWorld(player.getWorld().getName()) 
					|| ConfigHandler.getConfig("items.yml").getString("items-Overwrite") != null && ConfigHandler.getConfig("items.yml").getBoolean("items-Overwrite") == true) {
				Language.getSendMessage(player, "failedInvFull", SetItems.getFailCount().get(player).toString());
			} else {
				Language.getSendMessage(player, "failedOverwrite", SetItems.getFailCount().get(player).toString());
				}
			SetItems.removeFailCount(player);
		}
		
		
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
