package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>Commandpowertooltoggle class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Commandpowertooltoggle extends EssentialsCommand {
    /**
     * <p>Constructor for Commandpowertooltoggle.</p>
     */
    public Commandpowertooltoggle() {
        super("powertooltoggle");
    }

    /** {@inheritDoc} */
    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (!user.hasPowerTools()) {
            user.sendMessage(tl("noPowerTools"));
            return;
        }
        user.sendMessage(user.togglePowerToolsEnabled() ? tl("powerToolsEnabled") : tl("powerToolsDisabled"));
    }
}
