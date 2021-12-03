package net.essentialsx.discord.util;

import java.text.MessageFormat;

public final class MessageUtil {
    private MessageUtil() {
    }

    /**
     * Sanitizes text to be sent to Discord, escaping any Markdown syntax.
     */
    public static String sanitizeDiscordMarkdown(String message) {
        if (message == null) {
            return null;
        }

        return message.replace("*", "\\*")
                .replace("~", "\\~")
                .replace("_", "\\_")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("|", "\\|");
    }

    /**
     * Shortcut method allowing for use of varags in {@link MessageFormat} instances
     */
    public static String formatMessage(MessageFormat format, Object... args) {
        return format.format(args);
    }
}
