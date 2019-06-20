package com.earth2me.essentials.perm;

import org.bukkit.entity.Player;

import java.util.List;


/**
 * <p>IPermissionsHandler interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IPermissionsHandler {
    /**
     * <p>getGroup.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     * @return a {@link java.lang.String} object.
     */
    String getGroup(Player base);

    /**
     * <p>getGroups.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     * @return a {@link java.util.List} object.
     */
    List<String> getGroups(Player base);

    /**
     * <p>canBuild.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     * @param group a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean canBuild(Player base, String group);

    /**
     * <p>inGroup.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     * @param group a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean inGroup(Player base, String group);

    /**
     * <p>hasPermission.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     * @param node a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean hasPermission(Player base, String node);

    // Does not check for * permissions
    /**
     * <p>isPermissionSet.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     * @param node a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isPermissionSet(Player base, String node);

    /**
     * <p>getPrefix.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     * @return a {@link java.lang.String} object.
     */
    String getPrefix(Player base);

    /**
     * <p>getSuffix.</p>
     *
     * @param base a {@link org.bukkit.entity.Player} object.
     * @return a {@link java.lang.String} object.
     */
    String getSuffix(Player base);

    /**
     * <p>tryProvider.</p>
     *
     * @return a boolean.
     */
    boolean tryProvider();
}
