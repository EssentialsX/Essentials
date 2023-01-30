package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static com.earth2me.essentials.I18n.tl;

public class AsyncTimedTeleport implements Runnable {
    private static final double MOVE_CONSTANT = 0.3;
    private final IUser teleportOwner;
    private final IEssentials ess;
    private final AsyncTeleport teleport;
    private final UUID timer_teleportee;
    private final long timer_started; // time this task was initiated
    private final long timer_delay; // how long to delay the teleportPlayer
    private final CompletableFuture<Boolean> parentFuture;
    // note that I initially stored a clone of the location for reference, but...
    // when comparing locations, I got incorrect mismatches (rounding errors, looked like)
    // so, the X/Y/Z values are stored instead and rounded off
    private final long timer_initX;
    private final long timer_initY;
    private final long timer_initZ;
    private final ITarget timer_teleportTarget;
    private final boolean timer_respawn;
    private final boolean timer_canMove;
    private final Trade timer_chargeFor;
    private final TeleportCause timer_cause;
    private int timer_task;
    private double timer_health;

    AsyncTimedTeleport(final IUser user, final IEssentials ess, final AsyncTeleport teleport, final long delay, final IUser teleportUser, final ITarget target, final Trade chargeFor, final TeleportCause cause, final boolean respawn) {
        this(user, ess, teleport, delay, null, teleportUser, target, chargeFor, cause, respawn);
    }

    AsyncTimedTeleport(final IUser user, final IEssentials ess, final AsyncTeleport teleport, final long delay, final CompletableFuture<Boolean> future, final IUser teleportUser, final ITarget target, final Trade chargeFor, final TeleportCause cause, final boolean respawn) {
        this.teleportOwner = user;
        this.ess = ess;
        this.teleport = teleport;
        this.timer_started = System.currentTimeMillis();
        this.timer_delay = delay;
        this.timer_health = teleportUser.getBase().getHealth();
        this.timer_initX = Math.round(teleportUser.getBase().getLocation().getX() * MOVE_CONSTANT);
        this.timer_initY = Math.round(teleportUser.getBase().getLocation().getY() * MOVE_CONSTANT);
        this.timer_initZ = Math.round(teleportUser.getBase().getLocation().getZ() * MOVE_CONSTANT);
        this.timer_teleportee = teleportUser.getBase().getUniqueId();
        this.timer_teleportTarget = target;
        this.timer_chargeFor = chargeFor;
        this.timer_cause = cause;
        this.timer_respawn = respawn;
        this.timer_canMove = user.isAuthorized("essentials.teleport.timer.move");

        timer_task = ess.runTaskTimerAsynchronously(this, 20, 20).getTaskId();

        if (future != null) {
            this.parentFuture = future;
            return;
        }

        final CompletableFuture<Boolean> cFuture = new CompletableFuture<>();
        cFuture.exceptionally(e -> {
            ess.showError(teleportOwner.getSource(), e, "\\ teleport");
            return false;
        });
        this.parentFuture = cFuture;
    }

    @Override
    public void run() {

        if (teleportOwner == null || !teleportOwner.getBase().isOnline() || teleportOwner.getBase().getLocation() == null) {
            cancelTimer(false);
            return;
        }

        final IUser teleportUser = ess.getUser(this.timer_teleportee);

        if (teleportUser == null || !teleportUser.getBase().isOnline()) {
            cancelTimer(false);
            return;
        }

        final Location currLocation = teleportUser.getBase().getLocation();
        if (currLocation == null) {
            cancelTimer(false);
            return;
        }

        if (!timer_canMove && (Math.round(currLocation.getX() * MOVE_CONSTANT) != timer_initX || Math.round(currLocation.getY() * MOVE_CONSTANT) != timer_initY || Math.round(currLocation.getZ() * MOVE_CONSTANT) != timer_initZ || teleportUser.getBase().getHealth() < timer_health)) {
            // user moved, cancelTimer teleportPlayer
            cancelTimer(true);
            return;
        }

        class DelayedTeleportTask implements Runnable {
            @Override
            public void run() {

                timer_health = teleportUser.getBase().getHealth(); // in case user healed, then later gets injured
                final long now = System.currentTimeMillis();
                if (now > timer_started + timer_delay) {
                    try {
                        teleport.cooldown(false);
                    } catch (final Throwable ex) {
                        teleportOwner.sendMessage(tl("cooldownWithMessage", ex.getMessage()));
                        if (teleportOwner != teleportUser) {
                            teleportUser.sendMessage(tl("cooldownWithMessage", ex.getMessage()));
                        }
                    }
                    try {
                        cancelTimer(false);

                        // the target might change location, so check if they can accept teleports.
                        if (AsyncTeleport.TeleportType.TPA == teleport.getTpType() && timer_teleportTarget instanceof PlayerTarget) {
                            final Player targetPlayer = Bukkit.getPlayer(((PlayerTarget) timer_teleportTarget).getUUID());
                            if (targetPlayer == null) return;
                            try {
                                if (!ess.getPermissionsHandler().hasPermission(targetPlayer, "essentials.tpaccept")) {
                                    teleportOwner.sendMessage(tl("teleportNoAcceptPermission", targetPlayer.getDisplayName()));
                                    return;
                                }
                            } catch (Exception ex) {
                                if (ess.getSettings().isDebug()) {
                                    ess.getLogger().log(Level.SEVERE, "Permission System Error: " + ess.getPermissionsHandler().getName() + " returned: " + ex.getMessage(), ex);
                                } else {
                                    ess.getLogger().log(Level.SEVERE, "Permission System Error: " + ess.getPermissionsHandler().getName() + " returned: " + ex.getMessage());
                                }
                                return;
                            }
                        }

                        teleportUser.sendMessage(tl("teleportationCommencing"));

                        if (timer_chargeFor != null) {
                            timer_chargeFor.isAffordableFor(teleportOwner);
                        }

                        if (timer_respawn) {
                            teleport.respawnNow(teleportUser, timer_cause, parentFuture);
                        } else {
                            teleport.nowAsync(teleportUser, timer_teleportTarget, timer_cause, parentFuture);
                        }
                        parentFuture.thenAccept(success -> {
                            if (timer_chargeFor != null) {
                                try {
                                    timer_chargeFor.charge(teleportOwner);
                                } catch (final ChargeException ex) {
                                    ess.showError(teleportOwner.getSource(), ex, "\\ teleport");
                                }
                            }
                        });

                    } catch (final Exception ex) {
                        ess.showError(teleportOwner.getSource(), ex, "\\ teleport");
                    }
                }
            }
        }

        ess.scheduleSyncDelayedTask(new DelayedTeleportTask());
    }

    //If we need to cancelTimer a pending teleportPlayer call this method
    void cancelTimer(final boolean notifyUser) {
        if (timer_task == -1) {
            return;
        }
        try {
            ess.getServer().getScheduler().cancelTask(timer_task);
            if (notifyUser) {
                teleportOwner.sendMessage(tl("pendingTeleportCancelled"));
                if (timer_teleportee != null && !timer_teleportee.equals(teleportOwner.getBase().getUniqueId())) {
                    ess.getUser(timer_teleportee).sendMessage(tl("pendingTeleportCancelled"));
                }
            }
        } finally {
            timer_task = -1;
        }
    }
}
