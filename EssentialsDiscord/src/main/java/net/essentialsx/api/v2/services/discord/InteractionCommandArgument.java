package net.essentialsx.api.v2.services.discord;

import com.google.gson.JsonObject;

public class InteractionCommandArgument {
    private final String name;
    private final String description;
    private final InteractionCommandArgumentType type;
    private final boolean required;
    private final boolean defaultOption;

    public InteractionCommandArgument(String name, String description, InteractionCommandArgumentType type, boolean required) {
        this(name, description, type, required, false);
    }

    public InteractionCommandArgument(String name, String description, InteractionCommandArgumentType type, boolean required, boolean defaultOption) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
        this.defaultOption = defaultOption;
    }

    public JsonObject serialize() {
        final JsonObject object = new JsonObject();
        object.addProperty("type", type.getId());
        object.addProperty("name", name);
        object.addProperty("description", description);
        object.addProperty("default", defaultOption);
        object.addProperty("required", required);
        return object;
    }
}
