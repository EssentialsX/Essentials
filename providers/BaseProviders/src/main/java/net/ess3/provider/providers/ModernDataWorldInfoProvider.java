package net.ess3.provider.providers;

import net.ess3.provider.WorldInfoProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.World;

@ProviderData(description = "1.17.1+ World Info Provider", weight = 2)
public class ModernDataWorldInfoProvider implements WorldInfoProvider {
    @Override
    public int getMaxHeight(World world) {
        return world.getMaxHeight();
    }

    @Override
    public int getLogicalHeight(World world) {
        return world.getLogicalHeight();
    }

    @Override
    public int getMinHeight(World world) {
        return world.getMinHeight();
    }

    @ProviderTest
    public static boolean test() {
        try {
            Class.forName("org.bukkit.generator.WorldInfo");
            return true;
        } catch (final ClassNotFoundException ignored) {
            return false;
        }
    }
}
