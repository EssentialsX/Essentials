package net.ess3.provider.providers;

import net.ess3.provider.PersistentDataProvider;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("ConstantConditions")
public class ModernPersistentDataProvider implements PersistentDataProvider {
    private final Plugin plugin;

    public ModernPersistentDataProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void set(ItemStack itemStack, String key, String value) {
        if (itemStack == null || itemStack.getItemMeta() == null || key == null || value == null) {
            return;
        }
        final ItemMeta im = itemStack.getItemMeta();

        im.getPersistentDataContainer().set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
        itemStack.setItemMeta(im);
    }

    @Override
    public String getString(ItemStack itemStack, String key) {
        if (itemStack == null || itemStack.getItemMeta() == null || key == null) {
            return null;
        }

        try {
            return itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public void remove(ItemStack itemStack, String key) {
        itemStack.getItemMeta().getPersistentDataContainer().remove(new NamespacedKey(plugin, key));
    }

    @Override
    public String getDescription() {
        return "1.14+ Persistent Data Container Provider";
    }
}
