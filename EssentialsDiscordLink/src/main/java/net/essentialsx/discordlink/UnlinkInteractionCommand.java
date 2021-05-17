package net.essentialsx.discordlink;

import net.essentialsx.api.v2.services.discord.InteractionCommand;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgument;
import net.essentialsx.api.v2.services.discord.InteractionEvent;

import java.util.List;

import static com.earth2me.essentials.I18n.tl;

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
        return tl("discordCommandUnlinkDescription");
    }

    @Override
    public List<InteractionCommandArgument> getArguments() {
        return null;
    }
}
