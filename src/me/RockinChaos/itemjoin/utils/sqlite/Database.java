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
	protected Connection connection;
	public abstract Connection getSQLConnection();
	public abstract void load();
	
	public void initialize() {
		this.connection = this.getSQLConnection();
		try {
			final PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM EMPTY_TABLE");
			final ResultSet rs = ps.executeQuery();
			this.close(ps, rs);
		} catch (SQLException e) { 
			ServerHandler.logSevere("{SQLite} Unable to connect to database!");
			ServerHandler.sendDebugTrace(e);
		}
	}
	
	public Boolean executeStatement(final String statement) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = this.getSQLConnection();
			ps = conn.prepareStatement(statement);
			return !ps.execute();
		} catch (SQLException e) {
			ServerHandler.logSevere("{SQLite} Failed to execute database statement.");
			ServerHandler.sendDebugTrace(e);
			return false;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !SQLite.MySQLEnabled()) {
					conn.close();
				}
			} catch (SQLException e) {
				ServerHandler.logSevere("{SQLite} Failed to close database connection.");
				ServerHandler.sendDebugTrace(e);
				return false;
			}
		}
	}
	
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
			ServerHandler.logSevere("{SQLite} Failed to execute database statement.");
			ServerHandler.sendDebugTrace(e);
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !SQLite.MySQLEnabled()) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.logSevere("{SQLite} Failed to close database connection."); 
				ServerHandler.sendDebugTrace(e2);
			}
			return null;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !SQLite.MySQLEnabled()) {
					conn.close();
				}
			} catch (SQLException e) { 
				ServerHandler.logSevere("{SQLite} Failed to close database connection."); 
				ServerHandler.sendDebugTrace(e); 
			}
		}
		try {
			if (ps != null) {
				ps.close();
			}
			if (conn != null && !SQLite.MySQLEnabled()) {
				conn.close();
			}
		} catch (SQLException e) { 
			ServerHandler.logSevere("{SQLite} Failed to close database connection."); 
			ServerHandler.sendDebugTrace(e); 
		}
		return null;
	}
	
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
			ServerHandler.logSevere("{SQLite} Failed to execute database statement.");
			ServerHandler.sendDebugTrace(e);
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !SQLite.MySQLEnabled()) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.logSevere("{SQLite} Failed to close database connection."); 
				ServerHandler.sendDebugTrace(e2);
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !SQLite.MySQLEnabled()) {
					conn.close();
				}
			} catch (SQLException e) { 
				ServerHandler.logSevere("{SQLite} Failed to close database connection."); 
				ServerHandler.sendDebugTrace(e);
			}
		}
		return null;
	}
	
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
			ServerHandler.logSevere("{SQLite} Failed to execute database statement.");
			ServerHandler.sendDebugTrace(e);
		}
		return existingData;
	}
	
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
				ServerHandler.logSevere("{SQLite} Failed to execute database statement.");
				ServerHandler.sendDebugTrace(e);
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !SQLite.MySQLEnabled()) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.logSevere("{SQLite} Failed to close database connection."); 
				ServerHandler.sendDebugTrace(e2);
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null && !SQLite.MySQLEnabled()) {
					conn.close();
				}
			} catch (SQLException e) { 
				ServerHandler.logSevere("{SQLite} Failed to close database connection."); 
				ServerHandler.sendDebugTrace(e);
			}
		}
		return null;
	}

	public boolean columnExists(final String statement) {
		try (ResultSet rs = this.getSQLConnection().prepareStatement(statement).executeQuery()) {
			rs.close();
			return true;
		} catch (SQLException e) {
			if (Utils.containsIgnoreCase(e.getMessage(), "no such column")) {
				return false;
			} else {
				ServerHandler.logSevere("{SQLite} Failed to execute database statement.");
				ServerHandler.sendDebugTrace(e);
			}
		}
		return false;
	}
	
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
			ServerHandler.logSevere("{SQLite} Failed to check if a table exists.");
			ServerHandler.sendDebugTrace(e);
		}
		return tExists;
	}
	
	public boolean dataExists(String statement) {
		try {
			ResultSet result = this.getSQLConnection().prepareStatement(statement).executeQuery();
			try {
				if (!result.isBeforeFirst()) {
					ServerHandler.logDebug("{SQLite} Result set is empty.");
					return false;
				} else {
					ServerHandler.logDebug("{SQLite} Result set is not empty.");
					return true;
				}
			} finally { result.close(); SQLite.getDatabase("database").closeConnection(); }
		} catch (Exception e) {
			ServerHandler.logSevere("{SQLite} Could not read from the database.db file, some ItemJoin features have been disabled!");
			ServerHandler.sendDebugTrace(e);
		}
		return false;
	}
	
	public void close(final PreparedStatement ps, final ResultSet rs) {
		try {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) { 
			ServerHandler.logSevere("{SQLite} Failed to close database connection."); 
			ServerHandler.sendDebugTrace(e);
		}
	}
	
	public void openConnection() {
		try {
			this.getSQLConnection();
		} catch (Exception e) {
			ServerHandler.logSevere("{SQLite} Failed to close database connection."); 
			ServerHandler.sendDebugTrace(e);
		}
	}
	
	public void closeConnection() {
		try {
			if (!SQLite.MySQLEnabled()) {
				this.connection.close();
			}
		} catch (SQLException | NullPointerException e) { 
			if (e.getCause() != null) {
				ServerHandler.logSevere("{SQLite} Failed to close database connection."); 
				ServerHandler.sendDebugTrace(e);
			}
		}
	}
}