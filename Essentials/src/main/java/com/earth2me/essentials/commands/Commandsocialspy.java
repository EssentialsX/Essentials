package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

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
    protected void togglePlayer(final CommandSource sender, final User user, Boolean enabled) {
        if (enabled == null) {
            enabled = !user.isSocialSpyEnabled();
        }

        user.setSocialSpyEnabled(enabled);

        user.sendMessage(tl("socialSpy", user.getName(), enabled ? tl("enabled") : tl("disabled")));
        if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
            sender.sendMessage(tl("socialSpy", user.getName(), enabled ? tl("enabled") : tl("disabled")));
        }
    }
}
