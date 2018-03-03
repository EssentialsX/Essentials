package com.neximation.essentials.commands;

import static com.neximation.essentials.I18n.tl;

import com.neximation.essentials.User;

import org.bukkit.Server;

public class Commandclearinventoryconfirmtoggle extends EssentialsCommand {

    public Commandclearinventoryconfirmtoggle() {
        super("clearinventoryconfirmtoggle");
    }

    @Override
    public void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        boolean confirmingClear = !user.isPromptingClearConfirm();
        if (commandLabel.toLowerCase().endsWith("on")) {
            confirmingClear = true;
        } else if (commandLabel.toLowerCase().endsWith("off")) {
            confirmingClear = false;
        }
        user.setPromptingClearConfirm(confirmingClear);
        if (confirmingClear) {
            user.sendMessage(tl("clearInventoryConfirmToggleOn"));
        } else {
            user.sendMessage(tl("clearInventoryConfirmToggleOff"));
        }
        user.setConfirmingClearCommand(null);
    }
}

