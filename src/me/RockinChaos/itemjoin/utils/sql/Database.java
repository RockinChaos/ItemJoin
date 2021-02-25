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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.bukkit.configuration.file.FileConfiguration;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.Utils;

public class Database extends Controller {
	
	private static Database data;
	
   /**
	* Creates a new instance of SQL Connections.
	* 
	* @param databaseName - The name of the database.
	* @param createStatement - the statement to be run.
	*/
	public Database(String baseName) {
		this.dataFolder = baseName;
		this.loadSource();
	}
	
   /**
	* Executes a specified SQL statement.
	* 
	* @param statement - the statement to be executed.
	* @return The statement was successfully executed.
	*/
	public void executeStatement(final String statement) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.getConnection();
			if (conn != null) {
				ps = conn.prepareStatement(statement);
				ps.execute();
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQL} [1] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQL} [1] Database Status: Open: " + !this.isClosed(conn) + "! Writable: " + !conn.isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQL} [1] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQL} [1] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
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
		Object returnValue = null;
		try {
			conn = this.getConnection();
			if (conn != null) {
				ps = conn.prepareStatement(statement);
				rs = ps.executeQuery();
				if (rs.next()) {
					returnValue = rs.getObject(row);
				}
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQL} [2] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQL} [2] Database Status: Open: " + !this.isClosed(conn) + "! Writable: " + !conn.isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQL} [2] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQL} [2] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn, false);
		}
		return returnValue;
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
			if (conn != null) {
				ps = conn.prepareStatement(statement);
				rs = ps.executeQuery();
				while (rs.next()) {
					objects.add(rs.getObject(row));
				}
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQL} [3] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQL} [3] Database Status: Open: " + !this.isClosed(conn) + "! Writable: " + !conn.isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQL} [3] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQL} [3] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn, false);
		}
		return objects;
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
			if (conn != null) {
				ps = conn.prepareStatement(statement);
				rs = ps.executeQuery();
				while (rs.next()) {
					final HashMap < String, String > columnData = new HashMap < String, String > ();
					for (final String singleRow: rows.split(", ")) {
						if (!this.isClosed(rs) && !this.isClosed(conn)) {
							columnData.put(singleRow, rs.getString(singleRow));
						}
					}
					existingData.add(columnData);
				}
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQL} [4] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQL} [4] Database Status: Open: " + !this.isClosed(conn) + "! Writable: " + !conn.isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQL} [4] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQL} [4] Statement: " + statement);
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
			if (conn != null) {
				ps = conn.prepareStatement(statement);
				rs = ps.executeQuery();
				while (rs.next()) {
					final List < String > columnData = new ArrayList < String > ();
					for (final String singleRow: row) {
						columnData.add(rs.getString(singleRow));
					}
					existingData.add(columnData);
				}
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQL} [4] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQL} [4] Database Status: Open: " + !this.isClosed(conn) + "! Writable: " + !conn.isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQL} [4] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQL} [4] Statement: " + statement);
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
			if (conn != null) {
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
			}
		} catch (Exception e) {
				ServerHandler.getServer().logSevere("{SQL} [5] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQL} [5] Database Status: Open: " + !this.isClosed(conn) + "! Writable: " + !conn.isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQL} [5] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQL} [5] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn, false);
		}
		return map;
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
		boolean columnExists = false;
		try {
			conn = this.getConnection();
			if (conn != null) {
				ps = conn.prepareStatement(statement);
				rs = ps.executeQuery();
				columnExists = true;
			}
		} catch (Exception e) {
			if (Utils.getUtils().containsIgnoreCase(e.getMessage(), "no such column") || Utils.getUtils().containsIgnoreCase(e.getMessage(), "Unknown column")) {
				columnExists = false;
			} else {
				ServerHandler.getServer().logSevere("{SQL} [6] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQL} [6] Database Status: Open: " + !this.isClosed(conn) + "! Writable: " + !conn.isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQL} [6] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQL} [6] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
			}
		} finally {
			this.close(ps, rs, conn, false);
		}
		return columnExists;
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
			if (conn != null) {
				rs = conn.getMetaData().getTables(null, null, tableName, null);
				while (rs.next()) {
					if (!this.isClosed(rs) && !this.isClosed(conn)) {
						String tName = rs.getString("TABLE_NAME");
						if (tName != null && tName.equals(tableName)) {
							tExists = true;
							break;
						}
					}
				}
			}
		} catch (SQLException e) {
			ServerHandler.getServer().logSevere("{SQL} [9] Failed to check if the table " + tableName + " exists.");
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
		boolean dataExists = false;
		try {
			conn = this.getConnection();
			if (conn != null) {
				ps = conn.prepareStatement(statement);
				rs = ps.executeQuery();
			}
			if (!rs.isBeforeFirst()) {
				ServerHandler.getServer().logDebug("{SQL} Result set is empty.");
				dataExists = false;
			} else {
				ServerHandler.getServer().logDebug("{SQL} Result set is not empty.");
				dataExists = true;
			}
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQL} Could not read from the database.db file, some ItemJoin features have been disabled!");
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn, false);
		}
		return dataExists;
	}

   /**
	* Closes the active database connection.
	* 
	*/
	public void closeConnection(final boolean force) {
		this.close(null, null, this.connection, force);
	}
	
   /**
	* Closes the active database connections and destroys existing Singletons.
	* 
	*/
	public static void kill() {
		if (data != null) {
			data.closeConnection(true);
			data = null;
		}
	}
	
   /**
	* Gets the instance of the Database.
	* 
	* @return The Database instance.
	*/
	public static Database getDatabase() {
		if (data == null || !data.dataFolder.equalsIgnoreCase("database")) {
			data = new Database("database"); 
			try {
				data.getConnection();
			} catch (SQLException e) {
				ServerHandler.getServer().logSevere("{SQL} [1] Failed to open database connection."); 
				ServerHandler.getServer().sendDebugTrace(e);
			}
		}
        return data; 
	}
	
   /**
	* Gets the instance of the Database.
	* 
	* @param dbname - The database being fetched.
	* @return The Database instance.
	*/
	public static Database getDatabase(final String baseName) {
		if (data == null || !data.dataFolder.equalsIgnoreCase(baseName)) {
			data = new Database(baseName); 
			try {
				data.getConnection();
			} catch (SQLException e) {
				ServerHandler.getServer().logSevere("{SQL} [2] Failed to open database connection."); 
				ServerHandler.getServer().sendDebugTrace(e);
			}
		}
        return data; 
	}
}


