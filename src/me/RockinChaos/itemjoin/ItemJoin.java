/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.UpdateHandler;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.interfaces.pages.InterMenu;
import me.RockinChaos.itemjoin.utils.protocol.ProtocolManager;
import me.RockinChaos.itemjoin.utils.sql.Database;

public class ItemJoin extends JavaPlugin {
	
  	private static ItemJoin instance;
  	private boolean isStarted = false;
  	
   /**
    * Called when the plugin is loaded.
    * 
    */
    @Override
    public void onLoad() {
    	instance = this;
    }
    
   /**
    * Called when the plugin is enabled.
    * 
    */
  	@Override
	public void onEnable() {
        ConfigHandler.getConfig().registerEvents();
        SchedulerUtils.runAsync(() -> {
        	UpdateHandler.getUpdater(true); {
        		ServerUtils.logDebug("has been Enabled.");
        	}
        });
  	}
  	
   /**
    * Called when the plugin is disabled.
    * 
    */
  	@Override
	public void onDisable() {
  		Bukkit.getScheduler().cancelTasks(this);
  		InterMenu.closeMenu();
  		ItemHandler.saveCooldowns();
  		ItemHandler.purgeCraftItems(true);
	  	Database.getDatabase().closeConnection(true);
	  	ProtocolManager.closeProtocol();
	  	ItemUtilities.getUtilities().clearItems();
  		ServerUtils.logDebug("has been Disabled.");
  	}
  	
   /**
	* Checks if the plugin has fully loaded.
	* 
	*/
	public boolean isStarted() {
		return this.isStarted;
	}
	
   /**
	* Sets the plugin as fully loaded.
	* 
	*/
	public void setStarted(final boolean bool) {
		this.isStarted = bool;
	}
  	
   /**
    * Gets the Plugin File.
    * 
    * @return The Plugin File.
    */
  	public File getPlugin() {
  		return this.getFile();
  	}

   /**
    * Gets the static instance of the main class for ItemJoin. 
    * Notice: This class is not the actual API class, this is the main class that extends JavaPlugin. 
    * For API methods, use the static methods available from the class: {@link ItemJoinAPI}.
    *
    * @return ItemJoin instance.
    */  	
  	public static ItemJoin getInstance() {
  		return instance;
  	}
}