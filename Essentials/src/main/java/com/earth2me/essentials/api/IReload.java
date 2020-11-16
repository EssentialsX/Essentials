package com.earth2me.essentials.api;

/**
 * Represents a storage object that is reloadable.
 *
 * @deprecated This is a remnant of the abandoned 3.x storage system. Neither future 2.x code nor external plugins
 *             should use this interface.
 */
@Deprecated
public interface IReload {
    /**
     * Reloads the given storage object.
     */
    void onReload();
}
