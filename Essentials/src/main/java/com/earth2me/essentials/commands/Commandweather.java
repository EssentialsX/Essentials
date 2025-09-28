package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.google.common.collect.Lists;
import net.ess3.api.TranslatableException;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.Collections;
import java.util.List;

public class Commandweather extends EssentialsCommand {
    public Commandweather() {
        super("weather");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final boolean isStorm;
        if (args.length == 0) {
            if (commandLabel.endsWith("sun")) {
                isStorm = false;
            } else if (commandLabel.endsWith("storm") || commandLabel.endsWith("rain")) {
                isStorm = true;
            } else {
                throw new NotEnoughArgumentsException();
            }
        } else {
            isStorm = args[0].equalsIgnoreCase("storm");
        }

        final World world = user.getWorld();

        if (args.length > 1) {
            world.setStorm(isStorm);
            world.setWeatherDuration(Integer.parseInt(args[1]) * 20);
            user.sendTl(isStorm ? "weatherStormFor" : "weatherSunFor", world.getName(), args[1]);
            return;
        }
        world.setStorm(isStorm);
        user.sendTl(isStorm ? "weatherStorm" : "weatherSun", world.getName());
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) { //running from console means inserting a world arg before other args
            throw new Exception("When running from console, usage is: /" + commandLabel + " <world> <storm/sun> [duration]");
        }

        final boolean isStorm = args[1].equalsIgnoreCase("storm");
        final World world = server.getWorld(args[0]);
        if (world == null) {
            throw new TranslatableException("weatherInvalidWorld", args[0]);
        }

        if (args.length > 2) {
            world.setStorm(isStorm);
            world.setWeatherDuration(Integer.parseInt(args[2]) * 20);
            sender.sendTl(isStorm ? "weatherStormFor" : "weatherSunFor", world.getName(), args[2]);
            return;
        }
        world.setStorm(isStorm);
        sender.sendTl(isStorm ? "weatherStorm" : "weatherSun", world.getName());
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("storm", "sun");
        } else if (args.length == 2) {
            return COMMON_DURATIONS;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            final List<String> worlds = Lists.newArrayList();
            for (final World world : server.getWorlds()) {
                worlds.add(world.getName());
            }
            return worlds;
        } else if (args.length == 2) {
            return Lists.newArrayList("storm", "sun");
        } else if (args.length == 3) {
            return COMMON_DURATIONS;
        } else {
            return Collections.emptyList();
        }
    }
}
