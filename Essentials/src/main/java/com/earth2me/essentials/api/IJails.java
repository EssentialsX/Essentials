package com.earth2me.essentials.api;

import com.earth2me.essentials.IConf;
import net.ess3.api.IUser;
import org.bukkit.Location;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Provides access to the storage of jail locations. Maintainers should add methods to <i>this interface</i>.
 *
 * @deprecated External plugins should use {@link net.ess3.api.IJails} instead of this interface in case future APIs are added.
 */
@Deprecated
public interface IJails extends IConf {
    /**
     * Gets the location of the jail with the given name
     *
     * @param jailName The name of the jail
     * @return the location of the jail
     * @throws Exception if the jail does not exist
     */
    Location getJail(String jailName) throws Exception;

    /**
     * Gets a list of jails by names
     *
     * @return a list of jails, if there are none the list will be empty
     * @throws Exception
     */
    Collection<String> getList() throws Exception;

    /**
     * Gets the number of jails
     *
     * @return the size of the list of jails
     */
    int getCount();

    /**
     * Remove the jail with the given name
     *
     * @param jail the jail to remove
     * @throws Exception if the jail does not exist
     */
    void removeJail(String jail) throws Exception;

    /**
     * Attempts to send the given user to the given jail
     *
     * @param user the user to send to jail
     * @param jail the jail to send the user to
     * @throws Exception if the user is offline or jail does not exist
     * @deprecated Use {@link IJails#sendToJail(IUser, String, CompletableFuture)}
     */
    @Deprecated
    void sendToJail(IUser user, String jail) throws Exception;

    /**
     * Attempts to send the given user to the given jail
     *
     * @param user   the user to send to jail
     * @param jail   the jail to send the user to
     * @param future Future which is completed with the success status of the execution
     * @throws Exception if the user is offline or jail does not exist
     */
    void sendToJail(IUser user, String jail, CompletableFuture<Boolean> future) throws Exception;

    /**
     * Set a new jail with the given name and location
     *
     * @param jailName the name of the jail being set
     * @param loc      the location of the jail being set
     * @throws Exception
     */
    void setJail(String jailName, Location loc) throws Exception;

    /**
     * Begins a transaction
     */
    void startTransaction();

    /**
     * Ends the current transaction and saves the state
     */
    void stopTransaction(boolean blocking);
}
