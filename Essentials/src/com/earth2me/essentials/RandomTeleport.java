package com.earth2me.essentials;

import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;

import java.io.File;

public class RandomTeleport implements IConf {
    private final IEssentials essentials;
    private final EssentialsConf config;

    public RandomTeleport(final IEssentials essentials) {
        this.essentials = essentials;
        File file = new File(essentials.getDataFolder(), "random.yml");
        config = new EssentialsConf(file);
        config.setTemplateName("/random.yml");
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        config.load();
    }

    public Location getCenter() {
        try {
            return config.getLocation("center", essentials.getServer());
        } catch (InvalidWorldException e) {
            return null;
        }
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
}
