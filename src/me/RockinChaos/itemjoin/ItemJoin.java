package me.RockinChaos.itemjoin;

import org.bukkit.plugin.java.JavaPlugin;

import me.RockinChaos.itemjoin.giveitems.utils.ItemDesigner;
import me.RockinChaos.itemjoin.giveitems.utils.ObtainItem;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Updater;


public class ItemJoin extends JavaPlugin {
	
  	private static ItemJoin instance;
  	
  	public void onEnable() {
  		instance = this;
  		ConfigHandler.loadConfigs();
  		Hooks.getHooks();
  		Hooks.registerEvents();
  		ItemDesigner itemDesigner = new ItemDesigner();
  		itemDesigner.generateItems();
  		ObtainItem.updateItems();
  		ServerHandler.sendConsoleMessage("&ahas been Enabled!");
  		Updater.setAbsoluteFile(getFile());
  		Updater.checkUpdates(getInstance().getServer().getConsoleSender());
  	}
  	
  	public void onDisable() {
  		//AnimationHandler.CloseAllAnimations();
  		ServerHandler.sendConsoleMessage("&4has been Disabled!");
  	}
  	
  	public static ItemJoin getInstance() {
  		return instance;
  	}
  	
}