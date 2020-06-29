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
import java.util.Queue;
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
        getRandomLocation(randomTeleport, event.getCenter(), event.getMinRange(), event.getMaxRange()).thenAccept(location -> {
            CompletableFuture<Boolean> future = getNewExceptionFuture(user.getSource(), commandLabel);
            user.getAsyncTeleport().teleport(location, charge, PlayerTeleportEvent.TeleportCause.COMMAND, future);
            future.thenAccept(success -> {
                if (success) {
                    user.sendMessage(tl("tprSuccess"));
                }
            });
        });
    }

    // Get a random location; cached if possible. Otherwise on demand.
    private CompletableFuture<Location> getRandomLocation(RandomTeleport randomTeleport, Location center, double minRange, double maxRange) {
        int findAttempts = randomTeleport.getFindAttempts();
        Queue<Location> cachedLocations = randomTeleport.getCachedLocations();
        // Try to build up the cache if it is below the threshold
        if (cachedLocations.size() < randomTeleport.getCacheThreshold()) {
            ess.getServer().getScheduler().scheduleSyncDelayedTask(ess, () -> {
                for (int i = 0; i < findAttempts; ++i) {
                    calculateRandomLocation(center, minRange, maxRange).thenAccept(location -> {
                        if (isValidRandomLocation(randomTeleport, location)) {
                            randomTeleport.getCachedLocations().add(location);
                        }
                    });
                }
            });
        }
        CompletableFuture<Location> future = new CompletableFuture<>();
        // Return a random location immediately if one is available, otherwise try to find one now
        if (cachedLocations.isEmpty()) {
            attemptRandomLocation(findAttempts, randomTeleport, center, minRange, maxRange).thenAccept(future::complete);
        } else {
            future.complete(cachedLocations.poll());
        }
        return future;
    }

    // Recursively attempt to find a random location. After a maximum number of attempts, the center is returned.
    private CompletableFuture<Location> attemptRandomLocation(int attempts, RandomTeleport randomTeleport, Location center, double minRange, double maxRange) {
        CompletableFuture<Location> future = new CompletableFuture<>();
        if (attempts > 0) {
            calculateRandomLocation(center, minRange, maxRange).thenAccept(location -> {
                if (isValidRandomLocation(randomTeleport, location)) {
                    future.complete(location);
                } else {
                    attemptRandomLocation(attempts - 1, randomTeleport, center, minRange, maxRange).thenAccept(future::complete);
                }
            });
        } else {
            future.complete(center);
        }
        return future;
    }

    // Calculates a random location asynchronously.
    private CompletableFuture<Location> calculateRandomLocation(Location center, double minRange, double maxRange) {
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

    private boolean isValidRandomLocation(RandomTeleport randomTeleport, Location location) {
        return !randomTeleport.getExcludedBiomes().contains(location.getBlock().getBiome());
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        return Collections.emptyList();
    }
}
