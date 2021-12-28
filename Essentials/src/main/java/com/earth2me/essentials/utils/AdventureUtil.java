package com.earth2me.essentials.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyFormat;
import org.bukkit.ChatColor;

public final class AdventureUtil {
    public static final LegacyComponentSerializer LEGACY_SERIALIZER;

    static {
        final LegacyComponentSerializer.Builder builder = LegacyComponentSerializer.builder().flattener(ComponentFlattener.basic()).useUnusualXRepeatedCharacterHexFormat();
        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_1_R01)) {
            builder.hexColors();
        }
        LEGACY_SERIALIZER = builder.build();
    }

    private AdventureUtil() {
    }

    public static Component toComponent(final String text) {
        return LEGACY_SERIALIZER.deserialize(text);
    }

    public static TextColor asAdventure(final ChatColor color) {
        final LegacyFormat format = LegacyComponentSerializer.parseChar(color.getChar());
        if (format == null) {
            throw new IllegalStateException("Unable to parse legacy format for " + color);
        }
        return format.color();
    }
}
