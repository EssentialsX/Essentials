package com.earth2me.essentials.api;

import com.earth2me.essentials.Trade;
import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.CompletableFuture;


public interface IAsyncTeleport {
    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @deprecated Use {@link IAsyncTeleport#now(Location, boolean, PlayerTeleportEvent.TeleportCause, CompletableFuture, CompletableFuture)}
     *
     * @param loc      - Where should the player end up
     * @param cooldown - If cooldown should be enforced
     * @param cause    - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    @Deprecated
    void now(Location loc, boolean cooldown, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @param loc             - Where should the player end up
     * @param cooldown        - If cooldown should be enforced
     * @param cause           - The reported teleportPlayer cause
     * @param exceptionFuture - Future which is completed with an exception if one is thrown during execution
     * @param future          - Future which is completed with the success status of the execution
     */
    void now(Location loc, boolean cooldown, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future);

    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @deprecated Use {@link IAsyncTeleport#now(Player, boolean, PlayerTeleportEvent.TeleportCause, CompletableFuture, CompletableFuture)}
     *
     * @param entity   - Where should the player end up
     * @param cooldown - If cooldown should be enforced
     * @param cause    - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    @Deprecated
    void now(Player entity, boolean cooldown, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @param entity          - Where should the player end up
     * @param cooldown        - If cooldown should be enforced
     * @param cause           - The reported teleportPlayer cause
     * @param exceptionFuture - Future which is completed with an exception if one is thrown during execution
     * @param future          - Future which is completed with the success status of the execution
     */
    void now(Player entity, boolean cooldown, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future);

    @Deprecated
    void teleport(Location loc, Trade chargeFor) throws Exception;

    /**
     * Teleport a player to a specific location
     *
     * @deprecated {@link IAsyncTeleport#teleport(Location, Trade, PlayerTeleportEvent.TeleportCause, CompletableFuture, CompletableFuture)}
     *
     * @param loc       - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    @Deprecated
    void teleport(Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific location
     *
     * @param loc             - Where should the player end up
     * @param chargeFor       - What the user will be charged if teleportPlayer is successful
     * @param cause           - The reported teleportPlayer cause.
     * @param exceptionFuture - Future which is completed with an exception if one is thrown during execution
     * @param future          - Future which is completed with the success status of the execution
     */
    void teleport(Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future);

    /**
     * Teleport a player to a specific player
     *
     * @deprecated Use {@link IAsyncTeleport#teleport(Player, Trade, PlayerTeleportEvent.TeleportCause, CompletableFuture, CompletableFuture)}
     *
     * @param entity    - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    @Deprecated
    void teleport(Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific player
     *
     * @param entity          - Where should the player end up
     * @param chargeFor       - What the user will be charged if teleportPlayer is successful
     * @param cause           - The reported teleportPlayer cause
     * @param exceptionFuture - Future which is completed with an exception if one is thrown during execution
     * @param future          - Future which is completed with the success status of the execution
     */
    void teleport(Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future);

    /**
     * Teleport a player to a specific location
     *
     * @deprecated Use {@link IAsyncTeleport#teleportPlayer(IUser, Location, Trade, PlayerTeleportEvent.TeleportCause, CompletableFuture, CompletableFuture)}
     *
     * @param otherUser - Which user will be teleported
     * @param loc       - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    @Deprecated
    void teleportPlayer(IUser otherUser, Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific location
     *
     * @param otherUser       - Which user will be teleported
     * @param loc             - Where should the player end up
     * @param chargeFor       - What the user will be charged if teleportPlayer is successful
     * @param cause           - The reported teleportPlayer cause
     * @param exceptionFuture - Future which is completed with an exception if one is thrown during execution
     * @param future          - Future which is completed with the success status of the execution
     */
    void teleportPlayer(IUser otherUser, Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future);

    /**
     * Teleport a player to a specific player
     *
     * @deprecated Use {@link IAsyncTeleport#teleportPlayer(IUser, Player, Trade, PlayerTeleportEvent.TeleportCause, CompletableFuture, CompletableFuture)}
     *
     * @param otherUser - Which user will be teleported
     * @param entity    - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    @Deprecated
    void teleportPlayer(IUser otherUser, Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific player
     *
     * @param otherUser       - Which user will be teleported
     * @param entity          - Where should the player end up
     * @param chargeFor       - What the user will be charged if teleportPlayer is successful
     * @param cause           - The reported teleportPlayer cause
     * @param exceptionFuture - Future which is completed with an exception if one is thrown during execution
     * @param future          - Future which is completed with the success status of the execution
     */
    void teleportPlayer(IUser otherUser, Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future);

    /**
     * Teleport wrapper used to handle tp fallback on /jail and /home
     *
     * @deprecated Use {@link IAsyncTeleport#respawn(Trade, PlayerTeleportEvent.TeleportCause, CompletableFuture, CompletableFuture)}
     *
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    @Deprecated
    void respawn(final Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport wrapper used to handle tp fallback on /jail and /home
     *
     * @param chargeFor       - What the user will be charged if teleportPlayer is successful
     * @param cause           - The reported teleportPlayer cause
     * @param exceptionFuture - Future which is completed with an exception if one is thrown during execution
     * @param future          - Future which is completed with the success status of the execution
     */
    void respawn(final Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future);

    /**
     * Teleport wrapper used to handle /warp teleports
     *
     * @deprecated Use {@link IAsyncTeleport#warp(IUser, String, Trade, PlayerTeleportEvent.TeleportCause, CompletableFuture, CompletableFuture)}
     *
     * @param otherUser - Which user will be teleported
     * @param warp      - The name of the warp the user will be teleported too.
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     *
     * @throws Exception
     */
    @Deprecated
    void warp(IUser otherUser, String warp, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport wrapper used to handle /warp teleports
     *
     * @param otherUser       - Which user will be teleported
     * @param warp            - The name of the warp the user will be teleported too.
     * @param chargeFor       - What the user will be charged if teleportPlayer is successful
     * @param cause           - The reported teleportPlayer cause
     * @param exceptionFuture - Future which is completed with an exception if one is thrown during execution
     * @param future          - Future which is completed with the success status of the execution
     */
    void warp(IUser otherUser, String warp, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future);

    /**
     * Teleport wrapper used to handle /back teleports
     *
     * @deprecated {@link IAsyncTeleport#back(Trade, CompletableFuture, CompletableFuture)}
     *
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     *
     * @throws Exception
     */
    @Deprecated
    void back(Trade chargeFor) throws Exception;

    /**
     * Teleport wrapper used to handle /back teleports
     *
     * @param chargeFor       - What the user will be charged if teleportPlayer is successful
     * @param exceptionFuture - Future which is completed with an exception if one is thrown during execution
     * @param future          - Future which is completed with the success status of the execution
     */
    void back(Trade chargeFor, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future);

    /**
     * Teleport wrapper used to handle /back teleports that
     * are executed by a different player with this
     * instance of teleport as a target.
     *
     * @deprecated Use {@link IAsyncTeleport#back(IUser, Trade, CompletableFuture, CompletableFuture)}
     *
     * @param teleporter - The user performing the /back command.
     *                     This value may be {@code null} to indicate console.
     * @param chargeFor - What the {@code teleporter} will be charged if teleportPlayer is successful
     *
     * @throws Exception
     */
    @Deprecated
    void back(IUser teleporter, Trade chargeFor) throws Exception;

    /**
     * Teleport wrapper used to handle /back teleports that
     * are executed by a different player with this
     * instance of teleport as a target.
     *
     * @param teleporter      - The user performing the /back command.
     *                          This value may be {@code null} to indicate console.
     * @param chargeFor       - What the {@code teleporter} will be charged if teleportPlayer is successful
     * @param exceptionFuture - Future which is completed with an exception if one is thrown during execution
     * @param future          - Future which is completed with the success status of the execution
     */
    void back(IUser teleporter, Trade chargeFor, CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future);

    /**
     * Teleport wrapper used to handle throwing user home after a jail sentence
     *
     * @deprecated Use {@link IAsyncTeleport#back(CompletableFuture, CompletableFuture)}
     *
     * @throws Exception
     */
    @Deprecated
    void back() throws Exception;

    /**
     * Teleport wrapper used to handle throwing user home after a jail sentence
     *
     * @param exceptionFuture - Future which is completed with an exception if one is thrown during execution
     * @param future          - Future which is completed with the success status of the execution
     */
    void back(CompletableFuture<Exception> exceptionFuture, CompletableFuture<Boolean> future);

}
