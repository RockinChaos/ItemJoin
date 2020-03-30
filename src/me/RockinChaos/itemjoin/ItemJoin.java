package me.RockinChaos.itemjoin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class ItemJoin extends JavaPlugin {
  	private static ItemJoin instance;
  	
  	@Override
	public void onEnable() {
  		instance = this;
  		ConfigHandler.generateData(getFile());
        ConfigHandler.registerEvents();
        ServerHandler.sendConsoleMessage("&ahas been Enabled.");
  	}
  	
  	@Override
	public void onDisable() {
  		ServerHandler.purgeCraftItems(true);
  		Bukkit.getScheduler().cancelTasks(this);
  		ConfigHandler.getProtocolManager().closeProtocol();
  		ConfigHandler.getSQLData().executeLaterStatements();
  		ServerHandler.sendConsoleMessage("&4has been Disabled.");
  	}
  	
  	public static ItemJoin getInstance() {
  		return instance;
  	}
}