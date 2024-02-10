package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import net.ess3.api.TranslatableException;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent;

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
                user.sendTl("teleportOfflineUnknown", user.getDisplayName());
                throw new NoChargeException();
            }

            if (user.getWorld() != logout.getWorld() && ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + logout.getWorld().getName())) {
                throw new TranslatableException("noPerm", "essentials.worlds." + logout.getWorld().getName());
            }

            user.sendTl("teleporting", logout.getWorld().getName(), logout.getBlockX(), logout.getBlockY(), logout.getBlockZ());
            user.getAsyncTeleport().now(logout, false, PlayerTeleportEvent.TeleportCause.COMMAND, getNewExceptionFuture(user.getSource(), label));
        }
    }
}

