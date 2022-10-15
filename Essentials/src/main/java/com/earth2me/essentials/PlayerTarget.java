package com.earth2me.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerTarget implements ITarget {
    private final UUID uuid;

    public PlayerTarget(final Player entity) {
        this.uuid = entity.getUniqueId();
    }

    @Override
    public Location getLocation() {
        return Bukkit.getPlayer(uuid).getLocation();
    }
}
