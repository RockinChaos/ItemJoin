package me.RockinChaos.itemjoin.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.utils.BungeeCord;
import me.RockinChaos.itemjoin.utils.CustomFilter;
import me.RockinChaos.itemjoin.utils.Econ;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Language;
import me.RockinChaos.itemjoin.utils.Utils;

public class CommandHandler {
	private static Map < String, Long > playersOnCooldown = new HashMap < String, Long > ();
	private static HashMap < String, Long > storedSpammedPlayers = new HashMap < String, Long > ();
	public static HashMap < String, ArrayList<String> > filteredCommands = new HashMap < String, ArrayList<String> > ();
	private static int cdtime = 0;
	private static int spamtime = 1;

	public static Entity getNearestEntityInSight(Player player, int range) {
		ArrayList < Entity > entities = (ArrayList < Entity > ) player.getNearbyEntities(range, range, range);
		ArrayList < Block > sightBlock = (ArrayList < Block > ) player.getLineOfSight((Set < Material > ) null, range);
		ArrayList < Location > sight = new ArrayList < Location > ();
		for (int i = 0; i < sightBlock.size(); i++) sight.add(sightBlock.get(i).getLocation());
		for (int i = 0; i < sight.size(); i++) {
			for (int k = 0; k < entities.size(); k++) {
				if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3) {
					if (Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5) {
						if (Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3) {
							return entities.get(k);
						}
					}
				}
			}
		}
		return null;
	}

	public static boolean isCommandable(String action, ConfigurationSection items) {
		if (isInteractPhysical(action, items)) {
			return true;
		} else if (isInteractInv(action, items)) {
			return true;
		}
		return false;
	}

	public static void runGlobalCmds(Player player) {
		if (ConfigHandler.getConfig("config.yml").getBoolean("enabled-global-commands") == true && WorldHandler.inGlobalWorld(player.getWorld().getName())) {
			if (ConfigHandler.getConfig("config.yml").getStringList("global-commands") != null) {
			List <String> commands = ConfigHandler.getConfig("config.yml").getStringList("global-commands");
			for (String command: commands) {
				String Command = Utils.format(command, player);
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
			}
			}
		}
	}
	
	public static boolean isInteractInv(String action, ConfigurationSection items) {
		String commandType = items.getString(".commands-type");
		if (commandType != null && ItemHandler.containsIgnoreCase(commandType, "inventory")) {
			if (ItemHandler.containsIgnoreCase(".multi-click", getAction("inventory", action, items)) 
					|| ItemHandler.containsIgnoreCase(".inventory", getAction("inventory", action, items))) {
				return true;
		}
		}
		return false;
	}

	public static boolean isInteractPhysical(String action, ConfigurationSection items) {
		if (items.getString(".commands-type") == null || ItemHandler.containsIgnoreCase(items.getString(".commands-type"), "interact")) {
			if (ItemHandler.containsIgnoreCase(".multi-click", getAction("interact", action, items)) 
					|| ItemHandler.containsIgnoreCase(".left-click", getAction("interact", action, items))
					|| ItemHandler.containsIgnoreCase(".right-click", getAction("interact", action, items)) 
					|| ItemHandler.containsIgnoreCase(".multi-click-air", getAction("interact", action, items)) 
					|| ItemHandler.containsIgnoreCase(".left-click-air", getAction("interact", action, items))
					|| ItemHandler.containsIgnoreCase(".right-click-air", getAction("interact", action, items))
					|| ItemHandler.containsIgnoreCase(".physical", getAction("interact", action, items))) {
				return true;
			}
		}
		return false;
	}
	
	public static String getMode(String action) {
		if (ItemHandler.containsIgnoreCase("LEFT_CLICK_AIR", action) 
				|| ItemHandler.containsIgnoreCase("LEFT_CLICK_BLOCK", action) 
				|| ItemHandler.containsIgnoreCase("RIGHT_CLICK_AIR", action) 
				|| ItemHandler.containsIgnoreCase("RIGHT_CLICK_BLOCK", action)
				|| ItemHandler.containsIgnoreCase("PHYSICAL", action)) {
			return "interact";
		} else if (ItemHandler.containsIgnoreCase("PICKUP_ALL", action) 
		|| ItemHandler.containsIgnoreCase("PICKUP_HALF", action) 
		|| ItemHandler.containsIgnoreCase("PLACE_ALL", action)) {
			return "inventory";
		}
		return action;
	}
	
	public static String getAction(String mode, String action, ConfigurationSection items) {
		List<String> actions = items.getStringList(".commands" + ".multi-click");
		if (actions != null && actions.toString() != "[]") {
			if (ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("LEFT_CLICK_AIR", action) 
					|| ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("LEFT_CLICK_BLOCK", action) 
					|| ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("RIGHT_CLICK_AIR", action) 
					|| ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("RIGHT_CLICK_BLOCK", action)) {
			return ".multi-click";
		}
		}
		actions = items.getStringList(".commands" + ".left-click");
		if (actions != null && actions.toString() != "[]") {
			if (ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("LEFT_CLICK_AIR", action) 
					|| ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("LEFT_CLICK_BLOCK", action)) {
			return ".left-click";
		}
		}
        actions = items.getStringList(".commands" + ".right-click");
		if (actions != null && actions.toString() != "[]") {
			if (ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("RIGHT_CLICK_AIR", action) 
					|| ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("RIGHT_CLICK_BLOCK", action)) {
			return ".right-click";
		}
		}
		actions = items.getStringList(".commands" + ".multi-click-air");
		if (actions != null && actions.toString() != "[]") {
			if (ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("LEFT_CLICK_AIR", action) 
					||ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("RIGHT_CLICK_AIR", action)) {
			return ".multi-click-air";
		}
		}
		actions = items.getStringList(".commands" + ".left-click-air");
		if (actions != null && actions.toString() != "[]") {
			if (ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("LEFT_CLICK_AIR", action)) {
			return ".left-click-air";
		}
		}
        actions = items.getStringList(".commands" + ".right-click-air");
		if (actions != null && actions.toString() != "[]") {
			if (ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("RIGHT_CLICK_AIR", action)) {
			return ".right-click-air";
		}
		}
        actions = items.getStringList(".commands" + ".inventory");
		if (actions != null && actions.toString() != "[]") {
			if (ItemHandler.containsIgnoreCase(mode, "inventory") && ItemHandler.containsIgnoreCase("PICKUP_ALL", action) 
					|| ItemHandler.containsIgnoreCase(mode, "inventory") && ItemHandler.containsIgnoreCase("PICKUP_HALF", action) 
					|| ItemHandler.containsIgnoreCase(mode, "inventory") && ItemHandler.containsIgnoreCase("PLACE_ALL", action)) {
			return ".inventory";
		}
		}
        actions = items.getStringList(".commands" + ".multi-click");
		if (actions != null && actions.toString() != "[]") {
			if (ItemHandler.containsIgnoreCase(mode, "inventory") && ItemHandler.containsIgnoreCase("PICKUP_ALL", action) 
					|| ItemHandler.containsIgnoreCase(mode, "inventory") && ItemHandler.containsIgnoreCase("PICKUP_HALF", action) 
					|| ItemHandler.containsIgnoreCase(mode, "inventory") && ItemHandler.containsIgnoreCase("PLACE_ALL", action)) {
			return ".multi-click";
		}
		}
        actions = items.getStringList(".commands" + ".physical");
		if (actions != null && actions.toString() != "[]") {
			if (ItemHandler.containsIgnoreCase(mode, "interact") && ItemHandler.containsIgnoreCase("PHYSICAL", action)) {
			return ".physical";
		}
		}
		return "null";
	}

	public static boolean spamTask(final Player player, String item) {
		boolean itemsSpam = ConfigHandler.getConfig("items.yml").getBoolean("items-Spamming");
		if (itemsSpam != true) {
			long playersCooldownList = 0L;
			if (CommandHandler.storedSpammedPlayers.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item)) {
				playersCooldownList = CommandHandler.storedSpammedPlayers.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item);
			}
			int cdmillis = CommandHandler.spamtime * 1000;
			if (System.currentTimeMillis() - playersCooldownList >= cdmillis) {} else {
				return false;
			}
		}
		return true;
	}

	public static boolean onCooldown(ConfigurationSection items, Player player, String item, ItemStack item1) {
		long playersCooldownList = 0L;
		if (CommandHandler.playersOnCooldown.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item)) {
			playersCooldownList = CommandHandler.playersOnCooldown.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item);
		}
		CommandHandler.cdtime = items.getInt(".commands-cooldown");
		int cdmillis = CommandHandler.cdtime * 1000;
		if (System.currentTimeMillis() - playersCooldownList >= cdmillis) {
			return false;
		} else {
			if (items.getString(".cooldown-message") != null) {
				spamTask(player, item);
				if (spamTask(player, item)) {
					storedSpammedPlayers.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
					int timeLeft = (int)(CommandHandler.cdtime - ((System.currentTimeMillis() - playersCooldownList) / 1000));
					String inhand = items.getString(".name");
					String cooldownmsg = (items.getString(".cooldown-message").replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", inhand).replace("%itemraw%", ItemHandler.getName(item1)));
					cooldownmsg = Utils.format(cooldownmsg, player);
					player.sendMessage(cooldownmsg);
				}
			}
		}
		return true;
	}

	public static void chargePlayer(ConfigurationSection items, String item, Player player, String action) {
		if (isChargeable(items) && chargeCost(items, item, player)) {
			convertCommands(items, item, player, action);
		} else if (!isChargeable(items)) {
			convertCommands(items, item, player, action);
		}
	}

	public static boolean isChargeable(ConfigurationSection items) {
		if (items.getString(".commands-cost") != null && Econ.isVaultAPI() && Hooks.hasVault() == true) {
			return true;
		}
		return false;
	}

	public static boolean chargeCost(ConfigurationSection items, String item, Player player) {
		if (items.getString(".commands-cost") != null && Utils.isInt(items.getString(".commands-cost"))) {
			int cost = items.getInt(".commands-cost");
		    double balance = 0.0;
		    try {
		    	balance = PlayerHandler.getBalance(player);
		    } catch (NullPointerException e) {
		    	if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		    }
			if (balance >= cost) {
				if (cost != 0) {
				try {
				PlayerHandler.withdrawBalance(player, cost);
				} catch (NullPointerException e) {
					if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
				}
				Language.getSendMessage(player, "itemChargeSuccess", "" + items.getString(".commands-cost"));
				}
				return true;
			} else if (!(balance >= cost)) {
				Language.getSendMessage(player, "itemChargeFailed", items.getString(".commands-cost") + ", " + balance);
				return false;
			}
		}
		return false;
	}

	public static void removeDisposable(ConfigurationSection items, ItemStack item, Player player) {
		String ItemFlags = items.getString(".itemflags");
		if (ItemHandler.containsIgnoreCase(ItemFlags, "disposable")) {
			if (item.getAmount() > 1 && item.getAmount() != 1) {
				item.setAmount(item.getAmount() - 1);
			} else {
				PlayerHandler.setItemInHand(player, Material.AIR);
			}
		}
	}

	public static String returnIdentifier(String Identify) {
		if (ItemHandler.containsIgnoreCase(Identify, "console:")) {
			return "console:";
		} else if (ItemHandler.containsIgnoreCase(Identify, "op:")) {
			return "op:";
		} else if (ItemHandler.containsIgnoreCase(Identify, "player:")) {
			return "player:";
		} else if (ItemHandler.containsIgnoreCase(Identify, "message:")) {
			return "message:";
		} else if (ItemHandler.containsIgnoreCase(Identify, "server:")) {
			return "server:";
		} else if (ItemHandler.containsIgnoreCase(Identify, "delay:")) {
			return "delay:";
		}
		return "Error Code: (CTC4301)";
	}

	public static String returnCommand(String Identify) {
		if (ItemHandler.containsIgnoreCase(Identify, "console:")) {
			return Identify.replace("console: ", "").replace("console:", "");
		} else if (ItemHandler.containsIgnoreCase(Identify, "op:")) {
			return Identify.replace("op: ", "").replace("op:", "");
		} else if (ItemHandler.containsIgnoreCase(Identify, "player:")) {
			return Identify.replace("player: ", "").replace("player:", "");
		} else if (ItemHandler.containsIgnoreCase(Identify, "message:")) {
			return Identify.replace("message: ", "").replace("message:", "");
		} else if (ItemHandler.containsIgnoreCase(Identify, "server:")) {
			return Identify.replace("server: ", "").replace("server:", "");
		} else if (ItemHandler.containsIgnoreCase(Identify, "delay:")) {
			return Identify.replace("delay: ", "").replace("delay:", "");
		}
		return "Error Code: (CTC4300)";
	}

	public static String hitPlayer(String returnedCommand, Player player) {
		if (ItemHandler.containsIgnoreCase(returnedCommand, "%hitplayer%") && ServerHandler.hasChangedTheWorldUpdate()) {
			Entity entityHit = getNearestEntityInSight(player, 4);
			if (entityHit != null && entityHit instanceof Player) {
				Player hitPlayer = (Player) entityHit;
				return returnedCommand.replace("%hitplayer%", hitPlayer.getName());
			}
		}
		return returnedCommand;
	}

	public static void convertCommands(ConfigurationSection items, final String item, final Player player, String action) {
		List < String > command = items.getStringList(".commands" + getAction(getMode(action), action, items));
		playSound(items, player);
		long delay = 0;
		for (final String Identify: command) {
			final String returnedIndentity = returnIdentifier(Identify);
			final String returnedCommand = hitPlayer(returnCommand(Identify), player);
			if (ItemHandler.containsIgnoreCase(returnedIndentity, "delay:")) {
				try {
					String Command = Utils.format(returnedCommand, player);
					if (Utils.isInt(Command)) {
						delay = Integer.parseInt(Command);
					}
				} catch (Exception e) {
					if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
				}
			}
			BlockCode(delay, Identify, returnedCommand, returnedIndentity, item, player);
		}
	}
	
	
	public static void BlockCode(final long delay, final String Identify, 
			final String returnedCommand, final String returnedIndentity, final String item, final Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), (Runnable) new Runnable() {
			public void run() {
				if (ItemHandler.containsIgnoreCase(returnedIndentity, "console:")) {
					try {
						dispatchConsoleCommands(returnedCommand, player, item);
					} catch (ArrayIndexOutOfBoundsException e) {
						ServerHandler.sendConsoleMessage("&cThere was an issue executing an items command as console, if this continues please report it to the developer!");
						ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC435)");
						if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
					}
				} else if (ItemHandler.containsIgnoreCase(returnedIndentity, "op:")) {
					try {
						dispatchOpCommands(returnedCommand, player, item);
					} catch (ArrayIndexOutOfBoundsException e) {
						ServerHandler.sendConsoleMessage("&cThere was an issue executing an items command as an op, if this continues please report it to the developer!");
						ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC434)");
						if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
					}
				} else if (ItemHandler.containsIgnoreCase(returnedIndentity, "player:")) {
					try {
						dispatchPlayerCommands(returnedCommand, player, item);
					} catch (ArrayIndexOutOfBoundsException e) {
						ServerHandler.sendConsoleMessage("&cThere was an issue executing an items command as a player, if this continues please report it to the developer!");
						ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC434)");
						if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
					}
				} else if (ItemHandler.containsIgnoreCase(returnedIndentity, "message:")) {
					try {
						dispatchMessageCommands(returnedCommand, player, item);
					} catch (ArrayIndexOutOfBoundsException e) {
						ServerHandler.sendConsoleMessage("&cThere was an issue executing an items command to send a message, if this continues please report it to the developer!");
						ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC433)");
						if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
					}
				} else if (ItemHandler.containsIgnoreCase(returnedIndentity, "server:")) {
					try {
						dispatchBungeeCordCommands(returnedCommand, player, item);
					} catch (ArrayIndexOutOfBoundsException e) {
						ServerHandler.sendConsoleMessage("&cThere was an issue executing an items command to switch servers, if this continues please report it to the developer!");
						ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC432)");
						if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
					}
				} else if (ItemHandler.containsIgnoreCase(returnedIndentity, "delay:")) {} else {
					try {
						dispatchPlayerCommands(Identify, player, item);
					} catch (ArrayIndexOutOfBoundsException e) {
						ServerHandler.sendConsoleMessage("&cThere was an issue executing an items command as a player, if this continues please report it to the developer!");
						ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC431)");
						if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
					}
				}
			}
		}, delay);
	}

	public static void playSound(ConfigurationSection items, Player player) {
		if (items.getString(".commands-sound") != null) {
			try {
			player.playSound(player.getLocation(), Sound.valueOf(items.getString(".commands-sound")), 1, 1);
			} catch (IllegalArgumentException e) {
				   String pkgname = ItemJoin.getInstance().getServer().getClass().getPackage().getName();
				   String vers = pkgname.substring(pkgname.lastIndexOf('.') + 1);
				ServerHandler.sendConsoleMessage("&cThere was an issue executing the commands-sound you defined.");
				ServerHandler.sendConsoleMessage("&c" + items.getString(".commands-sound") + "&c is not a sound in " + vers + ".");
				ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC431)");
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			}
		}
	}
	
	public static void filterCommands(Player player, String stuff) {
		if (ConfigHandler.getConfig("config.yml").getString("Log-Commands") != null && ConfigHandler.getConfig("config.yml").getBoolean("Log-Commands") == false) {
			ArrayList < String > templist = new ArrayList < String > ();
			if (filteredCommands.get("commands-list") != null && !filteredCommands.get("commands-list").contains(stuff)) { templist = filteredCommands.get("commands-list"); }
			templist.add(stuff);
			filteredCommands.put("commands-list", templist);
			((Logger) LogManager.getRootLogger()).addFilter(new CustomFilter());
		}
	}

	public static void dispatchMessageCommands(String returnedCommand, Player player, String item) {
		String Command = Utils.format(returnedCommand, player);
		player.sendMessage(Command);
		playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
	}

	public static void dispatchBungeeCordCommands(String returnedCommand, Player player, String item) {
		String Command = Utils.format(returnedCommand, player);
		BungeeCord.SwitchServers(player, Command);
		playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
	}

	public static void dispatchPlayerCommands(String returnedCommand, Player player, String item) {
		String Command = Utils.format(returnedCommand, player);
		filterCommands(player, "/" + Command);
		player.chat("/" + Command);
		playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
	}
	
	public static void dispatchOpCommands(String returnedCommand, Player player, String item) {
      boolean isOp = player.isOp();
		try {
		    player.setOp(true);
			String Command = Utils.format(returnedCommand, player);
			filterCommands(player, "/" + Command);
			player.chat("/" + Command);
		} catch(Exception e) {
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
	        player.setOp(isOp);
	        ServerHandler.sendConsoleMessage("&cAn error has occurred while removing " + player.getName() + " from the OPs list. OP or not OP they were removed from OPs list!");
		} finally {
			player.setOp(isOp);
			playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
		}
	}

	public static void dispatchConsoleCommands(String returnedCommand, Player player, String item) {
		String Command = Utils.format(returnedCommand, player);
		filterCommands(player, "/" + Command);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
		playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
	}
}