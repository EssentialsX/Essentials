package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandstonecutter extends EssentialsCommand {

    public Commandstonecutter() {
        super("stonecutter");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (ess.getContainerProvider() == null) {
            user.sendMessage(tl("unsupportedBrand"));
            return;
        }

        ess.getContainerProvider().openStonecutter(user.getBase());
    }
}
