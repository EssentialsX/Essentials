package com.earth2me.essentials.commands;

import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandtpdeny extends EssentialsCommand {
    public Commandtpdeny() {
        super("tpdeny");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final boolean excludeOthers;
        if (args.length > 0) {
            excludeOthers = args[0].startsWith("*") || args[0].equalsIgnoreCase("all");
        } else {
            excludeOthers = false;
        }

        if (!user.hasPendingTpaRequests(false, excludeOthers)) {
            throw new Exception(tl("noPendingRequest"));
        }

        final IUser.TpaRequest denyToken;
        if (args.length > 0) {
            if (excludeOthers) {
                IUser.TpaRequest token;
                int count = 0;
                while ((token = user.getNextTpaToken(false, true, true)) != null) {
                    final User player = ess.getUser(token.getRequesterUuid());
                    if (player != null && player.getBase().isOnline()) {
                        player.sendMessage(tl("requestDeniedFrom", user.getDisplayName()));
                    }

                    user.removeTpaRequest(token.getName());
                    count++;
                }
                user.sendMessage(tl("requestDeniedAll", count));
                return;
            }
            denyToken = user.getOutstandingTpaRequest(getPlayer(server, user, args, 0).getName(), false);
        } else {
            denyToken = user.getNextTpaToken(false, true, false);
        }

        final User player = ess.getUser(denyToken.getRequesterUuid());
        if (player == null || !player.getBase().isOnline()) {
            throw new Exception(tl("noPendingRequest"));
        }

        user.sendMessage(tl("requestDenied"));
        player.sendMessage(tl("requestDeniedFrom", user.getDisplayName()));
        user.removeTpaRequest(denyToken.getName());
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            final List<String> options = new ArrayList<>(user.getPendingTpaKeys());
            options.add("*");
            return options;
        } else {
            return Collections.emptyList();
        }
    }
}
