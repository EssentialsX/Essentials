package net.ess3.provider.providers;

import net.ess3.provider.WorldInfoProvider;
import org.bukkit.World;

public class ModernDataWorldInfoProvider implements WorldInfoProvider {
    @Override
    public String getDescription() {
        return "API world info provider for data-driven worldgen for 1.17.1+";
    }

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
}
