package me.RockinChaos.itemjoin.utils.sqlite;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.Database;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class SQLite extends Database {
	private String dbname;
	private String createTable;
	private static boolean isMySQL = false;
	private static int port = 3306;
	private static String host = "localhost";
	private static String table = "database";
	private static String user = "root";
	private static String pass = "password";
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
	
	public static void loadSQLDatabase() {
		if (ConfigHandler.getConfig("config.yml").getString("Database.port") != null) { port = ConfigHandler.getConfig("config.yml").getInt("Database.port"); }
		if (ConfigHandler.getConfig("config.yml").getString("Database.host") != null) { host = ConfigHandler.getConfig("config.yml").getString("Database.host"); }
		if (ConfigHandler.getConfig("config.yml").getString("Database.table") != null) { table = ConfigHandler.getConfig("config.yml").getString("Database.table"); }
		if (ConfigHandler.getConfig("config.yml").getString("Database.user") != null) { user = Utils.encrypt(ConfigHandler.getConfig("config.yml").getString("Database.user")); }
		if (ConfigHandler.getConfig("config.yml").getString("Database.pass") != null) { pass = Utils.encrypt(ConfigHandler.getConfig("config.yml").getString("Database.pass")); }
	}	
	
	@Override
	public Connection getSQLConnection() {
		if (ConfigHandler.getConfig("config.yml").getString("Database.MySQL") != null && ConfigHandler.getConfig("config.yml").getBoolean("Database.MySQL")) {
			isMySQL = true;
			try { 
				if (connection != null && !connection.isClosed()) { 
			    	return connection; 
			    } else {
			    	Class.forName("com.mysql.jdbc.Driver");
			        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + table + "?useSSL=false", Utils.decrypt(user), Utils.decrypt(pass));
			        return connection;
			    }
			} catch (Exception e) { 
					ServerHandler.sendDebugMessage("&c&lERROR: &cUnable to get the defined MySQL database, check your settings.");
					ServerHandler.sendDebugTrace(e); 
			}
			return null;
		} else {
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
	}
	
	@Override
	public void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
				connection = getSQLConnection();
				try {
					Statement s = connection.createStatement();
					s.executeUpdate(createTable);
					s.close();
					initialize();
				} catch (SQLException e) { ServerHandler.sendDebugTrace(e); }
            }
        }.runTaskAsynchronously(ItemJoin.getInstance());
	}
	
	public static boolean MySQLEnabled() {
		return isMySQL;
	}
}