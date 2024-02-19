package net.ess3.provider.providers;

import net.ess3.provider.ServerStateProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Bukkit;

@ProviderData(description = "Paper Server State Provider", weight = 1)
public class PaperServerStateProvider implements ServerStateProvider {
    @Override
    public boolean isStopping() {
        return Bukkit.isStopping();
    }

    @ProviderTest
    public static boolean test() {
        try {
            Bukkit.class.getDeclaredMethod("isStopping");
            return true;
        } catch (final NoSuchMethodException ignored) {
            return false;
        }
    }
}
