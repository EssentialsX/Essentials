package net.essentialsx.api.v2.services.discord;

/**
 * Represents an argument type to be shown on the discord client.
 */
public enum InteractionCommandArgumentType {
    STRING(3),
    INTEGER(4),
    BOOLEAN(5),
    USER(6),
    CHANNEL(7);

    private final int id;
    InteractionCommandArgumentType(int id) {
        this.id = id;
    }

    /**
     * Gets the internal discord ID for this argument type.
     * @return the internal discord id.
     */
    public int getId() {
        return id;
    }
}
