package com.earth2me.essentials.config.serializers;

import com.earth2me.essentials.config.entities.LazyLocation;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * A Configurate type serializer for {@link LazyLocation}s.
 */
public class LocationTypeSerializer implements TypeSerializer<LazyLocation> {
    @Override
    public LazyLocation deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final String worldValue = node.node("world").getString();
        if (worldValue == null || worldValue.isEmpty()) {
            throw new SerializationException("No world value present!");
        }

        return new LazyLocation(
                worldValue,
                node.node("world-name").getString(""),
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble(),
                node.node("yaw").getFloat(),
                node.node("pitch").getFloat());
    }

    @Override
    public void serialize(Type type, @Nullable LazyLocation value, ConfigurationNode node) throws SerializationException {
        if (value == null || value.world() == null) {
            node.raw(null);
            return;
        }

        node.node("world").set(String.class, value.world());
        node.node("world-name").set(String.class, value.worldName());
        node.node("x").set(Double.class, value.x());
        node.node("y").set(Double.class, value.y());
        node.node("z").set(Double.class, value.z());
        node.node("yaw").set(Float.class, value.yaw());
        node.node("pitch").set(Float.class, value.pitch());
    }
}
