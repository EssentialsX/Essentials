package net.ess3.nms.refl.providers;

import net.ess3.provider.WorldInfoProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.World;

@ProviderData(description = "Reflection World Info Provider", weight = 1)
public class ReflDataWorldInfoProvider implements WorldInfoProvider {
    @Override
    public int getMaxHeight(World world) {
        // Method has existed since Beta 1.7 (yes, *beta*)
        return world.getMaxHeight();
    }

    @Override
    public int getLogicalHeight(World world) {
        // TODO: THIS IS INCORRECT
        // This mirrors the vanilla behaviour up until Minecraft 1.16
        return world.getEnvironment() == World.Environment.NETHER ? 128 : 256;
    }

    @Override
    public int getMinHeight(World world) {
        // TODO: THIS IS INCORRECT
        // Worlds could not go below 0 until Minecraft 1.16
        return 0;
    }

    @ProviderTest
    public static boolean test() {
        // TODO: THIS IS INCORRECT
        return false;
    }
}
