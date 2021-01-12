package net.essentialsx.discord.interactions.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class InteractionCommand {
    private final String name;
    private final String description;
    private final List<InteractionCommandArgument> arguments = new ArrayList<>();

    public InteractionCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void addArgument(InteractionCommandArgument argument) {
        arguments.add(argument);
    }

    public JsonObject serialize() {
        final JsonObject cmdObject = new JsonObject();
        cmdObject.addProperty("name", name);
        cmdObject.addProperty("description", description);
        final JsonArray optionsArray = new JsonArray();
        for (InteractionCommandArgument argument : arguments) {
            optionsArray.add(argument.serialize());
        }
        cmdObject.add("options", optionsArray);
        return cmdObject;
    }
}
