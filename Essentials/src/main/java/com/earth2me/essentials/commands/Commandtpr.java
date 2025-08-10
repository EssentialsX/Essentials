package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.RandomTeleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.TranslatableException;
import net.ess3.api.events.UserRandomTeleportEvent;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Commandtpr extends EssentialsCommand {

    public Commandtpr() {
        super("tpr");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        final RandomTeleport randomTeleport = ess.getRandomTeleport();

        final String randomLocationName;
        final User target;
        if (args.length == 0) {
            // No arguments provided, use the default random teleport location
            randomLocationName = randomTeleport.getDefaultLocation().replace("{world}", user.getLocation().getWorld().getName());
            target = user;
        } else {
            // Use the first argument as the location name
            randomLocationName = args[0];
            if (!randomTeleport.hasLocation(randomLocationName)) {
                throw new TranslatableException("tprNotExist");
            }

            if (randomTeleport.isPerLocationPermission() && !user.isAuthorized("essentials.tpr.location." + randomLocationName)) {
                throw new TranslatableException("tprNoPermission");
            }

            if (args.length > 1 && user.isAuthorized("essentials.tpr.others")) {
                target = getPlayer(server, user, args, 1);
            } else {
                target = user;
            }
        }

        final UserRandomTeleportEvent event = new UserRandomTeleportEvent(target, randomLocationName, randomTeleport.getCenter(randomLocationName), randomTeleport.getMinRange(randomLocationName), randomTeleport.getMaxRange(randomLocationName));
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        target.sendTl("tprSuccess");
        if (target != user) {
            user.sendTl("tprOtherUser", target.getDisplayName());
        }

        (event.isModified() ? randomTeleport.getRandomLocation(event.getCenter(), event.getMinRange(), event.getMaxRange()) : randomTeleport.getRandomLocation(randomLocationName))
                .thenAccept(location -> {
                    final CompletableFuture<Boolean> future = getNewExceptionFuture(user.getSource(), commandLabel);
                    future.thenAccept(success -> {
                        if (success) {
                            target.sendTl("tprSuccessDone");
                        }
                    });
                    target.getAsyncTeleport().teleport(location, charge, PlayerTeleportEvent.TeleportCause.COMMAND, future);
                });
        throw new NoChargeException();
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 2) {
            throw new NotEnoughArgumentsException();
        }
        final RandomTeleport randomTeleport = ess.getRandomTeleport();
        final User userToTeleport = getPlayer(server, sender, args, 1);

        // Validate the location name - only use if it exists and sender has permission
        final String potentialLocation = args[0];
        if (!randomTeleport.hasLocation(potentialLocation)) {
            throw new TranslatableException("tprNotExist");
        }

        final UserRandomTeleportEvent event = new UserRandomTeleportEvent(userToTeleport, potentialLocation, randomTeleport.getCenter(potentialLocation), randomTeleport.getMinRange(potentialLocation), randomTeleport.getMaxRange(potentialLocation));
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        userToTeleport.sendTl("tprSuccess");
        sender.sendTl("tprOtherUser", userToTeleport.getDisplayName());
        (event.isModified() ? randomTeleport.getRandomLocation(event.getCenter(), event.getMinRange(), event.getMaxRange()) : randomTeleport.getRandomLocation(potentialLocation))
                .thenAccept(location -> {
                    final CompletableFuture<Boolean> future = getNewExceptionFuture(sender, commandLabel);
                    future.thenAccept(success -> {
                        if (success) {
                            userToTeleport.sendTl("tprSuccessDone");
                        }
                    });
                    userToTeleport.getAsyncTeleport().now(location, false, PlayerTeleportEvent.TeleportCause.COMMAND, future);
                });
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        final RandomTeleport randomTeleport = ess.getRandomTeleport();
        if (args.length == 1) {
            if (randomTeleport.isPerLocationPermission()) {
                return randomTeleport.listLocations().stream().filter(name -> sender.isAuthorized("essentials.tpr.location." + name)).collect(Collectors.toList());
            } else {
                return randomTeleport.listLocations();
            }
        } else if (args.length == 2 && sender.isAuthorized("essentials.tpr.others")) {
            return getPlayers(server, sender);
        }
        return Collections.emptyList();
    }
}
