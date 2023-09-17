package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.CommonPlaceholders;
import net.ess3.api.IUser;
import net.ess3.api.events.FlyStatusChangeEvent;
import org.bukkit.Server;

public class Commandfly extends EssentialsToggleCommand {
    public Commandfly() {
        super("fly", "essentials.fly.others");
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
            enabled = !user.getBase().getAllowFlight();
        }

        final FlyStatusChangeEvent event = new FlyStatusChangeEvent(user, sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null, enabled);
        ess.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            user.getBase().setFallDistance(0f);
            user.getBase().setAllowFlight(enabled);

            if (!user.getBase().getAllowFlight()) {
                user.getBase().setFlying(false);
            }

            user.sendTl("flyMode", CommonPlaceholders.enableDisable(user.getSource(), enabled), CommonPlaceholders.displayName((IUser) user));
            if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
                sender.sendTl("flyMode", CommonPlaceholders.enableDisable(user.getSource(), enabled), CommonPlaceholders.displayName((IUser) user));
            }
        }
    }
}
