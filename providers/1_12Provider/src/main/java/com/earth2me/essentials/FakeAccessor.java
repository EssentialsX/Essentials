package com.earth2me.essentials;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Utility method to bridge certain features between our test suite and the base module.
 */
public interface FakeAccessor {
    void onPlayerJoin(PlayerJoinEvent event);

    void getUser(Player player);
}
