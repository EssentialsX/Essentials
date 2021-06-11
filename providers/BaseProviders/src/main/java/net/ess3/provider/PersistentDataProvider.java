package net.ess3.provider;

import org.bukkit.inventory.ItemStack;

public interface PersistentDataProvider extends Provider {
    void set(ItemStack itemStack, String key, String value);

    String getString(ItemStack itemStack, String key);

    void remove(ItemStack itemStack, String key);
}
