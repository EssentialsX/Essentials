package com.earth2me.essentials;

import com.earth2me.essentials.api.IAsyncTeleport;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.LocationUtil;
import io.papermc.lib.PaperLib;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import net.ess3.api.InvalidWorldException;
import net.ess3.api.events.UserWarpEvent;
import net.ess3.api.events.teleport.PreTeleportEvent;
import net.ess3.api.events.teleport.TeleportWarmupEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.earth2me.essentials.I18n.tl;


public class AsyncTeleport implements IAsyncTeleport {
    private final IUser teleportOwner;
    private final IEssentials ess;
    private AsyncTimedTeleport timedTeleport;

    private TeleportType tpType;

    public AsyncTeleport(IUser user, IEssentials ess) {
        this.teleportOwner = user;
        this.ess = ess;
        tpType = TeleportType.NORMAL;
    }

    public enum TeleportType {
        TPA,
        BACK,
        NORMAL
    }

    public void cooldown(boolean check) throws Throwable {
        CompletableFuture<Boolean> exceptionFuture = new CompletableFuture<>();
        if (cooldown(check, exceptionFuture)) {
            try {
                exceptionFuture.get();
            } catch (ExecutionException e) {
                throw e.getCause();
            }
        }
    }

    public boolean cooldown(boolean check, CompletableFuture<Boolean> future) {
        final Calendar time = new GregorianCalendar();
        if (teleportOwner.getLastTeleportTimestamp() > 0) {
            // Take the current time, and remove the delay from it.
            final double cooldown = ess.getSettings().getTeleportCooldown();
            final Calendar earliestTime = new GregorianCalendar();
            earliestTime.add(Calendar.SECOND, -(int) cooldown);
            earliestTime.add(Calendar.MILLISECOND, -(int) ((cooldown * 1000.0) % 1000.0));
            // This value contains the most recent time a teleportPlayer could have been used that would allow another use.
            final long earliestLong = earliestTime.getTimeInMillis();

            // When was the last teleportPlayer used?
            final long lastTime = teleportOwner.getLastTeleportTimestamp();

            if (lastTime > time.getTimeInMillis()) {
                // This is to make sure time didn't get messed up on last teleportPlayer use.
                // If this happens, let's give the user the benifit of the doubt.
                teleportOwner.setLastTeleportTimestamp(time.getTimeInMillis());
                return false;
            } else if (lastTime > earliestLong
                && cooldownApplies()) {
                time.setTimeInMillis(lastTime);
                time.add(Calendar.SECOND, (int) cooldown);
                time.add(Calendar.MILLISECOND, (int) ((cooldown * 1000.0) % 1000.0));
                future.completeExceptionally(new Exception(tl("timeBeforeTeleport", DateUtil.formatDateDiff(time.getTimeInMillis()))));
                return true;
            }
        }
        // if justCheck is set, don't update lastTeleport; we're just checking
        if (!check) {
            teleportOwner.setLastTeleportTimestamp(time.getTimeInMillis());
        }
        return false;
    }

    private boolean cooldownApplies() {
        boolean applies = true;
        String globalBypassPerm = "essentials.teleport.cooldown.bypass";
        switch (tpType) {
            case NORMAL:
                applies = !teleportOwner.isAuthorized(globalBypassPerm);
                break;
            case BACK:
                applies = !(teleportOwner.isAuthorized(globalBypassPerm) &&
                    teleportOwner.isAuthorized("essentials.teleport.cooldown.bypass.back"));
                break;
            case TPA:
                applies = !(teleportOwner.isAuthorized(globalBypassPerm) &&
                    teleportOwner.isAuthorized("essentials.teleport.cooldown.bypass.tpa"));
                break;
        }
        return applies;
    }

    private void warnUser(final IUser user, final double delay) {
        Calendar c = new GregorianCalendar();
        c.add(Calendar.SECOND, (int) delay);
        c.add(Calendar.MILLISECOND, (int) ((delay * 1000.0) % 1000.0));
        user.sendMessage(tl("dontMoveMessage", DateUtil.formatDateDiff(c.getTimeInMillis())));
    }


