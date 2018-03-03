package com.neximation.essentials.commands;

import com.neximation.essentials.CommandSource;
import com.neximation.essentials.User;
import org.bukkit.Server;

import static com.neximation.essentials.I18n.tl;


public class Commandmsgtoggle extends EssentialsToggleCommand {
    public Commandmsgtoggle() {
        super("msgtoggle", "essentials.msgtoggle.others");
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
    void togglePlayer(CommandSource sender, User user, Boolean enabled) {
        if (enabled == null) {
            enabled = !user.isIgnoreMsg();
        }

        user.setIgnoreMsg(enabled);

        user.sendMessage(!enabled ? tl("msgEnabled") : tl("msgDisabled"));
        if (!sender.isPlayer() || !user.getBase().equals(sender.getPlayer())) {
            sender.sendMessage(!enabled ? tl("msgEnabledFor", user.getDisplayName()) : tl("msgDisabledFor", user.getDisplayName()));
        }
    }
}
