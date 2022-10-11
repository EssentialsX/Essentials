package com.earth2me.essentials;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerTarget implements ITarget {
    private final Player player;

    public PlayerTarget(final Player entity) {
        this.player = entity;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public Location getLocation() {
        return getPlayer().getLocation();
    }
}
