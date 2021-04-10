package net.essentialsx.api.v2.services.discord;

import com.google.gson.JsonObject;

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
     * Serializes this argument into its JSON representation.
     * @return a {@link JsonObject} of this argument.
     */
    public JsonObject serialize() {
        final JsonObject object = new JsonObject();
        object.addProperty("type", type.getId());
        object.addProperty("name", name);
        object.addProperty("description", description);
        object.addProperty("required", required);
        return object;
    }
}
