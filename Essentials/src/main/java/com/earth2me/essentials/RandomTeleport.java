package com.earth2me.essentials;

import com.earth2me.essentials.config.ConfigurateUtil;
import com.earth2me.essentials.config.EssentialsConfiguration;
import com.earth2me.essentials.config.entities.LazyLocation;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.VersionUtil;
import io.papermc.lib.PaperLib;
import net.ess3.provider.BiomeKeyProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private final Map<String, ConcurrentLinkedQueue<Location>> cachedLocations = new HashMap<>();

    public RandomTeleport(final IEssentials essentials) {
        this.ess = essentials;
        config = new EssentialsConfiguration(new File(essentials.getDataFolder(), "tpr.yml"), "/tpr.yml",
                "Configuration for the random teleport command.\nUse the /settpr command in-game to set random teleport locations.");
        reloadConfig();
    }

    public EssentialsConfiguration getConfig() {
        return config;
    }

    @Override
    public void reloadConfig() {
        config.load();
        cachedLocations.clear();
    }

    public boolean hasLocation(final String name) {
        return config.hasProperty("locations." + name);
    }

    public Location getCenter(final String name) {
        final LazyLocation center = config.getLocation(locationKey(name, "center"));
        if (center != null && center.location() != null) {
            return center.location();
        }

        final Location worldCenter = ess.getServer().getWorlds().get(0).getWorldBorder().getCenter();
        worldCenter.setY(worldCenter.getWorld().getHighestBlockYAt(worldCenter) + HIGHEST_BLOCK_Y_OFFSET);
        setCenter(name, worldCenter);
        return worldCenter;
    }

    public void setCenter(final String name, final Location center) {
        config.setProperty(locationKey(name, "center"), center);
        config.save();
    }

    public double getMinRange(final String name) {
        return config.getDouble(locationKey(name, "min-range"), 0d);
    }

    public void setMinRange(final String name, final double minRange) {
        config.setProperty(locationKey(name, "min-range"), minRange);
        config.save();
    }

    public double getMaxRange(final String name) {
        return config.getDouble(locationKey(name, "max-range"), getCenter(name).getWorld().getWorldBorder().getSize() / 2);
    }

    public void setMaxRange(final String name, final double maxRange) {
        config.setProperty(locationKey(name, "max-range"), maxRange);
        config.save();
    }

    public String getDefaultLocation() {
        return config.getString("default-location", "{world}");
    }

    public boolean isPerLocationPermission() {
        return config.getBoolean("per-location-permission", false);
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

    public List<String> listLocations() {
        return new ArrayList<>(ConfigurateUtil.getKeys(config.getRootNode().node("locations")));
    }

    public Queue<Location> getCachedLocations(final String name) {
        this.cachedLocations.computeIfAbsent(name, x -> new ConcurrentLinkedQueue<>());
        return cachedLocations.get(name);
    }

    // Get a named random teleport location; cached if possible, otherwise on demand.
    public CompletableFuture<Location> getRandomLocation(final String name) {
        final Queue<Location> cached = this.getCachedLocations(name);
        // Try to build up the cache if it is below the threshold
        if (cached.size() < this.getCacheThreshold()) {
            cacheRandomLocations(name);
        }
        final CompletableFuture<Location> future = new CompletableFuture<>();
        // Return a random location immediately if one is available, otherwise try to find one now
        if (cached.isEmpty()) {
            final int findAttempts = this.getFindAttempts();
            final Location center = this.getCenter(name);
            final double minRange = this.getMinRange(name);
            final double maxRange = this.getMaxRange(name);
            attemptRandomLocation(findAttempts, center, minRange, maxRange).thenAccept(future::complete);
        } else {
            future.complete(cached.poll());
        }
        return future;
    }

    // Get a random location with specific parameters (note: not cached).
    public CompletableFuture<Location> getRandomLocation(final Location center, final double minRange, final double maxRange) {
        return attemptRandomLocation(this.getFindAttempts(), center, minRange, maxRange);
    }

    // Prompts caching random valid locations, up to a maximum number of attempts.
    public void cacheRandomLocations(final String name) {
        ess.getServer().getScheduler().scheduleSyncDelayedTask(ess, () -> {
            for (int i = 0; i < this.getFindAttempts(); ++i) {
                calculateRandomLocation(getCenter(name), getMinRange(name), getMaxRange(name)).thenAccept(location -> {
                    if (isValidRandomLocation(location)) {
                        this.getCachedLocations(name).add(location);
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
            ess.getWorldInfoProvider().getMaxHeight(center.getWorld()),
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

    // Returns an appropriate elevation for a given location in the nether, or MIN_VALUE if none is found
    private double getNetherYAt(final Location location) {
        final World world = location.getWorld();
        for (int y = 32; y < ess.getWorldInfoProvider().getMaxHeight(world); ++y) {
            if (Material.BEDROCK.equals(world.getBlockAt(location.getBlockX(), y, location.getBlockZ()).getType())) {
                break;
            }
            if (!LocationUtil.isBlockUnsafe(ess, world, location.getBlockX(), y, location.getBlockZ())) {
                return y;
            }
        }
        return Double.MIN_VALUE;
    }

    private boolean isValidRandomLocation(final Location location) {
        return location.getBlockY() > ess.getWorldInfoProvider().getMinHeight(location.getWorld()) && !isExcludedBiome(location);
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

    private String locationKey(final String name, final String key) {
        return "locations." + name + "." + key;
    }

    public File getFile() {
        return config.getFile();
    }
}
