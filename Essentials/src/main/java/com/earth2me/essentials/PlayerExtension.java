package com.earth2me.essentials;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlayerExtension {
    protected Player base;

    public PlayerExtension(final Player base) {
        this.base = base;
    }

    public final Player getBase() {
        return base;
    }

    public final Player setBase(final Player base) {
        return this.base = base;
    }

    public Server getServer() {
        return base.getServer();
    }

    public World getWorld() {
        return base.getWorld();
    }

    public Location getLocation() {
        return base.getLocation();
    }

    public OfflinePlayer getOffline() {
        return base.getServer().getOfflinePlayer(base.getUniqueId());
    }
}
