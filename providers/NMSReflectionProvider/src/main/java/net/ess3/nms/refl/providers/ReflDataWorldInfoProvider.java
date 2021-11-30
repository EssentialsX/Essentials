package net.ess3.nms.refl.providers;

import net.ess3.provider.WorldInfoProvider;
import org.bukkit.World;

public class ReflDataWorldInfoProvider implements WorldInfoProvider {
    @Override
    public String getDescription() {
        return "NMS world info provider for data-driven worldgen for 1.16.x";
    }

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
}
