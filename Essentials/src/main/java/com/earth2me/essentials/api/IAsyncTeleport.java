package com.earth2me.essentials.api;

import com.earth2me.essentials.Trade;
import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.CompletableFuture;

/**
 * Manages EssentialsX's teleport functionality for a player.
 * Use this if you want to access EssentialsX's async/safe teleport functionality and teleport warmups and cooldowns.
 */
public interface IAsyncTeleport {

    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @param loc      - Where should the player end up
     * @param cooldown - If cooldown should be enforced
     * @param cause    - The reported teleportPlayer cause
     * @param future   - Future which is completed with the success status of the execution
     */
    void now(Location loc, boolean cooldown, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Boolean> future);

    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @param entity   - Where should the player end up
     * @param cooldown - If cooldown should be enforced
     * @param cause    - The reported teleportPlayer cause
     * @param future   - Future which is completed with the success status of the execution
     */
    void now(Player entity, boolean cooldown, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Boolean> future);

    /**
     * Used to skip all safety checks while teleporting a player asynchronously.
     *
     * @param loc    - Where should the player end up
     * @param cause  - The reported teleportPlayer cause
     * @param future - Future which is completed with the success status of the execution
     */
    void nowUnsafe(Location loc, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Boolean> future);

    /**
     * Teleport a player to a specific location
     *
     * @param loc       - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause.
     * @param future    - Future which is completed with the success status of the execution
     */
    void teleport(Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Boolean> future);

    /**
     * Teleport a player to a specific player
     *
     * @param entity    - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @param future    - Future which is completed with the success status of the execution
     */
    void teleport(Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Boolean> future);

    /**
     * Teleport a player to a specific location
     *
     * @param otherUser - Which user will be teleported
     * @param loc       - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @param future    - Future which is completed with the success status of the execution
     */
    void teleportPlayer(IUser otherUser, Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Boolean> future);

    /**
     * Teleport a player to a specific player
     *
     * @param otherUser - Which user will be teleported
     * @param entity    - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @param future    - Future which is completed with the success status of the execution
     */
    void teleportPlayer(IUser otherUser, Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Boolean> future);

    /**
     * Teleport wrapper used to handle tp fallback on /jail and /home
     *
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @param future    - Future which is completed with the success status of the execution
     */
    void respawn(final Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Boolean> future);

    /**
     * Teleport wrapper used to handle /warp teleports
     *
     * @param otherUser - Which user will be teleported
     * @param warp      - The name of the warp the user will be teleported too.
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @param future    - Future which is completed with the success status of the execution
     */
    void warp(IUser otherUser, String warp, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Boolean> future);

    /**
     * Teleport wrapper used to handle /back teleports
     *
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param future    - Future which is completed with the success status of the execution
     */
    void back(Trade chargeFor, CompletableFuture<Boolean> future);

    /**
     * Teleport wrapper used to handle /back teleports that
     * are executed by a different player with this
     * instance of teleport as a target.
     *
     * @param teleporter - The user performing the /back command.
     *                   This value may be {@code null} to indicate console.
     * @param chargeFor  - What the {@code teleporter} will be charged if teleportPlayer is successful
     * @param future     - Future which is completed with the success status of the execution
     */
    void back(IUser teleporter, Trade chargeFor, CompletableFuture<Boolean> future);

    /**
     * Teleport wrapper used to handle throwing user home after a jail sentence
     *
     * @param future - Future which is completed with the success status of the execution
     */
    void back(CompletableFuture<Boolean> future);

}
