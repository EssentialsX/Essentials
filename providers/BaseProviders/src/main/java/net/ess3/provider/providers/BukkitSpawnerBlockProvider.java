package net.ess3.provider.providers;

import net.ess3.provider.SpawnerBlockProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.block.CreatureSpawner;

@ProviderData(description = "1.12+ Spawner Block Provider", weight = 1)
public class BukkitSpawnerBlockProvider implements SpawnerBlockProvider {
    @Override
    public void setMaxSpawnDelay(final CreatureSpawner spawner, final int delay) {
        spawner.setMaxSpawnDelay(delay);
    }

    @Override
    public void setMinSpawnDelay(final CreatureSpawner spawner, final int delay) {
        spawner.setMinSpawnDelay(delay);
    }

    @ProviderTest
    public static boolean test() {
        try {
            CreatureSpawner.class.getDeclaredMethod("setMaxSpawnDelay", int.class);
            return true;
        } catch (final NoSuchMethodException ignored) {
            return false;
        }
    }
}
