package net.ess3.provider.providers;

import net.ess3.provider.WorldInfoProvider;
import org.bukkit.World;

public class ModernWorldInfoProvider implements WorldInfoProvider {
    @Override
    public String getDescription() {
        return "World info provider for data-driven world generation";
    }

    @Override
    public int getMaxSafeHeight(World world) {
        return world.getLogicalHeight();
    }

    @Override
    public int getMinSafeHeight(World world) {
        return world.getMinHeight();
    }
}
