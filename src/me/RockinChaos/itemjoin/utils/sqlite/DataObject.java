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

import java.sql.Timestamp;

import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.handlers.PlayerHandler;

public class DataObject {
	private Table table = Table.IJ_FIRST_JOIN;
	private Boolean isTemporary = false;
	private String playerId = new String();
	private String regionName = new String();
	private String worldName = new String();
	private String cooldown = new String();
	private String ipAddress = new String();
	private String duration = new String();
	private String command = new String();
	private String inventory64 = new String();
	private String mapImage = new String();
	private String mapId = new String();
	private String item = new String();
	private String isEnabled = new String();
	private String timeStamp = new String();
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getPlayerId() {
		return this.playerId;
	}
	
   /**
    * Gets the World Name of the DataObject
    * 
    * @return The World Name.
    */
	public String getWorld() {
		return this.worldName;
	}
	
   /**
    * Gets the Region Name of the DataObject
    * 
    * @return The Region Name.
    */
	public String getRegion() {
		return this.regionName;
	}
	
   /**
    * Gets the IP Address of the DataObject
    * 
    * @return The IP Address.
    */
	public String getIPAddress() {
		return this.ipAddress;
	}
	
   /**
    * Gets the Map Image of the DataObject
    * 
    * @return The Map Image.
    */
	public String getMapIMG() {
		return this.mapImage;
	}
	
   /**
    * Gets the Map ID of the DataObject
    * 
    * @return The Map ID.
    */
	public String getMapID() {
		return this.mapId;
	}
	
   /**
    * Gets the Cooldown of the DataObject
    * 
    * @return The Cooldown.
    */
	public String getCooldown() {
		return this.cooldown;
	}
	
   /**
    * Gets the Duration of the DataObject
    * 
    * @return The Duration.
    */
	public String getDuration() {
		return this.duration;
	}
	
   /**
    * Gets the Enabled/Disabled Boolean of the DataObject
    * 
    * @return The Enabled/Disabled Boolean.
    */
	public String getEnabled() {
		return this.isEnabled;
	}
	
   /**
    * Gets the Inventory in 64 Hash of the DataObject
    * 
    * @return The Inventory in 64 Hash.
    */
	public String getInventory64() {
		return this.inventory64;
	}
	
   /**
    * Gets the Command String of the DataObject
    * 
    * @return The Command String.
    */
	public String getCommand() {
		return this.command;
	}
	
   /**
    * Gets the Item Node of the DataObject
    * 
    * @return The Item Node.
    */
	public String getItem() {
		return this.item;
	}
	
   /**
    * Gets the Time Stamp of the DataObject
    * 
    * @return The Time Stamp.
    */
	public String getTimeStamp() {
		return this.timeStamp;
	}
	
   /**
    * Sets the Time Stamp of the DataObject
    * 
    * @param stamp - The Time Stamp.
    */
	public void setTimeStamp(String stamp) {
		this.timeStamp = stamp;
	}

   /**
    * Gets the Table of the DataObject
    * 
    * @return The Table.
    */	
	public Table getTable() {
		return this.table;
	}
	
   /**
    * Gets if the DataObject is only a reference.
    * 
    * @return If the DataObject is a reference.
    */	
	public boolean isTemporary() {
		return this.isTemporary;
	}
	
   /**
    * Creates a new DataObject instance
    * 
    * @param table - The Table being accessed.
    * @param isTemporary - If the DataObject is only a reference.
    */
	public DataObject(Table table) {
		this.table = table;
		this.isTemporary = true;
	}
	
   /**
    * Creates a new DataObject instance
    * 
    * @param table - The Table being accessed.
    * @param player - The Player being referenced.
    * @param worldName - The World Name being referenced.
    * @param object - The Object being referenced.
    */
	public DataObject(Table table, Player player, String worldName, String object) {
		if (player != null) {
			this.playerId = PlayerHandler.getPlayer().getPlayerID(player);
		} else if (table.equals(Table.IJ_ENABLED_PLAYERS)) { 
			this.playerId = "ALL"; 
		}
		if (table.equals(Table.IJ_FIRST_JOIN) || table.equals(Table.IJ_FIRST_WORLD)) {
			this.item = object;
		} else if (table.equals(Table.IJ_RETURN_CRAFTITEMS) || table.equals(Table.IJ_RETURN_SWITCH_ITEMS)) {
			this.inventory64 = object;
		}else if (table.equals(Table.IJ_FIRST_COMMANDS)) {
			this.command = object;
		} else if (table.equals(Table.IJ_ENABLED_PLAYERS)) {
			this.isEnabled = object;
		}
		this.table = table;
		this.worldName = worldName;
		this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
	}
	
