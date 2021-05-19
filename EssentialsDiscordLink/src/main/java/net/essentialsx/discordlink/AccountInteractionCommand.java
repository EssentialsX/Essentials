package net.essentialsx.discordlink;

import com.google.common.collect.ImmutableList;
import net.essentialsx.api.v2.services.discord.InteractionCommand;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgument;
import net.essentialsx.api.v2.services.discord.InteractionCommandArgumentType;
import net.essentialsx.api.v2.services.discord.InteractionEvent;

import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class AccountInteractionCommand implements InteractionCommand {
    private final List<InteractionCommandArgument> arguments;
    private final AccountStorage accounts;

    public AccountInteractionCommand(AccountStorage accounts) {
        this.arguments = ImmutableList.of(new InteractionCommandArgument("user", tl("discordCommandAccountArgumentUser"), InteractionCommandArgumentType.USER, false));
        this.accounts = accounts;
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
        return "account";
    }

    @Override
    public String getDescription() {
        return tl("discordCommandAccountDescription");
    }

    @Override
    public List<InteractionCommandArgument> getArguments() {
        return arguments;
    }

    @Override
    public void onCommand(InteractionEvent event) {
        final String accountId = event.getMember().getId();
        event.reply("uuid " + accounts.getUUID(accountId));
    }
}
