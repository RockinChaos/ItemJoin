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
package me.RockinChaos.itemjoin.utils.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;

public class SQL {
	
	private Map < String, List<DataObject> > databaseData = new HashMap < String, List<DataObject> >();
	
	private static SQL data;
	
   /**
    * Creates a new SQLData instance.
    * 
    */
	public SQL() {
		Database.kill(); {
			this.createTables();
			this.loadData();
			ServerHandler.getServer().logDebug("{SQL} Database Connected."); 
		}
	}
	
   /**
    * Removes ij_* tables from the database.
    * 
    */
	public void purgeDatabase() {
		this.databaseData.clear();
		SchedulerUtils.getScheduler().runAsync(() -> {
			for (Table table: Table.values()) {
				Database.getDatabase().executeStatement("DROP TABLE IF EXISTS " + table.name().toLowerCase());
			}
		});
	}
	
   /**
    * Saves the table data for the specified DataObject.
    * 
    * @param object - The DataObject data being saved.
    */
	public void saveData(DataObject object) {
		if (object != null) { 
			String table = object.getTable().name().toLowerCase();
			if (ItemJoin.getInstance().isEnabled()) {
				SchedulerUtils.getScheduler().runAsync(() -> {
					Database.getDatabase().executeStatement("INSERT INTO " + object.getTable().name().toLowerCase() + " (" + object.getTable().headers() + ") VALUES (" + object.getInsertValues() + ")");
				});
			} else {
				Database.getDatabase().executeStatement("INSERT INTO " + object.getTable().name().toLowerCase() + " (" + object.getTable().headers() + ") VALUES (" + object.getInsertValues() + ")");
			}
			if (this.databaseData.get(table) != null) {
				List <DataObject> h1 = this.databaseData.get(table);
				h1.add(object);
				this.databaseData.put(table, h1);
			} else {
				List <DataObject> h1 = new ArrayList<DataObject>();
				h1.add(object);
				this.databaseData.put(table, h1);
			}
		}
	}
	
   /**
    * Removes the table data for the specified DataObject.
    * 
    * @param object - The DataObject being accessed.
    */
	public void removeData(DataObject object) {
		if (object != null) { 
			String table = object.getTable().name().toLowerCase();
			if (this.databaseData.get(table) != null && !this.databaseData.get(table).isEmpty()) {
				Iterator<DataObject> dataSet = this.databaseData.get(table).iterator();
				while (dataSet.hasNext()) {
					DataObject dataObject = dataSet.next();
					if (dataObject != null && dataObject.getTable().equals(object.getTable()) && object.equalsData(object, dataObject)) {
						if (ItemJoin.getInstance().isEnabled()) {
							SchedulerUtils.getScheduler().runAsync(() -> {
								Database.getDatabase().executeStatement("DELETE FROM " + dataObject.getTable().name().toLowerCase() + " WHERE (" + dataObject.getTable().removal() + ") = (" + dataObject.getRemovalValues() + ")");
							});
						} else {
							Database.getDatabase().executeStatement("DELETE FROM " + dataObject.getTable().name().toLowerCase() + " WHERE (" + dataObject.getTable().removal() + ") = (" + dataObject.getRemovalValues() + ")");
						}
						dataSet.remove();
					}
				}
			}
		}
	}
	
   /**
    * Gets the table data for the specified DataObject.
    * 
    * @param object - The DataObject being accessed.
    * @return The found table data.
    */
	public DataObject getData(DataObject object) {
		if (object != null) { 
			String table = object.getTable().name().toLowerCase();
			if (this.databaseData.get(table) != null && !this.databaseData.get(table).isEmpty()) {
				Iterator<DataObject> dataSet = this.databaseData.get(table).iterator();
				while (dataSet.hasNext()) {
					DataObject dataObject = dataSet.next();
					if (dataObject != null && dataObject.getTable().equals(object.getTable()) && object.equalsData(object, dataObject)) {
						return dataObject;
					}
				}
			}
		}
		return null;
	}
	
   /**
    * Gets the table data list for the specified DataObject.
    * 
    * @param object - The DataObject being accessed.
    * @return The found table data list.
    */
	public List<DataObject> getDataList(DataObject object) {
		List<DataObject> dataList = new ArrayList<DataObject>();
		String table = object.getTable().name().toLowerCase();
		if (this.databaseData.get(table) != null && !this.databaseData.get(table).isEmpty()) {
			Iterator<DataObject> dataSet = this.databaseData.get(table).iterator();
			while (dataSet.hasNext()) {
				DataObject dataObject = dataSet.next();
				if (dataObject != null && dataObject.getTable().equals(object.getTable()) && (object.isTemporary() || object.equalsData(object, dataObject))) {
					dataList.add(dataObject);
				}
			}
		}
		return dataList;
	}
	
