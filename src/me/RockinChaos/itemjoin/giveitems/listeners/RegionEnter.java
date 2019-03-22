package me.RockinChaos.itemjoin.giveitems.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.MemoryHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Legacy;
import me.RockinChaos.itemjoin.utils.Utils;

public class RegionEnter implements Listener {
	private static HashMap < Player, String > playersInRegions = new HashMap < Player, String > ();
	private static List < String > localeRegions = new ArrayList < String > ();
	
	@EventHandler
	private void giveOnRegionEnter(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (MemoryHandler.isWorldGuard() == true) {
			if (isInRegion(player) && MemoryHandler.getSQLData().isEnabled(player)) {
				String regionId = getRegion(player).getId();
				if (Utils.containsIgnoreCase(localeRegions.toString(), regionId) || Utils.containsIgnoreCase(localeRegions.toString(), "UNDEFINED")) {
					if (playersInRegions.get(player) != null && playersInRegions.get(player).equalsIgnoreCase(regionId)) {} else if (playersInRegions.get(player) != null && !playersInRegions.get(player).equalsIgnoreCase(regionId)) {
						removeItems(player);
						setItems(player, regionId);
					} else { setItems(player, regionId); }
				} else { removeItems(player);  }
			} else { removeItems(player);  }
		}
	}

	private static void setItems(final Player player, final String regionId) {
		ItemUtilities.safeSet(player, "Region-Enter");
		playersInRegions.put(player, regionId);
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			@Override
			public void run() {
				final int session = Utils.getRandom(1, 100000);
				removeLeaveItems(player);
				giveItems(player, regionId, 1, session);
				ItemUtilities.sendFailCount(player, session);
				PlayerHandler.delayUpdateInventory(player, 15L);
			}
		}, ConfigHandler.getItemDelay());
	}
	
	private static void removeItems(Player player) {
		if (playersInRegions.get(player) != null) {
			String Probable = ItemUtilities.getProbabilityItem(player);
			for (ItemMap item: ItemUtilities.getItems()) {
				if (item.isGiveOnRegionEnter() && MemoryHandler.getSQLData().isEnabled(player) && item.inWorld(player.getWorld()) && ItemUtilities.isChosenProbability(item, Probable) && item.hasPermission(player)) {
					item.removeFrom(player, 0);
				}
			}
			playersInRegions.remove(player);
		}
	}
	
	private static void removeLeaveItems(Player player) {
		String Probable = ItemUtilities.getProbabilityItem(player);
		for (ItemMap item: ItemUtilities.getItems()) {
			if (item.isTakeOnRegionLeave() && MemoryHandler.getSQLData().isEnabled(player) && item.inWorld(player.getWorld()) && ItemUtilities.isChosenProbability(item, Probable) && item.hasPermission(player)) {
				item.removeFrom(player, 0);
			}
		}
	}
	
	private static void giveItems(Player player, String region, int step, int session) {
		String Probable = ItemUtilities.getProbabilityItem(player);
		for (ItemMap item: ItemUtilities.getItems()) {
			if (item.isGiveOnRegionEnter() && MemoryHandler.getSQLData().isEnabled(player) && item.inWorld(player.getWorld()) && ItemUtilities.isChosenProbability(item, Probable) && item.hasPermission(player) && ItemUtilities.isObtainable(player, item, session)) {
				if (Utils.containsIgnoreCase(item.getEnabledRegions(), region) || Utils.containsIgnoreCase(item.getEnabledRegions(), "UNDEFINED")) {
					item.giveTo(player, false, 0);
				}
				item.setAnimations(player);
			}
			item.setAnimations(player);
		}
	}
	
	private static ProtectedRegion getRegion(Player player) {
		ApplicableRegionSet set = getRegionSets(player.getWorld(), player.getLocation());
		if (set == null) { return null; }
		for (ProtectedRegion r: set) {
			if (!r.getType().equals(RegionType.GLOBAL)) { return r; }
		}
		return null;
	}
	
	private static boolean isInRegion(Player player) {
		ApplicableRegionSet set = getRegionSets(player.getWorld(), player.getLocation());
		if (set == null) { return false; }
		for (ProtectedRegion r: set) {
			if (!r.getType().equals(RegionType.GLOBAL)) { return true; }
		}
		return false;
	}
	
	private static ApplicableRegionSet getRegionSets(World world, Location loc) {
		if (MemoryHandler.getWorldGuardVersion() >= 700) {
			com.sk89q.worldedit.world.World wgWorld;
			try { wgWorld = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getWorldByName(world.getName()); }
			catch (NoSuchMethodError e) { wgWorld = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(world.getName()); }
			com.sk89q.worldguard.protection.regions.RegionContainer rm = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			if (rm == null) { return null; }
			if (Legacy.hasLegacyWorldEdit()) {
				com.sk89q.worldedit.Vector wgVector = new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ());
				return rm.get(wgWorld).getApplicableRegions(wgVector);
			} else { return rm.get(wgWorld).getApplicableRegions(Legacy.asBlockVector(loc)); }
		} else { return Legacy.getLegacyRegionSet(world, loc); }
	}
	
	public static HashMap < Player, String > getPlayerRegions() {
		return playersInRegions;
	}
	
	public static void delPlayerRegion(Player player) {
		playersInRegions.remove(player);
	}
	
	public static List < String > getLocaleRegions() {
		return localeRegions;
	}
	
	public static void addLocaleRegion(String region) {
		if (!localeRegions.contains(region)) { localeRegions.add(region); }
	}
	
	public static void clearLocaleRegions() {
		localeRegions.clear();
	}
}