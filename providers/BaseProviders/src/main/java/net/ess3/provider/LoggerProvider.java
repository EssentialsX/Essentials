package net.ess3.provider;

import java.util.logging.Level;

public interface LoggerProvider {
    void log(Level level, String message, Throwable throwable);

    void log(Level level, String message);

    default void warning(String message) {
        log(Level.WARNING, message);
    }

    default void info(String message) {
        log(Level.INFO, message);
    }

    default void severe(String message) {
        log(Level.SEVERE, message);
    }
}
