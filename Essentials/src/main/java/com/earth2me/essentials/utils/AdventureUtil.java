package com.earth2me.essentials.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AdventureUtil {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER;
    public static final String MINI_MESSAGE_PREFIX = "MM||";
    public static final char KEZZ_MAGIC_CHAR = 0x7f;
    private static final Pattern NAMED_PATTERN = Pattern.compile(KEZZ_MAGIC_CHAR + "[0-9a-fk-orA-FK-OR]");
    private static final Pattern HEX_PATTERN = Pattern.compile(KEZZ_MAGIC_CHAR + "x((?:" + KEZZ_MAGIC_CHAR + "[0-9a-fA-F]){6})");
    private static final String LOOKUP = "0123456789abcdefklmnor";
    private static final String[] MINI_TAGS = new String[] {"black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "white", "obf", "b", "st", "u", "i", "reset"};

    static {
        final LegacyComponentSerializer.Builder builder = LegacyComponentSerializer.builder().flattener(ComponentFlattener.basic()).useUnusualXRepeatedCharacterHexFormat();
        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_1_R01)) {
            builder.hexColors();
        }
        LEGACY_SERIALIZER = builder.build();
    }

    private AdventureUtil() {
    }

    public static Component deserializeLegacy(final String text) {
        return LEGACY_SERIALIZER.deserialize(text);
    }

    public static String minifyLegacy(String text) {
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            final String code = matcher.group(1).replace(String.valueOf(KEZZ_MAGIC_CHAR), "");
            matcher.appendReplacement(buffer, "<#" + code + ">");
        }
        matcher.appendTail(buffer);

        matcher = NAMED_PATTERN.matcher(buffer.toString());
        buffer = new StringBuffer();
        while (matcher.find()) {
            final int format = LOOKUP.indexOf(Character.toLowerCase(matcher.group().charAt(1)));
            if (format != -1) {
                matcher.appendReplacement(buffer, "<" + MINI_TAGS[format] + ">");
            }
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }
}
