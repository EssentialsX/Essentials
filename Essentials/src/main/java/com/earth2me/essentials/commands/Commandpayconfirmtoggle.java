package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;

import com.earth2me.essentials.User;

import org.bukkit.Server;

public class Commandpayconfirmtoggle extends EssentialsCommand {

    public Commandpayconfirmtoggle() {
        super("payconfirmtoggle");
    }

    @Override
    public void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        boolean confirmingPay = !user.isPromptingPayConfirm();
        if (commandLabel.contains("payconfirmon")) {
            confirmingPay = true;
        } else if (commandLabel.contains("payconfirmoff")) {
            confirmingPay = false;
        }
        user.setPromptingPayConfirm(confirmingPay);
        if (confirmingPay) {
            user.sendMessage(tl("payConfirmToggleOn"));
        } else {
            user.sendMessage(tl("payConfirmToggleOff"));
        }
        user.getConfirmingPayments().clear(); // Clear any outstanding confirmations.
    }
}

