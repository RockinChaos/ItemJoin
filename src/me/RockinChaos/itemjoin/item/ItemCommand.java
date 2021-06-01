/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.LogHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.api.BungeeAPI;

public class ItemCommand {
	
	private long delay = 0L;
	private int cycleTask = 0;
	private String command;
	private String listSection;
	private Executor executorType;
	private Action actionType;
	private ItemMap itemMap;
	private List < Player > playerVoid = new ArrayList < Player > ();
	private List < Player > playerPending = new ArrayList < Player > ();
	
   /**
	* Creates a new ItemCommand instance.
	* 
	* @param command - the command to be executed.
	* @param action - that will recieve the items.
	* @param type - the executor of the command.
	* @param delay - the delay to wait before executing the command.
	* @param commandType - the interaction type of the command.
	* @param listSection - the section identifier of the command, used in random lists.
	*/
	private ItemCommand(final String command, final Action action, final Executor executorType, final ItemMap itemMap, final long delay, final String listSection) {
		this.command = command;
		this.actionType = action;
		this.executorType = executorType;
		this.itemMap = itemMap;
		this.delay = delay;
		this.listSection = listSection;
	}
	
   /**
	* Executes the command for the specified player.
    * 
	* @param player - player that is interacting with the custom items command.
	* @param altPlayer - that is associated with the commands.
	* @param action - the action when interacting with the item.
	* @param slot - the slot that the custom item is in.
	* @param itemMap - the ItemMap of the custom item.
	* @return If the command execute was successful.
	*/
	public boolean execute(final Player player, final Player altPlayer, final String action, final String clickType, final String slot, final ItemMap itemMap) {
		if (this.command == null || this.command.length() == 0 || !this.actionType.hasClickType(clickType) || !this.actionType.hasAction(action)) { return false; }
		if (this.actionType.equals(Action.ON_HOLD)) {
			int cooldown = itemMap.getCommandCooldown() * 20;
			if (cooldown == 0) { cooldown += 1 * 20; }
			this.taskOnHold(player, altPlayer, slot, cooldown, itemMap);
		} else if (this.actionType.equals(Action.ON_RECEIVE)) {
			int cooldown = itemMap.getCommandCooldown() * 20;
			if (cooldown == 0) { cooldown += 1 * 20; }
			int receive = itemMap.getCommandReceive();
			this.sendDispatch(player, altPlayer, this.executorType, slot);
			if (receive >= 2) { this.taskOnReceive(player, altPlayer, slot, cooldown, (receive - 1), itemMap); }
		} else { this.sendDispatch(player, altPlayer, this.executorType, slot); }
		return true;
	}

   /**
	* Runs the ItemCommand every x seconds while the player is holding the custom item.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param altPlayer - that is associated with the commands.
	* @param slot - the slot that the custom item is in.
	* @param cooldown - the delay between each command execution.
	* @param itemMap - the ItemMap of the custom item.
	*/
	private void taskOnHold(final Player player, final Player altPlayer, final String slot, final int cooldown, final ItemMap itemMap) {
    	this.cycleTask = SchedulerUtils.runAsyncAtInterval(cooldown, 0L, () -> {
    		if (itemMap.isSimilar(PlayerHandler.getMainHandItem(player))) {
    			this.sendDispatch(player, altPlayer, this.executorType, slot);
    		} else if (itemMap.isSimilar(PlayerHandler.getOffHandItem(player))) {
    			this.sendDispatch(player, altPlayer, this.executorType, slot);
    		} else { this.cancelTask(); }
    	});
    }
	
   /**
	* Cancels the tasks for onHold and onRecieve.
	* 
	*/
	private void cancelTask() {
		Bukkit.getServer().getScheduler().cancelTask(this.cycleTask);
	}
	
