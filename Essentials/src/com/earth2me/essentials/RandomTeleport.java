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
    private static final Random RANDOM = new Random();
    private static final int HIGHEST_BLOCK_Y_OFFSET = VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_15_R01) ? 1 : 0;
    private final IEssentials essentials;
    private final EssentialsConf config;
    private final ConcurrentLinkedQueue<Location> cachedLocations = new ConcurrentLinkedQueue<>();

    public RandomTeleport(final IEssentials essentials) {
        this.essentials = essentials;
        final File file = new File(essentials.getDataFolder(), "tpr.yml");
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
            final Location center = config.getLocation("center", essentials.getServer());
            if (center != null) {
                return center;
            }
        } catch (final InvalidWorldException ignored) {
        }
        final Location center = essentials.getServer().getWorlds().get(0).getWorldBorder().getCenter();
        center.setY(center.getWorld().getHighestBlockYAt(center) + 1);
        setCenter(center);
        return center;
    }

    public void setCenter(final Location center) {
        config.setProperty("center", center);
        config.save();
    }

    public double getMinRange() {
        return config.getDouble("min-range", 0d);
    }

    public void setMinRange(final double minRange) {
        config.setProperty("min-range", minRange);
        config.save();
    }

    public double getMaxRange() {
        return config.getDouble("max-range", getCenter().getWorld().getWorldBorder().getSize() / 2);
    }

    public void setMaxRange(final double maxRange) {
        config.setProperty("max-range", maxRange);
        config.save();
    }

    public Set<Biome> getExcludedBiomes() {
        final List<String> biomeNames = config.getStringList("excluded-biomes");
        final Set<Biome> excludedBiomes = new HashSet<>();
        for (final String biomeName : biomeNames) {
            try {
                excludedBiomes.add(Biome.valueOf(biomeName.toUpperCase()));
            } catch (final IllegalArgumentException ignored) {
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
        return config.getBoolean("pre-cache", false);
    }

    public Queue<Location> getCachedLocations() {
        return cachedLocations;
    }

    // Get a random location; cached if possible. Otherwise on demand.
    public CompletableFuture<Location> getRandomLocation(final Location center, final double minRange, final double maxRange) {
        final int findAttempts = this.getFindAttempts();
        final Queue<Location> cachedLocations = this.getCachedLocations();
        // Try to build up the cache if it is below the threshold
        if (cachedLocations.size() < this.getCacheThreshold()) {
            cacheRandomLocations(center, minRange, maxRange);
        }
        final CompletableFuture<Location> future = new CompletableFuture<>();
        // Return a random location immediately if one is available, otherwise try to find one now
        if (cachedLocations.isEmpty()) {
            attemptRandomLocation(findAttempts, center, minRange, maxRange).thenAccept(future::complete);
        } else {
            future.complete(cachedLocations.poll());
        }
        return future;
    }

    // Prompts caching random valid locations, up to a maximum number of attempts
    public void cacheRandomLocations(final Location center, final double minRange, final double maxRange) {
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
    private CompletableFuture<Location> attemptRandomLocation(final int attempts, final Location center, final double minRange, final double maxRange) {
        final CompletableFuture<Location> future = new CompletableFuture<>();
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
    private CompletableFuture<Location> calculateRandomLocation(final Location center, final double minRange, final double maxRange) {
        final CompletableFuture<Location> future = new CompletableFuture<>();
        // Find an equally distributed offset by randomly rotating a point inside a rectangle about the origin
        final double rectX = RANDOM.nextDouble() * (maxRange - minRange) + minRange;
        final double rectZ = RANDOM.nextDouble() * (maxRange + minRange) - minRange;
        final double offsetX;
        final double offsetZ;
        final int transform = RANDOM.nextInt(4);
        if (transform == 0) {
            offsetX = rectX;
            offsetZ = rectZ;
        } else if (transform == 1) {
            offsetX = -rectZ;
            offsetZ = rectX;
        } else if (transform == 2) {
            offsetX = -rectX;
            offsetZ = -rectZ;
        } else {
            offsetX = rectZ;
            offsetZ = -rectX;
        }
        final Location location = new Location(
            center.getWorld(),
            center.getX() + offsetX,
            center.getWorld().getMaxHeight(),
            center.getZ() + offsetZ,
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

    // Returns an appropriate elevation for a given location in the nether, or -1 if none is found
    private double getNetherYAt(final Location location) {
        for (int y = 32; y < location.getWorld().getMaxHeight() / 2; ++y) {
            if (!LocationUtil.isBlockUnsafe(location.getWorld(), location.getBlockX(), y, location.getBlockZ())) {
                return y;
            }
        }
        return -1;
    }

    private boolean isValidRandomLocation(final Location location) {
        return location.getBlockY() > 0 && !this.getExcludedBiomes().contains(location.getBlock().getBiome());
    }
}
