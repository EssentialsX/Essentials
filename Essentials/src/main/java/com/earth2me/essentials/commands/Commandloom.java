package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import net.ess3.provider.ContainerProvider;
import org.bukkit.Server;

public class Commandloom extends EssentialsCommand {

    public Commandloom() {
        super("loom");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ContainerProvider containerProvider = ess.provider(ContainerProvider.class);

        if (containerProvider == null) {
            user.sendTl("unsupportedBrand");
            return;
        }

        containerProvider.openLoom(user.getBase());
    }
}
