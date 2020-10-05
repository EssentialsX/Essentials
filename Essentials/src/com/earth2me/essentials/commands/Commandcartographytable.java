package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandcartographytable extends EssentialsCommand {

    public Commandcartographytable() {
        super("cartographytable");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (ess.getContainerProvider() == null) {
            user.sendMessage(tl("unsupportedBrand"));
            return;
        }

        ess.getContainerProvider().openCartographyTable(user.getBase());
    }
}
