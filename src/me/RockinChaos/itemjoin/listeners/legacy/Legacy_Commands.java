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
package me.RockinChaos.itemjoin.listeners.legacy;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.Utils;

/**
* Handles the Interaction events for custom items.
* 
* @deprecated This is a LEGACY listener, only use on Minecraft versions below 1.8.
*/
public class Legacy_Commands implements Listener {

   /**
	* Runs the inventory commands for the custom item upon clicking it.
	* 
	* @param event - InventoryClickEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	@EventHandler(ignoreCancelled = false)
	private void onInventory(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		String action = event.getAction().toString();
		String slot = (event.getSlotType().name().equalsIgnoreCase("CRAFTING") ? "CRAFTING[" + String.valueOf(event.getSlot()) + "]" : String.valueOf(event.getSlot()));
		this.runCommands(player, item, action, slot);
	}
	
   /**
	* Runs the on_death commands for the custom item upon player death.
	* 
	* @param event - PlayerDeathEvent.
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onDeath(PlayerDeathEvent event) {
		ListIterator < ItemStack > litr = event.getDrops().listIterator();
		while (litr.hasNext()) {
			ItemStack item = litr.next();
			this.runCommands(event.getEntity(), item, "ON_DEATH", null);
		}
	}

   /**
	* Runs the on_hold commands for the custom item upon holding it.
	* 
	* @param event - PlayerItemHeldEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	@EventHandler(ignoreCancelled = false)
	private void onHold(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(event.getNewSlot());
		String slot = String.valueOf(event.getNewSlot());
		this.runCommands(player, item, "ON_HOLD", slot);
	}
	
   /**
	* Runs the on_equip and un_equip commands for the custom item upon clicking and moving it to the armor slots.
	* 
	* @param event - InventoryClickEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEquipClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (Utils.getUtils().containsIgnoreCase(event.getAction().name(), "HOTBAR") && event.getView().getBottomInventory().getSize() >= event.getHotbarButton() && event.getHotbarButton() >= 0
		 && event.getView().getBottomInventory().getItem(event.getHotbarButton()) != null && event.getView().getBottomInventory().getItem(event.getHotbarButton()).getType() != Material.AIR) {
			this.equipCommands(player, event.getView().getBottomInventory().getItem(event.getHotbarButton()), "ON_EQUIP", String.valueOf(event.getSlot()), event.getSlotType());
		}
		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			this.equipCommands(player, event.getCurrentItem(), "UN_EQUIP", String.valueOf(event.getSlot()), event.getSlotType());
		}
		if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) { 
			this.equipCommands(player, event.getCursor(), "ON_EQUIP", String.valueOf(event.getSlot()), event.getSlotType());
		}
	}
	
   /**
	* Runs the on_equip and un_equip commands for the custom item upon clicking and dragging it to the armor slots.
	* 
	* @param event - InventoryDragEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEquipDrag(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		Set<Integer> slideSlots = event.getInventorySlots();
		int slot = 0; for (int actualSlot: slideSlots) { slot = actualSlot; break; }
		if (event.getOldCursor() != null && event.getOldCursor().getType() != Material.AIR) {
			this.equipCommands(player, event.getOldCursor(), "ON_EQUIP", String.valueOf(slot), SlotType.ARMOR);
		}
	}
	
   /**
	* Runs the on_equip and un_equip commands for the custom item upon right clicking it from their hand to the armor slots.
	* 
	* @param event - PlayerInteractEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEquip(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (item != null && item.getType() != Material.AIR && !PlayerHandler.getPlayer().isMenuClick(player.getOpenInventory(), event.getAction())) {
			String[] itemType = item.getType().name().split("_");
			if (itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && Utils.getUtils().isInt(Utils.getUtils().getArmorSlot(itemType[1], true)) 
				&& player.getInventory().getItem(Integer.parseInt(Utils.getUtils().getArmorSlot(itemType[1], true))) == null) {
				this.equipCommands(player, event.getItem(), "ON_EQUIP", Utils.getUtils().getArmorSlot(itemType[1], true), SlotType.ARMOR);
			}
		}
	}
	
   /**
	* Runs the commands upon right or left clicking the custom item on an entity.
	* 
	* @param event - PlayerInteractEntityEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof org.bukkit.entity.ItemFrame) {
			ItemStack item;
			if (ServerHandler.getServer().hasSpecificUpdate("1_9")) { item = PlayerHandler.getPlayer().getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
			else { item = PlayerHandler.getPlayer().getPerfectHandItem(event.getPlayer(), ""); }
			Player player = event.getPlayer();
			String action = Action.RIGHT_CLICK_BLOCK.name();
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(PlayerHandler.getPlayer().getHandItem(player), null, player.getWorld());
			if (itemMap != null && itemMap.isSimilar(item)) {
				this.runCommands(player, item, action, String.valueOf(player.getInventory().getHeldItemSlot()));
			}
		}
	}
	
   /**
	* Runs the commands upon physically interacting with the custom item.
	* 
	* @param event - PlayerInteractEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	@EventHandler(ignoreCancelled = false)
	private void onInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		final Player player = event.getPlayer();
		String action = event.getAction().toString();
		if ((PlayerHandler.getPlayer().isAdventureMode(player) && !action.contains("LEFT") 
				|| !PlayerHandler.getPlayer().isAdventureMode(player)) && !this.isDropEvent(event.getPlayer())) {
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(PlayerHandler.getPlayer().getHandItem(player), null, player.getWorld());
			if (itemMap != null && itemMap.isSimilar(item)) {
				this.runCommands(player, item, action, String.valueOf(player.getInventory().getHeldItemSlot()));
			}
		}
	}
	
   /**
	* Runs the commands upon left clicking with the custom item in adventure mode.
	* 
	* @param event - PlayerAnimationEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	@EventHandler(ignoreCancelled = false)
	private void onSwingArm(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		ItemStack item = PlayerHandler.getPlayer().getHandItem(player);
		if (PlayerHandler.getPlayer().isAdventureMode(player) && !this.isDropEvent(event.getPlayer())) {
			this.runCommands(player, item, "LEFT_CLICK_AIR", String.valueOf(player.getInventory().getHeldItemSlot()));
		}
	}
	
   /**
	* Places the player dropping an item into a temporary hashmap to be noted as,
	* having recently dropped an item. This is to prevent command execution when dropping an item using the item drop keybind.
	* 
	* @param event - PlayerDropItemEvent
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	@EventHandler(ignoreCancelled = false)
	private void onHandDrop(PlayerDropItemEvent event) {
		if (!this.isDropEvent(event.getPlayer())) {
			this.itemDrop.put(PlayerHandler.getPlayer().getPlayerID(event.getPlayer()), true);
			ServerHandler.getServer().runThread(main -> { 
				if (this.isDropEvent(event.getPlayer())) {
					this.itemDrop.remove(PlayerHandler.getPlayer().getPlayerID(event.getPlayer()));
				}
			}, 1L);
		}
	}
	
   /**
	* Runs the on_equip and un_equip custom item commands.
    * 
	* @param player - that is running the commands.
	* @param item - the item the player is trying to use to execute a command.
	* @param action - the action that is being performed.
	* @param slot - the slot the item originally resided in.
	* @param slotType - the SlotType the item originated in.
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	private void equipCommands(Player player, ItemStack item, String action, String slot, SlotType slotType) {
		try {
			String[] itemType = item.getType().name().split("_");
			if (slotType == SlotType.ARMOR && itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && !itemType[1].equalsIgnoreCase("HEAD") && (itemType[1].equalsIgnoreCase(Utils.getUtils().getArmorSlot(slot, false)) 
					|| (itemType[1].equalsIgnoreCase("HEAD") && Utils.getUtils().getArmorSlot(slot, false).equalsIgnoreCase("HELMET")))) {
				this.runCommands(player, item, action, slot);
			}
		} catch (Exception e) { }
	}
	
   /**
	* Runs the custom item commands.
	* 
	* @param player - that is running the commands.
	* @param item - the item the player is trying to use to execute a command.
	* @param action - the action that is being performed.
	* @param slot - the slot the item originally resided in.
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	private void runCommands(Player player, ItemStack item, String action, String slot) {
		ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item, null, player.getWorld());
		if (itemMap != null && itemMap.inWorld(player.getWorld()) && itemMap.hasPermission(player)) {
			itemMap.executeCommands(player, item, action, slot);
		}
	}
	
	private HashMap<String, Boolean> itemDrop = new HashMap<String, Boolean>();
   /**
	* Checks if the player recently attempted to drop an item.
	* 
	* @param player - The player being checked.
	* @return If the player has dropped the item.
	* @deprecated This is a LEGACY event, only use on Minecraft versions below 1.8.
	*/
	private boolean isDropEvent(Player player) {
		if (!((this.itemDrop.get(PlayerHandler.getPlayer().getPlayerID(player)) == null 
			 || (this.itemDrop.get(PlayerHandler.getPlayer().getPlayerID(player)) != null 
			 && !this.itemDrop.get(PlayerHandler.getPlayer().getPlayerID(player)))))) {
			return true;
		}
		return false;
	}
}
