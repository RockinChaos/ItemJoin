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
package me.RockinChaos.itemjoin.utils.sqlite;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.utils.Utils;

public class SQLite {
	
	private Map < String, Integer > mapImages = new HashMap < String, Integer >();
	private Map < String, List <String> > ipLimitAddresses = new HashMap < String, List <String> >();
	private Map < String, List <String> > firstJoinPlayers = new HashMap < String, List <String> >();
	private Map < String, List <String> > firstCommandPlayers = new HashMap < String, List <String> >();
	private Map < String, List <String> > firstWorldPlayers = new HashMap < String, List <String> >();
	private Map < String, List <String> > enabledPlayers = new HashMap < String, List <String> >();
	private Map < String, String > returnCraftItems = new HashMap < String, String >();
	private Map < String, List <String> > returnItems = new HashMap < String, List <String> >();
	private Map < String, Long > onCooldown = new HashMap < String, Long >();
	private List <String> executeStatementsLater = new ArrayList<String>();
	
	private static SQLite lite;
	
   /**
	* Defines the existing tables.
	* 
	*/
    private enum Tables {
       IJ_FIRST_JOIN, IJ_FIRST_WORLD, IJ_IP_LIMITS, IJ_FIRST_COMMANDS, IJ_ENABLED_PLAYERS, IJ_RETURN_ITEMS, IJ_RETURN_CRAFTITEMS, IJ_ON_COOLDOWN, IJ_MAP_IDS
    }
	
   /**
    * Creates a new SQLData instance.
    * 
    */
	public SQLite() {
        ServerHandler.getServer().runAsyncThread(async -> {
			SQDrivers.getDatabase("database").loadSQLDatabase();
			this.createTables();
			this.convertYAMLS();
			this.loadCooldown();
			this.loadMapImages();
			this.loadFirstJoinPlayers();
			this.loadFirstWorldPlayers();
			this.loadFirstCommandPlayers();
			this.loadIPLimitAddresses();
			this.loadEnabledPlayers();
			this.loadReturnRegionItems();
			this.loadReturnCraftItems();
			try { SQDrivers.getDatabase("database").closeConnection(); } catch (Exception e) { } 
			this.executeSaveStatements();
		});
	}
	
