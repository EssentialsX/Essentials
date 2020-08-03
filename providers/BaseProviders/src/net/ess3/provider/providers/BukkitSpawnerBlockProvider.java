package net.ess3.provider.providers;

import net.ess3.provider.SpawnerBlockProvider;
import org.bukkit.block.CreatureSpawner;

public class BukkitSpawnerBlockProvider implements SpawnerBlockProvider {
    @Override
    public void setMaxSpawnDelay(CreatureSpawner spawner, int delay) {
        spawner.setMaxSpawnDelay(delay);
    }

    @Override
    public void setMinSpawnDelay(CreatureSpawner spawner, int delay) {
        spawner.setMinSpawnDelay(delay);
    }

    @Override
    public String getDescription() {
        return "Bukkit 1.12+ provider";
    }
}
