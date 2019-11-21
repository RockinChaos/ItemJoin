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
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Legacy;
import me.RockinChaos.itemjoin.utils.Chances;
import me.RockinChaos.itemjoin.utils.Utils;

public class PlayerGuard implements Listener {
	private HashMap < Player, String > playersInRegions = new HashMap < Player, String > ();
	private static List < String > localeRegions = new ArrayList < String > ();

	@EventHandler
	private void regionEvent(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (ConfigHandler.getSQLData().isEnabled(player)) {
			this.regionItems(player);
		}
	}

	private void removeItems(Player player, String region) {
		if (region != null && !region.isEmpty()) {
			ItemUtilities.pasteReturnItems(player, player.getWorld().getName(), region);
			for (ItemMap item: ItemUtilities.getItems()) {
				if (item.isTakeOnRegionLeave() || item.isGiveOnRegionEnter()) {
					if (item.inRegion(region) && item.inWorld(player.getWorld()) && item.hasPermission(player)) {
						item.removeFrom(player, 0);
					}
				}
			}
		}
	}
	
	private void getItems(Player player, String region) {
		if (region != null && !region.isEmpty()) {
			ItemUtilities.clearEventItems(player, "", "Region-Enter", region);
			final int session = Utils.getRandom(1, 100000);
			final Chances probability = new Chances();
			final ItemMap probable = probability.getRandom(player);
			for (ItemMap item: ItemUtilities.getItems()) {
				if (item.isGiveOnRegionEnter() && item.inRegion(region) && item.inWorld(player.getWorld())
						&& probability.isProbability(item, probable) && item.hasPermission(player) && ItemUtilities.isObtainable(player, item, session)) {
						item.giveTo(player, false, 0);
					}
					item.setAnimations(player);
				}
		}
	}
	
	private void regionItems(Player player) {
		String regions = this.getLocationRegions(player);
		if (playersInRegions.get(player) != null) {
			List < String > regionSet = Arrays.asList(regions.replace(" ", "").split(","));
			List < String > playerSet = Arrays.asList(playersInRegions.get(player).replace(" ", "").split(","));
			List < String > regionSetAdditional = new ArrayList < String > (regionSet);
			List < String > playerSetAdditional = new ArrayList < String > (playerSet);
			regionSetAdditional.removeAll(playerSet);
			playerSetAdditional.removeAll(regionSet);
			if (!playerSetAdditional.isEmpty()) {
				for (String region: playerSetAdditional) {
					this.removeItems(player, region);
				}
			}
			if (!regionSetAdditional.isEmpty()) {
				for (String region: regionSetAdditional) {
					this.getItems(player, region);
				}
			}
		} else {
			List < String > regionSet = Arrays.asList(regions.replace(" ", "").split(","));
			for (String region: regionSet) {
				this.getItems(player, region);
			}
		}
		this.playersInRegions.put(player, regions);
	}
	
	private String getLocationRegions(Player player) {
		ApplicableRegionSet set = null;
		try { set = this.getApplicableRegionSet(player.getWorld(), player.getLocation()); } catch (Exception e) { ServerHandler.sendDebugTrace(e); } 
		if (set == null) { return ""; }
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
	
	private ApplicableRegionSet getApplicableRegionSet(World world, Location loc) throws Exception {
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