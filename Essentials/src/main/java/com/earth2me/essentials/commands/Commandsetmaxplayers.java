package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import org.bukkit.Server;

public class Commandsetmaxplayers extends EssentialsCommand {
    public Commandsetmaxplayers() {
        super("setmaxplayers");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        try {
            final int maxPlayers = Integer.parseInt(args[0]);
            if (maxPlayers < 0) {
                throw new Exception(sender.tl("setmaxplayersBelowZero"));
            }

            server.setMaxPlayers(maxPlayers);
            sender.sendMessage(sender.tl("setmaxplayersSuccess", maxPlayers));
        } catch (NumberFormatException e) {
            throw new Exception(sender.tl("setmaxplayersInvalid"));
        }
    }

}
