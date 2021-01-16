package net.essentialsx.discord.interactions.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.essentialsx.discord.EssentialsJDA;
import net.essentialsx.discord.util.DiscordUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class InteractionCommand {
    protected final EssentialsJDA jda;
    private final String name;
    private final String description;
    private final List<InteractionCommandArgument> arguments = new ArrayList<>();

    public InteractionCommand(EssentialsJDA jda, String name, String description) {
        this.jda = jda;
        this.name = name;
        this.description = description;
    }

    public final boolean isEnabled() {
        return jda.getSettings().isCommandEnabled(name);
    }

    public final boolean isConsume() {
        return jda.getSettings().isCommandConsumed(name);
    }

    public final boolean isEphemeral() {
        return jda.getSettings().isCommandEphemeral(name);
    }

    public final void onPreCommand(InteractionEvent event) {
        if (!DiscordUtil.hasRoles(event.getMember(), jda.getSettings().getCommandSnowflakes(name))) {
            event.reply("You lack the sufficient permission to execute that command!");
            return;
        }
        onCommand(event);
    }

    public abstract void onCommand(InteractionEvent event);

    public String getName() {
        return name;
    }

    public List<String> getAdminSnowflakes() {
        return jda.getSettings().getCommandAdminSnowflakes(name);
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
