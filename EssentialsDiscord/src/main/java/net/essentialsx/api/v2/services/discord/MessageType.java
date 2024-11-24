package net.essentialsx.api.v2.services.discord;

/**
 * Indicates the type of message being sent and its literal channel name used in the config.
 */
public final class MessageType {
    private final String key;
    private final boolean player;

    /**
     * Creates a {@link MessageType} which will send channels to the specified channel key.
     * <p>
     * The message type key may only contain: lowercase letters, numbers, and dashes.
     * @param key The channel key defined in the {@code message-types} section of the config.
     */
    public MessageType(final String key) {
        this(key, false);
    }

    /**
     * Internal constructor used by EssentialsX Discord
     */
    private MessageType(String key, boolean player) {
        if (!key.matches("^[a-z0-9-]*$")) {
            throw new IllegalArgumentException("Key must match \"^[a-z0-9-]*$\"");
        }
        this.key = key;
        this.player = player;
    }

    /**
     * Gets the key used in {@code message-types} section of the config.
     * @return The config key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Checks if this message type should be beholden to player-specific config settings.
     * @return true if message type should be beholden to player-specific config settings.
     */
    public boolean isPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return key;
    }

    /**
     * Default {@link MessageType MessageTypes} provided and documented by EssentialsX Discord.
     */
    public static final class DefaultTypes {
        public final static MessageType JOIN = new MessageType("join", true);
        public final static MessageType FIRST_JOIN = new MessageType("first-join", true);
        public final static MessageType LEAVE = new MessageType("leave", true);
        public final static MessageType CHAT = new MessageType("chat", true);
        public final static MessageType DEATH = new MessageType("death", true);
        public final static MessageType AFK = new MessageType("afk", true);
        public final static MessageType ADVANCEMENT = new MessageType("advancement", true);
        public final static MessageType ACTION = new MessageType("action", true);
        public final static MessageType SERVER_START = new MessageType("server-start", false);
        public final static MessageType SERVER_STOP = new MessageType("server-stop", false);
        public final static MessageType KICK = new MessageType("kick", false);
        public final static MessageType MUTE = new MessageType("mute", false);
        public final static MessageType LOCAL = new MessageType("local", true);
        public final static MessageType QUESTION = new MessageType("question", true);
        public final static MessageType SHOUT = new MessageType("shout", true);
        private final static MessageType[] VALUES = new MessageType[]{JOIN, FIRST_JOIN, LEAVE, CHAT, DEATH, AFK, ADVANCEMENT, ACTION, SERVER_START, SERVER_STOP, KICK, MUTE, LOCAL, QUESTION, SHOUT};

        /**
         * Gets an array of all the default {@link MessageType MessageTypes}.
         * @return An array of all the default {@link MessageType MessageTypes}.
         */
        public static MessageType[] values() {
            return VALUES;
        }
    }
}
