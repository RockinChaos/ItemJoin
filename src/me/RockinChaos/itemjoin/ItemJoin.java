package me.RockinChaos.itemjoin;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

    @SuppressWarnings("unchecked")
	public void onDisable()
    {
    		Collection < ? extends Player > playersOnlineNew;
    		Player[] playersOnlineOld;
    		try {
    			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    				playersOnlineNew = (Collection < ? extends Player > )((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    				for (Player player: playersOnlineNew) {
    					AnimationHandler.CloseAnimations(player);
    				}
    			} else {
    				playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    				for (Player player: playersOnlineOld) {
    					AnimationHandler.CloseAnimations(player);
    				}
    			}
    		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
    			if (ServerHandler.hasDebuggingMode()) { e.printStackTrace(); }
    		}
    	ServerHandler.sendConsoleMessage("&4Disabled!");
      }
    
    public static ItemJoin getInstance() {
    	return instance;
    }
    
}
