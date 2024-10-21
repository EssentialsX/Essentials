package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import net.ess3.api.TranslatableException;
import net.essentialsx.api.v2.events.HomeModifyEvent;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Commanddelhome extends EssentialsCommand {
    public Commanddelhome() {
        super("delhome");
    }

    private void deleteHome(CommandSource sender, User user, String home) {
        final HomeModifyEvent event = new HomeModifyEvent(sender.getUser(), user, home, user.getHome(home), false);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info("HomeModifyEvent canceled for /delhome execution by " + sender.getDisplayName());
            }
            return;
        }

        try {
            user.delHome(home);
        } catch (Exception e) {
            sender.sendTl("invalidHome", home);
        }
        sender.sendTl("deleteHome", home);
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        User user = ess.getUser(sender.getPlayer());
        final String name;
        final String[] expandedArg;

        //Allowing both formats /sethome khobbits house | /sethome khobbits:house
        final String[] nameParts = args[0].split(":");
        if (nameParts[0].length() != args[0].length()) {
            expandedArg = nameParts;
        } else {
            expandedArg = args;
        }

        if (expandedArg.length > 1 && (user == null || user.isAuthorized("essentials.delhome.others"))) {
            user = getPlayer(server, expandedArg, 0, true, true);
            name = expandedArg[1].toLowerCase(Locale.ENGLISH);
        } else if (user == null) {
            throw new NotEnoughArgumentsException();
        } else {
            name = expandedArg[0].toLowerCase(Locale.ENGLISH);
        }

        switch (name) {
            case "bed":
                throw new TranslatableException("invalidHomeName");
            case "*":
                final List<String> homes = user.getHomes();
                for (String home : homes) {
                    deleteHome(sender, user, home);
                }
                break;
            default:
                deleteHome(sender, user, name);
                break;
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        final IUser user = sender.getUser();
        final boolean canDelOthers = sender.isAuthorized("essentials.delhome.others");
        if (args.length == 1) {
            final List<String> homes = user == null ? new ArrayList<>() : user.getHomes();
            if (canDelOthers) {
                final int sepIndex = args[0].indexOf(':');
                if (sepIndex < 0) {
                    getPlayers(server, sender).forEach(player -> homes.add(player + ":"));
                } else {
                    final String namePart = args[0].substring(0, sepIndex);
                    final User otherUser;
                    try {
                        otherUser = getPlayer(server, new String[] {namePart}, 0, true, true);
                    } catch (final Exception ex) {
                        return homes;
                    }
                    otherUser.getHomes().forEach(home -> homes.add(namePart + ":" + home));
                    homes.add(namePart + ":" + "*");
                }
            }
            return homes;
        } else {
            return Collections.emptyList();
        }
    }
}
