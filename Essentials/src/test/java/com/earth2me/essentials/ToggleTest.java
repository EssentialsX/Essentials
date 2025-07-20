package com.earth2me.essentials;

import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ToggleTest {
    private PlayerMock base1;
    private Essentials ess;
    private ServerMock server;

    @BeforeEach
    public void setUp() {
        this.server = MockBukkit.mock();
        Essentials.TESTING = true;
        ess = MockBukkit.load(Essentials.class);
        base1 = server.addPlayer("testPlayer1");
        ess.getUser(base1);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
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
            cmd.run(server, new CommandSource(ess, sender), command, null, args);
        } catch (final NoChargeException ignored) {
        }

    }

    @Test
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

    @Test
    public void testFlyDisOnToggle() throws Exception {
        final User user = ess.getUser(base1);

        user.getBase().setAllowFlight(true);
        user.getBase().setFlying(true);
        assertTrue(user.getBase().isFlying());
        runCommand("fly", user, new String[] {});
        assertFalse(user.getBase().getAllowFlight());
        assertFalse(user.getBase().isFlying());
    }

    @Test
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

    @Test
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

    @Test
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
