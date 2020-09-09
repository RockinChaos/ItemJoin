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
	private Executor executorType;
	private Action actionType;
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
	private ItemCommand(final String command, final Action action, final Executor executorType, final long delay, final String listSection) {
		this.command = command;
		this.executorType = executorType;
		this.actionType = action;
		this.delay = delay;
		this.listSection = listSection;
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
	public boolean execute(final Player player, final String action, final String clickType, final String slot, final ItemMap itemMap) {
		if (this.command == null || this.command.length() == 0 || !this.actionType.hasClickType(clickType) || !this.actionType.hasAction(action)) { return false; }
		if (this.actionType.equals(Action.ON_HOLD)) {
			int cooldown = itemMap.getCommandCooldown() * 20;
			if (cooldown == 0) { cooldown += 1 * 20; }
			this.taskOnHold(player, slot, cooldown, itemMap);
		} else if (this.actionType.equals(Action.ON_RECEIVE)) {
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
	* @param slot - the slot that the custom item is in.
	* @param cooldown - the delay between each command execution.
	* @param receive - the number of times to execute the ItemCommand.
	* @param itemMap - the ItemMap of the custom item.
	*/
	private void taskOnReceive(final Player player, final String slot, final int cooldown, final int receive, final ItemMap itemMap) {
    	ServerHandler.getServer().runThread(main -> {
    		if (receive != 0) {
    			this.sendDispatch(player, this.executorType, slot); 
    			this.taskOnReceive(player, slot, cooldown, (receive - 1), itemMap);
    		} 
    	}, cooldown);
    }
	
   /**
	* Checks if the player is able to execute the command.
	* 
	* @param player - player that is interacting with the custom items command.
	* @param action - the action that triggered the command execution.
	* @return If the player is able to execute the command.
	*/
	public boolean canExecute(final Player player, final String action, final String clickType) {
		if (this.command == null || this.command.length() == 0 || !this.actionType.hasClickType(clickType) || !this.actionType.hasAction(action)) { return false; }
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
		ServerHandler.getServer().runThread(main -> {
			if (this.getPending(player)) {
				if ((!this.actionType.equals(Action.ON_DEATH) && player.isDead()) || !player.isOnline() || player.getWorld() != world) {
					this.setExecute(player, true);
					this.setPending(player, false);
				} else { this.allowDispatch(player, world); }
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
	private void sendDispatch(final Player player, final Executor cmdtype, final String slot) {
		final World world = player.getWorld();
		this.setPending(player, true); 
		ServerHandler.getServer().runThread(main -> {
			this.allowDispatch(player, world);
			this.setPending(player, false);
			ItemMap itemMap = ItemUtilities.getUtilities().getItemMap(this.itemCopy, null, player.getWorld());
			if ((this.actionType.equals(Action.ON_DEATH) || !player.isDead()) && ((itemMap != null && ((this.actionType.equals(Action.ON_HOLD) && itemMap.isSimilar(PlayerHandler.getPlayer().getMainHandItem(player))) 
				|| (this.actionType.equals(Action.ON_RECEIVE) && itemMap.hasItem(player)))) || (!this.actionType.equals(Action.ON_HOLD) && !this.actionType.equals(Action.ON_RECEIVE))) 
				&& (player.isOnline() && player.getWorld() == world && !this.getExecute(player))) {
				switch (cmdtype) {
					case CONSOLE: this.dispatchConsoleCommands(player); break;
					case OP: this.dispatchOpCommands(player); break;
					case PLAYER: this.dispatchPlayerCommands(player); break;
					case MESSAGE: this.dispatchMessageCommands(player); break;
					case SERVERSWITCH: this.dispatchServerCommands(player); break;
					case BUNGEE: this.dispatchBungeeCordCommands(player); break;
					case SWAPITEM: this.dispatchSwapItem(player, slot); break;
					case DEFAULT: this.dispatchPlayerCommands(player); break;
					case DELAY: break;
					default: this.dispatchPlayerCommands(player); break;
				}
			} else if (this.getExecute(player)) { this.setExecute(player, false); }
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
				PlayerHandler.getPlayer().safeInventoryClose(player);
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
				PlayerHandler.getPlayer().safeInventoryClose(player);
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
				PlayerHandler.getPlayer().safeInventoryClose(player);
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
				if (item.getConfigName().equalsIgnoreCase(this.command) && slot != null) {
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
	public static ItemCommand fromString(String input, final Action action, final long delay, final String listSection) {
		if (input == null || input.length() == 0) { return new ItemCommand("", Action.DEFAULT, Executor.DEFAULT, 0L, null); }
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
		input = Utils.getUtils().colorFormat(input);
		return new ItemCommand(input, action, type, delay, listSection);
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
				new ItemCommand("", Action.DEFAULT, Executor.DEFAULT, 0L, null)
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
			arrayCommands.add(fromString(commandsList.get(i).trim(), getExactAction(itemMap, definition), delay, internalCommands));
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
		
		ON_RECEIVE(".on-receive", "ON_RECEIVE", "RECEIVED"),
		ON_HOLD(".on-hold", "ON_HOLD", "HELD"),
		ON_EQUIP(".on-equip", "ON_EQUIP", "EQUIPPED"),
		UN_EQUIP(".un-equip", "UN_EQUIP", "UNEQUIPPED"),
		ON_DEATH(".on-death", "ON_DEATH", "DEAD"),
		PHYSICAL(".physical", "PHYSICAL", "INTERACTED");
		
		private final String config;
		private final String actions;
		private final String clickType;
		private Action(String Config, String Actions, String ClickType) { this.config = Config; this.actions = Actions; this.clickType = ClickType; }
		public boolean hasConfig(String Config) { return this.config.contains(Config); }
		public boolean hasAction(String Action) { return Utils.getUtils().splitIgnoreCase(this.actions, Action, ","); }
		public boolean hasClickType(String ClickType) { return Utils.getUtils().splitIgnoreCase(this.clickType, ClickType, ","); }
	}
	
   /**
	* Defines the Sequence for the command.
	* 
	*/
	public enum CommandSequence { RANDOM, RANDOM_SINGLE, RANDOM_LIST, SEQUENTIAL, ALL; }
}