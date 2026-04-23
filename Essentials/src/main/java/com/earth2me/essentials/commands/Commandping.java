package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class Commandping extends EssentialsCommand {
    public Commandping() {
        super("ping");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            if (sender.isPlayer()) {
                final Player player = sender.getPlayer();
                final int ping = player.getPing();
                sender.sendTl(getOwnPingKey(ping), ping);
            } else {
                sender.sendTl("pong");
            }
        } else {
            final Player target = server.getPlayerExact(args[0]);
            if (target == null) {
                throw new PlayerNotFoundException();
            }
            final int ping = target.getPing();
            sender.sendTl(getPingKey(ping), target.getName(), ping);
        }
    }

    private static String getOwnPingKey(final int ping) {
        if (ping < 100) {
            return "pingOwnGood";
        } else if (ping < 200) {
            return "pingOwnMid";
        }
        return "pingOwnBad";
    }

    private static String getPingKey(final int ping) {
        if (ping < 100) {
            return "pingGood";
        } else if (ping < 200) {
            return "pingMid";
        }
        return "pingBad";
    }
}
