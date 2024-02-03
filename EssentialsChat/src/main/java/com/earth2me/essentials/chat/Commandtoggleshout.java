package com.earth2me.essentials.chat;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsToggleCommand;
import com.earth2me.essentials.utils.CommonPlaceholders;
import net.ess3.api.IUser;
import org.bukkit.Server;

public class Commandtoggleshout extends EssentialsToggleCommand {
    public Commandtoggleshout() {
        super("toggleshout", "essentials.toggleshout.others");
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
            enabled = !user.isToggleShout();
        }

        user.setToggleShout(enabled);

        user.sendTl(enabled ? "shoutEnabled" : "shoutDisabled");
        if (!sender.isPlayer() || !user.getBase().equals(sender.getPlayer())) {
            sender.sendTl(enabled ? "shoutEnabledFor" : "shoutDisabledFor", CommonPlaceholders.displayName((IUser) user));
        }
    }
}
