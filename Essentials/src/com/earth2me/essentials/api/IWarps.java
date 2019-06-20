package com.earth2me.essentials.api;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.commands.WarpNotFoundException;
import org.bukkit.Location;

import java.io.File;
import java.util.Collection;
import java.util.UUID;


/**
 * <p>IWarps interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IWarps extends IConf {
    /**
     * Get a warp by name
     *
     * @param warp - Warp name
     * @return - Location the warp is set to
     * @throws com.earth2me.essentials.commands.WarpNotFoundException When the warp is not found
     * @throws net.ess3.api.InvalidWorldException When the world the warp is in is not found
     */
    Location getWarp(String warp) throws WarpNotFoundException, net.ess3.api.InvalidWorldException;

    /**
     * Gets a list of warps
     *
     * @return - A {@link java.util.Collection} of warps
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
     * @throws java.lang.Exception when the warp is not found.
     */
    void removeWarp(String name) throws Exception;

    /**
     * Set a warp
     *
     * @param name - Name of warp
     * @param loc  - Location of warp
     * @throws java.lang.Exception when the warp at the specified location is not found.
     */
    void setWarp(String name, Location loc) throws Exception;

    /**
     * Set a warp
     *
     * @param user - User of warp
     * @param name - Name of warp
     * @param loc  - Location of warp
     * @throws java.lang.Exception when the warp owned by the user at the specified location is not found.
     */
    void setWarp(IUser user, String name, Location loc) throws Exception;
      
    /**
     * Gets Lastowner UUID
     *
     * @throws com.earth2me.essentials.commands.WarpNotFoundException if any.
     * @param warp a {@link java.lang.String} object.
     * @return a {@link java.util.UUID} object.
     */
    UUID getLastOwner(String warp) throws WarpNotFoundException;
    /**
     * Check to see if the file is empty
     *
     * @return a boolean.
     */
    boolean isEmpty();

    /**
     * Get a warp file note: this is not yet implemented, as 3.x uses different storage methods
     *
     * @param name - name of file
     * @return - an instance of the file
     * @throws net.ess3.api.InvalidNameException - When the file is not found
     */
    File getWarpFile(String name) throws net.ess3.api.InvalidNameException;
}
