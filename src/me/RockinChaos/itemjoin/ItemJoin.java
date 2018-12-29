package me.RockinChaos.itemjoin;

import org.bukkit.plugin.java.JavaPlugin;

import me.RockinChaos.itemjoin.giveitems.utils.ItemDesigner;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.DataStorage;
import me.RockinChaos.itemjoin.utils.Updater;


public class ItemJoin extends JavaPlugin {
  	private static ItemJoin instance;
  	
  	public void onEnable() {
  		instance = this;
  		ConfigHandler.loadConfigs();
  		DataStorage.generateData();
  		DataStorage.registerEvents();
  		(new ItemDesigner()).generateItems();
  		ItemUtilities.updateItems();
  		ServerHandler.sendConsoleMessage("&ahas been Enabled!");
  		Updater.setAbsoluteFile(getFile());
  		Updater.checkUpdates(getInstance().getServer().getConsoleSender());
  	}
  	
  	public void onDisable() {
  		ServerHandler.sendConsoleMessage("&4has been Disabled!");
  	}
  	
  	public static ItemJoin getInstance() {
  		return instance;
  	}
}