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

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;

public class Commands implements Listener {

   /**
	* Runs the inventory commands for the custom item upon clicking it.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onInventory(InventoryClickEvent event) {
		final ItemStack item = event.getCurrentItem();
		final Player player = (Player) event.getWhoClicked();
		final String action = event.getAction().name();
		final String slot = (event.getSlotType().name().equalsIgnoreCase("CRAFTING") ? "CRAFTING[" + String.valueOf(event.getSlot()) + "]" : String.valueOf(event.getSlot()));
		this.runCommands(player, null, item, action, event.getClick().name(), slot);
	}
	
   /**
	* Runs the on_death commands for the custom item upon player death.
	* 
	* @param event - PlayerDeathEvent.
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onDeath(PlayerDeathEvent event) {
		final ListIterator < ItemStack > litr = event.getDrops().listIterator();
		while (litr.hasNext()) {
			final ItemStack item = litr.next();
			this.runCommands(event.getEntity(), null, item, "ON_DEATH", "DEAD", null);
		}
	}
	
   /**
	* Runs the on_consume commands for the custom item upon item consumption.
	* 
	* @param event - PlayerItemConsumeEvent.
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onConsume(PlayerItemConsumeEvent event) {
		final ItemStack item = event.getItem();
		this.runCommands(event.getPlayer(), null, item, "ON_CONSUME", "CONSUME", String.valueOf(event.getPlayer().getInventory().getHeldItemSlot()));
	}
	
   /**
	* Runs the on_fire commands for the custom item upon the player shooting a bow.
	* 
	* @param event - EntityShootBowEvent.
	*/
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
	private void onFire(EntityShootBowEvent event) {
		if (event.getEntity() instanceof Player) {
			final ItemStack bow = (event.getBow() != null ? event.getBow().clone() : event.getBow());
			this.runCommands((Player)event.getEntity(), null, bow, "ON_FIRE", "FIRE", String.valueOf(((Player)event.getEntity()).getInventory().getHeldItemSlot()));
			if (ServerUtils.hasSpecificUpdate("1_16")) {
				final ItemStack arrow = (event.getConsumable() != null ? event.getConsumable().clone() : event.getConsumable());
				this.runCommands((Player)event.getEntity(), null, arrow, "ON_FIRE", "FIRE", null);
			}
		}
	}

