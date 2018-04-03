package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tlp;

import com.earth2me.essentials.User;

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
            user.sendMessage(tlp(user, "clearInventoryConfirmToggleOn"));
        } else {
            user.sendMessage(tlp(user, "clearInventoryConfirmToggleOff"));
        }
        user.setConfirmingClearCommand(null);
    }
}

