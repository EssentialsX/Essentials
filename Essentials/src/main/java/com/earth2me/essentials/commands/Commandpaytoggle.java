package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.CommonPlaceholders;
import net.ess3.api.IUser;
import org.bukkit.Server;

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

        user.sendTl(enabled ? "payToggleOn" : "payToggleOff");
        if (!sender.isPlayer() || !user.getBase().equals(sender.getPlayer())) {
            sender.sendTl(enabled ? "payEnabledFor" : "payDisabledFor", CommonPlaceholders.displayName((IUser) user));
        }
    }
}

