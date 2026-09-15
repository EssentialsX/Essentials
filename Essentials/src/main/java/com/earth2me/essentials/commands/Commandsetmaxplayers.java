package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import org.bukkit.Server;

import java.util.List;

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
                throw new Exception("The player limit cannot be set below 0.");
            }

            server.setMaxPlayers(maxPlayers);
            sender.sendMessage("Player limit updated to " + maxPlayers + ".");
        } catch (NumberFormatException e) {
            throw new Exception("Invalid number.");
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(sender);
        } else {
            return COMMON_DATE_DIFFS;
        }
    }
}
