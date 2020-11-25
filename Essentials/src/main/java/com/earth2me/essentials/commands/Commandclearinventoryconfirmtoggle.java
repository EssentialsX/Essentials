package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandclearinventoryconfirmtoggle extends EssentialsCommand {

    public Commandclearinventoryconfirmtoggle() {
        super("clearinventoryconfirmtoggle");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
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

