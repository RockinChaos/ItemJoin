package me.RockinChaos.itemjoin.giveitems.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.MemoryHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class PlayerJoin implements Listener {
	
	private static String enabledCommandWorlds;
	private static List<String> globalCommands;
	private static boolean globalCommandsEnabled = false;
	
	@EventHandler
	private void giveOnJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (MemoryHandler.isAuthMe() == true) { setAuthenticating(player); } 
		else { setItems(player); }
	}
	
	private void setAuthenticating(final Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (MemoryHandler.isAuthMe() == true && fr.xephi.authme.api.v3.AuthMeApi.getInstance().isAuthenticated(player)) {
					setItems(player);
					this.cancel();
				}
			}
		}.runTaskTimer(ItemJoin.getInstance(), 0, 20);
	}
	
	private void setItems(final Player player) {
		runGlobalCmds(player);
		ItemUtilities.safeSet(player, "Join");
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			@Override
			public void run() {
				ItemUtilities.updateItems(player, false);
				String Probable = ItemUtilities.getProbabilityItem(player);
				final int session = Utils.getRandom(1, 100000);
				for (ItemMap item : ItemUtilities.getItems()) {
					if (item.isGiveOnJoin() && item.inWorld(player.getWorld()) 
							&& ItemUtilities.isChosenProbability(item, Probable) && MemoryHandler.getSQLData().isEnabled(player)
							&& item.hasPermission(player) && ItemUtilities.isObtainable(player, item, session)) {
							item.giveTo(player, false, 0);
					}
					item.setAnimations(player);
				}
				ItemUtilities.sendFailCount(player, session);
				PlayerHandler.delayUpdateInventory(player, 15L);
			}
		}, ConfigHandler.getItemDelay());
	}
	
	private void runGlobalCmds(Player player) {
		if (globalCommandsEnabled && inCommandsWorld(player.getWorld().getName(), "enabled-worlds") && globalCommands != null) {
			for (String Command: globalCommands) {
				String command = Utils.translateLayout(Command, player).replace("first-join: ", "").replace("first-join:", "");
				if (!MemoryHandler.getSQLData().hasFirstCommanded(player, command)) {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
					if (Utils.containsIgnoreCase(Command, "first-join:")) {
						MemoryHandler.getSQLData().saveFirstCommandData(player, command);
					}
				}
			}
		}
	}
	
	private boolean inCommandsWorld(String world, String stringLoc) {
		if (enabledCommandWorlds != null) {
			String[] compareWorlds = enabledCommandWorlds.split(",");
			for (String compareWorld: compareWorlds) {
				if (compareWorld.equalsIgnoreCase(world) || compareWorld.equalsIgnoreCase("ALL") || compareWorld.equalsIgnoreCase("GLOBAL")) {
					return true;
				}
			}
		} else if (enabledCommandWorlds == null) {
			return true;
		}
		return false;
	}
	
	public static void setRunCommands() {
		if (!ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("DISABLED") || !ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds").equalsIgnoreCase("FALSE")) {
			enabledCommandWorlds = ConfigHandler.getConfig("config.yml").getString("Active-Commands.enabled-worlds").replace(" ", "");
			globalCommands = ConfigHandler.getConfig("config.yml").getStringList("Active-Commands.commands");
			globalCommandsEnabled = true;
		}
	}
}