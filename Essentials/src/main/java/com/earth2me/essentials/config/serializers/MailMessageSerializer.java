package com.earth2me.essentials.config.serializers;

import net.essentialsx.api.v2.services.mail.MailMessage;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

public class MailMessageSerializer implements TypeSerializer<MailMessage> {
    @Override
    public MailMessage deserialize(Type type, ConfigurationNode node) throws SerializationException {
        final boolean legacy = node.node("legacy").getBoolean(true);
        final String rawUuid = node.node("sender-uuid").getString();
        final String uuid = (rawUuid == null || rawUuid.equals("null")) ? null : rawUuid;

        return new MailMessage(node.node("read").getBoolean(false),
                legacy,
                !legacy ? node.node("sender-name").getString() : null,
                uuid == null ? null : UUID.fromString(uuid),
                !legacy ? node.node("timestamp").getLong() : 0L,
                !legacy ? node.node("expire").getLong() : 0L,
                node.node("message").getString());
    }

    @Override
    public void serialize(Type type, @Nullable MailMessage obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        node.node("legacy").set(Boolean.class, obj.isLegacy());
        node.node("read").set(Boolean.class, obj.isRead());
        node.node("message").set(String.class, obj.getMessage());
        if (!obj.isLegacy()) {
            node.node("sender-name").set(String.class, obj.getSenderUsername());
            node.node("sender-uuid").set(String.class, obj.getSenderUUID() == null ? "null" : obj.getSenderUUID().toString());
            node.node("timestamp").set(Long.class, obj.getTimeSent());
            node.node("expire").set(Long.class, obj.getTimeExpire());
        }
    }
}
