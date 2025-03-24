package net.ess3.provider.providers;

import net.ess3.provider.TickCountProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Bukkit;

@ProviderData(description = "Paper Tick Count Provider")
public class PaperTickCountProvider implements TickCountProvider {
    @Override
    public int getTickCount() {
        return Bukkit.getCurrentTick();
    }

    @ProviderTest
    public static boolean test() {
        try {
            Bukkit.class.getDeclaredMethod("getCurrentTick");
            return true;
        } catch (final NoSuchMethodException ignored) {
            return false;
        }
    }
}
