package com.neximation.essentials.commands;

import com.neximation.essentials.User;
import org.bukkit.Server;

import static com.neximation.essentials.I18n.tl;

public class Commanddisposal extends EssentialsCommand {

    public Commanddisposal() {
        super("disposal");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        user.sendMessage(tl("openingDisposal"));
        user.getBase().openInventory(ess.getServer().createInventory(user.getBase(), 36, "Disposal"));
    }

}