   /**
    * Creates the missing database tables.
    * 
    */
	private void createTables() {
		this.alterTables();
        SQDrivers.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS ij_first_join (`World_Name` varchar(1000), `Player_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
        SQDrivers.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS ij_first_world (`World_Name` varchar(1000), `Player_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
        SQDrivers.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS ij_ip_limits (`World_Name` varchar(1000), `IP_Address` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
        SQDrivers.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS ij_first_commands (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Command_String` varchar(1000), `Time_Stamp` varchar(1000));");
        SQDrivers.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS ij_enabled_players (`World_Name` varchar(1000), `Player_Name` varchar(1000), `Player_UUID` varchar(1000), `isEnabled` varchar(1000), `Time_Stamp` varchar(1000));");
        SQDrivers.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS ij_return_items (`World_Name` varchar(1000), `Region_Name` varchar(1000), `Player_UUID` varchar(1000), `Inventory64` varchar(1000), `Time_Stamp` varchar(1000));");
        SQDrivers.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS ij_return_craftitems (`Player_UUID` varchar(1000), `Inventory64` varchar(1000), `Time_Stamp` varchar(1000));");
        SQDrivers.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS ij_on_cooldown (`World_Name` varchar(1000), `Item_Name` varchar(1000), `Player_UUID` varchar(1000), `Cooldown` varchar(1000), `Duration` varchar(1000), `Time_Stamp` varchar(1000));");
        SQDrivers.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS ij_map_ids (`Map_IMG` varchar(1000), `Map_ID` varchar(1000), `Time_Stamp` varchar(1000));");
	}
	
   /**
    * Alters any existing tables to fit the new TIME_STAMP datatype.
    * 
    */
	private void alterTables() {
		if (SQDrivers.getDatabase("database").tableExists("first_join") && !SQDrivers.getDatabase("database").columnExists("SELECT Time_Stamp FROM first_join")) {
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE first_join ADD Time_Stamp datatype;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE first_world ADD Time_Stamp datatype;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE ip_limits ADD Time_Stamp datatype;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE first_commands ADD Time_Stamp datatype;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE enabled_players ADD Time_Stamp datatype;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE map_ids ADD Time_Stamp datatype;");
		} 
		
		if (!SQDrivers.getDatabase("database").tableExists("ij_first_join") && SQDrivers.getDatabase("database").tableExists("first_join")) {
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE first_join RENAME TO ij_first_join;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE first_world RENAME TO ij_first_world;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE ip_limits RENAME TO ij_ip_limits;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE first_commands RENAME TO ij_first_commands;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE enabled_players RENAME TO ij_enabled_players;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE return_items RENAME TO ij_return_items;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE return_craftitems RENAME TO ij_return_craftitems;");
			SQDrivers.getDatabase("database").executeStatement("ALTER TABLE map_ids RENAME TO ij_map_ids;");
		}
	}
	
   /**
    * Executes pending datatypes to be saved to the SQL database.
    * 
    */
	public void executeLaterStatements() {
		if (this.executeStatementsLater != null && !this.executeStatementsLater.isEmpty()) {
			for (String statement : this.executeStatementsLater) {
				SQDrivers.getDatabase("database").executeStatement(statement);
			}
			ServerHandler.getServer().logDebug("{SQLite} Saving newly generated data to the database.");
			this.executeStatementsLater.clear();
			try { SQDrivers.getDatabase("database").closeConnection(); } catch (Exception e) { }
		}
	}
	
   /**
    * Saves the pending data to the database data file.
    * 
    */
	private void executeSaveStatements() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(ItemJoin.getInstance(), new Runnable() {
            @Override
			public void run() {
            	executeLaterStatements();
            	executeSaveStatements();
            }
        }, 36000L);
	}
	
   /**
    * Loads the map related data.
    * 
    */
	private void loadMapImages() {
		List<List<String>> selectedMapImages = SQDrivers.getDatabase("database").queryTableData("SELECT * FROM ij_map_ids", "Map_IMG", "Map_ID");
		if (selectedMapImages != null && !selectedMapImages.isEmpty()) {
			for (List<String> sl1 : selectedMapImages) {
				if (!this.imageNumberExists(sl1.get(0))) {
					this.mapImages.put(sl1.get(0), Utils.getUtils().returnInteger(sl1.get(1)));
				}	
			}
		}
	}
	
   /**
    * Loads the first join related data.
    * 
    */
	private void loadFirstJoinPlayers() {
		List < List < String > > selectedFirstJoinPlayers = SQDrivers.getDatabase("database").queryTableData("SELECT * FROM ij_first_join", "Item_Name", "Player_UUID");
		if (selectedFirstJoinPlayers != null && !selectedFirstJoinPlayers.isEmpty()) {
			for (List<String> sl1 : selectedFirstJoinPlayers) {
				if (this.firstJoinPlayers.get(sl1.get(1)) != null) {
					List <String> h1 = this.firstJoinPlayers.get(sl1.get(1));
					h1.add(sl1.get(0));
					this.firstJoinPlayers.put(sl1.get(1), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(0));
					this.firstJoinPlayers.put(sl1.get(1), h1);
				}
			}
		}
	}
	
   /**
    * Loads the first world related data.
    *
    */
	private void loadFirstWorldPlayers() {
		List<List<String>> selectedFirstWorldPlayers = SQDrivers.getDatabase("database").queryTableData("SELECT * FROM ij_first_world", "Item_Name", "Player_UUID", "World_Name");
		if (selectedFirstWorldPlayers != null && !selectedFirstWorldPlayers.isEmpty()) {
			for (List<String> sl1 : selectedFirstWorldPlayers) {
				if (this.firstWorldPlayers.get(sl1.get(1)) != null) {
					List <String> h1 = this.firstWorldPlayers.get(sl1.get(1));
					h1.add(sl1.get(2) + "." + sl1.get(0));
					this.firstWorldPlayers.put(sl1.get(1), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(2) + "." + sl1.get(0));
					this.firstWorldPlayers.put(sl1.get(1), h1);
				}
			}
		}
	}
	
   /**
    * Loads the first command related data.
    * 
    */
	private void loadFirstCommandPlayers() {
		List<List<String>> selectedFirstCommandPlayers = SQDrivers.getDatabase("database").queryTableData("SELECT * FROM ij_first_commands", "Command_String", "Player_UUID", "World_Name");
		if (selectedFirstCommandPlayers != null && !selectedFirstCommandPlayers.isEmpty()) {
			for (List<String> sl1 : selectedFirstCommandPlayers) {
				if (this.firstCommandPlayers.get(sl1.get(1)) != null) {
					List <String> h1 = this.firstCommandPlayers.get(sl1.get(1));
					h1.add(sl1.get(2) + "." + sl1.get(0));
					this.firstCommandPlayers.put(sl1.get(1), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(2) + "." + sl1.get(0));
					this.firstCommandPlayers.put(sl1.get(1), h1);
				}
			}
		}
	}
	
   /**
    * Loads the ip limit related data.
    * 
    */
	private void loadIPLimitAddresses() {
		List<List<String>> selectedIPLimitAddresses = SQDrivers.getDatabase("database").queryTableData("SELECT * FROM ij_ip_limits", "Item_Name", "Player_UUID", "World_Name", "IP_Address");
		if (selectedIPLimitAddresses != null && !selectedIPLimitAddresses.isEmpty()) {
			for (List<String> sl1 : selectedIPLimitAddresses) {
				if (this.ipLimitAddresses.get(sl1.get(1)) != null) {
					List <String> h1 = this.ipLimitAddresses.get(sl1.get(1));
					h1.add(sl1.get(2) + "." + sl1.get(3) + "." + sl1.get(0));
					this.ipLimitAddresses.put(sl1.get(1), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(2) + "." + sl1.get(3) + "." + sl1.get(0));
					this.ipLimitAddresses.put(sl1.get(1), h1);
				}
			}
		}
	}
	
   /**
    * Loads the enabled related data.
    * 
    */
	private void loadEnabledPlayers() {
		List<List<String>> selectedEnabledPlayers = SQDrivers.getDatabase("database").queryTableData("SELECT * FROM ij_enabled_players", "Player_UUID", "World_Name", "isEnabled");
		if (selectedEnabledPlayers != null && !selectedEnabledPlayers.isEmpty()) {
			for (List<String> sl1 : selectedEnabledPlayers) {
				if (this.enabledPlayers.get(sl1.get(0)) != null) {
					List <String> h1 = this.enabledPlayers.get(sl1.get(0));
					h1.add(sl1.get(1) + "." + sl1.get(2));
					this.enabledPlayers.put(sl1.get(0), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(1) + "." + sl1.get(2));
					this.enabledPlayers.put(sl1.get(0), h1);
				}
			}
		}
	}
	
   /**
    * Loads the return region related data.
    * 
    */
	private void loadReturnRegionItems() {
		List<List<String>> selectedReturnItems = SQDrivers.getDatabase("database").queryTableData("SELECT * FROM ij_return_items", "Player_UUID", "World_Name", "Region_Name", "Inventory64");
		if (selectedReturnItems != null && !selectedReturnItems.isEmpty()) {
			for (List<String> sl1 : selectedReturnItems) {
				if (this.returnItems.get(sl1.get(0)) != null) {
					List <String> h1 = this.returnItems.get(sl1.get(0));
					h1.add(sl1.get(1) + "." + sl1.get(2) + "." + sl1.get(3));
					this.returnItems.put(sl1.get(0), h1);
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(sl1.get(1) + "." + sl1.get(2) + "." + sl1.get(3));
					this.returnItems.put(sl1.get(0), h1);
				}
			}
		}
	}
	
   /**
    * Loads the return crafting related data.
    * 
    */
	private void loadReturnCraftItems() {
		List<List<String>> selectedReturnCraftItems = SQDrivers.getDatabase("database").queryTableData("SELECT * FROM ij_return_craftitems", "Player_UUID", "Inventory64");
		if (selectedReturnCraftItems != null && !selectedReturnCraftItems.isEmpty()) {
			for (List<String> sl1 : selectedReturnCraftItems) {
				this.returnCraftItems.put(sl1.get(0), sl1.get(1));
			}
		}
	}
	
   /**
    * Loads the players on cooldown and their relative items.
    * 
    */
	private void loadCooldown() {
		List<List<String>> selectedCooldownItems = SQDrivers.getDatabase("database").queryTableData("SELECT * FROM ij_on_cooldown", "Cooldown", "Item_Name", "Duration", "World_Name", "Player_UUID");
		if (selectedCooldownItems != null && !selectedCooldownItems.isEmpty()) {
			for (List<String> sl1 : selectedCooldownItems) {
				this.onCooldown.put(sl1.get(1) + "._." + sl1.get(2) + ".__." + sl1.get(3) + "-.-" + sl1.get(4), Long.parseLong(sl1.get(0)));
			}
		}
	}
	
   /**
    * Saves the first join related data.
    * 
    * @param player - The player being saved.
    * @param itemMap - The ItemMap being saved.
    */
	public void saveFirstJoinData(Player player, ItemMap itemMap) {
		if ((itemMap.isOnlyFirstJoin() || itemMap.isOnlyFirstLife()) && !this.hasFirstJoined(player, itemMap)) {
			this.executeStatementsLater.add("INSERT INTO ij_first_join (`World_Name`, `Player_Name`, `Player_UUID`, `Item_Name`, `Time_Stamp`) VALUES ('" + player.getWorld().getName() + "','" + player.getName().toString() + "','" + PlayerHandler.getPlayer().getPlayerID(player) + "','" + itemMap.getConfigName() + "','" + new Timestamp(System.currentTimeMillis()) + "')");
			if (this.firstJoinPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)) != null) {
				List <String> h1 = this.firstJoinPlayers.get(PlayerHandler.getPlayer().getPlayerID(player));
				h1.add(itemMap.getConfigName());
				this.firstJoinPlayers.put(PlayerHandler.getPlayer().getPlayerID(player), h1);
			} else {
				List <String> h1 = new ArrayList<String>();
				h1.add(itemMap.getConfigName());
				this.firstJoinPlayers.put(PlayerHandler.getPlayer().getPlayerID(player), h1);
			}
		}
	}
	
   /**
    * Saves the first world related data.
    * 
    * @param player - The player being saved.
    * @param itemMap - The ItemMap being saved.
    */
	public void saveFirstWorldData(Player player, ItemMap itemMap) {
		if (itemMap.isOnlyFirstWorld()) {
			this.executeStatementsLater.add("INSERT INTO ij_first_world (`World_Name`, `Player_Name`, `Player_UUID`, `Item_Name`, `Time_Stamp`) VALUES ('" + player.getWorld().getName() + "','" + player.getName().toString() + "','" + PlayerHandler.getPlayer().getPlayerID(player) + "','" + itemMap.getConfigName() + "','" + new Timestamp(System.currentTimeMillis()) + "')");
			if (this.firstWorldPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)) != null) {
				List <String> h1 = this.firstWorldPlayers.get(PlayerHandler.getPlayer().getPlayerID(player));
				h1.add(player.getWorld().getName() + "." + itemMap.getConfigName());
				this.firstWorldPlayers.put(PlayerHandler.getPlayer().getPlayerID(player), h1);
			} else {
				List <String> h1 = new ArrayList<String>();
				h1.add(player.getWorld().getName() + "." + itemMap.getConfigName());
				this.firstWorldPlayers.put(PlayerHandler.getPlayer().getPlayerID(player), h1);
			}
		}
	}
	
   /**
    * Saves the ip limit related data.
    * 
    * @param player - The player being saved.
    * @param itemMap - The ItemMap being saved.
    */
	public void saveIpLimitData(Player player, ItemMap itemMap) {
		if (itemMap.isIpLimted()) {
			this.executeStatementsLater.add("INSERT INTO ij_ip_limits (`World_Name`, `IP_Address`, `Player_UUID`, `Item_Name`, `Time_Stamp`) VALUES ('" + player.getWorld().getName() + "','" + player.getAddress().getHostString() + "','" + PlayerHandler.getPlayer().getPlayerID(player) + "','" + itemMap.getConfigName() + "','" + new Timestamp(System.currentTimeMillis()) + "')");
			if (this.ipLimitAddresses.get(PlayerHandler.getPlayer().getPlayerID(player)) != null) {
				List <String> h1 = this.ipLimitAddresses.get(PlayerHandler.getPlayer().getPlayerID(player));
				h1.add(player.getWorld().getName() + "." + player.getAddress().getHostString() + "." + itemMap.getConfigName());
				this.ipLimitAddresses.put(PlayerHandler.getPlayer().getPlayerID(player), h1);
			} else {
				List <String> h1 = new ArrayList<String>();
				h1.add(player.getWorld().getName() + "." + player.getAddress().getHostString() + "." + itemMap.getConfigName());
				this.ipLimitAddresses.put(PlayerHandler.getPlayer().getPlayerID(player), h1);
			}
		}
	}
	
   /**
    * Saves the first command related data.
    * 
    * @param player - The player being saved.
    * @param command - The command being saved.
    */
	public void saveFirstCommandData(Player player, String command) {
		this.executeStatementsLater.add("INSERT INTO ij_first_commands (`World_Name`, `Player_UUID`, `Command_String`, `Time_Stamp`) VALUES ('" + player.getWorld().getName() + "','" + PlayerHandler.getPlayer().getPlayerID(player) + "','" + command + "','" + new Timestamp(System.currentTimeMillis()) + "')");
		if (this.firstCommandPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)) != null) {
			List <String> h1 = this.firstCommandPlayers.get(PlayerHandler.getPlayer().getPlayerID(player));
			h1.add(player.getWorld().getName() + "." + command);
			this.firstCommandPlayers.put(PlayerHandler.getPlayer().getPlayerID(player), h1);
		} else {
			List <String> h1 = new ArrayList<String>();
			h1.add(player.getWorld().getName() + "." + command);
			this.firstCommandPlayers.put(PlayerHandler.getPlayer().getPlayerID(player), h1);
		}
	}
	
   /**
    * Saves the return region related data.
    * 
    * @param player - The player being saved.
    * @param world - The world being saved.
    * @param region - The region being saved.
    * @param inventory - The inventory items being saved.
    */
	public void saveReturnRegionItems(Player player, String world, String region, Inventory inventory) {
		String inventory64 = world + "." + region + "." + ItemHandler.getItem().serializeInventory(inventory);
		this.executeStatementsLater.add("INSERT INTO ij_return_items (`World_Name`, `Region_Name`, `Player_UUID`, `Inventory64`, `Time_Stamp`) VALUES ('" + world + "','" + region + "','" + PlayerHandler.getPlayer().getPlayerID(player) + "','" + ItemHandler.getItem().serializeInventory(inventory) + "','" + new Timestamp(System.currentTimeMillis()) + "')");
		if (this.returnItems.get(PlayerHandler.getPlayer().getPlayerID(player)) != null && Utils.getUtils().containsIgnoreCase(this.returnItems.get(PlayerHandler.getPlayer().getPlayerID(player)).toString(), world + "." + region)) {
			return;
	    } else if (this.returnItems.get(PlayerHandler.getPlayer().getPlayerID(player)) != null) {
			List <String> h1 = this.returnItems.get(PlayerHandler.getPlayer().getPlayerID(player));
			h1.add(inventory64);
			this.returnItems.put(PlayerHandler.getPlayer().getPlayerID(player), h1);
		} else {
			List <String> h1 = new ArrayList<String>();
			h1.add(inventory64);
			this.returnItems.put(PlayerHandler.getPlayer().getPlayerID(player), h1);
		}
	}
	
   /**
    * Saves the return crafting related data.
    * 
    * @param player - The player being saved.
    * @param inventory - The inventory items being saved.
    */
	public void saveReturnCraftItems(Player player, Inventory inventory) {
		String inventory64 = ItemHandler.getItem().serializeInventory(inventory);
		this.executeStatementsLater.add("INSERT INTO ij_return_craftitems (`Player_UUID`, `Inventory64`, `Time_Stamp`) VALUES ('" + PlayerHandler.getPlayer().getPlayerID(player) + "','" + ItemHandler.getItem().serializeInventory(inventory) + "','" + new Timestamp(System.currentTimeMillis()) + "')");
		this.returnCraftItems.put(PlayerHandler.getPlayer().getPlayerID(player), inventory64);
	}
	
   /**
    * Saves the players on cooldown related data.
    * 
    * @param item - The item on cooldown.
    * @param player - The player on cooldown.
    * @param worldName - The world name of the item on cooldown.
    * @param cooldown - The current system time.
    * @param duration - The duration of cooldown expected.
    */
	public void saveCooldown(String item, String player, String worldName, long cooldown, int duration) {
		this.executeStatementsLater.add("INSERT INTO ij_on_cooldown (`World_Name`, `Item_Name`, `Player_UUID`, `Cooldown`, `Duration`, `Time_Stamp`) VALUES ('" + worldName + "','" + item + "','" + player + "','" + cooldown + "','" + duration + "','" + new Timestamp(System.currentTimeMillis()) + "')");
	}
	
   /**
    * Saves the enabled and/or disabled players related data.
    * 
    * @param player - The player being saved.
    * @param world - The world being saved.
    * @param boolValue - The boolean value being saved.
    * @param type - Saving as enabled or disabled type.
    */
	public void saveToDatabase(Player player, String world, String boolValue, String type) {
			String realPlayer = "ALL"; String realName = "ALL";
			if (player != null) { realPlayer = PlayerHandler.getPlayer().getPlayerID(player); realName = player.getName().toString(); }
			if (type.contains("enabled-players") || type.contains("disabled-players")) {
				if (this.enabledPlayers.get(realPlayer) != null) {
					List <String> h1 = this.enabledPlayers.get(realPlayer);
					if (Utils.getUtils().containsIgnoreCase(h1.toString(), world + ".")) {
						for (int i = 0; i < h1.size(); i++) {
							if (Utils.getUtils().containsIgnoreCase(h1.get(i), world + ".")) {
								h1.remove(i);
							}
						}
					}
					h1.add(world + "." + boolValue);
					this.enabledPlayers.put(realPlayer, h1);
					this.executeStatementsLater.add("UPDATE ij_enabled_players SET IsEnabled='" + boolValue + "' WHERE World_Name='" + world + "' AND Player_UUID='" + realPlayer + "'");
				} else {
					List <String> h1 = new ArrayList<String>();
					h1.add(world + "." + boolValue);
					this.enabledPlayers.put(realPlayer, h1);
					this.executeStatementsLater.add("INSERT INTO ij_enabled_players (`World_Name`, `Player_Name`, `Player_UUID`, `isEnabled`, `Time_Stamp`) VALUES ('" + world + "','" + realName + "','" + realPlayer + "','" + boolValue + "','" + new Timestamp(System.currentTimeMillis()) + "')");
				}
			}
	}
	
   /**
    * Saves the item data related data.
    * 
    * @param player - The player being saved.
    * @param itemMap - The ItemMap being saved.
    */
	public void saveItemData(Player player, ItemMap itemMap) {
		this.saveFirstJoinData(player, itemMap);
		this.saveFirstWorldData(player, itemMap);
		this.saveIpLimitData(player, itemMap);
	}
	
   /**
    * Removed specified from the database data.
    * 
    * @param player - The player being removed.
    * @param section - The datatype being removed.
    */
	public void purgeDatabaseData(OfflinePlayer player, String image, String section) {
		if (section.equalsIgnoreCase("map_ids")) {
			if (this.mapImages.values() != null && !this.mapImages.isEmpty()) {
				this.executeStatementsLater.add("DELETE FROM ij_" + section + " WHERE Map_IMG='" + image + "';");
				this.mapImages.remove(image);
			}
		} else { 
			String UUID = (player == null ? "ALL" : PlayerHandler.getPlayer().getOfflinePlayerID(player));
			if (section.equalsIgnoreCase("first_join") && this.firstJoinPlayers.values() != null && !this.firstJoinPlayers.isEmpty()) {
				this.executeStatementsLater.add("DELETE FROM ij_" + section + " WHERE Player_UUID='" + UUID + "';");
				this.firstJoinPlayers.remove(UUID);
			} else if (section.equalsIgnoreCase("first_world") && this.firstWorldPlayers.values() != null && !this.firstWorldPlayers.isEmpty()) {
				this.executeStatementsLater.add("DELETE FROM ij_" + section + " WHERE Player_UUID='" + UUID + "';");
				this.firstWorldPlayers.remove(UUID);
			} else if (section.equalsIgnoreCase("ip_limits") && this.ipLimitAddresses.values() != null && !this.ipLimitAddresses.isEmpty()) {
				this.executeStatementsLater.add("DELETE FROM ij_" + section + " WHERE Player_UUID='" + UUID + "';");
				this.ipLimitAddresses.remove(UUID);
			} else if (section.equalsIgnoreCase("enabled_players") && this.enabledPlayers.values() != null && !this.enabledPlayers.isEmpty()) {
				this.executeStatementsLater.add("DELETE FROM ij_" + section + " WHERE Player_UUID='" + UUID + "';");
				this.enabledPlayers.remove(UUID);
			}
		}
	}
	
   /**
    * Removes ij_* tables from the database.
    * 
    */
	public void purgeDatabase() {
		for (Tables table: Tables.values()) {
			SQDrivers.getDatabase("database").executeStatement("DROP TABLE IF EXISTS " + table.name().toLowerCase());
		}
	}
	
   /**
    * Removes the return region related data.
    * 
    * @param player - The player being removed.
    * @param world - The world being saved.
    * @param region - The region being saved.
    */
	public void removeReturnRegionItems(Player player, String world, String region) {
		List <String> h1 = this.returnItems.get(PlayerHandler.getPlayer().getPlayerID(player));
		if (this.returnItems.values() != null && !this.returnItems.isEmpty() && h1 != null && !h1.isEmpty()) {
			this.executeStatementsLater.add("DELETE FROM ij_return_Items WHERE Player_UUID='" + PlayerHandler.getPlayer().getPlayerID(player) + "' AND World_Name='" + world + "' AND Region_Name='" + region + "';");
			for (String inventory : h1) {
				if (Utils.getUtils().containsIgnoreCase(inventory, world + "." + region + ".")) {
					h1.remove(inventory);
					break;
				}
			}
			this.returnItems.put(PlayerHandler.getPlayer().getPlayerID(player), h1);
		}
	}
	
   /**
    * Removes the return crafting related data.
    * 
    * @param player - The player being removed.
    */
	public void removeReturnCraftItems(Player player) {
		String h1 = this.returnCraftItems.get(PlayerHandler.getPlayer().getPlayerID(player));
		if (this.returnCraftItems.values() != null && !this.returnCraftItems.isEmpty() && h1 != null && !h1.isEmpty()) {
			this.executeStatementsLater.add("DELETE FROM ij_return_craftitems WHERE Player_UUID='" + PlayerHandler.getPlayer().getPlayerID(player) + "';");
			this.returnCraftItems.remove(PlayerHandler.getPlayer().getPlayerID(player));
		}
	}
	
   /**
    * Checks if the player has executed the first time execute command.
    * 
    * @param player - The player being checked.
    * @param command - The command being checked.
    * @return If the player has first time executed the command.
    */
	public Boolean hasFirstCommanded(Player player, String command) {
		if (this.firstCommandPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)) != null
				&& Utils.getUtils().containsIgnoreCase(this.firstCommandPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)).toString(), player.getWorld().getName() + "." + command.replace("first-join: ", "").replace("first-join:", ""))) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the player has executed the first join ItemMap.
    * 
    * @param player - The player being checked.
    * @param itemMap - The ItemMap being checked.
    * @return If the player has first joined.
    */
	public Boolean hasFirstJoined(Player player, ItemMap itemMap) {
		if ((itemMap.isOnlyFirstJoin() || itemMap.isOnlyFirstLife()) && this.firstJoinPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)) != null 
				&& Utils.getUtils().containsIgnoreCase(this.firstJoinPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)).toString(), itemMap.getConfigName())) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the player has executed the first world ItemMap.
    * 
    * @param player - The player being checked.
    * @param itemMap - The ItemMap being checked.
    * @return If the player has first world.
    */
	public Boolean hasFirstWorld(Player player, ItemMap itemMap) {
		if (itemMap.isOnlyFirstWorld() && this.firstWorldPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)) != null 
			&& Utils.getUtils().containsIgnoreCase(this.firstWorldPlayers.get(PlayerHandler.getPlayer().getPlayerID(player)).toString(), player.getWorld().getName() + "." + itemMap.getConfigName())) {
			return true;
		}
		return false;
	}
	
   /**
    * Gets the return region items.
    * 
    * @param player - The player being fetched.
    * @param world - The world being fetched.
    * @param region - The region to be fetched.
    * @return The inventory to be returned.
    */
	public Inventory getReturnRegionItems(Player player, String world, String region) {
		if (this.returnItems.get(PlayerHandler.getPlayer().getPlayerID(player)) != null) {
			for (String inventory : this.returnItems.get(PlayerHandler.getPlayer().getPlayerID(player))) {
				if (Utils.getUtils().containsIgnoreCase(inventory, world) && Utils.getUtils().containsIgnoreCase(inventory, region)) {
					return ItemHandler.getItem().deserializeInventory(inventory.replace(world + "." + region + ".", ""));
				}
			}
		}
		return null;
	}
	
   /**
    * Gets the return crafting items.
    * 
    * @param player - The player being fetched.
    * @return The inventory to be returned.
    */
	public Inventory getReturnCraftItems(Player player) {
		if (this.returnCraftItems.get(PlayerHandler.getPlayer().getPlayerID(player)) != null) {
			return ItemHandler.getItem().deserializeInventory(this.returnCraftItems.get(PlayerHandler.getPlayer().getPlayerID(player)));
		}
		return null;
	}
	
   /**
    * Gets the players on cooldown for the item.
    * 
    * @param item - The item to be fetched.
    * @return The players on cooldown for the item.
    */
	public Map<String, Long> getCooldown(ItemMap itemMap) {
		Map < String, Long > playersOnCooldown = new HashMap < String, Long > ();
		for (String keys: this.onCooldown.keySet()) {
			String[] parts1 = keys.split(".__.");
			String[] parts2 = parts1[0].split("._.");
			if (parts2[0].equalsIgnoreCase(itemMap.getConfigName()) && String.valueOf(itemMap.getCommandCooldown()).equalsIgnoreCase(parts2[1])) {
				playersOnCooldown.put(parts1[1], this.onCooldown.get(keys));
				this.onCooldown.remove(keys);
			}
		}
		this.executeStatementsLater.add("DELETE FROM ij_on_cooldown WHERE Item_Name='" + itemMap.getConfigName() + "';");
		return playersOnCooldown;
	}
	
   /**
    * Gets the currently rendered map images.
    * 
    * @return The HashMap of images for custom items.
    */
	public Map<String, Integer> getMapImages() {
		return this.mapImages;
	}
	
   /**
    * Gets the first join player items.
    * 
    * @return The HashMap of players and their first join items.
    */
	public Map<String, List<String>> getFirstPlayers() {
		return this.firstJoinPlayers;
	}
	
   /**
    * Gets the first join player items.
    * 
    * @return The HashMap of players and their first join items.
    */
	public Map<String, List<String>> getFirstWorlds() {
		return this.firstWorldPlayers;
	}
	
   /**
    * Gets the ip limited player items.
    * 
    * @return The HashMap of players and their ip limited items.
    */
	public Map<String, List<String>> getLimitPlayers() {
		return this.ipLimitAddresses;
	}
	
   /**
    * Gets the players who have items enabled.
    * 
    * @return The HashMap of players that have items enabled.
    */
	public Map<String, List<String>> getEnabledPlayers() {
		return this.enabledPlayers;
	}
	
   /**
    * Checks if the players ItemMap is ip limited.
    * 
    * @param player - The player being checked.
    * @param itemMap - The ItemMap being checked.
    * @return If the players ItemMap is ip limited.
    */
	public Boolean isIPLimited(Player player, ItemMap itemMap) {
		if (itemMap.isIpLimted()) {
			for (String playerValue : this.ipLimitAddresses.keySet()) {
				if (Utils.getUtils().containsIgnoreCase(this.ipLimitAddresses.get(playerValue).toString(), player.getWorld().getName() + "." + player.getAddress().getHostString() + "." + itemMap.getConfigName())
						|| Utils.getUtils().containsIgnoreCase(this.ipLimitAddresses.get(playerValue).toString(), player.getWorld().getName() + "." + player.getAddress().getHostString().replace(".", "") + "." + itemMap.getConfigName())) {
					if (PlayerHandler.getPlayer().getPlayerID(player).equalsIgnoreCase(playerValue)) {
						return false;
					} else { return true; }
				}
			}
		}
		return false;
	}
	
   /**
    * Saves the specified ItemMap and its image id to the database.
    * 
    * @param itemMap - The itemMap being saved.
    */
	public void saveMapImage(ItemMap itemMap) {
		if (!this.imageNumberExists(itemMap.getMapImage())) {
			this.executeStatementsLater.add("INSERT INTO ij_map_ids (`Map_IMG`, `Map_ID`, `Time_Stamp`) VALUES ('" + itemMap.getMapImage() + "','" + itemMap.getMapID() + "','" + new Timestamp(System.currentTimeMillis()) + "')");
			this.mapImages.put(itemMap.getMapImage(), itemMap.getMapID());
		}
	}
	
   /**
    * Removes the specified ItemMap from the database.
    * 
    * @param itemMap - The itemMap being removed.
    */
	public void purgeMapImage(ItemMap itemMap) {
		if (this.imageNumberExists(itemMap.getMapImage()) && mapImages.values() != null && !this.mapImages.isEmpty()) {
			this.executeStatementsLater.add("DELETE FROM ij_map_ids WHERE Map_IMG='" + itemMap.getMapImage() + "';");
			this.mapImages.remove(itemMap.getMapImage());
		}
	}
	
   /**
    * Checks if the specified map image is already rendered.
    * 
    * @param image - The image being checked.
    * @return If the image is already rendered.
    */
	public Boolean imageNumberExists(String image) {
		if (this.mapImages.get(image) != null) {
			return true;
		}
		return false;
	}
	
   /**
    * Gets the current image number for the already rendered image.
    * 
    * @param player - that will recieve the items.
    */
	public int getImageNumber(String image) {
		if (this.mapImages.get(image) != null) {
			return this.mapImages.get(image);
		}
		return 0;
	}
	
   /**
    * Checks if the player has their items enabled.
    * 
    * @param player - The player being checked.
    * @return If the player is enabled.
    */
	public boolean isEnabled(final Player player) {
		String UUID = PlayerHandler.getPlayer().getPlayerID(player);
		if (this.enabledPlayers.get(UUID) == null) { UUID = "ALL"; }
		if (this.enabledPlayers.get(UUID) != null && (Utils.getUtils().containsIgnoreCase(this.enabledPlayers.get(UUID).toString(), player.getWorld().getName() + "." + "false") 
		 || Utils.getUtils().containsIgnoreCase(this.enabledPlayers.get(UUID).toString(), "Global" + "." + "false"))) {
			return false;
		}
		return true;
	}
	
   /**
    * Checks if the player is writable for disabling items.
    * 
    * @param world - The name of the world being checked.
    * @param playerString - The UUID of the player being checked.
    * @return If the player has their items enabled.
    */
	public boolean isWritable(String world, String playerString, boolean enabled) {
		if (this.enabledPlayers.get(playerString) == null) { playerString = "ALL"; world = "Global"; }
		if (this.enabledPlayers.get(playerString) != null && Utils.getUtils().containsIgnoreCase(this.enabledPlayers.get(playerString).toString(), world + "." + "false")) {
				return false;
		}
		return true;
	}

   /**
    * Converts the old .YML data files to the new SQL Database.
    * 
    */
	public void convertYAMLS() {
		File firstJoin = new File(ItemJoin.getInstance().getDataFolder(), "first-join.yml");
		File ipLimit = new File(ItemJoin.getInstance().getDataFolder(), "ip-limit.yml");
		boolean converting = false;
		if (firstJoin.exists() || ipLimit.exists()) {
			if (firstJoin.exists()) {
				this.convertFirstJoinData(firstJoin);
				ServerHandler.getServer().logWarn(("The first-join.yml file is outdated, all data is now stored in a database file."));
			}
			if (ipLimit.exists()) {
				this.convertIpLimitData(ipLimit);
				ServerHandler.getServer().logWarn(("The ip-limit.yml file is outdated, all data is now stored in a database file."));
			}
			converting = true;
			ServerHandler.getServer().logWarn("Starting YAML to Database conversion, stored data in the file(s) will not be lost...");
			
		}
		if (converting == true) { ServerHandler.getServer().logWarn("YAML to Database conversion complete!"); }
	}
	
   /**
    * Converts the first join data to the new SQL Database.
    * 
    * @param firstJoin - The file being converted.
    */
	private void convertFirstJoinData(File firstJoin) {
		try {
			YamlConfiguration configSection = YamlConfiguration.loadConfiguration(firstJoin);
			for (String worldsec: configSection.getKeys(false)) {
				ConfigurationSection world = configSection.getConfigurationSection(worldsec);
				for (String itemsec: world.getKeys(false)) {
					ConfigurationSection item = world.getConfigurationSection(itemsec);
					for (String uuidsec: item.getKeys(false)) {
						ConfigurationSection uuid = item.getConfigurationSection(uuidsec);
						OfflinePlayer player = ItemJoin.getInstance().getServer().getOfflinePlayer(UUID.fromString(uuid.getName()));
						if (!SQDrivers.getDatabase("database").dataExists("SELECT * FROM ij_first_join WHERE World_Name='" + world.getName() + "' AND Player_UUID='" + uuid.getName() + "' AND Item_Name='" + item.getName() + "';")) {
							SQDrivers.getDatabase("database").executeStatement("INSERT INTO ij_first_join (`World_Name`, `Player_Name`, `Player_UUID`, `Item_Name`) VALUES ('" + world.getName() + "','" + player.getName().toString() + "','" + uuid.getName() + "','" + item.getName() + "')");
							SQDrivers.getDatabase("database").closeConnection();
						}
					}
				}
			}
			File userfiles = new File(ItemJoin.getInstance().getDataFolder() + File.separator + "backup");
			if (!userfiles.exists()) {
				userfiles.mkdirs();
			}
			String newGen = "converted" + Utils.getUtils().getRandom(0, 100) + "-first-join.yml";
			File newFile = new File(userfiles, newGen);
			firstJoin.renameTo(newFile);
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} Failed to convert the first-join.yml to the database!");
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
    * Converts the ip limited data to the new SQL Database.
    * 
    * @param ipLimit - The file being converted.
    */
	private void convertIpLimitData(File ipLimit) {
		try {
			YamlConfiguration configSection = YamlConfiguration.loadConfiguration(ipLimit);
			for (String worldsec: configSection.getKeys(false)) {
				ConfigurationSection world = configSection.getConfigurationSection(worldsec);
				for (String itemsec: world.getKeys(false)) {
					ConfigurationSection item = world.getConfigurationSection(itemsec);
					for (String ipaddrsec: item.getKeys(false)) {
						ConfigurationSection ipaddr = item.getConfigurationSection(ipaddrsec);
						if (!SQDrivers.getDatabase("database").dataExists("SELECT * FROM ij_ip_limits WHERE World_Name='" + world.getName() + "' AND IP_Address='" + ipaddr.getName() + "' AND Item_Name='" + item.getName() + "';")) {
							SQDrivers.getDatabase("database").executeStatement("INSERT INTO ij_ip_limits (`World_Name`, `IP_Address`, `Player_UUID`, `Item_Name`) VALUES ('" + world.getName() + "','" + ipaddr.getName() + "','" + ipaddr.get("Current User") + "','" + item.getName() + "')");
							SQDrivers.getDatabase("database").closeConnection();
						}
					}
				}
			}
			File userfiles = new File(ItemJoin.getInstance().getDataFolder() + File.separator + "backup");
			if (!userfiles.exists()) {
				userfiles.mkdirs();
			}
			String newGen = "converted" + Utils.getUtils().getRandom(0, 100) + "-ip-limit.yml";
			File newFile = new File(userfiles, newGen);
			ipLimit.renameTo(newFile);
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} Failed to convert the ip-limit.yml to the database!");
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
    * Gets the instance of the SQLite.
    * 
    * @param regen - If the SQLite should have a new instance created.
    * @return The SQLite instance.
    */
    public static SQLite getLite(boolean regen) { 
        if (lite == null || regen) { lite = new SQLite(); }
        return lite; 
    } 
}