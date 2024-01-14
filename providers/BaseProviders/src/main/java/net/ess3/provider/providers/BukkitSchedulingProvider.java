package net.ess3.provider.providers;

import net.ess3.provider.SchedulingProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitSchedulingProvider implements SchedulingProvider {
    private final Plugin plugin;

    public BukkitSchedulingProvider(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerInitTask(Runnable runnable) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable);
    }

    @Override
    public boolean isEntityThread(Entity entity) {
        return plugin.getServer().isPrimaryThread();
    }

    @Override
    public boolean isRegionThread(Location location) {
        return plugin.getServer().isPrimaryThread();
    }

    @Override
    public boolean isGlobalThread() {
        return Bukkit.isPrimaryThread();
    }

    @Override
    public void runEntityTask(Entity entity, Runnable runnable) {
        runEntityTask(entity, runnable, 1);
    }

    @Override
    public EssentialsTask runEntityTask(Entity entity, Runnable runnable, long delay) {
        return scheduleSyncTask(runnable, delay);
    }

    @Override
    public EssentialsTask runEntityTaskRepeating(Entity entity, Runnable runnable, long delay, long period) {
        return scheduleSyncTaskRepeating(runnable, delay, period);
    }

    @Override
    public void runLocationalTask(Location location, Runnable runnable) {
        runGlobalLocationalTask(runnable, 1);
    }

    @Override
    public void runLocationalTask(Location location, Runnable runnable, long delay) {
        runGlobalLocationalTask(runnable, delay);
    }

    @Override
    public EssentialsTask runLocationalTaskRepeating(Location location, Runnable runnable, long delay, long period) {
        return scheduleSyncTaskRepeating(runnable, delay, period);
    }

    @Override
    public void runGlobalLocationalTask(Runnable runnable, long delay) {
        scheduleSyncTask(runnable, delay);
    }

    @Override
    public EssentialsTask runGlobalLocationalTaskRepeating(Runnable runnable, long delay, long period) {
        return scheduleSyncTaskRepeating(runnable, delay, period);
    }

    private EssentialsTask scheduleSyncTask(Runnable runnable, long delay) {
        final int task = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
        return () -> plugin.getServer().getScheduler().cancelTask(task);
    }

    private EssentialsTask scheduleSyncTaskRepeating(Runnable runnable, long delay, long period) {
        final BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
        return task::cancel;
    }

    @Override
    public void runAsyncTask(Runnable runnable) {
        runAsyncTaskLater(runnable, 0);
    }

    @Override
    public void runAsyncTaskLater(Runnable runnable, long delay) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    @Override
    public EssentialsTask runAsyncTaskRepeating(Runnable runnable, long delay, long period) {
        final BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
        return task::cancel;
    }

    @Override
    public String getDescription() {
        return "Bukkit Scheduling Provider";
    }
}
