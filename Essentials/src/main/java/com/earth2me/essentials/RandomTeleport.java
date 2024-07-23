package com.earth2me.essentials;

import com.earth2me.essentials.config.EssentialsConfiguration;
import com.earth2me.essentials.config.entities.LazyLocation;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.VersionUtil;
import io.papermc.lib.PaperLib;
import net.ess3.api.InvalidWorldException;
import net.ess3.provider.BiomeKeyProvider;
import net.ess3.provider.WorldInfoProvider;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.HashSet;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RandomTeleport implements IConf {
    private static final Random RANDOM = new Random();
    private static final int HIGHEST_BLOCK_Y_OFFSET = VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_15_R01) ? 1 : 0;
    private final IEssentials ess;
    private final EssentialsConfiguration config;
    private final ConcurrentLinkedQueue<Location> cachedLocations = new ConcurrentLinkedQueue<>();
    private WorldInfoProvider worldInfoProvider;

    public RandomTeleport(final IEssentials essentials) {
        this.ess = essentials;
        config = new EssentialsConfiguration(new File(essentials.getDataFolder(), "tpr.yml"), "/tpr.yml",
                "Configuration for the random teleport command.\nSome settings may be defaulted, and can be changed via the /settpr command in-game.");
    }

    @Override
    public void reloadConfig() {
        worldInfoProvider = ess.provider(WorldInfoProvider.class);
        config.load();
        cachedLocations.clear();
    }

    public Location getCenter() {
        try {
            final LazyLocation center = config.getLocation("center");
            if (center != null && center.location() != null) {
                return center.location();
            }
        } catch (final InvalidWorldException ignored) {
        }
        final Location center = ess.getServer().getWorlds().get(0).getWorldBorder().getCenter();
        center.setY(center.getWorld().getHighestBlockYAt(center) + HIGHEST_BLOCK_Y_OFFSET);
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

    public Set<String> getExcludedBiomes() {
        final Set<String> excludedBiomes = new HashSet<>();
        for (final String key : config.getList("excluded-biomes", String.class)) {
            excludedBiomes.add(key.toLowerCase());
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
        ess.getServer().getScheduler().scheduleSyncDelayedTask(ess, () -> {
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
            worldInfoProvider.getMaxHeight(center.getWorld()),
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
        for (int y = 32; y < worldInfoProvider.getMaxHeight(location.getWorld()); ++y) {
            if (!LocationUtil.isBlockUnsafe(ess, location.getWorld(), location.getBlockX(), y, location.getBlockZ())) {
                return y;
            }
        }
        return -1;
    }

    private boolean isValidRandomLocation(final Location location) {
        return location.getBlockY() > worldInfoProvider.getMinHeight(location.getWorld()) && !isExcludedBiome(location);
    }

    // Exclude biome if enum or namespaced key matches
    private boolean isExcludedBiome(final Location location) {
        final Set<String> excluded = getExcludedBiomes();
        final String enumKey = location.getBlock().getBiome().name().toLowerCase();
        // Try with good old bukkit enum
        if (excluded.contains(enumKey)) {
            return true;
        }
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_14_4_R01)) {
            // No way to get the biome key on versions below this
            return false;
        }
        final String biomeKey;
        final BiomeKeyProvider biomeKeyProvider = ess.getBiomeKeyProvider();
        if (biomeKeyProvider != null) {
            // Works with custom biome keys
            biomeKey = biomeKeyProvider.getBiomeKey(location.getBlock()).toString();
        } else {
            // Custom biome keys resolve as "minecraft:custom" which is unfortunate
            biomeKey = location.getBlock().getBiome().getKey().toString();
        }
        return excluded.contains(biomeKey);
    }

    public File getFile() {
        return config.getFile();
    }
}
