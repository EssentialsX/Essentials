package net.ess3.provider;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public abstract class LoggerProvider extends PluginLogger {
    public LoggerProvider(final Plugin plugin) {
        super(plugin);
    }

    protected abstract void doTheLog(Level level, String message, Throwable throwable);

    protected abstract void doTheLog(Level level, String message);

    @Override
    public void log(Level level, String msg) {
        doTheLog(level, msg);
    }

    @Override
    public void log(Level level, String msg, Throwable thrown) {
        doTheLog(level, msg, thrown);
    }

    @Override
    public void log(LogRecord logRecord) {
        doTheLog(logRecord.getLevel(), logRecord.getMessage(), logRecord.getThrown());
    }

    @Override
    public void warning(String message) {
        doTheLog(Level.WARNING, message);
    }

    @Override
    public void info(String message) {
        doTheLog(Level.INFO, message);
    }

    @Override
    public void severe(String message) {
        doTheLog(Level.SEVERE, message);
    }
}
