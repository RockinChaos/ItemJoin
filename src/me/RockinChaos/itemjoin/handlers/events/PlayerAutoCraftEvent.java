package me.RockinChaos.itemjoin.handlers.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
/**
 * Called when a player tries to autocraft using the recipe book.
 * Event is limited to Minecraft 1.12+
 */
public class PlayerAutoCraftEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	protected Inventory craftingInventory;
	private Result useAutoCraft;
	
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
	 * @return boolean cancellation state
	 */
	public boolean isCancelled() {
		return useAutoCraft() == Result.DENY;
	}
	
	/**
	 * Sets the cancellation state of this event. A canceled event will not be
	 * executed in the server, but will still pass to other plugins
	 * <p>
	 * Canceling this event will prevent use of the autocraft feature (clicking an
	 * item to autocraft will result in nothing happening, materials will not be lost.)
	 *
	 * @param cancel true if you wish to cancel this event
	 */
	public void setCancelled(boolean cancel) {
		useAutoCraft(cancel ? Result.DENY : useAutoCraft() == Result.DENY ? Result.DEFAULT : useAutoCraft());
	}
	
	/**
	 * Returns the crafting inventory represented by this event
	 *
	 * @return Crafting inventory of the autocraft pattern
	 */
	public Inventory getCrafting() {
		return this.craftingInventory;
	}
	
	/**
	 * Convenience method. Returns the contents of the crafting inventory represented by
	 * this event
	 *
	 * @return Contents the inventory of the crafting inventory
	 */
	public ItemStack[] getContents() {
		return craftingInventory.getContents();
	}
	
	/**
	 * This controls the action to take with the crafting slots the player is trying to autocraft in
	 * This includes both the crafting inventory and items (such as flint and steel or
	 * records). When this is set to default, it will be allowed if no action
	 * is taken on the crafting inventory
	 *
	 * @return the action to take with the autocraft pattern
	 */
	public Result useAutoCraft() {
		return useAutoCraft;
	}
	
	/**
	 * @param useAutoCraft the action to take with the autocraft pattern
	 */
	public void useAutoCraft(Result useAutoCraft) {
		this.useAutoCraft = useAutoCraft;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}