package com.earth2me.essentials;

import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.VersionUtil;
import io.papermc.lib.PaperLib;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RandomTeleport implements IConf {
    private final IEssentials essentials;
    private final EssentialsConf config;
    private final ConcurrentLinkedQueue<Location> cachedLocations = new ConcurrentLinkedQueue<>();
    private static final Random RANDOM = new Random();
    private static final int HIGHEST_BLOCK_Y_OFFSET = VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_15_R01) ? 1 : 0;

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
        return config.getDouble("max-range", getCenter().getWorld().getWorldBorder().getSize() / 2);
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

    public boolean getPreCache() {
        return config.getBoolean("pre-cache", true);
    }

    public Queue<Location> getCachedLocations() {
        return cachedLocations;
    }

    // Get a random location; cached if possible. Otherwise on demand.
    public CompletableFuture<Location> getRandomLocation(Location center, double minRange, double maxRange) {
        int findAttempts = this.getFindAttempts();
        Queue<Location> cachedLocations = this.getCachedLocations();
        // Try to build up the cache if it is below the threshold
        if (cachedLocations.size() < this.getCacheThreshold()) {
            cacheRandomLocations(center, minRange, maxRange);
        }
        CompletableFuture<Location> future = new CompletableFuture<>();
        // Return a random location immediately if one is available, otherwise try to find one now
        if (cachedLocations.isEmpty()) {
            attemptRandomLocation(findAttempts, center, minRange, maxRange).thenAccept(future::complete);
        } else {
            future.complete(cachedLocations.poll());
        }
        return future;
    }

    // Prompts caching random valid locations, up to a maximum number of attempts
    public void cacheRandomLocations(Location center, double minRange, double maxRange) {
        essentials.getServer().getScheduler().scheduleSyncDelayedTask(essentials, () -> {
            for (int i = 0; i < this.getFindAttempts(); ++i) {
                calculateRandomLocation(center, minRange, maxRange).thenAccept(location -> {
                    if (isValidRandomLocation(location)) {
                        this.getCachedLocations().add(location);
                    }
                });
            }
        });
    }

    // Recursively attempt to find a random location. After a maximum number of attempts, the center is returned.
    private CompletableFuture<Location> attemptRandomLocation(int attempts, Location center, double minRange, double maxRange) {
        CompletableFuture<Location> future = new CompletableFuture<>();
        if (attempts > 0) {
            calculateRandomLocation(center, minRange, maxRange).thenAccept(location -> {
                if (isValidRandomLocation(location)) {
                    future.complete(location);
                } else {
                    attemptRandomLocation(attempts - 1, center, minRange, maxRange).thenAccept(future::complete);
                }
            });
        } else {
            future.complete(center);
        }
        return future;
    }

    // Calculates a random location asynchronously.
    private CompletableFuture<Location> calculateRandomLocation(Location center, double minRange, double maxRange) {
        CompletableFuture<Location> future = new CompletableFuture<>();
        double[] offsets = getRandomOffsets(minRange, maxRange);
        Location location = new Location(
                center.getWorld(),
                center.getX() + offsets[0],
                center.getWorld().getMaxHeight(),
                center.getZ() + offsets[1],
                360 * RANDOM.nextFloat() - 180,
                0
        );
        PaperLib.getChunkAtAsync(location).thenAccept(chunk -> {
            if (World.Environment.NETHER.equals(center.getWorld().getEnvironment())) {
                location.setY(getNetherYAt(location));
            } else {
                location.setY(center.getWorld().getHighestBlockYAt(location) + HIGHEST_BLOCK_Y_OFFSET);
            }
            future.complete(location);
        });
        return future;
    }

    // Returns an array of length 2 representing random coordinates inside a square with a square hole in the middle
    private double[] getRandomOffsets(double minRange, double maxRange) {
        double[] randPairAtSize = new double[]{
                RANDOM.nextDouble() * (maxRange - minRange) + minRange,
                RANDOM.nextDouble() * (maxRange + minRange) - minRange};
        switch (RANDOM.nextInt(4)) {
            case 1:
                return new double[]{-randPairAtSize[1], randPairAtSize[0]};
            case 2:
                return new double[]{-randPairAtSize[0], -randPairAtSize[1]};
            case 3:
                return new double[]{randPairAtSize[1], -randPairAtSize[0]};
            default: //Catches case 0 without requiring a return outside the switch
                return randPairAtSize;
        }
    }

    // Returns an appropriate elevation for a given location in the nether, or -1 if none is found
    private double getNetherYAt(Location location) {
        for (int y = 32; y < location.getWorld().getMaxHeight() / 2; ++y) {
            if (!LocationUtil.isBlockUnsafe(location.getWorld(), location.getBlockX(), y, location.getBlockZ())) {
                return y;
            }
        }
        return -1;
    }

    private boolean isValidRandomLocation(Location location) {
        return location.getBlockY() > 0 && !this.getExcludedBiomes().contains(location.getBlock().getBiome());
    }
}
