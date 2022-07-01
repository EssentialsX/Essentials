package com.earth2me.essentials;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.LoggerProvider;
import net.ess3.provider.providers.BaseLoggerProvider;
import net.ess3.provider.providers.PaperLoggerProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class EssentialsLogger {
    private final static Map<String, LoggerProvider> loggerProviders = new HashMap<>();
    private final static MethodHandle loggerFieldHandle;

    static {
        try {
            final Field loggerField = ReflUtil.getFieldCached(JavaPlugin.class, "logger");
            //noinspection ConstantConditions
            loggerFieldHandle = MethodHandles.lookup().unreflectSetter(loggerField);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to get logger field handle", t);
        }
    }

    private EssentialsLogger() {
    }

    public static LoggerProvider getLoggerProvider(final Plugin plugin) {
        if (loggerProviders.containsKey(plugin.getName())) {
            return loggerProviders.get(plugin.getName());
        }

        final Logger parentLogger = Logger.getLogger(plugin.getName());
        final LoggerProvider provider;
        if (ReflUtil.getClassCached("io.papermc.paper.adventure.providers.ComponentLoggerProviderImpl") != null) {
            provider = new PaperLoggerProvider(plugin);
            provider.setParent(parentLogger);
        } else {
            provider = new BaseLoggerProvider(plugin, parentLogger);
            provider.setParent(parentLogger);
        }
        loggerProviders.put(plugin.getName(), provider);
        return provider;
    }

    public static void updatePluginLogger(final Plugin plugin) {
        final LoggerProvider provider = getLoggerProvider(plugin);
        try {
            loggerFieldHandle.invoke(plugin, provider);
        } catch (Throwable e) {
            provider.log(Level.SEVERE, "Failed to update " + plugin.getName() + " logger", e);
        }
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
}
