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

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;


public class LogFilter extends AbstractFilter {
	
	private HashMap < String, ArrayList < String > > hiddenExecutors = new HashMap < String, ArrayList < String > > ();
	private static LogFilter filter;
	
   /**
    * Sets the Result of the filter,
    * attempts to prevent the hiddenExecutors from being chat logged.
    * 
    * @param message - The message being checked for hiddenExecutors.
    */
    private Result handle(final String message) {
        if (message == null) { return Result.NEUTRAL; }
        if (this.hiddenExecutors != null && !this.hiddenExecutors.isEmpty() && this.hiddenExecutors.containsKey("commands-list")) {
	        for (String word : this.hiddenExecutors.get("commands-list")) {
	            if (message.toLowerCase().contains(word.toLowerCase())) {
	                return Result.DENY;
	            }
	        }
        }
        return Result.NEUTRAL;
    }

   /**
    * Attempts to hide the hiddenExecutors from the chat logger.
    * 
    * @param event - The logger handling the message.
    */
    @Override
    public Result filter(final LogEvent event) {
        return this.handle(event.getMessage().getFormattedMessage());
    }

   /**
    * Attempts to hide the hiddenExecutors from the chat logger.
    * 
    * @param logger - The logger handling the message.
    * @param level - The level of execution.
    * @param marker - The filter marker.
    * @param msg - The message catched by the filter.
    * @param t - The cached Throwable.
    * @return The result of the filter.
    */
    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Message msg, final Throwable t) {
        return this.handle(msg.getFormattedMessage());
    }

   /**
    * Attempts to hide the hiddenExecutors from the chat logger.
    * 
    * @param logger - The logger handling the message.
    * @param level - The level of execution.
    * @param marker - The filter marker.
    * @param msg - The message catched by the filter.
    * @param t - The cached Throwable.
    * @return The result of the filter.
    */
    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Object msg, final Throwable t) {
        return this.handle(msg.toString());
    }

   /**
    * Attempts to hide the hiddenExecutors from the chat logger.
    * 
    * @param logger - The logger handling the message.
    * @param level - The level of execution.
    * @param marker - The filter marker.
    * @param msg - The message catched by the filter.
    * @param params - The filter parameters.
    * @return The result of the filter.
    */
    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String msg, final Object... params) {
        return this.handle(msg);
    }
    
   /**
    * Adds an executor to be hidden from chat logging.
    * 
    * @param log - The log identifier.
    * @param logList - The executor to be hidden.
    */
    public void addHidden(final String log, final ArrayList < String > logList) {
    	this.hiddenExecutors.put(log, logList);
    }
    
   /**
    * Gets the currently hiddenExecutors HashMap.
    * 
    * @return The current hiddenExecutors HashMap.
    */
    public HashMap<String, ArrayList<String>> getHidden() {
    	return this.hiddenExecutors;
    }
	
   /**
    * Gets the instance of the CustomFilter.
    * 
    * @param regen - If the CustomFilter should have a new instance created.
    * @return The CustomFilter instance.
    */
    public static LogFilter getFilter(final boolean regen) { 
        if (filter == null || regen) { 
        	// ((Logger) LogManager.getRootLogger()).addFilter(filter);
        	//String d = LogManager.ROOT_LOGGER_NAME;
            filter = new LogFilter();
            LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            LoggerConfig loggerConfig = ctx.getConfiguration().getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
            loggerConfig.addFilter(filter);
        }
        return filter; 
    } 
}