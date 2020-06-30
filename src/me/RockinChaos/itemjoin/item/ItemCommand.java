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
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.BungeeCord;
import me.RockinChaos.itemjoin.utils.LogFilter;
import me.RockinChaos.itemjoin.utils.Utils;

public class ItemCommand {
	
	private long delay = 0L;
	private int cycleTask = 0;
	private String command;
	private String listSection;
	private ItemStack itemCopy;
	private ExecutorType executorType;
	private ActionType actionType;
	private CommandType commandType;
	private List < Player > setStop = new ArrayList < Player > ();
	private List < Player > setCounting = new ArrayList < Player > ();
	
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
	private ItemCommand(final String command, final ActionType action, final ExecutorType executorType, final long delay, final String commandType, final String listSection) {
		this.command = command;
		this.executorType = executorType;
		this.actionType = action;
		this.delay = delay;
		this.listSection = listSection;
		if (commandType.equalsIgnoreCase("INTERACT")) { this.commandType = CommandType.INTERACT; } 
		else if (commandType.equalsIgnoreCase("INVENTORY")) { this.commandType = CommandType.INVENTORY; } 
		else if (commandType.equalsIgnoreCase("BOTH")) { this.commandType = CommandType.BOTH; }
	}
	
   /**
	* Executes the command for the specified player.
    * 
	* @param player - player that is interacting with the custom items command.
	* @param action - the action when interacting with the item.
	* @param slot - the slot that the custom item is in.
	* @param itemMap - the ItemMap of the custom item.
	* @return If the command execute was successful.
	*/
	public boolean execute(final Player player, final String action, final String slot, final ItemMap itemMap) {
		if (this.command == null || this.command.length() == 0 || !this.commandType.hasAction(action) || !this.actionType.hasAction(action)) { return false; }
		if (this.actionType.equals(ActionType.ON_HOLD)) {
			int cooldown = itemMap.getCommandCooldown() * 20;
			if (cooldown == 0) { cooldown += 1 * 20; }
			this.taskOnHold(player, slot, cooldown, itemMap);
		} else if (this.actionType.equals(ActionType.ON_RECEIVE)) {
			int cooldown = itemMap.getCommandCooldown() * 20;
			if (cooldown == 0) { cooldown += 1 * 20; }
			int receive = itemMap.getCommandReceive();
			this.sendDispatch(player, this.executorType, slot);
			if (receive >= 2) { this.taskOnReceive(player, slot, cooldown, (receive - 1), itemMap); }
		} else { this.sendDispatch(player, this.executorType, slot); }
		return true;
	}

   /**
	* Runs the ItemCommand every x seconds while the player is holding the custom item.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param slot - the slot that the custom item is in.
	* @param cooldown - the delay between each command execution.
	* @param itemMap - the ItemMap of the custom item.
	*/
	private void taskOnHold(final Player player, final String slot, final int cooldown, final ItemMap itemMap) {
    	this.cycleTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(ItemJoin.getInstance(), new Runnable() {
    		public void run() {
    			if (itemMap.isSimilar(PlayerHandler.getPlayer().getMainHandItem(player))) {
    				sendDispatch(player, executorType, slot);
    			} else if (itemMap.isSimilar(PlayerHandler.getPlayer().getOffHandItem(player))) {
    				sendDispatch(player, executorType, slot);
    			} else { cancelTask(); }
    		}
    	}, 0L, cooldown);
    }
	
