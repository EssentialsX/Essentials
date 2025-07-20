package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.SpawnerBlockProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.block.CreatureSpawner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ProviderData(description = "Reflection Spawner Block Provider")
public class ReflSpawnerBlockProvider implements SpawnerBlockProvider {
    @Override
    public void setMaxSpawnDelay(final CreatureSpawner spawner, final int delay) {
        final Class<?> mobSpawnerAbstract = ReflUtil.getNMSClass("MobSpawnerAbstract");
        final Field maxSpawnDelay = ReflUtil.getFieldCached(mobSpawnerAbstract, "maxSpawnDelay");
        if (maxSpawnDelay != null) {
            try {
                maxSpawnDelay.setInt(getNMSSpawner(spawner), delay);
            } catch (final IllegalAccessException ignored) {
            }
        }
    }

    @Override
    public void setMinSpawnDelay(final CreatureSpawner spawner, final int delay) {
        final Class<?> mobSpawnerAbstract = ReflUtil.getNMSClass("MobSpawnerAbstract");
        final Field minSpawnDelay = ReflUtil.getFieldCached(mobSpawnerAbstract, "minSpawnDelay");
        if (minSpawnDelay != null) {
            try {
                minSpawnDelay.setInt(getNMSSpawner(spawner), delay);
            } catch (final IllegalAccessException ignored) {
            }
        }
    }

    private Object getNMSSpawner(final CreatureSpawner spawner) {
        try {
            final Class<?> craftWorld = ReflUtil.getOBCClass("CraftWorld");
            final Class<?> tileEntityMobSpawner = ReflUtil.getNMSClass("TileEntityMobSpawner");
            final Method getSpawner = ReflUtil.getMethodCached(tileEntityMobSpawner, "getSpawner");
            final Method getTileEntityAt = ReflUtil.getMethodCached(craftWorld, "getTileEntityAt", int.class, int.class, int.class);
            if (getSpawner != null && getTileEntityAt != null) {
                final Object craftTileEntity = getTileEntityAt.invoke(spawner.getWorld(), spawner.getX(), spawner.getY(), spawner.getZ());
                return getSpawner.invoke(craftTileEntity);
            }
        } catch (final IllegalAccessException | InvocationTargetException ignored) {
        }
        return null;
    }
}
