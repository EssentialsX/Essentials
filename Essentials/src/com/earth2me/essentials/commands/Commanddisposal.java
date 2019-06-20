package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

/**
 * <p>Commanddisposal class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Commanddisposal extends EssentialsCommand {

    /**
     * <p>Constructor for Commanddisposal.</p>
     */
    public Commanddisposal() {
        super("disposal");
    }

    /** {@inheritDoc} */
    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        user.sendMessage(tl("openingDisposal"));
        user.getBase().openInventory(ess.getServer().createInventory(user.getBase(), 36, tl("disposal")));
    }

}
