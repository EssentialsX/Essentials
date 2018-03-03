package com.neximation.essentials.commands;

import com.neximation.essentials.CommandSource;
import com.neximation.essentials.User;
import org.bukkit.Server;

import static com.neximation.essentials.I18n.tl;


public class Commandsocialspy extends EssentialsToggleCommand {
    public Commandsocialspy() {
        super("socialspy", "essentials.socialspy.others");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        toggleOtherPlayers(server, sender, args);
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        handleToggleWithArgs(server, user, args);
    }

    @Override
    void togglePlayer(CommandSource sender, User user, Boolean enabled) throws NotEnoughArgumentsException {
        if (enabled == null) {
            enabled = !user.isSocialSpyEnabled();
        }

        user.setSocialSpyEnabled(enabled);


        user.sendMessage(tl("socialSpy", user.getDisplayName(), enabled ? tl("enabled") : tl("disabled")));
        if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
            sender.sendMessage(tl("socialSpy", user.getDisplayName(), enabled ? tl("enabled") : tl("disabled")));
        }
    }
}
