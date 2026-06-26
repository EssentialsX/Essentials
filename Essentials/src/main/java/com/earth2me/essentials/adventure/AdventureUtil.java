package com.earth2me.essentials.adventure;

import net.kyori.adventure.text.format.NamedTextColor;

public final class AdventureUtil {
    private static final String LOOKUP = "0123456789abcdefklmnor";
    private static final NamedTextColor[] COLORS = new NamedTextColor[]{NamedTextColor.BLACK, NamedTextColor.DARK_BLUE, NamedTextColor.DARK_GREEN, NamedTextColor.DARK_AQUA, NamedTextColor.DARK_RED, NamedTextColor.DARK_PURPLE, NamedTextColor.GOLD, NamedTextColor.GRAY, NamedTextColor.DARK_GRAY, NamedTextColor.BLUE, NamedTextColor.GREEN, NamedTextColor.AQUA, NamedTextColor.RED, NamedTextColor.LIGHT_PURPLE, NamedTextColor.YELLOW, NamedTextColor.WHITE};

    private static AdventureFacet ADVENTURE_FACET_INSTANCE = null;

    private AdventureUtil() {
    }

    public static void setAdventureFacet(AdventureFacet adventureFacetInstance) {
        ADVENTURE_FACET_INSTANCE = adventureFacetInstance;
    }

    public static AdventureFacet getAdventureFacet() {
        return ADVENTURE_FACET_INSTANCE;
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

    public static String fromCharName(final char c) {
        final NamedTextColor namedTextColor = fromChar(c);
        if (namedTextColor == null) {
            return null;
        }

        return namedTextColor.toString();
    }

    /**
     * Parameters for a translation message are not parsed for MiniMessage by default to avoid injection. If you want
     * a parameter to be parsed for MiniMessage you must wrap it in a ParsedPlaceholder by using this method.
     */
    public static ParsedPlaceholder parsed(final String literal) {
        return new ParsedPlaceholder(literal);
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
