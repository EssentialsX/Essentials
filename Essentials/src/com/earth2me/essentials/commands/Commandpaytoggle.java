package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;

import com.earth2me.essentials.I18n;
import com.earth2me.essentials.User;

import org.bukkit.Server;

/**
 * <p>Commandpaytoggle class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class Commandpaytoggle extends EssentialsCommand {

    /**
     * <p>Constructor for Commandpaytoggle.</p>
     */
    public Commandpaytoggle() {
        super("paytoggle");
    }

    /** {@inheritDoc} */
    @Override
    public void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        boolean acceptingPay = !user.isAcceptingPay();
        if (commandLabel.contains("payon")) {
            acceptingPay = true;
        } else if (commandLabel.contains("payoff")) {
            acceptingPay = false;
        }
        user.setAcceptingPay(acceptingPay);
        if (acceptingPay) {
            user.sendMessage(tl("payToggleOn"));
        } else {
            user.sendMessage(tl("payToggleOff"));
        }
    }
}

