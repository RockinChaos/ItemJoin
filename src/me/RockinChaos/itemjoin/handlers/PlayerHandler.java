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
package me.RockinChaos.itemjoin.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import de.domedd.betternick.BetterNick;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.utils.ReflectionUtils;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.api.DependAPI;
import me.RockinChaos.itemjoin.utils.api.LanguageAPI;
import me.RockinChaos.itemjoin.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.SQL;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;

public class PlayerHandler {
	
	private static HashMap<String, ItemStack[]> craftingItems = new HashMap<String, ItemStack[]>();
	private static HashMap<String, ItemStack[]> craftingOpenItems = new HashMap<String, ItemStack[]>();
	private static HashMap<String, ItemStack[]> creativeCraftingItems = new HashMap<String, ItemStack[]>();
	private static final int PLAYER_CRAFT_INV_SIZE = 5;
	
   /**
    * Restores the crafting items for all currently online players.
    * 
    */
    public static void restoreCraftItems() {
    	forOnlinePlayers(player -> { ItemHandler.restoreCraftItems(player); } );
    }
    
   /**
    * Safely closes the players inventory in order to call the InventoryCloseEvent.
    * Fixes a bug with player.closeInventory() not calling the event by default, breaking crafting items.
    * 
    * @param player - The player to have their inventory closed.
    */
    public static void safeInventoryClose(final Player player) {
    	player.openInventory(Bukkit.createInventory(player.getInventory().getHolder(), 9));
    	player.closeInventory();	
    }
    
    public static boolean isEnabled(final Player player) {
    	DataObject dataPlayer = SQL.getData().getData(new DataObject(Table.ENABLED_PLAYERS, getPlayerID(player), player.getWorld().getName(), Boolean.toString(true)));
    	DataObject dataGlobal = SQL.getData().getData(new DataObject(Table.ENABLED_PLAYERS, getPlayerID(player), "Global", Boolean.toString(true)));
    	DataObject dataALL = SQL.getData().getData(new DataObject(Table.ENABLED_PLAYERS, null, "Global", Boolean.toString(true)));
    	final boolean enabled = (((dataPlayer != null ? Boolean.valueOf(dataPlayer.getEnabled()) : ((dataGlobal != null ? Boolean.valueOf(dataGlobal.getEnabled()) : (dataALL != null ? Boolean.valueOf(dataALL.getEnabled()) : true))))));
    	if (!enabled) { ServerUtils.logDebug("{ItemMap} " + player.getName() + " will not receive any items, they have custom items are disabled."); }
    	return enabled;
    }
    
   /**
    * Checks if the Entity is a real player.
    * 
    * @param entity - The entity being checked.
    * @return If the entity is a real Player.
    */
    public static boolean isPlayer(final Entity entity) {
    	if (DependAPI.getDepends(false).citizensEnabled() && net.citizensnpcs.api.CitizensAPI.getNPCRegistry().isNPC(entity)) {
    		return false;
    	} else if (!(entity instanceof Player)) { 
    		return false; 
    	}
    	return true;
    }
	
   /**
    * Checks if the InventoryView is a player crafting inventory.
    * 
    * @param view - The InventoryView to be checked.
    * @return If the currently open inventory is a player crafting inventory.
    */
    public static boolean isCraftingInv(final InventoryView view) {
        return ((!view.getType().name().equalsIgnoreCase("HOPPER") && !view.getType().name().equalsIgnoreCase("BREWING")) ? view.getTopInventory().getSize() == PLAYER_CRAFT_INV_SIZE : false);
    }
	
