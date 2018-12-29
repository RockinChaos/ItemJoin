package me.RockinChaos.itemjoin.api;

import org.bukkit.entity.Player;

public class ItemJoinAPI {
	private APIUtils apiUtils = new APIUtils();
	
    /**
     * Gives all ItemJoin items to the specified player.
     * 
     * @param player that will recieve the items.
     */
	public void getItems(Player player) {
		this.apiUtils.setItems(player);
	}
}