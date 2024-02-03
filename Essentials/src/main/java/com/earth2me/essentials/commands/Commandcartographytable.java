package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

public class Commandcartographytable extends EssentialsCommand {

    public Commandcartographytable() {
        super("cartographytable");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (ess.getContainerProvider() == null) {
            user.sendTl("unsupportedBrand");
            return;
        }

        ess.getContainerProvider().openCartographyTable(user.getBase());
    }
}
