package me.RockinChaos.itemjoin.utils.api;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import me.RockinChaos.itemjoin.ItemJoin;

public class ProtocolAPI {
	
	private static ProtocolManager protocolManager;
	
   /**
    * Handles both server side and client side protocol packets.
    * This is specific to ProtocolLib and is required for this to function.
    * 
    */
	public static void handleProtocols() {
		if (protocolManager == null) { protocolManager = ProtocolLibrary.getProtocolManager(); }
		protocolManager.addPacketListener(new PacketAdapter(ItemJoin.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.AUTO_RECIPE, PacketType.Play.Client.CLOSE_WINDOW) {
  		   /**
  		    * Handles incomming client packets.
  		    * 
            * @param player - the player tied to the packet.
            * @param channel - the channel the packet was called on.
            * @param packet - the packet object.
  		    */
		    @Override
		    public void onPacketReceiving(final PacketEvent event) {
		        if (me.RockinChaos.itemjoin.utils.protocol.ProtocolManager.manageEvents(event.getPlayer(), event.getPacket())) {
		        	event.setCancelled(true);
		        }
		    }
		});
	}
	
   /**
    * Checks if the protocol handler(s) are open.
    * 
    * @return If the protocol handler(s) are open.
    */
	public static boolean isHandling() {
		return (protocolManager != null);
	}
}