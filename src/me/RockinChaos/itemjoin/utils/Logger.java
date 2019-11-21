package me.RockinChaos.itemjoin.utils;

import org.bukkit.entity.Player;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class Logger {
	
	public void existsDebug(Player player, ItemMap itemMap) {
		ServerHandler.sendDebugMessage(player.getName() + " already has item; " + itemMap.getConfigName());
	}
	
	public void obtainDebug(Player player, ItemMap itemMap, boolean firstJoin, boolean firstWorld, boolean ipLimit) {
		if (firstJoin) { 
			ServerHandler.sendDebugMessage(player.getName() + " has already received first-join " + itemMap.getConfigName() + ", they can no longer recieve this."); 
		} else if (firstWorld) { 
			ServerHandler.sendDebugMessage(player.getName() + " has already received first-world " + itemMap.getConfigName() + ", they can no longer recieve this in " + player.getWorld().getName()); 
		} else if (ipLimit) { 
			ServerHandler.sendDebugMessage(player.getName() + " has already received ip-limited " + itemMap.getConfigName() + ", they will only recieve this on their dedicated ip.");  
		}
	}
	
	public void receiveDebug(Player player, ItemMap itemMap) {
		ServerHandler.sendDebugMessage(player.getName() + " has failed to receive item; " + itemMap.getConfigName());
	}
	
	public void givenDebug(ItemMap itemMap) {
		ServerHandler.sendDebugMessage("Given the Item; " + itemMap.getConfigName());
	}
	
	public void sqLiteMissing(Exception e) {
		ServerHandler.sendDebugMessage("[SQLITE] You need the SQLite JBDC library, see: &ahttps://bitbucket.org/xerial/sqlite-jdbc/downloads/ &rand put it in /lib folder.");
		ServerHandler.sendDebugTrace(e);
	}
	
	public void sqLiteException(Exception e) {
		ServerHandler.sendDebugMessage("[SQLITE] SQLite exception on initialize");
		ServerHandler.sendDebugTrace(e);
	}
	
	public void sqLiteError(Exception e, String dbname) {
		ServerHandler.sendDebugMessage("[SQLITE] File write error: " + dbname + ".db");
		ServerHandler.sendDebugTrace(e);
	}
	
	public void sqLitePurge(Exception e, String dbname) {
		ServerHandler.sendDebugMessage("[SQLITE] Failed to close purge database " + dbname + ".db");
		ServerHandler.sendDebugTrace(e);
	}
	
	public void sqLiteClose(Exception e, String dbname) {
		ServerHandler.sendDebugMessage("[SQLITE] Failed to close database " + dbname + ".db " + "connection.");
		ServerHandler.sendDebugTrace(e);
	}
	
	public void sqLiteSaving() {
		ServerHandler.sendDebugMessage("[SQLITE] Saving newly generated data to the database.");
	}
	
	public void sqLiteConverting(String type) {
		ServerHandler.sendErrorMessage("&aThe &c" + type + ".yml&a file is &coutdated&a, all data is now stored in a database file.");
	}
	
	public void sqLiteConversion() {
		ServerHandler.sendErrorMessage("&aStarting YAML to Database conversion, stored data in the file(s) will not be lost...");
	}
	
	public void sqLiteConvertFailed(Exception e, String type) {
		ServerHandler.sendDebugMessage("[ERROR] Failed to convert the type.yml to the database!");
		ServerHandler.sendDebugTrace(e);
	}
	
	public void sqLiteComplete() {
		ServerHandler.sendErrorMessage("&aYAML to Database conversion complete!");
	}
}
