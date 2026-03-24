package com.earth2me.essentials.utils.schedulers.runnables;

import com.earth2me.essentials.utils.schedulers.SchedulerRunnable;
import com.earth2me.essentials.utils.schedulers.SchedulerTask;
import com.earth2me.essentials.utils.schedulers.adapter.FoliaSchedulerTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public abstract class FoliaSchedulerRunnable extends SchedulerRunnable {

    @Override
    public SchedulerTask runEntityTask(Plugin plugin, Entity entity, Runnable retired) {
        return setupTask(new FoliaSchedulerTask(entity.getScheduler().run(plugin, t -> run(), retired)));
    }

    @Override
    public SchedulerTask runEntityTaskLater(Plugin plugin, Entity entity, Runnable retired, long delayTicks) {
        return setupTask(new FoliaSchedulerTask(
                entity.getScheduler().runDelayed(plugin, t -> run(), retired, Math.max(1, delayTicks))));
    }

    @Override
    public SchedulerTask runEntityTaskTimer(Plugin plugin, Entity entity, Runnable retired, long delayTicks,
                                            long periodTicks) {
        return setupTask(new FoliaSchedulerTask(entity.getScheduler().runAtFixedRate(plugin, t -> run(), retired,
                Math.max(1, delayTicks), Math.max(1, periodTicks))));
    }

    @Override
    public SchedulerTask runRegionTask(Plugin plugin, Location location) {
        return setupTask(new FoliaSchedulerTask(Bukkit.getRegionScheduler().run(plugin, location, t -> run())));
    }

    @Override
    public SchedulerTask runRegionTask(Plugin plugin, World world, int chunkX, int chunkZ) {
        return setupTask(
                new FoliaSchedulerTask(Bukkit.getRegionScheduler().run(plugin, world, chunkX, chunkZ, t -> run())));
    }

    @Override
    public SchedulerTask runRegionTaskLater(Plugin plugin, Location location, long delayTicks) {
        return setupTask(new FoliaSchedulerTask(
                Bukkit.getRegionScheduler().runDelayed(plugin, location, t -> run(), Math.max(1, delayTicks))));
    }

    @Override
    public SchedulerTask runRegionTaskLater(Plugin plugin, World world, int chunkX, int chunkZ, long delayTicks) {
        return setupTask(new FoliaSchedulerTask(Bukkit.getRegionScheduler().runDelayed(plugin, world, chunkX, chunkZ,
                t -> run(), Math.max(1, delayTicks))));
    }

    @Override
    public SchedulerTask runRegionTaskTimer(Plugin plugin, Location location, long delayTicks, long periodTicks) {
        return setupTask(new FoliaSchedulerTask(Bukkit.getRegionScheduler().runAtFixedRate(plugin, location, t -> run(),
                Math.max(1, delayTicks), Math.max(1, periodTicks))));
    }

    @Override
    public SchedulerTask runRegionTaskTimer(Plugin plugin, World world, int chunkX, int chunkZ, long delayTicks,
                                            long periodTicks) {
        return setupTask(new FoliaSchedulerTask(Bukkit.getRegionScheduler().runAtFixedRate(plugin, world, chunkX,
                chunkZ, t -> run(), Math.max(1, delayTicks), Math.max(1, periodTicks))));
    }

    @Override
    public SchedulerTask runTask(Plugin plugin) {
        return setupTask(new FoliaSchedulerTask(Bukkit.getGlobalRegionScheduler().run(plugin, t -> run())));
    }

    @Override
    public SchedulerTask runTaskAsynchronously(Plugin plugin) {
        return setupTask(new FoliaSchedulerTask(Bukkit.getAsyncScheduler().runNow(plugin, t -> run())));
    }

    @Override
    public SchedulerTask runTaskLater(Plugin plugin, long delayTicks) {
        return setupTask(new FoliaSchedulerTask(
                Bukkit.getGlobalRegionScheduler().runDelayed(plugin, t -> run(), Math.max(1, delayTicks))));
    }

    @Override
    public SchedulerTask runTaskLaterAsynchronously(Plugin plugin, long delayTicks) {
        return setupTask(new FoliaSchedulerTask(Bukkit.getAsyncScheduler().runDelayed(plugin, t -> run(),
                Math.max(1, delayTicks) * 50L, TimeUnit.MILLISECONDS)));
    }

    @Override
    public SchedulerTask runTaskTimer(Plugin plugin, long delayTicks, long periodTicks) {
        return setupTask(new FoliaSchedulerTask(Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> run(),
                Math.max(1, delayTicks), Math.max(1, periodTicks))));
    }

    @Override
    public SchedulerTask runTaskTimerAsynchronously(Plugin plugin, long delayTicks, long periodTicks) {
        return setupTask(new FoliaSchedulerTask(Bukkit.getAsyncScheduler().runAtFixedRate(plugin, t -> run(),
                Math.max(1, delayTicks) * 50L, Math.max(1, periodTicks) * 50L, TimeUnit.MILLISECONDS)));
    }
}