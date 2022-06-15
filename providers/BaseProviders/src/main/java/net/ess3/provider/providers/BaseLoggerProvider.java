package net.ess3.provider.providers;

import net.ess3.provider.LoggerProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseLoggerProvider extends LoggerProvider {
    private final Logger logger;

    public BaseLoggerProvider(final Logger logger) {
        super(logger.getName());
        this.logger = logger;
    }

    @Override
    protected void doTheLog(Level level, String message, Throwable throwable) {
        logger.log(level, message, throwable);
    }

    @Override
    protected void doTheLog(Level level, String message) {
        logger.log(level, message);
    }
}
