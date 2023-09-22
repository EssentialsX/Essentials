package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandpaytoggle extends EssentialsToggleCommand {

    public Commandpaytoggle() {
        super("paytoggle", "essentials.paytoggle.others");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        toggleOtherPlayers(server, sender, args);
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (commandLabel.contains("payon")) {
            togglePlayer(user.getSource(), user, true);
        } else if (commandLabel.contains("payoff")) {
            togglePlayer(user.getSource(), user, false);
        } else {
            handleToggleWithArgs(server, user, args);
        }
    }

    @Override
    protected void togglePlayer(final CommandSource sender, final User user, Boolean enabled) {
        if (enabled == null) {
            enabled = !user.isAcceptingPay();
        }

        user.setAcceptingPay(enabled);

        user.sendMessage(enabled ? tl("payToggleOn") : tl("payToggleOff"));
        if (!sender.isPlayer() || !user.getBase().equals(sender.getPlayer())) {
            sender.sendMessage(enabled ? tl("payEnabledFor", user.getName()) : tl("payDisabledFor", user.getName()));
        }
    }
}