   /**
	* Runs the on_hold commands for the custom item upon holding it.
	* 
	* @param event - PlayerItemHeldEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onHold(PlayerItemHeldEvent event) {
		final Player player = event.getPlayer();
		final ItemStack item = player.getInventory().getItem(event.getNewSlot());
		final String slot = String.valueOf(event.getNewSlot());
		this.runCommands(player, null, item, "ON_HOLD", "HELD", slot);
	}
	
   /**
	* Runs the on_equip and un_equip commands for the custom item upon clicking and moving it to the armor slots.
	* 
	* @param event - InventoryClickEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEquipClick(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final InventoryView view = event.getView();
		if (PlayerHandler.isCraftingInv(view)) {
			if (StringUtils.containsIgnoreCase(event.getAction().name(), "HOTBAR") && view.getBottomInventory().getSize() >= event.getHotbarButton() && event.getHotbarButton() >= 0
			 && !event.getClick().name().equalsIgnoreCase("MIDDLE") && event.getSlotType() == SlotType.ARMOR && view.getBottomInventory().getItem(event.getHotbarButton()) != null && view.getBottomInventory().getItem(event.getHotbarButton()).getType() != Material.AIR) {
				this.equipCommands(player, null, view.getBottomInventory().getItem(event.getHotbarButton()), "ON_EQUIP", "EQUIPPED", String.valueOf(event.getSlot()), event.getSlotType());
			}
			if (!event.getClick().name().equalsIgnoreCase("MIDDLE") && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
				if (event.getSlotType() == SlotType.ARMOR) { 
					this.equipCommands(player, null, event.getCurrentItem(), "UN_EQUIP", "UNEQUIPPED", String.valueOf(event.getSlot()), event.getSlotType());
				} else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				final String[] itemType = (event.getCurrentItem().getType().name().equalsIgnoreCase("ELYTRA") ? "ELYTRA_CHESTPLATE".split("_") : 
					(ItemHandler.isSkull(event.getCurrentItem().getType()) || StringUtils.splitIgnoreCase(event.getCurrentItem().getType().name(), "HEAD", "_") ? "SKULL_HELMET".split("_") : event.getCurrentItem().getType().name().split("_")));
					if (itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && StringUtils.isInt(StringUtils.getArmorSlot(itemType[1], true)) 
						&& player.getInventory().getItem(Integer.parseInt(StringUtils.getArmorSlot(itemType[1], true))) == null) { 
						this.equipCommands(player, null, event.getCurrentItem(), "ON_EQUIP", "SHIFT_EQUIPPED", String.valueOf(event.getSlot()), event.getSlotType());
					}
				}
			}
			if (!event.getClick().name().equalsIgnoreCase("MIDDLE") && !event.getClick().name().contains("SHIFT") && event.getSlotType() == SlotType.ARMOR && event.getCursor() != null && event.getCursor().getType() != Material.AIR) { 
				this.equipCommands(player, null, event.getCursor(), "ON_EQUIP", "EQUIPPED", String.valueOf(event.getSlot()), event.getSlotType());
			}
		}
	}
	
   /**
	* Runs the on_equip and un_equip commands for the custom item upon clicking and dragging it to the armor slots.
	* 
	* @param event - InventoryDragEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEquipDrag(InventoryDragEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final Set<Integer> slideSlots = event.getInventorySlots();
		int slot = 0; for (int actualSlot: slideSlots) { slot = actualSlot; break; }
		if (event.getOldCursor() != null && event.getOldCursor().getType() != Material.AIR && PlayerHandler.isCraftingInv(event.getView())) {
			this.equipCommands(player, null, event.getOldCursor(), "ON_EQUIP", "EQUIPPED", String.valueOf(slot), SlotType.ARMOR);
		}
	}
	
   /**
	* Runs the on_equip and un_equip commands for the custom item upon right clicking it from their hand to the armor slots.
	* 
	* @param event - PlayerInteractEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEquip(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final Action action = event.getAction();
		final ItemStack item = (event.getItem() != null ? event.getItem().clone() : event.getItem());
		if (item != null && item.getType() != Material.AIR && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)&& PlayerHandler.isCraftingInv(player.getOpenInventory()) && !PlayerHandler.isMenuClick(player.getOpenInventory(), event.getAction())) {
			final String[] itemType = (item.getType().name().equalsIgnoreCase("ELYTRA") ? "ELYTRA_CHESTPLATE".split("_") : 
				(ItemHandler.isSkull(item.getType()) || StringUtils.splitIgnoreCase(item.getType().name(), "HEAD", "_") ? "SKULL_HELMET".split("_") : item.getType().name().split("_")));
			if (itemType.length >= 2 && itemType[1] != null && !itemType[1].isEmpty() && StringUtils.isInt(StringUtils.getArmorSlot(itemType[1], true)) 
				&& player.getInventory().getItem(Integer.parseInt(StringUtils.getArmorSlot(itemType[1], true))) == null 
				&& ((itemType[0].equalsIgnoreCase("SKULL") && !event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) || (!itemType[0].equalsIgnoreCase("SKULL")))) {
				this.equipCommands(player, null, item, "ON_EQUIP", "EQUIPPED", StringUtils.getArmorSlot(itemType[1], true), SlotType.ARMOR);
			}
		}
	}
	
   /**
	* Runs the on_damage commands for the custom item upon damaging an entity or being damaged by an entity.
	* 
	* @param event - EntityDamageByEntityEvent
	*/
	@EventHandler(ignoreCancelled = false)
    public void onDamage(EntityDamageByEntityEvent event){
		final Player player = ((event.getEntity() instanceof Player) ? (Player)event.getEntity() : (event.getDamager() instanceof Player) ? (Player)event.getDamager() : null);
		if (player != null) {
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				this.handleOnDamage(player, (event.getEntity() instanceof Player ? (Player)event.getEntity() : null), String.valueOf(i));
			}
			if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
				for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
					this.handleOnDamage(player, (event.getEntity() instanceof Player ? (Player)event.getEntity() : null), "CR" + i);
				}
			}
		}
    }
	
   /**
	* Runs the on_hit commands for the custom item upon damaging an entity.
	* 
	* @param event - EntityDamageByEntityEvent
	*/
	@EventHandler(ignoreCancelled = false)
    public void onHit(EntityDamageByEntityEvent event) {
		final Player player = ((event.getDamager() instanceof Player) ? (Player)event.getDamager() : null);
		if (player != null) {
			final ItemStack item = PlayerHandler.getHandItem(player);
			final int slot = player.getInventory().getHeldItemSlot();
			if (item != null && item.getType() != Material.AIR && !PlayerHandler.isMenuClick(player.getOpenInventory(), Action.LEFT_CLICK_AIR)) {
				this.runCommands(player, (event.getEntity() instanceof Player ? (Player)event.getEntity() : null), item, "ON_HIT", "HIT", Integer.toString(slot));
			}
		}
    }
	
   /**
	* Saves projectile ItemStacks as their id reference to be used in the ProjectileHitEvent.
	* 
	* @param event - ProjectileLaunchEvent
	*/
	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunch(final ProjectileLaunchEvent event) {
	    final Projectile projectile = event.getEntity();
	    final Player player = (projectile.getShooter() instanceof Player ? (Player)projectile.getShooter() : null);
	    if (player != null) {
	    	ItemStack item = PlayerHandler.getMainHandItem(player);
	    	if (item != null) {
	    		item = item.clone();
	    		if (item.getAmount() == 0) { item.setAmount(1); }
	    		this.projectileList.put(projectile.getEntityId(), item.clone());
	    	}
	    }
	}
	private HashMap<Integer, ItemStack> projectileList = new HashMap<Integer, ItemStack>();
	
   /**
	* Runs the on_hit commands for the custom item upon a projectile hitting an enemy.
	* 
	* @param event - ProjectileHitEvent
	*/
	@EventHandler(ignoreCancelled = false)
    public void onProjectileHit(ProjectileHitEvent event){
		final Player player = ((event.getEntity().getShooter() instanceof Player) ? (Player)event.getEntity().getShooter() : null);
		Entity hitEntity = (ServerUtils.hasSpecificUpdate("1_12") ? event.getHitEntity() : null);
		if (!ServerUtils.hasSpecificUpdate("1_12")) { for (Entity entity : event.getEntity().getNearbyEntities(2, 2, 2)) { if (entity instanceof Player) { hitEntity = entity; } break; } }
		final Player altPlayer = ((hitEntity instanceof Player) ? (Player)hitEntity : null);
		final ItemStack item = (this.projectileList != null && this.projectileList.containsKey(event.getEntity().getEntityId()) ? this.projectileList.get(event.getEntity().getEntityId()).clone() : null);
		if (this.projectileList != null && this.projectileList.containsKey(event.getEntity().getEntityId())) { this.projectileList.remove(event.getEntity().getEntityId()); }
		if (player != null && hitEntity != null) {
			final int slot = player.getInventory().getHeldItemSlot();
			if (item != null && item.getType() != Material.AIR && !PlayerHandler.isMenuClick(player.getOpenInventory(), Action.LEFT_CLICK_AIR)) {
				this.runCommands(player, altPlayer, item, "ON_HIT", "HIT", Integer.toString(slot));
			}
		}
    }
	
   /**
	* Runs the commands upon right or left clicking the custom item on an entity.
	* 
	* @param event - PlayerInteractEntityEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof org.bukkit.entity.ItemFrame) {
			ItemStack item;
			if (ServerUtils.hasSpecificUpdate("1_9")) { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().name()); } 
			else { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), ""); }
			final Player player = event.getPlayer();
			final String action = Action.RIGHT_CLICK_BLOCK.name();
			final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(PlayerHandler.getHandItem(player));
			if (itemMap != null && itemMap.isSimilar(player, item)) {
				this.runCommands(player, (event.getRightClicked() instanceof Player ? (Player)event.getRightClicked() : null), item, action, action.split("_")[0], String.valueOf(player.getInventory().getHeldItemSlot()));
			}
		}
	}
	
   /**
	* Runs the commands upon right or left clicking the custom item towards an entity.
	* 
	* @param event - PlayerInteractAtEntityEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onTargetEntity(PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked().toString().equalsIgnoreCase("CraftArmorStand")) {
			ItemStack item;
			if (ServerUtils.hasSpecificUpdate("1_9")) { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), event.getHand().name()); } 
			else { item = PlayerHandler.getPerfectHandItem(event.getPlayer(), ""); }
			final Player player = event.getPlayer();
			final String action = Action.RIGHT_CLICK_BLOCK.name();
			final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(PlayerHandler.getHandItem(player));
			if (itemMap != null && itemMap.isSimilar(player, item)) {
				this.runCommands(player, (event.getRightClicked() instanceof Player ? (Player)event.getRightClicked() : null), item, action, action.split("_")[0], String.valueOf(player.getInventory().getHeldItemSlot()));
			}
		}
	}
	
   /**
	* Runs the commands upon physically interacting with the custom item.
	* 
	* @param event - PlayerInteractEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final ItemStack item = (event.getItem() != null ? event.getItem().clone() : (event.getAction() == Action.PHYSICAL ? PlayerHandler.getMainHandItem(player) : event.getItem()));
		final String action = event.getAction().name();
		if (((PlayerHandler.isAdventureMode(player) && !action.contains("LEFT") || !PlayerHandler.isAdventureMode(player))) && !this.isDropEvent(event.getPlayer())) {
			final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(PlayerHandler.getHandItem(player));
			if (!PlayerHandler.isMenuClick(player.getOpenInventory(), event.getAction()) && itemMap != null && itemMap.isSimilar(player, item)) {
				long dupeDuration = (this.interactDupe != null && !this.interactDupe.isEmpty() && this.interactDupe.get(item) != null ? (((System.currentTimeMillis()) - this.interactDupe.get(item))) : -1);
				if (dupeDuration == -1 || dupeDuration > 30) {
					this.interactDupe.put(item, System.currentTimeMillis());
					this.runCommands(player, null, item, action, (event.getAction() == Action.PHYSICAL ? "INTERACTED" : action.split("_")[0]), String.valueOf(player.getInventory().getHeldItemSlot()));
				}
			}
		}
	}
	private HashMap<ItemStack, Long> interactDupe = new HashMap<ItemStack, Long>();
	
   /**
	* Runs the commands upon left clicking with the custom item in adventure mode.
	* 
	* @param event - PlayerAnimationEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onSwingArm(PlayerAnimationEvent event) {
		final Player player = event.getPlayer();
		final ItemStack item = PlayerHandler.getHandItem(player);
		if (PlayerHandler.isAdventureMode(player) && !this.isDropEvent(event.getPlayer()) && (!PlayerHandler.isMenuClick(player.getOpenInventory(), Action.LEFT_CLICK_AIR) || !PlayerHandler.isMenuClick(player.getOpenInventory(), Action.LEFT_CLICK_BLOCK))) {
			this.runCommands(player, null, item, "LEFT_CLICK_AIR", "LEFT", String.valueOf(player.getInventory().getHeldItemSlot()));
		}
	}
	
   /**
	* Places the player dropping an item into a temporary hashmap to be noted as,
	* having recently dropped an item. This is to prevent command execution when dropping an item using the item drop keybind.
	* 
	* @param event - PlayerDropItemEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onHandDrop(PlayerDropItemEvent event) {
		if (!this.isDropEvent(event.getPlayer())) {
			this.itemDrop.put(PlayerHandler.getPlayerID(event.getPlayer()), true);
			SchedulerUtils.runLater(1L, () -> {
				if (this.isDropEvent(event.getPlayer())) {
					this.itemDrop.remove(PlayerHandler.getPlayerID(event.getPlayer()));
				}
			});
		}
	}
	
   /**
	* Runs the on damage commands.
	* 
	* @param player - The player being damaged or damaging an entity.
	* @param altPlayer - that is associated with the commands.
	* @param slot - The slot being checked.
	*/
	public void handleOnDamage(final Player player, final Player altPlayer, final String slot) {
		final ItemStack item = (slot.startsWith("CR") ? player.getOpenInventory().getTopInventory().getItem(Integer.valueOf(slot.replace("CR", ""))) : player.getInventory().getItem(Integer.valueOf(slot)));
		if (item != null && item.getType() != Material.AIR && !PlayerHandler.isMenuClick(player.getOpenInventory(), Action.LEFT_CLICK_AIR)) {
			this.runCommands(player, altPlayer, item, "ON_DAMAGE", "DAMAGED", slot.replace("CR", ""));
		}
	}
	
   /**
	* Runs the on_equip and un_equip custom item commands.
    * 
	* @param player - that is running the commands.
	* @param altPlayer - that is associated with the commands.
	* @param item - the item the player is trying to use to execute a command.
	* @param action - the action that is being performed.
	* @param clickType - the clicking type that is being performed.
	* @param slot - the slot the item originally resided in.
	* @param slotType - the SlotType the item originated in.
	*/
	private void equipCommands(final Player player, final Player altPlayer, final ItemStack item, final String action, String clickType, final String slot, final SlotType slotType) {
			final String[] itemType = (item.getType().name().equalsIgnoreCase("ELYTRA") ? "ELYTRA_CHESTPLATE".split("_") : 
				(ItemHandler.isSkull(item.getType()) || StringUtils.splitIgnoreCase(item.getType().name(), "HEAD", "_") ? "SKULL_HELMET".split("_") : item.getType().name().split("_")));
			if (itemType.length >= 2 && (itemType[1] != null && !itemType[1].isEmpty()
					&& (clickType.equalsIgnoreCase("SHIFT_EQUIPPED") || itemType[1].equalsIgnoreCase(StringUtils.getArmorSlot(slot, false))))) {
				clickType = (clickType.equalsIgnoreCase("SHIFT_EQUIPPED") ? "EQUIPPED" : clickType);
				this.runCommands(player, altPlayer, item, action, clickType, slot);
			}
	}
	
   /**
	* Runs the custom item commands.
	* 
	* @param player - that is running the commands.
	* @param altPlayer - that is associated with the commands.
	* @param item - the item the player is trying to use to execute a command.
	* @param action - the action that is being performed.
	* @param slot - the slot the item originally resided in.
	*/
	private void runCommands(final Player player, final Player altPlayer, final ItemStack item, final String action, final String clickType, final String slot) {
		final ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item);
		if (itemMap != null && itemMap.inWorld(player.getWorld()) && itemMap.hasPermission(player, player.getWorld())) {
			itemMap.executeCommands(player, altPlayer, item, action, clickType, (slot == null ? itemMap.getSlot() : slot));
		}
	}
	
   /**
	* Checks if the player recently attempted to drop an item.
	* 
	* @param player - The player being checked.
	* @return If the player has dropped the item.
	*/
	private boolean isDropEvent(final Player player) {
		if (!((this.itemDrop.get(PlayerHandler.getPlayerID(player)) == null 
			 || (this.itemDrop.get(PlayerHandler.getPlayerID(player)) != null 
			 && !this.itemDrop.get(PlayerHandler.getPlayerID(player)))))) {
			return true;
		}
		return false;
	}
	private HashMap<String, Boolean> itemDrop = new HashMap<String, Boolean>();
}