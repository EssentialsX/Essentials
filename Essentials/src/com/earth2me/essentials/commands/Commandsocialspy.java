package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>Commandsocialspy class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Commandsocialspy extends EssentialsToggleCommand {
    /**
     * <p>Constructor for Commandsocialspy.</p>
     */
    public Commandsocialspy() {
        super("socialspy", "essentials.socialspy.others");
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
    void togglePlayer(CommandSource sender, User user, Boolean enabled) throws NotEnoughArgumentsException {
        if (enabled == null) {
            enabled = !user.isSocialSpyEnabled();
        }

        user.setSocialSpyEnabled(enabled);


        user.sendMessage(tl("socialSpy", user.getDisplayName(), enabled ? tl("enabled") : tl("disabled")));
        if (!sender.isPlayer() || !sender.getPlayer().equals(user.getBase())) {
            sender.sendMessage(tl("socialSpy", user.getDisplayName(), enabled ? tl("enabled") : tl("disabled")));
        }
    }
}
