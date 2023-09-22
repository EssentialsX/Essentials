package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.IUser;
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
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (VersionUtil.PRE_FLATTENING) {
            sender.sendMessage(tl("unsupportedFeature"));
            return;
        }
        if (args.length == 0 && !sender.isPlayer()) {
            throw new NotEnoughArgumentsException();
        }
        if (args.length > 0 && sender.isAuthorized("essentials.rest.others", ess)) {
            loopOnlinePlayers(server, sender, false, true, args[0], null);
            return;
        }
        restPlayer(sender.getUser(ess));
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args) throws PlayerExemptException {
        restPlayer(player);
        sender.sendMessage(tl("restOther", player.getName()));
    }

    private void restPlayer(final IUser user) {
        user.getBase().setStatistic(Statistic.TIME_SINCE_REST, 0);
        user.sendMessage(tl("rest"));
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1 && sender.isAuthorized("essentials.rest.others", ess)) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
