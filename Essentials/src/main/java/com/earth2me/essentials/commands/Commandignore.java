package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.earth2me.essentials.I18n.tl;

public class Commandignore extends EssentialsCommand {
    public Commandignore() {
        super("ignore");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            final StringBuilder sb = new StringBuilder();
            for (final UUID uuid : user._getIgnoredPlayers()) {
                final User curUser = ess.getUser(uuid);
                if (curUser != null && curUser.getName() != null && !curUser.getName().trim().isEmpty()) {
                    sb.append(curUser.getName()).append(" ");
                }
            }
            final String ignoredList = sb.toString().trim();
            user.sendMessage(ignoredList.length() > 0 ? tl("ignoredList", ignoredList) : tl("noIgnored"));
            return;
        }

        User player;
        try {
            player = getPlayer(server, args, 0, true, true);
        } catch (final PlayerNotFoundException ex) {
            player = ess.getOfflineUser(args[0]);
        }
        if (player == null) {
            throw new PlayerNotFoundException();
        }

        if (player.isIgnoreExempt()) {
            user.sendMessage(tl("ignoreExempt"));
        } else if (user.isIgnoredPlayer(player)) {
            user.setIgnoredPlayer(player, false);
            user.sendMessage(tl("unignorePlayer", player.getName()));
        } else if (user.getUUID().equals(player.getUUID())) {
            user.sendMessage(tl("ignoreYourself"));
        } else {
            user.setIgnoredPlayer(player, true);
            user.sendMessage(tl("ignorePlayer", player.getName()));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, user);
        } else {
            return Collections.emptyList();
        }
    }
}
