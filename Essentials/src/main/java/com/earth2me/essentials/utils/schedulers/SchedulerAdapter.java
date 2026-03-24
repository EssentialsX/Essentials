package com.earth2me.essentials.utils.schedulers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

/**
 * An abstraction layer for scheduling tasks across different Minecraft server implementations, such as Folia and
 * Spigot.
 */
public interface SchedulerAdapter {
    default void checkedRunEntityTask(Entity entity, Runnable runnable) {
        if (isOnOwnerThread(entity)) {
            runnable.run();
        } else {
            runEntityTask(entity, runnable);
        }
    }

    default void checkedRunRegionTask(Location location, Runnable runnable) {
        if (isOnOwnerThread(location)) {
            runnable.run();
        } else {
            runRegionTask(location, runnable);
        }
    }

    /**
     * Returns true if the current thread is the correct owner thread for safely accessing the target.
     * <p>
     * Spigot: true if running on the main server thread.
     * </p>
     * <p>
     * Folia: true if the current thread owns the target's region (isOwnedByCurrentRegion).
     * </p>
     *
     * @param block the target block
     * @return {@code true} if the current thread is the owner thread for the block; {@code false} otherwise
     */
    boolean isOnOwnerThread(Block block);

    /**
     * Returns true if the current thread is the correct owner thread for safely accessing the target.
     * <p>
     * Spigot: true if running on the main server thread.
     * </p>
     * <p>
     * Folia: true if the current thread owns the target's region (isOwnedByCurrentRegion).
     * </p>
     *
     * @param entity the target entity
     * @return {@code true} if the current thread is the owner thread for the entity; {@code false} otherwise
     */
    boolean isOnOwnerThread(Entity entity);

    /**
     * Returns true if the current thread is the correct owner thread for safely accessing the target.
     * <p>
     * Spigot: true if running on the main server thread.
     * </p>
     * <p>
     * Folia: true if the current thread owns the target's region (isOwnedByCurrentRegion).
     * </p>
     *
     * @param location the target location
     * @return {@code true} if the current thread is the owner thread for the location; {@code false} otherwise
     */
    boolean isOnOwnerThread(Location location);

    /**
     * Returns true if the current thread is the correct owner thread for safely accessing the target.
     * <p>
     * Spigot: true if running on the main server thread.
     * </p>
     * <p>
     * Folia: true if the current thread owns the target's region (isOwnedByCurrentRegion).
     * </p>
     *
     * @param world  the target world
     * @param chunkX the target chunk X coordinate
     * @param chunkZ the target chunk Z coordinate
     * @return {@code true} if the current thread is the owner thread for the chunk; {@code false} otherwise
     */
    boolean isOnOwnerThread(World world, int chunkX, int chunkZ);

    /**
     * Executes a task specifically linked to an entity immediately. On Spigot, this defaults to the global thread.
     */
    SchedulerTask runEntityTask(Entity entity, Runnable runnable);

    /**
     * Executes a task specifically linked to an entity after a delay. On Spigot, this defaults to the global thread.
     *
     * @param delayTicks Delay before execution, measured in ticks.
     */
    SchedulerTask runEntityTaskLater(Entity entity, Runnable runnable, long delayTicks);

    /**
     * Executes a repeating task specifically linked to an entity. On Spigot, this defaults to the global thread.
     *
     * @param delayTicks  Initial delay before the first execution (in ticks).
     * @param periodTicks Period between each subsequent execution (in ticks).
     */
    SchedulerTask runEntityTaskTimer(Entity entity, Runnable runnable, long delayTicks, long periodTicks);

    /**
     * Executes a task associated with a specific region or chunk. On Spigot, this defaults to the global thread.
     */
    SchedulerTask runRegionTask(Location location, Runnable runnable);

    /**
     * Executes a task associated with a specific region or chunk. On Spigot, this defaults to the global thread.
     */
    SchedulerTask runRegionTask(World world, int chunkX, int chunkZ, Runnable runnable);

    /**
     * Executes a task associated with a specific region or chunk after a delay. On Spigot, this defaults to the global
     * thread.
     *
     * @param delayTicks Delay before execution, measured in ticks.
     */
    SchedulerTask runRegionTaskLater(Location location, Runnable runnable, long delayTicks);

    /**
     * Executes a task associated with a specific region or chunk after a delay. On Spigot, this defaults to the global
     * thread.
     *
     * @param delayTicks Delay before execution, measured in ticks.
     */
    SchedulerTask runRegionTaskLater(World world, int chunkX, int chunkZ, Runnable runnable, long delayTicks);

    /**
     * Executes a repeating task associated with a specific region or chunk. On Spigot, this defaults to the global
     * thread.
     *
     * @param delayTicks  Initial delay before the first execution (in ticks).
     * @param periodTicks Period between each subsequent execution (in ticks).
     */
    SchedulerTask runRegionTaskTimer(Location location, Runnable runnable, long delayTicks, long periodTicks);

    /**
     * Executes a repeating task associated with a specific region or chunk. On Spigot, this defaults to the global
     * thread.
     *
     * @param delayTicks  Initial delay before the first execution (in ticks).
     * @param periodTicks Period between each subsequent execution (in ticks).
     */
    SchedulerTask runRegionTaskTimer(World world, int chunkX, int chunkZ, Runnable runnable, long delayTicks,
                                     long periodTicks);

    /**
     * Executes a task on the global server thread.
     */
    SchedulerTask runTask(Runnable runnable);

    /**
     * Executes a task asynchronously immediately, off the main server thread.
     */
    SchedulerTask runTaskAsynchronously(Runnable runnable);

    /**
     * Executes a task on the global server thread after a specified delay.
     *
     * @param delayTicks Delay before execution, measured in server ticks (1 tick = 50ms).
     */
    SchedulerTask runTaskLater(Runnable runnable, long delayTicks);

    /**
     * Executes an asynchronous task after a specified delay.
     *
     * @param delayTicks Delay before execution, measured in ticks.
     */
    SchedulerTask runTaskLaterAsynchronously(Runnable runnable, long delayTicks);

    /**
     * Executes a repeating task on the global server thread.
     *
     * @param delayTicks  Initial delay before the first execution (in ticks).
     * @param periodTicks Period between each subsequent execution (in ticks).
     */
    SchedulerTask runTaskTimer(Runnable runnable, long delayTicks, long periodTicks);

    /**
     * Executes an asynchronous repeating task.
     *
     * @param delayTicks  Initial delay before the first execution (in ticks).
     * @param periodTicks Period between each subsequent execution (in ticks).
     */
    SchedulerTask runTaskTimerAsynchronously(Runnable runnable, long delayTicks, long periodTicks);

}