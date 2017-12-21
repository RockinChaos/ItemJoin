package me.RockinChaos.itemjoin.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import me.RockinChaos.itemjoin.handlers.CommandHandler;

public class CustomFilter extends AbstractFilter {
	
    private Result handle(String message) {
        if(message == null) {
            return Result.NEUTRAL;
        }

        for(String word : CommandHandler.filteredCommands.get("commands-list")) {
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