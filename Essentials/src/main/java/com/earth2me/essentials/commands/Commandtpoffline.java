package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent;

import static com.earth2me.essentials.I18n.tl;

public class Commandtpoffline extends EssentialsCommand {

    public Commandtpoffline() {
        super("tpoffline");
    }

    @Override
    public void run(final Server server, final User user, final String label, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        } else {
            final User target = getPlayer(server, args, 0, true, true);
            final Location logout = target.getLogoutLocation();

            if (logout == null) {
                user.sendMessage(tl("teleportOfflineUnknown", user.getDisplayName()));
                throw new NoChargeException();
            }

            if (user.getWorld() != logout.getWorld() && ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + logout.getWorld().getName())) {
                throw new Exception(tl("noPerm", "essentials.worlds." + logout.getWorld().getName()));
            }

            user.sendMessage(tl("teleporting", logout.getWorld().getName(), logout.getBlockX(), logout.getBlockY(), logout.getBlockZ()));
            user.getAsyncTeleport().now(logout, false, PlayerTeleportEvent.TeleportCause.COMMAND, getNewExceptionFuture(user.getSource(), label));
        }
    }
}

