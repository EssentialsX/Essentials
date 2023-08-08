package com.earth2me.essentials.config.serializers;

import org.bukkit.Material;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class MaterialTypeSerializer extends ScalarSerializer<Material> {
    public MaterialTypeSerializer() {
        super(Material.class);
    }

    @Override
    public Material deserialize(Type type, Object obj) throws SerializationException {
        if (obj instanceof String) {
            return Material.matchMaterial((String) obj);
        }

        // Configurate will use an EnumSet to deserialize, which doesn't support null types. Default to air.
        return Material.AIR;
    }

    @Override
    protected Object serialize(Material item, Predicate<Class<?>> typeSupported) {
        if (item == null) {
            return null;
        }
        return item.name();
    }
}