   /**
	* Runs the ItemCommand every x seconds for x amount of times when the player receives the item.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param altPlayer - that is associated with the commands.
	* @param slot - the slot that the custom item is in.
	* @param cooldown - the delay between each command execution.
	* @param receive - the number of times to execute the ItemCommand.
	* @param itemMap - the ItemMap of the custom item.
	*/
	private void taskOnReceive(final Player player, final Player altPlayer, final String slot, final int cooldown, final int receive, final ItemMap itemMap) {
		SchedulerUtils.runAsyncLater(cooldown, () -> {
	    	if (receive != 0) {
	    		this.sendDispatch(player, altPlayer, this.executorType, slot); 
	    		this.taskOnReceive(player, altPlayer, slot, cooldown, (receive - 1), itemMap);
	    	} 
	    });
    }
	
   /**
	* Checks if the player is able to execute the command.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param action - the action that triggered the command execution.
	* @return If the player is able to execute the command.
	*/
	public boolean canExecute(final Player player, final String action, final String clickType) {
		if (this.command == null || this.command.length() == 0 || !this.actionType.hasClickType(clickType) || !this.actionType.hasAction(action) || !this.itemMap.conditionMet(player, this.actionType.config + "-conditions")) { return false; }
		return true;
	}
	
   /**
	* Checks if the player action matches the defined action.
	* 
	* @param action - the action that triggered the command execution.
	* @return If the action matches the defined action.
	*/
	public boolean matchAction(final Action action) {
		if (this.actionType.equals(action)) {
			return true;
		}
		return false;
	}

   /**
	* Gets the command list identifier for the ItemCommand section.
	* 
	* @return The name of the section identifier.
	*/
	public String getSection() {
		return this.listSection;
	}
	
   /**
	* Checks if the player action matches the defined action.
	* 
	* @param section - sets the list section identifier.
	*/
	public void setSection(final String section) {
		this.listSection = section;
	}
	
   /**
	* Gets the raw command as defined in the config for the custom item.
	* 
	* @return The command line defined in the config.
	*/
	public String getRawCommand() {
		return this.executorType.getName() + this.command;
	}
	
   /**
	* Sets the command that is to be executed.
	* 
	* @param input - The command to be executed.
	*/
	public void setCommand(String input) {
		input = input.trim();
		input = StringUtils.colorFormat(input);
		this.command = input;
	}
	
   /**
	* Checks if the player can execute the command.
	* Prevents the command from being executed if the player is offline or dead.
	* 
	* @param player - player that is interacting with the custom items command.
	* @return if the player is able to succesfully execute.
	*/
	private boolean getExecute(final Player player) {
		return this.playerVoid.contains(player);
	}
	
   /**
	* Sets the player to allow execution.
	* Prevents the command from being executed if the player is offline or dead.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param bool - if the player is able to succesfully execute.
	*/
	private void setExecute(final Player player, final boolean bool) {
		synchronized("IJ_VOID") {
			if (bool) { 
				this.playerVoid.add(player);
			} else { 
				this.playerVoid.remove(player); 
			}
		}
	}
	
   /**
	* Checks if the player is pending command execution.
	* Prevents the command from being executed if the player is offline or dead.
	* 
	* @param player - player that is interacting with the custom items command.
	* @return if the player is pending execution.
	*/
	private boolean getPending(final Player player) {
		return this.playerPending.contains(player);
	}
	
   /**
	* Sets the player as pending command execution.
	* Prevents the command from being executed if the player is offline or dead.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param bool - if the player is pending execution.
	*/
	private void setPending(final Player player, final boolean bool) {
		synchronized("IJ_PENDING") {
			if (bool) {
				this.playerPending.add(player); 
			} else { 
				this.playerPending.remove(player); 
			}
		}
	}
	
   /**
	* Checks if the dispatched command is allowed execution.
	* Prevents the command from being executed if the player is offline or dead.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param world - the world that the player is in.
	*/
	private void allowDispatch(final Player player, final World world) {
		SchedulerUtils.runAsyncLater(20L, () -> {
			synchronized("IJ_ALLOW") {
				if (this.getPending(player)) {
					if ((!this.actionType.equals(Action.ON_DEATH) && player.isDead()) || !player.isOnline() || player.getWorld() != world) {
						this.setExecute(player, true);
						this.setPending(player, false);
					} else { this.allowDispatch(player, world); }
				}
			}
		});
	}
	
