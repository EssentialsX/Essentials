package com.earth2me.essentials.utils.schedulers.runnables;

import com.earth2me.essentials.utils.schedulers.SchedulerRunnable;
import com.earth2me.essentials.utils.schedulers.SchedulerTask;
import com.earth2me.essentials.utils.schedulers.adapter.SpigotSchedulerTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public abstract class SpigotSchedulerRunnable extends SchedulerRunnable {
    @Override
    public SchedulerTask runEntityTask(Plugin plugin, Entity entity, Runnable retired) {
        return runTask(plugin);
    }

    @Override
    public SchedulerTask runEntityTaskLater(Plugin plugin, Entity entity, Runnable retired, long delayTicks) {
        return runTaskLater(plugin, delayTicks);
    }

    @Override
    public SchedulerTask runEntityTaskTimer(Plugin plugin, Entity entity, Runnable retired, long delayTicks,
                                            long periodTicks) {
        return runTaskTimer(plugin, delayTicks, periodTicks);
    }

    @Override
    public SchedulerTask runRegionTask(Plugin plugin, Location location) {
        return runTask(plugin);
    }

    @Override
    public SchedulerTask runRegionTask(Plugin plugin, World world, int chunkX, int chunkZ) {
        return runTask(plugin);
    }

    @Override
    public SchedulerTask runRegionTaskLater(Plugin plugin, Location location, long delayTicks) {
        return runTaskLater(plugin, delayTicks);
    }

    @Override
    public SchedulerTask runRegionTaskLater(Plugin plugin, World world, int chunkX, int chunkZ, long delayTicks) {
        return runTaskLater(plugin, delayTicks);
    }

    @Override
    public SchedulerTask runRegionTaskTimer(Plugin plugin, Location location, long delayTicks, long periodTicks) {
        return runTaskTimer(plugin, delayTicks, periodTicks);
    }

    @Override
    public SchedulerTask runRegionTaskTimer(Plugin plugin, World world, int chunkX, int chunkZ, long delayTicks,
                                            long periodTicks) {
        return runTaskTimer(plugin, delayTicks, periodTicks);
    }

    @Override
    public SchedulerTask runTask(Plugin plugin) {
        return setupTask(new SpigotSchedulerTask(Bukkit.getScheduler().runTask(plugin, this)));
    }

    @Override
    public SchedulerTask runTaskAsynchronously(Plugin plugin) {
        return setupTask(new SpigotSchedulerTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, this)));
    }

    @Override
    public SchedulerTask runTaskLater(Plugin plugin, long delayTicks) {
        return setupTask(new SpigotSchedulerTask(Bukkit.getScheduler().runTaskLater(plugin, this, delayTicks)));
    }

    @Override
    public SchedulerTask runTaskLaterAsynchronously(Plugin plugin, long delayTicks) {
        return setupTask(
                new SpigotSchedulerTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, delayTicks)));
    }

    @Override
    public SchedulerTask runTaskTimer(Plugin plugin, long delayTicks, long periodTicks) {
        return setupTask(
                new SpigotSchedulerTask(Bukkit.getScheduler().runTaskTimer(plugin, this, delayTicks, periodTicks)));
    }

    @Override
    public SchedulerTask runTaskTimerAsynchronously(Plugin plugin, long delayTicks, long periodTicks) {
        return setupTask(new SpigotSchedulerTask(
                Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delayTicks, periodTicks)));
    }
}