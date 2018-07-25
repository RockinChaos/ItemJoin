package me.RockinChaos.itemjoin.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import me.RockinChaos.itemjoin.utils.sqlite.SQLData;

public class CommandHandler {
	private static Map < String, Long > playersOnCooldown = new HashMap < String, Long > ();
	private static HashMap < String, Long > storedSpammedPlayers = new HashMap < String, Long > ();
	public static HashMap < String, ArrayList < String > > filteredCommands = new HashMap < String, ArrayList < String > > ();
	public static HashMap < String, Boolean > isActive = new HashMap < String, Boolean > ();
	private static int cdtime = 0;
	private static int spamtime = 1;
	private static Type CmdType = Type.DEFAULT;
	
	public static void chargePlayer(ConfigurationSection items, String item, Player player, String action) {
		if (isActive.get(PlayerHandler.getPlayerID(player)) != null && isActive.get(PlayerHandler.getPlayerID(player)) != true 
				|| isActive.get(PlayerHandler.getPlayerID(player)) == null) {
			isActive.put(PlayerHandler.getPlayerID(player), true);
			if (isChargeable(items) && chargeCost(items, item, player)) {
				InitializeCommands(items, item, player, action);
			} else if (!isChargeable(items)) {
				InitializeCommands(items, item, player, action);
			}
		}
	}
	
