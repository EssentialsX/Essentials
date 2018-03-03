package com.neximation.essentials.settings;

import com.neximation.essentials.storage.MapValueType;
import com.neximation.essentials.storage.StorageObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;


@Data @EqualsAndHashCode(callSuper = false) public class Jails implements StorageObject {
    @MapValueType(Location.class)
    private Map<String, Location> jails = new HashMap<String, Location>();
}
