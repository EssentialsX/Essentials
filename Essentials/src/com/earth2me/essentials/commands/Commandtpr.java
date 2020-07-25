package com.earth2me.essentials.commands;

import com.earth2me.essentials.RandomTeleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.events.UserRandomTeleportEvent;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.earth2me.essentials.I18n.tl;


public class Commandtpr extends EssentialsCommand {

    public Commandtpr() {
        super("tpr");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        RandomTeleport randomTeleport = ess.getRandomTeleport();
        World world = randomTeleport.getPerWorld() ? user.getWorld() : null;
        UserRandomTeleportEvent event = new UserRandomTeleportEvent(user, world, randomTeleport.getCenter(world), randomTeleport.getMinRange(world), randomTeleport.getMaxRange(world));
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        randomTeleport.getRandomLocation(event.getCenter(), event.getMinRange(), event.getMaxRange()).thenAccept(location -> {
            CompletableFuture<Boolean> future = getNewExceptionFuture(user.getSource(), commandLabel);
            user.getAsyncTeleport().teleport(location, charge, PlayerTeleportEvent.TeleportCause.COMMAND, future);
            future.thenAccept(success -> {
                if (success) {
                    user.sendMessage(tl("tprSuccess"));
                }
            });
        });
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        return Collections.emptyList();
    }
}
