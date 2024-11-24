package com.earth2me.essentials;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class StorageTest {
    private final Essentials ess;
    private final FakeServer server;
    private final World world;

    public StorageTest() {
        server = FakeServer.getServer();
        world = server.getWorld("testWorld");
        ess = new Essentials(server);
        try {
            ess.setupForTesting(server);
        } catch (final InvalidDescriptionException ex) {
            Assert.fail("InvalidDescriptionException");
        } catch (final IOException ex) {
            Assert.fail("IOException");
        }
    }

    @Test
    public void testOldUserdata() {
        final ExecuteTimer ext = new ExecuteTimer();
        ext.start();
        final OfflinePlayerStub base1 = server.createPlayer("testPlayer1");
        server.addPlayer(base1);
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
