package com.earth2me.essentials;

import com.earth2me.essentials.commands.WarpNotFoundException;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.LocationUtil;
import io.papermc.lib.PaperLib;
import net.ess3.api.IEssentials;
import net.ess3.api.IAsyncTeleport;
import net.ess3.api.IUser;
import net.ess3.api.InvalidWorldException;
import net.ess3.api.events.UserTeleportEvent;
import net.ess3.api.events.UserWarpEvent;
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
    private TimedTeleport timedTeleport;

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

    public void cooldown(boolean check) throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        if (cooldown(check, exceptionFuture)) {
            throw exceptionFuture.get();
        }
    }

    public boolean cooldown(boolean check, CompletableFuture<Exception> exceptionFuture) {
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
                exceptionFuture.complete(new Exception(tl("timeBeforeTeleport", DateUtil.formatDateDiff(time.getTimeInMillis()))));
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

    //The now function is used when you want to skip tp delay when teleporting someone to a location or player.
    @Override
    @Deprecated
    public void now(Location loc, boolean cooldown, TeleportCause cause) throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        now(loc, cooldown, cause, exceptionFuture, future);
        if (!future.get()) {
            throw exceptionFuture.get();
        }
    }

    @Override
    public void now(Location loc, boolean cooldown, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        if (cooldown && cooldown(false, exceptionFuture)) {
            future.complete(false);
            return;
        }
        final ITarget target = new LocationTarget(loc);
        nowAsync(teleportOwner, target, cause, exceptionFuture, future);
    }

    @Override
    @Deprecated
    public void now(Player entity, boolean cooldown, TeleportCause cause) throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        now(entity, cooldown, cause, exceptionFuture, future);
        if (!future.get()) {
            throw exceptionFuture.get();
        }
    }

    @Override
    public void now(Player entity, boolean cooldown, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        if (cooldown && cooldown(false, exceptionFuture)) {
            future.complete(false);
            return;
        }
        final ITarget target = new PlayerTarget(entity);
        nowAsync(teleportOwner, target, cause, exceptionFuture, future);
        future.thenAccept(success -> {
            if (success) {
                teleportOwner.sendMessage(tl("teleporting", target.getLocation().getWorld().getName(), target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ()));
            }
        });
    }

    protected void nowAsync(IUser teleportee, ITarget target, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        cancel(false);

        UserTeleportEvent event = new UserTeleportEvent(teleportee, cause, target.getLocation());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        teleportee.setLastLocation();

        if (!teleportee.getBase().isEmpty()) {
            if (!ess.getSettings().isTeleportPassengerDismount()) {
                exceptionFuture.complete(new Exception(tl("passengerTeleportFail")));
                future.complete(false);
                return;
            }
            CompletableFuture<Object> dismountFuture = new CompletableFuture<>();
            Bukkit.getScheduler().runTask(ess, () -> {
                teleportee.getBase().eject();
                dismountFuture.complete(new Object());
            });
            try {
                dismountFuture.get(); //EntityDismountEvent requires sync context we also want to wait for it to finish
            } catch (InterruptedException | ExecutionException e) {
                exceptionFuture.complete(e);
                future.complete(false);
                return;
            }
        }
        teleportee.setLastLocation();
        PaperLib.getChunkAtAsync(target.getLocation()).thenAccept(chunk -> {
            Location loc = target.getLocation();
            if (LocationUtil.isBlockUnsafeForUser(teleportee, chunk, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                if (ess.getSettings().isTeleportSafetyEnabled()) {
                    if (ess.getSettings().isForceDisableTeleportSafety()) {
                        //The chunk we're teleporting to is 100% going to be loaded here, no need to teleport async.
                        teleportee.getBase().teleport(loc, cause);
                    } else {
                        try {
                            //There's a chance the safer location is outside the loaded chunk so still teleport async here.
                            PaperLib.teleportAsync(teleportee.getBase(), LocationUtil.getSafeDestination(ess, teleportee, loc), cause);
                        } catch (Exception e) {
                            exceptionFuture.complete(e);
                            future.complete(false);
                            return;
                        }
                    }
                } else {
                    exceptionFuture.complete(new Exception(tl("unsafeTeleportDestination", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));
                    future.complete(false);
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

    //The teleportPlayer function is used when you want to normally teleportPlayer someone to a location or player.
    //This method is nolonger used internally and will be removed.
    @Deprecated
    @Override
    public void teleport(Location loc, Trade chargeFor) throws Exception {
        teleport(loc, chargeFor, TeleportCause.PLUGIN);
    }

    @Override
    @Deprecated
    public void teleport(Location loc, Trade chargeFor, TeleportCause cause) throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        teleport(loc, chargeFor, cause, exceptionFuture, future);
        if (!future.get()) {
            throw exceptionFuture.get();
        }
    }

    @Override
    public void teleport(Location loc, Trade chargeFor, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        teleport(teleportOwner, new LocationTarget(loc), chargeFor, cause, exceptionFuture, future);
    }

    //This is used when teleporting to a player
    @Override
    @Deprecated
    public void teleport(Player entity, Trade chargeFor, TeleportCause cause) throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        teleport(entity, chargeFor, cause, exceptionFuture, future);
        if (!future.get()) {
            throw exceptionFuture.get();
        }
    }

    @Override
    public void teleport(Player entity, Trade chargeFor, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        teleportOwner.sendMessage(tl("teleportToPlayer", entity.getDisplayName()));
        teleport(teleportOwner, new PlayerTarget(entity), chargeFor, cause, exceptionFuture, future);
    }

    //This is used when teleporting to stored location
    @Override
    @Deprecated
    public void teleportPlayer(IUser teleportee, Location loc, Trade chargeFor, TeleportCause cause) throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        teleportPlayer(teleportee, loc, chargeFor, cause, exceptionFuture, future);
        if (!future.get()) {
            throw exceptionFuture.get();
        }
    }

    @Override
    public void teleportPlayer(IUser otherUser, Location loc, Trade chargeFor, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        teleport(otherUser, new LocationTarget(loc), chargeFor, cause, exceptionFuture, future);
    }

    //This is used on /tphere
    @Override
    @Deprecated
    public void teleportPlayer(IUser teleportee, Player entity, Trade chargeFor, TeleportCause cause) throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        teleportPlayer(teleportee, entity, chargeFor, cause, exceptionFuture, future);
        if (!future.get()) {
            throw exceptionFuture.get();
        }
    }

    @Override
    public void teleportPlayer(IUser otherUser, Player entity, Trade chargeFor, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        ITarget target = new PlayerTarget(entity);
        teleport(otherUser, target, chargeFor, cause, exceptionFuture, future);
        future.thenAccept(success -> {
            if (success) {
                otherUser.sendMessage(tl("teleporting", target.getLocation().getWorld().getName(), target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ()));
                teleportOwner.sendMessage(tl("teleporting", target.getLocation().getWorld().getName(), target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ()));
            }
        });
    }

    private void teleport(IUser teleportee, ITarget target, Trade chargeFor, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        double delay = ess.getSettings().getTeleportDelay();

        Trade cashCharge = chargeFor;

        if (chargeFor != null) {
            chargeFor.isAffordableFor(teleportOwner, exceptionFuture);
            if (exceptionFuture.isDone()) {
                future.complete(false);
                return;
            }

            //This code is to make sure that commandcosts are checked in the initial world, and not in the resulting world.
            if (!chargeFor.getCommandCost(teleportOwner).equals(BigDecimal.ZERO)) {
                //By converting a command cost to a regular cost, the command cost permission isn't checked when executing the charge after teleport.
                cashCharge = new Trade(chargeFor.getCommandCost(teleportOwner), ess);
            }
        }

        if (cooldown(true, exceptionFuture)) {
            future.complete(false);
            return;
        }
        if (delay <= 0 || teleportOwner.isAuthorized("essentials.teleport.timer.bypass") || teleportee.isAuthorized("essentials.teleport.timer.bypass")) {
            if (cooldown(false, exceptionFuture)) {
                future.complete(false);
                return;
            }
            nowAsync(teleportee, target, cause, exceptionFuture, future);
            if (cashCharge != null) {
                cashCharge.charge(teleportOwner, exceptionFuture);
                if (exceptionFuture.isDone()) {
                    future.complete(false);
                    return;
                }
            }
            return;
        }

        cancel(false);
        warnUser(teleportee, delay);
        initTimer((long) (delay * 1000.0), teleportee, target, cashCharge, cause, false);
    }

    private void teleportOther(IUser teleporter, IUser teleportee, ITarget target, Trade chargeFor, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        double delay = ess.getSettings().getTeleportDelay();

        Trade cashCharge = chargeFor;

        if (teleporter != null && chargeFor != null) {
            chargeFor.isAffordableFor(teleporter, exceptionFuture);
            if (exceptionFuture.isDone()) {
                future.complete(false);
                return;
            }

            //This code is to make sure that commandcosts are checked in the initial world, and not in the resulting world.
            if (!chargeFor.getCommandCost(teleporter).equals(BigDecimal.ZERO)) {
                //By converting a command cost to a regular cost, the command cost permission isn't checked when executing the charge after teleport.
                cashCharge = new Trade(chargeFor.getCommandCost(teleporter), ess);
            }
        }

        if (cooldown(true, exceptionFuture)) {
            future.complete(false);
            return;
        }
        if (delay <= 0 || teleporter == null
            || teleporter.isAuthorized("essentials.teleport.timer.bypass")
            || teleportOwner.isAuthorized("essentials.teleport.timer.bypass")
            || teleportee.isAuthorized("essentials.teleport.timer.bypass")) {
            if (cooldown(false, exceptionFuture)) {
                future.complete(false);
                return;
            }

            nowAsync(teleportee, target, cause, exceptionFuture, future);
            if (teleporter != null && cashCharge != null) {
                cashCharge.charge(teleporter, exceptionFuture);
                if (exceptionFuture.isDone()) {
                    future.complete(false);
                    return;
                }
            }
            return;
        }

        cancel(false);
        warnUser(teleportee, delay);
        initTimer((long) (delay * 1000.0), teleportee, target, cashCharge, cause, false);
    }

    //The respawn function is a wrapper used to handle tp fallback, on /jail and /home
    @Override
    @Deprecated
    public void respawn(final Trade chargeFor, TeleportCause cause) throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        respawn(chargeFor, cause, exceptionFuture, future);
        if (!future.get()) {
            throw exceptionFuture.get();
        }
    }

    @Override
    public void respawn(Trade chargeFor, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        double delay = ess.getSettings().getTeleportDelay();
        if (chargeFor != null) {
            chargeFor.isAffordableFor(teleportOwner, exceptionFuture);
            if (exceptionFuture.isDone()) {
                future.complete(false);
                return;
            }
        }
        if (cooldown(true, exceptionFuture)) {
            future.complete(false);
            return;
        }
        if (delay <= 0 || teleportOwner.isAuthorized("essentials.teleport.timer.bypass")) {
            if (cooldown(false, exceptionFuture)) {
                future.complete(false);
                return;
            }
            respawnNow(teleportOwner, cause, exceptionFuture, future);
            if (chargeFor != null) {
                chargeFor.charge(teleportOwner, exceptionFuture);
            }
            return;
        }

        cancel(false);
        warnUser(teleportOwner, delay);
        initTimer((long) (delay * 1000.0), teleportOwner, null, chargeFor, cause, true);
    }

    void respawnNow(IUser teleportee, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        final Player player = teleportee.getBase();
        Location bed = player.getBedSpawnLocation();
        if (bed != null) {
            nowAsync(teleportee, new LocationTarget(bed), cause, exceptionFuture, future);
        } else {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info("Could not find bed spawn, forcing respawn event.");
            }
            final PlayerRespawnEvent pre = new PlayerRespawnEvent(player, player.getWorld().getSpawnLocation(), false);
            ess.getServer().getPluginManager().callEvent(pre);
            nowAsync(teleportee, new LocationTarget(pre.getRespawnLocation()), cause, exceptionFuture, future);
        }
    }

    //The warp function is a wrapper used to teleportPlayer a player to a /warp
    @Override
    @Deprecated
    public void warp(IUser teleportee, String warp, Trade chargeFor, TeleportCause cause) throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        warp(teleportee, warp, chargeFor, cause, exceptionFuture, future);
        if (!future.get()) {
            throw exceptionFuture.get();
        }
    }

    @Override
    public void warp(IUser otherUser, String warp, Trade chargeFor, TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
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
            exceptionFuture.complete(e);
            future.complete(false);
            return;
        }
        otherUser.sendMessage(tl("warpingTo", warp, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        if (!otherUser.equals(teleportOwner)) {
            teleportOwner.sendMessage(tl("warpingTo", warp, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        }
        teleport(otherUser, new LocationTarget(loc), chargeFor, cause, exceptionFuture, future);
    }

    //The back function is a wrapper used to teleportPlayer a player /back to their previous location.
    @Override
    @Deprecated
    public void back(Trade chargeFor) throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        back(chargeFor, exceptionFuture, future);
        if (!future.get()) {
            throw exceptionFuture.get();
        }
    }

    @Override
    public void back(Trade chargeFor, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        back(teleportOwner, chargeFor, exceptionFuture, future);
    }

    //This function is a wrapper over the other back function for cases where another player performs back for them
    @Override
    @Deprecated
    public void back(IUser teleporter, Trade chargeFor) throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        back(teleporter, chargeFor, exceptionFuture, future);
        if (!future.get()) {
            throw exceptionFuture.get();
        }
    }

    @Override
    public void back(IUser teleporter, Trade chargeFor, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        tpType = TeleportType.BACK;
        final Location loc = teleportOwner.getLastLocation();
        teleportOwner.sendMessage(tl("backUsageMsg", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        teleportOther(teleporter, teleportOwner, new LocationTarget(loc), chargeFor, TeleportCause.COMMAND, exceptionFuture, future);
    }

    //This function is used to throw a user back after a jail sentence
    @Override
    @Deprecated
    public void back() throws Exception {
        CompletableFuture<Exception> exceptionFuture = new CompletableFuture<>();
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        back(exceptionFuture, future);
        if (!future.get()) {
            throw exceptionFuture.get();
        }
    }

    @Override
    public void back(CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future) {
        nowAsync(teleportOwner, new LocationTarget(teleportOwner.getLastLocation()), TeleportCause.COMMAND, exceptionFuture, future);
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
        timedTeleport = new TimedTeleport(teleportOwner, ess, this, delay, teleportUser, target, chargeFor, cause, respawn);
    }
}
