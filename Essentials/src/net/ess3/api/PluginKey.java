package net.ess3.api;

import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.UUID;

/**
 * A namespaced key that uses plugins as namespaces.
 */
public final class PluginKey {
    private final Plugin plugin;
    private final String key;

    private PluginKey(final Plugin plugin, final String key) {
        this.plugin = plugin;
        this.key = key;
    }

    /**
     * Create a randomly-generated plugin key under the given plugin's namespace.
     * <p>
     * Note: Plugins should prefer to create keys with predictable names - see {@link PluginKey#fromKey(Plugin, String)}.
     *
     * @param plugin The plugin whose namespace to use
     * @return A random key under the given plugin's namespace
     */
    public static PluginKey random(final Plugin plugin) {
        return new PluginKey(plugin, UUID.randomUUID().toString());
    }

    /**
     * Create a plugin key under the given plugin's namespace with the given name.
     *
     * @param plugin The plugin whose namespace to use
     * @param key    The name of the key to create
     * @return The key under the given plugin's namespace.
     */
    public static PluginKey fromKey(final Plugin plugin, final String key) {
        return new PluginKey(plugin, key);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugin, key);
    }

    @Override
    public String toString() {
        return plugin.getName().toLowerCase() + ":" + key;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PluginKey || o.getClass().getName().equals("org.bukkit.NamespacedKey"))) {
            return false;
        }
        return this == o || this.toString().equals(o.toString());
    }

}
