package com.neximation.essentials.xmpp;

import com.neximation.essentials.User;
import com.neximation.essentials.commands.EssentialsCommand;
import com.neximation.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Server;


public class Commandsetxmpp extends EssentialsCommand {
    public Commandsetxmpp() {
        super("setxmpp");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws NotEnoughArgumentsException {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        EssentialsXMPP.getInstance().setAddress(user.getBase(), args[0]);
        user.sendMessage("XMPP address set to " + args[0]);
    }
}
