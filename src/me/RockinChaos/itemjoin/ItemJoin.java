package me.RockinChaos.itemjoin;

import org.bukkit.plugin.java.JavaPlugin;

import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.handlers.UpdateHandler;
import me.RockinChaos.itemjoin.utils.DataStorage;


public class ItemJoin extends JavaPlugin {
  	private static ItemJoin instance;
  	
  	public void onEnable() {
  		instance = this;
  		ServerHandler.sendConsoleMessage("Warming up the oven!");
  		ConfigHandler.loadConfigs();
  		DataStorage.setUpdater(new UpdateHandler(getFile()));
  		DataStorage.generateData();
  		DataStorage.registerEvents();
  		ItemUtilities.updateItems();
  		ServerHandler.sendConsoleMessage("&ahas been Enabled.");
  	}
  	
  	public void onDisable() {
  		DataStorage.getSQLData().executeLaterStatements();
  		ServerHandler.sendConsoleMessage("&4has been Disabled.");
  	}
  	
  	public static ItemJoin getInstance() {
  		return instance;
  	}
}