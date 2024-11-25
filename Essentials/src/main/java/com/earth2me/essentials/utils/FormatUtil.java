package com.earth2me.essentials.utils;

import net.ess3.api.IUser;
import org.bukkit.ChatColor;
import org.bukkit.Color;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FormatUtil {
    public static final Pattern IPPATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final Set<ChatColor> COLORS = EnumSet.of(ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE);
    private static final Set<ChatColor> FORMATS = EnumSet.of(ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.ITALIC, ChatColor.RESET);
    private static final Set<ChatColor> MAGIC = EnumSet.of(ChatColor.MAGIC);
    //Vanilla patterns used to strip existing formats
    private static final Pattern STRIP_ALL_PATTERN = Pattern.compile(ChatColor.COLOR_CHAR + "+([0-9a-fk-orA-FK-OR])");
    //Pattern used to strip md_5 legacy hex hack
    private static final Pattern STRIP_RGB_PATTERN = Pattern.compile(ChatColor.COLOR_CHAR + "x((?:" + ChatColor.COLOR_CHAR + "[0-9a-fA-F]){6})");
    //Essentials '&' convention colour codes
    private static final Pattern REPLACE_ALL_PATTERN = Pattern.compile("(&)?&([0-9a-fk-orA-FK-OR])");
    private static final Pattern REPLACE_ALL_RGB_PATTERN = Pattern.compile("(&)?&#([0-9a-fA-F]{6})");
    //Used to prepare xmpp output
    private static final Pattern LOGCOLOR_PATTERN = Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})?)?[m|K]");
    private static final Pattern URL_PATTERN = Pattern.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-zA-Z]{2,3}(?:/\\S+)?)");
    //Used to strip ANSI control codes from console
    private static final Pattern ANSI_CONTROL_PATTERN = Pattern.compile("[\\x1B\\x9B][\\[\\]()#;?]*(?:(?:(?:;[-a-zA-Z\\d/#&.:=?%@~_]+)*|[a-zA-Z\\d]+(?:;[-a-zA-Z\\d/#&.:=?%@~_]*)*)?\\x07|(?:\\d{1,4}(?:;\\d{0,4})*)?[\\dA-PR-TZcf-nq-uy=><~])");
    private static final Pattern PAPER_CONTROL_PATTERN = Pattern.compile("(?i)" + (char) 0x7f + "[0-9A-FK-ORX]");

    private FormatUtil() {
    }

    //This method is used to simply strip the native minecraft colour codes
    public static String stripFormat(final String input) {
        if (input == null) {
            return null;
        }
        return ChatColor.stripColor(input);
    }

    public static String stripMiniFormat(final String input) {
        if (input == null) {
            return null;
        }
        return AdventureUtil.miniMessage().stripTags(input);
    }

    //This method is used to simply strip the & convention colour codes
    public static String stripEssentialsFormat(final String input) {
        if (input == null) {
            return null;
        }
        return stripColor(stripColor(input, REPLACE_ALL_PATTERN), REPLACE_ALL_RGB_PATTERN);
    }

    public static String stripAnsi(final String input) {
        if (input == null) {
            return null;
        }
        return stripColor(input, ANSI_CONTROL_PATTERN);
    }

    public static String stripPaper(final String input) {
        if (input == null) {
            return null;
        }
        return stripColor(input, PAPER_CONTROL_PATTERN);
    }

    //This is the general permission sensitive message format function, checks for urls.
    public static String formatMessage(final IUser user, final String permBase, final String input) {
        if (input == null) {
            return null;
        }
        String message = formatString(user, permBase, input);
        if (!user.isAuthorized(permBase + ".url")) {
            message = FormatUtil.blockURL(message);
        }
        return message;
    }

    //This method is used to simply replace the ess colour codes with minecraft ones, ie &c
    public static String replaceFormat(final String input) {
        if (input == null) {
            return null;
        }
        return replaceColor(input, EnumSet.allOf(ChatColor.class), true);
    }

    static String replaceColor(final String input, final Set<ChatColor> supported, final boolean rgb) {
        final StringBuffer legacyBuilder = new StringBuffer();
        final Matcher legacyMatcher = REPLACE_ALL_PATTERN.matcher(input);
        legacyLoop:
        while (legacyMatcher.find()) {
            final boolean isEscaped = legacyMatcher.group(1) != null;
            if (!isEscaped) {
                final char code = legacyMatcher.group(2).toLowerCase(Locale.ROOT).charAt(0);
                for (final ChatColor color : supported) {
                    if (color.getChar() == code) {
                        legacyMatcher.appendReplacement(legacyBuilder, ChatColor.COLOR_CHAR + "$2");
                        continue legacyLoop;
                    }
                }
            }
            // Don't change & to section sign (or replace two &'s with one)
            legacyMatcher.appendReplacement(legacyBuilder, "&$2");
        }
        legacyMatcher.appendTail(legacyBuilder);

        if (rgb) {
            final StringBuffer rgbBuilder = new StringBuffer();
            final Matcher rgbMatcher = REPLACE_ALL_RGB_PATTERN.matcher(legacyBuilder.toString());
            while (rgbMatcher.find()) {
                final boolean isEscaped = rgbMatcher.group(1) != null;
                if (!isEscaped) {
                    try {
                        final String hexCode = rgbMatcher.group(2);
                        rgbMatcher.appendReplacement(rgbBuilder, parseHexColor(hexCode));
                        continue;
                    } catch (final NumberFormatException ignored) {
                    }
                }
                rgbMatcher.appendReplacement(rgbBuilder, "&#$2");
            }
            rgbMatcher.appendTail(rgbBuilder);
            return rgbBuilder.toString();
        }
        return legacyBuilder.toString();
    }

    /**
     * @throws NumberFormatException If the provided hex color code is invalid or if version is lower than 1.16.
     */
    public static String parseHexColor(String hexColor) throws NumberFormatException {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_16_1_R01)) {
            throw new NumberFormatException("Cannot use RGB colors in versions < 1.16");
        }

        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1); //fuck you im reassigning this.
        }
        if (hexColor.length() != 6) {
            throw new NumberFormatException("Invalid hex length");
        }

        //noinspection ResultOfMethodCallIgnored
        Color.fromRGB(Integer.decode("#" + hexColor));
        final StringBuilder assembledColorCode = new StringBuilder();
        assembledColorCode.append(ChatColor.COLOR_CHAR + "x");
        for (final char curChar : hexColor.toCharArray()) {
            assembledColorCode.append(ChatColor.COLOR_CHAR).append(curChar);
        }
        return assembledColorCode.toString();
    }

    static String stripColor(final String input, final Set<ChatColor> strip) {
        final StringBuffer builder = new StringBuffer();
        final Matcher matcher = STRIP_ALL_PATTERN.matcher(input);
        searchLoop:
        while (matcher.find()) {
            final char code = matcher.group(1).toLowerCase(Locale.ROOT).charAt(0);
            for (final ChatColor color : strip) {
                if (color.getChar() == code) {
                    matcher.appendReplacement(builder, "");
                    continue searchLoop;
                }
            }
            // Don't replace
            matcher.appendReplacement(builder, "$0");
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    public static String unformatString(final String message) {
        if (message == null) {
            return null;
        }
        return unformatString(message, EnumSet.allOf(ChatColor.class), true);
    }

    public static String unformatString(final IUser user, final String permBase, final String message) {
        if (message == null) {
            return null;
        }
        return unformatString(message, getSupported(user, permBase), user.isAuthorized(permBase + ".rgb"));
    }

    public static String unformatString(String message, final EnumSet<ChatColor> supported, boolean rgb) {
        if (message == null) {
            return null;
        }

        final StringBuffer rgbBuilder = new StringBuffer();
        final Matcher rgbMatcher = STRIP_RGB_PATTERN.matcher(message);
        while (rgbMatcher.find()) {
            final String code = rgbMatcher.group(1).replace(String.valueOf(ChatColor.COLOR_CHAR), "");
            if (rgb) {
                rgbMatcher.appendReplacement(rgbBuilder, "&#" + code);
                continue;
            }
            rgbMatcher.appendReplacement(rgbBuilder, "");
        }
        rgbMatcher.appendTail(rgbBuilder);
        message = rgbBuilder.toString(); // arreter de parler

        // Legacy Colors
        final StringBuffer builder = new StringBuffer();
        final Matcher matcher = STRIP_ALL_PATTERN.matcher(message);
        searchLoop:
        while (matcher.find()) {
            final char code = matcher.group(1).toLowerCase(Locale.ROOT).charAt(0);
            for (final ChatColor color : supported) {
                if (color.getChar() == code) {
                    matcher.appendReplacement(builder, "&" + code);
                    continue searchLoop;
                }
            }
            matcher.appendReplacement(builder, "");
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    //This is the general permission sensitive message format function, does not touch urls.
    public static String formatString(final IUser user, final String permBase, String message) {
        if (message == null) {
            return null;
        }
        final EnumSet<ChatColor> supported = getSupported(user, permBase);
        final EnumSet<ChatColor> strip = EnumSet.complementOf(supported);

        final boolean rgb = user.isAuthorized(permBase + ".rgb");
        if (!strip.isEmpty()) {
            message = stripColor(message, strip);
        }
        if (!supported.isEmpty() || rgb) {
            message = replaceColor(message, supported, rgb);
        }
        return message;
    }

    private static EnumSet<ChatColor> getSupported(final IUser user, final String permBase) {
        final EnumSet<ChatColor> supported = EnumSet.noneOf(ChatColor.class);
        if (user.isAuthorized(permBase + ".color")) {
            supported.addAll(COLORS);
        }
        if (user.isAuthorized(permBase + ".format")) {
            supported.addAll(FORMATS);
        }
        if (user.isAuthorized(permBase + ".magic")) {
            supported.addAll(MAGIC);
        }
        for (final ChatColor chatColor : ChatColor.values()) {
            String colorName = chatColor.name();
            if (chatColor == ChatColor.MAGIC) {
                // Bukkit's name doesn't match with vanilla's
                colorName = "obfuscated";
            }

            final String node = permBase + "." + colorName.toLowerCase(Locale.ROOT);
            // Only handle individual colors that are explicitly added or removed.
            if (!user.isPermissionSet(node)) {
                continue;
            }
            if (user.isAuthorized(node)) {
                supported.add(chatColor);
            } else {
                supported.remove(chatColor);
            }
        }
        return supported;
    }

    public static String stripLogColorFormat(final String input) {
        if (input == null) {
            return null;
        }
        return stripColor(input, LOGCOLOR_PATTERN);
    }

    static String stripColor(final String input, final Pattern pattern) {
        return pattern.matcher(input).replaceAll("");
    }

    public static String lastCode(final String input) {
        final int pos = input.lastIndexOf(ChatColor.COLOR_CHAR);
        if (pos == -1 || (pos + 1) == input.length()) {
            return "";
        }
        return input.substring(pos, pos + 2);
    }

    static String blockURL(final String input) {
        if (input == null) {
            return null;
        }
        String text = URL_PATTERN.matcher(input).replaceAll("$1 $2");
        while (URL_PATTERN.matcher(text).find()) {
            text = URL_PATTERN.matcher(text).replaceAll("$1 $2");
        }
        return text;
    }

    public static boolean validIP(final String ipAddress) {
        return IPPATTERN.matcher(ipAddress).matches();
    }
}