	public static boolean isChargeable(ConfigurationSection items) {
		if (items.getString(".commands-cost") != null && Econ.isVaultAPI() && Hooks.hasVault() == true) { return true; }
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
			if (item.getAmount() > 1 && item.getAmount() != 1) { item.setAmount(item.getAmount() - 1); } 
			else { PlayerHandler.setItemInHand(player, Material.AIR); }
		}
	}
	
	public static void InitializeCommands(ConfigurationSection items, final String item, final Player player, String action) {
		List < String > commandList = items.getStringList(".commands" + getClickType(items, action));
		playSound(items, player);
		long delay = 0;
		String sequence = items.getString(".commands-sequence");
		ArrayList < String > sequencialCommands = new ArrayList < > ();
		for (String command: commandList) {
			String splicedCommand = hitPlayer(fetchCommand(command, player), player);
			Type cmdtype = CmdType;
			delay = fetchDelay(splicedCommand);
			if (sequence != null && ItemHandler.containsIgnoreCase(sequence, "RANDOM")) {
				sequencialCommands.add(command);
			} else if (sequence == null || ItemHandler.containsIgnoreCase(sequence, "ALL") || ItemHandler.containsIgnoreCase(sequence, "SEQUENTIAL")) {
				sendDispatch(delay, cmdtype, splicedCommand, item, player);
			}
		}
		if (sequence != null && ItemHandler.containsIgnoreCase(sequence, "RANDOM")) {
			Random randomGenerator = new Random();
			int index = randomGenerator.nextInt(sequencialCommands.size());
			String splicedCommand = hitPlayer(fetchCommand(sequencialCommands.get(index), player), player);
			Type cmdtype = CmdType;
			delay = fetchDelay(splicedCommand);
			sendDispatch(delay, cmdtype, splicedCommand, item, player);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), (Runnable) new Runnable() {
			public void run() {
				isActive.put(PlayerHandler.getPlayerID(player), false);
			}
		}, 1L);
	}
	
	public static void sendDispatch(long delay, final Type cmdtype, final String command, final String item, final Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), (Runnable) new Runnable() {
			public void run() {
				switch (cmdtype) {
					case CONSOLE: dispatchConsoleCommands(player, command, item); break;
					case OP: dispatchOpCommands(player, command, item); break;
					case PLAYER: dispatchPlayerCommands(player, command, item); break;
					case MESSAGE: dispatchMessageCommands(player, command, item); break;
					case SERVERSWITCH: dispatchServerSwitchCommands(player, command, item); break;
					case BUNGEE: dispatchBungeeCordCommands(player, command, item); break;
					case DEFAULT: dispatchPlayerCommands(player, command, item); break;
					default: dispatchPlayerCommands(player, command, item); break;
				}
			}
		}, delay);
	}
	
	public static String fetchCommand(String cmdline, Player player) {
		if ((cmdline == null) || (cmdline.length() == 0)) { return ""; }
		
		cmdline = cmdline.trim();
		CmdType = Type.DEFAULT;
		
		if (cmdline.startsWith("console:")) { cmdline = cmdline.substring(8); CmdType = Type.CONSOLE; } 
		else if (cmdline.startsWith("op:")) { cmdline = cmdline.substring(3); CmdType = Type.OP; } 
		else if (cmdline.startsWith("player:")) { cmdline = cmdline.substring(7); CmdType = Type.PLAYER; } 
		else if (cmdline.startsWith("server:")) { cmdline = cmdline.substring(13); CmdType = Type.SERVERSWITCH; } 
		else if (cmdline.startsWith("bungee:")) { cmdline = cmdline.substring(7); CmdType = Type.BUNGEE; } 
		else if (cmdline.startsWith("message:")) { cmdline = cmdline.substring(8); CmdType = Type.MESSAGE; } 
		else if (cmdline.startsWith("delay:")) { cmdline = cmdline.substring(6); CmdType = Type.DELAY; }
		
		cmdline = cmdline.trim();
		cmdline = Utils.format(cmdline, player);
		
		return cmdline;
	}
	
	public static int fetchDelay(String lDelay) {
		if (CmdType == Type.DELAY) {
			try {
				if (Utils.isInt(lDelay)) { return Integer.parseInt(lDelay); }
			} catch (Exception e) {
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			}
		}
		return 0;
	}
	
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
	
	public static String hitPlayer(String returnedCommand, Player player) {
		if (ItemHandler.containsIgnoreCase(returnedCommand, "%hitplayer%") && ServerHandler.hasAltUpdate("1_8")) {
			Entity entityHit = getNearestEntityInSight(player, 4);
			if (entityHit != null && entityHit instanceof Player) {
				Player hitPlayer = (Player) entityHit;
				return returnedCommand.replace("%hitplayer%", hitPlayer.getName());
			}
		}
		return returnedCommand;
	}
	
	public static String getClickType(ConfigurationSection items, String action) {
		String commandType = items.getString(".commands-type");
		if (ConfigHandler.getCommandsSection(items) != null) {
			Iterator < String > it = ConfigHandler.getCommandsSection(items).getKeys(false).iterator();
			while (it.hasNext()) {
				String definition = it.next();
				if (ItemHandler.containsIgnoreCase(commandType, "inventory") && CommandsType.INVENTORY.hasAction(action)) {
					if (ActionType.INVENTORY.hasAction(action) && ActionType.INVENTORY.hasDefine(definition)) {
						return ActionType.INVENTORY.definition;
					} else if (ActionType.MULTI_CLICK_INVENTORY.hasAction(action) && ActionType.MULTI_CLICK_INVENTORY.hasDefine(definition)) {
						return ActionType.MULTI_CLICK_INVENTORY.definition;
					}
				} else if (ItemHandler.containsIgnoreCase(commandType, "interact") && CommandsType.INTERACT.hasAction(action) || CommandsType.INTERACT.hasAction(action)) {
					if (ActionType.LEFT_CLICK_ALL.hasAction(action) && ActionType.LEFT_CLICK_ALL.hasDefine(definition)) {
						return ActionType.LEFT_CLICK_ALL.definition;
					} else if (ActionType.LEFT_CLICK_AIR.hasAction(action) && ActionType.LEFT_CLICK_AIR.hasDefine(definition)) {
						return ActionType.LEFT_CLICK_AIR.definition;
					} else if (ActionType.LEFT_CLICK_BLOCK.hasAction(action) && ActionType.LEFT_CLICK_BLOCK.hasDefine(definition)) {
						return ActionType.LEFT_CLICK_BLOCK.definition;
					} else if (ActionType.RIGHT_CLICK_ALL.hasAction(action) && ActionType.RIGHT_CLICK_ALL.hasDefine(definition)) {
						return ActionType.RIGHT_CLICK_ALL.definition;
					} else if (ActionType.RIGHT_CLICK_AIR.hasAction(action) && ActionType.RIGHT_CLICK_AIR.hasDefine(definition)) {
						return ActionType.RIGHT_CLICK_AIR.definition;
					} else if (ActionType.RIGHT_CLICK_BLOCK.hasAction(action) && ActionType.RIGHT_CLICK_BLOCK.hasDefine(definition)) {
						return ActionType.RIGHT_CLICK_BLOCK.definition;
					} else if (ActionType.MULTI_CLICK_ALL.hasAction(action) && ActionType.MULTI_CLICK_ALL.hasDefine(definition)) {
						return ActionType.MULTI_CLICK_ALL.definition;
					} else if (ActionType.MULTI_CLICK_AIR.hasAction(action) && ActionType.MULTI_CLICK_AIR.hasDefine(definition)) {
						return ActionType.MULTI_CLICK_AIR.definition;
					} else if (ActionType.MULTI_CLICK_BLOCK.hasAction(action) && ActionType.MULTI_CLICK_BLOCK.hasDefine(definition)) {
						return ActionType.MULTI_CLICK_BLOCK.definition;
					} else if (ActionType.PHYSICAL.hasAction(action) && ActionType.PHYSICAL.hasDefine(definition)) {
						return ActionType.PHYSICAL.definition;
					}
				}
			}
		}
		return "";
	}
	
	public static void filterCommands(Player player, String stuff) {
		if (ConfigHandler.getConfig("config.yml").getString("Log-Commands") != null && ConfigHandler.getConfig("config.yml").getBoolean("Log-Commands") == false) {
			ArrayList < String > templist = new ArrayList < String > ();
			if (filteredCommands.get("commands-list") != null && !filteredCommands.get("commands-list").contains(stuff)) {
				templist = filteredCommands.get("commands-list");
			}
			templist.add(stuff);
			filteredCommands.put("commands-list", templist);
			((Logger) LogManager.getRootLogger()).addFilter(new CustomFilter());
		}
	}
	
	public static void dispatchConsoleCommands(Player player, String returnedCommand, String item) {
		try {
			String Command = Utils.format(returnedCommand, player);
			filterCommands(player, "/" + Command);
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
			playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
		} catch (Exception e) {
			ServerHandler.sendConsoleMessage("&cThere was an issue executing an item's command as console, if this continues please report it to the developer!");
			ServerHandler.sendConsoleMessage("&cError Code to Report: &c&l(CTC435-CONSOLE)");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
	}
	
	public static void dispatchOpCommands(Player player, String returnedCommand, String item) { // lookat
		try {
			boolean isOp = player.isOp();
			try {
				player.setOp(true);
				String Command = Utils.format(returnedCommand, player);
				filterCommands(player, "/" + Command);
				player.chat("/" + Command);
			} catch (Exception e) {
				if (ServerHandler.hasDebuggingMode()) {
					e.printStackTrace();
				}
				player.setOp(isOp);
				ServerHandler.sendConsoleMessage("&cAn error has occurred while removing " + player.getName() + " from the OPs list. OP or not OP they were removed from OPs list!");
			} finally {
				player.setOp(isOp);
				playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
			}
		} catch (Exception e) {
			ServerHandler.sendConsoleMessage("&cThere was an issue executing an item's command as an op, if this continues please report it to the developer!");
			ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC434-OP)");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
	}
	
	public static void dispatchPlayerCommands(Player player, String returnedCommand, String item) {
		try {
			String Command = Utils.format(returnedCommand, player);
			filterCommands(player, "/" + Command);
			player.chat("/" + Command);
			playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
		} catch (Exception e) {
			ServerHandler.sendConsoleMessage("&cThere was an issue executing an item's command as a player, if this continues please report it to the developer!");
			ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC433-PLAYER)");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
	}
	
	public static void dispatchMessageCommands(Player player, String returnedCommand, String item) {
		try {
			String Command = Utils.format(returnedCommand, player);
			player.sendMessage(Command);
			playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
		} catch (Exception e) {
			ServerHandler.sendConsoleMessage("&cThere was an issue executing an item's command to send a message, if this continues please report it to the developer!");
			ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC432-MESSAGES)");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
	}
	
	public static void dispatchServerSwitchCommands(Player player, String returnedCommand, String item) {
		try {
			String Command = Utils.format(returnedCommand, player);
			BungeeCord.SwitchServers(player, Command);
			playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
		} catch (Exception e) {
			ServerHandler.sendConsoleMessage("&cThere was an issue executing an item's command to switch servers, if this continues please report it to the developer!");
			ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC431-SERVERSWITCH)");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
	}
	
	public static void dispatchBungeeCordCommands(Player player, String returnedCommand, String item) {
		try {
			String Command = Utils.format(returnedCommand, player);
			//player.sendMessage("yeet1");
			//player.performCommand("/glist");
			//Bukkit.getServer().dispatchCommand(player, "glist");
			//	ItemJoin.getInstance().getPluginManager().dispatchCommand((CommandSender) player, "/glist");
			BungeeCord.ExecuteCommand(player, Command);
			//ProxyServer.getInstance().getPluginManager().dispatchCommand((CommandSender) player, "glist");
			playersOnCooldown.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
		} catch (Exception e) {
			ServerHandler.sendConsoleMessage("&cThere was an issue executing an item's command to BungeeCord, if this continues please report it to the developer!");
			ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC430-BUNGEECORD)");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
	}
	
	public static void playSound(ConfigurationSection items, Player player) {
		if (items.getString(".commands-sound") != null) {
			try {
				player.playSound(player.getLocation(), Sound.valueOf(items.getString(".commands-sound")), 1, 1);
			} catch (Exception e) {
				String pkgname = ItemJoin.getInstance().getServer().getClass().getPackage().getName();
				String vers = pkgname.substring(pkgname.lastIndexOf('.') + 1);
				ServerHandler.sendConsoleMessage("&cThere was an issue executing the commands-sound you defined.");
				ServerHandler.sendConsoleMessage("&c" + items.getString(".commands-sound") + "&c is not a sound in " + vers + ".");
				ServerHandler.sendConsoleMessage("&cError Code: &c&l(CTC439-SOUNDS)");
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			}
		}
	}
	
	public static boolean spamTask(Player player, String item) {
		boolean itemsSpam = ConfigHandler.getConfig("items.yml").getBoolean("items-Spamming");
		if (itemsSpam != true) {
			long playersCooldownList = 0L;
			if (storedSpammedPlayers.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item)) {
				playersCooldownList = storedSpammedPlayers.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item);
			}
			int cdmillis = spamtime * 1000;
			if (System.currentTimeMillis() - playersCooldownList >= cdmillis) {} else {
				return false;
			}
		}
		return true;
	}
	
	public static boolean onCooldown(ConfigurationSection items, Player player, String item, ItemStack item1) {
		long playersCooldownList = 0L;
		if (playersOnCooldown.containsKey(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item)) {
			playersCooldownList = playersOnCooldown.get(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item);
		}
		cdtime = items.getInt(".commands-cooldown");
		int cdmillis = cdtime * 1000;
		if (System.currentTimeMillis() - playersCooldownList >= cdmillis) {
			return false;
		} else {
			if (items.getString(".cooldown-message") != null) {
				if (spamTask(player, item)) {
					storedSpammedPlayers.put(player.getWorld().getName() + "." + PlayerHandler.getPlayerID(player) + ".items." + item, System.currentTimeMillis());
					int timeLeft = (int)(cdtime - ((System.currentTimeMillis() - playersCooldownList) / 1000));
					String inhand = items.getString(".name");
					String cooldownmsg = (items.getString(".cooldown-message").replace("%timeleft%", String.valueOf(timeLeft)).replace("%item%", inhand).replace("%itemraw%", ItemHandler.getName(item1)));
					cooldownmsg = Utils.format(cooldownmsg, player);
					player.sendMessage(cooldownmsg);
				}
			}
		}
		return true;
	}
	
	public static boolean isCommandable(String action, ConfigurationSection items) {
		String commandType = items.getString(".commands-type");
		if (ItemHandler.containsIgnoreCase(commandType, "inventory") && CommandsType.INVENTORY.hasAction(action)) {
			return true;
		} else if (ItemHandler.containsIgnoreCase(commandType, "interact") && CommandsType.INTERACT.hasAction(action)) {
			return true;
		} else if (CommandsType.INTERACT.hasAction(action)) {
			return true;
		}
		return false;
	}
	
	public static void runGlobalCmds(Player player) {
		if (ConfigHandler.getConfig("config.yml").getBoolean("enabled-global-commands") == true && WorldHandler.inGlobalWorld(player.getWorld().getName(), "enabled-worlds")) {
			if (ConfigHandler.getConfig("config.yml").getStringList("global-commands") != null) {
				List < String > commands = ConfigHandler.getConfig("config.yml").getStringList("global-commands");
				for (String command: commands) {
					if (!SQLData.hasFirstCommanded(player, command)) {
						String Command = Utils.format(command, player).replace("first-join: ", "").replace("first-join:", "");
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command);
						if (ItemHandler.containsIgnoreCase(command, "first-join:")) {
							SQLData.saveToDatabase(player, "NULL", command, "");
						}
					}
				}
			}
		}
	}
	
	public static enum Type { DEFAULT, CONSOLE, OP, PLAYER, SERVERSWITCH, MESSAGE, BUNGEE, DELAY; }
	
	public enum ActionType {
		DEFAULT("", ""),
		PHYSICAL("PHYSICAL", ".physical"),
		INVENTORY("PICKUP_ALL, PICKUP_HALF, PLACE_ALL", ".inventory"),
		MULTI_CLICK_INVENTORY("PICKUP_ALL, PICKUP_HALF, PLACE_ALL", ".multi-click"),
		MULTI_CLICK_ALL("LEFT_CLICK_BLOCK, LEFT_CLICK_AIR, RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR", ".multi-click"),
		MULTI_CLICK_AIR("LEFT_CLICK_AIR, RIGHT_CLICK_AIR", ".multi-click-air"),
		MULTI_CLICK_BLOCK("LEFT_CLICK_BLOCK, RIGHT_CLICK_BLOCK", ".multi-click-block"),
		LEFT_CLICK_ALL("LEFT_CLICK_AIR, LEFT_CLICK_BLOCK", ".left-click"),
		LEFT_CLICK_AIR("LEFT_CLICK_AIR", ".left-click-air"),
		LEFT_CLICK_BLOCK("LEFT_CLICK_BLOCK", ".left-click-block"),
		RIGHT_CLICK_ALL("RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK", ".right-click"),
		RIGHT_CLICK_AIR("RIGHT_CLICK_AIR", ".right-click-air"),
		RIGHT_CLICK_BLOCK("RIGHT_CLICK_BLOCK", ".right-click-block");
		
		private final String name;
		private final String definition;
		private ActionType(String Action, String Definition) {
			name = Action;
			definition = Definition;
		}
		
		public boolean hasAction(String Action) { return name.contains(Action); }
		public boolean hasDefine(String Define) { return definition.contains(Define); }
	}
	
	public enum CommandsType {
		INTERACT("PHYSICAL, LEFT_CLICK_BLOCK, LEFT_CLICK_AIR, RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR"),
		INVENTORY("PICKUP_ALL, PICKUP_HALF, PLACE_ALL");
		private final String name;
		private CommandsType(String Action) { name = Action; }
		public boolean hasAction(String Action) { return name.contains(Action); }
	}
	
}