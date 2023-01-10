package net.essentialsx.api.v2.services.discord;

/**
 * Represents a triggered interaction event.
 */
public interface InteractionEvent {
    /**
     * Appends the given string to the initial response message and creates one if it doesn't exist.
     * @param message The message to append.
     */
    void reply(String message);

    /**
     * Gets the member which caused this event.
     * @return the member which caused the event.
     */
    InteractionMember getMember();

    /**
     * Get the value of the argument matching the given key represented as a String, or null if no argument by that name is present. 
     * @param key The key of the argument to lookup.
     * @return the string value or null.
     */
    String getStringArgument(String key);

    /**
     * Get the Long representation of the argument by the given key or null if none by that key is present.
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
     * Helper method to get the role representation of the argument by the given key or null if none by that key is present.
     * @param key The key of the argument to lookup.
     * @return the role value or null
     */
    InteractionRole getRoleArgument(String key);

    /**
     * Gets the channel ID where this interaction occurred.
     * @return the channel ID.
     */
    String getChannelId();
}
