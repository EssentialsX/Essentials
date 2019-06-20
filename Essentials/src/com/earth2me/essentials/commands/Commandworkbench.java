package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;


/**
 * <p>Commandworkbench class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Commandworkbench extends EssentialsCommand {
    /**
     * <p>Constructor for Commandworkbench.</p>
     */
    public Commandworkbench() {
        super("workbench");
    }


    /** {@inheritDoc} */
    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        user.getBase().openWorkbench(null, true);
    }
}
