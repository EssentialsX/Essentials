package com.earth2me.essentials.commands;

import com.earth2me.essentials.RandomTeleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.VersionUtil;
import io.papermc.lib.PaperLib;
import net.ess3.api.events.UserRandomTeleportEvent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static com.earth2me.essentials.I18n.tl;


public class Commandtpr extends EssentialsCommand {
    private static final Random RANDOM = new Random();
    private static final int HIGHEST_BLOCK_Y_OFFSET = VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_15_R01) ? 1 : 0;

    public Commandtpr() {
        super("tpr");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        final Trade charge = new Trade(this.getName(), ess);
        charge.isAffordableFor(user);
        RandomTeleport randomTeleport = ess.getRandomTeleport();
        UserRandomTeleportEvent event = new UserRandomTeleportEvent(user, randomTeleport.getCenter(), randomTeleport.getMinRange(), randomTeleport.getMaxRange());
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Location center = event.getCenter();
        double minRange = event.getMinRange();
        double maxRange = event.getMaxRange();
        CompletableFuture<Boolean> future = getNewExceptionFuture(user.getSource(), commandLabel);
        calculateRandomLocationAround(center, minRange, maxRange).thenAccept(location -> {
            user.getAsyncTeleport().teleport(location, charge, PlayerTeleportEvent.TeleportCause.COMMAND, future);
            future.thenAccept(success -> {
                if (success) {
                    user.sendMessage(tl("tprSuccess"));
                }
            });
        });
    }

    private CompletableFuture<Location> calculateRandomLocationAround(Location center, double minRange, double maxRange) {
        Objects.requireNonNull(center.getWorld()); // TODO: remove when location setting works
        CompletableFuture<Location> future = new CompletableFuture<>();
        final int dx = RANDOM.nextBoolean() ? 1 : -1, dz = RANDOM.nextBoolean() ? 1 : -1;
        Location location = new Location(
                center.getWorld(),
                center.getX() + dx * (minRange + RANDOM.nextDouble() * (maxRange - minRange)),
                center.getWorld().getMaxHeight(),
                center.getZ() + dz * (minRange + RANDOM.nextDouble() * (maxRange - minRange)),
                360 * RANDOM.nextFloat() - 180,
                0
        );
        PaperLib.getChunkAtAsync(location).thenAccept(chunk -> {
            location.setY(center.getWorld().getHighestBlockYAt(location) + HIGHEST_BLOCK_Y_OFFSET);
            future.complete(location);
        });
        return future;
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        return Collections.emptyList();
    }
}
