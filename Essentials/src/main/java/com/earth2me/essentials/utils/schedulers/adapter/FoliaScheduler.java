package com.earth2me.essentials.utils.schedulers.adapter;

import com.earth2me.essentials.utils.schedulers.SchedulerAdapter;
import com.earth2me.essentials.utils.schedulers.SchedulerTask;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;


public class FoliaScheduler implements SchedulerAdapter {

    private final AsyncScheduler asyncScheduler;
    private final GlobalRegionScheduler globalScheduler;
    private final Plugin plugin;
    private final RegionScheduler regionScheduler;

    public FoliaScheduler(Plugin plugin) {
        this.plugin = plugin;
        globalScheduler = Bukkit.getGlobalRegionScheduler();
        asyncScheduler = Bukkit.getAsyncScheduler();
        regionScheduler = Bukkit.getRegionScheduler();
    }

    @Override
    public SchedulerTask runEntityTask(Entity entity, Runnable runnable) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = entity.getScheduler().run(plugin, t -> runnable.run(), null);
        return wrap(task);
    }

    @Override
    public SchedulerTask runEntityTaskLater(Entity entity, Runnable runnable, long delayTicks) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = entity.getScheduler().runDelayed(plugin, t -> runnable.run(), null,
                Math.max(1, delayTicks));
        return wrap(task);
    }

    @Override
    public SchedulerTask runEntityTaskTimer(Entity entity, Runnable runnable, long delayTicks, long periodTicks) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = entity.getScheduler().runAtFixedRate(plugin, t -> runnable.run(), null,
                Math.max(1, delayTicks), Math.max(1, periodTicks));
        return wrap(task);
    }

    @Override
    public SchedulerTask runRegionTask(Location location, Runnable runnable) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = regionScheduler.run(plugin, location, t -> runnable.run());
        return wrap(task);
    }

    @Override
    public SchedulerTask runRegionTask(World world, int chunkX, int chunkZ, Runnable runnable) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = regionScheduler.run(plugin, world, chunkX, chunkZ, t -> runnable.run());
        return wrap(task);
    }

    @Override
    public SchedulerTask runRegionTaskLater(Location location, Runnable runnable, long delayTicks) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = regionScheduler.runDelayed(plugin, location, t -> runnable.run(), Math.max(1, delayTicks));
        return wrap(task);
    }

    @Override
    public SchedulerTask runRegionTaskLater(World world, int chunkX, int chunkZ, Runnable runnable, long delayTicks) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = regionScheduler.runDelayed(plugin, world, chunkX, chunkZ, t -> runnable.run(),
                Math.max(1, delayTicks));
        return wrap(task);
    }

    @Override
    public SchedulerTask runRegionTaskTimer(Location location, Runnable runnable, long delayTicks, long periodTicks) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = regionScheduler.runAtFixedRate(plugin, location, t -> runnable.run(),
                Math.max(1, delayTicks), Math.max(1, periodTicks));
        return wrap(task);
    }

    @Override
    public SchedulerTask runRegionTaskTimer(World world, int chunkX, int chunkZ, Runnable runnable, long delayTicks,
                                            long periodTicks) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = regionScheduler.runAtFixedRate(plugin, world, chunkX, chunkZ, t -> runnable.run(),
                Math.max(1, delayTicks), Math.max(1, periodTicks));
        return wrap(task);
    }

    @Override
    public SchedulerTask runTask(Runnable runnable) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = globalScheduler.run(plugin, t -> runnable.run());
        return wrap(task);
    }

    @Override
    public SchedulerTask runTaskAsynchronously(Runnable runnable) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = asyncScheduler.runNow(plugin, t -> runnable.run());
        return wrap(task);
    }

    @Override
    public SchedulerTask runTaskLater(Runnable runnable, long delayTicks) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = globalScheduler.runDelayed(plugin, t -> runnable.run(), Math.max(1, delayTicks));
        return wrap(task);
    }

    @Override
    public SchedulerTask runTaskLaterAsynchronously(Runnable runnable, long delayTicks) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = asyncScheduler.runDelayed(plugin, t -> runnable.run(),
                ticksToMillis(Math.max(1, delayTicks)), TimeUnit.MILLISECONDS);
        return wrap(task);
    }

    @Override
    public SchedulerTask runTaskTimer(Runnable runnable, long delayTicks, long periodTicks) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = globalScheduler.runAtFixedRate(plugin, t -> runnable.run(), Math.max(1, delayTicks),
                Math.max(1, periodTicks));
        return wrap(task);
    }

    @Override
    public SchedulerTask runTaskTimerAsynchronously(Runnable runnable, long delayTicks, long periodTicks) {
        if (!plugin.isEnabled())
            return null;
        ScheduledTask task = asyncScheduler.runAtFixedRate(plugin, t -> runnable.run(),
                ticksToMillis(Math.max(1, delayTicks)), ticksToMillis(Math.max(1, periodTicks)), TimeUnit.MILLISECONDS);
        return wrap(task);
    }

    @Override
    public boolean isOnOwnerThread(Entity entity) {
        return Bukkit.isOwnedByCurrentRegion(entity);
    }

    @Override
    public boolean isOnOwnerThread(Location location) {
        return Bukkit.isOwnedByCurrentRegion(location);
    }

    @Override
    public boolean isOnOwnerThread(World world, int chunkX, int chunkZ) {
        return Bukkit.isOwnedByCurrentRegion(world, chunkX, chunkZ);
    }

    @Override
    public boolean isOnOwnerThread(Block block) {
        return Bukkit.isOwnedByCurrentRegion(block);
    }

    private long ticksToMillis(long ticks) {
        return ticks * 50L;
    }

    private SchedulerTask wrap(ScheduledTask task) {
        return new FoliaSchedulerTask(task);
    }
}