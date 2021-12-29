package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

public class Commanddisposal extends EssentialsCommand {

    public Commanddisposal() {
        super("disposal");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        user.sendTl("openingDisposal");
        user.getBase().openInventory(ess.getServer().createInventory(user.getBase(), 36, user.playerTl("disposal")));
    }

}
