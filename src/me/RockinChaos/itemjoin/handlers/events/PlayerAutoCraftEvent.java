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

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
* Called when a player tries to autocraft using the recipe book.
* 
*/
public class PlayerAutoCraftEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	protected Inventory craftingInventory;
	private Result useAutoCraft;
	
   /**
	* Creates a new PlayerAutoCraftEvent instance.
	*
	* @param who - The Player triggering the event.
	* @param craftingInventory - The crafting inventory being interacted.
	*/
	public PlayerAutoCraftEvent(final Player who, final Inventory craftingInventory) {
		super(who);
		this.craftingInventory = craftingInventory;
		this.useAutoCraft = craftingInventory == null ? Result.DENY : Result.ALLOW;
	}
	
   /**
	* Gets the cancellation state of this event. Set to true if you want to
	* prevent the autocraft from shifting materials from the players inventory
	* to their crafting slots, materials will not be lost.
	*
	* @return boolean cancellation state.
	*/
	public boolean isCancelled() {
		return this.useAutoCraft() == Result.DENY;
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
		this.useAutoCraft(cancel ? Result.DENY : this.useAutoCraft() == Result.DENY ? Result.DEFAULT : this.useAutoCraft());
	}
	
   /**
	* Returns the crafting inventory represented by this event.
	*
	* @return Crafting inventory of the autocraft pattern.
	*/
	public Inventory getCrafting() {
		return this.craftingInventory;
	}
	
   /**
	* Convenience method. Returns the contents of the crafting inventory represented by
	* this event.
	*
	* @return Contents the crafting inventory.
	*/
	public ItemStack[] getContents() {
		return this.craftingInventory.getContents();
	}
	
   /**
	* This controls the action to take with the crafting slots the player is trying to autocraft in
	* This includes both the crafting inventory and items (such as flint and steel or
	* records). When this is set to default, it will be allowed if no action
	* is taken on the crafting inventory.
	*
	* @return The action to take with the autocraft pattern.
	*/
	public Result useAutoCraft() {
		return this.useAutoCraft;
	}
	
   /**
    * Sets the autocraft feature to be enabled or disabled.
    * 
	* @param useAutoCraft the action to take with the autocraft pattern.
	*/
	public void useAutoCraft(Result useAutoCraft) {
		this.useAutoCraft = useAutoCraft;
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
}