/**
 * Handles the current Controller instance.
 * Controls all database connection information.
 * 
 */
abstract class Controller {
	protected Connection connection;
	protected String dataFolder;
    protected HikariDataSource hikari;
    protected HikariConfig hikariConfig;
	protected boolean stopConnection = false;
	protected boolean constConnection = false;
		
   /**
	* Gets the proper SQL connection.
	* 
	* @return The SQL connection.
    * @throws SQLException 
	*/
	protected Connection getConnection() throws SQLException {
		if (this.isClosed(this.connection) && !this.stopConnection) {
			synchronized (this) {
				try {
					if (this.isClosed(this.connection)) {
					    try {
					    	this.hikari = new HikariDataSource(this.hikariConfig);
						} catch (Exception e) { 
							this.close(null, null, this.connection, true);
							if (ConfigHandler.getConfig().sqlEnabled()) {
								ServerHandler.getServer().logSevere("{SQL} Unable to connect to the defined MySQL database, check your settings.");
							} else { ServerHandler.getServer().logSevere("{SQL} SQLite exception on initialize."); }
							ServerHandler.getServer().sendSevereTrace(e);
						}
						this.connection = this.hikari.getConnection();
					}
				} catch (Exception e) { 
					if (Utils.getUtils().containsIgnoreCase(e.getMessage(), "Apparent connection leak detected") || Utils.getUtils().containsIgnoreCase(e.getMessage(), "Connection leak detection triggered")) {
						ServerHandler.getServer().logSevere("{SQL} Connection leak due to server lag has been detected, restarting database connection.");
					} else { e.printStackTrace(); }
				}
			}
		}
		return this.connection;
	}
	
   /**
	* Checks if the Connection Object isClosed.
	* 
	* @return If the Connection isClosed.
	*/
	protected boolean isClosed(final PreparedStatement object) {
		try {
			if (object == null || object.isClosed()) {
				return true;
			}
		} catch (AbstractMethodError | NoClassDefFoundError e) { return false; } 
		  catch (SQLException e) { 
			  ServerHandler.getServer().logSevere("{SQL} [11] Failed to close database connection."); 
			  ServerHandler.getServer().sendDebugTrace(e); 
			  return true; 
		}
		return false;
	}
	
   /**
	* Checks if the Connection Object isClosed.
	* 
	* @return If the Connection isClosed.
	*/
	protected boolean isClosed(final ResultSet object) {
		try {
			if (object == null || object.isClosed()) {
				return true;
			}
		} catch (AbstractMethodError | NoClassDefFoundError e) { return false; } 
		  catch (SQLException e) { 
			  ServerHandler.getServer().logSevere("{SQL} [11] Failed to close database connection."); 
			  ServerHandler.getServer().sendDebugTrace(e); 
			  return true; 
		}
		return false;
	}
	
   /**
	* Checks if the Connection Object isClosed.
	* 
	* @return If the Connection isClosed.
	*/
	protected boolean isClosed(final Connection object) {
		try {
			if (this.hikari == null || this.hikari.isClosed() || object == null || object.isClosed()) {
				return true;
			}
		} catch (AbstractMethodError | NoClassDefFoundError e) { return false; } 
		  catch (SQLException e) { 
			  ServerHandler.getServer().logSevere("{SQL} [11] Failed to close database connection."); 
			  ServerHandler.getServer().sendDebugTrace(e); 
			  return true; 
		}
		return false;
	}
	
