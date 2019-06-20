package com.earth2me.essentials.api;

import com.earth2me.essentials.Trade;
import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;


/**
 * <p>ITeleport interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface ITeleport {
    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @param loc      - Where should the player end up
     * @param cooldown - If cooldown should be enforced
     * @param cause    - The reported teleportPlayer cause
     * @throws java.lang.Exception if any.
     */
    void now(Location loc, boolean cooldown, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Used to skip teleportPlayer delay when teleporting someone to a location or player.
     *
     * @param entity   - Where should the player end up
     * @param cooldown - If cooldown should be enforced
     * @param cause    - The reported teleportPlayer cause
     * @throws java.lang.Exception if any.
     */
    void now(Player entity, boolean cooldown, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * <p>teleport.</p>
     *
     * @param loc a {@link org.bukkit.Location} object.
     * @param chargeFor a {@link com.earth2me.essentials.Trade} object.
     * @throws java.lang.Exception if any.
     */
    @Deprecated
    void teleport(Location loc, Trade chargeFor) throws Exception;

    /**
     * Teleport a player to a specific location
     *
     * @param loc       - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws java.lang.Exception if any.
     */
    void teleport(Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific player
     *
     * @param entity    - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws java.lang.Exception if any.
     */
    void teleport(Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific location
     *
     * @param otherUser - Which user will be teleported
     * @param loc       - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws java.lang.Exception if any.
     */
    void teleportPlayer(IUser otherUser, Location loc, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport a player to a specific player
     *
     * @param otherUser - Which user will be teleported
     * @param entity    - Where should the player end up
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws java.lang.Exception if any.
     */
    void teleportPlayer(IUser otherUser, Player entity, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport wrapper used to handle tp fallback on /jail and /home
     *
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws java.lang.Exception if any.
     */
    void respawn(final Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport wrapper used to handle /warp teleports
     *
     * @param otherUser - Which user will be teleported
     * @param warp      - The name of the warp the user will be teleported too.
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @param cause     - The reported teleportPlayer cause
     * @throws java.lang.Exception if any.
     */
    void warp(IUser otherUser, String warp, Trade chargeFor, PlayerTeleportEvent.TeleportCause cause) throws Exception;

    /**
     * Teleport wrapper used to handle /back teleports
     *
     * @param chargeFor - What the user will be charged if teleportPlayer is successful
     * @throws java.lang.Exception if any.
     */
    void back(Trade chargeFor) throws Exception;

    /**
     * Teleport wrapper used to handle /back teleports that
     * are executed by a different player with this
     * instance of teleport as a target.
     *
     * @param teleporter - The user performing the /back command.
     *                   This value may be {@code null} to indicate console.
     * @param chargeFor - What the {@code teleporter} will be charged if teleportPlayer is successful
     * @throws java.lang.Exception if any.
     */
    void back(IUser teleporter, Trade chargeFor) throws Exception;

    /**
     * Teleport wrapper used to handle throwing user home after a jail sentence
     *
     * @throws java.lang.Exception if the operation failed.
     */
    void back() throws Exception;

}
