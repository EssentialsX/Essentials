package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.AdventureUtil;
import com.google.common.collect.Lists;
import net.ess3.api.TranslatableException;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.Collections;
import java.util.List;

public class Commandbroadcastworld extends EssentialsCommand {

    public Commandbroadcastworld() {
        super("broadcastworld");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        World world = user.getWorld();
        String message = getFinalArg(args, 0);
        if (ess.getSettings().isAllowWorldInBroadcastworld()) {
            final World argWorld = ess.getWorld(args[0]);
            if (argWorld != null) {
                world = argWorld;
                message = getFinalArg(args, 1);
            }
        }

        sendBroadcast(world, user.getDisplayName(), message);
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }

        final World world = ess.getWorld(args[0]);
        if (world == null) {
            throw new TranslatableException("invalidWorld");
        }
        sendBroadcast(world, sender.getSender().getName(), getFinalArg(args, 1));
    }

    private void sendBroadcast(final World world, final String name, final String message) throws Exception {
        if (message.isEmpty()) {
            throw new NotEnoughArgumentsException();
        }
        ess.broadcastTl(null, u -> !u.getBase().getWorld().equals(world), true, "broadcast", message, AdventureUtil.parsed(AdventureUtil.legacyToMini(name)));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1 && (!sender.isPlayer() || ess.getSettings().isAllowWorldInBroadcastworld())) {
            final List<String> worlds = Lists.newArrayList();
            for (final World world : server.getWorlds()) {
                worlds.add(world.getName());
            }
            return worlds;
        } else {
            return Collections.emptyList();
        }
    }
}
