package com.earth2me.essentials.utils;

import net.ess3.api.IEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AdventureUtil {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER;
    private static final MiniMessage MINI_MESSAGE_INSTANCE;
    private static IEssentials ess;
    public static final String MINI_MESSAGE_PREFIX = "MM||";
    private static final Pattern NAMED_PATTERN = Pattern.compile(ChatColor.COLOR_CHAR + "[0-9a-fk-orA-FK-OR]");
    private static final Pattern HEX_PATTERN = Pattern.compile(ChatColor.COLOR_CHAR + "x((?:" + ChatColor.COLOR_CHAR + "[0-9a-fA-F]){6})");
    private static final String LOOKUP = "0123456789abcdefklmnor";
    private static final String[] MINI_TAGS = new String[] {"black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "white", "obf", "b", "st", "u", "i", "reset"};
    private static final NamedTextColor[] COLORS = new NamedTextColor[] {NamedTextColor.BLACK, NamedTextColor.DARK_BLUE, NamedTextColor.DARK_GREEN, NamedTextColor.DARK_AQUA, NamedTextColor.DARK_RED, NamedTextColor.DARK_PURPLE, NamedTextColor.GOLD, NamedTextColor.GRAY, NamedTextColor.DARK_GRAY, NamedTextColor.BLUE, NamedTextColor.GREEN, NamedTextColor.AQUA, NamedTextColor.RED, NamedTextColor.LIGHT_PURPLE, NamedTextColor.YELLOW, NamedTextColor.WHITE};

    static {
        final LegacyComponentSerializer.Builder builder = LegacyComponentSerializer.builder().flattener(ComponentFlattener.basic()).useUnusualXRepeatedCharacterHexFormat();
        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_1_R01)) {
            builder.hexColors();
        }
        LEGACY_SERIALIZER = builder.build();

        MINI_MESSAGE_INSTANCE = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolvers(TagResolver.standard())
                        .resolver(TagResolver.resolver("primary", supplyTag(true)))
                        .resolver(TagResolver.resolver("secondary", supplyTag(false)))
                        .build())
                .build();

    }

    private AdventureUtil() {
    }

    public static void setEss(final IEssentials ess) {
        AdventureUtil.ess = ess;
    }

    public static MiniMessage miniMessage() {
        return MINI_MESSAGE_INSTANCE;
    }

    public static Component deserializeLegacy(final String text) {
        return LEGACY_SERIALIZER.deserialize(text);
    }

    public static String minifyLegacy(String text) {
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            final String code = matcher.group(1).replace(String.valueOf(ChatColor.COLOR_CHAR), "");
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

    public static NamedTextColor fromChar(final char c) {
        final int index = LOOKUP.indexOf(c);
        if (index == -1 || index > 15) {
            return null;
        }
        return COLORS[index];
    }

    public static Tag supplyTag(final boolean primary) {
        if (primary) {
            return ess != null ? ess.getSettings().getPrimaryColor() : Tag.styling(NamedTextColor.GOLD);
        }
        return ess != null ? ess.getSettings().getSecondaryColor() : Tag.styling(NamedTextColor.RED);
    }
}
