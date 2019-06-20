package net.ess3.api;

import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.UUID;

/**
 * <p>PluginKey class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public final class PluginKey {
    private final Plugin plugin;
    private final String key;

    private PluginKey(Plugin plugin, String key) {
        this.plugin = plugin;
        this.key = key;
    }

    /**
     * <p>Getter for the field <code>plugin</code>.</p>
     *
     * @return a {@link org.bukkit.plugin.Plugin} object.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * <p>Getter for the field <code>key</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getKey() {
        return key;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(plugin, key);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return plugin.getName().toLowerCase() + ":" + key;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PluginKey || o.getClass().getName().equals("org.bukkit.NamespacedKey"))) {
            return false;
        }
        return this == o || this.toString().equals(o.toString());
    }

    /**
     * <p>random.</p>
     *
     * @param plugin a {@link org.bukkit.plugin.Plugin} object.
     * @return a {@link net.ess3.api.PluginKey} object.
     */
    public static PluginKey random(Plugin plugin) {
        return new PluginKey(plugin, UUID.randomUUID().toString());
    }

    /**
     * <p>fromKey.</p>
     *
     * @param plugin a {@link org.bukkit.plugin.Plugin} object.
     * @param key a {@link java.lang.String} object.
     * @return a {@link net.ess3.api.PluginKey} object.
     */
    public static PluginKey fromKey(Plugin plugin, String key) {
        return new PluginKey(plugin, key);
    }

}
