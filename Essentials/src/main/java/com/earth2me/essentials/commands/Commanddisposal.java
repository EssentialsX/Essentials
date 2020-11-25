package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commanddisposal extends EssentialsCommand {

    public Commanddisposal() {
        super("disposal");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        user.sendMessage(tl("openingDisposal"));
        user.getBase().openInventory(ess.getServer().createInventory(user.getBase(), 36, tl("disposal")));
    }

}
