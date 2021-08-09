package com.earth2me.essentials.config.entities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

/**
 * Represents a Location but doesn't parse the location until it is requested via {@link LazyLocation#location()}.
 */
public class LazyLocation {
    private String world;
    private String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public LazyLocation(String worldId, String worldName, double x, double y, double z, float yaw, float pitch) {
        this.world = worldId;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public String world() {
        return world;
    }

    public String worldName() {
        return worldName;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public float yaw() {
        return yaw;
    }

    public float pitch() {
        return pitch;
    }

    public Location location() {
        if (this.world == null || this.world.isEmpty()) {
            return null;
        }

        World world = null;

        try {
            final UUID worldId = UUID.fromString(this.world);
            world = Bukkit.getWorld(worldId);
        } catch (IllegalArgumentException ignored) {
        }

        if (world == null) {
            world = Bukkit.getWorld(this.world);
        }

        if (world == null && this.worldName != null && !this.worldName.isEmpty()) {
            world = Bukkit.getWorld(this.worldName);
        }

        if (world == null) {
            return null;
        }

        this.world = world.getUID().toString();
        this.worldName = world.getName();

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static LazyLocation fromLocation(final Location location) {
        //noinspection ConstantConditions
        return new LazyLocation(location.getWorld().getUID().toString(), location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
