package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import static com.earth2me.essentials.I18n.tlp;

public class Commanddisposal extends EssentialsCommand {

    public Commanddisposal() {
        super("disposal");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        user.sendMessage(tlp(user, "openingDisposal"));
        user.getBase().openInventory(ess.getServer().createInventory(user.getBase(), 36, "Disposal"));
    }

}
