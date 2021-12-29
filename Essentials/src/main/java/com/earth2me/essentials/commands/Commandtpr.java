package com.earth2me.essentials.commands;

import com.earth2me.essentials.RandomTeleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.events.UserRandomTeleportEvent;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Commandtpr extends EssentialsCommand {

    public Commandtpr() {
        super("tpr");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        final RandomTeleport randomTeleport = ess.getRandomTeleport();
        final UserRandomTeleportEvent event = new UserRandomTeleportEvent(user, randomTeleport.getCenter(), randomTeleport.getMinRange(), randomTeleport.getMaxRange());
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        randomTeleport.getRandomLocation(event.getCenter(), event.getMinRange(), event.getMaxRange()).thenAccept(location -> {
            final CompletableFuture<Boolean> future = getNewExceptionFuture(user.getSource(), commandLabel);
            user.getAsyncTeleport().teleport(location, charge, PlayerTeleportEvent.TeleportCause.COMMAND, future);
            future.thenAccept(success -> {
                if (success) {
                    user.sendTl("tprSuccess");
                }
            });
        });
        throw new NoChargeException();
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        return Collections.emptyList();
    }
}
