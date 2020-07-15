package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandtogglekeepinv extends EssentialsToggleCommand {

    public Commandtogglekeepinv() {
        super("togglekeepinv", "essentials.togglekeepinv.others");
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
            enabled = !user.isKeepInvEnabled();
        }

        user.setKeepInventory(enabled);

        user.sendMessage(enabled ? tl("togglekeepinvEnabled") : tl("togglekeepinvDisabled"));

        if (!sender.isPlayer() || !user.getBase().equals(sender.getPlayer())) {
            sender.sendMessage(enabled ? tl("toggleKeepinvEnabledFor", user.getDisplayName()) : tl("togglekeepinvDisabledFor", user.getDisplayName()));
        }
    }
}
