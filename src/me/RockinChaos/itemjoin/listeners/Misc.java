package me.RockinChaos.itemjoin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;

import me.RockinChaos.itemjoin.utils.interfaces.Interface;

public class Misc implements Listener {
  	
//  ============================================== //
//          Handlers for virtualInventory          //
//  ============================================== //
  	private Interface expiredInventory;

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Interface) {
			((Interface) holder).onClick(event);
    		this.expiredInventory = ((Interface) holder);
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (this.expiredInventory != null && this.expiredInventory.chatPending()) {
			this.expiredInventory.onChat(event);
		}
	}

//  ===============================================================================

}