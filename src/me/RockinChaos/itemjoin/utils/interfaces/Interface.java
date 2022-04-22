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

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.interfaces.menus.Menu;

public class Interface implements InventoryHolder {
	
	private Inventory inventory;
	private int activeButton = -1;
	private int currentIndex;
	private int pageSize;
	private Player panePlayer;
	private SortedMap < Integer, Page > pages = new TreeMap < > ();
	
	private boolean isPaged;
	private boolean pendingChat = false;
	private boolean pendingClick = false;
	
	private Button controlBack;
	private Button controlNext;
	private Button controlExit;
	
   /**
    * Creates a new interface instance.
    * 
    * @param isPaged - If the inventory has multiple pages.
    * @param rows - Number of rows for the inventory.
    * @param title - Title to be displayed on the inventory.
    */
	public Interface(boolean isPaged, int rows, String title, Player player) {
		this.panePlayer = player;
		this.isPaged = isPaged;
		if (this.isPaged) {
			this.pageSize = rows - 1;
		} else {
			this.pageSize = rows * 9;
		}
		this.inventory = Bukkit.createInventory(this, rows * 9, StringUtils.colorFormat(title));
		this.inventory.setMaxStackSize(128);
		this.pages.put(0, new Page(this.pageSize));
		this.createControls(this.inventory);
	}
	
   /**
    * Called on player inventory click.
    * Handles the player click event for the button method execution.
    * 
    * @param event - InventoryClickEvent
    */
	public void onClick(InventoryClickEvent event) {
		if (this.panePlayer.equals(event.getWhoClicked()) && !(this.pendingClick && event.getSlot() <= event.getWhoClicked().getInventory().getSize() && event.getSlot() >= 0 && this.clickInventory(event))) {
			try {
				if (this.isPaged && event.getSlot() == this.inventory.getSize() - 8 && this.getCurrentPage() > 1) {
					if (this.controlBack != null) {
						this.controlBack.onClick(event);
					}
				} else if (this.isPaged && event.getSlot() == this.inventory.getSize() - 2 && this.getCurrentPage() < this.getPageAmount()) {
					if (this.controlNext != null) {
						this.controlNext.onClick(event);
					}
				} else if (this.isPaged && (event.getSlot() == this.inventory.getSize() - 1 || event.getSlot() == this.inventory.getSize() - 9)) {
					if (this.controlExit != null) {
						this.controlExit.onClick(event);
					}
				} else if (event.getCurrentItem() != null) {
					this.pages.get(this.currentIndex).handleClick(event);
					this.activeButton = event.getSlot();
					if (this.pages.get(this.currentIndex).chatEvent(event.getSlot())) {
						this.pendingChat = true;
					}
				}
			} catch (IndexOutOfBoundsException e) { }
			event.setCancelled(true);
		}
	}
	
   /**
    * Called on player chat.
    * Handles the player chat event for the button clicked.
    * 
    * @param event - AsyncPlayerChatEvent
    */
	public void onChat(AsyncPlayerChatEvent event) {
		if (this.panePlayer.equals(event.getPlayer()) && this.activeButton != -1) {
			this.pages.get(this.currentIndex).handleChat(event, this.activeButton);
			this.pendingChat = false;
			event.setCancelled(true);
		}
	}
	
   /**
    * Allows the button to be clicked.
    * 
    * @param bool - Allows the button to be clicked.
    */
	public void allowClick(boolean bool) {
		this.pendingClick = bool;
	}
	
   /**
    * Adds a button to the current page.
    * 
    * @param button - The button to be added.
    */
	public void addButton(Button button) {
		for (Entry < Integer, Page > entry: this.pages.entrySet()) {
			if (entry.getValue().addButton(button)) {
				if (entry.getKey() == this.currentIndex) {
					this.renderPage();
				}
				return;
			}
		}
		Page page = new Page(this.pageSize);
		page.addButton(button);
		this.pages.put(this.pages.lastKey() + 1, page);
		this.renderPage();
	}
	
   /**
    * Adds a button to the current page.
    * 
    * @param button - The button to be added.
    * @param amount - The number of buttons to be added.
    */
	public void addButton(Button button, int amount) {
		if (amount == 0 || amount == 1) { this.addButton(button); }
		else {
			for (int i = 0; i < amount; i++) {
				this.addButton(button);
			}
		}
	}
	
   /**
    * Removes a button from the current page.
    * 
    * @param button - The button to be removed.
    */
	public void removeButton(Button button) {
		for (Iterator < Entry < Integer, Page >> iterator = pages.entrySet().iterator(); iterator.hasNext();) {
			Entry < Integer, Page > entry = iterator.next();
			if (entry.getValue().removeButton(button)) {
				if (entry.getValue().isEmpty()) {
					if (this.pages.size() > 1) {
						iterator.remove();
					}
					if (this.currentIndex >= this.pages.size()) {
						this.currentIndex--;
					}
				}
				if (entry.getKey() >= this.currentIndex) {
					this.renderPage();
				}
				return;
			}
		}
	}
	
   /**
    * Sets a custom return button.
    * 
    * @param button - The button to be set as the return button.
    */
	public void setReturnButton(Button button) {
		if (this.isPaged) {
			this.controlExit = button;
			this.inventory.setItem(inventory.getSize() - 9, button.getItemStack());
			this.inventory.setItem(inventory.getSize() - 1, button.getItemStack());
		}
	}
	
