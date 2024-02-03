package com.earth2me.essentials.spawn;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import com.earth2me.essentials.utils.CommonPlaceholders;
import net.ess3.api.IUser;
import net.essentialsx.api.v2.events.UserTeleportSpawnEvent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
            final CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.thenAccept(success -> {
                if (success) {
                    if (!otherUser.equals(user)) {
                        otherUser.sendTl("teleportAtoB", CommonPlaceholders.displayName((IUser) user), "spawn");
                    }
                }
            });
            respawn(user.getSource(), user, otherUser, charge, commandLabel, future);
        } else {
            respawn(user.getSource(), user, user, charge, commandLabel, new CompletableFuture<>());
        }

        throw new NoChargeException();
    }

    @Override
    protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }
        final User user = getPlayer(server, args, 0, true, false);
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        respawn(sender, null, user, null, commandLabel, future);
        future.thenAccept(success -> {
            if (success) {
                user.sendTl("teleportAtoB", Console.DISPLAY_NAME, "spawn");
            }
        });
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final CommandSource sender, final String commandLabel, final String[] args) {
        if (args.length == 1 && sender.isAuthorized("essentials.spawn.others")) {
            return getPlayers(server, sender);
        }
        return Collections.emptyList();
    }

    private void respawn(final CommandSource sender, final User teleportOwner, final User teleportee, final Trade charge, final String commandLabel, final CompletableFuture<Boolean> future) throws Exception {
        final Location spawn = ((SpawnStorage) this.module).getSpawn(teleportee.getGroup());
        if (spawn == null) {
            return;
        }
        sender.sendTl("teleporting", spawn.getWorld().getName(), spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
        future.exceptionally(e -> {
            showError(sender.getSender(), e, commandLabel);
            return false;
        });
        final UserTeleportSpawnEvent spawnEvent = new UserTeleportSpawnEvent(teleportee, teleportOwner, teleportee.getGroup(), spawn);
        ess.getServer().getPluginManager().callEvent(spawnEvent);
        if (spawnEvent.isCancelled()) {
            return;
        }
        if (teleportOwner == null) {
            teleportee.getAsyncTeleport().now(spawn, false, TeleportCause.COMMAND, future);
            return;
        }
        teleportOwner.getAsyncTeleport().teleportPlayer(teleportee, spawn, charge, TeleportCause.COMMAND, future);
    }
}
