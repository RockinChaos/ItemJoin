package me.RockinChaos.itemjoin.utils.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import me.RockinChaos.itemjoin.utils.sqlite.Database;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
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
	
	public static Database getDatabase(final String dbname) {
		if (getDatabases().get(dbname) == null) {
			try {
				final Database db = new SQLite(dbname, "");
				databases.put(dbname, db);
			} catch (Exception e) { ConfigHandler.getLogger().sqLiteClose(e, dbname); }
		}
		return getDatabases().get(dbname);
	}
	
	public static void purgeDatabase(final String dbname) {
		File dataFolder = new File(ItemJoin.getInstance().getDataFolder(), dbname + ".db");
		if (dataFolder.exists()) {
			try { dataFolder.delete(); } 
			catch (Exception e) { ConfigHandler.getLogger().sqLitePurge(e, dbname); }
		}
	}
	
	@Override
	public Connection getSQLConnection() {
		File dataFolder = new File(ItemJoin.getInstance().getDataFolder(), dbname + ".db");
		if (!dataFolder.exists()) {
			try { dataFolder.createNewFile(); } 
			catch (IOException e) { ConfigHandler.getLogger().sqLiteError(e, dbname); }
		} try {
			if ((connection != null) && (!connection.isClosed())) { return connection; }
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
			return connection;
		} catch (SQLException e) { ConfigHandler.getLogger().sqLiteException(e); } catch (ClassNotFoundException e) { ConfigHandler.getLogger().sqLiteMissing(e); }
		return null;
	}
	
	@Override
	public void load() {
		connection = getSQLConnection();
		try {
			Statement s = connection.createStatement();
			s.executeUpdate(createTable);
			s.close();
			initialize();
		} catch (SQLException e) { ServerHandler.sendDebugTrace(e); }
	}
}