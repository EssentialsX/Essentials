package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandburn extends EssentialsCommand {
    public Commandburn() {
        super("burn");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final User user = getPlayer(server, sender, args, 0);
        user.getBase().setFireTicks(Integer.parseInt(args[1]) * 20);
        sender.sendMessage(tl("burnMsg", user.getDisplayName(), Integer.parseInt(args[1])));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else if (args.length == 2) {
            return COMMON_DURATIONS;
        } else {
            return Collections.emptyList();
        }
    }
}
