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
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.RockinChaos.itemjoin.utils.sqlite.Database;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class SQDrivers extends Database {
	
	private String dbname;
	private boolean closeConnection = true;
	private static SQDrivers drivers;
	
   /**
	* Creates a new instance of SQL Connections.
	* 
	* @param databaseName - The name of the database.
	* @param createStatement - the statement to be run.
	*/
	public SQDrivers(String databaseName) {
		this.dbname = databaseName;
	}

   /**
	* Gets the proper SQL connection.
	* 
	* @return The SQL connection.
	*/
	@Override
	public Connection getSQLConnection() {
		synchronized (this) {
			if (ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.MySQL") != null && ConfigHandler.getConfig(false).getFile("config.yml").getBoolean("Database.MySQL")) {
				try { 
					if (this.connection != null && !this.connection.isClosed()) {
				    	return this.connection; 
				    } else {
				    	Class.forName("com.mysql.jdbc.Driver");
				    	this.connection = DriverManager.getConnection("jdbc:mysql://" + 
				    						ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.host")+ ":" + 
				    						ConfigHandler.getConfig(false).getFile("config.yml").getInt("Database.port") + "/" + 
				    						ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.table") + "?createDatabaseIfNotExist=true" + "&useSSL=false", 
				    						ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.user"), ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.pass"));
				    	this.closeConnection = false;
				        return this.connection;
				    }
				} catch (Exception e) { 
					ServerHandler.getServer().logSevere("{MySQL} Unable to connect to the defined MySQL database, check your settings.");
					ServerHandler.getServer().sendSevereTrace(e); 
				}
				return null;
			} else {
				File dataFolder = new File(ItemJoin.getInstance().getDataFolder(), this.dbname + ".db");
				if (!dataFolder.exists()) {
					try { dataFolder.createNewFile(); } 
					catch (IOException e) { 
						ServerHandler.getServer().logSevere("{SQLite} File write error: " + this.dbname + ".db.");
						ServerHandler.getServer().sendDebugTrace(e);
					}
				} try {
					if ((connection != null) && (!connection.isClosed())) { return this.connection; }
					Class.forName("org.sqlite.JDBC");
					this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
					return this.connection;
				} catch (SQLException e) { 
					ServerHandler.getServer().logSevere("{SQLite} SQLite exception on initialize.");
					ServerHandler.getServer().sendDebugTrace(e);
				} catch (ClassNotFoundException e) { 
					ServerHandler.getServer().logSevere("{SQLite} You need the SQLite JBDC library, see: https://bitbucket.org/xerial/sqlite-jdbc/downloads/ and put it in the /lib folder of Java.");
					ServerHandler.getServer().sendDebugTrace(e);
				}
				return null;
			}
		}
	}
	
   /**
	* Initializes the SQL Database connection.
	* 
	*/
	@Override
	public void load() {
        ServerHandler.getServer().runAsyncThread(async -> {
			this.connection = this.getSQLConnection();
			initialize();
        });
	}
	
   /**
	* Attempts to close the Database Connection.
	* 
	*/
	public void closeConnection() {
		if (this.closeConnection) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				ServerHandler.getServer().logSevere("{SQLite} [11] Failed to close database connection."); 
				ServerHandler.getServer().sendDebugTrace(e);
			}
		}
	}
	
   /**
	* Gets the instance of the SQDrivers.
	* 
	* @param dbname - The database being fetched.
	* @return The SQDrivers instance.
	*/
	public static SQDrivers getDatabase(final String dbname) {
		if (drivers == null) { drivers = new SQDrivers(dbname); }
        return drivers; 
	}
}