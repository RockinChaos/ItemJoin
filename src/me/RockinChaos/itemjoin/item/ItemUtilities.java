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
package me.RockinChaos.itemjoin.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Chances;
import me.RockinChaos.itemjoin.utils.DependAPI;
import me.RockinChaos.itemjoin.utils.LanguageAPI;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLite;
import net.md_5.bungee.api.ChatColor;

public class ItemUtilities {
  	private List < ItemMap > items = new ArrayList < ItemMap >();
  	private List < ItemMap > craftingItems = new ArrayList < ItemMap >();
  	private List < ItemMap > protectItems = new ArrayList < ItemMap >();
	private HashMap <Integer, Integer> failCount = new HashMap <Integer, Integer> ();
	
	private static ItemUtilities utilities;

   /**
    * Checks if the specified itemflag and ItemStack is allowed.
    * 
    * @param player - The Player that is being allowed.
    * @param item - The ItemStack to check if it is allowed to perform the itemflag.
    * @param itemflag - The itemflag to be checked if it is allowed.
    */
	public boolean isAllowed(final Player player, final ItemStack item, final String itemflag) {
		ItemMap itemMap = this.getItemMap(item, null, player.getWorld());
		if (itemMap != null && itemMap.isAllowedItem(player, item, itemflag)) {
			return false;
		}
		return true;
	}
	
   /**
    * Finds the matching ItemMap from the list of provided ItemMaps,
    * that has the exact same ItemStack and world or config node name defined.
    * 
    * @param itemStack - The ItemStack to have its ItemMap located.
    * @param configName - The config node name of the ItemMap.
    * @param world - The world of the ItemMap.
    */
	public ItemMap getItemMap(final ItemStack itemStack, final String configName, final World world) {
		for (ItemMap itemMap : this.getItems()) {
			if (world != null && configName == null && itemMap.inWorld(world) && itemMap.isSimilar(itemStack)) {
				return itemMap;
			} else if (configName != null && itemMap.getConfigName().equalsIgnoreCase(configName)) {
				return itemMap;
			}
		}
		return null;
	}
	
   /**
    * Finds the matching ItemMap from the list of provided ItemMaps,
    * that has the exact same slot defined.
    * 
    * @param slot - The slot that the ItemMap should have defined.
    * @param items - The list of ItemMap to be checked.
    * @return The ItemMap matching the specified slot.
    */
	public ItemMap getItemMap(final String slot, final List < ItemMap > items) {
		ItemMap itemMap = null;
		for (final ItemMap item: items) {
			if (item.getSlot().equalsIgnoreCase(slot)) {
				itemMap = item;
				break;
			}
		}
		return itemMap;
	}
	
   /**
    * Closes the ItemAnimations for ALL Online Players.
    * 
    */
	public void closeAnimations() {
		PlayerHandler.getPlayer().forOnlinePlayers(player -> { this.closeAnimations(player); });
	}
	
   /**
    * Closes the ItemAnimations for the specified Player.
    * 
    * @param player - The Player that is having their item animations closed.
    */
	public void closeAnimations(final Player player) {
		for (ItemMap item : this.getItems()) {
			if (item.isAnimated() && item.getAnimationHandler().get(player) != null
					|| item.isDynamic() && item.getAnimationHandler().get(player) != null) {
				item.getAnimationHandler().get(player).closeAnimation(player);
				item.removeFromAnimationHandler(player);
			}
		}
	}
	
   /**
    * Updates the ItemMap List to fill the Player placeholders for ALL Online Players.
    * 
    */
	public void updateItems() {
		PlayerHandler.getPlayer().forOnlinePlayers(player -> { this.updateItems(player, true); });
	}
	
   /**
    * Updates the ItemMap List to fill the Player placeholders.
    * 
    * @param player - The Player that is having their items updated.
    * @param refresh - If the ItemAnimation should be restarted.
    */
	private void updateItems(final Player player, final boolean refresh) {
		for (ItemMap item: this.getItems()) {
			item.updateItem(player);
			if (refresh) {
				item.setAnimations(player);
			}
		}
	}
	