   /**
    * Creates the controls for the existing inventory page.
    * 
    * @param inventory - The inventory to have the controls added.
    */
	private void createControls(Inventory inventory) {
		if (this.isPaged) {
			if (this.getCurrentPage() > 1) {
				ItemStack backItem;
				backItem = ItemHandler.getItem("ARROW", 1, false, "&3&n&lPrevious Page", "&7", "&7*Previous page &a&l" + (this.getCurrentPage() - 1) + "&7 / &c&l" + this.getPageAmount());
				this.controlBack = new Button(backItem, event -> this.selectPage(this.currentIndex - 1));
				inventory.setItem(inventory.getSize() - 8, backItem);
			} else {
				ItemStack backItem;
				backItem = ItemHandler.getItem("LEVER", 1, false, "&c&n&lPrevious Page", "&7", "&7*You are already at the first page.");
				inventory.setItem(inventory.getSize() - 8, backItem);
			}
			if (this.getCurrentPage() < this.getPageAmount()) {
				ItemStack nextItem;
				nextItem = ItemHandler.getItem("ARROW", 1, false, "&3&n&lNext Page", "&7", "&7*Next page &a&l" + (this.getCurrentPage() + 1) + "&7 / &c&l" + this.getPageAmount());
				this.controlNext = new Button(nextItem, event -> this.selectPage(this.getCurrentPage()));
				inventory.setItem(inventory.getSize() - 2, nextItem);
			} else {
				ItemStack nextItem;
				nextItem = ItemHandler.getItem("LEVER", 1, false, "&c&n&lNext Page", "&7", "&7*You are already at the last page.");
				inventory.setItem(inventory.getSize() - 2, nextItem);
			}
			inventory.setItem(inventory.getSize() - 5, ItemHandler.getItem("BOOK", 1, false, "&3&lPage &a&l" + this.getCurrentPage() + "&7 / &c&l" + this.getPageAmount(), "&7You are on page &a&l" + this.getCurrentPage() + "&7 / &c&l" + this.getPageAmount()));
			ItemStack exitItem = ItemHandler.getItem("BARRIER", 1, false, "&c&l&nMain Menu", "&7", "&7*Returns you to the main menu.");
			if (this.controlExit == null) {
				this.controlExit = new Button(exitItem, event -> Menu.startMenu(((Player)event.getWhoClicked())));
			} else {
				exitItem = controlExit.getItemStack();
			}
			inventory.setItem(inventory.getSize() - 9, exitItem);
			inventory.setItem(inventory.getSize() - 1, exitItem);
			ItemStack blackPane = ItemHandler.getItem((ServerUtils.hasSpecificUpdate("1_13") ? "BLACK_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE:15"), 1, false, "&f", "");
			inventory.setItem(inventory.getSize() - 3, blackPane);
			inventory.setItem(inventory.getSize() - 4, blackPane);
			inventory.setItem(inventory.getSize() - 6, blackPane);
			inventory.setItem(inventory.getSize() - 7, blackPane);
		}
	}
	
   /**
    * Renders the current page.
    * 
    */
	private void renderPage() {
		this.inventory.clear();
		this.pages.get(this.currentIndex).render(this.inventory);
		this.createControls(this.inventory);
	}
	
   /**
    * Gets the total number of existing pages.
    * 
    * @return The current number of pages.
    */
	private int getPageAmount() {
		return this.pages.size();
	}
	
   /**
    * Gets the current inventory page.
    * 
    * @return The current inventory page number.
    */
	private int getCurrentPage() {
		return (this.currentIndex + 1);
	}
	
   /**
    * Changes the current inventory page to the specified page number
    * 
    * @param index - The page to become the current page.
    */
	private void selectPage(int index) {
		if (index == this.currentIndex) {
			return;
		}
		this.currentIndex = index;
		this.renderPage();
	}
	
   /**
    * This is not called, rather it handles the onClick event to check
    * if the clicked inventory and button is valid.
    * 
    * @param event - InventoryClickEvent
    * @return If the inventory clicked is the same as the current inventory page.
    */
	public boolean clickInventory(final InventoryClickEvent event) {
		if (ServerUtils.hasSpecificUpdate("1_14")) {
			return (event.getSlot() == -999 || event.getSlot() == -1 || event.getClickedInventory() == event.getWhoClicked().getInventory());
		} else {
			final ItemStack clickItem = event.getCurrentItem();
			final int slot = event.getSlot();
			return (slot == -999 || slot == -1 || (clickItem.equals(event.getWhoClicked().getInventory().getItem(slot)) || clickItem.getType() == org.bukkit.Material.AIR));
		}
	}
	
   /**
    * Checks if the current page is expecting a player chat event before continuing.
    * 
    * @return If there is a pending chat event.
    */
	public boolean chatPending() {
		return this.pendingChat;
	}
	
   /**
    * Opens the current inventory page for the player to view.
    * 
    * @param player - The player to have the current inventory page opened.
    */
	public void open(Player player) {
		SchedulerUtils.run(() -> {
			this.renderPage();
			player.openInventory(this.getInventory());
		});
	}
	
   /**
    * Gets the inventory.
    * 
    * @return The inventory for the interface.
    */
	@Override
	public Inventory getInventory() {
		return this.inventory;
	}
}