   /**
    * Checks if the player is currently in creative mode.
    * 
    * @param player - The player to be checked.
    * @return If the player is currently in creative mode.
    */
	public static boolean isCreativeMode(final Player player) {
		if (player.getGameMode() == GameMode.CREATIVE) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the player is currently in adventure mode.
    * 
    * @param player - The player to be checked.
    * @return If the player is currently in adventure mode.
    */
	public static boolean isAdventureMode(final Player player) {
		if (player.getGameMode() == GameMode.ADVENTURE) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the player is has an open menu while left clicking.
    * 
    * @param view - The InventoryView being compared.
    * @param view - The action being checked.
    * @return If the player is currently interacting with an open menu.
    */
	public static boolean isMenuClick(final InventoryView view, final Action action) {
		if (!isCraftingInv(view) && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
			return true;
		}
		return false;
	}
	
   /**
    * Gets the current crafting slot contents of the player.
    * 
    * @param player - the Player to get the crafting contents of.
    * @return The ItemStack list of crafting slot contents.
    */
    public static ItemStack[] getTopContents(final Player player) {
		ItemStack[] tempContents = player.getOpenInventory().getTopInventory().getContents();
		ItemStack[] contents = new ItemStack[5];
		if (contents != null && tempContents != null) { 
			for (int i = 0; i <= 4; i++) { 
				contents[i] = tempContents[i].clone(); 
			} 
			return contents;
		}
		return tempContents;
    }
	
   /**
    * Sets the currently selected hotbar slot for the specified player.
    * 
    * @param player - The player to have their slot set.
    */
	public static void setHotbarSlot(final Player player, int slot) {
		if (slot != -1 && slot <= 8 && slot >= 0) {
			player.getInventory().setHeldItemSlot(slot);
		}
	}
	
   /**
    * Gets the current ItemStack in the players Main Hand,
    * If it is empty it will get the ItemStack in the Off Hand,
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @return The current ItemStack in the players hand.
    */
	public static ItemStack getHandItem(final Player player) {
		if (ServerUtils.hasSpecificUpdate("1_9") && player.getInventory().getItemInMainHand().getType() != null && player.getInventory().getItemInMainHand().getType() != Material.AIR) {
			return player.getInventory().getItemInMainHand();
		} else if (ServerUtils.hasSpecificUpdate("1_9") && player.getInventory().getItemInOffHand().getType() != null && player.getInventory().getItemInOffHand().getType() != Material.AIR) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
			return LegacyAPI.getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Gets the current ItemStack in the players hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @param type - The hand type to get.
    * @return The current ItemStack in the players hand.
    */
	public static ItemStack getPerfectHandItem(final Player player, final String type) {
		if (ServerUtils.hasSpecificUpdate("1_9") && type != null && type.equalsIgnoreCase("HAND")) {
			return player.getInventory().getItemInMainHand();
		} else if (ServerUtils.hasSpecificUpdate("1_9") && type != null && type.equalsIgnoreCase("OFF_HAND")) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
			return LegacyAPI.getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Gets the current ItemStack in the players Main Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @return The current ItemStack in the players hand.
    */
	public static ItemStack getMainHandItem(final Player player) {
		if (ServerUtils.hasSpecificUpdate("1_9")) {
			return player.getInventory().getItemInMainHand();
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
			return LegacyAPI.getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Gets the current ItemStack in the players Off Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to be checked.
    * @return The current ItemStack in the players hand.
    */
	public static ItemStack getOffHandItem(final Player player) {
		if (ServerUtils.hasSpecificUpdate("1_9")) {
			return player.getInventory().getItemInOffHand();
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
			return LegacyAPI.getInHandItem(player);
		}
		return null;
	}
	
   /**
    * Sets the specified ItemStack to the players Main Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to have the item set.
    * @param item - The ItemStack to be set.
    */
	public static void setMainHandItem(final Player player, final ItemStack item) {
		if (ServerUtils.hasSpecificUpdate("1_9")) {
			player.getInventory().setItemInMainHand(item);
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
			LegacyAPI.setInHandItem(player, item);
		}
	}
	
   /**
    * Sets the specified ItemStack to the players Off Hand.
    * If the server version is below MC 1.9 it will use the 
    * legacy hand method to get the single hand.
    * 
    * @param player - The player to have the item set.
    * @param item - The ItemStack to be set.
    */
	public static void setOffHandItem(final Player player, final ItemStack item) {
		if (ServerUtils.hasSpecificUpdate("1_9")) {
			player.getInventory().setItemInOffHand(item);
		} else if (!ServerUtils.hasSpecificUpdate("1_9")) {
			LegacyAPI.setInHandItem(player, item);
		}
	}
	
   /**
    * Resolves a bug where canceling an experience level event causes it to visually glitch
    * and remain showing the uncanceled experience levels.
    * 
    * This simply gets the players current experience levels and resets 
    * them to cause a clientside update.
    * 
    * @param player - The player to have their levels set.
    */
	public static void updateExperienceLevels(final Player player) {
		SchedulerUtils.runLater(1L, () -> {
			player.setExp(player.getExp());
			player.setLevel(player.getLevel());
		});
	}
	
   /**
    * Updates the specified players inventory.
    * 
    * @param player - The player to have their inventory updated.
    * @param delay - The ticks to wait before updating the inventory.
    */
	public static void updateInventory(final Player player) {
		updateInventory(player, null, 0L);
	}
	
   /**
    * Updates the specified players inventory.
    * 
    * @param player - The player to have their inventory updated.
    * @param delay - The ticks to wait before updating the inventory.
    */
	public static void updateInventory(final Player player, final long delay) {
		updateInventory(player, null, delay);
	}
	
   /**
    * Updates the specified players inventory.
    * 
    * @param player - The player to have their inventory updated.
    * @param itemMap - The ItemMap expected to be updated.
    * @param delay - The ticks to wait before updating the inventory.
    */
	public static void updateInventory(final Player player, final ItemMap itemMap, final long delay) {
		SchedulerUtils.runAsyncLater(delay, () -> {
			try {
				for (int i = 0; i < 36; i++) {
					if (itemMap == null || itemMap.isReal(player.getInventory().getItem(i))) { 
					    ReflectionUtils.sendPacketPlayOutSetSlot(player, player.getInventory().getItem(i), (i < 9 ? (i + 36) : i)); 
					}
				}
				if (ServerUtils.hasSpecificUpdate("1_9")) { 
					if (itemMap == null || itemMap.isReal(getOffHandItem(player))) { 
						ReflectionUtils.sendPacketPlayOutSetSlot(player, getOffHandItem(player), 45); 
					} 
				}
				if (isCraftingInv(player.getOpenInventory())) {
					for (int i = 4; i >= 0; i--) { 
						if (itemMap == null || itemMap.isReal(player.getInventory().getItem(i)) || itemMap.isCraftingItem()) { 
							ReflectionUtils.sendPacketPlayOutSetSlot(player, player.getOpenInventory().getTopInventory().getItem(i), i); 
						}
					}
					for (int i = 0; i <= 3; i++) { 
						if (itemMap == null || itemMap.isReal(player.getInventory().getItem(i))) { 
							ReflectionUtils.sendPacketPlayOutSetSlot(player, player.getInventory().getItem(i + 36), (8 - i)); 
						}
					}
				}
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		});
	}
	
   /**
    * Gets the current skull owner of the specified item.
    * 
    * @param item - The item to have its skull owner fetched.
    * @return The ItemStacks current skull owner.
    */
	public static String getSkullOwner(final ItemStack item) {
		if (ServerUtils.hasSpecificUpdate("1_12") && item != null && item.hasItemMeta() && ItemHandler.isSkull(item.getType()) 
				&& ((SkullMeta) item.getItemMeta()).hasOwner() && ItemHandler.usesOwningPlayer() != false) {
			String owner =  ((SkullMeta) item.getItemMeta()).getOwningPlayer().getName();
			if (owner != null) { return owner; }
		} else if (item != null && item.hasItemMeta() 
				&& ItemHandler.isSkull(item.getType())
				&& ((SkullMeta) item.getItemMeta()).hasOwner()) {
			String owner = LegacyAPI.getSkullOwner(((SkullMeta) item.getItemMeta()));
			if (owner != null) { return owner; }
		} 
		return "NULL";
	}

   /**
    * Gets the Player instance from their String name.
    * 
    * @param playerName - The player name to be transformed.
    * @return The fetched Player instance.
    */
	public static Player getPlayerString(final String playerName) {
		Player args = null;
		try { args = Bukkit.getPlayer(UUID.fromString(playerName)); } catch (Exception e) { }
		if (playerName != null && DependAPI.getDepends(false).nickEnabled()) {
			try { 
				de.domedd.betternick.api.nickedplayer.NickedPlayer np = new de.domedd.betternick.api.nickedplayer.NickedPlayer(LegacyAPI.getPlayer(playerName));
				if (np.isNicked()) { return LegacyAPI.getPlayer(np.getRealName()); }
				else { return LegacyAPI.getPlayer(playerName); }
			} catch (NoClassDefFoundError e) {
				if (BetterNick.getApi().isPlayerNicked(LegacyAPI.getPlayer(playerName))) { return LegacyAPI.getPlayer(BetterNick.getApi().getRealName(LegacyAPI.getPlayer(playerName))); }
				else { return LegacyAPI.getPlayer(playerName); }
			}
		} else if (args == null) { return LegacyAPI.getPlayer(playerName); }
		return args;
	}
	
   /**
    * Gets the UUID of the Player.
    * If the UUID does not exist it will fetch their String name.
    * 
    * @param player - The player to have their UUID fetched.
    * @return The UUID of the player or if not found, their String name.
    */
	public static String getPlayerID(final Player player) {
		try {
			if (player != null && ServerUtils.hasSpecificUpdate("1_8") && player.getUniqueId() != null) {
				return player.getUniqueId().toString();
			} else if (player != null && DependAPI.getDepends(false).nickEnabled()) {
				try {
					de.domedd.betternick.api.nickedplayer.NickedPlayer np = new de.domedd.betternick.api.nickedplayer.NickedPlayer(player);
					if (np.isNicked()) { 
						if (ServerUtils.hasSpecificUpdate("1_8") && np.getUniqueId() != null) {
							return np.getUniqueId().toString();
						} else {
							return np.getRealName();
						}
					} else { return player.getName(); }
				} catch (NoClassDefFoundError e) {
					if (BetterNick.getApi().isPlayerNicked(player)) { return BetterNick.getApi().getRealName(player);
					} else { return player.getName(); }
				}
			} else if (player != null) {
				return player.getName();
			}
		} catch (Exception e) { 
			if (player != null) { return player.getName(); }
			ServerUtils.sendDebugTrace(e);
		}
		return "";
	}
	
   /**
    * Gets the UUID of the OfflinePlayer.
    * If the UUID does not exist it will fetch their String name.
    * 
    * @param player - The OfflinePlayer instance to have their UUID fetched.
    * @return The UUID of the player or if not found, their String name.
    */
	public static String getOfflinePlayerID(final OfflinePlayer player) {
		try {
			if (player != null && ServerUtils.hasSpecificUpdate("1_8") && player.getUniqueId() != null) {
				return player.getUniqueId().toString();
			} else if (player != null && DependAPI.getDepends(false).nickEnabled()) {
				try {
					de.domedd.betternick.api.nickedplayer.NickedPlayer np = new de.domedd.betternick.api.nickedplayer.NickedPlayer((BetterNick) player);
					if (np.isNicked()) { 
						if (ServerUtils.hasSpecificUpdate("1_8") && np.getUniqueId() != null) {
							return np.getUniqueId().toString();
						} else {
							return np.getRealName();
						}
					} else { return player.getName(); }
				} catch (NoClassDefFoundError e) {
					if (BetterNick.getApi().isPlayerNicked((Player) player)) { return BetterNick.getApi().getRealName((Player) player);
					} else { return player.getName(); }
				}
			} else if (player != null) {
				return player.getName();
			}
		} catch (Exception e) { 
			if (player != null) { return player.getName(); }
			ServerUtils.sendDebugTrace(e);
		}
		return "";
	}
	
   /**
    * Quick saves the current inventories crafting items.
    * 
    * @oaram player - The player having their crafting items saved.
    */
    public static void quickCraftSave(Player player) {
    	if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
    		ItemStack[] contents = new ItemStack[5];
    		if (contents != null && player.getOpenInventory().getTopInventory().getContents() != null) {
    			for (int i = 0; i <= 4; i++) {
    				contents[i] = player.getOpenInventory().getTopInventory().getContents()[i].clone();
    			}
    		}
    		craftingItems.put(PlayerHandler.getPlayerID(player), contents);
    	}
    }
    
   /**
    * Emulates the Player dropping the ItemStack.
    * 
    * @param player - The Player being referenced.
    * @param item - The item to be dropped.
    */
    public static void dropItem(final Player player, final ItemStack item) { 
    	Location location = player.getLocation();
    	location.setY(location.getY() + 1);
    	Item dropped = player.getWorld().dropItem(location, item);
		dropped.setVelocity(location.getDirection().multiply(.30));
    }
    
   /**
    * Gets the crafting items HashMap of players crafting contents.
    * 
    * @return The HashMap of players and their crafting contents.
    */
    public static HashMap<String, ItemStack[]> getCraftItems() {
    	return craftingItems;
    }
    
   /**
    * Adds the Player and their Crafting items to a HashMap.
    * 
    * @param player - The player being referenced.
    * @param items - THe items to be added.
    */
    public static void addCraftItems(final Player player, final ItemStack[] items) {
    	craftingItems.put(PlayerHandler.getPlayerID(player), items);
    }
    
   /**
    * Gets the crafting items HashMap of players prior to creative crafting contents.
    * 
    * @return The HashMap of players and their prior to creative crafting contents.
    */
    public static HashMap<String, ItemStack[]> getCreativeCraftItems() {
    	return creativeCraftingItems;
    }
    
   /**
    * Adds the Player and their Crafting items to a HashMap.
    * 
    * @param player - The player being referenced.
    * @param items - THe items to be added.
    */
    public static void addCreativeCraftItems(final Player player, final ItemStack[] items) {
    	creativeCraftingItems.put(PlayerHandler.getPlayerID(player), items);
    }
    
   /**
    * Removves the Player and their Crafting items from a HashMap.
    * 
    * @param player - The player being referenced.
    */
    public static void removeCreativeCraftItems(final Player player) {
    	creativeCraftingItems.remove(PlayerHandler.getPlayerID(player));
    }
    
   /**
    * Gets the crafting items HashMap of players prior to opened inventory crafting contents.
    * 
    * @return The HashMap of players and their prior to opened inventory crafting contents.
    */
    public static HashMap<String, ItemStack[]> getOpenCraftItems() {
    	return craftingOpenItems;
    }
    
   /**
    * Adds the Player and their Crafting items to a HashMap.
    * 
    * @param player - The player being referenced.
    * @param items - THe items to be added.
    */
    public static void addOpenCraftItems(final Player player, final ItemStack[] items) {
    	craftingOpenItems.put(PlayerHandler.getPlayerID(player), items);
    }
    
   /**
    * Removves the Player and their Crafting items from a HashMap.
    * 
    * @param player - The player being referenced.
    */
    public static void removeOpenCraftItems(final Player player) {
    	craftingOpenItems.remove(PlayerHandler.getPlayerID(player));
    }
    
   /**
    * Constantly cycles through the players crafting slots saving them to a HashMap for later use.
    * 
    */
    public static void cycleCrafting() {
    	SchedulerUtils.runAsyncTimer(40L, 0L, () -> {
    		Collection < ? > playersOnlineNew = null;
    		Player[] playersOnlineOld;
    		try {
    			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    				if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    					playersOnlineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    					for (Object objPlayer: playersOnlineNew) {
    						if (((Player) objPlayer).isOnline() && PlayerHandler.isCraftingInv(((Player) objPlayer).getOpenInventory())) {
    							ItemStack[] tempContents = ((Player) objPlayer).getOpenInventory().getTopInventory().getContents();
    							ItemStack[] contents = new ItemStack[5];
    							if (contents != null && tempContents != null) {
	    							for (int i = 0; i <= 4; i++) {
	    								if (tempContents[i] != null) { 
	    									contents[i] = tempContents[i].clone();
	    								}
	    							}
    							}
    							craftingItems.put(PlayerHandler.getPlayerID(((Player) objPlayer)), contents);
    						} else {
    							craftingItems.remove(PlayerHandler.getPlayerID((Player) objPlayer));
    						}
    					}
    				}
    			} else {
    				playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    				for (Player player: playersOnlineOld) {
    					if (player.isOnline() && PlayerHandler.isCraftingInv(player.getOpenInventory())) {
    						ItemStack[] tempContents = player.getOpenInventory().getTopInventory().getContents();
    						ItemStack[] contents = new ItemStack[5];
    						if (contents != null && tempContents != null) {
    							for (int i = 0; i <= 4; i++) {
    								contents[i] = tempContents[i].clone();
    							}
    						}
    						craftingItems.put(PlayerHandler.getPlayerID(player), contents);
    					} else {
    						craftingItems.remove(PlayerHandler.getPlayerID(player));
    					}
    				}
    			}
    		} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
    	});
    }
	
   /**
    * Gets the Nearby Players from the specified Players Location inside the Range.
    * 
    * @param player - The Player that is searching for Nearby Players.
    * @param range - The distance to check for Nearby Players.
    * @return The String name of the Nearby Player.
    */
    public static String getNearbyPlayer(final Player player, final int range) {
	    ArrayList < Location > sight = new ArrayList < Location > ();
	    ArrayList < Entity > entities = (ArrayList < Entity > ) player.getNearbyEntities(range, range, range);
	    Location origin = player.getEyeLocation();
	    sight.add(origin.clone().add(origin.getDirection()));
	    sight.add(origin.clone().add(origin.getDirection().multiply(range)));
	    sight.add(origin.clone().add(origin.getDirection().multiply(range + 3)));
	   	for (int i = 0; i < sight.size(); i++) {
	    	for (int k = 0; k < entities.size(); k++) {
	    		if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3) {
	    			if (Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5) {
	    				if (Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3) {
	    					if (entities.get(k) instanceof Player) {
	    						if (ServerUtils.hasSpecificUpdate("1_8")) {
	    							return entities.get(k).getName();
	    						} else {
	    							return ((Player) entities.get(k)).getName();
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}
	    }
    	return (LanguageAPI.getLang(false).getLangMessage("placeholders.PLAYER_INTERACT") != null ? LanguageAPI.getLang(false).getLangMessage("placeholders.PLAYER_INTERACT") : "INVALID");
    }
	
   /**
    * Executes an input of methods for the currently online players.
    * 
    * @param input - The methods to be executed.
    */
    public static void forOnlinePlayers(final Consumer<Player> input) {
		try {
		  /** New method for getting the current online players.
			* This is for MC 1.12+
			*/
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				for (Object objPlayer: ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]))) { 
					input.accept(((Player) objPlayer));
				}
			} 
		  /** New old for getting the current online players.
			* This is for MC versions below 1.12.
			* 
			* @deprecated Legacy version of getting online players.
			*/
			else {
				for (Player player: ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]))) {
					input.accept(player);
				}
			}
		} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
	}
}