   /**
	* Closes the specified connections.
	* 
	* @param ps - the PreparedStatement being closed.
	* @param rs - the ResultSet being closed.
	* @param conn - the Connection being closed.
	* @param force - If the connection should be forced to close.
	*/
	protected void close(final PreparedStatement ps, final ResultSet rs, final Connection conn, final boolean force) {
		try {
			if (!this.isClosed(ps)) {
				ps.close();
			}
			if (!this.isClosed(rs)) {
				rs.close();
			}
			if (!this.stopConnection && (!this.isClosed(conn) && (!ConfigHandler.getConfig().sqlEnabled() || force))) {
				this.closeLater(conn, force);
			}
		} catch (SQLException e) { 
			ServerHandler.getServer().logSevere("{SQL} [10] Failed to close database connection."); 
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Closes the specified connection after 5 second(s) if the database is left IDLE.
	* Prevents Database closed and/or locked errors.
	* 
	* @param conn - the Connection being closed.
	* @param force - If the connection should be forced to close.
	*/
	protected void closeLater(final Connection conn, final boolean force) { 
		this.stopConnection = true;
		if (ItemJoin.getInstance().isEnabled()) {
			SchedulerUtils.getScheduler().runLater(100L, () -> {
				try {
					if (!this.isClosed(conn) && (!ConfigHandler.getConfig().sqlEnabled() || force)) {
						conn.close();
						this.hikari.close();
						this.stopConnection = false;
					}
				} catch (SQLException e) { 
					ServerHandler.getServer().logSevere("{SQL} [10] Failed to close database connection."); 
					ServerHandler.getServer().sendDebugTrace(e);
				}
			});
		} else {
			try {
				if (!this.isClosed(conn) && (!ConfigHandler.getConfig().sqlEnabled() || force)) {
					conn.close();
					this.hikari.close();
					this.stopConnection = false;
				}
			} catch (SQLException e) {
				ServerHandler.getServer().logSevere("{SQL} [10] Failed to close database connection."); 
				ServerHandler.getServer().sendDebugTrace(e);
			}
		}
	}
	
   /**
	* Loads the database source connection information.
	* 
	*/
	protected void loadSource() {
		this.hikariConfig = new HikariConfig();
		FileConfiguration config = ConfigHandler.getConfig().getFile("config.yml");
	    String database = (config.getString("Database.table") != null ? config.getString("Database.table") : config.getString("Database.database"));
		if (ConfigHandler.getConfig().sqlEnabled()) {
			this.hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getString("Database.host") + ":" + config.getString("Database.port") + "/" + database + "?useSSL=false" + "&createDatabaseIfNotExist=true" + "&allowPublicKeyRetrieval=true");
		    this.hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
	    	this.hikariConfig.setConnectionTimeout(TimeUnit.MINUTES.toMillis(60));
	    	this.hikariConfig.setValidationTimeout(TimeUnit.SECONDS.toMillis(100));
		    this.hikariConfig.setUsername(config.getString("Database.user"));
		    this.hikariConfig.setPassword(config.getString("Database.pass"));
		    this.constConnection = true;
		} else {
			this.hikariConfig.setJdbcUrl("jdbc:sqlite:" + this.getDatabaseFile());
			this.hikariConfig.setDriverClassName("org.sqlite.JDBC");
	    	this.hikariConfig.setConnectionTimeout(TimeUnit.SECONDS.toMillis(100));
	    	this.hikariConfig.setValidationTimeout(TimeUnit.SECONDS.toMillis(100));
	    	this.hikariConfig.setMaxLifetime(TimeUnit.MINUTES.toMillis(10));
		}
		this.hikariConfig.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(300));
		this.hikariConfig.setConnectionTestQuery("SELECT 1");
	    this.hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
	    this.hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
	    this.hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
	    this.hikariConfig.addDataSourceProperty("userServerPrepStmts", "true");

	    this.hikariConfig.setMaximumPoolSize(10);
	}
	
   /**
	* Checks if the connection is an SQL Constant.
	* 
	*/
	public boolean getConstant() {
		return this.constConnection;
	}
	
   /**
	* Gets the database file.
	* 
	* @return The Database File.
	*/
	protected File getDatabaseFile() {
		File dataFolder = new File(ItemJoin.getInstance().getDataFolder(), this.dataFolder + ".db");
		if (!dataFolder.exists()) {
			try { dataFolder.createNewFile(); } 
			catch (IOException e) { 
				ServerHandler.getServer().logSevere("{SQL} File write error: " + this.dataFolder + ".db."); 
				ServerHandler.getServer().sendDebugTrace(e);
			}
		}
		return dataFolder;
	}
}