   /**
    * Loads all the database data into memory.
    * 
    */
	private void loadData() {
		for (Table tableEnum: Table.values()) {
			String table = tableEnum.name().toLowerCase();
			List<HashMap<String, String>> selectTable = Database.getDatabase().queryTableData("SELECT * FROM " + table, tableEnum.headers().replace("`", ""));
			if (selectTable != null && !selectTable.isEmpty()) {
				for (HashMap<String, String> sl1 : selectTable) {
					DataObject dataObject = null;
					if (tableEnum.equals(Table.IJ_FIRST_JOIN)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), "", sl1.get("Item_Name"));
					} else if (tableEnum.equals(Table.IJ_FIRST_WORLD)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Item_Name"));
					} else if (tableEnum.equals(Table.IJ_IP_LIMITS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Item_Name"), sl1.get("IP_Address"));
					} else if (tableEnum.equals(Table.IJ_FIRST_COMMANDS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Command_String"));
					} else if (tableEnum.equals(Table.IJ_ENABLED_PLAYERS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("isEnabled"));
					} else if (tableEnum.equals(Table.IJ_RETURN_ITEMS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Region_Name"), sl1.get("Inventory64"));
					} else if (tableEnum.equals(Table.IJ_RETURN_CRAFTITEMS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), "", sl1.get("Inventory64"));
					} else if (tableEnum.equals(Table.IJ_RETURN_SWITCH_ITEMS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Inventory64"));
					} else if (tableEnum.equals(Table.IJ_ON_COOLDOWN)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Item_Name"), sl1.get("Cooldown"), sl1.get("Duration"));
					} else if (tableEnum.equals(Table.IJ_MAP_IDS)) {
						dataObject = new DataObject(tableEnum, null, null, sl1.get("Map_IMG"), sl1.get("Map_ID"));
					}
					dataObject.setTimeStamp(sl1.get("Time_Stamp"));
					List <DataObject> dataSet = (this.databaseData.get(table) != null ? this.databaseData.get(table) : new ArrayList<DataObject>());
					dataSet.add(dataObject);
					this.databaseData.put(table, dataSet);
				}
			}
		}
	}
	
   /**
    * Gets the Equal Data of the DataObject
    * 
    * @param object - The DataObject being accessed.
    * @return If the data is equal.
    */	
	public boolean hasDataSet(DataObject object) {
		String table = object.getTable().name().toLowerCase();
		Iterator<DataObject> dataSet = this.databaseData.get(table).iterator();
		while (dataSet.hasNext()) {
			DataObject dataObject = dataSet.next();
			if (dataObject.getTable().equals(object.getTable()) && object.equalsData(object, dataObject)) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Creates the missing database tables.
    * 
    */
	private void createTables() {
		this.alterTables(); {
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS ij_first_join (`Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS ij_first_world (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS ij_ip_limits (`World_Name` varchar(1000), `IP_Address` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS ij_first_commands (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Command_String` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS ij_enabled_players (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `isEnabled` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS ij_return_items (`World_Name` varchar(1000), `Region_Name` varchar(1000), `Player_UUID` varchar(1000), `Inventory64` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS ij_return_craftitems (`Player_UUID` varchar(1000), `Inventory64` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS ij_return_switch_items (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Inventory64` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS ij_on_cooldown (`World_Name` varchar(1000), `Item_Name` varchar(1000), `Player_UUID` varchar(1000), `Cooldown` varchar(1000), `Duration` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS ij_map_ids (`Map_IMG` varchar(1000), `Map_ID` varchar(1000), `Time_Stamp` varchar(1000));");
		}
	}
	
   /**
    * Alters any existing tables to fit the new TIME_STAMP datatype.
    * 
    */
	private void alterTables() {
		// Change legacy table names to include the ij_ prefix.
		if (!Database.getDatabase().tableExists("ij_first_join") && Database.getDatabase().tableExists("first_join")) {
			Database.getDatabase().executeStatement("ALTER TABLE first_join RENAME TO ij_first_join;");
		}
		if (!Database.getDatabase().tableExists("ij_first_world") && Database.getDatabase().tableExists("first_world")) {
			Database.getDatabase().executeStatement("ALTER TABLE first_world RENAME TO ij_first_world;");
		}
		if (!Database.getDatabase().tableExists("ij_ip_limits") && Database.getDatabase().tableExists("ip_limits")) {
			Database.getDatabase().executeStatement("ALTER TABLE ip_limits RENAME TO ij_ip_limits;");
		}
		if (!Database.getDatabase().tableExists("ij_first_commands") && Database.getDatabase().tableExists("first_commands")) {
			Database.getDatabase().executeStatement("ALTER TABLE first_commands RENAME TO ij_first_commands;");
		}
		if (!Database.getDatabase().tableExists("ij_enabled_players") && Database.getDatabase().tableExists("enabled_players")) {
			Database.getDatabase().executeStatement("ALTER TABLE enabled_players RENAME TO ij_enabled_players;");
		}
		if (!Database.getDatabase().tableExists("ij_return_items") && Database.getDatabase().tableExists("return_items")) {
			Database.getDatabase().executeStatement("ALTER TABLE return_items RENAME TO ij_return_items;");
		}
		if (!Database.getDatabase().tableExists("ij_return_craftitems") && Database.getDatabase().tableExists("return_craftitems")) {
			Database.getDatabase().executeStatement("ALTER TABLE return_craftitems RENAME TO ij_return_craftitems;");
		}
		if (!Database.getDatabase().tableExists("ij_map_ids") && Database.getDatabase().tableExists("map_ids")) {
			Database.getDatabase().executeStatement("ALTER TABLE map_ids RENAME TO ij_map_ids;");
		}
		// Removes legacy columns from legacy tables.
		if (Database.getDatabase().tableExists("ij_first_join") && Database.getDatabase().columnExists("SELECT Player_Name FROM ij_first_join")) {
			Database.getDatabase().executeStatement("CREATE TEMPORARY TABLE ij_first_join_backup (`Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO ij_first_join_backup SELECT Player_UUID,Item_Name,Time_Stamp FROM ij_first_join;");
			Database.getDatabase().executeStatement("DROP TABLE ij_first_join");
			Database.getDatabase().executeStatement("CREATE TABLE ij_first_join (`Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO ij_first_join SELECT Player_UUID,Item_Name,Time_Stamp FROM ij_first_join_backup;");
			Database.getDatabase().executeStatement("DROP TABLE ij_first_join_backup");
		}
		if (Database.getDatabase().tableExists("ij_first_world") && Database.getDatabase().columnExists("SELECT Player_Name FROM ij_first_world")) {
			Database.getDatabase().executeStatement("CREATE TEMPORARY TABLE ij_first_world_backup (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO ij_first_world_backup SELECT World_Name,Player_UUID,Item_Name,Time_Stamp FROM ij_first_world;");
			Database.getDatabase().executeStatement("DROP TABLE ij_first_world");
			Database.getDatabase().executeStatement("CREATE TABLE ij_first_world (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO ij_first_world SELECT World_Name,Player_UUID,Item_Name,Time_Stamp FROM ij_first_world_backup;");
			Database.getDatabase().executeStatement("DROP TABLE ij_first_world_backup");
		}
		if (Database.getDatabase().tableExists("ij_enabled_players") && Database.getDatabase().columnExists("SELECT Player_Name FROM ij_enabled_players")) {
			Database.getDatabase().executeStatement("CREATE TEMPORARY TABLE ij_enabled_players_backup (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `isEnabled` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO ij_enabled_players_backup SELECT World_Name,Player_UUID,isEnabled,Time_Stamp FROM ij_enabled_players;");
			Database.getDatabase().executeStatement("DROP TABLE ij_enabled_players");
			Database.getDatabase().executeStatement("CREATE TABLE ij_enabled_players (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `isEnabled` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO ij_enabled_players SELECT World_Name,Player_UUID,isEnabled,Time_Stamp FROM ij_enabled_players_backup;");
			Database.getDatabase().executeStatement("DROP TABLE ij_enabled_players_backup");
		}
	}
	
	public static void newData(final boolean reload) {
		if (!reload) {
			data = new SQL();
		} else if ((!ConfigHandler.getConfig().sqlEnabled() && Database.getDatabase().getConstant()) || (ConfigHandler.getConfig().sqlEnabled() && !Database.getDatabase().getConstant())) {
			data = new SQL();
		}
	}
	
   /**
    * Gets the instance of the SQLite.
    * 
    * @param regen - If the SQLite should have a new instance created.
    * @return The SQLite instance.
    */
    public static SQL getData() { 
        if (data == null) { newData(false); }
        return data; 
    } 
}