package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;
import java.util.regex.Pattern;

public class AsyncTimedCommand implements Runnable {
    private static final double MOVE_CONSTANT = 0.3;
    private final IUser commandUser;
    private final IEssentials ess;
    private final UUID timer_userId;
    private final long timer_started;
    private final long timer_delay;
    private final long timer_initX;
    private final long timer_initY;
    private final long timer_initZ;
    private final String timer_command;
    private final Pattern timer_pattern;
    private final boolean timer_canMove;
    private volatile int timer_task;
    private volatile double timer_health;

    AsyncTimedCommand(final IUser user, final IEssentials ess, final long delay, final String command, final Pattern pattern) {
        this.commandUser = user;
        this.ess = ess;
        this.timer_started = System.currentTimeMillis();
        this.timer_delay = delay;
        this.timer_health = user.getBase().getHealth();
        Location initLocation = user.getBase().getLocation();
        if (initLocation == null) {
            // Defensive: set to zero if location is null (could also throw or cancel)
            this.timer_initX = 0;
            this.timer_initY = 0;
            this.timer_initZ = 0;
        } else {
            this.timer_initX = Math.round(initLocation.getX() * MOVE_CONSTANT);
            this.timer_initY = Math.round(initLocation.getY() * MOVE_CONSTANT);
            this.timer_initZ = Math.round(initLocation.getZ() * MOVE_CONSTANT);
        }
        this.timer_userId = user.getBase().getUniqueId();
        this.timer_command = command;
        this.timer_pattern = pattern;
        this.timer_canMove = user.isAuthorized("essentials.commandwarmups.move");

        timer_task = ess.runTaskTimerAsynchronously(this, 20, 20).getTaskId();
    }

    @Override
    public void run() {
        if (commandUser == null || !commandUser.getBase().isOnline() || commandUser.getBase().getLocation() == null) {
            cancelTimer(false);
            return;
        }

        final IUser user = ess.getUser(this.timer_userId);

        if (user == null || !user.getBase().isOnline()) {
            cancelTimer(false);
            return;
        }

        final Location currLocation = user.getBase().getLocation();
        if (currLocation == null) {
            cancelTimer(false);
            return;
        }

        if (!timer_canMove && (Math.round(currLocation.getX() * MOVE_CONSTANT) != timer_initX 
            || Math.round(currLocation.getY() * MOVE_CONSTANT) != timer_initY 
            || Math.round(currLocation.getZ() * MOVE_CONSTANT) != timer_initZ 
            || user.getBase().getHealth() < timer_health)) {
            // user moved or took damage, cancel command warmup
            cancelTimer(true);
            return;
        }

        class DelayedCommandTask implements Runnable {
            @Override
            public void run() {
                timer_health = user.getBase().getHealth();
                final long now = System.currentTimeMillis();
                if (now > timer_started + timer_delay) {
                    try {
                        cancelTimer(false);
                        
                        // Clear the warmup from the user's data BEFORE executing the command
                        // This prevents the warmup check from triggering again
                        user.clearCommandWarmup(timer_pattern);

                        // Execute the command by dispatching it to the server
                        Bukkit.getScheduler().runTask(ess, () -> {
                            try {
                                // Execute as server command to bypass the warmup check
                                Bukkit.dispatchCommand(user.getBase(), timer_command.substring(1)); // Remove the leading '/'
                                user.sendTl("commandWarmupComplete");
                            } catch (final Exception ex) {
                                ess.showError(user.getSource(), ex, "\\ command warmup");
                            }
                        });
                }
            }
        }

        ess.scheduleSyncDelayedTask(new DelayedCommandTask());
    }

    void cancelTimer(final boolean notifyUser) {
        if (timer_task == -1) {
            return;
        }
        try {
            ess.getServer().getScheduler().cancelTask(timer_task);
            if (notifyUser) {
                commandUser.sendTl("commandWarmupCancelled");
            }
            // Clear the warmup from the user's data
            commandUser.clearCommandWarmup(timer_pattern);
        } finally {
            timer_task = -1;
        }
    }
}
