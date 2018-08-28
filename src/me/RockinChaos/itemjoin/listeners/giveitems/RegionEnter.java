package me.RockinChaos.itemjoin.listeners.giveitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.AnimationHandler;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Legacy;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

public class RegionEnter implements Listener {
	private static HashMap < Player, String > isInRegion = new HashMap < Player, String > ();
	private static List < String > regions = new ArrayList < String > ();
	
	@EventHandler
	public void giveOnRegionEnter(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (Hooks.hasWorldGuard() == true && SQLData.isEnabled(player)) {
			if (!isInRegion(player) && isInRegion.get(player) != null) {
				isInRegion.remove(player);
				long delay = ConfigHandler.getConfig("items.yml").getInt("items-Delay") * 10L;
				Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
					public void run() {
						removeEnterItems(player, 1);
						String[] regions = getRegions().toString().split(",");
						for (String region: regions) {
							String getRegion = region.replace("[", "").replace("]", "").replace(" ", "");
							if (!CheckInRegion(player.getWorld(), player.getLocation(), getRegion)) {
								setEnterItems(player, region, 2);
							}
						}
					}
				}, delay);
			}
		}
	}
	
	public static void setItems(final Player player, final String region) {
		final long delay = ConfigHandler.getConfig("items.yml").getInt("items-Delay") * 10L;
		CreateItems.run(player);
		SetItems.putFailCount(player, 0);
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			public void run() {
				try {
					removeEnterItems(player, 2);
					setEnterItems(player, region, 1);
					SetItems.itemsOverwrite(player);
					PlayerHandler.delayUpdateInventory(player, 15L);
				} catch (NullPointerException e) {
					if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
				}
				AnimationHandler.OpenAnimations(player);
			}
		}, delay);
	}
	
	public static void removeEnterItems(Player player, int step) {
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				if (step == 1) {
					if (ItemHandler.containsIgnoreCase(items.getString(".triggers"), "region-enter") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "region enter") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "enter-region") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "enter region")) {
						toRemove(items, item, player);
					}
				} else if (step == 2) {
					if (ItemHandler.containsIgnoreCase(items.getString(".triggers"), "region-remove") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "region remove") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "remove-region") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "remove region")) {
						toRemove(items, item, player);
					}
				}
			}
		}
	}
	
	public static void toRemove(ConfigurationSection items, String item, Player player) {
		if (items.getString(".slot") != null) {
			String slotlist = items.getString(".slot").replace(" ", "");
			String[] slots = slotlist.split(",");
			final String world = player.getWorld().getName();
			ItemHandler.clearItemID(player);
			for (String slot: slots) {
				String ItemID = ItemHandler.getItemID(player, slot);
				ItemStack inStoredItems = CreateItems.items.get(world + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
				if (Utils.isCustomSlot(slot)) {
					PlayerInventory inventory = player.getInventory();
					if (inventory.getHelmet() != null && ItemHandler.isSimilar(inventory.getHelmet(), inStoredItems)) {
						inventory.setHelmet(null);
					}
					if (inventory.getChestplate() != null && ItemHandler.isSimilar(inventory.getChestplate(), inStoredItems)) {
						inventory.setChestplate(null);
					}
					if (inventory.getLeggings() != null && ItemHandler.isSimilar(inventory.getLeggings(), inStoredItems)) {
						inventory.setLeggings(null);
					}
					if (inventory.getBoots() != null && ItemHandler.isSimilar(inventory.getBoots(), inStoredItems)) {
						inventory.setBoots(null);
					}
					if (ServerHandler.hasCombatUpdate() && inventory.getItemInOffHand() != null && ItemHandler.isSimilar(inventory.getItemInOffHand(), inStoredItems)) {
						inventory.setItemInOffHand(null);
					}
				} else if (Utils.isInt(slot)) {
					PlayerInventory inventory = player.getInventory();
					HashMap < String, ItemStack[] > inventoryContents = new HashMap < String, ItemStack[] > ();
					inventoryContents.put(PlayerHandler.getPlayerID(player), inventory.getContents());
					for (ItemStack contents: inventoryContents.get(PlayerHandler.getPlayerID(player))) {
						if (contents != null && ItemHandler.isSimilar(contents, inStoredItems)) {
							inventory.remove(contents);
						}
					}
					inventoryContents.clear();
				}
			}
		}
	}
	
	public static void setEnterItems(Player player, String region, int step) {
		String nameProbability = SetItems.setProbabilityItems(player);
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				final String world = player.getWorld().getName();
				if (WorldHandler.inWorld(items, world) && PermissionsHandler.hasItemsPermission(items, item, player)) {
					if (step == 1) {
						if (ItemHandler.containsIgnoreCase(items.getString(".triggers"), "region-enter") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "region enter") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "enter-region") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "enter region")) {
							if (ItemHandler.containsIgnoreCase(items.getString(".enabled-regions"), region)) {
								toEnter(items, item, player, nameProbability);
							}
						}
					} else if (step == 2) {
						if (ItemHandler.containsIgnoreCase(items.getString(".triggers"), "region-remove") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "region remove") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "remove-region") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "remove region")) {
							if (!ItemHandler.containsIgnoreCase(items.getString(".enabled-regions"), region)) {
								toEnter(items, item, player, nameProbability);
							}
						}
					}
				}
			}
		}
	}
	
	public static void toEnter(ConfigurationSection items, String item, Player player, String nameProbability) {
	  if (items.getString(".probability") != null && item.equalsIgnoreCase(nameProbability) || items.getString(".probability") == null) {
		if (items.getString(".slot") != null) {
			String slotlist = items.getString(".slot").replace(" ", "");
			String[] slots = slotlist.split(",");
			ItemHandler.clearItemID(player);
			for (String slot: slots) {
				String ItemID = ItemHandler.getItemID(player, slot);
				ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
				if (Utils.isCustomSlot(slot) && ItemHandler.isObtainable(player, items, item, slot, ItemID, inStoredItems)) {
					SetItems.setCustomSlots(player, item, slot, ItemID);
				} else if (Utils.isInt(slot) && ItemHandler.isObtainable(player, items, item, slot, ItemID, inStoredItems)) {
					SetItems.setInvSlots(player, item, slot, ItemID);
				}
			}
		}
	  }
	}
	
	public static boolean isInRegion(Player player) {
		String[] regions = getRegions().toString().split(",");
		for (String region: regions) {
			String getRegion = region.replace("[", "").replace("]", "").replace(" ", "");
			if (CheckInRegion(player.getWorld(), player.getLocation(), getRegion) && isInRegion.get(player) == null) {
				isInRegion.put(player, getRegion);
				setItems(player, getRegion);
				return true;
			} else if (CheckInRegion(player.getWorld(), player.getLocation(), getRegion) && !getRegion.equalsIgnoreCase(isInRegion.get(player))) {
				isInRegion.remove(player);
				isInRegion.put(player, getRegion);
				setItems(player, getRegion);
				return true;
			} else if (CheckInRegion(player.getWorld(), player.getLocation(), getRegion) && getRegion.equalsIgnoreCase(isInRegion.get(player))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean CheckInRegion(World world, Location playerlocation, String regionname) {
		if (regionname == null) {
			return true;
		}
		ApplicableRegionSet set = getGuardSetRegions(world, playerlocation);
		if (set == null) {
			return false;
		}
		for (ProtectedRegion r: set) {
			if (r.getId().equalsIgnoreCase(regionname)) {
				return true;
			}
		}
		return false;
	}

	private static ApplicableRegionSet getGuardSetRegions(World world, Location loc) {
		if (Hooks.getWorldGuardVersion() >= 700) {
			com.sk89q.worldedit.world.World wgWorld = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getWorldByName(world.getName());
			com.sk89q.worldedit.Vector wgVector = new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ());
			com.sk89q.worldguard.protection.regions.RegionContainer rm = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			if (rm == null) { return null; }
			return rm.get(wgWorld).getApplicableRegions(wgVector);
		} else {
			return Legacy.getLegacyRegionSet(world, loc);
		}
	}

	public static HashMap < Player, String > getInRegion() {
		return isInRegion;
	}
	
	public static void removeInRegion(Player player) {
		isInRegion.remove(player);
	}
	
	public static List < String > getRegions() {
		return regions;
	}
	
	public static void saveRegion(String region) {
		regions.add(region);
	}
	
	public static void resetRegions() {
		regions.clear();
	}

}