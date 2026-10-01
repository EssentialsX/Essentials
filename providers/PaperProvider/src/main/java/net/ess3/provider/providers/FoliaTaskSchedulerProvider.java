package net.ess3.provider.providers;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.ess3.provider.TaskSchedulerProvider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

@ProviderData(description = "Folia Task Scheduler Provider", weight = 1)
public class FoliaTaskSchedulerProvider implements TaskSchedulerProvider {
    private static final Task NOOP = () -> {
    };

    private final Plugin plugin;
    private final Server server;

    public FoliaTaskSchedulerProvider(final Plugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
    }

    @Override
    public boolean isRegionized() {
        return true;
    }

    @Override
    public boolean isGlobalThread() {
        return server.isGlobalTickThread();
    }

    @Override
    public boolean isOwnedByCurrentThread(final Entity entity) {
        return server.isOwnedByCurrentRegion(entity);
    }

    @Override
    public boolean isOwnedByCurrentThread(final Location location) {
        return server.isOwnedByCurrentRegion(location);
    }

    @Override
    public Task runGlobal(final Runnable task, final long delay) {
        if (delay <= 0) {
            return wrap(server.getGlobalRegionScheduler().run(plugin, scheduled -> task.run()));
        }
        return wrap(server.getGlobalRegionScheduler().runDelayed(plugin, scheduled -> task.run(), delay));
    }

    @Override
    public Task runGlobalTimer(final Runnable task, final long delay, final long period) {
        return wrap(server.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduled -> task.run(), Math.max(delay, 1), period));
    }

    @Override
    public Task runEntity(final Entity entity, final Runnable task, final Runnable retired, final long delay) {
        final ScheduledTask scheduled;
        if (delay <= 0) {
            scheduled = entity.getScheduler().run(plugin, t -> task.run(), retired);
        } else {
            scheduled = entity.getScheduler().runDelayed(plugin, t -> task.run(), retired, delay);
        }
        return wrapEntity(scheduled, retired);
    }

    @Override
    public Task runEntityTimer(final Entity entity, final Runnable task, final Runnable retired, final long delay, final long period) {
        return wrapEntity(entity.getScheduler().runAtFixedRate(plugin, t -> task.run(), retired, Math.max(delay, 1), period), retired);
    }

    @Override
    public Task runLocation(final Location location, final Runnable task, final long delay) {
        if (delay <= 0) {
            return wrap(server.getRegionScheduler().run(plugin, location, scheduled -> task.run()));
        }
        return wrap(server.getRegionScheduler().runDelayed(plugin, location, scheduled -> task.run(), delay));
    }

    @Override
    public Task runAsync(final Runnable task, final long delay) {
        if (delay <= 0) {
            return wrap(server.getAsyncScheduler().runNow(plugin, scheduled -> task.run()));
        }
        return wrap(server.getAsyncScheduler().runDelayed(plugin, scheduled -> task.run(), delay * 50, TimeUnit.MILLISECONDS));
    }

    @Override
    public Task runAsyncTimer(final Runnable task, final long delay, final long period) {
        return wrap(server.getAsyncScheduler().runAtFixedRate(plugin, scheduled -> task.run(), Math.max(delay, 0) * 50, period * 50, TimeUnit.MILLISECONDS));
    }

    @Override
    public void cancelAll() {
        server.getGlobalRegionScheduler().cancelTasks(plugin);
        server.getAsyncScheduler().cancelTasks(plugin);
    }

    private static Task wrap(final ScheduledTask scheduled) {
        return scheduled == null ? NOOP : scheduled::cancel;
    }

    // The entity scheduler returns null, without calling the retired callback, if the entity is already removed
    private static Task wrapEntity(final ScheduledTask scheduled, final Runnable retired) {
        if (scheduled == null) {
            if (retired != null) {
                retired.run();
            }
            return NOOP;
        }
        return scheduled::cancel;
    }

    @ProviderTest
    public static boolean test() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (final ClassNotFoundException ignored) {
            return false;
        }
    }
}
