package com.earth2me.essentials;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.LoggerProvider;
import net.ess3.provider.providers.BaseLoggerProvider;
import net.ess3.provider.providers.PaperLoggerProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EssentialsLogger {
    private static LoggerProvider loggerProvider;
    private final static Map<String, LoggerProvider> loggerProviders = new HashMap<>();

    private EssentialsLogger() {
    }

    public static LoggerProvider getLoggerProvider(final Plugin plugin) {
        if (loggerProviders.containsKey(plugin.getName())) {
            return loggerProviders.get(plugin.getName());
        }

        final LoggerProvider provider;
        if (ReflUtil.getClassCached("io.papermc.paper.adventure.providers.ComponentLoggerProviderImpl") != null) {
            provider = new PaperLoggerProvider(plugin);
        } else {
            provider = new BaseLoggerProvider(Logger.getLogger(plugin.getName()));
        }
        loggerProviders.put(plugin.getName(), provider);
        return provider;
    }

    public static LoggerProvider getLoggerProvider(final String pluginName) {
        if (loggerProviders.containsKey(pluginName)) {
            return loggerProviders.get(pluginName);
        }

        final Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin not found: " + pluginName);
        }
        return getLoggerProvider(plugin);
    }

    public static void setLoggerProvider(LoggerProvider loggerProvider) {
        EssentialsLogger.loggerProvider = loggerProvider;
    }

    public static void log(Level level, String message, Throwable throwable) {
        loggerProvider.log(level, message, throwable);
    }

    public static void log(Level level, String message) {
        log(level, message, null);
    }

    public static void info(final String message) {
        log(Level.INFO, message);
    }

    public static void warning(final String message) {
        log(Level.WARNING, message);
    }

    public static void severe(final String message) {
        log(Level.SEVERE, message);
    }
}
