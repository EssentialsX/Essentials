package com.earth2me.essentials.commands;

import com.earth2me.essentials.AsyncTeleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.CommonPlaceholders;
import net.ess3.api.TranslatableException;
import net.ess3.api.events.TPARequestEvent;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Commandtpa extends EssentialsCommand {
    public Commandtpa() {
        super("tpa");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }

        final User player = getPlayer(server, user, args, 0);
        if (user.getName().equalsIgnoreCase(player.getName())) {
            throw new NotEnoughArgumentsException();
        }
        if (!player.isAuthorized("essentials.tpaccept")) {
            throw new TranslatableException("teleportNoAcceptPermission", CommonPlaceholders.displayName(player));
        }
        if (!player.isTeleportEnabled()) {
            throw new TranslatableException("teleportDisabled", CommonPlaceholders.displayName(player));
        }
        if (user.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + player.getWorld().getName())) {
            throw new TranslatableException("noPerm", "essentials.worlds." + player.getWorld().getName());
        }

        // Don't let sender request teleport twice to the same player.
        if (player.hasOutstandingTpaRequest(user.getName(), false)) {
            throw new TranslatableException("requestSentAlready", CommonPlaceholders.displayName(player));
        }

        if (player.isAutoTeleportEnabled() && !player.isIgnoredPlayer(user)) {
            final Trade charge = new Trade(this.getName(), ess);
            final AsyncTeleport teleport = user.getAsyncTeleport();
            teleport.setTpType(AsyncTeleport.TeleportType.TPA);
            final CompletableFuture<Boolean> future = getNewExceptionFuture(user.getSource(), commandLabel);
            teleport.teleport(player.getBase(), charge, PlayerTeleportEvent.TeleportCause.COMMAND, future);
            future.thenAccept(success -> {
                if (success) {
                    player.sendTl("requestAcceptedAuto", CommonPlaceholders.displayName(user));
                    user.sendTl("requestAcceptedFromAuto", CommonPlaceholders.displayName(player));
                }
            });
            throw new NoChargeException();
        }

        if (!player.isIgnoredPlayer(user)) {
            final TPARequestEvent tpaEvent = new TPARequestEvent(user.getSource(), player, false);
            ess.getServer().getPluginManager().callEvent(tpaEvent);
            if (tpaEvent.isCancelled()) {
                throw new TranslatableException("teleportRequestCancelled", CommonPlaceholders.displayName(player));
            }
            player.requestTeleport(user, false);
            player.sendTl("teleportRequest", CommonPlaceholders.displayName(user));
            player.sendTl("typeTpaccept");
            player.sendTl("typeTpdeny");
            if (ess.getSettings().getTpaAcceptCancellation() != 0) {
                player.sendTl("teleportRequestTimeoutInfo", ess.getSettings().getTpaAcceptCancellation());
            }
        }

        user.sendTl("requestSent", CommonPlaceholders.displayName(player));
        if (user.isAuthorized("essentials.tpacancel")) {
            user.sendTl("typeTpacancel");
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
