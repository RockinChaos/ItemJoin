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
package me.RockinChaos.itemjoin.utils;

import org.bukkit.Bukkit;

import me.RockinChaos.itemjoin.ItemJoin;

public class SchedulerUtils {
	
	private static SchedulerUtils scheduler;

   /**
    * Runs the task on the main thread
    * @param runnable - The task to be performed.
    */
    public void run(final Runnable runnable){
    	if (ItemJoin.getInstance().isEnabled()) {
    		Bukkit.getScheduler().runTask(ItemJoin.getInstance(), runnable);
    	}
    }
	
   /**
    * Runs the task on the main thread.
    * @param runnable - The task to be performed.
    * @param delay - The ticks to wait before performing the task.
    */
    public void runLater(final long delay, final Runnable runnable) {
    	if (ItemJoin.getInstance().isEnabled()) {
    		Bukkit.getScheduler().runTaskLater(ItemJoin.getInstance(), runnable, delay);
    	}
    }
    
   /**
    * Runs the task repeating on the main thread.
    * @param runnable - The task to be performed.
    * @param delay - The ticks to wait before performing the task.
    * @param interval - The interval in which to run the task.
    * @return The repeating task identifier.
    */
    public int runAtInterval(final long delay, final long interval, final Runnable runnable) {
    	if (ItemJoin.getInstance().isEnabled()) {
    		return Bukkit.getScheduler().scheduleSyncRepeatingTask(ItemJoin.getInstance(), runnable, interval, delay);
    	}
    	return 0;
    }
    
   /**
    * Runs the task on another thread.
    * @param runnable - The task to be performed.
    */
    public void runAsync(final Runnable runnable) {
    	if (ItemJoin.getInstance().isEnabled()) {
    		Bukkit.getScheduler().runTaskAsynchronously(ItemJoin.getInstance(), runnable);
    	}
    }

   /**
    * Runs the task on another thread.
    * @param runnable - The task to be performed.
    * @param delay - The ticks to wait before performing the task.
    */
    public void runAsyncLater(final long delay, final Runnable runnable) {
    	if (ItemJoin.getInstance().isEnabled()) {
    		Bukkit.getScheduler().runTaskLaterAsynchronously(ItemJoin.getInstance(), runnable, delay);
    	}
    }
    
   /**
    * Runs the task timer on the another thread.
    * @param runnable - The task to be performed.
    * @param delay - The ticks to wait before performing the task.
    * @param interval - The interval in which to run the task.
    */
    public void runAsyncTimer(final long delay, final long interval, final Runnable runnable) {
    	if (ItemJoin.getInstance().isEnabled()) {
    		Bukkit.getScheduler().runTaskTimerAsynchronously(ItemJoin.getInstance(), runnable, interval, delay);
    	}
    }
    
   /**
    * Gets the instance of the SchedulerUtils.
    * 
    * @return The SchedulerUtils instance.
    */
    public static SchedulerUtils getScheduler() { 
        if (scheduler == null) { scheduler = new SchedulerUtils(); }
        return scheduler; 
    } 
}