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
package me.RockinChaos.itemjoin.utils.api;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;

/**
 * Welcome to the magical land of make-believe.
 * These are Deprecated Legacy Methods and/or non-functioning methods
 * that exist to support legacy versions of Minecraft.
 * 
 */
@SuppressWarnings("deprecation")
public class LegacyAPI {
    
   /**
    * Registers the Legacy Pickups Listener.
    * Only called when the Server version is below 1.12.
    * 
    */
	public static void registerPickups() {
		if (!StringUtils.isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Pickups.class.getSimpleName())) { 
			ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Pickups(), ItemJoin.getCore().getPlugin()); 
		}
	}
	
   /**
    * Registers the Legacy Stackable Listener.
    * Only called when the Server version is below 1.12.
    * 
    */
	public static void registerStackable() {
		if (!ServerUtils.hasSpecificUpdate("1_12") && !StringUtils.isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Stackable.class.getSimpleName())) { 
			ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Stackable(), ItemJoin.getCore().getPlugin()); 
		}
	}
	
   /**
    * Registers the Legacy Interact Listener.
    * Only called when the Server version is below 1.8.
    * 
    */
	public static void registerCommands() {
		if (!ServerUtils.hasSpecificUpdate("1_8") && !StringUtils.isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Commands.class.getSimpleName())) {
			ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Commands(), ItemJoin.getCore().getPlugin());
		}
	}
	
   /**
    * Registers the Legacy Consumes Listener.
    * Only called when the Server version is below 1.11.
    * 
    */
	public static void registerConsumes() {
		if (!ServerUtils.hasSpecificUpdate("1_11") && !StringUtils.isRegistered( me.RockinChaos.itemjoin.listeners.legacy.Legacy_Consumes.class.getSimpleName())) {
			ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new  me.RockinChaos.itemjoin.listeners.legacy.Legacy_Consumes(), ItemJoin.getCore().getPlugin());
		}
	}
	
   /**
    * Registers the Legacy Storable Listener.
    * Only called when the Server version is below 1.8.
    * 
    */
	public static void registerStorable() {
		if (!ServerUtils.hasSpecificUpdate("1_8") && !StringUtils.isRegistered(me.RockinChaos.itemjoin.listeners.legacy.Legacy_Storable.class.getSimpleName())) {
			ItemJoin.getCore().getPlugin().getServer().getPluginManager().registerEvents(new me.RockinChaos.itemjoin.listeners.legacy.Legacy_Storable(), ItemJoin.getCore().getPlugin());
		}
	}
}