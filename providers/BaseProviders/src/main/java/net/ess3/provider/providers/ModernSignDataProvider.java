package net.ess3.provider.providers;

import net.ess3.provider.SignDataProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

@ProviderData(description = "1.14+ Sign Data Provider")
public class ModernSignDataProvider implements SignDataProvider {
    private final Plugin plugin;

    public ModernSignDataProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setSignData(Sign sign, String key, String value) {
        if (sign == null || key == null || value == null) {
            return;
        }

        sign.getPersistentDataContainer().set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
        sign.update();
    }

    @Override
    public String getSignData(Sign sign, String key) {
        if (sign == null || key == null) {
            return null;
        }

        try {
            return sign.getPersistentDataContainer().get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @ProviderTest
    public static boolean test() {
        try {
            Class.forName("org.bukkit.block.TileState");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
