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
import org.bukkit.ChatColor;
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
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.SQL;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;

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
		if (player == null || item == null) { return true; }
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
			if (item.getUISlot().equalsIgnoreCase(slot)) {
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
    * @param world - The World that the player is from.
    * @param type - The TriggerType that is being performed.
    * @param newMode - The GameMode of the Player.
    * @param region - The region the Player is in.
    */
	public void setAuthenticating(final Player player, final World world, TriggerType type, final GameMode newMode, final String region) {
		if (DependAPI.getDepends(false).authMeEnabled()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try { 
						if (fr.xephi.authme.api.v3.AuthMeApi.getInstance().isAuthenticated(player)) {
							setItems(player, world, type, newMode, region);
							this.cancel();
						}
					} catch (NoClassDefFoundError e) {
						ServerHandler.getServer().logSevere("{ItemMap} You are using an outdated version of AuthMe, custom items will not be given after login.");
						this.cancel();
					}
				}
			}.runTaskTimer(ItemJoin.getInstance(), 0, 20);
		} else { 
			this.setItems(player, world, type, newMode, region);
		}
	}
	
   /**
    * Sets the items to be given.
    * 
    * @param player - The Player that is having their items set.
    * @param world - The World that the player is from.
    * @param type - The TriggerType that is being performed.
    * @param newMode - The GameMode of the Player.
    * @param region - The region the Player is in.
    */
	public void setItems(final Player player, final World world, final TriggerType type, final GameMode newMode, final String region) {
		this.safeSet(player, world, type, region);
		if (this.getItemDelay() != 0 && type != TriggerType.LIMIT_SWITCH && type != TriggerType.REGION_ENTER && type != TriggerType.REGION_LEAVE) { 
			SchedulerUtils.getScheduler().runLater(this.getItemDelay(), () -> {
				ItemHandler.getItem().restoreCraftItems(player);
				this.handleItems(player, type, newMode, region);
			});
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
	private void handleItems(final Player player, TriggerType type, final GameMode gameMode, final String region) {
		final ItemMap randomMap = Chances.getChances().getRandom(player);
		final int session = Utils.getUtils().getRandom(1, 100000);
		for (ItemMap item : this.getItems()) { 
			item.setAnimations(player);
			if (((type.equals(TriggerType.JOIN) && item.isGiveOnJoin()) 
			  || (type.equals(TriggerType.RESPAWN) && item.isGiveOnRespawn())
			  || (type.equals(TriggerType.WORLD_SWITCH) && item.isGiveOnWorldSwitch())
			  || (type.equals(TriggerType.LIMIT_SWITCH) && item.isUseOnLimitSwitch())
		      || ((((type.equals(TriggerType.REGION_ENTER) && (item.isGiveOnRegionEnter() || item.isGiveOnRegionAccess())) 
			  || (type.equals(TriggerType.REGION_LEAVE) && (item.isGiveOnRegionLeave() || item.isGiveOnRegionEgress()))) && item.inRegion(region))))
			   && item.isLimitMode(gameMode) && item.inWorld(player.getWorld()) && Chances.getChances().isProbability(item, randomMap) && item.conditionMet(player, "trigger-conditions")
			   && PlayerHandler.getPlayer().isEnabled(player) && item.hasPermission(player) 
			   && this.isObtainable(player, item, session, type)) {
				item.giveTo(player); 
			} else if (((type.equals(TriggerType.LIMIT_SWITCH) && item.isUseOnLimitSwitch() && !item.isLimitMode(gameMode))
					|| (((type.equals(TriggerType.REGION_LEAVE) && item.isGiveOnRegionAccess()) || (type.equals(TriggerType.REGION_ENTER) && item.isGiveOnRegionEgress())) && item.inRegion(region)))
					&& item.inWorld(player.getWorld()) && item.hasItem(player)) {
				item.removeFrom(player);
			} else if (item.isAutoRemove() && (!item.inWorld(player.getWorld()) || !item.isLimitMode(gameMode)) && item.hasItem(player)) {
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
	private void safeSet(final Player player, final World world, final TriggerType type, final String region) {
		if (Utils.getUtils().splitIgnoreCase(ConfigHandler.getConfig(false).getHotbarTriggers(), type.name, ",")) { PlayerHandler.getPlayer().setHotbarSlot(player, ConfigHandler.getConfig(false).getHotbarSlot()); }
		if (type.equals(TriggerType.REGION_LEAVE)) { DependAPI.getDepends(false).getGuard().pasteReturnItems(player, region); }
		if (type.equals(TriggerType.WORLD_SWITCH)) { this.pasteReturnItems(type, player, world.getName()); }
		if (type.equals(TriggerType.REGION_ENTER)) { this.clearEvent(type, player, "", region); }
		if (this.getClearDelay() != 0) {
			SchedulerUtils.getScheduler().runLater(this.getClearDelay(), () -> {
				if (type.equals(TriggerType.JOIN) || type.equals(TriggerType.WORLD_SWITCH)) {
					this.clearEvent(type, player, player.getWorld().getName(), "");
				}
				this.triggerCommands(player, type);
			});
		} else {
			if (type.equals(TriggerType.JOIN) || type.equals(TriggerType.WORLD_SWITCH)) {
				this.clearEvent(type, player, player.getWorld().getName(), "");
			}
			this.triggerCommands(player, type);
		}
	}
	
   /**
    * Sets the Players items to be cleared upon performing the specified event.
    * 
    * @param type - The trigger type.
    * @param player - The Player performing the event.
    * @param world - The world the Player is in.
    * @param event - The event/trigger being performed.
    * @param region - The region the Player is in (if any).
    */
	private void clearEvent(final TriggerType type, final Player player, final String world, final String region) {
		String clearEvent = ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items." + type.name);
		if (clearEvent != null && ((region != null && !region.isEmpty() && Utils.getUtils().containsLocation(region, clearEvent.replace(" ", ""))) || Utils.getUtils().containsLocation(world, clearEvent.replace(" ", "")))) {
			if ((Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Options"), "PROTECT_OP") && player.isOp())
				|| (Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Options"), "PROTECT_CREATIVE") && PlayerHandler.getPlayer().isCreativeMode(player))) {
			} else {
				String clearType = ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Type");
				if (clearType != null && (clearType.equalsIgnoreCase("ALL") || clearType.equalsIgnoreCase("GLOBAL"))) {
					this.clearItems(type, player, region, type.name, true);
				} else if (clearType != null && clearType.equalsIgnoreCase("ITEMJOIN")) {
					this.clearItems(type, player, region, type.name, false);
				} else if (clearType != null) {
					ServerHandler.getServer().logSevere("{ItemMap} " + clearType + " for Clear-Items in the config.yml is not a valid option.");
				}
			}
		}
	}
	
   /**
    * Clears the items from the Player Inventory.
    * 
    * @param type - The trigger type.
    * @param player - The Player performing the event.
    * @param event - The event/trigger being performed.
    * @param region - The region the Player is in (if any).
    * @param clearAll - If ALL items are expected to be cleared from the Player Inventory.
    */
	private void clearItems(final TriggerType type, final Player player, final String event, final String region, final boolean clearAll) {
		this.protectItems = this.getProtectItems();
		PlayerInventory inventory = player.getInventory();
		Inventory craftView = player.getOpenInventory().getTopInventory();
		DependAPI.getDepends(false).getGuard().saveReturnItems(player, event, region, craftView, inventory, true);
		this.saveReturnItems(type, player, player.getWorld().getName(), craftView, inventory, true);
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
	public boolean isObtainable(final Player player, final ItemMap itemMap, final int session, final TriggerType type) {
		if (!itemMap.hasItem(player) || itemMap.isAlwaysGive()) {
			DataObject firstJoin = SQL.getData().getData(new DataObject(Table.IJ_FIRST_JOIN, PlayerHandler.getPlayer().getPlayerID(player), "", itemMap.getConfigName()));
			DataObject firstWorld = SQL.getData().getData(new DataObject(Table.IJ_FIRST_WORLD, PlayerHandler.getPlayer().getPlayerID(player), player.getWorld().getName(), itemMap.getConfigName()));
			DataObject ipLimit = SQL.getData().getData(new DataObject(Table.IJ_IP_LIMITS, PlayerHandler.getPlayer().getPlayerID(player), player.getWorld().getName(), itemMap.getConfigName(), player.getAddress().getHostString()));
			if ((firstJoin == null || (itemMap.isOnlyFirstLife() && type.equals(TriggerType.RESPAWN))) && firstWorld == null 
					&& (ipLimit == null || (ipLimit != null && ipLimit.getPlayerId().equalsIgnoreCase(PlayerHandler.getPlayer().getPlayerID(player)))) && this.canOverwrite(player, itemMap)) {
				return true;
			} else if (firstJoin == null && firstWorld == null && ipLimit == null) {
				if (session != 0 && this.failCount.get(session) != null) {
					this.failCount.put(session, this.failCount.get(session) + 1);
				} else if (session != 0) { this.failCount.put(session, 1); }
				ServerHandler.getServer().logDebug("{ItemMap} " + player.getName() + " has failed to receive item: " + itemMap.getConfigName() + ".");
				return false;
			} else { 
				if (firstJoin != null) { 
					ServerHandler.getServer().logDebug("{ItemMap} " + player.getName() + " has already received first-join " + itemMap.getConfigName() + ", they can no longer recieve this."); 
					return false;
				} else if (firstWorld != null) { 
					ServerHandler.getServer().logDebug("{ItemMap} " + player.getName() + " has already received first-world " + itemMap.getConfigName() + ", they can no longer recieve this in " + player.getWorld().getName() + "."); 
					return false;
				} else if (ipLimit != null && !ipLimit.getPlayerId().equalsIgnoreCase(PlayerHandler.getPlayer().getPlayerID(player))) { 
					ServerHandler.getServer().logDebug("{ItemMap} " + player.getName() + " has already received ip-limited " + itemMap.getConfigName() + ", they will only recieve this on their dedicated ip.");
					return false;
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
	public boolean canOverwrite(final Player player, final ItemMap itemMap) {
		try {
			if ((itemMap.isCraftingItem() && Utils.getUtils().getSlotConversion(itemMap.getSlot()) == 0) || this.isOverwritable(player, itemMap) || (itemMap.isDropFull() || ((itemMap.isGiveNext() || itemMap.isMoveNext()) && player.getInventory().firstEmpty() != -1))) { return true; }
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
		SchedulerUtils.getScheduler().run(() -> {
			if (this.failCount.get(session) != null && this.failCount.get(session) != 0) {
				String overWrite = ConfigHandler.getConfig(false).getFile("items.yml").getString("items-Overwrite");
				if ((overWrite != null && Utils.getUtils().containsLocation(player.getWorld().getName(), overWrite.replace(" ", "")))) {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[7] = this.failCount.get(session).toString();
					LanguageAPI.getLang(false).sendLangMessage("general.failedInventory", player, placeHolders);
				} else {
					String[] placeHolders = LanguageAPI.getLang(false).newString(); placeHolders[7] = this.failCount.get(session).toString();
					LanguageAPI.getLang(false).sendLangMessage("general.failedOverwrite", player, placeHolders);
				}
				this.failCount.remove(session);
			}
		});
	}
	
   /**
    * Saves the current items in the Player Inventory to be returned later.
    * 
    * @param type - The trigger executed.
    * @param player - The Player that had their items saved.
    * @param world - The world to be checked.
    * @param type - The clear type that is being executed.
    * @param craftView - The players current CraftView.
    * @param inventory - The players current Inventory.
    * @param clearAll - If ALL items are being cleared.
    */
	public void saveReturnItems(final TriggerType type, final Player player, final String world, final Inventory craftView, final PlayerInventory inventory, final boolean clearAll) {
		boolean doReturn = Utils.getUtils().splitIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Options").replace(" ", ""), "RETURN_SWITCH", ",");
		List < ItemMap > protectItems = ItemUtilities.getUtilities().getProtectItems();
		if (type == TriggerType.WORLD_SWITCH && doReturn) {
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
			SQL.getData().saveData(new DataObject(Table.IJ_RETURN_SWITCH_ITEMS, PlayerHandler.getPlayer().getPlayerID(player), world, ItemHandler.getItem().serializeInventory(saveInventory)));
		}
	}
	
   /**
    * Returns the previously removed Region Items to the Player.
    * 
    * @param type - The trigger executed.
    * @param player - The Player that had their items returned.
    * @param world - The world to be checked.
    */
	public void pasteReturnItems(final TriggerType type, final Player player, final String world) {
		if (type == TriggerType.WORLD_SWITCH && Utils.getUtils().splitIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Clear-Items.Options").replace(" ", ""), "RETURN_SWITCH", ",")) {
			DataObject dataObject = SQL.getData().getData(new DataObject(Table.IJ_RETURN_SWITCH_ITEMS, PlayerHandler.getPlayer().getPlayerID(player), world, ""));
			Inventory inventory = (dataObject != null ? ItemHandler.getItem().deserializeInventory(dataObject.getInventory64().replace(world + ".", "")) : null);
			for (int i = 47; i >= 0; i--) {
				if (inventory != null && inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
					if (i <= 41) {
						player.getInventory().setItem(i, inventory.getItem(i).clone());
					} else if (i >= 42 && PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
						player.getOpenInventory().getTopInventory().setItem(i - 42, inventory.getItem(i).clone());
						PlayerHandler.getPlayer().updateInventory(player, 1L);
					}
				}
				SQL.getData().removeData(new DataObject(Table.IJ_RETURN_SWITCH_ITEMS, PlayerHandler.getPlayer().getPlayerID(player), world, ""));
			}
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
		SchedulerUtils.getScheduler().run(() -> {
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
				if (itemMap.getSlot().contains("%")) {
					String slot = Utils.getUtils().translateLayout(itemMap.getSlot(), player);
					if (Utils.getUtils().isInt(slot)) {
						player.getInventory().setItem(Integer.parseInt(slot), item);
					}
				} else {
					player.getInventory().setItem(Integer.parseInt(itemMap.getSlot()), item);
				}
			} else if (itemMap.isDropFull()) { 
				player.getWorld().dropItem(player.getLocation(), item);
			}
			ServerHandler.getServer().logDebug("{ItemMap} " + player.getName() + " has been given the item " + itemMap.getConfigName() + " in the world " + player.getWorld().getName() + ".");
		});
		DataObject ipLimit = SQL.getData().getData(new DataObject(Table.IJ_IP_LIMITS, PlayerHandler.getPlayer().getPlayerID(player), player.getWorld().getName(), itemMap.getConfigName(), player.getAddress().getHostString()));
		if ((itemMap.isOnlyFirstJoin() || itemMap.isOnlyFirstLife())) { SQL.getData().saveData(new DataObject(Table.IJ_FIRST_JOIN, PlayerHandler.getPlayer().getPlayerID(player), "", itemMap.getConfigName())); }
		if (itemMap.isOnlyFirstWorld()) { SQL.getData().saveData(new DataObject(Table.IJ_FIRST_WORLD, PlayerHandler.getPlayer().getPlayerID(player), player.getWorld().getName(), itemMap.getConfigName())); }
		if (itemMap.isIpLimted() && ipLimit == null) { SQL.getData().saveData(new DataObject(Table.IJ_IP_LIMITS, PlayerHandler.getPlayer().getPlayerID(player), player.getWorld().getName(), itemMap.getConfigName(), player.getAddress().getHostString())); }
	}
	
   /**
    * Sets the ItemMap to the Player Inventory Custom Slots.
    * 
    * @param player - The Player to have the ItemMap set to their Inventory slot(s).
    * @param itemMap - The ItemMap to be given to the Player.
    * @param size - The expected stack size of the item.
    */
	public void setCustomSlots(final Player player, final ItemMap itemMap, final int size) {
		SchedulerUtils.getScheduler().run(() -> {
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
			} else if (craftSlot != -1 && (existingItem == null || overWrite || craftSlot == 0)) {
				this.setCraftingSlots(player, item, craftSlot, 240);
			} else if (itemMap.isDropFull()) {
				player.getWorld().dropItem(player.getLocation(), item);
			}
			ServerHandler.getServer().logDebug("{ItemMap} " + player.getName() + " has been given the item " + itemMap.getConfigName() + " in " + player.getWorld().getName() + ".");
		});
		DataObject ipLimit = SQL.getData().getData(new DataObject(Table.IJ_IP_LIMITS, PlayerHandler.getPlayer().getPlayerID(player), player.getWorld().getName(), itemMap.getConfigName(), player.getAddress().getHostString()));
		if ((itemMap.isOnlyFirstJoin() || itemMap.isOnlyFirstLife())) { SQL.getData().saveData(new DataObject(Table.IJ_FIRST_JOIN, PlayerHandler.getPlayer().getPlayerID(player), "", itemMap.getConfigName())); }
		if (itemMap.isOnlyFirstWorld()) { SQL.getData().saveData(new DataObject(Table.IJ_FIRST_WORLD, PlayerHandler.getPlayer().getPlayerID(player), player.getWorld().getName(), itemMap.getConfigName())); }
		if (itemMap.isIpLimted() && ipLimit == null) { SQL.getData().saveData(new DataObject(Table.IJ_IP_LIMITS, PlayerHandler.getPlayer().getPlayerID(player), player.getWorld().getName(), itemMap.getConfigName(), player.getAddress().getHostString())); }
	}
	
   /**
    * Sets the ItemStack to the Player Inventory Crafting Slots.
    * 
    * @param player - The Player to have the ItemStack set to their Crafting slot(s).
    * @param itemStack - The ItemStack to be given to the Player.
    * @param craftSlot - The designated slot for the Crafting Item.
    * @param attempts - The number of attempts to give the Crafting Slot item before failing.
    */
	private void setCraftingSlots(final Player player, final ItemStack itemStack, final int craftSlot, int attempts) {
		if (attempts != 0) {
			if (craftSlot == 0) {
				SchedulerUtils.getScheduler().runLater(2L, () -> {
					if (PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
						player.getOpenInventory().getTopInventory().setItem(craftSlot, itemStack);
						PlayerHandler.getPlayer().updateInventory(player, 1L);
					} else { 
						SchedulerUtils.getScheduler().runLater(20L, () -> this.setCraftingSlots(player, itemStack, craftSlot, (attempts - 1))); 
					}
				});
			} else if (PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
				if (player.getOpenInventory().getTopInventory().getItem(0) != null && !player.getOpenInventory().getTopInventory().getItem(0).getType().equals(Material.AIR)) {
					ItemHandler.getItem().returnCraftingItem(player, 0, player.getOpenInventory().getTopInventory().getItem(0).clone(), 0L);
				}
				player.getOpenInventory().getTopInventory().setItem(craftSlot, itemStack);
			} else { 		
				SchedulerUtils.getScheduler().runLater(20L, () -> this.setCraftingSlots(player, itemStack, craftSlot, (attempts - 1)));
			}
		}
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
		else if (itemMap.getSlot().contains("%")) {
			String slot = Utils.getUtils().translateLayout(itemMap.getSlot(), player);
			if (Utils.getUtils().isInt(slot)) {
				i = Integer.parseInt(slot); k = i;
			}
		} 
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
		else if (itemMap.getSlot().contains("%")) {
			String slot = Utils.getUtils().translateLayout(itemMap.getSlot(), player);
			if (Utils.getUtils().isInt(slot)) {
				i = Integer.parseInt(slot); k = i;
			}
		} 
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
	public void triggerCommands(final Player player, TriggerType trigger) {
		if ((ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.enabled-worlds") != null && ConfigHandler.getConfig(false).getFile("config.yml").getStringList("Active-Commands.commands") != null) 
				&& (!ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("DISABLED") || !ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("FALSE"))
				&& ((Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.triggers"), TriggerType.JOIN.name) && trigger.equals(TriggerType.JOIN))
				|| (Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.triggers"), TriggerType.FIRST_JOIN.name) && trigger.equals(TriggerType.FIRST_JOIN))
				|| (Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.triggers"), TriggerType.WORLD_SWITCH.name) && trigger.equals(TriggerType.WORLD_SWITCH))
				|| (Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.triggers"), TriggerType.RESPAWN.name) && trigger.equals(TriggerType.RESPAWN)))) {
			String commandsWorlds = ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.enabled-worlds").replace(", ", ",");
			if (commandsWorlds == null) { commandsWorlds = "DISABLED"; }
			String[] compareWorlds = commandsWorlds.split(",");
			for (String compareWorld: compareWorlds) {
				if (compareWorld.equalsIgnoreCase(player.getWorld().getName()) || compareWorld.equalsIgnoreCase("ALL") || compareWorld.equalsIgnoreCase("GLOBAL")) {
					for (String commands: ConfigHandler.getConfig(false).getFile("config.yml").getStringList("Active-Commands.commands")) {
						String formatCommand = Utils.getUtils().translateLayout(commands, player).replace("first-join: ", "").replace("first-join:", "");
						DataObject dataObject = SQL.getData().getData(new DataObject(Table.IJ_FIRST_COMMANDS, PlayerHandler.getPlayer().getPlayerID(player), player.getWorld().getName(), formatCommand));
						if (!(dataObject != null && (Utils.getUtils().containsIgnoreCase(commands, "first-join:") || Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.triggers"), TriggerType.FIRST_JOIN.name)))) {
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), formatCommand);
							if (Utils.getUtils().containsIgnoreCase(commands, "first-join:") || Utils.getUtils().containsIgnoreCase(ConfigHandler.getConfig(false).getFile("config.yml").getString("Active-Commands.triggers"), TriggerType.FIRST_JOIN.name)) {
								SQL.getData().saveData(new DataObject(Table.IJ_FIRST_COMMANDS, PlayerHandler.getPlayer().getPlayerID(player), player.getWorld().getName(), formatCommand));
							}
						}
					}
					break;
				}
			}
		}
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
	public long getClearDelay() {
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
		FIRST_JOIN("First-Join"),
		JOIN("Join"),
		RESPAWN("Respawn"),
		WORLD_SWITCH("World-Switch"),
		LIMIT_SWITCH("Limit-Modes"),
		REGION_ENTER("Region-Enter"),
		REGION_LEAVE("Region-Leave"),
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