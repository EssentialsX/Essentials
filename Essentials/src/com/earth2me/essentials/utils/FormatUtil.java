package com.earth2me.essentials.utils;

import net.ess3.api.IUser;
import org.bukkit.ChatColor;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FormatUtil {
    private static final Set<ChatColor> COLORS = EnumSet.of(ChatColor.BLACK, ChatColor.DARK_BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED, ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.BLUE, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE);
    private static final Set<ChatColor> FORMATS = EnumSet.of(ChatColor.BOLD, ChatColor.STRIKETHROUGH, ChatColor.UNDERLINE, ChatColor.ITALIC, ChatColor.RESET);
    private static final Set<ChatColor> MAGIC = EnumSet.of(ChatColor.MAGIC);

    //Vanilla patterns used to strip existing formats
    private static final Pattern STRIP_ALL_PATTERN = Pattern.compile("\u00a7+([0-9a-fk-orA-FK-OR])");
    //Essentials '&' convention colour codes
    private static final Pattern REPLACE_ALL_PATTERN = Pattern.compile("(&)?&([0-9a-fk-orA-FK-OR])");
    //Used to prepare xmpp output
    private static final Pattern LOGCOLOR_PATTERN = Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})?)?[m|K]");
    private static final Pattern URL_PATTERN = Pattern.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-zA-Z]{2,3}(?:/\\S+)?)");
    public static final Pattern IPPATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    //This method is used to simply strip the native minecraft colour codes
    public static String stripFormat(final String input) {
        if (input == null) {
            return null;
        }
        return ChatColor.stripColor(input);
    }

    //This method is used to simply strip the & convention colour codes
    public static String stripEssentialsFormat(final String input) {
        if (input == null) {
            return null;
        }
        return stripColor(input, REPLACE_ALL_PATTERN);
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
        return replaceColor(input, EnumSet.allOf(ChatColor.class));
    }

    static String replaceColor(final String input, final Set<ChatColor> supported) {
        StringBuffer builder = new StringBuffer();
        Matcher matcher = REPLACE_ALL_PATTERN.matcher(input);
        searchLoop: while (matcher.find()) {
            boolean isEscaped = (matcher.group(1) != null);
            if (!isEscaped) {
                char code = matcher.group(2).toLowerCase(Locale.ROOT).charAt(0);
                for (ChatColor color : supported) {
                    if (color.getChar() == code) {
                        matcher.appendReplacement(builder, "\u00a7$2");
                        continue searchLoop;
                    }
                }
            }
            // Don't change & to section sign (or replace two &'s with one)
            matcher.appendReplacement(builder, "&$2");
        }
        matcher.appendTail(builder);
        return builder.toString();
    }

    static String stripColor(final String input, final Set<ChatColor> strip) {
        StringBuffer builder = new StringBuffer();
        Matcher matcher = STRIP_ALL_PATTERN.matcher(input);
        searchLoop: while (matcher.find()) {
            char code = matcher.group(1).toLowerCase(Locale.ROOT).charAt(0);
            for (ChatColor color : strip) {
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

    //This is the general permission sensitive message format function, does not touch urls.
    public static String formatString(final IUser user, final String permBase, String message) {
        if (message == null) {
            return null;
        }
        EnumSet<ChatColor> supported = EnumSet.noneOf(ChatColor.class);
        if (user.isAuthorized(permBase + ".color")) {
            supported.addAll(COLORS);
        }
        if (user.isAuthorized(permBase + ".format")) {
            supported.addAll(FORMATS);
        }
        if (user.isAuthorized(permBase + ".magic")) {
            supported.addAll(MAGIC);
        }
        for (ChatColor chatColor : ChatColor.values()) {
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
        EnumSet<ChatColor> strip = EnumSet.complementOf(supported);

        if (!supported.isEmpty()) {
            message = replaceColor(message, supported);
        }
        if (!strip.isEmpty()) {
            message = stripColor(message, strip);
        }
        return message;
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
        int pos = input.lastIndexOf('\u00a7');
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

    public static boolean validIP(String ipAddress) {
        return IPPATTERN.matcher(ipAddress).matches();
    }
}
