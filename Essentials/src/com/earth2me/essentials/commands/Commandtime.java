package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DescParseTickFormat;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.*;

import static com.earth2me.essentials.I18n.tl;


public class Commandtime extends EssentialsCommand {
    private final List<String> subCommands = Arrays.asList("add", "set");
    private final List<String> timeNames = Arrays.asList("sunrise", "day", "morning", "noon", "afternoon", "sunset", "night", "midnight");
    private final List<String> timeNumbers = Arrays.asList("1000", "2000", "3000", "4000", "5000");


    public Commandtime() {
        super("time");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        long timeTick;
        Set<World> worlds;
        boolean add = false;
        if (args.length == 0) {
            worlds = getWorlds(server, sender, null);
            if (commandLabel.endsWith("day") || commandLabel.endsWith("night")) {
                timeTick = DescParseTickFormat.parse(commandLabel.toLowerCase(Locale.ENGLISH).replace("e", "")); // These are 100% safe things to parse, no need for catching
            } else {
                getWorldsTime(sender, worlds);
                return;
            }
        } else if (args.length == 1) {
            worlds = getWorlds(server, sender, null);
            try {
                timeTick = DescParseTickFormat.parse(NumberUtil.isInt(args[0]) ? (args[0] + "t") : args[0]);
            } catch (NumberFormatException e) {
                throw new NotEnoughArgumentsException(e);
            }
        } else {
            if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add")) {
                try {
                    add = args[0].equalsIgnoreCase("add");
                    timeTick = DescParseTickFormat.parse(NumberUtil.isInt(args[1]) ? (args[1] + "t") : args[1]);
                    worlds = getWorlds(server, sender, args.length > 2 ? args[2] : null);
                } catch (NumberFormatException e) {
                    throw new NotEnoughArgumentsException(e);
                }
            } else {
                try {
                    timeTick = DescParseTickFormat.parse(NumberUtil.isInt(args[0]) ? (args[0] + "t") : args[0]);
                    worlds = getWorlds(server, sender, args[1]);
                } catch (NumberFormatException e) {
                    throw new NotEnoughArgumentsException(e);
                }
            }
        }

        // Start updating world times, we have what we need
        User user = ess.getUser(sender.getPlayer());
        for (World world : worlds) {
            if (!canUpdateWorld(user, world)) {
                throw new Exception(tl("timeSetWorldPermission", user.getWorld().getName()));
            }
        }

        final StringJoiner joiner = new StringJoiner(",");
        for (World world : worlds) {
            long time = world.getTime();
            if (!add) {
                time -= time % 24000;
            }
            world.setTime(time + (add ? 0 : 24000) + timeTick);
            joiner.add(world.getName());
        }

        sender.sendMessage(tl(add ? "timeWorldAdd" : "timeWorldSet", DescParseTickFormat.formatTicks(timeTick), joiner.toString()));
    }

    private void getWorldsTime(final CommandSource sender, final Collection<World> worlds) {
        if (worlds.size() == 1) {
            final Iterator<World> iter = worlds.iterator();
            sender.sendMessage(DescParseTickFormat.format(iter.next().getTime()));
            return;
        }

        for (World world : worlds) {
            sender.sendMessage(tl("timeWorldCurrent", world.getName(), DescParseTickFormat.format(world.getTime())));
        }
    }

    private Set<World> getWorlds(final Server server, final CommandSource sender, final String selector) throws Exception {
        final Set<World> worlds = new TreeSet<>(new WorldNameComparator());

        // If there is no selector we want the world the user is currently in. Or all worlds if it isn't a user.
        if (selector == null) {
            if (sender.isPlayer()) {
                worlds.add(sender.getPlayer().getWorld());
            } else {
                worlds.addAll(server.getWorlds());
            }
            return worlds;
        }

        // Try to find the world with name = selector
        final World world = server.getWorld(selector);
        if (world != null) {
            worlds.add(world);
        } else if (selector.equalsIgnoreCase("*") || selector.equalsIgnoreCase("all")) { // If that fails, Is the argument something like "*" or "all"?
            worlds.addAll(server.getWorlds());
        } else { // We failed to understand the world target...
            throw new Exception(tl("invalidWorld"));
        }
        return worlds;
    }
    
    private boolean canUpdateAll(User user) {
        return !ess.getSettings().isWorldTimePermissions() // First check if per world permissions are enabled, if not, return true. 
            || user == null || user.isAuthorized("essentials.time.world.all");
    }

    private boolean canUpdateWorld(User user, World world) {
        return canUpdateAll(user) || user.isAuthorized("essentials.time.world." + normalizeWorldName(world));
    }

    private String normalizeWorldName(World world) {
        return world.getName().toLowerCase().replaceAll("\\s+", "_");
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        boolean isConsole = sender.isPlayer();
        if (args.length == 1) {
            if (isConsole || sender.isAuthorized("essentials.time.set", ess)) {
                return subCommands;
            } else {
                return Collections.emptyList();
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                return timeNames;
            } else if (args[0].equalsIgnoreCase("add")) {
                return timeNumbers;
            } else {
                return Collections.emptyList();
            }
        } else if (args.length == 3 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add"))) {
            List<String> worlds = Lists.newArrayList();
            for (World world : server.getWorlds()) {
                if (isConsole || sender.isAuthorized("essentials.time.world." + normalizeWorldName(world), ess)) {
                    worlds.add(world.getName());
                }
            }
            if (isConsole || sender.isAuthorized("essentials.time.world.all", ess)) {
                worlds.add("*");
            }
            return worlds;
        } else {
            return Collections.emptyList();
        }
    }
}


class WorldNameComparator implements Comparator<World> {
    @Override
    public int compare(final World a, final World b) {
        return a.getName().compareTo(b.getName());
    }
}