   /**
	* Runs the ItemCommand every x seconds for x amount of times when the player receives the item.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param slot - the slot that the custom item is in.
	* @param cooldown - the delay between each command execution.
	* @param receive - the number of times to execute the ItemCommand.
	* @param itemMap - the ItemMap of the custom item.
	*/
	private void taskOnReceive(final Player player, final String slot, final int cooldown, final int receive, final ItemMap itemMap) {
    	Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
    		public void run() {
    			if (receive != 0) {
    				sendDispatch(player, executorType, slot); 
    				taskOnReceive(player, slot, cooldown, (receive - 1), itemMap);
    			} 
    		}
    	}, cooldown);
    }
	
   /**
	* Cancels the tasks for onHold and onRecieve.
	* 
	*/
	private void cancelTask() {
		Bukkit.getServer().getScheduler().cancelTask(this.cycleTask);
	}
	
   /**
	* Checks if the player is able to execute the command.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param action - the action that triggered the command execution.
	* @return If the player is able to execute the command.
	*/
	public boolean canExecute(final Player player, final String action) {
		if (this.command == null || this.command.length() == 0 || !this.commandType.hasAction(action) || !this.actionType.hasAction(action)) { return false; }
		return true;
	}
	
   /**
	* Checks if the player action matches the defined action.
	* 
	* @param action - the action that triggered the command execution.
	* @return If the action matches the defined action.
	*/
	public boolean matchAction(final ActionType action) {
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
		input = Utils.getUtils().colorFormat(input);
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
		return this.setStop.contains(player);
	}
	
   /**
	* Sets the player to allow execution.
	* Prevents the command from being executed if the player is offline or dead.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param bool - if the player is able to succesfully execute.
	*/
	private void setExecute(final Player player, final boolean bool) {
		if (bool) { 
			this.setStop.add(player);
		} else { 
			this.setStop.remove(player); 
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
		return this.setCounting.contains(player);
	}
	
   /**
	* Sets the player as pending command execution.
	* Prevents the command from being executed if the player is offline or dead.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param bool - if the player is pending execution.
	*/
	private void setPending(final Player player, final boolean bool) {
		if (bool) {
			this.setCounting.add(player); 
		} else { 
			this.setCounting.remove(player); 
		}
	}
	
   /**
	* Sets the Swap Item ItemStack to be removed.
	* 
	* @param itemCopy - The ItemStack to be removed.
	*/
	public void setItem(final ItemStack itemCopy) {
		this.itemCopy = itemCopy.clone();
	}
	
   /**
	* Checks if the dispatched command is allowed execution.
	* Prevents the command from being executed if the player is offline or dead.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param world - the world that the player is in.
	*/
	private void allowDispatch(final Player player, final World world) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (getPending(player)) {
					if (player.isDead() || !player.isOnline() || player.getWorld() != world) {
						setExecute(player, true);
						setPending(player, false);
					} else { allowDispatch(player, world); }
				}
			}
		}, 20);
	}
	
   /**
	* Officially executes the ItemCommand.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param cmdtype - the executor of the command.
	* @param slot - the slot the custom item is in.
	*/
	private void sendDispatch(final Player player, final ExecutorType cmdtype, final String slot) {
		final World world = player.getWorld();
		this.setPending(player, true); 
		this.allowDispatch(player, world);
		Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (!player.isDead() && player.isOnline() && player.getWorld() == world && !getExecute(player)) {
					setPending(player, false);
					switch (cmdtype) {
						case CONSOLE: dispatchConsoleCommands(player); break;
						case OP: dispatchOpCommands(player); break;
						case PLAYER: dispatchPlayerCommands(player); break;
						case MESSAGE: dispatchMessageCommands(player); break;
						case SERVERSWITCH: dispatchServerCommands(player); break;
						case BUNGEE: dispatchBungeeCordCommands(player); break;
						case SWAPITEM: dispatchSwapItem(player, slot); break;
						case DEFAULT: dispatchPlayerCommands(player); break;
						case DELAY: break;
						default: dispatchPlayerCommands(player); break;
					}
				} else if (getExecute(player)) { setExecute(player, false); }
			}
		}, this.delay);
	}
	
   /**
	* Executes the ItemCommand as Console.
	* 
	* @param player - player that is interacting with the custom items command.
	*/
	private void dispatchConsoleCommands(final Player player) {
		try {
			if (Utils.getUtils().containsIgnoreCase(this.command, "[close]")) {
				player.closeInventory();
			} else {
				this.setLoggable(player, "/" + Utils.getUtils().translateLayout(this.command, player));
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Utils.getUtils().translateLayout(this.command, player));
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{ItemCommand} There was an error executing an item's command as console, if this continues report it to the developer.");
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Executes the ItemCommand as the specified Player with OP permissions.
	* 
	* @param player - player that is interacting with the custom items command.
	*/
	private void dispatchOpCommands(final Player player) {
		try {
			if (Utils.getUtils().containsIgnoreCase(this.command, "[close]")) {
				player.closeInventory();
			} else {
				boolean isOp = player.isOp();
				try {
					player.setOp(true);
					this.setLoggable(player, "/" + Utils.getUtils().translateLayout(this.command, player));
					player.chat("/" + Utils.getUtils().translateLayout(this.command, player));
				} catch (Exception e) {
					ServerHandler.getServer().sendDebugTrace(e);
					player.setOp(isOp);
					ServerHandler.getServer().logSevere("{ItemCommand} An critical error has occurred while setting " + player.getName() + " status on the OP list, to maintain server security they have been removed as an OP.");
				} finally { player.setOp(isOp); }
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{ItemCommand} There was an error executing an item's command as an op, if this continues report it to the developer.");
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Executes the ItemCommand as the specified Player.
	* 
	* @param player - player that is interacting with the custom items command.
	*/
	private void dispatchPlayerCommands(final Player player) {
		try {
			if (Utils.getUtils().containsIgnoreCase(this.command, "[close]")) {
				player.closeInventory();
			} else {
				this.setLoggable(player, "/" + Utils.getUtils().translateLayout(this.command, player));
				player.chat("/" + Utils.getUtils().translateLayout(this.command, player));
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{ItemCommand} There was an error executing an item's command as a player, if this continues report it to the developer.");
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Executes the ItemCommand as a custom Message.
	* 
	* @param player - player that is interacting with the custom items command.
	*/
	private void dispatchMessageCommands(final Player player) {
		try { player.sendMessage(Utils.getUtils().translateLayout(this.command, player)); } 
		catch (Exception e) {
			ServerHandler.getServer().logSevere("{ItemCommand} There was an error executing an item's command to send a message, if this continues report it to the developer.");
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Sends the specified Player to the defined commands Bungee server.
	* 
	* @param player - player that is interacting with the custom items command.
	*/
	private void dispatchServerCommands(final Player player) {
		try { BungeeCord.getBungee().SwitchServers(player, Utils.getUtils().translateLayout(this.command, player)); } 
		catch (Exception e) {
			ServerHandler.getServer().logSevere("{ItemCommand} There was an error executing an item's command to switch servers, if this continues report it to the developer.");
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Sends the ItemCommand to be executed in BungeeCord.
	* 
	* @param player - player that is interacting with the custom items command.
	*/
	private void dispatchBungeeCordCommands(final Player player) {
		try { BungeeCord.getBungee().ExecuteCommand(player, Utils.getUtils().translateLayout(this.command, player)); } 
		catch (Exception e) {
			ServerHandler.getServer().logSevere("{ItemCommand} There was an error executing an item's command to BungeeCord, if this continues report it to the developer.");
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Swaps the executed command item with the defined command item.
	* 
	* @param player - player that is interacting with the custom items command.
	*/
	private void dispatchSwapItem(final Player player, final String slot) {
		try {
			for (ItemMap item : ItemUtilities.getUtilities().getItems()) {
				if (item.getConfigName().equalsIgnoreCase(this.command)) {
					ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(this.itemCopy, null, player.getWorld());
					if (itemMap != null) { item.removeDisposable(player, itemMap, this.itemCopy, true); }
					item.swapItem(player, slot);
					break;
				}
			}
		} 
		catch (Exception e) {
			ServerHandler.getServer().logSevere("{ItemCommand} There was an error executing an item's command to swap an items attributes, if this continues report it to the developer.");
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Sets the executed command to be logged or "shown" in the console window.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param logCommand - the command that wont be logged.
	*/
	private void setLoggable(final Player player, final String logCommand) {
		if (!ConfigHandler.getConfig(false).getFile("config.yml").getBoolean("General.Log-Commands")) {
			ArrayList < String > templist = new ArrayList < String > ();
			if (LogFilter.getFilter(false).getHidden().get("commands-list") != null && !LogFilter.getFilter(false).getHidden().get("commands-list").contains(logCommand)) {
				templist = LogFilter.getFilter(false).getHidden().get("commands-list");
			}
			templist.add(logCommand);
			LogFilter.getFilter(false).addHidden("commands-list", templist);
		}
	}
	
   /**
	* Gets the ActionType for the custom items command.
	* 
	* @param itemMap - the ItemMap of the custom item.
	* @param definition - the config definition.
	* @return The ActionType when executing the command.
	*/
	private static ActionType getExactActionType(final ItemMap itemMap, final String definition) {
		String invExists = itemMap.getNodeLocation().getString(".commands" + ActionType.INVENTORY.definition);
		CommandType type = CommandType.INTERACT; if (itemMap.getCommandType() != null) { type = itemMap.getCommandType(); } 
		if ((type.equals(CommandType.INVENTORY) || invExists != null || type.equals(CommandType.BOTH)) && ActionType.INVENTORY.hasDefine(definition)) {
			return ActionType.INVENTORY;
		} else if (ActionType.LEFT_CLICK_ALL.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.INVENTORY) || invExists != null || type.equals(CommandType.BOTH))) {
			return ActionType.LEFT_CLICK_ALL;
		} else if (ActionType.LEFT_CLICK_AIR.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.BOTH))) {
			return ActionType.LEFT_CLICK_AIR;
		} else if (ActionType.LEFT_CLICK_BLOCK.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.BOTH))) {
			return ActionType.LEFT_CLICK_BLOCK;
		} else if (ActionType.RIGHT_CLICK_ALL.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.INVENTORY) || invExists != null || type.equals(CommandType.BOTH))) {
			return ActionType.RIGHT_CLICK_ALL;
		} else if (ActionType.RIGHT_CLICK_AIR.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.BOTH))) {
			return ActionType.RIGHT_CLICK_AIR;
		} else if (ActionType.RIGHT_CLICK_BLOCK.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.BOTH))) {
			return ActionType.RIGHT_CLICK_BLOCK;
		} else if (ActionType.MULTI_CLICK_ALL.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.INVENTORY) || invExists != null || type.equals(CommandType.BOTH))) {
			return ActionType.MULTI_CLICK_ALL;
		} else if (ActionType.MULTI_CLICK_AIR.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.BOTH))) {
			return ActionType.MULTI_CLICK_AIR;
		} else if (ActionType.MULTI_CLICK_BLOCK.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.BOTH))) {
			return ActionType.MULTI_CLICK_BLOCK;
		} else if (ActionType.PHYSICAL.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.BOTH))) {
			return ActionType.PHYSICAL;
		} else if (ActionType.ON_RECEIVE.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.INVENTORY) || invExists != null || type.equals(CommandType.BOTH))) {
			return ActionType.ON_RECEIVE;
		} else if (ActionType.ON_HOLD.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.INVENTORY) || invExists != null || type.equals(CommandType.BOTH))) {
			return ActionType.ON_HOLD;
		} else if (ActionType.ON_EQUIP.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.INVENTORY) || invExists != null || type.equals(CommandType.BOTH))) {
			return ActionType.ON_EQUIP;
		} else if (ActionType.UN_EQUIP.hasDefine(definition) && (type.equals(CommandType.INTERACT) || type.equals(CommandType.INVENTORY) || invExists != null || type.equals(CommandType.BOTH))) {
			return ActionType.UN_EQUIP;
		}	
		return ActionType.DEFAULT;
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
	public static ItemCommand fromString(String input, final ActionType action, final CommandType commandType, final long delay, final String listSection) {
		if (input == null || input.length() == 0) { return new ItemCommand("", ActionType.DEFAULT, ExecutorType.DEFAULT, 0L, commandType != null ? commandType.name() : "INTERACT", null); }
		input = input.trim();
		ExecutorType type = ExecutorType.DEFAULT;
			
		if (input.startsWith("default:")) { input = input.substring(8); type = ExecutorType.DEFAULT; } 
		else if (input.startsWith("console:")) { input = input.substring(8); type = ExecutorType.CONSOLE; } 
		else if (input.startsWith("op:")) { input = input.substring(3); type = ExecutorType.OP; } 
		else if (input.startsWith("player:")) { input = input.substring(7); type = ExecutorType.PLAYER; } 
		else if (input.startsWith("server:")) { input = input.substring(7); type = ExecutorType.SERVERSWITCH; } 
		else if (input.startsWith("bungee:")) { input = input.substring(7); type = ExecutorType.BUNGEE; } 
		else if (input.startsWith("message:")) { input = input.substring(8); type = ExecutorType.MESSAGE; } 
		else if (input.startsWith("swap-item:")) { input = input.substring(10); type = ExecutorType.SWAPITEM; }
		else if (input.startsWith("delay:")) { input = input.substring(6); type = ExecutorType.DELAY; }
			
		input = input.trim();
		input = Utils.getUtils().colorFormat(input);
		return new ItemCommand(input, action, type, delay, commandType != null ? commandType.name() : "INTERACT", listSection);
	}
	
   /**
	* Generates a list of new instanced ItemCommands from the itemMap definition.
	* 
	* @param itemMap - the ItemMap of the custom item.
	* @param isList - are the commands multiple lists or a single list in an identifier.
	* @return The list of ItemCommands relating to the specified ItemMap.
	*/
	public static ItemCommand[] arrayFromString(final ItemMap itemMap, final boolean isList) {
		if (ConfigHandler.getConfig(false).getCommandsSection(itemMap.getNodeLocation()) == null) {
			return new ItemCommand[] {
				new ItemCommand("", ActionType.DEFAULT, ExecutorType.DEFAULT, 0L, itemMap.getCommandType() != null ? itemMap.getCommandType().name() : "INTERACT", null)
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
		if (ConfigHandler.getConfig(false).getCommandsSection(itemMap.getNodeLocation()) != null) {
			final List < ItemCommand > arrayCommands = new ArrayList < ItemCommand > ();
			Iterator < String > it = ConfigHandler.getConfig(false).getCommandsSection(itemMap.getNodeLocation()).getKeys(false).iterator();
			while (it.hasNext()) {
				String definition = it.next();
				ConfigurationSection commandSection = ConfigHandler.getConfig(false).getFile("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".commands." + definition);
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
		List < String > commandsList = itemMap.getNodeLocation().getStringList("commands." + definition + (internalCommands != null ?  ("." + internalCommands) : ""));
		final List < ItemCommand > arrayCommands = new ArrayList < ItemCommand > ();
		for (int i = 0; i < commandsList.size(); i++) {
			if (commandsList.get(i).trim().startsWith("delay:")) { delay = delay + ItemHandler.getItem().getDelay(commandsList.get(i).trim()); }
			arrayCommands.add(fromString(commandsList.get(i).trim(), getExactActionType(itemMap, definition), itemMap.getCommandType(), delay, internalCommands));
		}
		return arrayCommands;	
	}
	
   /**
	* Defines the Executor type for the command.
	* 
	*/
	private enum ExecutorType {
		DEFAULT("default: ", 0), CONSOLE("console: ", 1), OP("op: ", 2), PLAYER("player: ", 3), 
		SERVERSWITCH("server: ", 4), MESSAGE("message: ", 5), BUNGEE("bungee: ", 6), SWAPITEM("swap-item: ", 7), DELAY("delay: ", 8);
		
		private final String name;
		private ExecutorType(final String name, final int intType) { this.name = name; }
		private String getName() { return this.name; }
	}
	
   /**
	* Defines the config Command type for the command.
	* 
	*/
	public enum CommandType {
		INTERACT("PHYSICAL, LEFT_CLICK_BLOCK, LEFT_CLICK_AIR, RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR, ON_RECEIVE, ON_HOLD, ON_EQUIP, UN_EQUIP"),
		INVENTORY("PICKUP_ALL, PICKUP_HALF, PLACE_ALL, ON_RECEIVE, ON_EQUIP, UN_EQUIP"),
		BOTH("PICKUP_ALL, PICKUP_HALF, PLACE_ALL, PHYSICAL, LEFT_CLICK_BLOCK, LEFT_CLICK_AIR, RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR, ON_RECEIVE, ON_HOLD, ON_EQUIP, UN_EQUIP");
		private final String name;
		private CommandType(String Action) { this.name = Action; }
		public boolean hasAction(String Action) { return this.name.contains(Action); }
	}
	
   /**
	* Defines the Action type for the command.
	* 
	*/
	public enum ActionType {
		DEFAULT("", ""),
		PHYSICAL("PHYSICAL", ".physical"),
		INVENTORY("PICKUP_ALL, PICKUP_HALF, PLACE_ALL", ".inventory"),
		MULTI_CLICK_ALL("LEFT_CLICK_BLOCK, LEFT_CLICK_AIR, RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR, PICKUP_ALL, PICKUP_HALF, PLACE_ALL", ".multi-click"),
		MULTI_CLICK_AIR("LEFT_CLICK_AIR, RIGHT_CLICK_AIR", ".multi-click-air"),
		MULTI_CLICK_BLOCK("LEFT_CLICK_BLOCK, RIGHT_CLICK_BLOCK", ".multi-click-block"),
		LEFT_CLICK_ALL("LEFT_CLICK_AIR, LEFT_CLICK_BLOCK, PICKUP_ALL", ".left-click"),
		LEFT_CLICK_AIR("LEFT_CLICK_AIR", ".left-click-air"),
		LEFT_CLICK_BLOCK("LEFT_CLICK_BLOCK", ".left-click-block"),
		RIGHT_CLICK_ALL("RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK, PICKUP_HALF", ".right-click"),
		RIGHT_CLICK_AIR("RIGHT_CLICK_AIR", ".right-click-air"),
		RIGHT_CLICK_BLOCK("RIGHT_CLICK_BLOCK", ".right-click-block"),
		ON_RECEIVE("ON_RECEIVE", ".on-receive"),
		ON_HOLD("ON_HOLD", ".on-hold"),
		ON_EQUIP("ON_EQUIP", ".on-equip"),
		UN_EQUIP("UN_EQUIP", ".un-equip");
			
		private final String name;
		private final String definition;
		private ActionType(String Action, String Definition) { this.name = Action; this.definition = Definition; }
		public boolean hasAction(String Action) { return this.name.contains(Action); }
		public boolean hasDefine(String Define) { return this.definition.contains(Define); }
	}
	
   /**
	* Defines the Sequence for the command.
	* 
	*/
	public enum CommandSequence { RANDOM, RANDOM_SINGLE, RANDOM_LIST, SEQUENTIAL, ALL; }
}