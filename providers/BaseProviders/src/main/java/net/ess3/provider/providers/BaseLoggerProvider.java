package net.ess3.provider.providers;

import net.ess3.provider.LoggerProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseLoggerProvider implements LoggerProvider {
    private final Logger logger;

    public BaseLoggerProvider(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(Level level, String message, Throwable throwable) {
        logger.log(level, message, throwable);
    }

    @Override
    public void log(Level level, String message) {
        logger.log(level, message);
    }
}
