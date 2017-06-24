package me.RockinChaos.itemjoin;

import org.bukkit.plugin.java.JavaPlugin;

import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Updater;


  public class ItemJoin
    extends JavaPlugin
  {

    public static ItemJoin pl;
    
    public void onEnable()
    {
    	  pl = this;
    	  Updater.AbsoluteFile = getFile();
		  ConfigHandler.loadConfigs();
		  Hooks.getHooks();
		  Hooks.getRegisters();
		  ConfigHandler.secretMsg();
		  CreateItems.setRun();
		  Updater.checkUpdates(Updater.Console);
		  ServerHandler.sendConsoleMessage("&ahas been Enabled!");
        }

    public void onDisable()
    {
    	ServerHandler.sendConsoleMessage("&4Disabled!");
      }
}
