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
	
	public static HashMap < String, ArrayList < String > > clearLoggables = new HashMap < String, ArrayList < String > > ();
	
    private Result handle(String message) {
        if(message == null) {
            return Result.NEUTRAL;
        }

        for(String word : clearLoggables.get("commands-list")) {
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
}