   /**
    * Creates a new DataObject instance
    * 
    * @param table - The Table being accessed.
    * @param player - The Player being referenced.
    * @param worldName - The World Name being referenced.
    * @param object1 - The Object being referenced.
    * @param object2 - The Object being referenced.
    */
	public DataObject(Table table, Player player, String worldName, String object1, String object2) {
		if (player != null) {
			this.playerId = PlayerHandler.getPlayer().getPlayerID(player);
		} else if (table.equals(Table.IJ_ENABLED_PLAYERS)) { 
			this.playerId = "ALL"; 
		}
		if (table.equals(Table.IJ_MAP_IDS)) {
			this.mapImage = object1;
			this.mapId = object2;
		} else if (table.equals(Table.IJ_RETURN_ITEMS)) {
			this.regionName = object1;
			this.inventory64 = object2;
		} else if (table.equals(Table.IJ_IP_LIMITS)) {
			this.item = object1;
			this.ipAddress = object2;
		} 
		this.table = table;
		this.worldName = worldName;
		this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
	}
	
   /**
    * Creates a new DataObject instance
    * 
    * @param table - The Table being accessed.
    * @param player - The Player being referenced.
    * @param worldName - The World Name being referenced.
    * @param object1 - The Object being referenced.
    * @param object2 - The Object being referenced.
    * @param object3 - The Object being referenced.
    */
	public DataObject(Table table, Player player, String worldName, String object1, String object2, String object3) {
		if (player != null) {
			this.playerId = PlayerHandler.getPlayer().getPlayerID(player);
		} else if (table.equals(Table.IJ_ENABLED_PLAYERS)) { 
			this.playerId = "ALL"; 
		}
		if (table.equals(Table.IJ_ON_COOLDOWN)) {
			this.item = object1;
			this.cooldown = object2;
			this.duration = object3;
		}
		this.table = table;
		this.worldName = ((worldName != null && !worldName.isEmpty()) ? worldName : "Global");
		this.timeStamp = new Timestamp(System.currentTimeMillis()).toString();
	}
	
