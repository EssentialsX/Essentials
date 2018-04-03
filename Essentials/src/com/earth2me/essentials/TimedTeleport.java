package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.UUID;

import static com.earth2me.essentials.I18n.tlp;


public class TimedTeleport implements Runnable {
    private static final double MOVE_CONSTANT = 0.3;
    private final IUser teleportOwner;
    private final IEssentials ess;
    private final Teleport teleport;
    private final UUID timer_teleportee;
    private int timer_task = -1;
    private final long timer_started;    // time this task was initiated
    private final long timer_delay;        // how long to delay the teleportPlayer
    private double timer_health;
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

    public TimedTeleport(IUser user, IEssentials ess, Teleport teleport, long delay, IUser teleportUser, ITarget target, Trade chargeFor, TeleportCause cause, boolean respawn) {

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

                timer_health = teleportUser.getBase().getHealth();  // in case user healed, then later gets injured
                final long now = System.currentTimeMillis();
                if (now > timer_started + timer_delay) {
                    try {
                        teleport.cooldown(false);
                    } catch (Exception ex) {
                        teleportOwner.sendMessage(tlp(teleportOwner, "cooldownWithMessage", ex.getMessage()));
                        if (teleportOwner != teleportUser) {
                            teleportUser.sendMessage(tlp(teleportUser, "cooldownWithMessage", ex.getMessage()));
                        }
                    }
                    try {
                        cancelTimer(false);
                        teleportUser.sendMessage(tlp(teleportUser, "teleportationCommencing"));

                        try {
                            if (timer_chargeFor != null) {
                                timer_chargeFor.isAffordableFor(teleportOwner);
                            }
                            if (timer_respawn) {
                                teleport.respawnNow(teleportUser, timer_cause);
                            } else {
                                teleport.now(teleportUser, timer_teleportTarget, timer_cause);
                            }
                            if (timer_chargeFor != null) {
                                timer_chargeFor.charge(teleportOwner);
                            }
                        } catch (Exception ex) {
                        }

                    } catch (Exception ex) {
                        ess.showError(teleportOwner.getSource(), ex, "\\ teleport");
                    }
                }
            }
        }
        ess.scheduleSyncDelayedTask(new DelayedTeleportTask());
    }

    //If we need to cancelTimer a pending teleportPlayer call this method
    public void cancelTimer(boolean notifyUser) {
        if (timer_task == -1) {
            return;
        }
        try {
            ess.getServer().getScheduler().cancelTask(timer_task);
            if (notifyUser) {
                teleportOwner.sendMessage(tlp(teleportOwner, "pendingTeleportCancelled"));
                if (timer_teleportee != null && !timer_teleportee.equals(teleportOwner.getBase().getUniqueId())) {
                    IUser teleporteeUser = ess.getUser(timer_teleportee);
                    teleporteeUser.sendMessage(tlp(teleporteeUser, "pendingTeleportCancelled"));
                }
            }
        } finally {
            timer_task = -1;
        }
    }
}
