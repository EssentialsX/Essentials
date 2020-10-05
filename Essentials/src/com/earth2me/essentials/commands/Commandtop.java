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
        final Location loc = LocationUtil.getSafeDestination(new Location(user.getWorld(), topX, user.getWorld().getMaxHeight(), topZ, yaw, pitch));
        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.thenAccept(success -> {
            if (success) {
                user.sendMessage(tl("teleportTop", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            }
        });
        user.getAsyncTeleport().teleport(loc, new Trade(this.getName(), ess), TeleportCause.COMMAND, getNewExceptionFuture(user.getSource(), commandLabel));
    }
}
