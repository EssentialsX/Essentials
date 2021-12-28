package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

public class Commandanvil extends EssentialsCommand {

    public Commandanvil() {
        super("anvil");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        if (ess.getContainerProvider() == null) {
            user.sendTl("unsupportedBrand");
            return;
        }

        ess.getContainerProvider().openAnvil(user.getBase());
    }
}
