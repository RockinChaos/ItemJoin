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
			} catch (Exception e) { 
				ServerHandler.logSevere("{SQLite} Failed to close database " + dbname + ".db connection.");
				ServerHandler.sendDebugTrace(e); 
			}
		}
		return getDatabases().get(dbname);
	}
	
	public static void purgeDatabase(final String dbname) {
		File dataFolder = new File(ItemJoin.getInstance().getDataFolder(), dbname + ".db");
		if (dataFolder.exists()) {
			try { dataFolder.delete(); } 
			catch (Exception e) {
				ServerHandler.logSevere("{SQLite} Failed to close database " + dbname + ".db after purging.");
				ServerHandler.sendDebugTrace(e); 
			}
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
				if (this.connection != null && !this.connection.isClosed()) { 
			    	return this.connection; 
			    } else {
			    	Class.forName("com.mysql.jdbc.Driver");
			    	this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + table + "?useSSL=false", Utils.decrypt(user), Utils.decrypt(pass));
			        return this.connection;
			    }
			} catch (Exception e) { 
					ServerHandler.logSevere("{MySQL} Unable to connect to the defined MySQL database, check your settings.");
					ServerHandler.sendDebugTrace(e); 
			}
			return null;
		} else {
			File dataFolder = new File(ItemJoin.getInstance().getDataFolder(), this.dbname + ".db");
			if (!dataFolder.exists()) {
				try { dataFolder.createNewFile(); } 
				catch (IOException e) { 
					ServerHandler.logSevere("{SQLite} File write error: " + this.dbname + ".db.");
					ServerHandler.sendDebugTrace(e);
				}
			} try {
				if ((connection != null) && (!connection.isClosed())) { return this.connection; }
				Class.forName("org.sqlite.JDBC");
				this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
				return this.connection;
			} catch (SQLException e) { 
				ServerHandler.logSevere("{SQLite} SQLite exception on initialize.");
				ServerHandler.sendDebugTrace(e);
			} catch (ClassNotFoundException e) { 
				ServerHandler.logSevere("{SQLite} You need the SQLite JBDC library, see: &ahttps://bitbucket.org/xerial/sqlite-jdbc/downloads/ &rand put it in /lib folder.");
				ServerHandler.sendDebugTrace(e);
			}
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