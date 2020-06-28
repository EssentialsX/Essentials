package com.earth2me.essentials;

import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;
import org.bukkit.block.Biome;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RandomTeleport implements IConf {
    private final IEssentials essentials;
    private final EssentialsConf config;
    private final ConcurrentLinkedQueue<Location> cachedLocations = new ConcurrentLinkedQueue<>();

    public RandomTeleport(final IEssentials essentials) {
        this.essentials = essentials;
        File file = new File(essentials.getDataFolder(), "tpr.yml");
        config = new EssentialsConf(file);
        config.setTemplateName("/tpr.yml");
        config.options().copyHeader(true);
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        config.load();
        cachedLocations.clear();
    }

    public Location getCenter() {
        try {
            Location center = config.getLocation("center", essentials.getServer());
            if (center != null) {
                return center;
            }
        } catch (InvalidWorldException ignored) {
        }
        Location center = essentials.getServer().getWorlds().get(0).getWorldBorder().getCenter();
        center.setY(center.getWorld().getHighestBlockYAt(center) + 1);
        setCenter(center);
        return center;
    }

    public void setCenter(Location center) {
        config.setProperty("center", center);
        config.save();
    }

    public double getMinRange() {
        return config.getDouble("min-range", 0d);
    }

    public void setMinRange(double minRange) {
        config.setProperty("min-range", minRange);
        config.save();
    }

    public double getMaxRange() {
        return config.getDouble("max-range", 1000d);
    }

    public void setMaxRange(double maxRange) {
        config.setProperty("max-range", maxRange);
        config.save();
    }

    public Set<Biome> getExcludedBiomes() {
        List<String> biomeNames = config.getStringList("excluded-biomes");
        Set<Biome> excludedBiomes = new HashSet<>();
        for (String biomeName : biomeNames) {
            try {
                excludedBiomes.add(Biome.valueOf(biomeName.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return excludedBiomes;
    }

    public int getFindAttempts() {
        return config.getInt("find-attempts", 10);
    }

    public int getCacheThreshold() {
        return config.getInt("cache-threshold", 10);
    }

    public Queue<Location> getCachedLocations() {
        return cachedLocations;
    }
}
