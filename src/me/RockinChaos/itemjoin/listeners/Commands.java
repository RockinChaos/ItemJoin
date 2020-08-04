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
package me.RockinChaos.itemjoin.listeners;

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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.Utils;

public class Commands implements Listener {

   /**
	* Runs the inventory commands for the custom item upon clicking it.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onInventoryCommand(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		String action = event.getAction().toString();
		String slot = String.valueOf(event.getSlot());
		if (event.getSlotType().name().equalsIgnoreCase("CRAFTING")) { slot = "CRAFTING[" + slot + "]"; }
		if (this.setupCommands(player, item, action, slot)) { event.setCancelled(true); }
	}
	
   /**
	* Runs the on_death commands for the custom item upon player death.
	* 
	* @param event - PlayerDeathEvent.
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onDeathCommand(PlayerDeathEvent event) {
		ListIterator < ItemStack > litr = event.getDrops().listIterator();
		while (litr.hasNext()) {
			ItemStack item = litr.next();
			this.setupCommands(event.getEntity(), item, "ON_DEATH", null);
		}
	}

   /**
	* Runs the on_hold commands for the custom item upon holding it.
	* 
	* @param event - PlayerItemHeldEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onHoldCommand(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(event.getNewSlot());
		String slot = String.valueOf(event.getNewSlot());
		if (this.setupCommands(player, item, "ON_HOLD", slot)) { event.setCancelled(true); }
	}
	
   /**
	* Runs the on_equip and un_equip commands for the custom item upon clicking and moving it to the armor slots.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEquipClickCommand(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (Utils.getUtils().containsIgnoreCase(event.getAction().name(), "HOTBAR") && event.getView().getBottomInventory().getSize() >= event.getHotbarButton() 
		 && event.getView().getBottomInventory().getItem(event.getHotbarButton()) != null && event.getView().getBottomInventory().getItem(event.getHotbarButton()).getType() != Material.AIR) {
			if (!this.equipSetup(player, event.getView().getBottomInventory().getItem(event.getHotbarButton()), "ON_EQUIP", String.valueOf(event.getSlot()), event.getSlotType())) { event.setCancelled(true); }
		}
		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			if (!this.equipSetup(player, event.getCurrentItem(), "UN_EQUIP", String.valueOf(event.getSlot()), event.getSlotType())) { event.setCancelled(true); }
		}
		if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) { 
			if (!this.equipSetup(player, event.getCursor(), "ON_EQUIP", String.valueOf(event.getSlot()), event.getSlotType())) { event.setCancelled(true); } 
		}
	}
	
   /**
	* Runs the on_equip and un_equip commands for the custom item upon clicking and dragging it to the armor slots.
	* 
	* @param event - InventoryDragEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEquipDragCommand(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		Set<Integer> slideSlots = event.getInventorySlots();
		int slot = 0; for (int actualSlot: slideSlots) { slot = actualSlot; break; }
		if (event.getOldCursor() != null && event.getOldCursor().getType() != Material.AIR) {
			if (!this.equipSetup(player, event.getOldCursor(), "ON_EQUIP", String.valueOf(slot), SlotType.ARMOR)) { event.setCancelled(true); }
		}
	}
	
   /**
	* Runs the on_equip and un_equip commands for the custom item upon right clicking it from their hand to the armor slots.
	* 
	* @param event - PlayerInteractEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEquipInteractCommand(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (item != null && item.getType() != Material.AIR && !PlayerHandler.getPlayer().isMenuClick(player.getOpenInventory(), event.getAction())) {
			String[] itemType = item.getType().name().split("_");
			if (itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && Utils.getUtils().isInt(Utils.getUtils().getArmorSlot(itemType[1], true)) 
				&& player.getInventory().getItem(Integer.parseInt(Utils.getUtils().getArmorSlot(itemType[1], true))) == null && !this.equipSetup(player, event.getItem(), "ON_EQUIP", Utils.getUtils().getArmorSlot(itemType[1], true), SlotType.ARMOR)) { event.setCancelled(true); }
		}
	}
	
   /**
	* Runs the commands upon right or left clicking the custom item on an entity.
	* 
	* @param event - PlayerInteractEntityEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEntityCommand(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof org.bukkit.entity.ItemFrame) {
			ItemStack item;
			if (ServerHandler.getServer().hasSpecificUpdate("1_9")) { item = PlayerHandler.getPlayer().getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
			else { item = PlayerHandler.getPlayer().getPerfectHandItem(event.getPlayer(), ""); }
			Player player = event.getPlayer();
			String action = Action.RIGHT_CLICK_BLOCK.name();
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(PlayerHandler.getPlayer().getHandItem(player), null, player.getWorld());
			if (itemMap != null && itemMap.isSimilar(item)) {
				if (this.setupCommands(player, item, action, String.valueOf(player.getInventory().getHeldItemSlot()))) { event.setCancelled(true); }
			}
		}
	}
	
   /**
	* Runs the commands upon right or left clicking the custom item towards an entity.
	* 
	* @param event - PlayerInteractAtEntityEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onTargetEntityCommand(PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked().toString().equalsIgnoreCase("CraftArmorStand")) {
			ItemStack item;
			if (ServerHandler.getServer().hasSpecificUpdate("1_9")) { item = PlayerHandler.getPlayer().getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
			else { item = PlayerHandler.getPlayer().getPerfectHandItem(event.getPlayer(), ""); }
			Player player = event.getPlayer();
			String action = Action.RIGHT_CLICK_BLOCK.name();
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(PlayerHandler.getPlayer().getHandItem(player), null, player.getWorld());
			if (itemMap != null && itemMap.isSimilar(item)) {
				if (this.setupCommands(player, item, action, String.valueOf(player.getInventory().getHeldItemSlot()))) { event.setCancelled(true); }
			}
		}
	}

   /**
	* Runs the commands upon physically interacting with the custom item.
	* 
	* @param event - PlayerInteractEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onInteractCommand(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		final Player player = event.getPlayer();
		String action = event.getAction().toString();
		if (PlayerHandler.getPlayer().isAdventureMode(player) && !action.contains("LEFT") 
				|| !PlayerHandler.getPlayer().isAdventureMode(player)) {
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(PlayerHandler.getPlayer().getHandItem(player), null, player.getWorld());
			if (!PlayerHandler.getPlayer().isMenuClick(player.getOpenInventory(), event.getAction()) && itemMap != null && itemMap.isSimilar(item)) {
				if (this.setupCommands(player, item, action, String.valueOf(player.getInventory().getHeldItemSlot()))) { event.setCancelled(true); }
			}
		}
	}
	
   /**
	* Runs the commands upon left clicking with the custom item in adventure mode.
	* 
	* @param event - PlayerAnimationEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onSwingHandCommand(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		ItemStack item = PlayerHandler.getPlayer().getHandItem(player);
		if (PlayerHandler.getPlayer().isAdventureMode(player)) {
			if (this.setupCommands(player, item, "LEFT_CLICK_AIR", String.valueOf(player.getInventory().getHeldItemSlot()))) { event.setCancelled(true); }
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
	*/
	private boolean equipSetup(Player player, ItemStack item, String action, String slot, SlotType slotType) {
		try {
			String[] itemType = item.getType().name().split("_");
			if (slotType == SlotType.ARMOR && itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && !itemType[1].equalsIgnoreCase("HEAD") && (itemType[1].equalsIgnoreCase(Utils.getUtils().getArmorSlot(slot, false)) 
					|| (itemType[1].equalsIgnoreCase("HEAD") && Utils.getUtils().getArmorSlot(slot, false).equalsIgnoreCase("HELMET")))) {
				if (this.setupCommands(player, item, action, slot)) { return false; }
			}
		} catch (Exception e) { }
		return true;
	}
	
   /**
	* Runs the custom item commands.
	* 
	* @param player - that is running the commands.
	* @param item - the item the player is trying to use to execute a command.
	* @param action - the action that is being performed.
	* @param slot - the slot the item originally resided in.
	*/
	private boolean setupCommands(Player player, ItemStack item, String action, String slot) {
		ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item, null, player.getWorld());
		if (itemMap != null && itemMap.inWorld(player.getWorld()) && itemMap.hasPermission(player)) {
			return itemMap.executeCommands(player, item, action, (slot == null ? itemMap.getSlot() : slot));
		}
		return false;
	}
}