   /**
	* Officially executes the ItemCommand.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param altPlayer - that is associated with the commands.
	* @param cmdtype - the executor of the command.
	* @param slot - the slot the custom item is in.
	*/
	private void sendDispatch(final Player player, final Player altPlayer, final Executor cmdtype, final String slot) {
		final World world = player.getWorld();
		this.setPending(player, true); 
		SchedulerUtils.runLater(this.delay, () -> {
			synchronized("IJ_DISPATCH") {
				this.allowDispatch(player, world);
				this.setPending(player, false);
				if ((this.actionType.equals(Action.ON_DEATH) || !player.isDead()) && ((this.itemMap != null && ((this.actionType.equals(Action.ON_HOLD) && this.itemMap.isReal(PlayerHandler.getMainHandItem(player))) 
					|| (this.actionType.equals(Action.ON_RECEIVE) && this.itemMap.hasItem(player, true)))) || (!this.actionType.equals(Action.ON_HOLD) && !this.actionType.equals(Action.ON_RECEIVE))) 
					&& (((this.itemMap.getCommandSequence() == CommandSequence.REMAIN && cmdtype != Executor.SWAPITEM && cmdtype != Executor.DELAY && this.itemMap.hasItem(player, true)) 
					|| ((this.itemMap.getCommandSequence() == CommandSequence.REMAIN && (cmdtype == Executor.SWAPITEM || cmdtype == Executor.DELAY))) || this.itemMap.getCommandSequence() != CommandSequence.REMAIN))
					&& (player.isOnline() && player.getWorld() == world && !this.getExecute(player))) {
					switch (cmdtype) {
						case CONSOLE: this.dispatchConsoleCommands(player, altPlayer); break;
						case OP: this.dispatchOpCommands(player, altPlayer); break;
						case PLAYER: this.dispatchPlayerCommands(player, altPlayer); break;
						case MESSAGE: this.dispatchMessageCommands(player, altPlayer); break;
						case SERVERSWITCH: this.dispatchServerCommands(player, altPlayer); break;
						case BUNGEE: this.dispatchBungeeCordCommands(player, altPlayer); break;
						case SWAPITEM: this.dispatchSwapItem(player, altPlayer, slot); break;
						case DEFAULT: this.dispatchPlayerCommands(player, altPlayer); break;
						case DELAY: break;
						default: this.dispatchPlayerCommands(player, altPlayer); break;
					}
				} else if (this.getExecute(player)) { this.setExecute(player, false); }
			}
		});
	}
	
