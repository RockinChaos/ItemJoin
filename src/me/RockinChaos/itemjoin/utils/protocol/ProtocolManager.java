package me.RockinChaos.itemjoin.utils.protocol;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.netty.channel.Channel;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;

public class ProtocolManager {
	private TinyProtocol protocol;
	
  	public void handleProtocols() {
  		if (this.protocol != null) { this.closeProtocol(); }
  		this.protocol = new TinyProtocol(ItemJoin.getInstance()) {
  			@Override
  			public Object onPacketInAsync(Player player, Channel channel, Object packet) {
  				if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInAutoRecipe")) {
  					if (hasCraftingItem(player)) {
  						return null;
  					}
  				}
  				return super.onPacketInAsync(player, channel, packet);
  			}
  			@Override
  			public Object onPacketOutAsync(Player reciever, Channel channel, Object packet) {
  				return packet;
  			}
  		};
  	}
  	
  	private boolean hasCraftingItem(Player player) {
  		for (int i = 0; i <= 4; i++) {
  			final ItemStack[] craftingContents = player.getOpenInventory().getTopInventory().getContents().clone();
  			for (ItemMap itemMap: ItemUtilities.getItems()) {
  				if (itemMap.isCraftingItem() && itemMap.isSimilar(craftingContents[i])) {
  					return true;
  				}
  			}
  		}
  		return false;
  	}
  	
  	public void closeProtocol() {
  		if (this.protocol != null) {
  			this.protocol.close();
  		}
  	}
}
