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
package me.RockinChaos.itemjoin.handlers.events;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.PlayerHandler;

/**
* Called when a player tries to pick block using the middle mouse button.
* 
*/
public class PlayerPickItemEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean clonedInventory = false;
	protected Inventory inventory;
	private Result usePickItem;
	
   /**
	* Creates a new PlayerPickItemEvent instance.
	*
	* @param who - The Player triggering the event.
	* @param inventory - The inventory being interacted.
	*/
	public PlayerPickItemEvent(final Player who, final Inventory inventory) {
		super(who);
		this.inventory = inventory;
		this.usePickItem = inventory == null ? Result.DENY : Result.ALLOW;
	}
	
   /**
	* Gets the cancellation state of this event. Set to true if you want to
	* prevent the pick block action from shifting materials in the players inventory, materials will not be lost.
	*
	* @return boolean cancellation state.
	*/
	public boolean isCancelled() {
		return this.usePickItem() == Result.DENY;
	}
	
   /**
	* Sets the cancellation state of this event. A canceled event will not be
	* executed in the server, but will still pass to other plugins.
	* <p>
	* Canceling this event will prevent use of the autocraft feature (clicking an
	* item to autocraft will result in nothing happening, materials will not be lost.)
	*
	* @param cancel true if you wish to cancel this event.
	*/
	public void setCancelled(boolean cancel) {
		this.usePickItem(cancel ? Result.DENY : this.usePickItem() == Result.DENY ? Result.DEFAULT : this.usePickItem());
	}
	
   /**
	* Attempts to returns the slot of the picked block.
	* This will return -1 if no slot was found.
	* 
	* This was designed to be compared against a "delayed" inventory,
	* after the event has already occured.
	*
	* @return The Picked Block Slot.
	*/
	public int getPickSlot() {
		final Inventory inventory = super.getPlayer().getInventory();
		final Block targetBlock = this.getTargetBlock();
		for (int i = 8; i < 36; i++) {
			if (this.inventory.getContents()[i] != null && this.inventory.getContents()[i].getType() != Material.AIR && this.inventory.getContents()[i].getType().equals(targetBlock.getType())
					&& ((inventory.getContents()[i] != null && inventory.getContents()[i].getType() != Material.AIR && !inventory.getContents()[i].getType().equals(targetBlock.getType())) 
					 || (inventory.getContents()[i] == null || inventory.getContents()[i].getType() == Material.AIR))) {
				return i;
			}
		}
		return -1;
	}
	
   /**
	* Returns the Block being targeted by the Player.
	* The Block Material is the material of the item being swapped to the players hand.
	*
	* @return The Block being targeted.
	*/
	public Block getTargetBlock() {
		this.cloneInventory();
		Block targetBlock = null;
		try {
			targetBlock = super.getPlayer().getTargetBlock((Set<Material>) null, 200);
		} catch (Exception e) { }
		return targetBlock;
	}
	
   /**
	* Returns the ItemStack that is being moved from the Players hand prior to swapping.
	*
	* @return The ItemStack that is being swapped.
	*/
	public ItemStack getPickHand() {
		this.cloneInventory();
		return PlayerHandler.getPlayer().getHandItem(super.getPlayer());
	}
	
   /**
	* Convenience method. Returns the contents of the inventory represented by this event.
	*
	* @return Contents the inventory.
	*/
	public ItemStack[] getContents() {
		this.cloneInventory();
		return this.inventory.getContents();
	}
	
   /**
	* This controls the action to take with the pick block action.
	* When this is set to default, it will be allowed if no action
	* is taken on the pick block action.
	*
	* @return The action to take with the pick block action.
	*/
	public Result usePickItem() {
		return this.usePickItem;
	}
	
   /**
    * Sets the pick block action to be enabled or disabled.
    * 
	* @param usePickItem the action to take with the pick block action.
	*/
	public void usePickItem(Result usePickItem) {
		this.usePickItem = usePickItem;
	}
	
   /**
    * Gets the Handlers for the event.
    * 
	* @return The HandlerList for the event.
	*/
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
   /**
    * Gets the HandlerList for the event.
    * 
	* @return The HandlerList for the event.
	*/
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
   /**
    * Attempts to clone the specified Inventory
    * The cloned Inventory will become the active event Inventory.
    * 
	*/
	public void cloneInventory() {
		if (!this.clonedInventory) {
			Inventory cloneInventory = Bukkit.createInventory(super.getPlayer(), 36);
			for (int i = 0; i < 36; i++) {
				if (this.inventory.getContents()[i] != null && this.inventory.getContents()[i].getType() != Material.AIR) {
					cloneInventory.setItem(i, this.inventory.getContents()[i]);
				}
			}
			this.inventory = cloneInventory;
			this.clonedInventory = true;
		}
	}
}