package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.CommonPlaceholders;
import net.ess3.api.TranslatableException;
import net.ess3.api.events.TPARequestEvent;
import org.bukkit.Server;

import java.util.Collections;
import java.util.List;

public class Commandtpahere extends EssentialsCommand {
    public Commandtpahere() {
        super("tpahere");
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
        if (user.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions() && !user.isAuthorized("essentials.worlds." + user.getWorld().getName())) {
            throw new TranslatableException("noPerm", "essentials.worlds." + user.getWorld().getName());
        }

        // Don't let sender request teleport twice to the same player.
        if (player.hasOutstandingTpaRequest(user.getName(), true)) {
            throw new TranslatableException("requestSentAlready", CommonPlaceholders.displayName(player));
        }

        if (!player.isIgnoredPlayer(user)) {
            final TPARequestEvent tpaEvent = new TPARequestEvent(user.getSource(), player, true);
            ess.getServer().getPluginManager().callEvent(tpaEvent);
            if (tpaEvent.isCancelled()) {
                throw new TranslatableException("teleportRequestCancelled", CommonPlaceholders.displayName(player));
            }
            player.requestTeleport(user, true);
            player.sendTl("teleportHereRequest", CommonPlaceholders.displayName(user));
            player.sendTl("typeTpaccept");
            player.sendTl("typeTpdeny");
            if (ess.getSettings().getTpaAcceptCancellation() != 0) {
                player.sendTl("teleportRequestTimeoutInfo", ess.getSettings().getTpaAcceptCancellation());
            }
        }
        user.sendTl("requestSent", CommonPlaceholders.displayName(player));
        user.sendTl("typeTpacancel");
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
