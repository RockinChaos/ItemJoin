package me.RockinChaos.itemjoin.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class Interact implements Listener {
	private Map < String, Long > playersOnCooldown = new HashMap < String, Long > ();
	private HashMap < String, Long > storedSpammedPlayers = new HashMap < String, Long > ();
	private int cdtime = 0;
	private int spamtime = 1;
	
	 @EventHandler(priority = EventPriority.LOWEST)
	 public void onInteraction(PlayerInteractEvent event) {
	 	ItemStack item = event.getItem();
	 	Player player = event.getPlayer();
	 	if (event.hasItem() && event.getAction() != Action.PHYSICAL && !ItemUtilities.isAllowed(player, item, "cancel-events")) {
	 		event.setCancelled(true);
	 		PlayerHandler.updateInventory(player);
	 	}
	 }

	 @EventHandler
	 public void onInteractCooldown(PlayerInteractEvent event) {
	 	Player player = event.getPlayer();
	 	ItemStack item = event.getItem();
	 	if (event.hasItem() && event.getAction() != Action.PHYSICAL) {
	 		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	 			ItemMap itemMap = ItemUtilities.getMappedItem(item, player.getWorld());
	 			if (itemMap != null && itemMap.getInteractCooldown() != 0) {
	 				if (!onItemCooldown(itemMap, player, item)) {
	 					playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + itemMap.getConfigName(), System.currentTimeMillis());
	 				} else if (onItemCooldown(itemMap, player, item)) {
	 					event.setCancelled(true);
	 				}
	 			}
	 		}
	 	}
	 }

	@EventHandler
	public void onInventoryCmds(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		String action = event.getAction().toString();
		if (setupCommands(player, item, action)) { event.setCancelled(true); }
	}

	@EventHandler
	public void onInteractCmds(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		final Player player = event.getPlayer();
		String action = event.getAction().toString();
		if (PlayerHandler.isAdventureMode(player) && !action.contains("LEFT") 
				|| !PlayerHandler.isAdventureMode(player)) {
			ItemMap itemMap = ItemUtilities.getMappedItem(PlayerHandler.getHandItem(player), player.getWorld());
			if (itemMap != null && itemMap.isSimilar(item)) {
				if (setupCommands(player, item, action)) { event.setCancelled(true); }
			}
		}
	}
	
	@EventHandler
	public void onAdventureAnimationCmds(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		ItemStack item = PlayerHandler.getHandItem(player);
		if (PlayerHandler.isAdventureMode(player)) {
			if (setupCommands(player, item, "LEFT_CLICK_AIR")) { event.setCancelled(true); }
		}
	}
	
	private boolean setupCommands(Player player, ItemStack item, String action) {
		  ItemMap itemMap = ItemUtilities.getMappedItem(item, player.getWorld());
			if (itemMap != null && itemMap.inWorld(player.getWorld()) && itemMap.hasPermission(player)) {
				return itemMap.executeCommands(player, action);
			}
		return false;
	}
	
	private boolean onItemCooldown(ItemMap itemMap, Player player, ItemStack item) {
		long playersCooldownList = 0L;
		if (playersOnCooldown.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item)) {
			playersCooldownList = playersOnCooldown.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item);
		}
		cdtime = itemMap.getInteractCooldown();
		int cdmillis = cdtime * 1000;
		if (System.currentTimeMillis() - playersCooldownList >= cdmillis) {
			return false;
		} else {
			if (spamItemTask(player, itemMap.getConfigName())) {
				storedSpammedPlayers.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
				if (itemMap.getCooldownMessage() != null && !itemMap.getCooldownMessage().isEmpty()) {
					int timeLeft = (int)(cdtime - ((System.currentTimeMillis() - playersCooldownList) / 1000));
					String inhand = itemMap.getCustomName();
					String cooldownmsg = itemMap.getCooldownMessage().replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", inhand);;
					cooldownmsg = Utils.translateLayout(cooldownmsg, player);
					player.sendMessage(cooldownmsg);
				}
			}
			return true;
		}
	}
	
	private boolean spamItemTask(Player player, String name) {
		boolean itemsSpam = ConfigHandler.getConfig("items.yml").getBoolean("items-Spamming");
		if (itemsSpam != true) {
			long playersCooldownList = 0L;
			if (storedSpammedPlayers.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + name)) {
				playersCooldownList = storedSpammedPlayers.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + name);
			}
			int cdmillis = spamtime * 1000;
			if (System.currentTimeMillis() - playersCooldownList >= cdmillis) {} else { return false; }
		}
		return true;
	}
}