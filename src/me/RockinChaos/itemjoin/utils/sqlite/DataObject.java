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

public class DataObject {
	private Table table;
	private String playerId;
	private String playerName;
	private String regionName;
	private String worldName;
	private String cooldown;
	private String ipAddress;
	private String duration;
	private String command;
	private String inventory64;
	private String mapImage;
	private String mapId;
	private String item;
	private String isEnabled;
	private String timeStamp;
	
   /**
    * Creates a new GuildObject instance
    * 
    * @param table - The Table being accessed.
    * @param playerId - The Player UUID being referenced.
    * @param playerName - The Player Name being referenced.
    * @param worldName - The World Name being referenced.
    * @param regionName - The Region Name being referenced.
    * @param command - The Command String being referenced.
    * @param duration - The Total Cooldown Duration being referenced.
    * @param cooldown - The Time The Cooldown Initiated being referenced.
    * @param item - The Item Node being referenced.
    * @param ipAddress - The Player IP Address being referenced.
    * @param isEnabled - The Player Has ItemJoin Enabled For Them being referenced.
    * @param inventory64 - The Base64 Encoded Inventory being referenced.
    */
	public DataObject(Table table, String playerId, String playerName, String worldName, String regionName, String command, String duration, String cooldown, String item, String ipAddress, String isEnabled, String inventory64, String mapImage, String mapId, String timeStamp) {
		this.table = table;
		this.playerId = playerId;
		this.playerName = playerName;
		this.worldName = worldName;
		this.regionName = regionName;
		this.command = command;
		this.duration = duration;
		this.cooldown = cooldown;
		this.item = item;
		this.ipAddress = ipAddress;
		this.isEnabled = isEnabled;
		this.inventory64 = inventory64;
		this.mapImage = mapImage;
		this.mapId = mapId;
		this.timeStamp = timeStamp;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getPlayerId() {
		return this.playerId;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getPlayerName() {
		return this.playerName;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getWorld() {
		return this.worldName;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getRegion() {
		return this.regionName;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getIPAddress() {
		return this.ipAddress;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getMapIMG() {
		return this.mapImage;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getMapId() {
		return this.mapId;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getCooldown() {
		return this.cooldown;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getDuration() {
		return this.duration;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getEnabled() {
		return this.isEnabled;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getInventory64() {
		return this.inventory64;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getCommand() {
		return this.command;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getItem() {
		return this.item;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */
	public String getTimeStamp() {
		return this.timeStamp;
	}

   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */	
	public Table getTable() {
		return this.table;
	}
	
   /**
    * Gets the Player ID of the DataObject
    * 
    * @return The Player ID.
    */	
	public String getHeaderValues() {
		return 
				  (this.getWorld() != null ? "'" + this.getWorld() + "'," : "")
				+ (this.getPlayerName() != null ? "'" + this.getPlayerName() + "'," : "")
				+ (this.getRegion() != null ? "'" + this.getRegion() + "'," : "")
				+ (this.getPlayerId() != null ? "'" + this.getPlayerId() + "'," : "")
				+ (this.getItem() != null ? "'" + this.getItem() + "'," : "")
			    + (this.getIPAddress() != null ? "'" + this.getIPAddress() + "'," : "")
			    + (this.getCooldown() != null ? "'" + this.getCooldown() + "'," : "")
			    + (this.getDuration() != null ? "'" + this.getDuration() + "'," : "")
			    + (this.getEnabled() != null ? "'" + this.getEnabled() + "'," : "")
			    + (this.getInventory64() != null ? "'" + this.getInventory64() + "'," : "")
			    + (this.getCommand() != null ? "'" + this.getCommand() + "'," : "")
			    + (this.getMapIMG() != null ? "'" + this.getMapIMG() + "'," : "")
			    + (this.getMapId() != null ? "'" + this.getMapId() + "'," : "")
			    + "'" + new Timestamp(System.currentTimeMillis()) + "'";
	}
	
   /**
	* Defines the existing tables.
	* 
	*/
    public enum Table {
       IJ_FIRST_JOIN("`World_Name`, `Player_Name`, `Player_UUID`, `Item_Name`, `Time_Stamp`", "Player_UUID"), 
       IJ_FIRST_WORLD("`World_Name`, `Player_Name`, `Player_UUID`, `Item_Name`, `Time_Stamp`", "Player_UUID"), 
       IJ_IP_LIMITS("`World_Name`, `Player_UUID`, `Item_Name`, `IP_Address`, `Time_Stamp`", "Player_UUID"), 
       IJ_FIRST_COMMANDS("`World_Name`, `Player_UUID`, `Command_String`, `Time_Stamp`", "Player_UUID"), 
       IJ_ENABLED_PLAYERS("`World_Name`, `Player_Name`, `Player_UUID`, `isEnabled`, `Time_Stamp`", "Player_UUID"), 
       IJ_RETURN_ITEMS("`World_Name`, `Region_Name`, `Player_UUID`, `Inventory64`, `Time_Stamp`", "Player_UUID, World_Name, Region_Name"), 
       IJ_RETURN_CRAFTITEMS("`Player_UUID`, `Inventory64`, `Time_Stamp`", "Player_UUID"), 
       IJ_ON_COOLDOWN("`World_Name`, `Player_UUID`, `Item_Name`, `Cooldown`, `Duration`, `Time_Stamp`", ""), 
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