package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandtpauto extends EssentialsToggleCommand {
    public Commandtpauto() {
        super("tpauto", "essentials.tpauto.others");
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
            enabled = !user.isAutoTeleportEnabled();
        }

        user.setAutoTeleportEnabled(enabled);

        user.sendMessage(enabled ? tl("autoTeleportEnabled") : tl("autoTeleportDisabled"));
        if (enabled && !user.isTeleportEnabled()) {
            user.sendMessage(tl("teleportationDisabledWarning"));
        }
        if (!sender.isPlayer() || !user.getBase().equals(sender.getPlayer())) {
            sender.sendMessage(enabled ? tl("autoTeleportEnabledFor", user.getDisplayName()) : tl("autoTeleportDisabledFor", user.getDisplayName()));
        }
    }
}
