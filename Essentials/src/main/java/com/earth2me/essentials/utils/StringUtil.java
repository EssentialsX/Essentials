package com.earth2me.essentials.utils;

import com.google.common.primitives.Chars;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class StringUtil {
    private static final Pattern INVALIDFILECHARS = Pattern.compile("[^a-z0-9-]");
    private static final Pattern STRICTINVALIDCHARS = Pattern.compile("[^a-z0-9]");
    @SuppressWarnings("CheckStyle")
    private static final Pattern INVALIDCHARS = Pattern.compile("[^\t\n\r\u0020-\u007E\u0085\u00A0-\uD7FF\uE000-\uFFFC]");

    private StringUtil() {
    }

    //Used to clean file names before saving to disk
    public static String sanitizeFileName(final String name) {
        return INVALIDFILECHARS.matcher(name.toLowerCase(Locale.ENGLISH)).replaceAll("_");
    }

    //Used to clean strings/names before saving as filenames/permissions
    public static String safeString(final String string) {
        return STRICTINVALIDCHARS.matcher(string.toLowerCase(Locale.ENGLISH)).replaceAll("_");
    }

    //Less restrictive string sanitizing, when not used as perm or filename
    public static String sanitizeString(final String string) {
        return INVALIDCHARS.matcher(string).replaceAll("");
    }

    public static String joinList(final Object... list) {
        return joinList(", ", list);
    }

    public static String joinList(final String seperator, final Object... list) {
        final StringBuilder buf = new StringBuilder();
        for (final Object each : list) {
            if (buf.length() > 0) {
                buf.append(seperator);
            }

            if (each instanceof Collection) {
                buf.append(joinList(seperator, ((Collection) each).toArray()));
            } else {
                try {
                    buf.append(each.toString());
                } catch (final Exception e) {
                    buf.append(each.toString());
                }
            }
        }
        return buf.toString();
    }

    public static String joinListSkip(final String seperator, final String skip, final Object... list) {
        final StringBuilder buf = new StringBuilder();
        for (final Object each : list) {
            if (each.toString().equalsIgnoreCase(skip)) {
                continue;
            }

            if (buf.length() > 0) {
                buf.append(seperator);
            }

            if (each instanceof Collection) {
                buf.append(joinListSkip(seperator, skip, ((Collection) each).toArray()));
            } else {
                try {
                    buf.append(each.toString());
                } catch (final Exception e) {
                    buf.append(each.toString());
                }
            }
        }
        return buf.toString();
    }

    public static UUID toUUID(final String input) {
        try {
            return UUID.fromString(input);
        } catch (final IllegalArgumentException ignored) {
        }

        return null;
    }

    public static String abbreviate(final String input, final int length) {
        if (input == null) return null;
        if (length < 4) throw new IllegalArgumentException("Invalid length " + length);

        if (input.length() <= length) return input;
        return input.substring(0, length - 3) + "...";
    }

    // Replacement for org.apache.commons.lang3.StringUtils.stripToNull(String)
    public static String stripToNull(final String input) {
        if (input == null) return null;

        final String result = strip(input);
        return result.isEmpty() ? null : result;
    }

    // Replacement for org.apache.commons.lang3.StringUtils.strip(String)
    public static String strip(final String input) {
        return strip(input, Character::isWhitespace);
    }

    // Replacement for org.apache.commons.lang3.StringUtils.strip(String, String)
    public static String strip(final String input, final String stripChars) {
        if (stripChars == null) return strip(input);
        final List<Character> toStrip = Chars.asList(stripChars.toCharArray());
        return strip(input, toStrip::contains);
    }

    public static String strip(final String input, Function<Character, Boolean> shouldStrip) {
        if (input == null) return null;

        int startIndex = 0;
        int endIndex = input.length();

        for (; startIndex < endIndex; startIndex++) {
            if (!shouldStrip.apply(input.charAt(startIndex))) break;
        }

        for (; endIndex > startIndex; endIndex--) {
            if (!shouldStrip.apply(input.charAt(endIndex - 1))) break;
        }

        return input.substring(startIndex, endIndex);
    }
}
