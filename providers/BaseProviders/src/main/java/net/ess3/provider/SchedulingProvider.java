package net.ess3.provider;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface SchedulingProvider extends Provider {
    void registerInitTask(Runnable runnable);

    boolean isEntityThread(Entity entity);

    boolean isRegionThread(Location location);

    boolean isGlobalThread();

    void runEntityTask(Entity entity, Runnable runnable);

    EssentialsTask runEntityTask(Entity entity, Runnable runnable, long delay);

    EssentialsTask runEntityTaskRepeating(Entity entity, Runnable runnable, long delay, long period);

    void runLocationalTask(Location location, Runnable runnable);

    void runLocationalTask(Location location, Runnable runnable, long delay);

    EssentialsTask runLocationalTaskRepeating(Location location, Runnable runnable, long delay, long period);

    void runGlobalLocationalTask(Runnable runnable, long delay);

    EssentialsTask runGlobalLocationalTaskRepeating(Runnable runnable, long delay, long period);

    void runAsyncTask(Runnable runnable);

    void runAsyncTaskLater(Runnable runnable, long delay);

    EssentialsTask runAsyncTaskRepeating(Runnable runnable, long delay, long period);

    interface EssentialsTask {
        void cancel();
    }
}
