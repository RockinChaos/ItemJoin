package me.RockinChaos.itemjoin;

import org.bukkit.plugin.java.JavaPlugin;

import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.MemoryHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.handlers.UpdateHandler;


public class ItemJoin extends JavaPlugin {
  	private static ItemJoin instance;
  	
  	public void onEnable() {
  		instance = this;
  		ConfigHandler.loadConfigs();
  		MemoryHandler.setUpdater(new UpdateHandler(getFile()));
  		MemoryHandler.generateData();
  		ItemUtilities.updateItems();
  		MemoryHandler.registerEvents();
  		ServerHandler.sendConsoleMessage("&ahas been Enabled.");
  	}
  	
  	public void onDisable() {
  		MemoryHandler.getSQLData().executeLaterStatements();
  		ServerHandler.sendConsoleMessage("&4has been Disabled.");
  	}
  	
  	public static ItemJoin getInstance() {
  		return instance;
  	}
}