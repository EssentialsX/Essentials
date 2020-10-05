package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.MapValueType;
import com.earth2me.essentials.storage.StorageObject;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Spawns implements StorageObject {
    @MapValueType(Location.class)
    private Map<String, Location> spawns = new HashMap<>();

    public Map<String, Location> getSpawns() {
        return spawns;
    }

    public void setSpawns(final Map<String, Location> spawns) {
        this.spawns = spawns;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Spawns spawns1 = (Spawns) o;
        return Objects.equals(spawns, spawns1.spawns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spawns);
    }
}
