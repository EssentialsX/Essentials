package net.essentialsx.api.v2.services.discord;

/**
 * A class which provides information about what triggered an interaction event.
 */
public interface InteractionEvent {
    /**
     * Creates/Appends the initial response message.
     * @param message The message to append.
     */
    void reply(String message);

    /**
     * Gets the member which caused this event.
     * @return the member which caused the event.
     */
    InteractionMember getMember();

    /**
     * Helper method to get the String representation of the argument by the given key or null if none by that key is present.
     * @param key The key of the argument to lookup.
     * @return the string value or null.
     */
    String getStringArgument(String key);

    /**
     * Helper method to get the Long representation of the argument by the given key or null if none by that key is present.
     * @param key The key of the argument to lookup.
     * @return the long value or null
     */
    Long getIntegerArgument(String key);

    /**
     * Helper method to get the Boolean representation of the argument by the given key or null if none by that key is present.
     * @param key The key of the argument to lookup.
     * @return the boolean value or null
     */
    Boolean getBooleanArgument(String key);

    /**
     * Helper method to get the user representation of the argument by the given key or null if none by that key is present.
     * @param key The key of the argument to lookup.
     * @return the user value or null
     */
    InteractionMember getUserArgument(String key);

    /**
     * Helper method to get the channel representation of the argument by the given key or null if none by that key is present.
     * @param key The key of the argument to lookup.
     * @return the channel value or null
     */
    InteractionChannel getChannelArgument(String key);

    /**
     * Gets the channel id where this interaction occurred.
     * @return the channel id.
     */
    String getChannelId();
}
