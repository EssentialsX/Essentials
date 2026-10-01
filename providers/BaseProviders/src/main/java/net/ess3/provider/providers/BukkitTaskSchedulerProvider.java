package net.ess3.provider.providers;

import net.ess3.provider.TaskSchedulerProvider;
import net.essentialsx.providers.ProviderData;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

@ProviderData(description = "Bukkit Task Scheduler Provider")
public class BukkitTaskSchedulerProvider implements TaskSchedulerProvider {
    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public BukkitTaskSchedulerProvider(final Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    @Override
    public boolean isGlobalThread() {
        return plugin.getServer().isPrimaryThread();
    }

    @Override
    public boolean isOwnedByCurrentThread(final Entity entity) {
        return isGlobalThread();
    }

    @Override
    public boolean isOwnedByCurrentThread(final Location location) {
        return isGlobalThread();
    }

    @Override
    public Task runGlobal(final Runnable task, final long delay) {
        final BukkitTask bukkitTask = delay <= 0 ? scheduler.runTask(plugin, task) : scheduler.runTaskLater(plugin, task, delay);
        return bukkitTask::cancel;
    }

    @Override
    public Task runGlobalTimer(final Runnable task, final long delay, final long period) {
        final BukkitTask bukkitTask = scheduler.runTaskTimer(plugin, task, delay, period);
        return bukkitTask::cancel;
    }

    @Override
    public Task runEntity(final Entity entity, final Runnable task, final Runnable retired, final long delay) {
        return runGlobal(task, delay);
    }

    @Override
    public Task runEntityTimer(final Entity entity, final Runnable task, final Runnable retired, final long delay, final long period) {
        return runGlobalTimer(task, delay, period);
    }

    @Override
    public Task runLocation(final Location location, final Runnable task, final long delay) {
        return runGlobal(task, delay);
    }

    @Override
    public Task runAsync(final Runnable task, final long delay) {
        final BukkitTask bukkitTask = delay <= 0 ? scheduler.runTaskAsynchronously(plugin, task) : scheduler.runTaskLaterAsynchronously(plugin, task, delay);
        return bukkitTask::cancel;
    }

    @Override
    public Task runAsyncTimer(final Runnable task, final long delay, final long period) {
        final BukkitTask bukkitTask = scheduler.runTaskTimerAsynchronously(plugin, task, delay, period);
        return bukkitTask::cancel;
    }

    @Override
    public void cancelAll() {
        scheduler.cancelTasks(plugin);
    }
}
