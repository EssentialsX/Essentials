package net.ess3.provider.providers;

import io.papermc.paper.threadedregions.RegionizedServerInitEvent;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.ess3.provider.SchedulingProvider;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FoliaSchedulingProvider implements SchedulingProvider, Listener {
    private final Plugin plugin;
    private List<Runnable> initTasks = new ArrayList<>();

    public FoliaSchedulingProvider(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onServerInit(RegionizedServerInitEvent event) {
        for (final Runnable tasks : initTasks) {
            tasks.run();
        }
        initTasks = null;
    }

    @Override
    public void registerInitTask(Runnable runnable) {
        if (initTasks == null) {
            throw new IllegalStateException();
        }
        initTasks.add(runnable);
    }

    @Override
    public boolean isEntityThread(Entity entity) {
        return plugin.getServer().isOwnedByCurrentRegion(entity);
    }

    @Override
    public boolean isRegionThread(Location location) {
        return plugin.getServer().isOwnedByCurrentRegion(location);
    }

    @Override
    public boolean isGlobalThread() {
        return plugin.getServer().isGlobalTickThread();
    }

    @Override
    public void runEntityTask(Entity entity, Runnable runnable) {
        runEntityTask(entity, runnable, 1);
    }

    @Override
    public EssentialsTask runEntityTask(Entity entity, Runnable runnable, long delay) {
        final ScheduledTask task = entity.getScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), null, delay);
        if (task == null) {
            throw new IllegalArgumentException("entity is removed!");
        }
        return task::cancel;
    }

    @Override
    public EssentialsTask runEntityTaskRepeating(Entity entity, Runnable runnable, long delay, long period) {
        final ScheduledTask task = entity.getScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), null, delay, period);
        if (task == null) {
            throw new IllegalArgumentException("entity is removed!");
        }
        return task::cancel;
    }

    @Override
    public void runLocationalTask(Location location, Runnable runnable) {
        plugin.getServer().getRegionScheduler().execute(plugin, location, runnable);
    }

    @Override
    public void runLocationalTask(Location location, Runnable runnable, long delay) {
        plugin.getServer().getRegionScheduler().runDelayed(plugin, location, scheduledTask -> runnable.run(), delay);
    }

    @Override
    public EssentialsTask runLocationalTaskRepeating(Location location, Runnable runnable, long delay, long period) {
        final ScheduledTask task = plugin.getServer().getRegionScheduler().runAtFixedRate(plugin, location, scheduledTask -> runnable.run(), delay, period);;
        return task::cancel;
    }

    @Override
    public void runGlobalLocationalTask(Runnable runnable, long delay) {
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delay);
    }

    @Override
    public EssentialsTask runGlobalLocationalTaskRepeating(Runnable runnable, long delay, long period) {
        final ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), delay, period);
        return task::cancel;
    }

    @Override
    public void runAsyncTask(Runnable runnable) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runAsyncTaskLater(Runnable runnable, long delay) {
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delay * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public EssentialsTask runAsyncTaskRepeating(Runnable runnable, long delay, long period) {
        final ScheduledTask task = plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), delay, period * 50, TimeUnit.MILLISECONDS);
        return task::cancel;
    }

    @Override
    public String getDescription() {
        return "Folia Scheduling Provider";
    }

}
