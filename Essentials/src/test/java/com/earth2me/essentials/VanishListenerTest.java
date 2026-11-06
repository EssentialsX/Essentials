package com.earth2me.essentials;

import net.ess3.api.IUser;
import net.ess3.api.events.VanishStatusChangeEvent;
import org.bukkit.Statistic;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VanishListenerTest {

    private ServerMock server;
    private Essentials ess;
    private PlayerMock player;
    private User user;
    private IUser vanishedUser;

    @BeforeEach
    public void setUp() {
        this.server = MockBukkit.mock();
        Essentials.TESTING = true;
        ess = MockBukkit.load(Essentials.class);
        player = server.addPlayer("testPlayer");
        ess.getUser(player);
        user = ess.getUser(player);
        assertNotNull(user);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void shouldSaveTimeSinceRestWhenEnteringVanish() {
        player.setStatistic(Statistic.TIME_SINCE_REST, 1200);
        VanishStatusChangeEvent event = new VanishStatusChangeEvent(user, user, true);
        assertEquals(user, event.getAffected());
        server.getPluginManager().callEvent(event);
        assertEquals(1200, user.getLastSleptBeforeVanish());
    }

    @Test
    public void shouldRestoreTimeSinceRestWhenLeavingVanish() {
        player.setStatistic(Statistic.TIME_SINCE_REST, 1200);
        server.getPluginManager().callEvent(new VanishStatusChangeEvent(user, user, true));
        player.setStatistic(Statistic.TIME_SINCE_REST, 8000);
        server.getPluginManager().callEvent(new VanishStatusChangeEvent(user, user, false));
        assertEquals(1200, player.getStatistic(Statistic.TIME_SINCE_REST));
    }

    @Test
    public void shouldPreserveTimeSinceRestWhenSleepingWhileVanished() {
        player.setStatistic(Statistic.TIME_SINCE_REST, 1200);
        server.getPluginManager().callEvent(new VanishStatusChangeEvent(user, user, true));
        server.getPluginManager().callEvent(new PlayerBedEnterEvent(player, player.getLocation().getBlock(), PlayerBedEnterEvent.BedEnterResult.OK));
        server.getScheduler().performTicks(1500);
        server.getPluginManager().callEvent(new PlayerBedLeaveEvent(player, player.getLocation().getBlock(), true));
        server.getPluginManager().callEvent(new VanishStatusChangeEvent(user, user, false));
        assertEquals(1200, player.getStatistic(Statistic.TIME_SINCE_REST));
    }
}