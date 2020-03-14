package com.earth2me.essentials.utils;

import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;


public class StringUtil {
    private static final Pattern INVALIDFILECHARS = Pattern.compile("[^a-z0-9-]");
    private static final Pattern STRICTINVALIDCHARS = Pattern.compile("[^a-z0-9]");
    private static final Pattern INVALIDCHARS = Pattern.compile("[^\t\n\r\u0020-\u007E\u0085\u00A0-\uD7FF\uE000-\uFFFC]");

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

    public static String joinList(Object... list) {
        return joinList(", ", list);
    }

    public static String joinList(String seperator, Object... list) {
        StringBuilder buf = new StringBuilder();
        for (Object each : list) {
            if (buf.length() > 0) {
                buf.append(seperator);
            }

            if (each instanceof Collection) {
                buf.append(joinList(seperator, ((Collection) each).toArray()));
            } else {
                try {
                    buf.append(each.toString());
                } catch (Exception e) {
                    buf.append(each.toString());
                }
            }
        }
        return buf.toString();
    }

    public static String joinListSkip(String seperator, String skip, Object... list) {
        StringBuilder buf = new StringBuilder();
        for (Object each : list) {
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
                } catch (Exception e) {
                    buf.append(each.toString());
                }
            }
        }
        return buf.toString();
    }

    public static UUID toUUID(String input) {
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException ignored) {}

        return null;
    }

    private StringUtil() {
    }
}
