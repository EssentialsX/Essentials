package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import net.ess3.api.events.GodStatusChangeEvent;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>Commandgod class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Commandgod extends EssentialsToggleCommand {
    /**
     * <p>Constructor for Commandgod.</p>
     */
    public Commandgod() {
        super("god", "essentials.god.others");
    }

    /** {@inheritDoc} */
    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        toggleOtherPlayers(server, sender, args);
    }

    /** {@inheritDoc} */
    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        handleToggleWithArgs(server, user, args);
    }

    @Override
    void togglePlayer(CommandSource sender, User user, Boolean enabled) {
        if (enabled == null) {
            enabled = !user.isGodModeEnabled();
        }

        final User controller = sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null;
        final GodStatusChangeEvent godEvent = new GodStatusChangeEvent(user, controller, enabled);
        ess.getServer().getPluginManager().callEvent(godEvent);
        if (!godEvent.isCancelled()) {
            user.setGodModeEnabled(enabled);

            if (enabled && user.getBase().getHealth() != 0) {
                user.getBase().setHealth(user.getBase().getMaxHealth());
                user.getBase().setFoodLevel(20);
            }

            user.sendMessage(tl("godMode", enabled ? tl("enabled") : tl("disabled")));
            if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
                sender.sendMessage(tl("godMode", tl(enabled ? "godEnabledFor" : "godDisabledFor", user.getDisplayName())));
            }
        }
    }
}
