package com.earth2me.essentials.spawn;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IEssentialsModule;
import com.earth2me.essentials.config.EssentialsConfiguration;
import com.earth2me.essentials.config.entities.LazyLocation;
import net.ess3.api.IEssentials;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SpawnStorage implements IEssentialsModule, IConf {
    private final IEssentials ess;
    private final EssentialsConfiguration config;
    private final Map<String, LazyLocation> spawns = new HashMap<>();

    SpawnStorage(final IEssentials ess) {
        this.ess = ess;
        this.config = new EssentialsConfiguration(new File(ess.getDataFolder(), "spawn.yml"));
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        synchronized (spawns) {
            config.load();
            spawns.clear();
            // need to outsource this because transitive relocations :)
            spawns.putAll(config.getLocationSectionMap("spawns"));
        }
    }

    void setSpawn(final Location loc, String group) {
        group = group.toLowerCase(Locale.ENGLISH);
        synchronized (spawns) {
            spawns.put(group, LazyLocation.fromLocation(loc));
            config.setProperty("spawns." + group, loc);
            config.save();
        }
    }

    Location getSpawn(String group) {
        if (group == null) {
            return getWorldSpawn();
        }

        group = group.toLowerCase(Locale.ENGLISH);
        synchronized (spawns) {
            if (!spawns.containsKey(group)) {
                if (spawns.containsKey("default")) {
                    return spawns.get("default").location();
                }
                return getWorldSpawn();
            }
            return spawns.get(group).location();
        }
    }

    private Location getWorldSpawn() {
        for (final World world : ess.getServer().getWorlds()) {
            if (world.getEnvironment() != World.Environment.NORMAL) {
                continue;
            }
            return world.getSpawnLocation();
        }
        return ess.getServer().getWorlds().get(0).getSpawnLocation();
    }
}
