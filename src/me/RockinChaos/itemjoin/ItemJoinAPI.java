package me.RockinChaos.itemjoin;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.listeners.giveitems.SetItems;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

public class ItemJoinAPI {
	
	public static void getAllItems(Player player) {
		setItems(player);
	}
	
	private static void setItems(Player player) {
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				final String world = player.getWorld().getName();
				if (WorldHandler.inWorld(items, world) && PermissionsHandler.hasItemsPermission(items, item, player) && SQLData.isEnabled(player)) {
					if (items.getString(".slot") != null) {
						String slotlist = items.getString(".slot").replace(" ", "");
						String[] slots = slotlist.split(",");
						ItemHandler.clearItemID(player);
						for (String slot: slots) {
							String ItemID = ItemHandler.getItemID(player, slot);
							ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
							if (Utils.isCustomSlot(slot) && ItemHandler.isObtainable(player, items, item, slot, ItemID, inStoredItems)) {
								SetItems.setCustomSlots(player, item, slot, ItemID);
							} else if (Utils.isInt(slot) && ItemHandler.isObtainable(player, items, item, slot, ItemID, inStoredItems)) {
								SetItems.setInvSlots(player, item, slot, ItemID);
							}
						}
					}
				}
			}
		}
	}
}
