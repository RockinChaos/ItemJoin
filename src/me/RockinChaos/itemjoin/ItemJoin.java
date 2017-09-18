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

    private static ItemJoin pl;
    
    public void onEnable()
    {
    	  pl = this;
    	  Updater.setAbsoluteFile(getFile());
		  ConfigHandler.loadConfigs();
		  Hooks.getHooks();
		  Hooks.getRegisters();
		  
		  //ConfigHandler.secretMsg(); // Currently is no longer needed as you cannot disable the internal tags.
		  
		  CreateItems.setRun();
		  Updater.checkUpdates(getInstance().getServer().getConsoleSender());
		  ServerHandler.sendConsoleMessage("&ahas been Enabled!");
        }

    public void onDisable()
    {
    	ServerHandler.sendConsoleMessage("&4Disabled!");
      }
    
    public static ItemJoin getInstance() {
    	return pl;
    }
    
}
