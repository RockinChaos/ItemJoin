package me.RockinChaos.itemjoin;

import org.bukkit.plugin.java.JavaPlugin;

import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.handlers.UpdateHandler;

public class ItemJoin extends JavaPlugin {
  	private static ItemJoin instance;
  	
  	@Override
	public void onEnable() {
  		instance = this;
  		ConfigHandler.getConfigs();
  		ConfigHandler.setUpdater(new UpdateHandler(getFile()));
  		ConfigHandler.generateData();
  		ItemUtilities.updateItems();
  		ConfigHandler.registerEvents();
  		ServerHandler.sendConsoleMessage("&ahas been Enabled.");
  	}
  	
  	@Override
	public void onDisable() {
  		ConfigHandler.getSQLData().executeLaterStatements();
  		ServerHandler.sendConsoleMessage("&4has been Disabled.");
  	}
  	
  	public static ItemJoin getInstance() {
  		return instance;
  	}
}