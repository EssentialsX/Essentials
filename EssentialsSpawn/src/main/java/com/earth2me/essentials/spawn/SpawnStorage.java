package com.earth2me.essentials.spawn;

import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.settings.Spawns;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import net.ess3.api.IEssentials;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SpawnStorage extends AsyncStorageObjectHolder<Spawns> implements IEssentialsModule {
    SpawnStorage(final IEssentials ess) {
        super(ess, Spawns.class);
        reloadConfig();
    }

    @Override
    public File getStorageFile() {
        return new File(ess.getDataFolder(), "spawn.yml");
    }

    @Override
    public void finishRead() { }

    @Override
    public void finishWrite() { }

    void setSpawn(final Location loc, final String group) {
        acquireWriteLock();
        try {
            if (getData().getSpawns() == null) {
                getData().setSpawns(new HashMap<>());
            }
            getData().getSpawns().put(group.toLowerCase(Locale.ENGLISH), loc);
        } finally {
            unlock();
        }

        if ("default".equalsIgnoreCase(group)) {
            loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        }
    }

    Location getSpawn(final String group) {
        acquireReadLock();
        try {
            if (getData().getSpawns() == null || group == null) {
                return getWorldSpawn();
            }
            final Map<String, Location> spawnMap = getData().getSpawns();
            String groupName = group.toLowerCase(Locale.ENGLISH);
            if (!spawnMap.containsKey(groupName)) {
                groupName = "default";
            }
            if (!spawnMap.containsKey(groupName)) {
                return getWorldSpawn();
            }
            return spawnMap.get(groupName);
        } finally {
            unlock();
        }
    }

    private Location getWorldSpawn() {
        for (World world : ess.getServer().getWorlds()) {
            if (world.getEnvironment() != World.Environment.NORMAL) {
                continue;
            }
            return world.getSpawnLocation();
        }
        return ess.getServer().getWorlds().get(0).getSpawnLocation();
    }
}
