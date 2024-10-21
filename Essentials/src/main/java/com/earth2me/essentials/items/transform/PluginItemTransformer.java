package com.earth2me.essentials.items.transform;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class PluginItemTransformer {
    private final Plugin plugin;

    public PluginItemTransformer(Plugin thePlugin){
        if(thePlugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");

        this.plugin = thePlugin;
    }

    public abstract ItemStack apply(String data, ItemStack original);

    public Plugin getPlugin() {
        return plugin;
    }
}
