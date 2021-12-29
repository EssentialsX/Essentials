package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

public class Commandstonecutter extends EssentialsCommand {

    public Commandstonecutter() {
        super("stonecutter");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (ess.getContainerProvider() == null) {
            user.sendTl("unsupportedBrand");
            return;
        }

        ess.getContainerProvider().openStonecutter(user.getBase());
    }
}
