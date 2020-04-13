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

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class Interface implements InventoryHolder {
	private Inventory inventory;
	private boolean isPaged;
	private boolean pendingChat = false;
	private boolean pendingClick = false;
	private int activeButton = -1;
	private int currentIndex;
	private int pageSize;
	private SortedMap < Integer, Page > pages = new TreeMap < > ();
	
	private Button controlBack;
	private Button controlNext;
	private Button controlExit;
	
	@Override
	public Inventory getInventory() {
		return this.inventory;
	}
	
	public Interface(boolean isPaged, int rows, String title) {
		this.isPaged = isPaged;
		if (this.isPaged) {
			this.pageSize = rows - 1;
		} else {
			this.pageSize = rows * 9;
		}
		this.inventory = Bukkit.createInventory(this, rows * 9, Utils.colorFormat(title));
		this.inventory.setMaxStackSize(128);
		this.pages.put(0, new Page(this.pageSize));
		this.createControls(this.inventory);
	}
	
	public void onClick(InventoryClickEvent event) {
		if (!(this.pendingClick && event.getSlot() <= this.inventory.getSize() && event.getSlot() >= 0 && this.clickInventory(event))) {
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
			event.setCancelled(true);
		}
	}
	
	public void onChat(AsyncPlayerChatEvent event) {
		if (this.activeButton != -1) {
			this.pages.get(this.currentIndex).handleChat(event, this.activeButton);
			this.pendingChat = false;
			event.setCancelled(true);
		}
	}
	
	public void allowClick(boolean bool) {
		this.pendingClick = bool;
	}
	
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
	
	public void addButton(Button button, int amount) {
		for (Entry < Integer, Page > entry: this.pages.entrySet()) {
			for (int i = amount; i >= 1; i--) {
				if (entry.getValue().addButton(button)) {
					if (entry.getKey() == this.currentIndex) {
						this.renderPage();
					}
					if (i == 1) {
						return;
					}
				}
			}
		}
		Page page = new Page(this.pageSize);
		for (int i = amount; i >= 1; i--) {
			page.addButton(button);
		}
		this.pages.put(this.pages.lastKey() + amount, page);
		this.renderPage();
	}
	
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
	
	public void setReturnButton(Button button) {
		if (this.isPaged) {
			this.controlExit = button;
			this.inventory.setItem(inventory.getSize() - 9, button.getItemStack());
			this.inventory.setItem(inventory.getSize() - 1, button.getItemStack());
		}
	}
	
	private void createControls(Inventory inventory) {
		if (this.isPaged) {
			if (this.getCurrentPage() > 1) {
				ItemStack backItem;
				if (ServerHandler.hasSpecificUpdate("1_8")) {
					backItem = ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&3&n&lPrevious Page", "&7", "&7*Previous page &a&l" + (this.getCurrentPage() - 1) + "&7 / &c&l" + this.getPageAmount()), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19");
				} else {
					backItem = ItemHandler.getItem("ARROW", 1, false, "&3&n&lPrevious Page", "&7", "&7*Previous page &a&l" + (this.getCurrentPage() - 1) + "&7 / &c&l" + this.getPageAmount());
				}
				this.controlBack = new Button(backItem, event -> this.selectPage(this.currentIndex - 1));
				inventory.setItem(inventory.getSize() - 8, backItem);
			} else {
				ItemStack backItem;
				if (ServerHandler.hasSpecificUpdate("1_8")) {
					backItem = ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&c&n&lPrevious Page", "&7", "&7*You are already at the first page."), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTdjMjE0NGZkY2I1NWMzZmMxYmYxZGU1MWNhYmRmNTJjMzg4M2JjYjU3ODkyMzIyNmJlYjBkODVjYjJkOTgwIn19fQ==");
				} else {
					backItem = ItemHandler.getItem("LEVER", 1, false, "&c&n&lPrevious Page", "&7", "&7*You are already at the first page.");
				}
				inventory.setItem(inventory.getSize() - 8, backItem);
			}
			if (this.getCurrentPage() < this.getPageAmount()) {
				ItemStack nextItem;
				if (ServerHandler.hasSpecificUpdate("1_8")) {
					nextItem = ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&3&n&lNext Page", "&7", "&7*Next page &a&l" + (this.getCurrentPage() + 1) + "&7 / &c&l" + this.getPageAmount()), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ");
				} else {
					nextItem = ItemHandler.getItem("ARROW", 1, false, "&3&n&lNext Page", "&7", "&7*Next page &a&l" + (this.getCurrentPage() + 1) + "&7 / &c&l" + this.getPageAmount());
				}
				this.controlNext = new Button(nextItem, event -> this.selectPage(this.getCurrentPage()));
				inventory.setItem(inventory.getSize() - 2, nextItem);
			} else {
				ItemStack nextItem;
				if (ServerHandler.hasSpecificUpdate("1_8")) {
					nextItem = ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&c&n&lNext Page", "&7", "&7*You are already at the last page."), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTdjMjE0NGZkY2I1NWMzZmMxYmYxZGU1MWNhYmRmNTJjMzg4M2JjYjU3ODkyMzIyNmJlYjBkODVjYjJkOTgwIn19fQ==");
				} else {
					nextItem = ItemHandler.getItem("LEVER", 1, false, "&c&n&lNext Page", "&7", "&7*You are already at the last page.");
				}
				inventory.setItem(inventory.getSize() - 2, nextItem);
			}
			inventory.setItem(inventory.getSize() - 5, ItemHandler.getItem("BOOK", 1, false, "&3&lPage &a&l" + this.getCurrentPage() + "&7 / &c&l" + this.getPageAmount(), "&7You are on page &a&l" + this.getCurrentPage() + "&7 / &c&l" + this.getPageAmount()));
			ItemStack exitItem = ItemHandler.getItem("BARRIER", 1, false, "&c&l&nMain Menu", "&7", "&7*Returns you to the main menu.");
			if (this.controlExit == null) {
				this.controlExit = new Button(exitItem, event -> ConfigHandler.getItemCreator().startMenu(((Player)event.getWhoClicked())));
			} else {
				exitItem = controlExit.getItemStack();
			}
			inventory.setItem(inventory.getSize() - 9, exitItem);
			inventory.setItem(inventory.getSize() - 1, exitItem);
			ItemStack blackPane = ItemHandler.getItem("STAINED_GLASS_PANE:15", 1, false, "&f", "");
			inventory.setItem(inventory.getSize() - 3, blackPane);
			inventory.setItem(inventory.getSize() - 4, blackPane);
			inventory.setItem(inventory.getSize() - 6, blackPane);
			inventory.setItem(inventory.getSize() - 7, blackPane);
		}
	}
	
	private void renderPage() {
		this.inventory.clear();
		this.pages.get(this.currentIndex).render(this.inventory);
		this.createControls(this.inventory);
	}
	
	private int getPageAmount() {
		return this.pages.size();
	}
	
	private int getCurrentPage() {
		return (this.currentIndex + 1);
	}
	
	private void selectPage(int index) {
		if (index == this.currentIndex) {
			return;
		}
		this.currentIndex = index;
		this.renderPage();
	}
	
	public boolean clickInventory(InventoryClickEvent event) {
		if (ServerHandler.hasSpecificUpdate("1_14")) {
			return (event.getClickedInventory() == event.getWhoClicked().getInventory());
		} else {
			final ItemStack clickItem = event.getCurrentItem();
			final int slot = event.getSlot();
			return clickItem.equals(event.getWhoClicked().getInventory().getItem(slot)) || clickItem.getType() == org.bukkit.Material.AIR;
		}
	}
	
	public boolean chatPending() {
		return this.pendingChat;
	}
	
	public void open(Player player) {
		this.renderPage();
		player.openInventory(getInventory());
	}
}