   /**
	* Executes the ItemCommand as Console.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param altPlayer - that is associated with the commands.
	*/
	private void dispatchConsoleCommands(final Player player, final Player altPlayer) {
		try {
			if (StringUtils.containsIgnoreCase(this.command, "[close]")) {
				SchedulerUtils.run(() -> {
					PlayerHandler.safeInventoryClose(player);
				});
			} else {
				String[] values = new String[1];
				if (altPlayer != null) { values[0] = altPlayer.getName(); }
				this.setLoggable(player, "/" + StringUtils.translateLayout(this.command, player, values));
				SchedulerUtils.run(() -> {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), StringUtils.translateLayout(this.command, player, values));
				});
			}
		} catch (Exception e) {
			ServerUtils.logSevere("{ItemCommand} There was an error executing an item's command as console, if this continues report it to the developer.");
			ServerUtils.sendDebugTrace(e);
		}
	}
	
   /**
	* Executes the ItemCommand as the specified Player with OP permissions.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param altPlayer - that is associated with the commands.
	*/
	private void dispatchOpCommands(final Player player, final Player altPlayer) {
		try {
			if (StringUtils.containsIgnoreCase(this.command, "[close]")) {
				SchedulerUtils.run(() -> {
					PlayerHandler.safeInventoryClose(player);
				});
			} else {
				SchedulerUtils.run(() -> {
					boolean isOp = player.isOp();
					try {
						player.setOp(true);
						String[] values = new String[1];
						if (altPlayer != null) { values[0] = altPlayer.getName(); }
						this.setLoggable(player, "/" + StringUtils.translateLayout(this.command, player, values));
						player.chat("/" + StringUtils.translateLayout(this.command, player, values));
					} catch (Exception e) {
						ServerUtils.sendDebugTrace(e);
						player.setOp(isOp);
						ServerUtils.logSevere("{ItemCommand} An critical error has occurred while setting " + player.getName() + " status on the OP list, to maintain server security they have been removed as an OP.");
					} finally { player.setOp(isOp); }
				});
			}
		} catch (Exception e) {
			ServerUtils.logSevere("{ItemCommand} There was an error executing an item's command as an op, if this continues report it to the developer.");
			ServerUtils.sendDebugTrace(e);
		}
	}
	
   /**
	* Executes the ItemCommand as the specified Player.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param altPlayer - that is associated with the commands.
	*/
	private void dispatchPlayerCommands(final Player player, final Player altPlayer) {
		try {
			if (StringUtils.containsIgnoreCase(this.command, "[close]")) {
				SchedulerUtils.run(() -> {
					PlayerHandler.safeInventoryClose(player);
				});
			} else {
				String[] values = new String[1];
				if (altPlayer != null) { values[0] = altPlayer.getName(); }
				this.setLoggable(player, "/" + StringUtils.translateLayout(this.command, player, values));
				SchedulerUtils.run(() -> {
					player.chat("/" + StringUtils.translateLayout(this.command, player, values));
				});
			}
		} catch (Exception e) {
			ServerUtils.logSevere("{ItemCommand} There was an error executing an item's command as a player, if this continues report it to the developer.");
			ServerUtils.sendDebugTrace(e);
		}
	}
	
   /**
	* Executes the ItemCommand as a custom Message.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param altPlayer - that is associated with the commands.
	*/
	private void dispatchMessageCommands(final Player player, final Player altPlayer) {
		try { 
			SchedulerUtils.run(() -> {
				String[] values = new String[1];
				if (altPlayer != null) { values[0] = altPlayer.getName(); }
				player.sendMessage(StringUtils.translateLayout(this.command, player, values)); 
			});
		} 
		catch (Exception e) {
			ServerUtils.logSevere("{ItemCommand} There was an error executing an item's command to send a message, if this continues report it to the developer.");
			ServerUtils.sendDebugTrace(e);
		}
	}
	
   /**
	* Sends the specified Player to the defined commands Bungee server.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param altPlayer - that is associated with the commands.
	*/
	private void dispatchServerCommands(final Player player, final Player altPlayer) {
		try { 
			SchedulerUtils.run(() -> {
				String[] values = new String[1];
				if (altPlayer != null) { values[0] = altPlayer.getName(); }
				BungeeAPI.getBungee(false).SwitchServers(player, StringUtils.translateLayout(this.command, player, values)); 
			});
		} 
		catch (Exception e) {
			ServerUtils.logSevere("{ItemCommand} There was an error executing an item's command to switch servers, if this continues report it to the developer.");
			ServerUtils.sendDebugTrace(e);
		}
	}
	
   /**
	* Sends the ItemCommand to be executed in BungeeCord.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param altPlayer - that is associated with the commands.
	*/
	private void dispatchBungeeCordCommands(final Player player, final Player altPlayer) {
		try { 
			SchedulerUtils.run(() -> {
				String[] values = new String[1];
				if (altPlayer != null) { values[0] = altPlayer.getName(); }
				BungeeAPI.getBungee(false).ExecuteCommand(player, StringUtils.translateLayout(this.command, player, values)); 
			});
		} 
		catch (Exception e) {
			ServerUtils.logSevere("{ItemCommand} There was an error executing an item's command to BungeeCord, if this continues report it to the developer.");
			ServerUtils.sendDebugTrace(e);
		}
	}
	
   /**
	* Swaps the executed command item with the defined command item.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param altPlayer - that is associated with the commands.
	*/
	private void dispatchSwapItem(final Player player, final Player altPlayer, final String slot) {
		try {
			for (ItemMap item : ItemUtilities.getUtilities().getItems()) {
				if (item.getConfigName().equalsIgnoreCase(this.command) && slot != null) {
					boolean itemExists = ((this.itemMap.getCommandSequence() == CommandSequence.REMAIN && this.itemMap.hasItem(player, true)) || this.itemMap.getCommandSequence() != CommandSequence.REMAIN);
					this.itemMap.removeDisposable(player, this.itemMap, this.itemMap.getItem(player), true); 
					for (ItemCommand command : this.itemMap.getCommands()) {
						if (command.executorType == Executor.SWAPITEM && this.matchAction(command.actionType)) {
							ItemMap commandMap = ItemUtilities.getUtilities().getItemMap(null, command.command, null);
							if (commandMap != null) {
								if (!itemExists) { itemExists = commandMap.hasItem(player, true); }
								commandMap.removeDisposable(player, commandMap, commandMap.getItem(player), true); 
							}
						}
					}
					if (itemExists) { item.swapItem(player, slot); }
					break;
				}
			}
		} catch (Exception e) {
			ServerUtils.logSevere("{ItemCommand} There was an error executing an item's command to swap an items attributes, if this continues report it to the developer.");
			ServerUtils.sendDebugTrace(e);
		}
	}
	
   /**
	* Sets the executed command to be logged or "shown" in the console window.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param logCommand - the command that wont be logged.
	*/
	private void setLoggable(final Player player, final String logCommand) {
		if (!ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.Log-Commands")) {
			ArrayList < String > templist = new ArrayList < String > ();
			if (LogHandler.getFilter(false).getHidden().get("commands-list") != null && !LogHandler.getFilter(false).getHidden().get("commands-list").contains(logCommand)) {
				templist = LogHandler.getFilter(false).getHidden().get("commands-list");
			}
			templist.add(logCommand);
			LogHandler.getFilter(false).addHidden("commands-list", templist);
		}
	}
	
   /**
	* Gets the Action for the custom items command.
	* 
	* @param itemMap - the ItemMap of the custom item.
	* @param definition - the config definition.
	* @return The Action when executing the command.
	*/
	private static Action getExactAction(final ItemMap itemMap, final String config) {
		Action exactAction = Action.DEFAULT;
		for (Action action: Action.values()) {
			if (action.hasConfig(config)) {
				exactAction = action; break;
			}
		}
		return exactAction;
	}
	
   /**
	* Gets the exact command line from the string input.
	* 
	* @param input - the raw command line input.
	* @param action - the game interaction type.
	* @param commandType - the command interaction type.
	* @param delay - the delay before executing the command.
	* @param listSection - the listed section identifier.
	* @return The new ItemCommand instance.
	*/
	public static ItemCommand fromString(String input, final Action action, final ItemMap itemMap, final long delay, final String listSection) {
		if (input == null || input.length() == 0) { return new ItemCommand("", Action.DEFAULT, Executor.DEFAULT, null, 0L, null); }
		input = input.trim();
		Executor type = Executor.DEFAULT;
			
		if (input.startsWith("default:")) { input = input.substring(8); type = Executor.DEFAULT; } 
		else if (input.startsWith("console:")) { input = input.substring(8); type = Executor.CONSOLE; } 
		else if (input.startsWith("op:")) { input = input.substring(3); type = Executor.OP; } 
		else if (input.startsWith("player:")) { input = input.substring(7); type = Executor.PLAYER; } 
		else if (input.startsWith("server:")) { input = input.substring(7); type = Executor.SERVERSWITCH; } 
		else if (input.startsWith("bungee:")) { input = input.substring(7); type = Executor.BUNGEE; } 
		else if (input.startsWith("message:")) { input = input.substring(8); type = Executor.MESSAGE; } 
		else if (input.startsWith("swap-item:")) { input = input.substring(10); type = Executor.SWAPITEM; }
		else if (input.startsWith("delay:")) { input = input.substring(6); type = Executor.DELAY; }
			
		input = input.trim();
		input = StringUtils.colorFormat(input);
		return new ItemCommand(input, action, type, itemMap, delay, listSection);
	}
	
   /**
	* Generates a list of new instanced ItemCommands from the itemMap definition.
	* 
	* @param itemMap - the ItemMap of the custom item.
	* @param isList - are the commands multiple lists or a single list in an identifier.
	* @return The list of ItemCommands relating to the specified ItemMap.
	*/
	public static ItemCommand[] arrayFromString(final ItemMap itemMap, final boolean isList) {
		if (ConfigHandler.getConfig().getCommandsSection(itemMap.getNodeLocation()) == null) {
			return new ItemCommand[] {
				new ItemCommand("", Action.DEFAULT, Executor.DEFAULT, null, 0L, null)
			};
		}
		return fromConfig(itemMap, isList);
	}
	
   /**
	* Gets the commands from the ItemMap config.
	* 
	* @param itemMap - the ItemMap of the custom item.
	* @param isList - are the commands multiple lists or a single list in an identifier.
	* @return The list of ItemCommands relating to the specified ItemMap.
	*/
	private static ItemCommand[] fromConfig(final ItemMap itemMap, final boolean isList) {
		if (ConfigHandler.getConfig().getCommandsSection(itemMap.getNodeLocation()) != null) {
			final List < ItemCommand > arrayCommands = new ArrayList < ItemCommand > ();
			Iterator < String > it = ConfigHandler.getConfig().getCommandsSection(itemMap.getNodeLocation()).getKeys(false).iterator();
			while (it.hasNext()) {
				String definition = it.next();
				ConfigurationSection commandSection = ConfigHandler.getConfig().getFile("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + "." + definition);
				if (isList && commandSection != null) {
					for (String internalCommands: commandSection.getKeys(false)) {
						arrayCommands.addAll(arrayFromConfig(itemMap, definition, internalCommands));
					}
				} else { arrayCommands.addAll(arrayFromConfig(itemMap, definition, null)); }
			}
			final ItemCommand[] commands = new ItemCommand[arrayCommands.size()];
			for (int i = 0; i < arrayCommands.size(); ++i) { commands[i] = arrayCommands.get(i);}
			return commands;
		}
		return null;
	}
	
   /**
	* Gets the a list of ItemCommands from the ItemMap config.
	* 
	* @param itemMap - the ItemMap of the custom item.
	* @param definition - the action type to be determined.
	* @param internalCommands - the commands list defined for a random sequence.
	* @return The list of ItemCommands relating to the specified ItemMap.
	*/
	private static List < ItemCommand > arrayFromConfig(final ItemMap itemMap, final String definition, final String internalCommands) {
		long delay = 0L;
		List < String > commandsList = itemMap.getNodeLocation().getStringList("." + definition + (internalCommands != null ?  ("." + internalCommands) : ""));
		final List < ItemCommand > arrayCommands = new ArrayList < ItemCommand > ();
		for (int i = 0; i < commandsList.size(); i++) {
			if (commandsList.get(i).trim().startsWith("delay:")) { delay = delay + ItemHandler.getDelay(commandsList.get(i).trim()); }
			arrayCommands.add(fromString(commandsList.get(i).trim(), getExactAction(itemMap, definition), itemMap, delay, internalCommands));
		}
		return arrayCommands;	
	}
	
   /**
	* Defines the Executor type for the command.
	* 
	*/
	private enum Executor {
		DEFAULT("default: ", 0), CONSOLE("console: ", 1), OP("op: ", 2), PLAYER("player: ", 3), 
		SERVERSWITCH("server: ", 4), MESSAGE("message: ", 5), BUNGEE("bungee: ", 6), SWAPITEM("swap-item: ", 7), DELAY("delay: ", 8);
		
		private final String name;
		private Executor(final String name, final int intType) { this.name = name; }
		private String getName() { return this.name; }
	}
	
   /**
	* Defines the Action type for the command.
	* 
	*/
	public enum Action {
		DEFAULT("", "", ""),
		
		INTERACT_ALL(".interact", "LEFT_CLICK_BLOCK, LEFT_CLICK_AIR, RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR", "LEFT, RIGHT"),
		INTERACT_AIR(".interact-air", "LEFT_CLICK_AIR, RIGHT_CLICK_AIR", "LEFT, RIGHT"),
		INTERACT_BLOCK(".interact-block", "LEFT_CLICK_BLOCK, RIGHT_CLICK_BLOCK", "LEFT, RIGHT"),
		
		INTERACT_LEFT_ALL(".interact-left", "LEFT_CLICK_AIR, LEFT_CLICK_BLOCK", "LEFT"),
		INTERACT_LEFT_AIR(".interact-air-left", "LEFT_CLICK_AIR", "LEFT"),
		INTERACT_LEFT_BLOCK(".interact-block-left", "LEFT_CLICK_BLOCK", "LEFT"),
		
		INTERACT_RIGHT_ALL(".interact-right", "RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK", "RIGHT"),
		INTERACT_RIGHT_AIR(".interact-air-right", "RIGHT_CLICK_AIR", "RIGHT"),
		INTERACT_RIGHT_BLOCK(".interact-block-right", "RIGHT_CLICK_BLOCK", "RIGHT"),
		
		INVENTORY_ALL(".inventory", "NOTHING, PICKUP_ALL, PLACE_ALL, PLACE_SOME, PICKUP_HALF, PLACE_ONE, MOVE_TO_OTHER_INVENTORY", "MIDDLE, LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT"),
		INVENTORY_MIDDLE(".inventory-middle", "NOTHING", "MIDDLE"),
		INVENTORY_CREATIVE(".inventory-creative", "PICKUP_ALL, PLACE_ALL, PLACE_SOME, PICKUP_HALF, PLACE_ONE, CLONE_STACK", "CREATIVE"),
		INVENTORY_LEFT(".inventory-left", "PICKUP_ALL, PLACE_ALL, PLACE_SOME", "LEFT"),
		INVENTORY_RIGHT(".inventory-right", "PICKUP_HALF, PLACE_ONE", "RIGHT"),
		INVENTORY_SHIFT_LEFT(".inventory-shift-left", "MOVE_TO_OTHER_INVENTORY", "SHIFT_LEFT"),
		INVENTORY_SHIFT_RIGHT(".inventory-shift-right", "MOVE_TO_OTHER_INVENTORY", "SHIFT_RIGHT"),
		INVENTORY_SWAP_CURSOR(".inventory-swap-cursor", "SWAP_WITH_CURSOR", "LEFT, RIGHT"),
		
		ON_HOLD(".on-hold", "ON_HOLD", "HELD"),
		ON_EQUIP(".on-equip", "ON_EQUIP", "EQUIPPED"),
		UN_EQUIP(".un-equip", "UN_EQUIP", "UNEQUIPPED"),
		ON_DEATH(".on-death", "ON_DEATH", "DEAD"),
		ON_DAMAGE(".on-damage", "ON_DAMAGE", "DAMAGED"),
		ON_HIT(".on-hit", "ON_HIT", "HIT"),
		ON_RECEIVE(".on-receive", "ON_RECEIVE", "RECEIVED"),
		PHYSICAL(".physical", "PHYSICAL", "INTERACTED");
		
		private final String config;
		private final String actions;
		private final String clickType;
		private Action(String Config, String Actions, String ClickType) { this.config = Config; this.actions = Actions; this.clickType = ClickType; }
		public String config() { return this.config; }
		public boolean hasConfig(String Config) { return this.config.contains(Config); }
		public boolean hasAction(String Action) { return StringUtils.splitIgnoreCase(this.actions, Action, ","); }
		public boolean hasClickType(String ClickType) { return StringUtils.splitIgnoreCase(this.clickType, ClickType, ","); }
	}
	
   /**
	* Defines the Sequence for the command.
	* 
	*/
	public enum CommandSequence { RANDOM, RANDOM_SINGLE, RANDOM_LIST, REMAIN, SEQUENTIAL, ALL; }
}