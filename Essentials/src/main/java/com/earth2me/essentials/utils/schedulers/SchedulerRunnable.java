package com.earth2me.essentials.utils.schedulers;

import com.earth2me.essentials.utils.VersionUtil;
import com.earth2me.essentials.utils.schedulers.runnables.FoliaSchedulerRunnable;
import com.earth2me.essentials.utils.schedulers.runnables.SpigotSchedulerRunnable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public abstract class SchedulerRunnable implements Runnable {
    private SchedulerTask task;

    public void cancel() {
        checkScheduled();
        task.cancel();
    }

    private void checkScheduled() {
        if (task == null)
            throw new IllegalStateException("Task not yet scheduled");
    }

    public SchedulerTask getTask() {
        checkScheduled();
        return task;
    }

    public int getTaskId() {
        checkScheduled();
        return task.getOriginalTask().hashCode();
    }

    public boolean isCancelled() {
        checkScheduled();
        return task.isCancelled();
    }

    @Override
    public abstract void run();

    public SchedulerTask runEntityTask(Plugin plugin, Entity entity, Runnable retired) {
        return VersionUtil.isFoliaServer() ? new FoliaSchedulerRunnableImpl(this).runEntityTask(plugin, entity, retired)
                : new SpigotSchedulerRunnableImpl(this).runEntityTask(plugin, entity, retired);
    }

    public SchedulerTask runEntityTaskLater(Plugin plugin, Entity entity, Runnable retired, long delayTicks) {
        return VersionUtil.isFoliaServer()
                ? new FoliaSchedulerRunnableImpl(this).runEntityTaskLater(plugin, entity, retired, delayTicks)
                : new SpigotSchedulerRunnableImpl(this).runEntityTaskLater(plugin, entity, retired, delayTicks);
    }

    public SchedulerTask runEntityTaskTimer(Plugin plugin, Entity entity, Runnable retired, long delayTicks,
                                            long periodTicks) {
        return VersionUtil.isFoliaServer()
                ? new FoliaSchedulerRunnableImpl(this).runEntityTaskTimer(plugin, entity, retired, delayTicks,
                periodTicks)
                : new SpigotSchedulerRunnableImpl(this).runEntityTaskTimer(plugin, entity, retired, delayTicks,
                periodTicks);
    }

    public SchedulerTask runRegionTask(Plugin plugin, Location location) {
        return VersionUtil.isFoliaServer() ? new FoliaSchedulerRunnableImpl(this).runRegionTask(plugin, location)
                : new SpigotSchedulerRunnableImpl(this).runRegionTask(plugin, location);
    }

    public SchedulerTask runRegionTask(Plugin plugin, World world, int chunkX, int chunkZ) {
        return VersionUtil.isFoliaServer()
                ? new FoliaSchedulerRunnableImpl(this).runRegionTask(plugin, world, chunkX, chunkZ)
                : new SpigotSchedulerRunnableImpl(this).runRegionTask(plugin, world, chunkX, chunkZ);
    }

    public SchedulerTask runRegionTaskLater(Plugin plugin, Location location, long delayTicks) {
        return VersionUtil.isFoliaServer()
                ? new FoliaSchedulerRunnableImpl(this).runRegionTaskLater(plugin, location, delayTicks)
                : new SpigotSchedulerRunnableImpl(this).runRegionTaskLater(plugin, location, delayTicks);
    }

    public SchedulerTask runRegionTaskLater(Plugin plugin, World world, int chunkX, int chunkZ, long delayTicks) {
        return VersionUtil.isFoliaServer()
                ? new FoliaSchedulerRunnableImpl(this).runRegionTaskLater(plugin, world, chunkX, chunkZ, delayTicks)
                : new SpigotSchedulerRunnableImpl(this).runRegionTaskLater(plugin, world, chunkX, chunkZ, delayTicks);
    }

    public SchedulerTask runRegionTaskTimer(Plugin plugin, Location location, long delayTicks, long periodTicks) {
        return VersionUtil.isFoliaServer()
                ? new FoliaSchedulerRunnableImpl(this).runRegionTaskTimer(plugin, location, delayTicks, periodTicks)
                : new SpigotSchedulerRunnableImpl(this).runRegionTaskTimer(plugin, location, delayTicks, periodTicks);
    }

    public SchedulerTask runRegionTaskTimer(Plugin plugin, World world, int chunkX, int chunkZ, long delayTicks,
                                            long periodTicks) {
        return VersionUtil.isFoliaServer()
                ? new FoliaSchedulerRunnableImpl(this).runRegionTaskTimer(plugin, world, chunkX, chunkZ, delayTicks,
                periodTicks)
                : new SpigotSchedulerRunnableImpl(this).runRegionTaskTimer(plugin, world, chunkX, chunkZ, delayTicks,
                periodTicks);
    }

    public SchedulerTask runTask(Plugin plugin) {
        return VersionUtil.isFoliaServer() ? new FoliaSchedulerRunnableImpl(this).runTask(plugin)
                : new SpigotSchedulerRunnableImpl(this).runTask(plugin);
    }

    public SchedulerTask runTaskAsynchronously(Plugin plugin) {
        return VersionUtil.isFoliaServer() ? new FoliaSchedulerRunnableImpl(this).runTaskAsynchronously(plugin)
                : new SpigotSchedulerRunnableImpl(this).runTaskAsynchronously(plugin);
    }

    public SchedulerTask runTaskLater(Plugin plugin, long delayTicks) {
        return VersionUtil.isFoliaServer() ? new FoliaSchedulerRunnableImpl(this).runTaskLater(plugin, delayTicks)
                : new SpigotSchedulerRunnableImpl(this).runTaskLater(plugin, delayTicks);
    }

    public SchedulerTask runTaskLaterAsynchronously(Plugin plugin, long delayTicks) {
        return VersionUtil.isFoliaServer()
                ? new FoliaSchedulerRunnableImpl(this).runTaskLaterAsynchronously(plugin, delayTicks)
                : new SpigotSchedulerRunnableImpl(this).runTaskLaterAsynchronously(plugin, delayTicks);
    }

    public SchedulerTask runTaskTimer(Plugin plugin, long delayTicks, long periodTicks) {
        return VersionUtil.isFoliaServer()
                ? new FoliaSchedulerRunnableImpl(this).runTaskTimer(plugin, delayTicks, periodTicks)
                : new SpigotSchedulerRunnableImpl(this).runTaskTimer(plugin, delayTicks, periodTicks);
    }

    public SchedulerTask runTaskTimerAsynchronously(Plugin plugin, long delayTicks, long periodTicks) {
        return VersionUtil.isFoliaServer()
                ? new FoliaSchedulerRunnableImpl(this).runTaskTimerAsynchronously(plugin, delayTicks, periodTicks)
                : new SpigotSchedulerRunnableImpl(this).runTaskTimerAsynchronously(plugin, delayTicks, periodTicks);
    }

    protected SchedulerTask setupTask(SchedulerTask task) {
        this.task = task;
        return task;
    }

    private static final class FoliaSchedulerRunnableImpl extends FoliaSchedulerRunnable {
        private final SchedulerRunnable delegate;

        private FoliaSchedulerRunnableImpl(SchedulerRunnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void run() {
            delegate.run();
        }

        @Override
        protected SchedulerTask setupTask(SchedulerTask task) {
            delegate.setupTask(task);
            return task;
        }
    }

    private static final class SpigotSchedulerRunnableImpl extends SpigotSchedulerRunnable {
        private final SchedulerRunnable delegate;

        private SpigotSchedulerRunnableImpl(SchedulerRunnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void run() {
            delegate.run();
        }

        @Override
        protected SchedulerTask setupTask(SchedulerTask task) {
            delegate.setupTask(task);
            return task;
        }
    }
}