package com.earth2me.essentials.config;

import com.earth2me.essentials.Essentials;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.logging.Level;

public class ConfigurationSaveTask implements Runnable {
    private final YamlConfigurationLoader loader;
    private final Supplier<CommentedConfigurationNode> nodeSupplier;
    private final AtomicInteger pendingWrites;

    public ConfigurationSaveTask(final YamlConfigurationLoader loader, final Supplier<CommentedConfigurationNode> nodeSupplier, final AtomicInteger pendingWrites) {
        this.loader = loader;
        this.nodeSupplier = nodeSupplier;
        this.pendingWrites = pendingWrites;
    }

    @Override
    public void run() {
        if (pendingWrites.decrementAndGet() > 0) {
            return;
        }

        synchronized (loader) {
            try {
                final CommentedConfigurationNode node = nodeSupplier.get();
                loader.save(node);
            } catch (ConfigurateException e) {
                Essentials.getWrappedLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}