   /**
    * Gets the Equal Data of the DataObject
    * 
    * @param object1 - The DataObject being compared.
    * @param object2 - The DataObject being compared.
    * @return If the data is equal.
    */	
	public boolean equalsData(DataObject object1, DataObject object2) {
		if (object1 == null || object2 == null) { return false; }
		if (object1.getTable().equals(Table.IJ_FIRST_JOIN)) {
			if (object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId()) && (object1.getItem().isEmpty() || object1.getItem().equalsIgnoreCase(object2.getItem()))) {
				return true;
			}
		} else if (object1.getTable().equals(Table.IJ_FIRST_WORLD)) {
			if (object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId()) 
					&& (object1.getWorld().isEmpty() || object1.getWorld().equalsIgnoreCase(object2.getWorld())) 
					&& (object1.getItem().isEmpty() || object1.getItem().equalsIgnoreCase(object2.getItem()))) {
				return true;
			}
		} else if (object1.getTable().equals(Table.IJ_IP_LIMITS)) {
			if ((object1.getItem().isEmpty() && object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId()) 
					|| (object1.getWorld().equalsIgnoreCase(object2.getWorld()) && object1.getItem().equalsIgnoreCase(object2.getItem()) && object1.getIPAddress().equalsIgnoreCase(object2.getIPAddress())))) {
				return true;
			}
		} else if (object1.getTable().equals(Table.IJ_FIRST_COMMANDS)) {
			if (object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId()) && (object1.getWorld().isEmpty() || object1.getWorld().equalsIgnoreCase(object2.getWorld())) 
					&& (object1.getCommand().isEmpty() || object1.getCommand().equalsIgnoreCase(object2.getCommand()))) {
				return true;
			}
		} else if (object1.getTable().equals(Table.IJ_ENABLED_PLAYERS)) {
			if ((object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId()))
					&& (object1.getWorld().isEmpty() || (object1.getWorld().equalsIgnoreCase(object2.getWorld()) || object2.getWorld().equalsIgnoreCase("Global")))) {
				return true;
			}
		} else if (object1.getTable().equals(Table.IJ_RETURN_ITEMS)) {
			if (object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId()) && object1.getWorld().equalsIgnoreCase(object2.getWorld()) && object1.getRegion().equalsIgnoreCase(object2.getRegion())) {
				return true;
			}
		} else if (object1.getTable().equals(Table.IJ_RETURN_SWITCH_ITEMS)) {
			if (object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId()) && object1.getWorld().equalsIgnoreCase(object2.getWorld())) {
				return true;
			}
		} else if (object1.getTable().equals(Table.IJ_RETURN_CRAFTITEMS)) {
			if (object1.getPlayerId().equalsIgnoreCase(object2.getPlayerId())) {
				return true;
			}
		} else if (object1.getTable().equals(Table.IJ_ON_COOLDOWN)) {
			if (object1.getItem().equalsIgnoreCase(object2.getItem()) && object1.getCooldown().equalsIgnoreCase(object2.getCooldown())) {
				return true;
			}
		} else if (object1.getTable().equals(Table.IJ_MAP_IDS)) {
			if (object1.getMapIMG().equalsIgnoreCase(object2.getMapIMG())) {
				return true;
			}
		} 
		return false;
	}
	
   /**
    * Gets the Removal Values of the DataObject
    * 
    * @return The Removal Values.
    */	
	public String getRemovalValues() {
		String removal = "";
		for (String column : table.removal().split(", ")) {
			if (column.equalsIgnoreCase("Player_UUID")) { removal += "'" + this.getPlayerId() + "',"; }
			else if (column.equalsIgnoreCase("World_Name")) { removal += "'" + this.getWorld() + "',"; }
			else if (column.equalsIgnoreCase("Region_Name")) { removal += "'" + this.getRegion() + "',"; }
			else if (column.equalsIgnoreCase("Map_IMG")) { removal += "'" + this.getMapIMG() + "',"; }
			else if (column.equalsIgnoreCase("Item_Name")) { removal += "'" + this.getItem() + "',"; }
		}
		return removal.substring(0, removal.length() - 1);
	}
	
   /**
    * Gets the Insert Values of the DataObject
    * 
    * @return The Insert Values.
    */	
	public String getInsertValues() {
		return 
				  (this.getWorld() != null && !this.getWorld().isEmpty() ? "'" + this.getWorld() + "'," : "")
				+ (this.getRegion() != null && !this.getRegion().isEmpty() ? "'" + this.getRegion() + "'," : "")
				+ (this.getPlayerId() != null && !this.getPlayerId().isEmpty() ? "'" + this.getPlayerId() + "'," : "")
				+ (this.getItem() != null && !this.getItem().isEmpty() ? "'" + this.getItem() + "'," : "")
			    + (this.getIPAddress() != null && !this.getIPAddress().isEmpty() ? "'" + this.getIPAddress() + "'," : "")
			    + (this.getCooldown() != null && !this.getCooldown().isEmpty() ? "'" + this.getCooldown() + "'," : "")
			    + (this.getDuration() != null && !this.getDuration().isEmpty() ? "'" + this.getDuration() + "'," : "")
			    + (this.getEnabled() != null && !this.getEnabled().isEmpty() ? "'" + this.getEnabled() + "'," : "")
			    + (this.getInventory64() != null && !this.getInventory64().isEmpty() ? "'" + this.getInventory64() + "'," : "")
			    + (this.getCommand() != null && !this.getCommand().isEmpty() ? "'" + this.getCommand() + "'," : "")
			    + (this.getMapIMG() != null && !this.getMapIMG().isEmpty() ? "'" + this.getMapIMG() + "'," : "")
			    + (this.getMapID() != null && !this.getMapID().isEmpty() ? "'" + this.getMapID() + "'," : "")
			    + "'" + new Timestamp(System.currentTimeMillis()) + "'";
	}
	
   /**
	* Defines the existing tables.
	* 
	*/ 
    public enum Table {
       IJ_FIRST_JOIN("`Player_UUID`, `Item_Name`, `Time_Stamp`", "Player_UUID"), 
       IJ_FIRST_WORLD("`World_Name`, `Player_UUID`, `Item_Name`, `Time_Stamp`", "Player_UUID"), 
       IJ_IP_LIMITS("`World_Name`, `Player_UUID`, `Item_Name`, `IP_Address`, `Time_Stamp`", "Player_UUID"), 
       IJ_FIRST_COMMANDS("`World_Name`, `Player_UUID`, `Command_String`, `Time_Stamp`", "Player_UUID"), 
       IJ_ENABLED_PLAYERS("`World_Name`, `Player_UUID`, `isEnabled`, `Time_Stamp`", "Player_UUID, World_Name"), 
       IJ_RETURN_ITEMS("`World_Name`, `Region_Name`, `Player_UUID`, `Inventory64`, `Time_Stamp`", "Player_UUID, World_Name, Region_Name"), 
       IJ_RETURN_SWITCH_ITEMS("`World_Name`, `Player_UUID`, `Inventory64`, `Time_Stamp`", "Player_UUID, World_Name"), 
       IJ_RETURN_CRAFTITEMS("`Player_UUID`, `Inventory64`, `Time_Stamp`", "Player_UUID"), 
       IJ_ON_COOLDOWN("`World_Name`, `Player_UUID`, `Item_Name`, `Cooldown`, `Duration`, `Time_Stamp`", "Item_Name"), 
       IJ_MAP_IDS("`Map_IMG`, `Map_ID`, `Time_Stamp`", "Map_IMG");
    	
    	private String header;
    	private String removal;
    	private Table(String header, String removal) {
    		this.header = header;
    		this.removal = removal;
    	}
    	public String headers() { return this.header; }
    	public String removal() { return this.removal; }
    	
    }
}