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
	
    private int counter;
    private final int ID = counter++;
    private ItemStack itemStack;
    private Consumer<InventoryClickEvent> clickAction;
    private Consumer<AsyncPlayerChatEvent> chatAction;

   /**
    * Creates a new button instance.
    * There is no click event or chat event.
    * 
    * @param itemStack - The ItemStack the button is to be placed as.
    */
    public Button(ItemStack itemStack) {
    	this(itemStack, event -> {});
    }
    
   /**
    * Creates a new button instance.
    * There is no chat event.
    * 
    * @param itemStack - The ItemStack the button is to be placed as.
    * @param clickAction - The method to be executed upon clicking the button.
    */
    public Button(ItemStack itemStack, Consumer < InventoryClickEvent > clickAction) {
    	this.itemStack = itemStack;
    	this.clickAction = clickAction;
    }
    
   /**
    * Creates a new button instance.
    * 
    * @param itemStack - The ItemStack the button is to be placed as.
    * @param clickAction - The method to be executed upon clicking the button.
    * @param chatAction - The method to be executed upon chatting after clicking the button.
    */
    public Button(ItemStack itemStack, Consumer < InventoryClickEvent > clickAction, Consumer < AsyncPlayerChatEvent > chatAction) {
    	this.itemStack = itemStack;
    	this.clickAction = clickAction;
    	this.chatAction = chatAction;
    }
    
   /**
    * Gets the ItemStack for the button.
    * 
    * @return The buttons ItemStack.
    */
    public ItemStack getItemStack() {
    	return this.itemStack;
    }
    
   /**
    * Sets the click action method to be executed.
    * 
    * @param clickAction - The click action method to be executed.
    */
    public void setClickAction(Consumer < InventoryClickEvent > clickAction) {
    	this.clickAction = clickAction;
    }
    
   /**
    * Sets the chat action method to be executed.
    * 
    * @param chatAction - The chat action method to be executed.
    */
    public void setChatAction(Consumer < AsyncPlayerChatEvent > chatAction) {
    	this.chatAction = chatAction;
    }
    
   /**
	* Called on player inventory click.
	* Executes the pending click actions.
    * 
    * @param event - InventoryClickEvent
    */
    public void onClick(InventoryClickEvent event) {
		if (ItemJoin.getInstance().isEnabled()) {
			Bukkit.getServer().getScheduler().runTask(ItemJoin.getInstance(), () -> {
				this.clickAction.accept(event);
			});
		}
    }
    
   /**
	* Called on player chat.
	* Executes the pending chat actions.
    * 
    * @param event - AsyncPlayerChatEvent
    */
    public void onChat(AsyncPlayerChatEvent event) {
		if (ItemJoin.getInstance().isEnabled()) {
			Bukkit.getServer().getScheduler().runTask(ItemJoin.getInstance(), () -> {
				this.chatAction.accept(event);
			});
		}
    }
    
   /**
    * Checks if the button is waits for a chat event.
    * 
    * @return If the button is listening for a chat event.
    */
    public boolean chatEvent() {
    	if (this.chatAction != null) {
    		return true;
    	}
    	return false;
    }
    
   /**
    * Checks if the current button instance is similar to the referenced object.
    * 
    * @param obj - The object being compared.
    */
    @Override
    public boolean equals(Object obj) {
    	if (this == obj) { return true; }
    	if (!(obj instanceof Button)) { return false; }
    	Button button = (Button) obj;
    	return this.ID == button.ID;
    }
    
   /**
    * Gets the hash of the current button id.
    * 
    * @return The hash of the current button id.
    */
    @Override
    public int hashCode() {
    	return Objects.hash(this.ID);
    }
}