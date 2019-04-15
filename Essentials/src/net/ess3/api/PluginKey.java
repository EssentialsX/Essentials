package net.ess3.api;

import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.UUID;

public final class PluginKey {
    private final Plugin plugin;
    private final String key;

    private PluginKey(Plugin plugin, String key) {
        this.plugin = plugin;
        this.key = key;
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
    public boolean equals(Object o) {
        if (!(o instanceof PluginKey || o.getClass().getName().equals("org.bukkit.NamespacedKey"))) {
            return false;
        }
        return this == o || this.toString().equals(o.toString());
    }

    public static PluginKey random(Plugin plugin) {
        return new PluginKey(plugin, UUID.randomUUID().toString());
    }

    public static PluginKey fromKey(Plugin plugin, String key) {
        return new PluginKey(plugin, key);
    }

}
