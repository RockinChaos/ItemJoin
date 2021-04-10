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
		protocolManager.addPacketListener(new PacketAdapter(ItemJoin.getInstance(), ListenerPriority.LOWEST, PacketType.Play.Client.AUTO_RECIPE, PacketType.Play.Client.CLOSE_WINDOW, 
				PacketType.Play.Client.PICK_ITEM) {
  		   /**
  		    * Handles incomming client packets.
  		    * 
            * @param event - PacketEvent
  		    */
		    @Override
		    public void onPacketReceiving(final PacketEvent event) {
		    	String packetName = (event.getPacket() != null && event.getPacketType() == PacketType.Play.Client.AUTO_RECIPE ? "PacketPlayInAutoRecipe" : 
		    						(event.getPacket() != null && event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW ? "PacketPlayInCloseWindow" : 
		    						(event.getPacket() != null && event.getPacketType() == PacketType.Play.Client.PICK_ITEM ? "PacketPlayInPickItem" : null)));
		        if (me.RockinChaos.itemjoin.utils.protocol.ProtocolManager.manageEvents(event.getPlayer(), packetName)) {
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