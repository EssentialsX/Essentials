package net.ess3.provider.providers;

import net.ess3.provider.SyncCommandsProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@ProviderData(description = "1.21.4+ Sync Commands Provider", weight = 1)
public class ModernSyncCommandsProvider implements SyncCommandsProvider {
    @Override
    public void syncCommands() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.updateCommands();
        }
    }

    @ProviderTest
    public static boolean test() {
        try {
            // There isn't a real good way to test this, but we can check if the Creaking class exists.
            Class.forName("org.bukkit.entity.Creaking");
            return true;
        } catch (final Throwable ignored) {
            return false;
        }
    }
}
