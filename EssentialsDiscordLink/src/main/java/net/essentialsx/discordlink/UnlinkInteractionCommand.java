package net.essentialsx.discordlink;

import net.essentialsx.api.v2.services.discord.InteractionCommand;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgument;
import net.essentialsx.api.v2.services.discord.InteractionEvent;

import java.util.List;

public class UnlinkInteractionCommand implements InteractionCommand {
    @Override
    public void onCommand(InteractionEvent event) {

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
        return "unlink";
    }

    @Override
    public String getDescription() {
        return "Unlinks any associated minecraft account from this discord account.";
    }

    @Override
    public List<InteractionCommandArgument> getArguments() {
        return null;
    }
}
