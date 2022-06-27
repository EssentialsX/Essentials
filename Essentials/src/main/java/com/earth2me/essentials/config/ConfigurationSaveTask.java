package com.earth2me.essentials.config;

import com.earth2me.essentials.Essentials;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class ConfigurationSaveTask implements Runnable {
    private final YamlConfigurationLoader loader;
    private final CommentedConfigurationNode node;
    private final AtomicInteger pendingWrites;

    public ConfigurationSaveTask(final YamlConfigurationLoader loader, final CommentedConfigurationNode node, final AtomicInteger pendingWrites) {
        this.loader = loader;
        this.node = node;
        this.pendingWrites = pendingWrites;
    }

    @Override
    public void run() {
        synchronized (loader) {
            // Check if there are more writes in queue.
            // If that's the case, we shouldn't bother writing data which is already out-of-date.
            if (pendingWrites.get() > 1) {
                pendingWrites.decrementAndGet();
            }

            try {
                loader.save(node);
            } catch (ConfigurateException e) {
                Essentials.getWrappedLogger().log(Level.SEVERE, e.getMessage(), e);
            } finally {
                pendingWrites.decrementAndGet();
            }
        }
    }
}
