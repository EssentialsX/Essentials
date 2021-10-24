package com.earth2me.essentials.api;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.commands.WarpNotFoundException;
import org.bukkit.Location;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

/**
 * Provides access to the storage of warp locations. Maintainers should add methods to <i>this interface</i>.
 *
 * @deprecated External plugins should use {@link net.ess3.api.IWarps} instead of this interface, in case future APIs are added.
 */
@Deprecated
public interface IWarps extends IConf {
    /**
     * Get a warp by name
     *
     * @param warp - Warp name
     * @return - Location the warp is set to
     * @throws WarpNotFoundException When the warp is not found
     * @throws net.ess3.api.InvalidWorldException When the world the warp is in is not found
     */
    Location getWarp(String warp) throws WarpNotFoundException, net.ess3.api.InvalidWorldException;

    /**
     * Checks if the provided name is a warp.
     *
     * @param name The warp name.
     * @return true if a warp by that name exists.
     */
    boolean isWarp(String name);

    /**
     * Gets a list of warps
     *
     * @return - A {@link Collection} of warps
     */
    Collection<String> getList();

    /**
     * Gets the number of warps
     *
     * @return the size of the list of warps
     */
    int getCount();

    /**
     * Delete a warp from the warp DB
     *
     * @param name - Name of warp
     * @throws Exception If the warp could not be removed
     */
    void removeWarp(String name) throws Exception;

    /**
     * Set a warp
     *
     * @param name - Name of warp
     * @param loc  - Location of warp
     * @throws Exception If the warp could not be set
     */
    void setWarp(String name, Location loc) throws Exception;

    /**
     * Set a warp
     *
     * @param user - User of warp
     * @param name - Name of warp
     * @param loc  - Location of warp
     * @throws Exception If the warp could not be set
     */
    void setWarp(IUser user, String name, Location loc) throws Exception;

    /**
     * Gets Lastowner UUID
     *
     * @param warp - Name of warp
     * @throws WarpNotFoundException If the warp is not found
     */
    UUID getLastOwner(String warp) throws WarpNotFoundException;

    /**
     * Check to see if the file is empty
     *
     * @return Whether or not the file is empty
     */
    boolean isEmpty();

    /**
     * @deprecated This method relates to the abandoned 3.x storage refactor and is not implemented.
     */
    @Deprecated
    File getWarpFile(String name) throws net.ess3.api.InvalidNameException;
}
