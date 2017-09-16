package me.RockinChaos.itemjoin.listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.CommandHandler;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class InteractCmds implements Listener {

	@EventHandler
	public void onInventoryCmds(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		final Player player = (Player) event.getWhoClicked();
		final String world = player.getWorld().getName();
		String action = event.getAction().toString();
		setupCommands(player, world, item, action);
	}

	@EventHandler
	public void onInteractCmds(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		final Player player = event.getPlayer();
		final String world = player.getWorld().getName();
		String action = event.getAction().toString();
		setupCommands(player, world, item, action);
	}

	public void setupCommands(Player player, String world, ItemStack item1, String action) {
		if (Utils.isConfigurable()) {
			for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
				ConfigurationSection items = ConfigHandler.getItemSection(item);
				if (item != null && WorldHandler.inWorld(items, world) && PermissionsHandler.hasPermission(items, item, player)) {
					if (items.getString(".slot") != null) {
						String slotlist = items.getString(".slot").replace(" ", "");
						String[] slots = slotlist.split(",");
						ItemHandler.clearItemID(player);
						for (String slot: slots) {
							String ItemID = ItemHandler.getItemID(player, slot);
							ItemStack inStoredItems = CreateItems.items.get(player.getWorld().getName() + "." + player.getName().toString() + ".items." + ItemID + item);
							if (inStoredItems != null && ItemHandler.isSimilar(item1, inStoredItems) && CommandHandler.isCommandable(action, items)) {
								if (!CommandHandler.onCooldown(items, player, item, item1)) {
									CommandHandler.chargePlayer(items, item, player, action);
									CommandHandler.removeDisposable(items, item1, player);
									break;
								}
							}
						}
					}
				}
			}
		}
	}
}
