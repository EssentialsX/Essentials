package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import net.ess3.provider.ContainerProvider;
import org.bukkit.Server;

public class Commandsmithingtable extends EssentialsCommand {

    public Commandsmithingtable() {
        super("smithingtable");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        final ContainerProvider containerProvider = ess.provider(ContainerProvider.class);

        if (containerProvider == null) {
            user.sendTl("unsupportedBrand");
            return;
        }

        containerProvider.openSmithingTable(user.getBase());
    }
}
