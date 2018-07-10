package me.RockinChaos.itemjoin;

import org.bukkit.plugin.java.JavaPlugin;
import me.RockinChaos.itemjoin.cacheitems.CreateItems;
import me.RockinChaos.itemjoin.handlers.AnimationHandler;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Hooks;
import me.RockinChaos.itemjoin.utils.Updater;


  public class ItemJoin
    extends JavaPlugin
  {

    private static ItemJoin instance;
    
    public void onEnable()
    {
    	  instance = this;
    	  Updater.setAbsoluteFile(getFile());
		  ConfigHandler.loadConfigs();
		  Hooks.getHooks();
		  Hooks.registerEvents();		  
		  CreateItems.setRun();
		  Updater.checkUpdates(getInstance().getServer().getConsoleSender());
		  ServerHandler.sendConsoleMessage("&ahas been Enabled!");
        }

	public void onDisable()
    {
		AnimationHandler.CloseAllAnimations();
    	ServerHandler.sendConsoleMessage("&4Disabled!");
      }
    
    public static ItemJoin getInstance() {
    	return instance;
    }
    
}
