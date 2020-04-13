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
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;


public class CustomFilter extends AbstractFilter {
	
	private HashMap < String, ArrayList < String > > hideLogging = new HashMap < String, ArrayList < String > > ();
	
    private Result handle(String message) {
        if(message == null) {
            return Result.NEUTRAL;
        }

        for(String word : hideLogging.get("commands-list")) {
            if(message.toLowerCase().contains(word.toLowerCase())) {
                return Result.DENY;
            }
        }
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(LogEvent event) {
        return handle(event.getMessage().getFormattedMessage());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return handle(msg.getFormattedMessage());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return handle(msg.toString());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return handle(msg);
    }
    
    public void setHideLogging(String log, ArrayList < String > logList) {
    	hideLogging.put(log, logList);
    }
    
    public HashMap<String, ArrayList<String>> getHideLogging() {
    	return hideLogging;
    }
}