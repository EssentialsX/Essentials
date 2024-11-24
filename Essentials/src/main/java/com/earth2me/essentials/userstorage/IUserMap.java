package com.earth2me.essentials.userstorage;

import com.earth2me.essentials.User;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IUserMap {
    /**
     * Gets all the UUIDs of every User which has joined.
     * @return the UUIDs of all Users.
     */
    Set<UUID> getAllUserUUIDs();

    /**
     * Gets the current amount of users loaded into memory.
     * @return the amount of users loaded into memory.
     */
    long getCachedCount();

    /**
     * Gets the amount of users stored by Essentials.
     * @return the amount of users stored by Essentials.
     */
    int getUserCount();

    User getUser(final UUID uuid);

    User getUser(final Player base);

    User getUser(final String name);

    User loadUncachedUser(final Player base);

    /**
     * Gets a User by the given UUID in the cache, if present, otherwise loads the user without placing them in the cache.
     * Ideally to be used when running operations on all stored users.
     *
     * @param uuid the UUID of the user to get.
     * @return the User with the given UUID, or null if not found.
     */
    User loadUncachedUser(final UUID uuid);

    /**
     * Gets the name to UUID cache.
     * @return the name to UUID cache.
     */
    Map<String, UUID> getNameCache();
}
