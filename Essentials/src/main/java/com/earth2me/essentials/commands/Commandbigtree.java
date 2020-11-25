package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.LocationUtil;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.TreeType;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandbigtree extends EssentialsCommand {
    public Commandbigtree() {
        super("bigtree");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final TreeType tree;
        if (args.length > 0 && args[0].equalsIgnoreCase("redwood")) {
            tree = TreeType.TALL_REDWOOD;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("tree")) {
            tree = TreeType.BIG_TREE;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("jungle")) {
            tree = TreeType.JUNGLE;
        } else if (args.length > 0 && args[0].equalsIgnoreCase("darkoak")) {
            tree = TreeType.DARK_OAK;
        } else {
            throw new NotEnoughArgumentsException();
        }

        final Location loc = LocationUtil.getTarget(user.getBase()).add(0, 1, 0);
        if (loc.getBlock().getType().isSolid()) {
            throw new Exception(tl("bigTreeFailure"));
        }
        final boolean success = user.getWorld().generateTree(loc, tree);
        if (success) {
            user.sendMessage(tl("bigTreeSuccess"));
        } else {
            throw new Exception(tl("bigTreeFailure"));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("redwood", "tree", "jungle", "darkoak");
        } else {
            return Collections.emptyList();
        }
    }
}
