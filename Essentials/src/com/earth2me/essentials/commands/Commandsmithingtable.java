package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tl;

public class Commandsmithingtable extends EssentialsCommand {

    public Commandsmithingtable() {
        super("smithingtable");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        if (ess.getContainerProvider() == null) {
            user.sendMessage(tl("unsupportedBrand"));
            return;
        }

        ess.getContainerProvider().openSmithingTable(user.getBase());
    }
}
