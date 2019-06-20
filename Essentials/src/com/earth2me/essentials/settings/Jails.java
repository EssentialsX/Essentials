package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.MapValueType;
import com.earth2me.essentials.storage.StorageObject;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * <p>Jails class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Jails implements StorageObject {
    @MapValueType(Location.class)
    private Map<String, Location> jails = new HashMap<>();

    /**
     * <p>Getter for the field <code>jails</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Location> getJails() {
        return jails;
    }

    /**
     * <p>Setter for the field <code>jails</code>.</p>
     *
     * @param jails a {@link java.util.Map} object.
     */
    public void setJails(Map<String, Location> jails) {
        this.jails = jails;
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
        Jails jails1 = (Jails) o;
        return Objects.equals(jails, jails1.jails);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(jails);
    }
}
