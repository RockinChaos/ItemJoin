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

import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;

public class Button {
    private static int counter;
    private final int ID = counter++;
    private ItemStack itemStack;
    private Consumer<InventoryClickEvent> clickAction;
    private Consumer<AsyncPlayerChatEvent> chatAction;

    public Button(ItemStack itemStack) {
    	this(itemStack, event -> {});
    }
    
    public Button(ItemStack itemStack, Consumer < InventoryClickEvent > clickAction) {
    	this.itemStack = itemStack;
    	this.clickAction = clickAction;
    }
    
    public Button(ItemStack itemStack, Consumer < InventoryClickEvent > clickAction, Consumer < AsyncPlayerChatEvent > chatAction) {
    	this.itemStack = itemStack;
    	this.clickAction = clickAction;
    	this.chatAction = chatAction;
    }
    
    public ItemStack getItemStack() {
    	return this.itemStack;
    }
    
    public void setClickAction(Consumer < InventoryClickEvent > clickAction) {
    	this.clickAction = clickAction;
    }
    
    public void setChatAction(Consumer < AsyncPlayerChatEvent > chatAction) {
    	this.chatAction = chatAction;
    }
    
    public void onClick(InventoryClickEvent event) {
    	this.clickAction.accept(event);
    }
    
    public void onChat(AsyncPlayerChatEvent event) {
    	final Consumer<AsyncPlayerChatEvent> chatAction = this.chatAction;
    	Bukkit.getScheduler().runTask(ItemJoin.getInstance(), new Runnable() {
    	    @Override
    	    public void run() {
    	    	chatAction.accept(event);
    	    }
    	});
    }
    
    public boolean chatEvent() {
    	if (this.chatAction != null) {
    		return true;
    	}
    	return false;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (this == obj) { return true; }
    	if (!(obj instanceof Button)) { return false; }
    	Button button = (Button) obj;
    	return this.ID == button.ID;
    }
    
    @Override
    public int hashCode() {
    	return Objects.hash(this.ID);
    }
}