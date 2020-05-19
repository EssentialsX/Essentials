package net.ess3.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationData {

    private String worldName;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public LocationData(String worldName, double x, double y, double z, float pitch, float yaw) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public boolean exists() {
        return Bukkit.getWorld(worldName) != null;
    }

    public Location getLocation() throws InvalidWorldException {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new InvalidWorldException(worldName);
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

}
