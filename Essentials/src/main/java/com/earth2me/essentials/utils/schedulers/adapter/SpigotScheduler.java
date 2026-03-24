package com.earth2me.essentials.utils.schedulers.adapter;

import com.earth2me.essentials.utils.schedulers.SchedulerAdapter;
import com.earth2me.essentials.utils.schedulers.SchedulerTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class SpigotScheduler implements SchedulerAdapter {
    private final Plugin plugin;

    public SpigotScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public SchedulerTask runEntityTask(Entity entity, Runnable runnable) {
        return runTask(runnable);
    }

    @Override
    public SchedulerTask runEntityTaskLater(Entity entity, Runnable runnable, long delayTicks) {
        return runTaskLater(runnable, delayTicks);
    }

    @Override
    public SchedulerTask runEntityTaskTimer(Entity entity, Runnable runnable, long delayTicks, long periodTicks) {
        return runTaskTimer(runnable, delayTicks, periodTicks);
    }

    @Override
    public SchedulerTask runRegionTask(Location location, Runnable runnable) {
        return runTask(runnable);
    }

    @Override
    public SchedulerTask runRegionTask(World world, int chunkX, int chunkZ, Runnable runnable) {
        return runTask(runnable);
    }

    @Override
    public SchedulerTask runRegionTaskLater(Location location, Runnable runnable, long delayTicks) {
        return runTaskLater(runnable, delayTicks);
    }

    @Override
    public SchedulerTask runRegionTaskLater(World world, int chunkX, int chunkZ, Runnable runnable, long delayTicks) {
        return runTaskLater(runnable, delayTicks);
    }

    @Override
    public SchedulerTask runRegionTaskTimer(Location location, Runnable runnable, long delayTicks, long periodTicks) {
        return runTaskTimer(runnable, delayTicks, periodTicks);
    }

    @Override
    public SchedulerTask runRegionTaskTimer(World world, int chunkX, int chunkZ, Runnable runnable, long delayTicks,
                                            long periodTicks) {
        return runTaskTimer(runnable, delayTicks, periodTicks);
    }

    @Override
    public SchedulerTask runTask(Runnable runnable) {
        return wrap(Bukkit.getScheduler().runTask(plugin, runnable));
    }

    @Override
    public SchedulerTask runTaskAsynchronously(Runnable runnable) {
        return wrap(Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable));
    }

    @Override
    public SchedulerTask runTaskLater(Runnable runnable, long delayTicks) {
        return wrap(Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks));
    }

    @Override
    public SchedulerTask runTaskLaterAsynchronously(Runnable runnable, long delayTicks) {
        return wrap(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delayTicks));
    }

    @Override
    public SchedulerTask runTaskTimer(Runnable runnable, long delayTicks, long periodTicks) {
        return wrap(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks));
    }

    @Override
    public SchedulerTask runTaskTimerAsynchronously(Runnable runnable, long delayTicks, long periodTicks) {
        return wrap(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delayTicks, periodTicks));
    }

    @Override
    public boolean isOnOwnerThread(Entity entity) {
        return Bukkit.isPrimaryThread();
    }

    @Override
    public boolean isOnOwnerThread(Location location) {
        return Bukkit.isPrimaryThread();
    }

    @Override
    public boolean isOnOwnerThread(World world, int chunkX, int chunkZ) {
        return Bukkit.isPrimaryThread();
    }

    @Override
    public boolean isOnOwnerThread(Block block) {
        return Bukkit.isPrimaryThread();
    }

    private SchedulerTask wrap(BukkitTask task) {
        return new SpigotSchedulerTask(task);
    }
}