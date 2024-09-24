package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TeleportationTest {

    private final OfflinePlayer base1;
    private final Essentials ess;
    private final FakeServer server;

    public TeleportationTest() {
        server = FakeServer.getServer();
        ess = new Essentials(server);
        try {
            ess.setupForTesting(server);
        } catch (final InvalidDescriptionException ex) {
            fail("InvalidDescriptionException");
        } catch (final IOException ex) {
            fail("IOException");
        }
        base1 = server.createPlayer("testPlayer1");
        server.addPlayer(base1);
        ess.getUser(base1);
    }

    @Test
    public void testTeleportationCooldownConfig() {
        final User user1 = ess.getUser(base1);

        final double defaultTeleportCooldown = ess.getSettings().getTeleportCooldown("default");
        assertEquals(defaultTeleportCooldown, 0, 0.01);

        boolean fail = false;
        try {
            // Test two home commands in quick succession; will work as long as config is properly set
            runCommand("home", user1, "a");
            runCommand("home", user1, "a");
        } catch (Exception e) {
            fail = true;
        } finally {
            assertEquals(fail, false);
        }
    }

    private void runCommand(final String command, final User user, final String args) throws Exception {
        runCommand(command, user, args.split("\\s+"));
    }

    private void runCommand(final String command, final User user, final String[] args) throws Exception {
        final IEssentialsCommand cmd;

        try {
            cmd = (IEssentialsCommand) Essentials.class.getClassLoader()
                    .loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
            cmd.setEssentials(ess);
            cmd.run(server, user, command, null, args);
        } catch (final NoChargeException ignored) {
        }
    }
}
