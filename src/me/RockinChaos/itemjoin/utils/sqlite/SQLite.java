package me.RockinChaos.itemjoin.utils.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import me.RockinChaos.itemjoin.utils.sqlite.Database;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class SQLite extends Database {
	private String dbname;
	private String createTable;
	private static Map < String, Database > databases = new HashMap < String, Database > ();
	
	public SQLite(String databaseName, String createStatement) {
		dbname = databaseName;
		createTable = createStatement;
	}
	
	public void initializeDatabase(final String databaseName, final String createStatement) {
		final Database db = new SQLite(databaseName, createStatement);
		db.load();
		databases.put(databaseName, db);
	}
	
	public static Map < String, Database > getDatabases() {
		return databases;
	}
	
	public static Database getDatabase(final String databaseName) {
		if (getDatabases().get(databaseName) == null) {
			try {
				final Database db = new SQLite(databaseName, "");
				databases.put(databaseName, db);
			} catch (Exception e) {
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close database connection.");
				ServerHandler.sendDebugTrace(e);
			}
		}
		return getDatabases().get(databaseName);
	}
	
	public static void purgeDatabase(final String databaseName) {
		File dataFolder = new File(ItemJoin.getInstance().getDataFolder(), databaseName + ".db");
		if (dataFolder.exists()) {
			try {
				dataFolder.delete();
			} catch (Exception e) {
				ServerHandler.sendDebugMessage("[SQLITE] Failed to close purge database " + databaseName + ".db");
				ServerHandler.sendDebugTrace(e);
			}
		}
	}
	
	public Connection getSQLConnection() {
		File dataFolder = new File(ItemJoin.getInstance().getDataFolder(), dbname + ".db");
		if (!dataFolder.exists()) {
			try {
				dataFolder.createNewFile();
			} catch (IOException e) {
				ItemJoin.getInstance().getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db");
			}
		}
		try {
			if ((connection != null) && (!connection.isClosed())) {
				return connection;
			}
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
			return connection;
		} catch (SQLException e) {
			ItemJoin.getInstance().getLogger().log(Level.SEVERE, "SQLite exception on initialize", e);
			ServerHandler.sendDebugTrace(e);
		} catch (ClassNotFoundException e) {
			ItemJoin.getInstance().getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
			ServerHandler.sendDebugTrace(e);
		}
		return null;
	}
	
	public void load() {
		connection = getSQLConnection();
		try {
			Statement s = connection.createStatement();
			s.executeUpdate(createTable);
			s.close();
		} catch (SQLException e) { ServerHandler.sendDebugTrace(e); }
		initialize();
	}
}