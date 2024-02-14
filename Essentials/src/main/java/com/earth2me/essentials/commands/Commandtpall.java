package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collections;
import java.util.List;

public class Commandtpall extends EssentialsCommand {
    public Commandtpall() {
        super("tpall");
    }

    @Override
    public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            if (sender.isPlayer()) {
                teleportAllPlayers(server, sender, ess.getUser(sender.getPlayer()), commandLabel);
                return;
            }
            throw new NotEnoughArgumentsException();
        }

        final User target = getPlayer(server, sender, args, 0);
        teleportAllPlayers(server, sender, target, commandLabel);
    }

    private void teleportAllPlayers(final Server server, final CommandSource sender, final User target, final String label) {
        sender.sendTl("teleportAll");
        final Location loc = target.getLocation();
        for (final User player : ess.getOnlineUsers()) {
            if (target == player) {
                continue;
            }
            if (sender.getSender().equals(target.getBase()) && target.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions() && !target.isAuthorized("essentials.worlds." + target.getWorld().getName())) {
                continue;
            }
            player.getAsyncTeleport().now(loc, false, TeleportCause.COMMAND, getNewExceptionFuture(sender, label));
        }
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
