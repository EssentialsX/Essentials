package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import javafx.util.Pair;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.earth2me.essentials.I18n.tl;


public class Commandnear extends EssentialsCommand {
    public Commandnear() {
        super("near");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        long maxRadius = ess.getSettings().getNearRadius();

        if (maxRadius == 0) {
            maxRadius = 200;
        }

        long radius = maxRadius;

        User otherUser = null;

        if (args.length > 0) {
            try {
                radius = Long.parseLong(args[0]);
            } catch (NumberFormatException e) {
                try {
                    otherUser = getPlayer(server, user, args, 0);
                } catch (Exception ex) {
                }
            }
            if (args.length > 1 && otherUser != null) {
                try {
                    radius = Long.parseLong(args[1]);
                } catch (NumberFormatException e) {
                }
            }
        }

        radius = Math.abs(radius);

        if (radius > maxRadius && !user.isAuthorized("essentials.near.maxexempt")) {
            user.sendMessage(tl("radiusTooBig", maxRadius));
            radius = maxRadius;
        }

        if (otherUser == null || !user.isAuthorized("essentials.near.others")) {
            otherUser = user;
        }
        user.sendMessage(tl("nearbyPlayers", getLocal(server, otherUser, radius)));
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }
        final User otherUser = getPlayer(server, args, 0, true, false);
        long radius = 200;
        if (args.length > 1) {
            try {
                radius = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
            }
        }
        sender.sendMessage(tl("nearbyPlayers", getLocal(server, otherUser, radius)));
    }

    private String getLocal(final Server server, final User user, final long radius) {
        final Location loc = user.getLocation();
        final World world = loc.getWorld();
        final StringBuilder output = new StringBuilder();
        final long radiusSquared = radius * radius;
        boolean showHidden = user.canInteractVanished();

        Queue<Pair<User, Long>> playerDistances = new PriorityQueue<>(Comparator.comparingLong(Pair::getValue));

        for (User player : ess.getOnlineUsers()) {
            if (!player.equals(user) && (!player.isHidden(user.getBase()) || showHidden || user.getBase().canSee(player.getBase()))) {
                final Location playerLoc = player.getLocation();
                if (playerLoc.getWorld() != world) {
                    continue;
                }

                final long delta = (long) playerLoc.distanceSquared(loc);
                if (delta < radiusSquared) {
                    playerDistances.offer(new Pair<>(player, delta));
                }
            }
        }

        while (!playerDistances.isEmpty()) {
            if (output.length() > 0) {
                output.append(", ");
            }
            Pair<User, Long> playerDistance = playerDistances.poll();
            output.append(playerDistance.getKey().getDisplayName()).append("§f(§4").append((long) Math.sqrt(playerDistance.getValue())).append("m§f)");
        }

        return output.length() > 1 ? output.toString() : tl("none");
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (user.isAuthorized("essentials.near.others")) {
            if (args.length == 1) {
                return getPlayers(server, user);
            } else if (args.length == 2) {
                return Lists.newArrayList(Integer.toString(ess.getSettings().getNearRadius()));
            } else {
                return Collections.emptyList();
            }
        } else {
            if (args.length == 1) {
                return Lists.newArrayList(Integer.toString(ess.getSettings().getNearRadius()));
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else if (args.length == 2) {
            return Lists.newArrayList(Integer.toString(ess.getSettings().getNearRadius()));
        } else {
            return Collections.emptyList();
        }
    }
}
