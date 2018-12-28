package me.RockinChaos.itemjoin.api;

import org.bukkit.entity.Player;

public class ItemJoinAPI {
	private APIUtils apiUtils = new APIUtils();
	
	public void getItems(Player player) {
		this.apiUtils.setItems(player);
	}
}