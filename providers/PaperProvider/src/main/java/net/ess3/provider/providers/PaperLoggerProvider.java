package net.ess3.provider.providers;

import net.ess3.provider.LoggerProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class PaperLoggerProvider extends LoggerProvider {
    private final ComponentLogger logger;

    public PaperLoggerProvider(final Plugin plugin) {
        super(plugin);
        this.logger = plugin.getComponentLogger();
    }

    @Override
    protected void doTheLog(Level level, String message, Throwable throwable) {
        if (message == null) {
            return;
        }
        final Component component = LegacyComponentSerializer.legacySection().deserialize(message);
        if (level == Level.SEVERE) {
            logger.error(component, throwable);
        } else if (level == Level.WARNING) {
            logger.warn(component, throwable);
        } else if (level == Level.INFO) {
            logger.info(component, throwable);
        } else if (level == Level.FINE) {
            logger.trace(component, throwable);
        } else {
            throw new IllegalArgumentException("Unknown level: " + level);
        }
    }

    @Override
    protected void doTheLog(Level level, String message) {
        if (message == null) {
            return;
        }
        final Component component = LegacyComponentSerializer.legacySection().deserialize(message);
        if (level == Level.SEVERE) {
            logger.error(component);
        } else if (level == Level.WARNING) {
            logger.warn(component);
        } else if (level == Level.INFO) {
            logger.info(component);
        } else if (level == Level.FINE) {
            logger.trace(component);
        } else {
            throw new IllegalArgumentException("Unknown level: " + level);
        }
    }
}