   /**
    * Checks if the Player is waiting Authentication from a plugin such as AuthMe. 
    * Then sets the item after Authentication is complete.
    * 
    * @param player - The Player that is being authenticated.
    * @param type - The TriggerType that is being performed.
    * @param newMode - The GameMode of the Player.
    * @param region - The region the Player is in.
    */
	public void setAuthenticating(final Player player, TriggerType type, final GameMode newMode, final String region) {
		if (DependAPI.getDepends(false).authMeEnabled()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (fr.xephi.authme.api.v3.AuthMeApi.getInstance().isAuthenticated(player)) {
						setItems(player, type, newMode, region);
						this.cancel();
					}
				}
			}.runTaskTimer(ItemJoin.getInstance(), 0, 20);
		} else { 
			this.setItems(player, type, newMode, region);
		}
	}
	
   /**
    * Sets the items to be given.
    * 
    * @param player - The Player that is having their items set.
    * @param type - The TriggerType that is being performed.
    * @param newMode - The GameMode of the Player.
    * @param region - The region the Player is in.
    */
	public void setItems(final Player player, final TriggerType type, final GameMode newMode, final String region) {
		this.safeSet(player, type, region);
		if (this.getItemDelay() != 0 && type != TriggerType.LIMITSWITCH && type != TriggerType.REGIONENTER && type != TriggerType.REGIONLEAVE) { 
			Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
				@Override
				public void run() { 
					ItemHandler.getItem().restoreCraftItems(player);
					handleItems(player, type, newMode, region); 
				}
			}, this.getItemDelay());
		} else { 
			ItemHandler.getItem().restoreCraftItems(player);
			this.handleItems(player, type, newMode, region); 
		}
	}
	
   /**
    * Handles the item give and removal provided the trigger type.
    * 
    * @param player - The Player that is having their items handled.
    * @param type - The TriggerType that is being performed.
    * @param newMode - The GameMode of the Player.
    * @param region - The region the Player is in.
    */
	private void handleItems(final Player player, TriggerType type, final GameMode newMode, final String region) {
		final ItemMap randomMap = Chances.getChances().getRandom(player);
		final int session = Utils.getUtils().getRandom(1, 100000);
		for (ItemMap item : this.getItems()) { 
			item.setAnimations(player);
			if (((type.equals(TriggerType.JOIN) && item.isGiveOnJoin()) 
			  || (type.equals(TriggerType.RESPAWN) && item.isGiveOnRespawn())
			  || (type.equals(TriggerType.WORLDSWITCH) && item.isGiveOnWorldSwitch())
			  || (type.equals(TriggerType.LIMITSWITCH) && item.isUseOnLimitSwitch())
			  || ((((type.equals(TriggerType.REGIONENTER) && item.isGiveOnRegionEnter()) 
			  || (type.equals(TriggerType.REGIONLEAVE) && item.isTakeOnRegionLeave())) && item.inRegion(region))))
					 && item.inWorld(player.getWorld()) && Chances.getChances().isProbability(item, randomMap) 
					 && SQLite.getLite(false).isEnabled(player) && item.hasPermission(player) 
					 && this.isObtainable(player, item, session, type, (newMode != null ? newMode : player.getGameMode()))) {
				item.giveTo(player); 
			} else if (((type.equals(TriggerType.LIMITSWITCH) && item.isUseOnLimitSwitch() && !item.isLimitMode(newMode))
					|| (((type.equals(TriggerType.REGIONENTER) && item.isTakeOnRegionLeave()) 
					|| (type.equals(TriggerType.REGIONLEAVE) && item.isGiveOnRegionEnter())) && item.inRegion(region))) 
					&& item.inWorld(player.getWorld()) && item.hasItem(player)) {
				item.removeFrom(player);
			} else if (item.isAutoRemove() && !item.inWorld(player.getWorld()) && item.hasItem(player)) {
				item.removeFrom(player);
			}
		}
		this.sendFailCount(player, session);
		PlayerHandler.getPlayer().updateInventory(player, 15L);
	}
	
   /**
    * Safely checks and sets the items for their corresponding TriggerType.
    * 
    * @param player - The Player that is having their items set.
    * @param type - The TriggerType that is being performed.
    */
	private void safeSet(final Player player, final TriggerType type, final String region) {
		if (Utils.getUtils().splitIgnoreCase(ConfigHandler.getConfig(false).getHotbarTriggers(), type.name)) { PlayerHandler.getPlayer().setHotbarSlot(player); }
		if (type.equals(TriggerType.REGIONLEAVE)) { DependAPI.getDepends(false).getGuard().pasteReturnItems(player, player.getWorld().getName(), region); }
		if (type.equals(TriggerType.REGIONENTER)) { this.clearEvent(player, "", type.name, region); }
		if (this.getClearDelay() != 0) {
			ServerHandler.getServer().runAsyncThread(main -> {
				if (type.equals(TriggerType.JOIN)) {
							clearEvent(player, player.getWorld().getName(), type.name, "");
							this.triggerCommands(player);
						} else if (type.equals(TriggerType.WORLDSWITCH)) {
							this.clearEvent(player, player.getWorld().getName(), type.name, "");
						}
			}, this.getClearDelay());
		} else {
			if (type.equals(TriggerType.JOIN)) {
				this.clearEvent(player, player.getWorld().getName(), type.name, "");
				this.triggerCommands(player);
			} else if (type.equals(TriggerType.WORLDSWITCH)) {
				this.clearEvent(player, player.getWorld().getName(), type.name, "");
			}
		}
	}
	
   /**
    * Sets the Players items to be cleared upon performing the specified event.
    * 
    * @param player - The Player performing the event.
    * @param world - The world the Player is in.
    * @param event - The event/trigger being performed.
    * @param region - The region the Player is in (if any).
    */
	private void clearEvent(Player player, String world, String event, String region) {
		String clearEvent = ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items." + event);
		if (clearEvent != null && ((region != null && !region.isEmpty() && Utils.getUtils().containsLocation(region, clearEvent.replace(" ", ""))) || Utils.getUtils().containsLocation(world, clearEvent.replace(" ", "")))) {
			if ((Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Options"), "PROTECT_OP") && player.isOp())
				|| (Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Options"), "PROTECT_CREATIVE") && PlayerHandler.getPlayer().isCreativeMode(player))) {
			} else {
				String clearType = ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Type");
				if (clearType != null && (clearType.equalsIgnoreCase("ALL") || clearType.equalsIgnoreCase("GLOBAL"))) {
					this.clearItems(player, region, event, true);
				} else if (clearType != null && clearType.equalsIgnoreCase("ITEMJOIN")) {
					this.clearItems(player, region, event, false);
				} else if (clearType != null) {
					ServerHandler.getServer().logSevere("{ItemMap} " + clearType + " for Clear-Items in the config.yml is not a valid option.");
				}
			}
		}
	}
	
   /**
    * Clears the items from the Player Inventory.
    * 
    * @param player - The Player performing the event.
    * @param event - The event/trigger being performed.
    * @param region - The region the Player is in (if any).
    * @param clearAll - If ALL items are expected to be cleared from the Player Inventory.
    */
	private void clearItems(final Player player, final String event, final String region, final boolean clearAll) {
		this.protectItems = this.getProtectItems();
		PlayerInventory inventory = player.getInventory();
		Inventory craftView = player.getOpenInventory().getTopInventory();
		DependAPI.getDepends(false).getGuard().saveReturnItems(player, event, region, craftView, inventory, true);
		for (int i = 0; i < (!this.protectItems.isEmpty() ? this.protectItems.size() : 1); i++) {
			if (this.canClear(inventory.getHelmet(), "Helmet", i, clearAll)) {
				inventory.setHelmet(new ItemStack(Material.AIR));
			} if (this.canClear(inventory.getChestplate(), "Chestplate", i, clearAll)) {
				inventory.setChestplate(new ItemStack(Material.AIR));
			} if (this.canClear(inventory.getLeggings(), "Leggings", i, clearAll)) {
				inventory.setLeggings(new ItemStack(Material.AIR));
			} if (this.canClear(inventory.getBoots(), "Boots", i, clearAll)) {
				inventory.setBoots(new ItemStack(Material.AIR));
			} if (ServerHandler.getServer().hasSpecificUpdate("1_9") && this.canClear(inventory.getItemInOffHand(), "OffHand", i, clearAll)) {
				PlayerHandler.getPlayer().setOffHandItem(player, new ItemStack(Material.AIR));
			} if (PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
				for (int k = 0; k < player.getOpenInventory().getTopInventory().getContents().length; k++) {
					if (this.canClear(player.getOpenInventory().getTopInventory().getItem(k), "CRAFTING[" + k + "]", i, clearAll)) {
						craftView.setItem(k, new ItemStack(Material.AIR));
					}
				}
			}
			for (int f = 0; f < inventory.getSize(); f++) {
				if (this.canClear(inventory.getItem(f), Integer.toString(f), i, clearAll)) {
					inventory.setItem(f, new ItemStack(Material.AIR));
				}
			}
		}
	}
	
   /**
    * Checks if the ItemStack is allowed to be cleared.
    * 
    * @param item - The ItemStack being checked.
    * @param slot - The slot the ItemStack is in.
    * @param i - The position in the cycle for protected items.
    * @param clearAll - If ALL items are expected to be cleared from the Player Inventory.
    * @return If the ItemStack is allowed.
    */
	public boolean canClear(final ItemStack item, final String slot, final int i, final boolean clearAll) {
		return item != null && !this.isBlacklisted(slot, item) && !this.isProtected(i, item) && (clearAll ? true : ItemHandler.getItem().containsNBTData(item));
	}
	
   /**
    * Checks if the ItemStack is blacklisted from being cleared.
    * 
    * @param item - The ItemStack being checked.
    * @param slot - The slot the ItemStack is in.
    * @return If the ItemStack is blacklisted.
    */
	private boolean isBlacklisted(String slot, ItemStack item) {
		String[] blacklist = null;
		String list = ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Blacklist");
		if (list != null && list.contains("{") && list.contains("}")) { blacklist = list.split(","); }
		try {
			if (blacklist != null) {
				for (String value: blacklist) {
					String valType = (Utils.getUtils().containsIgnoreCase(value, "{id") ? "id" : (Utils.getUtils().containsIgnoreCase(value, "{slot") ? "slot" : (Utils.getUtils().containsIgnoreCase(value, "{name") ? "name" : "")));
					String inputResult = StringUtils.substringBetween(value, "{" + valType + ":", "}");
					if (valType.equalsIgnoreCase("id") && item.getType() == ItemHandler.getItem().getMaterial(inputResult.trim(), null)) {
						return true;
					} else if (valType.equalsIgnoreCase("slot") && slot.trim().equalsIgnoreCase(inputResult.trim())) {
						return true;
					} else if (valType.equalsIgnoreCase("name") && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
						&& ChatColor.stripColor(item.getItemMeta().getDisplayName()).trim().equalsIgnoreCase(inputResult.trim())) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{ItemMap} It looks like the Blacklist section is missing quotations or apostrohes.");
			ServerHandler.getServer().logSevere("{ItemMap} Include quotations or apostrophes at the beginning and the end or this error will persist.");
			ServerHandler.getServer().logSevere("{ItemMap} The blacklist should look like '{id:DIAMOND}, {slot:0}' or \"{id:DIAMOND}, {slot:0}\".");
		}
		return false;
	}
	
   /**
    * Checks if the ItemMap is obtainable by the Player.
    * 
    * @param player - The Player that is trying to obtain the ItemMap.
    * @param itemMap - The ItemMap being checked.
    * @param session - The current set items session.
    * @param gamemode - The current GameMode of the Player.
    * @return If the ItemMap is Obtainable.
    */
	public boolean isObtainable(final Player player, final ItemMap itemMap, final int session, final TriggerType type, final GameMode gamemode) {
		if (!itemMap.hasItem(player) || itemMap.isAlwaysGive()) {
			boolean firstJoin = SQLite.getLite(false).hasFirstJoined(player, itemMap);
			boolean firstWorld = SQLite.getLite(false).hasFirstWorld(player, itemMap);
			boolean ipLimit = SQLite.getLite(false).isIPLimited(player, itemMap);
			if (itemMap.isLimitMode(gamemode)) {
				if ((!firstJoin || (firstJoin && itemMap.isOnlyFirstLife() && type.equals(TriggerType.RESPAWN))) && !firstWorld && !ipLimit && this.canOverwrite(player, itemMap)) {
					return true;
				} else if (!firstJoin && !firstWorld && !ipLimit) {
					if (session != 0 && this.failCount.get(session) != null) {
						this.failCount.put(session, this.failCount.get(session) + 1);
					} else if (session != 0) { this.failCount.put(session, 1); }
					ServerHandler.getServer().logDebug("{ItemMap} " + player.getName() + " has failed to receive item: " + itemMap.getConfigName() + ".");
					return false;
				} else { 
					if (firstJoin) { 
						ServerHandler.getServer().logDebug("{ItemMap} " + player.getName() + " has already received first-join " + itemMap.getConfigName() + ", they can no longer recieve this."); 
						return false;
					} else if (firstWorld) { 
						ServerHandler.getServer().logDebug("{ItemMap} " + player.getName() + " has already received first-world " + itemMap.getConfigName() + ", they can no longer recieve this in " + player.getWorld().getName() + "."); 
						return false;
					} else if (ipLimit) { 
						ServerHandler.getServer().logDebug("{ItemMap} " + player.getName() + " has already received ip-limited " + itemMap.getConfigName() + ", they will only recieve this on their dedicated ip.");
						return false;
					}
				}
			}
		}
		ServerHandler.getServer().logDebug("{ItemMap} " + player.getName() + " already has item: " + itemMap.getConfigName() + ".");
		return false;
	}
	
   /**
    * Checks if the ItemMap can overwrite itself.
    * 
    * @param player - The Player that is trying to overwrite the ItemMap.
    * @param itemMap - The ItemMap being checked.
    * @return If the ItemMap is Overwritable.
    */
	private boolean canOverwrite(final Player player, final ItemMap itemMap) {
		try {
			if (this.isOverwritable(player, itemMap) || (itemMap.isDropFull() || ((itemMap.isGiveNext() || itemMap.isMoveNext()) && player.getInventory().firstEmpty() != -1))) { return true; }
		} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
		return false;
	}
	
   /**
    * Gets if the ItemMap can overwrite itself.
    * 
    * @param player - The Player that is trying to overwrite the ItemMap.
    * @param itemMap - The ItemMap being checked.
    * @return If the ItemMap is Overwritable.
    */
	private boolean isOverwritable(final Player player, final ItemMap itemMap) {
		try {
			String overWrite = ConfigHandler.getConfig(false).getFile("items.yml").getString("items-Overwrite");
			if (itemMap.isOverwritable() || (((overWrite == null || (overWrite != null && Utils.getUtils().containsLocation(player.getWorld().getName(), overWrite.replace(" ", ""))))) 
					|| (ConfigHandler.getConfig(false).getFile("items.yml").getBoolean("items-Overwrite")))) {
				return true; 
			} else if (CustomSlot.ARBITRARY.isSlot(itemMap.getSlot()) && player.getInventory().firstEmpty() == -1) {
				return false;
			} else if (CustomSlot.HELMET.isSlot(itemMap.getSlot()) && player.getInventory().getHelmet() != null) {
				return false;
			} else if (CustomSlot.CHESTPLATE.isSlot(itemMap.getSlot()) && player.getInventory().getChestplate() != null) {
				return false;
			} else if (CustomSlot.LEGGINGS.isSlot(itemMap.getSlot()) && player.getInventory().getLeggings() != null) {
				return false;
			} else if (CustomSlot.BOOTS.isSlot(itemMap.getSlot()) && player.getInventory().getBoots() != null) {
				return false;
			} else if (ServerHandler.getServer().hasSpecificUpdate("1_9") && CustomSlot.OFFHAND.isSlot(itemMap.getSlot())) {
				if (player.getInventory().getItemInOffHand().getType() != Material.AIR) {
					return false;
				}
			} else if (Utils.getUtils().isInt(itemMap.getSlot()) && player.getInventory().getItem(Integer.parseInt(itemMap.getSlot())) != null) {
				return false;
			}
		} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
		return true;
	}
	
   /**
    * Sends the number of items that the Player failed to Obtain.
    * 
    * @param player - The Player that has failed to be given some items.
    * @param session - The current set items session.
    */
	public void sendFailCount(final Player player, final int session) {
		if (this.failCount.get(session) != null && this.failCount.get(session) != 0) {
			String overWrite = ConfigHandler.getConfig(false).getFile("items.yml").getString("items-Overwrite");
			if ((overWrite != null && Utils.getUtils().containsLocation(player.getWorld().getName(), overWrite.replace(" ", "")))) {
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[7] = this.failCount.get(session).toString();
				LanguageAPI.getLang(false).sendLangMessage("General.failedInventory", player, placeHolders);
			} else {
				String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[7] = this.failCount.get(session).toString();
				LanguageAPI.getLang(false).sendLangMessage("General.failedOverwrite", player, placeHolders);
			}
			this.failCount.remove(session);
		}
	}
	
   /**
    * Sets the ItemMap to the Player Inventory Slots.
    * 
    * @param player - The Player to have the ItemMap set to their Inventory slot(s).
    * @param itemMap - The ItemMap to be given to the Player.
    * @param size - The expected stack size of the item.
    */
	public void setInvSlots(final Player player, final ItemMap itemMap, final int size) {
		ItemStack existingItem = ItemHandler.getItem().getItem(player, itemMap);
		ItemStack item = itemMap.getItem(player).clone();
		this.shiftItem(player, itemMap);
		int nextSlot = this.nextItem(player, itemMap);
		boolean overWrite = itemMap.isOverwritable() || ConfigHandler.getConfig(false).getFile("items.yml").getBoolean("items-Overwrite");
		if (size > 1) { item.setAmount(size); }
		if ((size > 1 || itemMap.isAlwaysGive()) && !overWrite && existingItem != null) {
			player.getInventory().addItem(item);
		} else if (nextSlot != 0) {
			player.getInventory().setItem(nextSlot, item);
		} else if (player.getInventory().firstEmpty() != -1 || overWrite) {
			player.getInventory().setItem(Integer.parseInt(itemMap.getSlot()), item);
		} else if (itemMap.isDropFull()) { 
			player.getWorld().dropItem(player.getLocation(), item);
		}
		ServerHandler.getServer().logDebug("{ItemMap} Given the Item: " + itemMap.getConfigName() + ".");
		SQLite.getLite(false).saveItemData(player, itemMap);
	}
	
   /**
    * Sets the ItemMap to the Player Inventory Custom Slots.
    * 
    * @param player - The Player to have the ItemMap set to their Inventory slot(s).
    * @param itemMap - The ItemMap to be given to the Player.
    * @param size - The expected stack size of the item.
    */
	public void setCustomSlots(final Player player, final ItemMap itemMap, final int size) {
		int craftSlot = Utils.getUtils().getSlotConversion(itemMap.getSlot());
		ItemStack existingItem = ItemHandler.getItem().getItem(player, itemMap);
		ItemStack item = itemMap.getItem(player).clone();
		this.shiftItem(player, itemMap);
		int nextSlot = this.nextItem(player, itemMap);
		boolean overWrite = itemMap.isOverwritable() || ConfigHandler.getConfig(false).getFile("items.yml").getBoolean("items-Overwrite");
		if (size > 1) { item.setAmount(size); }
		if ((size > 1 || itemMap.isAlwaysGive()) && !overWrite && existingItem != null) {
			player.getInventory().addItem(item);
		} else if (nextSlot != 0) {
			player.getInventory().setItem(nextSlot, item);
		} else if (CustomSlot.ARBITRARY.isSlot(itemMap.getSlot()) && player.getInventory().firstEmpty() != -1) {
			player.getInventory().addItem(item);
		} else if (CustomSlot.HELMET.isSlot(itemMap.getSlot()) && (existingItem == null || overWrite)) {
			player.getEquipment().setHelmet(item);
		} else if (CustomSlot.CHESTPLATE.isSlot(itemMap.getSlot()) && (existingItem == null || overWrite)) {
			player.getEquipment().setChestplate(item);
		} else if (CustomSlot.LEGGINGS.isSlot(itemMap.getSlot()) && (existingItem == null || overWrite)) {
			player.getEquipment().setLeggings(item);
		} else if (CustomSlot.BOOTS.isSlot(itemMap.getSlot()) && (existingItem == null || overWrite)) {
			player.getEquipment().setBoots(item);
		} else if (ServerHandler.getServer().hasSpecificUpdate("1_9") && CustomSlot.OFFHAND.isSlot(itemMap.getSlot()) && (existingItem == null || overWrite)) {
			PlayerHandler.getPlayer().setOffHandItem(player, item);
		} else if (craftSlot != -1 && (existingItem == null || overWrite)) {
			if (craftSlot == 0) {
				ServerHandler.getServer().runAsyncThread(main -> {
					if (PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
			    			player.getOpenInventory().getTopInventory().setItem(craftSlot, item);
			    			PlayerHandler.getPlayer().updateInventory(player, 1L);
			    		}
					}, 2L);
			} else {
				player.getOpenInventory().getTopInventory().setItem(craftSlot, item);
			}
		} else if (itemMap.isDropFull()) {
			player.getWorld().dropItem(player.getLocation(), item);
		}
		ServerHandler.getServer().logDebug("{ItemMap} Given the Item: " + itemMap.getConfigName() + ".");
		SQLite.getLite(false).saveItemData(player, itemMap);
	}
	
   /**
    * Moves any pre-exiting items in the players defined ItemMap slot to the next available slot.
    * 
    * @param player - The Player to have the ItemMap set to their Inventory slot(s).
    * @param itemMap - The ItemMap to be given to the Player.
    */
	public void shiftItem(final Player player, final ItemMap itemMap) {
		int i = 0; int k = 0;
		if (Utils.getUtils().isInt(itemMap.getSlot())) { i = Integer.parseInt(itemMap.getSlot()); k = i; }
		ItemStack existingItem = ItemHandler.getItem().getItem(player, itemMap);
		if (itemMap.isMoveNext() && existingItem != null && player.getInventory().firstEmpty() != -1) {
			for (i = 0; i <= 35; i++) {
				if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) {
					player.getInventory().setItem(i, existingItem);
					existingItem.setAmount(0);
					existingItem.setType(Material.AIR);
					existingItem.setData(new ItemStack(Material.AIR).getData());
					return;
				} else if (i == 35) {
					for (k = 0; k >= 0; k--) {
						if (player.getInventory().getItem(k) == null || player.getInventory().getItem(k).getType() == Material.AIR) {
							player.getInventory().setItem(k, existingItem);
							existingItem.setAmount(0);
							existingItem.setType(Material.AIR);
							existingItem.setData(new ItemStack(Material.AIR).getData());
							return;
						}
					}
				}
			}
		}
	}
	
   /**
    * Gets the next available inventory slot from the defined ItemMap slot,
    * only if the give-next itemflag is defined.
    * 
    * @param player - The Player to have the ItemMap set to their Inventory slot(s).
    * @param itemMap - The ItemMap to be given to the Player.
    * @return The next available inventory slot.
    */
	public int nextItem(final Player player, final ItemMap itemMap) {
		int i = 0; int k = 0;
		if (Utils.getUtils().isInt(itemMap.getSlot())) { i = Integer.parseInt(itemMap.getSlot()); k = i; }
		ItemStack existingItem = ItemHandler.getItem().getItem(player, itemMap);
		if (itemMap.isGiveNext() && existingItem != null && player.getInventory().firstEmpty() != -1) {
			for (i = 0; i <= 35; i++) {
				if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) {
					return i;
				} else if (i == 35) {
					for (k = 0; k >= 0; k--) {
						if (player.getInventory().getItem(k) == null || player.getInventory().getItem(k).getType() == Material.AIR) {
							return k;
						}
					}
				}
			}
		}
		return 0;
	}
	
   /**
    * Executes the Global Commands defined in the config.
    * 
    * @param player - The Player having the commands executed.
    */
	public void triggerCommands(final Player player) {
		ServerHandler.getServer().runAsyncThread(main -> {
			if ((ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.enabled-worlds") != null && ConfigHandler.getConfig(false).getFile("config.yml").getStringList("Active-Commands.commands") != null) 
					&& (!ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("DISABLED") || !ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("FALSE"))) {
				String commandsWorlds = ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.enabled-worlds").replace(" ", "");
				if (commandsWorlds == null) { commandsWorlds = "DISABLED"; }
				String[] compareWorlds = commandsWorlds.split(",");
				for (String compareWorld: compareWorlds) {
					if (compareWorld.equalsIgnoreCase(player.getWorld().getName()) || compareWorld.equalsIgnoreCase("ALL") || compareWorld.equalsIgnoreCase("GLOBAL")) {
						for (String commands: ConfigHandler.getConfig(false).getFile("config.yml").getStringList("Active-Commands.commands")) {
							String formatCommand = Utils.getUtils().translateLayout(commands, player).replace("first-join: ", "").replace("first-join:", "");
							if (!SQLite.getLite(false).hasFirstCommanded(player, formatCommand)) {
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), formatCommand);
								if (Utils.getUtils().containsIgnoreCase(commands, "first-join:")) {
									SQLite.getLite(false).saveFirstCommandData(player, formatCommand);
								}
							}
						}
					}
					break;
				}
			}
		});
	}
	
   /**
    * Gets the ItemDelay.
    * 
    * @return The items-Delay that is to be expected before the items are given.
    */
	private long getItemDelay() {
		if ((this.getClearDelay() / 20) >= (ConfigHandler.getConfig(false).getFile("items.yml").getInt("items-Delay") / 2) && this.getClearDelay() != -1) { return this.getClearDelay() + 1; }
		return ConfigHandler.getConfig(false).getFile("items.yml").getInt("items-Delay") * 10L;
	}
	
   /**
    * Gets the Delay before clearing the items.
    * 
    * @return The clear-Delay before the items are cleared.
    */
	private long getClearDelay() {
		if (!Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Join"), "DISABLED") 
				&& !Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Join"), "FALSE")
				|| !Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.World-Switch"), "DISABLED")
				&& !Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.World-Switch"), "FALSE")
				|| !Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Region-Enter"), "DISABLED")
				&& !Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Region-Enter"), "FALSE")) {
				return ConfigHandler.getConfig(false).getFile("config.yml").getInt("Clear-Items.Delay-Tick");
		}
		return -1;
	}
	
   /**
    * Checks the ItemStack to see if it is a protected item.
    * 
    * @param i - The position in the protectItems List when cycling.
    * @param item - The ItemStack being checked.
    * @return If the ItemStack is a protected ItemMap.
    */
	public boolean isProtected(final int i, final ItemStack item) {
			return !this.protectItems.isEmpty() && this.protectItems.get(i).isSimilar(item) && i == (this.protectItems.size() - 1);
	}
	
   /**
    * Gets the list of ItemMaps that should be protected from clearing.
    * 
    * @return The list of protected ItemMaps.
    */
	public List<ItemMap> getProtectItems() {
		List<ItemMap> protectItems = new ArrayList<ItemMap>();
		if (Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Options"), "PROTECT")) {
			for (ItemMap item: this.getItems()) {
				if (item.isOnlyFirstJoin() || item.isOnlyFirstLife() || item.isOnlyFirstWorld()) {
					protectItems.add(item);
				}
			}
		}
		return protectItems;
	}
	
   /**
    * Clears any crafting recipes for the custom items.
    * 
    */
  	public void clearRecipes() {
  		List < Recipe > backupRecipes = new ArrayList < Recipe > ();
  		Iterator < Recipe > recipes = Bukkit.getServer().recipeIterator();
  		while (recipes.hasNext()) {
  			Recipe recipe = recipes.next();
  			ItemStack result = recipe.getResult();
  			boolean backupItem = true;
  			for (ItemMap itemMap: this.getItems()) {
  				if (itemMap.isSimilar(result) && !itemMap.getIngredients().isEmpty()) {
  					backupItem = false;
  				}
  			}
  			if (backupItem) { backupRecipes.add(recipe); }
  		}
  		Bukkit.getServer().clearRecipes();
  		for (Recipe recipe: backupRecipes) { try { Bukkit.getServer().addRecipe(recipe); } catch (IllegalStateException e) { } }
  	}
	
   /**
    * Creates a duplicate items HashMap.
    * 
    * @return The duplicated items HashMap.
    */
	public List < ItemMap > copyItems() {
		List < ItemMap > itemsCopy = new ArrayList < ItemMap > ();
		for (ItemMap itemMap : this.items) { 
			itemsCopy.add(itemMap.clone());
		}
		return itemsCopy;
	}
	
   /**
    * Adds a new item to the items List.
    * 
    * @param itemMap - The ItemMap to be added to the items List.
    */
	public void addItem(final ItemMap itemMap) {
		this.items.add(itemMap);
	}
	
   /**
    * Adds a new item to the crafting items List.
    * 
    * @param itemMap - The crafting ItemMap to be added to the items List.
    */
	public void addCraftingItem(final ItemMap itemMap) {
		if (itemMap.isCraftingItem()) { 
			this.craftingItems.add(itemMap); 
		}
	}
	
   /**
    * Gets the current ItemMap List.
    * 
    * @return The current ItemMap list.
    */
	public List < ItemMap > getItems() {
		return this.items;
	}
	
   /**
    * Gets the current crafting ItemMap List.
    * 
    * @return The current crafting ItemMap list.
    */
	public List < ItemMap > getCraftingItems() {
		return this.craftingItems;
	}

   /**
    * Clears the existing ItemMaps for the items List.
    * 
    */
	public void clearItems() {
		this.clearRecipes();
		this.items = new ArrayList < ItemMap >();
		this.craftingItems = new ArrayList < ItemMap >();
	}
	
   /**
    * Trigger types.
    * 
    */
	public enum CustomSlot {
		HELMET("Helmet"),
		CHESTPLATE("Chestplate"),
		LEGGINGS("Leggings"),
		BOOTS("Boots"),
		OFFHAND("Offhand"),
		CRAFTING("Crafting"),
		ARBITRARY("Arbitrary");
		private final String name;
		public boolean isSlot(String slot) { return this.name.equalsIgnoreCase(slot); }
		private CustomSlot(String name) { this.name = name; }
	}	
	
   /**
    * Trigger types.
    * 
    */
	public enum TriggerType {
		JOIN("Join"),
		RESPAWN("Respawn"),
		WORLDSWITCH("World-Switch"),
		LIMITSWITCH("Limit-Modes"),
		REGIONENTER("Region-Enter"),
		REGIONLEAVE("Region-Leave"),
		DEFAULT("DEFAULT");
		private final String name;
		private TriggerType(String name) { this.name = name; }
	}	
	
   /**
    * Gets the instance of the ItemUtilities.
    * 
    * @return The ItemUtilities instance.
    */
    public static ItemUtilities getUtilities() { 
        if (utilities == null) { utilities = new ItemUtilities(); }
        return utilities; 
    } 
}