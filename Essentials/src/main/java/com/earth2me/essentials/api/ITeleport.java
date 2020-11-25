package com.earth2me.essentials.api;

import com.earth2me.essentials.Trade;
import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @deprecated This API is not asynchronous and is no longer maintained. Use {@link com.earth2me.essentials.api.IAsyncTeleport IAsyncTeleport}.
 */
public interface ITeleport {
    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @param loc      - Where should the player end up
     * @param cooldown - If cooldown should be enforced
     * @param cause    - The reported teleportPlayer cause
     * @throws Exception
     */
    @Deprecated
    void now(Location loc, boolean cooldown, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @param entity   - Where should the player end up
     * @param cooldown - If cooldown should be enforced
     * @param cause    - The reported teleportPlayer cause
     * @throws Exception
     */
    @Deprecated
    void now(Player entity, boolean cooldown, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific location
     *
     * @param loc       - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @throws Exception
     */
    @Deprecated
    void teleport(Location loc, Trade chargeFor) throws Exception;

    /**
     * Teleport a player to a specific location
     *
     * @param loc       - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws Exception
     */
    @Deprecated
    void teleport(Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific player
     *
     * @param entity    - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws Exception
     */
    @Deprecated
    void teleport(Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific location
     *
     * @param otherUser - Which user will be teleported
     * @param loc       - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws Exception
     */
    @Deprecated
    void teleportPlayer(IUser otherUser, Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific player
     *
     * @param otherUser - Which user will be teleported
     * @param entity    - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws Exception
     */
    @Deprecated
    void teleportPlayer(IUser otherUser, Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport wrapper used to handle tp fallback on /jail and /home
     *
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws Exception
     */
    @Deprecated
    void respawn(final Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport wrapper used to handle /warp teleports
     *
     * @param otherUser - Which user will be teleported
     * @param warp      - The name of the warp the user will be teleported too.
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws Exception
     */
    @Deprecated
    void warp(IUser otherUser, String warp, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport wrapper used to handle /back teleports
     *
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @throws Exception
     */
    @Deprecated
    void back(Trade chargeFor) throws Exception;

    /**
     * Teleport wrapper used to handle /back teleports that
     * are executed by a different player with this
     * instance of teleport as a target.
     *
     * @param teleporter - The user performing the /back command.
     *                   This value may be {@code null} to indicate console.
     * @param chargeFor  - What the {@code teleporter} will be charged if teleportPlayer is successful
     * @throws Exception
     */
    @Deprecated
    void back(IUser teleporter, Trade chargeFor) throws Exception;

    /**
     * Teleport wrapper used to handle throwing user home after a jail sentence
     *
     * @throws Exception
     */
    @Deprecated
    void back() throws Exception;

}
