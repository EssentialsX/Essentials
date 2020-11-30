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
        if (!user.hasPendingTpaRequests(false)) {
            throw new Exception(tl("noPendingRequest"));
        }

        final IUser.TpaRequestToken denyToken;
        if (args.length > 0) {
            if (args[0].startsWith("*") || args[0].equalsIgnoreCase("all")) {
                IUser.TpaRequestToken token;
                int count = 0;
                while ((token = user.getNextTpaToken(false, true, true)) != null) {
                    final User player = ess.getUser(token.getRequesterUuid());
                    if (player != null) {
                        player.sendMessage(tl("requestDeniedFrom", user.getDisplayName()));
                    }

                    user.removeTpaRequest(token.getName());
                    count++;
                }
                user.sendMessage(tl("requestDeniedAll", count));
                return;
            } else {
                denyToken = user.getOutstandingTpaRequest(getPlayer(server, user, args, 0).getName(), false);
            }
        } else {
            denyToken = user.getNextTpaToken(false, true, false);
        }

        final User player = ess.getUser(denyToken.getRequesterUuid());
        if (player == null) {
            throw new Exception(tl("noPendingRequest"));
        }

        user.sendMessage(tl("requestDenied"));
        player.sendMessage(tl("requestDeniedFrom", user.getDisplayName()));
        user.removeTpaRequest(denyToken.getName());
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(user.getPendingTpaKeys());
            options.add("*");
            return options;
        } else {
            return Collections.emptyList();
        }
    }
}
