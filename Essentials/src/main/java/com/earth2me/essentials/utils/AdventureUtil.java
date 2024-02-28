package com.earth2me.essentials.utils;

import net.ess3.api.IEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.CharacterAndFormat;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.Reset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class AdventureUtil {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER;
    private static final MiniMessage MINI_MESSAGE_NO_TAGS;
    private static final String LOOKUP = "0123456789abcdefklmnor";
    private static final NamedTextColor[] COLORS = new NamedTextColor[]{NamedTextColor.BLACK, NamedTextColor.DARK_BLUE, NamedTextColor.DARK_GREEN, NamedTextColor.DARK_AQUA, NamedTextColor.DARK_RED, NamedTextColor.DARK_PURPLE, NamedTextColor.GOLD, NamedTextColor.GRAY, NamedTextColor.DARK_GRAY, NamedTextColor.BLUE, NamedTextColor.GREEN, NamedTextColor.AQUA, NamedTextColor.RED, NamedTextColor.LIGHT_PURPLE, NamedTextColor.YELLOW, NamedTextColor.WHITE};
    private static IEssentials ess;
    private static MiniMessage miniMessageInstance;

    static {
        final List<CharacterAndFormat> formats = new ArrayList<>();
        formats.addAll(CharacterAndFormat.defaults());
        formats.addAll(Arrays.asList(
                CharacterAndFormat.characterAndFormat('A', NamedTextColor.GREEN),
                CharacterAndFormat.characterAndFormat('B', NamedTextColor.AQUA),
                CharacterAndFormat.characterAndFormat('C', NamedTextColor.RED),
                CharacterAndFormat.characterAndFormat('D', NamedTextColor.LIGHT_PURPLE),
                CharacterAndFormat.characterAndFormat('E', NamedTextColor.YELLOW),
                CharacterAndFormat.characterAndFormat('F', NamedTextColor.WHITE),
                CharacterAndFormat.characterAndFormat('K', TextDecoration.OBFUSCATED),
                CharacterAndFormat.characterAndFormat('L', TextDecoration.BOLD),
                CharacterAndFormat.characterAndFormat('M', TextDecoration.STRIKETHROUGH),
                CharacterAndFormat.characterAndFormat('N', TextDecoration.UNDERLINED),
                CharacterAndFormat.characterAndFormat('O', TextDecoration.ITALIC),
                CharacterAndFormat.characterAndFormat('R', Reset.INSTANCE)
        ));
        final LegacyComponentSerializer.Builder builder = LegacyComponentSerializer.builder()
                .flattener(ComponentFlattener.basic())
                .formats(formats)
                .useUnusualXRepeatedCharacterHexFormat();
        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_1_R01)) {
            builder.hexColors();
        }
        LEGACY_SERIALIZER = builder.build();

        MINI_MESSAGE_NO_TAGS = MiniMessage.miniMessage();

        miniMessageInstance = createMiniMessageInstance();
    }

    private AdventureUtil() {
    }

    public static void setEss(final IEssentials ess) {
        AdventureUtil.ess = ess;
        miniMessageInstance = createMiniMessageInstance();
    }

    private static MiniMessage createMiniMessageInstance() {
        return MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolvers(TagResolver.standard())
                        .resolver(TagResolver.resolver("primary", supplyTag(true)))
                        .resolver(TagResolver.resolver("secondary", supplyTag(false)))
                        .build())
                .build();
    }

    public static MiniMessage miniMessage() {
        return miniMessageInstance;
    }

    /**
     * Converts a section sign legacy string to an adventure component.
     */
    public static Component legacyToAdventure(final String text) {
        return LEGACY_SERIALIZER.deserialize(text);
    }

    /**
     * Converts an adventure component to a section sign legacy string.
     */
    public static String adventureToLegacy(final Component component) {
        return LEGACY_SERIALIZER.serialize(component);
    }

    /**
     * Converts a MiniMessage string to a section sign legacy string.
     */
    public static String miniToLegacy(final String format) {
        return adventureToLegacy(miniMessage().deserialize(format));
    }

    /**
     * Converts a section sign legacy string to a MiniMessage string.
     */
    public static String legacyToMini(String text) {
        return legacyToMini(text, false);
    }

    /**
     * Converts a section sign legacy string to a MiniMessage string.
     *
     * @param useCustomTags true if gold and red colors should use primary and secondary tags instead.
     */
    public static String legacyToMini(String text, boolean useCustomTags) {
        final Component deserializedText = LEGACY_SERIALIZER.deserialize(text);
        if (useCustomTags) {
            return miniMessageInstance.serialize(deserializedText);
        } else {
            return MINI_MESSAGE_NO_TAGS.serialize(deserializedText);
        }
    }

    /**
     * Get the {@link NamedTextColor} from its associated section sign char.
     */
    public static NamedTextColor fromChar(final char c) {
        final int index = LOOKUP.indexOf(c);
        if (index == -1 || index > 15) {
            return null;
        }
        return COLORS[index];
    }

    /**
     * Convenience method for submodules to escape MiniMessage tags.
     */
    public static String escapeTags(final String input) {
        return miniMessage().escapeTags(input);
    }

    /**
     * Parameters for a translation message are not parsed for MiniMessage by default to avoid injection. If you want
     * a parameter to be parsed for MiniMessage you must wrap it in a ParsedPlaceholder by using this method.
     */
    public static ParsedPlaceholder parsed(final String literal) {
        return new ParsedPlaceholder(literal);
    }

    private static Tag supplyTag(final boolean primary) {
        if (primary) {
            return ess != null ? ess.getSettings().getPrimaryColor() : Tag.styling(NamedTextColor.GOLD);
        }
        return ess != null ? ess.getSettings().getSecondaryColor() : Tag.styling(NamedTextColor.RED);
    }

    public static class ParsedPlaceholder {
        private final String value;

        protected ParsedPlaceholder(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
