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
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
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
			ServerUtils.logDebug("{SQL} Database Connected."); 
		}
	}
	
   /**
    * Removes ItemJoin tables from the database.
    * 
    */
	public void purgeDatabase() {
		this.databaseData.clear();
		SchedulerUtils.runSingleAsync(() -> {
			for (Table table: Table.values()) {
				synchronized("IJ_SQL") {
					Database.getDatabase().executeStatement("DROP TABLE IF EXISTS " + ConfigHandler.getConfig().getTable() + table.tableName());
				}
			} { this.createTables(); }
		});
	}
	
   /**
    * Saves the table data for the specified DataObject.
    * 
    * @param object - The DataObject data being saved.
    */
	public void saveData(DataObject object) {
		if (object != null) { 
			String table = object.getTable().tableName();
			if (ItemJoin.getInstance().isEnabled()) {
				SchedulerUtils.runSingleAsync(() -> {
					synchronized("IJ_SQL") {
						Database.getDatabase().executeStatement("INSERT INTO " + ConfigHandler.getConfig().getTable() + object.getTable().tableName() + " (" + object.getTable().headers() + ") VALUES (" + object.getInsertValues() + ")");
					}
				});
			} else {
				synchronized("IJ_SQL") {
					Database.getDatabase().executeStatement("INSERT INTO " + ConfigHandler.getConfig().getTable() + object.getTable().tableName() + " (" + object.getTable().headers() + ") VALUES (" + object.getInsertValues() + ")");
				}
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
			String table = object.getTable().tableName();
			if (this.databaseData.get(table) != null && !this.databaseData.get(table).isEmpty()) {
				Iterator<DataObject> dataSet = this.databaseData.get(table).iterator();
				while (dataSet.hasNext()) {
					DataObject dataObject = dataSet.next();
					if (dataObject != null && dataObject.getTable().equals(object.getTable()) && object.equalsData(object, dataObject)) {
						if (ItemJoin.getInstance().isEnabled()) {
							SchedulerUtils.runSingleAsync(() -> {
								synchronized("IJ_SQL") {
									Database.getDatabase().executeStatement("DELETE FROM " + ConfigHandler.getConfig().getTable() + dataObject.getTable().tableName() + " WHERE (" + dataObject.getTable().removal() + ") = (" + dataObject.getRemovalValues() + ")");
								}
							});
						} else {
							synchronized("IJ_SQL") {
								Database.getDatabase().executeStatement("DELETE FROM " + ConfigHandler.getConfig().getTable() + dataObject.getTable().tableName() + " WHERE (" + dataObject.getTable().removal() + ") = (" + dataObject.getRemovalValues() + ")");
							}
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
			String table = object.getTable().tableName();
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
		String table = object.getTable().tableName();
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
			String table = tableEnum.tableName();
			List<HashMap<String, String>> selectTable = Database.getDatabase().queryTableData("SELECT * FROM " + ConfigHandler.getConfig().getTable() + table, tableEnum.headers().replace("`", ""));
			if (selectTable != null && !selectTable.isEmpty()) {
				for (HashMap<String, String> sl1 : selectTable) {
					DataObject dataObject = null;
					if (tableEnum.equals(Table.FIRST_JOIN)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), "", sl1.get("Item_Name"));
					} else if (tableEnum.equals(Table.FIRST_WORLD)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Item_Name"));
					} else if (tableEnum.equals(Table.IP_LIMITS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Item_Name"), sl1.get("IP_Address"));
					} else if (tableEnum.equals(Table.FIRST_COMMANDS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Command_String"));
					} else if (tableEnum.equals(Table.ENABLED_PLAYERS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("isEnabled"));
					} else if (tableEnum.equals(Table.RETURN_ITEMS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Region_Name"), sl1.get("Inventory64"));
					} else if (tableEnum.equals(Table.RETURN_CRAFTITEMS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), "", sl1.get("Inventory64"));
					} else if (tableEnum.equals(Table.RETURN_SWITCH_ITEMS)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Inventory64"));
					} else if (tableEnum.equals(Table.ON_COOLDOWN)) {
						dataObject = new DataObject(tableEnum, sl1.get("Player_UUID"), sl1.get("World_Name"), sl1.get("Item_Name"), sl1.get("Cooldown"), sl1.get("Duration"));
					} else if (tableEnum.equals(Table.MAP_IDS)) {
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
		String table = object.getTable().tableName();
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
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "first_join (`Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "first_world (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "ip_limits (`World_Name` varchar(1000), `IP_Address` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "first_commands (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Command_String` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "enabled_players (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `isEnabled` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "return_items (`World_Name` varchar(1000), `Region_Name` varchar(1000), `Player_UUID` varchar(1000), `Inventory64` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "return_craftitems (`Player_UUID` varchar(1000), `Inventory64` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "return_switch_items (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Inventory64` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "on_cooldown (`World_Name` varchar(1000), `Item_Name` varchar(1000), `Player_UUID` varchar(1000), `Cooldown` varchar(1000), `Duration` varchar(1000), `Time_Stamp` varchar(1000));");
	        Database.getDatabase().executeStatement("CREATE TABLE IF NOT EXISTS " + ConfigHandler.getConfig().getTable() + "map_ids (`Map_IMG` varchar(1000), `Map_ID` varchar(1000), `Time_Stamp` varchar(1000));");
		}
	}
	
   /**
    * Alters any existing tables to fit the new TIME_STAMP datatype.
    * 
    */
	private void alterTables() {
		// Removes legacy columns from legacy tables.
		if (Database.getDatabase().tableExists(ConfigHandler.getConfig().getTable() + "first_join") && Database.getDatabase().columnExists("SELECT Player_Name FROM " + ConfigHandler.getConfig().getTable() + "first_join")) {
			Database.getDatabase().executeStatement("CREATE TEMPORARY TABLE " + ConfigHandler.getConfig().getTable() + "first_join_backup (`Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO " + ConfigHandler.getConfig().getTable() + "first_join_backup SELECT Player_UUID,Item_Name,Time_Stamp FROM " + ConfigHandler.getConfig().getTable() + "first_join;");
			Database.getDatabase().executeStatement("DROP TABLE " + ConfigHandler.getConfig().getTable() + "first_join");
			Database.getDatabase().executeStatement("CREATE TABLE " + ConfigHandler.getConfig().getTable() + "first_join (`Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO " + ConfigHandler.getConfig().getTable() + "first_join SELECT Player_UUID,Item_Name,Time_Stamp FROM " + ConfigHandler.getConfig().getTable() + "first_join_backup;");
			Database.getDatabase().executeStatement("DROP TABLE " + ConfigHandler.getConfig().getTable() + "first_join_backup");
		}
		if (Database.getDatabase().tableExists(ConfigHandler.getConfig().getTable() + "first_world") && Database.getDatabase().columnExists("SELECT Player_Name FROM " + ConfigHandler.getConfig().getTable() + "first_world")) {
			Database.getDatabase().executeStatement("CREATE TEMPORARY TABLE " + ConfigHandler.getConfig().getTable() + "first_world_backup (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO " + ConfigHandler.getConfig().getTable() + "first_world_backup SELECT World_Name,Player_UUID,Item_Name,Time_Stamp FROM " + ConfigHandler.getConfig().getTable() + "first_world;");
			Database.getDatabase().executeStatement("DROP TABLE " + ConfigHandler.getConfig().getTable() + "first_world");
			Database.getDatabase().executeStatement("CREATE TABLE " + ConfigHandler.getConfig().getTable() + "first_world (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `Item_Name` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO " + ConfigHandler.getConfig().getTable() + "first_world SELECT World_Name,Player_UUID,Item_Name,Time_Stamp FROM " + ConfigHandler.getConfig().getTable() + "first_world_backup;");
			Database.getDatabase().executeStatement("DROP TABLE " + ConfigHandler.getConfig().getTable() + "first_world_backup");
		}
		if (Database.getDatabase().tableExists(ConfigHandler.getConfig().getTable() + "enabled_players") && Database.getDatabase().columnExists("SELECT Player_Name FROM " + ConfigHandler.getConfig().getTable() + "enabled_players")) {
			Database.getDatabase().executeStatement("CREATE TEMPORARY TABLE " + ConfigHandler.getConfig().getTable() + "enabled_players_backup (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `isEnabled` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO " + ConfigHandler.getConfig().getTable() + "enabled_players_backup SELECT World_Name,Player_UUID,isEnabled,Time_Stamp FROM " + ConfigHandler.getConfig().getTable() + "enabled_players;");
			Database.getDatabase().executeStatement("DROP TABLE " + ConfigHandler.getConfig().getTable() + "enabled_players");
			Database.getDatabase().executeStatement("CREATE TABLE " + ConfigHandler.getConfig().getTable() + "enabled_players (`World_Name` varchar(1000), `Player_UUID` varchar(1000), `isEnabled` varchar(1000), `Time_Stamp` varchar(1000));");
			Database.getDatabase().executeStatement("INSERT INTO " + ConfigHandler.getConfig().getTable() + "enabled_players SELECT World_Name,Player_UUID,isEnabled,Time_Stamp FROM " + ConfigHandler.getConfig().getTable() + "enabled_players_backup;");
			Database.getDatabase().executeStatement("DROP TABLE " + ConfigHandler.getConfig().getTable() + "enabled_players_backup");
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