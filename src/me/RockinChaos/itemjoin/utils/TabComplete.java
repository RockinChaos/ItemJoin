package me.RockinChaos.itemjoin.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PermissionsHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class TabComplete implements TabCompleter {
	
	@Override
    public List < String > onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    	final List < String > completions = new ArrayList < > ();
    	final List < String > commands = new ArrayList < > ();
    	Collection < ? > playersOnlineNew = null;
    	Player[] playersOnlineOld;
    	if (args.length == 2 && args[0].equalsIgnoreCase("help") && PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) {
    		commands.add("2");
    		commands.add("3");
    		commands.add("4");
    		commands.add("5");
    		commands.add("6");
    		commands.add("7");
    		commands.add("8");
    		commands.add("9");
    	} else if (args.length == 2 && args[0].equalsIgnoreCase("permissions") && PermissionsHandler.hasCommandPermission(sender, "itemjoin.permissions")) {
    		commands.add("2");
    	} else if ((args.length == 2 || args.length == 3) && args[0].equalsIgnoreCase("purge") && PermissionsHandler.hasCommandPermission(sender, "itemjoin.purge")) {
    		if (args.length == 2) {
    			commands.add("first-world");
    			commands.add("first-join");
    			commands.add("ip-limits");
    		} else {
    			if (args[1].equalsIgnoreCase("first-world")) {
    				for (String playerValue: ConfigHandler.getSQLData().getFirstWorlds().keySet()) {
    					commands.add(PlayerHandler.getPlayerString(playerValue).getName());
    				}
    			} else if (args[1].equalsIgnoreCase("first-join")) {
    				for (String playerValue: ConfigHandler.getSQLData().getFirstPlayers().keySet()) {
    					commands.add(PlayerHandler.getPlayerString(playerValue).getName());
    				}
    			} else if (args[1].equalsIgnoreCase("ip-limits")) {
    				for (String playerValue: ConfigHandler.getSQLData().getLimitPlayers().keySet()) {
    					commands.add(PlayerHandler.getPlayerString(playerValue).getName());
    				}
    			}
    		}
    	} else if ((args.length == 2 || args.length == 3) && (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("enable"))) {
    		if (args.length == 2 && ((PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable.others") && args[0].equalsIgnoreCase("enable")) || (PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable.others") && args[0].equalsIgnoreCase("disable")))) {
    			try {
    				if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    					if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    						playersOnlineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    						for (Object objPlayer: playersOnlineNew) {
    							commands.add(((Player) objPlayer).getName());
    						}
    					}
    				} else {
    					playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    					for (Player player: playersOnlineOld) {
    						commands.add(player.getName());
    					}
    				}
    			} catch (Exception e) {
    				ServerHandler.sendDebugTrace(e);
    			}
    		} else {
    			for (World world: Bukkit.getServer().getWorlds()) {
    				commands.add(world.getName());
    			}
    		}
    	} else if ((args.length == 2 || args.length == 3 || args.length == 4) && (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("getOnline") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("removeOnline"))) {
    		if (args.length == 2) {
    			for (ItemMap itemMap: ItemUtilities.getItems()) {
    				commands.add(itemMap.getConfigName());
    			}
    		} else if (args.length == 3 && ((PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others") && (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("getOnline"))) || (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others") && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("removeOnline"))))) {
    			commands.add("2");
    			commands.add("4");
    			commands.add("8");
    			commands.add("16");
    			if (!args[0].equalsIgnoreCase("getOnline") && !args[0].equalsIgnoreCase("removeOnline")) {
    				try {
    					if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    						if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    							playersOnlineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    							for (Object objPlayer: playersOnlineNew) {
    								commands.add(((Player) objPlayer).getName());
    							}
    						}
    					} else {
    						playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    						for (Player player: playersOnlineOld) {
    							commands.add(player.getName());
    						}
    					}
    				} catch (Exception e) {
    					ServerHandler.sendDebugTrace(e);
    				}
    			}
    		} else if (args.length == 4 && !Utils.isInt(args[2]) && !args[0].equalsIgnoreCase("getOnline") && !args[0].equalsIgnoreCase("removeOnline")  && ((PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others") && args[0].equalsIgnoreCase("get")) || (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others") && args[0].equalsIgnoreCase("remove")))) {
    			commands.add("2");
    			commands.add("3");
    			commands.add("4");
    			commands.add("6");
    			commands.add("8");
    			commands.add("16");
    			commands.add("32");
    			commands.add("64");
    		}
    	} else if (args.length == 2 && (args[0].equalsIgnoreCase("getAll") && PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others") || args[0].equalsIgnoreCase("removeAll") && PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others"))) {
    		try {
    			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    				if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    					playersOnlineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    					for (Object objPlayer: playersOnlineNew) {
    						commands.add(((Player) objPlayer).getName());
    					}
    				}
    			} else {
    				playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    				for (Player player: playersOnlineOld) {
    					commands.add(player.getName());
    				}
    			}
    		} catch (Exception e) {
    			ServerHandler.sendDebugTrace(e);
    		}
    	} else if (args.length == 1) {
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.use")) { commands.add("help"); commands.add("info"); commands.add("world");}
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.permissions")) { commands.add("permissions"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.purge")) { commands.add("purge"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.enable")) { commands.add("enable"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.disable")) { commands.add("disable"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get")) { commands.add("get"); commands.add("getAll"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.get.others")) { commands.add("getOnline"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove")) { commands.add("remove"); commands.add("removeAll"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.remove.others")) { commands.add("removeOnline"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.reload")) { commands.add("reload"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.creator") || PermissionsHandler.hasCommandPermission(sender, "itemjoin.menu")) { commands.add("menu"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.list")) { commands.add("list"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.updates")) { commands.add("updates"); }
    		if (PermissionsHandler.hasCommandPermission(sender, "itemjoin.autoupdate")) { commands.add("autoupdate"); }
    	}
    	StringUtil.copyPartialMatches(args[(args.length - 1)], commands, completions);
    	Collections.sort(completions);
    	return completions;
    }
}