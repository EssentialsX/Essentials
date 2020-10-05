package net.ess3.nms.refl.providers;

import net.ess3.nms.refl.ReflUtil;
import net.ess3.provider.SpawnerBlockProvider;
import org.bukkit.block.CreatureSpawner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflSpawnerBlockProvider implements SpawnerBlockProvider {
    @Override
    public void setMaxSpawnDelay(CreatureSpawner spawner, int delay) {
        Class<?> mobSpawnerAbstract = ReflUtil.getNMSClass("MobSpawnerAbstract");
        Field maxSpawnDelay = ReflUtil.getFieldCached(mobSpawnerAbstract, "maxSpawnDelay");
        if (maxSpawnDelay != null) {
            try {
                maxSpawnDelay.setInt(getNMSSpawner(spawner), delay);
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    @Override
    public void setMinSpawnDelay(CreatureSpawner spawner, int delay) {
        Class<?> mobSpawnerAbstract = ReflUtil.getNMSClass("MobSpawnerAbstract");
        Field minSpawnDelay = ReflUtil.getFieldCached(mobSpawnerAbstract, "minSpawnDelay");
        if (minSpawnDelay != null) {
            try {
                minSpawnDelay.setInt(getNMSSpawner(spawner), delay);
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    @Override
    public String getDescription() {
        return "Reflection based provider";
    }

    private Object getNMSSpawner(CreatureSpawner spawner) {
        try {
            Class<?> craftWorld = ReflUtil.getOBCClass("CraftWorld");
            Class<?> tileEntityMobSpawner = ReflUtil.getNMSClass("TileEntityMobSpawner");
            Method getSpawner = ReflUtil.getMethodCached(tileEntityMobSpawner, "getSpawner");
            Method getTileEntityAt = ReflUtil.getMethodCached(craftWorld, "getTileEntityAt", int.class, int.class, int.class);
            if (getSpawner != null && getTileEntityAt != null) {
                Object craftTileEntity = getTileEntityAt.invoke(spawner.getWorld(), spawner.getX(), spawner.getY(), spawner.getZ());
                return getSpawner.invoke(craftTileEntity);
            }
        } catch (IllegalAccessException | InvocationTargetException ignored) {
        }
        return null;
    }
}
