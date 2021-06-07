package net.essentialsx.api.v2.services.discord;

/**
 * Represents an argument for a command to be shown to the discord client.
 */
public class InteractionCommandArgument {
    private final String name;
    private final String description;
    private final InteractionCommandArgumentType type;
    private final boolean required;

    /**
     * Builds a command argument.
     * @param name        The name of the argument to be shown to the discord client.
     * @param description A brief description of the argument to be shown to the discord client.
     * @param type        The type of argument.
     * @param required    Whether or not the argument is required in order to send the command in the discord client.
     */
    public InteractionCommandArgument(String name, String description, InteractionCommandArgumentType type, boolean required) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
    }

    /**
     * Gets the name of this argument.
     * @return the name of the argument.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this argument.
     * @return the description of the argument.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the type of this argument.
     * @return the argument type.
     */
    public InteractionCommandArgumentType getType() {
        return type;
    }

    /**
     * Whether or not this argument is required or not.
     * @return true if the argument is required.
     */
    public boolean isRequired() {
        return required;
    }
}
