package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.CommonPlaceholders;
import net.ess3.api.IUser;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Commandext extends EssentialsLoopCommand {
    public Commandext() {
        super("ext");
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        loopOnlinePlayers(server, sender, true, true, args[0], null);
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length > 0 && user.isAuthorized("essentials.ext.others")) {
            loopOnlinePlayers(server, user.getSource(), true, true, args[0], null);
            return;
        }

        extPlayer(user.getBase());
        user.sendTl("extinguish");
    }

    @Override
    protected void updatePlayer(final Server server, final CommandSource sender, final User player, final String[] args) {
        extPlayer(player.getBase());
        sender.sendTl("extinguishOthers", CommonPlaceholders.displayName((IUser) player));
    }

    private void extPlayer(final Player player) {
        player.setFireTicks(0);
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return getPlayers(server, sender);
        } else {
            return Collections.emptyList();
        }
    }
}
