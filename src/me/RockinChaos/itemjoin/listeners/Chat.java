package me.RockinChaos.itemjoin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.StringUtils;

public class Chat implements Listener {
	
   /**
    * Called when a player sends a chat message.
    * Attempts to prevent a chat message from being sent.
    * 
    * @param event - AsyncPlayerChatEvent
    */
	@EventHandler()
    public void onChat(AsyncPlayerChatEvent event) { 
		if (!event.isCancelled() && (StringUtils.splitIgnoreCase(ConfigHandler.getConfig().getPrevent("Chat"), "TRUE", ",") 
				  || StringUtils.splitIgnoreCase(ConfigHandler.getConfig().getPrevent("Chat"), event.getPlayer().getWorld().getName(), ",")
				  || StringUtils.splitIgnoreCase(ConfigHandler.getConfig().getPrevent("Chat"), "ALL", ",") || StringUtils.splitIgnoreCase(ConfigHandler.getConfig().getPrevent("Chat"), "GLOBAL", ","))) {
			if (ConfigHandler.getConfig().isPreventOP() && event.getPlayer().isOp() || ConfigHandler.getConfig().isPreventCreative() && PlayerHandler.isCreativeMode(event.getPlayer())) { } 
			else { 
				event.setCancelled(true);
			}
		}
    }
}