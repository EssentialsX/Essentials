package com.earth2me.essentials.spawn;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.concurrent.CompletableFuture;

import static com.earth2me.essentials.I18n.tl;


public class Commandspawn extends EssentialsCommand {
    public Commandspawn() {
        super("spawn");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        if (args.length > 0 && user.isAuthorized("essentials.spawn.others")) {
            final User otherUser = getPlayer(server, user, args, 0);
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.thenAccept(success -> {
                if (success) {
                    if (!otherUser.equals(user)) {
                        otherUser.sendMessage(tl("teleportAtoB", user.getDisplayName(), "spawn"));
                    }
                }
            });
            respawn(user.getSource(), user, otherUser, charge, commandLabel, future);
        } else {
            respawn(user.getSource(), user, user, charge, commandLabel, new CompletableFuture<>());
        }
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length < 1) {
            throw new NotEnoughArgumentsException();
        }
        final User user = getPlayer(server, args, 0, true, false);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        respawn(sender, null, user, null, commandLabel, future);
        future.thenAccept(success -> {
            if (success) {
                user.sendMessage(tl("teleportAtoB", Console.NAME, "spawn"));
            }
        });
    }

    private void respawn(final CommandSource sender, final User teleportOwner, final User teleportee, final Trade charge, String commandLabel, CompletableFuture<Boolean> future) throws Exception {
        final SpawnStorage spawns = (SpawnStorage) this.module;
        final Location spawn = spawns.getSpawn(teleportee.getGroup());
        sender.sendMessage(tl("teleporting", spawn.getWorld().getName(), spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()));
        CompletableFuture<Exception> eFuture = new CompletableFuture<>();
        eFuture.thenAccept(e -> showError(sender.getSender(), e, commandLabel));
        if (teleportOwner == null) {
            teleportee.getAsyncTeleport().now(spawn, false, TeleportCause.COMMAND, eFuture, future);
        } else {
            teleportOwner.getAsyncTeleport().teleportPlayer(teleportee, spawn, charge, TeleportCause.COMMAND, eFuture, future);
        }
    }
}
