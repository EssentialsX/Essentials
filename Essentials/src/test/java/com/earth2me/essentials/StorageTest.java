package com.earth2me.essentials;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

public class StorageTest {
    private Essentials ess;
    private ServerMock server;
    private World world;

    @BeforeEach
    public void setUp() {
        this.server = MockBukkit.mock();
        world = server.addSimpleWorld("testWorld");
        Essentials.TESTING = true;
        ess = MockBukkit.load(Essentials.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testOldUserdata() {
        final ExecuteTimer ext = new ExecuteTimer();
        ext.start();
        final PlayerMock base1 = server.addPlayer("testPlayer1");
        ext.mark("fake user created");
        final UserData user = ess.getUser(base1);
        ext.mark("load empty user");
        for (int j = 0; j < 1; j++) {
            user.setHome("home", new Location(world, j, j, j));
        }
        ext.mark("change home 1 times");
        user.save();
        ext.mark("write user");
        user.save();
        ext.mark("write user (cached)");
        user.reloadConfig();
        ext.mark("reloaded file");
        user.reloadConfig();
        ext.mark("reloaded file (cached)");
        System.out.println(ext.end());
    }
}
