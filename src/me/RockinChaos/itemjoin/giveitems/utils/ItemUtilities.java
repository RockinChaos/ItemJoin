package me.RockinChaos.itemjoin.giveitems.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredListener;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.listeners.LimitSwitch;
import me.RockinChaos.itemjoin.giveitems.listeners.PlayerJoin;
import me.RockinChaos.itemjoin.giveitems.listeners.PlayerQuit;
import me.RockinChaos.itemjoin.giveitems.listeners.RegionEnter;
import me.RockinChaos.itemjoin.giveitems.listeners.Respawn;
import me.RockinChaos.itemjoin.giveitems.listeners.WorldSwitch;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.listeners.Consumes;
import me.RockinChaos.itemjoin.listeners.Drops;
import me.RockinChaos.itemjoin.listeners.Interact;
import me.RockinChaos.itemjoin.listeners.InventoryClick;
import me.RockinChaos.itemjoin.listeners.InventoryClose;
import me.RockinChaos.itemjoin.listeners.Legacy_Storable;
import me.RockinChaos.itemjoin.listeners.Placement;
import me.RockinChaos.itemjoin.listeners.Recipes;
import me.RockinChaos.itemjoin.listeners.Storable;
import me.RockinChaos.itemjoin.listeners.SwitchHands;
import me.RockinChaos.itemjoin.utils.Language;
import me.RockinChaos.itemjoin.utils.ProbabilityUtilities;
import me.RockinChaos.itemjoin.utils.Reflection;
import me.RockinChaos.itemjoin.utils.Utils;

public class ItemUtilities {
	
  	private static List < ItemMap > items = new ArrayList < ItemMap >();
	public static Map < String, Integer > probability = new HashMap < String, Integer > ();
	private static HashMap <Integer, Integer> failCount = new HashMap <Integer, Integer> ();
	
	private static String NBTData = "ItemJoin";
	
	private static boolean oldMapMethod = false;
	private static boolean oldMapViewMethod = false;

	public static boolean isAllowed(Player player, ItemStack item, String itemflag) {
		ItemMap fetched = getMappedItem(item, player.getWorld());
		if (fetched != null && fetched.isAllowedItem(player, item, itemflag)) {
			return false;
		}
		return true;
	}
	
	public static ItemMap getMappedItem(ItemStack lookUp, World world) {
		for (ItemMap item : ItemUtilities.getItems()) {
			if (item.inWorld(world) && item.isSimilar(lookUp)) {
				return item;
			}
		}
		return null;
	}
	
	public static ItemMap getMappedItem(String lookUp) {
		for (ItemMap item : ItemUtilities.getItems()) {
			if (item.getConfigName().equalsIgnoreCase(lookUp)) {
				return item;
			}
		}
		return null;
	}
	
	public static void closeAnimations(Player player) {
		for (ItemMap item : ItemUtilities.getItems()) {
			if (item.isAnimated() && item.getAnimationHandler().get(player) != null
					|| item.isDynamic() && item.getAnimationHandler().get(player) != null) {
				item.getAnimationHandler().get(player).closeAnimation(player);
				item.removeFromAnimationHandler(player);
			}
		}
	}
	
