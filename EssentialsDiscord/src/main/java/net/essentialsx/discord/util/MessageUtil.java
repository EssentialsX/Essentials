package net.essentialsx.discord.util;

import java.text.MessageFormat;

public final class MessageUtil {
    private MessageUtil() {
    }

    /**
     * Sanitizes text coming from discord, regardless of the content sanitization done by JDA themselves.
     */
    public static String sanitizeDiscordText(String message) {
        return message.replace("", ""); //TODO
    }

    /**
     * Shortcut method allowing for use of varags in {@link MessageFormat} instances
     */
    public static String formatMessage(MessageFormat format, Object... args) {
        return format.format(args);
    }
}
