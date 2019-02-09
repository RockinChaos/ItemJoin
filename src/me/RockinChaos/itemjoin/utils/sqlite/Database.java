package me.RockinChaos.itemjoin.utils.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
			ServerHandler.sendDebugMessage("[SQLITE] Unable to connect to database!");
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
			ServerHandler.sendDebugMessage("[SQLITE] Failed to execute database statement.");
			ServerHandler.sendDebugTrace(e);
			return false;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection (3ZA5DB654).");
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
			ServerHandler.sendDebugMessage("[SQLITE] Failed to execute database statement.");
			ServerHandler.sendDebugTrace(e);
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection (3ZA5DB654)."); 
				ServerHandler.sendDebugTrace(e2);
			}
			return null;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) { 
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection (3ZA5DB654)."); 
				ServerHandler.sendDebugTrace(e); 
			}
		}
		try {
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) { 
			ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection (3ZA5DB654)."); 
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
			ServerHandler.sendDebugMessage("[SQLITE] Failed to execute database statement.");
			ServerHandler.sendDebugTrace(e);
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection (3ZA5DB654)."); 
				ServerHandler.sendDebugTrace(e2);
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) { 
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection (3ZA5DB654)."); 
				ServerHandler.sendDebugTrace(e);
			}
		}
		return null;
	}
	
	public List < List < String >> queryTableData(final String statement, final String...row) {
		final List < List < String > > existingData = new ArrayList < List < String > > ();
		try (Connection conn = this.getSQLConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(statement)) {
			while (rs.next()) {
				final List < String > columnData = new ArrayList < String > ();
				for (final String singleRow: row) {
					columnData.add(rs.getString(singleRow));
				}
				existingData.add(columnData);
			}
			conn.close();
			rs.close();
		} catch (SQLException e) {
			ServerHandler.sendDebugMessage("[SQLITE] Failed to execute database statement.");
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
				ServerHandler.sendDebugMessage("[SQLITE] Failed to execute database statement.");
				ServerHandler.sendDebugTrace(e);
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection (3ZA5DB654)."); 
				ServerHandler.sendDebugTrace(e2);
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) { 
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection (3ZA5DB654)."); 
				ServerHandler.sendDebugTrace(e);
			}
		}
		return null;
	}

	public boolean columnExists(final String statement) {
		try (Connection conn = this.getSQLConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(statement)) {
			conn.close();
			rs.close();
			return true;
		} catch (SQLException e) {
			if (Utils.containsIgnoreCase(e.getMessage(), "no such column")) {
				return false;
			} else {
				ServerHandler.sendDebugMessage("[SQLITE] Failed to execute database statement.");
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
			ServerHandler.sendDebugMessage("[SQLITE] Failed to check if a table exists..");
			ServerHandler.sendDebugTrace(e);
		}
		return tExists;
	}
	
	public boolean isInDatabase(String StatementString) {
		try {
			Statement statement = SQLite.getDatabase("database").getSQLConnection().createStatement();
			ResultSet result = statement.executeQuery(StatementString);
			try {
				if (!result.isBeforeFirst()) {
					ServerHandler.sendDebugMessage("[SQLite] Result set is empty.");
					return false;
				} else {
					ServerHandler.sendDebugMessage("[SQLite] Result set is not empty.");
					return true;
				}
			} finally { result.close(); statement.close(); SQLite.getDatabase("database").closeConnection();}
		} catch (Exception e) {
			ServerHandler.sendDebugMessage("Could not read from the database.db file, some ItemJoin features have been disabled!");
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
			ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection."); 
			ServerHandler.sendDebugTrace(e);
		}
	}
	
	public void openConnection() {
		try {
			this.getSQLConnection();
		} catch (Exception e) {
			ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection."); 
			ServerHandler.sendDebugTrace(e);
		}
	}
	
	public void closeConnection() {
		try {
			this.connection.close();
		} catch (SQLException | NullPointerException e) { 
			if (e.getCause() != null) {
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection."); 
				ServerHandler.sendDebugTrace(e);
			}
		}
	}
}