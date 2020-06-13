package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.VersionUtil;
import org.bukkit.Server;
import org.bukkit.Statistic;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandrest extends EssentialsLoopCommand {
    public Commandrest() {
        super("rest");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_13_0_R01)) {
            user.sendMessage(tl("unsupportedFeature"));
            return;
        }
        if (args.length > 0 && user.isAuthorized("essentials.rest.others")) {
            loopOnlinePlayers(server, user.getSource(), true, true, args[0], null);
            return;
        }
        restPlayer(user);
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_13_0_R01)) {
            sender.sendMessage(tl("unsupportedFeature"));
            return;
        }
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        loopOnlinePlayers(server, sender, true, true, args[0], null);
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args) throws PlayerExemptException {
        restPlayer(player);
        sender.sendMessage(tl("restOther", player.getDisplayName()));
    }

    private void restPlayer(final User user) {
        user.getBase().setStatistic(Statistic.TIME_SINCE_REST, 0);
        user.sendMessage(tl("rest"));
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1 && user.isAuthorized("essentials.rest.others")) {
            return getPlayers(server, user);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, CommandSource sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
