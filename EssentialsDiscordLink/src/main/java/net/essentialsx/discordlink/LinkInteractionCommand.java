package net.essentialsx.discordlink;

import com.google.common.collect.ImmutableList;
import net.essentialsx.api.v2.services.discord.InteractionCommand;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgument;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgumentType;
import net.essentialsx.api.v2.services.discord.InteractionEvent;

import java.util.List;

public class LinkInteractionCommand implements InteractionCommand {
    private final List<InteractionCommandArgument> arguments;

    public LinkInteractionCommand() {
        this.arguments = ImmutableList.of(new InteractionCommandArgument("code", "The code associated with your minecraft account as provided in game", InteractionCommandArgumentType.STRING, true));
    }

    @Override
    public void onCommand(InteractionEvent event) {
        event.reply("yo");
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    @Override
    public String getName() {
        return "link";
    }

    @Override
    public String getDescription() {
        return "Links your discord account with a minecraft account.";
    }

    @Override
    public List<InteractionCommandArgument> getArguments() {
        return arguments;
    }
}

