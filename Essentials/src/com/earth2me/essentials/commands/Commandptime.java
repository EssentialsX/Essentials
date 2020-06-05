package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DescParseTickFormat;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.*;

import static com.earth2me.essentials.I18n.tl;


public class Commandptime extends EssentialsLoopCommand {
    protected static final Set<String> getAliases = new HashSet<>();

    static {
        getAliases.add("get");
        getAliases.add("list");
        getAliases.add("show");
        getAliases.add("display");
    }

    public Commandptime() {
        super("ptime");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0 || getAliases.contains(args[0].toLowerCase())) {
            if (args.length > 1) { // /ptime get md_5 || /ptime get *
                if (args[1].equals("*") || args[1].equals("**")) {
                    sender.sendMessage(tl("pTimePlayers"));
                }
                loopOnlinePlayersConsumer(server, sender, false, true, args[1], player -> getUserTime(sender, player));
                return;
            }

            if (args.length == 1 || sender.isPlayer()) { // /ptime get
                if (sender.isPlayer()) {
                    getUserTime(sender, sender.getUser(ess));
                    return;
                }
                throw new NotEnoughArgumentsException(); // We cannot imply the target for console
            }

            // Default to showing the player times of all online users for console when no arguments are provided
            if (ess.getOnlinePlayers().size() > 1) {
                sender.sendMessage(tl("pTimePlayers"));
            }
            for (User player : ess.getOnlineUsers()) {
                getUserTime(sender, player);
            }
        }

        if (args.length > 1 && !sender.isAuthorized("essentials.ptime.others", ess) && !args[1].equalsIgnoreCase(sender.getSelfSelector())) {
            sender.sendMessage(tl("pTimeOthersPermission"));
            return;
        }

        String time = args[0];
        boolean fixed = time.startsWith("@");
        if (fixed) {
            time = time.substring(1);
        }

        final Long ticks;
        if (DescParseTickFormat.meansReset(time)) {
            ticks = null;
        } else {
            try {
                ticks = DescParseTickFormat.parse(time);
            } catch (NumberFormatException e) {
                throw new NotEnoughArgumentsException(e);
            }
        }

        StringJoiner joiner = new StringJoiner(",");
        loopOnlinePlayersConsumer(server, sender, false, true, args.length > 1 ? args[1] : sender.getSelfSelector(), player -> {
            setUserTime(player, ticks, !fixed);
            joiner.add(player.getName());
        });

        if (ticks == null) {
            sender.sendMessage(tl("pTimeReset", joiner.toString()));
            return;
        }

        String formattedTime = DescParseTickFormat.format(ticks);
        sender.sendMessage(fixed ? tl("pTimeSetFixed", formattedTime, joiner.toString()) : tl("pTimeSet", formattedTime, joiner.toString()));
    }

    public void getUserTime(final CommandSource sender, IUser user) {
        if (user == null) {
            return;
        }

        if (user.getBase().getPlayerTimeOffset() == 0) {
            sender.sendMessage(tl("pTimeNormal", user.getName()));
            return;
        }

        String time = DescParseTickFormat.format(user.getBase().getPlayerTime());
        sender.sendMessage(user.getBase().isPlayerTimeRelative() ? tl("pTimeCurrent", user.getName(), time) : tl("pTimeCurrentFixed", user.getName(), time));
    }

    private void setUserTime(final User user, final Long ticks, Boolean relative) {
        if (ticks == null) {
            user.getBase().resetPlayerTime();
        } else {
            final World world = user.getWorld();
            long time = user.getBase().getPlayerTime();
            time -= time % 24000;
            time += 24000 + ticks;
            if (relative) {
                time -= world.getTime();
            }
            user.getBase().setPlayerTime(time, relative);
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        final User user = ess.getUser(sender.getPlayer());

        if (args.length == 1) {
            return Lists.newArrayList("get", "reset", "sunrise", "day", "morning", "noon", "afternoon", "sunset", "night", "midnight");
        } else if (args.length == 2 && (getAliases.contains(args[0]) || user == null || user.isAuthorized("essentials.ptime.others"))) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected void updatePlayer(Server server, CommandSource sender, User user, String[] args) {
    }
}


class UserNameComparator implements Comparator<User> {
    @Override
    public int compare(User a, User b) {
        return a.getName().compareTo(b.getName());
    }
}
