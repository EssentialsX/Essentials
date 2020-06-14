package net.ess3.nms.updatedmeta;

import net.ess3.nms.SpawnerBlockProvider;
import org.bukkit.block.CreatureSpawner;

public class BukkitSpawnerBlockProvider extends SpawnerBlockProvider {
    @Override
    public void setMaxSpawnDelay(CreatureSpawner spawner, int delay) {
        spawner.setMaxSpawnDelay(delay);
    }

    @Override
    public void setMinSpawnDelay(CreatureSpawner spawner, int delay) {
        spawner.setMinSpawnDelay(delay);
    }

    @Override
    public boolean tryProvider() {
        try {
            CreatureSpawner.class.getMethod("setMaxSpawnDelay", int.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Bukkit 1.12+ provider";
    }
}
