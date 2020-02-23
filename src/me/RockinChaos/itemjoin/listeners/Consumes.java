package me.RockinChaos.itemjoin.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class Consumes implements Listener {

	@EventHandler
	private void onPlayerConsumesItem(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		if (item.getType() == Material.GOLDEN_APPLE) {
			ItemMap itemMap = ItemUtilities.getItemMap(item, null, player.getWorld());
			if (itemMap != null && itemMap.getMaterial() == Material.GOLDEN_APPLE && itemMap.isCustomConsumable()) {
				if (itemMap.getPotionEffect() != null && !itemMap.getPotionEffect().isEmpty()) {
					for (PotionEffect potion: itemMap.getPotionEffect()) { player.addPotionEffect(potion, true); }
				}
				event.setCancelled(true);
				player.getInventory().remove(item);
			}
		}
	}
	
	 @EventHandler
	 private void onRefillTotem(EntityResurrectEvent event) {
	 	if (event.getEntity() instanceof Player) {
	 		Player player = (Player) event.getEntity();
	 		ItemStack saveMainStack = null; if (PlayerHandler.getMainHandItem(player) != null) { saveMainStack = PlayerHandler.getMainHandItem(player).clone(); }
	 		final ItemStack mainStack = saveMainStack;
	 		ItemStack saveOffStack = null; if (PlayerHandler.getOffHandItem(player) != null) { saveOffStack = PlayerHandler.getOffHandItem(player).clone(); }
	 		final ItemStack offStack = saveOffStack;
	 		ItemMap mainHandMap = ItemUtilities.getItemMap(mainStack, null, player.getWorld());
	 		ItemMap offHandMap = ItemUtilities.getItemMap(offStack, null, player.getWorld());
	 		if ((mainHandMap != null && !ItemUtilities.isAllowed(player, mainStack, "count-lock")) || (offHandMap != null && !ItemUtilities.isAllowed(player, offStack, "count-lock"))) {
	 			if ((Utils.containsIgnoreCase(mainStack.getType().name(), "TOTEM") && mainHandMap != null) || (Utils.containsIgnoreCase(offStack.getType().name(), "TOTEM") && offHandMap != null)) {
	 				new BukkitRunnable() {
	 					@Override
	 					public void run() {
	 						if (mainHandMap != null && mainHandMap.isSimilar(mainStack)) {
	 							if (Utils.containsIgnoreCase(PlayerHandler.getMainHandItem(player).getType().name(), "TOTEM")) {
	 								PlayerHandler.getMainHandItem(player).setAmount(mainHandMap.getCount());
	 							} else if (Utils.containsIgnoreCase(PlayerHandler.getOffHandItem(player).getType().name(), "TOTEM")) {
	 								PlayerHandler.getOffHandItem(player).setAmount(mainHandMap.getCount());
	 							}
	 							if (PlayerHandler.getMainHandItem(player).getType() == Material.AIR) {
	 								PlayerHandler.setMainHandItem(player, mainStack);
	 							} else if (PlayerHandler.getOffHandItem(player).getType() == Material.AIR) {
	 								PlayerHandler.setOffHandItem(player, mainStack);
	 							}
	 						} else if (offHandMap != null && offHandMap.isSimilar(offStack)) {
	 							if (Utils.containsIgnoreCase(PlayerHandler.getOffHandItem(player).getType().name(), "TOTEM")) {
	 								PlayerHandler.getOffHandItem(player).setAmount(offHandMap.getCount());
	 							} else if (Utils.containsIgnoreCase(PlayerHandler.getMainHandItem(player).getType().name(), "TOTEM")) {
	 								PlayerHandler.getMainHandItem(player).setAmount(offHandMap.getCount());
	 							}
	 							if (PlayerHandler.getOffHandItem(player).getType() == Material.AIR) {
	 								PlayerHandler.setOffHandItem(player, offStack);
	 							} else if (PlayerHandler.getMainHandItem(player).getType() == Material.AIR) {
	 								PlayerHandler.setMainHandItem(player, offStack);
	 							}
	 						}
	 					}
	 				}.runTaskLater(ItemJoin.getInstance(), 1L);
	 			}
	 		}
	 	}
	 }
}