	public static void closeAllAnimations() {
		Collection < ? > playersOnlineNew = null;
		Player[] playersOnlineOld;
		try {
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
					playersOnlineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
					for (Object objPlayer: playersOnlineNew) {
						closeAnimations(((Player) objPlayer));
					}
				}
			} else {
				playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Player player: playersOnlineOld) {
					closeAnimations(player);
				}
			}
		} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
	}
	
	public static void addItem(ItemMap itemMap) {
		items.add(itemMap);
	}
	
	public static List < ItemMap > getItems() {
		return items;
	}
	
	public static void clearItems() {
		items = new ArrayList < ItemMap >();
	}
	
	public static void updateItems(Player player, boolean newAnimation) {
		for (ItemMap item: getItems()) {
			item.updateItem(player);
			if (newAnimation) {
				item.setAnimations(player);
			}
		}
	}
	
	public static void updateItems() {
		Collection < ? > playersOnlineNew = null;
		Player[] playersOnlineOld;
		try {
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
					playersOnlineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
					for (Object objPlayer: playersOnlineNew) {
						Player player = ((Player) objPlayer);
						updateItems(player, true);
					}
				}
			} else {
				playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Player player: playersOnlineOld) {
					updateItems(player, true);
				}
			}
		} catch (Exception e) {
			ServerHandler.sendDebugTrace(e);
		}
	}
	
	public static void safeSet(final Player player, final String type) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (type.equalsIgnoreCase("JOIN")) {
					ItemUtilities.setClearingOfItems(player, player.getWorld().getName(), "Join");
				} else if (type.equalsIgnoreCase("WORLD-SWITCH")) {
					ItemUtilities.setClearingOfItems(player, player.getWorld().getName(), "World-Switch");
				}
			}
		}, ConfigHandler.getClearDelay());
		if (!type.equalsIgnoreCase("Region-Enter") && !type.equalsIgnoreCase("Limit-Modes")) { PlayerHandler.setHeldItemSlot(player); }
	}
	
	public static String getProbabilityItem(Player player) {
		ProbabilityUtilities probabilities = new ProbabilityUtilities();
		if (!probability.isEmpty()) {
			for (String name: probability.keySet()) {
				for (ItemMap item: getItems()) {
					if (item.getConfigName().equalsIgnoreCase(name) && item.hasItem(player)) { return name; }
				}
				probabilities.addChance(name, probability.get(name));
			}
			return ((String) probabilities.getRandomElement());
		}
		return null;
	}
	
	public static boolean isChosenProbability(ItemMap itemMap, String probable) {
		if (probable != null && itemMap.getConfigName().equalsIgnoreCase(probable) || itemMap.getProbability().equals(-1)) {
			return true;
		}
		return false;
	}
	
	public static boolean hasProbabilityItem(Player player, ItemMap itemMap) {
		for (String probables: probability.keySet()) {
			for (ItemMap item: getItems()) {
				if (item.getConfigName().equalsIgnoreCase(probables) && item.hasItem(player)) {
					if (itemMap.equals(item) || !itemMap.getConfigName().equalsIgnoreCase(item.getConfigName())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static Boolean inClearingWorld(String world, String stringLoc) {
		if (ConfigHandler.getConfig("config.yml").getString("Clear-Items." + stringLoc) != null) {
			String worldlist = ConfigHandler.getConfig("config.yml").getString("Clear-Items." + stringLoc).replace(" ", "");
			String[] compareWorlds = worldlist.split(",");
			for (String compareWorld: compareWorlds) {
				if (compareWorld.equalsIgnoreCase(world) || compareWorld.equalsIgnoreCase("all") || compareWorld.equalsIgnoreCase("global")) {
					return true;
				}
			}
		} else if (ConfigHandler.getConfig("config.yml").getString("Clear-Items." + stringLoc) == null) {
			return true;
		}
		return false;
	}
	
	public static void setClearingOfItems(Player player, String world, String stringLoc) {
		if (ConfigHandler.getConfig("config.yml").getString("Clear-Items.Type").equalsIgnoreCase("ALL") || ConfigHandler.getConfig("config.yml").getString("Clear-Items.Type").equalsIgnoreCase("GLOBAL")) {
			if (ConfigHandler.getConfig("config.yml").getString("Clear-Items." + stringLoc) != null && inClearingWorld(world, stringLoc) 
					|| ConfigHandler.getConfig("config.yml").getString("Clear-Items." + stringLoc) != null && ConfigHandler.getConfig("config.yml").getBoolean("Clear-Items." + stringLoc) == true) {
				if (Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.Bypass"), "OP") && player.isOp()) {} else {
					setClearAllItems(player);
				}
			}
		} else if (ConfigHandler.getConfig("config.yml").getString("Clear-Items.Type").equalsIgnoreCase("ITEMJOIN")) {
			if (ConfigHandler.getConfig("config.yml").getString("Clear-Items." + stringLoc) != null && inClearingWorld(world, stringLoc) 
					|| ConfigHandler.getConfig("config.yml").getString("Clear-Items." + stringLoc) != null && ConfigHandler.getConfig("config.yml").getBoolean("Clear-Items." + stringLoc) == true) {
				if (Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.Bypass"), "OP") && player.isOp()) {} else {
					setClearItemJoinItems(player);
				}
			}
		} else if (ConfigHandler.getConfig("config.yml").getBoolean("Clear-Items." + stringLoc) == true || inClearingWorld(world, stringLoc)) {
			ServerHandler.sendErrorMessage("&c" + ConfigHandler.getConfig("config.yml").getString("Clear-Items.Type") + " for Clear-Items in the config.yml is not a valid option.");
		}
	}
	
	public static void setClearAllItems(Player player) {
		List<ItemMap> items = new ArrayList<ItemMap>();
		PlayerInventory inventory = player.getInventory();
		if (Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.Options"), "PROTECT")) {
			for (ItemMap item: ItemUtilities.getItems()) {
				if (item.isOnlyFirstJoin() || item.isOnlyFirstWorld()) {
					items.add(item);
				}
			}
			if (!items.isEmpty()) { inventoryAllWipe(player, items); }
			else {
				inventory.clear();
				inventory.setHelmet(null);
				inventory.setChestplate(null);
				inventory.setLeggings(null);
				inventory.setBoots(null);
				PlayerHandler.setOffHandItem(player, null);
			}
		} else {
			inventory.clear();
			inventory.setHelmet(null);
			inventory.setChestplate(null);
			inventory.setLeggings(null);
			inventory.setBoots(null);
			PlayerHandler.setOffHandItem(player, null);
		}
	}
	
	public static void setClearItemJoinItems(Player player) {
		PlayerInventory inventory = player.getInventory();
		if (Utils.containsIgnoreCase(ConfigHandler.getConfig("config.yml").getString("Clear-Items.Options"), "PROTECT")) {
			for (ItemMap item: ItemUtilities.getItems()) {
				if (!item.isOnlyFirstJoin() && !item.isOnlyFirstWorld()) {
					inventoryWipe(item, player);
				}
			}
		} else {
			if (inventory.getHelmet() != null && ItemHandler.containsNBTData(inventory.getHelmet())) {
				inventory.setHelmet(null);
			}
			if (inventory.getChestplate() != null && ItemHandler.containsNBTData(inventory.getChestplate())) {
				inventory.setChestplate(null);
			}
			if (inventory.getLeggings() != null && ItemHandler.containsNBTData(inventory.getLeggings())) {
				inventory.setLeggings(null);
			}
			if (inventory.getBoots() != null && ItemHandler.containsNBTData(inventory.getBoots())) {
				inventory.setBoots(null);
			}
			if (ServerHandler.hasCombatUpdate() && inventory.getItemInOffHand() != null && ItemHandler.containsNBTData(inventory.getItemInOffHand())) {
				inventory.setItemInOffHand(null);
			}
			HashMap < String, ItemStack[] > inventoryContents = new HashMap < String, ItemStack[] > ();
			inventoryContents.put(PlayerHandler.getPlayerID(player), inventory.getContents());
			for (ItemStack contents: inventoryContents.get(PlayerHandler.getPlayerID(player))) {
				if (contents != null && ItemHandler.containsNBTData(contents)) {
					inventory.remove(contents);
				}
			}
			inventoryContents.clear();
		}
	}
	
	public static void inventoryAllWipe(Player player, List<ItemMap> items) {
		PlayerInventory inventory = player.getInventory();
		if (inventory.getHelmet() != null) {
			for (int i = 0; i < items.size(); i++) {
				ItemMap item = items.get(i);
				if (!item.isSimilar(inventory.getHelmet())) {
					if (i == (items.size() - 1)) {
						inventory.setHelmet(null);
					}
				}
			}
		}
		if (inventory.getChestplate() != null) {
			for (int i = 0; i < items.size(); i++) {
				ItemMap item = items.get(i);
				if (!item.isSimilar(inventory.getChestplate())) {
					if (i == (items.size() - 1)) {
						inventory.setChestplate(null);
					}
				}
			}
		}
		if (inventory.getLeggings() != null) {
			for (int i = 0; i < items.size(); i++) {
				ItemMap item = items.get(i);
				if (!item.isSimilar(inventory.getLeggings())) {
					if (i == (items.size() - 1)) {
						inventory.setLeggings(null);
					}
				}
			}
		}
		if (inventory.getBoots() != null) {
			for (int i = 0; i < items.size(); i++) {
				ItemMap item = items.get(i);
				if (!item.isSimilar(inventory.getBoots())) {
					if (i == (items.size() - 1)) {
						inventory.setBoots(null);
					}
				}
			}
		}
		if (ServerHandler.hasCombatUpdate() && inventory.getItemInOffHand() != null) {
			for (int i = 0; i < items.size(); i++) {
				ItemMap item = items.get(i);
				if (!item.isSimilar(inventory.getItemInOffHand())) {
					if (i == (items.size() - 1)) {
						inventory.setItemInOffHand(null);
					}
				}
			}
		}
		HashMap < String, ItemStack[] > inventoryContents = new HashMap < String, ItemStack[] > ();
		inventoryContents.put(PlayerHandler.getPlayerID(player), inventory.getContents());
		for (ItemStack contents: inventoryContents.get(PlayerHandler.getPlayerID(player))) {
			if (contents != null) {
				for (int i = 0; i < items.size(); i++) {
					ItemMap item = items.get(i);
					if (!item.isSimilar(contents)) {
						if (i == (items.size() - 1)) {
							inventory.remove(contents);
						}
					}
				}
			}
		}
		inventoryContents.clear();
	}
	
	public static void inventoryWipe(ItemMap item, Player player) {
		PlayerInventory inventory = player.getInventory();
		if (inventory.getHelmet() != null && item.isSimilar(inventory.getHelmet()) && ItemHandler.containsNBTData(inventory.getHelmet())) {
			inventory.setHelmet(null);
		}
		if (inventory.getChestplate() != null && item.isSimilar(inventory.getChestplate()) && ItemHandler.containsNBTData(inventory.getChestplate())) {
			inventory.setChestplate(null);
		}
		if (inventory.getLeggings() != null && item.isSimilar(inventory.getLeggings()) && ItemHandler.containsNBTData(inventory.getLeggings())) {
			inventory.setLeggings(null);
		}
		if (inventory.getBoots() != null && item.isSimilar(inventory.getBoots()) && ItemHandler.containsNBTData(inventory.getBoots())) {
			inventory.setBoots(null);
		}
		if (ServerHandler.hasCombatUpdate() && inventory.getItemInOffHand() != null && item.isSimilar(inventory.getItemInOffHand()) && ItemHandler.containsNBTData(inventory.getItemInOffHand())) {
			inventory.setItemInOffHand(null);
		}
		HashMap < String, ItemStack[] > inventoryContents = new HashMap < String, ItemStack[] > ();
		inventoryContents.put(PlayerHandler.getPlayerID(player), inventory.getContents());
		for (ItemStack contents: inventoryContents.get(PlayerHandler.getPlayerID(player))) {
			if (contents != null && item.isSimilar(contents) && ItemHandler.containsNBTData(contents)) {
				inventory.remove(contents);
			}
		}
		inventoryContents.clear();
	}
	
	public static void sendFailCount(Player player, int session) {
		if (getFailCount().get(session) != null && getFailCount().get(session) != 0) {
			if (ConfigHandler.getConfig("items.yml").getString("items-Overwrite") != null && isOverwriteWorld(player.getWorld().getName()) 
					|| ConfigHandler.getConfig("items.yml").getString("items-Overwrite") != null && ConfigHandler.getConfig("items.yml").getBoolean("items-Overwrite")) {
				String[] placeHolders = Language.newString(); placeHolders[7] = getFailCount().get(session).toString();
				Language.sendLangMessage("General.failedInventory", player, placeHolders);
			} else {
				String[] placeHolders = Language.newString(); placeHolders[7] = getFailCount().get(session).toString();
				Language.sendLangMessage("General.failedOverwrite", player, placeHolders);
			}
			removeFailCount(session);
		}
	}
	
	public static HashMap < Integer, Integer > getFailCount() {
		return failCount;
	}
	
	public static void putFailCount(int session, int i) {
		failCount.put(session, i);
	}
	
	public static void removeFailCount(int session) {
		failCount.remove(session);
	}
	
	public static Boolean isObtainable(Player player, ItemMap itemMap, int session) {
		if (itemMap.getProbability().equals(-1) || !itemMap.getProbability().equals(-1) && probability.containsKey(itemMap.getConfigName()) && !hasProbabilityItem(player, itemMap)) {
			if (!itemMap.hasItem(player) || itemMap.isAlwaysGive() || !itemMap.isLimitMode(player.getGameMode())) {
				boolean firstJoin = ConfigHandler.getSQLData().hasFirstJoined(player, itemMap);
				boolean firstWorld = ConfigHandler.getSQLData().hasFirstWorld(player, itemMap);
				boolean ipLimited = ConfigHandler.getSQLData().isIPLimited(player, itemMap);
				if (itemMap.isLimitMode(player.getGameMode())) {
					if (Utils.isInt(itemMap.getSlot()) && Integer.parseInt(itemMap.getSlot()) >= 0 && Integer.parseInt(itemMap.getSlot()) <= 35) {
						if (!firstJoin && !firstWorld && !ipLimited && canOverwrite(player, itemMap)) {
							return true;
						}
					} else if (ItemHandler.isCustomSlot(itemMap.getSlot())) {
						if (!firstJoin && !firstWorld && !ipLimited && canOverwrite(player, itemMap)) {
							return true;
						}
					}
					if (!firstJoin && !firstWorld && !ipLimited) {
						if (session != 0 && getFailCount().get(session) != null) {
						putFailCount(session, getFailCount().get(session) + 1);
						} else if (session != 0) { putFailCount(session, 1); }
						ServerHandler.sendDebugMessage(player.getName() + " has failed to receive item; " + itemMap.getConfigName());
					} else {
						if (firstJoin) {
							ServerHandler.sendDebugMessage(player.getName() + " has already received first-join " + itemMap.getConfigName() + ", they can no longer recieve this.");
						} else if (firstWorld) {
							ServerHandler.sendDebugMessage(player.getName() + " has already received first-world " + itemMap.getConfigName() + ", they can no longer recieve this in " + player.getWorld().getName());
						} else if (ipLimited) {
							ServerHandler.sendDebugMessage(player.getName() + " has already received ip-limited " + itemMap.getConfigName() + ", they will only recieve this on their dedicated ip."); 
						}
					}
					return false;
				} else { return false; }
			}
		} else { return false; }
		ServerHandler.sendDebugMessage(player.getName() + " already has item; " + itemMap.getConfigName());
		return false;
	}
	
	public static Boolean isOverwriteWorld(String world) {
		if (ConfigHandler.getConfig("items.yml").getString("items-Overwrite") != null) {
			String worldlist = ConfigHandler.getConfig("items.yml").getString("items-Overwrite").replace(" ", "");
			String[] compareWorlds = worldlist.split(",");
			for (String compareWorld: compareWorlds) {
				if (compareWorld.equalsIgnoreCase(world) || compareWorld.equalsIgnoreCase("all") || compareWorld.equalsIgnoreCase("global")) {
					return true;
				}
			}
		} else if (ConfigHandler.getConfig("items.yml").getString("items-Overwrite") == null) {
			return true;
		}
		return false;
	}
	
	public static Boolean isOverwrite(Player player) {
		if (ConfigHandler.getConfig("items.yml").getString("items-Overwrite") != null && isOverwriteWorld(player.getWorld().getName()) 
				|| ConfigHandler.getConfig("items.yml").getString("items-Overwrite") != null && ConfigHandler.getConfig("items.yml").getBoolean("items-Overwrite")) {
			return true;
		}
		return false;
	}
	
	public static Boolean canOverwrite(Player player, ItemMap itemMap) {
		try {
			if (itemMap.isOverwritable() || isOverwrite(player)) { return true; }
			if (Utils.isInt(itemMap.getSlot()) && player.getInventory().getItem(Integer.parseInt(itemMap.getSlot())) != null) {
				if (itemMap.isGiveNext() && player.getInventory().firstEmpty() == -1) { return false; }
				else if (!itemMap.isGiveNext()) { return false; }
			} else if (ItemHandler.isCustomSlot(itemMap.getSlot())) {
				if (itemMap.getSlot().equalsIgnoreCase("Arbitrary") && player.getInventory().firstEmpty() == -1) {
					return false;
				} else if (itemMap.getSlot().equalsIgnoreCase("Helmet") && player.getInventory().getHelmet() != null) {
					return false;
				} else if (itemMap.getSlot().equalsIgnoreCase("Chestplate") && player.getInventory().getChestplate() != null) {
					return false;
				} else if (itemMap.getSlot().equalsIgnoreCase("Leggings") && player.getInventory().getLeggings() != null) {
					return false;
				} else if (itemMap.getSlot().equalsIgnoreCase("Boots") && player.getInventory().getBoots() != null) {
					return false;
				} else if (ServerHandler.hasCombatUpdate() && itemMap.getSlot().equalsIgnoreCase("Offhand")) {
					if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
						return false;
					}
				} else if (getSlotConversion(itemMap.getSlot()) != 5 
						&& player.getOpenInventory().getTopInventory().getItem(getSlotConversion(itemMap.getSlot())) != null 
						&& player.getOpenInventory().getTopInventory().getItem(getSlotConversion(itemMap.getSlot())).getType() != Material.AIR) {
						return false;
				}
			}
		} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		return true;
	}
	
	public static void setInvSlots(Player player, ItemMap itemMap, boolean noTriggers, ItemStack item, int amount) {
		if (amount != 0 || itemMap.isAlwaysGive()) {
			if (noTriggers) { item.setAmount(amount); }
			if (itemMap.hasItem(player)) { setDirectSlots(player, itemMap, item, true);
			} else { setDirectSlots(player, itemMap, item, false); }
		} else { setDirectSlots(player, itemMap, item, false); }
		saveSQLItemData(player, itemMap);
		ServerHandler.sendDebugMessage("Given the Item; " + itemMap.getConfigName());
	}
	
	private static void setDirectSlots(Player player, ItemMap itemMap, ItemStack item, boolean addItem) {
		if (itemMap.isGiveNext() && player.getInventory().getItem(Integer.parseInt(itemMap.getSlot())) != null) {
			for (int i = Integer.parseInt(itemMap.getSlot()); i <= 35; i++) {
				if (player.getInventory().getItem(i) == null) { player.getInventory().setItem(i, item); break; }
				else if (i == 35) {
					for (int k = Integer.parseInt(itemMap.getSlot()); k >= 0; k--) {
						if (player.getInventory().getItem(k) == null) { player.getInventory().setItem(k, item); break; }
					}
				}
			}
		} else if (addItem) { player.getInventory().addItem(item); } 
		else { player.getInventory().setItem(Integer.parseInt(itemMap.getSlot()), item); }
	}
	
	public static void setCustomSlots(Player player, ItemMap itemMap, boolean noTriggers, ItemStack item, int amount) {
		EntityEquipment Equip = player.getEquipment();
			if (itemMap.getSlot().equalsIgnoreCase("Arbitrary")) {
				if (amount != 0 && noTriggers) { item.setAmount(amount); }
				player.getInventory().addItem(item);
				ServerHandler.sendDebugMessage("Given the Item; [" + itemMap.getConfigName() + "]");
				saveSQLItemData(player, itemMap);
			} else if (itemMap.getSlot().equalsIgnoreCase("Helmet")) {
				if (amount != 0 || itemMap.isAlwaysGive()) {
					if (noTriggers) { item.setAmount(amount); }
					if (itemMap.hasItem(player)) { player.getInventory().addItem(item);
					} else { Equip.setHelmet(item); }
				} else { Equip.setHelmet(item); }
				ServerHandler.sendDebugMessage("Given the Item; [" + itemMap.getConfigName() + "]");
				saveSQLItemData(player, itemMap);
			} else if (itemMap.getSlot().equalsIgnoreCase("Chestplate")) {
				if (amount != 0 || itemMap.isAlwaysGive()) {
					if (noTriggers) { item.setAmount(amount); }
					if (itemMap.hasItem(player)) { player.getInventory().addItem(item);
					} else { Equip.setChestplate(item); }
				} else { Equip.setChestplate(item); }
				ServerHandler.sendDebugMessage("Given the Item; [" + itemMap.getConfigName() + "]");
				saveSQLItemData(player, itemMap);
			} else if (itemMap.getSlot().equalsIgnoreCase("Leggings")) {
				if (amount != 0 || itemMap.isAlwaysGive()) {
					if (noTriggers) { item.setAmount(amount); }
					if (itemMap.hasItem(player)) { player.getInventory().addItem(item);
					} else { Equip.setLeggings(item); }
				} else { Equip.setLeggings(item); }
				ServerHandler.sendDebugMessage("Given the Item; [" + itemMap.getConfigName() + "]");
				saveSQLItemData(player, itemMap);
			} else if (itemMap.getSlot().equalsIgnoreCase("Boots")) {
				if (amount != 0 || itemMap.isAlwaysGive()) {
					if (noTriggers) { item.setAmount(amount); }
					if (itemMap.hasItem(player)) { player.getInventory().addItem(item);
					} else { Equip.setBoots(item); }
				} else { Equip.setBoots(item); }
				ServerHandler.sendDebugMessage("Given the Item; [" + itemMap.getConfigName() + "]");
				saveSQLItemData(player, itemMap);
			} else if (ServerHandler.hasCombatUpdate() && itemMap.getSlot().equalsIgnoreCase("Offhand")) {
				if (amount != 0 || itemMap.isAlwaysGive()) {
					if (noTriggers) { item.setAmount(amount); }
					if (itemMap.hasItem(player)) { player.getInventory().addItem(item);
					} else { PlayerHandler.setOffhandItem(player, item); }
				} else { PlayerHandler.setOffhandItem(player, item); }
				ServerHandler.sendDebugMessage("Given the Item; [" + itemMap.getConfigName() + "]");
				saveSQLItemData(player, itemMap);
			} else if (getSlotConversion(itemMap.getSlot()) != 5) {
				if (amount != 0 || itemMap.isAlwaysGive()) {
					if (noTriggers) { item.setAmount(amount); }
					if (itemMap.hasItem(player)) { player.getInventory().addItem(item);
					} else { player.getOpenInventory().getTopInventory ().setItem(getSlotConversion(itemMap.getSlot()), item); }
				} else { player.getOpenInventory().getTopInventory ().setItem(getSlotConversion(itemMap.getSlot()), item); }
				ServerHandler.sendDebugMessage("Given the Item; [" + itemMap.getConfigName() + "]");
				saveSQLItemData(player, itemMap);
			}
	}
	
	public static int getSlotConversion(String str) {
		if (str.equalsIgnoreCase("CRAFTING[0]") || str.equalsIgnoreCase("C[0]") || str.equalsIgnoreCase("C(0)")) {
			return 0;
		} else if (str.equalsIgnoreCase("CRAFTING[1]") || str.equalsIgnoreCase("C[1]") || str.equalsIgnoreCase("C(1)")) {
			return 1;
		} else if (str.equalsIgnoreCase("CRAFTING[2]") || str.equalsIgnoreCase("C[2]") || str.equalsIgnoreCase("C(2)")) {
			return 2;
		} else if (str.equalsIgnoreCase("CRAFTING[3]") || str.equalsIgnoreCase("C[3]") || str.equalsIgnoreCase("C(3)")) {
			return 3;
		} else if (str.equalsIgnoreCase("CRAFTING[4]") || str.equalsIgnoreCase("C[4]") || str.equalsIgnoreCase("C(4)")) {
			return 4;
		}
		return 5;
	}

	public static void setListenerRestrictions(ItemMap itemMap) {
		if (((!itemMap.isGiveOnDisabled() && itemMap.isGiveOnJoin()) || (!ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("DISABLED") 
		|| !ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("FALSE"))) && !isListenerEnabled(PlayerJoin.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerJoin(), ItemJoin.getInstance()); }
		if (!itemMap.isGiveOnDisabled() && itemMap.isGiveOnRespawn() && !isListenerEnabled(Respawn.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Respawn(), ItemJoin.getInstance()); }
		if (!itemMap.isGiveOnDisabled() && itemMap.isGiveOnWorldChange() && !isListenerEnabled(WorldSwitch.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new WorldSwitch(), ItemJoin.getInstance()); }
		if (!itemMap.isGiveOnDisabled() && (itemMap.isGiveOnRegionEnter() || itemMap.isTakeOnRegionLeave()) && !isListenerEnabled(RegionEnter.class.getSimpleName()) && ConfigHandler.getDepends().getGuard().guardEnabled()) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new RegionEnter(), ItemJoin.getInstance()); }
		if (!itemMap.isGiveOnDisabled() && itemMap.isUseOnLimitSwitch() && !isListenerEnabled(LimitSwitch.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new LimitSwitch(), ItemJoin.getInstance()); }
		if ((itemMap.isAnimated() || itemMap.isDynamic()) && !isListenerEnabled(PlayerQuit.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new PlayerQuit(), ItemJoin.getInstance()); }
		if (itemMap.isInventoryClose() && !isListenerEnabled(InventoryClose.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new InventoryClose(), ItemJoin.getInstance()); }
		if ((itemMap.isMovement() || itemMap.isInventoryClose()) && !isListenerEnabled(InventoryClick.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new InventoryClick(), ItemJoin.getInstance()); }
		if ((itemMap.isDeathDroppable() || itemMap.isSelfDroppable()) && !isListenerEnabled(Drops.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Drops(), ItemJoin.getInstance()); }
		if ((itemMap.isCancelEvents() || (itemMap.getCommands() != null && itemMap.getCommands().length != 0)) && !isListenerEnabled(Interact.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Interact(), ItemJoin.getInstance()); }
		if ((itemMap.isPlacement() || itemMap.isCountLock()) && !isListenerEnabled(Placement.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Placement(), ItemJoin.getInstance()); }
		if (itemMap.isCustomConsumable() && !isListenerEnabled(Consumes.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Consumes(), ItemJoin.getInstance()); }
		if ((itemMap.isItemRepairable() || itemMap.isItemCraftable()) && !isListenerEnabled(Recipes.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Recipes(), ItemJoin.getInstance()); }
		if (itemMap.isItemStore() || itemMap.isItemModify()) {
			if (!ServerHandler.hasSpecificUpdate("1_8") && !isListenerEnabled(Legacy_Storable.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Legacy_Storable(), ItemJoin.getInstance());} 
			else if (!isListenerEnabled(Storable.class.getSimpleName())) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Storable(), ItemJoin.getInstance()); }
		}
		if (itemMap.isMovement() && !isListenerEnabled(SwitchHands.class.getSimpleName()) && ServerHandler.hasCombatUpdate() && Reflection.getEventClass("player.PlayerSwapHandItemsEvent") != null) { ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new SwitchHands(), ItemJoin.getInstance()); }
	}

	private static boolean isListenerEnabled(String compare) {
		boolean returnValue = false;
        ArrayList<RegisteredListener> rls = HandlerList.getRegisteredListeners(ItemJoin.getInstance());
        for(RegisteredListener rl: rls) {
        	if (rl.getListener().getClass().getSimpleName().equalsIgnoreCase(compare)) {
        		returnValue = true; break;
        	}
        }
		return returnValue;
	}
	
	private static void saveSQLItemData(Player player, ItemMap itemMap) {
		ConfigHandler.getSQLData().saveFirstJoinData(player, itemMap);
		ConfigHandler.getSQLData().saveFirstWorldData(player, itemMap);
		ConfigHandler.getSQLData().saveIpLimitData(player, itemMap);
	}
	
	public static void setMapMethod(boolean bool) {
		oldMapMethod = bool;
	}
	
	public static void setMapViewMethod(boolean bool) {
		oldMapViewMethod = bool;
	}
	
	public static boolean getMapMethod() {
		return oldMapMethod;
	}
	
	public static boolean getMapViewMethod() {
		return oldMapViewMethod;
	}
	
	public static int getHeldSlot() {
		if (!ConfigHandler.getConfig("config.yml").getString("Settings.HeldItem-Slot").equalsIgnoreCase("DISABLED")) {
			return ConfigHandler.getConfig("config.yml").getInt("Settings.HeldItem-Slot");
		}
		return -1;
	}
	
	public static String getNBTData(ItemMap itemMap) {
		if (itemMap != null) {
			return NBTData + itemMap.getItemValue() + itemMap.getConfigName();
		} else { return NBTData; }
	}
	
	public static boolean dataTagsEnabled() {
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			return ConfigHandler.getConfig("config.yml").getBoolean("Settings.DataTags");
		}
		return false;
	}
}