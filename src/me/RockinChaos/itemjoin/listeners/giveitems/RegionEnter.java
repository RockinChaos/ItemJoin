package me.RockinChaos.itemjoin.listeners.giveitems;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Language;
import me.RockinChaos.itemjoin.utils.Utils;

public class RegionEnter implements Listener {
	public static HashMap < Player, String > isInRegion = new HashMap < Player, String > ();
	
	@EventHandler
	public void giveOnRegionEnter(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		if (Hooks.hasWorldGuard == true) {
				if (!isInRegion(player) && isInRegion.get(player) != null) {
					isInRegion.remove(player);
					long delay = ConfigHandler.getConfig("items.yml").getInt("items-Delay") * 10L;
					Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable() {
						public void run() {
							SetItems.setClearItemJoinItems(player);
						}
					}, delay);
			}
		}
	}
	
	public static void setItems(final Player player, final String region) {
		final long delay = ConfigHandler.getConfig("items.yml").getInt("items-Delay") * 10L;
		CreateItems.run(player);
		SetItems.setClearingOfItems(player, player.getWorld().getName(), "Clear-On-Join");
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.pl, new Runnable() {
			public void run() {
				try {
				setEnterItems(player, region);
				if (SetItems.failCount.get(player) != 0) {
					boolean Overwrite = ConfigHandler.getConfig("items.yml").getBoolean("items-Overwrite");
					if (Overwrite == true) {
						Language.getSendMessage(player, "failedInvFull", SetItems.failCount.get(player).toString());
					} else if (Overwrite == false) {
						Language.getSendMessage(player, "failedOverwrite", SetItems.failCount.get(player).toString());
						}
					SetItems.failCount.remove(player);
				}
				PlayerHandler.delayUpdateInventory(player, 15L);
				} catch (NullPointerException ex) {
					
				}
			}
		}, delay);
	}
	
	public static void setEnterItems(Player player, String region) {
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				final String world = player.getWorld().getName();
				if (WorldHandler.inWorld(items, world) && ItemHandler.containsIgnoreCase(items.getString(".enabled-regions"), region) && PermissionsHandler.hasPermission(items, item, player)) {
					if(ItemHandler.containsIgnoreCase(items.getString(".triggers"), "region-enter") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "region enter")
							|| ItemHandler.containsIgnoreCase(items.getString(".triggers"), "enter-region") || ItemHandler.containsIgnoreCase(items.getString(".triggers"), "enter region")) {
					if (items.getString(".slot") != null) {
						String slotlist = items.getString(".slot").replace(" ", "");
						String[] slots = slotlist.split(",");
						ItemHandler.clearItemID(player);
						for (String slot: slots) {
							String ItemID = ItemHandler.getItemID(player, slot);
							if (Utils.isCustomSlot(slot)) {
								SetItems.setCustomSlots(player, item, slot, ItemID);
							} else if (Utils.isInt(slot)) {
								SetItems.setInvSlots(player, item, slot, ItemID);
							}
						}
					}
				}
			}
		}
	  }
	}
	
	public static boolean isInRegion(Player player) {
		String[] regions = CreateItems.regions.toString().split(",");
		for (String region : regions) {
			String getRegion = region.replace("[", "").replace("]", "").replace(" ", "");
			if (CheckInRegion(player.getLocation(), getRegion) && isInRegion.get(player) == null) {
				isInRegion.put(player, getRegion);
		        setItems(player, getRegion);
		        return true;
			} else if (CheckInRegion(player.getLocation(), getRegion) && !getRegion.equalsIgnoreCase(isInRegion.get(player))) {
				isInRegion.remove(player);
				isInRegion.put(player, getRegion);
				setItems(player, getRegion);
				return true;
			} else if (CheckInRegion(player.getLocation(), getRegion) && getRegion.equalsIgnoreCase(isInRegion.get(player))) {	
				return true;
			}
		}
 		return false;
 	}

	public static boolean CheckInRegion(Location playerlocation, String regionname) {
 		if (regionname == null) {
 			return true;
 		}
 	ApplicableRegionSet set = getWGSet(playerlocation);
		if (set == null) {
 			return false;
 		}
 		for (ProtectedRegion r : set) {
 			if (r.getId().equalsIgnoreCase(regionname)) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private static ApplicableRegionSet getWGSet(Location loc) {
 		WorldGuardPlugin wg = getWorldGuard();
 		if (wg == null) {
 			return null;
 		}
 		RegionManager rm = wg.getRegionManager(loc.getWorld());
 		if (rm == null) {
			return null;
 		}
 		return rm.getApplicableRegions(com.sk89q.worldguard.bukkit.BukkitUtil.toVector(loc));
 	}
 
 	public static WorldGuardPlugin getWorldGuard() {
 		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
 
 		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
 			return null;
 		}
 		return (WorldGuardPlugin) plugin;
 	}
}