package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TreeType;

import static com.earth2me.essentials.I18n.tl;


public class Commandbigtree extends EssentialsCommand {
    public Commandbigtree() {
        super("bigtree");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        TreeType tree;
        if (args.length > 0 && args[0].equalsIgnoreCase("redwood")) {
            tree = TreeType.TALL_REDWOOD;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("tree")) {
            tree = TreeType.BIG_TREE;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("jungle")) {
            tree = TreeType.JUNGLE;
        } else {
            throw new NotEnoughArgumentsException();
        }

        final Location loc = LocationUtil.getTarget(user.getBase());
        final Location safeLocation = LocationUtil.getSafeDestination(loc);
        final boolean success = user.getWorld().generateTree(safeLocation, tree);
        if (success) {
            user.sendMessage(tl("bigTreeSuccess"));
        } else {
            throw new Exception(tl("bigTreeFailure"));
        }
    }
}
