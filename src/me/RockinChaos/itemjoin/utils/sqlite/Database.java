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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.listeners.triggers.PlayerLogin;
import me.RockinChaos.itemjoin.utils.Utils;

public class Database {
	
	private String dataFolder, host, port, database, user, pass;
	private Connection connection;
	private boolean stopConnection = false;
	
	private static Database data;
	
   /**
	* Creates a new instance of SQL Connections.
	* 
	* @param databaseName - The name of the database.
	* @param createStatement - the statement to be run.
	*/
	public Database(String databaseName) {
		FileConfiguration config = ConfigHandler.getConfig(false).getFile("config.yml");
		this.dataFolder = databaseName;
		this.host = config.getString("Database.host");
		this.port = config.getString("Database.port");
		this.user = Utils.getUtils().encrypt(config.getString("Database.user"));
		this.pass = Utils.getUtils().encrypt(config.getString("Database.pass"));
		this.database = config.getString("Database.table") != null ? config.getString("Database.table") : config.getString("Database.database");
	}
	
   /**
	* Gets the proper SQL connection.
	* 
	* @return The SQL connection.
    * @throws SQLException 
	*/
	public Connection getConnection() throws SQLException {
		if (this.connection != null && !this.connection.isClosed()) {
			return this.connection; 
		} else if (!this.stopConnection) {
			synchronized (this) {
				if (ConfigHandler.getConfig(false).sqlEnabled()) {
					try { 
						Class.forName("com.mysql.jdbc.Driver");
						if (!PlayerLogin.hasStarted()) {
						ServerHandler.getServer().logInfo("Loading MySQL Connection...");
						}
						this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?createDatabaseIfNotExist=true" + "&useSSL=false", Utils.getUtils().decrypt(this.user), Utils.getUtils().decrypt(this.pass));
						return this.connection;
					} catch (Exception e) { 
						this.stopConnection = true;
						ServerHandler.getServer().logSevere("{MySQL} Unable to connect to the defined MySQL database, check your settings.");
						ServerHandler.getServer().sendSevereTrace(e);
					}
					return this.connection;
				} else {
					try {
						Class.forName("org.sqlite.JDBC");
						this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.getDatabase());
					} catch (SQLException e) { 
						this.stopConnection = true;
						ServerHandler.getServer().logSevere("{SQLite} SQLite exception on initialize.");
						ServerHandler.getServer().sendDebugTrace(e);
					} catch (ClassNotFoundException e) { 
						this.stopConnection = true;
						ServerHandler.getServer().logSevere("{SQLite} You need the SQLite JBDC library, see: https://bitbucket.org/xerial/sqlite-jdbc/downloads/ and put it in the /lib folder of Java.");
						ServerHandler.getServer().sendDebugTrace(e);
					}
					return this.connection;
				}
			}
		}
		return this.connection;
	}
	
   /**
	* Executes a specified SQL statement.
	* 
	* @param statement - the statement to be executed.
	* @return The statement was successfully executed.
	*/
	public boolean executeStatement(final String statement) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.getConnection();
			ps = conn.prepareStatement(statement);
			return !ps.execute();
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} [1] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [1] Database Status: Open: " + !this.getConnection().isClosed() + "! Writable: " + !this.getConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [1] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [1] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
			return false;
		} finally {
			this.close(ps, null, conn, false);
		}
	}
	
   /**
	* Queries the specified row and the specified statement for a specific value.
    * 
	* @param statement - the statement to be executed.
	* @param row - the row being queried.
	* @return The result in as an object.
	*/
	public Object queryValue(final String statement, final String row) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getObject(row);
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} [2] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [2] Database Status: Open: " + !this.getConnection().isClosed() + "! Writable: " + !this.getConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [2] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [2] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn, false);
		}
		return null;
	}
	
	/**
	* Queries a row for a specified list of values.
	* 
	* @param statement - the statement to be executed.
	* @param row - the row being queried.
	* @return The result in as a listed object.
	*/
	public List < Object > queryRow(final String statement, final String row) {
		final List < Object > objects = new ArrayList < Object > ();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			while (rs.next()) {
				objects.add(rs.getObject(row));
			}
			return objects;
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} [3] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [3] Database Status: Open: " + !this.getConnection().isClosed() + "! Writable: " + !this.getConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [3] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [3] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn, false);
		}
		return null;
	}
	
   /**
	* Queries a list of rows for their specified statements for a specific list of multiple values.
	* 
	* @param statement - the statement to be executed.
	* @param row - the list of rows being queried.
	* @return The result in as a listed list of strings.
	*/
	public List < HashMap < String, String >> queryTableData(final String statement, final String rows) {
		final List < HashMap < String, String > > existingData = new ArrayList < HashMap < String, String > > ();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			while (rs.next()) {
				final HashMap < String, String > columnData = new HashMap < String, String > ();
				for (final String singleRow: rows.split(", ")) {
					if (!rs.isClosed() && !conn.isClosed()) {
						columnData.put(singleRow, rs.getString(singleRow));
					}
				}
				existingData.add(columnData);
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} [4] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [4] Database Status: Open: " + !this.getConnection().isClosed() + "! Writable: " + !this.getConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [4] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [4] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn, false);
		}
		return existingData;
	}
	
   /**
	* Queries a list of rows for their specified statements for a specific list of multiple values.
	* 
	* @param statement - the statement to be executed.
	* @param row - the list of rows being queried.
	* @return The result in as a listed list of strings.
	*/
	public List < List < String >> queryTableData(final String statement, final String...row) { //old remove later
		final List < List < String > > existingData = new ArrayList < List < String > > ();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			while (rs.next()) {
				final List < String > columnData = new ArrayList < String > ();
				for (final String singleRow: row) {
					columnData.add(rs.getString(singleRow));
				}
				existingData.add(columnData);
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} [4] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [4] Database Status: Open: " + !this.getConnection().isClosed() + "! Writable: " + !this.getConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [4] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [4] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn, false);
		}
		return existingData;
	}
	
   /**
	* Qeuries multiple rows for a specific value.
	* 
	* @param statement - the statement to be executed.
	* @param row - the list of rows being queried.
	* @return The result in as a HashMap.
	*/
	public Map < String, List < Object >> queryMultipleRows(final String statement, final String...row) {
		final List < Object > objects = new ArrayList < Object > ();
		final Map < String, List < Object >> map = new HashMap < String, List < Object >> ();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			while (rs.next()) {
				for (final String singleRow: row) {
					objects.add(rs.getObject(singleRow));
				}
				for (final String singleRow: row) {
					map.put(singleRow, objects);
				}
			}
			return map;
		} catch (Exception e) {
				ServerHandler.getServer().logSevere("{SQLite} [5] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [5] Database Status: Open: " + !this.getConnection().isClosed() + "! Writable: " + !this.getConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [5] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [5] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn, false);
		}
		return null;
	}
	
   /**
	* Checks if the column exists in the database.
	* 
	* @param statement - the statement to be executed.
	* @return If the column exists.
	*/
	public boolean columnExists(final String statement) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			return true;
		} catch (Exception e) {
			if (Utils.getUtils().containsIgnoreCase(e.getMessage(), "no such column")) {
				return false;
			} else {
				ServerHandler.getServer().logSevere("{SQLite} [6] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [6] Database Status: Open: " + !this.getConnection().isClosed() + "! Writable: " + !this.getConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [6] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [6] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
			}
		} finally {
			this.close(ps, rs, conn, false);
		}
		return false;
	}
	
   /**
	* Checks if the table exists in the database.
	* 
	* @param tableName - the name of the table.
	* @return If the table exists.
	*/
	public boolean tableExists(String tableName) {
		boolean tExists = false;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			rs = conn.getMetaData().getTables(null, null, tableName, null);
			while (rs.next()) {
				if (!rs.isClosed() && !conn.isClosed()) {
					String tName = rs.getString("TABLE_NAME");
					if (tName != null && tName.equals(tableName)) {
						tExists = true;
						break;
					}
				}
			}
		} catch (SQLException e) {
			ServerHandler.getServer().logSevere("{SQLite} [9] Failed to check if a table exists.");
			ServerHandler.getServer().sendDebugTrace(e);
		} finally {
			this.close(null, rs, conn, false);
		}
		return tExists;
	}
	
   /**
	* Checks if the specific data set exists in the database.
	* 
	* @param statement - the statement to be executed.
	* @return If the data exists.
	*/
	public boolean dataExists(String statement) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			if (!rs.isBeforeFirst()) {
				ServerHandler.getServer().logDebug("{SQLite} Result set is empty.");
				return false;
			} else {
				ServerHandler.getServer().logDebug("{SQLite} Result set is not empty.");
				return true;
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} Could not read from the database.db file, some ItemJoin features have been disabled!");
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(null, rs, conn, false);
		}
		return false;
	}
	
   /**
	* Gets the database file.
	* 
	* @return The Database File.
	*/
	private File getDatabase() {
		File dataFolder = new File(ItemJoin.getInstance().getDataFolder(), this.dataFolder + ".db");
		if (!dataFolder.exists()) {
			try { dataFolder.createNewFile(); } 
			catch (IOException e) { 
				ServerHandler.getServer().logSevere("{SQLite} File write error: " + this.dataFolder + ".db."); 
				ServerHandler.getServer().sendDebugTrace(e);
			}
		}
		return dataFolder;
	}
	
   /**
	* Closes the specific PreparedStatement and ResultSet.
	* 
	* @param ps - the PreparedStatement being closed.
	* @param rs - the ResultSet being closed.
	* @param conn - the Connection being closed.
	*/
	public void close(final PreparedStatement ps, final ResultSet rs, final Connection conn, final boolean force) {
		try {
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
			if (conn != null && !conn.isClosed() && (!ConfigHandler.getConfig(false).sqlEnabled() || force)) {
				conn.close();
			}
		} catch (SQLException e) { 
			ServerHandler.getServer().logSevere("{SQLite} [10] Failed to close database connection."); 
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Closes the active database connection.
	* 
	*/
	public void closeConnection(final boolean force) {
		this.close(null, null, this.connection, force);
	}
	
   /**
	* Gets the instance of the Database.
	* 
	* @param dbname - The database being fetched.
	* @return The Database instance.
	*/
	public static Database getDatabase(final String dataFolder) {
		if (data == null || !data.dataFolder.equalsIgnoreCase(dataFolder)) {
			data = new Database(dataFolder); 
			try {
				data.getConnection();
			} catch (SQLException e) {
				ServerHandler.getServer().logSevere("{SQLite} [1] Failed to open database connection."); 
				ServerHandler.getServer().sendDebugTrace(e);
			}
		}
        return data; 
	}
}