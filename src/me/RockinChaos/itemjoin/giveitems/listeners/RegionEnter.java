package me.RockinChaos.itemjoin.giveitems.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.utils.Legacy;
import me.RockinChaos.itemjoin.utils.Utils;

public class RegionEnter implements Listener {
	private static HashMap < Player, String > playersInRegions = new HashMap < Player, String > ();
	private static List < String > localeRegions = new ArrayList < String > ();

	@EventHandler
	private void RegionListener(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (ConfigHandler.getDepends().getGuard().guardEnabled()) {
			if (ConfigHandler.getSQLData().isEnabled(player)) {
				updateRegionItems(player);
			}
		}
	}

	private static void removeItems(Player player, String region) {
		ItemUtilities.safeSet(player, "Region-Enter");
		ItemUtilities.updateItems(player, false);
		for (ItemMap item: ItemUtilities.getItems()) {
			if (item.isTakeOnRegionLeave() || item.isGiveOnRegionEnter()) {
				if (item.isEnabledRegion(region) && item.inWorld(player.getWorld()) && item.hasPermission(player)) {
					item.removeFrom(player, 0);
				}
			}
		}
	}
	
	private static void getItems(Player player, String region) {
		final int session = Utils.getRandom(1, 100000);
		ItemUtilities.safeSet(player, "Region-Enter");
		ItemUtilities.updateItems(player, false);
		String Probable = ItemUtilities.getProbabilityItem(player);
		for (ItemMap item: ItemUtilities.getItems()) {
			if (item.isGiveOnRegionEnter() && item.isEnabledRegion(region) && item.inWorld(player.getWorld())
					&& ItemUtilities.isChosenProbability(item, Probable) && item.hasPermission(player) && ItemUtilities.isObtainable(player, item, session)) {
					item.giveTo(player, false, 0);
				}
				item.setAnimations(player);
			}
	}
	
	private static void updateRegionItems(Player player) {
		String regions = getLocationRegions(player);
		if (playersInRegions.get(player) != null) {
			List < String > regionSet = Arrays.asList(regions.replace(" ", "").split(","));
			List < String > playerSet = Arrays.asList(playersInRegions.get(player).replace(" ", "").split(","));
			List < String > regionSetAdditional = new ArrayList < String > (regionSet);
			List < String > playerSetAdditional = new ArrayList < String > (playerSet);
			regionSetAdditional.removeAll(playerSet);
			playerSetAdditional.removeAll(regionSet);
			if (!playerSetAdditional.isEmpty()) {
				for (String region: playerSetAdditional) {
					removeItems(player, region);
				}
			}
			if (!regionSetAdditional.isEmpty()) {
				for (String region: regionSetAdditional) {
					getItems(player, region);
				}
			}
		} else {
			List < String > regionSet = Arrays.asList(regions.replace(" ", "").split(","));
			for (String region: regionSet) {
				getItems(player, region);
			}
		}
		playersInRegions.put(player, regions);
	}
	
	private static String getLocationRegions(Player player) {
		ApplicableRegionSet set = getApplicableRegionSet(player.getWorld(), player.getLocation()); if (set == null) { return ""; }
		String regionSet = "";
		for (ProtectedRegion r: set) {
			if (regionSet.isEmpty()) { regionSet += r.getId(); } else { regionSet +=  ", " + r.getId(); }
		}
		return regionSet;
	}
	
	private static boolean isLocaleRegion(String compareRegion) {
		for(String region : localeRegions) {
			if (region.equalsIgnoreCase(compareRegion) || region.equalsIgnoreCase("UNDEFINED")) {
				return true;
			}
		}
		return false;
	}
	
	private static ApplicableRegionSet getApplicableRegionSet(World world, Location loc) {
		if (ConfigHandler.getDepends().getGuard().guardVersion() >= 700) {
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
	
	public static List < String > getLocaleRegions() {
		return localeRegions;
	}
	
	public static void addLocaleRegion(String region) {
		if (!isLocaleRegion(region)) { localeRegions.add(region); }
	}
	
	public static void clearLocaleRegions() {
		localeRegions.clear();
	}
}