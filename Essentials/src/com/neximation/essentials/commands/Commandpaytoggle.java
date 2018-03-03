package com.neximation.essentials.commands;

import static com.neximation.essentials.I18n.tl;

import com.neximation.essentials.I18n;
import com.neximation.essentials.User;

import org.bukkit.Server;

public class Commandpaytoggle extends EssentialsCommand {

    public Commandpaytoggle() {
        super("paytoggle");
    }

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

