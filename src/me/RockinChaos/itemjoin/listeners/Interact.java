package me.RockinChaos.itemjoin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;

public class Interact implements Listener {

	/**
	 * Cancels any event that is triggered when interacting with the custom item.
	 * 
	 * @param event - PlayerInteractEvent
	 */
	 @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	 private void onInteractCancel(PlayerInteractEvent event) {
	 	ItemStack item = event.getItem();
	 	Player player = event.getPlayer();
	 	if (!PlayerHandler.getPlayer().isMenuClick(player.getOpenInventory(), event.getAction()) && (event.hasItem() && event.getAction() != Action.PHYSICAL && !ItemUtilities.getUtilities().isAllowed(player, item, "cancel-events")
	 			|| event.getAction() != Action.PHYSICAL && ServerHandler.getServer().hasSpecificUpdate("1_9") && event.getHand() != null 
	 			&& event.getHand().toString().equalsIgnoreCase("OFF_HAND") && !ItemUtilities.getUtilities().isAllowed(player, PlayerHandler.getPlayer().getMainHandItem(event.getPlayer()), "cancel-events"))) {
	 		if (ItemHandler.getItem().isBookQuill(item) || ItemHandler.getItem().isBookQuill(PlayerHandler.getPlayer().getMainHandItem(event.getPlayer()))) { player.closeInventory(); } 
	 		event.setCancelled(true);
	 		PlayerHandler.getPlayer().updateInventory(player, 1L);
	 	}
	 }
	 
	/**
	 * Sets the custom item on cooldown upon interaction.
	 * 
	 * @param event - PlayerInteractEvent
	 */
	 @EventHandler(ignoreCancelled = false)
	 private void onInteractCooldown(PlayerInteractEvent event) {
	 	Player player = event.getPlayer();
	 	ItemStack item = event.getItem();
	 	if ((event.hasItem() && event.getAction() != Action.PHYSICAL) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
	 		ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(item, null, player.getWorld());
	 		if (itemMap != null && itemMap.getInteractCooldown() != 0 && itemMap.onInteractCooldown(player)) {
	 			event.setCancelled(true);
	 		}
	 	}
	 }
	 
   /**
	* Prevents the player from selecting custom items with the selectable itemflag upon holding it.
	* 
	* @param event - PlayerItemHeldEvent
	*/
	@EventHandler(ignoreCancelled = false)
	private void onSelectItem(PlayerItemHeldEvent event) {
		final Player player = event.getPlayer();
		final ItemStack item = player.getInventory().getItem(event.getNewSlot());
		final int newSlot = event.getNewSlot();
		final int oldSlot = event.getPreviousSlot();
		if (!ItemUtilities.getUtilities().isAllowed(player, item, "selectable")) {
			ServerHandler.getServer().runThread(main -> {
				if (PlayerHandler.getPlayer().getMainHandItem(player).equals(item)) {
					if (!this.setSelectSlot(player, newSlot, (newSlot > oldSlot))) {
						this.setSelectSlot(player, newSlot, !(newSlot > oldSlot));
					}
				}
			}, 10L);
		}
	}
	
   /**
	* Sets the players hotbar slot towards the direction they were moving from prior.
	* 
	* @param player - The player changing their selected slots.
	* @param slot - The currently selected slot.
	* @param forward - If they are moving to right of the inventory.
	*/
	private boolean setSelectSlot(final Player player, final int slot, final boolean forward) {
		for (int i = slot; (forward ? i < 9 : i >= 0);) {
			if (ItemUtilities.getUtilities().isAllowed(player, player.getInventory().getItem(i), "selectable")) {
				PlayerHandler.getPlayer().setHotbarSlot(player, i); 
				return true;
			} else if (forward) { i++; } else { i--; }
		}
		return false;
	}
}