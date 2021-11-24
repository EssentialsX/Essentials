package net.ess3.provider.providers;

import net.ess3.provider.WorldInfoProvider;
import org.bukkit.World;

public class FixedHeightWorldInfoProvider implements WorldInfoProvider {
    @Override
    public String getDescription() {
        return "World info provider for fixed world heights";
    }

    @Override
    public int getMaxSafeHeight(World world) {
        return world.getEnvironment() == World.Environment.NETHER ? 128 : 256;
    }

    @Override
    public int getMinSafeHeight(World world) {
        return 0;
    }
}
