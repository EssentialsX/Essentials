package net.ess3.provider;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class LoggerProvider extends Logger {
    public LoggerProvider(final String name) {
        super(name, null);
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
