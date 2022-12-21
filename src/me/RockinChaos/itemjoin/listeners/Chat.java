package me.RockinChaos.itemjoin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.RockinChaos.itemjoin.item.ItemData;

public class Chat implements Listener {
	
   /**
    * Called when a player sends a chat message.
    * Attempts to prevent a chat message from being sent.
    * 
    * @param event - AsyncPlayerChatEvent
    */
	@EventHandler()
    public void onChat(AsyncPlayerChatEvent event) { 
		if (!event.isCancelled() && (ItemData.getInfo().isPreventString(event.getPlayer(), "Chat"))) {
			if (ItemData.getInfo().isPreventBypass(event.getPlayer())) { } 
			else { event.setCancelled(true); }
		}
    }
}