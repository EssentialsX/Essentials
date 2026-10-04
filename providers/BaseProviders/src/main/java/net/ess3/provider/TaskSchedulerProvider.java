package net.ess3.provider;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Runs tasks on the thread which owns the thing the task touches.
 * <p>
 * On Bukkit, Spigot and Paper every game task runs on the main thread, so the global, entity and location methods all
 * queue onto it. On Folia, game state is split between regions which tick on their own threads: a task must run on the
 * region which owns the entity or location it touches, and anything that is not owned by a region (the world clock,
 * the weather, the command map) runs on the global region.
 * <p>
 * Delays and periods are in ticks. A delay of 0 runs the task as soon as possible on its thread, which is not
 * necessarily during the current call.
 */
public interface TaskSchedulerProvider extends Provider {
    /**
     * Gets whether the world is split between regions, so that the current thread may own only part of it.
     */
    default boolean isRegionized() {
        return false;
    }

    /**
     * Gets whether the current thread runs global tasks. This is the main thread everywhere but Folia.
     */
    boolean isGlobalThread();

    /**
     * Gets whether the current thread is allowed to access the given entity.
     */
    boolean isOwnedByCurrentThread(Entity entity);

    /**
     * Gets whether the current thread is allowed to access the blocks and entities at the given location.
     */
    boolean isOwnedByCurrentThread(Location location);

    /**
     * Runs a task on the global thread.
     */
    Task runGlobal(Runnable task, long delay);

    Task runGlobalTimer(Runnable task, long delay, long period);

    /**
     * Runs a task on the thread which owns an entity, following the entity if it moves to another region.
     *
     * @param retired run instead of the task if the entity is removed before the task could run, may be null.
     */
    Task runEntity(Entity entity, Runnable task, Runnable retired, long delay);

    Task runEntityTimer(Entity entity, Runnable task, Runnable retired, long delay, long period);

    /**
     * Runs a task on the thread which owns a location.
     */
    Task runLocation(Location location, Runnable task, long delay);

    /**
     * Runs a task on a thread which does not own anything, such as for blocking IO.
     */
    Task runAsync(Runnable task, long delay);

    Task runAsyncTimer(Runnable task, long delay, long period);

    /**
     * Cancels everything this provider has scheduled which can be cancelled in bulk.
     */
    void cancelAll();

    default Task runGlobal(final Runnable task) {
        return runGlobal(task, 0);
    }

    default Task runAsync(final Runnable task) {
        return runAsync(task, 0);
    }

    default Task runEntity(final Entity entity, final Runnable task) {
        return runEntity(entity, task, null, 0);
    }

    default Task runEntity(final Entity entity, final Runnable task, final long delay) {
        return runEntity(entity, task, null, delay);
    }

    default Task runLocation(final Location location, final Runnable task) {
        return runLocation(location, task, 0);
    }

    /**
     * Runs a task now if the current thread owns the entity, otherwise as soon as possible on the thread which does.
     */
    default void executeEntity(final Entity entity, final Runnable task) {
        if (isOwnedByCurrentThread(entity)) {
            task.run();
        } else {
            runEntity(entity, task, 0);
        }
    }

    /**
     * Runs a task now if the current thread owns the location, otherwise as soon as possible on the thread which does.
     */
    default void executeLocation(final Location location, final Runnable task) {
        if (isOwnedByCurrentThread(location)) {
            task.run();
        } else {
            runLocation(location, task, 0);
        }
    }

    /**
     * Runs a task now if this is the global thread, otherwise as soon as possible on the global thread.
     */
    default void executeGlobal(final Runnable task) {
        if (isGlobalThread()) {
            task.run();
        } else {
            runGlobal(task, 0);
        }
    }

    interface Task {
        void cancel();
    }
}
