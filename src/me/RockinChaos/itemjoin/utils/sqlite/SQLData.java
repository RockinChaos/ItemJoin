package me.RockinChaos.itemjoin.utils.sqlite;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class SQLData {
	
	public static void saveAllToDatabase(Player player, String item) {
		saveToDatabase(player, item, "first-join", "");
		saveToDatabase(player, item, "ip-limit", "");
	}
	
	public static void createTables(String itemflag) {
		if (itemflag.equalsIgnoreCase("first-join") && !SQLite.getDatabase("database").tableExists("first_join")) {
			SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS first_join (`World_Name` varchar(32), `Player_Name` varchar(32), `Player_UUID` varchar(32), `Item_Name` varchar(32));");
		} else if (itemflag.equalsIgnoreCase("ip-limit") && !SQLite.getDatabase("database").tableExists("ip_limits")) {
			SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS ip_limits (`World_Name` varchar(32), `IP_Address` varchar(32), `Player_UUID` varchar(32), `Item_Name` varchar(32));");
		} else if (itemflag.contains("first-join:") && !SQLite.getDatabase("database").tableExists("first_commands")) {
			SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS first_commands (`World_Name` varchar(32), `Player_UUID` varchar(32), `Command_String` varchar(32));");
		} else if (itemflag.equalsIgnoreCase("enabled-players") || itemflag.equalsIgnoreCase("disabled-players")) {
			if (!SQLite.getDatabase("database").tableExists("enabled_players")) {
				SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS enabled_players (`World_Name` varchar(32), `Player_Name` varchar(32), `Player_UUID` varchar(32), `isEnabled` varchar(32));");
			}
		} else if (itemflag.equalsIgnoreCase("map-id") && !SQLite.getDatabase("database").tableExists("map_ids")) {
				SQLite.getDatabase("database").executeStatement("CREATE TABLE IF NOT EXISTS map_ids (`Map_IMG` varchar(32), `Map_ID` varchar(32));");
		}
		SQLite.getDatabase("database").closeConnection();
	}
	
	public static boolean isInDatabase(String itemflag, String StatementString) {
		try {
			createTables(itemflag);
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
			ServerHandler.sendDebugMessage("Could not read from the database.db file, the itemflags first-join and ip-limits have been disabled!");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return false;
	}
	
	public static void saveToDatabase(Player player, String item, String itemflag, String world) {
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		if (items != null && Utils.containsIgnoreCase(items.getString(".itemflags"), itemflag) || Utils.containsIgnoreCase(itemflag, "first-join:") || Utils.containsIgnoreCase(itemflag, "enabled-players") || Utils.containsIgnoreCase(itemflag, "disabled-players")) {
			createTables(itemflag);
			String realPlayer = "ALL";
			String realName = "ALL";
			if (player != null) { realPlayer = PlayerHandler.getPlayerID(player); realName = player.getName().toString(); }
			try {
				if (itemflag.equalsIgnoreCase("ip-limit") && !isInDatabase(itemflag, "SELECT * FROM ip_limits WHERE World_Name='" + player.getWorld().getName() + "' AND IP_Address='" + player.getAddress().getHostString() + "' AND Item_Name='" + item + "';")
						&& !isInDatabase(itemflag, "SELECT * FROM ip_limits WHERE World_Name='" + player.getWorld().getName() + "' AND IP_Address='" + player.getAddress().getHostString().replace(".", "") + "' AND Item_Name='" + item + "';")) {
					SQLite.getDatabase("database").executeStatement("INSERT INTO ip_limits (`World_Name`, `IP_Address`, `Player_UUID`, `Item_Name`) VALUES ('" + player.getWorld().getName() + "','" + player.getAddress().getHostString() + "','" + PlayerHandler.getPlayerID(player) + "','" + item + "')");
				} else if (itemflag.equalsIgnoreCase("first-join") && !isInDatabase(itemflag, "SELECT * FROM first_join WHERE World_Name='" + player.getWorld().getName() + "' AND Player_UUID='" + PlayerHandler.getPlayerID(player) + "' AND Item_Name='" + item + "';")) {
					SQLite.getDatabase("database").executeStatement("INSERT INTO first_join (`World_Name`, `Player_Name`, `Player_UUID`, `Item_Name`) VALUES ('" + player.getWorld().getName() + "','" + player.getName().toString() + "','" + PlayerHandler.getPlayerID(player) + "','" + item + "')");
				} else if (itemflag.contains("first-join:") && !isInDatabase(itemflag, "SELECT * FROM first_commands WHERE World_Name='" + player.getWorld().getName() + "' AND Player_UUID='" + PlayerHandler.getPlayerID(player) + "' AND Command_String='" + itemflag.replace("first-join: ", "").replace("first-join:", "") + "';")) {
					SQLite.getDatabase("database").executeStatement("INSERT INTO first_commands (`World_Name`, `Player_UUID`, `Command_String`) VALUES ('" + player.getWorld().getName() + "','" + PlayerHandler.getPlayerID(player) + "','" + itemflag.replace("first-join: ", "").replace("first-join:", "") + "')");
				} else if (itemflag.contains("enabled-players") || itemflag.contains("disabled-players")) {
					if (world.equalsIgnoreCase("Global") && realPlayer.equalsIgnoreCase("ALL")) {
						SQLite.getDatabase("database").executeStatement("DELETE FROM enabled_players;");
					}
					if (world.equalsIgnoreCase("Global") && !realPlayer.equalsIgnoreCase("ALL")) {
						SQLite.getDatabase("database").executeStatement("DELETE FROM enabled_players WHERE Player_UUID='" + realPlayer + "';");
					}
					if (!isInDatabase(itemflag, "SELECT * FROM enabled_players WHERE World_Name='" + world + "' AND Player_UUID='" + realPlayer + "';")) {
						SQLite.getDatabase("database").executeStatement("INSERT INTO enabled_players (`World_Name`, `Player_Name`, `Player_UUID`, `isEnabled`) VALUES ('" + world + "','" + realName + "','" + realPlayer + "','" + item + "')");
					} else if (!isInDatabase(itemflag, "SELECT * FROM enabled_players WHERE World_Name='" + world + "' AND Player_UUID='" + realPlayer + "' AND isEnabled='" + item + "';")) {
						SQLite.getDatabase("database").executeStatement("UPDATE enabled_players SET IsEnabled='" + item + "' WHERE World_Name='" + world + "' AND Player_UUID='" + realPlayer + "'");
					}
				}
			} catch (Exception e) {
				ItemJoin.getInstance().getServer().getLogger().severe("Could not save " + realName + " to the data file!");
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			}
		}
		SQLite.getDatabase("database").closeConnection();
	}
	
	public static int getMapID(String Image) {
			try {
				try {
					createTables("map-id");
					Statement stmt = SQLite.getDatabase("database").getSQLConnection().createStatement();
					try {
						ResultSet rset = stmt.executeQuery("SELECT * FROM map_ids WHERE Map_IMG='" + Image + "';");
						try {
							if (!rset.isBeforeFirst()) {} else {
								if (rset.getString(2) != null) {
									return Integer.parseInt(rset.getString(2));
								} else { }
							}
						} finally { rset.close(); }
					} finally { stmt.close(); }
				} finally { SQLite.getDatabase("database").closeConnection(); }
			} catch (Exception e) {
				ServerHandler.sendDebugMessage("Could not read from the database.db file!");
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
			}
			return 0;
	}
	
	public static void saveMapImage(String item, String itemflag, String Image, int mapID) {
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		if (items != null) {
			createTables(itemflag);
			try {
				if (itemflag.equalsIgnoreCase("map-id") && !isInDatabase(itemflag, "SELECT * FROM map_ids WHERE Map_IMG='" + Image + "';")
						&& !isInDatabase(itemflag, "SELECT * FROM map_ids WHERE Map_IMG='" + Image + "' AND Map_ID='" + mapID + "';")) {
					SQLite.getDatabase("database").executeStatement("INSERT INTO map_ids (`Map_IMG`, `Map_ID`) VALUES ('" + Image + "','" + mapID + "')");
					SQLite.getDatabase("database").closeConnection();
				}
			} catch (Exception e) {
				ItemJoin.getInstance().getServer().getLogger().severe("Could not save the mapId; " + mapID + "with the image; " + Image  + " to the data file!");
				ServerHandler.sendDebugTrace(e);
			}
		}
	}
	
	public static void purgeDatabaseData(String section, OfflinePlayer player) {
		String UUID = PlayerHandler.getOfflinePlayerID(player);
		if (section.equalsIgnoreCase("first_join") && SQLite.getDatabase("database").tableExists("first_join")) {
			SQLite.getDatabase("database").executeStatement("DELETE FROM " + section + " WHERE Player_UUID='" + UUID + "';");
			SQLite.getDatabase("database").closeConnection();
		} else if (section.equalsIgnoreCase("ip_limits") && SQLite.getDatabase("database").tableExists("ip_limits")) {
			SQLite.getDatabase("database").executeStatement("DELETE FROM " + section + " WHERE Player_UUID='" + UUID + "';");
			SQLite.getDatabase("database").closeConnection();
		}
	}
	
	public static Boolean hasFirstCommanded(Player player, String command) {
		if (Utils.containsIgnoreCase(command, "first-join:") && isInDatabase(command, "SELECT * FROM first_commands WHERE World_Name='" + player.getWorld().getName() + "' AND Player_UUID='" + PlayerHandler.getPlayerID(player) + "' AND Command_String='" + command.replace("first-join: ", "").replace("first-join:", "") + "';")) {
			return true;
		}
		return false;
	}
	
	public static Boolean hasFirstJoined(Player player, String item) {
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		String ItemFlags = items.getString(".itemflags");
		if (Utils.containsIgnoreCase(ItemFlags, "first-join") && isInDatabase("first-join", "SELECT * FROM first_join WHERE World_Name='" + player.getWorld().getName() + "' AND Player_UUID='" + PlayerHandler.getPlayerID(player) + "' AND Item_Name='" + item + "';")) {
			return true;
		}
		return false;
	}
	
	public static Boolean hasIPLimited(Player player, String item) {
		ConfigurationSection items = ConfigHandler.getItemSection(item);
		String ItemFlags = items.getString(".itemflags");
		if (Utils.containsIgnoreCase(ItemFlags, "ip-limit") && isLimited(player, item, player.getAddress().getHostString()) 
				|| Utils.containsIgnoreCase(ItemFlags, "ip-limit") && isLimited(player, item, player.getAddress().getHostString().replace(".", ""))) {
			return true;
		}
		return false;
	}
	
	public static Boolean hasImage(String item, String image) {
		if (isImageSaved(item, image)) {
			return true;
		}
		return false;
	}
	
	public static boolean isImageSaved(String item, String Image) {
		try {
			createTables("map-id");
			Statement statement = SQLite.getDatabase("database").getSQLConnection().createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM map_ids WHERE Map_IMG='" + Image + "';");
			try {
				if (!result.isBeforeFirst()) {
					return false;
				} else {
					if (result.getString(1).equalsIgnoreCase(Image)) {
						return true;
					} else { return false; }
				}
			} finally { result.close(); statement.close(); SQLite.getDatabase("database").closeConnection(); }
		} catch (Exception e) {
			ServerHandler.sendDebugMessage("Could not read from the database.db file, map item images have been disabled!");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return false;
	}
	
	public static boolean isLimited(Player player, String item, String ipAddresss) {
		try {
			createTables("ip-limit");
			Statement statement = SQLite.getDatabase("database").getSQLConnection().createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM ip_limits WHERE World_Name='" + player.getWorld().getName() + "' AND IP_Address='" + ipAddresss + "' AND Item_Name='" + item + "';");
			try {
				if (!result.isBeforeFirst()) {
					return false;
				} else {
					if (result.getString(3).equalsIgnoreCase(PlayerHandler.getPlayerID(player))) {
						return false;
					} else { return true; }
				}
			} finally { result.close(); statement.close(); SQLite.getDatabase("database").closeConnection(); }
		} catch (Exception e) {
			ServerHandler.sendDebugMessage("Could not read from the database.db file, ip-limit itemflag has been disabled!");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return false;
	}
	
	public static boolean isEnabled(final Player player) {
		try {
			try {
				createTables("enabled-players");
				Statement stmt = SQLite.getDatabase("database").getSQLConnection().createStatement();
				ArrayList < String > queries = new ArrayList < String > ();
				queries.add("SELECT * FROM enabled_players WHERE World_Name='" + player.getWorld().getName() + "' AND Player_UUID='" + PlayerHandler.getPlayerID(player) + "';");
				queries.add("SELECT * FROM enabled_players WHERE World_Name='" + "Global" + "' AND Player_UUID='" + PlayerHandler.getPlayerID(player) + "';");
				queries.add("SELECT * FROM enabled_players WHERE World_Name='" + "Global" + "' AND Player_UUID='" + "ALL" + "';");
				try {
					for (String q: queries) {
						ResultSet rset = stmt.executeQuery(q);
						try {
							if (!rset.isBeforeFirst()) {} else {
								if (rset.getString(4).equalsIgnoreCase("true")) {
									return true;
								} else { return false; }
							}
						} finally { rset.close(); }
					}
				} finally { stmt.close(); }
			} finally { SQLite.getDatabase("database").closeConnection(); }
		} catch (Exception e) {
			ServerHandler.sendDebugMessage("Could not read from the database.db file!");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return true;
	}
	
	public static boolean isWritable(final String world, final String playerString) {
		try {
			try {
				createTables("enabled-players");
				Statement stmt = SQLite.getDatabase("database").getSQLConnection().createStatement();
				try {
					ResultSet rset = stmt.executeQuery("SELECT * FROM enabled_players WHERE World_Name='" + world + "' AND Player_UUID='" + playerString + "';");
					try {
						if (!rset.isBeforeFirst()) {} else {
							if (rset.getString(4).equalsIgnoreCase("true")) {
								return true;
							} else { return false; }
						}
					} finally { rset.close(); }
				} finally { stmt.close(); }
			} finally { SQLite.getDatabase("database").closeConnection(); }
		} catch (Exception e) {
			ServerHandler.sendDebugMessage("Could not read from the database.db file!");
			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
		}
		return true;
	}

	public static void convertYAMLS() {
		
		File firstJoin = new File(ItemJoin.getInstance().getDataFolder(), "first-join.yml");
		File ipLimit = new File(ItemJoin.getInstance().getDataFolder(), "ip-limit.yml");
		boolean converting = false;
		if (firstJoin.exists() || ipLimit.exists()) {
			if (firstJoin.exists()) {
			ServerHandler.sendConsoleMessage("&aThe &cfirst-join.yml&a file is &coutdated&a, all data is now stored in a database file.");
			}
			if (ipLimit.exists()) {
				ServerHandler.sendConsoleMessage("&aThe &cip-limit.yml&a file is &coutdated&a, all data is now stored in a database file.");
			}
			ServerHandler.sendConsoleMessage("&aStarting YAML to Database conversion, stored data in the file(s) will not be lost...");
			converting = true;
		}
		if (firstJoin.exists()) {
			try {
				ConfigHandler.loadConfig("first-join.yml");
				createTables("first-join");
				for (String worldsec: ConfigHandler.getConfig("first-join.yml").getKeys(false)) {
					ConfigurationSection world = ConfigHandler.getConfig("first-join.yml").getConfigurationSection(worldsec);
					for (String itemsec: world.getKeys(false)) {
						ConfigurationSection item = world.getConfigurationSection(itemsec);
						for (String uuidsec: item.getKeys(false)) {
							ConfigurationSection uuid = item.getConfigurationSection(uuidsec);
							OfflinePlayer player = ItemJoin.getInstance().getServer().getOfflinePlayer(UUID.fromString(uuid.getName()));
							if (!isInDatabase("", "SELECT * FROM first_join WHERE World_Name='" + world.getName() + "' AND Player_UUID='" + uuid.getName() + "' AND Item_Name='" + item.getName() + "';")) {
								SQLite.getDatabase("database").executeStatement("INSERT INTO first_join (`World_Name`, `Player_Name`, `Player_UUID`, `Item_Name`) VALUES ('" + world.getName() + "','" + player.getName().toString() + "','" + uuid.getName() + "','" + item.getName() + "')");
								SQLite.getDatabase("database").closeConnection();
							} 
						}
					}
				}
		        File userfiles = new File(ItemJoin.getInstance().getDataFolder() + File.separator + "backup");
	            if(!userfiles.exists()){
	                userfiles.mkdirs();
	            }
				String newGen = "converted" + Utils.getRandom(0, 100) + "-first-join.yml";
				File newFile = new File(userfiles, newGen);
				firstJoin.renameTo(newFile);
			} catch (Exception e) { ServerHandler.sendDebugMessage("[ERROR] Failed to convert the first-join.yml to the database!");
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }}
			}
		
		if (ipLimit.exists()) {
			try {
				ConfigHandler.loadConfig("ip-limit.yml");
				createTables("ip-limit");
				for (String worldsec: ConfigHandler.getConfig("ip-limit.yml").getKeys(false)) {
					ConfigurationSection world = ConfigHandler.getConfig("ip-limit.yml").getConfigurationSection(worldsec);
					for (String itemsec: world.getKeys(false)) {
						ConfigurationSection item = world.getConfigurationSection(itemsec);
						for (String ipaddrsec: item.getKeys(false)) {
							ConfigurationSection ipaddr = item.getConfigurationSection(ipaddrsec);
							if (!isInDatabase("", "SELECT * FROM ip_limits WHERE World_Name='" + world.getName() + "' AND IP_Address='" + ipaddr.getName() + "' AND Item_Name='" + item.getName() + "';")) {
								SQLite.getDatabase("database").executeStatement("INSERT INTO ip_limits (`World_Name`, `IP_Address`, `Player_UUID`, `Item_Name`) VALUES ('" + world.getName() + "','" + ipaddr.getName() + "','" + ipaddr.get("Current User") + "','" + item.getName() + "')");
								SQLite.getDatabase("database").closeConnection();
							}
						}
					}
				}	
		        File userfiles = new File(ItemJoin.getInstance().getDataFolder() + File.separator + "backup");
	            if(!userfiles.exists()){
	                userfiles.mkdirs();
	            }
				String newGen = "converted" + Utils.getRandom(0, 100) + "-ip-limit.yml";
				File newFile = new File(userfiles, newGen);
				ipLimit.renameTo(newFile);
			} catch (Exception e) { ServerHandler.sendDebugMessage("[ERROR] Failed to convert the ip-limit.yml to the database!");
				if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }}
			}
		
		if (converting == true) {
			ServerHandler.sendConsoleMessage("&aYAML to Database conversion complete!");
		}
	}
	
}
