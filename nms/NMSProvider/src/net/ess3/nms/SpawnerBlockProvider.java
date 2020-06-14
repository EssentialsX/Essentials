package net.ess3.nms;

import net.ess3.providers.Provider;
import org.bukkit.block.CreatureSpawner;

public abstract class SpawnerBlockProvider implements Provider {
    public abstract void setMaxSpawnDelay(CreatureSpawner spawner, int delay);

    public abstract void setMinSpawnDelay(CreatureSpawner spawner, int delay);
}
