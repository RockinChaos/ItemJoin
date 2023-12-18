package me.RockinChaos.itemjoin.listeners;

import me.RockinChaos.itemjoin.PluginData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat implements Listener {

    /**
     * Called when a player sends a chat message.
     * Attempts to prevent a chat message from being sent.
     *
     * @param event - AsyncPlayerChatEvent
     */
    @EventHandler()
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled() && (PluginData.getInfo().isPreventString(event.getPlayer(), "Chat"))) {
            if (PluginData.getInfo().isPreventBypass(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }
}