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
package me.RockinChaos.itemjoin.utils.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

public class Page {
	
	private List < Button > buttons = new ArrayList < > ();
	private int maxSize;
	
   /**
    * Creates a new page instance.
    * 
    * @param maxSize - The size of the inventory page.
    */
	public Page(int maxSize) {
		this.maxSize = maxSize;
	}
	
   /**
    * Called on player inventory click.
    * Handles the click event for the inventory page.
    * 
    * @param event - InventoryClickEvent
    */
	public void handleClick(InventoryClickEvent event) {
		if (event.getRawSlot() > event.getInventory().getSize()) {
			return;
		}
		if (event.getSlot() >= this.buttons.size()) {
			return;
		}
		Button button = this.buttons.get(event.getSlot());
		button.onClick(event);
	}
	
   /**
    * Called on player chat.
    * Handles the chat event for the inventory page.
    * 
    * @param event - AsyncPlayerChatEvent
    * @param slot - The slot that relates to the button that was clicked for the page.
    */
	public void handleChat(AsyncPlayerChatEvent event, int slot) {
		Button button = this.buttons.get(slot);
		button.onChat(event);
	}
	
   /**
    * Adds a new button to the page.
    * 
    * @param button - The button to be added.
    * @return The button was successfully added.
    */
	public boolean addButton(Button button) {
		if (!this.hasSpace()) {
			return false;
		}
		this.buttons.add(button);
		return true;
	}
	
   /**
    * Removes the button from the page.
    * 
    * @param button - The button to be removed.
    * @return The button was successfully removed.
    */
	public boolean removeButton(Button button) {
		return this.buttons.remove(button);
	}
	
   /**
    * Renders the page to the specified inventory.
    * 
    * @param inventory - The inventory to have the page rendered.
    */
	public void render(Inventory inventory) {
		for (int i = 0; i < this.buttons.size(); i++) {
			Button button = this.buttons.get(i);
			inventory.setItem(i, button.getItemStack());
		}
	}
	
   /**
    * Checks if the page has any empty slots.
    * 
    * @return If the page has space to add another button.
    */
	private boolean hasSpace() {
		return this.buttons.size() < (this.maxSize * 9);
	}
	
   /**
    * Checks if there are any buttons defined for the page.
    * 
    * @return There are no buttons defined for the page.
    */
	public boolean isEmpty() {
		return this.buttons.isEmpty();
	}
	
   /**
    * Executes the chat event method for the specified slots button.
    * 
    * @param slot - The slot of the clicked button.
    * @return The chat event was successfully passed through to the button.
    */
	public boolean chatEvent(int slot) {
		if (slot <= this.buttons.size()) {
			return this.buttons.get(slot).chatEvent();
		}
		return false;
	}
}