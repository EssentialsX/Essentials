package net.essentialsx.discord.interactions;

import net.essentialsx.api.v2.services.discord.InteractionCommand;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgument;
import net.essentialsx.discord.JDADiscordService;

import java.util.ArrayList;
import java.util.List;

public abstract class InteractionCommandImpl implements InteractionCommand {
    protected final JDADiscordService jda;
    private final String name;
    private final String description;
    private final List<InteractionCommandArgument> arguments = new ArrayList<>();

    public InteractionCommandImpl(JDADiscordService jda, String name, String description) {
        this.jda = jda;
        this.name = name;
        this.description = description;
    }

    @Override
    public final boolean isDisabled() {
        return !jda.getSettings().isCommandEnabled(name);
    }

    @Override
    public final boolean isEphemeral() {
        return jda.getSettings().isCommandEphemeral(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<InteractionCommandArgument> getArguments() {
        return arguments;
    }

    public List<String> getAdminSnowflakes() {
        return jda.getSettings().getCommandAdminSnowflakes(name);
    }

    public void addArgument(InteractionCommandArgument argument) {
        arguments.add(argument);
    }
}
