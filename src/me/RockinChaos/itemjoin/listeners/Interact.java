package me.RockinChaos.itemjoin.listeners;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class Interact implements Listener {
	
	 @EventHandler(priority = EventPriority.LOWEST)
	 private void onInteractCancel(PlayerInteractEvent event) {
	 	ItemStack item = event.getItem();
	 	Player player = event.getPlayer();
	 	if (event.hasItem() && event.getAction() != Action.PHYSICAL && !ItemUtilities.isAllowed(player, item, "cancel-events")
	 			|| event.getAction() != Action.PHYSICAL && ServerHandler.hasSpecificUpdate("1_9") && event.getHand() != null 
	 			&& event.getHand().toString().equalsIgnoreCase("OFF_HAND") && !ItemUtilities.isAllowed(player, PlayerHandler.getMainHandItem(event.getPlayer()), "cancel-events")) {
	 		event.setCancelled(true);
	 		PlayerHandler.updateInventory(player);
	 	}
	 }

	 @EventHandler
	 private void onInteractCooldown(PlayerInteractEvent event) {
	 	Player player = event.getPlayer();
	 	ItemStack item = event.getItem();
	 	if (event.hasItem() && event.getAction() != Action.PHYSICAL) {
	 		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	 			ItemMap itemMap = ItemUtilities.getItemMap(item, null, player.getWorld());
	 			if (itemMap != null && itemMap.getInteractCooldown() != 0) {
	 				if (itemMap.onInteractCooldown(player)) {
	 					event.setCancelled(true);
	 				}
	 			}
	 		}
	 	}
	 }

	@EventHandler
	private void onInventoryCommands(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		String action = event.getAction().toString();
		String slot = String.valueOf(event.getSlot());
		if (event.getSlotType().name().equalsIgnoreCase("CRAFTING")) { slot = "CRAFTING[" + slot + "]"; }
		if (this.setupCommands(player, item, action, slot)) { event.setCancelled(true); }
	}

	@EventHandler
	public void onHoldCommand(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(event.getNewSlot());
		String slot = String.valueOf(event.getNewSlot());
		if (this.setupCommands(player, item, "ON_HOLD", slot)) { event.setCancelled(true); }
	}
	
	@EventHandler
	public void onEquipCommand(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (Utils.containsIgnoreCase(event.getAction().name(), "HOTBAR") && event.getView().getBottomInventory().getItem(event.getHotbarButton()) != null && event.getView().getBottomInventory().getItem(event.getHotbarButton()).getType() != Material.AIR) {
			if (!this.equipSetup(player, event.getView().getBottomInventory().getItem(event.getHotbarButton()), "ON_EQUIP", String.valueOf(event.getSlot()), event.getSlotType())) { event.setCancelled(true); }
		}
		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			if (!this.equipSetup(player, event.getCurrentItem(), "UN_EQUIP", String.valueOf(event.getSlot()), event.getSlotType())) { event.setCancelled(true); }
		}
		if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) { 
			if (!this.equipSetup(player, event.getCursor(), "ON_EQUIP", String.valueOf(event.getSlot()), event.getSlotType())) { event.setCancelled(true); } 
		}
	}
	
	@EventHandler
	public void onEquipCommand(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		Set<Integer> slideSlots = event.getInventorySlots();
		int slot = 0; for (int actualSlot: slideSlots) { slot = actualSlot; break; }
		if (event.getOldCursor() != null && event.getOldCursor().getType() != Material.AIR) {
			if (!this.equipSetup(player, event.getOldCursor(), "ON_EQUIP", String.valueOf(slot), SlotType.ARMOR)) { event.setCancelled(true); }
		}
	}
	
	@EventHandler
	public void onEquipCommand(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (item != null && item.getType() != Material.AIR) {
			String[] itemType = item.getType().name().split("_");
			if (itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && Utils.isInt(Utils.getArmorSlot(itemType[1], true)) 
				&& player.getInventory().getItem(Integer.parseInt(Utils.getArmorSlot(itemType[1], true))) == null && !this.equipSetup(player, event.getItem(), "ON_EQUIP", Utils.getArmorSlot(itemType[1], true), SlotType.ARMOR)) { event.setCancelled(true); }
		}
	}
	
	public boolean equipSetup(Player player, ItemStack item, String action, String slot, SlotType slotType) {
		try {
			String[] itemType = item.getType().name().split("_");
			if (slotType == SlotType.ARMOR && itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && !itemType[1].equalsIgnoreCase("HEAD") && (itemType[1].equalsIgnoreCase(Utils.getArmorSlot(slot, false)) 
					|| (itemType[1].equalsIgnoreCase("HEAD") && Utils.getArmorSlot(slot, false).equalsIgnoreCase("HELMET")))) {
				if (this.setupCommands(player, item, action, slot)) { return false; }
			}
		} catch (Exception e) { }
		return true;
	}
	
	@EventHandler
	private void onEntityCommands(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof org.bukkit.entity.ItemFrame) {
			ItemStack item;
			if (ServerHandler.hasSpecificUpdate("1_9")) { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
			else { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), ""); }
			Player player = event.getPlayer();
			String action = Action.RIGHT_CLICK_BLOCK.name();
			ItemMap itemMap = ItemUtilities.getItemMap(PlayerHandler.getHandItem(player), null, player.getWorld());
			if (itemMap != null && itemMap.isSimilar(item)) {
				if (this.setupCommands(player, item, action, String.valueOf(player.getInventory().getHeldItemSlot()))) { event.setCancelled(true); }
			}
		}
	}
	
	@EventHandler
	private void onTargetEntityCommands(PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked().toString().equalsIgnoreCase("CraftArmorStand")) {
			ItemStack item;
			if (ServerHandler.hasSpecificUpdate("1_9")) { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().toString()); } 
			else { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), ""); }
			Player player = event.getPlayer();
			String action = Action.RIGHT_CLICK_BLOCK.name();
			ItemMap itemMap = ItemUtilities.getItemMap(PlayerHandler.getHandItem(player), null, player.getWorld());
			if (itemMap != null && itemMap.isSimilar(item)) {
				if (this.setupCommands(player, item, action, String.valueOf(player.getInventory().getHeldItemSlot()))) { event.setCancelled(true); }
			}
		}
	}

	@EventHandler
	private void onInteractCommands(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		final Player player = event.getPlayer();
		String action = event.getAction().toString();
		if (PlayerHandler.isAdventureMode(player) && !action.contains("LEFT") 
				|| !PlayerHandler.isAdventureMode(player)) {
			ItemMap itemMap = ItemUtilities.getItemMap(PlayerHandler.getHandItem(player), null, player.getWorld());
			if (itemMap != null && itemMap.isSimilar(item)) {
				if (this.setupCommands(player, item, action, String.valueOf(player.getInventory().getHeldItemSlot()))) { event.setCancelled(true); }
			}
		}
	}
	
	@EventHandler
	public void onSwingHandCommands(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		ItemStack item = PlayerHandler.getHandItem(player);
		if (PlayerHandler.isAdventureMode(player)) {
			if (this.setupCommands(player, item, "LEFT_CLICK_AIR", String.valueOf(player.getInventory().getHeldItemSlot()))) { event.setCancelled(true); }
		}
	}
	
	private boolean setupCommands(Player player, ItemStack item, String action, String slot) {
		ItemMap itemMap = ItemUtilities.getItemMap(item, null, player.getWorld());
		if (itemMap != null && itemMap.inWorld(player.getWorld()) && itemMap.hasPermission(player)) {
			return itemMap.executeCommands(player, item, action, slot);
		}
		return false;
	}
}