    @Override
    public void now(Location loc, boolean cooldown, TeleportCause cause, CompletableFuture<Boolean> future) {
        if (cooldown && cooldown(false, future)) {
            return;
        }
        final ITarget target = new LocationTarget(loc);
        nowAsync(teleportOwner, target, cause, future);
    }

    @Override
    public void now(Player entity, boolean cooldown, TeleportCause cause, CompletableFuture<Boolean> future) {
        if (cooldown && cooldown(false, future)) {
            future.complete(false);
            return;
        }
        final ITarget target = new PlayerTarget(entity);
        nowAsync(teleportOwner, target, cause, future);
        future.thenAccept(success -> {
            if (success) {
                teleportOwner.sendMessage(tl("teleporting", target.getLocation().getWorld().getName(), target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ()));
            }
        });
    }

    private void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
            return;
        }
        CompletableFuture<Object> taskLock = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(ess, () -> {
            runnable.run();
            taskLock.complete(new Object());
        });
        taskLock.get();
    }

    protected void nowAsync(IUser teleportee, ITarget target, TeleportCause cause, CompletableFuture<Boolean> future) {
        cancel(false);

        PreTeleportEvent event = new PreTeleportEvent(teleportee, cause, target);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        teleportee.setLastLocation();

        if (!ess.getSettings().isForcePassengerTeleport() && !teleportee.getBase().isEmpty()) {
            if (!ess.getSettings().isTeleportPassengerDismount()) {
                future.completeExceptionally(new Exception(tl("passengerTeleportFail")));
                return;
            }

            try {
                runOnMain(() -> teleportee.getBase().eject()); //EntityDismountEvent requires a sync context.
            } catch (ExecutionException | InterruptedException e) {
                future.completeExceptionally(e);
                return;
            }
        }
        teleportee.setLastLocation();
        final Location targetLoc = target.getLocation();
        if (ess.getSettings().isTeleportSafetyEnabled() && LocationUtil.isBlockOutsideWorldBorder(targetLoc.getWorld(), targetLoc.getBlockX(), targetLoc.getBlockZ())) {
            targetLoc.setX(LocationUtil.getXInsideWorldBorder(targetLoc.getWorld(), targetLoc.getBlockX()));
            targetLoc.setZ(LocationUtil.getZInsideWorldBorder(targetLoc.getWorld(), targetLoc.getBlockZ()));
        }
        PaperLib.getChunkAtAsync(targetLoc).thenAccept(chunk -> {
            Location loc = targetLoc;
            if (LocationUtil.isBlockUnsafeForUser(teleportee, chunk.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                if (ess.getSettings().isTeleportSafetyEnabled()) {
                    if (ess.getSettings().isForceDisableTeleportSafety()) {
                        //The chunk we're teleporting to is 100% going to be loaded here, no need to teleport async.
                        teleportee.getBase().teleport(loc, cause);
                    } else {
                        try {
                            //There's a chance the safer location is outside the loaded chunk so still teleport async here.
                            PaperLib.teleportAsync(teleportee.getBase(), LocationUtil.getSafeDestination(ess, teleportee, loc), cause);
                        } catch (Exception e) {
                            future.completeExceptionally(e);
                            return;
                        }
                    }
                } else {
                    future.completeExceptionally(new Exception(tl("unsafeTeleportDestination", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));
                    return;
                }
            } else {
                if (ess.getSettings().isForceDisableTeleportSafety()) {
                    //The chunk we're teleporting to is 100% going to be loaded here, no need to teleport async.
                    teleportee.getBase().teleport(loc, cause);
                } else {
                    if (ess.getSettings().isTeleportToCenterLocation()) {
                        loc = LocationUtil.getRoundedDestination(loc);
                    }
                    //There's a *small* chance the rounded destination produces a location outside the loaded chunk so still teleport async here.
                    PaperLib.teleportAsync(teleportee.getBase(), loc, cause);
                }
            }
            future.complete(true);
        });
    }

    @Override
    public void teleport(Location loc, Trade chargeFor, TeleportCause cause, CompletableFuture<Boolean> future) {
        teleport(teleportOwner, new LocationTarget(loc), chargeFor, cause, future);
    }

    @Override
    public void teleport(Player entity, Trade chargeFor, TeleportCause cause, CompletableFuture<Boolean> future) {
        teleportOwner.sendMessage(tl("teleportToPlayer", entity.getDisplayName()));
        teleport(teleportOwner, new PlayerTarget(entity), chargeFor, cause, future);
    }

    @Override
    public void teleportPlayer(IUser otherUser, Location loc, Trade chargeFor, TeleportCause cause, CompletableFuture<Boolean> future) {
        teleport(otherUser, new LocationTarget(loc), chargeFor, cause, future);
    }

    @Override
    public void teleportPlayer(IUser otherUser, Player entity, Trade chargeFor, TeleportCause cause, CompletableFuture<Boolean> future) {
        ITarget target = new PlayerTarget(entity);
        teleport(otherUser, target, chargeFor, cause, future);
        future.thenAccept(success -> {
            if (success) {
                otherUser.sendMessage(tl("teleporting", target.getLocation().getWorld().getName(), target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ()));
                teleportOwner.sendMessage(tl("teleporting", target.getLocation().getWorld().getName(), target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ()));
            }
        });
    }

    private void teleport(IUser teleportee, ITarget target, Trade chargeFor, TeleportCause cause, CompletableFuture<Boolean> future) {
        double delay = ess.getSettings().getTeleportDelay();

        TeleportWarmupEvent event = new TeleportWarmupEvent(teleportee, cause, target, delay);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        delay = event.getDelay();

        Trade cashCharge = chargeFor;

        if (chargeFor != null) {
            chargeFor.isAffordableFor(teleportOwner, future);
            if (future.isCompletedExceptionally()) {
                return;
            }

            //This code is to make sure that commandcosts are checked in the initial world, and not in the resulting world.
            if (!chargeFor.getCommandCost(teleportOwner).equals(BigDecimal.ZERO)) {
                //By converting a command cost to a regular cost, the command cost permission isn't checked when executing the charge after teleport.
                cashCharge = new Trade(chargeFor.getCommandCost(teleportOwner), ess);
            }
        }

        if (cooldown(true, future)) {
            return;
        }
        if (delay <= 0 || teleportOwner.isAuthorized("essentials.teleport.timer.bypass") || teleportee.isAuthorized("essentials.teleport.timer.bypass")) {
            if (cooldown(false, future)) {
                return;
            }
            nowAsync(teleportee, target, cause, future);
            if (cashCharge != null) {
                cashCharge.charge(teleportOwner, future);
                if (future.isCompletedExceptionally()) {
                    return;
                }
            }
            return;
        }

        cancel(false);
        warnUser(teleportee, delay);
        initTimer((long) (delay * 1000.0), teleportee, target, cashCharge, cause, false);
    }

    private void teleportOther(IUser teleporter, IUser teleportee, ITarget target, Trade chargeFor, TeleportCause cause, CompletableFuture<Boolean> future) {
        double delay = ess.getSettings().getTeleportDelay();

        TeleportWarmupEvent event = new TeleportWarmupEvent(teleportee, cause, target, delay);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        delay = event.getDelay();

        Trade cashCharge = chargeFor;

        if (teleporter != null && chargeFor != null) {
            chargeFor.isAffordableFor(teleporter, future);
            if (future.isCompletedExceptionally()) {
                return;
            }

            //This code is to make sure that commandcosts are checked in the initial world, and not in the resulting world.
            if (!chargeFor.getCommandCost(teleporter).equals(BigDecimal.ZERO)) {
                //By converting a command cost to a regular cost, the command cost permission isn't checked when executing the charge after teleport.
                cashCharge = new Trade(chargeFor.getCommandCost(teleporter), ess);
            }
        }

        if (cooldown(true, future)) {
            return;
        }
        if (delay <= 0 || teleporter == null
            || teleporter.isAuthorized("essentials.teleport.timer.bypass")
            || teleportOwner.isAuthorized("essentials.teleport.timer.bypass")
            || teleportee.isAuthorized("essentials.teleport.timer.bypass")) {
            if (cooldown(false, future)) {
                return;
            }

            nowAsync(teleportee, target, cause, future);
            if (teleporter != null && cashCharge != null) {
                cashCharge.charge(teleporter, future);
                if (future.isCompletedExceptionally()) {
                    return;
                }
            }
            return;
        }

        cancel(false);
        warnUser(teleportee, delay);
        initTimer((long) (delay * 1000.0), teleportee, target, cashCharge, cause, false);
    }

    @Override
    public void respawn(Trade chargeFor, TeleportCause cause, CompletableFuture<Boolean> future) {
        double delay = ess.getSettings().getTeleportDelay();

        TeleportWarmupEvent event = new TeleportWarmupEvent(teleportOwner, cause, null, delay);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        delay = event.getDelay();

        if (chargeFor != null) {
            chargeFor.isAffordableFor(teleportOwner, future);
            if (future.isCompletedExceptionally()) {
                return;
            }
        }
        if (cooldown(true, future)) {
            return;
        }
        if (delay <= 0 || teleportOwner.isAuthorized("essentials.teleport.timer.bypass")) {
            if (cooldown(false, future)) {
                return;
            }
            respawnNow(teleportOwner, cause, future);
            if (chargeFor != null) {
                chargeFor.charge(teleportOwner, future);
            }
            return;
        }

        cancel(false);
        warnUser(teleportOwner, delay);
        initTimer((long) (delay * 1000.0), teleportOwner, null, chargeFor, cause, true);
    }

    void respawnNow(IUser teleportee, TeleportCause cause, CompletableFuture<Boolean> future) {
        final Player player = teleportee.getBase();
        PaperLib.getBedSpawnLocationAsync(player, true).thenAccept(location -> {
            if (location != null) {
                nowAsync(teleportee, new LocationTarget(location), cause, future);
            } else {
                if (ess.getSettings().isDebug()) {
                    ess.getLogger().info("Could not find bed spawn, forcing respawn event.");
                }
                final PlayerRespawnEvent pre = new PlayerRespawnEvent(player, player.getWorld().getSpawnLocation(), false);
                ess.getServer().getPluginManager().callEvent(pre);
                nowAsync(teleportee, new LocationTarget(pre.getRespawnLocation()), cause, future);
            }
        });
    }

    @Override
    public void warp(IUser otherUser, String warp, Trade chargeFor, TeleportCause cause, CompletableFuture<Boolean> future) {
        UserWarpEvent event = new UserWarpEvent(otherUser, warp, chargeFor);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        warp = event.getWarp();
        Location loc;
        try {
            loc = ess.getWarps().getWarp(warp);
        } catch (WarpNotFoundException | InvalidWorldException e) {
            future.completeExceptionally(e);
            return;
        }
        otherUser.sendMessage(tl("warpingTo", warp, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        if (!otherUser.equals(teleportOwner)) {
            teleportOwner.sendMessage(tl("warpingTo", warp, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        }
        teleport(otherUser, new LocationTarget(loc), chargeFor, cause, future);
    }

    @Override
    public void back(Trade chargeFor, CompletableFuture<Boolean> future) {
        back(teleportOwner, chargeFor, future);
    }

    @Override
    public void back(IUser teleporter, Trade chargeFor, CompletableFuture<Boolean> future) {
        tpType = TeleportType.BACK;
        final Location loc = teleportOwner.getLastLocation();
        teleportOwner.sendMessage(tl("backUsageMsg", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        teleportOther(teleporter, teleportOwner, new LocationTarget(loc), chargeFor, TeleportCause.COMMAND, future);
    }

    @Override
    public void back(CompletableFuture<Boolean> future) {
        nowAsync(teleportOwner, new LocationTarget(teleportOwner.getLastLocation()), TeleportCause.COMMAND, future);
    }

    public void setTpType(TeleportType tpType) {
        this.tpType = tpType;
    }

    //If we need to cancelTimer a pending teleportPlayer call this method
    private void cancel(boolean notifyUser) {
        if (timedTeleport != null) {
            timedTeleport.cancelTimer(notifyUser);
            timedTeleport = null;
        }
    }

    private void initTimer(long delay, IUser teleportUser, ITarget target, Trade chargeFor, TeleportCause cause, boolean respawn) {
        timedTeleport = new AsyncTimedTeleport(teleportOwner, ess, this, delay, teleportUser, target, chargeFor, cause, respawn);
    }
}
