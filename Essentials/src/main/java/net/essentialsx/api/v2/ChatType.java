package net.essentialsx.api.v2;

import java.util.Locale;

/**
 * Represents chat type for a message
 */
public enum ChatType {
    /**
     * Message is being sent to global chat as a shout
     */
    SHOUT,

    /**
     * Message is being sent to global chat as a question
     */
    QUESTION,

    /**
     * Message is being sent locally
     */
    LOCAL,

    /**
     * Message is being sent to spy channel
     */
    SPY,

    /**
     * Chat type is not determined
     *
     * <p>This type used when local/global chat features are disabled
     */
    UNKNOWN("normal"),
    ;

    private final String key;

    ChatType() {
        this.key = name().toLowerCase(Locale.ENGLISH);
    }

    ChatType(final String key) {
        this.key = key;
    }

    /**
     * @return Lowercase name of the chat type.
     */
    public String key() {
        return key;
    }
}
