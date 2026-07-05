package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

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
        final int seconds = Integer.parseInt(args[1]);

        final int fireTicks = (int) Math.max(0L, Math.min((long) seconds * 20, Integer.MAX_VALUE));

        user.getBase().setFireTicks(fireTicks);
        sender.sendTl("burnMsg", user.getDisplayName(), Math.max(0, seconds));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(sender);
        } else if (args.length == 2) {
            return COMMON_DURATIONS;
        } else {
            return Collections.emptyList();
        }
    }
}
