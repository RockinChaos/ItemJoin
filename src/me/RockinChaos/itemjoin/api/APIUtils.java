package me.RockinChaos.itemjoin.api;

import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

public class APIUtils {
	
	 void setItems(Player player) {
		String Probable = ItemUtilities.getProbabilityItem(player);
		final int session = Utils.getRandom(1, 80000);
		for (ItemMap item : ItemUtilities.getItems()) {
			if (item.inWorld(player.getWorld()) && ItemUtilities.isChosenProbability(item, Probable) && SQLData.isEnabled(player)
					&& item.hasPermission(player) && ItemUtilities.isObtainable(player, item, session)) {
					item.giveTo(player, false, 0);
			}
			item.setAnimations(player);
		}
		ItemUtilities.sendFailCount(player, session);
		PlayerHandler.delayUpdateInventory(player, 15L);
	}
}
