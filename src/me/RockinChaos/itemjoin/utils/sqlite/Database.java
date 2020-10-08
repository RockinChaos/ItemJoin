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
	
	protected abstract void load();
	protected abstract Connection getSQLConnection();
	protected Connection connection;
	
   /**
	* Initializes the SQLite or MySQL connection.
	* 
	*/
	public void initialize() {
		this.connection = this.getSQLConnection();
		try {
			final PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM EMPTY_TABLE");
			final ResultSet rs = ps.executeQuery();
			this.close(ps, rs, this.connection);
		} catch (SQLException e) { 
			ServerHandler.getServer().logSevere("{SQLite} Unable to connect to database!");
			ServerHandler.getServer().sendSevereTrace(e);
		}
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
			conn = this.getSQLConnection();
			ps = conn.prepareStatement(statement);
			return !ps.execute();
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} [1] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [1] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + !this.getSQLConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [1] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [1] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
			return false;
		} finally {
			this.close(ps, null, conn);
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
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} [2] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [2] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + !this.getSQLConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [2] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [2] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn);
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
			conn = this.getSQLConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			while (rs.next()) {
				objects.add(rs.getObject(row));
			}
			return objects;
		} catch (Exception e) {
			ServerHandler.getServer().logSevere("{SQLite} [3] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [3] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + !this.getSQLConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [3] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [3] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn);
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
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.getSQLConnection();
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
				ServerHandler.getServer().logSevere("{SQLite} [4] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + !this.getSQLConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [4] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [4] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn);
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
		} catch (Exception e) {
				ServerHandler.getServer().logSevere("{SQLite} [5] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [5] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + !this.getSQLConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [5] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [5] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
		} finally {
			this.close(ps, rs, conn);
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
			conn = this.getSQLConnection();
			ps = conn.prepareStatement(statement);
			rs = ps.executeQuery();
			return true;
		} catch (Exception e) {
			if (Utils.getUtils().containsIgnoreCase(e.getMessage(), "no such column")) {
				return false;
			} else {
				ServerHandler.getServer().logSevere("{SQLite} [6] Failed to execute database statement.");
			try {
				ServerHandler.getServer().logSevere("{SQLite} [6] Database Status: Open: " + !this.getSQLConnection().isClosed() + "! Writable: " + !this.getSQLConnection().isReadOnly() + "!");
			} catch (Exception e2) {
				ServerHandler.getServer().logSevere("{SQLite} [6] Failed to determine the Database Status.");
			}
			ServerHandler.getServer().logSevere("{SQLite} [6] Statement: " + statement);
			ServerHandler.getServer().sendSevereTrace(e);
			}
		} finally {
			this.close(ps, rs, conn);
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
			conn = this.getSQLConnection();
			rs = conn.getMetaData().getTables(null, null, tableName, null);
			while (rs.next()) {
				String tName = rs.getString("TABLE_NAME");
				if (tName != null && tName.equals(tableName)) {
					tExists = true;
					break;
				}
			}
		} catch (SQLException e) {
			ServerHandler.getServer().logSevere("{SQLite} [9] Failed to check if a table exists.");
			ServerHandler.getServer().sendDebugTrace(e);
		} finally {
			this.close(null, rs, conn);
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
			conn = this.getSQLConnection();
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
			this.close(null, rs, conn);
		}
		return false;
	}
	
   /**
	* Closes the specific PreparedStatement and ResultSet.
	* 
	* @param ps - the PreparedStatement being closed.
	* @param rs - the ResultSet being closed.
	* @param conn - the Connection being closed.
	*/
	public void close(final PreparedStatement ps, final ResultSet rs, final Connection conn) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (conn != null) {
				this.closeConnection();
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
	public void closeConnection() {
		this.close(null, null, this.connection);
	}
}