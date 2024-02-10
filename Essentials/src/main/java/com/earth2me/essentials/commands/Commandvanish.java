package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.CommonPlaceholders;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.Server;

public class Commandvanish extends EssentialsToggleCommand {
    public Commandvanish() {
        super("vanish", "essentials.vanish.others");
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
            enabled = !user.isVanished();
        }

        final VanishStatusChangeEvent vanishEvent = new VanishStatusChangeEvent(sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null, user, enabled);
        ess.getServer().getPluginManager().callEvent(vanishEvent);
        if (vanishEvent.isCancelled()) {
            return;
        }

        user.setVanished(enabled);
        user.sendTl("vanish", user.getDisplayName(), CommonPlaceholders.enableDisable(user.getSource(), enabled));

        if (enabled) {
            user.sendTl("vanished");
        }
        if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
            sender.sendTl("vanish", user.getDisplayName(), CommonPlaceholders.enableDisable(user.getSource(), enabled));
        }
    }
}
