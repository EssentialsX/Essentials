package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.LocationUtil;
import net.ess3.provider.WorldInfoProvider;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.CompletableFuture;

public class Commandbottom extends EssentialsCommand {

    public Commandbottom() {
        super("bottom");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final int bottomX = user.getLocation().getBlockX();
        final int bottomZ = user.getLocation().getBlockZ();
        final float pitch = user.getLocation().getPitch();
        final float yaw = user.getLocation().getYaw();
        final Location unsafe = new Location(user.getWorld(), bottomX, ess.provider(WorldInfoProvider.class).getMinHeight(user.getWorld()), bottomZ, yaw, pitch);
        final Location safe = LocationUtil.getSafeDestination(ess, unsafe);
        final CompletableFuture<Boolean> future = getNewExceptionFuture(user.getSource(), commandLabel);
        future.thenAccept(success -> {
            if (success) {
                user.sendTl("teleportBottom", safe.getWorld().getName(), safe.getBlockX(), safe.getBlockY(), safe.getBlockZ());
            }
        });
        user.getAsyncTeleport().teleport(safe, new Trade(this.getName(), ess), PlayerTeleportEvent.TeleportCause.COMMAND, future);
    }
}
