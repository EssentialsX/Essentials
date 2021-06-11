package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import junit.framework.TestCase;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;

import java.io.IOException;

public class ToggleTest extends TestCase {
    private final OfflinePlayer base1;
    private final Essentials ess;
    private final FakeServer server;

    public ToggleTest(final String testName) {
        super(testName);
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

    private void runCommand(final String command, final User user, final String[] args) throws Exception {
        final IEssentialsCommand cmd;

        try {
            cmd = (IEssentialsCommand) Essentials.class.getClassLoader().loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
            cmd.setEssentials(ess);
            cmd.run(server, user, command, null, args);
        } catch (final NoChargeException ignored) {
        }

    }

    private void runConsoleCommand(final String command, final String[] args) throws Exception {
        final IEssentialsCommand cmd;

        final CommandSender sender = server.getConsoleSender();

        try {
            cmd = (IEssentialsCommand) Essentials.class.getClassLoader().loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
            cmd.setEssentials(ess);
            cmd.run(server, new CommandSource(sender), command, null, args);
        } catch (final NoChargeException ignored) {
        }

    }

    public void testFlyToggle() throws Exception {
        final User user = ess.getUser(base1);

        assertFalse(user.getBase().getAllowFlight());

        runCommand("fly", user, new String[] {"on"});
        assertTrue(user.getBase().getAllowFlight());

        runCommand("fly", user, new String[] {"on"});
        assertTrue(user.getBase().getAllowFlight());

        runCommand("fly", user, new String[] {"off"});
        assertFalse(user.getBase().getAllowFlight());

        runCommand("fly", user, new String[] {"off"});
        assertFalse(user.getBase().getAllowFlight());

        runCommand("fly", user, new String[] {});
        assertTrue(user.getBase().getAllowFlight());

        runCommand("fly", user, new String[] {});
        assertFalse(user.getBase().getAllowFlight());
    }

    public void testFlyDisOnToggle() throws Exception {
        final User user = ess.getUser(base1);

        user.getBase().setAllowFlight(true);
        user.getBase().setFlying(true);
        assertTrue(user.getBase().isFlying());
        runCommand("fly", user, new String[] {});
        assertFalse(user.getBase().getAllowFlight());
        assertFalse(user.getBase().isFlying());
    }

    public void testGodToggle() throws Exception {
        final User user = ess.getUser(base1);

        assertFalse(user.isGodModeEnabled());

        runCommand("god", user, new String[] {"on"});
        assertTrue(user.isGodModeEnabled());

        runCommand("god", user, new String[] {"on"});
        assertTrue(user.isGodModeEnabled());

        runCommand("god", user, new String[] {"off"});
        assertFalse(user.isGodModeEnabled());

        runCommand("god", user, new String[] {"off"});
        assertFalse(user.isGodModeEnabled());

        runCommand("god", user, new String[] {});
        assertTrue(user.isGodModeEnabled());

        runCommand("god", user, new String[] {});
        assertFalse(user.isGodModeEnabled());
    }

    public void testConsoleToggle() throws Exception {
        final User user = ess.getUser(base1);

        assertFalse(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "on"});
        assertTrue(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "on"});
        assertTrue(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "off"});
        assertFalse(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "off"});
        assertFalse(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName()});
        assertTrue(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName()});
        assertFalse(user.getBase().getAllowFlight());
    }

    public void testAliasesToggle() throws Exception {
        final User user = ess.getUser(base1);

        assertFalse(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "enable"});
        assertTrue(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "enable"});
        assertTrue(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "disable"});
        assertFalse(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "disable"});
        assertFalse(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "1"});
        assertTrue(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "1"});
        assertTrue(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "0"});
        assertFalse(user.getBase().getAllowFlight());

        runConsoleCommand("fly", new String[] {base1.getName(), "0"});
        assertFalse(user.getBase().getAllowFlight());

    }
}
