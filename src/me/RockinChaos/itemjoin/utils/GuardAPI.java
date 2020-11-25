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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
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

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.sqlite.SQL;

public class GuardAPI {
	
    private Object worldGuard = null;
    private WorldGuardPlugin worldGuardPlugin = null;
    private Object regionContainer = null;
    private Method getRegionContainer = null;
    private Method getWorldAdapter = null;
    private Method getRegionManager = null;
    private Constructor<?> vectorConstructor = null;
    private Method getVector = null;
			
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
		if (this.isEnabled) {
			if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") instanceof WorldGuardPlugin) {
				this.worldGuardPlugin = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
				try {
					Class < ? > worldGuard = Class.forName("com.sk89q.worldguard.WorldGuard");
					Method getInstance = worldGuard.getMethod("getInstance");
					this.worldGuard = getInstance.invoke(null);
				} catch (Exception e) {}
			}
			if (this.worldGuard != null) {
				try {
					Method getPlatForm = this.worldGuard.getClass().getMethod("getPlatform");
					Object platform = getPlatForm.invoke(this.worldGuard);
					Method getRegionContainer = platform.getClass().getMethod("getRegionContainer");
					this.regionContainer = getRegionContainer.invoke(platform);
					Class < ? > getWorldEditWorld = Class.forName("com.sk89q.worldedit.world.World");
					Class < ? > getWorldEditAdapter = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
					this.getWorldAdapter = getWorldEditAdapter.getMethod("adapt", World.class);
					this.getRegionContainer = this.regionContainer.getClass().getMethod("get", getWorldEditWorld);
				} catch (Exception e) {
					ServerHandler.getServer().logSevere("{GuardAPI} Failed to bind to WorldGuard, integration will not work!");
					ServerHandler.getServer().sendDebugTrace(e);
					this.regionContainer = null;
					return;
				}
			} else {
				try {
					Method getRegionContainer = this.worldGuardPlugin.getClass().getMethod("getRegionContainer");
					this.regionContainer = getRegionContainer.invoke(this.worldGuardPlugin);
					this.getRegionContainer = this.regionContainer.getClass().getMethod("get", World.class);
				} catch (Exception e) {
					ServerHandler.getServer().logSevere("{GuardAPI} Failed to bind to WorldGuard, integration will not work!");
					ServerHandler.getServer().sendDebugTrace(e);
					this.regionContainer = null;
					return;
				}
			}
			try {
				Class < ? > vectorClass = Class.forName("com.sk89q.worldedit.Vector");
				this.vectorConstructor = vectorClass.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
				this.getRegionManager = RegionManager.class.getMethod("getApplicableRegions", vectorClass);
			} catch (Exception e) {
				try {
					Class < ? > vectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
					this.getVector = vectorClass.getMethod("at", Double.TYPE, Double.TYPE, Double.TYPE);
					this.getRegionManager = RegionManager.class.getMethod("getApplicableRegions", vectorClass);
				} catch (Exception e2) {
					ServerHandler.getServer().logSevere("{GuardAPI} Failed to bind to WorldGuard (no Vector class?), integration will not work!");
					ServerHandler.getServer().sendDebugTrace(e);
					this.regionContainer = null;
					return;
				}
			}
			if (this.regionContainer == null) { ServerHandler.getServer().logSevere("{GuardAPI} Failed to find RegionContainer, WorldGuard integration will not function!"); }
		}
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
		return this.getRegionManager(world).getRegions();
	}
	
   /**
	* Gets the current region(s) the player is currently in.
	* 
	* @param player - The player that has entered or exited a region.
	* @return regionSet The applicable regions at the players location.
	*/
	public String getRegionAtEntity(final Entity entity) {
		ApplicableRegionSet set = null;
		String regionSet = "";
		try { set = this.getRegionSet(entity.getLocation()); } 
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
	* @param location - The exact location of the player.
	* @return ApplicableRegionSet The WorldGuard RegionSet.
	*/
	private ApplicableRegionSet getRegionSet(final Location location) throws Exception {
		RegionManager regionManager = this.getRegionManager(location.getWorld());
		if (regionManager == null) { return null; }
		try {
			Object vector = this.getVector == null ? this.vectorConstructor.newInstance(location.getX(), location.getY(), location.getZ()) 
					        : this.getVector.invoke(null, location.getX(), location.getY(), location.getZ());
			return (ApplicableRegionSet) this.getRegionManager.invoke(regionManager, vector);
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{GuardAPI} An error occurred looking up a WorldGuard ApplicableRegionSet.");
			ServerHandler.getServer().sendDebugTrace(e);
		}
		return null;
	}
	
   /**
	* Gets the RegionManager for the Bukkit World.
	* 
	* @param world - The world that the player is currently in.
	* @return The WorldGuard RegionManager for the specified world.
	*/
    private RegionManager getRegionManager(final World world) {
    	if (this.regionContainer == null || this.getRegionContainer == null) { return null; }
    	RegionManager regionManager = null;
    	try {
    		if (this.getWorldAdapter != null) {
    			Object worldEditWorld = this.getWorldAdapter.invoke(null, world);
    			regionManager = (RegionManager) this.getRegionContainer.invoke(this.regionContainer, worldEditWorld);
    		} else {
    			regionManager = (RegionManager) this.getRegionContainer.invoke(this.regionContainer, world);
    		}
    	} catch (Exception e) {
    		ServerHandler.getServer().logSevere("{GuardAPI} An error occurred looking up a WorldGuard RegionManager.");
    		ServerHandler.getServer().sendDebugTrace(e);
    	}
    	return regionManager;
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
					if (i <= 41 && inventory.getSize() >= i && ItemUtilities.getUtilities().canClear(inventory.getItem(i), String.valueOf(i), k, clearAll)) {
						saveInventory.setItem(i, inventory.getItem(i).clone());
					} else if (i >= 42 && ItemUtilities.getUtilities().canClear(craftView.getItem(i - 42), "CRAFTING[" + (i - 42) + "]", k, clearAll) && PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
						saveInventory.setItem(i, craftView.getItem(i - 42).clone());
					}
				}
			}
			SQL.getData(false).saveReturnRegionItems(player, region, saveInventory);
		}
	}
	
   /**
    * Returns the previously removed Region Items to the Player.
    * 
    * @param player - The Player that had their items returned.
    * @param world - The world to be checked.
    * @param region - The region the items were removed from.
    */
	public void pasteReturnItems(final Player player, final String region) {
		if (region != null && !region.isEmpty() && Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Options"), "RETURN")) {
			Inventory inventory = SQL.getData(false).getReturnRegionItems(player, region);
			for (int i = 47; i >= 0; i--) {
				if (inventory != null && inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
					if (i <= 41) {
						player.getInventory().setItem(i, inventory.getItem(i).clone());
					} else if (i >= 42 && PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
						player.getOpenInventory().getTopInventory().setItem(i - 42, inventory.getItem(i).clone());
						PlayerHandler.getPlayer().updateInventory(player, 1L);
					}
				}
				SQL.getData(false).removeReturnRegionItems(player, region);
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