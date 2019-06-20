package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.MapValueType;
import com.earth2me.essentials.storage.StorageObject;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>Spawns class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Spawns implements StorageObject {
    @MapValueType(Location.class)
    private Map<String, Location> spawns = new HashMap<>();

    /**
     * <p>Getter for the field <code>spawns</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Location> getSpawns() {
        return spawns;
    }

    /**
     * <p>Setter for the field <code>spawns</code>.</p>
     *
     * @param spawns a {@link java.util.Map} object.
     */
    public void setSpawns(Map<String, Location> spawns) {
        this.spawns = spawns;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Spawns spawns1 = (Spawns) o;
        return Objects.equals(spawns, spawns1.spawns);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(spawns);
    }
}
