package com.earth2me.essentials;

import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.LocationUtil;
import io.papermc.lib.PaperLib;
import net.ess3.api.IEssentials;
import net.ess3.api.ITeleport;
import net.ess3.api.IUser;
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

import static com.earth2me.essentials.I18n.tl;

/**
 * @deprecated This API is not asynchronous. Use {@link com.earth2me.essentials.AsyncTeleport AsyncTeleport}
 */
@Deprecated
public class Teleport implements ITeleport {
    private final IUser teleportOwner;
    private final IEssentials ess;
    private TimedTeleport timedTeleport;

    private TeleportType tpType;

    @Deprecated
    public Teleport(final IUser user, final IEssentials ess) {
        this.teleportOwner = user;
        this.ess = ess;
        tpType = TeleportType.NORMAL;
    }

    @Deprecated
    public void cooldown(final boolean check) throws Exception {
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
                return;
            } else if (lastTime > earliestLong
                && cooldownApplies()) {
                time.setTimeInMillis(lastTime);
                time.add(Calendar.SECOND, (int) cooldown);
                time.add(Calendar.MILLISECOND, (int) ((cooldown * 1000.0) % 1000.0));
                throw new Exception(tl("timeBeforeTeleport", DateUtil.formatDateDiff(time.getTimeInMillis())));
            }
        }
        // if justCheck is set, don't update lastTeleport; we're just checking
        if (!check) {
            teleportOwner.setLastTeleportTimestamp(time.getTimeInMillis());
        }
    }

    @Deprecated
    private boolean cooldownApplies() {
        boolean applies = true;
        final String globalBypassPerm = "essentials.teleport.cooldown.bypass";
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

    @Deprecated
    private void warnUser(final IUser user, final double delay) {
        final Calendar c = new GregorianCalendar();
        c.add(Calendar.SECOND, (int) delay);
        c.add(Calendar.MILLISECOND, (int) ((delay * 1000.0) % 1000.0));
        user.sendMessage(tl("dontMoveMessage", DateUtil.formatDateDiff(c.getTimeInMillis())));
    }

    //The now function is used when you want to skip tp delay when teleporting someone to a location or player.
    @Override
    @Deprecated
    public void now(final Location loc, final boolean cooldown, final TeleportCause cause) throws Exception {
        if (cooldown) {
            cooldown(false);
        }
        final ITarget target = new LocationTarget(loc);
        now(teleportOwner, target, cause);
    }

    @Override
    @Deprecated
    public void now(final Player entity, final boolean cooldown, final TeleportCause cause) throws Exception {
        if (cooldown) {
            cooldown(false);
        }
        final ITarget target = new PlayerTarget(entity);
        now(teleportOwner, target, cause);
        teleportOwner.sendMessage(tl("teleporting", target.getLocation().getWorld().getName(), target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ()));
    }

    @Deprecated
    protected void now(final IUser teleportee, final ITarget target, final TeleportCause cause) throws Exception {
        cancel(false);
        Location loc = target.getLocation();

        final PreTeleportEvent event = new PreTeleportEvent(teleportee, cause, target);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (teleportee.isAuthorized("essentials.back.onteleport")) {
            teleportee.setLastLocation();
        }

        if (!teleportee.getBase().isEmpty()) {
            if (!ess.getSettings().isTeleportPassengerDismount()) {
                throw new Exception(tl("passengerTeleportFail"));
            }
            teleportee.getBase().eject();
        }

        if (LocationUtil.isBlockUnsafeForUser(ess, teleportee, loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
            if (ess.getSettings().isTeleportSafetyEnabled()) {
                if (ess.getSettings().isForceDisableTeleportSafety()) {
                    PaperLib.teleportAsync(teleportee.getBase(), loc, cause);
                } else {
                    PaperLib.teleportAsync(teleportee.getBase(), LocationUtil.getSafeDestination(ess, teleportee, loc), cause);
                }
            } else {
                throw new Exception(tl("unsafeTeleportDestination", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            }
        } else {
            if (ess.getSettings().isForceDisableTeleportSafety()) {
                PaperLib.teleportAsync(teleportee.getBase(), loc, cause);
            } else {
                if (ess.getSettings().isTeleportToCenterLocation()) {
                    loc = LocationUtil.getRoundedDestination(loc);
                }
                PaperLib.teleportAsync(teleportee.getBase(), loc, cause);
            }
        }
    }

    //The teleportPlayer function is used when you want to normally teleportPlayer someone to a location or player.
    //This method is nolonger used internally and will be removed.
    @Deprecated
    @Override
    public void teleport(final Location loc, final Trade chargeFor) throws Exception {
        teleport(loc, chargeFor, TeleportCause.PLUGIN);
    }

    @Override
    @Deprecated
    public void teleport(final Location loc, final Trade chargeFor, final TeleportCause cause) throws Exception {
        teleport(teleportOwner, new LocationTarget(loc), chargeFor, cause);
    }

    //This is used when teleporting to a player
    @Override
    @Deprecated
    public void teleport(final Player entity, final Trade chargeFor, final TeleportCause cause) throws Exception {
        final ITarget target = new PlayerTarget(entity);
        teleportOwner.sendMessage(tl("teleportToPlayer", entity.getDisplayName()));
        teleport(teleportOwner, target, chargeFor, cause);
    }

    //This is used when teleporting to stored location
    @Override
    @Deprecated
    public void teleportPlayer(final IUser teleportee, final Location loc, final Trade chargeFor, final TeleportCause cause) throws Exception {
        teleport(teleportee, new LocationTarget(loc), chargeFor, cause);
    }

    //This is used on /tphere
    @Override
    @Deprecated
    public void teleportPlayer(final IUser teleportee, final Player entity, final Trade chargeFor, final TeleportCause cause) throws Exception {
        final ITarget target = new PlayerTarget(entity);
        teleport(teleportee, target, chargeFor, cause);
        teleportee.sendMessage(tl("teleporting", target.getLocation().getWorld().getName(), target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ()));
        teleportOwner.sendMessage(tl("teleporting", target.getLocation().getWorld().getName(), target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ()));
    }

    @Deprecated
    private void teleport(final IUser teleportee, final ITarget target, final Trade chargeFor, final TeleportCause cause) throws Exception {
        double delay = ess.getSettings().getTeleportDelay();

        final TeleportWarmupEvent event = new TeleportWarmupEvent(teleportee, cause, target, delay);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        delay = event.getDelay();

        Trade cashCharge = chargeFor;

        if (chargeFor != null) {
            chargeFor.isAffordableFor(teleportOwner);

            //This code is to make sure that commandcosts are checked in the initial world, and not in the resulting world.
            if (!chargeFor.getCommandCost(teleportOwner).equals(BigDecimal.ZERO)) {
                //By converting a command cost to a regular cost, the command cost permission isn't checked when executing the charge after teleport.
                cashCharge = new Trade(chargeFor.getCommandCost(teleportOwner), ess);
            }
        }

        cooldown(true);
        if (delay <= 0 || teleportOwner.isAuthorized("essentials.teleport.timer.bypass") || teleportee.isAuthorized("essentials.teleport.timer.bypass")) {
            cooldown(false);
            now(teleportee, target, cause);
            if (cashCharge != null) {
                cashCharge.charge(teleportOwner);
            }
            return;
        }

        cancel(false);
        warnUser(teleportee, delay);
        initTimer((long) (delay * 1000.0), teleportee, target, cashCharge, cause, false);
    }

    @Deprecated
    private void teleportOther(final IUser teleporter, final IUser teleportee, final ITarget target, final Trade chargeFor, final TeleportCause cause) throws Exception {
        double delay = ess.getSettings().getTeleportDelay();

        final TeleportWarmupEvent event = new TeleportWarmupEvent(teleporter, teleportee, cause, target, delay);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        delay = event.getDelay();

        Trade cashCharge = chargeFor;

        if (teleporter != null && chargeFor != null) {
            chargeFor.isAffordableFor(teleporter);

            //This code is to make sure that commandcosts are checked in the initial world, and not in the resulting world.
            if (!chargeFor.getCommandCost(teleporter).equals(BigDecimal.ZERO)) {
                //By converting a command cost to a regular cost, the command cost permission isn't checked when executing the charge after teleport.
                cashCharge = new Trade(chargeFor.getCommandCost(teleporter), ess);
            }
        }

        cooldown(true);
        if (delay <= 0 || teleporter == null
            || teleporter.isAuthorized("essentials.teleport.timer.bypass")
            || teleportOwner.isAuthorized("essentials.teleport.timer.bypass")
            || teleportee.isAuthorized("essentials.teleport.timer.bypass")) {
            cooldown(false);
            now(teleportee, target, cause);
            if (teleporter != null && cashCharge != null) {
                cashCharge.charge(teleporter);
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
    public void respawn(final Trade chargeFor, final TeleportCause cause) throws Exception {
        double delay = ess.getSettings().getTeleportDelay();

        final TeleportWarmupEvent event = new TeleportWarmupEvent(teleportOwner, cause, null, delay);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        delay = event.getDelay();

        if (chargeFor != null) {
            chargeFor.isAffordableFor(teleportOwner);
        }
        cooldown(true);
        if (delay <= 0 || teleportOwner.isAuthorized("essentials.teleport.timer.bypass")) {
            cooldown(false);
            respawnNow(teleportOwner, cause);
            if (chargeFor != null) {
                chargeFor.charge(teleportOwner);
            }
            return;
        }

        cancel(false);
        warnUser(teleportOwner, delay);
        initTimer((long) (delay * 1000.0), teleportOwner, null, chargeFor, cause, true);
    }

    @Deprecated
    void respawnNow(final IUser teleportee, final TeleportCause cause) throws Exception {
        final Player player = teleportee.getBase();
        final Location bed = player.getBedSpawnLocation();
        if (bed != null) {
            now(teleportee, new LocationTarget(bed), cause);
        } else {
            if (ess.getSettings().isDebug()) {
                ess.getLogger().info("Could not find bed spawn, forcing respawn event.");
            }
            final PlayerRespawnEvent pre = new PlayerRespawnEvent(player, player.getWorld().getSpawnLocation(), false);
            ess.getServer().getPluginManager().callEvent(pre);
            now(teleportee, new LocationTarget(pre.getRespawnLocation()), cause);
        }
    }

    //The warp function is a wrapper used to teleportPlayer a player to a /warp
    @Override
    @Deprecated
    public void warp(final IUser teleportee, String warp, final Trade chargeFor, final TeleportCause cause) throws Exception {
        final UserWarpEvent event = new UserWarpEvent(teleportee, warp, chargeFor);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        warp = event.getWarp();
        final Location loc = ess.getWarps().getWarp(warp);
        teleportee.sendMessage(tl("warpingTo", warp, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        if (!teleportee.equals(teleportOwner)) {
            teleportOwner.sendMessage(tl("warpingTo", warp, loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        }
        teleport(teleportee, new LocationTarget(loc), chargeFor, cause);
    }

    //The back function is a wrapper used to teleportPlayer a player /back to their previous location.
    @Override
    @Deprecated
    public void back(final Trade chargeFor) throws Exception {
        back(teleportOwner, chargeFor);
    }

    //This function is a wrapper over the other back function for cases where another player performs back for them
    @Override
    @Deprecated
    public void back(final IUser teleporter, final Trade chargeFor) throws Exception {
        tpType = TeleportType.BACK;
        final Location loc = teleportOwner.getLastLocation();
        teleportOwner.sendMessage(tl("backUsageMsg", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        teleportOther(teleporter, teleportOwner, new LocationTarget(loc), chargeFor, TeleportCause.COMMAND);
    }

    //This function is used to throw a user back after a jail sentence
    @Override
    @Deprecated
    public void back() throws Exception {
        now(teleportOwner, new LocationTarget(teleportOwner.getLastLocation()), TeleportCause.COMMAND);
    }

    @Deprecated
    public void setTpType(final TeleportType tpType) {
        this.tpType = tpType;
    }

    //If we need to cancelTimer a pending teleportPlayer call this method
    @Deprecated
    private void cancel(final boolean notifyUser) {
        if (timedTeleport != null) {
            timedTeleport.cancelTimer(notifyUser);
            timedTeleport = null;
        }
    }

    @Deprecated
    private void initTimer(final long delay, final IUser teleportUser, final ITarget target, final Trade chargeFor, final TeleportCause cause, final boolean respawn) {
        timedTeleport = new TimedTeleport(teleportOwner, ess, this, delay, teleportUser, target, chargeFor, cause, respawn);
    }

    public enum TeleportType {
        TPA,
        BACK,
        NORMAL
    }
}
