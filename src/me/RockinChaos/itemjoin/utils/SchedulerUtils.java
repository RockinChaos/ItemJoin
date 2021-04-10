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
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;

public class SchedulerUtils {
	
	public static boolean SINGLE_THREAD_TRANSACTING = false;

   /**
    * Runs the task on the main thread
    * @param runnable - The task to be performed.
    */
    public static void run(final Runnable runnable){
    	if (ItemJoin.getInstance().isEnabled()) {
    		Bukkit.getScheduler().runTask(ItemJoin.getInstance(), runnable);
    	}
    }
	
   /**
    * Runs the task on the main thread.
    * @param runnable - The task to be performed.
    * @param delay - The ticks to wait before performing the task.
    */
    public static void runLater(final long delay, final Runnable runnable) {
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
    public static int runAsyncAtInterval(final long delay, final long interval, final Runnable runnable) {
    	if (ItemJoin.getInstance().isEnabled()) {
    		return Bukkit.getScheduler().runTaskTimerAsynchronously(ItemJoin.getInstance(), runnable, interval, delay).getTaskId();
    	}
    	return 0;
    }
    
   /**
    * Runs the task on another thread.
    * @param runnable - The task to be performed.
    */
    public static void runAsync(final Runnable runnable) {
    	if (ItemJoin.getInstance().isEnabled()) {
    		Bukkit.getScheduler().runTaskAsynchronously(ItemJoin.getInstance(), runnable);
    	}
    }

   /**
    * Runs the task on another thread.
    * @param runnable - The task to be performed.
    * @param delay - The ticks to wait before performing the task.
    */
    public static void runAsyncLater(final long delay, final Runnable runnable) {
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
    public static void runAsyncTimer(final long delay, final long interval, final Runnable runnable) {
    	if (ItemJoin.getInstance().isEnabled()) {
    		Bukkit.getScheduler().runTaskTimerAsynchronously(ItemJoin.getInstance(), runnable, interval, delay);
    	}
    }
    
   /**
    * Runs the task on another thread without duplication.
    * @param runnable - The task to be performed.
    */
    public static void runSingleAsync(final Runnable runnable) {
    	if (ItemJoin.getInstance().isEnabled()) {
    		if (!SINGLE_THREAD_TRANSACTING) {
    			SINGLE_THREAD_TRANSACTING = true;
    			new BukkitRunnable() {
    				@Override
    				public void run() {
    					runnable.run(); {
    						SINGLE_THREAD_TRANSACTING = false;
    					}
    				}
    			}.runTaskAsynchronously(ItemJoin.getInstance());
    		} else {
    			try {
    				Thread.sleep(500);
    				runSingleAsync(runnable);
    			} catch (InterruptedException e) {
    				runSingleAsync(runnable);
    			}
    		}
    	}
    }
}