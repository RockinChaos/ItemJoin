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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public abstract class Database {
	
	public abstract void load();
	public abstract Connection getSQLConnection();
	protected Connection connection;
	protected boolean remoteEnabled = false;
	
   /**
	* Initializes the SQLite or MySQL connection.
	* 
	*/
	public void initialize(final boolean isRemote) {
		this.connection = this.getSQLConnection();
		this.remoteEnabled = isRemote;
		try {
			final PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM EMPTY_TABLE");
			final ResultSet rs = ps.executeQuery();
			this.close(ps, rs);
		} catch (SQLException e) { 
			ServerHandler.getServer().logSevere("{SQLite} Unable to connect to database!");
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Executes a specified SQL statement.
	* 
	* @param statement - the statement to be executed.
	* @return The statement was successfully executed.
	*/
	public Boolean executeStatement(final String statement) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.getSQLConnection();
			ps = conn.prepareStatement(statement);
			return !ps.execute();
		} catch (SQLException e) {
			ServerHandler.getServer().logSevere("{SQLite} [1] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [1] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + this.getSQLConnection().isReadOnly() + "!");
			} catch (SQLException e2) {
				ServerHandler.getServer().logSevere("{SQLite} [1] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().sendDebugTrace(e);
			return false;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !this.remoteEnabled) {
					conn.close();
				}
			} catch (SQLException e) {
				ServerHandler.getServer().logSevere("{SQLite} [1] Failed to close database connection.");
				ServerHandler.getServer().sendDebugTrace(e);
				return false;
			}
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
			conn = this.getSQLConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getObject(row);
			}
		} catch (SQLException e) {
			ServerHandler.getServer().logSevere("{SQLite} [2] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [2] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + this.getSQLConnection().isReadOnly() + "!");
			} catch (SQLException e2) {
				ServerHandler.getServer().logSevere("{SQLite} [2] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().sendDebugTrace(e);
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !this.remoteEnabled) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.getServer().logSevere("{SQLite} [2] Failed to close database connection."); 
				ServerHandler.getServer().sendDebugTrace(e2);
			}
			return null;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !this.remoteEnabled) {
					conn.close();
				}
			} catch (SQLException e) { 
				ServerHandler.getServer().logSevere("{SQLite} [3] Failed to close database connection."); 
				ServerHandler.getServer().sendDebugTrace(e); 
			}
		}
		try {
			if (ps != null) {
				ps.close();
			}
			if (conn != null && !this.remoteEnabled) {
				conn.close();
			}
		} catch (SQLException e) { 
			ServerHandler.getServer().logSevere("{SQLite} [4] Failed to close database connection."); 
			ServerHandler.getServer().sendDebugTrace(e); 
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		final List < Object > objects = new ArrayList < Object > ();
		try {
			conn = this.getSQLConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			while (rs.next()) {
				objects.add(rs.getObject(row));
			}
			return objects;
		} catch (SQLException e) {
			ServerHandler.getServer().logSevere("{SQLite} [3] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [3] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + this.getSQLConnection().isReadOnly() + "!");
			} catch (SQLException e2) {
				ServerHandler.getServer().logSevere("{SQLite} [3] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().sendDebugTrace(e);
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !this.remoteEnabled) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.getServer().logSevere("{SQLite} [5] Failed to close database connection."); 
				ServerHandler.getServer().sendDebugTrace(e2);
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !this.remoteEnabled) {
					conn.close();
				}
			} catch (SQLException e) { 
				ServerHandler.getServer().logSevere("{SQLite} [6] Failed to close database connection."); 
				ServerHandler.getServer().sendDebugTrace(e);
			}
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
	public List < List < String >> queryTableData(final String statement, final String...row) {
		final List < List < String > > existingData = new ArrayList < List < String > > ();
		try (ResultSet rs = this.getSQLConnection().prepareStatement(statement).executeQuery()) {
			while (rs.next()) {
				final List < String > columnData = new ArrayList < String > ();
				for (final String singleRow: row) {
					columnData.add(rs.getString(singleRow));
				}
				existingData.add(columnData);
			}
			rs.close();
		} catch (SQLException e) {
			ServerHandler.getServer().logSevere("{SQLite} [4] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [4] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + this.getSQLConnection().isReadOnly() + "!");
			} catch (SQLException e2) {
				ServerHandler.getServer().logSevere("{SQLite} [4] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().sendDebugTrace(e);
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		final List < Object > objects = new ArrayList < Object > ();
		final Map < String, List < Object >> map = new HashMap < String, List < Object >> ();
		try {
			conn = this.getSQLConnection();
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
		} catch (SQLException e) {
				ServerHandler.getServer().logSevere("{SQLite} [5] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [5] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + this.getSQLConnection().isReadOnly() + "!");
			} catch (SQLException e2) {
				ServerHandler.getServer().logSevere("{SQLite} [5] Failed to determine the Database Status.");
			}
				ServerHandler.getServer().sendDebugTrace(e);
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !this.remoteEnabled) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.getServer().logSevere("{SQLite} [7] Failed to close database connection."); 
				ServerHandler.getServer().sendDebugTrace(e2);
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !this.remoteEnabled) {
					conn.close();
				}
			} catch (SQLException e) { 
				ServerHandler.getServer().logSevere("{SQLite} [8] Failed to close database connection."); 
				ServerHandler.getServer().sendDebugTrace(e);
			}
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
		try (ResultSet rs = this.getSQLConnection().prepareStatement(statement).executeQuery()) {
			rs.close();
			return true;
		} catch (SQLException e) {
			if (Utils.getUtils().containsIgnoreCase(e.getMessage(), "no such column")) {
				return false;
			} else {
				ServerHandler.getServer().logSevere("{SQLite} [6] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [6] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + this.getSQLConnection().isReadOnly() + "!");
			} catch (SQLException e2) {
				ServerHandler.getServer().logSevere("{SQLite} [6] Failed to determine the Database Status.");
			}
				ServerHandler.getServer().sendDebugTrace(e);
			}
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
		try {
			try (ResultSet rs = getSQLConnection().getMetaData().getTables(null, null, tableName, null)) {
				while (rs.next()) {
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
		try {
			ResultSet result = this.getSQLConnection().prepareStatement(statement).executeQuery();
			try {
				if (!result.isBeforeFirst()) {
					ServerHandler.getServer().logDebug("{SQLite} Result set is empty.");
					return false;
				} else {
					ServerHandler.getServer().logDebug("{SQLite} Result set is not empty.");
					return true;
				}
			} finally { result.close(); SQDrivers.getDatabase("database").closeConnection(); }
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} Could not read from the database.db file, some ItemJoin features have been disabled!");
			ServerHandler.getServer().sendDebugTrace(e);
		}
		return false;
	}
	
   /**
	* Closes the specific PreparedStatement and ResultSet.
	* 
	* @param ps - the PreparedStatement being closed.
	* @param rs - the ResultSet being closed.
	*/
	public void close(final PreparedStatement ps, final ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) { 
			ServerHandler.getServer().logSevere("{SQLite} [10] Failed to close database connection."); 
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Opens a new database connection.
	*
	*/
	public void openConnection() {
		try {
			this.getSQLConnection();
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} [11] Failed to close database connection."); 
			ServerHandler.getServer().sendDebugTrace(e);
		}
	}
	
   /**
	* Closes the active database connection.
	* 
	*/
	public void closeConnection() {
		try {
			if (!this.remoteEnabled) {
				this.connection.close();
			}
		} catch (SQLException | NullPointerException e) { 
			if (e.getCause() != null) {
				ServerHandler.getServer().logSevere("{SQLite} [12] Failed to close database connection."); 
				ServerHandler.getServer().sendDebugTrace(e);
			}
		}
	}
}