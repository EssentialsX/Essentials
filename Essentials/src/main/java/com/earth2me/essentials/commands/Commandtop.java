package com.earth2me.essentials.commands;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.concurrent.CompletableFuture;

import static com.earth2me.essentials.I18n.tl;

public class Commandtop extends EssentialsCommand {
    public Commandtop() {
        super("top");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final int topX = user.getLocation().getBlockX();
        final int topZ = user.getLocation().getBlockZ();
        final float pitch = user.getLocation().getPitch();
        final float yaw = user.getLocation().getYaw();
        final Location unsafe = new Location(user.getWorld(), topX, ess.getWorldInfoProvider().getMaxHeight(user.getWorld()), topZ, yaw, pitch);
        final Location safe = LocationUtil.getSafeDestination(ess, unsafe);
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.thenAccept(success -> {
            if (success) {
                user.sendMessage(tl("teleportTop", safe.getWorld().getName(), safe.getBlockX(), safe.getBlockY(), safe.getBlockZ()));
            }
        });
        user.getAsyncTeleport().teleport(safe, new Trade(this.getName(), ess), TeleportCause.COMMAND, getNewExceptionFuture(user.getSource(), commandLabel));
    }
}
