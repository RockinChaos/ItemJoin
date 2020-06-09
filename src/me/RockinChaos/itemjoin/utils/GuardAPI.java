/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.sqlite.SQLite;

public class GuardAPI {
	
	private boolean isEnabled = false;
	private int guardVersion = 0;
	private List < String > localeRegions = new ArrayList < String > ();
	
	private static GuardAPI guard;
	
   /**
	* Creates a new WorldGuard instance.
	* 
	*/
	public GuardAPI() {
		this.setGuardStatus(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit") != null && Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null);
	}
	
   /**
	* Enables WorldGuard if it is found.
	* 
	*/
	private void enableGuard() {
		try { this.guardVersion = Integer.parseInt(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion().replace(".", "").substring(0, 3));
		} catch (Exception e) { this.guardVersion = 622; }
	}

   /**
	* Checks if WorldGuard is enabled.
	* 
	* @return If WorldGuard is enabled.
	*/
    public boolean guardEnabled() {
    	return this.isEnabled;
    }
    
   /**
	* Gets the current WorldGuard version.
	* 
	* @return The current WorldGuard version.
	*/
    public int guardVersion() {
    	return this.guardVersion;
    }
    
   /**
	* Gets the WorldGuard regions in the specified world.
	* 
	* @param world - The world to get the regions from.
	* @return The List of Regions for the specified world.
	*/
	public Map<String, ProtectedRegion> getRegions(final World world) {
		if (this.guardVersion() >= 700) {
			com.sk89q.worldedit.world.World wgWorld;
			try { wgWorld = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getWorldByName(world.getName()); }
			catch (NoSuchMethodError e) { wgWorld = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(world.getName()); }
			com.sk89q.worldguard.protection.regions.RegionContainer rm = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			if (rm == null) { return null; }
			if (LegacyAPI.getLegacy().legacySk89q()) {
				return rm.get(wgWorld).getRegions();
			} else { return rm.get(wgWorld).getRegions(); }
		} else { return LegacyAPI.getLegacy().getRegions(world); }
	}
	
   /**
	* Gets the current region(s) the player is currently in.
	* 
	* @param player - The player that has entered or exited a region.
	* @return regionSet The applicable regions at the players location.
	*/
	public String getRegionsAtLocation(final Entity entity) {
		ApplicableRegionSet set = null;
		String regionSet = "";
		try { set = this.getApplicableRegionSet(entity.getWorld(), entity.getLocation()); } 
		catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
		if (set == null) { return regionSet; }
		for (ProtectedRegion r: set) {
			if (regionSet.isEmpty()) { regionSet += r.getId(); }
			else { regionSet += ", " + r.getId(); }
		}
		return regionSet;
	}
	
   /**
	* Gets the applicable region(s) set at the players location.
	* 
	* @param world - The world that the player is currently in.
	* @param location - The exact location of the player.
	* @return ApplicableRegionSet The WorldGuard RegionSet.
	*/
	private ApplicableRegionSet getApplicableRegionSet(final World world, final Location location) throws Exception {
		if (this.guardVersion() >= 700) {
			com.sk89q.worldedit.world.World wgWorld;
			try { wgWorld = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getWorldByName(world.getName()); } 
			catch (NoSuchMethodError e) { wgWorld = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(world.getName()); }
			com.sk89q.worldguard.protection.regions.RegionContainer rm = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			if (rm == null) { return null; }
			if (LegacyAPI.getLegacy().legacySk89q()) {
				final com.sk89q.worldedit.Vector wgVector = new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ());
				return rm.get(wgWorld).getApplicableRegions(wgVector);
			} else { return rm.get(wgWorld).getApplicableRegions(LegacyAPI.getLegacy().asBlockVector(location)); }
		} else { return LegacyAPI.getLegacy().getRegionSet(world, location); }
	}
	
   /**
	* Sets the status of WorldGuard.
	* 
	* @param bool - If WorldGuard is enabled.
	*/
    private void setGuardStatus(final boolean bool) {
    	if (bool) { this.enableGuard(); }
    	this.isEnabled = bool;
    }
    
   /**
	* Checks if the player has entered or exited 
	* any region(s) defined for any custom items.
	* 
	* @param region - The region that the player entered or exited.
	* @return If the region is defined.
	*/
	private boolean isLocaleRegion(final String checkRegion) {
		for (final String region : this.localeRegions) {
			if (region.equalsIgnoreCase(checkRegion) || region.equalsIgnoreCase("UNDEFINED")) {
				return true;
			}
		}
		return false;
	}

   /**
	* Adds a region to be compared against.
	* 
	* @param region - The region that the custom item has defined.
	*/
	public void addLocaleRegion(final String region) {
		if (!this.isLocaleRegion(region)) { 
			this.localeRegions.add(region); 
		}
	}
	
   /**
    * Saves the current items in the Player Inventory to be returned later.
    * 
    * @param player - The Player that had their items saved.
    * @param region - The region that the items are being saved from.
    * @param type - The clear type that is being executed.
    * @param craftView - The players current CraftView.
    * @param inventory - The players current Inventory.
    * @param clearAll - If ALL items are being cleared.
    */
	public void saveReturnItems(final Player player, final String region, final String type, final Inventory craftView, final PlayerInventory inventory, final boolean clearAll) {
		boolean doReturn = Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Options"), "RETURN");
		List < ItemMap > protectItems = ItemUtilities.getUtilities().getProtectItems();
		if (region != null && !region.isEmpty() && type.equalsIgnoreCase("REGION-ENTER") && doReturn) {
			Inventory saveInventory = Bukkit.createInventory(null, 54);
			for (int i = 0; i <= 47; i++) {
				for (int k = 0; k < (!protectItems.isEmpty() ? protectItems.size() : 1); k++) {
					if (ItemUtilities.getUtilities().canClear(inventory.getItem(i), String.valueOf(i), k, clearAll) && i <= 41) {
						saveInventory.setItem(i, inventory.getItem(i).clone());
					} else if (i >= 42 && ItemUtilities.getUtilities().canClear(craftView.getItem(i - 42), "CRAFTING[" + (i - 42) + "]", k, clearAll) && PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
						saveInventory.setItem(i, craftView.getItem(i - 42).clone());
					}
				}
			}
			SQLite.getLite(false).saveReturnRegionItems(player, player.getWorld().getName(), region, saveInventory);
		}
	}
	
   /**
    * Returns the previously removed Region Items to the Player.
    * 
    * @param player - The Player that had their items returned.
    * @param world - The world to be checked.
    * @param region - The region the items were removed from.
    */
	public void pasteReturnItems(final Player player, final String world, final String region) {
		if (region != null && !region.isEmpty() && Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Options"), "RETURN")) {
			Inventory inventory = SQLite.getLite(false).getReturnRegionItems(player, world, region);
			for (int i = 47; i >= 0; i--) {
				if (inventory != null && inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
					if (i <= 41) {
						player.getInventory().setItem(i, inventory.getItem(i).clone());
					} else if (i >= 42 && PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
						player.getOpenInventory().getTopInventory().setItem(i - 42, inventory.getItem(i).clone());
						PlayerHandler.getPlayer().updateInventory(player, 1L);
					}
				}
				SQLite.getLite(false).removeReturnRegionItems(player, world, region);
			}
		}
	}
	
   /**
    * Gets the instance of the GuardAPI.
    * 
    * @param regen - If the GuardAPI should have a new instance created.
    * @return The GuardAPI instance.
    */
    public static GuardAPI getGuard(final boolean regen) { 
        if (guard == null || regen) { guard = new GuardAPI(); }
        return guard; 
    } 
}