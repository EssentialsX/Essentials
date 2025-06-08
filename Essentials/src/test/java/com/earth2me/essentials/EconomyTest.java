package com.earth2me.essentials;

import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.earth2me.essentials.commands.IEssentialsCommand;
import com.earth2me.essentials.commands.NoChargeException;
import com.earth2me.essentials.utils.AdventureUtil;
import net.ess3.api.Economy;
import net.ess3.api.MaxMoneyException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.IOException;

public class EconomyTest {
    private static final String NPCNAME = "npc1";
    private static final String PLAYERNAME = "testPlayer1";
    private static final String PLAYERNAME2 = "testPlayer2";
    private static transient Essentials ess;
    private static ServerMock server;

    @BeforeAll
    static void setUp() {
        server = MockBukkit.mock();
        Essentials.TESTING = true;
        ess = MockBukkit.load(Essentials.class);
        try {
            ess.setupForTesting(server);
        } catch (final InvalidDescriptionException ex) {
            Assertions.fail("InvalidDescriptionException");
        } catch (final IOException ex) {
            Assertions.fail("IOException");
        }
        server.addPlayer(PLAYERNAME);
        server.addPlayer(PLAYERNAME2);
    }

    @AfterAll
    static void tearDown() {
        ess.tearDownForTesting();
        MockBukkit.unmock();
    }

    // only one big test, since we use static instances
    @Test
    public void testEconomy() {
        // test NPC
        Assertions.assertFalse(Economy.playerExists(NPCNAME), "NPC does not exists");
        Assertions.assertTrue(Economy.createNPC(NPCNAME), "Create NPC");
        Assertions.assertTrue(Economy.playerExists(NPCNAME), "NPC exists");
        Assertions.assertNotNull(ess.getOfflineUser(NPCNAME), "NPC can be accessed");
        try {
            Economy.removeNPC(NPCNAME);
        } catch (final UserDoesNotExistException ex) {
            Assertions.fail(ex.getMessage());
        }
        Assertions.assertFalse(Economy.playerExists(NPCNAME), "NPC can be removed");

        //test Math
        try {

            Assertions.assertTrue(Economy.playerExists(PLAYERNAME), "Player exists");
            Economy.resetBalance(PLAYERNAME);
            Assertions.assertEquals(0.0, Economy.getMoney(PLAYERNAME), 0, "Player has no money");
            Economy.add(PLAYERNAME, 10.0);
            Assertions.assertEquals(10.0, Economy.getMoney(PLAYERNAME), 0, "Add money");
            Economy.subtract(PLAYERNAME, 5.0);
            Assertions.assertEquals(5.0, Economy.getMoney(PLAYERNAME), 0, "Subtract money");
            Economy.multiply(PLAYERNAME, 2.0);
            Assertions.assertEquals(10.0, Economy.getMoney(PLAYERNAME), 0, "Multiply money");
            Economy.divide(PLAYERNAME, 2.0);
            Assertions.assertEquals(5.0, Economy.getMoney(PLAYERNAME), 0, "Divide money");
            Economy.setMoney(PLAYERNAME, 10.0);
            Assertions.assertEquals(10.0, Economy.getMoney(PLAYERNAME), 0, "Set money");
        } catch (final NoLoanPermittedException | UserDoesNotExistException | MaxMoneyException ex) {
            Assertions.fail(ex.getMessage());
        }

        //test Format
        Assertions.assertEquals("$1,000", Economy.format(1000.0), "Format $1,000");
        Assertions.assertEquals("$10", Economy.format(10.0), "Format $10");
        Assertions.assertEquals("$10.10", Economy.format(10.10), "Format $10.10");
        Assertions.assertEquals("$10.10", Economy.format(10.1000001), "Format $10.10");
        Assertions.assertEquals("$10.10", Economy.format(10.1099999), "Format $10.10");

        //test Exceptions
        try {
            Assertions.assertTrue(Economy.playerExists(PLAYERNAME), "Player exists");
            Economy.resetBalance(PLAYERNAME);
            Assertions.assertEquals(0.0, Economy.getMoney(PLAYERNAME), 0, "Reset balance");
            Economy.subtract(PLAYERNAME, 5.0);
            Assertions.fail("Did not throw exception");
        } catch (final NoLoanPermittedException | MaxMoneyException ignored) {
        } catch (final UserDoesNotExistException ex) {
            Assertions.fail(ex.getMessage());
        }

        try {
            Economy.resetBalance("UnknownPlayer");
            Assertions.fail("Did not throw exception");
        } catch (final NoLoanPermittedException | MaxMoneyException ex) {
            Assertions.fail(ex.getMessage());
        } catch (final UserDoesNotExistException ignored) {
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

    private void runConsoleCommand(final String command, final String args) throws Exception {
        runConsoleCommand(command, args.split("\\s+"));
    }

    private void runConsoleCommand(final String command, final String[] args) throws Exception {
        final IEssentialsCommand cmd;

        final CommandSender sender = server.getConsoleSender();

        try {
            cmd = (IEssentialsCommand) Essentials.class.getClassLoader()
                .loadClass("com.earth2me.essentials.commands.Command" + command).newInstance();
            cmd.setEssentials(ess);
            cmd.run(server, new CommandSource(ess, sender), command, null, args);
        } catch (final NoChargeException ignored) {
        }
    }

    @Test
    public void testNegativePayCommand() {
        final User user1 = ess.getUser(PLAYERNAME);
        try {
            runCommand("pay", user1, PLAYERNAME2 + " -123");
        } catch (final Exception e) {
            Assertions.assertEquals(AdventureUtil.miniToLegacy(I18n.tlLiteral("payMustBePositive")), e.getMessage());
        }
    }
}
