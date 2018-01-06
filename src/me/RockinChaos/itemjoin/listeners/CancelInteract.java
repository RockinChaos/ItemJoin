package me.RockinChaos.itemjoin.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.WorldHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class CancelInteract implements Listener {
	private static Map < String, Long > playersOnCooldown = new HashMap < String, Long > ();
	private static HashMap < String, Long > storedSpammedPlayers = new HashMap < String, Long > ();
	private static int cdtime = 0;
	private static int spamtime = 1;
	
	 @EventHandler
	  public void onCancelInteracts(PlayerInteractEvent event) 
	  {
	    ItemStack item = event.getItem();
	    final Player player = event.getPlayer();
	    String itemflag = "cancel-events";
	      if (event.hasItem() && event.getAction() != Action.PHYSICAL && !ItemHandler.isAllowedItem(player, item, itemflag)) {
	        event.setCancelled(true);
	        PlayerHandler.updateInventory(player);
	 }
  }
	 
	 @EventHandler
	  public void onUseCooldown(PlayerInteractEvent event) {
		  Player player = event.getPlayer();
		  ItemStack inPlayerInventory = event.getItem();
		  if (event.hasItem() && event.getAction() != Action.PHYSICAL) {
			  if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				  if (Utils.isConfigurable()) {
					  for (String item: ConfigHandler.getConfigurationSection().getKeys(false)) {
						  ConfigurationSection items = ConfigHandler.getItemSection(item);
						  String world = player.getWorld().getName();
						  int Arbitrary = 0;
						  String ItemID;
					      if (WorldHandler.inWorld(items, world) && items.getString(".use-cooldown") != null && Utils.isInt(items.getString(".use-cooldown")) && items.getString(".slot") != null) {
							  String slotlist = items.getString(".slot").replace(" ", "");
							  String[] slots = slotlist.split(",");
							  for (String slot: slots) {
								  if (slot.equalsIgnoreCase("Arbitrary")) {
									  Arbitrary = Arbitrary + 1;
									  ItemID = slot + Arbitrary;
								  } else { ItemID = slot; }
								  ItemStack inStoredItems = CreateItems.items.get(world + "." + PlayerHandler.getPlayerID(player) + ".items." + ItemID + item);
								  if (ItemHandler.isSimilar(inPlayerInventory, inStoredItems)) { 
									  if (!onItemCooldown(items, player, item)) { playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis()); } 
									  else if (onItemCooldown(items, player, item)) { event.setCancelled(true); }
								   }
							  }
						  }
					   }
				  }
			  }
		  }
	 }
	
	public static boolean onItemCooldown(ConfigurationSection items, Player player, String item) {
		long playersCooldownList = 0L;
		if (playersOnCooldown.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item)) {
			playersCooldownList = playersOnCooldown.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item);
		}
		cdtime = items.getInt(".use-cooldown");
		int cdmillis = cdtime * 1000;
		if (System.currentTimeMillis() - playersCooldownList >= cdmillis) { return false; } 
		else { if (spamItemTask(player, item)) {
			storedSpammedPlayers.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
			if (ConfigHandler.getConfig("en-lang.yml").getString("itemUsageCooldown") != null 
					&& !ConfigHandler.getConfig("en-lang.yml").getString("itemUsageCooldown").isEmpty()) {
				int timeLeft = (int)(cdtime - ((System.currentTimeMillis() - playersCooldownList) / 1000));
				String inhand = items.getString(".name");
				String cooldownmsg = ConfigHandler.getConfig("en-lang.yml").getString("itemUsageCooldown").replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", inhand);;
				cooldownmsg = Utils.format(cooldownmsg, player);
				player.sendMessage(cooldownmsg);
			}}
			return true;
		}
	}
	
	public static boolean spamItemTask(Player player, String item) {
		boolean itemsSpam = ConfigHandler.getConfig("items.yml").getBoolean("items-Spamming");
		if (itemsSpam != true) {
			long playersCooldownList = 0L;
			if (storedSpammedPlayers.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item)) {
				playersCooldownList = storedSpammedPlayers.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item);
			}
			int cdmillis = spamtime * 1000;
			if (System.currentTimeMillis() - playersCooldownList >= cdmillis) {} else { return false; }
		}
		return true;
	}

}
