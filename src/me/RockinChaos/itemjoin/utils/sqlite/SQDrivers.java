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

import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.Database;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class SQDrivers extends Database {
	
	private String dbname;
	private int port = 3306;
	private String host = "localhost";
	private String table = "database";
	private String user = "root";
	private String pass = "password";
	private boolean remoteEnabled = false;
	private static SQDrivers database;

	
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
		if (ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.MySQL") != null && ConfigHandler.getConfig(false).getFile("config.yml").getBoolean("Database.MySQL")) {
			this.remoteEnabled = true;
			try { 
				if (this.connection != null && !this.connection.isClosed()) { 
			    	return this.connection; 
			    } else {
			    	Class.forName("com.mysql.jdbc.Driver");
			    	this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.table + "?useSSL=false", Utils.getUtils().decrypt(this.user), Utils.getUtils().decrypt(this.pass));
			        return this.connection;
			    }
			} catch (Exception e) { 
					ServerHandler.getServer().logSevere("{MySQL} Unable to connect to the defined MySQL database, check your settings.");
					ServerHandler.getServer().sendDebugTrace(e); 
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
				ServerHandler.getServer().logSevere("{SQLite} You need the SQLite JBDC library, see: &ahttps://bitbucket.org/xerial/sqlite-jdbc/downloads/ &rand put it in /lib folder.");
				ServerHandler.getServer().sendDebugTrace(e);
			}
			return null;
		}
	}
	
   /**
	* Initializes the SQL Database connection.
	* 
	*/
	@Override
	public void load() {
        ServerHandler.getServer().runAsyncThread(main -> {
			connection = getSQLConnection();
			initialize(remoteEnabled);
        });
	}
	
   /**
	* Purges the data of the specified database.
	* 
	* @param dbname - the name of the database to be purged.
	*/
	public void purgeDatabase() {
		File dataFolder = new File(ItemJoin.getInstance().getDataFolder(), this.dbname + ".db");
		if (dataFolder.exists()) {
			try { dataFolder.delete(); } 
			catch (Exception e) {
				ServerHandler.getServer().logSevere("{SQLite} Failed to close database " + this.dbname + ".db after purging.");
				ServerHandler.getServer().sendDebugTrace(e); 
			}
		}
	}
	
   /**
	* Loads the necessary MySQL Database Connection information.
    * 
	*/
	public void loadSQLDatabase() {
		if (ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.port") != null) { this.port = ConfigHandler.getConfig(false).getFile("config.yml").getInt("Database.port"); }
		if (ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.host") != null) { this.host = ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.host"); }
		if (ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.table") != null) { this.table = ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.table"); }
		if (ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.user") != null) { this.user = Utils.getUtils().encrypt(ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.user")); }
		if (ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.pass") != null) { this.pass = Utils.getUtils().encrypt(ConfigHandler.getConfig(false).getFile("config.yml").getString("Database.pass")); }
	}
	
   /**
	* Checks if there is a remote database or if it is a locale database.
	* 
	* @return If there is a remote database enabled.
	*/
	public boolean remoteEnabled() {
		return this.remoteEnabled;
	}
	
   /**
	* Gets the instance of the SQL database.
	* 
	* @param database - The database being fetched.
	* @return The SQLite database instance.
	*/
	public static SQDrivers getDatabase(final String dbname) {
		if (database == null) { database = new SQDrivers(dbname); }
        return database; 
	}
}