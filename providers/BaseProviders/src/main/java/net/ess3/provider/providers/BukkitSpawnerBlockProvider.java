package net.ess3.provider.providers;

import net.ess3.provider.SpawnerBlockProvider;
import org.bukkit.block.CreatureSpawner;

public class BukkitSpawnerBlockProvider implements SpawnerBlockProvider {
    @Override
    public void setMaxSpawnDelay(final CreatureSpawner spawner, final int delay) {
        spawner.setMaxSpawnDelay(delay);
    }

    @Override
    public void setMinSpawnDelay(final CreatureSpawner spawner, final int delay) {
        spawner.setMinSpawnDelay(delay);
    }

    @Override
    public String getDescription() {
        return "Bukkit 1.12+ provider";
    }
}
