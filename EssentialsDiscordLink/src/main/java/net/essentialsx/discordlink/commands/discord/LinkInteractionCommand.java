package net.essentialsx.discordlink.commands.discord;

import com.google.common.collect.ImmutableList;
import net.essentialsx.api.v2.events.discordlink.DiscordLinkStatusChangeEvent;
import net.essentialsx.api.v2.services.discord.InteractionCommand;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgument;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgumentType;
import net.essentialsx.api.v2.services.discord.InteractionEvent;
import net.essentialsx.discordlink.AccountLinkManager;

import java.util.List;
import java.util.UUID;

import static com.earth2me.essentials.I18n.tlLiteral;

public class LinkInteractionCommand implements InteractionCommand {
    private final List<InteractionCommandArgument> arguments;
    private final AccountLinkManager accounts;

    public LinkInteractionCommand(final AccountLinkManager accounts) {
        this.arguments = ImmutableList.of(new InteractionCommandArgument("code", tlLiteral("discordCommandLinkArgumentCode"), InteractionCommandArgumentType.STRING, true));
        this.accounts = accounts;
    }

    @Override
    public void onCommand(InteractionEvent event) {
        if (accounts.isLinked(event.getMember().getId())) {
            event.replyTl("discordCommandLinkHasAccount");
            return;
        }

        final UUID uuid = accounts.getPendingUUID(event.getStringArgument("code"));
        if (uuid == null) {
            event.replyTl("discordCommandLinkInvalidCode");
            return;
        }

        accounts.registerAccount(uuid, event.getMember(), DiscordLinkStatusChangeEvent.Cause.SYNC_PLAYER);
        event.replyTl("discordCommandLinkLinked");
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
        return tlLiteral("discordCommandLinkDescription");
    }

    @Override
    public List<InteractionCommandArgument> getArguments() {
        return arguments;
    }
}

