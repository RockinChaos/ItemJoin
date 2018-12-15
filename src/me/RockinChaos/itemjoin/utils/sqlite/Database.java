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
		if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
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
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
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
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
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
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection (3ZA5DB654)."); 
			if (ServerHandler.hasDebuggingMode()) { e2.printStackTrace(); } 
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
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); } 
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
		if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); } 
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
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection (3ZA5DB654)."); 
			if (ServerHandler.hasDebuggingMode()) { e2.printStackTrace(); }
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
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			}
		}
		return null;
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
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e2) { 
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection (3ZA5DB654)."); 
			if (ServerHandler.hasDebuggingMode()) { e2.printStackTrace(); }
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
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			}
		}
		return null;
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
		if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return tExists;
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
		if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
	}
	
	public void openConnection() {
		try {
			this.getSQLConnection();
		} catch (Exception e) {
			ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection."); 
		if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
	}
	
	public void closeConnection() {
		try {
			this.connection.close();
		} catch (SQLException | NullPointerException e) { 
			if (e.getCause() != null) {
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection."); 
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			}
		}
	}
	
}