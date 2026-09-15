package com.earth2me.essentials.config.serializers;

import com.earth2me.essentials.config.entities.CommandWarmup;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

public class CommandWarmupSerializer implements TypeSerializer<CommandWarmup> {
    @Override
    public CommandWarmup deserialize(Type type, ConfigurationNode node) throws SerializationException {
        try {
            final Pattern pattern = node.node("pattern").get(Pattern.class);
            if (node.node("value").isNull()) {
                return null;
            }
            final Long longValue = node.node("value").getLong();
            final CommandWarmup warmup = new CommandWarmup();
            warmup.pattern(pattern);
            warmup.value(longValue);
            return warmup;
        } catch (final SerializationException ignored) {
        }
        return null;
    }

    @Override
    public void serialize(Type type, @Nullable CommandWarmup obj, ConfigurationNode node) throws SerializationException {
        if (obj == null || obj.isIncomplete()) {
            node.raw(null);
            return;
        }

        node.node("pattern").set(Pattern.class, obj.pattern());
        node.node("value").set(Long.class, obj.value());
    }
}
