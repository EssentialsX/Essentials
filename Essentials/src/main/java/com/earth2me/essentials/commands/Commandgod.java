package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.CommonPlaceholders;
import net.ess3.api.events.GodStatusChangeEvent;
import org.bukkit.Server;

public class Commandgod extends EssentialsToggleCommand {
    public Commandgod() {
        super("god", "essentials.god.others");
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
            enabled = !user.isGodModeEnabled();
        }

        final GodStatusChangeEvent godEvent = new GodStatusChangeEvent(user, sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null, enabled);
        ess.getServer().getPluginManager().callEvent(godEvent);
        if (!godEvent.isCancelled()) {
            user.setGodModeEnabled(enabled);

            if (enabled && user.getBase().getHealth() != 0) {
                user.getBase().setHealth(user.getBase().getMaxHealth());
                user.getBase().setFoodLevel(20);
            }

            user.sendTl("godMode", CommonPlaceholders.enableDisable(user.getSource(), enabled));
            if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
                sender.sendTl("godMode", CommonPlaceholders.enableDisable(user.getSource(), enabled));
            }
        }
    }
}
