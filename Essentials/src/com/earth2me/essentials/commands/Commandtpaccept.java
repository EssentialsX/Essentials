package com.earth2me.essentials.commands;

import com.earth2me.essentials.AsyncTeleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.concurrent.CompletableFuture;

import static com.earth2me.essentials.I18n.tl;


public class Commandtpaccept extends EssentialsCommand {
    public Commandtpaccept() {
        super("tpaccept");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final User requester;
        try {
            requester = ess.getUser(user.getTeleportRequest());
        } catch (Exception ex) {
            throw new Exception(tl("noPendingRequest"));
        }

        if (!requester.getBase().isOnline()) {
            throw new Exception(tl("noPendingRequest"));
        }

        if (user.isTpRequestHere() && ((!requester.isAuthorized("essentials.tpahere") && !requester.isAuthorized("essentials.tpaall")) || (user.getWorld() != requester.getWorld() && ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + user.getWorld().getName())))) {
            throw new Exception(tl("noPendingRequest"));
        }

        if (!user.isTpRequestHere() && (!requester.isAuthorized("essentials.tpa") || (user.getWorld() != requester.getWorld() && ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + requester.getWorld().getName())))) {
            throw new Exception(tl("noPendingRequest"));
        }

        if (args.length > 0 && !requester.getName().contains(args[0])) {
            throw new Exception(tl("noPendingRequest"));
        }

        if (!user.hasOutstandingTeleportRequest()) {
            user.requestTeleport(null, false);
            throw new Exception(tl("requestTimedOut"));
        }

        final Trade charge = new Trade(this.getName(), ess);
        user.sendMessage(tl("requestAccepted"));
        requester.sendMessage(tl("requestAcceptedFrom", user.getDisplayName()));

        CompletableFuture<Boolean> future = getNewExceptionFuture(requester.getSource(), commandLabel);
        future.exceptionally(e -> {
            user.sendMessage(tl("pendingTeleportCancelled"));
            return false;
        });
        future.thenAccept(success -> {
           if (success) {
               user.requestTeleport(null, false);
           }
        });
        if (user.isTpRequestHere()) {
            final Location loc = user.getTpRequestLocation();
            AsyncTeleport teleport = (AsyncTeleport) requester.getAsyncTeleport();
            teleport.setTpType(AsyncTeleport.TeleportType.TPA);
            future.thenAccept(success -> {
                if (success) {
                    requester.sendMessage(tl("teleporting", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                }
            });
            teleport.teleportPlayer(user, user.getTpRequestLocation(), charge, TeleportCause.COMMAND, future);
        } else {
            AsyncTeleport teleport = (AsyncTeleport) requester.getAsyncTeleport();
            teleport.setTpType(AsyncTeleport.TeleportType.TPA);
            teleport.teleport(user.getBase(), charge, TeleportCause.COMMAND, future);
        }
    }

}
