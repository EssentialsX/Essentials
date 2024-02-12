package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

public class Commandpayconfirmtoggle extends EssentialsCommand {

    public Commandpayconfirmtoggle() {
        super("payconfirmtoggle");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        boolean confirmingPay = !user.isPromptingPayConfirm();
        if (commandLabel.contains("payconfirmon")) {
            confirmingPay = true;
        } else if (commandLabel.contains("payconfirmoff")) {
            confirmingPay = false;
        }
        user.setPromptingPayConfirm(confirmingPay);
        user.sendTl(confirmingPay ? "payConfirmToggleOn" : "payConfirmToggleOff");
        user.getConfirmingPayments().clear(); // Clear any outstanding confirmations.
    }
}

